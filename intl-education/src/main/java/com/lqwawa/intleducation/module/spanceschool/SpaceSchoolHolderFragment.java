package com.lqwawa.intleducation.module.spanceschool;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.IBaseFragment;
import com.lqwawa.intleducation.base.utils.DisplayUtil;
import com.lqwawa.intleducation.base.widgets.adapter.PagerChangedAdapter;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.module.onclass.OnlineClassRole;
import com.lqwawa.intleducation.module.spanceschool.pager.SchoolFunctionPagerNavigator;
import com.lqwawa.intleducation.module.spanceschool.pager.SpaceSchoolHolderPagerFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 空中学校空中课堂功能列表的容器
 * @date 2018/07/04 11:36
 * @history v1.0
 * **********************************
 */
public class SpaceSchoolHolderFragment extends IBaseFragment{

    private static final String KEY_EXTRA_SCHOOL_ID = "KEY_EXTRA_SCHOOL_ID";
    private static final String KEY_EXTRA_SCHOOL_NAME = "KEY_EXTRA_SCHOOL_NAME";
    private static final String KEY_EXTRA_SCHOOL_LOGO = "KEY_EXTRA_SCHOOL_LOGO";
    private static final String KEY_EXTRA_FUNCTION_STATE = "KEY_EXTRA_FUNCTION_TYPE";
    private static final String KEY_EXTRA_ROLE = "KEY_EXTRA_ROLE";

    private SchoolFunctionPagerNavigator mNavigator;

    private ViewPager mViewPager;
    private LinearLayout mViewPagerIndicator;
    private List<SpaceSchoolHolderPagerFragment> mFragments;
    private HolderPagerAdapter mAdapter;

    private String mSchoolId;
    private String mSchoolName;
    private String mLogoUrl;
    private String mRole;
    private int mFunctionState;

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_space_school_holder;
    }

    public static SpaceSchoolHolderFragment newInstance(@NonNull String schoolId,
                                                        @NonNull String schoolName,
                                                        @NonNull String logoUrl,
                                                        @NonNull @OnlineClassRole.RoleRes String role,
                                                        @SchoolFunctionStateType.FunctionStateRes int state) {
        SpaceSchoolHolderFragment fragment = new SpaceSchoolHolderFragment();
        Bundle arguments = new Bundle();
        arguments.putString(KEY_EXTRA_SCHOOL_ID,schoolId);
        arguments.putString(KEY_EXTRA_SCHOOL_NAME,schoolName);
        arguments.putString(KEY_EXTRA_SCHOOL_LOGO, logoUrl);
        arguments.putString(KEY_EXTRA_ROLE,role);
        arguments.putInt(KEY_EXTRA_FUNCTION_STATE, state);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    protected boolean initArgs(Bundle bundle) {
        mSchoolId = bundle.getString(KEY_EXTRA_SCHOOL_ID);
        mSchoolName = bundle.getString(KEY_EXTRA_SCHOOL_NAME);
        mLogoUrl = bundle.getString(KEY_EXTRA_SCHOOL_LOGO);
        mRole = bundle.getString(KEY_EXTRA_ROLE);
        mFunctionState = bundle.getInt(KEY_EXTRA_FUNCTION_STATE);
        if(EmptyUtil.isEmpty(mSchoolId) || EmptyUtil.isEmpty(mSchoolName) || EmptyUtil.isEmpty(mRole)) {
            return false;
        }
        return super.initArgs(bundle);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mViewPager = (ViewPager) mRootView.findViewById(R.id.view_pager);
        mViewPagerIndicator = (LinearLayout) mRootView.findViewById(R.id.indicator_layout);
    }

    @Override
    protected void initData() {
        super.initData();

        List<SpaceSchoolHolderPagerFragment> fragments = new ArrayList<>();
        SpaceSchoolHolderPagerFragment fragment1 = SpaceSchoolHolderPagerFragment.newInstance(mSchoolId,mSchoolName,mLogoUrl,mRole,mFunctionState,0);
        fragment1.setNavigator(mNavigator);
        fragments.add(fragment1);

        if(mFunctionState != SchoolFunctionStateType.TYPE_GONE){
            // 显示第二个Tab
            SpaceSchoolHolderPagerFragment fragment2 = SpaceSchoolHolderPagerFragment.newInstance(mSchoolId,mSchoolName,mLogoUrl,mRole,mFunctionState,1);
            fragment2.setNavigator(mNavigator);
            fragments.add(fragment2);
        }

        this.mFragments = fragments;

        mAdapter = new HolderPagerAdapter(getChildFragmentManager(),mFragments);
        mViewPager.setAdapter(mAdapter);

        if(mFragments.size() == 1){
            mViewPagerIndicator.setVisibility(View.GONE);
        }else{
            for (int index = 0;index < mFragments.size(); index++) {
                CheckedTextView indicatorView = new CheckedTextView(getContext());
                LinearLayout.LayoutParams layoutParams =
                        new LinearLayout.LayoutParams(DisplayUtil.dip2px(UIUtil.getContext(),8),DisplayUtil.dip2px(UIUtil.getContext(),8));
                if(index != 0) layoutParams.leftMargin = DisplayUtil.dip2px(UIUtil.getContext(),8);
                if(index == 0) indicatorView.setChecked(true);
                indicatorView.setLayoutParams(layoutParams);
                indicatorView.setBackgroundResource(R.drawable.bg_space_school_function_indicator);
                mViewPagerIndicator.addView(indicatorView);
            }
        }

        if(mFragments.size() != 1){
            mViewPager.addOnPageChangeListener(new PagerChangedAdapter(){
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    // 更改指示器的背景
                    for (int index = 0; index < mViewPagerIndicator.getChildCount(); index++) {
                        CheckedTextView view = (CheckedTextView) mViewPagerIndicator.getChildAt(index);
                        view.setChecked(index == position);
                    }
                }
            });
        }
    }


    /**
     * 设置点击事件的监听
     * @param navigator
     */
    public void setNavigator(@NonNull SchoolFunctionPagerNavigator navigator){
        this.mNavigator = navigator;
    }

    private static class HolderPagerAdapter extends FragmentPagerAdapter{

        private List<SpaceSchoolHolderPagerFragment> mFragments;

        public HolderPagerAdapter(FragmentManager fm, @NonNull List<SpaceSchoolHolderPagerFragment> fragments) {
            super(fm);
            this.mFragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }
    }
}
