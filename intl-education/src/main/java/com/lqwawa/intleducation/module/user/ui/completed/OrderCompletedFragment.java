package com.lqwawa.intleducation.module.user.ui.completed;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.CourseEmptyView;
import com.lqwawa.intleducation.base.PresenterFragment;
import com.lqwawa.intleducation.base.ui.MyBaseAdapter;
import com.lqwawa.intleducation.base.utils.ToastUtil;
import com.lqwawa.intleducation.base.widgets.PullRefreshView.PullToRefreshView;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.module.user.adapter.MyOrderListAdapter;
import com.lqwawa.intleducation.module.user.tool.UserHelper;
import com.lqwawa.intleducation.module.user.ui.all.OrderAllContract;
import com.lqwawa.intleducation.module.user.ui.all.OrderAllPresenter;
import com.lqwawa.intleducation.module.user.vo.MyOrderVo;

import java.util.Date;
import java.util.List;

/**
 * 描述: 已完成fragment
 * 作者|时间: djj on 2019/6/10 0010 上午 9:36
 */
public class OrderCompletedFragment extends PresenterFragment<OrderAllContract.Presenter>
        implements OrderAllContract.View, ListView.OnItemClickListener {

    private PullToRefreshView mRefreshView;
    private ListView mListView;
    private CourseEmptyView mEmptyView;
    private MyOrderListAdapter mAdapter;

    private int mPageIndex = 0;
    private int tabType = 3;//已完成
    private String mMemberId = UserHelper.getUserId();

    @Override
    protected OrderAllContract.Presenter initPresenter() {
        return new OrderAllPresenter(this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_order_all;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mRefreshView = (PullToRefreshView) mRootView.findViewById(R.id.refresh_layout);
        mListView = (ListView) mRootView.findViewById(R.id.list_view);
        mEmptyView = (CourseEmptyView) mRootView.findViewById(R.id.empty_layout);

        // 下拉刷新
        mRefreshView.setLastUpdated(new Date().toLocaleString());
        mRefreshView.showRefresh();
        //初始化下拉刷新
        mRefreshView.setOnHeaderRefreshListener(new PullToRefreshView.OnHeaderRefreshListener() {
            @Override
            public void onHeaderRefresh(PullToRefreshView view) {
                requestOrderListData(false);
            }
        });
        mRefreshView.setOnFooterRefreshListener(new PullToRefreshView.OnFooterRefreshListener() {
            @Override
            public void onFooterRefresh(PullToRefreshView view) {
                requestOrderListData(true);
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        requestOrderListData(false);
    }

    /**
     * 请求列表数据
     *
     * @param moreData
     */
    private void requestOrderListData(boolean moreData) {
        if (moreData) {
            mPageIndex++;
            mPresenter.requestOrderList(mPageIndex, AppConfig.PAGE_SIZE, tabType, mMemberId);
        } else {
            mPageIndex = 0;
            mPresenter.requestOrderList(mPageIndex, AppConfig.PAGE_SIZE, tabType, mMemberId);
        }
    }

    @Override
    public void updateOrderList(@NonNull List<MyOrderVo> orderVos) {
        // 判断有无更多数据,打开或者关闭加载更多
        mRefreshView.onHeaderRefreshComplete();
        mRefreshView.setLoadMoreEnable(orderVos.size() >= AppConfig.PAGE_SIZE);
        mAdapter = new MyOrderListAdapter(getActivity(), new MyBaseAdapter.OnContentChangedListener() {
            @Override
            public void OnContentChanged() {
                mRefreshView.showRefresh();
                requestOrderListData(false);
            }
        });
        mAdapter.setData(orderVos);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);

        if (EmptyUtil.isEmpty(orderVos)) {
            // 数据为空
            mListView.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            // 数据不为空
            mListView.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public void updateMoreOrderList(@NonNull List<MyOrderVo> orderVos) {
        // 关闭加载更多
        mRefreshView.onFooterRefreshComplete();
        mRefreshView.setLoadMoreEnable(orderVos.size() >= AppConfig.PAGE_SIZE);
        // 设置数据
        mAdapter.addData(orderVos);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MyOrderVo vo = (MyOrderVo) mAdapter.getItem(position);
        if (vo != null) {
            if (vo.isIsExpire()) {
                try {
                    ToastUtil.showToast(getActivity(),
                            getResources().getString(R.string.order_expired));
                } catch (Exception e) {

                }
            }
        }
    }

    @Override
    public void showError(int str) {
        super.showError(str);
        mRefreshView.onHeaderRefreshComplete();
        mRefreshView.onFooterRefreshComplete();
    }
}
