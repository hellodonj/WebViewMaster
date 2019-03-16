package com.lqwawa.intleducation.module.box.common;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.CourseEmptyView;
import com.lqwawa.intleducation.base.PresenterFragment;
import com.lqwawa.intleducation.base.utils.DateUtils;
import com.lqwawa.intleducation.base.widgets.PullRefreshView.PullToRefreshView;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerItemDecoration;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.entity.tutorial.TaskEntity;
import com.lqwawa.intleducation.module.learn.tool.TaskSliderHelper;
import com.lqwawa.intleducation.module.tutorial.marking.list.MarkingStateType;
import com.lqwawa.intleducation.module.tutorial.marking.list.OrderByType;
import com.lqwawa.intleducation.module.tutorial.marking.list.pager.TutorialTaskAdapter;
import com.lqwawa.intleducation.module.tutorial.marking.require.TaskRequirementActivity;

import java.util.Date;
import java.util.List;

/**
 * @author mrmedici
 * @desc 最新作业页面
 */
public class CommonMarkingListFragment extends PresenterFragment<CommonMarkingListContract.Presenter>
    implements CommonMarkingListContract.View{

    private PullToRefreshView mRefreshLayout;
    private RecyclerView mRecycler;
    private CourseEmptyView mEmptyView;
    // Adapter
    private TutorialTaskAdapter mTutorialAdapter;

    private int pageIndex;

    private CommonMarkingParams mMarkingParams;
    private boolean mTutorialMode;
    private String mCurMemberId;

    public static Fragment newInstance(@NonNull CommonMarkingParams params){
        Fragment fragment = new CommonMarkingListFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(FRAGMENT_BUNDLE_OBJECT,params);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected CommonMarkingListContract.Presenter initPresenter() {
        return new CommonMarkingListPresenter(this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_common_marking_list;
    }

    @Override
    protected boolean initArgs(Bundle bundle) {
        mMarkingParams = (CommonMarkingParams) bundle.getSerializable(FRAGMENT_BUNDLE_OBJECT);
        if(EmptyUtil.isNotEmpty(mMarkingParams)){
            mTutorialMode = mMarkingParams.isTutorialMode();
            mCurMemberId = mMarkingParams.getCurMemberId();
        }
        return super.initArgs(bundle);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mRefreshLayout = (PullToRefreshView) mRootView.findViewById(R.id.refresh_layout);
        mEmptyView = (CourseEmptyView) mRootView.findViewById(R.id.empty_layout);

        mRecycler = (RecyclerView) mRootView.findViewById(R.id.recycler);
        mRecycler.setNestedScrollingEnabled(false);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity()){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        mRecycler.setLayoutManager(mLayoutManager);
        mTutorialAdapter = new TutorialTaskAdapter(mTutorialMode);
        mRecycler.setAdapter(mTutorialAdapter);
        mRecycler.addItemDecoration(new RecyclerItemDecoration(getActivity(),RecyclerItemDecoration.VERTICAL_LIST));

        mTutorialAdapter.setCallback(new TutorialTaskAdapter.EntityCallback() {
            @Override
            public void onRequireClick(View it, int position, @NonNull TaskEntity entity) {
                if(mTutorialMode) {
                    TaskRequirementActivity.show(getActivity(),entity);
                }else{
                    // 进入课件详情
                }
            }

            @Override
            public void onEntityClick(View it, int position, @NonNull TaskEntity entity, int state) {
                if(EmptyUtil.isNotEmpty(TaskSliderHelper.onTaskSliderListener)){
                    TaskSliderHelper.onTutorialMarkingListener.openAssistanceMark(getActivity(),entity);
                }
            }

            @Override
            public void onCheckMark(View it, int position, @NonNull TaskEntity entity, int state) {
                if(EmptyUtil.isNotEmpty(TaskSliderHelper.onTaskSliderListener)){
                    TaskSliderHelper.onTutorialMarkingListener.openAssistanceMark(getActivity(),entity);
                }
            }
        });

        mTutorialAdapter.setListener(new RecyclerAdapter.AdapterListenerImpl<TaskEntity>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, TaskEntity entity) {
                super.onItemClick(holder, entity);
                if(EmptyUtil.isNotEmpty(TaskSliderHelper.onTaskSliderListener)){
                    TaskSliderHelper.onTutorialMarkingListener.openAssistanceMark(getActivity(),entity);
                }
            }
        });

        // 下拉刷新
        mRefreshLayout.setLastUpdated(new Date().toLocaleString());
        mRefreshLayout.showRefresh();
        mRefreshLayout.setLoadMoreEnable(false);
        mRefreshLayout.setOnHeaderRefreshListener(new PullToRefreshView.OnHeaderRefreshListener() {
            @Override
            public void onHeaderRefresh(PullToRefreshView view) {
                loadStudyTask(false);
            }
        });

    }

    @Override
    protected void initData() {
        super.initData();
        loadStudyTask();
    }

    /**
     * 加载作业数据
     */
    private void loadStudyTask(){
        loadStudyTask(false);
    }

    /**
     * 加载作业数据
     * @param moreData 是否加载更多
     */
    private void loadStudyTask(boolean moreData){
        if(moreData){
            pageIndex ++;
        }else{
            pageIndex = 0;
        }

        // 未批阅页面
        // 最多显示五条未批阅的记录
        if(mTutorialMode) {
            mPresenter.requestWorkDataWithIdentityId("", mCurMemberId, "", "", "", "", "", "", MarkingStateType.MARKING_STATE_NOT, OrderByType.MARKING_ASC_TIME_DESC, pageIndex,5);
        }else{
            mPresenter.requestWorkDataWithIdentityId(mCurMemberId,"","", "", "", "", "", "", MarkingStateType.MARKING_STATE_NOT, OrderByType.MARKING_ASC_TIME_DESC, pageIndex,5);
        }
    }

    @Override
    public void updateWorkDataWithIdentityIdView(List<TaskEntity> entities) {
        // 判断有无更多数据,打开或者关闭加载更多
        mRefreshLayout.onHeaderRefreshComplete();
        mTutorialAdapter.replace(entities);

        if(EmptyUtil.isEmpty(entities)){
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
    public void showError(int str) {
        super.showError(str);
        mRefreshLayout.onHeaderRefreshComplete();
    }
}
