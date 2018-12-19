package com.lqwawa.intleducation.common.utils;

import android.util.Log;

import com.lqwawa.intleducation.R;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 日志工具类
 * @date 2018/04/09 14:15
 * @history v1.0
 * **********************************
 */
public class LogUtil {

    /**
     * 打开关闭log打印的闸
     */
    private static boolean logBrake;

    /**
     * 打开log日志打印闸
     */
    public void openLogBrake() {
        logBrake = true;
    }

    /**
     * 关闭log日志打印闸
     */
    public void closeLogBrake(){
        logBrake = false;
    }

    /**
     * 获取log打印tag
     * @param cls
     * @return
     */
    public static String makeLogTag(Class cls) {
        return UIUtil.getString(R.string.app_name) + cls.getSimpleName();
    }

    public static LogUtil getInstance(){
        return new LogUtil();
    }

    /**
     * 打印错误日志
     * @param eLog
     */
    public static void e(Class cls,String eLog){
        if(logBrake)
            Log.e(makeLogTag(cls), eLog);
    }

    /**
     * 打印消息日志
     * @param iLog
     */
    public static void i(Class cls,String iLog){
        if(logBrake)
            Log.i(makeLogTag(cls), iLog);
    }

    /**
     * 打印警告日志
     * @param wLog
     */
    public static void w(Class cls,String wLog){
        if(logBrake)
            Log.w(makeLogTag(cls), wLog);
    }

}
