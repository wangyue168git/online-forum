package com.bolo.crawler.interfaceclass;

import com.bolo.crawler.entitys.Request;
import com.bolo.crawler.entitys.Site;

/**
 * @Author wangyue
 * @Date 14:49
 */
public interface Task {

    public String getUUID();
    public Site getSite();
    public Task addUrl(String... urls) ;
    public Task addUrl(ProcessorObserver observer, String... urls) ;
    public Task addRequest(Request... requests) ;
    boolean isNoLogger();

    public boolean useProxy();
}
