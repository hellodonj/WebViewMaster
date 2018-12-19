package com.lqwawa.intleducation.base.utils;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
}
