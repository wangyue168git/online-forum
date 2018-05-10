package com.bolo.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @Author wangyue
 * @Date 16:55
 */
public class DateFormatUtils {
    /**返回几月前的日期字符串   如201408 前两个月就是201406
     * @param d  日期
     * @param format 返回格式
     * @param month  几月前
     * @return
     */
    public static String getBeforeMonth(Date d, String format, int month){
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -month);
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(cal.getTime());
    }

    public static String getBeforeDay(Date d,String format,int day){
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -day);
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(cal.getTime());
    }

    /**
     * <p>Title: getToday</p>
     * <p>Description: 获取今天的日期，并格式化为所指定的日期格式</p>
     * @author Jerry Sun
     * @param format 所需要得到的日期格式
     * @return
     */
    public static String getToday(String format){
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        String result = sdf.format(date);
        return result;
    }

    /**
     * @param d
     * @param format 默认 yyyy-MM-dd HH:mm:ss 可自定义
     * @return
     */
    public static String formatDate(Date d,String format){
        if(format==null||"".equals(format)){
            format = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(d);
    }

    /*
     *按照格式输出之前每个月的信息
     * */
    public static List<String> getMonthForm(int num, String type){
        List<String> objectTmp = new ArrayList<String>();
        java.text.DateFormat format2 = new SimpleDateFormat(
                type);
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, 1);
        for (int i = 0; i < num; i++) {
            c.add(Calendar.MONTH, -1);
            Date date = c.getTime();
            String date2 = format2.format(date);
            //System.out.println(date2);
            objectTmp.add(date2);
        }
        return objectTmp;
    }

    /*
     * 计算N月前的今天
     * */
    public static String getLMDay(int n){
        java.text.DateFormat format2 = new SimpleDateFormat(
                "yyyy-MM-dd");
        Calendar c = Calendar.getInstance();

        c.add(Calendar.MONTH, -n);
        Date date = c.getTime();
        String date2 = format2.format(date);

        return date2;
    }
}
