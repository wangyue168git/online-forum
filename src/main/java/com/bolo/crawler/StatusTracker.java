package com.bolo.crawler;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Author wangyue
 * @Date 15:10
 */
public class StatusTracker {
    public final static int STAT_INIT = 0;
    public final static int STAT_STOPPED = 1;
    public final static int STAT_STOPPED_FAIL = 2;
    public final static int STAT_RUNNING = 20;
    public final static int STAT_LOGIN_SUC = 30;
    public final static int STAT_SUC = 40;
    public final static int STAT_WAITING = 99;

    private int status = 0;
    private CountDownLatch latch = new CountDownLatch(1);
    private AtomicBoolean notified = new AtomicBoolean(false);
    public void notifyStatus(){
        latch.countDown();
        notified.set(true);
    }
    public boolean isNotified(){
        return notified.get();
    }

    public void waitStatus() throws InterruptedException {
        latch.await();
    }
    public int getStatus(){
        return status;
    }
    public void setStatus(int status){
        this.status = status;
    }

    public boolean isSuccess(){
        return StatusTracker.STAT_LOGIN_SUC <= getStatus() && getStatus() <= StatusTracker.STAT_SUC;
    }
}
