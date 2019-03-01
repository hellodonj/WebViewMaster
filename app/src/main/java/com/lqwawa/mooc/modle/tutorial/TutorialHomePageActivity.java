package com.lqwawa.mooc.modle.tutorial;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.galaxyschool.app.wawaschool.R;
import com.lqwawa.intleducation.base.PresenterActivity;

/**
 * @author mrmedici
 * @desc 帮辅模式助教个人主页页面
 */
public class TutorialHomePageActivity extends PresenterActivity<TutorialHomePageContract.Presenter>
    implements TutorialHomePageContract.View{

    @Override
    protected TutorialHomePageContract.Presenter initPresenter() {
        return new TutorialHomePagePresenter(this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_tutorial_home_page;
    }

    @Override
    protected void initData() {
        super.initData();
    }

    /**
     * 申请成为帮辅，注册信息的入口
     * @param context
     */
    public static void show(@NonNull final Context context){
        Intent intent = new Intent(context, TutorialHomePageActivity.class);
        Bundle bundle = new Bundle();
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}
