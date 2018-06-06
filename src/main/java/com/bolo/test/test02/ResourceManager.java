package com.bolo.test.test02;

import java.lang.ref.WeakReference;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author wangyue
 * @Date 14:05
 */
public class ResourceManager {
    private final Semaphore semaphore;

    private boolean resourceArray[];

    private final ReentrantLock lock;

    public ResourceManager(){
        this.resourceArray = new boolean[10];
        this.semaphore = new Semaphore(10,true); //true为公平锁，默认为非公平
        this.lock = new ReentrantLock(true); //true为公平，默认为非公平锁
        for (int i = 0; i < 10; i++){
            resourceArray[i] = true;
        }
    }

    public void useResource(int userId){
        try{
            semaphore.acquire();
            int id = getResourceId();
            System.out.println("userId:" + userId + "正在使用资源，资源id:" + id + "\n");
            Thread.sleep(100);
            resourceArray[id] = true;
        }catch (InterruptedException e){
            e.printStackTrace();
        }finally {
            semaphore.release();
        }
    }

    private int getResourceId(){
        int id = -1;
        lock.lock();
        try{

            for (int i = 0; i < 10; i++){
                if (resourceArray[i]){
                    resourceArray[i] = false;
                    id = i;
                    break;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }

        return id;
    }


    /*
        非静态内部类有一个很大的优点：可以自由使用外部类的变量和方法
        ，静态内部类又名，静态嵌套类，完全独立存在，不需要外部类引用，
        内部类是外部类的一部分，互相了解，互相依赖。内部类对象是以外部类对象存在而作为前提的
        一个与外围对象有关系，一个没有关系。
        非静态的才是真正的内部类，对其外部类有个引用,可以调用外部类的成员变量与方法
        静态嵌套类性能更高
     */
    private class ResourceUser implements Runnable{
        private ResourceManager resourceManager;
        private int userId;


        public ResourceUser(ResourceManager resourceManager,int userId){
            this.resourceManager = resourceManager;
            this.userId = userId;
        }


        @Override
        public void run() {

            System.out.println("userId:" + userId + "准备使用资源。。。\n");
            resourceManager.useResource(userId);
            System.out.println("userId:" + userId + "使用资源完毕。。。\n");
        }


    }

    public static void main(String[] args) throws InterruptedException {

        /**
         * 弱引用，GC()回收
         */
        ResourceManager car = new ResourceManager();
        WeakReference<ResourceManager> weakCar = new WeakReference<>(car);
        int i=0;
        while(true){
            if(weakCar.get()!=null){
                i++;
                System.out.println("Object is alive for "+i+" loops - "+weakCar);
            }else{
                //gc 回收
                System.out.println("Object has been collected.");
                break;
            }
        }

//        ResourceManager resourceManager = new ResourceManager();
//        Thread[] threads = new Thread[100];
//        for (int i = 0; i < 100; i++){
//            Thread thread = new Thread(resourceManager.new ResourceUser(resourceManager,i));
//            threads[i] = thread;
//        }
//
//        for (int i = 0; i < 100; i++){
//            Thread thread = threads[i];
//            try {
//                thread.start();
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//        }

    }


}
