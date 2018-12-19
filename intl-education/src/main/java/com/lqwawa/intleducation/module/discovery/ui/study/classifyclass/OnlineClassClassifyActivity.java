package com.lqwawa.intleducation.module.discovery.ui.study.classifyclass;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.CourseEmptyView;
import com.lqwawa.intleducation.base.PresenterActivity;
import com.lqwawa.intleducation.base.ToolbarActivity;
import com.lqwawa.intleducation.base.widgets.PriceArrowView;
import com.lqwawa.intleducation.base.widgets.PullRefreshView.PullToRefreshView;
import com.lqwawa.intleducation.base.widgets.adapter.TabSelectedAdapter;
import com.lqwawa.intleducation.base.widgets.adapter.TextWatcherAdapter;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerSpaceItemDecoration;
import com.lqwawa.intleducation.common.Common;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.KeyboardUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.entity.JoinClassEntity;
import com.lqwawa.intleducation.factory.data.entity.OnlineClassEntity;
import com.lqwawa.intleducation.factory.event.EventConstant;
import com.lqwawa.intleducation.factory.event.EventWrapper;
import com.lqwawa.intleducation.module.discovery.ui.study.OnlineStudyType;
import com.lqwawa.intleducation.module.discovery.ui.study.filtratelist.pager.OnlineStudyFiltratePagerContract;
import com.lqwawa.intleducation.module.onclass.OnlineClassAdapter;
import com.lqwawa.intleducation.module.onclass.OnlineClassRole;
import com.lqwawa.intleducation.module.onclass.OnlineSortType;
import com.lqwawa.intleducation.module.onclass.SearchNavigator;
import com.lqwawa.intleducation.module.onclass.detail.join.JoinClassDetailActivity;
import com.lqwawa.intleducation.module.onclass.detail.notjoin.ClassDetailActivity;
import com.lqwawa.intleducation.module.onclass.detail.notjoin.ClassInfoParams;
import com.lqwawa.intleducation.module.onclass.pager.OnlineClassPagerFragment;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @desc 在线学习在线班级分类列表的页面
 * @author MrMedici
 */
public class OnlineClassClassifyActivity extends PresenterActivity<OnlineClassClassifyContract.Presenter>
        implements OnlineClassClassifyContract.View, View.OnClickListener{

    private static final String KEY_EXTRA_SORT = "KEY_EXTRA_SORT";

    // 标题
    private TextView mToolbarTitle;

    // 搜索
    private EditText mSearchContent;
    private ImageView mSearchClear;
    private TextView mSearchFilter;

    // 下拉刷新布局
    private PullToRefreshView mRefreshLayout;
    private CourseEmptyView mEmptyLayout;
    // 班级列表布局
    private RecyclerView mRecycler;
    // 班级Adapter
    private OnlineClassAdapter mClassAdapter;

    private int mSort;
    // 页码
    private int pageIndex;
    // 当前点击的班级
    private OnlineClassEntity mCurrentClickEntity;


    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_online_class_classify;
    }

    @Override
    protected OnlineClassClassifyContract.Presenter initPresenter() {
        return new OnlineClassClassifyPresenter(this);
    }

    @Override
    protected boolean initArgs(@NonNull Bundle bundle) {
        mSort = bundle.getInt(KEY_EXTRA_SORT);
        return super.initArgs(bundle);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mToolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        mSearchContent = (EditText) findViewById(R.id.et_search);
        mSearchClear = (ImageView) findViewById(R.id.iv_search_clear);
        mSearchFilter = (TextView) findViewById(R.id.tv_filter);

        mSearchFilter.setVisibility(View.VISIBLE);
        mSearchClear.setOnClickListener(this);
        mSearchFilter.setOnClickListener(this);

        // @date   :2018/6/13 0013 上午 11:31
        // @func   :V5.7改用键盘搜索的方式
        mSearchContent.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                super.onTextChanged(s, start, before, count);
                if (s.length() > 0) {
                    mSearchClear.setVisibility(View.VISIBLE);
                } else {
                    mSearchClear.setVisibility(View.INVISIBLE);
                }
                mSearchContent.setImeOptions(s.length() > 0
                        ? EditorInfo.IME_ACTION_SEARCH
                        : EditorInfo.IME_ACTION_DONE);
            }
        });

        mSearchContent.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH){
                    // 搜索，收起软件盘
                    KeyboardUtil.hideSoftInput(OnlineClassClassifyActivity.this);
                    requestClassData(false);
                }
                return true;
            }
        });


        mRefreshLayout = (PullToRefreshView) findViewById(R.id.refresh_layout);
        mEmptyLayout = (CourseEmptyView) findViewById(R.id.empty_layout);
        mRecycler = (RecyclerView) findViewById(R.id.recycler);
        mRecycler.setNestedScrollingEnabled(false);
        GridLayoutManager mLayoutManager = new GridLayoutManager(this,2){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };

        mRecycler.setLayoutManager(mLayoutManager);
        mRecycler.addItemDecoration(new RecyclerSpaceItemDecoration(2,8,false));
        mClassAdapter = new OnlineClassAdapter();
        mRecycler.setAdapter(mClassAdapter);

        mClassAdapter.setListener(new RecyclerAdapter.AdapterListenerImpl<OnlineClassEntity>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, OnlineClassEntity onlineClassEntity) {
                super.onItemClick(holder, onlineClassEntity);
                onClickClass(onlineClassEntity);
            }
        });

        mRefreshLayout.setLastUpdated(new Date().toLocaleString());
        mRefreshLayout.setOnHeaderRefreshListener(new PullToRefreshView.OnHeaderRefreshListener() {
            @Override
            public void onHeaderRefresh(PullToRefreshView view) {
                requestClassData(false);
            }
        });

        mRefreshLayout.setOnFooterRefreshListener(new PullToRefreshView.OnFooterRefreshListener() {
            @Override
            public void onFooterRefresh(PullToRefreshView view) {
                requestClassData(true);
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }

        if(mSort == OnlineStudyType.SORT_LATEST){
            // 最近更新
            mToolbarTitle.setText(R.string.label_online_study_latest_data);
        }else if(mSort == OnlineStudyType.SORT_HOT){
            // 热门课堂
            mToolbarTitle.setText(R.string.label_online_study_hot_data);
        }
        requestClassData(false);
    }

    /**
     * 加载班级数据
     * @param isMoreData 是否是加载更多
     */
    private void requestClassData(boolean isMoreData){
        EditText searchEt = (EditText) findViewById(R.id.et_search);
        String searchKey = searchEt.getText().toString().trim();
        if(isMoreData){
            ++ pageIndex;
        }else{
            pageIndex = 0;
        }

        mPresenter.requestOnlineStudyDataFromLabel(pageIndex,searchKey,mSort,0,0,0,0);
    }

    @Override
    public void updateOnlineStudyClassDataView(@NonNull List<OnlineClassEntity> entities) {
        mRefreshLayout.onHeaderRefreshComplete();
        mRefreshLayout.onFooterRefreshComplete();
        mClassAdapter.replace(entities);
        mRefreshLayout.setLoadMoreEnable(EmptyUtil.isNotEmpty(entities) && entities.size() >= AppConfig.PAGE_SIZE);

        if(EmptyUtil.isEmpty(entities)){
            // 数据为空
            mRefreshLayout.setVisibility(View.GONE);
            mEmptyLayout.setVisibility(View.VISIBLE);
        }else{
            // 数据不为空
            mRefreshLayout.setVisibility(View.VISIBLE);
            mEmptyLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void updateOnlineStudyMoreClassDataView(@NonNull List<OnlineClassEntity> entities) {
        mRefreshLayout.onHeaderRefreshComplete();
        mRefreshLayout.onFooterRefreshComplete();
        mRefreshLayout.setLoadMoreEnable(EmptyUtil.isNotEmpty(entities) && entities.size() >= AppConfig.PAGE_SIZE);
        mClassAdapter.getItems().addAll(entities);

        List<OnlineClassEntity> items = mClassAdapter.getItems();
        if(items.size() > Common.Constance.LQMOOC_COURSE_MAX_COUNT){
            // 数据达到热门推荐，最近更新的顶峰
            items = new ArrayList<>(items.subList(0,Common.Constance.LQMOOC_COURSE_MAX_COUNT));
            mClassAdapter.replace(items);
            mClassAdapter.notifyDataSetChanged();
            mRefreshLayout.setLoadMoreEnable(false);
        }

        if(EmptyUtil.isEmpty(mClassAdapter.getItems())){
            // 数据为空
            mRefreshLayout.setVisibility(View.GONE);
            mEmptyLayout.setVisibility(View.VISIBLE);
        }else{
            // 数据不为空
            mRefreshLayout.setVisibility(View.VISIBLE);
            mEmptyLayout.setVisibility(View.GONE);
        }

    }

    /**
     * 点击在线班级
     * @param entity 点击在线课堂班级的实体
     */
    private void onClickClass(@NonNull OnlineClassEntity entity){
        mCurrentClickEntity = entity;
        // mPresenter.requestLoadClassInfo(entity.getClassId());
        ClassInfoParams params = new ClassInfoParams(entity);
        ClassDetailActivity.show(this,params);
    }

    @Override
    public void onClassCheckSucceed(@NonNull JoinClassEntity entity) {
        // 关闭Dialog
        hideLoading();
        // 非空判断
        if(EmptyUtil.isEmpty(entity) || EmptyUtil.isEmpty(mCurrentClickEntity)){
            return;
        }

        boolean needToJoin = entity.isIsInClass();
        String roles = entity.getRoles();
        String role = UserHelper.getOnlineRoleWithUserRoles(roles);
        if(needToJoin || OnlineClassRole.ROLE_TEACHER.equals(role)){
            // 已经加入班级 或者是老师身份
            JoinClassDetailActivity.show(this,entity.getClassId(),entity.getSchoolId(),mCurrentClickEntity.getId(),role,false);
        }else{
            // 未加入班级
            ClassDetailActivity.show(this,entity.getClassId(),entity.getSchoolId(),mCurrentClickEntity.getId(),role,false);
        }
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if(viewId == R.id.tv_filter){
            // 搜索 兼容其它平板问题，收起软件盘
            KeyboardUtil.hideSoftInput(OnlineClassClassifyActivity.this);
            requestClassData(false);
        }else if(viewId == R.id.iv_search_clear){
            // 删除关键字
            mSearchContent.getText().clear();
        }else if(viewId == R.id.et_search){
            // 点击搜索框
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(@NonNull EventWrapper event){
        if(EventWrapper.isMatch(event, EventConstant.ONLINE_CLASS_COMPLETE_GIVE_EVENT)){
            // 刷新UI
            requestClassData(false);
        }else if(EventWrapper.isMatch(event, EventConstant.ONLINE_CLASS_COMPLETE_GIVE_EVENT)){
            // 刷新UI
            requestClassData(false);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }
    }

    /**
     * 在线学习分类列表入口
     * @param context 上下文对象
     */
    public static void show(@NonNull Context context, @OnlineStudyType.OnlineStudyRes int sort){
        Intent intent = new Intent(context, OnlineClassClassifyActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_EXTRA_SORT,sort);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}
