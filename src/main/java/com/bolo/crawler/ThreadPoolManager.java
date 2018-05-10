package com.bolo.crawler;

/**
 * @Author wangyue
 * @Date 14:55
 */
public class ThreadPoolManager {
    private static final CountableThreadPool proxyThreadPool = new CountableThreadPool(Integer.MAX_VALUE);
    private static final CountableThreadPool spiderThreadPool = new CountableThreadPool(Integer.MAX_VALUE);
    public static final CountableThreadPool getProxyThreadPool() {
        return proxyThreadPool;
    }
    public static final CountableThreadPool getSpiderThreadPool() {
        return spiderThreadPool;
    }
}
