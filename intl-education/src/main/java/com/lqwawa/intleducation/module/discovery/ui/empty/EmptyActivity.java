package com.lqwawa.intleducation.module.discovery.ui.empty;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.github.jdsjlzx.interfaces.OnLoadMoreListener;
import com.github.jdsjlzx.recyclerview.LuRecyclerView;
import com.github.jdsjlzx.recyclerview.LuRecyclerViewAdapter;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.EmptyView;
import com.lqwawa.intleducation.base.PresenterActivity;
import com.lqwawa.intleducation.base.widgets.ScrollChildSwipeRefreshLayout;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.classifylist.ClassifyListAdapter;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.home.ClassifyAdapter;

import java.util.List;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 测试空布局规范的Activity
 * @date 2018/05/16 11:19
 * @history v1.0
 * **********************************
 */
public class EmptyActivity extends PresenterActivity<EmptyContract.Presenter>
    implements EmptyContract.View{

    private static final int PAGE_SIZE = 5;

    private ScrollChildSwipeRefreshLayout mRefreshLayout;
    private EmptyView mEmptyView;
    private LuRecyclerView mRecycler;
    private ClassifyListAdapter mAdapter;
    private LuRecyclerViewAdapter mLuAdapter;

    private int pageIndex = 0;

    @Override
    protected EmptyContract.Presenter initPresenter() {
        return new EmptyPresenter(this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_empty;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mRefreshLayout = (ScrollChildSwipeRefreshLayout) findViewById(R.id.refresh_layout);
        mEmptyView = (EmptyView) findViewById(R.id.empty_view);
        mRecycler = (LuRecyclerView) findViewById(R.id.recycler);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new ClassifyListAdapter();
        mLuAdapter = new LuRecyclerViewAdapter(mAdapter);
        mRecycler.setAdapter(mLuAdapter);
        mEmptyView.bind(mRefreshLayout);
        setPlaceHolderView(mEmptyView);

        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pageIndex = 0;
                mPresenter.getData(pageIndex,PAGE_SIZE);
            }
        });

        // 如果在初始化的时候设置false footerView 就被移除了
        // mRecycler.setLoadMoreEnabled(false);
        mRecycler.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                loadMore();
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        mRefreshLayout.autoRefresh();
    }

    /**
     * 加载更多
     */
    private void loadMore(){
        pageIndex++;
        mPresenter.getData(pageIndex,PAGE_SIZE);
    }

    @Override
    public RecyclerAdapter<LQCourseConfigEntity> getRecyclerAdapter() {
        return mAdapter;
    }

    @Override
    public void onAdapterDataChanged(boolean loadMore, @NonNull List<LQCourseConfigEntity> listData) {
        hideLoading();
        mRefreshLayout.setRefreshing(false);
        mPlaceHolderView.triggerOkOrEmpty(mAdapter.getItemCount() > 0);
        mRecycler.setLoadMoreEnabled(EmptyUtil.isNotEmpty(listData) && listData.size() >= PAGE_SIZE);
        // 如果totalCount == visibleCount 会发生UI错误
        mRecycler.refreshComplete(PAGE_SIZE);
        if(loadMore && listData.size() < PAGE_SIZE){
            // 加载更多，并且没有更多数据了
            mRecycler.setNoMore(true);
        }
    }

    @Override
    public void showError(int str) {
        super.showError(str);
        mRefreshLayout.setRefreshing(false);
    }

    @Override
    public void reloadData() {
        super.reloadData();
        pageIndex = 0;
        mPresenter.getData(pageIndex,PAGE_SIZE);
    }

    /**
     * 空页面的调试入口
     * @param context
     */
    public static void show(@NonNull Context context){
        Intent intent = new Intent(context,EmptyActivity.class);
        context.startActivity(intent);
    }
}
