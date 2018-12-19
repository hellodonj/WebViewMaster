package com.lqwawa.intleducation.base.ui;


import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.lqwawa.tools.DialogHelper;

public abstract class MyBaseFragment extends Fragment {

    public static final String FRAGMENT_BUNDLE_OBJECT = "ACTIVITY_BUNDLE_OBJECT";

    public boolean isShow = false;
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isShow = isVisibleToUser;
    }

    protected Activity activity;
    protected DialogHelper.LoadingDialog progressDialog;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;

        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            //透明状态栏
//            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            //透明导航栏
//            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        //}
    }

    /**
     * 显示提示框
     */
    protected void showProgressDialog(String msg) {

        if (progressDialog == null) {
            progressDialog = DialogHelper.getIt(activity).GetLoadingDialog(0);
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

}
