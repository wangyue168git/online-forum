package com.bolo.crawler.queue;

import com.bolo.crawler.entitys.Request;
import com.bolo.crawler.interfaceclass.Scheduler;
import com.bolo.crawler.interfaceclass.Task;


import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @Author wangyue
 * @Date 15:50
 */
public class ScheduleQueue implements Scheduler {

    private BlockingQueue<Request> queue = new LinkedBlockingQueue();

    public void push(Request request,Task task){
        queue.add(request);
    }


    @Override
    public synchronized Request poll(Task task){
        return queue.poll();
    }


}
