package com.bolo.test.test02;


import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by wangyue on 2018/11/5.
 */
public class SetquUtil<T,V>{

    public static <T, V> void transform(T t, V v) {
        Class cla = v.getClass();
        Class cls = t.getClass();
        Method[] methods = cla.getMethods();
        Method[] methods1 = cls.getMethods();
        for (Method method : methods) {
            if (method.getName().startsWith("set")) {
                for (Method method1 : methods1) {
                    if ((method1.getName().equals("get" +
                            method.getName().replaceFirst("set", ""))) &&
                            method.getParameterTypes()[0].equals(method1.getReturnType())) {
                        try {
                            method.invoke(v, method1.invoke(t));
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    public static void main(String[] args)  {
        SetquUtil setquUtil = new SetquUtil();
        A a = new A();

        A b = new A();
        SetquUtil.transform(a,b);
        System.out.println(b);

    }
}
