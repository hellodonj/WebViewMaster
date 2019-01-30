package com.galaxyschool.app.wawaschool.common;

import android.text.TextUtils;

import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.chat.DemoApplication;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author 作者 shouyi:
 * @version 创建时间：Mar 31, 2015 9:57:01 PM 类说明
 */
public class DateUtils {

    public static final String DATE_PATTERN_yyyy_MM_dd = "yyyy-MM-dd";
    public static final String DATE_PATTERN_yyyy_MM_dd_HH_MM = "yyyy-MM-dd HH:mm";
    public static final String DATE_PATTERN_yyyy_MM_dd_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    public static List<String> getDaysOfWeek(String dateTime, String pattern) {
        List<String> result = null;
        Date curDate = getDate(dateTime, pattern);
        if (curDate != null) {
            result = dateToWeek(curDate);
        }

        return result;
    }

    public static Date getDate(String dateTime, String pattern) {
        Date date = null;
        try {
            dateTime = dateTime.replace("T", "").replace("Z", "");
            SimpleDateFormat df = new SimpleDateFormat(pattern,Locale.CHINA);
            date = df.parse(dateTime);
        } catch (Exception e) {
            // TODO: handle exception
            int i = 0;

            i++;
        }
        return date;
    }

    public static Date getCurDate() {
        Date currentTime = new Date();
        return currentTime;
    }

    public static String getDateStr(Date date, String pattern) {
        SimpleDateFormat df = new SimpleDateFormat(pattern, Locale.CHINA);
        return df.format(date);
    }

    public static String getDateYmdStr() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd",Locale.CHINA);
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    public static String getDateStr(Date date) {
        String pattern = "yyyy-MM-dd HH:mm";
        SimpleDateFormat df = new SimpleDateFormat(pattern,Locale.CHINA);
        return df.format(date);
    }

    public static String getStringToString(String dateString, String patter) {
        Date date = stringToDate(dateString, patter);
        return dateToString(date, patter);
    }

    /**
     * @param mdate the first day of week
     * @return days of the week
     */
    public static List<String> dateToWeek(Date mdate) {
        System.out.println("mdate :" + mdate);
        Calendar calendar = Calendar.getInstance();

        Date fdate;
        List<String> list = new ArrayList();
        Long fTime = mdate.getTime();
        for (int a = 0; a < 7; a++) {
            calendar.setTimeInMillis(fTime);
            list.add("" + calendar.get(Calendar.DAY_OF_MONTH));
            fTime += 24 * 3600000;
        }
        return list;
    }

    public static String getDateString(String str, String pattern) {
        String result = "";
        Date date = getDate(str, pattern);
        if (isToday(str, pattern)) {
            result = "今天";
            if (date != null) {
                result += getDateStr(date, "HH:mm");
            }
        } else {
            Date today = new Date();
            Calendar cToday = Calendar.getInstance();
            Calendar theDay = Calendar.getInstance();
            cToday.setTime(today);
            theDay.setTime(date);
            if (theDay.get(Calendar.DAY_OF_MONTH)
                    - cToday.get(Calendar.DAY_OF_MONTH) == 1) {
                result = "昨天";
            } else {
                result = getDateStr(date, "YY-MM-DD");
            }
        }
        return result;
    }

    public static long getTodayZero() {
        Date date = new Date();
        long l = 24 * 60 * 60 * 1000; // 每天的毫秒数
        // date.getTime()是现在的毫秒数，它 减去 当天零点到现在的毫秒数（
        // 现在的毫秒数%一天总的毫秒数，取余。），理论上等于零点的毫秒数，不过这个毫秒数是UTC+0时区的。
        // 减8个小时的毫秒值是为了解决时区的问题。
        return (date.getTime() - (date.getTime() % l) - 8 * 60 * 60 * 1000);
    }

    public static String transferLongToDate(String dateFormat, Long millSec) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        Date date = new Date(millSec);
        return sdf.format(date);
    }

    public static boolean isToday(String str, String pattern) {
        String today = format(new Date(), pattern);
        return str.contains(today);
    }

    public static String format(Date date, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }

    public static String format(Long millSec, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        Date date = new Date(millSec);
        return sdf.format(date);
    }

    // long转换为Date类型
    // currentTime要转换的long类型的时间
    // formatType要转换的时间格式yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日 HH时mm分ss秒
    public static Date longToDate(long currentTime, String formatType)
            throws ParseException {
        Date dateOld = new Date(currentTime); // 根据long类型的毫秒数生命一个date类型的时间
        String sDateTime = dateToString(dateOld, formatType); // 把date类型的时间转换为string
        Date date = stringToDate(sDateTime, formatType); // 把String类型转换为Date类型
        return date;
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

    // date类型转换为String类型
    // formatType格式为yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日 HH时mm分ss秒
    // data Date类型的时间
    public static String dateToString(Date data, String formatType) {
        return new SimpleDateFormat(formatType).format(data);
    }

    // added by rmpan
    public static String[] weekName = {"日", "一", "二", "三", "四", "五", "六"};

    public static int getMonthDays(int year, int month) {
        if (month > 12) {
            month = 1;
            year += 1;
        } else if (month < 1) {
            month = 12;
            year -= 1;
        }
        int[] arr = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        int days = 0;

        if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
            arr[1] = 29; // 闰年2月29天
        }

        try {
            days = arr[month - 1];
        } catch (Exception e) {
            e.getStackTrace();
        }

        return days;
    }

    public static String getWeek() {
        String mWay;
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        mWay = String.valueOf(c.get(Calendar.DAY_OF_WEEK));
        if ("1".equals(mWay)) {
            mWay = "周日";
        } else if ("2".equals(mWay)) {
            mWay = "周一";
        } else if ("3".equals(mWay)) {
            mWay = "周二";
        } else if ("4".equals(mWay)) {
            mWay = "周三";
        } else if ("5".equals(mWay)) {
            mWay = "周四";
        } else if ("6".equals(mWay)) {
            mWay = "周五";
        } else if ("7".equals(mWay)) {
            mWay = "周六";
        }
        return mWay;

    }

    public static String getWeek(String pTime) {
        String Week = "";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(format.parse(pTime));

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            Week += "星期天";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
            Week += "星期一";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY) {
            Week += "星期二";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY) {
            Week += "星期三";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY) {
            Week += "星期四";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) {
            Week += "星期五";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
            Week += "星期六";
        }
        return Week;
    }

    /**
     * 根据索引获得对应的日期
     *
     * @param time
     * @param index 0：年，1：月，2：日
     * @return
     */
    public static String getDateStr(String time, int index) {
        // 日期格式：2016-06-21 14:14:43
        String result = "";
        String commonDate = "2016-06-21";
        String[] temp = new String[3];
        if (time == null || time.equals("")) {
            return "";
        } else {
            //截取前面的字段:2016-06-21
            time = time.substring(0, commonDate.length());
            if (time != null && !time.equals("")) {
                temp = time.split("-");
                if (temp != null && temp.length > 0) {
                    result = temp[index];
                }
            }
        }
        return result;
    }


    public static String getWeekDayName(String pTime) {
        DemoApplication instance = DemoApplication.getInstance();
        String Week = "";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(format.parse(pTime));

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            Week += instance.getString(R.string.sunday);
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
            Week += instance.getString(R.string.monday);
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY) {
            Week += instance.getString(R.string.tuesday);
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY) {
            Week += instance.getString(R.string.wednesday);
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY) {
            Week += instance.getString(R.string.thursday);
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) {
            Week += instance.getString(R.string.friday);
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
            Week += instance.getString(R.string.saturday);
        }

        return Week;
    }

    public static int getYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    public static int getMonth() {
        return Calendar.getInstance().get(Calendar.MONTH) + 1;
    }

    public static int getCurrentMonthDay() {
        return Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    }

    public static int getWeekDay() {
        return Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
    }

    public static int getHour() {
        return Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
    }

    public static int getMinute() {
        return Calendar.getInstance().get(Calendar.MINUTE);
    }

    public static int[] getWeekSunday(int year, int month, int day, int pervious) {
        int[] time = new int[3];
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);
        c.add(Calendar.DAY_OF_MONTH, pervious);
        time[0] = c.get(Calendar.YEAR);
        time[1] = c.get(Calendar.MONTH) + 1;
        time[2] = c.get(Calendar.DAY_OF_MONTH);
        return time;

    }

    public static int getWeekDayFromDate(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getDateFromString(year, month));
        int week_index = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (week_index < 0) {
            week_index = 0;
        }
        return week_index;
    }

    public static Date getDateFromString(int year, int month) {
        String dateString = year + "-" + (month > 9 ? month : (0 + month))
                + -01;
        Date date = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            date = sdf.parse(dateString);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return date;
    }

    public static String getTomorrow() {
        String result = null;
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        calendar.roll(Calendar.DAY_OF_YEAR, 1);
        result = df.format(calendar.getTime());
        return result;
    }

    public static String getTomorrow(String pattern) {
        String result = null;
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat(pattern);
        calendar.roll(Calendar.DAY_OF_YEAR, 1);
        result = df.format(calendar.getTime());
        return result;
    }

    public static Date parseDateStr(String dateStr, String pattern) {
        if (TextUtils.isEmpty(dateStr) || TextUtils.isEmpty(pattern)) {
            return null;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        try {
            return simpleDateFormat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int compareDate(String date1, String date2) {
        return compareDate(date1, date2, "yyyy-MM-dd");
    }

    public static int compareDate(String date1, String date2, String pattern) {
        DateFormat dateFormat = new SimpleDateFormat(pattern);
        try {
            Date dt1 = dateFormat.parse(date1);
            Date dt2 = dateFormat.parse(date2);
            if (dt1.getTime() > dt2.getTime()) {
                return 1;
            } else if (dt1.getTime() < dt2.getTime()) {
                return -1;
            } else {
                return 0;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return 0;
    }

    public static String getDateStr(String str, String pattern1, String pattern2) {
        try {
            Date date = null;
            try {
                str = str.replace("T", " ").replace("Z", " ");
                SimpleDateFormat df = new SimpleDateFormat(pattern1);
                date = df.parse(str);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (date != null) {
                return getDateStr(date, pattern2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Date getPrevDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int day = calendar.get(Calendar.DATE);
        calendar.set(Calendar.DATE, day - 1);
        return calendar.getTime();
    }

    public static Date getNextDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int day = calendar.get(Calendar.DATE);
        calendar.set(Calendar.DATE, day + 1);
        return calendar.getTime();
    }

    /**
     * 判断两次时间间隔是否大于3分钟
     *
     * @param timeNow
     * @param timeHistory
     * @return
     */
    public static boolean isUp3Minites(long timeNow, long timeHistory) {
        if ((timeNow - timeHistory) / 1000 / 60 > 3) {
            return true;
        }
        return false;
    }

    public static String millSecToDateStr(Long millSec) {
        String dateFormat = "yyyyMMddHHmmssSSS";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        Date date = new Date(millSec);
        return sdf.format(date);
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
     * 毫秒转成日期
     *
     * @param s
     * @return
     */
    public static String stampToDate(long s) {
      /*  TimeZone timeZone;
        if (TextUtils.isEmpty(zone)) {
            timeZone = TimeZone.getDefault();

        } else {
            timeZone = TimeZone.getTimeZone(zone);
        }*/
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        //simpleDateFormat.setTimeZone(timeZone);
        Date date = new Date(s);
        res = simpleDateFormat.format(date);
        return res;
    }


    /**
     * 将日期转换为毫秒数
     * @param s
     * @return
     * @throws ParseException
     */
    public static long dateToStamp(String s) throws ParseException {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = simpleDateFormat.parse(s);
        return date.getTime();
    }

    /**
     * 获取直播的开始时间
     */
    public static String getLive() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyy-MM-dd HH:mm");
        //dateFormat.setTimeZone(TimeZone.getTimeZone(time_zone));

        Date date = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int minute = calendar.get(Calendar.MINUTE);
        calendar.set(Calendar.MINUTE, minute % 10 != 0 ? (minute / 10 + 1) * 10 : minute);


        return dateFormat.format(calendar.getTime());

    }


    /**
     * 获取直播的开始时间
     */
    public static String getLiveStartTime() {

        String startTime = "";

        long time = System.currentTimeMillis();

        Calendar calendar = Calendar.getInstance();
        if (calendar.get(Calendar.MINUTE) % 10 == 0) {

            startTime = stampToDate(time);

        } else {

            long timeStamp = time + 10 * 60 * 1000;

            String start = stampToDate(timeStamp);

            startTime = start.replace(start.substring(start.length() - 1), "0");
        }

        return startTime;

    }


    /**
     * 获取直播的结束时间
     */
    public static String getLiveEndTime(String startTime) {

        String endTime = "";

        try {
            long end = dateToStamp(startTime);

            String s = stampToDate(end + 60 * 60 * 1000);

            endTime = s.replace(s.substring(s.length() - 1), "0");

        } catch (ParseException e) {
        }

        return endTime;
    }

    /**
     * 把格式化过的时间转换毫秒值
     *
     * @param time      时间
     * @param formatSrt 时间格式 如 yyyy-MM-dd
     * @return 当前日期的毫秒值
     */
    public static long getMillis(String time, String formatSrt) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat(formatSrt);
        return format.parse(time).getTime();
    }

    public static List<Long> getWeekDayList(String date, String formatSrt) {
        long M24HOURMS = 86400000;
        // 存放每一天时间的集合
        List<Long> weekMillisList = new ArrayList<Long>();
        long dateMill = 0;
        try {
            // 获取date的毫秒值
            dateMill = getMillis(date, formatSrt);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // Calendar
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dateMill);
        // 本周的第几天
        int weekNumber = calendar.get(Calendar.DAY_OF_WEEK);
        // 获取本周一的毫秒值
        long mondayMill = dateMill - M24HOURMS * (weekNumber - 2);

        for (int i = 0; i < 7; i++) {
            weekMillisList.add(mondayMill + M24HOURMS * i);
        }
        return weekMillisList;
    }

    public static String getWeekDayString(int position,String date,String format){
        return format(getWeekDayList(date,format).get(position),format);
    }
}
