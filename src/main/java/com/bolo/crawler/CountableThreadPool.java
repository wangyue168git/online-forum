package com.bolo.crawler;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author wangyue
 * @Date 10:16
 */
public class CountableThreadPool {
    private int threadNum;

    private AtomicInteger threadAlive = new AtomicInteger();

    private ReentrantLock lock = new ReentrantLock();

    private Condition condition = lock.newCondition();

    private ExecutorService executorService;

    public CountableThreadPool(int threadNum){
        this.threadNum = threadNum;
        this.executorService = Executors.newCachedThreadPool();
    }

    public CountableThreadPool(int threadNum,ExecutorService executorService){
        this.threadNum = threadNum;
        this.executorService = executorService;
    }

    public int getThreadNum(){
        return threadNum;
    }

    public int getThreadAlive(){
        return threadAlive.get();
    }
    public void execute(final Runnable runnable){
        execute1(runnable);
    }

    private void execute1(Runnable runnable) {
        if (threadAlive.get() >= threadNum){
            try{
                lock.lock();
                while(threadAlive.get() >= threadNum){
                    try{
                        condition.await();
                    } catch (InterruptedException e) {
                    }
                }
            }finally {
                lock.unlock();
            }
        }
        threadAlive.incrementAndGet();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try{
                    runnable.run();
                }finally {
                    try{
                        lock.lock();
                        threadAlive.decrementAndGet();
                        condition.signal();
                    }finally{
                        lock.unlock();
                    }
                }
            }
        });
    }

    public <T> Future<T> submit(Callable<T> task){
        return executorService.submit(task);
    }
    public boolean isShutdown(){
        return executorService.isShutdown();
    }
    public void shutdown(){
        executorService.shutdown();
    }

}
