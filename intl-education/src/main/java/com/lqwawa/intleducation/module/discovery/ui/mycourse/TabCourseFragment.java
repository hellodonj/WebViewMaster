package com.lqwawa.intleducation.module.discovery.ui.mycourse;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.IBaseFragment;
import com.lqwawa.intleducation.base.PresenterActivity;
import com.lqwawa.intleducation.base.PresenterFragment;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.KeyboardUtil;
import com.lqwawa.intleducation.module.discovery.ui.mycourse.tab.TabCourseTypeFragment;
import com.lqwawa.intleducation.module.user.tool.UserHelper;
import com.lqwawa.lqbaselib.pojo.MessageEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mrmedici
 * @desc 主页面我的课程页面，显示我的自主学习和我的孩子学习
 */
public class TabCourseFragment extends PresenterFragment<TabCourseContract.Presenter>
    implements TabCourseContract.View, View.OnClickListener{

    private static final String DISSOCIATED_EVENT = "handle_class_relationship_success";

    private CheckedTextView mAutonomouslyStudy,mOnlineStudy;
    private ViewPager mViewPager;
    private List<Fragment> mPagerFragments;
    private TabCoursePagerAdapter mPagerAdapter;

    private String mCurMemberId;

    /**
     * 首页我的课程入口
     * @return TabCourseFragment
     */
    public static TabCourseFragment newInstance(){
        TabCourseFragment fragment = new TabCourseFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected TabCourseContract.Presenter initPresenter() {
        return new TabCoursePresenter(this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_tab_course;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mAutonomouslyStudy = (CheckedTextView) mRootView.findViewById(R.id.tv_autonomously_study);
        mOnlineStudy = (CheckedTextView) mRootView.findViewById(R.id.tv_online_study);
        mViewPager = (ViewPager) mRootView.findViewById(R.id.view_pager);

        mPagerFragments = new ArrayList<>();
        // 添加我的自主学习
        mPagerFragments.add(TabCourseTypeFragment.newInstance(TabType.TAB_AUTONOMOUSLY));
        // 添加我的在线学习
        mPagerFragments.add(TabCourseTypeFragment.newInstance(TabType.TAB_ONLINE));

        mPagerAdapter = new TabCoursePagerAdapter(getChildFragmentManager(),mPagerFragments);
        mViewPager.setAdapter(mPagerAdapter);

        // 默认设置第一个选中
        mAutonomouslyStudy.setChecked(true);
        mOnlineStudy.setChecked(false);
        mViewPager.setCurrentItem(0);

        mAutonomouslyStudy.setOnClickListener(this);
        mOnlineStudy.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        mCurMemberId = UserHelper.getUserId();
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(getUserVisibleHint()){
            if(EmptyUtil.isNotEmpty(mPagerAdapter)){
                if(EmptyUtil.isEmpty(mCurMemberId) || !UserHelper.getUserId().equals(mCurMemberId)){
                    // 容错机制
                    // 发生用户注册登录
                    mPagerAdapter.notifyDataSetChanged();
                    mCurMemberId = UserHelper.getUserId();
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if(viewId == R.id.tv_autonomously_study){
            // 切换Tab,隐藏对话框
            KeyboardUtil.hideSoftInput(getActivity());
            mAutonomouslyStudy.setChecked(true);
            mOnlineStudy.setChecked(false);
            mViewPager.setCurrentItem(0);
        }else if(viewId == R.id.tv_online_study){
            // 切换Tab,隐藏对话框
            KeyboardUtil.hideSoftInput(getActivity());
            mAutonomouslyStudy.setChecked(false);
            mOnlineStudy.setChecked(true);
            mViewPager.setCurrentItem(1);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(@NonNull MessageEvent event){
        if(EmptyUtil.isNotEmpty(event)){
            if(DISSOCIATED_EVENT.equals(event.getUpdateAction())){
                // 解除关联
                if(EmptyUtil.isNotEmpty(mPagerAdapter)){
                    // 发生用户注册登录 容错处理
                    if(mPagerAdapter != null){
                        mPagerAdapter.notifyDataSetChanged();
                        mCurMemberId = UserHelper.getUserId();
                    }
                }
            }
        }
    }

    private class TabCoursePagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragments;

        public TabCoursePagerAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            TabCourseTypeFragment fragment = (TabCourseTypeFragment) super.instantiateItem(container, position);
            // 刷新ViewPager Adapter时候，TabCourseTypeFragment initData 会重新调用
            // fragment.initData();
            return fragment;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }
    }
}
