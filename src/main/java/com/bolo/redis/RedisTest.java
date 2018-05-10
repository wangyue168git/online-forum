package com.bolo.redis;

import com.bolo.entity.NotePad;
import org.aspectj.lang.annotation.Before;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @Author wangyue
 * @Date 15:01
 */
public class RedisTest {


    static RedisCacheUtil redisCache;
    static String key = "tb_student";
    static String field = "stu_name";
    static String value = "一系列的关于student的信息！";

    public static void main(String[] args) {

        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("service-context.xml");

        context.start();
        redisCache = (RedisCacheUtil) context.getBean("redisCache");

//        redisCache.hset(key,field,value);
//        System.out.println("数据保存成功！");
//        String re = redisCache.hget(key, field);
//        System.out.println("得到的数据：" + re);

        NotePad notePad = new NotePad();
        notePad.setId("wangyue");
        notePad.setTitle("123");
        redisCache.hset("tb_student","wangyue",notePad.toString());
        String re = redisCache.hget("tb_student", "wangyue");
        System.out.println("得到的数据：" + re);
        long size = redisCache.hsize(key);
        System.out.println("数量为：" + size);
    }







}
