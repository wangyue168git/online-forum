package com.bolo.crawler.entitys;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bolo.crawler.httpclient.HttpDownload;
import com.bolo.crawler.poolmanager.ProxyManagerFactory;
import com.bolo.crawler.utils.StatusTracker;
import com.bolo.crawler.interfaceclass.*;
import com.bolo.crawler.utils.SessionUtil;
import com.bolo.redis.Redis;
import com.bolo.redis.RedisCacheUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.conn.HttpHostConnectException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;



/**
 * @Author wangyue
 * @Date 16:14
 */
@Getter
@Setter
public class Spider implements Task {

    public static final String CLASSFICATION_STATUS_REMOVE = "Remove";



    protected Logger logger = LoggerFactory.getLogger("Task");

    protected int threadNum = 1;

    protected AtomicInteger stat = new AtomicInteger(STAT_INIT);

    protected boolean exitWhenComplete = true;

    protected final static int STAT_INIT = 0;

    protected final static int STAT_RUNNING = 1;

    protected final static int STAT_STOPPED = 2;


    protected boolean useProxy = false;

    protected boolean localProxy = false;
    protected boolean releaseProxy = false;
    @Setter
    protected boolean removeProxy = false;
    @Setter
    protected long recoverTime = -1L;
    @Setter
    protected boolean removeAllSiteProxy = false;
    protected boolean noLogger = false;
    protected int defaultPriority = 0;

    protected boolean destroyWhenExit = false;
    private List<StatusTracker> notifyList;

    private List<SpiderListener> spiderListeners;
    private Downloader downloader;


    private Date startTime;
    private Site site = Site.me();


//    private int proxyMethod = ProxyManager.PROXY_METHOD_MULTI;//30000;
//    private int proxyMode = ProxyManager.PROXY_MODE_HTTP;
//    private int proxyHolder = ProxyManager.PROXY_HOLDER_NONE;

    private SimpleObject context = new SimpleObject();
    private int emptySleepTime = 30000;//30000;

    private int recoverProxyWhenComplete = 0;

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
                .setUserAgent(userAgents[(int)(Math.random() * 100 % (userAgents.length))]);
        return s;
    }
    public void addNotify(StatusTracker st) {
        if (notifyList == null) {
            notifyList = new ArrayList<>();
        }
        notifyList.add(st);
    }
    public Spider addUrl(String... urls) {
        return addUrl(null, urls);
    }

    @Override
    public Spider addUrl(ProcessorObserver observer, String... urls) {
        for (String url : urls) {
            addRequest(new Request(url).addObjservers(observer));
        }
        signalNewUrl();
        return this;
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
    public void setReleaseProxy(boolean releaseProxy) {
        this.releaseProxy = releaseProxy;
    }

    public void addStage() {
        stage ++;
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


    private boolean isHighistPriority(Request request) {
        long priority = -1 * request.getPriority();
        boolean reQueue = false;
        if (priority != 0) {
            LongAdder la = urlMap.get(priority);
            if (la != null) {
                la.decrement();
            }
            Set<Long> removeSet = new HashSet<>();
            Iterator<Long> iter = urlMap.keySet().iterator();
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
            for(Long e : removeSet) {
                urlMap.remove(e);
            }

        }
        return reQueue;
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
    private void signalNewUrl() {
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


            Proxy host = null;

            long lastIntervalTs = System.currentTimeMillis();

            while (!Thread.currentThread().isInterrupted() && stat.get() == STAT_RUNNING) {
                try {
                    Request request = Redis.getRequest("requestsQueue");
                    if (request == null) {
                        // wait until new url  added
                        waitNewUrl();
                        continue;
                    } else {
                        if (sequentially) {
                            if (isHighistPriority(request)) {
                                // 如果更高权重的Request不为0，则把当前Request加入队列中，请求下一个
                                addRequest(request);
                                continue;
                            }
                        }
                        Set<Map.Entry<String, String>> eset = clsStatusMap.entrySet();
                        String status = null;
                        String cls = "";
                        // 如果Classification存在于clsStatusMap，则这个请求跳过
                        for(Map.Entry<String, String> e : eset) {
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
                            continue;
                        }

                        int time = 0;
                        int numProxy = 0;
                        spiderContext.put(ProcessorObserver.KEY_REQUEST, request);
                        host = process(spiderListener, obj, spiderContext, host, localProxy, request, time, numProxy);

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
    private void notifyList() {
        if (notifyList == null) {
            return;
        }
        for(StatusTracker st : notifyList) {
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
//        if (site != null && !StringUtils.isNotBlank(site.getDomain())) {
//            return site.getDomain();
//        }
        uuid = UUID.randomUUID().toString();
        return uuid;
    }

    @Override
    public Site getSite() {
        return site;
    }


    @Override
    public Task addRequest(Request... requests) {
        for (Request request : requests) {
            addRequest(request);
        }
        return this;
    }
    private TreeMap<Long, LongAdder> urlMap = new TreeMap<>();
    private boolean sequentially;


    private RedisCacheUtil redisCacheUtil = new RedisCacheUtil();

    private void addRequest(Request request) {

        if (request != null && request.getUrl() != null) {

            Redis.addRequests("requestsQueue",request);
            if (request.getPriority() == 0) {
                request.setPriority(defaultPriority);
            }
            if (request.getPriority() != 0) {
                sequentially = true;
                long priority = -1 * request.getPriority();
                LongAdder i = urlMap.get(priority);
                if (i == null) {
                    i = new LongAdder();
                    i.increment();
                    urlMap.put(priority, i);
                } else {
                    i.increment();
                }
            }
        }
    }


    @Override
    public boolean isNoLogger() {
        return false;
    }

    @Override
    public boolean useProxy() {
        return false;
    }
    private static String[] userAgents = new String[] {"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:35.0) Gecko/20100101 Firefox/35.0", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/22.0.1207.1 Safari/537.1",
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