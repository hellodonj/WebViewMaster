package com.lqwawa.intleducation.module.tutorial.course.filtrate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.PresenterActivity;
import com.lqwawa.intleducation.base.widgets.ControlViewPager;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.base.widgets.adapter.TabSelectedAdapter;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;
import com.lqwawa.intleducation.module.discovery.tool.LoginHelper;
import com.lqwawa.intleducation.module.discovery.ui.ConfirmOrderActivity;
import com.lqwawa.intleducation.module.discovery.ui.CourseDetailsActivity;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.filtrate.HideSortType;
import com.lqwawa.intleducation.module.discovery.ui.study.filtrate.NewOnlineStudyFiltrateActivity;
import com.lqwawa.intleducation.module.tutorial.course.filtrate.pager.ActivityNavigator;
import com.lqwawa.intleducation.module.tutorial.course.filtrate.pager.PagerNavigator;
import com.lqwawa.intleducation.module.tutorial.course.filtrate.pager.TutorialFiltrateGroupPagerFragment;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mrmedici
 * @desc 帮辅群标签筛选页面
 */
public class TutorialFiltrateGroupActivity extends PresenterActivity<TutorialFiltrateGroupContract.Presenter>
    implements TutorialFiltrateGroupContract.View,ActivityNavigator {

    private static final String KEY_EXTRA_MEMBER_ID = "KEY_EXTRA_MEMBER_ID";

    // 小语种课程
    private static final int MINORITY_LANGUAGE_COURSE_ID = 2004;
    // 英语国际课程
    private static final int ENGLISH_INTERNATIONAL_COURSE_ID = 2001;
    // 特色课程
    private static final int CHARACTERISTIC_COURSE_ID = 2005;
    // 基础课程
    private static final int COUNTRY_COURSE_ID = 2003;

    // IGCSE A-LEVEL
    private static final int ENGLISH_INTERNATIONAL_CHILDREN_IGCSE_ID = 2013;
    private static final int ENGLISH_INTERNATIONAL_CHILDREN_A_LEVEL_ID = 2012;

    private static final int CONFIG_TYPE_1 = 1;
    private static final int CONFIG_TYPE_2 = 2;
    private static final int CONFIG_TYPE_3 = 3;
    private static final int CONFIG_TYPE_4 = 4;

    private LinearLayout mHeaderLayout;
    private LinearLayout mTabVector1,mTabVector2,mTabVector3;
    private TextView mTabLabel1,mTabLabel2,mTabLabel3;
    private TabLayout mTabLayout1,mTabLayout2,mTabLayout3;

    private TopBar mTopBar;
    private FrameLayout mBodyLayout;
    private TabLayout mSortLayout;
    private ControlViewPager mViewPager;

    // 全部文本
    private String mAllText = UIUtil.getString(R.string.label_course_filtrate_all);
    private List<LQCourseConfigEntity> mConfigEntities;
    // 筛选集合1
    private List<Tab> mFiltrateArray1;
    // 筛选集合2
    private List<Tab> mFiltrateArray2;
    // 筛选集合3
    private List<Tab> mFiltrateArray3;

    private String[] mTabTitles;
    private List<PagerNavigator> mNavigatorList;

    private String mCurMemberId;

    @Override
    protected TutorialFiltrateGroupContract.Presenter initPresenter() {
        return new TutorialFiltrateGroupPresenter(this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_tutorial_filtrate_group;
    }

    @Override
    protected boolean initArgs(@NonNull Bundle bundle) {
        mCurMemberId = bundle.getString(KEY_EXTRA_MEMBER_ID);
        return super.initArgs(bundle);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mTopBar = (TopBar) findViewById(R.id.top_bar);
        mTopBar.setBack(true);
        mTopBar.setTitle(R.string.title_tutorial_group);

        mHeaderLayout = (LinearLayout) findViewById(R.id.header_layout);
        mTabVector1 = (LinearLayout) findViewById(R.id.tab_vector_1);
        mTabVector2 = (LinearLayout) findViewById(R.id.tab_vector_2);
        mTabVector3 = (LinearLayout) findViewById(R.id.tab_vector_3);
        mTabLabel1 = (TextView) findViewById(R.id.tab_label_1);
        mTabLabel2 = (TextView) findViewById(R.id.tab_label_2);
        mTabLabel3 = (TextView) findViewById(R.id.tab_label_3);
        mTabLayout1 = (TabLayout) findViewById(R.id.tab_layout_1);
        mTabLayout2 = (TabLayout) findViewById(R.id.tab_layout_2);
        mTabLayout3 = (TabLayout) findViewById(R.id.tab_layout_3);

        mBodyLayout = (FrameLayout) findViewById(R.id.body_layout);
        mSortLayout = (TabLayout) findViewById(R.id.sort_layout);
        mViewPager = (ControlViewPager) findViewById(R.id.view_pager);
    }

    @Override
    protected void initData() {
        super.initData();
        // 获取标签
        mPresenter.requestTutorialConfigData();
    }

    @Override
    public void updateTutorialConfigView(List<LQCourseConfigEntity> entities) {
        this.mConfigEntities = entities;
        // 组装Label数据
        // 默认第一个选中
        if(EmptyUtil.isNotEmpty(entities)){

            mFiltrateArray1 = new ArrayList<>();
            mFiltrateArray2 = new ArrayList<>();
            mFiltrateArray3 = new ArrayList<>();

            if(EmptyUtil.isEmpty(mConfigEntities)) return;
            recursionConfig(entities);


            LQCourseConfigEntity rootEntity = entities.get(0);
            int rootId = rootEntity.getId();
            configLabel(rootId);
            // 设置TabLayout相关监听
            initTabListener();
            // 设置数据到TabLayout上
            initTabControl();
            // 设置第一个选中
            // mTabLayout1.getTabAt(0).select();
            // mTabLayout1.getTabAt(0).getCustomView().setSelected(true);
        }else{
            // 隐藏HeaderLayout
            mHeaderLayout.setVisibility(View.GONE);
        }


        mTabTitles = UIUtil.getStringArray(R.array.label_online_class_tabs);

        TutorialFiltrateGroupPagerFragment recentUpdateFragment = TutorialFiltrateGroupPagerFragment.newInstance(mCurMemberId,HideSortType.TYPE_SORT_ONLINE_SHOP_RECENT_UPDATE,this);
        TutorialFiltrateGroupPagerFragment hotFragment = TutorialFiltrateGroupPagerFragment.newInstance(mCurMemberId,HideSortType.TYPE_SORT_HOT_RECOMMEND,this);

        List<Fragment> fragments = new ArrayList<>();
        fragments.add(recentUpdateFragment);
        fragments.add(hotFragment);

        mNavigatorList = new ArrayList<>();
        mNavigatorList.add(recentUpdateFragment);
        mNavigatorList.add(hotFragment);

        TabPagerAdapter mAdapter = new TabPagerAdapter(getSupportFragmentManager(), fragments);
        mSortLayout.setupWithViewPager(mViewPager);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(fragments.size());
    }

    /**
     * 递归调用
     */
    private void recursionConfig(List<LQCourseConfigEntity> entities){
        clearArray(CONFIG_TYPE_1);
        recursionConfigArray(entities);
    }

    /**
     * 清空集合
     */
    private void clearArray(int configType){
        // 清空所有数据
        if(configType <= CONFIG_TYPE_4 || configType <= CONFIG_TYPE_3){
            // 清除第三个
            mFiltrateArray3.clear();
        }

        if(configType <= CONFIG_TYPE_2){
            // 清除第二个
            mFiltrateArray2.clear();
        }

        if(configType <= CONFIG_TYPE_1){
            // 清除第一个
            mFiltrateArray1.clear();
        }
    }

    /**
     * 递归调用
     */
    private void recursionConfigArray(@NonNull List<LQCourseConfigEntity> array){
        if(EmptyUtil.isEmpty(array)) return;

        for (LQCourseConfigEntity entity:array) {
            if(entity.getConfigType() == CONFIG_TYPE_1){
                Tab tab = Tab.build(entity);
                if(!mFiltrateArray1.contains(tab)){
                    mFiltrateArray1.add(Tab.build(entity));
                }
            }

            if(entity.getConfigType() == CONFIG_TYPE_2){
                Tab tab = Tab.build(entity);
                if(!mFiltrateArray2.contains(tab)){
                    mFiltrateArray2.add(Tab.build(entity));
                }

                // 第二个筛选容器,加全部
                Tab allTab2 = Tab.buildAll(mAllText,null);
                if(!mFiltrateArray2.contains(allTab2)){
                    mFiltrateArray2.add(0,allTab2);
                }
            }

            if(entity.getConfigType() == CONFIG_TYPE_3 || entity.getConfigType() == CONFIG_TYPE_4){
                Tab tab = Tab.build(entity);
                if(!mFiltrateArray3.contains(tab)){
                    mFiltrateArray3.add(Tab.build(entity));
                }
                // 第三个筛选容器,加全部
                Tab allTab3 = Tab.buildAll(mAllText,null);
                if(!mFiltrateArray3.contains(allTab3)){
                    mFiltrateArray3.add(0,allTab3);
                }
            }
            // 递归调用
            List<LQCourseConfigEntity> childList = entity.getChildList();
            recursionConfigArray(childList);
        }
    }

    /**
     * 组装Label数据
     */
    private void configLabel(@NonNull int rootId){
        // 是否配置全部
        mTabVector2.setVisibility(View.VISIBLE);

        if(rootId == MINORITY_LANGUAGE_COURSE_ID){
            // 小语种课程 二级页面
            mTabVector3.setVisibility(View.GONE);

            // 类型语言
            mTabLabel1.setText(getString(R.string.label_colon_type));
            mTabLabel2.setText(getString(R.string.label_colon_language));
        }else if(rootId == ENGLISH_INTERNATIONAL_COURSE_ID){
            // 英语国际课程 三级页面
            // 三级页面
            mTabVector3.setVisibility(View.VISIBLE);

            // 类型 类型 科目
            mTabLabel1.setText(getString(R.string.label_colon_type));
            mTabLabel2.setText(getString(R.string.label_colon_type));
            mTabLabel3.setText(getString(R.string.label_colon_subject));
        }else if(rootId == CHARACTERISTIC_COURSE_ID || rootId == COUNTRY_COURSE_ID){
            // 三级页面
            mTabVector3.setVisibility(View.GONE);

            if(rootId == CHARACTERISTIC_COURSE_ID){
                // 类型 科目
                mTabLabel1.setText(getString(R.string.label_colon_type));
                mTabLabel2.setText(getString(R.string.label_colon_subject));
            }else if(rootId == COUNTRY_COURSE_ID){
                // 类型 学段
                mTabLabel1.setText(getString(R.string.label_colon_type));
                mTabLabel2.setText(getString(R.string.label_colon_period));
            }
        }
    }

    /**
     * 设置相关联动的监听
     */
    private void initTabListener(){
        mTabLayout1.removeOnTabSelectedListener(tabLayout1Adapter);
        mTabLayout1.addOnTabSelectedListener(tabLayout1Adapter);

        mTabLayout2.removeOnTabSelectedListener(tabLayout2Adapter);
        mTabLayout2.addOnTabSelectedListener(tabLayout2Adapter);

        // 小语种TabLayout3被隐藏了
        mTabLayout3.removeOnTabSelectedListener(tabLayout3Adapter);
        mTabLayout3.addOnTabSelectedListener(tabLayout3Adapter);
    }

    private TabSelectedAdapter tabLayout1Adapter = new TabSelectedAdapter(){
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            super.onTabSelected(tab);
            // 全部发生数据联动
            Tab tabData = (Tab) tab.getTag();
            setTabItemSelected(mFiltrateArray1,tabData);
            // 重新配置2,3数据的联动效果
            clearArray(CONFIG_TYPE_2);
            recursionConfigArray(tabData.getChildList());
            configLabel(tabData.getId());
            initTabControl2();
        }
    };

    private TabSelectedAdapter tabLayout2Adapter = new TabSelectedAdapter(){
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            super.onTabSelected(tab);
            Tab tabData = (Tab) tab.getTag();
            setTabItemSelected(mFiltrateArray2,tabData);
            // 所有标签都会发生二级，甚至三级联动
            TabLayout.Tab tabAt = mTabLayout1.getTabAt(mTabLayout1.getSelectedTabPosition());
            if(EmptyUtil.isNotEmpty(tabAt)){
                if(tabData.isAll()){
                    List<LQCourseConfigEntity> entities = new ArrayList<>();
                    for (Tab item:mFiltrateArray2) {
                        if(!item.isAll() && EmptyUtil.isNotEmpty(item.getChildList())){
                            entities.addAll(item.getChildList());
                        }
                    }
                    tabData.setChildList(entities);
                }

                Tab parentTab = (Tab) tabAt.getTag();
                if(tabData.getId() == ENGLISH_INTERNATIONAL_CHILDREN_IGCSE_ID ||
                        tabData.getId() == ENGLISH_INTERNATIONAL_CHILDREN_A_LEVEL_ID ||
                        (parentTab.getId() == ENGLISH_INTERNATIONAL_COURSE_ID &&
                        tabData.isAll())) {
                    // 设置第三个显示
                    mTabVector3.setVisibility(View.VISIBLE);
                    // 重新配置3数据的联动效果
                    clearArray(CONFIG_TYPE_3);
                    recursionConfigArray(tabData.getChildList());
                    initTabControl3();
                }else{
                    // 设置第三个显示
                    mTabVector3.setVisibility(View.GONE);
                    triggerUpdateData();
                }
            }

            // 数据请求
            /*for (Tab tab1:mFiltrateArray1){
                if(tab1.getId() == MINORITY_LANGUAGE_COURSE_ID ||
                        tab1.getId() == CHARACTERISTIC_COURSE_ID ||
                        tab1.getId() == COUNTRY_COURSE_ID){
                    // 选中的是小语种的Id
                    // 选中的是特色课程的Id
                    // 选中的是国家课程的Id
                    triggerUpdateData();
                }
            }*/

        }
    };

    private TabSelectedAdapter tabLayout3Adapter = new TabSelectedAdapter(){
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            super.onTabSelected(tab);
            Tab tabData = (Tab) tab.getTag();
            setTabItemSelected(mFiltrateArray3,tabData);
            // 数据请求
            triggerUpdateData();
        }
    };

    /**
     * 填充数据,设置监听
     */
    private void initTabControl(){
        initTabControl1();
    }

    private void initTabControl1(){
        mTabLayout1.removeAllTabs();

        for (Tab tab:mFiltrateArray1) {
            View tabView = UIUtil.inflate(R.layout.item_tab_control_layout);
            TextView tvContent = (TextView) tabView.findViewById(R.id.tv_content);
            tvContent.setText(tab.getConfigValue());
            // 将tab数据作为Tag设置到TabLayout的TabLayout.Tab上
            TabLayout.Tab newTab = mTabLayout1.newTab().setCustomView(tabView).setTag(tab);

            // 已经添加过已经选择的Tab
            mTabLayout1.addTab(newTab);
        }


        mTabLayout1.smoothScrollTo(0,0);
    }

    private void initTabControl2(){
        mTabLayout2.removeAllTabs();

        for (Tab tab:mFiltrateArray2) {
            View tabView = UIUtil.inflate(R.layout.item_tab_control_layout);
            TextView tvContent = (TextView) tabView.findViewById(R.id.tv_content);
            tvContent.setText(tab.getConfigValue());
            TabLayout.Tab newTab = mTabLayout2.newTab().setCustomView(tabView).setTag(tab);

            // 已经添加过已经选择的Tab
            mTabLayout2.addTab(newTab);
        }

        mTabLayout2.smoothScrollTo(0,0);
    }

    private void initTabControl3(){
        mTabLayout3.removeAllTabs();

        if(EmptyUtil.isNotEmpty(mFiltrateArray3)){
            for (Tab tab:mFiltrateArray3) {
                View tabView = UIUtil.inflate(R.layout.item_tab_control_layout);
                TextView tvContent = (TextView) tabView.findViewById(R.id.tv_content);
                tvContent.setText(tab.getConfigValue());
                TabLayout.Tab newTab = mTabLayout3.newTab().setCustomView(tabView).setTag(tab);

                // 已经添加过已经选择的Tab
                mTabLayout3.addTab(newTab);
            }
        }

        mTabLayout3.smoothScrollTo(0,0);
    }

    /**
     * 设置该Tab选中
     * @param array 对应的Tab集合
     * @param tab 选择的Tab
     */
    private void setTabItemSelected(@NonNull List<Tab> array, @NonNull Tab tab){
        if(EmptyUtil.isEmpty(array) || EmptyUtil.isEmpty(tab)) return;
        for (Tab item:array) {
            item.setChecked(false);
            if(item.equals(tab)){
                item.setChecked(true);
            }
        }
    }

    /**
     * 触发更新
     */
    private void triggerUpdateData(){
        if (EmptyUtil.isNotEmpty(mConfigEntities)){
            clearConfigArrayStatus(mConfigEntities);
        }
        triggerUpdateFragment();
    }

    /**
     * 清空默认设置科目的选择状态
     * @param array 标签数据
     */
    private void clearConfigArrayStatus(@NonNull List<LQCourseConfigEntity> array){
        if(EmptyUtil.isEmpty(array)) return;

        for (LQCourseConfigEntity entity:array) {
            entity.setSelected(false);

            // 递归调用
            List<LQCourseConfigEntity> childList = entity.getChildList();
            clearConfigArrayStatus(childList);
        }
    }

    /**
     * 更新Fragment数据
     */
    private void triggerUpdateFragment(){
        if(EmptyUtil.isNotEmpty(mNavigatorList)){
            for (PagerNavigator navigator : mNavigatorList) {
                navigator.reloadData();
            }
        }
    }

    @Override
    public String getLevel() {
        String level = "";
        if(EmptyUtil.isNotEmpty(mFiltrateArray1)){
            for (Tab tab : mFiltrateArray1) {
                if(tab.isChecked()){
                    level = tab.getLevel();
                    break;
                }
            }
        }

        if(EmptyUtil.isNotEmpty(mFiltrateArray2)){
            for (Tab tab : mFiltrateArray2) {
                if(!tab.isAll() && tab.isChecked()){
                    level = tab.getLevel();
                    break;
                }
            }
        }

        return level;
    }

    @Override
    public int[] getFiltrateParams() {
        int[] params = {0,0,0};
        if(EmptyUtil.isNotEmpty(mFiltrateArray1)){

            int rootId = 0;
            for (Tab tab : mFiltrateArray1) {
                if(tab.isChecked()){
                    rootId = tab.getId();
                    break;
                }
            }

            if(rootId == ENGLISH_INTERNATIONAL_COURSE_ID){
                // 国际课程
                if(EmptyUtil.isNotEmpty(mFiltrateArray2) &&
                        EmptyUtil.isNotEmpty(mFiltrateArray3)){
                    outer:
                    for (Tab tab : mFiltrateArray2) {
                        if(tab.isChecked() &&
                                (tab.getId() == ENGLISH_INTERNATIONAL_CHILDREN_A_LEVEL_ID ||
                                    tab.getId() == ENGLISH_INTERNATIONAL_CHILDREN_IGCSE_ID)){
                            inner:
                            for (Tab tab3 : mFiltrateArray3) {
                                if(tab3.isChecked()){
                                    params[0] = tab3.getLabelId();
                                    break outer;
                                }
                            }
                            break outer;
                        }
                    }
                }
            }else if(rootId == CHARACTERISTIC_COURSE_ID){
                // 特色课程
                if(EmptyUtil.isNotEmpty(mFiltrateArray2)){
                    for (Tab tab : mFiltrateArray2) {
                        if(tab.isChecked()){
                            params[1] = tab.getLabelId();
                            break;
                        }
                    }
                }
            }else if(rootId == COUNTRY_COURSE_ID){
                // 国家课程
                if(EmptyUtil.isNotEmpty(mFiltrateArray3)){
                    for (Tab tab : mFiltrateArray3) {
                        if(tab.isChecked()){
                            params[1] = tab.getParamTwoId();
                            params[2] = tab.getParamThreeId();
                            break;
                        }
                    }
                }
            }

        }
        return params;
    }

    @Override
    public String getKeyWord() {
        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LoginHelper.RS_LOGIN) {
            if(UserHelper.isLogin()) {
                // 重新进入该页面
                // 没有登录只能看自己的
                String userId = UserHelper.getUserId();
                TutorialFiltrateGroupActivity.show(this,userId);
                finish();
            }
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

        @Override
        public CharSequence getPageTitle(int position) {
            return mTabTitles[position];
        }
    }

    /**
     * 帮辅群筛选页面的入口
     * @param context 上下文对象
     */
    public static void show(@NonNull Context context,@Nullable String memberId){
        Intent intent = new Intent(context,TutorialFiltrateGroupActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(KEY_EXTRA_MEMBER_ID,memberId);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}
