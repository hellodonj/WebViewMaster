package com.lqwawa.intleducation.module.discovery.ui.person.mygive;

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
import android.widget.CheckedTextView;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.PresenterActivity;
import com.lqwawa.intleducation.common.utils.KeyboardUtil;
import com.lqwawa.intleducation.module.discovery.ui.mycourse.TabCourseFragment;
import com.lqwawa.intleducation.module.discovery.ui.person.mygive.pager.MyGiveInstructionPagerFragment;
import com.lqwawa.intleducation.module.learn.ui.MyCourseListPagerFragment;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * @author medici
 * @desc 我的授课
 */
public class MyGiveInstructionActivity extends PresenterActivity<MyGiveInstructionContract.Presenter>
    implements MyGiveInstructionContract.View, View.OnClickListener{

    private ImageView mBtnBack;
    private CheckedTextView mGiveCourse,mGiveOnlineStudy;
    private ViewPager mViewPager;
    private List<Fragment> mPagerFragments;
    private TabPagerAdapter mPagerAdapter;

    @Override
    protected MyGiveInstructionContract.Presenter initPresenter() {
        return new MyGiveInstructionPresenter(this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_my_give_instruction;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mBtnBack = (ImageView) findViewById(R.id.btn_back);
        mGiveCourse = (CheckedTextView) findViewById(R.id.tv_give_course);
        mGiveOnlineStudy = (CheckedTextView) findViewById(R.id.tv_give_online_study);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);

        mPagerFragments = new ArrayList<>();
        // 添加我开设的课程
        MyCourseListPagerFragment teacherFragment = new MyCourseListPagerFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("MemberId", UserHelper.getUserId());
        bundle.putBoolean(MyCourseListPagerFragment.KEY_IS_TEACHER,true);
        teacherFragment.setArguments(bundle);
        mPagerFragments.add(teacherFragment);

        // 添加我开设的自主学习
        MyGiveInstructionPagerFragment giveFragment = MyGiveInstructionPagerFragment.newInstance();
        mPagerFragments.add(giveFragment);

        mPagerAdapter = new TabPagerAdapter(getSupportFragmentManager(),mPagerFragments);
        mViewPager.setAdapter(mPagerAdapter);

        mBtnBack.setOnClickListener(this);
        mGiveCourse.setOnClickListener(this);
        mGiveOnlineStudy.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if(viewId == R.id.btn_back){
            // 返回
            finish();
        }else if(viewId == R.id.tv_give_course){
            // 切换Tab,隐藏对话框
            KeyboardUtil.hideSoftInput(this);
            mGiveCourse.setChecked(true);
            mGiveOnlineStudy.setChecked(false);
            mViewPager.setCurrentItem(0);
        }else if(viewId == R.id.tv_give_online_study){
            // 切换Tab,隐藏对话框
            KeyboardUtil.hideSoftInput(this);
            mGiveCourse.setChecked(false);
            mGiveOnlineStudy.setChecked(true);
            mViewPager.setCurrentItem(1);
        }
    }

    private class TabPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragments;

        public TabPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }

    /**
     * @author medici 我的授课入口
     * @param context 上下文对象
     */
    public static void show(@NonNull Context context){
        Intent intent = new Intent(context,MyGiveInstructionActivity.class);
        context.startActivity(intent);
    }
}
