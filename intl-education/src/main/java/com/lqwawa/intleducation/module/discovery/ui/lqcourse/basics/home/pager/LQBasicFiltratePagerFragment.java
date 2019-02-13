package com.lqwawa.intleducation.module.discovery.ui.lqcourse.basics.home.pager;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.CourseEmptyView;
import com.lqwawa.intleducation.base.PresenterFragment;
import com.lqwawa.intleducation.base.widgets.PullRefreshView.PullToRefreshView;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.factory.event.EventConstant;
import com.lqwawa.intleducation.factory.event.EventWrapper;
import com.lqwawa.intleducation.module.discovery.adapter.CourseListAdapter;
import com.lqwawa.intleducation.module.discovery.ui.CourseDetailsActivity;
import com.lqwawa.intleducation.module.discovery.ui.study.filtrate.ActivityNavigator;
import com.lqwawa.intleducation.module.discovery.ui.study.filtrate.PagerNavigator;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;
import com.lqwawa.intleducation.module.onclass.OnlineSortType;
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
public class LQBasicFiltratePagerFragment extends PresenterFragment<LQBasicFiltratePagerContract.Presenter>
        implements LQBasicFiltratePagerContract.View,PagerNavigator {

    // 下拉刷新布局
    private PullToRefreshView mRefreshLayout;
    private CourseEmptyView mEmptyLayout;
    private ListView mListView;
    // Adapter
    private CourseListAdapter courseListAdapter;

    private ActivityNavigator mNavigator;
    private PagerParams mPagerParams;
    private String mLevel;
    private String mSort;
    private String mKeyWord;

    private int pageIndex;

    /**
     * 将授课堂类型筛选班级Fragment的入口
     * @param params 核心参数
     * @return OnlineClassPagerFragment
     */
    public static LQBasicFiltratePagerFragment newInstance(@NonNull PagerParams params,
                                                           @NonNull ActivityNavigator navigator){
        LQBasicFiltratePagerFragment fragment = new LQBasicFiltratePagerFragment();
        fragment.setActivityNavigator(navigator);
        Bundle arguments = new Bundle();
        arguments.putSerializable(FRAGMENT_BUNDLE_OBJECT,params);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    protected LQBasicFiltratePagerContract.Presenter initPresenter() {
        return new LQBasicFiltratePagerPresenter(this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_lq_basic_filtrate_pager;
    }

    @Override
    protected boolean initArgs(Bundle bundle) {
        if(bundle.containsKey(FRAGMENT_BUNDLE_OBJECT)){
            mPagerParams = (PagerParams) bundle.getSerializable(FRAGMENT_BUNDLE_OBJECT);
            if(EmptyUtil.isNotEmpty(mPagerParams)){
                mLevel = mPagerParams.getLevel();
                mSort = mPagerParams.getSort();
                mKeyWord = mPagerParams.getKeyWord();
            }
        }

        if(EmptyUtil.isEmpty(mSort) || EmptyUtil.isEmpty(mLevel)) return false;
        return super.initArgs(bundle);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mRefreshLayout = (PullToRefreshView) mRootView.findViewById(R.id.refresh_layout);
        mEmptyLayout = (CourseEmptyView) mRootView.findViewById(R.id.empty_layout);
        mListView = (ListView) mRootView.findViewById(R.id.list_view);
        courseListAdapter = new CourseListAdapter(getActivity());
        mListView.setAdapter(courseListAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CourseVo vo = (CourseVo) courseListAdapter.getItem(position);
                CourseDetailsActivity.start(getActivity(), vo.getId(), true, UserHelper.getUserId());
            }
        });

        mRefreshLayout.setLastUpdated(new Date().toLocaleString());
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
    }

    @Override
    protected void initData() {
        super.initData();
        requestCourseData(false);
    }



    /**
     * 加载班级数据
     * @param isMoreData 是否是加载更多
     */
    private void requestCourseData(boolean isMoreData){
        if(isMoreData){
            ++ pageIndex;
        }else{
            pageIndex = 0;
            mRefreshLayout.showRefresh();
        }

        // 获取筛选的数据
        if(EmptyUtil.isNotEmpty(mNavigator)){
            int[] params = mNavigator.getFiltrateParams();
            if(EmptyUtil.isEmpty(params) || params.length < 3) return;
            int paramOneId = params[0];
            int paramTwoId = params[1];
            int paramThreeId = params[2];

            int intSort = Integer.parseInt(mSort);
            // 获取KeyWord
            mKeyWord = mNavigator.getKeyWord();
            mPresenter.requestCourseData(isMoreData,pageIndex, AppConfig.PAGE_SIZE,mLevel,mSort,mKeyWord,paramOneId,paramTwoId,paramThreeId);
        }

        Activity activity = getActivity();
        if(activity instanceof ActivityNavigator){
            ActivityNavigator navigator = (ActivityNavigator) activity;
        }
    }

    @Override
    public void updateCourseView(@NonNull List<CourseVo> courseVos) {
        mRefreshLayout.onHeaderRefreshComplete();
        mRefreshLayout.setLoadMoreEnable(EmptyUtil.isNotEmpty(courseVos) && courseVos.size() >= AppConfig.PAGE_SIZE);

        // 设置数据
        courseListAdapter = new CourseListAdapter(getActivity());
        courseListAdapter.setData(courseVos);
        mListView.setAdapter(courseListAdapter);

        if(EmptyUtil.isEmpty(courseVos)){
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
    public void updateMoreCourseView(@NonNull List<CourseVo> courseVos) {
        // 关闭加载更多
        mRefreshLayout.onFooterRefreshComplete();
        mRefreshLayout.setLoadMoreEnable(courseVos.size() >= AppConfig.PAGE_SIZE);
        // 设置数据
        courseListAdapter.addData(courseVos);
        courseListAdapter.notifyDataSetChanged();
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
            requestCourseData(false);
            return true;
        }
        return false;
    }

    @Override
    public void reloadData() {
        super.reloadData();
        // 重新加载数据
        requestCourseData(false);
    }

    /**
     * 设置回调对象
     */
    private void setActivityNavigator(@NonNull ActivityNavigator navigator){
        this.mNavigator = navigator;
    }
}
