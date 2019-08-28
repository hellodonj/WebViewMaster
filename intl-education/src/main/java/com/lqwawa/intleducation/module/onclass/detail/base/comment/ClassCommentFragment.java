package com.lqwawa.intleducation.module.onclass.detail.base.comment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.github.jdsjlzx.interfaces.OnLoadMoreListener;
import com.github.jdsjlzx.recyclerview.LuRecyclerView;
import com.github.jdsjlzx.recyclerview.LuRecyclerViewAdapter;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.PresenterFragment;
import com.lqwawa.intleducation.base.widgets.ScrollChildSwipeRefreshLayout;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.KeyboardUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.entity.ClassDetailEntity;
import com.lqwawa.intleducation.factory.data.entity.OnlineCommentEntity;
import com.lqwawa.intleducation.factory.event.EventConstant;
import com.lqwawa.intleducation.module.discovery.vo.CommentVo;
import com.lqwawa.intleducation.module.onclass.detail.base.BaseClassDetailNavigator;
import com.lqwawa.intleducation.module.onclass.detail.base.OnlineTabParams;
import com.lqwawa.intleducation.module.onclass.detail.join.JoinClassDetailNavigator;
import com.lqwawa.intleducation.module.onclass.detail.notjoin.ClassDetailNavigator;
import com.lqwawa.lqbaselib.pojo.MessageEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 在线课堂班级详情 授课计划Fragment
 * @date 2018/06/01 16:03
 * @history v1.0
 * **********************************
 */
public class ClassCommentFragment extends PresenterFragment<ClassCommentContract.Presenter>
        implements ClassCommentContract.View {

    private ScrollChildSwipeRefreshLayout mRefreshLayout;
    // 评论列表
    private LuRecyclerView mRecycler;
    // 空布局显示
    private TextView mTvRemind;

    private OnlineTabParams mTabParams;
    private ClassDetailEntity mClassDetailEntity;
    private String mRole;

    // 评论列表
    private ClassCommentAdapter mCommentAdapter;
    private LuRecyclerViewAdapter mLuCommentAdapter;

    private int pageIndex;

    public static Fragment newInstance(@NonNull OnlineTabParams params) {
        ClassCommentFragment fragment = new ClassCommentFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(FRAGMENT_BUNDLE_OBJECT, params);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected ClassCommentContract.Presenter initPresenter() {
        return new ClassCommentPresenter(this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_class_comment;
    }

    @Override
    protected boolean initArgs(Bundle bundle) {
        mTabParams = (OnlineTabParams) bundle.getSerializable(FRAGMENT_BUNDLE_OBJECT);
        if (EmptyUtil.isEmpty(mTabParams)) return false;
        mClassDetailEntity = mTabParams.getDetailEntity();
        mRole = mTabParams.getRole();

        if (EmptyUtil.isEmpty(mClassDetailEntity) ||
                EmptyUtil.isEmpty(mRole) ||
                EmptyUtil.isEmpty(mClassDetailEntity.getData())) {
            return false;
        }

        return super.initArgs(bundle);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mRefreshLayout =
                (ScrollChildSwipeRefreshLayout) mRootView.findViewById(R.id.refresh_layout);
        mRecycler = (LuRecyclerView) mRootView.findViewById(R.id.recycler);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecycler.setLayoutManager(mLayoutManager);

        mCommentAdapter = new ClassCommentAdapter(getActivity(), new ClassCommentAdapter.OnContentChangedListener() {
            @Override
            public void OnContentChanged() {
                getOnlineClassCommentData(0);
            }
        });
        mCommentAdapter.setData(mClassDetailEntity, (vo, content) -> {
            // 评论回复回调
            if (TextUtils.isEmpty(content)) {
                UIUtil.showToastSafe(R.string.enter_evaluation_content_please);
                return;
            }

            if (!EmptyUtil.isEmpty(mClassDetailEntity.getData())) {
                ClassDetailEntity.DataBean dataBean = mClassDetailEntity.getData().get(0);
                mPresenter.requestCommitComment(1, dataBean.getId(), vo.getId(), content, 0);
            }
        });

        mLuCommentAdapter = new LuRecyclerViewAdapter(mCommentAdapter);
        mRecycler.setAdapter(mLuCommentAdapter);

        mRecycler.setOnLoadMoreListener(() -> {
            pageIndex++;
            getOnlineClassCommentData(pageIndex);
        });

        mRefreshLayout.setOnRefreshListener(() -> getOnlineClassCommentData(0));

        mTvRemind = (TextView) mRootView.findViewById(R.id.tv_empty_remind);
    }

    private void getOnlineClassCommentData(int pageIndex) {
        List<ClassDetailEntity.DataBean> data = mClassDetailEntity.getData();
        if (!EmptyUtil.isEmpty(data)) {
            mPresenter.requestOnlineClassCommentData(data.get(0).getId(), pageIndex);
        }
    }

    @Override
    protected void initData() {
        super.initData();
        getOnlineClassCommentData(0);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(@NonNull MessageEvent event){
        String action = event.getUpdateAction();
        if(TextUtils.equals(action, EventConstant.TRIGGER_CLASS_DETAIL_COMMENTS_UPDATE)){
           getOnlineClassCommentData(0);
        }
    }

    @Override
    public void updateOnlineClassCommentView(@NonNull OnlineCommentEntity entity) {
        mRefreshLayout.setRefreshing(false);
        
        List<CommentVo> listComment = entity.getCommentList();

        mCommentAdapter.setData(entity.getStarLevel());
        mCommentAdapter.setData(listComment);

        if (EmptyUtil.isEmpty(listComment)) {
            // mRecycler.setVisibility(View.GONE);
            mTvRemind.setVisibility(View.VISIBLE);
        } else {
            // mRecycler.setVisibility(View.VISIBLE);
            mTvRemind.setVisibility(View.GONE);
        }
        updateOnlineCommentEntity(entity);
    }

    @Override
    public void updateOnlineClassMoreCommentView(@NonNull OnlineCommentEntity entity) {
        List<CommentVo> listComment = entity.getCommentList();

        mCommentAdapter.setData(entity.getStarLevel());
        mCommentAdapter.addData(listComment);

        mRecycler.refreshComplete(AppConfig.PAGE_SIZE);
        if (listComment.size() < AppConfig.PAGE_SIZE) {
            // 加载更多，并且没有更多数据了
            mRecycler.setNoMore(true);
        }
        updateOnlineCommentEntity(entity);
    }

    private void  updateOnlineCommentEntity(OnlineCommentEntity entity)  {
        if (entity != null) {
            if (getActivity() instanceof BaseClassDetailNavigator) {
                BaseClassDetailNavigator navigator = (BaseClassDetailNavigator) getActivity();
                navigator.setOnlineCommentEntity(entity);
            }
        }
    }


    @Override
    public void commitCommentResult(boolean isSucceed) {
        // 重新加载评论
        getOnlineClassCommentData(0);
        // 通知Activity 头部刷新UI
        if (getActivity() instanceof BaseClassDetailNavigator) {
            BaseClassDetailNavigator navigator = (BaseClassDetailNavigator) getActivity();
            navigator.refreshData();
        }
        if (isSucceed) {
            // 隐藏软件盘
            KeyboardUtil.hideSoftInput(getActivity());
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        // 在线课堂班级评论显示
        Activity activity = getActivity();
        if (activity instanceof ClassDetailNavigator) {
            ClassDetailNavigator navigator = (ClassDetailNavigator) activity;
            navigator.onCommentChanged(!getUserVisibleHint());
        }

        if (activity instanceof JoinClassDetailNavigator) {
            JoinClassDetailNavigator navigator = (JoinClassDetailNavigator) activity;
            navigator.updateCommentVisibility(!getUserVisibleHint());
        }
    }
}
