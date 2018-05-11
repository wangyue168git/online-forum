package com.bolo.test.test02;

import com.bolo.crawler.abstractclass.AbstractCrawler;
import com.bolo.crawler.entitys.SimpleObject;
import com.bolo.crawler.entitys.Spider;
import com.bolo.crawler.poolmanager.SpiderManager;
import org.apache.http.client.config.CookieSpecs;

import java.io.Serializable;

/**
 * @Author wangyue
 * @Date 17:53
 */
public class Crawler extends AbstractCrawler implements Serializable{

    public Crawler(Spider spider) {
        this.spider = spider;
        spider.getSite().setDomain("www.nx.10086.cn");
        spider.getSite().setRndSleepTime(3);
        spider.getSite().setCycleRetryTimes(2);
        spider.getSite().setTimeOut(28000);
        spider.getSite().setFollowRedirect(true);
        spider.getSite().setCookieSpec(CookieSpecs.BROWSER_COMPATIBILITY);
    }
    public void check() {
        getUrl("https://nx.ac.10086.cn/login", "http://www.10086.cn/nx/index_951_951.html",
            null, null, new CrawlerObserver() {
                @Override
                public void afterRequest(SimpleObject context) throws Exception {
                    logger.info("success");
                }
            }

    );
}

    public static void main(String[] args) {





        Spider spider = SpiderManager.getInstance().createSpider("test", "aaa");
        Spider spider1 = SpiderManager.getInstance().createSpider("tete","3");


        Crawler crawler = new Crawler(spider);
        crawler.check();
        spider1.start();




    }

}
