package com.bolo.test.test02;

import com.bolo.crawler.*;
import org.apache.http.client.config.CookieSpecs;
import org.jsoup.nodes.Document;

import java.io.Serializable;
import java.util.Observable;

/**
 * @Author wangyue
 * @Date 17:53
 */
public class Crawler extends AbstractCrawler implements Serializable{

    public Crawler(Spider spider) {
        this.spider = spider;
        spider.setUseProxy(false);
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
                public void preparedData(SimpleObject context) throws Exception {

                }

                @Override
                public void beforeRequest(SimpleObject context) throws Exception {

                }

                @Override
                public void afterRequest(SimpleObject context) throws Exception {

                    logger.info("success");
                }

                @Override
                public void breakRequest(Request req) throws Exception {

                }

                @Override
                public String getClassification() {
                    return null;
                }
            }

    );
}

    public static void main(String[] args) {





        Spider spider = SpiderManager.getInstance().createSpider("test", "aaa");
        Crawler crawler = new Crawler(spider);
        crawler.check();
        spider.start();




    }

}
