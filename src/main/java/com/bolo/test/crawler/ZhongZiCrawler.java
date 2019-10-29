package com.bolo.test.crawler;

import com.bolo.crawler.abstractclass.AbstractCrawler;
import com.bolo.crawler.entitys.Request;
import com.bolo.crawler.entitys.SimpleObject;
import com.bolo.crawler.entitys.Spider;
import com.bolo.crawler.poolmanager.SpiderManager;
import com.bolo.crawler.utils.ContextUtil;
import com.google.common.base.Preconditions;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;


/**
 * @Author wangyue
 * @Date 14:23
 */
public class ZhongZiCrawler extends AbstractCrawler {

    public static Map<String, String> map = new ConcurrentHashMap<>();


    public ZhongZiCrawler(Spider spider) {
        super(spider);
        spider.getSite().setRetryTimes(1);
        spider.getSite().setRndSleepTime(50);
        spider.getSite().setSleepTime(2000);
    }


    public void getZhongZi() {


        getUrl("https://mtop.damai.cn/h5/mtop.damai.wireless.search.search/1.0/?jsv=2.4.16&appKey=12574478&t=1539923951615&sign=1bbb6098a6918ee0be7a202fddd57492&type=originaljson&dataType=json&v=1.0&H5Request=true&AntiCreep=true&AntiFlood=true&api=mtop.damai.wireless.search.search&data=%7B%22cityId%22%3A%220%22%2C%22longitude%22%3A0%2C%22latitude%22%3A0%2C%22pageIndex%22%3A%221%22%2C%22pageSize%22%3A10%2C%22sortType%22%3Anull%2C%22categoryId%22%3A%223%22%2C%22dateType%22%3A%220%22%2C%22startDate%22%3A%22%22%2C%22endDate%22%3A%22%22%2C%22option%22%3A31%2C%22sourceType%22%3A21%2C%22returnItemOption%22%3A3%2C%22dmChannel%22%3A%22pc%40damai_pc%22%7D", null,null,new String[][]{{"cookie","x_hm_tuid=GxdJn/Ac3mqetQDXlqlbk4asnogYhFjW2Mc9ZbxhORT7s1s8bfZ+vGzr7KrOykOm; cookie2=1f6f9bbba6b8bc58149f3e0f1a30ebd0; t=366d45319c56d91268dc46e2d3b6c140; _tb_token_=e1337b593e176; _m_h5_tk=1fb4ee5fdd17b28970d2b033f6eb18fa_1539931484611; _m_h5_tk_enc=f38ec795b153eb6034f007d21718d440; isg=BIeH6B3CkNf6fxSjqLuarWKDFj2RJBcq_VE_Pll0cJY9yKaKYVzrvsX-boiWIDPm"}}, new CrawlerObserver() {
            @Override
            public void afterRequest(SimpleObject context) throws Exception {
                super.afterRequest(context);
            }
        });


        int i = 0;
        while (true) {
            postUrl("https://search.damai.cn/searchajax.html", "https://search.damai.cn/search.htm?spm=a2oeg.home.top.dcategory.591b48d3X9l4dp&order=1", new String[]{null,null,null,null,"UTF-8"
            }, new String[][]{
                    {"keyword", ""},
                    {"ctl", ""},
                    {"cty", "上海"},
                    {"tn", ""},
                    {"sctl", ""},
                    {"singleChar", ""},
                    {"tsg", "0"},
                    {"order", "1"}
            }, new String[][]{{
                    "cookie", "_uab_collina=153984442474211746551871; cna=SBdPFJ4APgECAXxKaTYBi8IV; isg=BJGRzpHZrmgyPsJ8yp-QvPuAo5vrVkk0C0bZ3XMmkdh2GrBsu08BQFq8uC5ZCZ2o; _umdata=65F7F3A2F63DF0200B39371395A0D9ABC16428FFC55762DB307D00045E921433601B861CAA11AF1BCD43AD3E795C914C46976202092E707AE3F308F97EFBE56A; x5sec=7b226d65632d67756964652d7765623b32223a223361343431663336393633343232306338633030643564613735653534613238435048536f4e3446454e326f674a6e45744d4b3944413d3d227d"
            },
                    {"user-agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36"}}, new CrawlerObserver() {
                @Override
                public void afterRequest(SimpleObject context) throws Exception {

                    String simpleObject = ContextUtil.getContent(context);
                    if (!simpleObject.contains("上海")){
                        logger.info("failed");
                    }
                }
            });

            i++;
            if (i > 1){
                break;
            }
        }

//        getUrl("https://www.zhongziso.net/tags", null, build(new CrawlerObserver() {
//            @Override
//            public void afterRequest(SimpleObject context) throws Exception {
//                Document document = ContextUtil.getDocumentOfContent(context);
//                Elements elements = document.select("div.search-item").first().select("a");
//                SimpleObject context1 = new SimpleObject();
//                context1.put(Request.KEY_PRIORITY, 100);
//
//                for (int i = 0; i < elements.size(); i++) {
//                    Element element = elements.get(i);
//                    String href = element.attr("href");
//                    String title = element.attr("title");
//                    getUrl("https://www.zhongziso.net" + href, null, null, null, build(new CrawlerObserver() {
//                        @Override
//                        public void afterRequest(SimpleObject context) throws Exception {
//                            Document document1 = ContextUtil.getDocumentOfContent(context);
//                            Elements elements1 = document1.select("div#content").first().select("h3");
//                            for (Element element1 : elements1) {
//
//                                String url = element1.select("a").first().attr("href");
//                                getUrl("https://www.zhongziso.net" + url, null, build(new CrawlerObserver() {
//                                    @Override
//                                    public void afterRequest(SimpleObject context) throws Exception {
//                                        Document document1 = ContextUtil.getDocumentOfContent(context);
//                                        Elements elements1 = document1.select("div#content").first().select("div.panel-body");
//                                        String torrent = elements1.get(1).select("a").first().attr("href");
//                                        map.put(title, "https://www.zhongziso.net" + torrent);
//                                    }
//                                }));
//                            }
//                        }
//                    }), context1);
//                }
//
//            }
//        }));
    }

    public static String getValue() {
        new Date();
        Instant.now();
        String i = "张三";
        try {
            return i;
        } finally {
            i = "里斯";
        }
    }

    static class A{
         String B;
    }


    public static void syn() throws InterruptedException {
        String a = "";
        synchronized (a){
            a = "1";
            synchronized (a) {
                a.notify();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {

//        syn();
        System.out.println(System.nanoTime());
        System.out.println(System.nanoTime());


        CountDownLatch countDownLatch = new CountDownLatch(1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("task ....");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                countDownLatch.countDown();
            }
        }).start();
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("task finish");
        ReentrantLock lock = new ReentrantLock();
        Condition condition = lock.newCondition();
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("task02 ....");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                lock.lock();
                condition.signal();
                lock.unlock();
            }
        }).start();

        lock.lock();
        condition.await();
        lock.unlock();
        System.out.println("task02 finish");
//        ZhongZiCrawler_Controller zhongZiCrawler_controller = new ZhongZiCrawler_Controller();
//        zhongZiCrawler_controller.start();
//        Spider spider = SpiderManager.getInstance().createSpider("test");
//        ZhongZiCrawler crawler = new ZhongZiCrawler(spider);
//        Spider spider1 = SpiderManager.getInstance().createSpider("te1st");
//        SpiderManager.getInstance().startSpider(spider, null, crawler);
//        SpiderManager.getInstance().startSpider(spider1, null, crawler);
    }
}
