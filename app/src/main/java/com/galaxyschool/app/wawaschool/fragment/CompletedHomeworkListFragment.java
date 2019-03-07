package com.galaxyschool.app.wawaschool.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.util.ArrayMap;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.galaxyschool.app.wawaschool.AnswerCardDetailActivity;
import com.galaxyschool.app.wawaschool.EnglishWritingCommentDetailsActivity;
import com.galaxyschool.app.wawaschool.EnglishWritingCommentRecordActivity;
import com.galaxyschool.app.wawaschool.HomeworkFinishStatusActivity;
import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.QDubbingActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.TeacherReviewDetailActivity;
import com.galaxyschool.app.wawaschool.chat.DemoApplication;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.CourseOpenUtils;
import com.galaxyschool.app.wawaschool.common.DensityUtils;
import com.galaxyschool.app.wawaschool.common.StudyTaskUtils;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.common.UIUtils;
import com.galaxyschool.app.wawaschool.common.Utils;
import com.galaxyschool.app.wawaschool.common.WatchWawaCourseResourceOpenUtils;
import com.galaxyschool.app.wawaschool.common.WawaCourseUtils;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.db.NewWatchWawaCourseResourceDao;
import com.galaxyschool.app.wawaschool.db.dto.NewWatchWawaCourseResourceDTO;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.galaxyschool.app.wawaschool.fragment.resource.HomeworkCommitResourceAdapterViewHelper;
import com.galaxyschool.app.wawaschool.helper.DoTaskOrderHelper;
import com.galaxyschool.app.wawaschool.helper.LqCourseHelper;
import com.galaxyschool.app.wawaschool.pojo.ExerciseAnswerCardParam;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.CheckMarkInfo;
import com.galaxyschool.app.wawaschool.pojo.CheckMarkResult;
import com.galaxyschool.app.wawaschool.pojo.CommitTask;
import com.galaxyschool.app.wawaschool.pojo.HomeworkCommitObjectInfo;
import com.galaxyschool.app.wawaschool.pojo.HomeworkCommitObjectResult;
import com.galaxyschool.app.wawaschool.pojo.HomeworkListInfo;
import com.galaxyschool.app.wawaschool.pojo.LookResDto;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfo;
import com.galaxyschool.app.wawaschool.pojo.ResType;
import com.galaxyschool.app.wawaschool.pojo.ResourceInfoTag;
import com.galaxyschool.app.wawaschool.pojo.RoleType;
import com.galaxyschool.app.wawaschool.pojo.StudyTask;
import com.galaxyschool.app.wawaschool.pojo.StudyTaskType;
import com.galaxyschool.app.wawaschool.pojo.TaskMarkParam;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseData;
import com.galaxyschool.app.wawaschool.pojo.weike.PlaybackParam;
import com.galaxyschool.app.wawaschool.pojo.weike.SplitCourseInfo;
import com.galaxyschool.app.wawaschool.views.ContactsMessageDialog;
import com.galaxyschool.app.wawaschool.views.NoDoubleClickListener;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;
import com.lqwawa.apps.views.StrokeTextView;
import com.lqwawa.lqbaselib.pojo.MessageEvent;
import com.osastudio.common.popmenu.CustomPopWindow;
import com.osastudio.common.utils.LogUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 已完成作业提交列表(可通用)
 */

public class CompletedHomeworkListFragment extends ContactsListFragment {

    public static final String TAG = CompletedHomeworkListFragment.class.getSimpleName();

    public static final int TITLE_MAX_LINES = 2;

    private int roleType = -1;
    private View rootView;
    private String taskId = "";
    private String studentId = "";
    private int taskType;
    private String sortStudentId = "";
    private boolean isPlaying; //限制只弹一个播放页面
    private ListView listView;
    private String taskTitle;
    private StudyTask task;
    private static boolean hasContentChanged;
    private boolean hiddenHeaderView;
    private boolean needFilterData;//需要过滤数据
    private String[] childIdArray;//孩子id数组
    private TextView commitBtn;//提交按钮
    private boolean shouldShowCommitBtn;//显示提交按钮
    //判断是否来自校园巡查的标识
    private boolean isCampusPatrolTag;
    private List<LookResDto> lookResDtoList = new ArrayList<>();
    private List<ResourceInfoTag> resourceInfoTagList = new ArrayList<>();
    private boolean fromHomeworkFinishStatusList = false;
    private UserInfo userInfo;
    private boolean readAll = false;//阅读完毕
    private HomeworkListInfo homeworkListInfo;
    private boolean hasReadTask = false; //是否阅读了任务
    private LocalBroadcastManager mBroadcastManager;
    public static final String ACTION_MARK_SCORE = "com.galaxyschool.app.wawaschool.Action_Mark_score";
    private PullToRefreshView mPullToRefreshView;
    private boolean isOnlineReporter;//是不是来自直播的小编
    private boolean isOnlineHost;//是不是主编的身份
    private View mCommitBtnFl;
    private View mStatisticFl;
    private CustomPopWindow mPopWindow;
    private boolean isHeadMaster;
    private boolean deleteBtnVisibled;
    private boolean needShow = false;
    private TextView speechAssessmentTextV;//语音评测的按钮
    private FrameLayout speechAssessmentFl;
    private boolean isHistoryClass;
    private boolean isAnswerTaskOrderQuestion;//任务单答题卡的信息
    private int fullMarkScore;//试卷的总分
    private int propertiesType;
    private boolean hasSubjectProblem;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.layout_fragment_completed_homework_list, null);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
    }

    public void refreshData() {
        loadViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        registResultBroadcast();
        isPlaying = false;
    }

    void initViews() {
        if (getArguments() != null) {
            isHeadMaster = getArguments().getBoolean(HomeworkMainFragment.Constants.EXTRA_IS_HEAD_MASTER);
            this.userInfo = (UserInfo) getArguments().get(UserInfo.class.getSimpleName());
            roleType = getArguments().getInt(HomeworkFinishStatusActivity.Constants.ROLE_TYPE);
            taskId = getArguments().getString(HomeworkFinishStatusActivity.Constants.TASK_ID);
            taskType = getArguments().getInt(HomeworkFinishStatusActivity.Constants.TASK_TYPE);
            taskTitle = getArguments().getString(HomeworkFinishStatusActivity.Constants.TASK_TITLE);
            //单个孩子id
            studentId = getArguments().getString(HomeworkFinishStatusActivity.Constants.STUDENT_ID);
            //学生Id串，以逗号分隔。
            sortStudentId = getArguments().getString(HomeworkFinishStatusActivity.Constants.
                    SORT_STUDENT_ID);
            hiddenHeaderView = getArguments().getBoolean(HomeworkFinishStatusActivity.Constants
                    .HIDDEN_HEADER_VIEW);
            needFilterData = getArguments().getBoolean(HomeworkFinishStatusActivity.Constants
                    .NEED_FILTER_DATA);
            //得到孩子Id数组
            childIdArray = getArguments().getStringArray(HomeworkMainFragment.Constants.
                    EXTRA_CHILD_ID_ARRAY);
            shouldShowCommitBtn = getArguments().getBoolean(HomeworkFinishStatusActivity.Constants
                    .SHOULD_SHOW_COMMIT_BTN);
            isCampusPatrolTag = getArguments().getBoolean(CampusPatrolMainFragment
                    .IS_CAMPUS_PATROL_TAG, false);
            lookResDtoList = (List<LookResDto>) getArguments()
                    .getSerializable(HomeworkCommitFragment.LOOK_RES_DTO_LIST);
            fromHomeworkFinishStatusList = getArguments().getBoolean
                    (HomeworkFinishStatusActivity.Constants.FROM_HOMEWORK_FINISH_STAUS_LIST);
            homeworkListInfo = (HomeworkListInfo) getArguments()
                    .getSerializable(HomeworkListInfo.class.getSimpleName());
            if (homeworkListInfo != null) {
                //标识任务是否阅读了
                hasReadTask = homeworkListInfo.isStudentIsRead();
                //主编和小编身份互斥 只能同时存在一个
                isOnlineReporter = homeworkListInfo.isOnlineReporter();
                isOnlineHost = homeworkListInfo.isOnlineHost();
                LogUtils.logd(TAG, "   isOnlineReporter = " + isOnlineReporter + "           isOnlineHost = " + isOnlineHost);
            }
            isHistoryClass = getArguments().getBoolean(ActivityUtils.EXTRA_IS_HISTORY_CLASS);
        }

        //头布局
        View headerView = findViewById(R.id.contacts_header_layout);
        if (headerView != null) {
            headerView.setVisibility(hiddenHeaderView ? View.GONE : View.VISIBLE);
        }

        //标题
        TextView textView = (TextView) findViewById(R.id.contacts_header_title);
        if (textView != null) {
            if (!TextUtils.isEmpty(taskTitle)) {
                textView.setText(taskTitle);
            }
        }

        //语音评测
        speechAssessmentTextV = (TextView) findViewById(R.id.tv_speech_assessment);
        if (speechAssessmentTextV != null) {
            speechAssessmentTextV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment fragment = getParentFragment();
                    if (fragment instanceof HomeworkCommitFragment) {
                        ((HomeworkCommitFragment) fragment).enterSpeechAssessmentActivity();
                    }
                }
            });
        }

        speechAssessmentFl = (FrameLayout) findViewById(R.id.fl_speech_assessment);

        //下拉刷新
        mPullToRefreshView = (PullToRefreshView) findViewById(
                R.id.contacts_pull_to_refresh);
        if (isNewWawaCourse()) {
            mPullToRefreshView.setRefreshEnable(false);
        } else {
            mPullToRefreshView.setRefreshEnable(true);
        }
        setPullToRefreshView(mPullToRefreshView);
        listView = (ListView) findViewById(R.id.contacts_list_view);
        if (listView != null) {
            if (isNewWawaCourse()) {
                AdapterViewHelper helper = new AdapterViewHelper(getActivity(), listView,
                        R.layout.item_commited_watch_course) {
                    @Override
                    public void loadData() {
                        buildResourceInfoTagData();
                    }

                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        final ResourceInfoTag data = (ResourceInfoTag) getDataAdapter()
                                .getItem(position);
                        if (data == null) {
                            return view;
                        }
                        ViewHolder holder = (ViewHolder) view.getTag();
                        if (holder == null) {
                            holder = new ViewHolder();
                        }
                        holder.data = data;

                        //备用图片
                        ImageView imageView = (ImageView) view.findViewById(R.id.iv_extra_icon);
                        if (imageView != null) {
                            imageView.setVisibility(View.VISIBLE);
                            //默认图片
                            int defaultIcon = WatchWawaCourseResourceOpenUtils
                                    .getItemDefaultIcon(data);
                            imageView.setImageResource(defaultIcon);
                            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams)
                                    imageView.getLayoutParams();
                            //48 * 48
                            int iconSize = (int) (48 * MyApplication.getDensity());
                            lp.width = iconSize;
                            lp.height = iconSize;
                            imageView.setLayoutParams(lp);
                        }


                        //备用内容
                        TextView textView = (TextView) view.findViewById(R.id.tv_extra_content);
                        if (textView != null) {
                            textView.setVisibility(View.VISIBLE);
                            //标题
                            String title = data.getTitle();
                            textView.setText(title);
                        }

                        //如果来自校园巡查的数据都不是显示小红点
                        ImageView redCircleView = (ImageView) view.findViewById(R.id.red_point);
                        if (redCircleView != null) {
                            if (isCampusPatrolTag || isHistoryClass) {
                                redCircleView.setVisibility(View.INVISIBLE);
                            } else {
                                //显示小红点
                                if (shouldDisplayUnreadStatus(data)) {
                                    redCircleView.setVisibility(View.VISIBLE);
                                } else {
                                    redCircleView.setVisibility(View.INVISIBLE);
                                }
                            }
                        }

                        int type = data.getResourceType();
                        View courseDetail = view.findViewById(R.id.tv_access_details);
                        //仅LQ课件显示
                        if (Utils.isLQCourse(type)) {
                            courseDetail.setVisibility(View.VISIBLE);
                        } else {
                            courseDetail.setVisibility(View.GONE);
                        }

                        //课件详情
                        courseDetail.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int originType = data.getResourceType();
                                if (Utils.isLQCourse(originType)) {
                                    //进入微课详情页
                                    //特殊处理一下
                                    StudyTask myTask = new StudyTask();
                                    myTask.setResId(data.getResId());
                                    myTask.setResUrl(data.getResourcePath());
                                    myTask.setResThumbnailUrl(data.getImgPath());
                                    myTask.setTaskTitle(data.getTitle());
                                    //老师布置的看课件打开要加密
                                    CourseOpenUtils.openCourseDetailsDirectly(getActivity(), myTask,
                                            roleType, getMemeberId(), studentId, CompletedHomeworkListFragment.this.userInfo, true);
                                }
                                //更新数据
                                if (shouldShowRedPointOrUpdateData(data)) {
                                    updateLocalData(data);
                                    getCurrAdapterViewHelper().update();
                                }
                            }
                        });
                        view.setTag(holder);
                        return view;
                    }

                    @Override
                    public void onItemClick(AdapterView parent, View view, int position, long id) {
                        ViewHolder holder = (ViewHolder) view.getTag();
                        if (holder == null) {
                            return;
                        }

                        //防止连续点击 目前控制2秒钟
                        if (UIUtils.isFastClick()) {
                            return;
                        }

                        ResourceInfoTag data = (ResourceInfoTag) holder.data;
                        if (data != null) {
                            //仅LQ课件显示
                            int originType = data.getResourceType();
                            if (Utils.isLQCourse(originType)) {
                                //打开资源
                                boolean isPublicRes = true;
                                //隐藏收藏按钮
                                boolean isNeedHideCollectBtn = false;
                                if (taskType == StudyTaskType.NEW_WATACH_WAWA_COURSE) {
                                    isPublicRes = false;
                                    isNeedHideCollectBtn = true;
                                }
                                CourseOpenUtils.openCourseDirectly((Activity) context
                                        , data.getResId(), isPublicRes, "", isNeedHideCollectBtn);
                                //点击缩略图也更新数据
                                if (shouldShowRedPointOrUpdateData(data)) {
                                    updateLocalData(data);
                                    getCurrAdapterViewHelper().update();
                                }
                            } else {
                                //打开看课件资源
                                WatchWawaCourseResourceOpenUtils.openResource(getActivity(), data,
                                        false, true);
                            }

                            updateLqCourseLookCourseState(data);
                            //更新数据
                            if (shouldShowRedPointOrUpdateData(data)) {
                                updateLocalData(data);
                                getCurrAdapterViewHelper().update();
                            }

                        }
                    }
                };
                setCurrAdapterViewHelper(listView, helper);

            } else {
                //作业通用列表
                AdapterViewHelper listViewHelper = new HomeworkCommitResourceAdapterViewHelper(
                        getActivity(), listView, roleType, getMemeberId()) {
                    @Override
                    public void loadData() {
                        loadCommonData();
                    }

                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        final View view = super.getView(position, convertView, parent);
                        final CommitTask data = (CommitTask) getDataAdapter().getItem(position);
                        if (data == null) {
                            return view;
                        }
                        ViewHolder holder = (ViewHolder) view.getTag();
                        if (holder == null) {
                            holder = new ViewHolder();
                        }
                        holder.data = data;

                        ImageView ivWarn = (ImageView) view.findViewById(R.id.icon_warn);
                        if (ivWarn != null) {
                            if (roleType == RoleType.ROLE_TYPE_TEACHER || roleType == RoleType.ROLE_TYPE_PARENT) {
                                String taskScore = data.getTaskScore();
                                if (TextUtils.isEmpty(taskScore)) {
                                    ivWarn.setVisibility(View.GONE);
                                } else if (isAnswerTaskOrderQuestion) {
                                    //答题卡得分
                                    ivWarn.setVisibility(Double.valueOf(taskScore) < fullMarkScore * 0.6 ? View.VISIBLE : View.GONE);
                                } else if (data.isHasCommitTaskReview()) {
                                    if (task.getScoringRule() == 2) {
                                        ivWarn.setVisibility(Double.valueOf(taskScore) < 60 ? View.VISIBLE : View.GONE);
                                    }
                                    if (task.getScoringRule() == 1) {
                                        ivWarn.setVisibility(taskScore.contains("D") ? View.VISIBLE : View.GONE);
                                    }
                                }
                            } else {
                                ivWarn.setVisibility(View.GONE);
                            }
                        }
                        //标题
                        TextView textView = (TextView) view.findViewById(R.id.tv_title);
                        if (textView != null) {
                            //设置打点显示策略
                            String content = "";
                            if (taskType == StudyTaskType.ENGLISH_WRITING) {
                                if (task != null) {
                                    //显示作文内容
                                    content = data.getWritingContent();
                                }
                            } else if (taskType == StudyTaskType.Q_DUBBING) {
                                if (task != null) {
                                    //显示作文内容
                                    content = task.getTaskTitle();
                                }
                            } else {
                                content = data.getStudentResTitle();
                            }
                            processTitle(textView, content,data.getCommitTime());
                        }
                        //作业图片布局
                        View iconLayout = view.findViewById(R.id.layout_icon);
                        if (iconLayout != null) {
                            if (taskType == StudyTaskType.ENGLISH_WRITING) {
                                iconLayout.setVisibility(View.GONE);
                            } else {
                                iconLayout.setVisibility(View.VISIBLE);
                            }
                        }

                        //显示任务类型
                        View taskTypeView = view.findViewById(R.id.commit_type);
                        if (taskTypeView != null) {
                            if (taskType != StudyTaskType.INTRODUCTION_WAWA_COURSE) {
                                taskTypeView.setVisibility(View.GONE);
                            } else {
                                //只有导读才显示任务类型
                                taskTypeView.setVisibility(View.VISIBLE);
                            }
                        }

                        //语音评测的图片布局
                        if (data.isEvalType() || isAnswerTaskOrderQuestion) {
                            ImageView imageView = (ImageView) view.findViewById(R.id.iv_icon);
                            if (imageView != null) {
                                //之前宽 90 高 120  //设置布局为A4比例
                                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) imageView.getLayoutParams();
                                int width = DensityUtils.dp2px(getActivity(), 90);
                                layoutParams.width = width;
                                layoutParams.height = width * 210 / 297;
                                imageView.setLayoutParams(layoutParams);
                                if (isAnswerTaskOrderQuestion) {
                                    //答题卡
                                    MyApplication.getThumbnailManager(getActivity()).displayUserIconWithDefault(
                                            "", imageView, R.drawable.icon_exercise_card);
                                } else {
                                    MyApplication.getThumbnailManager(getActivity()).displayUserIconWithDefault(
                                            "", imageView, R.drawable.icon_student_task_eval);
                                }
                            }
                        }

                        //如果来自校园巡查的数据都不是显示小红点
                        if (isCampusPatrolTag) {
                            ImageView redCircleView = (ImageView) view.findViewById(R.id.red_point);
                            if (redCircleView != null) {
                                redCircleView.setVisibility(View.INVISIBLE);
                            }
                        }

                        TextView tvCheckMark = (TextView) view.findViewById(R.id.tv_check_mark);//查看批阅
                        tvCheckMark.setText(getString(R.string.str_watch_mark));
                        StrokeTextView tvScore = (StrokeTextView) view.findViewById(R.id.tv_score);//得分
                        if (data.isHasCommitTaskReview()) {
                            String taskScore = data.getTaskScore();
                            if (TextUtils.isEmpty(taskScore)) {
                                tvScore.setVisibility(View.GONE);
                            } else {
                                StringBuilder builder = new StringBuilder(taskScore);
                                if (task.getScoringRule() == 2) {//百分制
                                    builder = new StringBuilder(taskScore).append(getString(R.string.str_scores));
                                }
                                tvScore.setText(builder);
                                tvScore.setVisibility(View.VISIBLE);
                            }
                            tvCheckMark.setBackgroundResource(R.drawable.green_10dp_gray);
                            tvCheckMark.setTextColor(getResources().getColor(R.color.text_green));
                        } else {
                            tvCheckMark.setBackgroundResource(R.drawable.green_10dp_red);
                            tvCheckMark.setTextColor(getResources().getColor(R.color.com_text_red));
                            tvScore.setVisibility(View.GONE);
                        }

                        if (taskType == StudyTaskType.TASK_ORDER || taskType == StudyTaskType.RETELL_WAWA_COURSE) {
                            tvCheckMark.setVisibility(View.VISIBLE);
                        } else {
                            tvCheckMark.setVisibility(View.GONE);
                        }

                        //语音评测点评按钮的显示
                        if (data.isEvalType() || data.isVideoType()) {
                            //立即点评老师点评
                            if (data.isHasVoiceReview()) {
                                //已经评测
                                tvCheckMark.setVisibility(View.VISIBLE);
                                if (data.isVideoType()){
                                    //q配音显示查看点评
                                    tvCheckMark.setText(getString(R.string.str_look_review));
                                } else {
                                    tvCheckMark.setText(getString(R.string.str_teacher_review));
                                }
                                tvCheckMark.setBackgroundResource(R.drawable.green_10dp_gray);
                                tvCheckMark.setTextColor(getResources().getColor(R.color.text_green));
                            } else {
                                //没有评测
                                if (hasEvalReviewPermission() && !isHistoryClass) {
                                    tvCheckMark.setText(getString(R.string.str_immediately_review));
                                    tvCheckMark.setBackgroundResource(R.drawable.green_10dp_red);
                                    tvCheckMark.setTextColor(getResources().getColor(R.color.com_text_red));
                                    tvCheckMark.setVisibility(View.VISIBLE);
                                } else {
                                    tvCheckMark.setVisibility(View.GONE);
                                }
                            }
                        }

                        tvCheckMark.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //更新小红点
                                updateLookTaskStatus(data.getCommitTaskId(), data.isRead());
                                if (isAnswerTaskOrderQuestion) {
                                    //任务单的答题卡
                                    enterStudentAnswerDetailActivity(data, true);
                                } else if (data.isEvalType() || data.isVideoType()) {
                                    //进入点评的详情页
                                    enterTeacherReviewDetailActivity(data, true);
                                } else {
                                    enterCheckMarkDetail(data);
                                }
                            }
                        });

                        View courseDetails = view.findViewById(R.id.tv_access_details);
                        if (taskType == StudyTaskType.ENGLISH_WRITING) {
                            courseDetails.setVisibility(View.GONE);
                            TextView commitTimeTextV = (TextView) view.findViewById(R.id.tv_commit_time);
                            if (commitTimeTextV != null) {
                                String commitTime = data.getUpdateTime();
                                if (TextUtils.isEmpty(commitTime)) {
                                    commitTime = data.getCommitTime();
                                }
                                if (!TextUtils.isEmpty(commitTime)) {
                                    if (commitTime.contains(":")) {
                                        //精确到分
                                        commitTime = commitTime.substring(0, commitTime.lastIndexOf(":"));
                                    }
                                    commitTimeTextV.setText(commitTime);
                                }
                            }
                        } else if (isAnswerTaskOrderQuestion) {
                            courseDetails.setVisibility(View.GONE);
                        } else {
                            courseDetails.setVisibility(View.VISIBLE);
                        }

                        //课件详情
                        courseDetails.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (isAnswerTaskOrderQuestion) {
                                    //任务单答题卡
                                    enterStudentAnswerDetailActivity(data, false);
                                } else if (data.isEvalType()) {
                                    updateLookTaskStatus(data.getCommitTaskId(), data.isRead());
                                    //语音评测资源
                                    enterSpeechAssessmentActivity(data);
                                } else if (data.isVideoType()) {
                                    enterQDubbingDetailActivity(data);
                                } else {
                                    if (!isPlaying) {
                                        isPlaying = true;
                                        //更新小红点
                                        updateLookTaskStatus(data.getCommitTaskId(), data.isRead());
                                        //打开微课详情页面
                                        //拼装
                                        StudyTask myTask = new StudyTask();
                                        myTask.setResId(data.getStudentResId());
                                        Bundle arguments = new Bundle();
                                        arguments.putBoolean(ActivityUtils.EXTRA_IS_NEED_HIDE_COLLECT_BTN, false);

                                        if (taskType == StudyTaskType.TASK_ORDER || taskType == StudyTaskType.RETELL_WAWA_COURSE) {
                                            loadMarkData(arguments, data, myTask, false);//加载最新数据
                                        } else {
                                            CourseOpenUtils.openCourseDetailsDirectly(getActivity(),
                                                    myTask, roleType, getMemeberId(), studentId, null,
                                                    false, arguments);
                                        }
                                    }
                                }
                            }
                        });

                        view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //更新小红点
                                updateLookTaskStatus(data.getCommitTaskId(), data.isRead());
                                if (isAnswerTaskOrderQuestion) {
                                    enterStudentAnswerDetailActivity(data, false);
                                } else if (data.isEvalType()) {
                                    enterTeacherReviewDetailActivity(data, false);
                                } else if (data.isVideoType()) {
                                    enterQDubbingDetailActivity(data);
                                } else {
                                    if ((taskType == StudyTaskType.TASK_ORDER || taskType == StudyTaskType.RETELL_WAWA_COURSE)) {
                                        //老师只能批阅自己布置的作业,学生只能提问自己的作业
                                        //听说课  读写单
                                        loadMarkData(null, data, null, true);//加载最新数据
                                    } else if (taskType == StudyTaskType.ENGLISH_WRITING) {
                                        //英文写作点评记录页面
                                        englishWritingPageSkip(data);
                                    } else {
                                        openImage(data);
                                    }
                                }
                            }
                        });

                        ImageView deleteImageV = (ImageView) view.findViewById(R.id.iv_delete_item);
                        if (deleteImageV != null) {
                            if (data.isShowDeleted()) {
                                deleteImageV.setVisibility(View.VISIBLE);
                            } else {
                                deleteImageV.setVisibility(View.GONE);
                            }
                            deleteImageV.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    upDateDeleteButtonShowStatus(null, true);
                                    deleteData(data);
                                }
                            });
                        }
                        view.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {

                                ViewHolder holder = (ViewHolder) view.getTag();
                                if (holder == null) {
                                    return false;
                                }

                                final CommitTask data = (CommitTask) holder.data;
                                if (data != null) {

                                    if (isOnlineReporter || isOnlineHost || (isHeadMaster && roleType != RoleType.ROLE_TYPE_PARENT) ||
                                            (roleType == RoleType.ROLE_TYPE_TEACHER && task.getCreateId().equalsIgnoreCase(getMemeberId()))) {
                                        //小编主编班主任可以删除全部
//                                        deleteDialog(data);

//                                        showDeletePopwindow(data,viewAnchor);
                                        upDateDeleteButtonShowStatus(data, true);

                                        return true;
                                    }
                                }
                                return false;
                            }
                        });

                        view.setTag(holder);
                        return view;
                    }

                    @Override
                    public void onItemClick(AdapterView parent, View view, int position, long id) {
                        super.onItemClick(parent, view, position, id);
                        ViewHolder holder = (ViewHolder) view.getTag();
                        if (holder == null) {
                            return;
                        }
                        CommitTask data = (CommitTask) holder.data;
                        if (data != null) {
                            //更新小红点
                            updateLookTaskStatus(data.getCommitTaskId(), data.isRead());

                            if ((taskType == StudyTaskType.TASK_ORDER || taskType == StudyTaskType.RETELL_WAWA_COURSE)) {
                                //老师只能批阅自己布置的作业,学生只能提问自己的作业
                                //听说课  读写单

                                loadMarkData(null, data, null, true);//加载最新数据
                            } else if (taskType == StudyTaskType.ENGLISH_WRITING) {
                                //英文写作点评记录页面
                                englishWritingPageSkip(data);

                            } else {
                                openImage(data);
                            }

                        }
                    }

                    @Override
                    protected void updateLookTaskStatus(int commitTaskId, boolean isRead) {
                        //只有发布的老师查看学生作业才调,过滤班主任。
                        if (task != null
                                && !TextUtils.isEmpty(task.getTaskCreateId())
                                && !TextUtils.isEmpty(getMemeberId())) {
                            if (!isRead && (task.getTaskCreateId().equals(getMemeberId()) || isOnlineReporter || isOnlineHost)) {
                                updateStatus(commitTaskId);
                            }
                        }
                    }
                };

                setCurrAdapterViewHelper(listView, listViewHelper);
            }
        }

        //提交列表
        commitBtn = (TextView) findViewById(R.id.retell_course_btn);
        mCommitBtnFl = findViewById(R.id.fl_retell_course_btn);

        if (commitBtn != null) {
            commitBtn.setOnClickListener(new NoDoubleClickListener() {
                @Override
                protected void onNoDoubleClick(View v) {
                    controlCommitClickEventByTaskType(taskType);
                }
            });
            if (shouldShowCommitBtn && !isHistoryClass) {
                initCommitBtnTextByTaskType(taskType, commitBtn);
            } else {
                //默认是隐藏的
                mCommitBtnFl.setVisibility(View.GONE);
            }
        }
        //成绩统计
        mStatisticFl = findViewById(R.id.fl_statistic);

        findViewById(R.id.tv_statistic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeworkCommitFragment parentFragment = (HomeworkCommitFragment) getParentFragment();
                parentFragment.enterAchievementStatistics();

            }
        });
    }

    public void upDateDeleteButtonShowStatus(CommitTask data, boolean onClick) {
        if (data == null && !deleteBtnVisibled) {
            return;
        }
        List<CommitTask> listData = getCurrAdapterViewHelper().getData();
        if (listData != null) {
            boolean flag = false;
            for (int i = 0, len = listData.size(); i < len; i++) {
                CommitTask task = listData.get(i);
                if (data == null) {
                    task.setShowDeleted(false);
                } else {
                    if (data.getCommitTaskId() == task.getCommitTaskId()) {
                        task.setShowDeleted(true);
                        flag = true;
                    } else {
                        task.setShowDeleted(false);
                    }
                }
            }
            deleteBtnVisibled = flag;
            if (onClick) {
                getCurrAdapterViewHelper().update();
            } else {
                rootView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getCurrAdapterViewHelper().update();
                    }
                }, 200);
            }

        }
    }

    private void deleteDialog(final CommitTask data) {
        ContactsMessageDialog messageDialog = new ContactsMessageDialog(getActivity(), null,
                getString(R.string.confirm_delete)
                , getString(R.string.cancel),
                null, getString(R.string.confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                deleteData(data);
            }
        });
        messageDialog.show();
    }

    private void showDeletePopwindow(final CommitTask data, View anchor) {
        if (mPopWindow == null) {
            View contentView = LayoutInflater.from(getContext()).inflate(R.layout.pop_delete, null);
            //处理popWindow 显示内容
            contentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteData(data);

                }
            });

            mPopWindow = new CustomPopWindow.PopupWindowBuilder(getContext())
                    //显示的布局，还可以通过设置一个View
                    .setView(contentView)
                    //创建PopupWindow
                    .create();
        }

        //水平偏移量
        int height = mPopWindow.getHeight();
        int[] location = new int[2];
        anchor.getLocationInWindow(location);
        mPopWindow.showAtLocation(anchor, Gravity.LEFT | Gravity.BOTTOM, location[0], location[1] + height);
    }

    /**
     * //小编主编班主任可以删除全部
     *
     * @param data
     */
    private void deleteData(final CommitTask data) {
        if (getUserInfo() == null) {
            return;
        }
        final Map<String, Object> params = new ArrayMap<>();
        params.put("Id", data.getId());
        final DefaultDataListener listener =
                new DefaultDataListener<DataModelResult>(DataModelResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        if (getResult() != null && getResult().isSuccess()) {
                            TipMsgHelper.ShowLMsg(getMyApplication(), R.string.delete_success);
                            LogUtils.logd(TAG, " == " + getCurrAdapterViewHelper().getData().size());
                            getCurrAdapterViewHelper().getData().remove(data);
                            LogUtils.logd(TAG, " == " + getCurrAdapterViewHelper().getData().size());
                            getCurrAdapterViewHelper().update();
                            HomeworkCommitFragment parentFragment = (HomeworkCommitFragment) getParentFragment();
                            if (parentFragment != null) {
                                parentFragment.updateFinishCount();
                            }
                            //更新完成状态数据
                            EventBus.getDefault().post(new MessageEvent("load_study_task_data"));
                        } else {
                            TipMsgHelper.ShowLMsg(getMyApplication(), R.string.delete_failure);

                        }
                    }
                };
        listener.setShowLoading(true);
        String url;
        if (taskType == StudyTaskType.ENGLISH_WRITING) {
            url = ServerUrl.POST_REMOVE_ENGLISH_WRITING_URL;
        } else {
            url = ServerUrl.POST_REMOVE_COMMIT_TASK_URL;
        }
        postRequest(url, params, listener);
    }

    /**
     * 拉取最新批阅提问数据
     *
     * @param arguments
     * @param data
     * @param myTask
     */
    private void loadMarkData(final Bundle arguments, final CommitTask data, final StudyTask myTask, final boolean isPic) {

        Map<String, Object> params = new ArrayMap<>();

        params.put("CommitTaskId", data.getCommitTaskId() + "");
        DefaultPullToRefreshDataListener<CheckMarkResult> listener = new DefaultPullToRefreshDataListener<CheckMarkResult>(
                CheckMarkResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                if (getActivity() == null) {
                    return;
                }
                //                            super.onSuccess(jsonString);
                CheckMarkInfo result = JSONObject.parseObject(jsonString,
                        CheckMarkInfo.class);
                if (result.getModel() == null || result.getModel().size() == 0) {
                    needShow = true;
                } else {
                    needShow = false;
                }
                if (result.getErrorCode() != 0 || result.getModel() == null) {
                    return;
                }

                if (((roleType == RoleType.ROLE_TYPE_TEACHER) && (task.getCreateId().equalsIgnoreCase(getMemeberId())))
                        || roleType == RoleType.ROLE_TYPE_STUDENT || isOnlineReporter || isOnlineHost) {
                    //老师只能批阅自己布置的作业,学生可以互相提问
                    //听说课  读写单

                    boolean isNeedMark = true;
                    if (task.getScoringRule() == 0) {//不打分
                        isNeedMark = false;
                    }

                    if (isPic) {//点击图片
                        if (result.getModel().size() > 0) {
                            openCourse(data,
                                    isNeedMark,
                                    result.getModel().get(0).getResId(),
                                    isHistoryClass ? true : false);
                        } else {
                            openCourse(data,
                                    isNeedMark,
                                    null,
                                    isHistoryClass ? true : false);
                        }

                        return;
                    }

                    arguments.putSerializable(PictureBooksDetailFragment.Constants.ACTION_TASKMARKPARAM,
                            new TaskMarkParam(data.isHasCommitTaskReview(),
                                    task.getScoringRule() == 2, getTempRoleType(),
                                    data.getCommitTaskId() + "",
                                    false, isNeedMark,
                                    data.getTaskScore(), false));

                } else {

                    //游客身份
                    if (isPic) {//点击图片
                        if (result.getModel().size() > 0) {
                            openCourse(data, false, result.getModel().get(0).getResId(), true);
                        } else {
                            openCourse(data, false, null, true);
                        }

                        return;
                    }

                }
                if (result.getModel().size() > 0) {
                    myTask.setResId(result.getModel().get(0).getResId());
                }
                CourseOpenUtils.openCourseDetailsDirectly(getActivity(),
                        myTask, roleType, getMemeberId(), studentId, null,
                        false, arguments);


            }

            @Override
            public void onFinish() {
                dismissLoadingDialog();
            }

            @Override
            public void onPreExecute() {
                showLoadingDialog();
            }
        };

        RequestHelper.sendPostRequest(getActivity(),
                ServerUrl.GET_LOADCOMMITTASKREVIEWLIST, params, listener);

    }


    /**
     * 更新操作
     *
     * @param data
     */
    private void updateLocalData(ResourceInfoTag data) {
        if (data == null) {
            return;
        }
        try {
            //设置已读
            data.setHasRead(true);
            String taskId = data.getTaskId();
            NewWatchWawaCourseResourceDTO dto = new NewWatchWawaCourseResourceDTO();
            //ids
            String ids = getItemIds(getCurrAdapterViewHelper().getData());
            dto.setIds(ids);
            //read标识
            readAll = checkReadAll();
            dto.setReadAll(readAll);
            NewWatchWawaCourseResourceDao.getInstance(getActivity()).updateResource(taskId,
                    studentId, dto);
            if (readAll && !isHistoryClass) {
                //全部读取了，学生需要调取更新任务已读接口。
                if (shouldCommitByRoleType()) {
                    updateReadState(taskId, studentId);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新看微课、看课件、看作业已读
     *
     * @param taskId
     * @param memberId
     */
    private void updateReadState(String taskId, String memberId) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("TaskId", taskId);
        params.put("StudentId", memberId);
        RequestHelper.RequestListener listener =
                new RequestHelper.RequestDataResultListener<DataModelResult>(
                        getActivity(), DataModelResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        if (getResult() == null || !getResult().isSuccess()) {
                            return;
                        } else {
                            //设置任务已读
                            hasReadTask = true;
                            //通知父页面刷新
                            notifyParent();
                            //删除本地数据
                            deleteLocalResource();
                            //更新综合任务
                            IntroductionSuperTaskFragment.sendBroadCast(getActivity());
                        }
                    }
                };
        listener.setShowLoading(true);
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.STUDENT_COMMIT_HOMEWORK_URL,
                params, listener);

    }

    /**
     * 获取数据库存储json数据
     *
     * @return
     */
    private String getItemIds(List<ResourceInfoTag> resourceInfoTagList) {
        if (resourceInfoTagList != null && resourceInfoTagList.size() > 0) {
            return JSON.toJSONString(resourceInfoTagList);
        }
        return null;
    }

    /**
     * 是否全部阅读
     *
     * @return
     */
    private boolean checkReadAll() {
        List<ResourceInfoTag> list = getCurrAdapterViewHelper().getData();
        if (list != null && list.size() > 0) {
            boolean readAll = true;
            for (ResourceInfoTag tag : list) {
                if (tag != null) {
                    boolean readItem = tag.isHasRead();
                    if (!readItem) {
                        readAll = false;
                        break;
                    }
                }
            }
            return readAll;
        }
        return false;
    }

    private boolean isNewWawaCourse() {
        return taskType == StudyTaskType.NEW_WATACH_WAWA_COURSE
                && !fromHomeworkFinishStatusList;
    }

    /**
     * 更新tab数据
     *
     * @param result
     */
    public void updateTabData(HomeworkCommitObjectResult result) {
        //看课件数据
        if (isNewWawaCourse()) {
            if (result != null) {
                task = result.getModel().getData().getTaskInfo();
            }
            buildResourceInfoTagData();
        } else {
            updateViews(result);
        }
    }

    private void initCommitBtnTextByTaskType(final int taskType, TextView commitBtn) {
        if (commitBtn == null) {
            return;
        }
        String result = "";
        if (roleType == RoleType.ROLE_TYPE_STUDENT || roleType == RoleType.ROLE_TYPE_PARENT) {
            mCommitBtnFl.setVisibility(View.VISIBLE);
            if (taskType == StudyTaskType.INTRODUCTION_WAWA_COURSE
                    || taskType == StudyTaskType.SUBMIT_HOMEWORK) {
                result = getString(R.string.commit_task);
            } else if (taskType == StudyTaskType.RETELL_WAWA_COURSE) {
                result = getString(R.string.retell_course_new);
            } else if (taskType == StudyTaskType.ENGLISH_WRITING) {
                //英文写作
                result = getString(R.string.start_writing);
                //先隐藏，防止闪现。
                mCommitBtnFl.setVisibility(View.GONE);
            } else if (taskType == StudyTaskType.TASK_ORDER) {
                result = getString(R.string.do_task);
            } else if (taskType == StudyTaskType.Q_DUBBING) {
                result = getString(R.string.str_start_dubbing);
            } else {
                mCommitBtnFl.setVisibility(View.GONE);
            }
            commitBtn.setText(result);
        } else {
            mCommitBtnFl.setVisibility(View.GONE);
        }
    }

    /**
     * 打开图片
     *
     * @param data
     */
    private void openImage(CommitTask data) {
        if (data == null) {
            return;
        }
        //直接打开
        CourseOpenUtils.openCourseDirectly(getActivity(), data.getStudentResId(), true);
    }


    /**
     * 打开课件
     */
    public void openCourse(final CommitTask data, final boolean isNeedMark, String resId, final boolean isVisiter) {
        String courseId = resId;
        if (TextUtils.isEmpty(courseId)) {
            courseId = data.getStudentResId();
        }
        String tempResId = courseId;
        int resType = 0;
        if (courseId.contains("-")) {
            String[] ids = courseId.split("-");
            if (ids != null && ids.length == 2) {
                tempResId = ids[0];
                if (ids[1] != null) {
                    resType = Integer.parseInt(ids[1]);
                }
            }
        }
        if (resType > ResType.RES_TYPE_BASE) {
            //分页信息
            if (TextUtils.isEmpty(tempResId)) {
                return;
            }
            WawaCourseUtils wawaCourseUtils = new WawaCourseUtils(getActivity());
            wawaCourseUtils.loadSplitCourseDetail(Integer.parseInt(tempResId));
            wawaCourseUtils.setOnSplitCourseDetailFinishListener(new WawaCourseUtils
                    .OnSplitCourseDetailFinishListener() {
                @Override
                public void onSplitCourseDetailFinish(SplitCourseInfo info) {
                    if (info != null) {
                        CourseData courseData = info.getCourseData();
                        if (courseData != null) {
                            processOpenImageData(courseData, data, isNeedMark, isVisiter);
                        }
                    }
                }
            });
        } else {
            //非分页信息
            WawaCourseUtils wawaCourseUtils = new WawaCourseUtils(getActivity());
            wawaCourseUtils.loadCourseDetail(courseId);
            wawaCourseUtils.setOnCourseDetailFinishListener(new WawaCourseUtils.
                    OnCourseDetailFinishListener() {
                @Override
                public void onCourseDetailFinish(CourseData courseData) {
                    processOpenImageData(courseData, data, isNeedMark, isVisiter);
                }
            });
        }
    }

    /**
     * 打开逻辑
     *
     * @param courseData
     */
    private void processOpenImageData(CourseData courseData, CommitTask data, boolean isNeedMark, boolean isVisiter) {
        if (courseData != null) {
            NewResourceInfo newResourceInfo = courseData.getNewResourceInfo();
            PlaybackParam playbackParam = null;
            if (!isVisiter) {
                playbackParam = new PlaybackParam();
                playbackParam.taskMarkParam = new TaskMarkParam(data.isHasCommitTaskReview(),
                        task.getScoringRule() == 2, getTempRoleType(), data.getCommitTaskId() +
                        "", isVisiter, isNeedMark, data.getTaskScore(), false);
            } else {
                playbackParam = new PlaybackParam();

                playbackParam.mIsHideToolBar = true;


            }
            if (newResourceInfo.isOnePage() || newResourceInfo.isStudyCard()) {
                ActivityUtils.openOnlineOnePage(getActivity(), newResourceInfo, false, playbackParam);
            } else {
                if (needShow) {
                    ActivityUtils.playOnlineCourse(getActivity(), newResourceInfo.getCourseInfo(), data,
                            false, playbackParam);
                } else {
                    ActivityUtils.playOnlineCourse(getActivity(), newResourceInfo.getCourseInfo(), false, playbackParam);
                }

            }

        }

    }

    private int getTempRoleType() {
        int tempRoleType = roleType;
        if (isOnlineReporter) {
            //空中课堂 的小编
            tempRoleType = RoleType.ROLE_TYPE_TEACHER;
        } else if (isOnlineHost) {
            //空中课堂 的主持人
            tempRoleType = RoleType.ROLE_TYPE_EDITOR;
        }
        return tempRoleType;
    }

    private void englishWritingPageSkip(CommitTask data) {
        if (data == null) {
            return;
        }
        if (roleType == RoleType.ROLE_TYPE_TEACHER) {
            //老师进入点评记录
            enterEnglishWritingCommentRecordActivity(data);
        } else if (roleType == RoleType.ROLE_TYPE_STUDENT
                || roleType == RoleType.ROLE_TYPE_PARENT) {
            //学生和家长进入评论详情页面
            enterEnglishWritingDetails(data);
        }
    }

    /**
     * 进入英文写作的详情界面
     *
     * @param data
     */
    private void enterEnglishWritingDetails(CommitTask data) {
        Intent intent = new Intent(getActivity(), EnglishWritingCommentDetailsActivity.class);
        Bundle bundle = getArguments();
        if (bundle != null) {
            //复用之前的bundle，需要先清除之前的老数据。
            if (bundle.containsKey(EnglishWritingCompletedFragment.Constant.STUDENTID)) {
                bundle.remove(EnglishWritingCompletedFragment.Constant.STUDENTID);
            }
            if (bundle.containsKey(EnglishWritingCompletedFragment.Constant.SORTSTUDENTID)) {
                bundle.remove(EnglishWritingCompletedFragment.Constant.SORTSTUDENTID);
            }
            String studentId = data.getStudentId();
            //单个孩子
            if (!TextUtils.isEmpty(studentId)) {
                bundle.putString(EnglishWritingCompletedFragment.Constant.STUDENTID, studentId);
                bundle.putString(EnglishWritingCompletedFragment.Constant.SORTSTUDENTID, studentId);
                boolean isTaskBelongsToChildrenOrOwner = isTaskBelongsToChildrenOrOwner(studentId);
                bundle.putBoolean(EnglishWritingCompletedFragment.Constant.
                        IS_TASK_BELONGS_TO_CHILDREN_OR_OWNER, isTaskBelongsToChildrenOrOwner);
            }
            bundle.putBoolean(ActivityUtils.EXTRA_IS_HISTORY_CLASS, isHistoryClass);
            intent.putExtras(bundle);
        }
        getActivity().startActivityForResult(intent, CampusPatrolPickerFragment
                .REQUEST_CODE_ENGLISH_WRITING_COMMIT);
    }

    /**
     * 判断作业是否属于自己的或者是孩子的
     *
     * @param studentId 学生id
     * @return
     */
    private boolean isTaskBelongsToChildrenOrOwner(String studentId) {

        boolean isTaskBelongsToChildrenOrOwner = false;
        //家长需要判断孩子是不是自己的
        if (roleType == RoleType.ROLE_TYPE_STUDENT) {
            if (!TextUtils.isEmpty(studentId)
                    && !TextUtils.isEmpty(getMemeberId())
                    && studentId.equals(getMemeberId())) {
                //是自己的可以编辑
                isTaskBelongsToChildrenOrOwner = true;
            }
        } else if (roleType == RoleType.ROLE_TYPE_PARENT) {
            if (childIdArray != null && childIdArray.length > 0) {
                for (int i = 0; i < childIdArray.length; i++) {
                    String childId = childIdArray[i];
                    if (!TextUtils.isEmpty(childId)
                            && !TextUtils.isEmpty(studentId)
                            && childId.equals(studentId)) {
                        //找到是自己的孩子，可以编辑。
                        isTaskBelongsToChildrenOrOwner = true;
                        break;
                    }
                }
            }
        }
        return isTaskBelongsToChildrenOrOwner;
    }

    /**
     * 进入点评记录页面
     *
     * @param data
     */
    private void enterEnglishWritingCommentRecordActivity(CommitTask data) {
        if (data == null) {
            return;
        }
        Intent intent = new Intent(getActivity(), EnglishWritingCommentRecordActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("roleType", roleType);
        bundle.putBoolean("isOnlineReporter", isOnlineReporter || isOnlineHost);
        String studentId = data.getStudentId();
        if (!TextUtils.isEmpty(studentId)) {
            bundle.putString(EnglishWritingCompletedFragment.Constant.STUDENTID,
                    data.getStudentId());
            bundle.putString(EnglishWritingCompletedFragment.Constant.SORTSTUDENTID,
                    data.getStudentId());
        }
        //任务的id。
        bundle.putInt(EnglishWritingCompletedFragment.Constant.TASKID, data.getTaskId());
        bundle.putBoolean(ActivityUtils.EXTRA_IS_HISTORY_CLASS, isHistoryClass);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void updateStatus(final int commitTaskId) {

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("CommitTaskId", commitTaskId);
        //英文写作等类型更新
        params.put("TaskType", taskType);
        RequestHelper.RequestListener listener =
                new RequestHelper.RequestDataResultListener<DataModelResult>(
                        getActivity(), DataModelResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        if (getResult() == null || !getResult().isSuccess()) {
                            return;
                        } else {
                            //refresh
                            setHasContentChanged(true);
                            if (getCurrAdapterViewHelper().hasData()) {
                                List<CommitTask> list = getCurrAdapterViewHelper().getData();
                                for (CommitTask task : list) {
                                    if (task.getCommitTaskId() == commitTaskId) {
                                        task.setIsRead(true);
                                        break;
                                    }
                                }
                                getCurrAdapterViewHelper().update();
                            }
                        }
                    }
                };
        listener.setShowLoading(true);
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.UPDATE_TEACHER_READ_TASK_URL,
                params, listener);

    }

    private void loadViews() {
        loadCommonData();
    }

    /**
     * 模拟数据
     */
    private void loadCommonData() {
        if (isNewWawaCourse()) {
            buildResourceInfoTagData();
        }
        Map<String, Object> params = new HashMap();
        //学校Id，必填
        params.put("TaskId", taskId);
        //非必填,如果填写就把该学生对应的任务排在前面,支持多个ID排序,多个ID时用逗号分隔传值.
        params.put("SortStudentId", sortStudentId);
        RequestHelper.sendPostRequest(getActivity(),
                ServerUrl.GET_COMMITTED_TASK_BY_TASK_ID_URL, params,
                new DefaultPullToRefreshDataListener<HomeworkCommitObjectResult>(
                        HomeworkCommitObjectResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        if (getResult() == null || !getResult().isSuccess()
                                || getResult().getModel() == null) {
                            return;
                        }

                        updateViews(getResult());
                    }
                });
    }

    /**
     * 根据角色判断是否能够提交
     *
     * @return
     */
    private boolean shouldCommitByRoleType() {
        boolean shouldCommit = false;
        //目前看课件只有学生和家长（替孩子提交）才能提交
        //目前只放开学生
        if (roleType == RoleType.ROLE_TYPE_STUDENT) {
            shouldCommit = true;
        }
        return shouldCommit;
    }

    /**
     * 是否需要更新数据或者显示小红点。
     *
     * @return
     */
    private boolean shouldShowRedPointOrUpdateData(ResourceInfoTag tag) {
        boolean needUpdate = false;
        //允许操作数据库
        if (needToOperationDatabase()) {
            //没有完全阅读
            if (!readAll) {
                //并且当前阅读状态是未读状态。
                if (tag != null && !tag.isHasRead()) {
                    needUpdate = true;
                }
            }
        }
        return needUpdate;
    }

    /**
     * 构建看课件数据
     */
    private void buildResourceInfoTagData() {
        if (lookResDtoList != null && lookResDtoList.size() > 0) {
            //组装数据
            this.resourceInfoTagList.clear();
            for (LookResDto dto : lookResDtoList) {
                if (dto != null) {
                    ResourceInfoTag tag = dto.toResourceInfoTag();
                    if (tag != null) {
                        this.resourceInfoTagList.add(tag);
                    }
                }
            }
            //操作数据库
            operationDatabase();
            //最终展示
            getCurrAdapterViewHelper().setData(resourceInfoTagList);
        }
    }

    /**
     * 是否需要显示未读状态
     *
     * @return
     */
    private boolean shouldDisplayUnreadStatus(ResourceInfoTag tag) {

        boolean shouldDisplay = false;
        //任务没完成，并且是学生家长角色。
        if (!hasReadTask &&
                (roleType == RoleType.ROLE_TYPE_STUDENT
                        || roleType == RoleType.ROLE_TYPE_PARENT)) {
            //没有完全阅读
            if (!readAll) {
                //并且当前阅读状态是未读状态。
                if (tag != null && !tag.isHasRead()) {
                    shouldDisplay = true;
                }
            }
        }
        return shouldDisplay;
    }

    /**
     * 是否需要同步数据库数据
     *
     * @return
     */
    private boolean shouldSyncDatabaseData() {

        boolean shouldSync = false;
        //任务没完成，并且是学生家长角色。
        if (!hasReadTask &&
                (roleType == RoleType.ROLE_TYPE_STUDENT
                        || roleType == RoleType.ROLE_TYPE_PARENT)) {
            shouldSync = true;
        }
        return shouldSync;
    }

    /**
     * 判断是否需要操作数据库
     *
     * @return
     */
    private boolean needToOperationDatabase() {
        //任务没完成，并且是学生家长角色。
        //目前只放开学生
        return !hasReadTask && shouldCommitByRoleType();
    }

    /**
     * 操作数据库
     */
    private void operationDatabase() {
        //学生和家长才保存数据
        //学生和家长需要同步数据库数据
        if (shouldSyncDatabaseData()) {
            //先检查本地是否有数据
            boolean hasData = false;
            NewWatchWawaCourseResourceDTO targetDto = getLocalResource();
            if (targetDto != null) {
                hasData = true;
                readAll = targetDto.isReadAll();
            }
            //有数据且没有完全阅读时才需要更新数据。
            if (hasData) {
                if (!readAll) {
                    //映射本地数据库数据
                    transferDtoToResourceInfoTagData(targetDto);
                }
            } else {
                //没数据需要存储数据
                NewWatchWawaCourseResourceDTO resourceDTO = new NewWatchWawaCourseResourceDTO();
                resourceDTO.setTaskId(taskId);
                //学生id，用来区分学生。
                resourceDTO.setStudentId(studentId);
                String ids = getItemIds(resourceInfoTagList);
                resourceDTO.setIds(ids);
                resourceDTO.setReadAll(false);
                //保存到数据库
                saveResourceToLocal(resourceDTO);
            }
        }
    }

    /**
     * 提醒父页面刷新
     */
    private void notifyParent() {
        Fragment parentFragment = getParentFragment();
        if (parentFragment != null && parentFragment instanceof HomeworkCommitFragment) {
            //调用父类的提交方法
            ((HomeworkCommitFragment) parentFragment).notifyDataSetChanged();
        }
    }

    /**
     * 删除资源
     */
    private void deleteLocalResource() {
        try {
            NewWatchWawaCourseResourceDao.getInstance(getActivity())
                    .deleteResource(taskId, studentId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 转换数据
     *
     * @param targetDto
     * @return
     */
    private void transferDtoToResourceInfoTagData(NewWatchWawaCourseResourceDTO targetDto) {
        if (targetDto != null) {
            String ids = targetDto.getIds();
            if (!TextUtils.isEmpty(ids)) {
                List<ResourceInfoTag> resultList = JSON.parseArray(ids, ResourceInfoTag.class);
                if (resultList != null && resultList.size() > 0) {
                    if (resourceInfoTagList != null && resourceInfoTagList.size() > 0) {
                        for (ResourceInfoTag tag : resourceInfoTagList) {
                            if (tag != null) {
                                for (ResourceInfoTag result : resultList) {
                                    if (result != null) {
                                        if (!TextUtils.isEmpty(tag.getId())
                                                && !TextUtils.isEmpty(result.getId())
                                                && tag.getId().equals(result.getId())) {
                                            //是同一条记录：从数据库取数据赋值
                                            tag.setHasRead(result.isHasRead());
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 判断本地数据是否已读
     */
    private NewWatchWawaCourseResourceDTO getLocalResource() {
        try {
            NewWatchWawaCourseResourceDTO resultDto = NewWatchWawaCourseResourceDao
                    .getInstance(getActivity()).queryResource(taskId, studentId);
            return resultDto;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 保存看课件资源到数据库
     *
     * @param resourceDTO
     */
    private void saveResourceToLocal(NewWatchWawaCourseResourceDTO resourceDTO) {
        try {
            NewWatchWawaCourseResourceDao.getInstance(getActivity()).addResource(resourceDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateViews(HomeworkCommitObjectResult result) {
        if (result == null) {
            return;
        }
        HomeworkCommitObjectInfo homeworkCommitObjectInfo = result.getModel().getData();
        if (homeworkCommitObjectInfo != null) {
            dealTaskTypeFinishDetail(homeworkCommitObjectInfo);
            task = homeworkCommitObjectInfo.getTaskInfo();
            if (!isAnswerTaskOrderQuestion && task != null) {
                //答题卡的信息
                if (task.getResPropType() == 1) {
                    isAnswerTaskOrderQuestion = true;
                }
            }
            //显示的时候才更新
            if (shouldShowCommitBtn) {
                updateCommitBtn(homeworkCommitObjectInfo);
            }
            if (task.getScoringRule() != 0) {
                //复述课件,任务单显示成绩统计  打分
                if (taskType == StudyTaskType.TASK_ORDER
                        || taskType == StudyTaskType.RETELL_WAWA_COURSE
                        || taskType == StudyTaskType.Q_DUBBING) {
                    mStatisticFl.setVisibility(View.VISIBLE);
                } else {
                    mStatisticFl.setVisibility(View.GONE);
                }
            } else {
                if (taskType == StudyTaskType.RETELL_WAWA_COURSE && propertiesType == 1) {
                    //语音评测课件
                    mStatisticFl.setVisibility(View.VISIBLE);
                } else {
                    mStatisticFl.setVisibility(View.GONE);
                }
            }
        }

        List<CommitTask> list = result.getModel().getData().getListCommitTask();
        if (list == null || list.size() <= 0) {
            return;
        } else {
            List<CommitTask> resultList = list;
            if (needFilterData) {
                resultList = filterData(resultList);
            }
            resultList = filterEvalData(resultList);
            int containsCount = 0;
            for (int i = 0; i < resultList.size(); i++) {
                CommitTask data = resultList.get(i);
                if (data != null) {
                    //后台返回数据默认学生自己或者是家长的孩子排在上面，下面是其他的学生或者其他的家长的孩子。
                    if (roleType == RoleType.ROLE_TYPE_STUDENT) {
                        boolean isContains = false;
                        isContains = !TextUtils.isEmpty(data.getStudentId()) &&
                                data.getStudentId().equals(getMemeberId());
                        if (isContains) {
                            containsCount++;
                        }
                        if (containsCount > 0 && !isContains) {
                            //其他的学生
                            data.setNeedSplit(true);
                            break;
                        }

                    } else if (roleType == RoleType.ROLE_TYPE_PARENT) {
                        boolean isContains = false;
                        isContains = !TextUtils.isEmpty(data.getStudentId()) && (
                                !TextUtils.isEmpty(sortStudentId) && sortStudentId
                                        .contains(data.getStudentId()));
                        if (isContains) {
                            containsCount++;
                        }
                        if (containsCount > 0 && !isContains) {
                            //其他家长的孩子
                            data.setNeedSplit(true);
                            break;
                        }
                    }
                }
            }

            //提交作业列表
            AdapterViewHelper adapterViewHelper = getCurrAdapterViewHelper();
            if (adapterViewHelper != null) {
                adapterViewHelper.setData(resultList);
            }
        }
    }

    private void dealTaskTypeFinishDetail(HomeworkCommitObjectInfo homeworkCommitObjectInfo){
        Fragment fragment = getParentFragment();
        if (fragment instanceof HomeworkCommitFragment){
            ((HomeworkCommitFragment) fragment).dealTaskTypeFinishDetail(homeworkCommitObjectInfo);
        }
    }

    /**
     * 判断是否需要更新提交按钮的状态
     *
     * @return
     */
    private boolean needUpdateCommitBtnState() {
        boolean shouldUpdate = false;
        if (roleType == RoleType.ROLE_TYPE_STUDENT
                || roleType == RoleType.ROLE_TYPE_PARENT) {
            if (taskType == StudyTaskType.ENGLISH_WRITING) {
                shouldUpdate = true;
            }
        }
        return shouldUpdate;
    }

    /**
     * 更新提交按钮：英文写作需要更新。
     *
     * @param data
     */
    private void updateCommitBtn(HomeworkCommitObjectInfo data) {
        if (isHistoryClass) {
            mCommitBtnFl.setVisibility(View.GONE);
            return;
        }
        boolean hiddenCommitBtn = false;
        //学生、家长英文写作才更新。
        if (needUpdateCommitBtnState()) {
            if (data != null) {
                List<CommitTask> commitTask = data.getListCommitTask();
                if (commitTask != null && commitTask.size() > 0) {
                    for (CommitTask task : commitTask) {
                        if (task != null) {
                            String childId = task.getStudentId();
                            if (!TextUtils.isEmpty(childId)
                                    && !TextUtils.isEmpty(studentId)
                                    && childId.equals(studentId)) {
                                //找到自己提交的信息，不能再写了。
                                hiddenCommitBtn = true;
                                break;
                            }
                        }
                    }
                }
            }
            mCommitBtnFl.setVisibility(hiddenCommitBtn ? View.GONE : View.VISIBLE);
        }
        //老师角色更新按钮状态
        if (roleType == RoleType.ROLE_TYPE_TEACHER) {
            //老师角色，如果有任务单，则显示“查看任务单”，否则不显示。
            if (taskType == StudyTaskType.TASK_ORDER
                    || taskType == StudyTaskType.INTRODUCTION_WAWA_COURSE) {
                if (task != null) {
                    if (!TextUtils.isEmpty(task.getWorkOrderId())) {
                        //有任务单
                        mCommitBtnFl.setVisibility(View.VISIBLE);
                        //查看任务单
                        commitBtn.setText(getString(R.string.watch_task));
                    } else {
                        mCommitBtnFl.setVisibility(View.GONE);
                    }
                }
            }
        }
    }

    /**
     * 过滤其他人
     *
     * @param list
     */
    private List<CommitTask> filterData(List<CommitTask> list) {
        if (list == null || list.size() <= 0) {
            return null;
        }
        List<CommitTask> resultList = new ArrayList<>();

        for (CommitTask commitTask : list) {
            if (commitTask != null) {
                String childId = commitTask.getStudentId();
                if (!TextUtils.isEmpty(childId) && !TextUtils.isEmpty(studentId)) {
                    if (!childId.equals(studentId)) {
                        continue;
                    }
                    //找到了
                    resultList.add(commitTask);
                }
            }
        }
        return resultList;
    }

    private List<CommitTask> filterEvalData(List<CommitTask> list) {
        if (list == null || list.size() <= 0) {
            return null;
        }
        List<CommitTask> resultList = new ArrayList<>();

        for (CommitTask commitTask : list) {
            if (commitTask != null) {
                if (!commitTask.isEvalType()) {
                    resultList.add(commitTask);
                }
            }
        }
        return resultList;
    }


    public static void setHasContentChanged(boolean hasContentChanged) {
        CompletedHomeworkListFragment.hasContentChanged = hasContentChanged;
    }

    public static boolean hasContentChanged() {
        return hasContentChanged;
    }


    /**
     * 按照类型提交
     *
     * @param taskType
     */
    private void controlCommitClickEventByTaskType(int taskType) {
        Fragment parentFragment = getParentFragment();
        if (parentFragment != null && parentFragment instanceof HomeworkCommitFragment) {
            //调用父类的提交方法
            if (isViewTaskOrder()) {
                //老师查看任务单
                ((HomeworkCommitFragment) parentFragment).takeTask(false);
            } else {
                //学生、家长提交按钮逻辑
                ((HomeworkCommitFragment) parentFragment).
                        controlCommitClickEventByTaskType(taskType);
            }
        }
    }

    /**
     * 判断是否是查看任务单：仅老师是“查看任务单”。
     *
     * @return
     */
    private boolean isViewTaskOrder() {
        return roleType == RoleType.ROLE_TYPE_TEACHER;
    }

    public List<CommitTask> getData() {
        return getCurrAdapterViewHelper().getData();
    }

    private void registResultBroadcast() {
        if (mBroadcastManager == null) {
            mBroadcastManager = LocalBroadcastManager.getInstance(getMyApplication());
            IntentFilter filter = new IntentFilter(ACTION_MARK_SCORE);
            //点评刷新
            filter.addAction(EvalHomeworkListFragment.ACTION_MARK_SCORE);
            mBroadcastManager.registerReceiver(mReceiver, filter);
        }

    }

    private void unRegistResultBroadcast() {
        if (mBroadcastManager != null && mReceiver != null) {
            mBroadcastManager.unregisterReceiver(mReceiver);
            mBroadcastManager = null;
            mReceiver = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unRegistResultBroadcast();
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            loadCommonData();
        }
    };

    /**
     * 处理打点显示省略号异常问题
     *
     * @param textView
     * @param content
     */
    private void processTitle(TextView textView, String content,String commitTime) {
        if (textView != null && isAnswerTaskOrderQuestion) {
            textView.setText(StudyTaskUtils.getCommitTaskTitle(getActivity(),task.getTaskTitle(),commitTime,
                    task.getEndTime()));
            return;
        }
        if (textView == null || TextUtils.isEmpty(content)) {
            return;
        }
        textView.setText(StudyTaskUtils.getCommitTaskTitle(getActivity(),content,commitTime,task
                .getEndTime()));
        TitleGlobalLayoutListener listener = new TitleGlobalLayoutListener(textView);
        textView.getViewTreeObserver().addOnGlobalLayoutListener(listener);
    }

    class TitleGlobalLayoutListener implements ViewTreeObserver.OnGlobalLayoutListener {

        private TextView textView;

        public TitleGlobalLayoutListener(TextView textView) {
            this.textView = textView;
        }

        @Override
        public void onGlobalLayout() {
            ViewTreeObserver observer = textView.getViewTreeObserver();
            if (observer == null) {
                return;
            }
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                observer.removeGlobalOnLayoutListener(this);
            } else {
                observer.removeOnGlobalLayoutListener(this);
            }
            int lineCount = textView.getLineCount();
            //超过2行截断打省略号
            if (lineCount > TITLE_MAX_LINES) {
                int lineEndIndex = textView.getLayout().getLineEnd(TITLE_MAX_LINES);
                String suffix = "...";
                String resultText = textView.getText().subSequence(0,
                        lineEndIndex - suffix.length()) + suffix;
                textView.setText(resultText);
            }
        }
    }

    public void setSpeechAssessmentViewVisible() {
        if (speechAssessmentFl != null && !isHistoryClass) {
            speechAssessmentFl.setVisibility(View.VISIBLE);
        }
    }

    public void setStatisticViewVisible() {
        if (mStatisticFl != null) {
            propertiesType = 1;
            mStatisticFl.setVisibility(View.VISIBLE);
        }
    }

    private void enterSpeechAssessmentActivity(CommitTask task) {
        Fragment fragment = getParentFragment();
        if (fragment instanceof HomeworkCommitFragment) {
            ((HomeworkCommitFragment) fragment).enterSpeechAssessmentActivity(task.getStudentResUrl());
        }
    }

    private void enterTeacherReviewDetailActivity(CommitTask commitTask, boolean isTeacherEval) {
        if (commitTask.isHasVoiceReview()) {
            if (commitTask.isVideoType()){
                enterQDubbingDetailActivity(commitTask);
            } else {
                enterTeacherEvalDetail(commitTask);
            }
        } else {
            if (isTeacherEval && !isHistoryClass) {
                //没有点评
                int scoreRule = task.getScoringRule();
                if (scoreRule == 0) {
                    //默认百分制
                    scoreRule = 2;
                }
                TeacherReviewDetailActivity.start(
                        getActivity(),
                        "",
                        String.valueOf(commitTask.getCommitTaskId()),
                        scoreRule,
                        commitTask.getTaskScore());
            } else {
                if (commitTask.isVideoType()){
                    enterQDubbingDetailActivity(commitTask);
                } else {
                    enterTeacherEvalDetail(commitTask);
                }
            }
        }
    }

    private void enterTeacherEvalDetail(CommitTask commitTask) {
        //已经点评
        int mOrientation = 0;
        Fragment fragment = getParentFragment();
        if (fragment instanceof HomeworkCommitFragment) {
            mOrientation = ((HomeworkCommitFragment) fragment).taskCourseOrientation;
        }
        int scoreRule = task.getScoringRule();
        if (scoreRule == 0) {
            //默认百分制
            scoreRule = 2;
        }
        TeacherReviewDetailActivity.start(getActivity(),
                commitTask.isHasVoiceReview(),
                hasEvalReviewPermission(),
                getPageScoreList(commitTask.getAutoEvalContent()),
                commitTask.getTaskScore(),
                commitTask.getTaskScoreRemark(),
                scoreRule,
                mOrientation,
                commitTask.getStudentResUrl(),
                "",
                String.valueOf(commitTask.getCommitTaskId()));
    }

    private boolean hasEvalReviewPermission() {
        if ((roleType == RoleType.ROLE_TYPE_TEACHER && TextUtils.equals(task.getCreateId(), getMemeberId()))
                || isOnlineHost || isOnlineReporter) {
            return true;
        }
        return false;
    }

    private ArrayList<Integer> getPageScoreList(String pageArray) {
        ArrayList<Integer> pageList = new ArrayList<>();
        if (TextUtils.isEmpty(pageArray)) {
            return pageList;
        }

        JSONArray jsonArray = JSONArray.parseArray(pageArray);
        if (jsonArray != null && jsonArray.size() > 0) {
            for (int i = 0; i < jsonArray.size(); i++) {
                pageList.add((Integer) jsonArray.get(i));
            }
        }
        return pageList;
    }

    /**
     * 查看批阅
     */
    private void enterCheckMarkDetail(CommitTask data) {
        String taskScore = data.getTaskScore();
        if (TextUtils.isEmpty(taskScore)) {
            taskScore = "";
        }
        boolean isVisitor = true;
        if (((roleType == RoleType.ROLE_TYPE_TEACHER) && (task.getCreateId().equalsIgnoreCase(getMemeberId())))
                || roleType == RoleType.ROLE_TYPE_STUDENT || isOnlineReporter || isOnlineHost) {
            //老师只能批阅自己布置的作业,学生可以互相提问
            //听说课  读写单
            isVisitor = false;
        }
        boolean isNeedMark = true;
        if (task.getScoringRule() == 0) {//不打分
            isNeedMark = false;
        }

        CheckMarkFragment checkMarkFragment = CheckMarkFragment
                .newInstance(data,
                        taskScore,
                        task,
                        isHistoryClass ? RoleType.ROLE_TYPE_VISITOR : roleType,
                        isHistoryClass ? true : isVisitor,
                        isNeedMark,
                        isOnlineReporter,
                        isOnlineHost, isHeadMaster);
        getParentFragment().getFragmentManager().beginTransaction()
                .add(R.id.activity_body, checkMarkFragment, CheckMarkFragment.TAG)
                .hide(getParentFragment())
                .addToBackStack(CheckMarkFragment.TAG)
                .commit();
    }

    /**
     * 任务单答题卡列表项
     */
    private void enterStudentAnswerDetailActivity(CommitTask data, boolean isCheckMark) {
        Fragment fragment = getParentFragment();
        ExerciseAnswerCardParam cardParam = null;
        if (fragment instanceof HomeworkCommitFragment) {
            cardParam = ((HomeworkCommitFragment) fragment).answerCardParam;
        }
        if (cardParam != null && task != null) {
            if (isCheckMark) {
                //查看批阅
                cardParam.setCommitTaskTitle(task.getTaskTitle());
                if (isHistoryClass) {
                    cardParam.setRoleType(RoleType.ROLE_TYPE_VISITOR);
                } else {
                    cardParam.setIsHeadMaster(isHeadMaster);
                    cardParam.setIsOnlineHost(isOnlineHost);
                    cardParam.setIsOnlineReporter(isOnlineReporter);
                    if (roleType == RoleType.ROLE_TYPE_TEACHER && TextUtils.equals(getMemeberId(),
                            task.getCreateId())) {
                        //布置任务者
                        if (!isOnlineReporter) {
                            //不是主编的情况
                            cardParam.setIsOnlineHost(true);
                        }
                    }
                    cardParam.setRoleType(roleType);
                }
                cardParam.setStudentName(data.getStudentName());
                cardParam.setStudentId(data.getStudentId());
                cardParam.setCommitTaskId(data.getCommitTaskId());
                cardParam.setTaskScoreRemark(data.getTaskScoreRemark());
                AnswerCardDetailActivity.start(getActivity(), cardParam);
            } else {
                DoTaskOrderHelper.openExerciseDetail(
                        getActivity(),
                        cardParam.getExerciseAnswerString(),
                        taskId,
                        data.getStudentId(),
                        cardParam.getResId(),
                        task.getTaskTitle(),
                        null,
                        null,
                        null,
                        null,
                        data.getStudentName(),
                        data.getCommitTaskId(),
                        false,
                        false);
            }
        }

    }

    public void setExerciseAnswerData(boolean isAnswerTaskOrderQuestion,
                                      String fullMarkScore,
                                      boolean hasSubjectProblem) {
        this.hasSubjectProblem = hasSubjectProblem;
        this.isAnswerTaskOrderQuestion = isAnswerTaskOrderQuestion;
        this.fullMarkScore = Integer.valueOf(fullMarkScore);
        AdapterViewHelper adapterViewHelper = getCurrAdapterViewHelper();
        if (adapterViewHelper != null) {
            adapterViewHelper.update();
        }
    }

    private void updateLqCourseLookCourseState(ResourceInfoTag data) {
        if (data.getResCourseId() <= 0) {
            return;
        }
        if (roleType == RoleType.ROLE_TYPE_TEACHER || roleType == RoleType.ROLE_TYPE_PARENT) {
            return;
        }
        if (TextUtils.isEmpty(getMemeberId())) {
            return;
        }
        String memberId = getMemeberId();
//        if (roleType == RoleType.ROLE_TYPE_PARENT && !TextUtils.isEmpty(studentId)){
//            memberId = studentId;
//        }
        LqCourseHelper.updateLookCourseState(getActivity(), data.getResCourseId(), task.getClassId
                (), task.getSchoolId(), memberId);
        LqCourseHelper.updateSourceReadState(getActivity(), data.getResCourseId(), data.getResId(), memberId);
    }

    /**
     * 进入q配音的详情页
     */
    private void enterQDubbingDetailActivity(CommitTask data){
        HomeworkCommitFragment parentFragment = (HomeworkCommitFragment) getParentFragment();
        if (parentFragment != null){
            parentFragment.startDubbingVideo(data,hasEvalReviewPermission());
        }
    }
}
