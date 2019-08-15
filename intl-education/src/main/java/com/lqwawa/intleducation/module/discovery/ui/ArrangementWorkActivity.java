package com.lqwawa.intleducation.module.discovery.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.osastudio.apps.BaseFragmentActivity;

import org.xutils.common.util.DensityUtil;

/**
 * 描述: 作业布置率跳转界面
 * 作者|时间: djj on 2019/8/10 0010 下午 2:11
 */

public class ArrangementWorkActivity extends BaseFragmentActivity {

    private TopBar topBar;

    public static void start(Activity activity,
                             String courseId,
                             String classId,
                             String courseName,
                             Bundle bundle) {
        Intent intent = new Intent(activity, ArrangementWorkActivity.class);
        Bundle args = bundle;
        if (args == null) {
            args = new Bundle();
        }
        args.putString("classId", classId);
        args.putString("courseId", courseId);
        args.putString("courseName", courseName);
        args.putBoolean("isArrangementEnter",true);
        intent.putExtras(args);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arrangement_work);
        topBar = (TopBar) findViewById(R.id.top_bar);
        topBar.setBack(true);

        Fragment fragment = new CourseDetailsItemFragment();
        Bundle args = getIntent().getExtras();

        if (args != null) {
            String courseName = args.getString("courseName");
            topBar.setTitle(courseName);
            topBar.setTitleWide(DensityUtil.dip2px(120));

            fragment.setArguments(args);
        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.activity_body, fragment);
        ft.commit();

    }
}
