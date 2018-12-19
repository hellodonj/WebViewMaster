package com.lqwawa.intleducation.module.discovery.lessontask.committedtask;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.PresenterFragment;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.module.learn.vo.LqTaskCommitVo;

import java.util.ArrayList;
import java.util.List;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 课时任务提交列表显示
 * @date 2018/04/13 09:37
 * @history v1.0
 * **********************************
 */
public class CommittedTaskFragment extends PresenterFragment<CommittedTaskContract.Presenter>
    implements CommittedTaskContract.View{

    public static final String KEY_EXTRA_COMMITTED_TASKS = "KEY_EXTRA_COMMITTED_TASKS";

    private RecyclerView mRecycler;

    // 任务提交列表的Adapter
    private CommittedTaskAdapter mAdapter;

    private List<LqTaskCommitVo> mCommitTaskVos;

    /**
     * 获取任务提交列表的Fragment
     * @param commitTasks 列表数据
     * @return Fragment
     */
    public static Fragment getInstance(List<LqTaskCommitVo> commitTasks){
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_EXTRA_COMMITTED_TASKS,(ArrayList<LqTaskCommitVo>)commitTasks);
        CommittedTaskFragment mFragment = new CommittedTaskFragment();
        mFragment.setArguments(bundle);
        return mFragment;
    }


    @Override
    protected CommittedTaskContract.Presenter initPresenter() {
        return new CommittedTaskPresenter(this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_committed_task;
    }

    @Override
    protected boolean initArgs(Bundle bundle) {
        mCommitTaskVos = (List<LqTaskCommitVo>) bundle.get(KEY_EXTRA_COMMITTED_TASKS);
        return super.initArgs(bundle);

    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mRecycler = (RecyclerView) mRootView.findViewById(R.id.recycler);
        mRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new CommittedTaskAdapter();
        mAdapter.replace(mCommitTaskVos);
        mRecycler.setAdapter(mAdapter);

        mAdapter.setListener(new RecyclerAdapter.AdapterListenerImpl<LqTaskCommitVo>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, LqTaskCommitVo lqTaskCommitVo) {
                super.onItemClick(holder, lqTaskCommitVo);
                UIUtil.showToastSafe("点击了某一项");
            }
        });
    }

    @Override
    public RecyclerAdapter<LqTaskCommitVo> getRecyclerAdapter() {
        return mAdapter;
    }

    @Override
    public void onAdapterDataChanged(boolean loadMore, @NonNull List<LqTaskCommitVo> listData) {

    }
}
