package com.bolo.test.crawler;

import com.bolo.crawler.abstractclass.AbstractCrawler;
import com.bolo.crawler.entitys.SimpleObject;
import com.bolo.crawler.entitys.Spider;
import com.bolo.crawler.poolmanager.SpiderManager;
import com.bolo.crawler.utils.ContextUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


/**
 * @Author wangyue
 * @Date 14:23
 */
public class ZhongZiCrawler extends AbstractCrawler{


    public ZhongZiCrawler(Spider spider){
       super(spider);
    }


    public void getZhongZi(){
        getUrl("https://www.zhongziso.net/tags",null,build(new CrawlerObserver(){

            @Override
            public void afterRequest(SimpleObject context) throws Exception {
                Document document = ContextUtil.getDocumentOfContent(context);
                Elements elements = document.select("div.search-item").first().select("a");
                Element element = elements.first();
                if (element!=null){
                    String href = element.attr("href");

                    //String title = element.attr("title");
                    getUrl("https://www.zhongziso.net" + href, null, build(new CrawlerObserver() {
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
                                    }
                                }));
                            }
                        }
                    }));


                }

            }
        }));
    }

    public static void main(String[] args) throws Exception {
        Spider spider = SpiderManager.getInstance().createSpider("test", "aaa");
        ZhongZiCrawler crawler = new ZhongZiCrawler(spider);
        Spider spider1 = SpiderManager.getInstance().createSpider("te1st", "1aaa");

        crawler.initForTest();
        crawler.getZhongZi();
        spider.start();
//        spider1.start();
    }
}
