package com.bolo.test.auther;

import com.bolo.redis.RedisCacheUtil;
import com.bolo.test.reqlimit.RequestLimitException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * @Author wangyue
 * @Date 14:54
 */
@Aspect
@Component
public class MyAuthManager {

    private static final Logger log = LoggerFactory.getLogger("MyAuthManager");

    @Autowired
    private RedisCacheUtil redisCacheUtil;

    @Before("within(@org.springframework.stereotype.Controller *) && @annotation(authManage)")
    public void getAuthManager(JoinPoint joinPoint, AuthManage authManage){
        try {
            Object[] args = joinPoint.getArgs();
            HttpServletRequest request = null;
            String id = null;

            for (int i = 0; i < args.length; i++){
                if(args[i] instanceof HttpServletRequest){
                    request = (HttpServletRequest) args[i];
                    break;
                }
            }
            if(request == null){
                throw  new RequestLimitException("方法中缺失HttpServletRequest参数");
            }

            if(request.getCookies() != null) {
                for (Cookie cookie : request.getCookies()) {
                    if (cookie.getName().equals("sd")) {
                        id = cookie.getValue();
                        break;
                    }
                }
            }
            if(id != null){
                if(authManage.value().equals("manager")) {
                    if (!redisCacheUtil.hgetUserAuth("userAuths", id).equals("1")) {
                        throw new AuthException();
                    }
                }else if(authManage.value().equals("user")){
                    if(redisCacheUtil.hgetUserAuth("userAuths", id).equals("-1")){
                        throw new AuthException("被禁言，请联系管理员");
                    }
                }
            }else {
                throw new AuthException("未登录用户");
            }

        }catch (AuthException e) {
            throw e;
        }catch (Exception e) {
            log.error("发生异常: ", e);
        }
    }

}
