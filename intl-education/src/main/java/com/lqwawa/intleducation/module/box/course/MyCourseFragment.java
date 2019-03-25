package com.lqwawa.intleducation.module.box.course;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.PresenterFragment;
import com.lqwawa.intleducation.base.widgets.adapter.TabSelectedAdapter;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.KeyboardUtil;
import com.lqwawa.intleducation.module.box.course.inner.MyCourseInnerFragment;
import com.lqwawa.intleducation.module.box.tutorial.TutorialSpaceContract;
import com.lqwawa.intleducation.module.box.tutorial.TutorialSpaceFragment;
import com.lqwawa.intleducation.module.box.tutorial.TutorialSpacePresenter;
import com.lqwawa.intleducation.module.discovery.tool.LoginHelper;
import com.lqwawa.intleducation.module.discovery.ui.mycourse.TabType;
import com.lqwawa.intleducation.module.discovery.ui.mycourse.tab.TabCourseTypeFragment;
import com.lqwawa.intleducation.module.discovery.ui.myonline.MyOnlinePagerFragment;
import com.lqwawa.intleducation.module.discovery.ui.person.mycourse.CourseTitle;
import com.lqwawa.intleducation.module.learn.ui.MyCourseListPagerFragment;
import com.lqwawa.intleducation.module.learn.vo.ChildrenListVo;
import com.lqwawa.intleducation.module.user.tool.UserHelper;
import com.lqwawa.lqbaselib.pojo.MessageEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author medici
 * @desc 我的课程的页面
 */
public class MyCourseFragment extends PresenterFragment<MyCourseContract.Presenter>
        implements MyCourseContract.View{

    private static final String LOGIN_ACTION = "MySchoolSpaceFragment_action_load_data";

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private SlidePagerAdapter mPagerAdapter;

    private List<Fragment> mPagerFragments;
    private List<CourseTitle> mPageArray;

    // 是家长身份
    private boolean isParent;

    @Override
    protected MyCourseContract.Presenter initPresenter() {
        return new MyCoursePresenter(this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_my_course;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mTabLayout = (TabLayout) mRootView.findViewById(R.id.tab_layout);
        mViewPager = (ViewPager) mRootView.findViewById(R.id.view_paper);
    }

    @Override
    public void initData() {
        super.initData();
        // 注册刷新数据的广播
        registerBroadcastReceiver();
        if(UserHelper.isLogin()){
            mPresenter.requestParentChildData();
        }
    }

    @Override
    public void updateParentChildrenData(@Nullable List<ChildrenListVo> childrenListVos) {
        mPageArray = new ArrayList<>();
        mPagerFragments = new ArrayList<>();
        if(EmptyUtil.isNotEmpty(mPagerAdapter)) mPagerAdapter.notifyDataSetChanged();

        if(EmptyUtil.isNotEmpty(childrenListVos)){
            isParent = true;
        }

        {
            // 添加我的
            MyCourseInnerFragment studentFragment = MyCourseInnerFragment.newInstance(UserHelper.getUserId(),null);
            mPagerFragments.add(studentFragment);

            // 添加标题
            CourseTitle studentTitle = new CourseTitle(getString(R.string.label_my),false);
            mPageArray.add(studentTitle);
        }


        if(isParent){
            // 如果是有家长身份
            for (ChildrenListVo vo : childrenListVos) {
                // 添加标题
                CourseTitle title = new CourseTitle(vo.getRealName(),true);
                mPageArray.add(title);
                MyCourseInnerFragment fragment = MyCourseInnerFragment.newInstance(vo.getMemberId(),vo.getSchoolId());
                mPagerFragments.add(fragment);
            }
        }

        mPagerAdapter = new SlidePagerAdapter(getChildFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);

        if(mPagerFragments.size() > 1){
            // 如果当前Fragment数目大于1 说明有我孩子的学程
            mTabLayout.setVisibility(View.VISIBLE);
            mViewPager.setOffscreenPageLimit(mPagerFragments.size());
            mTabLayout.setupWithViewPager(mViewPager);
            // 添加滑动事件
            mTabLayout.addOnTabSelectedListener(new TabSelectedAdapter(){
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    super.onTabSelected(tab);
                    KeyboardUtil.hideSoftInput(getActivity());
                }
            });
        }else{
            mTabLayout.setVisibility(View.GONE);
        }
    }

    private class SlidePagerAdapter extends FragmentStatePagerAdapter {

        public SlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mPagerFragments.get(position);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            return mPagerFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            CourseTitle mCourseTitle = mPageArray.get(position);
            String language = Locale.getDefault().getLanguage();
            return mCourseTitle.getRealName();
        }

        @Override
        public Parcelable saveState() {
            return null;
        }
    }

    /**
     * 注册广播事件
     */
    protected void registerBroadcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        //要接收的类型
        myIntentFilter.addAction(AppConfig.ServerUrl.joinInCourse);
        //登录成功
        myIntentFilter.addAction(AppConfig.ServerUrl.Login);
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
            if(action.equals(LOGIN_ACTION)){
                // 获取到登录的广播
                mPresenter.requestParentChildData();
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        getContext().unregisterReceiver(mBroadcastReceiver);
    }

    /**
     * 我的课程入口
     * @return MyCourseFragment
     */
    public static MyCourseFragment newInstance(){
        MyCourseFragment fragment = new MyCourseFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }
}
