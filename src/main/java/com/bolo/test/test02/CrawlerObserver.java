package com.bolo.test.test02;

import com.bolo.crawler.interfaceclass.ProcessorObserver;
import com.bolo.crawler.entitys.Request;
import com.bolo.crawler.entitys.SimpleObject;

import java.io.Serializable;

/**
 * @Author wangyue
 * @Date 9:37
 */
public abstract class CrawlerObserver implements ProcessorObserver,Serializable{

    @Override
    public void preparedData(SimpleObject context) throws Exception {

    }

    @Override
    public void beforeRequest(SimpleObject context) throws Exception {

    }

    @Override
    public void breakRequest(Request req) throws Exception {

    }

    @Override
    public String getClassification() {
        return this.toString();
    }
}
