package com.bolo.crawler.interfaceclass;

import com.bolo.crawler.entitys.Request;

/**
 * @Author wangyue
 * @Date 14:49
 */
public interface Downloader {
    public void download(Request request, Task task);
    Downloader setSleepTime(int sleepTime);
    Downloader setThread(int thread);
    void close();
}
