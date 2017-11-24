package com.bolo.redis;

import com.bolo.entity.NotePad;
import com.bolo.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @Author wangyue
 * @Date 14:56
 */
@Component("redisCache") //把普通pojo实例化到spring容器中，相当于配置文件中的<bean id="" class=""/>
public class RedisCacheUtil {

    @Resource
    private RedisTemplate redisTemplate;


    public String hgetUserAuth(String key, String field){
        if(key == null || "".equals(key)){
            return null;
        }
        return (String) redisTemplate.opsForHash().get(key, field);
    }

    public void hsetUserAuth(String key, String field, String value) {
        if(key == null || "".equals(key)){
            return ;
        }
        redisTemplate.opsForHash().put(key, field, value);
    }

    public NotePad hgetNotePad(String key, String field){
        if(key == null || "".equals(key)){
            return null;
        }
        return (NotePad) redisTemplate.opsForHash().get(key, field);
    }

    public void hsetNotePad(String key, String field, NotePad value) {
        if(key == null || "".equals(key)){
            return ;
        }
        redisTemplate.opsForHash().put(key, field, value);
    }

    public User hgetUser(String key, String field){
        if(key == null || "".equals(key)){
            return null;
        }
        return (User) redisTemplate.opsForHash().get(key, field);
    }

    public void hsetUser(String key, String field, User value) {
        if(key == null || "".equals(key)){
            return ;
        }
        redisTemplate.opsForHash().put(key, field, value);
    }

    /**
     * 设置key生命周期
     * @param key
     * @param time
     */
    public void setExpire(String key,long time){
        redisTemplate.expire(key,time, TimeUnit.MILLISECONDS);
    }

    /**
     * 向Hash中添加值
     * @param key      可以对应数据库中的表名
     * @param field    可以对应数据库表中的唯一索引
     * @param value    存入redis中的值
     */
    public void hset(String key, String field, String value) {
        if(key == null || "".equals(key)){
            return ;
        }
        redisTemplate.opsForHash().put(key, field, value);
    }

    /**
     * 从redis中取出值
     * @param key
     * @param field
     * @return
     */
    public String hget(String key, String field){
        if(key == null || "".equals(key)){
            return null;
        }
        return (String) redisTemplate.opsForHash().get(key, field);
    }

    /**
     * 判断 是否存在 key 以及 hash key
     * @param key
     * @param field
     * @return
     */
    public boolean hexists(String key, String field){
        if(key == null || "".equals(key)){
            return false;
        }
        return redisTemplate.opsForHash().hasKey(key, field);
    }

    /**
     * 查询 key中对应多少条数据
     * @param key
     * @return
     */
    public long hsize(String key) {
        if(key == null || "".equals(key)){
            return 0L;
        }
        return redisTemplate.opsForHash().size(key);
    }

    /**
     * 删除
     * @param key
     * @param field
     */
    public void hdel(String key, String field) {
        if(key == null || "".equals(key)){
            return;
        }
        redisTemplate.opsForHash().delete(key, field);
    }

    /**
     * 设置key-value自增
     * @param key
     * @param i
     * @return 返回增加后的值
     */
    public Long increment(String key,int i){
        return redisTemplate.opsForValue().increment(key,i);
    }

    /**
     * 根据范围返回字符串
     * @param key
     * @param var1
     * @param var3
     * @return
     */
    public String getBoundValue(String key,long var1, long var3){
        return redisTemplate.boundValueOps(key).get(var1,var3);
    }
}
