package com.bolo.crawler.entitys;

import com.bolo.crawler.abstractclass.AbstractTask;
import com.bolo.crawler.httpclient.HttpDownload;
import com.bolo.crawler.poolmanager.CountableThreadPool;
import com.bolo.crawler.poolmanager.ThreadPoolManager;
import com.bolo.crawler.queue.ScheduleQueue;
import com.bolo.crawler.queue.ScheduleQueueManager;
import com.bolo.crawler.utils.StatusTracker;
import com.bolo.crawler.interfaceclass.*;
import com.bolo.crawler.utils.SessionUtil;


import lombok.Getter;
import lombok.Setter;
import org.apache.http.conn.HttpHostConnectException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;


/**
 * @Author wangyue
 * @Date 16:14
 */
@Getter
@Setter
public class Spider extends AbstractTask implements Serializable {


    private static final long serialVersionUID = 6529685098267757693L;
    public static final String CLASSFICATION_STATUS_REMOVE = "Remove";


    protected ScheduleQueue scheduleQueue = ScheduleQueueManager.getScheduleQueue();

    protected CountableThreadPool crawlerThreadPool = ThreadPoolManager.getCrawlerThreadPool();

    protected Logger logger = LoggerFactory.getLogger("Task");

    protected int threadNum = 1;

    protected volatile AtomicInteger stat = new AtomicInteger(STAT_INIT);

    protected boolean exitWhenComplete = true;

    protected final static int STAT_INIT = 0;

    protected final static int STAT_RUNNING = 1;

    protected final static int STAT_STOPPED = 2;


    protected boolean useProxy = false;

    protected boolean localProxy = false;

    @Setter
    protected boolean removeProxy = false;
    @Setter
    protected long recoverTime = -1L;

    protected boolean noLogger = false;


    protected boolean destroyWhenExit = true;
    private List<StatusTracker> notifyList;

    private List<SpiderListener> spiderListeners;
    private Downloader downloader;


    private Date startTime;
    private Site site = Site.me();

    private SpiderAdder spiderAdder = new SpiderAdder(this);


    private SimpleObject context = new SimpleObject();
    private int emptySleepTime = 30000;//30000;



    protected String uuid;

    public static SimpleObject buildListenerContext() {
        return buildListenerContext1();
    }

    private static SimpleObject buildListenerContext1() {
        SimpleObject context = new SimpleObject();
        String businessKey = SessionUtil.getText(SessionUtil.CURRENT_BUSINESS_KEY);
        context.put(SessionUtil.CURRENT_BUSINESS_KEY, businessKey);
        Map map = (Map) SessionUtil.getObject(SessionUtil.CURRENT_DATA);
        context.put(SessionUtil.CURRENT_DATA, map);
        return context;
    }

    public Spider addSpiderListener(SpiderListener observer) {
        if (observer == null) {
            return this;
        }
        if (spiderListeners == null) {
            spiderListeners = new ArrayList<SpiderListener>();
        }
        spiderListeners.add(observer);
        return this;
    }

    protected String sequenceNo = "";
    public String certId = "";
    public boolean spiderLastStep = false;

    public static int DEFAULT_RETRY_TIMES = 2;
    public static int DEFAULT_SITE_RETRY_TIMES = 3;

    public static Spider create() {
        Spider s = new Spider();
        s.getSite().setCycleRetryTimes(DEFAULT_RETRY_TIMES).setRetryTimes(3).setShareCache(true)
                .setUserAgent(userAgents[(int) (Math.random() * 100 % (userAgents.length))]);
        return s;
    }

    public void addNotify(StatusTracker st) {
        if (notifyList == null) {
            notifyList = new ArrayList<>();
        }
        notifyList.add(st);
    }


    public void start() {
        start(null, null);
    }

    private SpiderListener paramListener;
    private Object paramObj;
    private SimpleObject startContext;
    private SimpleObject proxyContext;

    public void start(SpiderListener spiderListener, Object obj) {
        paramListener = spiderListener;
        paramObj = obj;
        start1(spiderListener, obj);
    }

    public void publishEvent(String event) {
        fireEvent(event, paramListener, 6, startContext, paramObj);
    }

    public void addClassificationStatus(String cls, String status) {
        clsStatusMap.put(cls, status);
    }


    public void addStage() {
        stage++;
    }

    private void checkRunningStat() {
        while (true) {
            int statNow = stat.get();
            if (statNow == STAT_RUNNING) {
                throw new IllegalStateException("Spider is already running!");
            }
            if (stat.compareAndSet(statNow, STAT_RUNNING)) {
                break;
            }
        }
    }

    protected void initComponent() {
        if (downloader == null) {
            downloader = new HttpDownload();
        }
        startTime = new Date();
    }

    private void fireEvent(String event, SpiderListener listener, int step, SimpleObject contenxt, Object obj) {
        if (!CollectionUtils.isEmpty(spiderListeners)) {
            for (SpiderListener spiderListener : spiderListeners) {
                triggerEvent(event, spiderListener, step, contenxt, obj);
            }
        }
        triggerEvent(event, listener, step, contenxt, obj);
    }

    private void triggerEvent(String event, SpiderListener listener, int step, SimpleObject contenxt, Object obj) {
        triggerEvent1(event, listener, step, contenxt, obj);
    }

    private void triggerEvent1(String event, SpiderListener listener, int step, SimpleObject contenxt, Object obj) {
        if (listener == null) {
            return;
        }
        try {
            if (step == 1) {
                listener.onStartup(contenxt, obj);
            } else if (step == 2) {
                listener.onRequestSuccess(contenxt, obj);
            } else if (step == 3) {
                listener.onRequestError(contenxt, obj);
            } else if (step == 4) {
                listener.onComplete(contenxt, obj);
            } else if (step == 5) {
                listener.onScheduled(contenxt, obj);
            } else if (step == 6) {
                listener.onEvent(event, contenxt, obj);
            }
        } catch (Exception e) {
            if (!noLogger) {
                logger.error("triggerEvent error", e);
            }
        }
    }



    private Map<String, String> clsStatusMap = new ConcurrentHashMap<>();
    private ReentrantLock newUrlLock = new ReentrantLock();
    private Condition newUrlCondition = newUrlLock.newCondition();

    private void waitNewUrl() {
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

    private void start1(SpiderListener spiderListener, Object obj) {
        checkRunningStat();
        initComponent();
        if (!noLogger) {
            logger.info("Spider " + getUUID() + " started!");
        }
        SimpleObject spiderContext = new SimpleObject();
        startContext = spiderContext;
        try {
            spiderContext.put(ProcessorObserver.KEY_TASK, this);
            fireEvent(null, spiderListener, 1, spiderContext, obj);


            final Proxy[] host = {null};
            long beforewait;
            long lastIntervalTs = System.currentTimeMillis();

            while (!Thread.currentThread().isInterrupted() && stat.get() == STAT_RUNNING) {
                try {
//                    Request request = Redis.getRequest("requestsQueue");
                    Request request = scheduleQueue.poll(this);
                    if (request == null) {
                        // wait until new url  added
                        beforewait = System.currentTimeMillis();
                        waitNewUrl();
                        if(System.currentTimeMillis() - beforewait >= 30000){
                            stopSpider();
                        }
                        continue;
                    } else {
                        crawlerThreadPool.execute(new Runnable() {
                            @Override
                            public void run() {
                                if (spiderAdder.isSequentially()) {
                                    if (spiderAdder.isHighistPriority(request)) {
                                        // 如果更高权重的Request不为0，则把当前Request加入队列中，请求下一个
                                        addRequest(request);
                                        return;
                                    }
                                }
                                /*
                                    这部分主要是对某些类型的请求进行筛选，并remove掉对应type的请求
                                    如果Classification存在于clsStatusMap，则这个请求跳过
                                 */
                                Set<Map.Entry<String, String>> eset = clsStatusMap.entrySet();
                                String status = null;
                                String cls = "";
                                for (Map.Entry<String, String> e : eset) {
                                    if (request.isClassification(e.getKey())) {
                                        cls = e.getKey();
                                        status = e.getValue();
                                        break;
                                    }
                                }
                                if (status != null && CLASSFICATION_STATUS_REMOVE.equalsIgnoreCase(status)) {
                                    logger.info(String.format("------%s----remove [%s] clf %s", request.getExtra("sequenceNo"), cls, request.getUrl()));
                                    try {
                                        request.notifyObserver(4, request, null);
                                    } catch (Exception e) {
                                        logger.error("notifyObserver when break request", e);
                                    }
                                    return;
                                }

                                //实际请求部分
                                spiderContext.put(ProcessorObserver.KEY_REQUEST, request);
                                host[0] = process(spiderListener, obj, spiderContext, host[0], localProxy, request, 0, 0);
                            }
                        });

                    }
                } catch (Exception e) {
                    logger.error("process request", e);
                }
                if (spiderListener != null && spiderListener.getInterval() > 0 && System.currentTimeMillis() - lastIntervalTs >= spiderListener.getInterval()) {
                    fireEvent(null, spiderListener, 5, spiderContext, obj);
                    lastIntervalTs = System.currentTimeMillis();
                }

            }
            stat.set(STAT_STOPPED);
        } catch (Exception e) {
            logger.error("start spider", e);
        }
        fireEvent(null, spiderListener, 4, spiderContext, obj);
        if (!noLogger) {
            logger.info("Spider " + getUUID() + " end!");
        }
        // release some resources
        if (destroyWhenExit) {
            close();
            if (!noLogger) {
                logger.info("Spider destroyWhenExit");
            }
        }
        notifyList();
    }


    public void stopSpider(){
        logger.info("no task in the queue,and spider stop....");
        if (stat.compareAndSet(STAT_RUNNING, STAT_STOPPED)) {
            logger.info("Crawler " + getUUID() + " stop success!");
        } else {
            logger.info("Crawler " + getUUID() + " stop fail!");
        }
    }

    private void notifyList() {
        if (notifyList == null) {
            return;
        }
        for (StatusTracker st : notifyList) {
            try {
                st.notifyStatus();
            } catch (Exception e) {
                logger.error("notifyStatus", e);
            }
        }
    }

    public void close() {
        downloader.close();
    }

    protected void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private int stage;
    private final AtomicLong pageCount = new AtomicLong(0);

    protected void processRequest(Request request) {
        downloader.download(request, this);
        sleep(site.getSleepTime() * stage);
        if (site.getRndSleepTime() > 0) {
            sleep((int) (Math.random() * site.getRndSleepTime() + 1000) * (stage + 1));
        }
    }

    private Proxy process(SpiderListener spiderListener, Object obj, SimpleObject context, Proxy host, boolean localProxy, Request request, int time, int numProxy) {
        context.put(ProcessorObserver.KEY_REQUEST, request);
        request.setUseProxy(localProxy);
        final Request requestFinal = request;
        //boolean refused = false;
        try {
            processRequest(requestFinal);
            fireEvent(null, spiderListener, 2, context, obj);
        } catch (Exception e) {
            context.put(ProcessorObserver.KEY_ERROR, e);
            fireEvent(null, spiderListener, 3, context, obj);
            if (!noLogger) {
                logger.error("process request " + requestFinal + " error", e);
            }
            if (e instanceof HttpHostConnectException || e.getMessage().contains("refused")) {
                logger.error("connection refused................");
                //refused = true;
            }
        } finally {
            pageCount.incrementAndGet();
            signalNewUrl();
        }
        return host;
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
        return site;
    }



    @Override
    public boolean isNoLogger() {
        return false;
    }

    @Override
    public boolean useProxy() {
        return false;
    }

    private static String[] userAgents = new String[]{"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:35.0) Gecko/20100101 Firefox/35.0", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/22.0.1207.1 Safari/537.1",
            "Mozilla/5.0 (X11; CrOS i686 2268.111.0) AppleWebKit/536.11 (KHTML, like Gecko) Chrome/20.0.1132.57 Safari/536.11", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/536.6 (KHTML, like Gecko) Chrome/20.0.1092.0 Safari/536.6",
            "Mozilla/5.0 (Windows NT 6.2) AppleWebKit/536.6 (KHTML, like Gecko) Chrome/20.0.1090.0 Safari/536.6", "Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/19.77.34.5 Safari/537.1",
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/536.5 (KHTML, like Gecko) Chrome/19.0.1084.9 Safari/536.5", "Mozilla/5.0 (Windows NT 6.0) AppleWebKit/536.5 (KHTML, like Gecko) Chrome/19.0.1084.36 Safari/536.5",
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/536.3 (KHTML, like Gecko) Chrome/19.0.1063.0 Safari/536.3", "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/536.3 (KHTML, like Gecko) Chrome/19.0.1063.0 Safari/536.3",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_0) AppleWebKit/536.3 (KHTML, like Gecko) Chrome/19.0.1063.0 Safari/536.3", "Mozilla/5.0 (Windows NT 6.2) AppleWebKit/536.3 (KHTML, like Gecko) Chrome/19.0.1062.0 Safari/536.3",
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/536.3 (KHTML, like Gecko) Chrome/19.0.1062.0 Safari/536.3", "Mozilla/5.0 (Windows NT 6.2) AppleWebKit/536.3 (KHTML, like Gecko) Chrome/19.0.1061.1 Safari/536.3",
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/536.3 (KHTML, like Gecko) Chrome/19.0.1061.1 Safari/536.3", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/536.3 (KHTML, like Gecko) Chrome/19.0.1061.1 Safari/536.3",
            "Mozilla/5.0 (Windows NT 6.2) AppleWebKit/536.3 (KHTML, like Gecko) Chrome/19.0.1061.0 Safari/536.3", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.24 (KHTML, like Gecko) Chrome/19.0.1055.1 Safari/535.24",
            "Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/535.24 (KHTML, like Gecko) Chrome/19.0.1055.1 Safari/535.24", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2) AppleWebKit/535.24 (KHTML, like Gecko) Chrome/19.0.1055.1 Safari/535.24",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_3) AppleWebKit/535.22 (KHTML, like Gecko) Chrome/19.0.1047.0 Safari/535.22", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21",
            "Mozilla/5.0 (X11; Linux i686) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1041.0 Safari/535.21", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_3) AppleWebKit/535.20 (KHTML, like Gecko) Chrome/19.0.1036.7 Safari/535.20",
            "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/535.2 (KHTML, like Gecko) Chrome/18.6.872.0 Safari/535.2 UNTRUSTED/1.0 3gpp-gba UNTRUSTED/1.0", "Mozilla/5.0 (X11; CrOS i686 1660.57.0) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.46 Safari/535.19",
            "Mozilla/5.0 (Windows NT 6.0; WOW64) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.45 Safari/535.19", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.45 Safari/535.19",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_6_8) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.45 Safari/535.19", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_5_8) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.151 Safari/535.19"};


}
