package com.lqwawa.intleducation.module.tutorial.course.filtrate.pager;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.CourseEmptyView;
import com.lqwawa.intleducation.base.PresenterFragment;
import com.lqwawa.intleducation.base.widgets.PullRefreshView.PullToRefreshView;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerItemDecoration;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerSpaceItemDecoration;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.entity.course.TutorialGroupEntity;
import com.lqwawa.intleducation.module.discovery.tool.LoginHelper;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.filtrate.HideSortType;
import com.lqwawa.intleducation.module.learn.tool.TaskSliderHelper;
import com.lqwawa.intleducation.module.tutorial.course.TutorialGroupAdapter;
import com.lqwawa.intleducation.module.user.tool.UserHelper;
import com.oosic.apps.iemaker.base.exercisenode.ExerciseNode;

import java.util.Date;
import java.util.List;

/**
 * @author mrmedici
 * @desc 帮辅筛选片段页面
 */
public class TutorialFiltrateGroupPagerFragment extends PresenterFragment<TutorialFiltrateGroupPagerContract.Presenter>
    implements TutorialFiltrateGroupPagerContract.View,PagerNavigator{

    private static final String KEY_EXTRA_SORT = "KEY_EXTRA_SORT";
    private static final String KEY_EXTRA_MEMBER_ID = "KEY_EXTRA_MEMBER_ID";

    private PullToRefreshView mRefreshLayout;
    private RecyclerView mRecycler;
    private CourseEmptyView mEmptyView;
    private TutorialGroupAdapter mGroupAdapter;
    // 未登录可能为空
    private String mCurMemberId;
    private String mSort;

    private ActivityNavigator mNavigator;
    private int pageIndex;

    public static TutorialFiltrateGroupPagerFragment newInstance(@Nullable String memberId,
                                                                 @HideSortType.SortRes String sort,
                                                                 @NonNull ActivityNavigator navigator){
        TutorialFiltrateGroupPagerFragment fragment = new TutorialFiltrateGroupPagerFragment();
        Bundle arguments = new Bundle();
        arguments.putString(KEY_EXTRA_MEMBER_ID,memberId);
        arguments.putString(KEY_EXTRA_SORT,sort);
        fragment.setArguments(arguments);
        fragment.setNavigator(navigator);
        return fragment;
    }

    private void setNavigator(@NonNull ActivityNavigator navigator){
        this.mNavigator = navigator;
    }

    @Override
    protected TutorialFiltrateGroupPagerContract.Presenter initPresenter() {
        return new TutorialFiltrateGroupPagerPresenter(this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_tutorial_filtrate_group_pager;
    }

    @Override
    protected boolean initArgs(Bundle bundle) {
        mCurMemberId = bundle.getString(KEY_EXTRA_MEMBER_ID);
        mSort = bundle.getString(KEY_EXTRA_SORT);
        if(EmptyUtil.isEmpty(mSort)) return false;
        return super.initArgs(bundle);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mRefreshLayout = (PullToRefreshView) mRootView.findViewById(R.id.refresh_layout);
        mEmptyView = (CourseEmptyView) mRootView.findViewById(R.id.empty_layout);
        mRecycler = (RecyclerView) mRootView.findViewById(R.id.recycler);
        mRecycler.setNestedScrollingEnabled(false);
        GridLayoutManager mLayoutManager = new GridLayoutManager(getActivity(),2){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        mRecycler.setLayoutManager(mLayoutManager);
        mRecycler.addItemDecoration(new RecyclerSpaceItemDecoration(2,8));
        mGroupAdapter = new TutorialGroupAdapter(null);
        mRecycler.setAdapter(mGroupAdapter);


        mGroupAdapter.setCallback(new TutorialGroupAdapter.EntityCallback() {
            @Override
            public void onAddTutorial(int position, @NonNull TutorialGroupEntity entity) {
                // 先判断是否登录
                if(!UserHelper.isLogin()){
                    LoginHelper.enterLogin(getActivity());
                    return;
                }

                // 添加帮辅
                String memberId = mCurMemberId;
                mPresenter.requestAddTutorByStudentId(memberId,entity.getCreateId(),entity.getCreateName());
            }
        });

        mGroupAdapter.setListener(new RecyclerAdapter.AdapterListenerImpl<TutorialGroupEntity>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, TutorialGroupEntity entity) {
                super.onItemClick(holder, entity);
                if(EmptyUtil.isNotEmpty(TaskSliderHelper.onTutorialMarkingListener)){
                    TaskSliderHelper.onTutorialMarkingListener.enterTutorialHomePager(getActivity(),entity.getCreateId(),entity.getCreateName());
                }
            }
        });

        // 下拉刷新
        mRefreshLayout.setLastUpdated(new Date().toLocaleString());
        mRefreshLayout.showRefresh();
        mRefreshLayout.setOnHeaderRefreshListener(new PullToRefreshView.OnHeaderRefreshListener() {
            @Override
            public void onHeaderRefresh(PullToRefreshView view) {
                requestTutorialData(false);
            }
        });

        mRefreshLayout.setLoadMoreEnable(false);
        mRefreshLayout.setOnFooterRefreshListener(new PullToRefreshView.OnFooterRefreshListener() {
            @Override
            public void onFooterRefresh(PullToRefreshView view) {
                requestTutorialData(true);
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        requestTutorialData(false);
    }

    @Override
    public void reloadData() {
        super.reloadData();
        requestTutorialData(false);
    }

    /**
     * 请求帮辅群数据
     */
    private void requestTutorialData(boolean moreData){
        if(!moreData){
            pageIndex = 0;
        }else{
            pageIndex ++;
        }

        if(EmptyUtil.isNotEmpty(mNavigator)){
            String level = mNavigator.getLevel();
            int[] params = mNavigator.getFiltrateParams();
            int paramOneId = params[0];
            int paramTwoId = params[1];
            int paramThereId = params[2];
            mPresenter.requestTutorDataByParams(level,paramOneId,paramTwoId,paramThereId,mSort,pageIndex);
        }


    }

    @Override
    public void updateTutorView(List<TutorialGroupEntity> entities) {
        mRefreshLayout.onHeaderRefreshComplete();
        mRefreshLayout.onFooterRefreshComplete();
        handleEntities(entities);
        mGroupAdapter.replace(entities);
        if(EmptyUtil.isEmpty(entities)){
            mEmptyView.setVisibility(View.VISIBLE);
            mRecycler.setVisibility(View.GONE);
        }else{
            mEmptyView.setVisibility(View.GONE);
            mRecycler.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void updateMoreTutorView(List<TutorialGroupEntity> entities) {
        mRefreshLayout.onHeaderRefreshComplete();
        mRefreshLayout.onFooterRefreshComplete();
        handleEntities(entities);
        mGroupAdapter.add(entities);
        mRefreshLayout.setLoadMoreEnable(EmptyUtil.isNotEmpty(entities) && entities.size() >= AppConfig.PAGE_SIZE);
    }

    private void handleEntities(@NonNull List<TutorialGroupEntity> entities){
        if(EmptyUtil.isNotEmpty(entities)){
            for (TutorialGroupEntity entity : entities) {
                entity.buildEntity();
            }
        }
    }

    @Override
    public void updateAddTutorByStudentIdView(boolean result) {
        if(result){
            UIUtil.showToastSafe(R.string.label_added_tutorial_succeed);
            // 刷新UI
            requestTutorialData(false);
        }else{
            UIUtil.showToastSafe(R.string.label_added_tutorial_failed);
        }
    }
}
