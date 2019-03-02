package com.galaxyschool.app.wawaschool.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.duowan.mobile.netroid.Request;
import com.galaxyschool.app.wawaschool.EnglishWritingBuildActivity;
import com.galaxyschool.app.wawaschool.EnglishWritingCompositionRequirementsActivity;
import com.galaxyschool.app.wawaschool.HomeworkFinishStatusActivity;
import com.galaxyschool.app.wawaschool.Note.OnlineMediaPaperActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.ScoreStatisticsActivity;
import com.galaxyschool.app.wawaschool.SpeechAssessmentActivity;
import com.galaxyschool.app.wawaschool.TopicDiscussionActivity;
import com.galaxyschool.app.wawaschool.WawaCourseChoiceActivity;
import com.galaxyschool.app.wawaschool.chat.DemoApplication;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.CallbackListener;
import com.galaxyschool.app.wawaschool.common.CheckReplaceIPAddressHelper;
import com.galaxyschool.app.wawaschool.common.Common;
import com.galaxyschool.app.wawaschool.common.CourseOpenUtils;
import com.galaxyschool.app.wawaschool.common.DateUtils;
import com.galaxyschool.app.wawaschool.common.DialogHelper;
import com.galaxyschool.app.wawaschool.common.DoCourseHelper;
import com.galaxyschool.app.wawaschool.common.LogUtils;
import com.galaxyschool.app.wawaschool.common.PassParamhelper;
import com.galaxyschool.app.wawaschool.common.ShareUtils;
import com.galaxyschool.app.wawaschool.common.StudyInfoRecordUtil;
import com.galaxyschool.app.wawaschool.common.StudyTaskUtils;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.common.UploadUtils;
import com.galaxyschool.app.wawaschool.common.Utils;
import com.galaxyschool.app.wawaschool.common.WatchWawaCourseResourceOpenUtils;
import com.galaxyschool.app.wawaschool.common.WawaCourseUtils;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.course.CacheCourseImagesTask;
import com.galaxyschool.app.wawaschool.course.DownloadOnePageTask;
import com.galaxyschool.app.wawaschool.db.LocalCourseDao;
import com.galaxyschool.app.wawaschool.db.dto.LocalCourseDTO;
import com.galaxyschool.app.wawaschool.fragment.library.DataAdapter;
import com.galaxyschool.app.wawaschool.fragment.library.MyFragmentPagerTitleAdapter;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.fragment.resource.ResourceBaseFragment;
import com.galaxyschool.app.wawaschool.helper.CheckLqShopPmnHelper;
import com.galaxyschool.app.wawaschool.helper.DoTaskOrderHelper;
import com.galaxyschool.app.wawaschool.imagebrowser.GalleryActivity;
import com.galaxyschool.app.wawaschool.pojo.AutoMarkText;
import com.galaxyschool.app.wawaschool.pojo.AutoMarkTextResult;
import com.galaxyschool.app.wawaschool.pojo.CommitTaskResult;
import com.galaxyschool.app.wawaschool.pojo.ExerciseAnswerCardParam;
import com.galaxyschool.app.wawaschool.pojo.ResourceInfoTag;
import com.galaxyschool.app.wawaschool.views.DoTaskOrderTipsDialog;
import com.lecloud.xutils.cache.MD5FileNameGenerator;
import com.lqwawa.intleducation.factory.event.EventConstant;
import com.lqwawa.intleducation.module.discovery.ui.order.LQCourseOrderActivity;
import com.lqwawa.lqbaselib.net.ThisStringRequest;
import com.lqwawa.lqbaselib.net.library.DataResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.CommitTask;
import com.galaxyschool.app.wawaschool.pojo.CourseImageListResult;
import com.galaxyschool.app.wawaschool.pojo.HomeworkCommitObjectInfo;
import com.galaxyschool.app.wawaschool.pojo.HomeworkCommitObjectResult;
import com.galaxyschool.app.wawaschool.pojo.HomeworkListInfo;
import com.galaxyschool.app.wawaschool.pojo.LookResDto;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfo;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfoTag;
import com.galaxyschool.app.wawaschool.pojo.PPTAndPDFCourseInfo;
import com.galaxyschool.app.wawaschool.pojo.PPTAndPDFCourseInfoCode;
import com.galaxyschool.app.wawaschool.pojo.ResType;
import com.galaxyschool.app.wawaschool.pojo.RoleType;
import com.galaxyschool.app.wawaschool.pojo.StudyTask;
import com.galaxyschool.app.wawaschool.pojo.StudyTaskType;
import com.galaxyschool.app.wawaschool.pojo.UploadParameter;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseData;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseType;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseUploadResult;
import com.galaxyschool.app.wawaschool.pojo.weike.LocalCourseInfo;
import com.galaxyschool.app.wawaschool.pojo.weike.MaterialType;
import com.galaxyschool.app.wawaschool.pojo.weike.PlaybackParam;
import com.galaxyschool.app.wawaschool.pojo.weike.SplitCourseInfo;
import com.galaxyschool.app.wawaschool.slide.CreateSlideHelper;
import com.galaxyschool.app.wawaschool.slide.SlideManagerHornForPhone;
import com.galaxyschool.app.wawaschool.views.ContactsListDialog;
import com.galaxyschool.app.wawaschool.views.MyViewPager;
import com.galaxyschool.app.wawaschool.views.OrientationSelectDialog;
import com.galaxyschool.app.wawaschool.views.PagerSlidingTabStrip;
import com.libs.gallery.ImageInfo;
import com.lqwawa.client.pojo.SourceFromType;
import com.lqwawa.lqbaselib.pojo.MessageEvent;
import com.lqwawa.tools.FileZipHelper;
import com.oosic.apps.iemaker.base.PlaybackActivity;
import com.oosic.apps.iemaker.base.SlideManager;
import com.oosic.apps.iemaker.base.data.NormalProperty;
import com.oosic.apps.share.ShareInfo;
import com.oosic.apps.share.SharedResource;
import com.osastudio.common.utils.TimerUtils;
import com.umeng.socialize.media.UMImage;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 作业提交
 */
public class HomeworkCommitFragment extends ResourceBaseFragment {

    public static final String TAG = HomeworkCommitFragment.class.getSimpleName();
    public static final String COMMIT_AUTO_MARK_SCORE_ACTION = "commit_auto_mark_score_action";
    public static final String BUG_LQ_COURSE_SHOP_SUCCESS = "buy_success";
    public static final String REFRESH_COMMIT_LIST_DATA = "refresh_commit_list_data";

    private int roleType = -1;
    private View headView;
    private View rootView;
    private String TaskId = "";
    private String StudentId = "";
    private int TaskType;
    private StudyTask studyTask;
    private TextView rightTextView;
    private ImageView homeworkIcon;
    private ImageView mediaCover;//播放按钮
    private TextView homeworkTitle;
    private TextView assignTime;
    private TextView finishTime;
    private TextView finishStatus;
    private TextView discussContent;
    private StudyTask task;
    private String destCoursePath;
    private int screenType = 0;
    private String sortStudentId = "";
    private UserInfo userInfo;
    private UserInfo stuUserInfo; //student user info when the role of current user is a householder
    private boolean isPlaying; //限制只弹一个播放页面
    private String taskTitle;
    private static boolean hasCommented;

    //提交任务修改
    private TextView accessDetails;//查阅详情
    private boolean isOwnerTask;
    private int commitDataType;
    private static CreateSlideHelper.CreateSlideParam mCreateSlideParam;
//    private TextView commitBtn;//提交按钮

    //学习任务新需求
    private MyViewPager viewPager;
    private PagerSlidingTabStrip pagerSlidingTabStrip;
    private MyFragmentPagerTitleAdapter adapter;
    private List<Fragment> fragmentList = new ArrayList<>();
    private List<String> titleList = new ArrayList<>();
    //    private boolean showCommitBtn;//显示提交按钮
    private TopicDiscussionFragment topicDiscussionFragment;
    private boolean isCampusPatrolTag;//校园巡查标识
    //新版看课件资源list
    public static final String LOOK_RES_DTO_LIST = "look_res_dto_list";
    private List<LookResDto> lookResDtoList = new ArrayList<>();
    private HomeworkListInfo homeworkListInfo;
    private TaskRequirementFragment taskRequirementFragment;
    private CompletedHomeworkListFragment completedHomeworkListFragment;
    private EvalHomeworkListFragment evalHomeworkListFragment;
    private int taskResType = -1;
    //如果 studyTaskType 为听说+读写 不显示任务要求
    private int studyTaskType;

    private NewResourceInfoTag newInfoTag;
    private boolean isSuperChildTask;
    private String score = "-1";
    private MyBroadCastReceiver receiver;
    private int schemeId;
    private String resultContent;
    private CheckLqShopPmnHelper checkLqShopPmnHelper;
    public int taskCourseOrientation;
    private int propertiesType;
    private boolean isHistoryClass;
    private boolean isOnlineSchoolClass;
    private boolean isAnswerTaskOrderQuestion;
    protected ExerciseAnswerCardParam answerCardParam;
    private boolean isOnlineReporter;
    private boolean isOnlineHost;
    private boolean isHeadMaster;
    private boolean isStudentFinishRetellTask;
    private boolean isStudentFinishEValTask;
    private boolean isFistIn = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.layout_commit_homework, null);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
        loadViews();
        initBroadCastReceiver();
    }


    private void refreshData() {
        //英文写作需要刷新按钮状态
        if (needUpdateCommitBtnState()) {
            //刷新页面
            updateChildLayoutCommitBtnState();
        } else {
            //只刷新提交列表
            if (fragmentList != null && fragmentList.size() > 1) {
                Fragment fragment = fragmentList.get(1);
                if (studyTaskType == StudyTaskType.LISTEN_READ_AND_WRITE) {
                    //听说+读写只有两个tab
                    fragment = fragmentList.get(0);
                }
                if (fragment != null && fragment instanceof CompletedHomeworkListFragment) {
                    //刷新提交列表
                    ((CompletedHomeworkListFragment) fragment).refreshData();
                }

                if (propertiesType == 1) {
                    fragment = fragmentList.get(2);
                    if (fragment != null && fragment instanceof EvalHomeworkListFragment) {
                        //刷新提交按钮
                        ((EvalHomeworkListFragment) fragment).refreshData();
                    }
                }
            }
        }
        //通知页面刷新
        notifyDataSetChanged();
    }

    /**
     * 更新子页面提交按钮状态
     */
    private void updateChildLayoutCommitBtnState() {
        if (fragmentList != null && fragmentList.size() > 0) {

            Fragment fragment = null;
            fragment = fragmentList.get(0);
            if (fragment != null && fragment instanceof TaskRequirementFragment) {
                //刷新提交按钮
                ((TaskRequirementFragment) fragment).refreshData();
            }

            fragment = fragmentList.get(1);
            if (fragment != null && fragment instanceof CompletedHomeworkListFragment) {
                //刷新提交按钮
                ((CompletedHomeworkListFragment) fragment).refreshData();
            }

            if (propertiesType == 1) {
                fragment = fragmentList.get(2);
                if (fragment != null && fragment instanceof EvalHomeworkListFragment) {
                    //刷新提交按钮
                    ((EvalHomeworkListFragment) fragment).refreshData();
                }
            }
        }
    }

    @Override
    public UserInfo getStuUserInfo() {
        return stuUserInfo;
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
            if (TaskType == StudyTaskType.ENGLISH_WRITING) {
                shouldUpdate = true;
            }
        }
        return shouldUpdate;
    }

    public static void setHasCommented(boolean hasCommented) {
        HomeworkCommitFragment.hasCommented = hasCommented;
    }

    public static boolean hasCommented() {
        return hasCommented;
    }

    @Override
    public void importResource(String title, int type) {

    }

    @Override
    public void onResume() {
        super.onResume();
        isPlaying = false;
        if (EnglishWritingBuildFragment.hasCommented()) {
            EnglishWritingBuildFragment.setHasCommented(false);
            refreshData();
        } else if (AutoCommentTabsFragment.hasContentChanged()) {
            //按句点评修改
            AutoCommentTabsFragment.setHasContentChanged(false);
            refreshData();
        } else if (StudentFinishedHomeworkListFragment.hasContentChanged()) {
            //学生已完成作业列表
            StudentFinishedHomeworkListFragment.setHasContentChanged(false);
            refreshData();
        }

        if (HomeworkMainFragment.hasPublishedCourseToStudyTask
                || HomeworkMainFragment.isScanTaskFinished) {
            refreshData();
        }
    }

    void initViews() {
        if (getArguments() != null) {
            isHeadMaster = getArguments().getBoolean(HomeworkMainFragment.Constants.EXTRA_IS_HEAD_MASTER);
            isCampusPatrolTag = getArguments().getBoolean(CampusPatrolMainFragment
                    .IS_CAMPUS_PATROL_TAG);
            roleType = getArguments().getInt("roleType");
            TaskId = getArguments().getString("TaskId");
            //单个孩子id
            StudentId = getArguments().getString("StudentId");
            //学生Id串，以逗号分隔。
            sortStudentId = getArguments().getString("sortStudentId");
            TaskType = getArguments().getInt("TaskType");
            stuUserInfo = (UserInfo) getArguments().getSerializable(UserInfo.class.getSimpleName());
            taskTitle = getArguments().getString("TaskTitle");
            isHistoryClass = getArguments().getBoolean(ActivityUtils.EXTRA_IS_HISTORY_CLASS);
            isOnlineSchoolClass = getArguments().getBoolean(ActivityUtils.EXTRA_IS_ONLINE_CLASS);
            lookResDtoList = (List<LookResDto>) getArguments().getSerializable(LOOK_RES_DTO_LIST);
            homeworkListInfo = (HomeworkListInfo) getArguments()
                    .getSerializable(HomeworkListInfo.class.getSimpleName());
            if (homeworkListInfo != null) {
                studyTaskType = homeworkListInfo.getStudyTaskType();
                isSuperChildTask = homeworkListInfo.isSuperChildTask();
                //主编和小编身份互斥 只能同时存在一个
                isOnlineReporter = homeworkListInfo.isOnlineReporter();
                isOnlineHost = homeworkListInfo.isOnlineHost();
            }
        }

        //标题
        TextView textView = (TextView) findViewById(R.id.contacts_header_title);
        if (textView != null) {
            textView.setText(Utils.getTypeNameByType(getActivity(), TaskType));
        }

        //右侧的布局
        rightTextView = (TextView) findViewById(R.id.contacts_header_right_btn);
        if (rightTextView != null) {
            rightTextView.setVisibility(View.GONE);//先隐藏
            rightTextView.setOnClickListener(this);
            rightTextView.setTextAppearance(getActivity(), R.style.txt_wawa_big_green);
            rightTextView.setText(getString(R.string.discussion, "0"));
        }

        //分享
        textView = (TextView) findViewById(R.id.contacts_header_right_text_view);
        if (textView != null) {
            //老看课件和其他（不需提交）的任务暂时隐藏分享
            if (TaskType == StudyTaskType.WATCH_WAWA_COURSE
                    || TaskType == StudyTaskType.WATCH_HOMEWORK) {
                textView.setVisibility(View.GONE);
            }
//            else if (studyTaskType == StudyTaskType.LISTEN_READ_AND_WRITE){
//                textView.setVisibility(View.GONE);
//            }
            else {
                textView.setVisibility(View.VISIBLE);
            }
            textView.setText(getString(R.string.share));
            textView.setOnClickListener(this);
        }

        headView = findViewById(R.id.layout_assign_homework);
        headView.setOnClickListener(this);

        //作业图片
        homeworkIcon = (ImageView) headView.findViewById(R.id.iv_icon);
        View rightView = findViewById(R.id.layout_right_content);
        if (homeworkIcon != null) {
            // //设置布局为A4比例
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) homeworkIcon.getLayoutParams();
            int screenWidth = com.osastudio.common.utils.Utils.getScreenWidth(getActivity());
            int width = (screenWidth * 2 / 5) - 20;
            layoutParams.width = width;
            layoutParams.height = width * 210 / 297;
            homeworkIcon.setLayoutParams(layoutParams);

            LinearLayout.LayoutParams layoutParams1 = (LinearLayout.LayoutParams) rightView.getLayoutParams();
            layoutParams1.height = width * 210 / 297;
            layoutParams1.width = ViewGroup.LayoutParams.MATCH_PARENT;
            rightView.setLayoutParams(layoutParams1);


            homeworkIcon.setOnClickListener(this);
            if (shouldHiddenCover()) {
                homeworkIcon.setVisibility(View.GONE);
            } else {
                homeworkIcon.setVisibility(View.VISIBLE);
            }
        }
        //播放按钮
        mediaCover = (ImageView) headView.findViewById(R.id.media_cover);
        if (mediaCover != null) {
            //默认显示
            if (shouldHiddenCover()) {
                mediaCover.setVisibility(View.GONE);
            } else {
                mediaCover.setVisibility(View.VISIBLE);
            }
        }

        //作业标题
        homeworkTitle = (TextView) headView.findViewById(R.id.tv_title);

        //布置时间
        assignTime = (TextView) headView.findViewById(R.id.tv_start_time);

        //完成时间
        finishTime = (TextView) headView.findViewById(R.id.tv_end_time);
        //查阅详情
        accessDetails = (TextView) headView.findViewById(R.id.tv_access_details);
        if (accessDetails != null) {
            if (shouldHiddenAccessDetail()) {
                //英文写作、看作业、交作业隐藏“查阅详情”
                accessDetails.setVisibility(View.GONE);
            } else {
                accessDetails.setVisibility(View.VISIBLE);
            }
            accessDetails.setOnClickListener(this);
        }
        discussContent = (TextView) findViewById(R.id.introduction_content);
        //完成状态仅对老师显示
        finishStatus = (TextView) headView.findViewById(R.id.tv_finish_status);

        if (finishStatus != null) {
            if (roleType == RoleType.ROLE_TYPE_TEACHER && !isOnlineSchoolClass) {
                finishStatus.setVisibility(View.VISIBLE);
            } else {
                finishStatus.setVisibility(View.INVISIBLE);

            }
            finishStatus.setOnClickListener(this);
        }

//        commitBtn = (TextView) findViewById(R.id.retell_course_btn);
//        if (commitBtn != null) {
//            initCommitBtnTextByTaskType(TaskType,commitBtn);
//        }

        if (TaskType != StudyTaskType.RETELL_WAWA_COURSE) {
            initViewPager();
        }
    }

    /**
     * 进入成绩统计
     */
    public void enterAchievementStatistics() {
        if (completedHomeworkListFragment != null) {
            ArrayList<CommitTask> data = new ArrayList<>();
            ArrayList<CommitTask> commitTasks = (ArrayList<CommitTask>) completedHomeworkListFragment.getData();
            if (commitTasks != null && commitTasks.size() > 0) {
                data.addAll(commitTasks);
            }
            if (propertiesType == 1 && evalHomeworkListFragment != null) {
                ArrayList<CommitTask> evalTaskList = (ArrayList<CommitTask>) evalHomeworkListFragment.getData();
                if (evalTaskList != null && evalTaskList.size() > 0) {
                    data.addAll(evalTaskList);
                }
            }
            String[] defaultTenLevel = {"A+", "A", "A-", "B+", "B", "B-", "C+", "C", "C-", "D"};
            List<String> tenLevelList = Arrays.asList(defaultTenLevel);
            ArrayList<CommitTask> list = new ArrayList<>();
            ArrayList<CommitTask> evalList = new ArrayList<>();
            ArrayList<String> studentIdList = new ArrayList<>();
            ArrayList<String> evalStudentIdList = new ArrayList<>();
            if (data.size() > 0) {
                for (int i = 0; i < data.size(); i++) {
                    CommitTask commitTask = data.get(i);
                    commitTask.setScoreRule(ScoringRule);
                    commitTask.setScreenType(taskCourseOrientation);
                    if (isAnswerTaskOrderQuestion) {
                        //答题卡的统计
                        if (!TextUtils.isEmpty(commitTask.getTaskScore())) {
                            handleCommitTypeTask(tenLevelList, studentIdList, list, commitTask);
                        }
                    } else if (commitTask.isHasCommitTaskReview()) {
                        if (commitTask.isEvalType()) {
                            handleCommitTypeTask(tenLevelList, evalStudentIdList, evalList, commitTask);
                        } else {
                            handleCommitTypeTask(tenLevelList, studentIdList, list, commitTask);
                        }
                    }
                }
            }
            if (answerCardParam != null) {
                answerCardParam.setTaskId(TaskId);
                answerCardParam.setCommitTaskTitle(task.getTaskTitle());
                answerCardParam.setIsHeadMaster(isHeadMaster);
                answerCardParam.setIsOnlineHost(isOnlineHost);
                answerCardParam.setIsOnlineReporter(isOnlineReporter);
                if (roleType == RoleType.ROLE_TYPE_TEACHER && TextUtils.equals(getMemeberId(),
                        task.getCreateId())) {
                    //布置任务者
                    if (!isOnlineReporter) {
                        //不是主编的情况
                        answerCardParam.setIsOnlineHost(true);
                    }
                }
                answerCardParam.setRoleType(roleType);
            }
            ScoreStatisticsActivity.start(
                    getActivity(),
                    list,
                    evalList,
                    studyTask.getTaskNum(),
                    roleType, ScoringRule,
                    propertiesType == 1,
                    answerCardParam);
        }
    }

    private void handleCommitTypeTask(List<String> tenLevelList, ArrayList<String> studentIdList, ArrayList<CommitTask>
            list, CommitTask commitTask) {
        //已经批阅打分
        String studentId = commitTask.getStudentId();
        if (studentIdList.contains(studentId)) {
            int position = studentIdList.indexOf(studentId);
            String studentTaskScore = list.get(position).getTaskScore();
            if (!TextUtils.isEmpty(studentTaskScore) && !TextUtils.isEmpty
                    (commitTask.getTaskScore())) {
                if (ScoringRule == 1) {
                    //十分制
                    int studentPosition = tenLevelList.indexOf(studentTaskScore);
                    int currentPosition = tenLevelList.indexOf(commitTask.getTaskScore());
                    if (studentPosition > currentPosition) {
                        //取学生提交的最高分
                        list.set(position, commitTask);
                    }
                } else {
                    //百分制
                    if (Double.valueOf(studentTaskScore) < Double.valueOf
                            (commitTask.getTaskScore())) {
                        //取学生提交的最高分
                        list.set(position, commitTask);
                    }
                }
            }
        } else {
            studentIdList.add(studentId);
            list.add(commitTask);
        }
    }

    /**
     * 判断是否要隐藏缩略图
     *
     * @return
     */
    private boolean shouldHiddenCover() {
        boolean hidden = false;
        //英文写作、新版看课件隐藏缩略图。
        if (TaskType == StudyTaskType.ENGLISH_WRITING
                || TaskType == StudyTaskType.NEW_WATACH_WAWA_COURSE) {
            hidden = true;
        }
        return hidden;
    }

    /**
     * 判断是否要隐藏查阅详情
     *
     * @return
     */
    private boolean shouldHiddenAccessDetail() {
        boolean hidden = false;
        //英文写作、看作业、交作业、新版看课件隐藏“查阅详情”。
        if (TaskType == StudyTaskType.SUBMIT_HOMEWORK
                || TaskType == StudyTaskType.WATCH_HOMEWORK
                || TaskType == StudyTaskType.ENGLISH_WRITING
                || TaskType == StudyTaskType.NEW_WATACH_WAWA_COURSE
                || TaskType == StudyTaskType.Q_DUBBING) {
            hidden = true;
        }
        return hidden;
    }

    /**
     * 获取Bundle信息
     *
     * @return
     */
    private Bundle getHomeworkListBundleInfo() {
        Bundle args = new Bundle();
        //角色信息
        args.putInt(HomeworkFinishStatusActivity.Constants.ROLE_TYPE, roleType);
        args.putString(HomeworkFinishStatusActivity.Constants.TASK_ID, TaskId);
        args.putString(HomeworkFinishStatusActivity.Constants.TASK_TITLE, taskTitle);
        //类型信息
        args.putInt(HomeworkFinishStatusActivity.Constants.TASK_TYPE, TaskType);
        args.putBoolean(HomeworkFinishStatusActivity.Constants.HIDDEN_HEADER_VIEW, true);
        args.putString(HomeworkFinishStatusActivity.Constants.STUDENT_ID, StudentId);
        args.putString(HomeworkFinishStatusActivity.Constants.SORT_STUDENT_ID, sortStudentId);
        //传递userInfo
        args.putSerializable(UserInfo.class.getSimpleName(), stuUserInfo);
        //传递孩子Id
        args.putStringArray(HomeworkMainFragment.Constants.EXTRA_CHILD_ID_ARRAY, getChildIdArray());
        //显示提交按钮
        args.putBoolean(HomeworkFinishStatusActivity.Constants.SHOULD_SHOW_COMMIT_BTN, true);
        args.putBoolean(CampusPatrolMainFragment.IS_CAMPUS_PATROL_TAG, isCampusPatrolTag);
        args.putSerializable(LOOK_RES_DTO_LIST, (Serializable) lookResDtoList);
        //直接传递data过去，省心了。
        args.putSerializable(HomeworkListInfo.class.getSimpleName(), homeworkListInfo);
        args.putBoolean(HomeworkMainFragment.Constants.EXTRA_IS_HEAD_MASTER,
                getArguments().getBoolean(HomeworkMainFragment.Constants.EXTRA_IS_HEAD_MASTER));
        args.putBoolean(ActivityUtils.EXTRA_IS_HISTORY_CLASS, isHistoryClass);
        args.putInt(HomeworkMainFragment.Constants.EXTRA_COURSE_TYPE_PROPERTIES, propertiesType);
        return args;
    }

    /**
     * 获得孩子id数组
     *
     * @return
     */
    private String[] getChildIdArray() {
        String[] childIdArray = null;
        if (!TextUtils.isEmpty(sortStudentId)) {
            if (sortStudentId.contains(",")) {
                //多个孩子
                childIdArray = sortStudentId.split(",");
            } else {
                //单个孩子
                childIdArray = new String[]{sortStudentId};
            }
        }
        return childIdArray;
    }

    /**
     * 获得讨论话题的Bundle信息
     *
     * @return
     */
    private Bundle getTopicDiscussionBundleInfo() {
        Bundle args = new Bundle();
        args.putInt("TaskId", Integer.parseInt(TaskId));
        args.putInt("roleType", roleType);
        args.putString("fromType", "commitHomework");
        args.putBoolean("hiddenHeaderView", true);
        //改变背景色
        args.putBoolean(HomeworkFinishStatusActivity.Constants.SHOULD_CHANGE_BG_COLOR, true);
        return args;
    }

    /**
     * 是否需要显示提交列表
     *
     * @return
     */
    private boolean shouldShowCommitedList() {
        //对看课件和看作业隐藏
        if (TaskType == StudyTaskType.WATCH_WAWA_COURSE
                || TaskType == StudyTaskType.WATCH_HOMEWORK) {
            return false;
        }
        return true;
    }

    private void initViewPager() {
        viewPager = (MyViewPager) findViewById(R.id.view_pager);
        //设置是否可以滑动
        viewPager.setCanScroll(true);
        pagerSlidingTabStrip = (PagerSlidingTabStrip) findViewById(R.id.pager_sliding_tab_strip);

        //初始化标题
        if (studyTaskType != StudyTaskType.LISTEN_READ_AND_WRITE) {
            titleList.add(getString(R.string.task_requirements));
        }
        //看课件和看作业不显示提交列表
        if (shouldShowCommitedList()) {
            if (propertiesType == 1) {
                titleList.add(getString(R.string.str_retell_list));
                titleList.add(getString(R.string.str_eval_list));
            } else {
                titleList.add(getCommitedTabNameByType());
            }
        }
        titleList.add(getString(R.string.discussion, "0"));

        //初始化fragment
        Fragment fragment = null;
        //任务要求
        if (studyTaskType != StudyTaskType.LISTEN_READ_AND_WRITE) {
            fragment = new TaskRequirementFragment();
            fragment.setArguments(getHomeworkListBundleInfo());
            fragmentList.add(fragment);
        }
        //赋值
        this.taskRequirementFragment = (TaskRequirementFragment) fragment;

        //看课件和看作业不显示提交列表
        //提交列表
        if (shouldShowCommitedList()) {
            fragment = new CompletedHomeworkListFragment();
            fragment.setArguments(getHomeworkListBundleInfo());
            fragmentList.add(fragment);
            //赋值
            this.completedHomeworkListFragment = (CompletedHomeworkListFragment) fragment;
            if (propertiesType == 1) {
                fragment = new EvalHomeworkListFragment();
                fragment.setArguments(getHomeworkListBundleInfo());
                fragmentList.add(fragment);
                //赋值
                this.evalHomeworkListFragment = (EvalHomeworkListFragment) fragment;
            }
        }

        //讨论话题
        fragment = new TopicDiscussionFragment();
        fragment.setArguments(getTopicDiscussionBundleInfo());
        fragmentList.add(fragment);
        //赋值
        this.topicDiscussionFragment = (TopicDiscussionFragment) fragment;

        //适配器
        adapter = new MyFragmentPagerTitleAdapter(getChildFragmentManager(), titleList, fragmentList);
        viewPager.setAdapter(adapter);
        if (roleType == RoleType.ROLE_TYPE_TEACHER) {//老师
            if (TaskType == StudyTaskType.WATCH_HOMEWORK
                    || TaskType == StudyTaskType.WATCH_WAWA_COURSE) {//作业 看课件
                viewPager.setCurrentItem(0);
            } else if (studyTaskType == StudyTaskType.LISTEN_READ_AND_WRITE) {
                //听说+读写的子任务
                viewPager.setCurrentItem(0);
            } else {
                viewPager.setCurrentItem(1);
            }
        } else {
            viewPager.setCurrentItem(0);
        }

        viewPager.setOffscreenPageLimit(titleList.size());
        //和viewpager保持联动
        pagerSlidingTabStrip.setViewPager(viewPager);
    }

    private String getCommitedTabNameByType() {
        String tabName = "";
        if (TaskType == StudyTaskType.NEW_WATACH_WAWA_COURSE) {
            //课件列表
            tabName = getString(R.string.course_ware_list);
        } else {
            //提交列表
            tabName = getString(R.string.commit_list);
        }
        return tabName;
    }

    private void controlCommitViewByPosition(int position) {
        //滑动的时候，如果讨论软键盘弹起，要收起软键盘。
        if (topicDiscussionFragment != null) {
            //收起键盘
            topicDiscussionFragment.controlKeyboardHiddenLogic();
        }
    }

//    private void initCommitBtnTextByTaskType(int taskType, TextView commitBtn) {
//        if (commitBtn == null){
//            return;
//        }
//        String result = "";
//        if (roleType == RoleType.ROLE_TYPE_STUDENT || roleType == RoleType.ROLE_TYPE_PARENT) {
//            commitBtn.setVisibility(View.VISIBLE);
//            if (taskType == StudyTaskType.INTRODUCTION_WAWA_COURSE
//                    || taskType == StudyTaskType.SUBMIT_HOMEWORK) {
//                result = getString(R.string.commit_task);
//            } else if (taskType == StudyTaskType.RETELL_WAWA_COURSE) {
//                result = getString(R.string.retell_course);
//            } else if (taskType == StudyTaskType.ENGLISH_WRITING) {
//                //英文写作
//                result = getString(R.string.start_writing);
//                //先隐藏，防止闪现。
//                commitBtn.setVisibility(View.GONE);
//            }else if (taskType == StudyTaskType.TASK_ORDER) {
//                result = getString(R.string.do_task);
//            }else {
//                commitBtn.setVisibility(View.GONE);
//            }
//            commitBtn.setText(result);
//            commitBtn.setOnClickListener(this);
//        }else {
//            commitBtn.setVisibility(View.GONE);
//        }
//
//        //设置是否显示
//        showCommitBtn = commitBtn.getVisibility() == View.VISIBLE ? true : false;
//    }

    private void loadViews() {
        loadCommonData();
    }

    /**
     * 模拟数据
     */
    private void loadCommonData() {
        showLoadingDialog();
        Map<String, Object> params = new HashMap();
        //学校Id，必填
        params.put("TaskId", TaskId);
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

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        dismissLoadingDialog();
                    }
                });
    }

    public void notifyDataSetChanged() {
        //设置通知上个页面需要刷新
        setHasCommented(true);
    }

    /**
     * 设置讨论数
     *
     * @param discussCount
     */
    public void setDiscussionCount(int discussCount) {
        LinearLayout layout = pagerSlidingTabStrip.getTabsContainer();
        if (layout != null) {
            TextView discussionTextView = (TextView) layout.getChildAt(titleList.size() - 1);
            if (discussionTextView != null) {
                //设置讨论数
                if (discussCount > 99) {
                    discussionTextView.setText(getString(R.string.discussion, "99+"));
                } else {
                    discussionTextView.setText(getString(R.string.discussion, discussCount + ""));
                }
            }
        }
    }

    private void updateViews(HomeworkCommitObjectResult result) {
        updateHeadView(result);
        //更新tab数据
        updateTabData(result);
        HomeworkCommitObjectInfo homeworkCommitObjectInfo = result.getModel().getData();
        if (homeworkCommitObjectInfo != null) {
            studyTask = homeworkCommitObjectInfo.getTaskInfo();
//            updateCommitBtn(homeworkCommitObjectInfo);
            if (studyTask != null) {
                ScoringRule = studyTask.getScoringRule();
                checkSpeechAssessmentPermission(studyTask.getResId(), result);
                if (studyTask.getResPropType() == 1) {
                    //任务单答题卡类型
                    checkTaskOrderAnswerQuestionCard();
                }
            }
        }
    }

    public void dealTaskTypeFinishDetail(HomeworkCommitObjectInfo homeworkCommitObjectInfo){
        if (TaskType == StudyTaskType.RETELL_WAWA_COURSE) {
            if (roleType == RoleType.ROLE_TYPE_STUDENT || roleType == RoleType.ROLE_TYPE_PARENT) {
                if (roleType == RoleType.ROLE_TYPE_STUDENT) {
                    StudentId = getMemeberId();
                }
                isStudentFinishRetellTask =
                        StudyTaskUtils.isStudentFinishStudyTask(StudentId, homeworkCommitObjectInfo.getListCommitTask(), false);
                isStudentFinishEValTask =
                        StudyTaskUtils.isStudentFinishStudyTask(StudentId, homeworkCommitObjectInfo.getListCommitTask(), true);
                if (isFistIn) {
                    isFistIn = false;
                    //没有完成给于toast提示
                    if (studyTask.getRepeatCourseCompletionMode() == 1 && propertiesType == 1){
                        if (!isStudentFinishRetellTask){
                            TipMsgHelper.ShowMsg(getActivity(),
                                    getString(R.string.str_completion_mode) + getString(R.string.retell_course_new));
                        }
                    } else if (studyTask.getRepeatCourseCompletionMode() == 2){
                        if (!isStudentFinishRetellTask || !isStudentFinishEValTask){
                            TipMsgHelper.ShowMsg(getActivity(),
                                    getString(R.string.str_completion_mode) + getString(R.string.str_task_type_combination));
                        }
                    }
                }
            }
        }
    }

    /**
     * 更新Tab的数据
     *
     * @param result
     */
    private void updateTabData(HomeworkCommitObjectResult result) {
        if (result != null) {
            //更新任务要求
            if (taskRequirementFragment != null) {
                taskRequirementFragment.updateTabData(result);
            }
            //更新提交列表
            if (completedHomeworkListFragment != null) {
                completedHomeworkListFragment.updateTabData(result);
            }

            if (evalHomeworkListFragment != null) {
                evalHomeworkListFragment.updateTabData(result);
            }
        }
    }

//    /**
//     * 更新提交按钮：英文写作需要更新。
//     * @param data
//     */
//    private void updateCommitBtn(HomeworkCommitObjectInfo data) {
//        //学生、家长英文写作才更新。
//        if (needUpdateCommitBtnState()){
//            boolean hiddenCommitBtn = false;
//            if (data != null) {
//                List<CommitTask> commitTask = data.getListCommitTask();
//                if (commitTask != null && commitTask.size() > 0) {
//                    for (CommitTask task : commitTask) {
//                        if (task != null) {
//                            String childId = task.getStudentId();
//                            if (!TextUtils.isEmpty(childId)
//                                    && !TextUtils.isEmpty(StudentId)
//                                    && childId.equals(StudentId)) {
//                                //找到自己提交的信息，不能再写了。
//                                hiddenCommitBtn = true;
//                                break;
//                            }
//                        }
//                    }
//                }
//            }
//            commitBtn.setVisibility(hiddenCommitBtn ? View.GONE : View.VISIBLE);
//        }
//        //设置是否显示
//        showCommitBtn = commitBtn.getVisibility() == View.VISIBLE ? true : false;
//    }

    private void updateHeadView(HomeworkCommitObjectResult result) {
        if (result == null) {
            return;
        }
        task = result.getModel().getData().getTaskInfo();
        if (task == null) {
            return;
        }

        String resId = task.getResId();
        if (!TextUtils.isEmpty(resId)) {
            if (resId.contains(",")) {
                String[] resIdArray = resId.split(",");
                taskResType = Integer.valueOf(resIdArray[0].split("-")[1]);
            } else {
                taskResType = Integer.valueOf(resId.split("-")[1]);
            }
        }
        //控制发送布局显示
        String taskCreateId = task.getTaskCreateId();
        isOwnerTask = !TextUtils.isEmpty(taskCreateId) && taskCreateId.equals(getMemeberId());

        //显示学生应完成的任务
        String disContent = result.getModel().getData().getTaskInfo().getDiscussContent();
        if (disContent != null && TaskType == StudyTaskType.INTRODUCTION_WAWA_COURSE) {
            if (discussContent != null) {
                discussContent.setText(disContent);
            }
        }

        //讨论数
        if (rightTextView != null) {
            rightTextView.setText(getString(R.string.discussion, String.valueOf(task
                    .getCommentCount())));
        }

        if (homeworkIcon != null) {
            getThumbnailManager().displayThumbnailWithDefault(AppSettings.getFileUrl(
                    task.getResThumbnailUrl()), homeworkIcon, R.drawable.default_book_cover);
        }
        //作业标题
        if (homeworkTitle != null) {
            String taskTitle = task.getTaskTitle();
            if (!TextUtils.isEmpty(taskTitle)) {
                if (taskTitle.contains(",")) {
                    taskTitle = taskTitle.split(",")[0];
                }
            }
            homeworkTitle.setText(taskTitle);
        }

        //布置时间
        if (assignTime != null) {
            assignTime.setText(getString(R.string.assign_date) + "：" + DateUtils.getDateStr(task
                    .getStartTime(), 0) + "-" + DateUtils.getDateStr(task.getStartTime(), 1) + "-" + DateUtils
                    .getDateStr(task.getStartTime(), 2));
        }

        //完成时间
        if (finishTime != null) {
            finishTime.setText(getString(R.string.finish_date) + "：" + DateUtils.getDateStr(task
                    .getEndTime(), 0) + "-" + DateUtils.getDateStr(task.getEndTime(), 1) + "-" + DateUtils
                    .getDateStr(task.getEndTime(), 2));
        }
        updateFinishStatus();


        //如果是复述课件对于多个类型区别对待
        if (TaskType == StudyTaskType.RETELL_WAWA_COURSE || TaskType == StudyTaskType.TASK_ORDER) {
            analysisRetellCourseData();
        }
    }

    /**
     * 更新完成状态
     */
    private void updateFinishStatus() {
        //完成状态
        if (finishStatus != null) {
            //只有老师才显示完成情况，才去计算。
            if (roleType == RoleType.ROLE_TYPE_TEACHER) {
                int taskNum = 0;
                int taskFinishCount = 0;
                int taskUnFinishCount = 0;
                taskNum = task.getTaskNum();
                taskFinishCount = task.getFinishTaskCount();
                taskUnFinishCount = taskNum - taskFinishCount;
                boolean isFinishAll = ((taskNum > 0) && (taskNum == taskFinishCount));
                if (!isFinishAll) {
                    //未完成
                    finishStatus.setText(getString(R.string.finish_count,
                            String.valueOf(taskFinishCount)) + " / " + getString(R.string.unfinish_count,
                            String.valueOf(taskUnFinishCount)));
                } else {
                    //全部完成
                    finishStatus.setText(getString(R.string.n_finish_all, String.valueOf(taskNum)));
                }
                StudyTaskUtils.setTaskFinishBackgroundDetail(getActivity(), finishStatus,
                        taskFinishCount, taskNum);
            }
        }
    }

    public void updateFinishCount() {
        loadCommonData();
        notifyDataSetChanged();

    }

    private void analysisRetellCourseData() {
        if (task == null) {
            return;
        }
        if (taskResType == ResType.RES_TYPE_PDF
                || taskResType == ResType.RES_TYPE_PPT
                || taskResType == ResType.RES_TYPE_DOC) {
            //隐藏查询详情
            if (accessDetails != null) {
                accessDetails.setVisibility(View.GONE);
            }

            //隐藏播放按钮
            if (mediaCover != null) {
                mediaCover.setVisibility(View.GONE);
            }

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("resId", task.getResId());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            StringBuilder builder = new StringBuilder();
            builder.append("?j=" + jsonObject.toString());
            String url = ServerUrl.WAWATV_COURSE_DETAIL_URL + builder.toString();
            final ThisStringRequest request = new ThisStringRequest(
                    Request.Method.GET, url, new Listener<String>() {
                @Override
                public void onSuccess(String jsonString) {
                    if (getActivity() == null) {
                        return;
                    }
                    if (TextUtils.isEmpty(jsonString)) {
                        return;
                    }
                    PPTAndPDFCourseInfoCode result = com.alibaba.fastjson.JSONObject.parseObject
                            (jsonString, PPTAndPDFCourseInfoCode.class);
                    if (result != null) {
                        List<NewResourceInfo> resourceInfoList = new ArrayList<>();
                        List<PPTAndPDFCourseInfo> splitCourseInfo = result.getData();
                        if (splitCourseInfo == null || splitCourseInfo.size() == 0) {
                            return;
                        }
                        List<SplitCourseInfo> splitList = splitCourseInfo.get(0).getSplitList();
                        if (splitList == null || splitList.size() == 0) {
                            return;
                        }
                        if (taskResType > ResType.RES_TYPE_BASE) {
                            taskResType = taskResType % ResType.RES_TYPE_BASE;
                        }
                        if (splitList.size() > 0) {
                            for (int i = 0; i < splitList.size(); i++) {
                                SplitCourseInfo splitCourse = splitList.get(i);
                                NewResourceInfo newResourceInfo = new NewResourceInfo();
                                newResourceInfo.setTitle(splitCourseInfo.get(0).getOriginname());
                                newResourceInfo.setResourceUrl(AppSettings.getFileUrl(splitCourse.getPlayUrl()));
                                newResourceInfo.setResourceId(task.getResId());
                                newResourceInfo.setResourceType(taskResType);
                                newResourceInfo.setAuthorId(task.getCreateId());
                                resourceInfoList.add(newResourceInfo);
                            }
                        }
                        if (newInfoTag == null) {
                            newInfoTag = new NewResourceInfoTag();
                            if (resourceInfoList.size() > 0) {
                                newInfoTag.setTitle(task.getTaskTitle());
                                newInfoTag.setResourceUrl(resourceInfoList.get(0).getResourceUrl());
                                newInfoTag.setDescription(task.getDiscussContent());
                                newInfoTag.setSplitInfoList(resourceInfoList);
                            }
                        }
                        //更新缩略图
                        if (homeworkIcon != null) {
                            getThumbnailManager().displayThumbnailWithDefault(AppSettings.getFileUrl(
                                    newInfoTag.getResourceUrl()), homeworkIcon, R.drawable.default_book_cover);
                        }
                    }
                }

                @Override
                public void onError(NetroidError error) {
                    if (getActivity() == null) {
                        return;
                    }
                }
            });
            request.addHeader("Accept-Encoding", "*");
            request.start(getActivity());
        } else if (taskResType == ResType.RES_TYPE_IMG) {
            //隐藏查询详情
            if (accessDetails != null) {
                accessDetails.setVisibility(View.GONE);
            }
            //隐藏播放按钮
            if (mediaCover != null) {
                mediaCover.setVisibility(View.GONE);
            }
            //图片的显示
            String resUrl = task.getResUrl();
            String resId = task.getResId();
            String authorId = task.getResAuthor();
            if (newInfoTag == null) {
                newInfoTag = new NewResourceInfoTag();
                List<NewResourceInfo> resourceInfoList = new ArrayList<>();
                NewResourceInfo tempInfo;
                if (resUrl.contains(",")) {
                    String[] resUrlArray = resUrl.split(",");
                    String[] resIdArray = resId.split(",");
                    String[] authorIdArray = authorId.split(",");
                    for (int i = 0; i < resUrlArray.length; i++) {
                        tempInfo = new NewResourceInfo();
                        tempInfo.setTitle(task.getTaskTitle());
                        tempInfo.setResourceUrl(AppSettings.getFileUrl(resUrlArray[i]));
                        tempInfo.setResourceId(resIdArray[i]);
                        tempInfo.setResourceType(taskResType);
                        tempInfo.setAuthorId(authorIdArray[i]);
                        resourceInfoList.add(tempInfo);
                    }
                } else {
                    tempInfo = new NewResourceInfo();
                    tempInfo.setTitle(task.getTaskTitle());
                    tempInfo.setResourceUrl(AppSettings.getFileUrl(resUrl));
                    tempInfo.setResourceId(resId);
                    tempInfo.setResourceType(taskResType);
                    tempInfo.setAuthorId(authorId);
                    resourceInfoList.add(tempInfo);
                }
                newInfoTag.setTitle(task.getTaskTitle());
                newInfoTag.setResourceUrl(resourceInfoList.get(0).getResourceUrl());
                newInfoTag.setDescription(task.getDiscussContent());
                newInfoTag.setSplitInfoList(resourceInfoList);
            }
            //更新缩略图
            if (homeworkIcon != null) {
                getThumbnailManager().displayThumbnailWithDefault(AppSettings.getFileUrl(
                        newInfoTag.getResourceUrl()), homeworkIcon, R.drawable.default_book_cover);
            }
        }
    }

    public void createSlide(int slideType) {
        int haveFree = Utils.checkStorageSpace(getActivity());
        if (haveFree == 0) {
            CreateSlideHelper.CreateSlideParam param = new CreateSlideHelper.CreateSlideParam();
            param.mFragment = HomeworkCommitFragment.this;
            param.mEntryType = Common.LIST_TYPE_SHARE;
            param.mEditable = true;
            param.mSlideType = slideType;
            if (task != null) {
                param.mTitle = task.getTaskTitle();
            }
            param.fromType = SlideManagerHornForPhone.FromWhereData.FROM_STUDY_TASK_OTHER_COURSE;
            if (slideType == CreateSlideHelper.SLIDETYPE_IMAGE) {
                param.mIsPickOneImage = false;
            }
            if (roleType == RoleType.ROLE_TYPE_PARENT) {
                param.mMemberId = StudentId;
                if (stuUserInfo != null) {
                    param.mUserInfo = stuUserInfo;
                }
            } else {
                UserInfo userInfo = getUserInfo();
                if (userInfo != null && !TextUtils.isEmpty(userInfo.getMemberId())) {
                    param.mMemberId = userInfo.getMemberId();
                }
            }
            param.mTaskId = TaskId;
            param.mOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
            CreateSlideHelper.createSlide(param);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_finish_status) {
            //查看作业完成状态
            enterHomeworkFinishStatusActivity();
        } else if (v.getId() == R.id.contacts_header_right_btn) {
            //讨论
            enterTopicDiscussionActivity();
        } else if (v.getId() == R.id.retell_course_btn) {
            int haveFree = Utils.checkStorageSpace(getActivity());
            if (haveFree == 0) {
                controlCommitClickEventByTaskType(TaskType);
            }
        } else if (v.getId() == R.id.contacts_header_right_text_view) {
            //分享
            share();
        } else if (v.getId() == R.id.tv_access_details) {
            //查阅详情
            if (task != null) {
                if (!isPlaying) {
                    isPlaying = true;
                    //点击查阅详情直接进入详情页面
                    if (TaskType == StudyTaskType.ENGLISH_WRITING
                            || TaskType == StudyTaskType.NEW_WATACH_WAWA_COURSE) {
                        //英文写作/新版看课件不跳转
                    } else if ((TaskType == StudyTaskType.RETELL_WAWA_COURSE || TaskType ==
                            StudyTaskType.TASK_ORDER)
                            && (taskResType == ResType.RES_TYPE_PDF
                            || taskResType == ResType.RES_TYPE_PPT
                            || taskResType == ResType.RES_TYPE_IMG
                            || taskResType == ResType.RES_TYPE_DOC)) {
                    } else {
                        if (studyTask != null) {
                            checkLqCourseShopPermission(studyTask.getResCourseId(), (object) -> {
                                boolean isNeedBuyLqCourseShopRes = (boolean) object;
                                if (isNeedBuyLqCourseShopRes) {
                                    Bundle bundle = new Bundle();
                                    PassParamhelper mParam = new PassParamhelper();
                                    mParam.isFromLQMOOC = true;
                                    mParam.isAudition = true;
                                    bundle.putSerializable(PassParamhelper.class.getSimpleName(), mParam);
                                    CourseOpenUtils.openCourseDetailsDirectly(getActivity(), task, roleType,
                                            getMemeberId(), StudentId, stuUserInfo, true, bundle);
                                } else {
                                    CourseOpenUtils.openCourseDetailsDirectly(getActivity(), task, roleType,
                                            getMemeberId(), StudentId, stuUserInfo, true);
                                }
                            });
                        } else {
                            CourseOpenUtils.openCourseDetailsDirectly(getActivity(), task, roleType,
                                    getMemeberId(), StudentId, stuUserInfo, true);
                        }
                    }
                }
            }

        } else if (v.getId() == R.id.iv_icon || v.getId() == R.id.layout_assign_homework) {
            if (TaskType == StudyTaskType.ENGLISH_WRITING
                    || TaskType == StudyTaskType.NEW_WATACH_WAWA_COURSE) {
                return;
                //英文写作/新版看课件不跳转
            }
            //点击图片直接打开
            if ((TaskType == StudyTaskType.RETELL_WAWA_COURSE || TaskType == StudyTaskType.TASK_ORDER)
                    && (taskResType == ResType.RES_TYPE_PPT
                    || taskResType == ResType.RES_TYPE_PDF
                    || taskResType == ResType.RES_TYPE_DOC
                    || taskResType == ResType.RES_TYPE_IMG)) {
                openPptAndPdf();
            } else if (TaskType == StudyTaskType.Q_DUBBING) {
                //打开Q配音的视频
                openQDubbingVideo();
            } else {
                openImage(task, false);
            }
        } else if (v.getId() == R.id.contacts_header_left_btn){
            onBackPress();
        }
    }

    public void onBackPress(){
        if (showTaskFinishDialogCondition()) {
            StudyTaskUtils.processTaskFinishDetailDialog(getActivity(),
                    task.getRepeatCourseCompletionMode() == 2,isStudentFinishRetellTask,isStudentFinishEValTask);
        } else {
            finish();
        }
    }

    private boolean showTaskFinishDialogCondition() {
        if (roleType == RoleType.ROLE_TYPE_STUDENT || roleType == RoleType.ROLE_TYPE_PARENT) {
            if (task != null) {
                if (task.getRepeatCourseCompletionMode() == 1 && propertiesType == 1 && !isStudentFinishRetellTask) {
                    return true;
                } else if (task.getRepeatCourseCompletionMode() == 2 && (!isStudentFinishRetellTask || !isStudentFinishEValTask)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 打开ppt 或者 pdf 的资源
     */
    private void openPptAndPdf() {
        if (newInfoTag != null) {
            List<NewResourceInfo> splitList = newInfoTag.getSplitInfoList();
            ArrayList<ImageInfo> imageItemInfos = new ArrayList<>();
            if (splitList != null && splitList.size() > 0) {
                if (splitList != null && splitList.size() > 0) {
                    for (int i = 0; i < splitList.size(); i++) {
                        NewResourceInfo splitCourse = splitList.get(i);
                        ImageInfo newResourceInfo = new ImageInfo();
                        newResourceInfo.setTitle(task.getTaskTitle());
                        newResourceInfo.setResourceUrl(AppSettings.getFileUrl(splitCourse.getResourceUrl()));
                        newResourceInfo.setResourceId(splitCourse.getResourceId());
                        newResourceInfo.setResourceType(taskResType);
                        newResourceInfo.setAuthorId(splitCourse.getAuthorId());
                        imageItemInfos.add(newResourceInfo);
                    }
                }
            }
            if (imageItemInfos != null && imageItemInfos.size() > 0) {
                //判断是否要显示页码
                boolean shouldShowPageNumber = (taskResType == ResType.RES_TYPE_PPT
                        || taskResType == ResType.RES_TYPE_PDF
                        || taskResType == ResType.RES_TYPE_DOC)
                        || (taskResType == ResType.RES_TYPE_IMG
                        && (imageItemInfos != null && imageItemInfos.size() > 1));
                GalleryActivity.newInstance(getActivity(), imageItemInfos, shouldShowPageNumber,
                        0, false, true, false);
            }
        }
    }

    /**
     * 打开图片
     */
    public void openImage(StudyTask task, boolean isDoTaskOrder) {
        if (task == null) {
            return;
        }
        String courseId = task.getResId();
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
                            courseData.setIsPublicRes(false);
                            processOpenImageData(courseData, isDoTaskOrder);
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
                    courseData.setIsPublicRes(false);
                    processOpenImageData(courseData, isDoTaskOrder);
                }
            });
        }
    }

    /**
     * 打开图片逻辑
     *
     * @param courseData
     */
    private void processOpenImageData(CourseData courseData, boolean isDoTaskOrder) {
        if (courseData != null) {
            PlaybackParam mParam = new PlaybackParam();
            //隐藏收藏按钮
            mParam.mIsHideCollectTip = true;
            if (isDoTaskOrder) {
                handleExerciseAnswerData(mParam, courseData);
            }
            int resType = courseData.type % ResType.RES_TYPE_BASE;
            if (resType == ResType.RES_TYPE_COURSE_SPEAKER ||
                    resType == ResType.RES_TYPE_COURSE ||
                    resType == ResType.RES_TYPE_OLD_COURSE) {
                ActivityUtils.playOnlineCourse(getActivity(), courseData.getCourseInfo(), false,
                        mParam);
            } else if (resType == ResType.RES_TYPE_STUDY_CARD) {
                //直接打开，不带编辑。
                ActivityUtils.openOnlineOnePage(getActivity(), courseData.getNewResourceInfo(), true,
                        mParam);
            } else if (resType == ResType.RES_TYPE_ONEPAGE) {
                ActivityUtils.openOnlineOnePage(getActivity(), courseData.getNewResourceInfo(), true,
                        mParam);
            } else if (resType == ResType.RES_TYPE_NOTE) {
                //直接打开帖子
                if (!TextUtils.isEmpty(courseData.resourceurl)) {
                    ActivityUtils.openOnlineNote(getActivity(), courseData.getCourseInfo(), false, true);
                }
            }
        }
    }

    private void handleExerciseAnswerData(PlaybackParam mParam, CourseData courseData) {
        if (isAnswerTaskOrderQuestion) {
            //任务单答题操作
            ExerciseAnswerCardParam cardParam = new ExerciseAnswerCardParam();
            cardParam.setShowExerciseButton(true);
            cardParam.setShowExerciseNode(true);
            cardParam.setExerciseAnswerString(answerCardParam.getExerciseAnswerString());
            cardParam.setTaskId(TaskId);
            cardParam.setResId(courseData.id + "-" + courseData.type);
            if (task != null) {
                cardParam.setCommitTaskTitle(task.getTaskTitle());
            }
            if (roleType == RoleType.ROLE_TYPE_PARENT) {
                cardParam.setStudentId(StudentId);
            } else {
                cardParam.setStudentId(getMemeberId());
            }
            mParam.exerciseCardParam = cardParam;
        }
    }

    /**
     * 打开Q配音的原视频
     */
    private void openQDubbingVideo(){
        if (task == null){
            return;
        }
        ResourceInfoTag info = new ResourceInfoTag();
        info.setResId(task.getResId());
        info.setResourcePath(task.getResUrl());
        WatchWawaCourseResourceOpenUtils.openResource(getActivity(), info,
                true, false, true);
    }

    private void enterEnglishWritingCompositionRequirementsActivity() {

        if (task == null) {
            return;
        }
        Intent intent = new Intent(getActivity(), EnglishWritingCompositionRequirementsActivity
                .class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("task", task);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void controlCommitClickEventByTaskType(int taskType) {
        if (taskType == StudyTaskType.INTRODUCTION_WAWA_COURSE) {
            //导读
            showIntroductionWaWaCoursePopupWindow();
        } else if (taskType == StudyTaskType.SUBMIT_HOMEWORK) {
            //提交作业
            showSubmitHomeworkPopupWindow();
        } else if (taskType == StudyTaskType.RETELL_WAWA_COURSE) {
            //复述课件
            retellCourse();
        } else if (taskType == StudyTaskType.ENGLISH_WRITING) {
            //英文写作
            enterEnglishBuildActivity();
        } else if (taskType == StudyTaskType.TASK_ORDER) {
            //任务单
            if (studyTask != null) {
                checkLqCourseShopPermission(studyTask.getResCourseId(), (object) -> {
                    boolean isNeedBuyLqCourseShopRes = (boolean) object;
                    if (isNeedBuyLqCourseShopRes) {
                        popBuyLqCourseShopResource();
                    } else {
                        takeTask(true);
                    }
                });
            } else {
                takeTask(true);
            }
        } else if (taskType == StudyTaskType.Q_DUBBING){
            startDubbingVideo();
        }
    }

    /**
     * 提交作业弹出框
     */
    private void showSubmitHomeworkPopupWindow() {
        List<String> contentList = new ArrayList();
        contentList.add(getString(R.string.whiteboard_plus_voice));
        contentList.add(getString(R.string.image_plus_voice));
        contentList.add(getString(R.string.camera_plus_voice));

        ContactsListDialog dialog = new ContactsListDialog(getActivity(),
                R.style.Theme_ContactsDialog, null,
                contentList, R.layout.contacts_dialog_list_text_item,
                new DataAdapter.AdapterViewCreator() {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        if (convertView != null) {
                            String title = (String) convertView.getTag();
                            TextView textView = (TextView) convertView.findViewById(
                                    R.id.contacts_dialog_list_item_title);
                            if (textView != null) {
                                textView.setText(title);
                                //新的样式：
                                //设置黑色字体
                                textView.setTextColor(getResources().getColor(R.color.black));
                                //设置圆角白色背景
                                textView.setBackgroundResource(R.drawable.btn_white_bg);
                            }
                        }
                        return convertView;
                    }
                },
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String title = (String) view.getTag();
                        if (getString(R.string.whiteboard_plus_voice).equals(title)) {
                            //白板+语音
                            createSlide(CreateSlideHelper.SLIDETYPE_WHITEBOARD);
                        } else if (getString(R.string.image_plus_voice).equals(title)) {
                            //相册+语音
                            createSlide(CreateSlideHelper.SLIDETYPE_IMAGE);
                        } else if (getString(R.string.camera_plus_voice).equals(title)) {
                            //拍照+语音
                            createSlide(CreateSlideHelper.SLIDETYPE_CAMERA);
                        }
                    }
                }, getString(R.string.cancel), null);
        //设置新的风格
        Utils.setDialogNewlyStyle(dialog, 0.5f);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        dialog.show();
    }

    /**
     * 提交导读弹出框
     */
    private void showIntroductionWaWaCoursePopupWindow() {
        List<String> contentList = new ArrayList();
        if (studyTask != null) {
            if (!TextUtils.isEmpty(studyTask.getWorkOrderId()))
                //有任务单才显示该按钮in
                contentList.add(getString(R.string.do_task));
        }
        contentList.add(getString(R.string.retell_course_new));
        contentList.add(getString(R.string.ask_question));
        contentList.add(getString(R.string.create_course));
        //隐藏看课件
//        contentList.add(getString(R.string.look_through_courseware)); //看课件

        ContactsListDialog dialog = new ContactsListDialog(getActivity(),
                R.style.Theme_ContactsDialog, null,
                contentList, R.layout.contacts_dialog_list_text_item,
                new DataAdapter.AdapterViewCreator() {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        if (convertView != null) {
                            String title = (String) convertView.getTag();
                            TextView textView = (TextView) convertView.findViewById(
                                    R.id.contacts_dialog_list_item_title);
                            if (textView != null) {
                                textView.setText(title);
                                //新的样式：
                                //设置黑色字体
                                textView.setTextColor(getResources().getColor(R.color.black));
                                //设置圆角白色背景
                                textView.setBackgroundResource(R.drawable.btn_white_bg);
                            }
                        }
                        return convertView;
                    }
                },
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String title = (String) view.getTag();
                        if (getString(R.string.do_task).equals(title)) {
                            //查看任务单
                            takeTask(false);
                        } else if (getString(R.string.retell_course_new).equals(title)) {
                            //复述
                            commitDataType = SelectedReadingDetailFragment.BtnEntity.TYPE_RETELL_COURSE;
                            retellCourse();
                        } else if (getString(R.string.ask_question).equals(title)) {
                            //提问
                            commitDataType = SelectedReadingDetailFragment.BtnEntity.TYPE_QUESTION_COURSE;
                            retellCourse();
                        } else if (getString(R.string.create_course).equals(title)) {
                            //创作
                            createCourse();
                        } else if (getString(R.string.look_through_courseware).equals(title)) {
                            //看课件
                            openImage(task, false);
                        }
                    }
                }, getString(R.string.cancel), null);

        //设置新的风格
        Utils.setDialogNewlyStyle(dialog, 0.5f);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        dialog.show();
    }

    /**
     * 做任务.任务单
     */
    public void takeTask(boolean doTask) {
        commitDataType = SelectedReadingDetailFragment.BtnEntity.TYPE_MAKE_TASK;
        if (doTask) {
            if (task != null) {
                if ((TaskType == StudyTaskType.TASK_ORDER)
                        && (taskResType == ResType.RES_TYPE_PPT
                        || taskResType == ResType.RES_TYPE_PDF
                        || taskResType == ResType.RES_TYPE_IMG
                        || taskResType == ResType.RES_TYPE_DOC)) {
                    //ppt pdf 图片
                    if (newInfoTag != null) {
                        List<String> imageList = new ArrayList<>();
                        List<NewResourceInfo> splitInfo = newInfoTag.getSplitInfoList();
                        if (splitInfo != null && splitInfo.size() > 0) {
                            for (int i = 0; i < splitInfo.size(); i++) {
                                NewResourceInfo info = splitInfo.get(i);
                                imageList.add(info.getResourceUrl());
                            }
                        }
                        selectOrientation(imageList, newInfoTag);
                    }
                } else {
                    if (isAnswerTaskOrderQuestion) {
                        //答题卡
                        if (hasEnabledDoToTaskOrderTips()) {
                            openImage(task, true);
                        }
                    } else {
                        //做任务单
                        loadCourseDetail(task.getResId(), false);
                    }
                }
            }
        } else {
            //查看任务单
            if (studyTask != null) {
                loadCourseDetail(studyTask.getWorkOrderId(), false);
            }
        }
    }

    /**
     * 加载任务单详情
     *
     * @param resId
     * @param isFinish
     */
    public void loadCourseDetail(String resId, final boolean isFinish) {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("resId", resId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        StringBuilder builder = new StringBuilder();
        builder.append("?j=" + jsonObject.toString());
        String url = ServerUrl.WAWATV_COURSE_DETAIL_URL + builder.toString();
        ThisStringRequest request = new ThisStringRequest(
                Request.Method.GET, url, new Listener<String>() {
            @Override
            public void onSuccess(String jsonString) {
                if (getActivity() == null) {
                    return;
                }
                if (jsonString != null) {
                    CourseUploadResult uploadResult = JSON.parseObject(
                            jsonString,
                            CourseUploadResult.class);
                    if (uploadResult != null && uploadResult.code == 0) {
                        CourseData courseData = uploadResult.getData().get(0);
                        if (courseData != null) {
                            prepareOpenCourse(23, String.valueOf(courseData.id));
                        }
                    } else {
                        TipMsgHelper.ShowLMsg(getActivity(), R.string.resource_not_exist);
                        if (isFinish && getActivity() != null) {
                            getActivity().finish();
                        }
                    }
                }
            }

            @Override
            public void onError(NetroidError error) {
                if (getActivity() == null) {
                    return;
                }
                TipMsgHelper.ShowLMsg(getActivity(), R.string.resource_not_exist);
                if (isFinish && getActivity() != null) {
                    getActivity().finish();
                }
            }
        });
        request.addHeader("Accept-Encoding", "*");
        request.start(getActivity());
    }

    private void prepareOpenCourse(int type, String courseId) {
        WawaCourseUtils utils = new WawaCourseUtils(getActivity());
        utils.loadSplitLearnCardDetail(courseId, true);
        utils.setOnSplitCourseDetailFinishListener(new WawaCourseUtils.OnSplitCourseDetailFinishListener() {
            @Override
            public void onSplitCourseDetailFinish(SplitCourseInfo info) {
                if (info != null) {
                    CourseData courseData = info.getCourseData();
                    if (courseData == null) return;
                    if (roleType == 0) {//老师
                        NewResourceInfo newResourceInfo = courseData.getNewResourceInfo();
                        if (newResourceInfo != null) {
                            ActivityUtils.openOnlineOnePage(getActivity(), newResourceInfo, true, null);
                        }
                    } else {
                        processData(courseData, true);
                    }
                }
            }
        });
    }

    private void processData(CourseData courseData, boolean isFinish) {
        if (courseData != null) {
            int resType = courseData.type % ResType.RES_TYPE_BASE;
            if (resType == ResType.RES_TYPE_COURSE_SPEAKER ||
                    resType == ResType.RES_TYPE_COURSE ||
                    resType == ResType.RES_TYPE_OLD_COURSE) {
                ActivityUtils.playOnlineCourse(getActivity(), courseData.getCourseInfo(), false,
                        null);
                if (isFinish) {
                    finish();
                }
            } else if (resType == ResType.RES_TYPE_STUDY_CARD) {
                downLoadOnePageData(courseData);
            } else if (resType == ResType.RES_TYPE_ONEPAGE) {
                if (TaskType == StudyTaskType.RETELL_WAWA_COURSE) {
                    downLoadOnePageData(courseData);
                } else {
                    ActivityUtils.openOnlineOnePage(getActivity(), courseData.getNewResourceInfo(),
                            true, null);
                }
            } else if (resType == ResType.RES_TYPE_NOTE) {
                //直接打开帖子
                if (!TextUtils.isEmpty(courseData.resourceurl)) {
                    ActivityUtils.openOnlineNote(getActivity(), courseData.getCourseInfo(),
                            false, true);
                }
            }
        }
    }

    /**
     * 下载有声相册
     *
     * @param courseData
     */
    private void downLoadOnePageData(CourseData courseData) {
        DownloadOnePageTask task = new DownloadOnePageTask(getActivity(), courseData.
                resourceurl, courseData.nickname, courseData.screentype, Utils
                .DOWNLOAD_TEMP_FOLDER, null);
        task.setCallbackListener(new CallbackListener() {
            @Override
            public void onBack(Object result) {
                if (result != null) {
                    LocalCourseDTO data = (LocalCourseDTO) result;
                    if (commitDataType == SelectedReadingDetailFragment.BtnEntity.TYPE_QUESTION_COURSE) {
//                enterEditCourseEvent(data);
                    } else if (commitDataType == SelectedReadingDetailFragment.BtnEntity.TYPE_MAKE_TASK) {
                        openLocalOnePage(data, data.getmOrientation());
                    } else if (TaskType == StudyTaskType.RETELL_WAWA_COURSE) {
                        //复述有声相册
                        openLocalOnePageRetellCourse(data, data.getmOrientation());
                    }
                }
            }
        });
        task.checkCanReplaceIPAddress(courseData.id, courseData.type, task);
    }

    /**
     * 复述课件 有声相册
     *
     * @param data
     * @param screenType
     */
    private void openLocalOnePageRetellCourse(LocalCourseDTO data, int screenType) {
        if (data == null) {
            return;
        }
        CreateSlideHelper.CreateSlideParam param = new CreateSlideHelper.CreateSlideParam
                (getActivity(), null, data.getmPath(), data.getmTitle(), data.getmDescription(),
                        screenType);

        param.fromType = SlideManagerHornForPhone.FromWhereData.FROM_STUDY_TASK_COURSE;
        param.mSchoolId = studyTask.getSchoolId();
        param.mClassId = studyTask.getClassId();
        param.mTitle = task.getTaskTitle();
        //解决喇叭作者不同步的问题
        if (roleType == RoleType.ROLE_TYPE_PARENT) {
            param.mMemberId = StudentId;
            if (stuUserInfo != null) {
                param.mUserInfo = stuUserInfo;
            }
        }
        CreateSlideHelper.startSlide(param, REQUEST_CODE_RETELLCOURSE);
    }

    private void openLocalOnePage(LocalCourseDTO data, int screenType) {
        if (data == null) {
            return;
        }
        CreateSlideHelper.CreateSlideParam param = new CreateSlideHelper.CreateSlideParam
                (getActivity(), null, data.getmPath(), data.getmTitle(), data.getmDescription(),
                        screenType);
        param.mIsScanTask = true;
        param.mIsIntroducationTask = true;
        param.mSchoolId = studyTask.getSchoolId();
        param.mClassId = studyTask.getClassId();
        param.mTitle = studyTask.getTaskTitle();
        //解决喇叭作者不同步的问题
        if (roleType == RoleType.ROLE_TYPE_PARENT) {
            param.mMemberId = StudentId;
            if (stuUserInfo != null) {
                param.mUserInfo = stuUserInfo;
            }
        }
        CreateSlideHelper.startSlide(param, Common.ACTIVITY_REQUEST_ATTACHMENGT_EDIT);
    }

    private void createNullImageCourse(int orientation, int slideType) {

        int haveFree = Utils.checkStorageSpace(getActivity());
        if (haveFree == 0) {
            CreateSlideHelper.CreateSlideParam param = new CreateSlideHelper.CreateSlideParam();
            param.mFragment = HomeworkCommitFragment.this;
            param.mEntryType = Common.LIST_TYPE_SHARE;
            param.mEditable = true;
            param.mSlideType = slideType;
            param.mIsIntroducationTask = true;
            param.mTitle = task.getTaskTitle();
            if (slideType == CreateSlideHelper.SLIDETYPE_IMAGE) {
                param.mIsPickOneImage = false;
            }
            UserInfo userInfo = getUserInfo();
            if (userInfo != null && !TextUtils.isEmpty(userInfo.getMemberId())) {
                param.mMemberId = userInfo.getMemberId();
            }
            param.mSlideSaveBtnParam = new CreateSlideHelper.SlideSaveBtnParam(true, true, true);
            param.mOrientation = orientation;
            param.courseMode = WawaCourseChoiceFragment.CourseMode.READ;
            CreateSlideHelper.createSlide(param);
        }
    }

    /**
     * 创作
     */
    private void createCourse() {
        Intent intent = new Intent(getActivity(), WawaCourseChoiceActivity.class);
        intent.putExtra(SelectedReadingDetailFragment.Constants.INTORDUCTION_CREATE, true);
        intent.putExtra(WawaCourseChoiceFragment.PASS_TASK_TITLE, task.getTaskTitle());
        if (roleType == RoleType.ROLE_TYPE_PARENT) {
            //传递孩子信息
            intent.putExtra(UserInfo.class.getSimpleName(), stuUserInfo);
        }
        getActivity().startActivityForResult(intent, SelectedReadingDetailFragment
                .CREATE_NEW_COURSE_TASK_TYPE);
    }

    private void enterEnglishBuildActivity() {
        Intent intent = new Intent(getActivity(), EnglishWritingBuildActivity.class);
        intent.putExtra("studyTask", studyTask);
        intent.putExtra(EnglishWritingCompletedFragment.Constant.TASKID, TaskId);
        intent.putExtras(getArguments());
        getActivity().startActivityForResult(intent, CampusPatrolPickerFragment
                .REQUEST_CODE_ENGLISH_WRITING_COMMIT);
    }

    private void share() {
        if (studyTask == null) {
            return;
        }
        String url = studyTask.getShareUrl();
        String tempThumbnail = studyTask.getResThumbnailUrl();
        if (tempThumbnail != null && tempThumbnail.contains(",")) {
            int index = tempThumbnail.indexOf(",");
            tempThumbnail = tempThumbnail.substring(0, index);
        }
        if (TaskType == StudyTaskType.RETELL_WAWA_COURSE && (taskResType == ResType.RES_TYPE_PDF
                || taskResType == ResType.RES_TYPE_PPT || taskResType == ResType.RES_TYPE_DOC)) {
            if (newInfoTag != null) {
                tempThumbnail = newInfoTag.getResourceUrl();
            }
        }
        //分享地址为空就没得玩了
        if (TextUtils.isEmpty(url)) {
            return;
        }
        ShareInfo shareInfo = new ShareInfo();
        String title = studyTask.getTaskTitle();
        String thumbnail = tempThumbnail;
        String schoolName = studyTask.getSchoolName();
        String className = studyTask.getClassName();
        String description = schoolName + className;
        String taskDescription = getString(R.string.learning_tasks) + "-" + Utils.getTypeNameByType
                (getActivity(), TaskType);
        taskDescription = taskDescription + "(" + description + ")";
        shareInfo.setTitle(title);
        shareInfo.setContent(taskDescription);
        shareInfo.setTargetUrl(url);
        UMImage umImage = null;
        if (!TextUtils.isEmpty(thumbnail)) {
            umImage = new UMImage(getActivity(), AppSettings.getFileUrl(thumbnail));
        } else {
            umImage = new UMImage(getActivity(), R.drawable.default_cover);
        }
        shareInfo.setuMediaObject(umImage);
        //蛙蛙好友分享资源
        SharedResource resource = new SharedResource();
        resource.setTitle(title);
        resource.setDescription(description);
        resource.setShareUrl(url);
        if (!TextUtils.isEmpty(thumbnail)) {
            resource.setThumbnailUrl(AppSettings.getFileUrl(thumbnail));
        }
        resource.setType(SharedResource.RESOURCE_TYPE_HTML);
        shareInfo.setSharedResource(resource);
        ShareUtils shareUtils = new ShareUtils(getActivity());
        shareUtils.share(getView(), shareInfo);
    }

    private void retellCourse() {
        if (studyTask != null) {
            checkLqCourseShopPermission(studyTask.getResCourseId(), (object) -> {
                boolean isNeedBuyLqCourseShopRes = (boolean) object;
                if (isNeedBuyLqCourseShopRes) {
                    popBuyLqCourseShopResource();
                } else {
                    retellHasPermissionCourse();
                }
            });
        } else {
            retellHasPermissionCourse();
        }

    }

    private void retellHasPermissionCourse() {
        if (task == null) {
            return;
        }
        String taskResId = task.getResId();
        if (!TextUtils.isEmpty(taskResId)) {
            if ((TaskType == StudyTaskType.RETELL_WAWA_COURSE) && (taskResType == ResType
                    .RES_TYPE_ONEPAGE)) {
                //有声相册
                retellOnePageCourse(taskResId);
            } else if ((TaskType == StudyTaskType.RETELL_WAWA_COURSE)
                    && (taskResType == ResType.RES_TYPE_PPT
                    || taskResType == ResType.RES_TYPE_PDF
                    || taskResType == ResType.RES_TYPE_IMG
                    || taskResType == ResType.RES_TYPE_DOC)) {
                //ppt pdf 图片
                if (newInfoTag != null) {
                    List<String> imageList = new ArrayList<>();
                    List<NewResourceInfo> splitInfo = newInfoTag.getSplitInfoList();
                    if (splitInfo != null && splitInfo.size() > 0) {
                        for (int i = 0; i < splitInfo.size(); i++) {
                            NewResourceInfo info = splitInfo.get(i);
                            imageList.add(info.getResourceUrl());
                        }
                    }
                    selectOrientation(imageList, newInfoTag);
                }
            } else {
                //微课
                retellMirCourse();
            }
        }
    }

    private void selectOrientation(final List<String> imageList, final NewResourceInfo newResourceInfo) {
        OrientationSelectDialog dialog = new OrientationSelectDialog(getActivity(),
                new OrientationSelectDialog.SelectHandler() {
                    DoCourseHelper doCourseHelper = new DoCourseHelper(getActivity());

                    @Override
                    public void orientationSelect(int orientation) {
                        if (TaskType == StudyTaskType.TASK_ORDER) {
                            //我要做任务单
                            String savePath = Utils.PIC_TEMP_FOLDER + new MD5FileNameGenerator()
                                    .generate(newResourceInfo.getResourceUrl());
                            downloadCourseImages(savePath, imageList, taskTitle, orientation);
                        } else {
                            doCourseHelper.doRemoteLqCourseFromImage(imageList, newResourceInfo, orientation,
                                    DoCourseHelper.FromType.Do_Retell_Course);
                        }
                    }
                });
        dialog.show();
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        Window window = dialog.getWindow();

        WindowManager m = getActivity().getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = null; // 获取对话框当前的参数值
        if (window != null) {
            p = window.getAttributes();
            window.setGravity(Gravity.CENTER);
            p.width = (int) (d.getWidth() * 0.75f);
            p.height = (int) (d.getHeight() * 0.3);
            window.setAttributes(p);
        }
    }

    /**
     * 复述有声相册类型
     */
    private void retellOnePageCourse(String taskId) {
        if (taskId.contains("-")) {
            taskId = taskId.split("-")[0];
        }
        WawaCourseUtils utils = new WawaCourseUtils(getActivity());
        utils.loadSplitLearnCardDetail(taskId, true);
        utils.setOnSplitCourseDetailFinishListener(new WawaCourseUtils.OnSplitCourseDetailFinishListener() {
            @Override
            public void onSplitCourseDetailFinish(SplitCourseInfo info) {
                if (info != null) {
                    CourseData courseData = info.getCourseData();
                    if (courseData != null) {
                        processData(courseData, true);
                    }
                }
            }
        });
    }

    /**
     * 复述微课
     */
    private void retellMirCourse() {
        final HashMap<String, Object> params = new HashMap<String, Object>();
        if (task != null) {
            String resId = task.getResId();
            if (!TextUtils.isEmpty(resId)) {
//                if (resId.contains("-")) {
//                    resId = resId.substring(0, resId.lastIndexOf("-"));
//                }
                params.put("courseId", resId);
            }
        }
        RequestHelper.RequestResourceResultListener listener = new RequestHelper
                .RequestResourceResultListener(getActivity(), CourseImageListResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                if (getActivity() == null) {
                    return;
                }
                super.onSuccess(jsonString);
                CourseImageListResult result = (CourseImageListResult) getResult();
                if (result == null || result.getCode() != 0) {
                    TipsHelper.showToast(getActivity(), R.string.no_course_images);
                    return;
                }
                List<CourseData> courseDatas = result.getCourse();
                if (courseDatas != null && courseDatas.size() > 0) {
                    CourseData courseData = courseDatas.get(0);
                    if (courseData != null && !TextUtils.isEmpty(courseData.resourceurl)) {
                        screenType = courseData.screentype;
                        String savePath = Utils.PIC_TEMP_FOLDER + new MD5FileNameGenerator()
                                .generate(courseData.resourceurl);
                        List<String> paths = result.getData();
                        if (paths == null || paths.size() == 0) {
//                            createNewRetellCourse(screenType);
                            String originVoicePath = null;
                            if (studyTask != null) {
                                originVoicePath = studyTask.getResUrl();
                                if (!TextUtils.isEmpty(originVoicePath)
                                        && originVoicePath.contains(".zip")) {
                                    //截取字符串
                                    originVoicePath = originVoicePath.substring(0,
                                            originVoicePath.indexOf(".zip"));
                                }
                            }
                            if (TaskType == StudyTaskType.RETELL_WAWA_COURSE) {
                                createNewRetellCourse(screenType, originVoicePath, task.getTaskTitle());
                            } else if (TaskType == StudyTaskType.INTRODUCTION_WAWA_COURSE) {
                                //导读
                                if (commitDataType == SelectedReadingDetailFragment.BtnEntity
                                        .TYPE_RETELL_COURSE) {
                                    //复述
                                    createNewRetellCourse(screenType, originVoicePath, task.getTaskTitle());
                                } else if (commitDataType == SelectedReadingDetailFragment
                                        .BtnEntity.TYPE_QUESTION_COURSE)
                                    //提问
                                    createNullImageCourse(screenType, CreateSlideHelper.SLIDETYPE_WHITEBOARD);
                            }
                        } else {
                            //downloadCourseImages(savePath, result.getData(), task.getTaskTitle());
                            if (TaskType == StudyTaskType.RETELL_WAWA_COURSE && TextUtils.equals("1", courseData.getResproperties())) {
                                //自动批阅
                                courseId = task.getResId();
                                if (!courseId.contains("-")) {
                                    courseId = courseId + "-19";
                                }
                                autoMark = true;
                            }
                            checkCanReplaceIPAddress(savePath, result.getData(), task.getTaskTitle(), courseData);
                        }
                    }
                }
            }
        };
        listener.setShowLoading(false);
        RequestHelper.sendGetRequest(getActivity(), ServerUrl.COURSE_IMAGES_URL, params, listener);
    }

    /**
     * 下载自动批阅的文本
     *
     * @param resId
     */
    private void downloadCourseText(String resId, final String savePath, final List<String> paths, final String
            taskTitle, final CourseData courseData) {
        final HashMap<String, Object> params = new HashMap<>();
        if (!resId.contains("-")) {
            resId = resId + "-19";
        }
        params.put("courseId", resId);
        RequestHelper.RequestResourceResultListener listener = new RequestHelper
                .RequestResourceResultListener(getActivity(), AutoMarkTextResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                if (getActivity() == null) {
                    return;
                }
                super.onSuccess(jsonString);
                AutoMarkTextResult result = (AutoMarkTextResult) getResult();
                if (result == null || result.getCode() != 0) {
                    TipsHelper.showToast(getActivity(), R.string.no_course_images);
                } else {
                    List<AutoMarkText> textContent = result.getData().getTextContent();
                    checkCanReplaceIPAddress(savePath, paths, taskTitle, courseData);

                }
            }
        };
        listener.setShowLoading(false);
        RequestHelper.sendGetRequest(getActivity(), ServerUrl.GET_AUTO_MARK_COURSE_TEXT, params, listener);

    }

    /**
     * 校验是否用内网的IP进行下载
     */
    private void checkCanReplaceIPAddress(final String savePath, final List<String> paths, final String
            taskTitle, CourseData courseData) {
        final CheckReplaceIPAddressHelper helper = new CheckReplaceIPAddressHelper(getActivity());
        helper.setResId(courseData.id)
                .setResType(courseData.type)
                .setCallBackListener(new CallbackListener() {
                    @Override
                    public void onBack(Object result) {
                        boolean flag = (boolean) result;
                        List<String> imageUrl = paths;
                        if (flag) {
                            imageUrl = helper.getChangeIPUrlArray(paths);
                        }
                        downloadCourseImages(savePath, imageUrl, taskTitle, courseData.screentype);
                    }
                })
                .checkIP();
    }

    private void downloadCourseImages(String savePath, List<String> paths, final String title,
                                      int screenType) {
        if (paths == null || paths.size() == 0) {
            return;
        }
        CacheCourseImagesTask task = new CacheCourseImagesTask(getActivity(), paths, savePath, title);
        task.setCallbackListener(new CallbackListener() {
            @Override
            public void onBack(Object result) {
                if (result != null) {
                    List<String> paths = (List<String>) result;
                    if (TaskType == StudyTaskType.TASK_ORDER) {
                        //任务单图片、ppt/pdf/doc
                        importDoTaskOrderImage(paths, screenType);
                    } else {
                        importLocalPicResourcesCheck(paths, title);
                    }
                }
            }
        });
        task.execute();
    }

    private void importLocalPicResources(final List<String> paths) {
        if (savePath == null) {
            savePath = Utils.getUserCourseRootPath(getMemeberId(), CourseType.COURSE_TYPE_IMPORT, false);
        }
        importLocalPicResources(paths, task.getTaskTitle());
    }

    private void uploadCourse(final LocalCourseInfo localCourseInfo, final String slidePath) {
        userInfo = getUserInfo();
        if (stuUserInfo != null) {
            userInfo = stuUserInfo;
        }
        if (userInfo == null || TextUtils.isEmpty(userInfo.getMemberId())) {
            ActivityUtils.enterLogin(getActivity());
            return;
        }
        final UploadParameter uploadParameter = UploadUtils.getUploadParameter(userInfo,
                localCourseInfo, null, 1);
        if (uploadParameter != null) {
            //增加参数控制上传的资源是否需要拆分
            if (TaskType == StudyTaskType.RETELL_WAWA_COURSE
                    || TaskType == StudyTaskType.TASK_ORDER
                    || TaskType == StudyTaskType.INTRODUCTION_WAWA_COURSE) {
                uploadParameter.setIsNeedSplit(false);
            }
            showLoadingDialog();
            FileZipHelper.ZipUnzipParam param = new FileZipHelper.ZipUnzipParam(
                    localCourseInfo.mPath, Utils.TEMP_FOLDER + Utils.getFileNameFromPath
                    (localCourseInfo.mPath) + Utils.COURSE_SUFFIX);
            FileZipHelper.zip(param,
                    new FileZipHelper.ZipUnzipFileListener() {
                        @Override
                        public void onFinish(
                                FileZipHelper.ZipUnzipResult result) {
                            // TODO Auto-generated method stub
                            if (result != null && result.mIsOk) {
                                uploadParameter.setZipFilePath(result.mParam.mOutputPath);
                                UploadUtils.uploadResource(getActivity(), uploadParameter, new
                                        CallbackListener() {
                                            @Override
                                            public void onBack(final Object result) {
                                                if (getActivity() != null) {
                                                    getActivity().runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            dismissLoadingDialog();
                                                            if (result != null) {
                                                                CourseUploadResult uploadResult = (CourseUploadResult) result;
                                                                if (uploadResult.code != 0) {
                                                                    TipMsgHelper.ShowLMsg(getActivity(), R.string.upload_file_failed);
                                                                    return;
                                                                }
                                                                if (uploadResult.data != null && uploadResult.data.size() > 0) {
                                                                    final CourseData courseData = uploadResult.data.get(0);
                                                                    if (courseData != null) {
                                                                        commitStudentCourse(userInfo, courseData, slidePath);
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    });

                                                }
                                            }
                                        });
                            }
                        }
                    });
        }
    }

    private void commitStudentCourse(final UserInfo userInfo, final CourseData courseData, final String slidePath) {
        if (task == null) {
            return;
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("TaskId", task.getId());
        if (roleType == RoleType.ROLE_TYPE_STUDENT) {
            params.put("StudentId", userInfo.getMemberId());
        } else if (roleType == RoleType.ROLE_TYPE_PARENT) {
            //家长复述微课需要传递孩子的Id
            params.put("StudentId", StudentId);
        }
        if (courseData != null) {
            params.put("StudentResId", courseData.getIdType());
            params.put("StudentResUrl", courseData.resourceurl);
            params.put("StudentResTitle", courseData.nickname);
        }

        RequestHelper.RequestDataResultListener listener = new RequestHelper
                .RequestDataResultListener(getActivity(), DataResult.class) {
            @Override
            public void onSuccess(String json) {
                try {
                    if (getActivity() == null) {
                        return;
                    }
                    DataResult result = JSON.parseObject(json, DataResult.class);
                    CommitTaskResult taskResult = JSON.parseObject(json, CommitTaskResult.class);
                    if (result != null && result.isSuccess()) {
                        //同步制作记录信息
                        synchronizationStudyInfoRecord(userInfo, courseData);
                        //通知前面刷新
                        setHasCommented(true);
                        //上传成功删除微课对应的素材
                        String temSlidePath = slidePath;
                        if (taskResType != ResType.RES_TYPE_ONEPAGE) {
                            if (!TextUtils.isEmpty(temSlidePath)) {
                                if (temSlidePath.endsWith(File.separator)) {
                                    temSlidePath = temSlidePath.substring(0, temSlidePath.length() - 1);
                                }
                                LocalCourseDTO.deleteLocalCourseByPath(getActivity(), getMemeberId(),
                                        temSlidePath, true);
                            }
                        }
                        TipMsgHelper.ShowLMsg(getActivity(), getString(R.string.save_to_lq_cloud));
                        //刷新页面
                        //5.10屏蔽
                       /* if (Double.valueOf(score) > -1) {
                            SetCommitTaskScore(taskResult.Model.CommitTaskId, courseData);
                            return;
                        }*/
                        refreshData();
                        setHasCommented(true);
                        EventBus.getDefault().post(new MessageEvent(EventConstant.TRIGGER_UPDATE_COURSE));
                    } else {
                        String errorMessage = getString(R.string.publish_course_error);
                        if (result != null && !TextUtils.isEmpty(result.getErrorMessage())) {
                            errorMessage = result.getErrorMessage();
                        }
                        TipMsgHelper.ShowLMsg(getActivity(), errorMessage);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.PUBLISH_STUDENT_HOMEWORK_URL, params,
                listener);
    }

    private void enterHomeworkFinishStatusActivity() {
        Intent intent = new Intent(getActivity(), HomeworkFinishStatusActivity.class);
        Bundle bundle = new Bundle();
        //校园巡查标识
        bundle.putBoolean(CampusPatrolMainFragment.IS_CAMPUS_PATROL_TAG, isCampusPatrolTag);
        //角色信息
        bundle.putInt(HomeworkFinishStatusActivity.Constants.ROLE_TYPE, roleType);
        bundle.putString(HomeworkFinishStatusActivity.Constants.TASK_ID, TaskId);
        bundle.putString(HomeworkFinishStatusActivity.Constants.TASK_TITLE, taskTitle);
        //类型信息
        bundle.putInt(HomeworkFinishStatusActivity.Constants.TASK_TYPE, TaskType);
        bundle.putBoolean(HomeworkFinishStatusActivity.Constants.IS_OWNER_TASK, isOwnerTask);
        bundle.putBoolean(HomeworkFinishStatusActivity.Constants.EXTRA_IS_SUPER_THIRD_TASK, isSuperChildTask);
        if (homeworkListInfo != null) {
            bundle.putBoolean(HomeworkFinishStatusActivity.Constants.EXTRA_ISONLINEREPORTER, homeworkListInfo.isOnlineReporter() || homeworkListInfo.isOnlineHost());
        }
        bundle.putBoolean(ActivityUtils.EXTRA_IS_HISTORY_CLASS, isHistoryClass);
        if (studyTask != null) {
            bundle.putString(HomeworkFinishStatusActivity.Constants.EXTRA_IS_TASK_COURSE_RESID, studyTask.getResId());
        }
        intent.putExtras(bundle);
        startActivityForResult(intent, CampusPatrolPickerFragment.REQUEST_CODE_FINISH_STATUS);
    }

    private void enterTopicDiscussionActivity() {
        Intent intent = new Intent(getActivity(), TopicDiscussionActivity.class);
        intent.putExtra("TaskId", Integer.parseInt(TaskId));
        intent.putExtra("roleType", roleType);
        intent.putExtra("fromType", "commitHomework");
        startActivityForResult(intent, CampusPatrolPickerFragment.REQUEST_CODE_DISCUSSION_TOPIC);
    }

    private void importAskQuesitonImage(List<String> imagePaths) {
        mCreateSlideParam = new CreateSlideHelper.CreateSlideParam();
        mCreateSlideParam.mActivity = getActivity();
        mCreateSlideParam.mFragment = HomeworkCommitFragment.this;
        mCreateSlideParam.mEntryType = Common.LIST_TYPE_SHARE;
        mCreateSlideParam.mEditable = true;
        mCreateSlideParam.mIsPickOneImage = false;
        mCreateSlideParam.mSlideType = CreateSlideHelper.SLIDETYPE_IMAGE;
        mCreateSlideParam.mOrientation = screenType;
        mCreateSlideParam.mIsIntroducationTask = true;
        mCreateSlideParam.mTitle = task.getTaskTitle();
        //家长提交孩子信息
        if (roleType == RoleType.ROLE_TYPE_PARENT) {
            mCreateSlideParam.mMemberId = StudentId;
            if (stuUserInfo != null) {
                mCreateSlideParam.mUserInfo = stuUserInfo;
            }
        } else {
            UserInfo userInfo = getUserInfo();
            if (userInfo != null && !TextUtils.isEmpty(userInfo.getMemberId())) {
                mCreateSlideParam.mMemberId = userInfo.getMemberId();
            }
        }
        mCreateSlideParam.mSlideSaveBtnParam = new CreateSlideHelper.SlideSaveBtnParam(true,
                true, true);
        mCreateSlideParam.courseMode = WawaCourseChoiceFragment.CourseMode.READ;
        mCreateSlideParam.mAttachmentPath = SlideManager.INSERT_IMAGES;
        mCreateSlideParam.mIsCreateAndPassResParam = true;
        mCreateSlideParam.mSlideParam = CreateSlideHelper.getDefaultSlideParam();
        List<com.libs.yilib.pickimages.MediaInfo> imageInfos = new ArrayList<>();
        if (imagePaths != null && imagePaths.size() > 0) {
            imageInfos = getMediaInfos(imagePaths);
            mCreateSlideParam.mMediaInfos = (ArrayList<com.libs.yilib.pickimages.MediaInfo>) imageInfos;
            mCreateSlideParam.mMediaType = com.lqwawa.client.pojo.MediaType.PICTURE;
            Intent it = CreateSlideHelper.getSlideNewIntent(mCreateSlideParam);
            getActivity().startActivityForResult(it, REQUEST_CODE_SLIDE);
        }
    }

    private List<com.libs.yilib.pickimages.MediaInfo> getMediaInfos(List<String> imagePaths) {
        List<com.libs.yilib.pickimages.MediaInfo> imageInfos = new ArrayList<>();
        if (imagePaths != null && imagePaths.size() > 0) {
            for (int i = 0; i < imagePaths.size(); i++) {
                com.libs.yilib.pickimages.MediaInfo mediaInfo = new com.libs.yilib.pickimages.
                        MediaInfo(imagePaths.get(i));
                imageInfos.add(mediaInfo);
            }
            return imageInfos;
        }
        return null;
    }

    private LocalCourseInfo getLocalCourseInfo(String coursePath) {
        LocalCourseInfo result = null;
        LocalCourseDao localCourseDao = new LocalCourseDao(getActivity());
        try {
            LocalCourseDTO localCourseDTO = localCourseDao.getLocalCourseDTOByPath
                    (getMemeberId(), coursePath);
            if (localCourseDTO != null) {
                return localCourseDTO.toLocalCourseInfo();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (TaskType == StudyTaskType.SUBMIT_HOMEWORK) {
            CreateSlideHelper.processActivityResule(null,
                    HomeworkCommitFragment.this, requestCode, resultCode, data);
        } else if (TaskType == StudyTaskType.RETELL_WAWA_COURSE) {
            if (requestCode == REQUEST_CODE_RETELLCOURSE || requestCode == Common
                    .ACTIVITY_REQUEST_EDITTEMPLATES_BASE + Common.LIST_TYPE_SHARE) {
                if (data != null) {
                    String slidePath = data.getStringExtra(SlideManager
                            .EXTRA_SLIDE_PATH);
                    String coursePath = data.getStringExtra(SlideManager
                            .EXTRA_COURSE_PATH);
                    LogUtils.logi("TEST", "SlidePath = " + slidePath);
                    LogUtils.logi("TEST", "CoursePath = " + coursePath);
                    if (taskResType == ResType.RES_TYPE_ONEPAGE) {
                        if (!TextUtils.isEmpty(slidePath)) {
                            LocalCourseInfo info = getLocalCourseInfo(slidePath);
                            if (info != null) {
                                uploadCourse(info, slidePath);
                            }
                        }
                    } else {
                        if (!TextUtils.isEmpty(slidePath) && !TextUtils.isEmpty(coursePath)) {
                            LocalCourseInfo info = getLocalCourseInfo(coursePath);
                            if (info != null) {
                                uploadCourse(info, slidePath);
                            }
                        } else if (!TextUtils.isEmpty(slidePath)) {
                            //只打开素材没有录制微课，此时slidePath不空，coursePath空值，此时删除素材
                            if (slidePath.endsWith(File.separator)) {
                                slidePath = slidePath.substring(0, slidePath.length() - 1);
                            }
                            LocalCourseDTO.deleteLocalCourseByPath(getActivity(), getMemeberId(),
                                    slidePath, true);
                        }
                    }
                }
            }
        } else {
            //导读和任务单都有查看任务单、做任务单。
            //复述课件
            if (requestCode == REQUEST_CODE_RETELLCOURSE) {
                if (data != null) {
                    String slidePath = data.getStringExtra(SlideManager.EXTRA_SLIDE_PATH);
                    String coursePath = data.getStringExtra(SlideManager.EXTRA_COURSE_PATH);
                    LogUtils.logi("TEST", "SlidePath = " + slidePath);
                    LogUtils.logi("TEST", "CoursePath = " + coursePath);
                    if (!TextUtils.isEmpty(slidePath) && !TextUtils.isEmpty(coursePath)) {
                        LocalCourseInfo info = getLocalCourseInfo(coursePath);
                        if (info != null) {
                            uploadCourse(info, slidePath, SelectedReadingDetailFragment.CommitType.
                                    RETELL_INTRODUCTION_COURSE);
                        }
                    } else if (!TextUtils.isEmpty(slidePath)) {
                        //只打开素材没有录制微课，此时slidePath不空，coursePath空值，此时删除素材
                        if (slidePath.endsWith(File.separator)) {
                            slidePath = slidePath.substring(0, slidePath.length() - 1);
                        }
                        LocalCourseDTO.deleteLocalCourseByPath(getActivity(), getMemeberId(),
                                slidePath, true);
                    }
                }
                //提问
            } else if (requestCode == REQUEST_CODE_EDITCOURSE) {
                if (data != null) {
                    NormalProperty normalProperty = data.getParcelableExtra(PlaybackActivity.
                            EXTRA_NAME_COURSE_PROPERTY);
                    if (normalProperty != null) {
                        String savePath = normalProperty.mPath;
                        if (savePath != null) {
                            LocalCourseDTO dto = LocalCourseDTO.getLocalCourse(getActivity(),
                                    getMemeberId(), savePath);
                            if (dto != null) {
                                LocalCourseInfo info = dto.toLocalCourseInfo();
                                if (info != null) {
                                    uploadCourse(info, null, SelectedReadingDetailFragment.
                                            CommitType.ASK_QUESTION);
                                }
                            }
                        }
                    }
                }
                //创作
            } else if (requestCode == SelectedReadingDetailFragment.CREATE_NEW_COURSE_TASK_TYPE) {
                if (data != null) {
                    String savePath = data.getStringExtra("path");
                    if (savePath != null) {
                        LocalCourseDTO dto = LocalCourseDTO.getLocalCourse(getActivity(), getMemeberId(), savePath);
                        if (dto != null) {
                            LocalCourseInfo info = dto.toLocalCourseInfo();
                            if (info != null) {
                                uploadCourse(info, null, SelectedReadingDetailFragment.CommitType.
                                        CREATE_NEW_COURSE);
                            }
                        }
                    }
                }
                //任务单以及提问的整合
            } else if (requestCode == Common.ACTIVITY_REQUEST_EDITTEMPLATES_BASE + Common.LIST_TYPE_SHARE) {
                if (data != null) {
                    int saveType = data.getIntExtra(SlideManager.EXTRA_SLIDE_PATH_TYPE, 0);
                    if (saveType == 0) {
                        TipMsgHelper.ShowLMsg(getActivity(), getString(R.string.lqcourse_save_local));
                        return;
                    }
                    String coursePath = data.getStringExtra(SlideManager.EXTRA_COURSE_PATH);
                    String slidePath = data.getStringExtra(SlideManager.EXTRA_SLIDE_PATH);
                    if (slidePath != null) {
                        LocalCourseDTO dto = LocalCourseDTO.getLocalCourse(getActivity(), getMemeberId(), slidePath);
                        if (dto != null) {
                            LocalCourseInfo info = dto.toLocalCourseInfo();
                            if (info != null) {
                                if (commitDataType == SelectedReadingDetailFragment.BtnEntity.
                                        TYPE_QUESTION_COURSE) {
                                    uploadCourse(info, null, SelectedReadingDetailFragment.
                                            CommitType.ASK_QUESTION);
                                }
                                if (commitDataType == SelectedReadingDetailFragment.BtnEntity.
                                        TYPE_MAKE_TASK) {
                                    uploadCourse(info, null, SelectedReadingDetailFragment.
                                            CommitType.TASK_ORDER);
                                }
                            }
                        }
                    }
                    if (!TextUtils.isEmpty(slidePath) && !TextUtils.isEmpty(coursePath)) {
                        //只打开素材没有录制微课，此时slidePath不空，coursePath空值，此时删除素材
                        if (slidePath.endsWith(File.separator)) {
                            slidePath = slidePath.substring(0, slidePath.length() - 1);
                        }
                        LocalCourseDTO.deleteLocalCourseByPath(getActivity(), getMemeberId(),
                                slidePath, true);
                    }
                }
                //提问
            } else if (requestCode == REQUEST_CODE_SLIDE) {
                if (data != null) {
                    int saveType = data.getIntExtra(SlideManager.EXTRA_SLIDE_PATH_TYPE, 0);
                    if (saveType == 0) {
                        TipMsgHelper.ShowLMsg(getActivity(), getString(R.string.lqcourse_save_local));
                        return;
                    }
                    String slidePath = data.getStringExtra(SlideManager.EXTRA_SLIDE_PATH);
                    String coursePath = data.getStringExtra(SlideManager.EXTRA_COURSE_PATH);
                    if (slidePath != null) {
                        LocalCourseDTO dto = LocalCourseDTO.getLocalCourse(getActivity(), getMemeberId(), slidePath);
                        if (dto != null) {
                            LocalCourseInfo info = dto.toLocalCourseInfo();
                            if (info != null) {
                                uploadCourse(info, null, SelectedReadingDetailFragment.CommitType.
                                        ASK_QUESTION);
                            }
                        }
                    }
                    LogUtils.logi("TEST", "SlidePath = " + slidePath);
                    LogUtils.logi("TEST", "CoursePath = " + coursePath);
                    if (!TextUtils.isEmpty(slidePath) && !TextUtils.isEmpty(coursePath)) {
                        //只打开素材没有录制微课，此时slidePath不空，coursePath空值，此时删除素材
                        if (slidePath.endsWith(File.separator)) {
                            slidePath = slidePath.substring(0, slidePath.length() - 1);
                        }
                        LocalCourseDTO.deleteLocalCourseByPath(getActivity(), getMemeberId(),
                                slidePath, true);
                    }
                }
            }
        }
//        super.onActivityResult(requestCode, resultCode, data);

        //讨论返回后刷新逻辑
        if (data == null) {
            if (requestCode == CampusPatrolPickerFragment.REQUEST_CODE_DISCUSSION_TOPIC) {
                //讨论话题
                if (TopicDiscussionFragment.hasCommented()) {
                    //通知之前的页面，需要刷新。
                    setHasCommented(true);
                    //reset value
                    TopicDiscussionFragment.setHasCommented(false);
                    //需要刷新
                    refreshData();
                }
            } else if (requestCode == CampusPatrolPickerFragment
                    .REQUEST_CODE_DISCUSSION_COURSE_DETAILS) {
                //复述课件
                if (PictureBooksDetailFragment.hasCommented()) {
                    //通知之前的页面，需要刷新。
                    setHasCommented(true);
                    //reset value
                    PictureBooksDetailFragment.setHasCommented(false);
                    //需要刷新
                    refreshData();
                }

            } else if (requestCode == CampusPatrolPickerFragment
                    .REQUEST_CODE_DISCUSSION_INTRODUCTION) {
                //导读
                //评论改变
                if (SelectedReadingDetailFragment.hasCommented()) {
                    //通知之前的页面，需要刷新。
                    setHasCommented(true);
                    //reset value
                    SelectedReadingDetailFragment.setHasCommented(false);
                    //需要刷新
                    refreshData();
                }

                //提交了作业,这里需要通知前面的页面刷新。
                if (SelectedReadingDetailFragment.hasHomeworkUploaded()) {
                    SelectedReadingDetailFragment.setHasHomeworkUploaded(false);
                    //通知之前的页面，需要刷新。
                    setHasCommented(true);
                    //刷新页面
                    refreshData();
                }

            } else if (requestCode == CampusPatrolPickerFragment
                    .EDIT_NOTE_DETAILS_REQUEST_CODE) {
                //编辑帖子
                if (OnlineMediaPaperActivity.hasContentChanged()) {
                    //通知之前的页面，需要刷新。
                    setHasCommented(true);
                    //reset value
                    OnlineMediaPaperActivity.setHasContentChanged(false);
                    //需要刷新
                    refreshData();
                }
            } else if (requestCode == SlideManager.REQUEST_CODE_PICK_IMAGE
                    || requestCode == Common.ACTIVITY_REQUEST_EDITTEMPLATES_BASE + Common.LIST_TYPE_SHARE
                    || requestCode == Common.LIST_TYPE_SHARE) {
                if (resultCode != Activity.RESULT_OK) {
                    //通知之前的页面，需要刷新。
                    setHasCommented(true);
                    refreshData();
                }
            } else if (requestCode == CampusPatrolPickerFragment
                    .REQUEST_CODE_FINISH_STATUS) {
                //编辑帖子
                if (HomeworkFinishStatusFragment.hasContentChanged()) {
                    //reset value
                    HomeworkFinishStatusFragment.setHasContentChanged(false);
                    //通知之前的页面，需要刷新。
//                    setHasCommented(true);
                    //需要刷新
                    refreshData();
                }
            }
        }
    }

    private void uploadCourse(final LocalCourseInfo localCourseInfo, final String slidePath, final String commitType) {
        userInfo = getUserInfo();
        if (roleType == RoleType.ROLE_TYPE_PARENT) {
            if (stuUserInfo != null) {
                userInfo = stuUserInfo;
            }
        }
        if (userInfo == null || TextUtils.isEmpty(userInfo.getMemberId())) {
            ActivityUtils.enterLogin(getActivity());
            return;
        }
        final UploadParameter uploadParameter = UploadUtils.getUploadParameter(userInfo,
                localCourseInfo, null, 1);
        if (uploadParameter != null) {
            //增加参数控制上传的资源是否需要拆分
            if (TaskType == StudyTaskType.RETELL_WAWA_COURSE
                    || TaskType == StudyTaskType.TASK_ORDER
                    || TaskType == StudyTaskType.INTRODUCTION_WAWA_COURSE) {
                uploadParameter.setIsNeedSplit(false);
            }
            showLoadingDialog();
            FileZipHelper.ZipUnzipParam param = new FileZipHelper.ZipUnzipParam(
                    localCourseInfo.mPath, Utils.TEMP_FOLDER + Utils.getFileNameFromPath
                    (localCourseInfo.mPath) + Utils.COURSE_SUFFIX);
            FileZipHelper.zip(param, new FileZipHelper.ZipUnzipFileListener() {
                @Override
                public void onFinish(FileZipHelper.ZipUnzipResult result) {
                    // TODO Auto-generated method stub
                    if (result != null && result.mIsOk) {
                        uploadParameter.setZipFilePath(result.mParam.mOutputPath);
                        UploadUtils.uploadResource(getActivity(), uploadParameter, new CallbackListener() {
                            @Override
                            public void onBack(final Object result) {
                                if (getActivity() != null) {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            dismissLoadingDialog();
                                            if (result != null) {
                                                CourseUploadResult uploadResult = (CourseUploadResult) result;
                                                if (uploadResult.code != 0) {
                                                    TipMsgHelper.ShowLMsg(getActivity(), R.string.upload_file_failed);
                                                    return;
                                                }
                                                if (uploadResult.data != null && uploadResult.data.size() > 0) {
                                                    final CourseData courseData = uploadResult.data.get(0);
                                                    if (courseData != null) {
                                                        commitStudentCourse(userInfo, courseData, slidePath, commitType);
                                                    }
                                                }
                                            }
                                        }
                                    });
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    /**
     * 导读提交
     *
     * @param userInfo
     * @param courseData
     * @param slidePath
     * @param commitType
     */
    private void commitStudentCourse(final UserInfo userInfo, final CourseData courseData, final String
            slidePath, String commitType) {
        Map<String, Object> params = new HashMap<String, Object>();
        if (task != null) {
            params.put("TaskId", task.getId());
        }
        if (roleType == RoleType.ROLE_TYPE_STUDENT) {
            params.put("StudentId", userInfo.getMemberId());
        } else if (roleType == RoleType.ROLE_TYPE_PARENT) {
            //家长复述微课需要传递孩子的Id
            params.put("StudentId", StudentId);
        }
        params.put("CommitType", commitType);
        if (courseData != null) {
            params.put("StudentResId", courseData.getIdType());
            params.put("StudentResUrl", courseData.resourceurl);
            params.put("StudentResTitle", courseData.nickname);
        }

        RequestHelper.RequestDataResultListener listener = new RequestHelper
                .RequestDataResultListener(getActivity(), DataResult.class) {
            @Override
            public void onSuccess(String json) {
                try {
                    if (getActivity() == null) {
                        return;
                    }
                    DataResult result = JSON.parseObject(json, DataResult.class);
                    if (result != null && result.isSuccess()) {
                        //同步制作记录信息
                        synchronizationStudyInfoRecord(userInfo, courseData);
                        //上传成功删除微课对应的素材
                        String temSlidePath = slidePath;
                        if (!TextUtils.isEmpty(temSlidePath)) {
                            if (temSlidePath.endsWith(File.separator)) {
                                temSlidePath = temSlidePath.substring(0, temSlidePath.length() - 1);
                            }
                            LocalCourseDTO.deleteLocalCourseByPath(getActivity(), getMemeberId(),
                                    temSlidePath, true);
                        }
//                        TipMsgHelper.ShowLMsg(getActivity(), getString(R.string.save_to_lq_cloud));
                        //上传成功要刷新
                        refreshData();
                        EventBus.getDefault().post(new MessageEvent(EventConstant.TRIGGER_UPDATE_COURSE));
                    } else {
                        String errorMessage = getString(R.string.publish_course_error);
                        if (result != null && !TextUtils.isEmpty(result.getErrorMessage())) {
                            errorMessage = result.getErrorMessage();
                        }
                        TipMsgHelper.ShowLMsg(getActivity(), errorMessage);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.PUBLISH_STUDENT_HOMEWORK_URL, params, listener);
    }

    /**
     * 上传自动批阅分数
     */
    private void SetCommitTaskScore(int taskId, CourseData courseData) {
        Map<String, Object> params = new HashMap<>();
        if (task != null) {
            params.put("CommitTaskId", taskId);
        }
        params.put("TaskScore", score);
        if (courseData != null) {
            params.put("ResId", courseData.getIdType());
            params.put("ResUrl", courseData.resourceurl);
        }
        params.put("AutoEvalCompanyType", schemeId);
        params.put("AutoEvalContent", resultContent);
        RequestHelper.RequestListener<DataResult> listener = new RequestHelper.RequestListener<DataResult>(getActivity(), DataResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                if (getActivity() == null) {
                    return;
                }
                super.onSuccess(jsonString);
                if (getResult() != null && getResult().isSuccess()) {
                    //自动批阅分数上传成功
                    refreshData();
                    setHasCommented(true);
                }
                score = "-1";
            }
        };
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.COMMIT_AUTO_MARK_SCORE,
                params, listener);
    }

    @Override
    protected void importLocalPicResourcesCheck(List<String> imagePaths, String title) {
        if (imagePaths != null && imagePaths.size() > 0) {
            if (TaskType == StudyTaskType.RETELL_WAWA_COURSE) {
                importLocalPicResources(imagePaths);
            } else if (TaskType == StudyTaskType.INTRODUCTION_WAWA_COURSE) {
                //处理导读
                //复述课件类型
                if (commitDataType == SelectedReadingDetailFragment.BtnEntity.TYPE_RETELL_COURSE) {
                    importLocalPicResources(imagePaths);
                }
                //提问
                if (commitDataType == SelectedReadingDetailFragment.BtnEntity.TYPE_QUESTION_COURSE) {
                    importAskQuesitonImage(imagePaths);
                }
            }
        }
    }

    private void importDoTaskOrderImage(List<String> imagePaths, int screenType) {
        mCreateSlideParam = new CreateSlideHelper.CreateSlideParam();
        mCreateSlideParam.mActivity = getActivity();
        mCreateSlideParam.mFragment = HomeworkCommitFragment.this;
        mCreateSlideParam.mEntryType = Common.LIST_TYPE_SHARE;
        mCreateSlideParam.mEditable = true;
        mCreateSlideParam.mIsPickOneImage = false;
        mCreateSlideParam.mSlideType = CreateSlideHelper.SLIDETYPE_IMAGE;
        mCreateSlideParam.mOrientation = screenType;

        mCreateSlideParam.mTitle = task.getTaskTitle();
        mCreateSlideParam.mSchoolId = studyTask.getSchoolId();
        mCreateSlideParam.mClassId = studyTask.getClassId();
        //家长提交孩子信息
        if (roleType == RoleType.ROLE_TYPE_PARENT) {
            mCreateSlideParam.mMemberId = StudentId;
            if (stuUserInfo != null) {
                mCreateSlideParam.mUserInfo = stuUserInfo;
            }
        } else {
            UserInfo userInfo = getUserInfo();
            if (userInfo != null && !TextUtils.isEmpty(userInfo.getMemberId())) {
                mCreateSlideParam.mMemberId = userInfo.getMemberId();
            }
        }
        mCreateSlideParam.mSlideSaveBtnParam = new CreateSlideHelper.SlideSaveBtnParam(true,
                true, true);
        mCreateSlideParam.mAttachmentPath = SlideManager.INSERT_IMAGES;
        mCreateSlideParam.mIsCreateAndPassResParam = true;
        mCreateSlideParam.fromType = SlideManagerHornForPhone.FromWhereData.FROM_STUDY_TASK_COURSE;
        mCreateSlideParam.mSlideParam = CreateSlideHelper.getDefaultSlideParam();
        List<com.libs.yilib.pickimages.MediaInfo> imageInfos = new ArrayList<>();
        if (imagePaths != null && imagePaths.size() > 0) {
            imageInfos = getMediaInfos(imagePaths);
            mCreateSlideParam.mMediaInfos = (ArrayList<com.libs.yilib.pickimages.MediaInfo>) imageInfos;
            mCreateSlideParam.mMediaType = com.lqwawa.client.pojo.MediaType.PICTURE;
            Intent it = CreateSlideHelper.getSlideNewIntent(mCreateSlideParam);
            startActivityForResult(it, 105);
        }
    }

    @Override
    protected void saveData(Message msg) {
        LocalCourseInfo localCourseInfo = (LocalCourseInfo) msg.obj;
        if (localCourseInfo != null) {
            if (screenType >= 0) {
                localCourseInfo.mOrientation = screenType;
            }
            saveData(localCourseInfo);
            if (studyTask != null) {
                localCourseInfo.mOriginVoicePath = studyTask.getResUrl();
                if (localCourseInfo.mOriginVoicePath != null
                        && localCourseInfo.mOriginVoicePath.contains(".zip")) {
                    //截取字符串
                    localCourseInfo.mOriginVoicePath
                            = localCourseInfo.mOriginVoicePath.substring(0,
                            localCourseInfo.mOriginVoicePath.indexOf(".zip"));
                }
            }
            if (TaskType == StudyTaskType.INTRODUCTION_WAWA_COURSE) {
                enterSlideNew(localCourseInfo, MaterialType.RECORD_BOOK,
                        REQUEST_CODE_RETELLCOURSE, true, TaskType, task.getTaskTitle());
            } else {
                enterSlideNewRetellCourse(localCourseInfo, task.getTaskTitle());
            }
        }
    }

    /**
     * 同步学生完成作业的记录信息
     *
     * @param userInfo
     */
    private void synchronizationStudyInfoRecord(final UserInfo userInfo, final CourseData commitData) {
        if (TaskType == StudyTaskType.RETELL_WAWA_COURSE || TaskType == StudyTaskType.INTRODUCTION_WAWA_COURSE) {
            String courseId = task.getResId();
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
            if (resType == ResType.RES_TYPE_IMG || resType == ResType.RES_TYPE_PPT
                    || resType == ResType.RES_TYPE_PDF || resType == ResType.RES_TYPE_DOC) {
                commitStudyInfoRecordData(userInfo, commitData, 0, 2);
                return;
            }

            if (commitData.type == ResType.RES_TYPE_COURSE_SPEAKER) {
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
                                    commitStudyInfoRecordData(userInfo, commitData, courseData.totaltime, 2);

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
                            if (courseData != null) {
                                commitStudyInfoRecordData(userInfo, commitData, courseData.totaltime, 2);
                            }
                        }
                    });
                }
            } else if (commitData.type == ResType.RES_TYPE_ONEPAGE) {
                if (commitDataType == SelectedReadingDetailFragment.BtnEntity.TYPE_MAKE_TASK) {
                    commitStudyInfoRecordData(userInfo, commitData, 0, 3);
                } else {
                    commitStudyInfoRecordData(userInfo, commitData, 0, 2);
                }
            }
        } else if (TaskType == StudyTaskType.TASK_ORDER) {
            commitStudyInfoRecordData(userInfo, commitData, 0, 3);
        }
    }

    private void commitStudyInfoRecordData(UserInfo userInfo, CourseData courseData, int
            originalTime, int recordType) {
        StudyInfoRecordUtil recordUtil = StudyInfoRecordUtil.getInstance().
                clearData().
                setActivity(getActivity()).
                setCourseData(courseData).
                setCurrentModel(StudyInfoRecordUtil.RecordModel.STUDY_MODEL).
                setRecordType(recordType).
                setOriginalTotalTime(originalTime).
                setSourceType(SourceFromType.STUDY_TASK).
                setUserInfo(userInfo);
        if (recordType == 3) {
            recordUtil.setRecordTime(0);
        } else {
            recordUtil.setRecordTime(TimerUtils.getInstance().getCurrentTotalTime());
        }
        recordUtil.send();
    }

    public void checkLqCourseShopPermission(int resCourseId, CallbackListener listener) {
        if (resCourseId <= 0) {
            listener.onBack(false);
            return;
        }
        if (TextUtils.isEmpty(getMemeberId())) {
            listener.onBack(false);
            return;
        }
        if (roleType == RoleType.ROLE_TYPE_TEACHER) {
            listener.onBack(false);
            return;
        }
        checkLqShopPmnHelper = new CheckLqShopPmnHelper().
                setActivity(getActivity()).
                setFromType(CheckLqShopPmnHelper.FromType.FROM_STUDYTASK_DETAIL).
                setMemberId(getMemeberId()).
                setRoleType(roleType).
                setResCourseId(resCourseId).
                setStudentId(StudentId).
                setSchoolId(studyTask != null ? studyTask.getSchoolId() : "").
                setClassId(studyTask != null ? studyTask.getClassId() : "").
                setCallBackListener(new CallbackListener() {
                    @Override
                    public void onBack(Object result) {
                        listener.onBack(result);
                        EventBus.getDefault().post(new MessageEvent(EventConstant.TRIGGER_UPDATE_COURSE));
                    }
                });
        checkLqShopPmnHelper.check();
    }

    private void popBuyLqCourseShopResource() {
        if (checkLqShopPmnHelper != null) {
            checkLqShopPmnHelper.popBuyLqCourseShopResource(stuUserInfo, new CallbackListener() {
                @Override
                public void onBack(Object result) {
                    if (studyTask != null && roleType == RoleType.ROLE_TYPE_STUDENT) {
                        LQCourseOrderActivity.show(getActivity(), studyTask.getSchoolId(), String.valueOf(studyTask
                                .getResCourseId()));
                    }
                }
            });
        }
    }

    private void checkSpeechAssessmentPermission(String resId, final HomeworkCommitObjectResult result) {
        if (TaskType != StudyTaskType.RETELL_WAWA_COURSE) {
            return;
        }
        if (TextUtils.isEmpty(resId)) {
            return;
        }
        WawaCourseUtils wawaCourseUtils = new WawaCourseUtils(getActivity());
        int courseType = ResType.RES_TYPE_COURSE_SPEAKER;
        if (resId.contains(",")) {
            resId = resId.split(",")[0];
        }
        if (resId.contains("-")) {
            String[] splitArray = resId.split("-");
            resId = splitArray[0];
            courseType = Integer.valueOf(splitArray[1]);
        }
        if (courseType == ResType.RES_TYPE_IMG
                || courseType == ResType.RES_TYPE_PDF
                || courseType == ResType.RES_TYPE_PPT
                || courseType == ResType.RES_TYPE_DOC) {
            if (viewPager == null) {
                initViewPager();
                updateTabData(result);
                dealTaskTypeFinishDetail(result.getModel().getData());
            }
            return;
        }
        wawaCourseUtils.loadCourseDetail(resId);
        wawaCourseUtils.setOnCourseDetailFinishListener(new WawaCourseUtils.OnCourseDetailFinishListener() {
            @Override
            public void onCourseDetailFinish(CourseData courseData) {
                if (getActivity() == null) {
                    return;
                }
                if (courseData != null) {
                    String resproperties = courseData.getResproperties();
                    taskCourseOrientation = courseData.screentype;
                    if (!TextUtils.isEmpty(resproperties)) {
                        propertiesType = Integer.valueOf(resproperties);
                    }
                    if (viewPager == null) {
                        initViewPager();
                        updateTabData(result);
                        dealTaskTypeFinishDetail(result.getModel().getData());
                    }
                    if (propertiesType == 1) {
                        //设置成绩统计可见
                        updateStatisticViewVisible();
                        //语音评测
                        if (roleType == RoleType.ROLE_TYPE_TEACHER) {
                            return;
                        }
                        updateSpeechAssessmentVisible();
                    }
                }
            }
        });
    }

    private void updateSpeechAssessmentVisible() {
        if (taskRequirementFragment != null) {
            taskRequirementFragment.setSpeechAssessmentViewVisible();
        }

        if (completedHomeworkListFragment != null) {
            completedHomeworkListFragment.setSpeechAssessmentViewVisible();
        }

        if (evalHomeworkListFragment != null) {
            evalHomeworkListFragment.setSpeechAssessmentViewVisible();
        }
    }

    private void updateStatisticViewVisible() {
        if (taskRequirementFragment != null) {
            taskRequirementFragment.setStatisticViewVisible();
        }

        if (completedHomeworkListFragment != null) {
            completedHomeworkListFragment.setStatisticViewVisible();
        }

        if (evalHomeworkListFragment != null) {
            evalHomeworkListFragment.setStatisticViewVisible();
        }
    }

    public void enterSpeechAssessmentActivity() {
        if (studyTask != null) {
            checkLqCourseShopPermission(studyTask.getResCourseId(), (object) -> {
                boolean isNeedBuyLqCourseShopRes = (boolean) object;
                if (isNeedBuyLqCourseShopRes) {
                    popBuyLqCourseShopResource();
                } else {
                    enterEvalActivity();
                }
            });
        } else {
            enterEvalActivity();
        }
    }

    private void enterEvalActivity() {
        if (stuUserInfo != null && roleType == RoleType.ROLE_TYPE_PARENT) {
            if (TextUtils.equals(stuUserInfo.getMemberId(), "00000000-0000-0000-0000-000000000000")) {
                stuUserInfo.setMemberId(StudentId);
            }
        }
        SpeechAssessmentActivity.start(
                getActivity(),
                taskCourseOrientation,
                stuUserInfo,
                studyTask.getResId(),
                String.valueOf(studyTask.getId()),
                studyTask.getTaskTitle(),
                ScoringRule == 0 ? 2 : ScoringRule);
    }

    public void enterSpeechAssessmentActivity(String resUrl) {
        SpeechAssessmentActivity.start(
                getActivity(),
                taskCourseOrientation,
                resUrl,
                ScoringRule == 0 ? 2 : ScoringRule);
    }

    public boolean hasEnabledDoToTaskOrderTips() {
        boolean hasEnabled = DemoApplication.getInstance().getPrefsManager()
                .isDoTaskOrderTipsEnabled();
        if (hasEnabled) {
            return true;
        } else {
            //没有check 弹出框
            Dialog doTaskTipDialog = new DoTaskOrderTipsDialog(getActivity(), new CallbackListener() {
                @Override
                public void onBack(Object result) {
                    //答题卡
                    openImage(task, true);
                }
            });
            doTaskTipDialog.setCancelable(true);
            doTaskTipDialog.show();
        }
        return false;
    }

    private void checkTaskOrderAnswerQuestionCard() {
        if (TaskType != StudyTaskType.TASK_ORDER || studyTask == null) {
            return;
        }
        if (taskResType == ResType.RES_TYPE_PDF
                || taskResType == ResType.RES_TYPE_PPT
                || taskResType == ResType.RES_TYPE_IMG
                || taskResType == ResType.RES_TYPE_DOC) {
            return;
        }
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("resId", studyTask.getResId());
            jsonObject.put("needAnswer", true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        StringBuilder builder = new StringBuilder();
        builder.append("?j=" + jsonObject.toString());
        String url = ServerUrl.WAWATV_COURSE_DETAIL_URL + builder.toString();
        showLoadingDialog();
        ThisStringRequest request = new ThisStringRequest(
                Request.Method.GET, url, new Listener<String>() {
            @Override
            public void onSuccess(String jsonString) {
                if (getActivity() == null) {
                    return;
                }
                if (jsonString != null) {
                    CourseUploadResult uploadResult = JSON.parseObject(
                            jsonString, CourseUploadResult.class);
                    if (uploadResult != null && uploadResult.code == 0) {
                        if (uploadResult.getData() != null && uploadResult.getData().size() > 0) {
                            CourseData courseData = uploadResult.getData().get(0);
                            if (courseData != null) {
                                String exerciseTotalScore = courseData.point;
                                if (!TextUtils.isEmpty(exerciseTotalScore)) {
                                    if (answerCardParam == null) {
                                        answerCardParam = new ExerciseAnswerCardParam();
                                    }
                                    answerCardParam.setExerciseTotalScore(exerciseTotalScore);
                                    answerCardParam.setResId(courseData.id + "-" + courseData.type);
                                    answerCardParam.setScreenType(courseData.screentype);
                                    isAnswerTaskOrderQuestion = true;
                                    answerCardParam.setExerciseAnswerString(uploadResult.exercise);
                                    answerCardParam.setTaskId(TaskId);
                                    answerCardParam.setRoleType(roleType);
                                    boolean hasSubjectProblem = DoTaskOrderHelper.hasSubjectProblem(uploadResult.exercise);
                                    answerCardParam.setHasSubjectProblem(hasSubjectProblem);
                                    if (completedHomeworkListFragment != null) {
                                        //更新提交列表的数据
                                        completedHomeworkListFragment.setExerciseAnswerData(
                                                true,
                                                exerciseTotalScore,
                                                hasSubjectProblem);
                                    }

                                    TextView showFullScoreView = (TextView) findViewById(R.id.tv_full_score);
                                    showFullScoreView.setVisibility(View.VISIBLE);
                                    showFullScoreView.setText(getString(R.string.str_full_marks, exerciseTotalScore));
                                }
                            }
                        }
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
        request.start(getActivity());
    }

    public CompletedHomeworkListFragment getCompletedHomeworkListFragment() {
        return completedHomeworkListFragment;
    }

    public EvalHomeworkListFragment getEvalHomeworkListFragment() {
        return evalHomeworkListFragment;
    }

    /**
     * 开始配音的动作
     */
    private void startDubbingVideo(){

    }

    private class MyBroadCastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(COMMIT_AUTO_MARK_SCORE_ACTION)) {
                score = getScore(intent.getStringExtra("score"));
                schemeId = intent.getIntExtra("schemeId", 1);
                resultContent = intent.getStringExtra("result");
            } else if (TextUtils.equals(intent.getAction(), REFRESH_COMMIT_LIST_DATA)) {
                //刷新数据
                refreshData();
            }
        }
    }

    private String getScore(String score) {
        if (score.contains(".")) {
            String[] split = score.split("\\.");
            return split[0];
        }
        return score;
    }

    private void initBroadCastReceiver() {
        receiver = new MyBroadCastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(COMMIT_AUTO_MARK_SCORE_ACTION);
        filter.addAction(BUG_LQ_COURSE_SHOP_SUCCESS);
        filter.addAction(REFRESH_COMMIT_LIST_DATA);
        getActivity().registerReceiver(receiver, filter);
    }

    @Override
    public void onDestroy() {
        if (receiver != null) {
            getActivity().unregisterReceiver(receiver);
        }
        super.onDestroy();
    }
}
