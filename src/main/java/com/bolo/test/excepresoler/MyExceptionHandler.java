package com.bolo.test.excepresoler;

import com.bolo.test.reqlimit.RequestLimitException;
import org.apache.shiro.authz.UnauthenticatedException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 自定义异常处理器
 * @Author wangyue
 * @Date 18:58
 */
public class MyExceptionHandler implements HandlerExceptionResolver {

    @Override
    public ModelAndView resolveException(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) {

        Map<String, Object> model = new HashMap<String, Object>();
        model.put("ex", e);

        // 根据不同错误转向不同页面
        if(e instanceof RequestLimitException) {
            if(e.getMessage().equals("该ip已被锁")){
                return new ModelAndView("page/limit_ip.jsp", model);
            }else {
                return new ModelAndView("page/limit_erro.jsp", model);
            }
        }else if(e instanceof UnauthenticatedException) {
            return new ModelAndView("redirect:/lode", model);
        } else {
            return new ModelAndView("page/ex_erro.jsp", model);
        }
    }
}
