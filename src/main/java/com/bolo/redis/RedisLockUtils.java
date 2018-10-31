package com.bolo.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import redis.clients.jedis.Jedis;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.UUID;

/**
 * Redis分布式锁 --http://www.importnew.com/27477.html
 * @Author wangyue
 * @Date 16:44
 */
public class RedisLockUtils {

    private static final Logger log = LoggerFactory.getLogger(RedisLockUtils.class);
    private static final String LOCK_SUCCESS = "OK";
    private static final String SET_IF_NOT_EXIST = "NX";
    private static final String SET_WITH_EXPIRE_TIME = "PX";

    private RedisLockUtils() {
    }

    private static boolean isEmpty(CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    public static Boolean tryLock(Jedis jedis, String lockName,String identifier,  int lockTimeout) {
        return tryLock(jedis, lockName,identifier,lockTimeout, 5);
    }

    public static Boolean tryLock(Jedis jedis, String lockName,String identifier,  int lockTimeout, int acquireTimeout) {
        return tryLock(jedis, lockName, identifier,lockTimeout,acquireTimeout, 1000L);
    }

    public static Boolean tryLock(Jedis jedis, String lockName,String identifier,  int lockTimeout, int acquireTimeout, long retryDuration) {
        if (!isEmpty(lockName) && lockTimeout > 0) {
            Long endTime = System.currentTimeMillis() + acquireTimeout*1000L;
            log.info("Try to acquire the lock. lockKey={},acquireTimeout={}s,lockTimeout={}s", new Object[]{lockName, acquireTimeout, lockTimeout});
            do {
                String acquiredLock = jedis.set(lockName, identifier, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, lockTimeout);
                if (LOCK_SUCCESS.equals(acquiredLock)) {
                    log.info("acquired lock. lockKey={}", lockName);
                    return true;
                }
                log.info("Retry to acquire the lock. lockKey={},acquireTimeout={}s,lockTimeout={}s", new Object[]{lockName, acquireTimeout, lockTimeout});
                try {
                    log.info("wait 1000 milliseconds before retry. lockKey={}", lockName);
                    Thread.sleep(retryDuration);
                } catch (InterruptedException ignored) {
                    ;
                }
            } while(System.currentTimeMillis() < endTime);

            return false;
        } else {
            return false;
        }
    }

    public static void unLock(Jedis jedis,  String lockName, String userTag) {
        if (!isEmpty(lockName) && !isEmpty(userTag)) {
            String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            jedis.eval(script, Collections.singletonList(lockName), Collections.singletonList(userTag));
        }
    }
}

