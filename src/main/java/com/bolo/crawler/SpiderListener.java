package com.bolo.crawler;

/**
 * @Author wangyue
 * @Date 16:16
 */
public interface SpiderListener {

    void onStartup(SimpleObject context, Object obj);
    void onComplete(SimpleObject context, Object obj);
    void onScheduled(SimpleObject context, Object obj);
    void onEvent(String event, SimpleObject context, Object obj);
    void onRequestSuccess(SimpleObject context, Object obj);

    void onRequestError(SimpleObject context, Object obj);

    long getInterval();
}
