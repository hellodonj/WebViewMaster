package com.galaxyschool.app.wawaschool.fragment;

import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.Request;
import com.galaxyschool.app.wawaschool.AnswerCardDetailActivity;
import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.QDubbingActivity;
import com.galaxyschool.app.wawaschool.TeacherReviewDetailActivity;
import com.galaxyschool.app.wawaschool.common.WawaCourseUtils;
import com.galaxyschool.app.wawaschool.helper.DoTaskOrderHelper;
import com.galaxyschool.app.wawaschool.pojo.ExerciseAnswerCardParam;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseData;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseUploadResult;
import com.galaxyschool.app.wawaschool.LearningStatisticActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.galaxyschool.app.wawaschool.pojo.CheckMarkResult;
import com.galaxyschool.app.wawaschool.pojo.CommitTask;
import com.galaxyschool.app.wawaschool.pojo.RoleType;
import com.galaxyschool.app.wawaschool.pojo.StudyTask;
import com.galaxyschool.app.wawaschool.pojo.StudyTaskType;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;
import com.galaxyschool.app.wawaschool.views.sortlistview.ClearEditText;
import com.lqwawa.intleducation.common.utils.ImageUtil;
import com.lqwawa.intleducation.common.utils.StringUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.entity.tutorial.TaskEntity;
import com.lqwawa.intleducation.module.tutorial.marking.require.TaskRequirementActivity;
import com.lqwawa.lqbaselib.net.ThisStringRequest;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.lqwawa.mooc.common.MOOCHelper;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReMarkTaskListFragment extends ContactsListFragment {

    private LinearLayout mSearchLayout;
    private EditText searchEditText;

    private String classId;
    private int courseId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_remark_task_list, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadIntentData();
        initView();
        initSearchData();
        loadAdapterView();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCommonData();
    }

    private void loadIntentData() {
        Bundle args = getArguments();
        if (args != null) {
            classId = args.getString(LearningStatisticActivity.Constants.CLASS_ID);
            courseId = args.getInt(LearningStatisticActivity.Constants.COURSE_ID);
        }
    }

    private void initView() {
        TextView titleView = (TextView) findViewById(R.id.contacts_header_title);
        if (titleView != null) {
            titleView.setText(getString(R.string.str_un_remark_homework));
        }
        ImageView backImageView = (ImageView) findViewById(R.id.contacts_header_left_btn);
        if (backImageView != null) {
            backImageView.setOnClickListener(v -> finish());
        }
    }

    /**
     * 初始化搜索的数据
     */
    private void initSearchData() {
        //搜索的总布局
        mSearchLayout = (LinearLayout) findViewById(R.id.contacts_search_bar_layout);
        //搜索文本框
        final ClearEditText editText = (ClearEditText) findViewById(R.id.search_keyword);
        if (editText != null) {
            editText.setHint(getString(R.string.search));
            editText.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
            editText.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    hideSoftKeyboard(getActivity());
                    //加载相应的数据
                    loadCommonData();
                    return true;
                }
                return false;
            });
            //清空文本框中内容
            editText.setOnClearClickListener(() -> {
                editText.setText("");
                getCurrAdapterViewHelper().clearData();
                getPageHelper().clear();
                //加载数据
                loadCommonData();
            });
            editText.setInputType(InputType.TYPE_CLASS_TEXT
                    | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
            searchEditText = editText;
        }
    }

    private void loadAdapterView() {
        PullToRefreshView pullToRefreshView = (PullToRefreshView) findViewById(R.id.contacts_pull_to_refresh);
        setPullToRefreshView(pullToRefreshView);
        GridView listView = (GridView) findViewById(R.id.resource_list_view);
        if (listView != null) {
            listView.setVerticalSpacing(0);
            listView.setHorizontalSpacing(0);
            AdapterViewHelper adapterViewHelper = new AdapterViewHelper(getActivity(),
                    listView, R.layout.item_tutorial_work_layout) {
                @Override
                public void loadData() {
                    loadCommonData();
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View itemView = super.getView(position, convertView, parent);
                    final CommitTask data = (CommitTask) getData().get(position);
                    if (data == null) {
                        return itemView;
                    }
                    ViewHolder holder = (ViewHolder) itemView.getTag();
                    if (holder == null) {
                        holder = new ViewHolder();
                    }
                    holder.data = data;
                    FrameLayout mRequireLayout =
                            (FrameLayout) itemView.findViewById(com.lqwawa.intleducation.R.id.require_layout);
                    mRequireLayout.setOnClickListener(v -> {
                        //点击任务要求
                        onClickItemView(data,true);
                    });
                    ImageView userIconImageV = (ImageView) itemView.findViewById(com.lqwawa.intleducation.R.id.iv_student_avatar);
                    ImageUtil.fillUserAvatar(userIconImageV, AppSettings.getFileUrl(data.getHeadPicUrl()),R.drawable.user_header_def);
                    TextView studentNameView =
                            (TextView) itemView.findViewById(com.lqwawa.intleducation.R.id.tv_student_name);
                    if (studentNameView != null){
                        studentNameView.setText(data.getStudentName());
                    }
                    LinearLayout mBodyLayout = (LinearLayout) itemView.findViewById(com.lqwawa.intleducation.R.id.body_layout);
                    if (mBodyLayout != null){
                        mBodyLayout.setOnClickListener(v -> {
                            //item 点击
                            onClickItemView(data,false);
                        });
                    }
                    ImageView taskImageView = (ImageView) itemView.findViewById(com.lqwawa.intleducation.R.id.iv_task_icon);
                    if (taskImageView != null){
                        if (data.isEvalType() || (data.isMarkCard() && !data.isCourseType())) {
                            if (data.isEvalType()){
                                MyApplication.getThumbnailManager(getActivity()).displayUserIconWithDefault(
                                        "", taskImageView, R.drawable.icon_student_task_eval);
                            } else {
                                //答题卡
                                MyApplication.getThumbnailManager(getActivity()).displayUserIconWithDefault(
                                        "", taskImageView, R.drawable.icon_exercise_card);
                            }
                        } else {
                            //显示的缩略图
                            ImageUtil.fillNotificationView(taskImageView, AppSettings.getFileUrl(data.getStudentResThumbnailUrl()));
                        }
                    }
                    //显示任务的类型
                    TextView mTaskType =
                            (TextView) itemView.findViewById(com.lqwawa.intleducation.R.id.tv_task_type);
                    if (mTaskType != null){
                        mTaskType.setVisibility(View.GONE);
                        if (data.getType() == StudyTaskType.RETELL_WAWA_COURSE) {
                            // 听读课
                            String typeName = String.format(UIUtil.getString(R.string.label_task_type_template), UIUtil.getString(R.string.label_tutorial_task_type_listen_read_course));
                            StringUtil.fillSafeTextView(mTaskType, typeName);
                        } else {
                            // 做读写单
                            String typeName = String.format(UIUtil.getString(R.string.label_task_type_template), UIUtil.getString(R.string.label_tutorial_task_type_do_task));
                            StringUtil.fillSafeTextView(mTaskType, typeName);
                        }
                    }
                    TextView mTaskName =
                            (TextView) itemView.findViewById(com.lqwawa.intleducation.R.id.tv_task_name);
                    if (mTaskName != null){
                        mTaskName.setText(data.getStudentResTitle());
                    }
                    //来源的班级
                    TextView mTaskClass =
                            (TextView) itemView.findViewById(com.lqwawa.intleducation.R.id.tv_task_class);
                    if (mTaskClass != null){
                        mTaskClass.setVisibility(View.INVISIBLE);
                    }
                    TextView classNameView =
                            (TextView) itemView.findViewById(com.lqwawa.intleducation.R.id.tv_task_chapter);
                    if (classNameView != null){
                        classNameView.setVisibility(View.GONE);
                        //一年级五班
                        classNameView.setText(data.getClassName());
                    }
                    TextView mTaskTime =
                            (TextView) itemView.findViewById(com.lqwawa.intleducation.R.id.tv_task_time);
                    if (mTaskTime != null){
                        //任务时间
                        String commitTime = data.getCommitTime();
                        if (!TextUtils.isEmpty(commitTime)) {
                            if (commitTime.contains(":")) {
                                //精确到分
                                commitTime = commitTime.substring(0,commitTime.lastIndexOf(":"));
                            }
                            mTaskTime.setText(commitTime);
                        }
                    }
                    TextView mCheckMark =
                            (TextView) itemView.findViewById(com.lqwawa.intleducation.R.id.tv_check_mark);
                    if (mCheckMark != null){
                        //未批阅
                        mCheckMark.setActivated(false);
                        mCheckMark.setTextColor(UIUtil.getColor(android.R.color.holo_red_light));
                        mCheckMark.setText(com.lqwawa.intleducation.R.string.label_un_mark);
                    }
                    //过期时间
                    TextView mExpiredTime =
                            (TextView) itemView.findViewById(com.lqwawa.intleducation.R.id.tv_expired_time);
                    if (mExpiredTime != null){
                        mExpiredTime.setVisibility(View.GONE);
                    }
                    itemView.setTag(holder);
                    return itemView;
                }

                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {

                }
            };
            setCurrAdapterViewHelper(listView, adapterViewHelper);
        }
    }

    private void onClickItemView(CommitTask data,boolean isRequirement){
        if (isRequirement){
            //查看任务要求
            TaskEntity entity = new TaskEntity();
            entity.setT_TaskId(data.getTaskId());
            TaskRequirementActivity.show(getActivity(),entity);
        } else {
            //进入批阅列表
            updateRedPoint(data);
            if (data.isEvalType()){
                //语音评测
                enterTeacherEvalDetail(data);
            } else if (data.isMarkCard()) {
                //自动批阅的读写单
                enterMarkCardDetail(data);
            } else if (data.isVideoType()) {
                //q配音
                enterQDubbingDetail(data);
            } else {
                enterCheckMarkDetail(data);
            }
        }
    }

    private void enterQDubbingDetail(CommitTask data){
        WawaCourseUtils wawaCourseUtils = new WawaCourseUtils(getActivity());
        wawaCourseUtils.loadCourseDetail(data.getTeacherResId());
        wawaCourseUtils.setOnCourseDetailFinishListener(courseData -> {
            QDubbingActivity.start(getActivity(),
                    courseData.resourceurl,
                    courseData.level,
                    data,
                    true,
                    data.getResPropType());
        });
    }

    private void enterMarkCardDetail(CommitTask data){
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("resId", data.getTeacherResId());
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
                                    ExerciseAnswerCardParam answerCardParam =
                                            new ExerciseAnswerCardParam();
                                    answerCardParam.setExerciseTotalScore(exerciseTotalScore);
                                    answerCardParam.setResId(courseData.id + "-" + courseData.type);
                                    answerCardParam.setScreenType(courseData.screentype);
                                    answerCardParam.setExerciseAnswerString(uploadResult.exercise);
                                    boolean hasSubjectProblem = DoTaskOrderHelper.hasSubjectProblem(uploadResult.exercise);
                                    answerCardParam.setHasSubjectProblem(hasSubjectProblem);
                                    answerCardParam.setCommitTaskTitle(data.getStudentResTitle());
                                    answerCardParam.setIsHeadMaster(true);
                                    answerCardParam.setIsOnlineHost(true);
                                    answerCardParam.setRoleType(RoleType.ROLE_TYPE_TEACHER);
                                    answerCardParam.setStudentName(data.getStudentName());
                                    answerCardParam.setStudentId(data.getStudentId());
                                    answerCardParam.setCommitTaskId(data.getCommitTaskId());
                                    answerCardParam.setTaskScoreRemark(data.getTaskScoreRemark());
                                    answerCardParam.setCommitTask(data);
                                    StudyTask task = new StudyTask();
                                    task.setTaskCreateId(getMemeberId());
                                    answerCardParam.setStudyTask(task);
                                    AnswerCardDetailActivity.start(getActivity(), answerCardParam);
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

    private void enterTeacherEvalDetail(CommitTask commitTask) {
        WawaCourseUtils wawaCourseUtils = new WawaCourseUtils(getActivity());
        String resId = commitTask.getTeacherResId();
        if (TextUtils.isEmpty(resId)){
            return;
        }
        if (resId.contains(",")) {
            resId = resId.split(",")[0];
        }
        wawaCourseUtils.loadCourseDetail(resId);
        wawaCourseUtils.setOnCourseDetailFinishListener(new WawaCourseUtils.OnCourseDetailFinishListener() {
            @Override
            public void onCourseDetailFinish(CourseData courseData) {
                if (getActivity() == null) {
                    return;
                }
                if (courseData != null) {
                    int taskCourseOrientation = courseData.screentype;
                    TeacherReviewDetailActivity.start(getActivity(),
                            commitTask.isHasVoiceReview(),
                            true,
                            getPageScoreList(commitTask.getAutoEvalContent()),
                            commitTask.getTaskScore(),
                            commitTask.getTaskScoreRemark(),
                            courseId > 0 ? 2 : commitTask.getScoringRule(),
                            taskCourseOrientation,
                            commitTask.getStudentResUrl(),
                            "",
                            String.valueOf(commitTask.getCommitTaskId()));
                }
            }
        });
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
        StudyTask task = new StudyTask();
        task.setType(data.getType());
        task.setTaskCreateId(getMemeberId());
        //mooc都是百分制
        task.setScoringRule(courseId > 0 ? 2 : data.getScoringRule());
        MOOCHelper.enterCheckMarkDetail(getActivity(),data,task,RoleType.ROLE_TYPE_TEACHER,false,
                false);
    }

    private void loadCommonData() {
        Map<String, Object> params = new HashMap<>();
        //必填
        if (courseId > 0){
            params.put("CourseId", courseId);
        } else {
            params.put("MemberId",getMemeberId());
        }
        params.put("ClassId", classId);
        params.put("SearchName", searchEditText.getText().toString().trim());
        params.put("Pager", getPageHelper().getFetchingPagerArgs());
        //来自筛选的界面
        DefaultPullToRefreshDataListener listener = new DefaultPullToRefreshDataListener<CheckMarkResult>(
                CheckMarkResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                super.onSuccess(jsonString);
                CheckMarkResult result = getResult();
                if (result == null || !result.isSuccess()
                        || result.getModel() == null) {
                    return;
                }
                updateDataView(result);
            }
        };
        String serverUrl = null;
        if (courseId > 0){
            serverUrl = ServerUrl.GET_TEACHER_NO_CORRECT_COMMITLIST;
        } else {
            serverUrl = ServerUrl.GET_CLASSTEACHER_NOCORRECT_COMMITLIST;
        }
        RequestHelper.sendPostRequest(getActivity(), serverUrl, params, listener);
    }

    private void updateDataView(CheckMarkResult result) {
        if (getPageHelper().isFetchingPageIndex(result.getModel().getPager())) {
            List<CommitTask> list = result.getModel().getData();
            if (list == null || list.size() <= 0) {
                if (getPageHelper().isFetchingFirstPage()) {
                    getCurrAdapterViewHelper().clearData();
                    TipsHelper.showToast(getActivity(),
                            getString(R.string.no_data));
                } else {
                    TipsHelper.showToast(getActivity(),
                            getString(R.string.no_more_data));
                }
                return;
            }
            if (getPageHelper().isFetchingFirstPage()) {
                getCurrAdapterViewHelper().clearData();
            }
            getPageHelper().updateByPagerArgs(result.getModel().getPager());
            getPageHelper().setCurrPageIndex(
                    getPageHelper().getFetchingPageIndex());
            if (getCurrAdapterViewHelper().hasData()) {
                int position = getCurrAdapterViewHelper().getData().size();
                if (position > 0) {
                    position--;
                }
                getCurrAdapterViewHelper().getData().addAll(list);
                getCurrAdapterView().setSelection(position);
            } else {
                getCurrAdapterViewHelper().setData(list);
            }
        }
    }

    /**
     * 更新小红点
     */
    private void updateRedPoint(CommitTask data) {
        if (data == null || data.isRead()){
            return;
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("CommitTaskId", data.getCommitTaskId());
        //英文写作等类型更新
        params.put("TaskType", data.getType());
        RequestHelper.RequestListener listener =
                new RequestHelper.RequestDataResultListener<DataModelResult>(
                        getActivity(), DataModelResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                    }
                };
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.UPDATE_TEACHER_READ_TASK_URL,
                params, listener);

    }
}
