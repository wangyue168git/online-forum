package com.bolo.redis;

import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.util.SafeEncoder;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Author wangyue
 * @Date 17:02
 */
public class JedisUtil {
    private Logger log = Logger.getLogger(this.getClass());
    /**缓存生存时间 */
    private final int expire = 60000;
    private static JedisPool jedisPool = null;
    /** 对存储结构为HashMap类型的操作 */
    public Hash HASH;
    /** 对存储结构为List类型的操作 */
    public Lists LISTS;
    public JedisUtil() {

    }
    static {
        JedisPoolConfig config = new JedisPoolConfig();
            /*InfoUtil info = InfoUtil.getInstance();
            config.setMaxActive(Integer.parseInt(info.getInfo(ConstantProperty.redis,"redis.pool.maxActive")));
            config.setMaxIdle(Integer.parseInt(info.getInfo(ConstantProperty.redis,"redis.pool.maxIdle").trim()));
            config.setMaxWait(Integer.parseInt(info.getInfo(ConstantProperty.redis,"redis.pool.maxWait").trim()));
            config.setTestOnBorrow(Boolean.parseBoolean(info.getInfo(ConstantProperty.redis,"redis.pool.testOnBorrow").trim()));
            config.setTestOnReturn(Boolean.parseBoolean(info.getInfo(ConstantProperty.redis,"redis.pool.testOnReturn").trim()));
            //redis如果设置了密码：
            jedisPool = new JedisPool(config, info.getInfo(ConstantProperty.redis,"redis.ip").trim(),
            		Integer.parseInt(info.getInfo(ConstantProperty.redis,"redis.port").trim()),
                    10000,info.getInfo(ConstantProperty.redis,"redis.password").trim());      */
        //redis未设置了密码：
        // jedisPool = new JedisPool(config, JRedisPoolConfig.REDIS_IP,
        //  JRedisPoolConfig.REDIS_PORT);
    }

    public JedisPool getPool() {
        return jedisPool;
    }

    /**
     * 从jedis连接池中获取获取jedis对象
     * @return
     */
    public Jedis getJedis() {
        Jedis jedis = null;
        try {
            return jedisPool.getResource();
        } catch (JedisConnectionException e) {
            jedisPool.returnBrokenResource(jedis);
            e.printStackTrace();
        }
        return null;
    }


    private static final JedisUtil jedisUtil = new JedisUtil();


    /**
     * 获取JedisUtil实例
     * @return
     */
    public static JedisUtil getInstance() {
        return jedisUtil;
    }

    /**
     * 回收jedis
     * @param jedis
     */
    public void returnJedis(Jedis jedis) {
        if (jedis != null) {
            if (jedis.isConnected()) {
                jedisPool.returnBrokenResource(jedis);
            } else {

                jedisPool.returnResource(jedis);
            }
        }
    }


    /**
     * 设置过期时间
     * @author ruan 2013-4-11
     * @param key
     * @param seconds
     */
    public void expire(String key, int seconds) {
        if (seconds <= 0) {
            return;
        }
        Jedis jedis = getJedis();
        jedis.expire(key, seconds);
        returnJedis(jedis);
    }

    /**
     * 设置默认过期时间 ,默认一分钟
     * @author ruan 2013-4-11
     * @param key
     */
    public void expire(String key) {
        expire(key, expire);
    }
    //*******************************************Hash*******************************************//
    public class Hash {

        /**
         * 从hash中删除指定的存储
         * @param String key
         * @param String  fieid 存储的名字
         * @return 状态码，1成功，0失败
         * */
        public long hdel(String key, String fieid) {
            Jedis jedis = getJedis();
            long s = jedis.hdel(key, fieid);
            returnJedis(jedis);
            return s;
        }

        /**
         * 测试hash中指定的存储是否存在
         * @param String key
         * @param String  fieid 存储的名字
         * @return 1存在，0不存在
         * */
        public boolean hexists(String key, String fieid) {
            //ShardedJedis sjedis = getShardedJedis();
            Jedis sjedis = getJedis();
            boolean s = sjedis.hexists(key, fieid);
            returnJedis(sjedis);
            return s;
        }

        /**
         * 返回hash中指定存储位置的值
         *
         * @param byte[] key
         * @param byte[] fieid 存储的名字
         * @return 存储对应的值
         * */
        public byte[] hget(byte[] key, byte[] fieid) {
            //ShardedJedis sjedis = getShardedJedis();
            Jedis sjedis = getJedis();
            byte[] s = sjedis.hget(key, fieid);
            returnJedis(sjedis);
            return s;
        }

        /**
         * 以Map的形式返回hash中的存储和值
         * @param String    key
         * @return Map<Strinig,String>
         * */
        public Map<String, String> hgetAll(String key) {
            //ShardedJedis sjedis = getShardedJedis();
            Jedis sjedis = getJedis();
            Map<String, String> map = sjedis.hgetAll(key);
            returnJedis(sjedis);
            return map;
        }

        /**
         * 添加一个对应关系
         * @param String  key
         * @param String fieid
         * @param String value
         * @return 状态码 1成功，0失败，fieid已存在将更新，也返回0
         * **/
        public long hset(String key, String fieid, byte[] value) {
            Jedis jedis = getJedis();
            long s = jedis.hset(key.getBytes(), fieid.getBytes(), value);
            returnJedis(jedis);
            return s;
        }


        /**
         * 返回指定hash中的所有存储名字,类似Map中的keySet方法
         * @param String key
         * @return Set<String> 存储名称的集合
         * */
        public Set<String> hkeys(String key) {
            //ShardedJedis sjedis = getShardedJedis();
            Jedis sjedis = getJedis();
            Set<String> set = sjedis.hkeys(key);
            returnJedis(sjedis);
            return set;
        }

        /**
         * 获取hash中存储的个数，类似Map中size方法
         * @param String  key
         * @return long 存储的个数
         * */
        public long hlen(String key) {
            //ShardedJedis sjedis = getShardedJedis();
            Jedis sjedis = getJedis();
            long len = sjedis.hlen(key);
            returnJedis(sjedis);
            return len;
        }

        /**
         * 根据多个key，获取对应的value，返回List,如果指定的key不存在,List对应位置为null
         * @param String  key
         * @param String ... fieids 存储位置
         * @return List<String>
         * */
        public List<String> hmget(String key, String... fieids) {
            //ShardedJedis sjedis = getShardedJedis();
            Jedis sjedis = getJedis();
            List<String> list = sjedis.hmget(key, fieids);
            returnJedis(sjedis);
            return list;
        }

        public List<byte[]> hmget(byte[] key, byte[]... fieids) {
            //ShardedJedis sjedis = getShardedJedis();
            Jedis sjedis = getJedis();
            List<byte[]> list = sjedis.hmget(key, fieids);
            returnJedis(sjedis);
            return list;
        }

        /**
         * 添加对应关系，如果对应关系已存在，则覆盖
         * @param Strin   key
         * @param Map <String,String> 对应关系
         * @return 状态，成功返回OK
         * */
        public String hmset(String key, Map<String, String> map) {
            Jedis jedis = getJedis();
            String s = jedis.hmset(key, map);
            returnJedis(jedis);
            return s;
        }

        /**
         * 添加对应关系，如果对应关系已存在，则覆盖
         * @param Strin key
         * @param Map <String,String> 对应关系
         * @return 状态，成功返回OK
         * */
        public String hmset(byte[] key, Map<byte[], byte[]> map) {
            Jedis jedis = getJedis();
            String s = jedis.hmset(key, map);
            returnJedis(jedis);
            return s;
        }

    }

    //*******************************************Lists*******************************************//
    public class Lists {
        /**
         * List长度
         * @param String key
         * @return 长度
         * */
        public long llen(String key) {
            return llen(SafeEncoder.encode(key));
        }

        /**
         * List长度
         * @param byte[] key
         * @return 长度
         * */
        public long llen(byte[] key) {
            //ShardedJedis sjedis = getShardedJedis();
            Jedis sjedis = getJedis();
            long count = sjedis.llen(key);
            returnJedis(sjedis);
            return count;
        }

        /**
         * 覆盖操作,将覆盖List中指定位置的值
         * @param byte[] key
         * @param int index 位置
         * @param byte[] value 值
         * @return 状态码
         * */
        public String lset(byte[] key, int index, byte[] value) {
            Jedis jedis = getJedis();
            String status = jedis.lset(key, index, value);
            returnJedis(jedis);
            return status;
        }

        /**
         * 覆盖操作,将覆盖List中指定位置的值
         * @param key
         * @param int index 位置
         * @param String  value 值
         * @return 状态码
         * */
        public String lset(String key, int index, String value) {
            return lset(SafeEncoder.encode(key), index,
                    SafeEncoder.encode(value));
        }

        /**
         * 获取List中指定位置的值
         * @param String  key
         * @param int index 位置
         * @return 值
         * **/
        public String lindex(String key, int index) {
            return SafeEncoder.encode(lindex(SafeEncoder.encode(key), index));
        }

        /**
         * 获取List中指定位置的值
         * @param byte[] key
         * @param int index 位置
         * @return 值
         * **/
        public byte[] lindex(byte[] key, int index) {
            //ShardedJedis sjedis = getShardedJedis();
            Jedis sjedis = getJedis();
            byte[] value = sjedis.lindex(key, index);
            returnJedis(sjedis);
            return value;
        }

        /**
         * 将List中的第一条记录移出List
         * @param String key
         * @return 移出的记录
         * */
        public String lpop(String key) {
            return SafeEncoder.encode(lpop(SafeEncoder.encode(key)));
        }

        /**
         * 将List中的第一条记录移出List
         * @param byte[] key
         * @return 移出的记录
         * */
        public byte[] lpop(byte[] key) {
            Jedis jedis = getJedis();
            byte[] value = jedis.lpop(key);
            returnJedis(jedis);
            return value;
        }

        /**
         * 将List中最后第一条记录移出List
         *
         * @param byte[] key
         * @return 移出的记录
         * */
        public String rpop(String key) {
            Jedis jedis = getJedis();
            String value = jedis.rpop(key);
            returnJedis(jedis);
            return value;
        }

        /**
         * 向List尾部追加记录
         * @param String key
         * @param String value
         * @return 记录总数
         * */
        public long lpush(String key, String value) {
            return lpush(SafeEncoder.encode(key), SafeEncoder.encode(value));
        }

        /**
         * 向List头部追加记录
         * @param String  key
         * @param String  value
         * @return 记录总数
         * */
        public long rpush(String key, String value) {
            Jedis jedis = getJedis();
            long count = jedis.rpush(key, value);
            returnJedis(jedis);
            return count;
        }

        /**
         * 向List头部追加记录
         * @param String key
         * @param String value
         * @return 记录总数
         * */
        public long rpush(byte[] key, byte[] value) {
            Jedis jedis = getJedis();
            long count = jedis.rpush(key, value);
            returnJedis(jedis);
            return count;
        }

        /**
         * 向List中追加记录
         * @param byte[] key
         * @param byte[] value
         * @return 记录总数
         * */
        public long lpush(byte[] key, byte[] value) {
            Jedis jedis = getJedis();
            long count = jedis.lpush(key, value);
            returnJedis(jedis);
            return count;
        }


    }
}
