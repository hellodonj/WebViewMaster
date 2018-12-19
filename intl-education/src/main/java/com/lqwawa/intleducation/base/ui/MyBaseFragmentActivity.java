package com.lqwawa.intleducation.base.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.lqwawa.intleducation.MainApplication;
import com.lqwawa.tools.DialogHelper;
import com.osastudio.common.utils.LogUtils;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by XChen on 2016/11/25.
 * emailman0fchina@foxmail.com
 */

public class MyBaseFragmentActivity extends FragmentActivity {

    public static final String ACTIVITY_BUNDLE_OBJECT = "ACTIVITY_BUNDLE_OBJECT";

    protected DialogHelper.LoadingDialog progressDialog;
    protected Activity activity;
    protected boolean hasNavigationBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        activity = this;
        super.onCreate(savedInstanceState);
        MainApplication.getInstance().addActivity(activity);
//        hasNavigationBar = checkDeviceHasNavigationBar(getApplicationContext());
        LogUtils.logd("currentActivity",getClass().getName());
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//        //透明状态栏
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        //透明导航栏
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
    }

    protected void onCreate(Bundle savedInstanceState, boolean transStatusbar) {
        activity = this;
        super.onCreate(savedInstanceState);
        MainApplication.getInstance().addActivity(activity);
        if (transStatusbar) {
//            //透明状态栏
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            //透明导航栏
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        }
    }

    //获取是否存在NavigationBar
    public static boolean checkDeviceHasNavigationBar(Context context) {
        boolean hasNavigationBar = false;
        Resources rs = context.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {

        }
        return hasNavigationBar;
    }

   /* @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (hasNavigationBar) {
            if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav
                                // bar
                                | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                                | View.SYSTEM_UI_FLAG_IMMERSIVE);
            }
        }
        return super.dispatchTouchEvent(ev);
    }
    TimerTask task = new TimerTask() {
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    getWindow().getDecorView().setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav
                                    // bar
                                    | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                                    | View.SYSTEM_UI_FLAG_IMMERSIVE);
                }
            });
        }
    };
    Timer timer = new Timer();
    @Override
    protected void onResume() {
        if (hasNavigationBar) {
            try {
                timer.schedule(task, 3000);
            }catch (Exception e){

            }
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        timer.purge();
    }
*/
    @Override
    protected void onDestroy() {
        super.onDestroy();
//        timer.cancel();
        MainApplication.getInstance().removeActivity(activity);
    }

    /**
     * 显示提示框
     */
    protected void showProgressDialog(String msg) {

        if ((!isFinishing()) && (progressDialog == null)) {
//            progressDialog = new ProgressDialog(activity, ProgressDialog.THEME_HOLO_LIGHT);
            progressDialog = DialogHelper.getIt(this).GetLoadingDialog(0);
        }

        this.progressDialog.setContent(msg);
        this.progressDialog.setCanceledOnTouchOutside(false);
        this.progressDialog.show();
    }

    /**
     * 关闭提示框
     */
    protected void closeProgressDialog() {
        if (progressDialog != null)
            progressDialog.dismiss();
    }

    private static final String TAG = "BaseActivity";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        FragmentManager fm = getSupportFragmentManager();
        int index = requestCode >> 16;
        if (index != 0) {
            index--;
            if (fm.getFragments() == null || index < 0
                    || index >= fm.getFragments().size()) {
                return;
            }
            Fragment frag = fm.getFragments().get(index);
            if (frag == null) {
            } else {
                handleResult(frag, requestCode, resultCode, data);
            }
            return;
        }
    }

    /**
     * 递归调用，对所有子Fragement生效
     *
     * @param frag
     * @param requestCode
     * @param resultCode
     * @param data
     */
    private void handleResult(Fragment frag, int requestCode, int resultCode,
                              Intent data) {
        frag.onActivityResult(requestCode & 0xffff, resultCode, data);
        List<Fragment> frags = frag.getChildFragmentManager().getFragments();
        if (frags != null) {
            for (Fragment f : frags) {
                if (f != null)
                    handleResult(f, requestCode, resultCode, data);
            }
        }
    }

    /**
     * 隐藏软键盘
     */
    public void hideKeyboard() {
        if (activity.getWindow().getAttributes().softInputMode
                != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (activity.getCurrentFocus() != null) {
                InputMethodManager inputManager =
                        (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }
    protected View getRootView()
    {
        return ((ViewGroup)findViewById(android.R.id.content)).getChildAt(0);
    }
}
