package com.bolo.crawler.abstractclass;

import com.bolo.crawler.interfaceclass.Downloader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author wangyue
 * @Date 15:06
 */
public abstract class AbstractDownloader implements Downloader{
    protected Logger logger = LoggerFactory.getLogger("Downloader");
    protected int sleepTime = 0;

    public Downloader setSleepTime(int sleepTime) {
        this.sleepTime = sleepTime;
        return this;
    }
    protected void sleep(int time) {
        if (time <= 0) {
            return;
        }
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
