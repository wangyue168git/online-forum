package com.bolo.redis;

import com.bolo.crawler.Request;
import org.apache.shiro.crypto.hash.Hash;
import redis.clients.jedis.Jedis;

/**
 * @Author wangyue
 * @Date 17:01
 */
public class Redis {
    /**
     * 设置过期时间
     * @param key
     * @param seconds
     */
    public static void expire(String key, int seconds) {
        JedisUtil.getInstance().expire(key, seconds);
    }


    public static long sadd(String key, Request request){
        Jedis jedis = JedisUtil.getInstance().getJedis();
        return jedis.sadd(key.getBytes(),SerializeUtil.serialize(request));
    }

    public static Request spop(String key){
        Jedis jedis = JedisUtil.getInstance().getJedis();
        byte[] bytes = jedis.spop(key.getBytes());
        if (bytes == null){
            return null;
        }
        return (Request) SerializeUtil.unserialize(bytes);
    }

    public static void addRequests(String key,Request...requests){
        for (Request request:requests) {
            sadd(key, request);
        }
    }

    public static Request getRequest(String key){
        return spop(key);
    }

    /**
     * 无过期时间,一般设置过期时间
     * @param String  key
     * @param String value
     * @return String 操作状态
     * */
    public static String setEx(String key, String value) {
        Jedis jedis = JedisUtil.getInstance().getJedis();
        String str = jedis.set(key,value);
        JedisUtil.getInstance().returnJedis(jedis);
        return str;
    }
    /**
     * 无过期时间,一般设置过期时间
     * @param String key
     * @param Object obj
     * @return String 操作状态
     * */
    public static String setEx(String key,Object obj) {
        Jedis jedis = JedisUtil.getInstance().getJedis();
        String str = jedis.set(key.getBytes(),  SerializeUtil.serialize(obj));
        JedisUtil.getInstance().returnJedis(jedis);
        return str;
    }
    /**
     * 需要设置过期时间
     * @param String key
     * @param 字节数组
     * @return String 操作状态
     * */
    public static String setEx(String key,byte[] b,int minute) {
        Jedis jedis = JedisUtil.getInstance().getJedis();
        if(minute==0){
            minute = 10;
        }
        String str = jedis.setex(key.getBytes(), getMinute(minute), b);
        JedisUtil.getInstance().returnJedis(jedis);
        return str;
    }
    /**返回对象*/
    public static byte[] getByte(String key){
        Jedis jedis = JedisUtil.getInstance().getJedis();
        byte[] s = jedis.get(key.getBytes());
        JedisUtil.getInstance().returnJedis(jedis);
        return s;
    }
    /**
     * 无过期时间,一般设置过期时间
     * @param String key
     * @param 字节数组
     * @return String 操作状态
     * */
    public static String setBytes(String key,byte[] b) {
        Jedis jedis = JedisUtil.getInstance().getJedis();
        String str = jedis.set(key.getBytes(),  b);
        JedisUtil.getInstance().returnJedis(jedis);
        return str;
    }

    /**
     * 添加有过期时间的记录
     * @param String  key
     * @param String value
     * @param int seconds 过期时间，以秒为单位
     * @return String 操作状态
     * */
    public static String setEx(String key, String value,int seconds) {
        Jedis jedis = JedisUtil.getInstance().getJedis();
        String str = jedis.setex(key, seconds, value);
        JedisUtil.getInstance().returnJedis(jedis);
        return str;
    }
    /**
     * 添加有过期时间的记录
     * @param String key
     * @param Object obj
     * @param int seconds 过期时间，以秒为单位
     * @return String 操作状态
     * */
    public static String setEx(String key,Object obj, int seconds) {
        String str = null;
        if(obj!=null){
            Jedis jedis = JedisUtil.getInstance().getJedis();
            str = jedis.setex(key.getBytes(), seconds, SerializeUtil.serialize(obj));
            JedisUtil.getInstance().returnJedis(jedis);
        }
        return str;
    }


    /**返回对象*/
    public static Object getObj(String key){
        Jedis jedis = JedisUtil.getInstance().getJedis();
        try {
            byte[] by = jedis.get(key.getBytes());
            if(by!=null){
                return SerializeUtil.unserialize(by);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JedisUtil.getInstance().returnJedis(jedis);
        }
        return null;
    }
    /**返回对象*/
    public static String getString(String key){
        Jedis jedis = JedisUtil.getInstance().getJedis();
        String s = jedis.get(key);
        JedisUtil.getInstance().returnJedis(jedis);
        return s;
    }
    /**
     * 删除keys对应的记录,可以是多个key
     * @param String  ... keys
     * @return 删除的记录数
     * */
    public static long del(String... keys) {
        Jedis jedis = JedisUtil.getInstance().getJedis();
        long count = jedis.del(keys);
        JedisUtil.getInstance().returnJedis(jedis);
        return count;
    }
    /**
     * 删除keys对应的记录
     * @param String key
     * @return 删除的记录数
     * */
    public static long del(String key) {
        Jedis jedis = JedisUtil.getInstance().getJedis();
        long count = jedis.del(key);
        JedisUtil.getInstance().returnJedis(jedis);
        return count;
    }
    /**删除以key为前缀的相关数据
     * 删除keys对应的记录,某一类型key 入前缀一样或者后缀一样 a_*
     * @param String key
     * @return 删除的记录数
     * */
    public static long delLike(String key) {
        Jedis jedis = JedisUtil.getInstance().getJedis();
        long count = jedis.del(key+"*");
        JedisUtil.getInstance().returnJedis(jedis);
        return count;
    }
    /**
     * 判断key是否存在
     * @param String key
     * @return boolean
     * */
    public static boolean exists(String key) {
        Jedis jedis = JedisUtil.getInstance().getJedis();
        boolean exis = jedis.exists(key);
        JedisUtil.getInstance().returnJedis(jedis);
        return exis;
    }
    /**设置过期时间
     * @param key
     * @param seconds
     */
    public static void exists(String key, int seconds) {
        JedisUtil.getInstance().expire(key, seconds);
    }
    /**
     * @return jedisUtil.Hash
     */
    public static Hash getHash(){
        return (Hash) JedisUtil.getInstance().new Hash();
    }
    /**
     * @return jedisUtil.List
     */
    public static JedisUtil.Lists getList(){
        return JedisUtil.getInstance().new Lists();
    }
    /**
     * @param min 设置几分钟
     * @return  返回分钟的秒数
     */
    public static int getMinute(int min){
        if(min<=0){
            min=1;
        }
        return 60*min;

    }
    /**
     * @param min 设置几分钟
     * @return  返回分钟的秒数
     */
    public static Integer getHour(int h){
        if(h<=0){
            h=1;
        }
        return 60*60*h;

    }
}
