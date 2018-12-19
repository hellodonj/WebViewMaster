package com.lqwawa.intleducation.module.discovery.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ui.MyBaseActivity;
import com.lqwawa.intleducation.base.widgets.TopBar;

public class LqGalaxyIntlActivity extends MyBaseActivity {

    public static boolean isShow = false;
    public static void start(Activity activity){
        if(isShow){
            return;
        }
        isShow = true;
        activity.startActivity(new Intent(activity, LqGalaxyIntlActivity.class));
    }

    private TopBar topBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lq_galaxy_intl);
        topBar = (TopBar) findViewById(R.id.top_bar);
        topBar.setBack(true);
        topBar.setTitle(activity.getResources().getString(R.string.lq_english));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isShow = false;
    }
}
