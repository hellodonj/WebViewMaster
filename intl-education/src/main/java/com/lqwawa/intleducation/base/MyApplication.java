package com.lqwawa.intleducation.base;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.lqwawa.intleducation.module.home.ui.HomeActivity;
import com.osastudio.apps.BaseApplication;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by XChen on 2016/12/2.
 * email:man0fchina@foxmail.com
 */

public class MyApplication extends BaseApplication {
    /**
     * It is the design ui width.
     */
    private static final int DESIGN_UI_WIDTH = 1920;

    /**
     * It is the desgin ui height.
     */
    private static final int DESIGN_UI_HEIGHT = 1080;

    /**
     * It is the display.
     */
    private static Display mDisplay;

    /**
     * The exact physical pixels per inch of the screen in the X dimension.
     */
    private static float xdpi;

    /**
     * The exact physical pixels per inch of the screen in the Y dimension.
     */
    private static float ydpi;

    /**
     * The absolute width of the display in pixels.
     */
    private static int wpixels;

    /**
     * The absolute height of the display in pixels.
     */
    private static int hpixels;

    /**
     * It is the screen width rate.
     */
    private static float mSWR;

    /**
     * It is the screen height rate.
     */
    private static float mSHR;

    /**
     * It is the screen rate. It is the smaller one between screen width rate and
     * screen height rate.
     */
    private static float mSR;

    private static float mDensity = 1.0f;
    @Override
    public void onCreate() {
        super.onCreate();
        final WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        mDisplay = wm.getDefaultDisplay();

        wpixels = mDisplay.getWidth();
        hpixels = mDisplay.getHeight();

        if (wpixels < hpixels) {
            int temp = hpixels;
            hpixels = wpixels;
            wpixels = temp;
        }

        mSWR = (float) wpixels / (float) DESIGN_UI_WIDTH;
        mSHR = (float) hpixels / (float) DESIGN_UI_HEIGHT;
        mSR = (mSWR > mSHR) ? mSHR : mSWR;

        DisplayMetrics dm = new DisplayMetrics();
        mDisplay.getMetrics(dm);
        mDensity = dm.density;
        xdpi = dm.xdpi;
        ydpi = dm.ydpi;
        wpixels = dm.widthPixels;
        hpixels = dm.heightPixels;
        if (wpixels < hpixels) {
            int temp = hpixels;
            hpixels = wpixels;
            wpixels = temp;
        }
    }

    public static int getHPixels() {
        return hpixels;
    }

    //***********控制Activity start***********************************//
    private List<Activity> activitys = new LinkedList<Activity>();

    /**
     * 添加Activity到容器中
     *
     * @param activity
     */
    public void addActivity(Activity activity) {
        if (!activitys.contains(activity)) {
            activitys.add(activity);
        }
    }

    /**
     * 移除Activity到容器中
     *
     * @param activity
     */
    public void removeActivity(Activity activity) {
        if (activitys.contains(activity)) {
            activitys.remove(activity);
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public Activity getActivity(Class<?> cls) {
        for (Activity activity : activitys) {
            if (activity.getClass().equals(cls)) {
                return activity;
            }
        }
        return null;
    }

    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<?> cls) {
        for (Activity activity : activitys) {
            if (activity.getClass().equals(cls)) {
                activity.finish();
            }
        }
    }

    /**
     * 获取activity列表
     *
     * @return
     */
    public int getActivityList() {
        if (activitys != null && activitys.size() > 1) {
            return activitys.size();
        } else {
            return 0;
        }
    }

    public void finishActivitysWithoutHome(){
        for (Activity activity : activitys) {
            if (!activity.getClass().equals(HomeActivity.class)) {
                activity.finish();
            }
        }
    }

    /**
     * 获取最顶层的activity
     *
     * @return
     */
    public Activity getLastActivity() {
        try {
            if (null == activitys || activitys.size() == 0) {
                return null;
            }
            int lastIndex = activitys.size() - 1;
            return activitys.get(lastIndex);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 退出程序
     */
    @Override
    public void exit() {
        for (Activity activity : activitys) {
            activity.finish();
        }
        System.exit(0);
    }
    //****************控制Activity end***********************************//
}
