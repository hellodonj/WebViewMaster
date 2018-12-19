package com.lqwawa.intleducation.module.organcourse.online.pager;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.CourseEmptyView;
import com.lqwawa.intleducation.base.PresenterFragment;
import com.lqwawa.intleducation.base.widgets.PullRefreshView.PullToRefreshView;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.module.discovery.adapter.CourseListAdapter;
import com.lqwawa.intleducation.module.discovery.ui.CourseDetailsActivity;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.filtrate.HideSortType;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;
import com.lqwawa.intleducation.module.onclass.OnlineSortType;
import com.lqwawa.intleducation.module.organcourse.online.CourseShopListActivity;
import com.lqwawa.intleducation.module.organcourse.online.SearchNavigator;
import com.lqwawa.intleducation.module.user.tool.UserHelper;
import com.lqwawa.intleducation.ui.course.notice.CourseNoticeContract;

import java.util.Date;
import java.util.List;

/**
 * @desc 在线机构学程馆分类的Fragment
 * @author medici
 */
public class CourseShopPagerFragment extends PresenterFragment<CourseShopPagerContract.Presenter>
        implements CourseShopPagerContract.View,SearchNavigator{

    private static final String KEY_EXTRA_SORT_TYPE = "KEY_EXTRA_SORT_TYPE";

    private static final String KEY_EXTRA_SCHOOL_ID = "KEY_EXTRA_SCHOOL_ID";

    private static final String KEY_EXTRA_IS_SCHOOL_ENTER = "KEY_EXTRA_IS_SCHOOL_ENTER";
    // 是不是从在线课堂班级进入的
    private static final String KEY_EXTRA_IS_ONLINE_CLASS_ENTER = "KEY_EXTRA_IS_ONLINE_CLASS_ENTER";



    // 下拉刷新布局
    private PullToRefreshView mRefreshLayout;
    // 空布局
    private CourseEmptyView mEmptyView;
    // 列表布局
    private ListView mListView;
    private CourseListAdapter courseListAdapter;
    // 当前页
    private int currentPage;

    private String mSortType;
    private String mSchoolId;
    private boolean isSchoolEnter;
    private boolean isOnlineClassEnter;

    public static CourseShopPagerFragment newInstance(@NonNull @HideSortType.SortRes String sort, @NonNull String schoolId,
                                       boolean isSchoolEnter, boolean isOnlineClassEnter){
        CourseShopPagerFragment fragment = new CourseShopPagerFragment();
        Bundle arguments = new Bundle();
        arguments.putString(KEY_EXTRA_SORT_TYPE,sort);
        arguments.putString(KEY_EXTRA_SCHOOL_ID,schoolId);
        arguments.putBoolean(KEY_EXTRA_IS_SCHOOL_ENTER,isSchoolEnter);
        arguments.putBoolean(KEY_EXTRA_IS_ONLINE_CLASS_ENTER,isOnlineClassEnter);
        fragment.setArguments(arguments);
        return fragment;
    }


    @Override
    protected CourseShopPagerContract.Presenter initPresenter() {
        return new CourseShopPagerPresenter(this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_course_shop_pager;
    }

    @Override
    protected boolean initArgs(Bundle bundle) {
        mSortType = bundle.getString(KEY_EXTRA_SORT_TYPE);
        mSchoolId = bundle.getString(KEY_EXTRA_SCHOOL_ID);
        isSchoolEnter = bundle.getBoolean(KEY_EXTRA_IS_SCHOOL_ENTER);
        isOnlineClassEnter = bundle.getBoolean(KEY_EXTRA_IS_ONLINE_CLASS_ENTER);
        if(EmptyUtil.isEmpty(mSchoolId)){
            return false;
        }
        return super.initArgs(bundle);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mRefreshLayout = (PullToRefreshView) mRootView.findViewById(R.id.refresh_layout);
        mListView = (ListView) mRootView.findViewById(R.id.listView);
        mEmptyView = (CourseEmptyView) mRootView.findViewById(R.id.empty_layout);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CourseVo vo = (CourseVo) courseListAdapter.getItem(position);
                CourseDetailsActivity.start(getActivity(),isSchoolEnter,isOnlineClassEnter, vo.getId(), true, UserHelper.getUserId());
            }
        });

        // 下拉刷新
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
        // 启动下拉刷新
        mRefreshLayout.setLastUpdated(new Date().toLocaleString());
        mRefreshLayout.showRefresh();
        requestCourseData(false);
    }



    /**
     * 查询课程 isMoreLoaded=true 加载更多数据
     */
    public void requestCourseData(boolean isMoreLoaded){
        int payType = Integer.MAX_VALUE;
        if(!AppConfig.BaseConfig.needShowPay()){
            //只显示免费课程
            payType = 0;
        }

        EditText searchEt = (EditText) getActivity().findViewById(R.id.search_et);
        String searchKey = searchEt.getText().toString();

        if(isMoreLoaded){
            currentPage++;
            mPresenter.requestMoreCourseData(mSchoolId,currentPage,0,mSortType,payType,searchKey);
        }else{
            currentPage = 0;
            mRefreshLayout.showRefresh();
            mPresenter.requestCourseData(mSchoolId,currentPage,0,mSortType,payType,searchKey);
        }
    }

    @Override
    public void onCourseLoaded(List<CourseVo> courseVos) {
        // 判断有无更多数据,打开或者关闭加载更多
        mRefreshLayout.onHeaderRefreshComplete();
        mRefreshLayout.onFooterRefreshComplete();
        mRefreshLayout.setLoadMoreEnable(courseVos.size() >= AppConfig.PAGE_SIZE);
        // 设置数据
        courseListAdapter = new CourseListAdapter(getActivity());
        courseListAdapter.setData(courseVos);
        mListView.setAdapter(courseListAdapter);
        courseListAdapter.notifyDataSetChanged();
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
    public void showError(int str) {
        super.showError(str);
        mRefreshLayout.onHeaderRefreshComplete();
        mRefreshLayout.onFooterRefreshComplete();
    }

    @Override
    public boolean search(@NonNull String searchKey) {
        if(getUserVisibleHint()){
            requestCourseData(false);
            return true;
        }
        return false;
    }

    @Override
    public boolean triggerPriceSwitch(@NonNull boolean up) {
        if(isVisible() &&
                (mSortType == HideSortType.TYPE_SORT_ONLINE_SHOP_PRICE_UP ||
                        mSortType == HideSortType.TYPE_SORT_ONLINE_SHOP_PRICE_DOWN)){
            mSortType = up ? HideSortType.TYPE_SORT_ONLINE_SHOP_PRICE_UP : HideSortType.TYPE_SORT_ONLINE_SHOP_PRICE_DOWN;
            requestCourseData(false);
            return true;
        }
        return false;
    }
}
