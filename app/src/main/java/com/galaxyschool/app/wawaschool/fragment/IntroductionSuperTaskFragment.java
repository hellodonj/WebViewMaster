package com.galaxyschool.app.wawaschool.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.duowan.mobile.netroid.NetroidError;
import com.galaxyschool.app.wawaschool.ContactsPickerActivity;
import com.galaxyschool.app.wawaschool.HomeworkCommitActivity;
import com.galaxyschool.app.wawaschool.HomeworkFinishStatusActivity;
import com.galaxyschool.app.wawaschool.PickerClassAndGroupActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.StudentFinishedHomeworkListActivity;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.ArrangeLearningTasksUtil;
import com.galaxyschool.app.wawaschool.common.CampusPatrolUtils;
import com.galaxyschool.app.wawaschool.common.CourseOpenUtils;
import com.galaxyschool.app.wawaschool.common.DateUtils;
import com.galaxyschool.app.wawaschool.common.MessageEventConstantUtils;
import com.galaxyschool.app.wawaschool.common.StudyTaskUtils;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.galaxyschool.app.wawaschool.helper.LqIntroTaskHelper;
import com.galaxyschool.app.wawaschool.helper.StudyTaskNetHelper;
import com.galaxyschool.app.wawaschool.pojo.Emcee;
import com.galaxyschool.app.wawaschool.pojo.HomeworkListInfo;
import com.galaxyschool.app.wawaschool.pojo.LookResDto;
import com.galaxyschool.app.wawaschool.pojo.ResType;
import com.galaxyschool.app.wawaschool.pojo.RoleType;
import com.galaxyschool.app.wawaschool.pojo.ShortSchoolClassInfo;
import com.galaxyschool.app.wawaschool.pojo.StudyTaskType;
import com.galaxyschool.app.wawaschool.pojo.UploadCourseType;
import com.galaxyschool.app.wawaschool.pojo.UploadParameter;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseData;
import com.galaxyschool.app.wawaschool.views.ContactsInputBoxDialog;
import com.galaxyschool.app.wawaschool.views.ContactsMessageDialog;
import com.galaxyschool.app.wawaschool.views.slidelistview.SlideListView;
import com.lqwawa.client.pojo.ResourceInfo;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.DataResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.lqwawa.lqbaselib.pojo.MessageEvent;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 布置综合任务界面
 */

public class IntroductionSuperTaskFragment extends ContactsListFragment {
    public static final String TAG = IntroductionSuperTaskFragment.class.getSimpleName();
    private LinearLayout addNewTaskLayout;
    private LinearLayout superTaskHeaderLayout;
    private TextView headRightTextV;
    private TextView taskTitleTextV;
    private TextView finishStudyTaskStatus;
    private TextView taskStartTimeTextV;//开始时间
    private RadioButton immediatelyRb;//立即发布
    private RadioButton answerByTimeRb;//按时间作答
    private RadioButton hasReadPerRb;//可以查看
    private RadioButton noReadPerRb;
    private ConstraintLayout publishTimeAndTypeLayout;
    private TextView showTaskFinishView;//显示任务完成的状态（已完成/未完成）
    private SlideListView listView;
    private int taskType;
    private String headTitle;
    private String[] taskSortNum;
    private List<UploadParameter> uploadParameters = new ArrayList<>();
    private boolean isOnlineSuperTaskDetail;
    private HomeworkListInfo homeworkListInfo;
    private int roleType = -1;
    private String TaskId;
    private boolean isHeadMaster;
    private String sortStudentId;
    private String childId;
    private UserInfo userInfo;
    private List<HomeworkListInfo> superTaskList = new ArrayList<>();
    private boolean isReporter;
    private boolean isHost;
    private boolean isOnlineClass;
    private String classId;
    private String schoolId;
    //布置的任务完成与未完成的情况
    private boolean lookStudentTaskFinish;
    private String studentName;
    private Emcee onlineRes;
    private List<ShortSchoolClassInfo> schoolClassInfos;
    private int currentStudyType;//空中课堂学习类型
    private boolean isPick;
    private HomeworkListInfo selectHomeworkInfo;
    private boolean isHistoryClass;
    private String taskFileName;
    private boolean isFromMoocIntroTask;
    private int airClassId;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_introduction_super_task, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadIntent();
        initViews();
        initAdapterData();
        loadData();
        registResultBroadcast();
        checkClassPlayEnd();
        if (isFromMoocIntroTask){
            notifyAdapterData();
        }
    }

    private void loadData() {
        if (isOnlineSuperTaskDetail || isPick || lookStudentTaskFinish) {
            loadOnlineTaskData();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (CampusPatrolUtils.hasStudyTaskAssigned()) {
            if (isFromMoocIntroTask) {
                CampusPatrolUtils.setHasStudyTaskAssigned(false);
            }
            //布置作业完成，需要刷新页面。
            finish();
        }
        refreshData();
    }

    private void refreshData() {
        if (HomeworkCommitFragment.hasCommented()
                || TopicDiscussionFragment.hasCommented()) {
            loadData();
        }
    }

    private void loadIntent() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            taskType = bundle.getInt(ActivityUtils.EXTRA_TASK_TYPE);
            headTitle = bundle.getString(ActivityUtils.EXTRA_HEADER_TITLE);
            homeworkListInfo = (HomeworkListInfo) bundle.getSerializable(HomeworkListInfo.class.getSimpleName());
            if (homeworkListInfo != null) {
                isReporter = homeworkListInfo.isOnlineReporter();
                isHost = homeworkListInfo.isOnlineHost();
                isOnlineSuperTaskDetail = true;
                airClassId = homeworkListInfo.getAirClassId();
            }
            roleType = bundle.getInt("roleType");
            TaskId = bundle.getString("TaskId");
            sortStudentId = bundle.getString("sortStudentId");
            childId = bundle.getString("StudentId");
            userInfo = (UserInfo) bundle.getSerializable(UserInfo.class.getSimpleName());
            isHeadMaster = bundle.getBoolean(HomeworkMainFragment.Constants.EXTRA_IS_HEAD_MASTER);
            isOnlineClass = getArguments().getBoolean(ActivityUtils.EXTRA_IS_ONLINE_CLASS);
            classId = getArguments().getString(ActivityUtils.EXTRA_CLASS_ID);
            schoolId = getArguments().getString(ActivityUtils.EXTRA_SCHOOL_ID);
            lookStudentTaskFinish = bundle.getBoolean(HomeworkFinishStatusActivity.Constants.EXTRA_STUDENT_FINISH_STUDY_TASK_LIST);
            studentName = bundle.getString("taskTitle");
            currentStudyType = getArguments().getInt(ActivityUtils.EXTRA_STDUY_TYPE);
            onlineRes = (Emcee) getArguments().getSerializable(ActivityUtils.EXTRA_DATA_INFO);
            schoolClassInfos = (List<ShortSchoolClassInfo>) getArguments().getSerializable(ActivityUtils.EXTRA_SCHOOL_INFO_LIST_DATA);
            isPick = bundle.getBoolean(ActivityUtils.EXTRA_IS_PICK);
            if (isPick) {
                selectHomeworkInfo = (HomeworkListInfo) bundle.getSerializable(ListenReadAndWriteStudyTaskFragment.Constants.EXTRA_TASK_INFO_DATA);
//                if (selectHomeworkInfo != null) {
//                    TaskId = selectHomeworkInfo.getTaskId();
//                }
            }
            isHistoryClass = getArguments().getBoolean(ActivityUtils.EXTRA_IS_HISTORY_CLASS);
            isFromMoocIntroTask = getArguments().getBoolean("is_from_mooc_intro_task");
            if (isFromMoocIntroTask){
                uploadParameters = LqIntroTaskHelper.getInstance().getUploadParameters();
            }
        }
        taskSortNum = getResources().getStringArray(R.array.str_array_task_num);
    }

    @SuppressLint("WrongViewCast")
    private void initViews() {
        //左侧返回按钮
        ImageView imageView = (ImageView) findViewById(R.id.contacts_header_left_btn);
        if (imageView != null) {
            imageView.setOnClickListener(this);
        }
        //title
        taskTitleTextV = (TextView) findViewById(R.id.contacts_header_title);
        if (taskTitleTextV != null) {
            if (lookStudentTaskFinish) {
                taskTitleTextV.setText(studentName);
            } else if (TextUtils.isEmpty(headTitle) || isPick) {
                taskTitleTextV.setText(getString(R.string.str_super_task));
            } else {
                taskTitleTextV.setText(headTitle);
            }
        }
        headRightTextV = (TextView) findViewById(R.id.contacts_header_right_btn);
        headRightTextV.setOnClickListener(v -> {
            StudyTaskNetHelper.getInstance().setCallListener(result -> {
                loadData();
            }).setViewOthersTaskPermission(TaskId,homeworkListInfo.getViewOthersTaskPermisson());
        });
        //底部的确实btn
        TextView confirmTextV = (TextView) findViewById(R.id.tv_bottom_confirm);
        if (confirmTextV != null) {
            confirmTextV.setOnClickListener(this);
        }
        //学生的完成的状态
        showTaskFinishView = (TextView) findViewById(R.id.tv_student_task_finish_status);
        //增加新任务的layout
        addNewTaskLayout = (LinearLayout) findViewById(R.id.ll_add_new_task);
        RelativeLayout addRelativeLayout = (RelativeLayout) findViewById(R.id.rl_add_new_task);
        if (addRelativeLayout != null) {
            addRelativeLayout.setOnClickListener(this);
        }
        superTaskHeaderLayout = (LinearLayout) findViewById(R.id.ll_task_detail);
        taskStartTimeTextV = (TextView) findViewById(R.id.tv_publish_start_time);
        immediatelyRb = (RadioButton) findViewById(R.id.rb_publish_right_now);
        answerByTimeRb = (RadioButton) findViewById(R.id.rb_publish_according_time);
        hasReadPerRb = (RadioButton) findViewById(R.id.rb_can_read);
        noReadPerRb = (RadioButton) findViewById(R.id.rb_not_read);
        if (isFromMoocIntroTask){
            boolean answerAtAnyTime = LqIntroTaskHelper.getInstance().getAnswerAtAnyTimeValue();
            if (answerAtAnyTime){
                immediatelyRb.setChecked(true);
                answerByTimeRb.setChecked(false);
            } else {
                immediatelyRb.setChecked(false);
                answerByTimeRb.setChecked(true);
            }
            boolean hasPermission = LqIntroTaskHelper.getInstance().isHasReadPermission();
            if (hasPermission){
                hasReadPerRb.setChecked(true);
                noReadPerRb.setChecked(false);
            } else {
                hasReadPerRb.setChecked(false);
                noReadPerRb.setChecked(true);
            }
        }
        publishTimeAndTypeLayout = (ConstraintLayout) findViewById(R.id.ll_publish_time_and_type);
        if (isOnlineSuperTaskDetail) {
            superTaskHeaderLayout.setVisibility(View.VISIBLE);
            addNewTaskLayout.setVisibility(View.GONE);
            publishTimeAndTypeLayout.setVisibility(View.GONE);
            confirmTextV.setVisibility(View.GONE);
            initOnlineTaskView();
        } else if (lookStudentTaskFinish || isPick) {
            superTaskHeaderLayout.setVisibility(View.GONE);
            addNewTaskLayout.setVisibility(View.GONE);
            publishTimeAndTypeLayout.setVisibility(View.GONE);
            confirmTextV.setVisibility(View.GONE);
        } else {
            superTaskHeaderLayout.setVisibility(View.GONE);
            addNewTaskLayout.setVisibility(View.VISIBLE);
            publishTimeAndTypeLayout.setVisibility(View.VISIBLE);
        }
    }

    private void initOnlineTaskView() {
        //作业标题
        TextView textView = (TextView) findViewById(R.id.tv_title);
        if (textView != null) {
            textView.setText(homeworkListInfo.getTaskTitle());
        }

        //布置时间
        textView = (TextView) findViewById(R.id.tv_start_time);
        if (textView != null) {
            textView.setText(getString(R.string.assign_date) + "：" + DateUtils.getDateStr(homeworkListInfo
                    .getStartTime(), 0) + "-" + DateUtils.getDateStr(homeworkListInfo.getStartTime(), 1) + "-" +
                    DateUtils.getDateStr(homeworkListInfo.getStartTime(), 2));
        }

        //完成时间
        textView = (TextView) findViewById(R.id.tv_end_time);
        if (textView != null) {
            textView.setText(getString(R.string.finish_date) + "：" + DateUtils.getDateStr(homeworkListInfo
                    .getEndTime(), 0) + "-" + DateUtils.getDateStr(homeworkListInfo.getEndTime(), 1) + "-" +
                    DateUtils.getDateStr(homeworkListInfo.getEndTime(), 2));
        }

        //完成状态仅对老师显示
        finishStudyTaskStatus = (TextView) findViewById(R.id.tv_finish_status);
        if (finishStudyTaskStatus != null) {
            finishStudyTaskStatus.setOnClickListener(this);
            if (roleType == RoleType.ROLE_TYPE_TEACHER && !isOnlineClass) {
                updateFinishStatus();
                finishStudyTaskStatus.setVisibility(View.VISIBLE);
            } else {
                finishStudyTaskStatus.setVisibility(View.INVISIBLE);
            }
        }
    }

    /**
     * 更新完成状态
     */
    private void updateFinishStatus() {
        //完成状态
        if (finishStudyTaskStatus != null) {
            //只有老师才显示完成情况，才去计算。
            if (roleType == RoleType.ROLE_TYPE_TEACHER) {
                int taskNum = 0;
                int taskFinishCount = 0;
                int taskUnFinishCount = 0;
                taskNum = homeworkListInfo.getTaskNum();
                taskFinishCount = homeworkListInfo.getFinishTaskCount();
                taskUnFinishCount = taskNum - taskFinishCount;
                boolean isFinishAll = ((taskNum > 0) && (taskNum == taskFinishCount));
                if (!isFinishAll) {
                    //未完成
                    finishStudyTaskStatus.setText(getString(R.string.finish_count,
                            String.valueOf(taskFinishCount)) + " / " + getString(R.string.unfinish_count,
                            String.valueOf(taskUnFinishCount)));
                } else {
                    //全部完成
                    finishStudyTaskStatus.setText(getString(R.string.n_finish_all, String.valueOf(taskNum)));
                }
                StudyTaskUtils.setTaskFinishBackgroundDetail(getActivity(),finishStudyTaskStatus,
                        taskFinishCount,taskNum);
            }
        }
    }

    private void updateRightView(){
        //更新右上角的是否可以查看
        if (homeworkListInfo != null && roleType == RoleType.ROLE_TYPE_TEACHER){
            if (TextUtils.equals(getMemeberId(),homeworkListInfo.getTaskCreateId())){
                //创建者
                if (isPick || lookStudentTaskFinish || isHistoryClass){

                } else {
                    headRightTextV.setVisibility(View.VISIBLE);
                    if (homeworkListInfo.getViewOthersTaskPermisson() == 1){
                        headRightTextV.setText(getString(R.string.str_set_can_read));
                    } else {
                        headRightTextV.setText(getString(R.string.str_set_cannot_read));
                    }
                }
            }
        }
    }

    private void initAdapterData() {
        listView = (SlideListView) findViewById(R.id.contacts_list_view);
        if (listView != null) {
            listView.setSlideMode(SlideListView.SlideMode.NONE);
            listView.setClipToPadding(false);
            AdapterViewHelper adapterViewHelper = null;
            if (isOnlineSuperTaskDetail || lookStudentTaskFinish || isPick) {
                adapterViewHelper = new AdapterViewHelper(getActivity(),
                        listView, R.layout.item_super_task_list) {
                    @Override
                    public void loadData() {

                    }

                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        if (view != null) {
                            final HomeworkListInfo data = (HomeworkListInfo) getData().get(position);
                            if (data == null) {
                                return view;
                            }
                            ViewHolder holder = (ViewHolder) view.getTag();
                            if (holder == null) {
                                holder = new ViewHolder();
                            }
                            holder.data = data;
                            //开始日期
                            String startTime = data.getStartTime();
                            //星期
                            TextView textView = (TextView) view.findViewById(R.id.tv_start_weekday);
                            if (textView != null) {
                                textView.setText(DateUtils.getWeekDayName(startTime));
                            }
                            //年份+月份 格式：2016/05
                            textView = (TextView) view.findViewById(R.id.tv_start_date);
                            if (textView != null) {
                                textView.setText(DateUtils.getStringToString(startTime, DateUtils.DATE_PATTERN_yyyy_MM_dd));
                            }
                            //结束日期
                            String endTime = data.getEndTime();
                            //星期
                            textView = (TextView) view.findViewById(R.id.tv_end_weekday);
                            if (textView != null) {
                                textView.setText(DateUtils.getWeekDayName(endTime));
                            }
                            ///年份+月份 格式：2016/05
                            textView = (TextView) view.findViewById(R.id.tv_end_date);
                            if (textView != null) {
                                textView.setText(DateUtils.getStringToString(endTime, DateUtils.DATE_PATTERN_yyyy_MM_dd));
                            }
                            //任务数
                            textView = (TextView) view.findViewById(R.id.tv_task_number);
                            if (textView != null) {
                                textView.setText(taskSortNum[position]);
                                data.setParentTaskTitle(taskSortNum[position] + getTaskTitle(data.getType()));
                            }
                            //任务类型
                            textView = (TextView) view.findViewById(R.id.tv_task_title);
                            if (textView != null) {
                                textView.setText(getTaskTitle(data.getType()));
                            }
                            //子任务的数量
                            textView = (TextView) view.findViewById(R.id.tv_task_count);
                            if (textView != null) {
                                int num = data.getThirdTaskCount();
                                if (num == 0) {
                                    textView.setVisibility(View.GONE);
                                } else {
                                    textView.setVisibility(View.VISIBLE);
                                    textView.setText("x" + num);
                                }
                            }
                            //标题
                            textView = (TextView) view.findViewById(R.id.tv_homework_title);
                            if (textView != null) {
                                textView.setText(data.getTaskTitle());
                            }
                            //删除
                            View deleteView = view.findViewById(R.id.layout_delete_homework);
                            if (lookStudentTaskFinish || isPick){
                                if (deleteView != null) {
                                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) deleteView
                                            .getLayoutParams();
                                    layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
                                    layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                                    deleteView.setLayoutParams(layoutParams);
                                    TextView showUnFinishCountView = (TextView) view.findViewById
                                            (R.id.tv_delete_homework);
                                    showUnFinishCountView.setBackground(null);
                                    showUnFinishCountView.setTextColor(ContextCompat.getColor
                                            (getActivity(),R.color.red));
                                    int taskType = data.getType();
                                    if (data.getUnDoneThirdTaskCount() > 0){
                                        showUnFinishCountView.setText(getString(R.string.n_unfinish,
                                                String.valueOf(data.getUnDoneThirdTaskCount())));
                                        deleteView.setVisibility(View.VISIBLE);
                                    } else if (!data.isStudentDoneTask()
                                            && (taskType == StudyTaskType.NEW_WATACH_WAWA_COURSE
                                            || taskType == StudyTaskType.ENGLISH_WRITING)){
                                        showUnFinishCountView.setText(getString(R.string.unfinished));
                                        deleteView.setVisibility(View.VISIBLE);
                                    } else {
                                        deleteView.setVisibility(View.GONE);
                                    }
                                }
                            } else {
                                deleteView.setVisibility(View.GONE);
                            }
                            ImageView finishImage = (ImageView) view.findViewById(R.id.iv_super_finish);
                            if (data.isStudentDoneTask()) {
                                //已完成
                                finishImage.setImageResource(R.drawable.icon_super_parent_task);
                                finishImage.setVisibility(View.VISIBLE);
                            } else if (needShowCommitFlag(data.getType())) {
                                //需提交
                                finishImage.setImageResource(R.drawable.need_to_commit);
                                finishImage.setVisibility(View.VISIBLE);
                            } else {
                                finishImage.setVisibility(View.GONE);
                            }
                            view.setTag(holder);
                        }
                        return view;
                    }

                    @Override
                    public void onItemClick(AdapterView parent, View view, int position, long id) {
                        ViewHolder holder = (ViewHolder) view.getTag();
                        if (holder.data == null) {
                            return;
                        }
                        HomeworkListInfo data = (HomeworkListInfo) holder.data;
                        data.setOnlineReporter(isReporter);
                        data.setOnlineHost(isHost);
                        data.setAirClassId(airClassId);
                        data.setTaskId(String.valueOf(data.getId()));
                        if (data.getType() == StudyTaskType.RETELL_WAWA_COURSE
                                || data.getType() == StudyTaskType.TASK_ORDER
                                || data.getType() == StudyTaskType.WATCH_HOMEWORK
                                || data.getType() == StudyTaskType.SUBMIT_HOMEWORK
                                || data.getType() == StudyTaskType.Q_DUBBING) {
                            data.setIsSuperChildTask(true);
                            data.setTaskType(StudyTaskType.LISTEN_READ_AND_WRITE + "");
                            if (lookStudentTaskFinish) {
                                enterStudentListenReadAndWriteListActivity(data);
                                return;
                            }
                        } else if (data.getType() == StudyTaskType.NEW_WATACH_WAWA_COURSE || data
                                .getType() == StudyTaskType.WATCH_WAWA_COURSE) {
                            data.setIsSuperChildTask(true);
                            data.setStudentIsRead(data.isStudentDoneTask());
                            data.setTaskType(data.getType() + "");
                        } else if (data.getType() == StudyTaskType.ENGLISH_WRITING) {
                            data.setIsSuperChildTask(true);
                            data.setTaskType(data.getType() + "");
                        } else {
                            data.setTaskType(data.getType() + "");
                        }

                        if (roleType != RoleType.ROLE_TYPE_TEACHER && !data.isStudentDoneTask()) {
                            if (data.getType() == StudyTaskType.TOPIC_DISCUSSION) {
                                updateStudentReadState(String.valueOf(data.getId()), String.valueOf(data.getType()));
                            }
                        }
                        if (lookStudentTaskFinish) {
                            enterStudentFinishedHomeworkListActivity(data);
                        } else if (isPick) {
                            enterListenReadAndWriteDetail(data);
                        } else {
                            data.setIsHistoryClass(isHistoryClass);
                            data.setIsOnlineSchoolClass(isOnlineClass);
                            CourseOpenUtils.openStudyTask(getActivity(), data, roleType, isHeadMaster,
                                    getMemeberId(), sortStudentId, childId, userInfo, false);
                        }

                    }
                };
            } else {
                adapterViewHelper = new AdapterViewHelper(getActivity(),
                        listView, R.layout.item_super_task_list) {
                    @Override
                    public void loadData() {

                    }

                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        if (view != null) {
                            final UploadParameter data = (UploadParameter) getData().get(position);
                            if (data == null) {
                                return view;
                            }
                            ViewHolder holder = (ViewHolder) view.getTag();
                            if (holder == null) {
                                holder = new ViewHolder();
                            }
                            holder.data = data;
                            //开始日期
                            String startTime = data.getStartDate();
                            //星期
                            TextView textView = (TextView) view.findViewById(R.id.tv_start_weekday);
                            if (textView != null) {
                                textView.setText(DateUtils.getWeekDayName(startTime));
                            }
                            //年份+月份 格式：2016/05
                            textView = (TextView) view.findViewById(R.id.tv_start_date);
                            if (textView != null) {
                                textView.setText(startTime);
                            }
                            //结束日期
                            String endTime = data.getEndDate();
                            //星期
                            textView = (TextView) view.findViewById(R.id.tv_end_weekday);
                            if (textView != null) {
                                textView.setText(DateUtils.getWeekDayName(endTime));
                            }
                            ///年份+月份 格式：2016/05
                            textView = (TextView) view.findViewById(R.id.tv_end_date);
                            if (textView != null) {
                                textView.setText(endTime);
                            }
                            //任务数
                            textView = (TextView) view.findViewById(R.id.tv_task_number);
                            if (textView != null) {
                                textView.setText(taskSortNum[position]);
                            }
                            //任务类型
                            textView = (TextView) view.findViewById(R.id.tv_task_title);
                            if (textView != null) {
                                textView.setText(getTaskTitle(data.getTaskType()));
                            }
                            //子任务的数量
                            textView = (TextView) view.findViewById(R.id.tv_task_count);
                            if (textView != null) {
                                List<LookResDto> resDtos = data.getLookResDtoList();
                                if (resDtos == null
                                        || resDtos.size() == 0) {
                                    textView.setVisibility(View.GONE);
                                } else {
                                    textView.setVisibility(View.VISIBLE);
                                    textView.setText("x" + resDtos.size());
                                }
                            }
                            //标题
                            textView = (TextView) view.findViewById(R.id.tv_homework_title);
                            if (textView != null) {
                                textView.setText(data.getFileName());
                            }
                            //删除
                            View deleteView = view.findViewById(R.id.layout_delete_homework);
                            if (deleteView != null) {
                                deleteView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        deleteCurrentItem(data);
                                    }
                                });
                            }
                            view.setTag(holder);
                        }
                        return view;
                    }

                    @Override
                    public void onItemClick(AdapterView parent, View view, int position, long id) {
                        ViewHolder holder = (ViewHolder) view.getTag();
                        if (holder.data == null) {
                            return;
                        }
                        UploadParameter data = (UploadParameter) holder.data;
                        resetTempData();
                        data.setTempData(true);
                        ActivityUtils.enterIntroductionCourseActivity(getActivity(), getTaskTitle(data.getTaskType()),
                                data.getTaskType(), null, true, isOnlineClass, classId, schoolId, data);
                    }
                };
            }
            setCurrAdapterViewHelper(listView, adapterViewHelper);
        }
    }

    private boolean needShowCommitFlag(int taskType) {
        boolean needCommitFlag = false;
        if (taskType == StudyTaskType.TASK_ORDER
                || taskType == StudyTaskType.RETELL_WAWA_COURSE
                || taskType == StudyTaskType.SUBMIT_HOMEWORK
                || taskType == StudyTaskType.ENGLISH_WRITING
                || taskType == StudyTaskType.Q_DUBBING) {
            needCommitFlag = true;
        }
        return needCommitFlag;
    }

    private void resetTempData() {
        if (uploadParameters != null && uploadParameters.size() > 0) {
            for (int i = 0, len = uploadParameters.size(); i < len; i++) {
                UploadParameter data = uploadParameters.get(i);
                if (data.isTempData()) {
                    data.setTempData(false);
                }
            }
        }
    }

    private void updateStudentReadState(final String taskId, String taskType) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("TaskId", taskId);
        if (roleType == RoleType.ROLE_TYPE_PARENT && !TextUtils.isEmpty(childId)) {
            params.put("StudentId", childId);
        } else {
            params.put("StudentId", getMemeberId());
        }
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

                        }
                    }
                };
        listener.setShowLoading(false);
        listener.setShowErrorTips(false);
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.UPDATE_STUDENT_IS_READ_URL, params, listener);
    }

    private void enterStudentFinishedHomeworkListActivity(HomeworkListInfo data) {
        if (data == null) {
            return;
        }
        if (data.getType() == StudyTaskType.NEW_WATACH_WAWA_COURSE
                || data.getType() == StudyTaskType.WATCH_WAWA_COURSE
                || data.getType() == StudyTaskType.TOPIC_DISCUSSION) {
            return;
        }

        Intent intent = new Intent(getActivity(), StudentFinishedHomeworkListActivity.class);
        Bundle args = getArguments();
        if (args == null) {
            args = new Bundle();
        }
        args.putString(HomeworkFinishStatusActivity.Constants.TASK_ID, data.getId() + "");
        args.putInt(HomeworkFinishStatusActivity.Constants.TASK_TYPE, data.getType());
        args.putString(HomeworkFinishStatusActivity.Constants.SORT_STUDENT_ID, sortStudentId);
        args.putString(HomeworkFinishStatusActivity.Constants.STUDENT_ID, sortStudentId);
        intent.putExtras(args);
        startActivityForResult(intent, CampusPatrolPickerFragment
                .RESULT_CODE_COMPLETED_HOMEWORK_LIST_FRAGMENT);
    }

    private void enterStudentListenReadAndWriteListActivity(HomeworkListInfo data) {
        if (data == null) {
            return;
        }
        Intent intent = new Intent(getActivity(), HomeworkCommitActivity.class);
        Bundle args = getArguments();
        args.putInt("TaskType", StudyTaskType.LISTEN_READ_AND_WRITE);
        args.putString("TaskId", String.valueOf(data.getTaskId()));
        args.putSerializable(HomeworkListInfo.class.getSimpleName(), data);
        intent.putExtras(args);
        startActivity(intent);
    }

    private String getTaskTitle(int taskType) {
        if (taskType == StudyTaskType.RETELL_WAWA_COURSE) {
            return getString(R.string.retell_course);
        } else if (taskType == StudyTaskType.TASK_ORDER) {
            return getString(R.string.do_task);
        } else if (taskType == StudyTaskType.ENGLISH_WRITING) {
            return getString(R.string.english_writing);
        } else if (taskType == StudyTaskType.TOPIC_DISCUSSION) {
            return getString(R.string.discuss_topic);
        } else if (taskType == StudyTaskType.SUBMIT_HOMEWORK || taskType == StudyTaskType.WATCH_HOMEWORK) {
            return getString(R.string.other);
        } else if (taskType == StudyTaskType.NEW_WATACH_WAWA_COURSE || taskType == StudyTaskType
                .WATCH_WAWA_COURSE) {
            return getString(R.string.look_through_courseware);
        } else if (taskType == StudyTaskType.Q_DUBBING){
            return getString(R.string.str_q_dubbing);
        }
        return "";
    }

    private void deleteCurrentItem(final UploadParameter uploadParameter) {
        ContactsMessageDialog messageDialog = new ContactsMessageDialog(
                getActivity(),
                null,
                getString(R.string.confirm_delete),
                getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                },
                getString(R.string.confirm),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        uploadParameters.remove(uploadParameter);
                        notifyAdapterData();
                    }
                });
        messageDialog.show();
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.contacts_header_left_btn) {
            backPress();
        } else if (viewId == R.id.tv_bottom_confirm) {
            showEditTaskNameDialog();
        } else if (viewId == R.id.rl_add_new_task) {
            addNewTask();
        } else if (viewId == R.id.tv_finish_status) {
            enterHomeworkFinishStatusActivity();
        }
    }

    public void backPress(){
        if (isPick) {
            popStack();
        } else {
            updateParameterDataList();
            finish();
        }
    }

    private void updateParameterDataList(){
        if (isPick){
            return;
        }
        if (isFromMoocIntroTask){
            //同步更新数据
            LqIntroTaskHelper.getInstance().updateUploadParameters(uploadParameters);
            LqIntroTaskHelper.getInstance().setAnswerAtAnyTime(immediatelyRb.isChecked());
            LqIntroTaskHelper.getInstance().setHasReadPermission(hasReadPerRb.isChecked());
        }
    }

    //未完成/已完成
    private void enterHomeworkFinishStatusActivity() {
        homeworkListInfo.setTaskType(String.valueOf(homeworkListInfo.getType()));
        homeworkListInfo.setTaskId(String.valueOf(homeworkListInfo.getId()));
        ActivityUtils.enterHomeworkFinishStatusActivity(getActivity(), homeworkListInfo, roleType, TaskId);
    }

    private void addNewTask() {
        ArrangeLearningTasksUtil.getInstance()
                .setActivity(getActivity())
                .setIsIntroSuperTask(true)
                .setCallBackListener(new ArrangeLearningTasksUtil.ArrangeLearningTaskListener() {
                    @Override
                    public void selectedTypeData(String title, int type) {
                        ActivityUtils.enterIntroductionCourseActivity(getActivity(), title, type,
                                null, true, isOnlineClass, classId, schoolId, null);
                    }
                })
                .show();
    }


    private void showEditTaskNameDialog() {
        if (uploadParameters == null || uploadParameters.size() == 0) {
            TipMsgHelper.ShowMsg(getActivity(), R.string.str_task_content_not_null);
            return;
        }
        UserInfo userInfo = getUserInfo();
        String userName = userInfo.getRealName();
        if (TextUtils.isEmpty(userName)) {
            userName = userInfo.getNickName();
        }
        String fileName = userName + getString(R.string.str_super_homework_total, uploadParameters.size());
        if (!TextUtils.isEmpty(taskFileName)) {
            fileName = taskFileName;
        }
        if (uploadParameters.size() > 1) {
            ContactsInputBoxDialog inputBoxDialog = new ContactsInputBoxDialog(
                    getActivity(),
                    getString(R.string.str_pls_input_super_task_title),
                    fileName,
                    null,
                    getString(R.string.cancel),
                    (dialog, which) -> dialog.dismiss(),
                    getString(R.string.confirm), (dialog, which) -> {
                String title = ((ContactsInputBoxDialog) dialog).getInputText();
                if (TextUtils.isEmpty(title)) {
                    TipMsgHelper.ShowLMsg(getActivity(), R.string.pls_enter_title);
                    return;
                }
                dialog.dismiss();
                taskFileName = title;
                confirmSelectTaskData();
            });
            inputBoxDialog.setIsAutoDismiss(false);
            inputBoxDialog.show();
        } else {
            confirmSelectTaskData();
        }
    }

    private void confirmSelectTaskData() {
        UploadParameter uploadParameter = new UploadParameter();
        UserInfo userInfo = getUserInfo();
        if (userInfo != null) {
            String userName = userInfo.getRealName();
            if (TextUtils.isEmpty(userName)) {
                userName = userInfo.getNickName();
            }
            uploadParameter.setFileName(taskFileName);
            uploadParameter.setMemberId(userInfo.getMemberId());
            uploadParameter.setCreateName(userName);
        }
        uploadParameter.setUploadParameters(uploadParameters);
        uploadParameter.setTaskType(taskType);
        uploadParameter.setStartDate(getIntroductionDate(true));
        uploadParameter.setEndDate(getIntroductionDate(false));
        uploadParameter.setSubmitType(immediatelyRb.isChecked() ? 0 : 1);
        uploadParameter.setViewOtherPermissionType(hasReadPerRb.isChecked() ? 0 : 1);
        if (onlineRes != null) {
            autoDistinguishStudyType(uploadParameter, schoolClassInfos);
        } else {
            enterContactsPicker(uploadParameter);
        }
    }

    private String getIntroductionDate(boolean isStartTime) {
        String startTime = null;
        String endTime = null;
        for (int i = 0, len = uploadParameters.size(); i < len; i++) {
            if (i == 0) {
                startTime = uploadParameters.get(0).getStartDate();
                endTime = uploadParameters.get(0).getEndDate();
            } else if (i > 0) {
                String middleStartTime = uploadParameters.get(i).getStartDate();
                String middleEndTime = uploadParameters.get(i).getEndDate();
                if (DateUtils.compareDate(startTime, middleStartTime) == 1) {
                    startTime = middleStartTime;
                }
                if (DateUtils.compareDate(endTime, middleEndTime) < 1) {
                    endTime = middleEndTime;
                }
            }
        }
        return isStartTime ? startTime : endTime;
    }

    private void enterContactsPicker(UploadParameter uploadParameter) {
        Bundle args = getArguments();
        if (uploadParameter != null) {
            args.putSerializable(UploadParameter.class.getSimpleName(), uploadParameter);
        }

        args.putInt(ContactsPickerActivity.EXTRA_UPLOAD_TYPE, UploadCourseType.STUDY_TASK);

        args.putBoolean(ContactsPickerActivity.EXTRA_USE_EXTENDED_PICKER, true);
        args.putInt(
                ContactsPickerActivity.EXTRA_PICKER_TYPE, ContactsPickerActivity.PICKER_TYPE_GROUP);
        args.putInt(
                ContactsPickerActivity.EXTRA_GROUP_TYPE, ContactsPickerActivity.GROUP_TYPE_CLASS);
        args.putBoolean(ContactsPickerActivity.EXTRA_PUBLISH_RESOURCE, true);
        args.putInt(
                ContactsPickerActivity.EXTRA_MEMBER_TYPE, ContactsPickerActivity.MEMBER_TYPE_STUDENT);
        args.putInt(
                ContactsPickerActivity.EXTRA_PICKER_MODE, ContactsPickerActivity.PICKER_MODE_MULTIPLE);
        args.putString(
                ContactsPickerActivity.EXTRA_PICKER_CONFIRM_BUTTON_TEXT, getString(R.string.send));
        args.putBoolean(ContactsPickerActivity.EXTRA_PICKER_SUPERUSER, true);
        args.putBoolean(ContactsPickerActivity.EXTRA_IS_ONLINE_CLASS, isOnlineClass);
        args.putInt(ContactsPickerActivity.EXTRA_ROLE_TYPE, ContactsPickerActivity
                .ROLE_TYPE_TEACHER);
        Fragment fragment;
        if (args.getBoolean(ContactsPickerActivity.EXTRA_USE_EXTENDED_PICKER)) {
            // TODO: 2017/11/23 选择班级  选择小组
            PickerClassAndGroupActivity.start(getActivity(), args);
            return;
        } else {
            fragment = new ContactsPickerEntryFragment();
        }
        fragment.setArguments(args);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.activity_body, fragment, ContactsPickerEntryFragment.TAG)
                .hide(IntroductionSuperTaskFragment.this);
        ft.addToBackStack(null);
        ft.commit();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == ActivityUtils.REQUEST_CODE_RETURN_REFRESH) {
                UploadParameter uploadParameter = (UploadParameter) data.getSerializableExtra
                        (UploadParameter.class.getSimpleName());
                if (uploadParameter != null) {
                    if (uploadParameters.size() == 0) {
                        uploadParameters.add(uploadParameter);
                    } else {
                        if (uploadParameter.isTempData()) {
                            for (int i = 0; i < uploadParameters.size(); i++) {
                                UploadParameter parameter = uploadParameters.get(i);
                                if (parameter.isTempData()) {
                                    uploadParameter.setTempData(false);
                                    uploadParameters.set(i, uploadParameter);
                                    break;
                                }
                            }
                        } else {
                            uploadParameters.add(uploadParameter);
                        }
                    }
                    notifyAdapterData();
                }
            }
        }
    }

    private void notifyAdapterData() {
        if (isOnlineSuperTaskDetail || isPick || lookStudentTaskFinish) {
            getCurrAdapterViewHelper().update();
            return;
        }
        notifyStartTimeData();
        if (uploadParameters.size() >= 6) {
            addNewTaskLayout.setVisibility(View.GONE);
        } else {
            addNewTaskLayout.setVisibility(View.VISIBLE);
        }
        if (uploadParameters.size() == 0) {
            listView.setVisibility(View.GONE);
        } else {
            listView.setVisibility(View.VISIBLE);
            if (getCurrAdapterViewHelper().getData() == null) {
                getCurrAdapterViewHelper().setData(uploadParameters);
            } else {
                getCurrAdapterViewHelper().update();
            }
        }
    }

    private void notifyStartTimeData(){
        if (uploadParameters == null || uploadParameters.size() == 0){
            //没有数据
            taskStartTimeTextV.setText("");
        } else {
            //有数据
            taskStartTimeTextV.setText(getIntroductionDate(true));
        }
    }

    private void loadOnlineTaskData() {
        if (TextUtils.isEmpty(TaskId)) {
            return;
        }
        Map<String, Object> param = new HashMap<>();
        param.put("TaskId", TaskId);
        if (roleType == RoleType.ROLE_TYPE_TEACHER){
            param.put("IsDistribute",false);
        } else {
            param.put("IsDistribute",true);
        }
        if (isPick) {
            param.put("IsDistribute",true);
            param.put("StudentId", getMemeberId());
        } else if (lookStudentTaskFinish) {
            param.put("StudentId", sortStudentId);
        } else {
            if (roleType == RoleType.ROLE_TYPE_STUDENT || roleType == RoleType.ROLE_TYPE_PARENT) {
                param.put("StudentId", childId);
            }
        }
        DefaultDataListener listener = new DefaultDataListener<DataModelResult>(
                DataModelResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                super.onSuccess(jsonString);
                DataModelResult result = getResult();
                if (result != null && result.isSuccess()) {
                    List<HomeworkListInfo> taskData = JSONObject.parseArray(result.getModel().getData()
                            .toString(), HomeworkListInfo.class);
                    if (taskData != null && taskData.size() > 0) {
                        updateDataView(taskData, false);
                    }
                }
            }
        };
        listener.setShowLoading(true);
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.GET_FIRST_TOGETHER_TASK_DETAIL_BASE_URL, param, listener);
    }

    private void updateDataView(List<HomeworkListInfo> taskData, boolean updateData) {
        if (superTaskList != null) {
            superTaskList.clear();
        }
        int totalCount = 0;
        int finishCount = 0;
        for (HomeworkListInfo info : taskData) {
            if (info.getType() == StudyTaskType.SUPER_TASK) {
                homeworkListInfo = info;
                updateFinishStatus();
                updateRightView();
            } else {
                if (isPick) {
                    if (info.getType() == StudyTaskType.RETELL_WAWA_COURSE
                            || info.getType() == StudyTaskType.TASK_ORDER
                            || info.getType() == StudyTaskType.SUBMIT_HOMEWORK) {
                        superTaskList.add(info);
                    }
                } else {
                    superTaskList.add(info);
                    totalCount++;
                    if (info.isStudentDoneTask()) {
                        finishCount++;
                    }
                }
            }
        }

        if (lookStudentTaskFinish && !TextUtils.isEmpty(studentName)) {
            //显示title中已完成和未完成的数量
            String titleString = studentName + "(" + finishCount + "/" + totalCount + ")";
            taskTitleTextV.setText(studentName);
            showTaskFinishView.setText(getString(R.string.str_look_student_finish_task_detail,
                    totalCount,finishCount));
            showTaskFinishView.setVisibility(View.VISIBLE);
        }

        if (listView != null) {
            listView.setVisibility(View.VISIBLE);
        }
        getCurrAdapterViewHelper().setData(superTaskList);
    }

    private void enterListenReadAndWriteDetail(HomeworkListInfo data) {
        ListenReadAndWriteStudyTaskFragment fragment = new ListenReadAndWriteStudyTaskFragment();
        Bundle args = getArguments();
        args.putBoolean(ActivityUtils.EXTRA_IS_PICK, true);
        if (selectHomeworkInfo != null) {
            selectHomeworkInfo.setIsSuperChildTask(true);
//            selectHomeworkInfo.setTaskId(data.getId() + "");
            args.putString("TaskId",data.getId() + "");
            args.putSerializable(ListenReadAndWriteStudyTaskFragment.Constants.EXTRA_TASK_INFO_DATA, selectHomeworkInfo);
        }
        fragment.setArguments(args);
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.add(R.id.activity_body, fragment, TAG);
        ft.show(fragment);
        ft.hide(this);
        ft.addToBackStack(null);
        ft.commit();
    }

    private void checkClassPlayEnd(){
        if (schoolClassInfos == null || schoolClassInfos.size() < 2){
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < schoolClassInfos.size(); i++){
            ShortSchoolClassInfo classInfo = schoolClassInfos.get(i);
            if (stringBuilder.length() == 0){
                stringBuilder.append(classInfo.getClassId());
            } else {
                stringBuilder.append(",").append(classInfo.getClassId());
            }
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("classIds", stringBuilder.toString());
        RequestHelper.RequestListener listener =
                new RequestHelper.RequestDataResultListener<DataModelResult>(
                        getActivity(), DataModelResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        if (!TextUtils.isEmpty(jsonString)){
                            try {
                                org.json.JSONObject jsonObject = new org.json.JSONObject(jsonString);
                                JSONArray dataArray = jsonObject.getJSONArray("data");
                                if (dataArray != null && dataArray.length() > 0){
                                }
                                JSONArray dataHisArray = jsonObject.getJSONArray("dataHis");
                                if (dataHisArray != null && dataHisArray.length() > 0){
                                    outer:for (int m = 0; m < dataHisArray.length(); m++){
                                        String hisClassId = dataHisArray.get(m).toString();
                                        for (int k = 0; k < schoolClassInfos.size(); k++){
                                            ShortSchoolClassInfo info = schoolClassInfos.get(k);
                                            if (TextUtils.equals(info.getClassId(),hisClassId)){
                                                schoolClassInfos.remove(k);
                                                continue outer;
                                            }
                                        }
                                    }
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }

                    }
                };
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.GET_CHECK_TEACHING_PLAN_BASE_URL, params, listener);
    }

    public void autoDistinguishStudyType(UploadParameter uploadParameter,
                                         List<ShortSchoolClassInfo> schoolClassInfos){
        List<UploadParameter> uploadParameters = uploadParameter.getUploadParameters();
        if (uploadParameters != null && uploadParameters.size() > 0){
            if (uploadParameters.size() == 1){
                //拆分学习任务具体的类型
                UploadParameter parameter = uploadParameters.get(0);
                parameter.setMemberId(uploadParameter.getMemberId());
                parameter.setCreateName(uploadParameter.getCreateName());
                parameter.setSubmitType(uploadParameter.getSubmitType());
                parameter.setViewOtherPermissionType(uploadParameter.getViewOtherPermissionType());
                int taskType = parameter.getTaskType();
                if (taskType == StudyTaskType.WATCH_WAWA_COURSE
                        || taskType == StudyTaskType.NEW_WATACH_WAWA_COURSE){
                    //看课件
                    publishWatchWawaCourseStudyTask(parameter,schoolClassInfos);
                } else {
                    List<LookResDto> dtos = parameter.getLookResDtoList();
                    if (dtos != null && dtos.size() > 1){
                        if (taskType == StudyTaskType.WATCH_HOMEWORK){
                            parameter.setTaskType(StudyTaskType.MULTIPLE_OTHER);
                        } else if (taskType == StudyTaskType.SUBMIT_HOMEWORK){
                            parameter.setTaskType(StudyTaskType.MULTIPLE_OTHER_SUBMIT);
                        }
                    }
                    //其他任务类型
                    publishStudyTask(parameter,parameter.getCourseData(),schoolClassInfos);
                }
            } else {
                publishSuperTask(uploadParameter, schoolClassInfos);
            }
        }
    }


    /**
     * 发送综合任务到班级和小组
     *
     * @param uploadParameter
     * @param schoolClassInfos
     */
    private void publishSuperTask(UploadParameter uploadParameter,
                                  List<ShortSchoolClassInfo> schoolClassInfos) {
        showLoadingDialog();
        org.json.JSONObject taskParams = new org.json.JSONObject();
        boolean isPickerGroup = false;
        if (uploadParameter != null) {
            //是否为选择小组模式
            if (!TextUtils.isEmpty(schoolClassInfos.get(0).getGroupId())) {
                //选择小组模式
                isPickerGroup = true;
            }
            try {
                taskParams.put("TaskCreateId", uploadParameter.getMemberId());
                taskParams.put("TaskCreateName", uploadParameter.getCreateName());
                if (isPickerGroup) {
                    //发送到小组
                    JSONArray groupArray = new JSONArray();
                    org.json.JSONObject groupObject = null;
                    if (schoolClassInfos.size() > 0) {
                        for (int i = 0; i < schoolClassInfos.size(); i++) {
                            groupObject = new org.json.JSONObject();
                            ShortSchoolClassInfo info = schoolClassInfos.get(i);
                            groupObject.put("GroupId", info.getGroupId());
                            groupObject.put("SchoolName", info.getSchoolName());
                            groupObject.put("SchoolId", info.getSchoolId());
                            groupArray.put(groupObject);
                        }
                    }
                    taskParams.put("SchoolStudyGroupList", groupArray);
                } else {
                    //发送到班级
                    JSONArray schoolArray = new JSONArray();
                    org.json.JSONObject schoolObject = null;
                    if (schoolClassInfos.size() > 0) {
                        for (int i = 0; i < schoolClassInfos.size(); i++) {
                            schoolObject = new org.json.JSONObject();
                            ShortSchoolClassInfo info = schoolClassInfos.get(i);
                            schoolObject.put("ClassName", info.getClassName());
                            schoolObject.put("ClassId", info.getClassId());
                            schoolObject.put("SchoolName", info.getSchoolName());
                            schoolObject.put("SchoolId", info.getSchoolId());
                            schoolArray.put(schoolObject);
                        }
                    }
                    taskParams.put("SchoolClassList", schoolArray);
                }
                taskParams.put("TaskTitle", uploadParameter.getFileName());
                taskParams.put("StartTime", uploadParameter.getStartDate());
                taskParams.put("EndTime", uploadParameter.getEndDate());
                //提交时间类型
                taskParams.put("SubmitType",uploadParameter.getSubmitType());
                taskParams.put("ViewOthersTaskPermisson", uploadParameter.getViewOtherPermissionType());
                //空中课堂的布置任务新增字段
                taskParams.put("TaskFlag", currentStudyType);
                taskParams.put("ExtId", onlineRes.getId());
                JSONArray secondTaskList = new JSONArray();
                org.json.JSONObject secondObject = null;
                List<UploadParameter> data = uploadParameter.getUploadParameters();
                for (int i = 0, len = data.size(); i < len; i++) {
                    UploadParameter parameter = data.get(i);
                    secondObject = new org.json.JSONObject();
                    secondObject.put("SecondTaskNum", i);
                    if (parameter.getTaskType() == StudyTaskType.WATCH_WAWA_COURSE) {
                        secondObject.put("TaskType", StudyTaskType.NEW_WATACH_WAWA_COURSE);
                    } else {
                        secondObject.put("TaskType", parameter.getTaskType());
                    }
                    secondObject.put("TaskTitle", parameter.getFileName());
                    secondObject.put("StartTime", parameter.getStartDate());
                    secondObject.put("EndTime", parameter.getEndDate());
                    secondObject.put("DiscussContent", parameter.getDisContent());
                    //打分
                    if (parameter.NeedScore) {
                        secondObject.put("NeedScore", true);
                        secondObject.put("ScoringRule", parameter.ScoringRule);
                    }
                    if (parameter.getTaskType() == StudyTaskType.ENGLISH_WRITING) {
                        secondObject.put("WritingRequire", parameter.getWritingRequire());
                        secondObject.put("MarkFormula", parameter.getMarkFormula());
                        secondObject.put("WordCountMin", parameter.getWordCountMin());
                        secondObject.put("WordCountMax", parameter.getWordCountMax());
                        CourseData englishData = parameter.getCourseData();
                        if (englishData != null) {
                            if (parameter.getType() == ResType.RES_TYPE_IMG){
                                secondObject.put("ResId", englishData.resId);
                            } else {
                                secondObject.put("ResId", englishData.getIdType());
                            }
                            secondObject.put("ResUrl", englishData.resourceurl);
                        }
                    }

                    JSONArray thirdTaskList = new JSONArray();
                    org.json.JSONObject thirdObject = null;
                    List<LookResDto> resDtos = parameter.getLookResDtoList();
                    if (resDtos != null && resDtos.size() > 0) {
                        if (parameter.getTaskType() == StudyTaskType.ENGLISH_WRITING){
                            if (resDtos.get(0).getCourseId() > 0 && resDtos.get(0).getCourseTaskType() > 0){
                                secondObject.put("CourseId",resDtos.get(0).getCourseId());
                                secondObject.put("CourseTaskType",resDtos.get(0).getCourseTaskType());
                                secondObject.put("ResCourseId", resDtos.get(0).getResCourseId());
                            }
                        } else {
                            for (int j = 0; j < resDtos.size(); j++) {
                                thirdObject = new org.json.JSONObject();
                                LookResDto lookDto = resDtos.get(j);
                                thirdObject.put("ResTitle", lookDto.getResTitle() == null ? "" : lookDto.getResTitle());
                                String resUrl = lookDto.getResUrl();
                                String resId = lookDto.getResId();
                                String authorId = lookDto.getAuthor();
                                List<ResourceInfo> splitInfo = lookDto.getSplitInfoList();
                                int taskType = parameter.getTaskType();
                                if ((taskType == StudyTaskType.RETELL_WAWA_COURSE
                                        || taskType == StudyTaskType.TASK_ORDER)
                                        && splitInfo != null && splitInfo.size() > 0) {
                                    resUrl = StudyTaskUtils.getPicResourceData(splitInfo, true,
                                            false, false);
                                    resId = StudyTaskUtils.getPicResourceData(splitInfo, false,
                                            false, true);
                                    authorId = StudyTaskUtils.getPicResourceData(splitInfo, false,
                                            true, false);
                                }
                                thirdObject.put("ResUrl", resUrl);
                                thirdObject.put("ResId", resId);
                                thirdObject.put("Author", authorId == null ? "" : authorId);
                                //学程馆资源的id
                                thirdObject.put("ResCourseId", lookDto.getResCourseId());
                                thirdObject.put("ResPropType", lookDto.getResPropType());
                                thirdObject.put("RepeatCourseCompletionMode", lookDto.getCompletionMode());
                                if (!TextUtils.isEmpty(lookDto.getPoint())) {
                                    thirdObject.put("ScoringRule", StudyTaskUtils.getScoringRule(lookDto.getPoint()));
                                }
                                if (lookDto.getCourseId() > 0 && lookDto.getCourseTaskType() > 0) {
                                    thirdObject.put("CourseId", lookDto.getCourseId());
                                    thirdObject.put("CourseTaskType", lookDto.getCourseTaskType());
                                }
                                thirdTaskList.put(thirdObject);
                            }
                        }
                    }
                    secondObject.put("ThirdTaskList", thirdTaskList);
                    secondTaskList.put(secondObject);
                }
                taskParams.put("SecondTaskList", secondTaskList);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        RequestHelper.RequestDataResultListener listener = new RequestHelper
                .RequestDataResultListener(getActivity(), DataResult.class) {
            @Override
            public void onSuccess(String json) {
                if (getActivity() == null) {
                    return;
                }
                if (TextUtils.isEmpty(json)) return;
                dismissLoadingDialog();
                try {
                    DataResult result = JSON.parseObject(json, DataResult.class);
                    if (result != null && result.isSuccess()) {
                        //布置完成刷新布置任务页面
                        CampusPatrolUtils.setHasStudyTaskAssigned(true);
                        LqIntroTaskHelper.getInstance().clearTaskList();
                        TipMsgHelper.ShowLMsg(getActivity(), R.string.publish_course_ok);
                        EventBus.getDefault().post(new MessageEvent(MessageEventConstantUtils.SEND_HOME_WORK_LIB_SUCCESS));
                        finish();
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

            @Override
            public void onFinish() {
                super.onFinish();
                dismissLoadingDialog();
                CampusPatrolUtils.setHasStudyTaskAssigned(true);
                finish();
            }

            @Override
            public void onError(NetroidError error) {
                super.onError(error);
                dismissLoadingDialog();
            }
        };
        listener.setShowLoading(true);
        RequestHelper.postRequest(getActivity(), ServerUrl.ADD_TOGETHER_TASK_TOAIRCLASS_BASE_URL, taskParams.toString(), listener);
    }

    public static String ACTION_DATA = TAG + "_loadData";
    private LocalBroadcastManager mBroadcastManager;

    private void registResultBroadcast() {
        if (mBroadcastManager == null) {
            mBroadcastManager = LocalBroadcastManager.getInstance(getMyApplication());
            IntentFilter filter = new IntentFilter(ACTION_DATA);
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

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            loadData();
        }
    };

    public static void sendBroadCast(Activity activity) {
        if (activity == null) {
            return;
        }
        Intent intent = new Intent();
        intent.setAction(ACTION_DATA);
        LocalBroadcastManager.getInstance(activity).sendBroadcast(intent);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unRegistResultBroadcast();
    }



    //空中课堂学习任务在当前界面发送
    private void publishStudyTask(final UploadParameter uploadParameter, CourseData courseData,
                                  List<ShortSchoolClassInfo> schoolClassInfos) {
        showLoadingDialog();
        org.json.JSONObject taskParams = new org.json.JSONObject();
        if (uploadParameter != null) {
            try {
                taskParams.put("TaskType", uploadParameter.getTaskType());
                taskParams.put("TaskCreateId", uploadParameter.getMemberId());
                taskParams.put("TaskCreateName", uploadParameter.getCreateName());
                JSONArray schoolArray = new JSONArray();
                org.json.JSONObject schoolObject = null;
                if (schoolClassInfos != null && schoolClassInfos.size() > 0) {
                    for (int i = 0; i < schoolClassInfos.size(); i++) {
                        schoolObject = new org.json.JSONObject();
                        ShortSchoolClassInfo info = schoolClassInfos.get(i);
                        schoolObject.put("ClassName", info.getClassName());
                        schoolObject.put("ClassId", info.getClassId());
                        schoolObject.put("SchoolName", info.getSchoolName());
                        schoolObject.put("SchoolId", info.getSchoolId());
                        schoolArray.put(schoolObject);
                    }
                }
                taskParams.put("SchoolClassList", schoolArray);
                taskParams.put("TaskTitle", uploadParameter.getFileName());
                if (courseData != null) {
                    if ((uploadParameter.getTaskType() == StudyTaskType.RETELL_WAWA_COURSE
                            || uploadParameter.getTaskType() == StudyTaskType.TASK_ORDER
                            || uploadParameter.getTaskType() == StudyTaskType.ENGLISH_WRITING)
                            && uploadParameter.getType() == ResType.RES_TYPE_IMG) {
                        taskParams.put("ResAuthor", courseData.code);
                        taskParams.put("ResId", courseData.resId);
                    } else {
                        taskParams.put("ResId", courseData.getIdType());
                    }
                    taskParams.put("ResUrl", courseData.resourceurl);
                } else {
                    taskParams.put("ResId", "");
                    taskParams.put("ResUrl", "");
                }
                //学程馆资源的id
                if (uploadParameter.getTaskType() == StudyTaskType.RETELL_WAWA_COURSE
                        || uploadParameter.getTaskType() == StudyTaskType.TASK_ORDER
                        || uploadParameter.getTaskType() == StudyTaskType.Q_DUBBING
                        || uploadParameter.getTaskType() == StudyTaskType.ENGLISH_WRITING){
                    taskParams.put("ResCourseId",uploadParameter.getResCourseId());
                }
                if (uploadParameter.getTaskType() == StudyTaskType.TASK_ORDER){
                    taskParams.put("ResPropType",uploadParameter.getResPropType());
                }
                if (uploadParameter.getWorkOrderId() != null) {
                    taskParams.put("WorkOrderId", uploadParameter.getWorkOrderId());
                }
                if (uploadParameter.getWorkOrderUrl() != null) {
                    taskParams.put("WorkOrderUrl", uploadParameter.getWorkOrderUrl());
                }
                taskParams.put("StartTime", uploadParameter.getStartDate());
                taskParams.put("EndTime", uploadParameter.getEndDate());
                //提交时间类型
                taskParams.put("SubmitType",uploadParameter.getSubmitType());
                taskParams.put("ViewOthersTaskPermisson", uploadParameter.getViewOtherPermissionType());
                if (uploadParameter.getTaskType() == StudyTaskType.INTRODUCTION_WAWA_COURSE) {
                    taskParams.put("DiscussContent", uploadParameter.getDisContent());
                } else {
                    taskParams.put("DiscussContent", uploadParameter.getDescription());
                }
                //布置任务之英文写作相关的字段
                //作文要求
                taskParams.put("WritingRequire", uploadParameter.getWritingRequire());
                //打分公式
                taskParams.put("MarkFormula", uploadParameter.getMarkFormula());
                //作业字数最小值
                taskParams.put("WordCountMin", uploadParameter.getWordCountMin());
                //作业字数最大值
                taskParams.put("WordCountMax", uploadParameter.getWordCountMax());

                //打分
                if (uploadParameter.NeedScore) {
                    taskParams.put("NeedScore", true);
                    taskParams.put("ScoringRule", uploadParameter.ScoringRule);
                }
                //空中课堂的布置任务新增字段
                taskParams.put("TaskFlag", currentStudyType);
                taskParams.put("ExtId", onlineRes.getId());

                if (uploadParameter.getCourseId() > 0 && uploadParameter.getCourseTaskType() > 0){
                    taskParams.put("CourseId",uploadParameter.getCourseId());
                    taskParams.put("CourseTaskType",uploadParameter.getCourseTaskType());
                }

                //判断是不是任务单和听说课的多选
                int taskType = uploadParameter.getTaskType();
                if (taskType == StudyTaskType.TASK_ORDER
                        || taskType == StudyTaskType.RETELL_WAWA_COURSE
                        || taskType == StudyTaskType.Q_DUBBING
                        || taskType == StudyTaskType.MULTIPLE_OTHER
                        || taskType == StudyTaskType.MULTIPLE_OTHER_SUBMIT){
                    List<LookResDto> lookResDtos = uploadParameter.getLookResDtoList();
                    if (lookResDtos != null){
                        if (lookResDtos.size() == 1){
                            String point = lookResDtos.get(0).getPoint();
                            if (uploadParameter.NeedScore && !TextUtils.isEmpty(point)) {
                                taskParams.put("ScoringRule", StudyTaskUtils.getScoringRule(point));
                            }
                            //完成方式
                            if (taskType == StudyTaskType.RETELL_WAWA_COURSE) {
                                taskParams.put("RepeatCourseCompletionMode", lookResDtos.get(0).getCompletionMode());
                            } else if (taskType == StudyTaskType.Q_DUBBING) {
                                taskParams.put("ResPropType",lookResDtos.get(0).getResPropType());
                            }
                            if (lookResDtos.get(0).getCourseId() > 0 && lookResDtos.get(0).getCourseTaskType() > 0){
                                taskParams.put("CourseId",lookResDtos.get(0).getCourseId());
                                taskParams.put("CourseTaskType",lookResDtos.get(0).getCourseTaskType());
                            }
                            if ((taskType == StudyTaskType.RETELL_WAWA_COURSE
                                    || taskType == StudyTaskType.TASK_ORDER)
                                    && courseData == null){
                                taskParams.put("ResId", lookResDtos.get(0).getResId());
                                taskParams.put("ResUrl", lookResDtos.get(0).getResUrl());
                            }
                            taskParams.put("ResCourseId", lookResDtos.get(0).getResCourseId());
                            taskParams.put("ResPropType",lookResDtos.get(0).getResPropType());
                        } else if (lookResDtos.size() > 1){
                            if (taskType == StudyTaskType.RETELL_WAWA_COURSE) {
                                taskParams.put("TaskType", StudyTaskType.MULTIPLE_RETELL_COURSE);
                            } else if (taskType == StudyTaskType.Q_DUBBING) {
                                taskParams.put("TaskType", StudyTaskType.MULTIPLE_Q_DUBBING);
                            } else if (taskType == StudyTaskType.TASK_ORDER){
                                taskParams.put("TaskType", StudyTaskType.MULTIPLE_TASK_ORDER);
                            }
                            StudyTaskUtils.addMultipleTaskParams(taskParams, lookResDtos);
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        RequestHelper.RequestDataResultListener listener = new RequestHelper
                .RequestDataResultListener(getActivity(), DataResult.class) {
            @Override
            public void onSuccess(String json) {
                if (getActivity() == null || TextUtils.isEmpty(json)) return;
                try {
                    dismissLoadingDialog();
                    DataResult result = JSON.parseObject(json, DataResult.class);
                    if (result != null && result.isSuccess()) {
                        //布置完成刷新布置任务页面
                        CampusPatrolUtils.setHasStudyTaskAssigned(true);
                        LqIntroTaskHelper.getInstance().clearTaskList();
                        EventBus.getDefault().post(new MessageEvent(MessageEventConstantUtils.SEND_HOME_WORK_LIB_SUCCESS));
                        TipMsgHelper.ShowLMsg(getActivity(), R.string.publish_course_ok);
                        finish();
                    } else {
                        TipMsgHelper.ShowLMsg(getActivity(), R.string.publish_course_error);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                dismissLoadingDialog();
                finish();
            }

            @Override
            public void onError(NetroidError error) {
                super.onError(error);
                CampusPatrolUtils.setHasStudyTaskAssigned(true);
                dismissLoadingDialog();
            }
        };
        RequestHelper.postRequest(getActivity(), ServerUrl.ADD_AIRCLASS_STUDY_TASK_LIST_BASE_URL, taskParams.toString(),
                listener);
    }

    /**
     * 看课件多类型上传
     *
     * @param uploadParameter
     * @param schoolClassInfos
     */
    private void publishWatchWawaCourseStudyTask(UploadParameter uploadParameter,
                                                 List<ShortSchoolClassInfo> schoolClassInfos) {
        showLoadingDialog();
        org.json.JSONObject taskParams = new org.json.JSONObject();
        if (uploadParameter != null) {
            try {
                taskParams.put("TaskCreateId", uploadParameter.getMemberId());
                taskParams.put("TaskCreateName", uploadParameter.getCreateName());
                JSONArray schoolArray = new JSONArray();
                org.json.JSONObject schoolObject = null;
                if (schoolClassInfos != null && schoolClassInfos.size() > 0) {
                    for (int i = 0; i < schoolClassInfos.size(); i++) {
                        schoolObject = new org.json.JSONObject();
                        ShortSchoolClassInfo info = schoolClassInfos.get(i);
                        schoolObject.put("ClassName", info.getClassName());
                        schoolObject.put("ClassId", info.getClassId());
                        schoolObject.put("SchoolName", info.getSchoolName());
                        schoolObject.put("SchoolId", info.getSchoolId());
                        schoolArray.put(schoolObject);
                    }
                }
                taskParams.put("SchoolClassList", schoolArray);

                taskParams.put("TaskTitle", uploadParameter.getFileName());
                taskParams.put("StartTime", uploadParameter.getStartDate());
                taskParams.put("EndTime", uploadParameter.getEndDate());
                //提交时间类型
                taskParams.put("SubmitType",uploadParameter.getSubmitType());
                taskParams.put("DiscussContent", uploadParameter.getDisContent());
                //空中课堂的布置任务新增字段
                taskParams.put("TaskFlag", currentStudyType);
                taskParams.put("ExtId", onlineRes.getId());

                List<LookResDto> lookResDtos = uploadParameter.getLookResDtoList();
                JSONArray lookResArray = new JSONArray();
                org.json.JSONObject lookObject = null;
                if (lookResDtos != null && lookResDtos.size() > 0) {
                    for (int i = 0; i < lookResDtos.size(); i++) {
                        lookObject = new org.json.JSONObject();
                        LookResDto lookDto = lookResDtos.get(i);
                        lookObject.put("Id", lookDto.getId());
                        lookObject.put("TaskId", lookDto.getTaskId());
                        lookObject.put("ResId", lookDto.getResId());
                        lookObject.put("ResUrl", lookDto.getResUrl());
                        lookObject.put("ResTitle", lookDto.getResTitle() == null ? "" : lookDto
                                .getResTitle());
                        lookObject.put("CreateId", lookDto.getCreateId() == null ? "" : lookDto
                                .getCreateId());
                        lookObject.put("CreateName", lookDto.getCreateName() == null ? "" :
                                lookDto.getCreateName());
                        lookObject.put("CreateTime", lookDto.getCreateTime() == null ? "" :
                                lookDto.getCreateTime());
                        lookObject.put("UpdateId", lookDto.getUpdateId() == null ? "" : lookDto
                                .getUpdateName());
                        lookObject.put("UpdateName", lookDto.getCreateName() == null ? "" :
                                lookDto.getCreateName());
                        lookObject.put("Deleted", lookDto.isDeleted());
                        lookObject.put("Author", lookDto.getAuthor() == null ? "" : lookDto.getAuthor());
                        lookObject.put("ResCourseId",lookDto.getResCourseId());
                        if (lookDto.getCourseId() > 0 && lookDto.getCourseTaskType() > 0){
                            lookObject.put("CourseId",lookDto.getCourseId());
                            lookObject.put("CourseTaskType",lookDto.getCourseTaskType());
                        }
                        lookResArray.put(lookObject);
                    }
                }
                taskParams.put("LookResList", lookResArray);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        RequestHelper.RequestDataResultListener listener = new RequestHelper
                .RequestDataResultListener(getActivity(), DataResult.class) {
            @Override
            public void onSuccess(String json) {
                if (getActivity() == null) {
                    return;
                }
                if (TextUtils.isEmpty(json)) return;
                dismissLoadingDialog();
                try {
                    DataResult result = JSON.parseObject(json, DataResult.class);
                    if (result != null && result.isSuccess()) {
                        CampusPatrolUtils.setHasStudyTaskAssigned(true);
                        LqIntroTaskHelper.getInstance().clearTaskList();
                        EventBus.getDefault().post(new MessageEvent(MessageEventConstantUtils.SEND_HOME_WORK_LIB_SUCCESS));
                        TipMsgHelper.ShowLMsg(getActivity(), R.string.publish_course_ok);
                        finish();
                    } else {
                        TipMsgHelper.ShowLMsg(getActivity(), R.string.publish_course_error);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                CampusPatrolUtils.setHasStudyTaskAssigned(true);
                dismissLoadingDialog();
                finish();
            }

            @Override
            public void onError(NetroidError error) {
                super.onError(error);
                dismissLoadingDialog();
            }
        };
        RequestHelper.postRequest(getActivity(),
                ServerUrl.ADD_AIRCLASS_LOOK_STUDY_TASK_BASE_URL, taskParams.toString(), listener);
    }

}


