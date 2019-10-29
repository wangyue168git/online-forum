package com.bolo.test.reqlimit;

import com.bolo.redis.RedisCacheUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;


/**
 * @Author wangyue
 * @Date 14:27
 */
@Aspect
@Component
public class RequestLimitContract  {

    private static final Logger logger = LoggerFactory.getLogger("RequestLimitContract");
    private static final int MAX_LIPCOUNT = 20;
    private static final long TIME_LITP = 60000*60*2;

    @Autowired
    private RedisCacheUtil redisCacheUtil;

    //在@Controller类里的加了@RequestLimit任意连接点
    @Before("within(@org.springframework.stereotype.Controller *) && @annotation(limit)")
    public void requestLimit(final JoinPoint joinPoint,RequestLimit limit) throws RequestLimitException {
        try{
            Object[] args = joinPoint.getArgs();
            HttpServletRequest request = null;
            for (Object arg : args) {
                if (arg instanceof HttpServletRequest) {
                    request = (HttpServletRequest) arg;
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

            long limit_ip = !redisCacheUtil.getBoundValue(key_ip,0,-1).equals("") ?
                    Long.valueOf(redisCacheUtil.getBoundValue(key_ip,0,-1)) : 0;
            if (limit_ip > MAX_LIPCOUNT) {
                throw new RequestLimitException("该ip已被锁");
            }

            //redis设置自增1
            long count = redisCacheUtil.increment(key,1);
            if(count == 1){
                redisCacheUtil.setExpire(key,limit.time());
            }
            if(count > limit.count()){
                long count_ip = redisCacheUtil.increment(key_ip,1);
                if(count_ip == 1){
                    redisCacheUtil.setExpire(key_ip,limit.time() * 2);
                }
                if(count_ip == MAX_LIPCOUNT){
                    redisCacheUtil.setExpire(key_ip,TIME_LITP);
                    Thread.sleep(2000);
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
