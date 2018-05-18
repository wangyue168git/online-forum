package com.bolo.crawler.queue;

/**
 * @Author wangyue
 * @Date 18:08
 */
public class ScheduleQueueManager {
    private static final ScheduleQueue scheduleQueue = new ScheduleQueue();
    public static final ScheduleQueue getScheduleQueue(){
        return scheduleQueue;
    }
}
