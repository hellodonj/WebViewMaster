package com.galaxyschool.app.wawaschool.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.galaxyschool.app.wawaschool.HomeworkFinishStatusActivity;
import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.StudentFinishedHomeworkListActivity;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.CallbackListener;
import com.galaxyschool.app.wawaschool.common.CourseOpenUtils;
import com.galaxyschool.app.wawaschool.common.DateUtils;
import com.galaxyschool.app.wawaschool.common.ScreenUtils;
import com.galaxyschool.app.wawaschool.common.ShareUtils;
import com.galaxyschool.app.wawaschool.common.StudyTaskUtils;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.common.UploadUtils;
import com.galaxyschool.app.wawaschool.common.Utils;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.db.dto.LocalCourseDTO;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.galaxyschool.app.wawaschool.helper.CheckLqShopPmnHelper;
import com.galaxyschool.app.wawaschool.pojo.HomeworkListInfo;
import com.galaxyschool.app.wawaschool.pojo.RoleType;
import com.galaxyschool.app.wawaschool.pojo.StudyTaskType;
import com.galaxyschool.app.wawaschool.pojo.UploadParameter;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseData;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseUploadResult;
import com.galaxyschool.app.wawaschool.slide.SlideManagerHornForPhone;
import com.lqwawa.intleducation.factory.event.EventConstant;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.DataResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.lqwawa.lqbaselib.pojo.MessageEvent;
import com.lqwawa.tools.FileZipHelper;
import com.oosic.apps.share.ShareInfo;
import com.oosic.apps.share.SharedResource;
import com.umeng.socialize.media.UMImage;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListenReadAndWriteStudyTaskFragment extends ContactsListFragment {
    public static final String TAG = ListenReadAndWriteStudyTaskFragment.class.getSimpleName();

    public interface Constants {
        String EXTRA_TASK_INFO_DATA = "task_info_data";
    }

    private String LISTEN_DATA_TAG = "listen_data_tag";
    private String READ_AND_WRITE_DATA_TAG = "read_and_write_data_tag";
    private TextView finishStudyTaskStatus;
    private TextView headTitleView;
    private TextView showTaskFinishView;//显示任务完成的状态（已完成/未完成）
    private int roleType = -1;
    private String TaskId;
    private HomeworkListInfo homeworkListInfo;
    private boolean isHeadMaster;
    private String sortStudentId;
    private String childId;
    private UserInfo userInfo;
    private List<HomeworkListInfo> listenData = new ArrayList<>();
    private List<HomeworkListInfo> readAndWriteData = new ArrayList<>();
    //布置的任务完成与未完成的情况
    private boolean lookStudentTaskFinish;
    //学生的姓名
    private String studentName;
    //是否筛选
    private boolean isPick;
    //选中发送的父类任务
    private HomeworkListInfo selectHomeworkInfo;
    private UploadParameter uploadParameter;
    private boolean isScanTask;
    private boolean isReporter;
    private boolean isHost;

    //综合任务的子任务
    private boolean isSuperChildTask;
    private boolean isHistoryClass;
    private boolean isOnlineClass;
    private int taskType;
    private int airClassId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_listen_readandwrite_layout, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadIntentData();
        initView();
        if (lookStudentTaskFinish && !isSuperChildTask) {
            loadStudentFinishData();
        } else {
            loadStudyData(false);
        }
    }

    private void loadIntentData() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            roleType = bundle.getInt("roleType");
            TaskId = bundle.getString("TaskId");
            sortStudentId = bundle.getString("sortStudentId");
            childId = bundle.getString("StudentId");
            taskType = bundle.getInt("TaskType");
            userInfo = (UserInfo) bundle.getSerializable(UserInfo.class.getSimpleName());
            isHeadMaster = bundle.getBoolean(HomeworkMainFragment.Constants.EXTRA_IS_HEAD_MASTER);
            homeworkListInfo = (HomeworkListInfo) bundle.getSerializable(HomeworkListInfo.class.getSimpleName());
            lookStudentTaskFinish = bundle.getBoolean(HomeworkFinishStatusActivity.Constants.EXTRA_STUDENT_FINISH_STUDY_TASK_LIST);
            studentName = bundle.getString("taskTitle");
            if (homeworkListInfo != null) {
                isReporter = homeworkListInfo.isOnlineReporter();
                isHost = homeworkListInfo.isOnlineHost();
                isSuperChildTask = homeworkListInfo.isSuperChildTask();
            }
            isPick = bundle.getBoolean(ActivityUtils.EXTRA_IS_PICK);
            if (isPick) {
                isScanTask = bundle.getBoolean("is_scan_task");
                selectHomeworkInfo = (HomeworkListInfo) bundle.getSerializable(Constants.EXTRA_TASK_INFO_DATA);
                if (selectHomeworkInfo != null) {
//                    TaskId = selectHomeworkInfo.getTaskId();
                    isSuperChildTask = selectHomeworkInfo.isSuperChildTask();
                }
                uploadParameter = (UploadParameter) getArguments().getSerializable
                        (UploadParameter.class.getSimpleName());
            }
            isHistoryClass = getArguments().getBoolean(ActivityUtils.EXTRA_IS_HISTORY_CLASS);
            isOnlineClass = getArguments().getBoolean(ActivityUtils.EXTRA_IS_ONLINE_CLASS);
        }
    }

    private void initView() {
        //标题
        headTitleView = (TextView) findViewById(R.id.contacts_header_title);
        if (headTitleView != null) {
            if (lookStudentTaskFinish) {
                if (!TextUtils.isEmpty(studentName)) {
                    headTitleView.setText(studentName);
                }
            } else if (isSuperChildTask) {
                if (isPick) {
                    headTitleView.setText(R.string.str_super_task);
                } else {
                    headTitleView.setText(homeworkListInfo.getParentTaskTitle());
                }
            } else if (taskType == StudyTaskType.MULTIPLE_RETELL_COURSE) {
                headTitleView.setText(getString(R.string.microcourse));
            } else if (taskType == StudyTaskType.MULTIPLE_TASK_ORDER) {
                headTitleView.setText(getString(R.string.make_task));
            } else if (taskType == StudyTaskType.MULTIPLE_Q_DUBBING) {
                headTitleView.setText(getString(R.string.str_q_dubbing));
            } else {
                headTitleView.setText(getString(R.string.str_listen_read_and_write));
            }
        }
        TextView textView = (TextView) findViewById(R.id.contacts_header_right_text_view);
        if (textView != null) {
            if (isPick) {
                textView.setText(getString(R.string.confirm));
                textView.setVisibility(View.VISIBLE);
                TextView taskTitleTextV = (TextView) findViewById(R.id.iv_task_title);
                if (taskTitleTextV != null && selectHomeworkInfo != null) {
                    taskTitleTextV.setVisibility(View.VISIBLE);
                    taskTitleTextV.setText(selectHomeworkInfo.getTaskTitle());
                }
            } else if (lookStudentTaskFinish
                    || isSuperChildTask
                    || taskType == StudyTaskType.MULTIPLE_TASK_ORDER
                    || taskType == StudyTaskType.MULTIPLE_RETELL_COURSE
                    || taskType == StudyTaskType.MULTIPLE_Q_DUBBING) {
                textView.setVisibility(View.INVISIBLE);
            } else {
                textView.setText(getString(R.string.share));
                textView.setVisibility(View.VISIBLE);
            }
            textView.setOnClickListener(this);
        }
        //学生的完成的状态
        showTaskFinishView = (TextView) findViewById(R.id.tv_student_task_finish_status);
        if (lookStudentTaskFinish || isPick) {
            //隐藏状态栏
            findViewById(R.id.ll_task_detail).setVisibility(View.GONE);
        } else {
            //作业标题
            textView = (TextView) findViewById(R.id.tv_title);
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
                StudyTaskUtils.setTaskFinishBackgroundDetail(getActivity(), finishStudyTaskStatus,
                        taskFinishCount, taskNum);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshData();
    }

    private void refreshData() {
        if (HomeworkCommitFragment.hasCommented()) {
            loadStudyData(true);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.contacts_header_right_text_view) {
            if (isPick) {
                commitStudentHomeWork();
            } else {
                share();
            }
        } else if (v.getId() == R.id.tv_finish_status) {
            enterHomeworkFinishStatusActivity();
        } else if (v.getId() == R.id.contacts_header_left_btn) {
            if (isPick) {
                popStack();
            } else {
                finish();
            }
        }
    }

    /**
     * 加载数据
     */
    private void loadStudyData(final boolean updateData) {
        if (TextUtils.isEmpty(TaskId)) {
            return;
        }
        Map<String, Object> param = new HashMap<>();
        param.put("TaskId", TaskId);
        if (isPick) {
            param.put("StudentId", getMemeberId());
        } else if (lookStudentTaskFinish) {
            param.put("StudentId", sortStudentId);
        } else if (roleType == RoleType.ROLE_TYPE_STUDENT || roleType == RoleType.ROLE_TYPE_PARENT) {
            param.put("StudentId", childId);
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
                        updateDataView(taskData, updateData);
                    }
                }
            }
        };
        listener.setShowLoading(true);
        String url = ServerUrl.GET_LISTEN_READANDWRITE_TASK_DETAIL_BASE_URL;
        if (isSuperChildTask) {
            url = ServerUrl.GET_SECOND_TOGETHER_TASK_DETAIL_BASE_URL;
        }
        RequestHelper.sendPostRequest(getActivity(), url, param, listener);
    }

    private void loadStudentFinishData() {
        if (TextUtils.isEmpty(TaskId)) {
            return;
        }
        Map<String, Object> param = new HashMap<>();
        param.put("TaskId", TaskId);
        param.put("StudentId", sortStudentId);
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
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.GET_LISTEN_READANDWRITE_DETAIL_DATA_BASE_URL, param, listener);
    }

    private void updateDataView(List<HomeworkListInfo> taskData, boolean updateData) {
        if (listenData == null) {
            listenData = new ArrayList<>();
        } else {
            listenData.clear();
        }
        if (readAndWriteData == null) {
            readAndWriteData = new ArrayList<>();
        } else {
            readAndWriteData.clear();
        }
        int finishCount = 0;
        for (HomeworkListInfo info : taskData) {
            if (isPick && info.getResPropType() == 1) {
                //过滤答题卡类型的数据
                continue;
            }
            if (lookStudentTaskFinish && info.isStudentDoneTask()) {
                finishCount++;
            }
            if ((info.getType() == StudyTaskType.LISTEN_READ_AND_WRITE
                    || info.getType() == StudyTaskType.MULTIPLE_RETELL_COURSE
                    || info.getType() == StudyTaskType.MULTIPLE_TASK_ORDER
                    || info.getType() == StudyTaskType.MULTIPLE_Q_DUBBING)
                    && !lookStudentTaskFinish) {
                //听说 + 读写 任务对象
                homeworkListInfo = info;
                updateFinishStatus();
            } else if (info.getType() == StudyTaskType.RETELL_WAWA_COURSE
                    || (isSuperChildTask && (info.getType() == StudyTaskType.WATCH_HOMEWORK
                    || info.getType() == StudyTaskType.SUBMIT_HOMEWORK))
                    || (lookStudentTaskFinish && (info.getType() == StudyTaskType.WATCH_HOMEWORK
                    || info.getType() == StudyTaskType.SUBMIT_HOMEWORK))
                    || info.getType() == StudyTaskType.Q_DUBBING) {
                //听说课
                if (isSuperChildTask) {
                    if (TextUtils.equals(info.getId() + "", TaskId)) {
                        homeworkListInfo = info;
                        updateFinishStatus();
                    } else {
                        listenData.add(info);
                    }
                } else {
                    listenData.add(info);
                }
            } else if (info.getType() == StudyTaskType.TASK_ORDER) {
                //读写单
                if (isSuperChildTask) {
                    if (TextUtils.equals(info.getId() + "", TaskId)) {
                        homeworkListInfo = info;
                        updateFinishStatus();
                    } else {
                        readAndWriteData.add(info);
                    }
                } else {
                    readAndWriteData.add(info);
                }
            }
        }
        studentFinishAllStudyTask();
        if (updateData) {
            //学生提交了任务更新adapter
            if (listenData != null && listenData.size() > 0) {
                AdapterViewHelper listenHelper = getAdapterViewHelper(LISTEN_DATA_TAG);
                if (listenHelper != null) {
                    listenHelper.update();
                }
            }
            if (readAndWriteData != null && readAndWriteData.size() > 0) {
                AdapterViewHelper readAndWriteHelper = getAdapterViewHelper(READ_AND_WRITE_DATA_TAG);
                if (readAndWriteHelper != null) {
                    readAndWriteHelper.update();
                }
            }
            return;
        }

        if (lookStudentTaskFinish && !TextUtils.isEmpty(studentName)) {
            //显示title中已完成和未完成的数量
            String titleString = null;
            int totalCount = taskData.size();
            if (isSuperChildTask) {
                if (finishCount == totalCount && totalCount > 1) {
                    finishCount = finishCount - 1;
                }
                if (totalCount > 1) {
                    totalCount = totalCount - 1;
                }
                titleString = studentName + "(" + finishCount + "/" + totalCount + ")";
            } else {
                titleString = studentName + "(" + finishCount + "/" + taskData.size() + ")";
            }
            headTitleView.setText(studentName);
            //学生完成任务的情况
            showTaskFinishView.setText(getString(R.string.str_look_student_finish_task_detail,
                    totalCount, finishCount));
            showTaskFinishView.setVisibility(View.VISIBLE);
        }

        LinearLayout parentLayout = (LinearLayout) findViewById(R.id.ll_parent_layout);
        //任务要求
        if (homeworkListInfo != null && !lookStudentTaskFinish && !isPick) {
            View requireChildView = LayoutInflater.from(getContext()).inflate(R.layout.item_classify_layout, parentLayout, false);
            TextView introTitleTextV = (TextView) requireChildView.findViewById(R.id.title_name);
            introTitleTextV.setText(R.string.task_requirements);
            GridView gridView = (GridView) requireChildView.findViewById(R.id.common_grid_view);
            if (gridView != null) {
                gridView.setVisibility(View.GONE);
            }
            //简介的内容
            final TextView introContentTextV = (TextView) requireChildView.findViewById(R.id.tv_introduction_content);
            final ImageView arrowIconImage = (ImageView) requireChildView.findViewById(R.id.iv_arrow_icon);
            if (introContentTextV != null) {
                String content = homeworkListInfo.getDiscussContent();
                if (TextUtils.isEmpty(content)) {
                    content = getString(R.string.no_content);
                }
                introContentTextV.setText(content);
                arrowIconImage.setVisibility(View.VISIBLE);
                if (roleType == RoleType.ROLE_TYPE_TEACHER) {
                    arrowIconImage.setImageResource(R.drawable.arrow_gray_down_icon);
                    introContentTextV.setVisibility(View.GONE);
                } else {
                    arrowIconImage.setImageResource(R.drawable.arrow_gray_up_icon);
                    introContentTextV.setVisibility(View.VISIBLE);
                }
            }

            LinearLayout taskRequireLayout = (LinearLayout) requireChildView.findViewById(R.id.item_title);
            if (taskRequireLayout != null) {
                taskRequireLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (introContentTextV.getVisibility() == View.VISIBLE) {
                            arrowIconImage.setImageResource(R.drawable.arrow_gray_down_icon);
                            introContentTextV.setVisibility(View.GONE);
                        } else {
                            arrowIconImage.setImageResource(R.drawable.arrow_gray_up_icon);
                            introContentTextV.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
            parentLayout.addView(requireChildView);
        }

        if (listenData != null && listenData.size() > 0) {
            if (!lookStudentTaskFinish && !isPick) {
                View tenDistanceDp = LayoutInflater.from(getContext()).inflate(R.layout
                        .include_10_dp_horizontal_line_layout, parentLayout, false);
                parentLayout.addView(tenDistanceDp);
            }
            //听说课
            View listenChildView = LayoutInflater.from(getContext()).inflate(R.layout
                    .item_classify_layout, parentLayout, false);
            TextView listenTitleTextV = (TextView) listenChildView.findViewById(R.id.title_name);
            if (listenTitleTextV != null) {
                if (isSuperOtherHomeWork()) {
                    listenTitleTextV.setText(getString(R.string.other));
                } else if (isQDubbingTask()) {
                    listenTitleTextV.setText(getString(R.string.str_q_dubbing));
                } else {
                    listenTitleTextV.setText(getString(R.string.microcourse));
                }
            }
            GridView listenGridView = (GridView) listenChildView.findViewById(R.id.common_grid_view);
            final int five_padding = getResources().getDimensionPixelSize(R.dimen.min_padding);
            final int ten_padding = getResources().getDimensionPixelSize(R.dimen.resource_gridview_padding);
            listenGridView.setNumColumns(2);
            listenGridView.setBackgroundColor(Color.WHITE);
            listenGridView.setVerticalSpacing(getResources().getDimensionPixelSize(R.dimen.gridview_spacing));
            listenGridView.setHorizontalSpacing(getResources().getDimensionPixelSize(R.dimen.gridview_spacing));
            listenGridView.setPadding(five_padding, ten_padding, five_padding, ten_padding);
            AdapterViewHelper listenGridViewHelper = new AdapterViewHelper(getActivity(),
                    listenGridView, R.layout.lq_course_grid_item) {
                @Override
                public void loadData() {
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    int min_padding = getResources().getDimensionPixelSize(R.dimen.min_padding);
                    int itemSize = (ScreenUtils.getScreenWidth(getActivity()) - min_padding * 3) / 2;
                    HomeworkListInfo data = (HomeworkListInfo) getDataAdapter().getItem(position);
                    if (data == null) {
                        return view;
                    }
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) {
                        holder = new ViewHolder();
                    }
                    holder.data = data;
                    FrameLayout frameLayout = (FrameLayout) view.findViewById(
                            R.id.resource_frameLayout);
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) frameLayout.getLayoutParams();
                    params.width = itemSize;
                    params.height = params.width * 9 / 16;
                    frameLayout.setLayoutParams(params);
                    //缩略图
                    ImageView thumbnail = (ImageView) view.findViewById(R.id.media_thumbnail);
                    if (thumbnail != null) {
                        String thumbnailUrl = data.getResThumbnailUrl();
                        if (!TextUtils.isEmpty(thumbnailUrl)) {
                            if (thumbnailUrl.contains(",")) {
                                thumbnailUrl = thumbnailUrl.split(",")[0];
                            }
                            thumbnailUrl = StudyTaskUtils.getResourceThumbnail(thumbnailUrl);
                        }
                        MyApplication.getThumbnailManager(getActivity()).displayImageWithDefault
                                (thumbnailUrl, thumbnail, R.drawable.default_cover);
                    }
                    //名字
                    TextView name = (TextView) view.findViewById(R.id.media_name);
                    if (name != null) {
                        name.setGravity(Gravity.CENTER);
                        name.setSingleLine(false);
                        name.setPadding(5, 5, 5, 5);
                        name.setTextSize(12);
                        name.setLines(2);
                        String taskTitle = data.getTaskTitle();
                        if (!TextUtils.isEmpty(taskTitle)) {
                            if (taskTitle.contains(",")) {
                                taskTitle = taskTitle.split(",")[0];
                            }
                        }
                        name.setText(taskTitle);
                    }
                    //选择的圆圈
                    ImageView selectIcon = (ImageView) view.findViewById(R.id.media_flag);
                    if (selectIcon != null) {
                        selectIcon.setVisibility(View.GONE);
                    }

                    //学生作业已经完成的图
                    if (lookStudentTaskFinish
                            || roleType == RoleType.ROLE_TYPE_STUDENT
                            || roleType == RoleType.ROLE_TYPE_PARENT
                            || isPick) {
                        ImageView finishTaskTag = (ImageView) view.findViewById(R.id.iv_finish_status);
                        if (finishTaskTag != null) {
                            finishTaskTag.setVisibility(data.isStudentDoneTask() ? View.VISIBLE : View.GONE);
                        }
                    }
                    if (isPick) {
                        ImageView selectImageView = (ImageView) view.findViewById(R.id.media_flag);
                        if (selectImageView != null) {
                            selectImageView.setVisibility(View.VISIBLE);
                            selectImageView.setImageResource(data.isSelect() ? R.drawable.select
                                    : R.drawable.unselect);
                        }
                    }
                    view.setTag(holder);
                    return view;
                }

                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null || holder.data == null) {
                        return;
                    }
                    HomeworkListInfo data = (HomeworkListInfo) holder.data;
                    data.setTaskId(String.valueOf(data.getId()));
                    data.setTaskType(String.valueOf(data.getType()));
                    data.setIsOnlineSchoolClass(isOnlineClass);
                    if (lookStudentTaskFinish) {
                        enterStudentTaskListDetail(data);
                    } else if (isPick) {
                        data.setIsSelect(!data.isSelect());
                        checkAllData(position, true);
                    } else {
                        enterStudyTaskDetail(data);
                    }
                }
            };
            addAdapterViewHelper(LISTEN_DATA_TAG, listenGridViewHelper);
            getAdapterViewHelper(LISTEN_DATA_TAG).setData(listenData);
            parentLayout.addView(listenChildView);
        }

        //读写单
        if (readAndWriteData != null && readAndWriteData.size() > 0) {
            View tenDistanceDp = LayoutInflater.from(getContext()).inflate(R.layout
                    .include_10_dp_horizontal_line_layout, parentLayout, false);
            if (lookStudentTaskFinish && isSuperTaskOrder()) {

            } else {
                parentLayout.addView(tenDistanceDp);
            }
            //听说课
            View readAndWriteChildView = LayoutInflater.from(getContext()).inflate(R.layout
                    .item_classify_layout, parentLayout, false);
            TextView listenTitleTextV = (TextView) readAndWriteChildView.findViewById(R.id.title_name);
            if (listenTitleTextV != null) {
                listenTitleTextV.setText(getString(R.string.make_task));
            }
            GridView listenGridView = (GridView) readAndWriteChildView.findViewById(R.id.common_grid_view);
            final int five_padding = getResources().getDimensionPixelSize(R.dimen.min_padding);
            final int ten_padding = getResources().getDimensionPixelSize(R.dimen.resource_gridview_padding);
            listenGridView.setNumColumns(2);
            listenGridView.setBackgroundColor(Color.WHITE);
            listenGridView.setVerticalSpacing(getResources().getDimensionPixelSize(R.dimen.gridview_spacing));
            listenGridView.setHorizontalSpacing(getResources().getDimensionPixelSize(R.dimen.gridview_spacing));
            listenGridView.setPadding(five_padding, ten_padding, five_padding, ten_padding);
            AdapterViewHelper listenGridViewHelper = new AdapterViewHelper(getActivity(),
                    listenGridView, R.layout.lq_course_grid_item) {
                @Override
                public void loadData() {
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    int min_padding = getResources().getDimensionPixelSize(R.dimen.min_padding);
                    int itemSize = (ScreenUtils.getScreenWidth(getActivity()) - min_padding * 3) / 2;
                    HomeworkListInfo data = (HomeworkListInfo) getDataAdapter().getItem(position);
                    if (data == null) {
                        return view;
                    }
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) {
                        holder = new ViewHolder();
                    }
                    holder.data = data;
                    FrameLayout frameLayout = (FrameLayout) view.findViewById(
                            R.id.resource_frameLayout);
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) frameLayout.getLayoutParams();
                    params.width = itemSize;
                    params.height = params.width * 9 / 16;
                    frameLayout.setLayoutParams(params);
                    //缩略图
                    ImageView thumbnail = (ImageView) view.findViewById(R.id.media_thumbnail);
                    if (thumbnail != null) {
                        String thumbnailUrl = data.getResThumbnailUrl();
                        if (!TextUtils.isEmpty(thumbnailUrl)) {
                            if (thumbnailUrl.contains(",")) {
                                thumbnailUrl = thumbnailUrl.split(",")[0];
                            }
                            thumbnailUrl = StudyTaskUtils.getResourceThumbnail(thumbnailUrl);
                        }
                        MyApplication.getThumbnailManager(getActivity()).displayImageWithDefault
                                (thumbnailUrl, thumbnail, R.drawable.default_cover);
                    }
                    //名字
                    TextView name = (TextView) view.findViewById(R.id.media_name);
                    if (name != null) {
                        name.setGravity(Gravity.CENTER);
                        name.setSingleLine(false);
                        name.setPadding(5, 5, 5, 5);
                        name.setTextSize(12);
                        name.setLines(2);
                        String taskTitle = data.getTaskTitle();
                        if (!TextUtils.isEmpty(taskTitle)) {
                            if (taskTitle.contains(",")) {
                                taskTitle = taskTitle.split(",")[0];
                            }
                        }
                        name.setText(taskTitle);
                    }
                    //选择的圆圈
                    ImageView selectIcon = (ImageView) view.findViewById(R.id.media_flag);
                    if (selectIcon != null) {
                        selectIcon.setVisibility(View.GONE);
                    }

                    //学生作业已经完成的图
                    if (lookStudentTaskFinish
                            || roleType == RoleType.ROLE_TYPE_STUDENT
                            || roleType == RoleType.ROLE_TYPE_PARENT
                            || isPick) {
                        ImageView finishTaskTag = (ImageView) view.findViewById(R.id.iv_finish_status);
                        if (finishTaskTag != null) {
                            finishTaskTag.setVisibility(data.isStudentDoneTask() ? View.VISIBLE : View.GONE);
                        }
                    }

                    if (isPick) {
                        ImageView selectImageView = (ImageView) view.findViewById(R.id.media_flag);
                        if (selectImageView != null) {
                            selectImageView.setVisibility(View.VISIBLE);
                            selectImageView.setImageResource(data.isSelect() ? R.drawable.select
                                    : R.drawable.unselect);
                        }
                    }

                    view.setTag(holder);
                    return view;
                }

                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null || holder.data == null) {
                        return;
                    }
                    HomeworkListInfo data = (HomeworkListInfo) holder.data;
                    data.setTaskId(String.valueOf(data.getId()));
                    data.setTaskType(String.valueOf(data.getType()));
                    data.setIsOnlineSchoolClass(isOnlineClass);
                    if (lookStudentTaskFinish) {
                        enterStudentTaskListDetail(data);
                    } else if (isPick) {
                        data.setIsSelect(!data.isSelect());
                        checkAllData(position, false);
                    } else {
                        enterStudyTaskDetail(data);
                    }
                }
            };
            addAdapterViewHelper(READ_AND_WRITE_DATA_TAG, listenGridViewHelper);
            getAdapterViewHelper(READ_AND_WRITE_DATA_TAG).setData(readAndWriteData);
            parentLayout.addView(readAndWriteChildView);
        }
    }

    private void checkAllData(int position, boolean isListen) {
        AdapterViewHelper listenHelp = getAdapterViewHelper(LISTEN_DATA_TAG);
        if (listenHelp != null) {
            List<HomeworkListInfo> ListenData = listenHelp.getData();
            if (ListenData != null) {
                for (int i = 0, len = ListenData.size(); i < len; i++) {
                    HomeworkListInfo info = ListenData.get(i);
                    if (isListen) {
                        if (i != position) {
                            info.setIsSelect(false);
                        }
                    } else {
                        info.setIsSelect(false);
                    }
                }
                listenHelp.update();
            }
        }
        AdapterViewHelper readHelp = getAdapterViewHelper(READ_AND_WRITE_DATA_TAG);
        if (readHelp != null) {
            List<HomeworkListInfo> readData = readHelp.getData();
            if (readData != null) {
                for (int i = 0, len = readData.size(); i < len; i++) {
                    HomeworkListInfo info = readData.get(i);
                    if (isListen) {
                        info.setIsSelect(false);
                    } else {
                        if (i != position) {
                            info.setIsSelect(false);
                        }
                    }
                }
                readHelp.update();
            }
        }
    }

    private void enterStudyTaskDetail(HomeworkListInfo data) {
        data.setOnlineReporter(isReporter);
        data.setOnlineHost(isHost);
        data.setStudyTaskType(StudyTaskType.LISTEN_READ_AND_WRITE);
        data.setIsSuperChildTask(isSuperChildTask);
        data.setIsHistoryClass(isHistoryClass);
        data.setAirClassId(airClassId);
        CourseOpenUtils.openStudyTask(getActivity(), data, roleType, isHeadMaster,
                getMemeberId(), sortStudentId, childId, userInfo, false);
        if (roleType == RoleType.ROLE_TYPE_STUDENT
                && !data.isStudentDoneTask()
                && TextUtils.equals(data.getTaskType(), String.valueOf(StudyTaskType.WATCH_HOMEWORK))) {
            updateReadState(data.getTaskId());
        }
    }

    private void enterStudentTaskListDetail(HomeworkListInfo data) {
        if (data.getType() == StudyTaskType.WATCH_HOMEWORK) {
            //看作业
            return;
        }
        Bundle args = getArguments();
        if (args != null) {
            //兼容答题卡
            args.putString(HomeworkFinishStatusActivity.Constants.EXTRA_IS_TASK_COURSE_RESID, data.getResId());
        }
        ActivityUtils.enterStudentTaskListDetail(getActivity(),
                data,
                args,
                studentName,
                sortStudentId);
    }

    //未完成/已完成
    private void enterHomeworkFinishStatusActivity() {
        homeworkListInfo.setTaskType(String.valueOf(homeworkListInfo.getType()));
        homeworkListInfo.setTaskId(String.valueOf(homeworkListInfo.getId()));
        homeworkListInfo.setIsSuperChildTask(isSuperChildTask);
        ActivityUtils.enterHomeworkFinishStatusActivity(getActivity(), homeworkListInfo, roleType, TaskId);
    }

    private boolean isSuperOtherHomeWork() {
        if (listenData != null && listenData.size() > 0) {
            int type = listenData.get(0).getType();
            if (type == StudyTaskType.WATCH_HOMEWORK || type == StudyTaskType.SUBMIT_HOMEWORK) {
                return true;
            }
        }
        return false;
    }

    private boolean isSuperTaskOrder() {
        if ((listenData == null || listenData.size() == 0) && readAndWriteData != null &&
                readAndWriteData.size() > 0) {
            return true;
        }
        return false;
    }

    private boolean isQDubbingTask(){
        if (listenData != null && listenData.size() > 0) {
            int type = listenData.get(0).getType();
            if (type == StudyTaskType.Q_DUBBING || type == StudyTaskType.MULTIPLE_Q_DUBBING) {
                return true;
            }
        }
        return false;
    }

    //分享
    private void share() {
        if (homeworkListInfo == null) {
            return;
        }
        String url = homeworkListInfo.getShareUrl();//分享地址
        if (TextUtils.isEmpty(url)) {
            return;
        }
        if (TextUtils.isEmpty(homeworkListInfo.getTaskTitle())) {
            return;
        }
        ShareInfo shareInfo = new ShareInfo();
        shareInfo.setTitle(homeworkListInfo.getTaskTitle());
        shareInfo.setTargetUrl(url);
        UMImage umImage = new UMImage(getActivity(), R.drawable.ic_launcher);
        shareInfo.setuMediaObject(umImage);
        SharedResource resource = new SharedResource();
        resource.setShareUrl(url);
        resource.setTitle(homeworkListInfo.getTaskTitle());
        resource.setThumbnailUrl("");
        resource.setType(SharedResource.RESOURCE_TYPE_HTML);
        shareInfo.setSharedResource(resource);
        ShareUtils shareUtils = new ShareUtils(getActivity());
        shareUtils.share(getView(), shareInfo);
    }

    private HomeworkListInfo getSelectData() {
        AdapterViewHelper listenHelp = getAdapterViewHelper(LISTEN_DATA_TAG);
        if (listenHelp != null) {
            List<HomeworkListInfo> ListenData = listenHelp.getData();
            if (ListenData != null) {
                for (int i = 0, len = ListenData.size(); i < len; i++) {
                    HomeworkListInfo info = ListenData.get(i);
                    if (info.isSelect()) {
                        return info;
                    }
                }
            }
        }

        AdapterViewHelper readAndWriteHelp = getAdapterViewHelper(READ_AND_WRITE_DATA_TAG);
        if (readAndWriteHelp != null) {
            List<HomeworkListInfo> readData = readAndWriteHelp.getData();
            if (readData != null) {
                for (int i = 0, len = readData.size(); i < len; i++) {
                    HomeworkListInfo info = readData.get(i);
                    if (info.isSelect()) {
                        return info;
                    }
                }
            }
        }
        return null;
    }

    private void commitStudentHomeWork() {
        final HomeworkListInfo selectData = getSelectData();
        if (selectData == null) {
            TipsHelper.showToast(getActivity(), R.string.pls_select_homework);
            return;
        }
        if (selectData.getResCourseId() > 0) {
            new CheckLqShopPmnHelper().
                    setActivity(getActivity()).
                    setMemberId(getMemeberId()).
                    setFromType(CheckLqShopPmnHelper.FromType.FROM_LQBLOARD_SEND).
                    setRoleType(RoleType.ROLE_TYPE_STUDENT).
                    setCallBackListener(new CallbackListener() {
                        @Override
                        public void onBack(Object result) {
                            commitSelectStudyTask(selectData);
                        }
                    }).
                    setResCourseId(selectData.getResCourseId()).
                    setClassId(selectData.getClassId()).
                    setSchoolId(selectData.getSchoolId()).
                    check();
        } else {
            commitSelectStudyTask(selectData);
        }
    }

    private void commitSelectStudyTask(final HomeworkListInfo selectData) {
        if (uploadParameter != null) {
            uploadParameter.setIsNeedSplit(false);
            final LocalCourseDTO data = uploadParameter.getLocalCourseDTO();
            if (data != null) {
                //本地课件
                showLoadingDialog(getString(R.string.upload_and_wait), false);
                FileZipHelper.ZipUnzipParam param = new FileZipHelper.ZipUnzipParam(
                        data.getmPath(), Utils.TEMP_FOLDER + Utils.getFileNameFromPath(data.getmPath())
                        + Utils.COURSE_SUFFIX);
                FileZipHelper.zip(param, new FileZipHelper.ZipUnzipFileListener() {
                    @Override
                    public void onFinish(
                            FileZipHelper.ZipUnzipResult result) {
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
                                                        TipMsgHelper.ShowLMsg(getActivity(), R.string
                                                                .upload_file_failed);
                                                        return;
                                                    }
                                                    if (uploadResult.data != null && uploadResult.data.size() > 0) {
                                                        final CourseData courseData = uploadResult.data.get(0);
                                                        if (courseData != null) {
                                                            commitStudentHomework(selectData.getTaskId(), getUserInfo(), courseData);
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
            } else {
                //在线课件
                commitStudentHomework(selectData.getTaskId(), getUserInfo(), uploadParameter.getCourseData());
            }
        }
    }

    private void commitStudentHomework(String taskId, UserInfo userInfo, CourseData courseData) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("TaskId", taskId);
        params.put("StudentId", userInfo.getMemberId());
        if (courseData != null) {
            params.put("StudentResId", courseData.getIdType());
            params.put("StudentResUrl", courseData.resourceurl);
            params.put("StudentResTitle", courseData.nickname);
        }
        RequestHelper.RequestModelResultListener listener = new
                RequestHelper.RequestModelResultListener(getActivity(), DataResult.class) {
                    @Override
                    public void onSuccess(String json) {
                        try {
                            if (getActivity() == null) {
                                return;
                            }
                            DataResult result = JSON.parseObject(json, DataResult.class);
                            if (result != null && result.isSuccess()) {
                                if (isScanTask) {
                                    TipMsgHelper.ShowLMsg(getActivity(), R.string.scan_task_send_ok);
                                    //发送成功后让学习任务页面刷新数据
                                    HomeworkMainFragment.isScanTaskFinished = true;
                                } else {
                                    //课件发送到学习任务列表后，学习任务主页面更新数据
                                    HomeworkMainFragment.hasPublishedCourseToStudyTask = true;
                                    TipMsgHelper.ShowLMsg(getActivity(), R.string.publish_course_ok);
                                }
                                EventBus.getDefault().post(new MessageEvent(EventConstant.TRIGGER_UPDATE_COURSE));
                                if (getActivity() != null) {
                                    if (uploadParameter.isTempData()) {
                                        Intent intent = new Intent();
                                        intent.putExtra(SlideManagerHornForPhone.SAVE_PATH,
                                                uploadParameter.getFilePath());
                                        getActivity().setResult(Activity.RESULT_OK, intent);
                                        getActivity().finish();
                                    } else {
                                        getActivity().finish();
                                    }
                                }
                            } else {
                                TipMsgHelper.ShowLMsg(getActivity(), R.string.publish_course_error);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.PUBLISH_STUDENT_HOMEWORK_URL,
                params, listener);
    }


    private void studentFinishAllStudyTask() {
        if (lookStudentTaskFinish || roleType == RoleType.ROLE_TYPE_TEACHER) {
            return;
        }
        boolean isFinishAll = true;
        if (listenData != null && listenData.size() > 0) {
            for (HomeworkListInfo info : listenData) {
                if (!info.isStudentDoneTask()) {
                    isFinishAll = false;
                    break;
                }
            }
        }
        if (readAndWriteData != null && readAndWriteData.size() > 0 && isFinishAll) {
            for (HomeworkListInfo info : readAndWriteData) {
                if (!info.isStudentDoneTask()) {
                    isFinishAll = false;
                    break;
                }
            }
        }
        if (isFinishAll) {
            HomeworkMainFragment.hasPublishedCourseToStudyTask = true;
        }
    }

    private void updateReadState(final String taskId) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("TaskId", taskId);
        params.put("StudentId", getMemeberId());
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
                        } else {
                            HomeworkCommitFragment.setHasCommented(true);
                        }
                    }
                };
        listener.setShowLoading(false);
        listener.setShowErrorTips(false);
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.STUDENT_COMMIT_HOMEWORK_URL,
                params, listener);
    }
}
