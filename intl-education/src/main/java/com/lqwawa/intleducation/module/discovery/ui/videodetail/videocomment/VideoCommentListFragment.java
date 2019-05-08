package com.lqwawa.intleducation.module.discovery.ui.videodetail.videocomment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.CourseEmptyView;
import com.lqwawa.intleducation.base.PresenterFragment;
import com.lqwawa.intleducation.base.widgets.PullRefreshView.PullToRefreshView;
import com.lqwawa.intleducation.common.ui.CommentDialog;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.KeyboardUtil;
import com.lqwawa.intleducation.factory.event.EventConstant;
import com.lqwawa.intleducation.module.discovery.vo.CommentVo;
import com.lqwawa.intleducation.module.user.tool.UserHelper;
import com.lqwawa.lqbaselib.pojo.MessageEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;
import java.util.List;

/**
 * @author: wangchao
 * @date: 2019/05/08
 * @desc:
 */
public class VideoCommentListFragment extends PresenterFragment<VideoCommentContract.Presenter>
        implements VideoCommentContract.View {

    private PullToRefreshView pullToRefreshView;
    private RecyclerView recyclerView;
    private CourseEmptyView courseEmptyView;
    private VideoCommentAdapter videoCommentAdapter;

    private EditText commentContentEditText;
    private TextView sendTextView;

    private CommentDialog commentDialog;
    private CommentDialog.CommentData commentData;

    private Long courseId;

    private int pageIndex = 0;
    private int pageSize = 12;

    public static VideoCommentListFragment newInstance(Long courseId) {

        Bundle args = new Bundle();
        args.putLong("courseId", courseId);
        VideoCommentListFragment fragment = new VideoCommentListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected VideoCommentContract.Presenter initPresenter() {
        return new VideoCommentPresenter(this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_video_comment_list;
    }

    @Override
    protected boolean initArgs(Bundle bundle) {
        courseId = bundle.getLong("courseId");
        return super.initArgs(bundle);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        pullToRefreshView = (PullToRefreshView) mRootView.findViewById(R.id.refresh_layout);
        courseEmptyView = (CourseEmptyView) mRootView.findViewById(R.id.empty_layout);

        recyclerView = (RecyclerView) mRootView.findViewById(R.id.recycler);
        recyclerView.setNestedScrollingEnabled(false);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        videoCommentAdapter = new VideoCommentAdapter();
        recyclerView.setAdapter(videoCommentAdapter);
        DividerItemDecoration divider = new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.divider_line_h_1dp));
        recyclerView.addItemDecoration(divider);

        // 下拉刷新
        pullToRefreshView.setLastUpdated(new Date().toLocaleString());
        pullToRefreshView.showRefresh();
        pullToRefreshView.setOnHeaderRefreshListener(view -> getVideoComments(false));

        pullToRefreshView.setLoadMoreEnable(false);
        pullToRefreshView.setOnFooterRefreshListener(view -> getVideoComments(true));

        commentContentEditText = (EditText) mRootView.findViewById(R.id.et_comment_content);
        sendTextView = (TextView) mRootView.findViewById(R.id.btn_send);
        sendTextView.setOnClickListener(view -> {
            commitComment(commentData);
        });

        // 点击文本框,显示评论对话框
        commentContentEditText.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                final CommentDialog.CommentData data = commentData;
                triggerComment(data);
                return true;
            }
            return false;
        });

    }

    @Override
    protected void initData() {
        super.initData();
        getVideoComments(false);
    }

    /**
     * 评论弹窗
     *
     * @param data 评论数据
     */
    private void triggerComment(@NonNull CommentDialog.CommentData data) {
        int commentType = CommentDialog.TYPE_COMMENT_LOW_PERMISSION;
        CommentDialog dialog = new CommentDialog(getContext(), 0, commentType, true, data,
                new CommentDialog.CommitCallBack() {
                    @Override
                    public void dismiss(CommentDialog.CommentData commentData) {
                        // 更新文本到输入框中
                        if (EmptyUtil.isNotEmpty(commentData)) {
                            VideoCommentListFragment.this.commentData = commentData;
                            commentContentEditText.setText(commentData.getContent());
                            commentContentEditText.setSelection(commentData.getContent().length());
                            // 关闭软键盘
                            KeyboardUtil.hideSoftInput(commentContentEditText);
                        }
                    }

                    @Override
                    public void triggerSend(CommentDialog.CommentData module) {
                        if (commentDialog.isShowing()) {
                            commentDialog.dismiss();
                            // 提交评论
                            commitComment(module);
                        }
                    }
                });

        this.commentDialog = dialog;
        if (commentDialog != null && !commentDialog.isShowing()) {
            Window window = commentDialog.getWindow();
            commentDialog.show();
            window.setGravity(Gravity.BOTTOM);
        }
    }

    private void commitComment(@NonNull CommentDialog.CommentData data) {
        if (courseId > 0) {
            mPresenter.addComment(UserHelper.getUserId(), courseId, data.getContent());
        }
    }

    private void getVideoComments(boolean moreData) {
        if (!moreData) {
            pageIndex = 0;
        } else {
            pageIndex++;
        }
        getVideoComments(pageIndex, pageSize);
    }

    private void getVideoComments(int pageIndex, int pageSize) {
        if (courseId > 0) {
            mPresenter.requestComments(courseId, pageIndex, pageSize);
        }
    }

    @Override
    public void updateCommentsView(List<CommentVo> comments) {
        pullToRefreshView.onHeaderRefreshComplete();
        pullToRefreshView.setLoadMoreEnable(comments.size() >= AppConfig.PAGE_SIZE);
        videoCommentAdapter.replace(comments);

        if(EmptyUtil.isEmpty(comments)){
            // 数据为空
            recyclerView.setVisibility(View.GONE);
            courseEmptyView.setVisibility(View.VISIBLE);
        }else{
            // 数据不为空
            recyclerView.setVisibility(View.VISIBLE);
            courseEmptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public void addComment(boolean isSuccess) {
        if (isSuccess) {
            // 评论发送成功之后，更新视频详情页评论列表
            MessageEvent messageEvent = new MessageEvent(EventConstant.TRIGGER_VIDEO_DETAIL_COMMENTS_UPDATE);
            EventBus.getDefault().post(messageEvent);

            getVideoComments(false);
        }
    }
}
