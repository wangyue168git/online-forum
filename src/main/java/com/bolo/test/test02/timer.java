package com.bolo.test.test02;

import com.bolo.mybatis.MyBatisDao;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author wangyue
 * @Date 19:14
 */


public class timer  {

    @Autowired
    private MyBatisDao myBatisDao;

    public void getPhoneBalance(){

    }

    /*****
     * 格式: [秒] [分] [小时] [日] [月] [周] [年]
     0 0 12 * * ?           每天12点触发
     0 15 10 ? * *          每天10点15分触发
     0 15 10 * * ?          每天10点15分触发
     0 15 10 * * ? *        每天10点15分触发
     0 15 10 * * ? 2005     2005年每天10点15分触发
     0 * 14 * * ?           每天下午的 2点到2点59分每分触发
     0 0/5 14 * * ?         每天下午的 2点到2点59分(整点开始，每隔5分触发)
     0 0/5 14,18 * * ?        每天下午的 18点到18点59分(整点开始，每隔5分触发)

     0 0-5 14 * * ?            每天下午的 2点到2点05分每分触发
     0 10,44 14 ? 3 WED        3月分每周三下午的 2点10分和2点44分触发
     0 15 10 ? * MON-FRI       从周一到周五每天上午的10点15分触发
     0 15 10 15 * ?            每月15号上午10点15分触发
     0 15 10 L * ?             每月最后一天的10点15分触发
     0 15 10 ? * 6L            每月最后一周的星期五的10点15分触发
     0 15 10 ? * 6L 2002-2005  从2002年到2005年每月最后一周的星期五的10点15分触发

     0 15 10 ? * 6#3           每月的第三周的星期五开始触发
     0 0 12 1/5 * ?            每月的第一个中午开始每隔5天触发一次
     0 11 11 11 11 ?           每年的11月11号 11点11分触发(光棍节)
     */
//    @Scheduled(cron = "0/5 * * * * ?")
    public void exejob1() {
        System.out.println("dsx" + " ：AAAA 执行中。。。");
    }


    public void timerSchedule(){
        final Timer timer = new Timer();
        final AtomicInteger taskExecuteCnt = new AtomicInteger();

        TimerTask timerTaskA = new TimerTask() {
            @Override
            public void run() {
                if(taskExecuteCnt.get() >= 10){
                    timer.cancel();
                }

                System.out.printf("Task A exexute,current time:%s\n",new Date());
                taskExecuteCnt.incrementAndGet();
            }
        };

        TimerTask timerTaskB = new TimerTask() {
            @Override
            public void run() {
                if (taskExecuteCnt.get() >= 10) {
                    timer.cancel();
                }

                System.out.printf("Task B execute,current time:%s\n", new Date());
                taskExecuteCnt.incrementAndGet();
            }
        };

        timer.scheduleAtFixedRate(timerTaskA, 5000, 500);
        timer.scheduleAtFixedRate(timerTaskB, 6000, 500);//以上一次任务开始时间加上delay时间，即为本次任务开始执行的时间
    }

    public void timerTask_1(){
        final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(2);
        final AtomicInteger taskExecuteCnt = new AtomicInteger();

        Runnable runnableA = new Runnable() {
            @Override
            public void run() {
                // scheduler work content
                if (taskExecuteCnt.get() >= 10) {
                    scheduledExecutorService.shutdownNow();
                }

                System.out.printf("Task A execute,current time:%s\n", new Date());
                taskExecuteCnt.incrementAndGet();
            }
        };

        Runnable runnableB = new Runnable() {
            @Override
            public void run() {
                // scheduler work content
                if (taskExecuteCnt.get() >= 10) {
                    scheduledExecutorService.shutdownNow();
                }

                System.out.printf("Task B execute,current time:%s\n", new Date());
                taskExecuteCnt.incrementAndGet();
            }
        };

        scheduledExecutorService.scheduleWithFixedDelay(runnableA, 0, 500, TimeUnit.MILLISECONDS);
        scheduledExecutorService.scheduleWithFixedDelay(runnableB, 0, 500,TimeUnit.MILLISECONDS);
    }

    public static void main(String[] args) {
        new timer().timerSchedule();
    }

}
