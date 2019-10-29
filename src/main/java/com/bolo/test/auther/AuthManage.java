package com.bolo.test.auther;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.lang.annotation.*;

/**
 * @Author wangyue
 * @Date 14:37
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
@Order(Ordered.HIGHEST_PRECEDENCE)//最高优先级
public @interface AuthManage{

    /**
     *
     * 用户角色，默认是普通用户
     */
    String value() default "user";

}
