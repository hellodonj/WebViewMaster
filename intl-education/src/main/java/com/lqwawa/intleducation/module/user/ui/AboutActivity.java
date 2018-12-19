package com.lqwawa.intleducation.module.user.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ui.MyBaseActivity;
import com.lqwawa.intleducation.base.widgets.TopBar;

public class AboutActivity extends MyBaseActivity implements View.OnClickListener{


    private TopBar topBar;

    public static void start(Activity activity){
        activity.startActivity(new Intent(activity, AboutActivity.class));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        topBar = (TopBar) findViewById(R.id.top_bar);
        topBar.setTitle(getResources().getString(R.string.about_product));
        topBar.setBack(true);
    }

    @Override
    public void onClick(View v) {

    }
}
