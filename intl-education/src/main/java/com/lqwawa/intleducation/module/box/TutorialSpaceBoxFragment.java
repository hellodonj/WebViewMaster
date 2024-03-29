package com.lqwawa.intleducation.module.box;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;

import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.MainApplication;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.PresenterFragment;
import com.lqwawa.intleducation.base.helper.NavHelper;
import com.lqwawa.intleducation.common.utils.SPUtil;
import com.lqwawa.intleducation.factory.constant.SharedConstant;
import com.lqwawa.intleducation.factory.event.EventConstant;
import com.lqwawa.intleducation.factory.event.EventWrapper;
import com.lqwawa.intleducation.module.box.course.MyCourseFragment;
import com.lqwawa.intleducation.module.box.tutorial.TutorialSpaceFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * @author medici
 * @desc 帮辅盒子页面，显示我的课程以及帮辅空间的页面
 */
public class TutorialSpaceBoxFragment extends PresenterFragment<TutorialSpaceBoxContract.Presenter>
        implements TutorialSpaceBoxContract.View,NavHelper.OnTabChangedListener<Integer>{

    private static final String LOGIN_ACTION = "MySchoolSpaceFragment_action_load_data";

    public static final int KEY_TUTORIAL_MODE_ID = 0x01;
    public static final int KEY_COURSE_MODE_ID = 0x02;


    private NavHelper<Integer> mNavHelper;
    private boolean isVisible;

    @Override
    protected TutorialSpaceBoxContract.Presenter initPresenter() {
        return new TutorialSpaceBoxPresenter(this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_tutorial_space_box;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        // 初始化辅助工具类
        mNavHelper = new NavHelper<>(this.getContext(), R.id.lay_content,
                getChildFragmentManager(), this);
        mNavHelper.add(KEY_TUTORIAL_MODE_ID, new NavHelper.Tab<>(TutorialSpaceFragment.class, R.string.label_tutorial_space))
                .add(KEY_COURSE_MODE_ID, new NavHelper.Tab<>(MyCourseFragment.class, R.string.label_course_box));
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(getUserVisibleHint()){
            showFragment();
        }
    }

    /**
     * @desc 获取到登录的广播,要刷新UI
     */
    private void showFragment(){
        // 获取当前的模式
        boolean tutorialMode = MainApplication.isTutorialMode();
        if(tutorialMode){
            // 帮辅模式
            mNavHelper.performClickMenu(KEY_TUTORIAL_MODE_ID);
        }else{
            // 我的课程
            mNavHelper.performClickMenu(KEY_COURSE_MODE_ID);
        }
    }

    @Override
    protected void initData() {
        super.initData();
        // 注册刷新数据的广播
        registerBroadcastReceiver();
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onTabChanged(NavHelper.Tab<Integer> newTab, NavHelper.Tab<Integer> oldTab) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventWrapper event){
        if(EventWrapper.isMatch(event,EventConstant.TRIGGER_SWITCH_APPLICATION_MODE)
                /*&& getUserVisibleHint()*/){
            int mode = (int) event.getData();
            if(mode == KEY_TUTORIAL_MODE_ID){
                // 帮辅模式
                mNavHelper.performClickMenu(KEY_TUTORIAL_MODE_ID);
            }else if(mode == KEY_COURSE_MODE_ID){
                // 课程模式
                mNavHelper.performClickMenu(KEY_COURSE_MODE_ID);
            }
        }
    }

    /**
     * 注册广播事件
     */
    protected void registerBroadcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        //登录广播
        myIntentFilter.addAction(LOGIN_ACTION);
        //注册广播
        getContext().registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    /**
     * @author medici
     * @desc BroadcastReceiver
     */
    protected BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(LOGIN_ACTION)) {
                // 获取到登录的广播
                // showFragment();
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        getContext().unregisterReceiver(mBroadcastReceiver);
        if(EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }
    }

    /**
     * 帮辅空间以及我的课程入口
     * @return TutorialBoxFragment
     */
    public static TutorialSpaceBoxFragment newInstance(){
        TutorialSpaceBoxFragment fragment = new TutorialSpaceBoxFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }
}
