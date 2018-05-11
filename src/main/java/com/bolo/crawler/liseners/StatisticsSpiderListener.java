package com.bolo.crawler.liseners;

import com.bolo.crawler.utils.Statistics;
import com.bolo.crawler.abstractclass.AbstractSpiderListener;
import com.bolo.crawler.entitys.Request;
import com.bolo.crawler.entitys.SimpleObject;
import com.bolo.crawler.entitys.Site;
import com.bolo.crawler.interfaceclass.ProcessorObserver;
import com.bolo.crawler.utils.ContextUtil;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @Author wangyue
 * @Date 16:53
 * spider状态监听类
 */
public class StatisticsSpiderListener extends AbstractSpiderListener implements ProcessorObserver {
    public static final int PERIOD_MINUTES = 5;
    /*
     1.统计每个site的访问频率，多少次或多久后会被封，会不会解封
     2.每个时段的url数，每个时段的开始到封（如何确认）
     */
    protected static StatisticsSpiderListener INSTANCE = new StatisticsSpiderListener();
    public static final StatisticsSpiderListener getInstance() {
        return INSTANCE;
    }
    private StatisticsSpiderListener() {

    }

    private final AtomicLong spiderCount = new AtomicLong(0);
    private Map<String, Statistics> siteStatistics = new ConcurrentHashMap<String, Statistics>();

    public void forbiddenSite(Site s) {

    }
    public Statistics getSiteStatistics(Site s) {
        Statistics s1 = siteStatistics.get(s.getDomain());
        if (s1 == null) {

            s1 = new Statistics(s.getDomain(), s.getDetectorUrl());
            siteStatistics.put(s.getDomain(), s1);
        } else {
            //long ts = Math.max(s1.getLastErrorTime(), s1.getLastSuccessTime());

            if (System.currentTimeMillis() - s1.getStartTime() >= PERIOD_MINUTES * 60 * 1000) {
                Statistics s2 = new Statistics(s.getDomain(), s.getDetectorUrl());
                siteStatistics.put(s.getDomain(), s2);
                s2.addSnapShot(s1);
                s1 = s2;
            }
        }
        return s1;
    }
    public Set<Map.Entry<String, Statistics>> entrySet() {
        return siteStatistics.entrySet();
    }
    public long getActiveSpiderCount() {
        return spiderCount.get();
    }
    @Override
    public void onStartup(SimpleObject context, Object obj) {
        spiderCount.incrementAndGet();
    }

    @Override
    public void onComplete(SimpleObject context, Object obj) {
        spiderCount.decrementAndGet();
    }

    @Override
    public void onRequestSuccess(SimpleObject context, Object obj) {

    }

    @Override
    public void onRequestError(SimpleObject context, Object obj) {

    }
    public void reqError(SimpleObject context, String desc) {
        Site site = ContextUtil.getTask(context).getSite();
        Request request = ContextUtil.getRequest(context);
        Statistics s = getSiteStatistics(site);
        s.reqError(request.getUrl(), null, desc);
    }
    @Override
    public void preparedData(SimpleObject context) throws Exception {
        Site site = ContextUtil.getTask(context).getSite();
        if (ContextUtil.getError(context) != null) {
            Request request = ContextUtil.getRequest(context);
            Statistics s = getSiteStatistics(site);
            s.reqError(request.getUrl(), ContextUtil.getError(context), null);
            //logger.error(request.getMethod() == null ? "GET" : request.getMethod() + " page " + request.getUrl(), ContextUtil.getError(context));
        }
        ProcessorObserver po = site.getPreventForbidden();
        if (po != null) {
            po.preparedData(context);
        }

    }
    @Override
    public void beforeRequest(SimpleObject context) throws Exception {
        ProcessorObserver po = ContextUtil.getTask(context).getSite().getPreventForbidden();
        if (po != null) {
            po.beforeRequest(context);
        }
        // TODO Auto-generated method stub

    }
    @Override
    public void afterRequest(SimpleObject context) throws Exception {
        Request request = ContextUtil.getRequest(context);
        Site site = ContextUtil.getTask(context).getSite();
        Statistics s = getSiteStatistics(site);
        s.reqSuccess(request.getUrl());
        ProcessorObserver po = site.getPreventForbidden();
        if (po != null) {
            po.afterRequest(context);
        }

    }

    @Override
    public void breakRequest(Request req) throws Exception {

    }

    @Override
    public String getClassification() {
        return null;
    }
}
