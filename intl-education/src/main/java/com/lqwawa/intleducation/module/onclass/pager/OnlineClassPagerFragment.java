package com.lqwawa.intleducation.module.onclass.pager;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.CourseEmptyView;
import com.lqwawa.intleducation.base.PresenterFragment;
import com.lqwawa.intleducation.base.widgets.PullRefreshView.PullToRefreshView;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerSpaceItemDecoration;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.factory.data.entity.JoinClassEntity;
import com.lqwawa.intleducation.factory.data.entity.OnlineClassEntity;
import com.lqwawa.intleducation.factory.event.EventConstant;
import com.lqwawa.intleducation.factory.event.EventWrapper;
import com.lqwawa.intleducation.module.onclass.OnlineClassAdapter;
import com.lqwawa.intleducation.module.onclass.OnlineClassRole;
import com.lqwawa.intleducation.module.onclass.OnlineSortType;
import com.lqwawa.intleducation.module.onclass.SearchNavigator;
import com.lqwawa.intleducation.module.onclass.detail.join.JoinClassDetailActivity;
import com.lqwawa.intleducation.module.onclass.detail.notjoin.ClassDetailActivity;
import com.lqwawa.intleducation.module.onclass.detail.notjoin.ClassInfoParams;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Date;
import java.util.List;

/**
 * @author medici
 * @desc 在线课堂班级列表的Fragment
 */
public class OnlineClassPagerFragment extends PresenterFragment<OnlineClassPagerContract.Presenter>
        implements OnlineClassPagerContract.View,SearchNavigator {

    private static final String KEY_EXTRA_SCHOOL_ID = "KEY_EXTRA_SCHOOL_ID";
    private static final String KEY_EXTRA_IS_SCHOOL_ENTER = "KEY_EXTRA_IS_SCHOOL_ENTER";
    private static final String KEY_EXTRA_SORT = "KEY_EXTRA_SORT";

    // 下拉刷新布局
    private PullToRefreshView mRefreshLayout;
    private CourseEmptyView mEmptyLayout;
    // 班级列表布局
    private RecyclerView mRecycler;
    // 班级Adapter
    private OnlineClassAdapter mClassAdapter;

    // 学校机构Id
    private String mSchoolId;
    // 标志是否从机构主页进入
    private boolean isSchoolEnter;
    // 筛选规则
    private String mSort;
    // 页码
    private int pageIndex;

    // 当前点击的在线课堂班级
    private OnlineClassEntity mCurrentClickEntity;



    /**
     * 在线课堂班级Fragment的入口
     * @param schoolId 机构Id
     * @param isSchoolEnter 是否是从机构进入
     * @param sort 筛选规则
     * @return OnlineClassPagerFragment
     */
    public static OnlineClassPagerFragment newInstance(@NonNull String schoolId,
                                                       boolean isSchoolEnter,
                                                       @NonNull @OnlineSortType.OnlineSortRes String sort){
        OnlineClassPagerFragment fragment = new OnlineClassPagerFragment();
        Bundle arguments = new Bundle();
        arguments.putString(KEY_EXTRA_SCHOOL_ID,schoolId);
        arguments.putBoolean(KEY_EXTRA_IS_SCHOOL_ENTER,isSchoolEnter);
        arguments.putString(KEY_EXTRA_SORT,sort);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    protected OnlineClassPagerContract.Presenter initPresenter() {
        return new OnlineClassPagerPresenter(this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_online_class_pager;
    }

    @Override
    protected boolean initArgs(@NonNull Bundle bundle) {
        mSchoolId = bundle.getString(KEY_EXTRA_SCHOOL_ID,null);
        isSchoolEnter = bundle.getBoolean(KEY_EXTRA_IS_SCHOOL_ENTER);
        mSort = bundle.getString(KEY_EXTRA_SORT);
        if(EmptyUtil.isEmpty(mSchoolId) || EmptyUtil.isEmpty(mSort)){
            return false;
        }
        return super.initArgs(bundle);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mRefreshLayout = (PullToRefreshView) mRootView.findViewById(R.id.refresh_layout);
        mEmptyLayout = (CourseEmptyView) mRootView.findViewById(R.id.empty_layout);
        mRecycler = (RecyclerView) mRootView.findViewById(R.id.recycler);
        mRecycler.setNestedScrollingEnabled(false);
        GridLayoutManager mLayoutManager = new GridLayoutManager(getContext(),2){
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
        requestClassData(false);
    }



    /**
     * 加载班级数据
     * @param isMoreData 是否是加载更多
     */
    private void requestClassData(boolean isMoreData){
        EditText searchEt = (EditText) getActivity().findViewById(R.id.et_search);
        String searchKey = searchEt.getText().toString().trim();
        if(isMoreData){
            ++ pageIndex;
        }else{
            pageIndex = 0;
            mRefreshLayout.showRefresh();
        }

        if(EmptyUtil.isEmpty(searchKey)){
            mPresenter.requestOnlineClassData(mSchoolId,pageIndex,mSort);
        }else{
            mPresenter.onSearch(mSchoolId,searchKey,pageIndex,mSort);
        }
    }

    @Override
    public void updateClassListView(@NonNull List<OnlineClassEntity> entities) {
        mClassAdapter.replace(entities);
        mRefreshLayout.onHeaderRefreshComplete();
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
    public void updateClassMoreListView(@NonNull List<OnlineClassEntity> entities) {
        mClassAdapter.add(entities);
        mRefreshLayout.onFooterRefreshComplete();
        mRefreshLayout.setLoadMoreEnable(EmptyUtil.isNotEmpty(entities) && entities.size() >= AppConfig.PAGE_SIZE);

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
     * 点击在线课堂班级
     * @param onlineClassEntity 班级实体
     */
    private void onClickClass(OnlineClassEntity onlineClassEntity) {
        mCurrentClickEntity = onlineClassEntity;
        String classId = onlineClassEntity.getClassId();
        // mPresenter.requestLoadClassInfo(classId);
        ClassInfoParams params = new ClassInfoParams(onlineClassEntity,isSchoolEnter,false);
        ClassDetailActivity.show(getActivity(),params);
    }

    @Override
    public void onClassCheckSucceed(JoinClassEntity entity) {
        // 非空判断
        if(EmptyUtil.isEmpty(entity) || EmptyUtil.isEmpty(mCurrentClickEntity)){
            return;
        }

        boolean needToJoin = entity.isIsInClass();
        String roles = entity.getRoles();
        String role = UserHelper.getOnlineRoleWithUserRoles(roles);
        if(needToJoin || OnlineClassRole.ROLE_TEACHER.equals(role)){
            // 已经加入班级 或者是老师身份
            JoinClassDetailActivity.show(getActivity(),entity.getClassId(),entity.getSchoolId(),mCurrentClickEntity.getId(),role,isSchoolEnter);
        }else{
            // 未加入班级
            ClassDetailActivity.show(getActivity(),entity.getClassId(),entity.getSchoolId(),mCurrentClickEntity.getId(),role,isSchoolEnter);
        }
    }



    @Override
    public void showError(int str) {
        super.showError(str);
        mRefreshLayout.onHeaderRefreshComplete();
        mRefreshLayout.onFooterRefreshComplete();
    }


    @Override
    public boolean search(@NonNull String searchKey) {
        if(getUserVisibleHint()){
            requestClassData(false);
            return true;
        }
        return false;
    }

    @Override
    public boolean triggerPriceSwitch(@NonNull boolean up) {
        if(isVisible() &&
                (mSort == OnlineSortType.TYPE_SORT_ONLINE_CLASS_PRICE_UP ||
                mSort == OnlineSortType.TYPE_SORT_ONLINE_CLASS_PRICE_DOWN)){
            mSort = up ? OnlineSortType.TYPE_SORT_ONLINE_CLASS_PRICE_UP : OnlineSortType.TYPE_SORT_ONLINE_CLASS_PRICE_DOWN;
            requestClassData(false);
            return true;
        }
        return false;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(@NonNull EventWrapper event){
        if(EventWrapper.isMatch(event, EventConstant.ONLINE_CLASS_COMPLETE_GIVE_EVENT)){
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
}
