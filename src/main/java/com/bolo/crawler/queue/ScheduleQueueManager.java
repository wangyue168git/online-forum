package com.bolo.crawler.queue;

import java.util.TreeMap;
import java.util.concurrent.atomic.LongAdder;

/**
 * @Author wangyue
 * @Date 18:08
 */
public class ScheduleQueueManager {
    private static final ScheduleQueue scheduleQueue = new ScheduleQueue();
    private static final TreeMap<Long, LongAdder> urlMap = new TreeMap<>();

    public static TreeMap<Long, LongAdder> getUrlMap() {
        return urlMap;
    }
    public static final ScheduleQueue getScheduleQueue(){
        return scheduleQueue;
    }
}
