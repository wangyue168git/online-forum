package com.bolo.test.test02;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * Created by wangyue on 2019/9/5.
 */
public class NonReentrantLock implements Lock {

    static class Sync extends AbstractQueuedSynchronizer {

        @Override
        public boolean isHeldExclusively() {
            return getState() == 1;
        }

        @Override
        public boolean tryAcquire(int arg) {
            assert arg == 1;
            if (compareAndSetState(0, 1)) {
                setExclusiveOwnerThread(Thread.currentThread());
                return true;
            }
            return false;
        }

        @Override
        protected boolean tryRelease(int arg) {
            assert arg ==1;
            //如果同步器同步器状态等于0,则抛出监视器非法状态异常
            if(getState() == 0)
                throw new IllegalMonitorStateException();
            //设置独占锁的线程为null
            setExclusiveOwnerThread(null);
            //设置同步状态为0
            setState(0);
            return true;
        }


        Condition newCondition(){
            return new ConditionObject();
        }
    }

    private Sync sync = new Sync();



    @Override
    public void lock() {
        sync.acquire(1);
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        sync.acquireInterruptibly(1);
    }

    @Override
    public boolean tryLock() {
        return true;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return sync.tryAcquireNanos(1, unit.toNanos(time));
    }

    @Override
    public void unlock() {
        sync.release(1);
    }

    @Override
    public Condition newCondition() {
        return sync.newCondition();
    }


    private static NonReentrantLock nonReentrantLock = new NonReentrantLock();

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            Thread thread = new Thread(() -> {
                nonReentrantLock.lock();
                try {
                    System.out.println(Thread.currentThread().getName());
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    nonReentrantLock.unlock();
                }
            });
            thread.start();
        }
    }
}
