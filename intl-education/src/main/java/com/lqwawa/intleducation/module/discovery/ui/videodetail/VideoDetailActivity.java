package com.lqwawa.intleducation.module.discovery.ui.videodetail;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.provider.Contacts;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lecloud.sdk.constant.PlayerEvent;
import com.lecloud.sdk.constant.PlayerParams;
import com.lecloud.sdk.surfaceview.ISurfaceView;
import com.lecloud.sdk.surfaceview.impl.BaseSurfaceView;
import com.lecloud.sdk.videoview.VideoViewListener;
import com.lecloud.skin.videoview.vod.UIVodVideoView;
import com.lqwawa.intleducation.MainApplication;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.CourseEmptyView;
import com.lqwawa.intleducation.base.PresenterActivity;
import com.lqwawa.intleducation.base.utils.DisplayUtil;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.base.widgets.ui.CommonContainerActivity;
import com.lqwawa.intleducation.common.ui.CommentDialog;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.KeyboardUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.common.utils.image.LQwawaImageUtil;
import com.lqwawa.intleducation.factory.data.entity.course.VideoResourceEntity;
import com.lqwawa.intleducation.factory.event.EventConstant;
import com.lqwawa.intleducation.module.discovery.ui.lesson.detail.ReadWeikeHelper;
import com.lqwawa.intleducation.module.discovery.ui.videodetail.videocomment.VideoCommentAdapter;
import com.lqwawa.intleducation.module.discovery.ui.videodetail.videocomment.VideoCommentListFragment;
import com.lqwawa.intleducation.module.discovery.vo.CommentVo;
import com.lqwawa.intleducation.module.learn.vo.SectionResListVo;
import com.lqwawa.intleducation.module.user.tool.UserHelper;
import com.lqwawa.lqbaselib.pojo.MessageEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author: wangchao
 * @date: 2019/05/07
 * @desc: 视频馆视频详情页面
 */
public class VideoDetailActivity extends PresenterActivity<VideoDetailContract.Presenter>
        implements VideoDetailContract.View {

    private RelativeLayout videoContainer;
    private ImageView backImageView;
    private ImageView playImageView;
    private ImageView thumbnailImageView;
    private UIVodVideoView uiVodVideoView;
    private TextView nameTextView;
    private TextView viewCountTextView;

    private RecyclerView resourceRecyclerView;
    private RecyclerView commentRecyclerView;
    private CourseEmptyView commentEmptyView;
    private EditText commentContentEditText;
    private TextView sendTextView;
    private LinearLayout commentTitleLayout;
    private View dividerLine;

    private CommentDialog commentDialog;
    private CommentDialog.CommentData commentData;

    private VideoResourceAdapter videoResourceAdapter;
    private VideoCommentAdapter videoCommentAdapter;
    private ReadWeikeHelper readWeikeHelper;

    private SectionResListVo sectionResListVo;
    private Long chapterId;
    private Long courseId;
    private boolean isMp4;
    private long currentPlayPosition = 0;
    private boolean isFirstIn = true;
    private int pageIndex = 0;
    private int pageSize = 3;

    public static void start(Context context, SectionResListVo sectionResListVo) {
        Intent starter = new Intent(context, VideoDetailActivity.class);
        starter.putExtra(SectionResListVo.class.getSimpleName(), sectionResListVo);
        context.startActivity(starter);
    }

    @Override
    protected VideoDetailPresenter initPresenter() {
        return new VideoDetailPresenter(this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_video_detail;
    }

    @Override
    protected boolean initArgs(@NonNull Bundle bundle) {
        sectionResListVo =
                (SectionResListVo) bundle.getSerializable(SectionResListVo.class.getSimpleName());
        if (sectionResListVo == null) {
            return false;
        }
        String chapterId = sectionResListVo.getChapterId();
        if (!TextUtils.isEmpty(chapterId) && TextUtils.isDigitsOnly(chapterId)) {
            this.chapterId = Long.parseLong(chapterId);
        }

        String courseId = sectionResListVo.getResId();
        if (!TextUtils.isEmpty(courseId) && TextUtils.isDigitsOnly(courseId)) {
            this.courseId = Long.parseLong(courseId);
        }

        return super.initArgs(bundle);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        initTopViews();
        initResourceViews();
        initCommentViews();
    }

    @Override
    protected void initData() {
        super.initData();
        getVideoResources();
        getVideoComments();
        readWeikeHelper = new ReadWeikeHelper(this);
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }
    }

    private void initTopViews() {
        backImageView = (ImageView) findViewById(R.id.iv_video_detail_back);
        playImageView = (ImageView) findViewById(R.id.iv_video_detail_play);
        thumbnailImageView = (ImageView) findViewById(R.id.iv_video_detail_thumbnail);
        videoContainer = (RelativeLayout) findViewById(R.id.fl_video_detail_video_container);
        nameTextView = (TextView) findViewById(R.id.tv_video_detail_name);
        viewCountTextView = (TextView) findViewById(R.id.tv_video_detail_view_count);

        backImageView.setOnClickListener(view -> {
            KeyboardUtil.hideSoftInput(this);
            finish();
        });
        playImageView.setOnClickListener(view -> {
            uiVodVideoView.setVisibility(View.VISIBLE);
            startPlayResources();
        });

        int height = DisplayUtil.getMobileWidth(this) * 9 / 16;
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) thumbnailImageView.getLayoutParams();
        layoutParams.height = height;
        thumbnailImageView.setLayoutParams(layoutParams);
        uiVodVideoView = new UIVodVideoView(this, false, true);
        uiVodVideoView.setVideoViewListener(new VideoViewListener() {
            @Override
            public void onStateResult(int event, Bundle bundle) {
                handlePlayerEvent(event, bundle);
            }

            @Override
            public String onGetVideoRateList(LinkedHashMap<String, String> linkedHashMap) {
                return null;
            }
        });
        videoContainer.addView(uiVodVideoView, computeContainerSize(this, 16, 9));
        uiVodVideoView.setVisibility(View.GONE);

        if (sectionResListVo != null) {
            LQwawaImageUtil.loadCourseThumbnail(this, thumbnailImageView, sectionResListVo.getThumbnail());
            nameTextView.setText(sectionResListVo.getName());
            viewCountTextView.setText(getString(R.string.some_study,
                    String.valueOf(sectionResListVo.getViewCount())));
        }
    }

    private void initResourceViews() {
        resourceRecyclerView = (RecyclerView) findViewById(R.id.rv_video_detail_resources);
        resourceRecyclerView.setNestedScrollingEnabled(false);
        DividerItemDecoration divider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(this, R.drawable.divider_line_h_1dp));
        resourceRecyclerView.addItemDecoration(divider);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        resourceRecyclerView.setLayoutManager(mLayoutManager);

        videoResourceAdapter = new VideoResourceAdapter(this);
        resourceRecyclerView.setAdapter(videoResourceAdapter);
        videoResourceAdapter.setListener(new RecyclerAdapter.AdapterListenerImpl<VideoResourceEntity>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, VideoResourceEntity entity) {
                if (entity != null) {
                    readWeikeHelper.readWeike(entity.buildSectionResListVo());
                }
            }
        });
    }

    private void initCommentViews() {
        dividerLine = findViewById(R.id.video_detail_divider_line);
        commentEmptyView = (CourseEmptyView) findViewById(R.id.empty_layout);
        commentRecyclerView = (RecyclerView) findViewById(R.id.rv_video_detail_comments);
        commentRecyclerView.setNestedScrollingEnabled(false);
        DividerItemDecoration divider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(this, R.drawable.divider_line_h_1dp));
        commentRecyclerView.addItemDecoration(divider);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        commentRecyclerView.setLayoutManager(mLayoutManager);
        videoCommentAdapter = new VideoCommentAdapter();
        commentRecyclerView.setAdapter(videoCommentAdapter);

        commentContentEditText = (EditText) findViewById(R.id.et_comment_content);
        sendTextView = (TextView) findViewById(R.id.btn_send);
        sendTextView.setOnClickListener(view -> commitComment(commentData));
        // 点击文本框,显示评论对话框
        commentContentEditText.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                final CommentDialog.CommentData data = commentData;
                triggerComment(data);
                return true;
            }
            return false;
        });

        commentTitleLayout = (LinearLayout) findViewById(R.id.ll_video_detail_comment_title_layout);
        commentTitleLayout.setOnClickListener(view -> {
            Bundle args = new Bundle();
            args.putLong("courseId", courseId);
            CommonContainerActivity.show(this, getString(R.string.user_comment),
                    VideoCommentListFragment.class, args);
        });

        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) commentEmptyView.getLayoutParams();
        layoutParams.height = DisplayUtil.dip2px(UIUtil.getContext(), 200);
        commentEmptyView.setLayoutParams(layoutParams);
    }

    private RelativeLayout.LayoutParams computeContainerSize(Context context, int widthRatio,
                                                             int heightRatio) {
        int width = DisplayUtil.getMobileWidth(context);
        int height = width * heightRatio / widthRatio;
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout
                .LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        params.width = width;
        params.height = height;
        return params;
    }

    private void handlePlayerEvent(int state, Bundle bundle) {
        switch (state) {
            //准备开始播放
            case PlayerEvent.PLAY_PREPARED:
                if (uiVodVideoView != null && MainApplication.getCdeInitSuccess()) {
                    uiVodVideoView.onStart();
                }
                break;
            case PlayerEvent.ACTION_LIVE_PLAY_PROTOCOL:
                setActionLiveParameter(bundle.getBoolean(PlayerParams.KEY_PLAY_USEHLS));
                break;
            case PlayerEvent.PLAY_VIDEOSIZE_CHANGED:
                if (uiVodVideoView != null && uiVodVideoView instanceof UIVodVideoView) {
                    ISurfaceView surfaceView = uiVodVideoView.getSurfaceView();
                    ((BaseSurfaceView) surfaceView).setDisplayMode(BaseSurfaceView.DISPLAY_MODE_SCALE_ZOOM);
                    int width = bundle.getInt(PlayerParams.KEY_WIDTH);
                    int height = bundle.getInt(PlayerParams.KEY_HEIGHT);
                    ((BaseSurfaceView) surfaceView).onVideoSizeChanged(width, height);
                }
                break;
            default:
                break;
        }
    }

    private void setActionLiveParameter(boolean hls) {
        if (hls) {
            uiVodVideoView.setCacheWatermark(1000, 100);
            uiVodVideoView.setMaxDelayTime(50000);
            uiVodVideoView.setCachePreSize(1000);
            uiVodVideoView.setCacheMaxSize(40000);
        } else {
            //rtmp
            uiVodVideoView.setCacheWatermark(500, 100);
            uiVodVideoView.setMaxDelayTime(1000);
            uiVodVideoView.setCachePreSize(200);
            uiVodVideoView.setCacheMaxSize(10000);
        }
    }

    /**
     * 开始播放视频资源
     */
    private void startPlayResources() {
        String vUid = sectionResListVo.getVuid();
        if (TextUtils.isEmpty(vUid) || sectionResListVo.getLeStatus() != 3) {
            isMp4 = true;
            String playPath = sectionResListVo.getResourceUrl();
            uiVodVideoView.setDataSource(playPath);
            uiVodVideoView.onStart();
        } else {
            isMp4 = false;
            String uuid = "b68e945493";
            String vuid = vUid;
            String p = "";
            boolean isSaas = true;
            Bundle mBundle = new Bundle();
            mBundle.putInt(PlayerParams.KEY_PLAY_MODE, PlayerParams.VALUE_PLAYER_VOD);
            mBundle.putString(PlayerParams.KEY_PLAY_UUID, uuid);
            mBundle.putString(PlayerParams.KEY_PLAY_VUID, vuid);
            mBundle.putString(PlayerParams.KEY_PLAY_BUSINESSLINE, p);
            mBundle.putBoolean("saas", isSaas);
            uiVodVideoView.setDataSource(mBundle);
        }
        if (uiVodVideoView != null) {
            //设置视频全屏播放的标题
            uiVodVideoView.setTitle(sectionResListVo.getName());
        }
    }

    /**
     * 评论弹窗
     *
     * @param data 评论数据
     */
    private void triggerComment(@NonNull CommentDialog.CommentData data) {
        int commentType = CommentDialog.TYPE_COMMENT_LOW_PERMISSION;
        CommentDialog dialog = new CommentDialog(this, 0, commentType, true, data,
                new CommentDialog.CommitCallBack() {
                    @Override
                    public void dismiss(CommentDialog.CommentData commentData) {
                        // 更新文本到输入框中
                        if (EmptyUtil.isNotEmpty(commentData)) {
                            VideoDetailActivity.this.commentData = commentData;
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

    private void getVideoResources() {
        if (chapterId > 0) {
            mPresenter.requestResources(chapterId);
        }
    }

    private void getVideoComments() {
        if (courseId > 0) {
            mPresenter.requestComments(courseId, pageIndex, pageSize);
        }
    }

    /**
     * 配置当屏幕进行旋转的时候video进行相应的变化
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (uiVodVideoView != null) {
            if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                backImageView.setVisibility(View.VISIBLE);
                thumbnailImageView.setVisibility(View.VISIBLE);
                playImageView.setVisibility(View.VISIBLE);
            } else {
                backImageView.setVisibility(View.GONE);
                thumbnailImageView.setVisibility(View.GONE);
                playImageView.setVisibility(View.GONE);
            }
            uiVodVideoView.onConfigurationChanged(newConfig);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (uiVodVideoView != null && !uiVodVideoView.isPlaying()) {
            if (!isMp4) {
                uiVodVideoView.onResume();
            } else {
                if (!isFirstIn) {
                    uiVodVideoView.seekTo(currentPlayPosition);
                    uiVodVideoView.onResume();
                    if (uiVodVideoView != null) {
                        uiVodVideoView.onClickContinuePlay();
                    }
                }
                isFirstIn = false;
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (uiVodVideoView != null) {
            if (isMp4) {
                currentPlayPosition = uiVodVideoView.getCurrentPosition();
            }
            uiVodVideoView.onPause();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (uiVodVideoView != null) {
            uiVodVideoView.stopAndRelease();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (uiVodVideoView != null) {
            uiVodVideoView.onDestroy();
        }
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(@NonNull MessageEvent event){
        String action = event.getUpdateAction();
        if(TextUtils.equals(action, EventConstant.TRIGGER_VIDEO_DETAIL_COMMENTS_UPDATE)){
            getVideoComments();
        }
    }

    @Override
    public void updateResourcesView(List<VideoResourceEntity> entities) {
        boolean isEmpty = entities == null || entities.isEmpty();
        dividerLine.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        videoResourceAdapter.replace(entities);
    }

    @Override
    public void updateCommentsView(List<CommentVo> comments) {
        videoCommentAdapter.replace(comments);
        if (comments == null || comments.isEmpty()) {
            commentRecyclerView.setVisibility(View.GONE);
            commentEmptyView.setVisibility(View.VISIBLE);
        } else {
            commentRecyclerView.setVisibility(View.VISIBLE);
            commentEmptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public void addComment(boolean isSuccess) {
        if (isSuccess) {
            getVideoComments();
        }
    }
}
