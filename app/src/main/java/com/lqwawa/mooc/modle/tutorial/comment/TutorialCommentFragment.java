package com.lqwawa.mooc.modle.tutorial.comment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.R;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.base.CourseEmptyView;
import com.lqwawa.intleducation.base.PresenterFragment;
import com.lqwawa.intleducation.base.widgets.PullRefreshView.PullToRefreshView;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerItemDecoration;
import com.lqwawa.intleducation.common.ui.CommentDialog;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.KeyboardUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.entity.tutorial.AssistStudentEntity;
import com.lqwawa.intleducation.factory.data.entity.tutorial.TutorCommentEntity;
import com.lqwawa.intleducation.factory.event.EventConstant;
import com.lqwawa.intleducation.factory.event.EventWrapper;
import com.lqwawa.intleducation.factory.helper.UserHelper;
import com.lqwawa.intleducation.module.discovery.ui.CourseDetailsActivity;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.coursedetails.CourseDetailItemParams;
import com.lqwawa.intleducation.module.discovery.ui.navigator.CourseDetailsNavigator;
import com.lqwawa.intleducation.module.learn.ui.MyCourseDetailsActivity;
import com.lqwawa.intleducation.module.tutorial.teacher.students.TutorialStudentAdapter;
import com.lqwawa.mooc.modle.tutorial.TutorialParams;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Date;
import java.util.List;

/**
 * @author mrmedici
 * @desc 帮辅主页评论列表页面
 */
public class TutorialCommentFragment extends PresenterFragment<TutorialCommentContract.Presenter>
    implements TutorialCommentContract.View,
        TutorialCommentAdapter.TutorialCommentCallback,
        View.OnClickListener {

    private PullToRefreshView mRefreshLayout;
    private RecyclerView mRecycler;
    private CourseEmptyView mEmptyView;
    private TutorialCommentAdapter mCommentAdapter;

    private LinearLayout mCommentLayout;
    private EditText mCommentContent;
    private TextView mBtnSend;

    private CommentDialog mCommentDialog;
    private CommentDialog.CommentData mCommentData;

    private TutorialParams mTutorialParams;
    private String mTutorMemberId;
    private String mTutorName;

    private int pageIndex;

    public static Fragment newInstance(@NonNull TutorialParams params){
        Fragment fragment = new TutorialCommentFragment();
        Bundle arguments = new Bundle();
        arguments.putSerializable(FRAGMENT_BUNDLE_OBJECT,params);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    protected TutorialCommentContract.Presenter initPresenter() {
        return new TutorialCommentPresenter(this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_tutorial_comment;
    }

    @Override
    protected boolean initArgs(Bundle bundle) {
        mTutorialParams = (TutorialParams) bundle.getSerializable(FRAGMENT_BUNDLE_OBJECT);
        if(EmptyUtil.isNotEmpty(mTutorialParams)){
            mTutorMemberId = mTutorialParams.getTutorMemberId();
            mTutorName = mTutorialParams.getTutorName();
        }
        return super.initArgs(bundle);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mRefreshLayout = (PullToRefreshView) mRootView.findViewById(com.lqwawa.intleducation.R.id.refresh_layout);
        mEmptyView = (CourseEmptyView) mRootView.findViewById(com.lqwawa.intleducation.R.id.empty_layout);

        mRecycler = (RecyclerView) mRootView.findViewById(com.lqwawa.intleducation.R.id.recycler);
        mRecycler.setNestedScrollingEnabled(false);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecycler.setLayoutManager(mLayoutManager);
        boolean isTutor = TextUtils.equals(com.lqwawa.intleducation.module.user.tool.UserHelper.getUserId(),mTutorMemberId);
        mCommentAdapter = new TutorialCommentAdapter(isTutor,null);
        mRecycler.setAdapter(mCommentAdapter);
        mRecycler.addItemDecoration(new RecyclerItemDecoration(getActivity(),RecyclerItemDecoration.VERTICAL_LIST));

        mCommentAdapter.setCallback(this);

        // 下拉刷新
        mRefreshLayout.setLastUpdated(new Date().toLocaleString());
        mRefreshLayout.showRefresh();
        mRefreshLayout.setOnHeaderRefreshListener(new PullToRefreshView.OnHeaderRefreshListener() {
            @Override
            public void onHeaderRefresh(PullToRefreshView view) {
                requestCommentData(false);
            }
        });

        mRefreshLayout.setLoadMoreEnable(false);
        mRefreshLayout.setOnFooterRefreshListener(new PullToRefreshView.OnFooterRefreshListener() {
            @Override
            public void onFooterRefresh(PullToRefreshView view) {
                requestCommentData(true);
            }
        });

        mCommentLayout = (LinearLayout) mRootView.findViewById(R.id.comment_layout);
        mCommentContent = (EditText) mRootView.findViewById(R.id.et_comment_content);
        mBtnSend = (TextView) mRootView.findViewById(R.id.btn_send);
        mBtnSend.setOnClickListener(this);

        // @func   :点击文本框,显示评论对话框
        mCommentContent.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                final CommentDialog.CommentData data = mCommentData;
                triggerComment(data);
                return true;
            }
            return false;
        });

    }

    @Override
    protected void initData() {
        super.initData();
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }
        requestCommentData(false);
    }

    private void requestCommentData(boolean moreData){
        if(!moreData){
            pageIndex = 0;
        }else {
            pageIndex ++;
        }

        String memberId = com.lqwawa.intleducation.module.user.tool.UserHelper.getUserId();
        // TODO 测试数据
        memberId = "95eab099-0cf8-4b69-866b-83c85dfad8a0";
        mTutorMemberId = "95eab099-0cf8-4b69-866b-83c85dfad8a0";
        mPresenter.requestTutorialCommentData(memberId,mTutorMemberId,pageIndex);
    }

    /**
     * 发生评论
     * @param data 评论数据
     */
    private void triggerComment(@NonNull CommentDialog.CommentData data){
        int commentType = CommentDialog.TYPE_COMMENT_LOW_PERMISSION;
        CommentDialog dialog = new CommentDialog(getActivity(), 0, commentType,true, data, new CommentDialog.CommitCallBack() {
            @Override
            public void dismiss(CommentDialog.CommentData module) {
                // 更新文本到输入框中
                if(EmptyUtil.isNotEmpty(module)) {
                    TutorialCommentFragment.this.mCommentData = module;
                    mCommentContent.setText(module.getContent());
                    mCommentContent.setSelection(module.getContent().length());
                    // 关闭软键盘
                    KeyboardUtil.hideSoftInput(mCommentContent);
                }
            }

            @Override
            public void triggerSend(CommentDialog.CommentData module) {
                if (mCommentDialog.isShowing()) {
                    mCommentDialog.dismiss();
                    // 提交评论
                    commitComment(module);
                }
            }
        });

        this.mCommentDialog = dialog;
        if (mCommentDialog != null && !mCommentDialog.isShowing()) {
            Window window = mCommentDialog.getWindow();
            mCommentDialog.show();
            window.setGravity(Gravity.BOTTOM);
        }
    }

    private void commitComment(@NonNull CommentDialog.CommentData data){
        String memberId = com.lqwawa.intleducation.module.user.tool.UserHelper.getUserId();
        // TODO 测试数据
        memberId = "95eab099-0cf8-4b69-866b-83c85dfad8a0";
        mTutorMemberId = "95eab099-0cf8-4b69-866b-83c85dfad8a0";
        mPresenter.requestSendTutorialComment(memberId,mTutorMemberId,data.getContent());
    }

    @Override
    public void updateTutorialCommentView(@NonNull List<TutorCommentEntity> entities) {
        // 判断有无更多数据,打开或者关闭加载更多
        mRefreshLayout.onHeaderRefreshComplete();
        mRefreshLayout.setLoadMoreEnable(entities.size() >= AppConfig.PAGE_SIZE);
        mCommentAdapter.replace(entities);

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
    public void updateMoreTutorialCommentView(@NonNull List<TutorCommentEntity> entities) {
        // 关闭加载更多
        mRefreshLayout.onFooterRefreshComplete();
        mRefreshLayout.setLoadMoreEnable(entities.size() >= AppConfig.PAGE_SIZE);
        // 设置数据
        mCommentAdapter.add(entities);
        mCommentAdapter.notifyDataSetChanged();
    }

    @Override
    public void updateSingleCommentChangeStatusView(boolean result, int oldStatus, int newStatus) {
        // 请求刷新数据
        requestCommentData(false);
    }

    @Override
    public void updateSendTutorialCommentView(@NonNull boolean result) {
        // 请求刷新数据
        requestCommentData(false);
    }

    @Override
    public void updateAddCommentPraiseView(boolean result) {
        // 请求刷新数据
        requestCommentData(false);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if(viewId == R.id.btn_send){
            // 发送
            final CommentDialog.CommentData data = this.mCommentData;
            commitComment(data);
            mCommentContent.getText().clear();
        }
    }

    @Override
    public void onStatusChanged(int position, @NonNull TutorCommentEntity entity) {
        String memberId = com.lqwawa.intleducation.module.user.tool.UserHelper.getUserId();
        int commentId = entity.getId();
        int oldStatus = entity.getStatus();
        mPresenter.requestSingleCommentChangeStatus(memberId,commentId,1 - oldStatus);
    }

    @Override
    public void onZanChanged(int position, @NonNull TutorCommentEntity entity) {
        String memberId = com.lqwawa.intleducation.module.user.tool.UserHelper.getUserId();
        int commentId = entity.getId();
        mPresenter.requestAddCommentPraise(memberId,commentId);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(@NonNull EventWrapper event){
        if(EventWrapper.isMatch(event,EventConstant.TRIGGER_ATTENTION_TUTORIAL_UPDATE)){
            boolean attention = (boolean) event.getData();
            boolean isTutor = TextUtils.equals(com.lqwawa.intleducation.module.user.tool.UserHelper.getUserId(),mTutorMemberId);
            if(!isTutor && attention){
                mCommentLayout.setVisibility(View.VISIBLE);
            }else{
                mCommentLayout.setVisibility(View.GONE);
            }
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
