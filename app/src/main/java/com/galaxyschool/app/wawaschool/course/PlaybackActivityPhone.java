package com.galaxyschool.app.wawaschool.course;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.util.ArrayMap;
import android.text.Layout;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.Request;
import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.chat.DemoApplication;
import com.galaxyschool.app.wawaschool.chat.library.ConversationHelper;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.CallbackListener;
import com.galaxyschool.app.wawaschool.common.Common;
import com.galaxyschool.app.wawaschool.common.DateUtils;
import com.galaxyschool.app.wawaschool.common.DensityUtils;
import com.galaxyschool.app.wawaschool.common.DialogHelper;
import com.galaxyschool.app.wawaschool.common.MessageEventConstantUtils;
import com.galaxyschool.app.wawaschool.common.ShareUtils;
import com.galaxyschool.app.wawaschool.common.StudyInfoRecordUtil;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.common.UIUtils;
import com.galaxyschool.app.wawaschool.common.UploadUtils;
import com.galaxyschool.app.wawaschool.common.Utils;
import com.galaxyschool.app.wawaschool.common.WawaCourseUtils;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.config.VipConfig;
import com.galaxyschool.app.wawaschool.course.DownloadAttachTask.DATParam;
import com.galaxyschool.app.wawaschool.course.DownloadAttachTask.DownloadChwCallbackParam;
import com.galaxyschool.app.wawaschool.course.DownloadAttachTask.DownloadDtoBase;
import com.galaxyschool.app.wawaschool.fragment.CompletedHomeworkListFragment;
import com.galaxyschool.app.wawaschool.fragment.MediaListFragment;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.helper.ApplyMarkHelper;
import com.galaxyschool.app.wawaschool.pojo.ExerciseAnswerCardParam;
import com.galaxyschool.app.wawaschool.pojo.ExerciseItem;
import com.galaxyschool.app.wawaschool.pojo.LearnTaskInfo;
import com.galaxyschool.app.wawaschool.views.AnswerCardPopWindow;
import com.lqwawa.intleducation.module.tutorial.marking.choice.QuestionResourceModel;
import com.lqwawa.intleducation.module.tutorial.marking.choice.TutorChoiceActivity;
import com.lqwawa.intleducation.module.tutorial.marking.choice.TutorChoiceParams;
import com.lqwawa.lqbaselib.common.DoubleOperationUtil;
import com.lqwawa.lqbaselib.net.ThisStringRequest;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.CourseInfo;
import com.galaxyschool.app.wawaschool.pojo.ResType;
import com.galaxyschool.app.wawaschool.pojo.ResourceType;
import com.galaxyschool.app.wawaschool.pojo.RoleType;
import com.galaxyschool.app.wawaschool.pojo.TaskMarkParam;
import com.galaxyschool.app.wawaschool.pojo.UploadParameter;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.galaxyschool.app.wawaschool.pojo.UserInfoResult;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseData;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseSourceType;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseUploadResult;
import com.galaxyschool.app.wawaschool.pojo.weike.PlaybackParam;
import com.galaxyschool.app.wawaschool.pojo.weike.SplitCourseInfo;
import com.galaxyschool.app.wawaschool.pojo.weike.SplitCourseInfoListResult;
import com.galaxyschool.app.wawaschool.views.ContactsMessageDialog;
import com.galaxyschool.app.wawaschool.views.MarkScoreDialog;
import com.galaxyschool.app.wawaschool.views.PopupMenu;
import com.galaxyschool.app.wawaschool.views.ShareTypeSelectDialog;
import com.hyphenate.EMCallBack;
import com.lqwawa.client.pojo.MediaType;
import com.lqwawa.lqbaselib.pojo.MessageEvent;
import com.lqwawa.tools.FileZipHelper;
import com.lqwawa.tools.FileZipHelper.ZipUnzipFileListener;
import com.lqwawa.tools.FileZipHelper.ZipUnzipParam;
import com.lqwawa.tools.FileZipHelper.ZipUnzipResult;
import com.oosic.apps.aidl.CollectParams;
import com.oosic.apps.iemaker.base.BaseSlideManager;
import com.oosic.apps.iemaker.base.BaseUtils;
import com.oosic.apps.iemaker.base.PlaybackActivity;
import com.oosic.apps.iemaker.base.data.CourseShareData;
import com.oosic.apps.iemaker.base.playback.Playback;
import com.oosic.apps.share.ShareInfo;
import com.oosic.apps.share.ShareSettings;
import com.oosic.apps.share.SharedResource;
import com.umeng.socialize.media.UMImage;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pp on 16/1/21.
 */
public class PlaybackActivityPhone extends PlaybackActivityNew implements
        PlaybackActivity.PlaybackActivityHandler, OnClickListener, CourseSourceType {

    private final int POP_WINDOW_WIDTH = 200;
    public final int PLAY_TIMEOUT_MS = 15000;
    protected PlaybackParam mParam;
    protected TaskMarkParam taskMarkParam;
    protected ExerciseAnswerCardParam cardParam;
    private UserInfo userInfo;
    private LinearLayout menuLayout;
    private TextView markView;
    private TextView answerBtn;
    private boolean isPraise;
    private boolean mInEditMode;
    private String mUrl;
    private int pageIndex, pageCount;
    private List<SplitCourseInfo> splitCourseInfos = null;
    private boolean isPublicRes = true;
    private long recordTime;
    private AnswerCardPopWindow answerCardPopWindow;
    protected boolean isAnswerCardQuestion;
    private List<ExerciseItem> exerciseItems;
    protected boolean applyMark;//申请批阅
    protected Handler handler = new Handler();

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            handler.removeCallbacks(runnable);
            // 暂停所有播放动作
            stopPlaying();
            ContactsMessageDialog dialog = new ContactsMessageDialog(PlaybackActivityPhone.this,
                    null, getString(R.string.learn_more_course),
                    getString(R.string.i_see),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            if (!isFinishing()) {
                                finish();
                            }
                        }
                    }, "", null);
            dialog.setCancelable(false);
            dialog.show();
        }
    };
    private AlertDialog mCommitDialog;
    private MarkScoreDialog mMarkScoreDialog;

    @Override
    public void addButtonToAttachedBarHandler(LinearLayout attachedBar) {
        menuLayout = attachedBar;
        // 课件是受保护的，隐藏侧边工具栏, 允许试看15s
        if (mParam != null && mParam.mIsAuth) {
            attachedBar.setVisibility(View.GONE);
            handler.postDelayed(runnable, PLAY_TIMEOUT_MS);
            return;
        }
        // 隐藏工具栏
        if (mParam.mIsHideToolBar) {
            attachedBar.setVisibility(View.GONE);
            return;
        }

        addMenuOrView(attachedBar);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getIntent() != null) {
            mParam = (PlaybackParam) getIntent().getSerializableExtra(
                    PlaybackParam.class.getSimpleName());
            if (mParam != null && VipConfig.isVip(PlaybackActivityPhone.this)) {
                mParam.mIsAuth = false;
            }
            // 根据是否授权判断是否禁止任务单多类型打开
            if (mParam != null && mParam.mIsAuth) {
                setCourseNodeViewClickable(false);
            } else {
                setCourseNodeViewClickable(true);
            }
            if (mParam != null) {
                applyMark = mParam.applyMark;
                taskMarkParam = mParam.taskMarkParam;
                enableTextPointerFeature(true);
                setUserType(taskMarkParam != null ? taskMarkParam.roleType : 0);
                //任务单答题卡
                cardParam = mParam.exerciseCardParam;
                if (cardParam != null) {
                    isAnswerCardQuestion = cardParam.isShowExerciseButton();
                    //设置软键盘的弹出方式
                    if (isAnswerCardQuestion) {
                        //强制改成学生的身份
                        setUserType(RoleType.ROLE_TYPE_STUDENT);
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
                                | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                        //初始化答题卡的popWindow
                        new Handler().postDelayed(() -> initAnswerPopWindowData(), 100);
                    }
                } else if (applyMark) {
                    setUserType(RoleType.ROLE_TYPE_STUDENT);
                }
            }
        }
        super.onCreate(savedInstanceState);
        addRayMenuByParam();
        //根据条件隐藏老课件一进来的播放状态的收藏按钮
        if (mParam != null && mParam.mIsHideCollectTip) {
            if (mCollectBtn != null) {
                mCollectBtn.setVisibility(View.GONE);
            }
        }
        onSelfCreate();
        if (isAnswerCardQuestion || applyMark) {
            edit();
        }
        //根据条件判断是否需要记录观看的时间
        if (isNeedTimerRecorder()) {
            recordTime = System.currentTimeMillis();
            analysisCourseTotalTime();
        }
    }

    /**
     * 定制微课二次编辑工具栏
     */
    protected void addRayMenuByParam() {
        if (mSlideInPlaybackParam != null) {
            mSlideInPlaybackParam.mRayMenusV = new int[]{
                    BaseSlideManager.MENU_ID_CAMERA, BaseSlideManager.MENU_ID_IMAGE,
                    BaseSlideManager.MENU_ID_WHITEBOARD, BaseSlideManager.MENU_ID_PAGE_HORN_AUDIO,
                    BaseSlideManager.MENU_ID_PERSONAL_MATERIAL};

            if (taskMarkParam != null && (taskMarkParam.roleType == RoleType.ROLE_TYPE_EDITOR ||
                    taskMarkParam.roleType == RoleType.ROLE_TYPE_TEACHER)) {
                //老师显示校本资源库
                mSlideInPlaybackParam.mRayMenusV = Arrays.copyOf(mSlideInPlaybackParam.mRayMenusV, mSlideInPlaybackParam.mRayMenusV.length + 1);
                mSlideInPlaybackParam.mRayMenusV[mSlideInPlaybackParam.mRayMenusV.length - 1] = BaseSlideManager.MENU_ID_SCHOOL_MATERIAL;
            }

            if (mParam != null && !isAnswerCardQuestion) {
                //提问批阅时放出文本按钮
                mSlideInPlaybackParam.mRayMenusH = new int[]{
                        BaseSlideManager.MENU_ID_TEXT_POINTER, BaseSlideManager.MENU_ID_PAGE_HORN_RECORD,
                        BaseSlideManager.MENU_ID_CURVE, BaseSlideManager.MENU_ID_ERASER
                };
            } else {
                mSlideInPlaybackParam.mRayMenusH = new int[]{
                        BaseSlideManager.MENU_ID_PAGE_HORN_RECORD,
                        BaseSlideManager.MENU_ID_CURVE, BaseSlideManager.MENU_ID_ERASER
                };
            }
        }
    }

    protected void onSelfCreate() {
        mUrl = mPath;
        if (collectParams != null) {
            String editResourceUrl = collectParams.getEditResourceUrl();
            if (!TextUtils.isEmpty(editResourceUrl) && editResourceUrl.endsWith(".zip")) {
                mUrl = editResourceUrl.substring(0, editResourceUrl.lastIndexOf("."));
            }
            setNewPageAsSubpage(collectParams.resourceType != ResType.RES_TYPE_ONEPAGE);
        }
        if (isAnswerCardQuestion) {
            setNewPageAsSubpage(false);
        }
        userInfo = ((MyApplication) getApplication()).getUserInfo();
        boolean isMySelf = isMySelf();
        if (mParam == null) {
            mParam = new PlaybackParam();
        }
        mParam.mIsOwner = isMySelf;
        setupPlaybackActivityHandler(this);
        // if(!isMySelf) {
        // if(collectParams != null && !TextUtils.isEmpty(collectParams.author))
        // loadUserInfo(collectParams.author);
        // }
        if (mSlideInPlayback != null) {
            pageCount = mSlideInPlayback.getPageCount();
            if (pageCount > 1 && collectParams != null && !TextUtils.isEmpty(collectParams.microId)) {
                loadSplitCourseList(collectParams.microId);
            }
        }

        //获取是否需要加密分享的数据
        if (originShareData != null) {
            isPublicRes = originShareData.getSharedResource().isPublicRescourse();
        }
    }

    /**
     * 添加菜单栏或者批阅按钮
     *
     * @param attachedBar
     */
    protected void addMenuOrView(LinearLayout attachedBar) {
        if (taskMarkParam != null) {
            addMarkView(attachedBar);
        } else {
            attachedBar.setVisibility(bShowSlide ? View.VISIBLE : View.GONE);
            addMenu(attachedBar, mParam != null && mParam.mIsOwner);
        }
    }

    /**
     * 添加批阅按钮
     *
     * @param attachedBar
     */
    protected void addMarkView(LinearLayout attachedBar) {
        // 竖屏显示工具栏，横屏不显示
        boolean isPortrait = mOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        attachedBar.setVisibility(isPortrait ? View.VISIBLE : View.GONE);
        if (isPortrait) {
            markView = addView(attachedBar);
        } else {
            markView = mMarkBtn;
            changeMarkViewSize();
        }
        updateMarkView(taskMarkParam, markView);
        OnClickListener listener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAnswerCardQuestion || applyMark) {
                    showCommitAnswerDialog(false);
                } else if (!mInEditMode) {
                    edit();
                } else {
                    onBackPressed();
                }
            }
        };
        if (markView != null) {
            markView.setOnClickListener(listener);
        }
        if (mSaveBtn != null) {
            mSaveBtn.setOnClickListener(listener);
        }
    }

    /**
     * 根据不同的状态更新批阅按钮
     *
     * @param param
     * @param markView
     */

    private void updateMarkView(TaskMarkParam param, TextView markView) {
        if (isAnswerCardQuestion || applyMark) {
            updateCommitView(markView);
            return;
        }
        if (param == null || markView == null) {
            return;
        }
        markView.setVisibility(mInEditMode ? View.GONE : View.VISIBLE);
        mSaveBtn.setVisibility(mInEditMode ? View.VISIBLE : View.GONE);
        if (mInEditMode) {
//            markView.setText(R.string.commit);
        } else {
            if (isTeacherOrEditor()) {
                markView.setText(R.string.read_over);
//                markView.setVisibility(isMySelf() ? View.GONE : View.VISIBLE);
                markView.setVisibility(param.isVisitor ? View.GONE : View.VISIBLE);
            } else if (param.roleType == RoleType.ROLE_TYPE_STUDENT) {
                markView.setText(R.string.ask_question);
//                markView.setVisibility(!param.isMarked ? View.GONE : View.VISIBLE);
                markView.setVisibility(param.isVisitor ? View.GONE : View.VISIBLE);

            } else {
                markView.setVisibility(View.GONE);
            }
        }
    }

    private void updateCommitView(TextView commitView) {
//        if (commitView != null) {
//            commitView.setVisibility(View.VISIBLE);
//            commitView.setText(getString(R.string.commit));
//        }
        if (mSaveBtn != null) {
            mSaveBtn.setVisibility(mInEditMode ? View.VISIBLE : View.GONE);
        }
    }

    protected TextView addView(LinearLayout attachedBar) {
        RelativeLayout layout = new RelativeLayout(this);
        TextView textView = new TextView(this);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        layoutParams.rightMargin = 20;
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        textView.setGravity(Gravity.CENTER_VERTICAL);
        textView.setTextColor(getResources().getColor(R.color.white));
        layout.addView(textView, layoutParams);

        LayoutParams lParams = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        lParams.gravity = Gravity.CENTER;
        lParams.weight = 1.0f;
        if (!isAnswerCardQuestion || applyMark) {
            //防止listener消费子类的事件
            layout.setOnClickListener(this);
        }
        attachedBar.addView(layout, lParams);

        return textView;
    }

    private void changeMarkViewSize() {
        if (markView != null) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) markView.getLayoutParams();
            layoutParams.width = LayoutParams.WRAP_CONTENT;
            layoutParams.height = LayoutParams.WRAP_CONTENT;
            layoutParams.gravity = Gravity.CENTER_VERTICAL | Gravity.END;
            layoutParams.rightMargin = 10;
            markView.setLayoutParams(layoutParams);
        }
    }

    // shouyi add
    protected void addMenu(LinearLayout attachedBar, int name, int icon) {
        RelativeLayout layout = new RelativeLayout(this);
        layout.setId(name);
//        TextView textView = new TextView(this);
//        textView.setText(name);
//        try {
//            Drawable drawable = getResources().getDrawable(icon);
//            drawable.setBounds(0, 0, drawable.getMinimumWidth(),
//                drawable.getMinimumHeight());
//            textView.setCompoundDrawables(null, drawable, null, null);
//        } catch (NotFoundException e) {
//            // TODO: handle exception
//        }
//        textView.setGravity(Gravity.CENTER);
//        textView.setTextColor(getResources().getColor(R.color.white));
        ImageView imageView = new ImageView(this);
        imageView.setImageResource(icon);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        layout.addView(imageView, layoutParams);

        LayoutParams lParams = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        lParams.gravity = Gravity.CENTER;
        lParams.weight = 1.0f;
        layout.setOnClickListener(this);
        attachedBar.addView(layout, lParams);
    }

    private void addMenu(LinearLayout attachedBar, boolean isOwner) {
        if (attachedBar == null) {
            return;
        }
        addMenu(attachedBar, R.string.playbackphone_menu_show, R.drawable.menu_icon_horn);
        //根据条件判断是否显示收藏的按钮
        boolean flag = (mParam != null && mParam.mIsHideCollectTip);
        if (!flag) {
            addMenu(attachedBar, R.string.playbackphone_menu_collect,
                    R.drawable.menu_icon_collect);
        }
        addMenu(attachedBar, R.string.playbackphone_menu_share,
                R.drawable.menu_icon_share);
//        if(collectParams != null && !collectParams.isChatForbidden) {
//            if (!isOwner) {
//                if (collectParams != null && ((collectParams.courseSourceType == BOOKSHELF
//                 && TextUtils.isEmpty(collectParams.editMicroId))|| collectParams.courseSourceType == CHAT)) {
//                    addMenu(attachedBar, R.string.playbackphone_menu_edit,
//                        R.drawable.menu_icon_edit);
//                } else {
//                    addMenu(attachedBar, R.string.playbackphone_menu_attention,
//                        R.drawable.menu_icon_attention);
//                }
//            } else {
//                addMenu(attachedBar, R.string.playbackphone_menu_edit,
//                    R.drawable.menu_icon_edit);
//            }
//        }
        if (!isOwner) {
            addMenu(attachedBar, R.string.playbackphone_menu_attention,
                    R.drawable.menu_icon_attention);
        }
        if (!isSplitCourse) {
            addMenu(attachedBar, R.string.playbackphone_menu_more, R.drawable.menu_icon_more);
        }
    }

    private boolean isMySelf() {
        if (userInfo != null && collectParams != null
                && !TextUtils.isEmpty(userInfo.getMemberId())
                && !TextUtils.isEmpty(collectParams.author)
                && userInfo.getMemberId().equals(collectParams.author)) {
            return true;
        }
        return false;
    }

    private void loadSplitCourseList(String courseId) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("pid", courseId);
        StringBuilder builder = new StringBuilder();
        builder.append("?j=" + jsonObject.toJSONString());
        String url = ServerUrl.SPLIT_COURSE_LIST_URL + builder.toString();
        final ThisStringRequest request = new ThisStringRequest(
                Request.Method.GET, url, new Listener<String>() {
            @Override
            public void onSuccess(String jsonString) {
                if (jsonString != null) {
                    SplitCourseInfoListResult result = JSON.parseObject(jsonString, SplitCourseInfoListResult.class);
                    if (result != null && result.getData() == null) {
                        TipsHelper.showToast(PlaybackActivityPhone.this,
                                R.string.please_retry_for_resource);
                        return;
                    }
                    splitCourseInfos = result.getData();
                    shareData = getShareData();
                    if (shareData != null) {
                        shareCourse(shareData);
                    } else {
                        TipsHelper.showToast(PlaybackActivityPhone.this,
                                R.string.please_retry_for_resource);
                    }
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                dismissLoadingDialog();
            }
        });
        request.addHeader("Accept-Encoding", "*");
        request.start(PlaybackActivityPhone.this);
        showLoadingDialog();
    }

    private void loadUserInfo(String userId) {
        Map<String, Object> params = new HashMap();
        params.put("MemberId", userInfo.getMemberId());
        params.put("UserId", userId);
        RequestHelper.sendPostRequest(PlaybackActivityPhone.this,
                ServerUrl.LOAD_USERINFO_URL, params, new Listener<String>() {
                    @Override
                    public void onSuccess(String jsonString) {
                        UserInfoResult result = JSON.parseObject(jsonString,
                                UserInfoResult.class);
                        if (result == null || !result.isSuccess()) {
                            return;
                        }
                        UserInfo otherUserInfo = result.getModel();
                        if (otherUserInfo != null && otherUserInfo.isFriend()) {
                            menuLayout.getChildAt(0).setVisibility(View.GONE);
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        processMenuClick(v.getId());
    }

    private void processMenuClick(int menuId) {
        switch (menuId) {
            case R.string.playbackphone_menu_show:
                showHorn();
                break;
            case R.string.playbackphone_menu_attention:
                followMe();
                break;
            case R.string.playbackphone_menu_praise:
                praise();
                break;
            case R.string.playbackphone_menu_comments:
                comment();
                break;
            case R.string.playbackphone_menu_share:
                share();
                break;
            case R.string.playbackphone_menu_collect:
                collect();
                break;
            case R.string.playbackphone_menu_edit:
                edit();
                break;
            case R.string.playbackphone_menu_complete:
                onBackPressed();
                break;
            case R.string.playbackphone_menu_more:
                showMoreMenu(menuLayout.findViewById(R.string.playbackphone_menu_more));
                break;
            case R.id.tv_btn_left:
                //放弃
                mCommitDialog.dismiss();
                mSlideInPlayback.resetEditedState();
                exit(true);
                break;
            case R.id.tv_btn_middle:
                //确认
                mCommitDialog.dismiss();
                commitCourse(null);
                break;
            case R.id.tv_btn_right:
                //重新打分
                mCommitDialog.dismiss();
                showMarkScoreDialog();
                break;

            default:
                break;
        }
    }

    private void showHorn() {
        if (mSlideInPlayback != null) {
            mSlideInPlayback.showHornSelectShowDialog();
        }
    }

    private void followMe() {
        if (collectParams == null
                || TextUtils.isEmpty(collectParams.getAuthor())) {
            return;
        }
        if (userInfo == null || TextUtils.isEmpty(userInfo.getMemberId())) {
            ActivityUtils.enterLogin(PlaybackActivityPhone.this);
            return;
        }
        Map<String, Object> params = new HashMap();
        params.put("FAttentionId", userInfo.getMemberId());
        params.put("TAttentionId", collectParams.getAuthor());
        RequestHelper.RequestDataResultListener<DataModelResult> listener = new RequestHelper.RequestDataResultListener<DataModelResult>(
                PlaybackActivityPhone.this, DataModelResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                super.onSuccess(jsonString);
                if (getResult() != null && getResult().isSuccess()) {
                    TipsHelper.showToast(PlaybackActivityPhone.this,
                            R.string.subscribe_success);
                }
            }
        };
        String serverUrl = ServerUrl.SUBSCRIBE_ADD_PERSON_URL;
        RequestHelper.sendPostRequest(PlaybackActivityPhone.this, serverUrl,
                params, listener);
    }

    private void praise() {
        if (userInfo == null || TextUtils.isEmpty(userInfo.getMemberId())) {
            TipMsgHelper.ShowMsg(this, getString(R.string.pls_login));
            return;
        }

        if (collectParams == null) {
            return;
        }
        if (isPraise) {
            TipMsgHelper.ShowLMsg(PlaybackActivityPhone.this,
                    R.string.have_praised);
            return;
        } else {
            isPraise = true;
        }
//        try {
//            courseAction.praiseCourse(collectParams);
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
        praiseCourse(collectParams);
    }

    private void comment() {
        CourseInfo courseInfo = new CourseInfo();
        if (collectParams != null) {
            courseInfo.setId(Integer.parseInt(collectParams.getMicroId()));
            courseInfo.setResourceType(ResourceType.VIDEO);
            courseInfo.setCommentnum(0);
            ActivityUtils.openCommentList(PlaybackActivityPhone.this,
                    courseInfo, mOrientation);
        }
    }

    private void share() {
        if (collectParams != null) {
            if (!isSplitCourse && collectParams.getResourceType() == ResType.RES_TYPE_COURSE_SPEAKER) {
                if (mSlideInPlayback != null) {
                    pageCount = mSlideInPlayback.getPageCount();
                    if (pageCount > 1 && !isSplitCourse) {
                        judgeNeedSplitStatus();
                    } else {
                        shareWholeCourse();
                    }
                }
            } else {
                shareWholeCourse();
            }
        }
    }

    /**
     * 分享整个课件
     */
    private void shareWholeCourse() {
        shareData = originShareData;
        shareCourse(shareData);
    }

    private void shareCourse(CourseShareData shareData) {
        if (shareData == null) {
            return;
        }
        ShareUtils shareUtils = new ShareUtils(PlaybackActivityPhone.this);
        ShareInfo shareInfo = getShareInfo(shareData);
        if (shareInfo != null) {
            shareUtils.share(rootView, shareInfo);
        }

    }

    @Override
    protected void shareCourse() {
        shareData = originShareData;
        shareCourse(shareData);
    }

    /**
     * 判断当前的录音课件是否需要拆分
     */
    private void judgeNeedSplitStatus() {
        WawaCourseUtils utils = new WawaCourseUtils(this);
        utils.loadCourseDetail(collectParams.getMicroId(), true);
        utils.setOnCourseDetailFinishListener(new WawaCourseUtils.OnCourseDetailFinishListener() {
            @Override
            public void onCourseDetailFinish(CourseData courseData) {
                if (courseData != null) {
                    if (courseData.getSplitFlag() == 0) {
                        //不需要拆分的
                        shareWholeCourse();
                    } else {
                        //需要拆分
                        showShareModeDialog();
                    }
                }
            }
        });
    }

    private void collect() {
//        if (courseAction != null) {
//            try {
//                courseAction.collectCourse(collectParams);
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
        collectCourse(collectParams);
    }

    private CourseShareData getShareData() {
        CourseShareData shareData = null;
        for (int i = 0, size = splitCourseInfos.size(); i < size; i++) {
            SplitCourseInfo info = splitCourseInfos.get(i);
            if (info != null && pageIndex >= 0 && pageIndex == i) {
                CourseInfo courseInfo = info.getCourseInfo();
                if (courseInfo != null) {
                    //给分享的数据加密
                    courseInfo.setIsPublicRescourse(isPublicRes);
                    shareData = courseInfo.toCourseShareData();
                }
            }
        }

        return shareData;
    }

    private void showShareModeDialog() {
        ShareTypeSelectDialog dialog = new ShareTypeSelectDialog(PlaybackActivityPhone.this, new ShareTypeSelectDialog.ShareTypeSelectHandler() {
            @Override
            public void shareTypeSelect(int type) {
                if (type == 0) {
                    if (mSlideInPlayback != null) {
                        pageIndex = mSlideInPlayback.getCurPageMasterIndex() - 1; //pageIndex begin with "1"
                    }
                    if (splitCourseInfos == null) {
                        if (collectParams != null && !TextUtils.isEmpty(collectParams.microId)) {
                            loadSplitCourseList(collectParams.microId);
                        }
                    } else {
                        shareData = getShareData();
                        if (shareData != null) {
                            shareCourse(shareData);
                        } else {
                            TipsHelper.showToast(PlaybackActivityPhone.this,
                                    R.string.please_retry_for_resource);
                        }
                    }
                } else if (type == 1) {
                    shareData = originShareData;
                    shareCourse(shareData);
                }
            }
        });
        dialog.show();
        Window window = dialog.getWindow();
        Display d = getWindowManager().getDefaultDisplay();
        WindowManager.LayoutParams p = window.getAttributes();
        window.setGravity(Gravity.CENTER);
        p.width = (int) (d.getWidth() * 0.75f);
        window.setAttributes(p);
    }


    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        if (isFinishing()) {
            return;
        }
        if (isAnswerCardQuestion || applyMark) {
            showCommitAnswerDialog(true);
        } else {
            processBackPressed();
        }
    }

    private void processBackPressed() {
        if (mInEditMode) {
            if (taskMarkParam != null) {
                //
                if (mPlayback != null && mPlayback.getPlayerStatus() != Playback.STATUS_STOP) {
                    super.onBackPressed();
                    return;
                }
                showMarkDialog();
            } else {
                if (!mSlideInPlayback.isEdited()) {
                    if (mPlayback != null && mPlayback.getPlayerStatus() != Playback.STATUS_STOP) {
                        super.onBackPressed();
                    } else {
                        backFromEdit(false);
                    }
                } else {
                    showSaveDialog();
                }
            }
        } else {
            super.onBackPressed();
        }
    }

    private void showMarkDialog() {
        if (taskMarkParam != null) {
            boolean isTeacher = (isTeacherOrEditor());
            if (!taskMarkParam.isMarked && taskMarkParam.isNeedMark) {//作业未批阅并且需要打分
                if (!isTeacher) {
                    showCommitDialog(isTeacher);
                } else {
                    showMarkScoreDialog();
                }
            } else {
                showCommitDialog(isTeacher);
            }
        }
    }

    private void showCommitDialog(boolean isTeacher) {
        if (mCommitDialog != null) {
            if (!mCommitDialog.isShowing()) {
                mCommitDialog.show();
            }
            return;
        }
        View inflate = LayoutInflater.from(getApplicationContext()).inflate(R.layout.dialog_mark_score_commit, null);
        TextView message = (TextView) inflate.findViewById(R.id.tv_tip_message);
        TextView leftBtn = (TextView) inflate.findViewById(R.id.tv_btn_left);
        TextView middleBtn = (TextView) inflate.findViewById(R.id.tv_btn_middle);
        TextView rightBtn = (TextView) inflate.findViewById(R.id.tv_btn_right);
        message.setText(R.string.commit_or_mot);
        leftBtn.setText(R.string.discard);
        middleBtn.setText(R.string.ok);
        rightBtn.setText(R.string.str_recalculate);
        //放弃
        leftBtn.setOnClickListener(this);
        //确认
        middleBtn.setOnClickListener(this);
        //重新打分
        rightBtn.setOnClickListener(this);

        if (isTeacher && !TextUtils.isEmpty(taskMarkParam.score)) {
            //老师身份 并且 分数不为空才显示重新打分
            rightBtn.setVisibility(View.VISIBLE);
        }

        mCommitDialog = new AlertDialog.Builder(PlaybackActivityPhone.this)
                .create();
        mCommitDialog.show();
        Window window = mCommitDialog.getWindow();
        if (window != null) {
            window.setContentView(inflate);
        }
    }

    protected void showCommitAnswerDialog(boolean isBackPress) {
        View inflate = LayoutInflater.from(getApplicationContext()).inflate(R.layout.dialog_mark_score_commit, null);
        TextView messageTextV = (TextView) inflate.findViewById(R.id.tv_tip_message);
        TextView leftBtn = (TextView) inflate.findViewById(R.id.tv_btn_left);
        TextView middleBtn = (TextView) inflate.findViewById(R.id.tv_btn_middle);
        String message = getString(R.string.commit_or_mot);
        String leftString = getString(R.string.discard);
        String rightString = getString(R.string.ok);
        boolean isFinishAllQuestion = false;
        if (!applyMark) {
            if (answerCardPopWindow != null) {
                isFinishAllQuestion = answerCardPopWindow.isFinishAllQuestion();
            }
            if (!isBackPress) {
                if (!isFinishAllQuestion) {
                    message = getString(R.string.str_unfinish_answer);
                    leftString = getString(R.string.str_continue_answer);
                    rightString = getString(R.string.commit);
                }
            }
        }
        messageTextV.setText(message);
        leftBtn.setText(leftString);
        middleBtn.setText(rightString);
        //放弃
        boolean finalIsFinishAllQuestion = isFinishAllQuestion;
        leftBtn.setOnClickListener(v -> {
            if (finalIsFinishAllQuestion || isBackPress || applyMark) {
                finish();
            } else {
                mCommitDialog.dismiss();
            }
        });
        //确认
        middleBtn.setOnClickListener(v -> {
            //确定提交
            mCommitDialog.dismiss();
            commitCourse(null);
        });
        mCommitDialog = new AlertDialog.Builder(PlaybackActivityPhone.this).create();
        mCommitDialog.show();
        Window window = mCommitDialog.getWindow();
        if (window != null) {
            window.setContentView(inflate);
        }
    }

    private void showMarkScoreDialog() {
        if (mMarkScoreDialog != null) {
            if (!mMarkScoreDialog.isShowing()) {
                mMarkScoreDialog.show();
            }
            return;
        }
        mMarkScoreDialog = new MarkScoreDialog(
                PlaybackActivityPhone.this,
                taskMarkParam.isPercent,
                getString(R.string.mark),
                taskMarkParam.score, null,
                getString(R.string.discard),
                null,
                getString(R.string.confirm),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String score = ((MarkScoreDialog) dialog).getScore();
                        if (TextUtils.isEmpty(score)) return;
                        if (taskMarkParam.isPercent) {
                            double scores = Double.valueOf(score);
                            if (taskMarkParam.cardParam != null) {
                                ExerciseItem itemData = taskMarkParam.cardParam.getExerciseItem();
                                if (itemData == null) {
                                    return;
                                }
                                double totalScore = Double.valueOf(itemData.getScore());
                                if (scores > totalScore) {
                                    TipMsgHelper.ShowLMsg(PlaybackActivityPhone.this, getString(R.string
                                            .str_single_question_total_score, Utils.changeDoubleToInt(String.valueOf(totalScore))));
                                    return;
                                }
                            } else {
                                if (scores > 100) {
                                    TipMsgHelper.ShowLMsg(getApplicationContext(), getString(R.string.str_tips_markscores));
                                    return;
                                }
                            }
                            score = Utils.changeDoubleToInt(String.valueOf(scores));
                        }
                        dialog.dismiss();
                        commitCourse(score);

                    }
                }, getString(R.string.discard), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                mSlideInPlayback.resetEditedState();
                //                callSuperOnBackPressed();
                exit(true);
            }
        },
                true);
        mMarkScoreDialog.show();
        mMarkScoreDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                hideSoftKeyboard(PlaybackActivityPhone.this);
            }
        });
    }

    public void hideSoftKeyboard(Activity activity) {
        try {
            ((InputMethodManager) activity
                    .getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(activity.getCurrentFocus()
                                    .getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {

        }
    }

    private void commitCourse(String score) {
        toUpload(score, true);
    }

    private void showSaveDialog() {
        ContactsMessageDialog messageDialog = new ContactsMessageDialog(
                this, null, getString(R.string.save_slide_msg),
                getString(R.string.give_up),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mSlideInPlayback.resetEditedState();
//                            callSuperOnBackPressed();//backFromEdit(true);
                        exit(true);
                        dialog.dismiss();
                    }
                },
                getString(R.string.confirm),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        toUpload("", false);
                    }
                });
        messageDialog.show();
    }

    private void callSuperOnBackPressed() {
        super.onBackPressed();
    }

    private void showMoreMenu(View attachedBar) {
        List<PopupMenu.PopupMenuData> itemDatas = new ArrayList();
        if (!isSplitCourse) {
            itemDatas.add(new PopupMenu.PopupMenuData(0, R.string.playbackphone_menu_comments,
                    R.string.playbackphone_menu_comments));
            itemDatas.add(new PopupMenu.PopupMenuData(0, R.string.playbackphone_menu_praise,
                    R.string.playbackphone_menu_praise));
        }
        AdapterView.OnItemClickListener itemClickListener =
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        processMenuClick((int) id);
                    }
                };
        int widthDp = DensityUtils.dp2px(PlaybackActivityPhone.this, POP_WINDOW_WIDTH);
        PopupMenu popupMenu = new PopupMenu(this, widthDp,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT, itemClickListener, itemDatas);
        //added by rmpan adjust the position for pop up menu
        int[] location = new int[2];
        attachedBar.getLocationOnScreen(location);
        int mCurrentOrientation = getResources().getConfiguration().orientation;
        if (mCurrentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            popupMenu.showAtLocation(attachedBar, Gravity.LEFT | Gravity.BOTTOM, location[0] - popupMenu.getWidth(), 0);
        }
        if (mCurrentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            popupMenu.showAsDropDown(attachedBar);
        }
    }

    /**
     * @param score       分数
     * @param isMarkScore 是否是提问,批阅
     */
    private void toUpload(final String score, final boolean isMarkScore) {
        // TODO: save, zip and upload
        if (isFinishing()) {
            return;
        }
        showLoadingDialog(getString(R.string.loading_and_wait), false);
        mSlideInPlayback.preBackPlaybackSlide();
        mSlideInPlayback
                .saveSlidePage(new com.oosic.apps.iemaker.base.onlineedit.CallbackListener<Boolean>() {

                    @Override
                    public void onBack(Boolean result) {
                        // TODO Auto-generated method stub
                        if (result) {
                            ZipUnzipParam param = new ZipUnzipParam(
                                    findWeikeFolder(), Utils.TEMP_FOLDER + getWeikeFolderName() + Utils.COURSE_SUFFIX);
                            FileZipHelper.zip(param,
                                    new ZipUnzipFileListener() {

                                        @Override
                                        public void onFinish(
                                                ZipUnzipResult result) {
                                            // TODO Auto-generated method stub
                                            if (result != null && result.mIsOk) {
                                                UploadUtils.uploadResource(PlaybackActivityPhone.this,
                                                        isMarkScore ? getBaseUploadParam(result) : getUploadParam(result), new CallbackListener() {

                                                            @Override
                                                            public void onBack(Object result) {
                                                                // TODO Auto-generated method stub
                                                                final Object data = result;
                                                                runOnUiThread(new Runnable() {
                                                                    public void run() {
                                                                        if (data != null) {
                                                                            final CourseUploadResult uploadResult = (CourseUploadResult) data;
                                                                            if (isAnswerCardQuestion) {
                                                                                if (uploadResult.code == 0) {
                                                                                    CourseData courseData = uploadResult.data.get(0);
                                                                                    answerCardPopWindow.commitAnswerQuestion(courseData.getIdType(), courseData.resourceurl);
                                                                                }
                                                                            } else if (applyMark) {
                                                                                //申请批阅
                                                                                if (mParam != null && mParam.applyMarkdata != null) {
                                                                                    if (uploadResult.code == 0) {
                                                                                        ApplyMarkHelper.enterApplyTeacherMarkActivity(PlaybackActivityPhone.this,
                                                                                                uploadResult.data.get(0),mParam.applyMarkdata);
                                                                                    }
                                                                                }
                                                                            } else if (doUploadResult(uploadResult, isMarkScore, score, data)) {

                                                                            }
                                                                        } else {
                                                                            TipMsgHelper.ShowLMsg(PlaybackActivityPhone.this, R.string.upload_comment_error);
                                                                        }
                                                                        dismissLoadingDialog();
                                                                    }
                                                                });
                                                            }
                                                        });
                                            } else {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        dismissLoadingDialog();
                                                        TipMsgHelper.ShowLMsg(PlaybackActivityPhone.this, R.string.upload_comment_error);
                                                    }
                                                });
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    /**
     * 处理上传结果
     *
     * @param uploadResult
     * @param isMarkScore
     * @param score
     * @param data
     * @return
     */
    private boolean doUploadResult(CourseUploadResult uploadResult, boolean isMarkScore, String score, Object data) {
        if (uploadResult.getCode() == 0) {
            //学生提问,老师批阅
            if (isMarkScore) {
                updateDataToMark(uploadResult, score);
                return true;
            }

            if (isSplitCourse) {
                if (collectParams != null && collectParams.courseSourceType == CHAT) {
                    uploadDataToChat(uploadResult);
                } else {
                    noticeServerUpdate(data);
                }
            } else {
                if (collectParams != null) {
                    if (collectParams.courseSourceType == BOOKSHELF && !collectParams.isMyBookShelf) {
                        updateDataToBookShelf(data);
                    } else if (collectParams.courseSourceType == CHAT) {
                        uploadDataToChat(uploadResult);
                    } else {
                        TipMsgHelper.ShowLMsg(PlaybackActivityPhone.this, R.string.success);
                        exit(true);
                    }
                }
            }
        } else {
            TipMsgHelper.ShowLMsg(PlaybackActivityPhone.this, R.string.failure);
        }
        return false;
    }

    private void uploadDataToChat(final CourseUploadResult uploadResult) {
        if (uploadResult.data != null && uploadResult.data.size() > 0) {
            CourseData courseData = uploadResult.data.get(0);
            if (courseData != null) {
                ConversationHelper.sendResource(PlaybackActivityPhone.this, collectParams.chatType, collectParams.userId, collectParams.userName, collectParams.userIcon, courseData.getSharedResource(), new EMCallBack() {
                    @Override
                    public void onSuccess() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                TipMsgHelper.ShowLMsg(PlaybackActivityPhone.this, R.string.upload_success);
                                MediaListFragment.updateMedia(PlaybackActivityPhone.this, userInfo, uploadResult.getShortCourseInfoList(), MediaType.FLIPPED_CLASSROOM);
                                exit(true);
                            }
                        });
                    }

                    @Override
                    public void onError(int i, String s) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                TipMsgHelper.ShowLMsg(PlaybackActivityPhone.this, R.string.upload_failure);
                            }
                        });
                    }

                    @Override
                    public void onProgress(int i, String s) {

                    }
                });
            }
        }
    }

    private void noticeServerUpdate(Object result) {
        CourseUploadResult uploadResult = (CourseUploadResult) result;
        if (uploadResult.data != null && uploadResult.data.size() > 0) {
            MediaListFragment.updateMedia(PlaybackActivityPhone.this, userInfo, uploadResult.getShortCourseInfoList(), MediaType.MICROCOURSE);
        }
//        TipMsgHelper.ShowLMsg(PlaybackActivityPhone.this, R.string.success);
        exit(true);
    }

    /**
     * 学生提问,老师批阅
     *
     * @param uploadResult
     */
    private void updateDataToMark(CourseUploadResult uploadResult, String score) {
        if (uploadResult.data != null && uploadResult.data.size() > 0) {
            updateData(uploadResult.data.get(0), collectParams, true, score);
        }
    }

    private void updateDataToBookShelf(Object result) {
        CourseUploadResult uploadResult = (CourseUploadResult) result;
        if (uploadResult.data != null && uploadResult.data.size() > 0) {
            updateData(uploadResult.data.get(0), collectParams, false, "");
        }
    }

    private void updateData(CourseData data, CollectParams collectParams, final boolean isMarkScore, final String score) {
        if (userInfo == null || TextUtils.isEmpty(userInfo.getMemberId())) {
            TipMsgHelper.ShowLMsg(PlaybackActivityPhone.this, R.string.pls_login);
            return;
        }
        Map<String, Object> params = new ArrayMap<>();
        if (isMarkScore) {
            //学生提问,老师批阅
            if (taskMarkParam.fromOnlineStudyTask) {
                params.put("CommitTaskOnlineId", taskMarkParam.commitTaskId);
            } else {
                params.put("CommitTaskId", taskMarkParam.commitTaskId);
            }
            params.put("ResId", data.getIdType());
            params.put("ResUrl", data.resourceurl);
            params.put("IsTeacher", isTeacherOrEditor());
            params.put("CreateId", data.code);
            if (taskMarkParam.cardParam != null) {
                int unFinishSubjectCount = taskMarkParam.cardParam.getUnFinishSubjectCount();
                if (unFinishSubjectCount <= 1) {
                    //之前作答的总分
                    String totalScore = taskMarkParam.cardParam.getStudentTotalScore();
                    if (!TextUtils.isEmpty(totalScore) && !TextUtils.isEmpty(score)) {
                        double totalDoubleScore = Double.valueOf(totalScore);
                        double commitScore = Double.valueOf(score);
                        double studentTotalScore = DoubleOperationUtil.add(totalDoubleScore, commitScore);
                        if (unFinishSubjectCount == 0 && !TextUtils.isEmpty(taskMarkParam.score)) {
                            params.put("TotalScore", DoubleOperationUtil.sub(studentTotalScore, Double.valueOf(taskMarkParam.score)));
                        } else {
                            if (TextUtils.isEmpty(taskMarkParam.score)) {
                                params.put("TotalScore", studentTotalScore);
                            }
                        }
                    }
                }
                if (isTeacherOrEditor()) {
                    params.put("EQScore", score);
                }
                ExerciseItem itemData = taskMarkParam.cardParam.getExerciseItem();
                if (itemData != null) {
                    params.put("EQId", itemData.getIndex());
                }
                params.put("CreateName", data.createname);
            } else {
                if (!TextUtils.isEmpty(score)) {
                    params.put("TaskScore", score);
                }
            }

        } else {
            params.put("MemberId", userInfo.getMemberId());
            params.put("MicroId", data.getIdType());
            params.put("Title", data.nickname);
            if (collectParams != null) {
                params.put("OldMicroId", collectParams.getMicroId() + "-" + collectParams.getResourceType());
                params.put("BookId", collectParams.getBookId());
                params.put("SectionId", collectParams.getSectionId());
            }
        }

        String serverUrl = null;
        if (isMarkScore) {
            if (taskMarkParam.cardParam != null) {
                //任务单答题卡的主观题
                serverUrl = ServerUrl.ADD_SUBJECTIVE_REVIEW_BASE_URL;
            } else {
                //提问或者批阅
                serverUrl = ServerUrl.GET_ADDCOMMITTASKREVIEW;
            }
        } else {
            serverUrl = ServerUrl.UPDATE_COLLECTION_BOOK_URL;
        }
        RequestHelper.sendPostRequest(PlaybackActivityPhone.this, serverUrl,
                params, new RequestHelper.RequestDataResultListener<DataModelResult>(PlaybackActivityPhone.this, DataModelResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        super.onSuccess(jsonString);
                        DataModelResult result = getResult();
                        if (result == null || !result.isSuccess()) {
                            dismissLoadingDialog();
                            return;
                        }
                        TipMsgHelper.ShowLMsg(PlaybackActivityPhone.this, R.string.commit_success);
                        if (isMarkScore) {
                            EventBus.getDefault().post(new MessageEvent(MessageEventConstantUtils.UPDATE_LIST_DATA));
                            LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(getContext());
                            broadcastManager.sendBroadcast(new Intent(CompletedHomeworkListFragment.ACTION_MARK_SCORE)
                                    .putExtra(CompletedHomeworkListFragment.ACTION_MARK_SCORE, score));
                        }
                        exit(true);
                    }
                });
    }

    /**
     * 判断是不是老师 或者 小编 或 主编
     *
     * @return
     */
    private boolean isTeacherOrEditor() {
        return taskMarkParam != null && (taskMarkParam.roleType == RoleType.ROLE_TYPE_TEACHER
                || taskMarkParam.roleType == RoleType.ROLE_TYPE_EDITOR);
    }

    private UploadParameter getBaseUploadParam(ZipUnzipResult result) {
        String coursePath = findWeikeFolder();
        int courseResType = BaseUtils.getCoursetType(coursePath);
        UploadParameter param = new UploadParameter();
        param.setMemberId(userInfo.getMemberId());
        param.setCreateName(userInfo.getRealName());
        param.setAccount(userInfo.getNickName());
        param.setZipFilePath(result.mParam.mOutputPath);
        param.setThumbPath(findWeikeFolder() + BaseUtils.RECORD_HEAD_IMAGE_NAME);
        param.setTotalTime(0);
        param.setResType(courseResType);
        param.setScreenType(mOrientation);
        param.setColType(1);

        if (collectParams != null) {
            param.setTotalTime(collectParams.getTotalTime());
            param.setResId(-1);
            param.setFileName(collectParams.title);
            String uploadUrl = String.format(ServerUrl.UPLOAD_RESOURCE_URL, ServerUrl.WEIKE_UPLOAD_BASE_SERVER);
            param.setUploadUrl(uploadUrl);
        }
        //批阅提问不需要拆分
        if (taskMarkParam != null) {
            param.setIsNeedSplit(false);
//            param.setFileName(collectParams.title + DateUtils.millSecToDateStr(SystemClock.currentThreadTimeMillis()));
        }
        return param;
    }

    private UploadParameter getUploadParam(ZipUnzipResult result) {
        UploadParameter param = getBaseUploadParam(result);
        if (param != null && collectParams != null) {
            int resType = collectParams.getResourceType();
            if (resType > ResType.RES_TYPE_BASE) {
                isSplitCourse = true;
            }
            String uploadUrl = String.format(ServerUrl.UPLOAD_RESOURCE_URL, ServerUrl.WEIKE_UPLOAD_BASE_SERVER);
            if (!isSplitCourse) {
                if ((collectParams.courseSourceType == BOOKSHELF && !collectParams.isMyBookShelf)
                        || collectParams.courseSourceType == CHAT) {
                    param.setResId(-1);
                } else {
                    if (!TextUtils.isEmpty(collectParams.editMicroId)) {
                        param.setResId(Long.valueOf(collectParams.editMicroId));
                        if (!TextUtils.isEmpty(collectParams.editMicroId) && !TextUtils.isEmpty(collectParams.getEditResourceUrl())) {
                            String baseUrl = UploadUtils.getBaseUploadUrl(collectParams.getEditResourceUrl());
                            if (!TextUtils.isEmpty(baseUrl)) {
                                uploadUrl = String.format(ServerUrl.UPLOAD_RESOURCE_URL, baseUrl);
                            }
                        }
                    } else {
                        param.setResId(Long.valueOf(collectParams.microId));
                        if (!TextUtils.isEmpty(collectParams.microId) && !TextUtils.isEmpty(collectParams.getResourceUrl())) {
                            String baseUrl = UploadUtils.getBaseUploadUrl(collectParams.getResourceUrl());
                            if (!TextUtils.isEmpty(baseUrl)) {
                                uploadUrl = String.format(ServerUrl.UPLOAD_RESOURCE_URL, baseUrl);
                            }
                        }
                    }
                }
                param.setFileName(collectParams.title);
            } else {
                String courseName = getNewSplitCourseName(collectParams.title);
                param.setFileName(courseName);
            }
            param.setUploadUrl(uploadUrl);
        }
        return param;
    }

    @Override
    protected boolean showSlide() {
        boolean rtn = super.showSlide();
        if (mAttachedBar != null) {
            mAttachedBar.setVisibility(View.VISIBLE);
        }
        return rtn;
    }

    private void edit() {
        Dialog dialog = showLoadingDialog();
        dialog.setCancelable(false);
        final DATParam datParam = new DATParam();
        datParam.mFileSuffix = Utils.COURSE_SUFFIX;
        datParam.mUrl = (mUrl.endsWith("/") ? mUrl.substring(0,
                mUrl.length() - 1) : mUrl) + Utils.COURSE_SUFFIX;
        datParam.mDownloadFolder = Common.DownloadWeike;
        if (collectParams != null) {
            datParam.mUpdateTime = collectParams.updateTime;
            if (!TextUtils.isEmpty(collectParams.editMicroId)) {
                datParam.mUpdateTime = collectParams.editUpdateTime;
            }
        }
        File folder = new File(datParam.mDownloadFolder);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        DownloadAttachTask.checkWeikeFiles(datParam.mDownloadFolder);
        String downloadFile = DownloadAttachTask.getDownloadPath(datParam);
        if (!TextUtils.isEmpty(downloadFile)) {
            File file = new File(downloadFile);
            if (file.exists()) {
                ZipUnzipParam param = new ZipUnzipParam(
                        downloadFile,
                        getWeikeFolder());
                unzipAndEdit(param);
                return;
            }
        }
        DownloadAttachTask<DownloadDtoBase> task = new DownloadAttachTask<DownloadDtoBase>(
                datParam, DownloadDtoBase.class, new CallbackListener() {

            @Override
            public void onBack(Object result) {
                // TODO Auto-generated method stub
                if (result != null) {
                    DownloadChwCallbackParam<DownloadDtoBase> dto = (DownloadChwCallbackParam<DownloadDtoBase>) result;
                    if (dto.mDownloadDto != null
                            && dto.mDownloadDto.getPath() != null) {
                        ZipUnzipParam param = new ZipUnzipParam(
                                dto.mDownloadDto.getPath(),
                                getWeikeFolder());
                        unzipAndEdit(param);
                    }
                } else {
                    dismissLoadingDialog();
                }
            }
        });
        task.execute();
    }

    private void unzipAndEdit(ZipUnzipParam param) {
        FileZipHelper.unzip(param,
                new ZipUnzipFileListener() {

                    @Override
                    public void onFinish(
                            ZipUnzipResult result) {
                        // TODO Auto-generated method
                        // stub
                        if (result.mIsOk) {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    String weikeFolder = Utils.getCourseRootPath(Common.DownloadWeike);
                                    if (!TextUtils.isEmpty(weikeFolder)) {
                                        if (!weikeFolder.endsWith(File.separator)) {
                                            weikeFolder += File.separator;
                                        }
                                        enterEdit(weikeFolder);
                                    }
                                }
                            });
                        }
                    }
                });
    }

    private void enterEdit(String weikeFolder) {
//        if (mAttachedBar != null) {
//            mAttachedBar.removeAllViews();
//            addMenu(mAttachedBar, R.string.playbackphone_menu_complete,
//                R.drawable.menu_icon_ok);
//        }
        if (weikeFolder != null) {
            updateByNewFile(weikeFolder);
        }
        dismissLoadingDialog();
        mSlideInPlayback.setEditable(true);
        mInEditMode = true;

        updateMarkView(taskMarkParam, markView);
//        if (mOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
        mAttachedBar.setVisibility(View.GONE);
//        }
    }

    private void backFromEdit(boolean updateSlide) {
        if (mAttachedBar != null) {
            mAttachedBar.removeAllViews();
//            addMenu(mAttachedBar, mParam != null && mParam.mIsOwner);
            addMenuOrView(mAttachedBar);
            mAttachedBar.setVisibility(View.VISIBLE);
        }
        if (updateSlide) {
            if (collectParams != null && !TextUtils.isEmpty(collectParams.editMicroId)) {
                //TODO Resume Raw Course
            } else {
                updateByNewFile(findWeikeFolder());
            }

        }
        mSlideInPlayback.setEditable(false);
        mInEditMode = false;
        updateMarkView(taskMarkParam, markView);
    }

    private void exit(boolean rmZipFile) {
        mSlideInPlayback.resetEditedState();
        mSlideInPlayback.setEditable(false);
        mInEditMode = false;
        if (rmZipFile) {
            rmWeikeFile();
        }
//        onBackPressed();
        finish();
    }

    private String findWeikeFolder() {
        return Utils.getCourseRootPath(getWeikeFolder()) + File.separator;
    }

    private String getWeikeFolder() {
        return Common.DownloadWeike + getWeikeFolderName() + "/";
    }

    private void rmWeikeFile() {
        File file = new File(Common.DownloadWeike + getWeikeFolderName() + Utils.COURSE_SUFFIX);
        if (file.exists()) {
            file.delete();
        }
    }

    private String getWeikeFolderName() {
        String url = (mUrl.endsWith("/") ? mUrl.substring(0, mUrl.length() - 1)
                : mUrl) + Utils.COURSE_SUFFIX;
        String updateTime = collectParams.updateTime;
        if (!TextUtils.isEmpty(collectParams.editMicroId)) {
            updateTime = collectParams.editUpdateTime;
        }
        return DownloadAttachTask.getFolderName(url + updateTime);
    }

    private String getNewSplitCourseName(String courseName) {
        if (TextUtils.isEmpty(courseName)) {
            return null;
        }

        String pattern = "yyyyMMddHHmmss";
        String dateStr = DateUtils.format(System.currentTimeMillis(), pattern);
        StringBuilder builder = new StringBuilder();
        builder.append(courseName);
        builder.append("-");
        builder.append(dateStr);
        return builder.toString();
    }

    @Override
    protected void onDestroy() {
        if (handler != null) {
            handler.removeCallbacks(runnable);
        }
        //根据条件判断是否需要记录观看的时间
        if (isNeedTimerRecorder()) {
            //把观看的数据同步给server
            recordTime = (System.currentTimeMillis() - recordTime + 500) / 1000;
            StudyInfoRecordUtil.getInstance().
                    clearData().
                    setActivity(PlaybackActivityPhone.this).
                    setCollectParams(collectParams).
                    setCurrentModel(StudyInfoRecordUtil.RecordModel.STUDY_MODEL).
                    setRecordTime((int) recordTime).
                    setRecordType(1).
                    setSourceType(UIUtils.currentSourceFromType).
                    setUserInfo(DemoApplication.getInstance().getUserInfo()).
                    send();
        }
        super.onDestroy();
    }

    /**
     * @return 判断返回记录观看时间的条件
     */
    private boolean isNeedTimerRecorder() {
        UserInfo userInfo = DemoApplication.getInstance().getUserInfo();
        if (userInfo == null || TextUtils.isEmpty(userInfo.getMemberId())) {
            return false;
        }
        if (collectParams == null) {
            return false;
        }
        if (mParam != null && mParam.mIsAuth) {
            return false;
        }
        int resType = collectParams.getResourceType() % ResType.RES_TYPE_BASE;
        if (resType == ResType.RES_TYPE_ONEPAGE ||
                resType == ResType.RES_TYPE_COURSE_SPEAKER ||
                resType == ResType.RES_TYPE_OLD_COURSE ||
                resType == ResType.RES_TYPE_COURSE) {
            return true;
        }
        return false;
    }

    /**
     * 分析微课是否有含有资源的时间
     */
    private void analysisCourseTotalTime() {
        if (isNeedTimerRecorder()) {
            int type = collectParams.getResourceType();
            if (type != ResType.RES_TYPE_ONEPAGE && collectParams.totalTime == 0) {
                if (type > ResType.RES_TYPE_BASE) {
                    WawaCourseUtils wawaCourseUtils = new WawaCourseUtils(PlaybackActivityPhone.this);
                    wawaCourseUtils.loadSplitCourseDetail(Integer.parseInt(collectParams.getMicroId()));
                    wawaCourseUtils.setOnSplitCourseDetailFinishListener(new WawaCourseUtils
                            .OnSplitCourseDetailFinishListener() {
                        @Override
                        public void onSplitCourseDetailFinish(SplitCourseInfo info) {
                            if (info != null) {
                                CourseData courseData = info.getCourseData();
                                if (courseData != null) {
                                    collectParams.setTotalTime(courseData.totaltime);
                                }
                            }
                        }
                    });
                } else {
                    //非分页信息
                    WawaCourseUtils wawaCourseUtils = new WawaCourseUtils(PlaybackActivityPhone.this);
                    wawaCourseUtils.loadCourseDetail(collectParams.getMicroId());
                    wawaCourseUtils.setOnCourseDetailFinishListener(new WawaCourseUtils.
                            OnCourseDetailFinishListener() {
                        @Override
                        public void onCourseDetailFinish(CourseData courseData) {
                            if (courseData != null) {
                                collectParams.setTotalTime(courseData.totaltime);
                            }
                        }
                    });
                }
            }
        }
    }

    private ShareInfo getShareInfo(CourseShareData shareData) {
        ShareInfo shareInfo = null;
        if (shareData != null) {
            String shareAddress = shareData.getShareAddress();
            if (TextUtils.isEmpty(shareAddress) && shareData.getId() > 0) {
                shareAddress = ShareSettings.WAWAWEIKE_SHARE_URL + shareData.getId();
            }
            shareInfo = new ShareInfo();
            shareInfo.setTitle(shareData.getTitle());
            shareInfo.setContent(shareData.getAuthor());
            //增加判断的分享连接的权限
            SharedResource sharedResource = shareData.getSharedResource();
            if (sharedResource != null) {
                shareInfo.setIsPublicRescourse(sharedResource.isPublicRescourse());
                shareInfo.setParentId(sharedResource.getParentId());
                shareInfo.setSharedResource(sharedResource);
            }
            shareInfo.setTargetUrl(shareAddress);
            UMImage umImage;
            if (!TextUtils.isEmpty(shareData.getThumbnail())) {
                umImage = new UMImage(PlaybackActivityPhone.this, shareData.getThumbnail());
            } else {
                umImage = new UMImage(PlaybackActivityPhone.this, R.drawable.ic_launcher);
            }
            shareInfo.setuMediaObject(umImage);
            if (shareData != null) {
                shareInfo.setSharedResource(shareData.getSharedResource());
            }
        }
        return shareInfo;
    }

    private void initAnswerPopWindowData() {
        if (answerCardPopWindow == null) {
            answerBtn = (TextView) findViewById(R.id.exercise_btn);
            JSONArray jsonArray = JSONObject.parseArray(cardParam.getExerciseAnswerString());
            if (jsonArray != null && jsonArray.size() > 0) {
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                LearnTaskInfo taskInfo = JSONObject.parseObject(jsonObject.toString(), LearnTaskInfo.class);
                if (taskInfo != null) {
                    exerciseItems = taskInfo.getExercise_item_list();
                    if (exerciseItems != null && exerciseItems.size() > 0) {
                        answerCardPopWindow = new AnswerCardPopWindow(
                                PlaybackActivityPhone.this,
                                cardParam,
                                exerciseItems,
                                mOrientation);
                    }
                }
            }
            if (answerCardPopWindow != null) {
                answerCardPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        //更新数据
                        answerCardPopWindow.updateAnswerDetail(result -> {
                            String dataList = (String) result;
                            updateExerciseNodes(dataList);
                        });
                    }
                });
            }
        }
    }

    private void showAnswerPopWindow(boolean showAllData, int questionIndex) {
        if (showAllData) {
            //显示所有的
            if (answerCardPopWindow.isShowSingleState()) {
                answerCardPopWindow.initAllData();
            }
        } else {
            //显示单题的
            answerCardPopWindow.showSingleQuestionDetail(questionIndex);
        }
        if (mOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            answerCardPopWindow.showPopupMenuRight();
        } else {
            answerCardPopWindow.showAsDropDown(answerBtn);
        }
    }


    @Override
    public void onExerciseButtonClick() {
        if (cardParam == null || TextUtils.isEmpty(cardParam.getExerciseAnswerString())) {
            return;
        }
        showAnswerPopWindow(true, 0);
    }

    /**
     * 单题的点击事件
     * @param exerciseIndex
     */
    @Override
    public void onExerciseNodeClick(int exerciseIndex) {
        if (isAnswerCardQuestion){
            showAnswerPopWindow(false,exerciseIndex-1);
        } else {
            //浏览模式
            reviewExerciseDetails(exerciseIndex);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        if (answerCardPopWindow != null) {
            //答题时拍照和从相册选取资源
            answerCardPopWindow.setRequestCodeData(requestCode, resultData);
        }
    }
}
