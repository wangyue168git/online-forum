package com.bolo.crawler.entitys;

import com.bolo.crawler.abstractclass.AbstractCrawler;
import com.bolo.crawler.abstractclass.AbstractTask;
import com.bolo.crawler.interfaceclass.ProcessorObserver;
import com.bolo.crawler.interfaceclass.Task;
import com.bolo.crawler.queue.ScheduleQueue;
import com.bolo.crawler.queue.ScheduleQueueManager;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.LongAdder;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import static com.bolo.crawler.entitys.Spider.emptySleepTime;

/**
 * @Author wangyue
 * @Date 18:32
 */
@Setter
@Getter
public class SpiderAdder extends AbstractTask{

    protected Logger logger = LoggerFactory.getLogger("SpiderAdder");
    protected ScheduleQueue scheduleQueue = ScheduleQueueManager.getScheduleQueue();
    private  TreeMap<Long, LongAdder> urlMap = ScheduleQueueManager.getUrlMap();
    protected static final int defaultPriority = 0;
    public static boolean sequentially;
    private static final SpiderAdder spiderAdder = new SpiderAdder();

    //多监听线程间通信，共享的condition，统一的阻塞队列
    private static final ReentrantLock newUrlLock = new ReentrantLock();
    private static final Condition newUrlCondition = newUrlLock.newCondition();

    protected Spider spider;
    protected String uuid;

    public static SpiderAdder getInstance(){
        return spiderAdder;
    }

    private SpiderAdder(){}

    public SpiderAdder(Spider spider){
        this.spider = spider;
    }

    protected void waitNewUrl() {
        newUrlLock.lock();
        try {
            //double check
            newUrlCondition.await(emptySleepTime, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            logger.warn("waitNewUrl - interrupted, error {}", e);
        } finally {
            newUrlLock.unlock();
        }
    }

    public void signalNewUrl() {
        try {
            newUrlLock.lock();
            newUrlCondition.signalAll();
        } finally {
            newUrlLock.unlock();
        }
    }

    @Override
    public String getUUID() {
        if (uuid != null) {
            return uuid;
        }
        uuid = UUID.randomUUID().toString();
        return uuid;
    }

    @Override
    public Site getSite() {
        return spider == null ? null : spider.getSite();
    }

    @Override
    public Task addUrl(String... urls) {
        return addUrl(null, urls);
    }

    @Override
    public Task addUrl(ProcessorObserver observer, String... urls) {
        for (String url : urls) {
            addRequest(new Request(url).addObjservers(observer));
        }
        signalNewUrl();
        return this;
    }

    @Override
    public Task addRequest(Request... requests) {
        for (Request request : requests) {
            addRequest(request);
        }
        signalNewUrl();
        return this;
    }

    private void addRequest(Request request) {
        if (request != null && request.getUrl() != null) {
//            Redis.addRequests("requestsQueue",request);
            scheduleQueue.push(request, this);
            if (request.getPriority() == 0) {
                request.setPriority(defaultPriority);
            }
            if (request.getPriority() != 0) {
                sequentially = true;
                long priority = -1 * request.getPriority();
                LongAdder i = urlMap.get(priority);
                if (i == null) {
                    synchronized (SpiderAdder.class) {
                        if (i == null) {
                            i = new LongAdder();
                            i.increment();
                            urlMap.put(priority, i);
                        }
                    }
                } else {
                    i.increment();
                }
            }

        }
    }

    public boolean isHighistPriority(Request request) {
        long priority = -1 * request.getPriority();
        boolean reQueue = false;
        if (priority != 0) {
            LongAdder la = urlMap.get(priority);
            if (la != null) {
                la.decrement();
            }
            Set<Long> removeSet = new HashSet<>();
            Iterator<Long> iter = urlMap.keySet().iterator();

            synchronized (SpiderAdder.class){
                    // 有序map循环
                while ((iter.hasNext())) {
                    Long entry = iter.next();
                    // 如果权重不相等，说明有更高优先级的权重
                    if (priority != entry) {
                        LongAdder longAdder = urlMap.get(entry);
                        if (longAdder != null) {
                            long pi = longAdder.longValue();
                            if (pi > 0) {
                                reQueue = true;
                                break;
                            } else if (pi == 0) {
                                removeSet.add(entry);
                            }
                        }
                    } else {
                        break;
                    }
                }
                for (Long e : removeSet) {
                    urlMap.remove(e);
                }
            }

        }
        return reQueue;
    }


}
