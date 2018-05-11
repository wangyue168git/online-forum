package com.bolo.crawler.utils;

import com.bolo.crawler.liseners.StatisticsSpiderListener;
import com.bolo.util.DateFormatUtils;
import io.netty.util.internal.ConcurrentSet;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author wangyue
 * @Date 16:54
 */
@Slf4j
public class Statistics {
    private String domainKey;
    private String detectorUrl;
    private final AtomicLong startTime = new AtomicLong(0);

    private final AtomicLong seqErr = new AtomicLong(0);
    private final AtomicLong lastTime = new AtomicLong(0);
    private final AtomicLong lastErrorTime = new AtomicLong(0);

    private final AtomicLong sucCount = new AtomicLong(0);
    private final AtomicLong errCount = new AtomicLong(0);
    private final AtomicLong parseCount = new AtomicLong(0);

    private final AtomicLong detectorCount = new AtomicLong(0);
    //N分钟一个snapshot，根据最新的snapshot控制访问流量
    private Collection<Statistics> snapshot = new ConcurrentSet<Statistics>();

    public Statistics(String domain, String detectorUrl) {
        startTime.set(getLatestPeriodDate(StatisticsSpiderListener.PERIOD_MINUTES));
        this.domainKey = domain;
        this.detectorUrl = detectorUrl;
    }

    public static long getLatestPeriodDate(int periodMinutes) {
        long ts = System.currentTimeMillis() / 1000 / 60;
        long num = ts % periodMinutes;
        if(num != 0) {
            ts = ts - num;
        }
        return ts * 1000 * 60;
    }

    public void addSnapShot(Statistics s) {
        long ts = Math.max(s.getLastErrorTime(), s.getLastSuccessTime());
        if (System.currentTimeMillis() - ts > 30 * 60 * 1000) {
            return;
        }
        if (s.getAllPageCount() > 0) {
            snapshot.add(s);
        }
        for(Statistics s1 : s.snapshot) {
            addSnapShot(s1);
        }
    }
    public void reqSuccess(String url) {
        lastTime.set(System.currentTimeMillis());
        sucCount.incrementAndGet();
    }

    private ReentrantLock errLock = new ReentrantLock();
    private StringBuilder errStat = new StringBuilder();
    public void reqError(String url, Exception err, String errStr) {
        if (lastErrorTime.get() > lastTime.get()) {
            seqErr.incrementAndGet();
        } else {
            seqErr.set(0);
        }
        long se = seqErr.get();
        if (se > 5) {
            log.error(String.format("sequential reqeust error site:%1$s- cnt:%2$s ---------------------------", domainKey, se));
        }
        long ts = System.currentTimeMillis();
        lastErrorTime.set(ts);
        long l = errCount.incrementAndGet();
        if (l < 100) {
            try {
                errLock.lock();
                errStat.append(String.format("err %1$s- ts:%2$s- url:%3$s- message:%4$s\r\n", errCount.get(), dateToString(ts), url, err == null ? errStr : err.getStackTrace()[0].toString()));
            } finally {
                errLock.unlock();
            }
        }
    }
    public void parseError(String url, Exception err) {
        parseCount.incrementAndGet();
    }
    public String getSite() {
        return domainKey;
    }
    public long getLastErrorTime() {
        return lastErrorTime.get();
    }
    public long getDetectorCount() {
        return detectorCount.get();
    }
    public String getDetectorStatus() {
        //ts url exception
        return "";
    }
    public String getErrorStatus() {
        try {
            errLock.lock();
            return errStat.toString();
        } finally {
            errLock.unlock();
        }
        //return "";
    }
    public long getErrorPageCount() {
        return errCount.get();
    }

    public long getSuccessCount() {
        return sucCount.get();
    }
    public long getAllPageCount() {
        return sucCount.get() + errCount.get();
    }
    public double getSecondsPerPage() {
        long l = sucCount.get();
        if (l > 0) {
            return (lastTime.get() - startTime.get()) / 1000.0 / l;
        }
        return 0.0;
    }
    public long getLastSuccessTime() {
        return lastTime.get();
    }
    public long getStartTime() {
        return startTime.get();
    }
    public void setStartTime(long startTime) {
        this.startTime.set(startTime);
    }
    private Statistics copy() {
        Statistics s = new Statistics(domainKey, detectorUrl);
        s.startTime.set(startTime.get());
        s.detectorCount.set(detectorCount.get());
        s.errCount.set(errCount.get());
        //s.errStat
        return s;
    }

    public static String dateToString(long ts) {
        return DateFormatUtils.formatDate(new Date(ts), "yyyy-MM-dd HH:mm:ss");
    }
    public Collection getSnapshot() {
        return snapshot;
    }
}
