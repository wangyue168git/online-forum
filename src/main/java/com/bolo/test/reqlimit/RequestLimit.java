package com.bolo.test.reqlimit;


import org.springframework.core.annotation.Order;

import java.lang.annotation.*;

/**
 * @Author wangyue
 * @Date 11:57
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
@Order(2147483647-1)//第二优先级
public @interface RequestLimit {

    /**
     *
     * 允许访问的次数，默认值MAX_VALUE
     */
    int count() default Integer.MAX_VALUE;

    /**
     *
     * 时间段，单位为毫秒，默认值一分钟
     */
    long time() default 60000;


}
