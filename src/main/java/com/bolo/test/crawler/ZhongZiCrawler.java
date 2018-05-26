package com.bolo.test.crawler;

import com.bolo.crawler.abstractclass.AbstractCrawler;
import com.bolo.crawler.entitys.Request;
import com.bolo.crawler.entitys.SimpleObject;
import com.bolo.crawler.entitys.Spider;
import com.bolo.crawler.poolmanager.SpiderManager;
import com.bolo.crawler.utils.ContextUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @Author wangyue
 * @Date 14:23
 */
public class ZhongZiCrawler extends AbstractCrawler{

    public static Map<String,String> map = new ConcurrentHashMap<>();


    public ZhongZiCrawler(Spider spider){
        super(spider);
        spider.getSite().setRetryTimes(1);
        spider.getSite().setRndSleepTime(1000);
    }


    public void getZhongZi(){
        getUrl("https://www.zhongziso.net/tags",null,build(new CrawlerObserver(){

            @Override
            public void afterRequest(SimpleObject context) throws Exception {
                Document document = ContextUtil.getDocumentOfContent(context);
                Elements elements = document.select("div.search-item").first().select("a");
                SimpleObject context1 = new SimpleObject();
                context1.put(Request.KEY_PRIORITY,100);

               for(int i = 0; i < elements.size(); i++){
                    Element element = elements.get(i);
                    String href = element.attr("href");
                    String title = element.attr("title");
                    getUrl("https://www.zhongziso.net" + href, null,null,null, build(new CrawlerObserver() {
                        @Override
                        public void afterRequest(SimpleObject context) throws Exception {
                            Document document1 = ContextUtil.getDocumentOfContent(context);
                            Elements elements1 = document1.select("div#content").first().select("h3");
                            for (Element element1 : elements1){

                                String url = element1.select("a").first().attr("href");
                                getUrl("https://www.zhongziso.net" + url, null, build(new CrawlerObserver() {
                                    @Override
                                    public void afterRequest(SimpleObject context) throws Exception {
                                        Document document1 = ContextUtil.getDocumentOfContent(context);
                                        Elements elements1 = document1.select("div#content").first().select("div.panel-body");
                                        String torrent = elements1.get(1).select("a").first().attr("href");
                                        map.put(title,"https://www.zhongziso.net" + torrent);
                                    }
                                }));
                            }
                        }
                    }),context1);
                }

            }
        }));
    }




    public static void main(String[] args) throws Exception {
        ZhongZiCrawler_Controller zhongZiCrawler_controller = new ZhongZiCrawler_Controller();
        zhongZiCrawler_controller.start();

        Spider spider = SpiderManager.getInstance().createSpider("test");
        ZhongZiCrawler crawler = new ZhongZiCrawler(spider);
        Spider spider1 = SpiderManager.getInstance().createSpider("te1st");
//        long before = System.currentTimeMillis();
//        crawler.initForTest();
        //crawler.getZhongZi();
        SpiderManager.getInstance().startSpider(spider,null,crawler);
        SpiderManager.getInstance().startSpider(spider1,null,crawler);
//        spider.start();
//        spider1.start();
//        long after = System.currentTimeMillis();
//        System.out.println(ZhongZiCrawler.map.size());
//        System.out.println(after-before);
//        crawler.destory();
//        System.out.println(ThreadPoolManager.getCrawlerThreadPool().isShutdown());
//        ThreadPoolManager.getCrawlerThreadPool().shutdown();

    }
}
