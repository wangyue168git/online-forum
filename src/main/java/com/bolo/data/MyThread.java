package com.bolo.data;

/**
 * @Author wangyue
 * @Date 17:39
 */
public class MyThread extends Thread {
    public void run(){
        super.run();
        try{
            for (int i = 0; i < 100000; i++){
                System.out.println("i=" + (i+1));
            }
            System.out.println("run begin");
            Thread.sleep(200000);
            System.out.println("run end");
        }catch (InterruptedException e){
            System.out.println("先停止");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        MyThread thread = new MyThread();
        thread.start();
        thread.interrupt();
        System.out.println("end");
    }
}
