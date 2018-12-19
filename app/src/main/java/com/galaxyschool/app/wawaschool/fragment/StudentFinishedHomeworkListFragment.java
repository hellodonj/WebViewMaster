package com.galaxyschool.app.wawaschool.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.Request;
import com.galaxyschool.app.wawaschool.AnswerCardDetailActivity;
import com.galaxyschool.app.wawaschool.EnglishWritingCommentDetailsActivity;
import com.galaxyschool.app.wawaschool.EnglishWritingCommentRecordActivity;
import com.galaxyschool.app.wawaschool.HomeworkFinishStatusActivity;
import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.SpeechAssessmentActivity;
import com.galaxyschool.app.wawaschool.TeacherReviewDetailActivity;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.CourseOpenUtils;
import com.galaxyschool.app.wawaschool.common.DensityUtils;
import com.galaxyschool.app.wawaschool.common.WawaCourseUtils;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.galaxyschool.app.wawaschool.fragment.resource.HomeworkCommitResourceAdapterViewHelper;
import com.galaxyschool.app.wawaschool.helper.DoTaskOrderHelper;
import com.galaxyschool.app.wawaschool.pojo.ExerciseAnswerCardParam;
import com.galaxyschool.app.wawaschool.pojo.ResType;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseData;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseUploadResult;
import com.lqwawa.lqbaselib.net.ThisStringRequest;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.CommitTask;
import com.galaxyschool.app.wawaschool.pojo.HomeworkCommitObjectInfo;
import com.galaxyschool.app.wawaschool.pojo.HomeworkCommitObjectResult;
import com.galaxyschool.app.wawaschool.pojo.RoleType;
import com.galaxyschool.app.wawaschool.pojo.StudyTask;
import com.galaxyschool.app.wawaschool.pojo.StudyTaskType;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 学生已完成作业提交列表
 */

public class StudentFinishedHomeworkListFragment extends ContactsListFragment {

    public static final String TAG = StudentFinishedHomeworkListFragment.class.getSimpleName();

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
    //判断是否来自校园巡查的标识
    private boolean isCampusPatrolTag;
    private boolean isOnlineReporter;//是不是来自直播的小编或者主持人
    private int taskCourseOrientation;
    private boolean isHistoryClass;
    private String courseResId;//任务课件的resId
    protected ExerciseAnswerCardParam answerCardParam;
    private boolean isAnswerTaskOrderQuestion;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.layout_fragment_student_finished_homework_list, null);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
        refreshData();
        checkTaskOrderAnswerQuestionCard();
    }

    public void refreshData() {
        loadViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        isPlaying = false;
    }

    void initViews() {
        if (getArguments() != null) {
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
            isCampusPatrolTag = getArguments().getBoolean(CampusPatrolMainFragment
                    .IS_CAMPUS_PATROL_TAG, false);
            isOnlineReporter = getArguments().getBoolean(HomeworkFinishStatusActivity.Constants.EXTRA_ISONLINEREPORTER);
            isHistoryClass = getArguments().getBoolean(ActivityUtils.EXTRA_IS_HISTORY_CLASS,false);
            courseResId = getArguments().getString(HomeworkFinishStatusActivity.Constants
                    .EXTRA_IS_TASK_COURSE_RESID);
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

        //下拉刷新
        PullToRefreshView pullToRefreshView = (PullToRefreshView) findViewById(
                R.id.contacts_pull_to_refresh);
        pullToRefreshView.setRefreshEnable(true);
        setPullToRefreshView(pullToRefreshView);
        listView = (ListView) findViewById(R.id.contacts_list_view);
        if (listView != null) {
            //作业通用列表
            AdapterViewHelper listViewHelper = new HomeworkCommitResourceAdapterViewHelper(
                    getActivity(), listView, roleType, getMemeberId()) {
                @Override
                public void loadData() {
                    loadCommonData();
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    final CommitTask data = (CommitTask) getDataAdapter().getItem(position);
                    if (data == null) {
                        return view;
                    }
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) {
                        holder = new ViewHolder();
                    }
                    holder.data = data;

                    //标题
                    TextView textView = (TextView) view.findViewById(R.id.tv_title);
                    if (textView != null) {
                        if (taskType == StudyTaskType.ENGLISH_WRITING) {
                            if (task != null) {
                                //显示作文内容
                                textView.setText(data.getWritingContent());
                            }
                            //现在两行，多余打点显示。
                            textView.setLines(2);
                            textView.setEllipsize(TextUtils.TruncateAt.END);
                        } else {
                            if (isAnswerTaskOrderQuestion && task != null){
                                textView.setText(task.getTaskTitle());
                            } else {
                                textView.setText(data.getStudentResTitle());
                            }
                        }
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
                            if (isAnswerTaskOrderQuestion){
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

                    View courseDetails = view.findViewById(R.id.tv_access_details);
                    if (taskType == StudyTaskType.ENGLISH_WRITING || isAnswerTaskOrderQuestion) {
                        courseDetails.setVisibility(View.GONE);
                    } else {
                        courseDetails.setVisibility(View.VISIBLE);
                    }

                    //课件详情
                    courseDetails.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            if (data.isEvalType()) {
                                updateLookTaskStatus(data.getCommitTaskId(), data.isRead());
                                //语音评测资源
                                enterSpeechAssessmentActivity(data.getStudentResUrl());
                            } else {
                                if (!isPlaying) {
                                    isPlaying = true;
                                    //更新小红点
                                    updateLookTaskStatus(data.getCommitTaskId(), data.isRead());
                                    if (taskType == StudyTaskType.ENGLISH_WRITING) {
                                        //英文写作点评记录页面
                                        englishWritingPageSkip(data);
                                    } else {
                                        //打开微课详情页面
                                        if (task != null) {
                                            task.setResId(data.getStudentResId());
                                            Bundle bundle = new Bundle();
                                            bundle.putBoolean(ActivityUtils.EXTRA_IS_NEED_HIDE_COLLECT_BTN, false);
                                            CourseOpenUtils.openCourseDetailsDirectly(getActivity(), task,
                                                    roleType, getMemeberId(), studentId, null, false, bundle);
                                        }
                                    }
                                }
                            }
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
                        if (isAnswerTaskOrderQuestion){
                            enterStudentAnswerDetailActivity(data,false);
                        } else if (data.isEvalType()) {
                            enterTeacherEvalDetail(data);
                        } else {
                            if (taskType == StudyTaskType.ENGLISH_WRITING) {
                                //英文写作点评记录页面
                                englishWritingPageSkip(data);
                            } else {
                                openImage(data);
                            }
                        }
                    }
                }

                @Override
                protected void updateLookTaskStatus(int commitTaskId, boolean isRead) {
                    //只有发布的老师查看学生作业才调,过滤班主任。
                    if (task != null
                            && !TextUtils.isEmpty(task.getTaskCreateId())
                            && !TextUtils.isEmpty(getMemeberId())) {
                        if (!isRead && (task.getTaskCreateId().equals(getMemeberId()) || isOnlineReporter)) {
                            updateStatus(commitTaskId);
                        }
                    }
                }
            };
            setCurrAdapterViewHelper(listView, listViewHelper);
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
        String studentId = data.getStudentId();
        if (!TextUtils.isEmpty(studentId)) {
            bundle.putString(EnglishWritingCompletedFragment.Constant.STUDENTID,
                    data.getStudentId());
            bundle.putString(EnglishWritingCompletedFragment.Constant.SORTSTUDENTID,
                    data.getStudentId());
        }
        bundle.putBoolean(ActivityUtils.EXTRA_IS_HISTORY_CLASS,isHistoryClass);
        //任务的id。
        bundle.putInt(EnglishWritingCompletedFragment.Constant.TASKID, data.getTaskId());
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

    private void updateViews(HomeworkCommitObjectResult result) {
        HomeworkCommitObjectInfo homeworkCommitObjectInfo = result.getModel().getData();
        if (homeworkCommitObjectInfo != null) {
            task = homeworkCommitObjectInfo.getTaskInfo();
            if (task != null) {
                loadTaskCourseDetail(task.getResId());
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
            getCurrAdapterViewHelper().setData(resultList);
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

    private void loadTaskCourseDetail(String resId) {
        if (TextUtils.isEmpty(resId)) {
            return;
        }
        WawaCourseUtils wawaCourseUtils = new WawaCourseUtils(getActivity());
        if (resId.contains("-")) {
            resId = resId.split("-")[0];
        }
        wawaCourseUtils.loadCourseDetail(resId);
        wawaCourseUtils.setOnCourseDetailFinishListener(new WawaCourseUtils.OnCourseDetailFinishListener() {
            @Override
            public void onCourseDetailFinish(CourseData courseData) {
                if (getActivity() == null) {
                    return;
                }
                if (courseData != null) {
                    taskCourseOrientation = courseData.screentype;
                }
            }
        });
    }

    private void enterSpeechAssessmentActivity(String resUrl) {
        SpeechAssessmentActivity.start(
                getActivity(),
                taskCourseOrientation,
                resUrl,
                task.getScoringRule() == 0 ? 2 : task.getScoringRule());
    }

    private void enterTeacherEvalDetail(CommitTask commitTask) {
        int scoreRule = task.getScoringRule();
        if (scoreRule == 0) {
            //默认百分制
            scoreRule = 2;
        }
        TeacherReviewDetailActivity.start(getActivity(),
                commitTask.isHasVoiceReview(),
                false,
                getPageScoreList(commitTask.getAutoEvalContent()),
                commitTask.getTaskScore(),
                commitTask.getTaskScoreRemark(),
                scoreRule,
                taskCourseOrientation,
                commitTask.getStudentResUrl(),
                "",
                String.valueOf(commitTask.getCommitTaskId()));
    }

    private boolean hasEvalReviewPermission() {
        if ((roleType == RoleType.ROLE_TYPE_TEACHER && TextUtils.equals(task.getCreateId(), getMemeberId()))
                || isOnlineReporter) {
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

    private void checkTaskOrderAnswerQuestionCard() {
        if (taskType != StudyTaskType.TASK_ORDER || TextUtils.isEmpty(courseResId)) {
            return;
        }
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("resId", courseResId);
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
                                    answerCardParam.setTaskId(taskId);
                                    answerCardParam.setRoleType(roleType);
                                    AdapterViewHelper adapterViewHelper = getCurrAdapterViewHelper();
                                    if (adapterViewHelper != null) {
                                        adapterViewHelper.update();
                                    }
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


    /**
     * 任务单答题卡列表项
     */
    private void enterStudentAnswerDetailActivity(CommitTask data, boolean isCheckMark) {
        if (answerCardParam != null && task != null) {
//            answerCardParam.setCommitTaskTitle(task.getTaskTitle());
//            answerCardParam.setRoleType(RoleType.ROLE_TYPE_VISITOR);
//            answerCardParam.setStudentName(data.getStudentName());
//            answerCardParam.setCommitTaskId(data.getCommitTaskId());
//            AnswerCardDetailActivity.start(getActivity(), answerCardParam);
            DoTaskOrderHelper.openExerciseDetail(
                    getActivity(),
                    answerCardParam.getExerciseAnswerString(),
                    taskId,
                    data.getStudentId(),
                    answerCardParam.getResId(),
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
    public static void setHasContentChanged(boolean hasContentChanged) {
        StudentFinishedHomeworkListFragment.hasContentChanged = hasContentChanged;
    }

    public static boolean hasContentChanged() {
        return hasContentChanged;
    }
}
