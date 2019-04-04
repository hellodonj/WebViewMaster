package com.lqwawa.intleducation.module.discovery.ui.lqcourse.filtrate;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.PresenterActivity;
import com.lqwawa.intleducation.base.widgets.PullRefreshView.PullToRefreshView;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.base.widgets.adapter.TabSelectedAdapter;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;
import com.lqwawa.intleducation.module.discovery.adapter.CourseListAdapter;
import com.lqwawa.intleducation.module.discovery.ui.CourseDetailsActivity;
import com.lqwawa.intleducation.base.CourseEmptyView;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.filtrate.pager.CourseFiltratePagerFragment;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.filtrate.state.AbstractFiltrateState;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.filtrate.state.GroupFiltrateState;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.search.SearchActivity;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 筛选页面, 顶部带搜索框
 * @date 2018/05/03 09:59
 * @history v1.0
 * **********************************
 */
public class CourseFiltrateActivity extends PresenterActivity<CourseFiltrateContract.Presenter>
    implements CourseFiltrateContract.View,
        CompoundButton.OnCheckedChangeListener {

    // 分级阅读ID
    private static final int SORT_LEVEL_READ_ID = 2059;
    // 绘本阅读ID
    private static final int BOOK_READING_ID = 2060;
    // 特色英语ID
    private static final int CHARACTER_ENGLISH_ID = 2096;
    // 基础课程的Id
    public static final int BASIC_COURSE_ID = 2003;
    // 特色英语的ID
    public static final int CHARACTERISTICS_ENGLISH_ID = 2005;
    // tab总数
    private static final int TAB_COUNT = 3;

    // 单个实体
    private static final String KEY_EXTRA_CLASSIFY_ENTITY = "KEY_EXTRA_CLASSIFY_ENTITY";
    // 状态
    private static final String KEY_EXTRA_STATE = "KEY_EXTRA_STATE";
    private static final String KEY_EXTRA_HIDE_TOP = "KEY_EXTRA_HIDE_TOP";
    // 搜索关键词
    private static final String KEY_EXTRA_SEARCH_KEY = "KEY_EXTRA_SEARCH_KEY";

    private String all;

    private TopBar topBar;

    // 头部筛选
    private FrameLayout mTopLayout;
    // 筛选布局根布局
    private LinearLayout mFiltrateLayout;
    // TabLayout
    private TabLayout mTabLayout;
    // 滑动指示
    private ImageView mIvSlide;
    // ViewPager
    private ViewPager mViewPager;
    // RecyclerView内容布局
    private FrameLayout mRecyclerLayout;
    // 内容布局
    private CourseEmptyView mEmptyView;
    // 刷新布局
    private PullToRefreshView mRefreshLayout;
    // 列表布局
    private ListView mListView;
    // Adapter
    private CourseListAdapter courseListAdapter;
    // Tab集合
    private List<CheckBox> mSortButtons;

    // 选择容器
    private LinearLayout mLaySort1,mLaySort2,mLaySort3;
    // 选择Button
    private CheckBox mCbSort1,mCbSort2,mCbSort3;
    // 分割线
    private View mVerticalLine1,mVerticalLine2;

    // PopupWindow
    private SortLinePopupWindow mPopupWindow;

    // 需要显示的configType
    private int mConfigType1 = 3;
    private int mConfigType2 = 4;
    private int mConfigType3 = 5;

    // 所有的筛选条件
    private List<Tab> mFirstTabs;
    private List<Tab> mSecondTabs;
    private List<Tab> mThirdTabs;

    // 状态
    private AbstractFiltrateState mState;
    // 点击的分类实体
    private LQCourseConfigEntity mClassifyEntity;
    // 所有的分类筛选数据
    private List<LQCourseConfigEntity> mAllClassifyEntities;
    // 搜索关键词
    private String mSearchKey;
    // 是否隐藏顶部筛选
    private boolean mHideTop;
    // 是否是联动数据
    private boolean isDataLinkage;
    // 显示TabLayout还是联动效果
    private boolean showTabLayout = true;
    // 分页数
    private int currentPage;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_course_filtrate;
    }

    @Override
    protected boolean initArgs(@NonNull Bundle bundle) {
        mClassifyEntity = (LQCourseConfigEntity) bundle.get(KEY_EXTRA_CLASSIFY_ENTITY);
        mState = (AbstractFiltrateState) bundle.get(KEY_EXTRA_STATE);
        mHideTop = bundle.getBoolean(KEY_EXTRA_HIDE_TOP, false);
        mSearchKey = bundle.getString(KEY_EXTRA_SEARCH_KEY);
        if (EmptyUtil.isEmpty(mState)) {
            return false;
        }
        return super.initArgs(bundle);

    }

    @Override
    protected void initWidget() {
        super.initWidget();
        topBar = (TopBar) findViewById(R.id.top_bar);
        topBar.setBack(true);
        topBar.setTitle(mState.generateTitle());
        // 分享
        topBar.setRightFunctionText1(R.string.label_share, view-> share());

        // 添加搜索
        if(!mHideTop && false){
            // 不是搜索页面过来的
            topBar.setRightFunctionImage1(R.drawable.search, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 筛选页面 mSortType 定死的-1;
                    SearchActivity.show(CourseFiltrateActivity.this,mClassifyEntity,HideSortType.TYPE_SORT_CLASSIFY,mState.generateTitle());
                }
            });
        }

        mTopLayout = (FrameLayout) findViewById(R.id.top_layout);
        mFiltrateLayout = (LinearLayout) findViewById(R.id.filtrate_layout);
        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mIvSlide = (ImageView) findViewById(R.id.iv_slide_tab);

        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mRecyclerLayout = (FrameLayout) findViewById(R.id.recycler_layout);

        mEmptyView = (CourseEmptyView) findViewById(R.id.empty_layout);
        mRefreshLayout = (PullToRefreshView) findViewById(R.id.refresh_layout);
        mListView = (ListView) findViewById(R.id.listView);

        mSortButtons = new ArrayList<>();
        mLaySort1 = (LinearLayout) findViewById(R.id.lay_sort1);
        mLaySort2 = (LinearLayout) findViewById(R.id.lay_sort2);
        mLaySort3 = (LinearLayout) findViewById(R.id.lay_sort3);
        mCbSort1 = (CheckBox) findViewById(R.id.cb_sort1);
        mCbSort2 = (CheckBox) findViewById(R.id.cb_sort2);
        mCbSort3 = (CheckBox) findViewById(R.id.cb_sort3);
        mSortButtons.add(mCbSort1);
        mSortButtons.add(mCbSort2);
        mSortButtons.add(mCbSort3);

        // 设置标题文本
        setTopFiltrateText();

        // 添加状态改变事件
        for(CheckBox btnSort:mSortButtons){
            btnSort.setOnCheckedChangeListener(this);
        }

        mVerticalLine1 = findViewById(R.id.split_view1);
        mVerticalLine2 = findViewById(R.id.split_view2);

        mTabLayout.addOnTabSelectedListener(tabSelectedListener);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CourseVo vo = (CourseVo) courseListAdapter.getItem(position);
                CourseDetailsActivity.start(CourseFiltrateActivity.this, vo.getId(), true, UserHelper.getUserId());
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
        });

        // @date   :2018/5/7 0007 下午 5:36
        // @func   :如果是搜索条件,也继续显示筛选布局
        /*if(mHideTop){
            // 搜索显示
            mTopLayout.setVisibility(View.GONE);
        }*/

    }

    /**
     * TabLayout选择器的监听
     */
    private TabLayout.OnTabSelectedListener tabSelectedListener = new TabSelectedAdapter(){
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            super.onTabSelected(tab);
            Tab tabData = (Tab) tab.getTag();
            clearTabSelected(mFirstTabs);
            tabData.setSelected(true);
            mRefreshLayout.showRefresh();
            requestCourseData(false);
        }
    };

    @Override
    protected void initData() {
        super.initData();
        // 初始化筛选数组
        mFirstTabs = new ArrayList<>();
        mSecondTabs = new ArrayList<>();
        mThirdTabs = new ArrayList<>();

        all = getText(R.string.label_course_filtrate_all).toString();

        isDataLinkage =
                mClassifyEntity.getId() == SORT_LEVEL_READ_ID ||
                mClassifyEntity.getId() == CHARACTER_ENGLISH_ID;

        // 请求数据
        mPresenter.requestConfigData(mClassifyEntity.getConfigType()+1,mClassifyEntity.getId());
    }


    @Override
    protected CourseFiltrateContract.Presenter initPresenter() {
        return new CourseFiltratePresenter(this);
    }

    @Override
    public void onCourseLoaded(List<CourseVo> courseVos) {
        // 判断有无更多数据,打开或者关闭加载更多
        mRefreshLayout.onHeaderRefreshComplete();
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
        }
    }

    @Override
    public void onMoreCourseLoaded(List<CourseVo> courseVos) {
        // 关闭加载更多
        mRefreshLayout.onFooterRefreshComplete();
        mRefreshLayout.setLoadMoreEnable(courseVos.size() >= AppConfig.PAGE_SIZE);
        // 设置数据
        courseListAdapter.addData(courseVos);
        courseListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onConfigLoaded(List<LQCourseConfigEntity> entities) {
        mAllClassifyEntities = entities;
        handleClassifyData(entities);
        // 启动下拉刷新
        mRefreshLayout.setLastUpdated(new Date().toLocaleString());
        mRefreshLayout.showRefresh();
        requestCourseData(false);
    }

    /**
     * 对服务器返回的数据进行处理解析封装
     * @param entities 数据实体
     */
    private void handleClassifyData(@NonNull List<LQCourseConfigEntity> entities){
        if(!isDataLinkage){
            // 没有联动关系，是层级数据
            for (LQCourseConfigEntity entity:entities) {
                if(entity.getConfigType() == mConfigType1){
                    // 一级筛选
                    mFirstTabs.add(Tab.build(entity));
                }else if(entity.getConfigType() == mConfigType2){
                    // 二级筛选
                    mSecondTabs.add(Tab.build(entity));
                }else if(entity.getConfigType() == mConfigType3){
                    // 三级筛选
                    mThirdTabs.add(Tab.build(entity));
                }
            }
        }else{
            for (LQCourseConfigEntity entity:entities) {
                // 第一级不区分
                if(entity.getConfigType() == mConfigType1){
                    // 一级筛选
                    mFirstTabs.add(Tab.build(entity));
                }
            }

            // 发生联动事件
            triggerLinkage(entities);
        }

        buildAll(false);
    }

    /**
     * 发生联动事件
     */
    private void triggerLinkage(@NonNull List<LQCourseConfigEntity> entities){
        // 树形结构,遍历获取child筛选数据
        for (LQCourseConfigEntity entity:entities){
            List<LQCourseConfigEntity> _entities = entity.getChildList();
            for(LQCourseConfigEntity _entity:_entities){
                Tab tab = Tab.build(_entity);
                if(_entity.getConfigType() == mConfigType2
                        && !mFirstTabs.contains(tab)
                        && !mSecondTabs.contains(tab)){
                    // 获取到全部的index
                    int allIndex = mFirstTabs.indexOf(Tab.buildAll(all));
                    if(allIndex >= 0){
                        // 已经初始化过,之前没有发生联动
                        // 全部Tab
                        Tab all = mFirstTabs.get(allIndex);
                        // 试图找出其它选择的Tab
                        Tab selectedTab = Tab.build(entity);
                        // 找到选择Tab的索引
                        int selectedIndex = mFirstTabs.indexOf(selectedTab);
                        if(selectedIndex > 0){
                            Tab reallyTab = mFirstTabs.get(selectedIndex);
                            if(all.isSelected() ||
                                    !all.isSelected() && reallyTab.isSelected() && _entity.getParentId() == entity.getId()){
                                // 判断不是全部筛选类别
                                // 判断是否是上下子级关系
                                mSecondTabs.add(tab);
                            }
                        }

                    }else{
                        // 没有初始化过,直接添加
                        mSecondTabs.add(tab);
                    }
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * 添加全部信息,以及判断Top标题的显示与隐藏
     * @param triggerFiltrate 是否触发筛选
     */
    private void buildAll(boolean triggerFiltrate){
        Tab allTab = Tab.buildAll(all);
        allTab.setSelected(true);
        if((mFirstTabs.size() > 1 || triggerFiltrate) && !mFirstTabs.contains(allTab)){
            // 添加全部
            mFirstTabs.add(0,allTab);
            mCbSort1.setVisibility(View.VISIBLE);
            mLaySort1.setVisibility(View.VISIBLE);
            showTabLayout = true;

        }

        // 必须保证所有的筛选条件 allTab都不是同一个对象
        allTab = Tab.buildAll(all);
        allTab.setSelected(true);
        if((mSecondTabs.size() > 1 || triggerFiltrate) && !mSecondTabs.contains(allTab)){
            // 添加全部
            mSecondTabs.add(0,allTab);
            mLaySort2.setVisibility(View.VISIBLE);
            mCbSort2.setVisibility(View.VISIBLE);
            mVerticalLine1.setVisibility(View.VISIBLE);
            // iTEP展示样式改为TabLayout
            if (!TextUtils.equals(mClassifyEntity.getConfigValue(), "iTEP")) {
                showTabLayout = false;
            }
        }

        // 必须保证所有的筛选条件 allTab都不是同一个对象
        allTab = Tab.buildAll(all);
        allTab.setSelected(true);
        if((mThirdTabs.size() > 1) && !mThirdTabs.contains(allTab)){
            // triggerFiltrate 第三个不参与触发联动的点击判断
            // 因为分级阅读和特色英语只有两个Tab
            // 添加全部
            mThirdTabs.add(0,allTab);
            mCbSort3.setVisibility(View.VISIBLE);
            mLaySort3.setVisibility(View.VISIBLE);
            mVerticalLine2.setVisibility(View.VISIBLE);
            showTabLayout = false;
        }

        if(showTabLayout){
            // 显示TabLayout
            mTabLayout.setVisibility(View.VISIBLE);
            mFiltrateLayout.setVisibility(View.GONE);
            mRecyclerLayout.setVisibility(View.GONE);
            mViewPager.setVisibility(View.VISIBLE);

            List<Fragment> fragments = new ArrayList<>();
            for (Tab tab:mFirstTabs) {
                fragments.add(CourseFiltratePagerFragment.newInstance(mClassifyEntity,tab.getLabelId(),mSearchKey));
            }
            // 设置TabLayout tabs
            /*for (int index = 0; index < mFirstTabs.size(); index++) {
                Tab tab = mFirstTabs.get(index);
                TabLayout.Tab tabLayoutTab = mTabLayout.newTab().setText(tab.getConfigValue());
                tabLayoutTab.setTag(tab);
                if(index == 0){
                    mTabLayout.addTab(tabLayoutTab,true);
                }else{
                    mTabLayout.addTab(tabLayoutTab,false);
                }
            }*/

            // 给TabLayout Bind ViewPager
            mTabLayout.removeOnTabSelectedListener(tabSelectedListener);
            mViewPager.setAdapter(new CoursePagerAdapter(getSupportFragmentManager(),fragments));
            mTabLayout.setupWithViewPager(mViewPager);
            // 此时确定了TabLayout的数目
            mTabLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mTabLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    int tabCount = mTabLayout.getTabCount();
                    if(!(mTabLayout.getChildCount() > 0)){
                        // 如果TabLayout没有子View
                        mIvSlide.setVisibility(View.GONE);
                        return;
                    }

                    LinearLayout slidingTabStrip = (LinearLayout) mTabLayout.getChildAt(0);
                    if(!(slidingTabStrip.getChildCount() > 0)){
                        // 如果TabLayout 没有子Tab
                        mIvSlide.setVisibility(View.GONE);
                        return;
                    }
                    int tabWidth = slidingTabStrip.getChildAt(0).getMeasuredWidth();
                    int allWidth = tabWidth * tabCount;
                    if(allWidth > mTabLayout.getMeasuredWidth()){
                        // 显示滑动
                        mIvSlide.setVisibility(View.VISIBLE);
                        mIvSlide.setOnClickListener(view->{
                            // 滑动到指定位置并不选择
                            // mTabLayout.getTabAt(mTabLayout.getTabCount() - 1).select();
                            mTabLayout.smoothScrollTo(allWidth,0);
                        });
                    }else{
                        // 不显示滑动
                        mIvSlide.setVisibility(View.GONE);
                        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mTabLayout.getLayoutParams();
                        params.rightMargin = 0;
                        mTabLayout.setLayoutParams(params);
                    }
                }
            });

            // 设置TabLayout tabs
            /*for (int index = 0; index < mFirstTabs.size(); index++) {
                Tab tab = mFirstTabs.get(index);
                TabLayout.Tab tabLayoutTab = mTabLayout.newTab().setText(tab.getConfigValue());
                tabLayoutTab.setTag(tab);
                if(index == 0){
                    mTabLayout.addTab(tabLayoutTab,true);
                }else{
                    mTabLayout.addTab(tabLayoutTab,false);
                }
            }*/
        }else{
            mTabLayout.setVisibility(View.GONE);
            mIvSlide.setVisibility(View.GONE);
            mFiltrateLayout.setVisibility(View.VISIBLE);
            mRecyclerLayout.setVisibility(View.VISIBLE);
            mViewPager.setVisibility(View.GONE);
        }
    }

    /**
     * 设置标题文本
     */
    private void setTopFiltrateText(){
        // 设置文本
        if(mClassifyEntity.getId() == SORT_LEVEL_READ_ID){
            // 分级阅读 语言,科目
            mCbSort1.setText(R.string.language);
            mCbSort2.setText(R.string.course_subject);
        }else if(mClassifyEntity.getId() == CHARACTER_ENGLISH_ID){
            // 特色英语
            // 分级阅读 语言,年龄段
            mCbSort1.setText(R.string.grade);
            mCbSort2.setText(R.string.course_subject);
        }else if(mClassifyEntity.getId() == BOOK_READING_ID){
            // 绘本阅读
            // 分级阅读 语言,科目
            mCbSort1.setText(R.string.language);
            mCbSort2.setText(R.string.label_age_bracket);
            mCbSort3.setText(R.string.label_category);
        }
    }

    /**
     * 当不联动数据的全部Tab被点击时，更新Top 筛选文本
     * @param singleBox 点击的Tab 全部
     */
    private void setSingleTopFiltrateText(@NonNull CheckBox singleBox){
        // 设置文本
        if(mClassifyEntity.getId() == SORT_LEVEL_READ_ID){
            // 分级阅读 语言,科目
            if(singleBox == mCbSort1){
                mCbSort1.setText(R.string.language);
            }

            if(singleBox == mCbSort2){
                mCbSort2.setText(R.string.course_subject);
            }
        }else if(mClassifyEntity.getId() == CHARACTER_ENGLISH_ID){
            // 特色英语
            // 分级阅读 语言,年龄段
            if(singleBox == mCbSort1){
                mCbSort1.setText(R.string.grade);
            }

            if(singleBox == mCbSort2){
                mCbSort2.setText(R.string.course_subject);
            }
        }else if(mClassifyEntity.getId() == BOOK_READING_ID){
            // 绘本阅读
            // 分级阅读 语言,科目
            if(singleBox == mCbSort1){
                mCbSort1.setText(R.string.language);
            }

            if(singleBox == mCbSort2){
                mCbSort2.setText(R.string.label_age_bracket);
            }

            if(singleBox == mCbSort3){
                mCbSort3.setText(R.string.label_category);
            }
        }
    }

    /**
     * 根据筛选条件,查询课程
     */
    public void requestCourseData(boolean isMoreLoaded){
        // 获取筛选条件
        int paramOneId = 0,paramTwoId = 0,paramThreeId = 0;
        for (Tab tab:mFirstTabs) {
            if(tab.isSelected()){
                paramOneId = tab.getLabelId();
            }
        }

        for (Tab tab:mSecondTabs) {
            if(tab.isSelected()){
                paramTwoId = tab.getLabelId();
            }
        }

        for (Tab tab:mThirdTabs) {
            if(tab.isSelected()){
                paramThreeId = tab.getLabelId();
            }
        }

        if(mClassifyEntity.getParentId() == BASIC_COURSE_ID){
            // 基础课程
            if(EmptyUtil.isNotEmpty(mClassifyEntity.getParamTwoId())){
                paramTwoId = mClassifyEntity.getParamTwoId();
            }

            if(EmptyUtil.isNotEmpty(mClassifyEntity.getParamThreeId())){
                paramThreeId = mClassifyEntity.getParamThreeId();
            }
        }


        if(isMoreLoaded){
            currentPage++;
            mPresenter.requestMoreCourseData(currentPage,0,mClassifyEntity.getLevel(),mSearchKey,paramOneId,paramTwoId,paramThreeId);
        }else{
            currentPage = 0;
            mPresenter.requestCourseData(currentPage,0,mClassifyEntity.getLevel(),mSearchKey,paramOneId,paramTwoId,paramThreeId);
        }
    }

    @Override
    public void onCheckedChanged(final CompoundButton buttonView, boolean isChecked) {
        int viewId = buttonView.getId();
        // 设置所有的筛选条件都为false
        // doNotCheckAll();
        List<Tab> mTabs = doCheckButton(viewId);

        if(mPopupWindow != null && mPopupWindow.isShowing()){
            mPopupWindow.dismiss();
            return;
        }

        if(!isChecked) return;

        SortLinePopupWindow mPopupWindow = new SortLinePopupWindow(this, buttonView, mTabs, new SortLinePopupWindow.PopupWindowListener() {
            @Override
            public void onItemClickListener(View buttonView, int position, Tab tab) {
                int viewId = buttonView.getId();
                if(viewId == R.id.cb_sort1){
                    clearTabSelected(mFirstTabs);
                    // 有可能发生联动
                    if(isDataLinkage){
                        mSecondTabs.clear();
                        // 点击了第一个筛选列表,必须重置第二个筛选文本
                        setTopFiltrateText();
                        // 这里情况特殊,先设置选择
                        tab.setSelected(true);
                        triggerLinkage(mAllClassifyEntities);
                        buildAll(true);
                    }

                    if(tab.isAll() && isDataLinkage){
                        setTopFiltrateText();
                    }else{
                        if(tab.isAll()){
                            setSingleTopFiltrateText(mCbSort1);
                        }else{
                            mCbSort1.setText(tab.getConfigValue());
                        }
                    }
                }else if(viewId == R.id.cb_sort2){
                    if(tab.isAll()){
                        // 只是第二个筛选按钮发生文本改变
                        setSingleTopFiltrateText(mCbSort2);
                        // mCbSort2.setText(all);
                        // setTopFiltrateText();
                    }else{
                        mCbSort2.setText(tab.getConfigValue());
                    }
                    clearTabSelected(mSecondTabs);
                }else if(viewId == R.id.cb_sort3){
                    if(tab.isAll()){
                        // 只是第三个筛选按钮发生文本改变
                        setSingleTopFiltrateText(mCbSort3);
                        // mCbSort3.setText(all);
                        // setTopFiltrateText();
                    }else{
                        mCbSort3.setText(tab.getConfigValue());
                    }
                    clearTabSelected(mThirdTabs);
                }
                tab.setSelected(true);
                mRefreshLayout.showRefresh();
                requestCourseData(false);
            }

            @Override
            public void onDismissListener() {
                buttonView.setChecked(false);
            }
        });
        // 弹出筛选框
        mPopupWindow.showPopupWindow(buttonView,true);
    }

    /**
     * 清空Tab所有的选择
     * @param tabs
     */
    private void clearTabSelected(List<Tab> tabs){
        for (Tab tab :tabs) {
            tab.setSelected(false);
        }
    }

    /**
     * 获取Tabs数据源
     */
    private List<Tab> doCheckButton(int viewId){
        // 获取到Tab数据
        if(viewId == R.id.cb_sort1){
            return mFirstTabs;
        }else if(viewId == R.id.cb_sort2){
            return mSecondTabs;
        }else if(viewId == R.id.cb_sort3){
            return mThirdTabs;
        }

        return null;
    }

    /**
     * 分享
     */
    private void share(){
        if (EmptyUtil.isNotEmpty(mClassifyEntity)) {
            final String title = mClassifyEntity.getConfigValue();
            final String description = "";
            final String thumbnailUrl = "";
            String shareUrl = AppConfig.ServerUrl.CourseLabelShareUrl;
            shareUrl = shareUrl.replace("{level}",mClassifyEntity.getLevel());
            shareUrl = shareUrl.replace("{parentId}",Integer.toString(mClassifyEntity.getId()));
            int paramTwoId = 0;
            int paramThreeId = 0;
            if(mClassifyEntity.getParentId() == BASIC_COURSE_ID){
                if(EmptyUtil.isNotEmpty(mClassifyEntity.getParamTwoId())){
                    // 基础课程
                    paramTwoId = mClassifyEntity.getParamTwoId();
                }

                if(EmptyUtil.isNotEmpty(mClassifyEntity.getParamTwoId())){
                    // 基础课程
                    paramThreeId = mClassifyEntity.getParamThreeId();
                }
            }

            if(mClassifyEntity.getParentId() == CHARACTERISTICS_ENGLISH_ID){
                if(EmptyUtil.isNotEmpty(mClassifyEntity.getParamTwoId())){
                    // 特色英语课程
                    paramTwoId = mClassifyEntity.getParamTwoId();
                }
            }



            shareUrl = shareUrl.replace("{paramTwoId}", Integer.toString(paramTwoId));
            shareUrl = shareUrl.replace("{paramThreeId}", Integer.toString(paramThreeId));
            mPresenter.share(title, description, thumbnailUrl, shareUrl);
        }
    }

    /**
     * 筛选页面的入口
     *
     * @param context  上下文对象
     * @param entity   点击的单个实体
     * @param state 带入状态
     */
    public static void show(@NonNull Context context,
                            @NonNull LQCourseConfigEntity entity,
                            @NonNull GroupFiltrateState state) {
        Intent intent = new Intent(context, CourseFiltrateActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_EXTRA_CLASSIFY_ENTITY, entity);
        bundle.putBoolean(KEY_EXTRA_HIDE_TOP, false);
        bundle.putSerializable(KEY_EXTRA_STATE,state);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }


    /**
     * 课程列表的入口
     *
     * @param context  上下文对象
     * @param entity   单个实体
     * @param state 带入状态
     * @param keyString 搜索关键词
     */
    public static void show(@NonNull Context context,
                            @NonNull LQCourseConfigEntity entity,
                            @NonNull GroupFiltrateState state,
                            @NonNull String keyString) {
        Intent intent = new Intent(context, CourseFiltrateActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_EXTRA_CLASSIFY_ENTITY, entity);
        bundle.putBoolean(KEY_EXTRA_HIDE_TOP, true);
        bundle.putSerializable(KEY_EXTRA_STATE,state);
        bundle.putString(KEY_EXTRA_SEARCH_KEY,keyString);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    private class CoursePagerAdapter extends FragmentPagerAdapter{

        private List<Fragment> mFragments;

        public CoursePagerAdapter(FragmentManager fm,List<Fragment> fragments) {
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

        @Override
        public CharSequence getPageTitle(int position) {
            return mFirstTabs.get(position).getConfigValue();
        }
    }
}
