package com.bolo.test.crawler;

import com.bolo.crawler.abstractclass.AbstractCrawler;
import com.bolo.crawler.interfaceclass.ProcessorObserver;
import com.bolo.crawler.entitys.Request;
import com.bolo.crawler.entitys.SimpleObject;

import java.io.Serializable;

/**
 * @Author wangyue
 * @Date 9:37
 */
public abstract class CrawlerObserver implements ProcessorObserver,Serializable{

    private static final long serialVersionUID = 6529685098267757697L;
    protected AbstractCrawler crawler;
    protected String description;
    protected int priority;
    protected String ctype;

    public CrawlerObserver() {

    }

    public CrawlerObserver(int priority, String description) {
        this.priority = priority;
        this.description = description;
    }

    public CrawlerObserver(int priority, String description, String ctype) {
        this(priority, description);
        this.ctype = ctype;
    }

    public void setCrawler(AbstractCrawler crawler) {
        this.crawler = crawler;
    }

    @Override
    public void preparedData(SimpleObject context) throws Exception {

    }

    @Override
    public void beforeRequest(SimpleObject context) throws Exception {

    }

    @Override
    public void afterRequest(SimpleObject context) throws Exception {

    }


    @Override
    public void breakRequest(Request req) throws Exception {

    }

    @Override
    public String getClassification() {
        return this.toString();
    }
}
