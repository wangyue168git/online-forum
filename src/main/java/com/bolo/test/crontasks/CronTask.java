package com.bolo.test.crontasks;

import com.bolo.entity.User;
import com.bolo.redis.RedisCacheUtil;
import com.bolo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



/**
 * @Author wangyue
 * @Date 20:35
 */
@Slf4j
@Configuration //Beans配置
@Component //Scheduling Bean注入
@EnableAsync
@EnableScheduling //开启任务调度
public class CronTask {

    private static final Logger logger = LoggerFactory.getLogger(CronTask.class);
    private String jobContent = "--更新权限缓存表--";
    public static Map<String,String> userAuths = new HashMap<String, String>();

    @Autowired
    private RedisCacheUtil redisCacheUtil;
    @Autowired
    private UserService service;

    @Scheduled(initialDelay=5000,fixedDelay=60000*30)
    public void run(){
        List<User> users = service.getUsers();
        for (User user : users) {
            userAuths.put(user.getId(),user.getPermission());
            redisCacheUtil.hsetUserAuth("userAuths",user.getId(),user.getPermission());
        }
        redisCacheUtil.setExpire("userAuths",(long)60000*60);

        if(logger.isInfoEnabled()){
            logger.info(jobContent + "{}","····");
        }

    }

    public void setJobContent(String jobContent) {
        this.jobContent = jobContent;
    }



    public static void main(String[] args) {
        try{
            String xmlPath = System.getProperty("user.dir")  + File.separator + "src/main/resources/quartz.xml";
            new FileSystemXmlApplicationContext(xmlPath);
            logger.info("Run QuartzJob");
        }catch (Exception e) {
            logger.error("Exception occurred during quartz job execution.", e);
        }
    }
}
