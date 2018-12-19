package com.lqwawa.intleducation.module.discovery.ui.mycourse;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.PresenterActivity;
import com.lqwawa.intleducation.factory.presenter.BaseContract;

/**
 * @author mrmedici
 * @desc 主页面我的课程页面，显示我的自主学习和我的孩子学习
 */
public class TabCourseActivity extends PresenterActivity<TabCourseContract.Presenter>
    implements TabCourseContract.View{

    @Override
    protected TabCourseContract.Presenter initPresenter() {
        return new TabCoursePresenter(this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_tab_course;
    }

    /**
     * 首页我的课程入口
     * @param context 上下文对象
     */
    public static void show(@NonNull Context context){
        Intent intent = new Intent(context,TabCourseActivity.class);
        Bundle bundle = new Bundle();
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}
