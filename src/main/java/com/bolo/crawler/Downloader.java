package com.bolo.crawler;

/**
 * @Author wangyue
 * @Date 14:49
 */
public interface Downloader {
    public void download(Request request,Task task);
    Downloader setSleepTime(int sleepTime);
    Downloader setThread(int thread);
    void close();
}
