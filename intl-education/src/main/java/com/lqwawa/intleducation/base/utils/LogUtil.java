package com.lqwawa.intleducation.base.utils;

/**
 * Created by Administrator on 2015/10/9.
 */
public class LogUtil {

    static boolean flag = false;
    private static final boolean VERBOSE = flag;
    private static final boolean DEBUG = flag;
    private static final boolean INFO = flag;
    private static final boolean WARN = flag;
    private static final boolean ERROR = flag;


    public static void v(String tag, String msg) {
        if (VERBOSE) {
            android.util.Log.v(tag, msg);
        }
    }

    public static void v(String tag, String msg, Throwable tr) {
        if (VERBOSE) {
            android.util.Log.v(tag, msg, tr);
        }
    }

    public static void d(String tag, String msg) {
        if (DEBUG) {
            android.util.Log.d(tag, msg);
        }
    }

    public static void d(String tag, String msg, Throwable tr) {
        if (DEBUG) {
            android.util.Log.d(tag, msg, tr);
        }
    }

    public static void i(String tag, String msg) {
        if (INFO) {
            android.util.Log.i(tag, msg);
        }
    }

    public static void i(String tag, String msg, Throwable tr) {
        if (INFO) {
            android.util.Log.i(tag, msg, tr);
        }
    }

    public static void w(String tag, String msg) {
        if (WARN) {
            android.util.Log.w(tag, msg);
        }
    }

    public static void w(String tag, String msg, Throwable tr) {
        if (WARN) {
            android.util.Log.w(tag, msg, tr);
        }
    }

    public static void w(String tag, Throwable tr) {
        if (WARN) {
            android.util.Log.w(tag, tr);
        }
    }

    public static void e(String tag, String msg) {
        if (ERROR) {
            android.util.Log.e(tag, msg);
        }
    }

    public static void e(String tag, String msg, Throwable tr) {
        if (ERROR) {
            android.util.Log.e(tag, msg, tr);
        }
    }
}
