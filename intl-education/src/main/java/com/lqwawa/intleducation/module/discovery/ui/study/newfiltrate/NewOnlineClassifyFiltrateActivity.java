package com.lqwawa.intleducation.module.discovery.ui.study.newfiltrate;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.PresenterActivity;
import com.lqwawa.intleducation.base.widgets.ControlViewPager;
import com.lqwawa.intleducation.base.widgets.PriceArrowView;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.base.widgets.adapter.TabSelectedAdapter;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.entity.online.NewOnlineConfigEntity;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.filtrate.HideSortType;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.search.SearchActivity;
import com.lqwawa.intleducation.module.discovery.ui.study.filtrate.ActivityNavigator;
import com.lqwawa.intleducation.module.discovery.ui.study.filtrate.PagerNavigator;
import com.lqwawa.intleducation.module.discovery.ui.study.filtrate.Tab;
import com.lqwawa.intleducation.module.discovery.ui.study.filtrate.pager.NewOnlineStudyFiltratePagerFragment;
import com.lqwawa.intleducation.module.discovery.ui.study.filtrate.pager.PagerParams;
import com.lqwawa.intleducation.module.onclass.OnlineSortType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author mrmedici
 * @desc 小语种,国际课程,国家课程 名师课更多页面
 */
public class NewOnlineClassifyFiltrateActivity extends PresenterActivity<NewOnlineClassifyFiltrateContract.Presenter>
    implements NewOnlineClassifyFiltrateContract.View,ActivityNavigator {

    private static final int KEY_EXTRA_SEARCH_CODE = 1 << 0;

    private static final String KEY_EXTRA_DATA_TYPE = "KEY_EXTRA_DATA_TYPE";

    // 配置小语种课程的ID，其它ID都是三级列表
    private static final int MINORITY_LANGUAGE_ID = 5005;
    // 配置初中高中课程的ID,这两个ID下的国际课程是三级联动
    private static final int JUNIOR_HIGH_SCHOOL_ID = 5003;
    private static final int SENIOR_HIGH_SCHOOL_ID = 5004;

    // 有三级联动的ID
    private static final int THREE_LEVEL_LINKAGE_JUNIOR = 5011;
    private static final int THREE_LEVEL_LINKAGE_SENIOR = 5013;

    // 需要显示的ConfigType
    private static final int CONFIG_TYPE_LEVEL_0 = 11;
    private static final int CONFIG_TYPE_LEVEL_1 = 12;
    private static final int CONFIG_TYPE_LEVEL_2 = 13;
    private static final int CONFIG_TYPE_LEVEL_3 = 14;

    // 全部文本
    private String mAllText = UIUtil.getString(R.string.label_course_filtrate_all);

    private TopBar mTopBar;
    private LinearLayout mHeaderLayout;
    private LinearLayout mTabVector1,mTabVector2,mTabVector3,mTabVector4;
    private TextView mTabLabel1,mTabLabel2,mTabLabel3,mTabLabel4;
    private TabLayout mTabLayout1,mTabLayout2,mTabLayout3,mTabLayout4;
    private FrameLayout mBodyLayout;
    private TabLayout mSortLayout;
    private ControlViewPager mViewPager;

    private String[] mTabTitles;
    private List<PagerNavigator> mNavigatorList;
    private PriceArrowView mPriceArrowView;
    // 价格tab是否显示
    private boolean priceTabVisible;

    // 筛选集合1
    private List<Tab> mFiltrateArray1;
    // 筛选集合2
    private List<Tab> mFiltrateArray2;
    // 筛选集合3
    private List<Tab> mFiltrateArray3;
    // 筛选集合4
    private List<Tab> mFiltrateArray4;

    private String mKeyWord;
    private DataType mDataType;
    private List<NewOnlineConfigEntity> mConfigEntities;

    @Override
    protected NewOnlineClassifyFiltrateContract.Presenter initPresenter() {
        return new NewOnlineClassifyFiltratePresenter(this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_new_online_classify_filtrate;
    }

    @Override
    protected boolean initArgs(@NonNull Bundle bundle) {
        mDataType = (DataType) bundle.getSerializable(KEY_EXTRA_DATA_TYPE);
        if(EmptyUtil.isEmpty(mDataType)) return false;
        return super.initArgs(bundle);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mTopBar = (TopBar) findViewById(R.id.top_bar);
        mTopBar.setBack(true);
        mTopBar.setRightFunctionImage1(R.drawable.search, view->triggerSearch());

        if(mDataType == DataType.MINORITY_LANGUAGE){
            mTopBar.setTitle(R.string.label_minority_language_holder_title);
        }else if(mDataType == DataType.INTERNATIONAL){
            mTopBar.setTitle(R.string.label_international_holder_title);
        }else if(mDataType == DataType.BASIC_COURSE){
            mTopBar.setTitle(R.string.label_basic_holder_title);
        }

        mHeaderLayout = (LinearLayout) findViewById(R.id.header_layout);
        mBodyLayout = (FrameLayout) findViewById(R.id.body_layout);

        mTabVector1 = (LinearLayout) findViewById(R.id.tab_vector_1);
        mTabVector2 = (LinearLayout) findViewById(R.id.tab_vector_2);
        mTabVector3 = (LinearLayout) findViewById(R.id.tab_vector_3);
        mTabVector4 = (LinearLayout) findViewById(R.id.tab_vector_4);
        mTabLabel1 = (TextView) findViewById(R.id.tab_label_1);
        mTabLabel2 = (TextView) findViewById(R.id.tab_label_2);
        mTabLabel3 = (TextView) findViewById(R.id.tab_label_3);
        mTabLabel4 = (TextView) findViewById(R.id.tab_label_4);
        mTabLayout1 = (TabLayout) findViewById(R.id.tab_layout_1);
        mTabLayout2 = (TabLayout) findViewById(R.id.tab_layout_2);
        mTabLayout3 = (TabLayout) findViewById(R.id.tab_layout_3);
        mTabLayout4 = (TabLayout) findViewById(R.id.tab_layout_4);
        mSortLayout = (TabLayout) findViewById(R.id.sort_layout);
        mViewPager = (ControlViewPager) findViewById(R.id.view_pager);


        mSortLayout.addOnTabSelectedListener(new TabSelectedAdapter() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                super.onTabSelected(tab);
                if (tab.getPosition() == mSortLayout.getTabCount() - 1) {
                    priceTabVisible = true;
                    // 价格被选中
                    if (EmptyUtil.isNotEmpty(mPriceArrowView)) {
                        int state = mPriceArrowView.triggerSwitch();
                        triggerPriceSwitch(state);
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                super.onTabUnselected(tab);
                if (tab.getPosition() == mSortLayout.getTabCount() - 1) {
                    priceTabVisible = false;
                    // 价格被选中
                    if (EmptyUtil.isNotEmpty(mPriceArrowView)) {
                        // 状态重置
                        mPriceArrowView.reset();
                    }
                }
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();

        mFiltrateArray1 = new ArrayList<>();
        mFiltrateArray2 = new ArrayList<>();
        mFiltrateArray3 = new ArrayList<>();
        mFiltrateArray4 = new ArrayList<>();

        mTabTitles = UIUtil.getStringArray(R.array.label_online_class_tabs);

        mTabTitles = UIUtil.getStringArray(R.array.label_online_class_tabs);
        PagerParams recentUpdateParams = new PagerParams(mKeyWord,OnlineSortType.TYPE_SORT_ONLINE_CLASS_RECENT_UPDATE);
        PagerParams hotParams = new PagerParams(mKeyWord,OnlineSortType.TYPE_SORT_ONLINE_CLASS_HOT_RECOMMEND);
        PagerParams priceParams = new PagerParams(mKeyWord,OnlineSortType.TYPE_SORT_ONLINE_CLASS_PRICE_UP);

        NewOnlineStudyFiltratePagerFragment recentUpdateFragment = NewOnlineStudyFiltratePagerFragment.newInstance(recentUpdateParams,this);
        NewOnlineStudyFiltratePagerFragment hotFragment = NewOnlineStudyFiltratePagerFragment.newInstance(hotParams,this);
        NewOnlineStudyFiltratePagerFragment priceFragment = NewOnlineStudyFiltratePagerFragment.newInstance(priceParams,this);

        List<Fragment> fragments = new ArrayList<>();
        fragments.add(recentUpdateFragment);
        fragments.add(hotFragment);
        fragments.add(priceFragment);

        mNavigatorList = new ArrayList<>();
        mNavigatorList.add(recentUpdateFragment);
        mNavigatorList.add(hotFragment);
        mNavigatorList.add(priceFragment);

        TabPagerAdapter mAdapter = new TabPagerAdapter(getSupportFragmentManager(), fragments);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(fragments.size());

        mSortLayout.setupWithViewPager(mViewPager);

        // 设置TabLayout最后一个节点有upDown
        TabLayout.Tab tabAt = mSortLayout.getTabAt(mSortLayout.getTabCount() - 1);
        PriceArrowView view = new PriceArrowView(this);
        view.setTabTitle(mTabTitles[mSortLayout.getTabCount() - 1]);
        tabAt.setCustomView(view.getRootView());
        mPriceArrowView = view;

        mPriceArrowView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (priceTabVisible && event.getAction() == MotionEvent.ACTION_DOWN) {
                    int state = mPriceArrowView.triggerSwitch();
                    triggerPriceSwitch(state);
                    return true;
                }
                return false;
            }
        });

        mPresenter.requestNewOnlineClassifyConfigData(mDataType);
    }

    @Override
    public void updateNewOnlineClassifyConfigView(@Nullable List<NewOnlineConfigEntity> entities) {
        this.mConfigEntities = entities;
        if(EmptyUtil.isEmpty(entities)) return;
        // 组装Label数据
        configLabel();
        // 递归调用,组装数据
        recursionConfig(entities,CONFIG_TYPE_LEVEL_0);
        // 设置TabLayout相关监听
        initTabListener();
        // 设置数据到TabLayout上
        initTabControl();
    }

    /**
     * 组装Label数据
     */
    private void configLabel(){

        // 是否配置全部
        if(mDataType == DataType.MINORITY_LANGUAGE){
            // 小语种课程
            // 二级页面
            mTabVector3.setVisibility(View.GONE);
            mTabVector4.setVisibility(View.GONE);

            // 语言 科目
            mTabLabel1.setText(getString(R.string.label_colon_language));
            mTabLabel2.setText(getString(R.string.label_colon_subject));
        }else if(mDataType == DataType.INTERNATIONAL){
            // 国际课程
            // 三级页面
            mTabVector2.setVisibility(View.GONE);
            mTabVector3.setVisibility(View.VISIBLE);
            mTabVector4.setVisibility(View.VISIBLE);

            // 学段 类型 类型 科目
            mTabLabel1.setText(getString(R.string.label_colon_period));
            mTabLabel2.setText(getString(R.string.label_colon_type));
            mTabLabel3.setText(getString(R.string.label_colon_type));
            mTabLabel4.setText(getString(R.string.label_colon_subject));
        }else if(mDataType == DataType.BASIC_COURSE){
            // 国家课程
            // 三级页面
            mTabVector2.setVisibility(View.GONE);
            mTabVector3.setVisibility(View.VISIBLE);
            mTabVector4.setVisibility(View.VISIBLE);

            // 学段 类型 年级 科目
            mTabLabel1.setText(getString(R.string.label_colon_period));
            mTabLabel2.setText(getString(R.string.label_colon_type));
            mTabLabel3.setText(getString(R.string.label_colon_grade));
            mTabLabel4.setText(getString(R.string.label_colon_subject));
        }
    }

    /**
     * 填充数据,设置监听
     */
    private void initTabControl(){
        initTabControl1();
        initTabControl2();
        initTabControl3();
        initTabControl4();
    }

    private void initTabControl1(){
        mTabLayout1.removeAllTabs();
        for (Tab tab:mFiltrateArray1) {
            View tabView = UIUtil.inflate(R.layout.item_tab_control_layout);
            TextView tvContent = (TextView) tabView.findViewById(R.id.tv_content);
            tvContent.setText(tab.getConfigValue());
            // 将tab数据作为Tag设置到TabLayout的TabLayout.Tab上
            TabLayout.Tab newTab = mTabLayout1.newTab().setCustomView(tabView).setTag(tab);
            // 默认选中第一个Tab
            if(mTabLayout1.getTabCount() == 0){
                mTabLayout1.addTab(newTab,true);
            }else{
                mTabLayout1.addTab(newTab);
            }
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
            mTabLayout2.addTab(newTab);
        }

        mTabLayout2.smoothScrollTo(0,0);
    }

    private void initTabControl3(){
        mTabLayout3.removeAllTabs();
        if(mDataType != DataType.MINORITY_LANGUAGE && EmptyUtil.isNotEmpty(mFiltrateArray3)){
            for (Tab tab:mFiltrateArray3) {
                View tabView = UIUtil.inflate(R.layout.item_tab_control_layout);
                TextView tvContent = (TextView) tabView.findViewById(R.id.tv_content);
                tvContent.setText(tab.getConfigValue());
                TabLayout.Tab newTab = mTabLayout3.newTab().setCustomView(tabView).setTag(tab);
                mTabLayout3.addTab(newTab);
            }
        }

        mTabLayout3.smoothScrollTo(0,0);
    }

    private void initTabControl4(){
        mTabLayout4.removeAllTabs();
        if(mDataType != DataType.MINORITY_LANGUAGE && EmptyUtil.isNotEmpty(mFiltrateArray4)){
            for (Tab tab:mFiltrateArray4) {
                View tabView = UIUtil.inflate(R.layout.item_tab_control_layout);
                TextView tvContent = (TextView) tabView.findViewById(R.id.tv_content);
                tvContent.setText(tab.getConfigValue());
                TabLayout.Tab newTab = mTabLayout4.newTab().setCustomView(tabView).setTag(tab);
                mTabLayout4.addTab(newTab);
            }
        }

        mTabLayout4.smoothScrollTo(0,0);
    }

    /**
     * 设置相关联动的监听
     */
    private void initTabListener(){
        mTabLayout1.addOnTabSelectedListener(new TabSelectedAdapter(){
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                super.onTabSelected(tab);
                // 全部发生数据联动
                Tab tabData = (Tab) tab.getTag();
                setTabItemSelected(mFiltrateArray1,tabData);
                // 重新配置二级TabLayout
                clearArray(CONFIG_TYPE_LEVEL_1);
                recursionConfigArray(tabData.getChildList());
                initTabControl2();
                initTabControl3();
                initTabControl4();
                // 数据请求
                // triggerUpdateData();
            }
        });

        mTabLayout2.addOnTabSelectedListener(new TabSelectedAdapter(){
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                super.onTabSelected(tab);
                Tab tabData = (Tab) tab.getTag();
                setTabItemSelected(mFiltrateArray2,tabData);
                // 高中,初中的国际课程才会发生二三两个标签的联动,其它不联动
                TabLayout.Tab tabAt = mTabLayout1.getTabAt(mTabLayout1.getSelectedTabPosition());
                if(EmptyUtil.isNotEmpty(tabAt)) {
                    Tab tab1Tab = (Tab) tabAt.getTag();
                    if(tab1Tab.getId() == JUNIOR_HIGH_SCHOOL_ID ||
                            tab1Tab.getId() == SENIOR_HIGH_SCHOOL_ID){
                        // 初中或者高中

                        if(tabData.getId() == THREE_LEVEL_LINKAGE_JUNIOR ||
                                tabData.getId() == THREE_LEVEL_LINKAGE_SENIOR){
                            // 国际课程
                            // 重新配置34数据的联动效果
                            clearArray(CONFIG_TYPE_LEVEL_2);
                            recursionConfigArray(tabData.getChildList());
                            initTabControl3();
                            initTabControl4();
                        }
                    }
                }


                // 数据请求
                // triggerUpdateData();
            }
        });

        if(mDataType != DataType.MINORITY_LANGUAGE){
            mTabLayout3.addOnTabSelectedListener(new TabSelectedAdapter(){
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    super.onTabSelected(tab);
                    Tab tabData = (Tab) tab.getTag();
                    setTabItemSelected(mFiltrateArray3,tabData);

                    // 高中,初中的国际课程才会发生二三两个标签的联动,其它不联动
                    TabLayout.Tab tabAt = mTabLayout1.getTabAt(mTabLayout1.getSelectedTabPosition());
                    if(EmptyUtil.isNotEmpty(tabAt)) {
                        Tab tab1Tab = (Tab) tabAt.getTag();
                        if(tab1Tab.getId() == JUNIOR_HIGH_SCHOOL_ID ||
                                tab1Tab.getId() == SENIOR_HIGH_SCHOOL_ID){

                            if(mDataType == DataType.INTERNATIONAL && tabData.isAll() ||
                                    tabData.getParentId() == THREE_LEVEL_LINKAGE_JUNIOR ||
                                    tabData.getParentId() == THREE_LEVEL_LINKAGE_SENIOR){

                                // 三级联动
                                if(tabData.isAll()){
                                    List<NewOnlineConfigEntity> entities = new ArrayList<>();
                                    for (Tab item:mFiltrateArray3) {
                                        if(!item.isAll() && EmptyUtil.isNotEmpty(item.getChildList())){
                                            entities.addAll(item.getChildList());
                                        }
                                    }
                                    tabData.setChildList(entities);
                                }

                                // 重新配置34数据的联动效果
                                clearArray(CONFIG_TYPE_LEVEL_3);
                                recursionConfigArray(tabData.getChildList());
                                initTabControl4();

                            }else{
                                // 不是国际课程 就可以
                                triggerUpdateData();
                            }
                        }else{
                            // 不是初中高中 就可以
                            triggerUpdateData();
                        }
                    }
                }
            });
        }

        if(mDataType != DataType.MINORITY_LANGUAGE){
            mTabLayout4.addOnTabSelectedListener(new TabSelectedAdapter(){
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    super.onTabSelected(tab);
                    Tab tabData = (Tab) tab.getTag();
                    setTabItemSelected(mFiltrateArray4,tabData);
                    // 数据请求
                    triggerUpdateData();
                }
            });
        }
    }

    /**
     * 设置该Tab选中
     * @param array 对应的Tab集合
     * @param tab 选择的Tab
     */
    private void setTabItemSelected(@NonNull List<Tab> array, @NonNull Tab tab){
        if(EmptyUtil.isEmpty(array) || EmptyUtil.isEmpty(tab)) return;
        for (Tab item:array) {
            tab.setChecked(false);
            if(item.equals(tab)){
                tab.setChecked(true);
            }
        }
    }

    /**
     * 递归调用
     */
    private void recursionConfig(List<NewOnlineConfigEntity> entity, int configType){
        clearArray(configType);
        recursionConfigArray(entity);
    }

    /**
     * 清空集合
     */
    private void clearArray(int configType){
        // 清空所有数据
        if(configType <= CONFIG_TYPE_LEVEL_3){
            // 清除第三个
            mFiltrateArray4.clear();
        }

        if(configType <= CONFIG_TYPE_LEVEL_2){
            // 清除第二个
            mFiltrateArray3.clear();
        }

        if(configType <= CONFIG_TYPE_LEVEL_1){
            // 清除第一个
            mFiltrateArray2.clear();
        }

        if(configType <= CONFIG_TYPE_LEVEL_0){
            // 清除第一个
            mFiltrateArray1.clear();
        }
    }

    /**
     * 递归调用
     */
    private void recursionConfigArray(@NonNull List<NewOnlineConfigEntity> array){
        if(EmptyUtil.isEmpty(array)) return;

        for (NewOnlineConfigEntity entity:array) {
            if(entity.getConfigType() == CONFIG_TYPE_LEVEL_0){
                Tab tab = Tab.build(entity);
                if(!mFiltrateArray1.contains(tab)){
                    mFiltrateArray1.add(Tab.build(entity));
                }
            }

            if(entity.getConfigType() == CONFIG_TYPE_LEVEL_1){
                Tab tab = Tab.build(entity);
                if(!mFiltrateArray2.contains(tab)){
                    mFiltrateArray2.add(Tab.build(entity));
                }
            }

            if(entity.getConfigType() == CONFIG_TYPE_LEVEL_2){
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

            if(entity.getConfigType() == CONFIG_TYPE_LEVEL_3){
                Tab tab = Tab.build(entity);
                if(!mFiltrateArray4.contains(tab)){
                    mFiltrateArray4.add(Tab.build(entity));
                }
                // 第三个筛选容器,加全部
                Tab allTab4 = Tab.buildAll(mAllText,null);
                if(!mFiltrateArray4.contains(allTab4)){
                    mFiltrateArray4.add(0,allTab4);
                }
            }

            // 递归调用
            List<NewOnlineConfigEntity> childList = entity.getChildList();
            recursionConfigArray(childList);
        }
    }

    /**
     * 发生价格排序变化
     *
     * @param state 状态  1 UP 2 Down
     */
    private void triggerPriceSwitch(int state) {
        if (state == PriceArrowView.STATE_UP) {
            // 升序
            for (PagerNavigator navigator : mNavigatorList) {
                boolean isVisible = navigator.triggerPriceSwitch(true);
                if (isVisible) break;
            }
        } else if (state == PriceArrowView.STATE_DOWN) {
            // 降序
            for (PagerNavigator navigator : mNavigatorList) {
                boolean isVisible = navigator.triggerPriceSwitch(false);
                if (isVisible) break;
            }
        }
    }

    /**
     * 标签发生改变
     */
    private void triggerUpdateData(){
        if(EmptyUtil.isNotEmpty(mNavigatorList)){
            for (PagerNavigator navigator : mNavigatorList) {
                // 重新加载数据
                navigator.reloadData();
            }
        }
    }

    @Override
    public int[] getFiltrateParams() {
        int[] params = new int[]{0,0,0,0};
        TabLayout.Tab tab1 = mTabLayout1.getTabAt(mTabLayout1.getSelectedTabPosition());
        if(EmptyUtil.isEmpty(tab1)) return params;
        int firstId = ((Tab)tab1.getTag()).getId();
        TabLayout.Tab tab2 = mTabLayout2.getTabAt(mTabLayout2.getSelectedTabPosition());
        if(EmptyUtil.isEmpty(tab2)) return params;
        int secondId = ((Tab)tab2.getTag()).getId();
        TabLayout.Tab tab3 = mTabLayout3.getTabAt(mTabLayout3.getSelectedTabPosition());
        if(EmptyUtil.isEmpty(tab3)) return params;
        int thirdId = ((Tab)tab3.getTag()).getId();
        TabLayout.Tab tab4 = mTabLayout4.getTabAt(mTabLayout4.getSelectedTabPosition());
        if(EmptyUtil.isEmpty(tab4)) return params;
        int fourthId = ((Tab)tab4.getTag()).getId();

        params[0] = firstId;
        params[1] = secondId;
        params[2] = thirdId;
        params[3] = fourthId;

        return params;
    }

    @Override
    public String getKeyWord() {
        return mKeyWord;
    }

    /**
     * 触发搜索
     */
    private void triggerSearch(){
        final TextView mTvTitle = (TextView) mTopBar.findViewById(R.id.title_tv);
        String configValue = mTvTitle.getText().toString();
        SearchActivity.show(this,HideSortType.TYPE_NEW_ONLINE_CLASS,configValue,KEY_EXTRA_SEARCH_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == KEY_EXTRA_SEARCH_CODE){
                // 更新字符串发生更新
                // 设置Top隐藏
                // mTopBar.findViewById(R.id.right_function1_image).setVisibility(View.GONE);
                mKeyWord = data.getStringExtra(SearchActivity.KEY_EXTRA_SEARCH_KEYWORD);
                // 刷新数据
                triggerUpdateData();
            }
        }
    }

    /**
     * 小语种,国际课程,国家课程 名师课更多页面的入口
     * @param context 上下文对象
     */
    public static void show(@NonNull Context context,
                            @NonNull DataType dataType){
        Intent intent = new Intent(context,NewOnlineClassifyFiltrateActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_EXTRA_DATA_TYPE,dataType);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    /**
     * @desc 定义的课程类型枚举类
     */
    public enum DataType implements Serializable {

        MINORITY_LANGUAGE(0),BASIC_COURSE(1),INTERNATIONAL(2);

        private int index;

        DataType(int index) {
            this.index = index;
        }

        public int getIndex() {
            return index;
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
}
