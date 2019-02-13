package com.lqwawa.intleducation.module.discovery.ui.lqcourse.basics.home;

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
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.CourseEmptyView;
import com.lqwawa.intleducation.base.PresenterActivity;
import com.lqwawa.intleducation.base.widgets.ControlViewPager;
import com.lqwawa.intleducation.base.widgets.PriceArrowView;
import com.lqwawa.intleducation.base.widgets.PullRefreshView.PullToRefreshView;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.base.widgets.adapter.TabSelectedAdapter;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;
import com.lqwawa.intleducation.module.discovery.adapter.CourseListAdapter;
import com.lqwawa.intleducation.module.discovery.ui.CourseDetailsActivity;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.basics.home.pager.LQBasicFiltratePagerFragment;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.basics.home.pager.PagerParams;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.filtrate.HideSortType;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.search.SearchActivity;
import com.lqwawa.intleducation.module.discovery.ui.study.filtrate.ActivityNavigator;
import com.lqwawa.intleducation.module.discovery.ui.study.filtrate.NewOnlineStudyFiltrateActivity;
import com.lqwawa.intleducation.module.discovery.ui.study.filtrate.NewOnlineStudyFiltrateParams;
import com.lqwawa.intleducation.module.discovery.ui.study.filtrate.PagerNavigator;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;
import com.lqwawa.intleducation.module.onclass.OnlineSortType;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author mrmedici
 * @desc 国家课程二级列表筛选的页面
 */
public class LQBasicFiltrateActivity extends PresenterActivity<LQBasicFiltrateContract.Presenter>
    implements LQBasicFiltrateContract.View,ActivityNavigator {

    private static final int KEY_SEARCH_REQUEST_CODE = 1 << 0;

    private static final String KEY_EXTRA_PARENT_ID = "KEY_EXTRA_PARENT_ID";
    private static final String KEY_EXTRA_LEVEL = "KEY_EXTRA_LEVEL";
    private static final String KEY_EXTRA_CONFIG_VALUE = "KEY_EXTRA_CONFIG_VALUE";
    private static final String KEY_EXTRA_KEY_STRING = "KEY_EXTRA_KEY_STRING";
    private static final String KEY_VISITOR_SEARCH_MODE = "KEY_VISITOR_SEARCH_MODE";

    // 全部文本
    private String mAllText = UIUtil.getString(R.string.label_course_filtrate_all);

    private static final int CONFIG_TYPE_1 = 2;
    private static final int CONFIG_TYPE_2 = 3;
    private static final int CONFIG_TYPE_3 = 4;
    private static final int CONFIG_TYPE_4 = 5;

    private TopBar mTopBar;

    private LinearLayout mHeaderLayout;
    private LinearLayout mTabVector1,mTabVector2,mTabVector3;
    private TextView mTabLabel1,mTabLabel2,mTabLabel3;
    private TabLayout mTabLayout1,mTabLayout2,mTabLayout3;

    private TabLayout mSortLayout;
    private ControlViewPager mViewPager;


    private String[] mTabTitles;
    private List<PagerNavigator> mNavigatorList;
    private PriceArrowView mPriceArrowView;
    // 价格tab是否显示
    private boolean priceTabVisible;

    private String mSearchKey;

    private int mParentId;
    private String mLevel;
    private String mConfigValue;
    private boolean mVisitorSearchMode;
    private List<LQCourseConfigEntity> mConfigEntities;
    // 筛选集合1
    private List<Tab> mFiltrateArray1;
    // 筛选集合2
    private List<Tab> mFiltrateArray2;
    // 筛选集合3
    private List<Tab> mFiltrateArray3;

    // private PullToRefreshView mRefreshLayout;
    // private CourseEmptyView mEmptyView;
    // private ListView mListView;
    // Adapter
    // private CourseListAdapter courseListAdapter;

    // private int currentPage;

    @Override
    protected LQBasicFiltrateContract.Presenter initPresenter() {
        return new LQBasicFiltratePresenter(this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_lq_basic_filtrate;
    }

    @Override
    protected boolean initArgs(@NonNull Bundle bundle) {
        mParentId = bundle.getInt(KEY_EXTRA_PARENT_ID);
        mLevel = bundle.getString(KEY_EXTRA_LEVEL);
        mConfigValue = bundle.getString(KEY_EXTRA_CONFIG_VALUE);
        mSearchKey = bundle.getString(KEY_EXTRA_KEY_STRING,null);
        mVisitorSearchMode = bundle.getBoolean(KEY_VISITOR_SEARCH_MODE);
        if(EmptyUtil.isEmpty(mConfigValue)) return false;
        return super.initArgs(bundle);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mTopBar = (TopBar) findViewById(R.id.top_bar);
        mTopBar.setBack(true);
        mTopBar.setTitle(mConfigValue);
        mTopBar.setRightFunctionImage1(R.drawable.search, view-> triggerSearch());

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

        mTabLabel1.setText(getString(R.string.label_colon_grade));
        mTabLabel2.setText(getString(R.string.label_colon_subject));
        mTabLabel3.setText(getString(R.string.label_colon_book_concern));

        /*mEmptyView = (CourseEmptyView) findViewById(R.id.empty_layout);
        mRefreshLayout = (PullToRefreshView) findViewById(R.id.refresh_layout);
        mListView = (ListView) findViewById(R.id.listView);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CourseVo vo = (CourseVo) courseListAdapter.getItem(position);
                CourseDetailsActivity.start(LQBasicFiltrateActivity.this, vo.getId(), true, UserHelper.getUserId());
            }
        });

        // 下拉刷新
        mRefreshLayout.setLastUpdated(new Date().toLocaleString());
        mRefreshLayout.showRefresh();
        mRefreshLayout.setOnHeaderRefreshListener(new PullToRefreshView.OnHeaderRefreshListener() {
            @Override
            public void onHeaderRefresh(PullToRefreshView view) {
                requestCourseData(false);
            }
        });

        mRefreshLayout.setOnFooterRefreshListener(new PullToRefreshView.OnFooterRefreshListener() {
            @Override
            public void onFooterRefresh(PullToRefreshView view) {
                requestCourseData(true);
            }
        });*/
    }

    @Override
    protected void initData() {
        super.initData();
        mFiltrateArray1 = new ArrayList<>();
        mFiltrateArray2 = new ArrayList<>();
        mFiltrateArray3 = new ArrayList<>();

        if(mVisitorSearchMode){
            mHeaderLayout.setVisibility(View.GONE);
            mTopBar.findViewById(R.id.right_function1_image).setVisibility(View.GONE);
            requestCourseData(false);
        }else{
            // 获取标签数据
            mHeaderLayout.setVisibility(View.VISIBLE);
            mPresenter.requestBasicConfigData(mParentId,1);
        }
    }

    @Override
    public void updateBasicConfigView(@NonNull List<LQCourseConfigEntity> entities) {
        // UIUtil.showToastSafe("获取到标签数据");
        this.mConfigEntities = entities;

        // 默认第一个选中
        if(EmptyUtil.isNotEmpty(entities)){
            recursionConfig(entities);
            initTabControl();
            // 设置TabLayout相关监听
            initTabListener();

            mTabTitles = UIUtil.getStringArray(R.array.label_online_class_tabs);
            PagerParams recentUpdateParams = new PagerParams(mLevel,mSearchKey,OnlineSortType.TYPE_SORT_ONLINE_CLASS_RECENT_UPDATE);
            PagerParams hotParams = new PagerParams(mLevel,mSearchKey,OnlineSortType.TYPE_SORT_ONLINE_CLASS_HOT_RECOMMEND);
            PagerParams priceParams = new PagerParams(mLevel,mSearchKey,OnlineSortType.TYPE_SORT_ONLINE_CLASS_PRICE_UP);

            LQBasicFiltratePagerFragment recentUpdateFragment = LQBasicFiltratePagerFragment.newInstance(recentUpdateParams,this);
            LQBasicFiltratePagerFragment hotFragment = LQBasicFiltratePagerFragment.newInstance(hotParams,this);
            LQBasicFiltratePagerFragment priceFragment = LQBasicFiltratePagerFragment.newInstance(priceParams,this);

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
        }
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
        if(configType <= CONFIG_TYPE_4){
            // 清除第三个
            mFiltrateArray3.clear();
        }

        if(configType <= CONFIG_TYPE_3){
            // 清除第二个
            mFiltrateArray2.clear();
        }

        if(configType <= CONFIG_TYPE_2){
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
            if(entity.getConfigType() == CONFIG_TYPE_2){
                Tab tab = Tab.build(entity);
                if(!mFiltrateArray1.contains(tab)){
                    mFiltrateArray1.add(Tab.build(entity));
                }
                // 第一个筛选容器,加全部
                Tab allTab1 = Tab.buildAll(mAllText,array);
                if(!mFiltrateArray1.contains(allTab1)){
                    mFiltrateArray1.add(0,allTab1);
                }
            }

            if(entity.getConfigType() == CONFIG_TYPE_3){
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

            if(entity.getConfigType() == CONFIG_TYPE_4){
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
     * 填充数据,设置监听
     */
    private void initTabControl(){
        initTabControl1();
        initTabControl2();
        initTabControl3();
    }

    private void initTabControl1(){
        mTabLayout1.removeAllTabs();

        for (Tab tab:mFiltrateArray1) {
            View tabView = UIUtil.inflate(R.layout.item_tab_control_layout);
            TextView tvContent = (TextView) tabView.findViewById(R.id.tv_content);
            tvContent.setText(tab.getConfigValue());
            // 将tab数据作为Tag设置到TabLayout的TabLayout.Tab上
            TabLayout.Tab newTab = mTabLayout1.newTab().setCustomView(tabView).setTag(tab);
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
                mTabLayout3.addTab(newTab);
            }
        }

        mTabLayout3.smoothScrollTo(0,0);
    }

    /**
     * 设置相关联动的监听
     */
    private void initTabListener(){
        mTabLayout1.removeOnTabSelectedListener(tabLayout1Adapter);
        mTabLayout1.addOnTabSelectedListener(tabLayout1Adapter);

        mTabLayout2.removeOnTabSelectedListener(tabLayout2Adapter);
        mTabLayout2.addOnTabSelectedListener(tabLayout2Adapter);

        mTabLayout3.removeOnTabSelectedListener(tabLayout3Adapter);
        mTabLayout3.addOnTabSelectedListener(tabLayout3Adapter);
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

        int[] params = new int[]{0,0,0};

        // 获取筛选条件
        int paramOneId = 0,paramTwoId = 0,paramThreeId = 0;

        if(EmptyUtil.isEmpty(mSearchKey)) mSearchKey = "";

        for (Tab tab : mFiltrateArray1){
            if(tab.isChecked()){
                paramOneId = tab.getLabelId();
                break;
            }
        }

        for (Tab tab : mFiltrateArray2){
            if(tab.isChecked()){
                paramTwoId = tab.getLabelId();
                break;
            }
        }

        for (Tab tab : mFiltrateArray3){
            if(tab.isChecked()){
                paramThreeId = tab.getLabelId();
                break;
            }
        }

        params[0] = paramOneId;
        params[1] = paramTwoId;
        params[2] = paramThreeId;

        return params;

    }

    @Override
    public String getKeyWord() {
        return mSearchKey;
    }

    private TabSelectedAdapter tabLayout1Adapter = new TabSelectedAdapter(){
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            super.onTabSelected(tab);
            // 全部发生数据联动
            Tab tabData = (Tab) tab.getTag();
            setTabItemSelected(mFiltrateArray1,tabData);
            // 数据请求
            triggerUpdateData();
        }
    };

    private TabSelectedAdapter tabLayout2Adapter = new TabSelectedAdapter(){
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            super.onTabSelected(tab);
            // 全部发生数据联动
            Tab tabData = (Tab) tab.getTag();
            setTabItemSelected(mFiltrateArray2,tabData);
            // 数据请求
            triggerUpdateData();
        }
    };

    private TabSelectedAdapter tabLayout3Adapter = new TabSelectedAdapter(){
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            super.onTabSelected(tab);
            // 全部发生数据联动
            Tab tabData = (Tab) tab.getTag();
            setTabItemSelected(mFiltrateArray3,tabData);
            // 数据请求
            triggerUpdateData();
        }
    };

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
     * 请求数据
     * @param more 是否加载更多
     */
    private void requestCourseData(boolean more){
        // 获取筛选条件
        int paramOneId = 0,paramTwoId = 0,paramThreeId = 0;

        if(EmptyUtil.isEmpty(mSearchKey)) mSearchKey = "";

        for (Tab tab : mFiltrateArray1){
            if(tab.isChecked()){
                paramOneId = tab.getLabelId();
                break;
            }
        }

        for (Tab tab : mFiltrateArray2){
            if(tab.isChecked()){
                paramTwoId = tab.getLabelId();
                break;
            }
        }

        for (Tab tab : mFiltrateArray3){
            if(tab.isChecked()){
                paramThreeId = tab.getLabelId();
                break;
            }
        }

        /*if(more){
            currentPage++;
            mPresenter.requestCourseData(more,currentPage, AppConfig.PAGE_SIZE,mLevel,mSearchKey,paramOneId,paramTwoId,paramThreeId);
        }else{
            currentPage = 0;
            mPresenter.requestCourseData(more,currentPage,AppConfig.PAGE_SIZE,mLevel,mSearchKey,paramOneId,paramTwoId,paramThreeId);
        }*/
    }

    @Override
    public void updateCourseView(@NonNull List<CourseVo> courseVos) {
        // 判断有无更多数据,打开或者关闭加载更多
        /*mRefreshLayout.onHeaderRefreshComplete();
        mRefreshLayout.setLoadMoreEnable(courseVos.size() >= AppConfig.PAGE_SIZE);
        // 设置数据
        courseListAdapter = new CourseListAdapter(this);
        courseListAdapter.setData(courseVos);
        mListView.setAdapter(courseListAdapter);

        if(EmptyUtil.isEmpty(courseVos)){
            // 数据为空
            mRefreshLayout.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
        }else{
            // 数据不为空
            mRefreshLayout.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.GONE);
        }*/
    }

    @Override
    public void updateMoreCourseView(@NonNull List<CourseVo> courseVos) {
        // 关闭加载更多
        /*mRefreshLayout.onFooterRefreshComplete();
        mRefreshLayout.setLoadMoreEnable(courseVos.size() >= AppConfig.PAGE_SIZE);
        // 设置数据
        courseListAdapter.addData(courseVos);
        courseListAdapter.notifyDataSetChanged();*/
    }

    /**
     * 触发搜索功能
     */
    private void triggerSearch(){
        SearchActivity.show(this,HideSortType.TYPE_SORT_BASIC_GRADE,mConfigValue,KEY_SEARCH_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == KEY_SEARCH_REQUEST_CODE){
                // 更新字符串发生更新
                mSearchKey = data.getStringExtra(SearchActivity.KEY_EXTRA_SEARCH_KEYWORD);
                // 刷新数据
                triggerUpdateData();
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
     * 国家课程二级列表筛选页面的入口
     * @param context 上下文对象
     * @param parentId 一级列表点击的Id
     * @param level 一级标签的Level
     * @param configValue 点击的标题
     */
    public static void show(@NonNull Context context, int parentId,
                            @NonNull String level,
                            @NonNull String configValue){
        Intent intent = new Intent(context,LQBasicFiltrateActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_EXTRA_PARENT_ID,parentId);
        bundle.putString(KEY_EXTRA_LEVEL,level);
        bundle.putString(KEY_EXTRA_CONFIG_VALUE,configValue);
        bundle.putBoolean(KEY_VISITOR_SEARCH_MODE,false);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    /**
     * 国家课程二级列表筛选页面的入口
     * @param context 上下文对象
     * @param level 一级标签的Level
     * @param configValue 点击的标题
     * @param keyString 搜索的字符串
     */
    public static void show(@NonNull Context context,
                            @NonNull String level,
                            @NonNull String configValue,
                            @Nullable String keyString){
        Intent intent = new Intent(context,LQBasicFiltrateActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(KEY_EXTRA_LEVEL,level);
        bundle.putString(KEY_EXTRA_CONFIG_VALUE,configValue);
        bundle.putString(KEY_EXTRA_KEY_STRING,keyString);
        bundle.putBoolean(KEY_VISITOR_SEARCH_MODE,true);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}
