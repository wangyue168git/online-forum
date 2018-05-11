package com.bolo.crawler.poolmanager;

import com.bolo.crawler.liseners.RedisSpiderListener;
import com.bolo.crawler.liseners.StatisticsSpiderListener;
import com.bolo.crawler.entitys.Spider;
import com.bolo.crawler.interfaceclass.SpiderListener;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * @Author wangyue
 * @Date 2017\9\23
 */
public class SpiderManager {
    protected org.slf4j.Logger logger = LoggerFactory.getLogger("SpiderManager");
    private static AtomicBoolean activateStatistics = new AtomicBoolean(true);
    protected CountableThreadPool threadPool = ThreadPoolManager.getSpiderThreadPool();
    protected ExecutorService  executorService;
    private static  final SpiderManager instance = new SpiderManager(50);

    public static boolean getActivateStatistics(){
        return activateStatistics.get();
    }

    public static void setActivateStatistics(boolean statistics){
       activateStatistics.set(statistics);
    }

    private  SpiderManager(int threadNum){
        if (threadPool == null || threadPool.isShutdown()){
            if (executorService != null && ! executorService.isShutdown()){
                threadPool = new CountableThreadPool(threadNum,executorService);
            }else {
                threadPool = new CountableThreadPool(threadNum);
            }
        }
    }

    public Spider createSpider(String user, String type){
        return createSpider1(user, type);
    }

    private Spider createSpider1(String user, String type) {
        Spider spider = Spider.create();
        spider.addSpiderListener(new RedisSpiderListener(Spider.buildListenerContext()));
        if (getActivateStatistics()) {
            spider.addSpiderListener(StatisticsSpiderListener.getInstance());
        }
        return spider;
    }

    public static SpiderManager getInstance(){
        return instance;
    }

    public void startSpider(final Spider s, final SpiderListener spiderListener, final Object obj){
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                s.start(spiderListener, obj);
            }
        });
    }




}

