package com.bolo.test.crawler;

import com.bolo.crawler.abstractclass.AbstractCrawler;
import com.bolo.crawler.abstractclass.AbstractSpiderListener;
import com.bolo.crawler.entitys.SimpleObject;
import com.bolo.crawler.entitys.Spider;
import com.bolo.crawler.interfaceclass.SpiderListener;
import com.bolo.crawler.poolmanager.SpiderManager;
import com.bolo.entity.Zhongzi;
import com.bolo.mybatis.MyBatisDao;
import com.bolo.mybatis.mapper.ZhongziMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * @Author wangyue
 * @Date 19:49
 */
@Component
public class ZhongZiCrawler_Controller {

    protected Logger logger = LoggerFactory.getLogger("ZhongZiCrawler_Controller");

    @Autowired
    private MyBatisDao myBatisDao;//之前的类也要采用注入

    public void start(){
        try {
            Spider spider = SpiderManager.getInstance().createSpider("test0");
            ZhongZiCrawler crawler = ZhongZiCrawler.class.getConstructor(Spider.class).newInstance(spider);
            crawler.getZhongZi();
            SpiderManager.getInstance().startSpider(spider,saveInfo(crawler,null),crawler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    protected SpiderListener saveInfo(final AbstractCrawler crawler,final String currentUser){
        return new AbstractSpiderListener(Spider.buildListenerContext(), 12 * 60 * 1000) {
            @Override
            public void onStartup(SimpleObject context, Object obj) {
                logger.info("task begin");
            }
            @Override
            public void onEvent(String event, SimpleObject context, Object obj) {
                logger.info("task erro");
            }
            @Override
            public void onComplete(SimpleObject contenxt, Object obj) {
                try {
                    saveZhongziByBatch();
                    logger.info("task finish");
                } catch (Exception e) {
                    logger.error("saveSpiderListener Error", e);
                }
            }
        };
    }


    private void saveZhongziByBatch(){
        //爬取数据保存逻辑
        ZhongziMapper zhongziMapper = myBatisDao.getSqlSession().getMapper(ZhongziMapper.class);
        List<Zhongzi> list = new ArrayList<>();
        Zhongzi zhongzi;
        for (Map.Entry<String,String> entry : ZhongZiCrawler.map.entrySet()){
            zhongzi = new Zhongzi();
            zhongzi.setTitle(entry.getKey());
            zhongzi.setTorrent(entry.getValue());
            list.add(zhongzi);
        }
        zhongziMapper.insertBatch(list);
        logger.info("save finish");
    }

}
