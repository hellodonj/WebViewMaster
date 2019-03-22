package com.lqwawa.intleducation;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.lqwawa.intleducation.base.MyApplication;
import com.lqwawa.intleducation.common.db.DbHelper;
//import com.lqwawa.intleducation.module.chat.EaseHelper;
import com.lqwawa.intleducation.common.utils.LogUtil;
import com.lqwawa.intleducation.common.utils.SPUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.constant.SharedConstant;
import com.lqwawa.intleducation.factory.throwable.LQUncaughtExceptionHandler;
import com.lqwawa.intleducation.module.user.tool.UserHelper;
import com.lqwawa.lqresviewlib.LqResViewHelper;
import com.osastudio.apps.Config;

import org.xutils.x;

/**
 * Created by XChen on 2016/11/2.
 * email:man0fchina@foxmail.com
 */

public class MainApplication extends MyApplication {
    /**
     * MainApplication 实例
     */
    private static MainApplication mInstance;
    /**
     * 上下文对象
     */
    private static Context mContext = null;
    /**
     * 获得主线程
     */
    private static Thread mMainThread = null;
    /**
     * 获得主线程的id
     */
    private static int mMainThreadId;
    /**
     * 获得主线程的handler
     */
    private static Handler mMainHandler = null;
    /**
     * 获得主线程的Looper
     */
    private static Looper mMainLooper = null;

    private static boolean IsAssistant;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        mContext = this;
        mMainThread = Thread.currentThread();
        mMainThreadId = android.os.Process.myTid();
        mMainHandler = new Handler();
        mMainLooper = getMainLooper();

        // 初始化工具类UIUtil
        UIUtil.init(this);
        //打开关闭日志阀
        if(Config.DEBUG){
            LogUtil.getInstance().openLogBrake();
        }else{
            LogUtil.getInstance().closeLogBrake();
        }

        initXUtils3();//
        //AreaHelper.init();
        UserHelper.getUserInfo();
        LqResViewHelper.init(this);
        //EaseHelper.getInstance().init(this);

        Thread.setDefaultUncaughtExceptionHandler(new LQUncaughtExceptionHandler());
    }

    public static MainApplication getInstance() {
        return mInstance;
    }

    public static Context getApplication() {
        return mContext;
    }

    public static Thread getMainThread() {
        return mMainThread;
    }

    public static int getMainThreadId() {
        return mMainThreadId;
    }

    public static Handler getMainThreadHandler() {
        return mMainHandler;
    }

    private void initXUtils3(){
        x.Ext.init(this);//初始化xUtils3
        x.Ext.setDebug(false); //是否输出debug日志，开启debug会影响性能。
        DbHelper.init();//初始化数据库
    }

    public static boolean appIsLQMOOC(){
        //return false;
        return getInstance().getPackageName().equals("com.lqwawa.intleducation");
    }

    public static boolean isTutorialMode(){
        return SPUtil.getInstance().getBoolean(SharedConstant.KEY_APPLICATION_MODE);
    }



    public static boolean isIsAssistant() {
        return IsAssistant;
    }

    public static void setIsAssistant(boolean isAssistant) {
        IsAssistant = isAssistant;
    }
}
