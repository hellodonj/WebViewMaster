package com.osastudio.apps;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.osastudio.common.library.ActivityStack;
import com.osastudio.common.utils.LogUtils;

public class BaseCompatActivity extends AppCompatActivity {

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

    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;
    }

}
