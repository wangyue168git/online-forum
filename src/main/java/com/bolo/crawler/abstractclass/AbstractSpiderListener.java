package com.bolo.crawler.abstractclass;

import com.bolo.crawler.entitys.SimpleObject;
import com.bolo.crawler.interfaceclass.SpiderListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author wangyue
 * @Date 16:47
 */
public abstract class AbstractSpiderListener implements SpiderListener {
    protected SimpleObject context;
    protected Logger logger = LoggerFactory.getLogger(getClass());
    protected long interval = -1;
    public AbstractSpiderListener() {
        //this.context = context;
    }
    public AbstractSpiderListener(long interval) {
        this.interval = interval;
        //this.context = context;
    }
    public AbstractSpiderListener(SimpleObject context) {
        this.context = context;
    }
    public AbstractSpiderListener(SimpleObject context, long interval) {
        this.context = context;
        this.interval = interval;
    }
    @Override
    public void onStartup(SimpleObject context, Object obj) {
    }

    @Override
    public void onComplete(SimpleObject context, Object obj) {

    }
    @Override
    public void onScheduled(SimpleObject context, Object obj) {

    }
    @Override
    public void onRequestSuccess(SimpleObject context, Object obj) {

    }
    @Override
    public void onEvent(String event, SimpleObject context, Object obj) {

    }

    @Override
    public void onRequestError(SimpleObject context, Object obj) {

    }
    public long getInterval() {
        return interval;
    }
}
