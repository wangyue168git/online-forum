package com.bolo.test.nettys;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @Author wangyue
 * @Date 19:21
 */
public class AppContext implements ApplicationContextAware
{

    public static final String TCP_SERVER = "tcpServer";

    // The spring application context.
    private static ApplicationContext applicationContext = new ClassPathXmlApplicationContext("servlet-context.xml");

    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException
    {
        AppContext.applicationContext = applicationContext;
    }

    // 根据beanName获取bean
    public static Object getBean(String beanName)
    {
        if (null == beanName)
        {
            return null;
        }
        return applicationContext.getBean(beanName);
    }
}
