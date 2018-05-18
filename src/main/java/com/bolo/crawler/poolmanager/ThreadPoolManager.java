package com.bolo.crawler.poolmanager;

/**
 * @Author wangyue
 * @Date 14:55
 */
public class ThreadPoolManager {
    private static final CountableThreadPool proxyThreadPool = new CountableThreadPool(Integer.MAX_VALUE);
    private static final CountableThreadPool spiderThreadPool = new CountableThreadPool(Integer.MAX_VALUE);
    private static final CountableThreadPool crawlerThreadPool = new CountableThreadPool(50);
    public static final CountableThreadPool getProxyThreadPool() {
        return proxyThreadPool;
    }
    public static final CountableThreadPool getSpiderThreadPool() {
        return spiderThreadPool;
    }
    public static final CountableThreadPool getCrawlerThreadPool() {
        return crawlerThreadPool;
    }

}
