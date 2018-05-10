
package com.bolo.util;

import com.bolo.util.DateFormatUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class DateUtils {
    public static final int PERIOD_TYPE_YEAR = 0;
    public static final int PERIOD_TYPE_MONTH = 1;
    public static final int PERIOD_TYPE_HALFMONTH = 2;
    public static final int PERIOD_TYPE_WEEK = 3;

    public final static long SECOND_MILLIS = 1000;
    public final static long MINUTE_MILLIS = SECOND_MILLIS * 60;
    public final static long HOUR_MILLIS = MINUTE_MILLIS * 60;
    public final static long DAY_MILLIS = HOUR_MILLIS * 24;
    public final static long YEAR_MILLIS = DAY_MILLIS * 365;

    public static String[] getAllMonths(String start, String end) {
        String splitSign = "-";
        String regex = "\\d{4}" + splitSign + "(([0][1-9])|([1][012]))"; //判断YYYY-MM时间格式的正则表达式
        if (!start.matches(regex) || !end.matches(regex)) return new String[0];

        List<String> list = new ArrayList<String>();
        if (start.compareTo(end) > 0) {
            //start大于end日期时，互换
            String temp = start;
            start = end;
            end = temp;
        }

        String temp = start; //从最小月份开始
        while (temp.compareTo(start) >= 0 && temp.compareTo(end) <= 0) {
            list.add(temp); //首先加上最小月份,接着计算下一个月份
            String[] arr = temp.split(splitSign);
            int year = Integer.valueOf(arr[0]);
            int month = Integer.valueOf(arr[1]) + 1;
            if (month > 12) {
                month = 1;
                year++;
            }
            if (month < 10) {//补0操作
                temp = year + splitSign + "0" + month;
            } else {
                temp = year + splitSign + month;
            }
        }

        int size = list.size();
        String[] result = new String[size];
        for (int i = 0; i < size; i++) {
            result[i] = list.get(i);
        }
        return result;
    }

    /*
     * 输出前几个月的信息，包括当前月
     * */
    public static List<String> getMonth(int num) {
        List<String> objectTmp = new ArrayList<String>();
        java.text.DateFormat format2 = new java.text.SimpleDateFormat(
                "yyyy/MM");
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

    /**
     * @param registDate               用户注册时间，为null时monthSize默认取max值
     * @param format                   输出日期格式化形式
     * @param maxMonthNum              最大的月数
     * @param includeCurrentMonthOrNot 是否包含当前月
     * @return
     */
    public static List<String> getMonthsByRegistDate(Date registDate, String format, int maxMonthNum, Boolean includeCurrentMonthOrNot) {
        List<String> months = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        int monthSize = maxMonthNum;
        //如果有注册时间，则计算max是否超出了注册时间范围。
        if (registDate != null) {
            Calendar now = Calendar.getInstance();
            Calendar before = Calendar.getInstance();
            before.setTime(registDate);
            int monthNum = now.get(Calendar.MONTH) - before.get(Calendar.MONTH);
            int yearNum = now.get(Calendar.YEAR) - before.get(Calendar.YEAR);
            int monthAbs = Math.abs(yearNum * 12 + monthNum);
            if (includeCurrentMonthOrNot) {
                monthSize = maxMonthNum > (monthAbs + 1) ? (monthAbs + 1) : maxMonthNum;
            } else {
                monthSize = maxMonthNum > monthAbs ? monthAbs : maxMonthNum;
            }
        }
        Calendar cal = Calendar.getInstance();
        if (includeCurrentMonthOrNot) {
            cal.add(Calendar.MONTH, 1);
        }
        for (int i = 0; i < monthSize; i++) {
            cal.add(Calendar.MONTH, -1);
            Date d = cal.getTime();
            String date = sdf.format(d);
            months.add(date);
        }

        return months;
    }

    /*
     * 输出前几个月的信息，包括当前月,format可以是yyyy/MM
     * */
    public static List<String> getMonths(int num, String format) {
        List<String> objectTmp = new ArrayList<String>();
        java.text.DateFormat format2 = new java.text.SimpleDateFormat(
                format);
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
     * 输出前几个月的信息，不包括当前月,format可以是yyyy/MM
     * */
    public static List<String> getMonthsNotInclude(int num, String format) {
        List<String> objectTmp = new ArrayList<String>();
        java.text.DateFormat format2 = new java.text.SimpleDateFormat(
                format);
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, 0);
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
     * 计算过去某天距今天多少天
     * */
    public static int dayDist(String dateStr) throws Exception {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date date = df.parse(dateStr);
        long timeMillion = new Date().getTime() - date.getTime();
        return (int) (timeMillion / (24l * 60 * 60 * 1000));
    }

    /*
     * 计算上个月的今天
     * */
    public static String getLMDay() {
        java.text.DateFormat format2 = new java.text.SimpleDateFormat(
                "yyyy-MM-dd");
        Calendar c = Calendar.getInstance();

        c.add(Calendar.MONTH, -1);
        Date date = c.getTime();
        String date2 = format2.format(date);

        return date2;
    }

    /*
     * 计算去年的今天
     * */
    public static String getLYDay() {
        java.text.DateFormat format2 = new java.text.SimpleDateFormat(
                "yyyy-MM-dd");
        Calendar c = Calendar.getInstance();

        c.add(Calendar.YEAR, -1);
        Date date = c.getTime();
        String date2 = format2.format(date);
        return date2;
    }

    /*
     * 计算N月前的今天
     * */
    public static String getLMDay(int n) {
        /*java.text.DateFormat format2 = new java.text.SimpleDateFormat(
                "yyyy-MM-dd");
		Calendar c = Calendar.getInstance();

		c.add(Calendar.MONTH, -n);
		Date date = c.getTime();
		String date2 = format2.format(date);

		return date2;*/
        return DateFormatUtils.getLMDay(n);
    }

    /*
     * 计算今天到过去某一天的总共天数
     * */
    public static int caculateDays(Date compareDay) {
        Date today = new Date();
        long todayMilliseconds = today.getTime();

        long compareMilliseconds = compareDay.getTime();
        //获得两个日期之间的毫秒差。
        long differenceMilliseconds = todayMilliseconds - compareMilliseconds;
        //一天的毫秒数
        long oneDayMilliseconds = 24 * 60 * 60 * 1000;
        //除以一天的毫秒数，就是相差的天数。
        int result = (int) (differenceMilliseconds / oneDayMilliseconds);
        return result;
    }

    /*
     *按照格式输出之前每个月的信息
     * */
    public static List<String> getMonthForm(int num, String type) {
		/*List<String> objectTmp = new ArrayList<String>();
		java.text.DateFormat format2 = new java.text.SimpleDateFormat(
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
		return objectTmp;*/
        return DateFormatUtils.getMonthForm(num, type);
    }

    /**
     * @param d
     * @param format 默认 yyyy-MM-dd HH:mm:ss 可自定义
     * @return
     */
    public static String formatDate(Date d, String format) {
		/*if(format==null||"".equals(format)){
			format = "yyyy-MM-dd HH:mm:ss";
		}
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(d);*/
        return DateFormatUtils.formatDate(d, format);
    }

    /*
     *String转Date
     * */
    public static Date StringToDate(String dateStr, String formatStr) {
        DateFormat dd = new SimpleDateFormat(formatStr);
        Date date = null;
        if (!StringUtils.isBlank(dateStr)) {
            try {
                date = dd.parse(dateStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return date;
    }

    /*
     * 将时间戳转换为时间
     */
    public static String stampToDate(long lt,String formatStr){
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatStr);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }

    /**
     * 默认返回yyyyMMddHHmmss，需传入详细时间,dateStr必须传入14位数
     *
     * @param dateStr
     * @return
     */
    public static Date StringToDateAll(String dateStr) {
        DateFormat dd = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = null;
        if (!StringUtils.isBlank(dateStr)) {
            try {
                Pattern compile = Pattern.compile("\\d*");
                Matcher matcher = compile.matcher(dateStr);
                StringBuffer group = new StringBuffer();
                while (matcher.find()) {
                    group.append(matcher.group());
                }
//				if (group.toString().length()!=14){
//					return date;
//				}
                date = dd.parse(group.toString());
            } catch (ParseException e) {
                log.error("日期解析错误" + dateStr);
                e.printStackTrace();
            }
        }
        return date;

    }

    public static Date StringToDateAllByFilter(String dateStr){
        DateFormat dateFormat;
        Date date = null;
        String dateStr1 = null;
        if(StringUtils.isNotBlank(dateStr)){
            String reg = "[^0-9]";
            Pattern pattern = Pattern.compile(reg);
            Matcher matcher = pattern.matcher(dateStr);
            dateStr1 = matcher.replaceAll("").trim();
            try {
                if(dateStr1.length() == 6){
                    dateFormat = new SimpleDateFormat("yyyyMM");
                    date = dateFormat.parse(dateStr1);
                }else if(dateStr1.length() == 8){
                    dateFormat = new SimpleDateFormat("yyyyMMdd");
                    date = dateFormat.parse(dateStr1);
                }else if(dateStr1.length() >= 14){
                    dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                    date = dateFormat.parse(dateStr1);
                }else {
                    log.error("日期解析错误：日期筛选后的数字字符串不为6,8或14位----" + dateStr1);
                }
            } catch (ParseException e) {
                log.error("日期解析错误" + dateStr);
                e.printStackTrace();
            }
        }
        return date;
    }

    /**
     * 判断月份是否相等
     */
    public static Boolean isEqual(String month) {
        Date d = new Date();
        String data = formatDate(d, "yyyyMM");
        Boolean flag = false;
        if (data.equals(month)) {
            flag = true;
        }
        return flag;
    }

    public static Date add(Date date, int field, int amount) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(field, amount);
        return cal.getTime();
    }

    public static Date[] getPeriodByType(Date currentDate, int type) {

        Date fromDate = currentDate;
        Date toDate = currentDate;
        Calendar cal;
        switch (type) {
            case PERIOD_TYPE_YEAR:

                cal = Calendar.getInstance();
                cal.setTime(currentDate);
                cal.set(Calendar.MONTH, 0);
                cal.set(Calendar.DATE, 1);

                fromDate = cal.getTime();

                cal.add(Calendar.YEAR, 1);
                toDate = add(cal.getTime(), Calendar.DATE, -1);
                break;

            case PERIOD_TYPE_MONTH:
                cal = Calendar.getInstance();
                cal.setTime(currentDate);
                cal.set(Calendar.DATE, 1);

                fromDate = cal.getTime();

                cal.add(Calendar.MONTH, 1);
                toDate = add(cal.getTime(), Calendar.DATE, -1);
                break;

            case PERIOD_TYPE_WEEK:
                int dayOfWeek = getWeekDay(currentDate);
                int seg = -1 * dayOfWeek;

                fromDate = add(currentDate, Calendar.DATE, seg);
                toDate = add(fromDate, Calendar.DATE, 6);

                break;

            case PERIOD_TYPE_HALFMONTH:
                int dayOfMonth = getDayOfMonth(currentDate);
                fromDate = add(currentDate, Calendar.DATE, -1 * dayOfMonth + 1);
                if (dayOfMonth > 15) {
                    cal = Calendar.getInstance();
                    cal.setTime(fromDate);
                    cal.add(Calendar.MONTH, 1);
                    toDate = add(cal.getTime(), Calendar.DATE, -1);
                    fromDate = add(fromDate, Calendar.DATE, 15);

                } else {
                    toDate = add(fromDate, Calendar.DATE, 14);
                }

                break;

            default:
                break;
        }

        Date[] period = new Date[2];
        period[0] = fromDate;
        period[1] = toDate;
        return period;
    }

    public static int getDayOfMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        return cal.get(Calendar.DAY_OF_MONTH);
    }

    public static int getWeekDay(Date date) {
        if (date == null) {
            return -1;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return getWeekDay(calendar.get(Calendar.DAY_OF_WEEK));
    }

    public static int getWeekDay(int weekNumber) {
        switch (weekNumber) {
            case Calendar.MONDAY:
                return 1;

            case Calendar.TUESDAY:
                return 2;

            case Calendar.WEDNESDAY:
                return 3;

            case Calendar.THURSDAY:
                return 4;

            case Calendar.FRIDAY:
                return 5;

            case Calendar.SATURDAY:
                return 6;

            case Calendar.SUNDAY:
                return 0;

            default:
                return -1;
        }
    }

    /**
     * 返回几月前的日期字符串   如201408 前两个月就是201406
     *
     * @param d      日期
     * @param format 返回格式
     * @param month  几月前
     * @return
     */
    public static String getBeforeMonth(Date d, String format, int month) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        cal.add(Calendar.MONTH, -month);
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(cal.getTime());
    }

    /**
     * 返回传入月份的天数
     *
     * @param date yyyyMM格式 例如 ：201409
     * @return
     */
    public static int getDaysOfMonth(String date) {
        int days = 0;
        try {
            Calendar rightNow = Calendar.getInstance();
            try {
                rightNow.setTime(DateUtils.StringToDate(date, "yyyyMM")); //要计算你想要的月份，改变这里即可
            } catch (Exception e) {
                e.printStackTrace();
            }
            days = rightNow.getActualMaximum(Calendar.DAY_OF_MONTH);
        } catch (Exception e) {
        }
        return days;
    }


    public static String formatDateMouth(String m) {
        if (m != null && m.length() == 2) {
            String fix1 = m.substring(0, 1);
            String fix2 = m.substring(1, 2);
            if (fix1.equals("0")) {
                return fix2;
            }
            return m;
        }
        return null;
    }

    /**
     * <p>Title: lastDayOfMonth</p>
     * <p>Description: 根据输入的日期返回该月中的最后一天的日期</p>
     *
     * @param d
     * @return
     * @author Jerry Sun
     */
    public static String lastDayOfMonth(String d) {
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM").parse(d);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.roll(Calendar.DAY_OF_MONTH, -1);
        Date time = cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(time);
    }

    /**
     * @param inputFormat 例如 yyyy-MM
     * @param outFormat   例如 yyyy-MM-dd
     * @return
     */
    public static String lastDayOfMonth(String d, String inputFormat, String outFormat) {
        Date date = null;
        try {
            date = new SimpleDateFormat(inputFormat).parse(d);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.roll(Calendar.DAY_OF_MONTH, -1);
        Date time = cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat(outFormat);
        return sdf.format(time);
    }


    /**
     * 求得当月的最后一天 23:59:59 Date类型
     *
     * @param d
     * @return
     */
    public static Date lastDateOfMonth(Date d) {
        Date date = d;
        if (date == null) {
            date = new Date();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.roll(Calendar.DAY_OF_MONTH, -1);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        return cal.getTime();
    }

    /**
     * 求得某月的第一天
     *
     * @param d     Date日期
     * @param value 几月后的月的第一天
     * @return
     */
    public static Date getMonthFirstDay(Date d, int value) {
        Date date = d;
        if (date == null) {
            date = new Date();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.roll(Calendar.DAY_OF_MONTH, value);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        return cal.getTime();
    }

    /**
     * <p>Title: firstDayOfMonth</p>
     * <p>Description: 根据输入的日期返回该月中的第一天的日期</p>
     *
     * @param d
     * @return
     * @author Jerry Sun
     */
    public static String firstDayOfMonth(String d) {
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM").parse(d);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        Date time = cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(time);
    }

    public static String firstDayOfMonth(String d, String inputFormat, String OutputFormat) {
        Date date = null;
        try {
            date = new SimpleDateFormat(inputFormat).parse(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        Date time = cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat(OutputFormat);
        return sdf.format(time);
    }

    public static String getDependCycle(String d, String inputFormat) {
        String format = "yyyy-MM";
        if (inputFormat != null) {
            format = inputFormat;
        }
        String start = DateUtils.firstDayOfMonth(d, format, "yyyy-MM-dd");
        String end = DateUtils.lastDayOfMonth(d, format, "yyyy-MM-dd");
        return start + "至" + end;
    }

    /**
     * <p>Title: nextMonthFirstDay</p>
     * <p>Description: 获得指定日期的下一月的第一天</p>
     *
     * @param d
     * @return
     * @author Jerry Sun
     */
    public static String nextMonthFirstDay(String d) {
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM").parse(d);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, 1);
        Date time = cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(time);
    }

    /**
     * <p>Title: nextMonthLastDay</p>
     * <p>Description: 获得指定日期的下一月的最后一天</p>
     *
     * @param d
     * @return
     * @author Jerry Sun
     */
    public static String nextMonthLastDay(String d) {
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM").parse(d);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, 1);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.roll(Calendar.DAY_OF_MONTH, -1);
        Date time = cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(time);
    }

    /**
     * <p>Title: monthIsEqual</p>
     * <p>Description: 比较date1的月份是否大于date2</p>
     *
     * @param date1
     * @param date2
     * @return
     * @author Jerry Sun
     */
    public static boolean monthIsEqual(String date1, String date2) {
        String pattern = "yyyy-MM";
        SimpleDateFormat sf = new SimpleDateFormat(pattern);
        Date d1 = null, d2 = null;
        try {
            d1 = sf.parse(date1);
            d2 = sf.parse(date2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (d1.getTime() <= d2.getTime()) {
            return true;
        }
        return false;
    }

    /**
     * @return float
     * <p>Title: transform</p>
     * <p>Description: 将HH:mm:ss格式的时间转化为秒</p>
     * @author jiangzongren
     */
    //将HH:mm:ss格式的时间转化为秒
    public static int transform(String time) {
        String temp[] = time.split(":");

        int allSeconds = 0;
        if (temp.length == 3) {
            int hours = Integer.valueOf(temp[0]);
            int minutes = Integer.valueOf(temp[1]);
            int seconds = Integer.valueOf(temp[2]);
            allSeconds = hours * 60 * 60 + minutes * 60 + seconds;
        } else if (temp.length == 2) {
            int minutes = Integer.valueOf(temp[0]);
            int seconds = Integer.valueOf(temp[1]);
            allSeconds = minutes * 60 + seconds;
        } else if (temp.length == 1) {
            int seconds = Integer.valueOf(temp[0]);
            allSeconds = seconds;
        }
        //System.out.println("秒数：" + allSeconds);
        return allSeconds;
    }

    /**
     * <p>Title: getToday</p>
     * <p>Description: 获取今天的日期，并格式化为所指定的日期格式</p>
     *
     * @param format 所需要得到的日期格式
     * @return
     * @author Jerry Sun
     */
    public static String getToday(String format) {
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        String result = sdf.format(date);
        return result;
    }


    /**
     * <p>Title: isSameMonth</p>
     * <p>Description: 比较两个日期是否是相同年月</p>
     *
     * @param date1
     * @param date2
     * @return
     * @author Jerry Sun
     */
    public static boolean isSameMonth(String date1, String date2) {
        return isSameMonth(date1, date2, null);
    }

    public static boolean isSameMonth(String date1, String date2, String format) {
        String pattern = "yyyy-MM";
        if (format != null) {
            pattern = format;
        }

        SimpleDateFormat sf = new SimpleDateFormat(pattern);
        Date d1 = null, d2 = null;
        try {
            d1 = sf.parse(date1);
            d2 = sf.parse(date2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (d1.getTime() == d2.getTime()) {
            return true;
        }
        return false;
    }


    /**
     * <p>Title: strDateToStr</p>
     * <p>Description: 将Tue Oct 21 12:24:26 CST 2014格式的字符串日期转换为yyyyMMdd格式的字符串日期</p>
     *
     * @param strDate 例：Tue Oct 21 12:24:26 CST 2014
     * @param fm      例：yyyyMMdd
     * @return
     * @author Jerry Sun
     */
    public static String strDateToStr(String strDate, String fm) {
        String result = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
            Date date = sdf.parse(strDate);

            SimpleDateFormat dd = new SimpleDateFormat(fm);
            result = dd.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * @param month
     * @return Date
     * @throws
     * @Title: getThisMonthMax
     * @Description: 获取当前月最大日期
     * @author JerrySun
     * @date 2014年11月4日 下午4:03:49
     */
    public static Date getThisMonthMax(String month) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        try {
            cal.setTime(sdf.parse(month + "01"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        cal.set(Calendar.MONTH, -1);
        Date thisMonthMax = cal.getTime();
        return thisMonthMax;
    }

    /**
     * Get the days difference
     */
    public static int daysDiff(Date earlierDate, Date laterDate) {
        if (earlierDate == null || laterDate == null) return 0;

        return (int) ((laterDate.getTime() / DAY_MILLIS) - (earlierDate.getTime() / DAY_MILLIS));
    }

    /**
     * Returns the maximum of two dates. A null date is treated as being less
     * than any non-null date.
     */
    public static Date max(Date d1, Date d2) {
        if (d1 == null && d2 == null) return null;
        if (d1 == null) return d2;
        if (d2 == null) return d1;
        return daysDiff(d1, d2) > 0 ? d2 : d1;
    }

    /**
     * Returns the minimum of two dates. A null date is treated as being greater
     * than any non-null date.
     */
    public static Date min(Date d1, Date d2) {
        if (d1 == null && d2 == null) return null;
        if (d1 == null) return d2;
        if (d2 == null) return d1;
        return daysDiff(d1, d2) < 0 ? d2 : d1;
    }


    /**
     * 增加分钟数
     *
     * @param date
     * @param addTime
     * @return
     */
    public static Date addMinutes(Date date, int addTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//		date.setMinutes(date.getMinutes()+addTime);//给当前时间加5分钟后的时间
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, addTime);
        String finaldate = sdf.format(calendar.getTime());
        return StringToDate(finaldate, "yyyy-MM-dd HH:mm");
    }

    public static String getBeforeDay(Date d, String format, int day) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        cal.add(Calendar.DATE, -day);
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(cal.getTime());
    }

    public static String getAfterDay(Date d, String format, int day) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        cal.add(Calendar.DATE, day);
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(cal.getTime());
    }

    public static String getBeforeMinute(Date d, String format, int minute) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        cal.add(Calendar.MINUTE, -minute);
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(cal.getTime());
    }

    public static String getBeforeHours(Date d, String format, int hours) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        cal.add(Calendar.HOUR, -hours);
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(cal.getTime());
    }

    public static boolean isBeforeMinute(Date d1, Date d2, int minute) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(d1);
        cal.add(Calendar.MINUTE, -minute);
        return cal.getTime().before(d2);

    }

    public static void main(String[] args) {

//		System.out.println(isBeforeMinute(new Date(), DateUtils.StringToDate("2017-08-10 15:40:00", "yyyy-MM-dd HH:mm:ss"), 120));
//        String a = "2001-05-07T09:24:57.235-0700";
////		Date b = DateUtils.StringToDate(a, "yyyy年MM月dd日 HH时mm分ss秒");
//        Date b = DateUtils.StringToDateAll(a);
//        System.out.println(b);
        System.out.println(DateUtils.StringToDateAll("20130112121212121"));
        Calendar cal = Calendar.getInstance();
        cal.set(2017, 2, 20);
        List<String> ls = DateUtils.getMonthsByRegistDate(cal.getTime(), "yyyyMM", 6, true);
        System.out.print(ls.toString());
    }
}
