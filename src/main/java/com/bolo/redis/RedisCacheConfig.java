package com.bolo.redis;

import com.bolo.entity.NotePad;
import com.bolo.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.data.redis.serializer.GenericToStringSerializer;

import java.lang.reflect.Method;


/**
 * @Author wangyue
 * @Date 2017\10\9 0009
 */
@Configuration //相当于XML中配置beans
@EnableCaching
public class RedisCacheConfig extends CachingConfigurerSupport  {

    private volatile JedisConnectionFactory mJedisConnectionFactory;
    private volatile RedisCacheManager mRedisCacheManager;
    private volatile RedisTemplate<String,String> mRedisTemplate;

    @Autowired
    private volatile JedisConnectionFactory connectionFactory;

//    @Bean
    public RedisTemplate<String, String> redisTemplate(){
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<String, String>();
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setValueSerializer(new GenericToStringSerializer<Long>(Long.class));
        return redisTemplate;
    }

//    @Bean
    public RedisTemplate<String, NotePad> redisTemplateNote(){
        RedisTemplate<String, NotePad> redisTemplate = new RedisTemplate<String, NotePad>();
        redisTemplate.setConnectionFactory(connectionFactory);
        return redisTemplate;
    }

//    @Bean
    public RedisTemplate<String, User> redisTemplateUser(){
        RedisTemplate<String, User> redisTemplate = new RedisTemplate<String, User>();
        redisTemplate.setConnectionFactory(connectionFactory);
        return redisTemplate;
    }

    @Bean
    public CacheManager cacheManager(RedisTemplate<?, ?> redisTemplate) {
        mRedisCacheManager = new RedisCacheManager(redisTemplate);
        mRedisCacheManager.setDefaultExpiration(3000); // Sets the default expire time (in seconds)
        return mRedisCacheManager;
    }

    @Bean
    public KeyGenerator customKeyGenerator() {
        return new KeyGenerator() {
            @Override
            public Object generate(Object o, Method method, Object... objects) {
                StringBuilder sb = new StringBuilder();
                sb.append(o.getClass().getName());
                sb.append(method.getName());
                for (Object obj : objects) {
                    sb.append(obj.toString());
                }
                return sb.toString();
            }
        };
    }


    public RedisCacheConfig(){
        super();
    }

    public RedisCacheConfig(JedisConnectionFactory mJedisConnectionFactory, RedisTemplate<String,String> mRedisTemplate,
                            RedisCacheManager mRedisCacheManager) {
        super();
        this.mJedisConnectionFactory = mJedisConnectionFactory;
        this.mRedisTemplate = mRedisTemplate;
        this.mRedisCacheManager = mRedisCacheManager;
    }

    public JedisConnectionFactory redisConnectionFactory() {
        return mJedisConnectionFactory;
    }


    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory cf) {
        mRedisTemplate = new RedisTemplate<String, String>();
        mRedisTemplate.setConnectionFactory(cf);
        return mRedisTemplate;
    }


}
