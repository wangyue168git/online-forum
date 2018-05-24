package com.bolo.test.crawler;

import com.bolo.crawler.abstractclass.AbstractCrawler;
import com.bolo.crawler.abstractclass.AbstractSpiderListener;
import com.bolo.crawler.entitys.SimpleObject;
import com.bolo.crawler.entitys.Spider;
import com.bolo.crawler.interfaceclass.SpiderListener;
import com.bolo.crawler.poolmanager.SpiderManager;


/**
 * @Author wangyue
 * @Date 19:49
 */
public class ZhongZiCrawler_Controller {



    public void start(){
        try {
            Spider spider = SpiderManager.getInstance().createSpider("test0");
            ZhongZiCrawler crawler = ZhongZiCrawler.class.getConstructor(Spider.class).newInstance(spider);
            crawler.getZhongZi();
            SpiderManager.getInstance().startSpider(spider,savePhoneInfo(crawler,null),crawler);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    protected SpiderListener savePhoneInfo(final AbstractCrawler crawler,final String currentUser){
        return new AbstractSpiderListener(Spider.buildListenerContext(), 12 * 60 * 1000) {
            @Override
            public void onStartup(SimpleObject context, Object obj) {

            }
            @Override
            public void onEvent(String event, SimpleObject context, Object obj) {

            }
            @Override
            public void onComplete(SimpleObject contenxt, Object obj) {
                try {
                    logger.info("task finish" + obj);
                } catch (Exception e) {
                    logger.error("saveSpiderListener Error", e);
                }
            }
        };
    }
}
