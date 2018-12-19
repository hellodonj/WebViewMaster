package com.lqwawa.intleducation.module.discovery.ui.study.filtrate.pager;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;

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
import com.lqwawa.intleducation.module.discovery.ui.study.filtrate.ActivityNavigator;
import com.lqwawa.intleducation.module.discovery.ui.study.filtrate.NewOnlineStudyFiltrateContract;
import com.lqwawa.intleducation.module.discovery.ui.study.filtrate.NewOnlineStudyFiltratePresenter;
import com.lqwawa.intleducation.module.discovery.ui.study.filtrate.PagerNavigator;
import com.lqwawa.intleducation.module.onclass.OnlineClassAdapter;
import com.lqwawa.intleducation.module.onclass.OnlineClassRole;
import com.lqwawa.intleducation.module.onclass.OnlineSortType;
import com.lqwawa.intleducation.module.onclass.detail.join.JoinClassDetailActivity;
import com.lqwawa.intleducation.module.onclass.detail.notjoin.ClassDetailActivity;
import com.lqwawa.intleducation.module.onclass.detail.notjoin.ClassInfoParams;
import com.lqwawa.intleducation.module.onclass.pager.OnlineClassPagerContract;
import com.lqwawa.intleducation.module.onclass.pager.OnlineClassPagerFragment;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Date;
import java.util.List;

/**
 * @author mrmedici
 * @desc 新的讲授课堂类型筛选的Pager页面
 */
public class NewOnlineStudyFiltratePagerFragment extends PresenterFragment<NewOnlineStudyFiltratePagerContract.Presenter>
    implements NewOnlineStudyFiltratePagerContract.View,PagerNavigator {

    // 下拉刷新布局
    private PullToRefreshView mRefreshLayout;
    private CourseEmptyView mEmptyLayout;
    // 班级列表布局
    private RecyclerView mRecycler;
    // 班级Adapter
    private OnlineClassAdapter mClassAdapter;

    private ActivityNavigator mNavigator;
    private PagerParams mPagerParams;
    private String mSort;
    private String mKeyWord;

    private int pageIndex;

    /**
     * 将授课堂类型筛选班级Fragment的入口
     * @param params 核心参数
     * @return OnlineClassPagerFragment
     */
    public static NewOnlineStudyFiltratePagerFragment newInstance(@NonNull PagerParams params,@NonNull ActivityNavigator navigator){
        NewOnlineStudyFiltratePagerFragment fragment = new NewOnlineStudyFiltratePagerFragment();
        fragment.setActivityNavigator(navigator);
        Bundle arguments = new Bundle();
        arguments.putSerializable(FRAGMENT_BUNDLE_OBJECT,params);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    protected NewOnlineStudyFiltratePagerContract.Presenter initPresenter() {
        return new NewOnlineStudyFiltratePagerPresenter(this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_new_online_study_filtrate_pager;
    }

    @Override
    protected boolean initArgs(Bundle bundle) {
        if(bundle.containsKey(FRAGMENT_BUNDLE_OBJECT)){
            mPagerParams = (PagerParams) bundle.getSerializable(FRAGMENT_BUNDLE_OBJECT);
            if(EmptyUtil.isNotEmpty(mPagerParams)){
                mSort = mPagerParams.getSort();
                mKeyWord = mPagerParams.getKeyWord();
            }
        }

        if(EmptyUtil.isEmpty(mSort)) return false;
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
            public void onItemClick(RecyclerAdapter.ViewHolder holder, OnlineClassEntity entity) {
                super.onItemClick(holder, entity);
                ClassInfoParams params = new ClassInfoParams(entity);
                ClassDetailActivity.show(getActivity(),params);
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
        if(isMoreData){
            ++ pageIndex;
        }else{
            pageIndex = 0;
            mRefreshLayout.showRefresh();
        }

        // 获取筛选的数据
        if(EmptyUtil.isNotEmpty(mNavigator)){
            int[] params = mNavigator.getFiltrateParams();
            if(EmptyUtil.isEmpty(params) || params.length < 4) return;
            int firstId = params[0];
            int secondId = params[1];
            int thirdId = params[2];
            int fourthId = params[3];

            int intSort = Integer.parseInt(mSort);
            // 获取KeyWord
            mKeyWord = mNavigator.getKeyWord();
            mPresenter.requestOnlineClassData(mKeyWord,pageIndex,AppConfig.PAGE_SIZE,intSort,firstId,secondId,thirdId,fourthId);
        }

        Activity activity = getActivity();
        if(activity instanceof ActivityNavigator){
            ActivityNavigator navigator = (ActivityNavigator) activity;
        }
    }

    @Override
    public void updateOnlineClassView(@NonNull List<OnlineClassEntity> entities) {
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
    public void updateMoreOnlineClassView(@NonNull List<OnlineClassEntity> entities) {
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

    @Override
    public void showError(int str) {
        super.showError(str);
        mRefreshLayout.onHeaderRefreshComplete();
        mRefreshLayout.onFooterRefreshComplete();
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

    @Override
    public void reloadData() {
        super.reloadData();
        // 重新加载数据
        requestClassData(false);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(@NonNull EventWrapper event){
        if(EventWrapper.isMatch(event, EventConstant.ONLINE_CLASS_COMPLETE_GIVE_EVENT)){
            // 刷新UI
            requestClassData(false);
        }
    }

    /**
     * 设置回调对象
     */
    private void setActivityNavigator(@NonNull ActivityNavigator navigator){
        this.mNavigator = navigator;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }
    }
}
