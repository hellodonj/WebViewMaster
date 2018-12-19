package com.lqwawa.intleducation.module.discovery.ui.study.filtrate;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
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
import com.lqwawa.intleducation.factory.data.entity.online.ParamResponseVo;
import com.lqwawa.intleducation.factory.event.EventConstant;
import com.lqwawa.intleducation.factory.event.EventWrapper;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.filtrate.HideSortType;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.search.SearchActivity;
import com.lqwawa.intleducation.module.discovery.ui.study.filtrate.pager.NewOnlineStudyFiltratePagerFragment;
import com.lqwawa.intleducation.module.discovery.ui.study.filtrate.pager.PagerParams;
import com.lqwawa.intleducation.module.onclass.OnlineClassListActivity;
import com.lqwawa.intleducation.module.onclass.OnlineSortType;
import com.lqwawa.intleducation.module.onclass.SearchNavigator;
import com.lqwawa.intleducation.module.onclass.pager.OnlineClassPagerFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mrmedici
 * @desc V5.11版本 新讲授课堂筛选列表
 */
public class NewOnlineStudyFiltrateActivity extends PresenterActivity<NewOnlineStudyFiltrateContract.Presenter>
    implements NewOnlineStudyFiltrateContract.View,ActivityNavigator{

    public static final int SEARCH_REQUEST_CODE = 100;

    // 配置小语种课程的ID，其它ID都是三级列表
    private static final int MINORITY_LANGUGAE_ID = 5005;
    // 配置初中高中课程的ID,这两个ID下的国际课程是三级联动
    private static final int JUNIOR_HIGH_SCHOOL_ID = 5003;
    private static final int SENIOR_HIGH_SCHOOL_ID = 5004;

    // 有三级联动的ID
    private static final int THREE_LEVEL_LINKAGE_JUNIOR = 5011;
    private static final int THREE_LEVEL_LINKAGE_SENIOR = 5013;

    // 需要显示的ConfigType
    private static final int CONFIG_TYPE_LEVEL_1 = 12;
    private static final int CONFIG_TYPE_LEVEL_2 = 13;
    private static final int CONFIG_TYPE_LEVEL_3 = 14;

    private TopBar mTopBar;
    private LinearLayout mHeaderLayout;
    private LinearLayout mTabVector1,mTabVector2,mTabVector3;
    private TextView mTabLabel1,mTabLabel2,mTabLabel3;
    private TabLayout mTabLayout1,mTabLayout2,mTabLayout3;
    private FrameLayout mBodyLayout;
    private TabLayout mSortLayout;
    private ControlViewPager mViewPager;


    private String[] mTabTitles;
    private List<PagerNavigator> mNavigatorList;
    private PriceArrowView mPriceArrowView;
    // 价格tab是否显示
    private boolean priceTabVisible;

    private NewOnlineStudyFiltrateParams mFiltrateParams;
    private boolean mHideTop;
    private String mKeyWord;
    private String mConfigValue;
    // 模式
    private int mMode;
    private ParamResponseVo.Param mParam;
    private NewOnlineConfigEntity mConfigEntity;
    // 全部文本
    private String mAllText = UIUtil.getString(R.string.label_course_filtrate_all);
    // 筛选集合1
    private List<Tab> mFiltrateArray1;
    // 筛选集合2
    private List<Tab> mFiltrateArray2;
    // 筛选集合3
    private List<Tab> mFiltrateArray3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_new_online_study_filtrate;
    }

    @Override
    protected NewOnlineStudyFiltrateContract.Presenter initPresenter() {
        return new NewOnlineStudyFiltratePresenter(this);
    }

    @Override
    protected boolean initArgs(@NonNull Bundle bundle) {
        if(bundle.containsKey(ACTIVITY_BUNDLE_OBJECT)){
            mFiltrateParams = (NewOnlineStudyFiltrateParams) bundle.getSerializable(ACTIVITY_BUNDLE_OBJECT);
            mHideTop = mFiltrateParams.isHideTop();
            mKeyWord = mFiltrateParams.getKeyWord();
            mConfigValue = mFiltrateParams.getConfigValue();
            mMode = mFiltrateParams.getMode();
            mParam = mFiltrateParams.getParam();
            mConfigEntity = mFiltrateParams.getConfigEntity();
        }

        if(null == mConfigValue) return false;
        if(mMode == NewOnlineStudyFiltrateParams.VIEW_MODE_HIDE_TOP){
            if(EmptyUtil.isEmpty(mParam)) return false;
        }

        if(mMode == NewOnlineStudyFiltrateParams.VIEW_MODE_FILTRATE){
            if(EmptyUtil.isEmpty(mConfigEntity)) return false;
        }
        return super.initArgs(bundle);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mTopBar = (TopBar) findViewById(R.id.top_bar);
        mTopBar.setBack(true);
        mTopBar.setTitle(mFiltrateParams.getConfigValue());

        if(mMode == NewOnlineStudyFiltrateParams.VIEW_MODE_FILTRATE && !mHideTop)
        // 只有这一种情况,显示搜索
        mTopBar.setRightFunctionImage1(R.drawable.search,view -> search());

        mHeaderLayout = (LinearLayout) findViewById(R.id.header_layout);
        mBodyLayout = (FrameLayout) findViewById(R.id.body_layout);

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

        if(mMode == NewOnlineStudyFiltrateParams.VIEW_MODE_HIDE_TOP){
            // 隐藏Top
            mHeaderLayout.setVisibility(View.GONE);
            mBodyLayout.setVisibility(View.GONE);
        }
    }

    @Override
    protected void initData() {
        super.initData();

        if(mMode == NewOnlineStudyFiltrateParams.VIEW_MODE_FILTRATE){
            // 组装Label数据
            configLabel();
            // 设置数据到TabLayout上
            initTabControl();
            // 设置TabLayout相关监听
            initTabListener();
        }else{
            // mPresenter.requestOnlineStudyLabelData();
        }

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

        if(mMode == NewOnlineStudyFiltrateParams.VIEW_MODE_FILTRATE){
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
        }else{
            // 不可滑动
            mViewPager.setScanScroll(false);
        }

        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void updateOnlineStudyLabelView(@NonNull List<NewOnlineConfigEntity> entities) {
        if(mMode == NewOnlineStudyFiltrateParams.VIEW_MODE_HIDE_TOP){
            for (NewOnlineConfigEntity entity:entities) {
                if(entity.getId() == mParam.getFirstId()){
                    mConfigEntity = entity;
                    break;
                }
            }
        }
    }

    /**
     * 组装Label数据
     */
    private void configLabel(){
        mFiltrateArray1 = new ArrayList<>();
        mFiltrateArray2 = new ArrayList<>();
        mFiltrateArray3 = new ArrayList<>();

        if(EmptyUtil.isEmpty(mConfigEntity)) return;
        recursionConfig(mConfigEntity,CONFIG_TYPE_LEVEL_1);

        // 是否配置全部
        if(mConfigEntity.getId() == MINORITY_LANGUGAE_ID){
            // 二级页面
            mTabVector3.setVisibility(View.GONE);

            // 语言 级别
            mTabLabel1.setText(getString(R.string.label_colon_language));
            mTabLabel2.setText(getString(R.string.label_colon_level));
        }else{
            // 三级页面
            mTabVector3.setVisibility(View.VISIBLE);

            // 类型 年级 科目
            mTabLabel1.setText(getString(R.string.label_colon_type));
            mTabLabel2.setText(getString(R.string.label_colon_grade));
            mTabLabel3.setText(getString(R.string.label_colon_subject));
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
        if(mConfigEntity.getId() != MINORITY_LANGUGAE_ID && EmptyUtil.isNotEmpty(mFiltrateArray3)){
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
        mTabLayout1.addOnTabSelectedListener(new TabSelectedAdapter(){
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                super.onTabSelected(tab);
                // 全部发生数据联动
                Tab tabData = (Tab) tab.getTag();
                setTabItemSelected(mFiltrateArray1,tabData);
                // 重新配置2,3数据的联动效果
                clearArray(CONFIG_TYPE_LEVEL_2);
                recursionConfigArray(tabData.getChildList());
                initTabControl2();
                initTabControl3();

                // 数据请求
                triggerUpdateData();
            }
        });

        mTabLayout2.addOnTabSelectedListener(new TabSelectedAdapter(){
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                super.onTabSelected(tab);
                Tab tabData = (Tab) tab.getTag();
                setTabItemSelected(mFiltrateArray2,tabData);
                // 高中,初中的国际课程才会发生二三两个标签的联动,其它不联动
                if(mConfigEntity.getId() == JUNIOR_HIGH_SCHOOL_ID ||
                        mConfigEntity.getId() == SENIOR_HIGH_SCHOOL_ID){
                    // 初中或者高中
                    TabLayout.Tab tabAt = mTabLayout1.getTabAt(mTabLayout1.getSelectedTabPosition());
                    if(EmptyUtil.isNotEmpty(tabAt)){
                        Tab tab1Tab = (Tab) tabAt.getTag();
                        if(tab1Tab.getId() == THREE_LEVEL_LINKAGE_JUNIOR ||
                                tab1Tab.getId() == THREE_LEVEL_LINKAGE_SENIOR){
                            // 国际课程
                            // 三级联动
                            if(tabData.isAll()){
                                List<NewOnlineConfigEntity> entities = new ArrayList<>();
                                for (Tab item:mFiltrateArray2) {
                                    if(!item.isAll() && EmptyUtil.isNotEmpty(item.getChildList())){
                                        entities.addAll(item.getChildList());
                                    }
                                }
                                tabData.setChildList(entities);
                            }

                            // 重新配置3数据的联动效果
                            clearArray(CONFIG_TYPE_LEVEL_3);
                            recursionConfigArray(tabData.getChildList());
                            initTabControl3();
                        }
                    }
                }

                // 数据请求
                triggerUpdateData();
            }
        });

        if(mConfigEntity.getId() != MINORITY_LANGUGAE_ID){
            mTabLayout3.addOnTabSelectedListener(new TabSelectedAdapter(){
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    super.onTabSelected(tab);
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
    private void setTabItemSelected(@NonNull List<Tab> array,@NonNull Tab tab){
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
    private void recursionConfig(NewOnlineConfigEntity entity,int configType){
        List<NewOnlineConfigEntity> childList = entity.getChildList();
        clearArray(configType);
        recursionConfigArray(childList);
    }

    /**
     * 清空集合
     */
    private void clearArray(int configType){
        // 清空所有数据
        if(configType <= CONFIG_TYPE_LEVEL_3){
            // 清除第三个
            mFiltrateArray3.clear();
        }

        if(configType <= CONFIG_TYPE_LEVEL_2){
            // 清除第二个
            mFiltrateArray2.clear();
        }

        if(configType <= CONFIG_TYPE_LEVEL_1){
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
            if(entity.getConfigType() == CONFIG_TYPE_LEVEL_1){
                Tab tab = Tab.build(entity);
                if(!mFiltrateArray1.contains(tab)){
                    mFiltrateArray1.add(Tab.build(entity));
                }
                // 第一个筛选容器,加全部
                Tab allTab1 = Tab.buildAll(mAllText,mConfigEntity.getChildList());
                if(!mFiltrateArray1.contains(allTab1)){
                    mFiltrateArray1.add(0,allTab1);
                }
            }

            if(entity.getConfigType() == CONFIG_TYPE_LEVEL_2){
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

            if(entity.getConfigType() == CONFIG_TYPE_LEVEL_3){
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

        if(mMode == NewOnlineStudyFiltrateParams.VIEW_MODE_FILTRATE){
            int firstId = mConfigEntity.getId();
            int[] params = new int[]{firstId,0,0,0};
            TabLayout.Tab tab1 = mTabLayout1.getTabAt(mTabLayout1.getSelectedTabPosition());
            if(EmptyUtil.isEmpty(tab1)) return params;
            int secondId = ((Tab)tab1.getTag()).getId();
            TabLayout.Tab tab2 = mTabLayout2.getTabAt(mTabLayout2.getSelectedTabPosition());
            if(EmptyUtil.isEmpty(tab2)) return params;
            int thirdId = ((Tab)tab2.getTag()).getId();

            int fourthId = 0;
            if(mConfigEntity.getId() != MINORITY_LANGUGAE_ID){
                TabLayout.Tab tab3 = mTabLayout3.getTabAt(mTabLayout3.getSelectedTabPosition());
                if(EmptyUtil.isEmpty(tab2)) return params;
                fourthId = ((Tab)tab3.getTag()).getId();
            }

            params[1] = secondId;
            params[2] = thirdId;
            params[3] = fourthId;

            return params;
        }else if(mMode == NewOnlineStudyFiltrateParams.VIEW_MODE_HIDE_TOP){
            if(EmptyUtil.isEmpty(mParam)) return null;
            return new int[]{mParam.getFirstId(),mParam.getSecondId(),mParam.getThirdId(),mParam.getFourthId()};
        }

        return null;

    }

    @Override
    public String getKeyWord() {
        return mKeyWord;
    }

    /**
     * 进入搜索页面
     */
    private void search(){
        SearchActivity.show(this,HideSortType.TYPE_SORT_TEACH_ONLINE_CLASS,mFiltrateParams);
    }

    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public void onEvent(@NonNull EventWrapper wrapper){
        if(EventWrapper.isMatch(wrapper,EventConstant.TRIGGER_SEARCH_CALLBACK_EVENT)){
            mKeyWord = (String) wrapper.getData();
            mTopBar.setTitle(mKeyWord);
            triggerUpdateData();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == SEARCH_REQUEST_CODE){
                // 更新字符串发生更新
                // 设置Top隐藏
                mTopBar.findViewById(R.id.right_function1_image).setVisibility(View.GONE);
                mKeyWord = data.getStringExtra(SearchActivity.KEY_EXTRA_SEARCH_KEYWORD);
                // 刷新数据
                triggerUpdateData();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
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
     * 讲授课堂类型筛选列表
     * @param context 上下文对象
     */
    public static void show(@NonNull Context context, @NonNull NewOnlineStudyFiltrateParams params){
        Intent intent = new Intent(context,NewOnlineStudyFiltrateActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ACTIVITY_BUNDLE_OBJECT,params);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}
