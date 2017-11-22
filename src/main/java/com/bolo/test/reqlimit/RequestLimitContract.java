package com.bolo.test.reqlimit;

import com.bolo.redis.RedisCacheUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

/**
 * @Author wangyue
 * @Date 14:27
 */
@Aspect
@Component
public class RequestLimitContract  {

    private static final Logger logger = LoggerFactory.getLogger("RequestLimitContract");

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RedisCacheUtil redisCacheUtil;

    //在@Controller类里的加了@RequestLimit任意连接点
    @Before("within(@org.springframework.stereotype.Controller *) && @annotation(limit)")
    public void requestLimit(final JoinPoint joinPoint,RequestLimit limit) throws RequestLimitException {
        try{
            Object[] args = joinPoint.getArgs();
            HttpServletRequest request = null;
            for (int i = 0; i < args.length; i++){
                if(args[i] instanceof HttpServletRequest){
                    request = (HttpServletRequest) args[i];
                    break;
                }
            }
            if(request == null){
                throw  new RequestLimitException("方法中缺失HttpServletRequest参数");
            }
            String ip = HttpRequestUtil.getIpAddress(request);
            String url = request.getRequestURL().toString();
            String key = "req_limit_".concat(url).concat(ip);
            String key_ip = "req_limit_ip".concat(ip);

            long limit_ip = !redisTemplate.boundValueOps(key_ip).get(0,-1).equals("") ?
                    Long.valueOf(redisTemplate.boundValueOps(key_ip).get(0,-1)) : 0;
            if (limit_ip > 20) {
                throw new RequestLimitException("该ip已被锁");
            }



            //redis设置自增1
            long count = redisTemplate.opsForValue().increment(key,1);
            if(count == 1){
                redisTemplate.expire(key,limit.time(), TimeUnit.MILLISECONDS);
            }
            if(count > limit.count()){

                long count_ip = redisTemplate.opsForValue().increment(key_ip,1);
                if(count_ip == 20){
                    redisTemplate.expire(key_ip,60000*60*2,TimeUnit.MILLISECONDS);
                    Thread.sleep(60000);
                    throw new RequestLimitException("该ip已被锁");
                }

                logger.info("用户IP[" + ip + "]访问地址[" + url + "]超过了限定的次数[" + limit.count() + "]");
                throw  new RequestLimitException();
            }

        }catch (RequestLimitException e) {
            throw e;
        }catch (Exception e) {
            logger.error("发生异常: ", e);
        }

    }
}