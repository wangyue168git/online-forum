package com.bolo.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

/**
 * Redis分布式锁 --http://www.importnew.com/27477.html
 * @Author wangyue
 * @Date 16:44
 */
public class RedisLockUtils {
    private static final Logger log = LoggerFactory.getLogger(RedisLockUtils.class);

    public RedisLockUtils() {
    }

    private static boolean isEmpty(CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    public static Boolean tryLock(RedisTemplate jedisTemplate, String lockName, int lockTimeout) {
        return tryLock(jedisTemplate, lockName, lockTimeout, 5);
    }

    public static Boolean tryLock(RedisTemplate jedisTemplate, String lockName, int lockTimeout, int acquireTimeout) {
        return tryLock(jedisTemplate, lockName, lockTimeout, acquireTimeout, 1000L);
    }

    public static Boolean tryLock(final RedisTemplate jedisTemplate, String lockName, final int lockTimeout, int acquireTimeout, long retryDuration) {
        if (!isEmpty(lockName) && lockTimeout > 0) {
            final String lockKey = lockName;
            final String identifier = UUID.randomUUID().toString();
            Calendar atoCal = Calendar.getInstance();
            atoCal.add(13, acquireTimeout);
            Date atoTime = atoCal.getTime();
            log.info("Try to acquire the lock. lockKey={},acquireTimeout={}s,lockTimeout={}s", new Object[]{lockName, acquireTimeout, lockTimeout});

            do {
                boolean acquiredLock = ((Boolean)jedisTemplate.execute(new RedisCallback<Boolean>() {
                    public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                        return connection.setNX(jedisTemplate.getStringSerializer().serialize(lockKey), jedisTemplate.getStringSerializer().serialize(identifier));
                    }
                })).booleanValue();
                if (acquiredLock) {
                    jedisTemplate.execute(new RedisCallback<Boolean>() {
                        public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                            return connection.expire(jedisTemplate.getStringSerializer().serialize(lockKey), (long)lockTimeout);
                        }
                    });
                    log.info("acquired lock. lockKey={}", lockKey);
                    return true;
                }

                log.info("Retry to acquire the lock. lockKey={},acquireTimeout={}s,lockTimeout={}s", new Object[]{lockKey, acquireTimeout, lockTimeout});
                if (acquireTimeout < 0) {
                    return false;
                }

                try {
                    log.info("wait 1000 milliseconds before retry. lockKey={}", lockKey);
                    Thread.sleep(retryDuration);
                } catch (InterruptedException var12) {
                    ;
                }
            } while(!(new Date()).after(atoTime));

            return false;
        } else {
            return false;
        }
    }

    public static void unLock(final RedisTemplate jedisTemplate, final String lockName) {
        if (!isEmpty(lockName)) {
            jedisTemplate.execute(new RedisCallback<Void>() {
                public Void doInRedis(RedisConnection connection) throws DataAccessException {
                    connection.del(new byte[][]{jedisTemplate.getStringSerializer().serialize(lockName)});
                    return null;
                }
            });
        }
    }
}

