package com.bolo.crawler.entitys;

import com.bolo.crawler.abstractclass.AbstractCrawler;
import com.bolo.crawler.abstractclass.AbstractTask;
import com.bolo.crawler.interfaceclass.ProcessorObserver;
import com.bolo.crawler.interfaceclass.Task;
import com.bolo.crawler.queue.ScheduleQueue;
import com.bolo.crawler.queue.ScheduleQueueManager;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.LongAdder;

/**
 * @Author wangyue
 * @Date 18:32
 */
@Setter
@Getter
public class SpiderAdder extends AbstractTask{


    protected ScheduleQueue scheduleQueue = ScheduleQueueManager.getScheduleQueue();
    public static boolean sequentially;
    private  TreeMap<Long, LongAdder> urlMap = ScheduleQueueManager.getUrlMap();

    protected static final int defaultPriority = 0;
    private static final AtomicBoolean atomicBoolean = new AtomicBoolean(false);

    protected Spider spider;
    protected String uuid;


    public SpiderAdder(Spider spider){
        this.spider = spider;
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
        return spider.getSite();
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
        spider.signalNewUrl();
        return this;
    }

    @Override
    public Task addRequest(Request... requests) {
        for (Request request : requests) {
            addRequest(request);
        }
        spider.signalNewUrl();
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
