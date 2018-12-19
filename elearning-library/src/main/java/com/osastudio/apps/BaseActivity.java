package com.osastudio.apps;

import android.app.Activity;
import android.app.Dialog;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;

import com.lqwawa.tools.DialogHelper;
import com.lqwawa.tools.DialogHelper.LoadingDialog;
import com.osastudio.common.library.ActivityStack;
import com.osastudio.common.utils.LogUtils;

public class BaseActivity extends Activity {
    LoadingDialog mLoadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.logd("currentActivity",getClass().getName());
        getActivityStack().add(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        getActivityStack().remove(this);
    }

    protected ActivityStack getActivityStack() {
//        return getBaseApplication().getActivityStack();
        return ActivityStack.getInstance();
    }

    protected BaseApplication getBaseApplication() {
        return (BaseApplication) getApplication();
    }

    public Dialog showLoadingDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            return mLoadingDialog;
        }
        mLoadingDialog = DialogHelper.getIt(this).GetLoadingDialog(0);
        return mLoadingDialog;
    }

    public Dialog showLoadingDialog(String content, boolean cancelable) {
        Dialog dialog = showLoadingDialog();
        ((LoadingDialog) dialog).setContent(content);
        dialog.setCancelable(cancelable);
        return dialog;
    }

    public void dismissLoadingDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }

    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;
    }
}
