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
import android.widget.ListView;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.EnglishWritingCommentRecordActivity;
import com.galaxyschool.app.wawaschool.HomeworkFinishStatusActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.CourseOpenUtils;
import com.galaxyschool.app.wawaschool.common.StudyTaskUtils;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.galaxyschool.app.wawaschool.fragment.resource.HomeworkCommitResourceAdapterViewHelper;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.CommitTask;
import com.galaxyschool.app.wawaschool.pojo.HomeworkCommitObjectInfo;
import com.galaxyschool.app.wawaschool.pojo.HomeworkCommitObjectResult;
import com.galaxyschool.app.wawaschool.pojo.RoleType;
import com.galaxyschool.app.wawaschool.pojo.StudyTask;
import com.galaxyschool.app.wawaschool.pojo.StudyTaskType;
import com.galaxyschool.app.wawaschool.views.NoDoubleClickListener;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by E450 on 2017/3/14.
 * 任务要求
 */

public class TaskRequirementFragment extends ContactsListFragment{
    public static String TAG = TaskRequirementFragment.class.getSimpleName();

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
    private TextView contentTextView;
    private boolean hiddenHeaderView;
    private View placeHolderLayout;//占位布局
    private TextView wordsCountTextView;//作文字数
    private TextView commitBtn;//提交按钮
    private boolean shouldShowCommitBtn;//显示提交按钮
    private TextView speechAssessmentTextV;//语音评测的按钮
    private FrameLayout SpeechAssessmentFl;
    private FrameLayout retellCourseFl;
    private FrameLayout mStatisticFl;
    private boolean isHistoryClass;
    private int propertiesType;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.layout_fragment_task_requirement, null);
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
            shouldShowCommitBtn = getArguments().getBoolean(HomeworkFinishStatusActivity.Constants
                    .SHOULD_SHOW_COMMIT_BTN);
            isHistoryClass = getArguments().getBoolean(ActivityUtils.EXTRA_IS_HISTORY_CLASS);
            propertiesType= getArguments().getInt(HomeworkMainFragment.Constants.EXTRA_COURSE_TYPE_PROPERTIES);
        }

        //头布局
        View headerView = findViewById(R.id.contacts_header_layout);
        if (headerView != null){
            headerView.setVisibility(hiddenHeaderView ? View.GONE : View.VISIBLE);
        }
        //标题
        TextView textView = (TextView) findViewById(R.id.contacts_header_title);
        if (textView != null) {
            if (!TextUtils.isEmpty(taskTitle)) {
                textView.setText(taskTitle);
            }
        }

        contentTextView = (TextView) findViewById(R.id.tv_content);
        if (contentTextView != null){
            contentTextView.setVisibility(View.VISIBLE);
        }

        //作文字数
        wordsCountTextView = (TextView) findViewById(R.id.tv_words_count);
        if (wordsCountTextView != null){
            wordsCountTextView.setVisibility(View.GONE);
        }

        //占位布局
        placeHolderLayout = findViewById(R.id.layout_place_holder);

        //语音评测
        speechAssessmentTextV = (TextView) findViewById(R.id.tv_speech_assessment);
        if (speechAssessmentTextV != null){
            speechAssessmentTextV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment fragment = getParentFragment();
                    if (fragment instanceof HomeworkCommitFragment){
                        ((HomeworkCommitFragment)fragment).enterSpeechAssessmentActivity();
                    }
                }
            });
        }
        SpeechAssessmentFl = (FrameLayout) findViewById(R.id.fl_speech_assessment);
        retellCourseFl = (FrameLayout) findViewById(R.id.fl_retell_course);
        if (isHistoryClass){
            SpeechAssessmentFl.setVisibility(View.GONE);
            retellCourseFl.setVisibility(View.GONE);
        }

        //成绩统计
        mStatisticFl = (FrameLayout) findViewById(R.id.fl_statistic);
        findViewById(R.id.tv_statistic).setOnClickListener((v) -> {
            HomeworkCommitFragment parentFragment = (HomeworkCommitFragment) getParentFragment();
            if (parentFragment != null) {
                parentFragment.enterAchievementStatistics();
            }
        });


        //下拉刷新
        PullToRefreshView pullToRefreshView = (PullToRefreshView) findViewById(
                R.id.contacts_pull_to_refresh);
        pullToRefreshView.setRefreshEnable(false);
        setPullToRefreshView(pullToRefreshView);
        listView = (ListView) findViewById(R.id.contacts_list_view);
        if (listView != null) {
            //先隐藏
            listView.setVisibility(View.GONE);

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
                    view.setBackgroundColor(getResources().getColor(R.color.main_bg_color));
                    CommitTask data = (CommitTask) getDataAdapter().getItem(position);
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
                                textView.setText(task.getTaskTitle());
                            }
                        }else {
                            textView.setText(data.getStudentResTitle());
                        }
                    }

                    //作业图片布局
                    View iconLayout = view.findViewById(R.id.layout_icon);
                    //作业图片
                    if (iconLayout != null) {
                        if (taskType == StudyTaskType.ENGLISH_WRITING) {
                            iconLayout.setVisibility(View.GONE);
                        }else {
                            iconLayout.setVisibility(View.VISIBLE);
                        }
                    }

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
                        if (!isPlaying) {
                            isPlaying = true;
                            //更新小红点
                            updateLookTaskStatus(data.getCommitTaskId(),data.isRead());
                            if (taskType == StudyTaskType.ENGLISH_WRITING){
                                //英文写作点评记录页面
                                enterEnglishWritingCommentRecordActivity(data);

                            }else {
                                //打开微课详情页面
                                CourseOpenUtils.openCourseDetailsDirectly(getActivity(),task,roleType,
                                        getMemeberId(),studentId,null,false);
                            }
                        }
                    }
                }

                protected void updateLookTaskStatus(int commitTaskId, boolean isRead) {
                    //只有发布的老师查看学生作业才调,过滤班主任。
                    if (task != null
                            && !TextUtils.isEmpty(task.getTaskCreateId())
                            && !TextUtils.isEmpty(getMemeberId())) {
                        if (!isRead && task.getTaskCreateId().equals(getMemeberId())) {
                            updateStatus(commitTaskId);
                        }
                    }
                }
            };
            setCurrAdapterViewHelper(listView,listViewHelper);
        }
        //提交列表
        commitBtn = (TextView) findViewById(R.id.retell_course_btn);
        if (commitBtn != null){
            commitBtn.setOnClickListener(new NoDoubleClickListener() {
                @Override
                protected void onNoDoubleClick(View v) {
                    controlCommitClickEventByTaskType(taskType);
                }
            });
            if (shouldShowCommitBtn && !isHistoryClass) {
                initCommitBtnTextByTaskType(taskType, commitBtn);
            }else {
                //默认是隐藏的
                retellCourseFl.setVisibility(View.GONE);
            }
        }
    }

    private void initCommitBtnTextByTaskType(final int taskType, TextView commitBtn) {
        if (commitBtn == null){
            return;
        }
        String result = "";
        if (roleType == RoleType.ROLE_TYPE_STUDENT
                || roleType == RoleType.ROLE_TYPE_PARENT) {
            commitBtn.setVisibility(View.VISIBLE);
            if (taskType == StudyTaskType.INTRODUCTION_WAWA_COURSE
                    || taskType == StudyTaskType.SUBMIT_HOMEWORK) {
                result = getString(R.string.commit_task);
            } else if (taskType == StudyTaskType.RETELL_WAWA_COURSE) {
                result = getString(R.string.retell_course_new);
            } else if (taskType == StudyTaskType.ENGLISH_WRITING) {
                //英文写作
                result = getString(R.string.start_writing);
                //先隐藏，防止闪现。
                commitBtn.setVisibility(View.GONE);
            } else if (taskType == StudyTaskType.TASK_ORDER) {
                result = getString(R.string.do_task);
            } else if (taskType == StudyTaskType.Q_DUBBING) {
                result = getString(R.string.str_start_dubbing);
            } else {
                commitBtn.setVisibility(View.GONE);
            }
            commitBtn.setText(result);
            retellCourseFl.setVisibility(View.VISIBLE);
        }else {
            retellCourseFl.setVisibility(View.GONE);
        }
    }

    private void enterEnglishWritingCommentRecordActivity(CommitTask data) {
        if (data == null){
            return;
        }
        Intent intent = new Intent(getActivity(), EnglishWritingCommentRecordActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("roleType",roleType);
        String studentId = data.getStudentId();
        if (!TextUtils.isEmpty(studentId)) {
            bundle.putString(EnglishWritingCompletedFragment.Constant.STUDENTID,
                    data.getStudentId());
            bundle.putString(EnglishWritingCompletedFragment.Constant.SORTSTUDENTID,
                    data.getStudentId());
        }
        //任务的id。
        bundle.putInt(EnglishWritingCompletedFragment.Constant.TASKID,data.getTaskId());
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void updateStatus(final int commitTaskId) {

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("CommitTaskId", commitTaskId);
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
        {
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
    }

    /**
     * 更新tab数据
     * @param result
     */
    public void updateTabData(HomeworkCommitObjectResult result){
        updateViews(result);
    }

    /**
     * 判断是否需要显示英文写作字数要求
     * @return
     */
    private boolean showWordsCountTextView() {
        boolean show = false;
        if (task != null){
            int taskType = task.getType();
            if (taskType == StudyTaskType.ENGLISH_WRITING) {
                int minCount = task.getWordCountMin();
                int maxCount = task.getWordCountMax();
                if (minCount >= 0 && maxCount > 0) {
                    show = true;
                }
            }
        }
        return show;
    }

    private void updateViews(HomeworkCommitObjectResult result) {
        if (!isAdded()) {
            return;
        }
        HomeworkCommitObjectInfo homeworkCommitObjectInfo = result.getModel().getData();
        if (homeworkCommitObjectInfo != null) {
            task = homeworkCommitObjectInfo.getTaskInfo();
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
                if (taskType == StudyTaskType.RETELL_WAWA_COURSE && propertiesType == 1){
                    //语音评测课件
                    mStatisticFl.setVisibility(View.VISIBLE);
                } else {
                    mStatisticFl.setVisibility(View.GONE);
                }
            }
        }
        if (task != null){
            String taskRequirements = "";
            if (taskType == StudyTaskType.ENGLISH_WRITING) {
                taskRequirements = task.getWritingRequire();
            }else {
                taskRequirements = task.getDiscussContent();
            }
            //作文字数
            String wordsCountStr = getString(R.string.article_limited_word_point) + " "+ task
                    .getWordCountMin()+"  -  "+task.getWordCountMax();
            wordsCountTextView.setText(wordsCountStr);
            //完成方式
            TextView completionModelView = (TextView) findViewById(R.id.tv_completion_mode);
            int completionMode = task.getRepeatCourseCompletionMode();
            if ((completionMode == 1 && propertiesType == 1) || completionMode == 2){
                //复述课件
                if (TextUtils.isEmpty(taskRequirements)){
                    taskRequirements = getString(R.string.str_no_analyse_tip);
                }
                taskRequirements = getString(R.string.student_should_complete_task) + "\n" + taskRequirements;
                //完成方式的string
                String modeTips = null;
                if (completionMode == 1){
                    modeTips = getString(R.string.retell_course_new);
                } else {
                    modeTips = getString(R.string.str_task_type_combination);
                }
                String completionModeString  = getString(R.string.str_completion_mode) + "\n" + modeTips;
                completionModelView.setText(completionModeString);
                completionModelView.setVisibility(View.VISIBLE);
            }
            //任务要求
            contentTextView.setText(taskRequirements);
            if (TextUtils.isEmpty(taskRequirements)){
                //任务要求是空的，需要显示占位图。
                contentTextView.setVisibility(View.GONE);
                //隐藏字数
                wordsCountTextView.setVisibility(View.GONE);
                placeHolderLayout.setVisibility(View.VISIBLE);
            }else {
                contentTextView.setVisibility(View.VISIBLE);
                ///英文写作才显示字数
                if (showWordsCountTextView()) {
                    wordsCountTextView.setVisibility(View.VISIBLE);
                }
                placeHolderLayout.setVisibility(View.GONE);
                //显示灰色背景
                rootView.setBackgroundColor(getResources().getColor(R.color.main_bg_color));
            }
        }

        //提交作业列表
        getCurrAdapterViewHelper().setData(null);
    }

    /**
     * 判断是否需要更新提交按钮的状态
     * @return
     */
    private boolean needUpdateCommitBtnState(){
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
     * @param data
     */
    private void updateCommitBtn(HomeworkCommitObjectInfo data) {
        if (isHistoryClass) {
            retellCourseFl.setVisibility(View.GONE);
            return;
        }
        boolean hiddenCommitBtn = false;
        //学生、家长英文写作才更新。
        if (needUpdateCommitBtnState()){
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
            retellCourseFl.setVisibility(hiddenCommitBtn ? View.GONE : View.VISIBLE);
        }

        //老师角色更新按钮状态
        if (roleType == RoleType.ROLE_TYPE_TEACHER){
            //老师角色，如果有任务单，则显示“查看任务单”，否则不显示。
            if (taskType == StudyTaskType.TASK_ORDER
                    || taskType == StudyTaskType.INTRODUCTION_WAWA_COURSE){
                if (task != null){
                    if (!TextUtils.isEmpty(task.getWorkOrderId())){
                        //有任务单
                        retellCourseFl.setVisibility(View.VISIBLE);
                        //查看任务单
                        commitBtn.setText(getString(R.string.watch_task));
                    }else {
                        retellCourseFl.setVisibility(View.GONE);
                    }
                }
            }
        }
    }

    /**
     * 按照类型提交
     * @param taskType
     */
    private void controlCommitClickEventByTaskType(int taskType) {
        Fragment parentFragment = getParentFragment();
        if (parentFragment != null && parentFragment instanceof HomeworkCommitFragment){
            //调用父类的提交方法
            if (isViewTaskOrder()){
                //老师查看任务单
                ((HomeworkCommitFragment) parentFragment).takeTask(false);
            }else {
                //学生、家长提交按钮逻辑
                ((HomeworkCommitFragment) parentFragment).
                        controlCommitClickEventByTaskType(taskType);
            }
        }
    }

    /**
     * 判断是否是查看任务单
     * @return
     */
    private boolean isViewTaskOrder(){
       return roleType == RoleType.ROLE_TYPE_TEACHER;
    }

    public void setSpeechAssessmentViewVisible(){
        if (SpeechAssessmentFl != null && !isHistoryClass){
            SpeechAssessmentFl.setVisibility(View.VISIBLE);
        }
    }

    public void setStatisticViewVisible() {
        if (mStatisticFl != null) {
            propertiesType = 1;
            mStatisticFl.setVisibility(View.VISIBLE);
        }
    }
}
