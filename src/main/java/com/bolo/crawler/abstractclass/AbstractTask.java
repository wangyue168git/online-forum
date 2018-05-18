package com.bolo.crawler.abstractclass;

import com.bolo.crawler.entitys.Request;
import com.bolo.crawler.entitys.Site;
import com.bolo.crawler.interfaceclass.ProcessorObserver;
import com.bolo.crawler.interfaceclass.Task;

/**
 * @Author wangyue
 * @Date 18:33
 */
public abstract class AbstractTask implements Task {
    @Override
    public String getUUID() {
        return null;
    }

    @Override
    public Site getSite() {
        return null;
    }

    @Override
    public Task addUrl(String... urls) {
        return null;
    }

    @Override
    public Task addUrl(ProcessorObserver observer, String... urls) {
        return null;
    }

    @Override
    public Task addRequest(Request... requests) {
        return null;
    }

    @Override
    public boolean isNoLogger() {
        return false;
    }

    @Override
    public boolean useProxy() {
        return false;
    }
}
