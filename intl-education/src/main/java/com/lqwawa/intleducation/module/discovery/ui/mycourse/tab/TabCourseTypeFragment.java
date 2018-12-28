package com.lqwawa.intleducation.module.discovery.ui.mycourse.tab;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.IBaseFragment;
import com.lqwawa.intleducation.base.PresenterFragment;
import com.lqwawa.intleducation.base.widgets.adapter.TabSelectedAdapter;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.KeyboardUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.module.discovery.ui.mycourse.TabCourseFragment;
import com.lqwawa.intleducation.module.discovery.ui.mycourse.TabType;
import com.lqwawa.intleducation.module.discovery.ui.myonline.MyOnlinePagerFragment;
import com.lqwawa.intleducation.module.discovery.ui.person.mycourse.CourseTitle;
import com.lqwawa.intleducation.module.discovery.ui.person.mycourse.MyCourseListActivity;
import com.lqwawa.intleducation.module.learn.ui.MyCourseListFragment;
import com.lqwawa.intleducation.module.learn.ui.MyCourseListPagerFragment;
import com.lqwawa.intleducation.module.learn.vo.ChildrenListVo;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author medici
 * @desc 我的课程Type分类的Fragment
 */
public class TabCourseTypeFragment extends PresenterFragment<TabCourseTypeContract.Presenter>
    implements TabCourseTypeContract.View{

    private static final String KEY_EXTRA_TYPE = "KEY_EXTRA_TYPE";

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private SlidePagerAdapter mPagerAdapter;

    private List<Fragment> mPagerFragments;
    private List<CourseTitle> mPageArray;

    // 是家长身份
    private boolean isParent;
    // Tab类型
    private int mTabType;
    // 是否初始化过
    public boolean mInit;

    /**
     * 首页我的课程Type分类入口
     * @return TabCourseFragment
     */
    public static TabCourseTypeFragment newInstance(@NonNull @TabType.TabRes int type){
        TabCourseTypeFragment fragment = new TabCourseTypeFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_EXTRA_TYPE,type);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_tab_course_type;
    }

    @Override
    protected TabCourseTypeContract.Presenter initPresenter() {
        return new TabCourseTypePresenter(this);
    }

    @Override
    protected boolean initArgs(Bundle bundle) {
        mTabType = bundle.getInt(KEY_EXTRA_TYPE,-1);
        if(mTabType != TabType.TAB_AUTONOMOUSLY || mTabType != TabType.TAB_ONLINE){
            return false;
        }
        return super.initArgs(bundle);

    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mTabLayout = (TabLayout) mRootView.findViewById(R.id.tab_layout);
        mViewPager = (ViewPager) mRootView.findViewById(R.id.view_paper);
        // 注册刷新数据的广播
        registerBroadcastReceiver();
        mInit = true;
    }

    @Override
    public void initData() {
        super.initData();
        // 开放initData为公有的，提供调用
        if(mInit){
            // 加载孩子信息
            mPresenter.start();
        }
    }

    /**
     * 刷新UI的广播
     */
    private void refreshData(){
        if(EmptyUtil.isEmpty(mPagerFragments)) return;
        for (Fragment fragment : mPagerFragments){
            if(fragment.getUserVisibleHint() && fragment instanceof MyCourseListPagerFragment){
                MyCourseListPagerFragment _fragment = (MyCourseListPagerFragment) fragment;
                if(_fragment.isVisible())
                    // 该Fragment是可见的
                    _fragment.getData();
            }

            // V5.12支持的方法
            if(fragment.getUserVisibleHint() && fragment instanceof com.lqwawa.intleducation.module.learn.ui.mycourse.MyCourseListFragment){
                com.lqwawa.intleducation.module.learn.ui.mycourse.MyCourseListFragment _fragment = (com.lqwawa.intleducation.module.learn.ui.mycourse.MyCourseListFragment) fragment;
                if(_fragment.isVisible())
                    // 该Fragment是可见的
                    _fragment.getData();
            }
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

        if(mTabType == TabType.TAB_AUTONOMOUSLY){
            // 我的自主学习
            {
                // 添加我的课程
                /*MyCourseListPagerFragment studentFragment = new MyCourseListPagerFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("MemberId", UserHelper.getUserId());
                // bundle.putBoolean(MyCourseListPagerFragment.KEY_HIDE_SEARCH,true);
                studentFragment.setArguments(bundle);*/
                String memberId = UserHelper.getUserId();
                Fragment studentFragment =
                        com.lqwawa.intleducation.module.learn.ui.mycourse.MyCourseListFragment.newInstance(null,memberId,false);
                mPagerFragments.add(studentFragment);

                // 添加标题
                CourseTitle studentTitle = new CourseTitle(getString(R.string.label_my_join_d),false);
                mPageArray.add(studentTitle);
            }


            if(isParent){
                // 如果是有家长身份
                for (ChildrenListVo vo : childrenListVos) {
                    // 添加标题
                    CourseTitle title = new CourseTitle(vo.getRealName(),true);
                    mPageArray.add(title);
                    /*MyCourseListPagerFragment fragment = new MyCourseListPagerFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("MemberId", vo.getMemberId());
                    bundle.putString("SchoolId", vo.getSchoolId());
                    // bundle.putBoolean(MyCourseListPagerFragment.KEY_HIDE_SEARCH,true);
                    fragment.setArguments(bundle);*/
                    Fragment fragment =
                            com.lqwawa.intleducation.module.learn.ui.mycourse.MyCourseListFragment.newInstance(
                                    vo.getSchoolId(),
                                    vo.getMemberId(),
                                    false);
                    mPagerFragments.add(fragment);
                }
            }

            mPagerAdapter = new SlidePagerAdapter(getChildFragmentManager());
            mViewPager.setAdapter(mPagerAdapter);

            if(mPagerFragments.size() > 1){
                // 如果当前Fragment数目大于1 说明有我孩子的学程
                mTabLayout.setVisibility(View.VISIBLE);
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
        }else{
            // 我的在线学习
            {
                // 添加我的课程
                MyOnlinePagerFragment studentFragment = MyOnlinePagerFragment.newInstance(UserHelper.getUserId(),false);
                mPagerFragments.add(studentFragment);

                // 添加标题
                CourseTitle studentTitle = new CourseTitle(getString(R.string.label_my_join_d),false);
                mPageArray.add(studentTitle);
            }


            if(isParent){
                // 如果是有家长身份
                for (ChildrenListVo vo : childrenListVos) {
                    // 添加标题
                    CourseTitle title = new CourseTitle(vo.getRealName(),true);
                    mPageArray.add(title);
                    MyOnlinePagerFragment fragment = MyOnlinePagerFragment.newInstance(vo.getMemberId(),false);
                    mPagerFragments.add(fragment);
                }
            }

            mPagerAdapter = new SlidePagerAdapter(getChildFragmentManager());
            mViewPager.setAdapter(mPagerAdapter);

            if(mPagerFragments.size() > 1){
                // 如果当前Fragment数目大于1 说明有我孩子的学程
                mTabLayout.setVisibility(View.VISIBLE);
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
            if (action.equals(AppConfig.ServerUrl.joinInCourse)
                    || action.equals(AppConfig.ServerUrl.Login)) {
                // 获取到登录或者是加入课程的广播
                refreshData();
            }
        }
    };

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
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getContext().unregisterReceiver(mBroadcastReceiver);
    }
}
