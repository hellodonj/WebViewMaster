package com.lqwawa.intleducation.base.utils;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    public final static String YYYYMMDD = "yyyy-MM-dd";
    public final static String YYYYMMDDHHMMSS = "yyyy-MM-dd HH:mm:ss";
    public final static String YYYYMMDDHHMM = "yyyy-MM-dd HH:mm";
    public final static String YYYYMMDDCH = "yyyy年MM月dd日";

    /***
     * 获得固定格式的format日期
     *
     * @param timeDate
     * @param format
     * @return
     */
    public static String getFormatByStringDate(String timeDate, String format) {
        if (TextUtils.isEmpty(timeDate)) {
            return "";
        }
        SimpleDateFormat simple = new SimpleDateFormat(format);
        Date old = parseToDate(timeDate);
        return simple.format(old);
    }

    public static String getFullTime() {
        return new SimpleDateFormat(YYYYMMDDHHMMSS, Locale.CHINA).format(new Date());
    }

    // string类型转换为date类型
    // strTime要转换的string类型的时间，formatType要转换的格式yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日
    // HH时mm分ss秒，
    // strTime的时间格式必须要与formatType的时间格式相同
    public static Date stringToDate(String strTime, String formatType) {
        SimpleDateFormat formatter = new SimpleDateFormat(formatType);
        Date date = null;
        try {
            date = formatter.parse(strTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String dateToString(Date date, String formatType){
        SimpleDateFormat formatter = new SimpleDateFormat(formatType);
        return formatter.format(date);
    }

    /**
     * 获取某月第一天的日期
     *
     * @return dateString
     */
    public static String getMonthFirstDay(Date date, String patter) {
        if (date == null)
            return null;
        SimpleDateFormat format = new SimpleDateFormat(patter);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return format.format(calendar.getTime());
    }

    /**
     * 获取某月最后一天的日期
     *
     * @return dateString
     */
    public static String getMonthLastDay(Date date, String patter) {
        if (date == null)
            return null;
        SimpleDateFormat format = new SimpleDateFormat(patter);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        return format.format(calendar.getTime());
    }

    /**
     * 字符串时间格式化成Date
     *
     * @param dateString
     * @return
     */
    public static Date parseToDate(String dateString) {
        try {
            return new Date(Long.parseLong(dateString));
        } catch (Exception var4) {
            return new Date();
        }
    }

    public static Date getPrevDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int day = calendar.get(Calendar.DATE);
        calendar.set(Calendar.DATE, day - 1);
        return calendar.getTime();
    }

    public static Date getCurDate() {
        Date currentTime = new Date();
        return currentTime;
    }

    public static Date getNextDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int day = calendar.get(Calendar.DATE);
        calendar.set(Calendar.DATE, day + 1);
        return calendar.getTime();
    }
    public static String getDateYmdStr() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd",Locale.CHINA);
        String dateString = formatter.format(currentTime);
        return dateString;
    }
}
