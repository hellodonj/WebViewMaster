package com.lqwawa.intleducation.module.discovery.ui.person.mycourse;

import android.app.Activity;
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
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.PresenterActivity;
import com.lqwawa.intleducation.base.utils.ButtonUtils;
import com.lqwawa.intleducation.base.utils.StringUtils;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.module.learn.ui.MyCourseListPagerFragment;
import com.lqwawa.intleducation.module.learn.vo.ChildrenListVo;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author medici
 * @desc 我的学程
 */
public class MyCourseListActivity extends PresenterActivity<MyCourseListContract.Presenter>
    implements MyCourseListContract.View{


    private RelativeLayout mTopBar;
    private ImageButton mBack;
    private TextView mTitleContent;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private SlidePagerAdapter mPagerAdapter;

    private boolean isParent;
    private boolean isTeacher;

    private List<MyCourseListPagerFragment> mPagerFragments;
    private List<CourseTitle> mPageArray;

    @Override
    protected MyCourseListContract.Presenter initPresenter() {
        return new MyCourseListPresenter(this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_my_course_list;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mTopBar = (RelativeLayout) findViewById(R.id.top_bar);
        mTitleContent = (TextView) findViewById(R.id.tv_title);
        mTabLayout = (TabLayout) mTopBar.findViewById(R.id.tab_layout);
        mBack = (ImageButton) mTopBar.findViewById(R.id.left_function1_image);
        mViewPager = (ViewPager) findViewById(R.id.view_paper);
        // 当只有一种学生身份时，显示我的学程
        mTitleContent.setText(getResources().getString(R.string.my_learning_process));

        mBack.setOnClickListener(view -> finish());

        if(UserHelper.isTeacher(UserHelper.getUserInfo().getRoles())){
            //以老师身份登录
            isTeacher = true;
        }
    }

    @Override
    protected void initData() {
        super.initData();
        // 注册刷新数据的广播
        registerBroadcastReceiver();

        mPageArray = new ArrayList<>();
        mPagerFragments = new ArrayList<>();
        // 加载孩子信息
        mPresenter.start();
    }

    @Override
    public void updateParentChildrenData(@Nullable List<ChildrenListVo> childrenListVos) {
        hideDialogLoading();
        if(EmptyUtil.isNotEmpty(childrenListVos)){
            isParent = true;
        }

        // 判断身份
        if(isTeacher){
            // 如果是老师身份
            MyCourseListPagerFragment teacherFragment = new MyCourseListPagerFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("MemberId", UserHelper.getUserId());
            bundle.putBoolean(MyCourseListPagerFragment.KEY_IS_TEACHER,true);
            teacherFragment.setArguments(bundle);
            mPagerFragments.add(teacherFragment);
            // 添加标题
            CourseTitle teacherTitle = new CourseTitle(getString(R.string.label_my_establish_course),false);
            mPageArray.add(teacherTitle);
        }

        {
            // 再添加我的课程
            MyCourseListPagerFragment studentFragment = new MyCourseListPagerFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("MemberId", UserHelper.getUserId());
            studentFragment.setArguments(bundle);
            mPagerFragments.add(studentFragment);

            // 添加标题
            CourseTitle studentTitle = new CourseTitle(getString(R.string.label_my_join_course),false);
            mPageArray.add(studentTitle);
        }


        if(isParent){
            // 如果是有家长身份
            for (ChildrenListVo vo : childrenListVos) {
                // 添加标题
                CourseTitle title = new CourseTitle(vo.getRealName(),true);
                mPageArray.add(title);
                MyCourseListPagerFragment fragment = new MyCourseListPagerFragment();
                Bundle bundle = new Bundle();
                bundle.putString("MemberId", vo.getMemberId());
                bundle.putString("SchoolId", vo.getSchoolId());
                fragment.setArguments(bundle);
                mPagerFragments.add(fragment);
            }
        }

        mPagerAdapter = new SlidePagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);

        if(mPagerFragments.size() > 1){
            // 如果当前Fragment数目大于1 说明有我开设的学堂或孩子学程
            mTitleContent.setVisibility(View.GONE);
            mTabLayout.setVisibility(View.VISIBLE);
            mTabLayout.setupWithViewPager(mViewPager);
        }else{
            mTitleContent.setVisibility(View.VISIBLE);
            mTabLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 刷新UI的广播
     */
    private void refreshData(){
        for (MyCourseListPagerFragment fragment : mPagerFragments){
            if(fragment.getUserVisibleHint()){
                // 该Fragment是可见的
                fragment.getData();
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
        registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
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

    private class SlidePagerAdapter extends FragmentPagerAdapter {

        public SlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mPagerFragments.get(position);
        }

        @Override
        public int getCount() {
            return mPagerFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            CourseTitle mCourseTitle = mPageArray.get(position);
            String language = Locale.getDefault().getLanguage();
            if(language.endsWith("en")){
                // 英文
                if(mCourseTitle.isChild()){
                    return mCourseTitle.getRealName() + " " + UIUtil.getString(R.string.xx_learning_process);
                }else{
                    return mCourseTitle.getRealName() + " " + UIUtil.getString(R.string.x_learning_process);
                }
            }else{
                // 中文
                return mCourseTitle.getRealName() + UIUtil.getString(R.string.xx_learning_process);
            }
        }
    }

    /**
     * 我的学程入口
     * @param context 上下文对象
     */
    public static void start(@NonNull Context context){
        if(!ButtonUtils.isFastDoubleClick()) {
            Intent intent = new Intent(context,MyCourseListActivity.class);
            context.startActivity(intent);
        }
    }
}
