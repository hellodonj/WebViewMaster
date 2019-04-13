package com.galaxyschool.app.wawaschool.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import com.galaxyschool.app.wawaschool.CaptureActivity;
import com.galaxyschool.app.wawaschool.ContactsPickerActivity;
import com.galaxyschool.app.wawaschool.HandleCheckResourceActivity;
import com.galaxyschool.app.wawaschool.HomeworkPickerActivity;
import com.galaxyschool.app.wawaschool.Note.OnlineMediaPaperActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.TodayHomeworkActivity;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.ArrangeLearningTasksUtil;
import com.galaxyschool.app.wawaschool.common.CampusPatrolUtils;
import com.galaxyschool.app.wawaschool.common.CourseOpenUtils;
import com.galaxyschool.app.wawaschool.common.StudyTaskUtils;
import com.galaxyschool.app.wawaschool.common.Utils;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.MyFragmentPagerTitleAdapter;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.galaxyschool.app.wawaschool.fragment.resource.HomeworkResourceAdapterViewHelper;
import com.galaxyschool.app.wawaschool.pojo.ReviewInfo;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.AddedSchoolInfoListResult;
import com.galaxyschool.app.wawaschool.pojo.HomeworkChildListResult;
import com.galaxyschool.app.wawaschool.pojo.HomeworkListInfo;
import com.galaxyschool.app.wawaschool.pojo.HomeworkListResult;
import com.galaxyschool.app.wawaschool.pojo.RoleType;
import com.galaxyschool.app.wawaschool.pojo.SchoolInfo;
import com.galaxyschool.app.wawaschool.pojo.StudentMemberInfo;
import com.galaxyschool.app.wawaschool.pojo.StudyTaskType;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.galaxyschool.app.wawaschool.slide.SlideWawaPageActivity;
import com.galaxyschool.app.wawaschool.views.ContactsMessageDialog;
import com.galaxyschool.app.wawaschool.views.MyViewPager;
import com.galaxyschool.app.wawaschool.views.PagerSlidingTabStrip;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;
import com.galaxyschool.app.wawaschool.views.SelectBindChildPopupView;
import com.galaxyschool.app.wawaschool.views.slidelistview.SlideListView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 作业主页面
 */
public class HomeworkMainFragment extends ContactsListFragment implements
        SelectBindChildPopupView.OnRelationChangeListener {

    public static final String TAG = HomeworkMainFragment.class.getSimpleName();

    public interface Constants {
        String EXTRA_SCHOOL_ID = "schoolId";
        String EXTRA_CLASS_ID = "classId";
        String EXTRA_CHANNEL_TYPE = "channelType";
        String EXTRA_IS_TEACHER = "isTeacher";
        String EXTRA_ROLE_TYPE = "role_type";
        String EXTRA_IS_HISTORY = "is_history";
        String EXTRA_IS_HEAD_MASTER = "isHeadMaster";
        String EXTRA_CHILD_ID_ARRAY = "childIdArray";
        String EXTRA_IS_ONLINE_SCHOOL_CLASS = "isOnlineSchoolClass";
        String EXTRA_IS_FROM_REVIEW_DATA = "from_review_data";
        String EXTRA_COURSE_TYPE_PROPERTIES = "course_type_properties";
    }

    private TextView finishedTab, unfinishedTab;
    private View mRootView;
    private View bindChildLayout;
    private ImageView bindChildIcon;
    private TextView bindChildName;
    private String[] imageArray;
    private String[] childNameArray;
    private String[] childIdArray;
    private TextView switchChild;
    private SlideListView listView;
    public static final int UNFINISHED = 0;
    public static final int FINISHED = 1;
    private int finishStatus = UNFINISHED;
    private HomeworkListResult homeworkListResult;
    //筛选条件
    private String StartTimeBegin = "";
    private String StartTimeEnd = "";
    private String EndTimeBegin = "";
    private String EndTimeEnd = "";
    private String TaskTypes = "";
    private String TeacherIds = "";
    private String studentId = null;
    private int position = 0;
    private boolean isHistory;
    private boolean isHeadMaster;
    private List<StudentMemberInfo> studentMemberInfos;
    public static final int REQUEST_CODE_MEDIAPAPER = 100;
    public static boolean isScanTaskFinished = false;
    private SchoolInfo schoolInfo;
    private boolean isOnlineClass;
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 100:
                    HomeworkChildListResult result = (HomeworkChildListResult) msg.obj;
                    if (result != null) {
                        bindChildren(result);
                    }
                    break;
            }
        }
    };
    private int roleType = -1;//0,老师 1,学生 2,家长
    private String schoolId;
    private String classId;
    private String sortStudentId = "";
    private boolean isNeedToUpdateSmallRedPoint;//更新小红点
    //学习任务新需求
    private MyViewPager viewPager;
    private PagerSlidingTabStrip pagerSlidingTabStrip;
    private MyFragmentPagerTitleAdapter adapter;
    private List<Fragment> fragmentList = new ArrayList<>();
    private List<String> titleList = new ArrayList<>();
    private UnCompletedHomeworkListTabFragment unCompletedHomeworkListTabFragment;
    private CompletedHomeworkListTabFragment completedHomeworkListTabFragment;
    public static boolean hasPublishedCourseToStudyTask = false; // 课件发送到学习任务是否成功
    private boolean isOnlineSchoolClass;//是否是在线课堂的班级
    private boolean isApplicationStart = true;
    private boolean isFromReviewStatistic;//来自点评统计
    private ReviewInfo reviewInfo;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.layout_class_homework, null);
        return mRootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
        initData();
    }

    private void initData() {
        getPageHelper().clear();
        loadView();
    }

    private void refreshData() {
        switchTab();
        loadSchools();
    }

    private void initListView() {
        PullToRefreshView pullToRefreshView = (PullToRefreshView) findViewById(
                R.id.contacts_pull_to_refresh);
        setPullToRefreshView(pullToRefreshView);
        //隐藏
        pullToRefreshView.setVisibility(View.GONE);

        listView = (SlideListView) findViewById(R.id.contacts_list_view);
        if (listView != null) {
            listView.setSlideMode(SlideListView.SlideMode.NONE);
            listView.setClipToPadding(false);

            //作业通用列表
            AdapterViewHelper listViewHelper = new HomeworkResourceAdapterViewHelper(getActivity(),
                    listView, roleType, getMemeberId(), isHeadMaster) {
                @Override
                public void loadData() {
                    loadCommonData();
                }

                @Override
                public View getView(final int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    final HomeworkListInfo data =
                            (HomeworkListInfo) getDataAdapter().getItem(position);
                    if (data == null) {
                        return view;
                    }
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) {
                        holder = new ViewHolder();
                    }
                    holder.data = data;

                    //删除
                    View deleteView = view.findViewById(R.id.layout_delete_homework);
                    if (deleteView != null) {
                        deleteView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //删除条目
                                showDeleteDialog(data);
                            }
                        });
                    }

                    view.setTag(holder);
                    return view;
                }

                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) {
                        return;
                    }
                    HomeworkListInfo data = (HomeworkListInfo) holder.data;
                    //打开学习任务列表
                    CourseOpenUtils.openStudyTask(getActivity(), data, roleType, isHeadMaster,
                            getMemeberId(), sortStudentId, studentId, getCurUserInfo(), false);
                    super.onItemClick(parent, view, position, id);
                }

                /**
                 * 更新话题讨论和提交作业已读
                 * @param taskId
                 * @param memberId
                 * @param taskType
                 */
                @Override
                protected void UpdateStudentIsRead(String taskId, String memberId,
                                                   String taskType) {
                    updateStudentReadState(taskId, memberId, taskType);
                }

                /**
                 * 更新看微课、看课件、看作业已读
                 * @param taskId
                 * @param memberId
                 */
                @Override
                protected void updateReadStatus(String taskId, String memberId) {
                    updateReadState(taskId, memberId);
                }
            };

            setCurrAdapterViewHelper(listView, listViewHelper);
        }
    }

    /**
     * 更新话题讨论和交作业未读状态
     *
     * @param taskId
     * @param memberId
     * @param taskType
     */
    private void updateStudentReadState(final String taskId, String memberId, String taskType) {
        if (Integer.valueOf(taskType) == StudyTaskType.SUPER_TASK){
            isNeedToUpdateSmallRedPoint = true;
            updateAdapter(taskId);
            return;
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("TaskId", taskId);
        if (roleType == RoleType.ROLE_TYPE_PARENT && !TextUtils.isEmpty(studentId)){
            params.put("StudentId", studentId);
        } else {
            params.put("StudentId", memberId);
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
                            //更新
                            isNeedToUpdateSmallRedPoint = true;
                            updateAdapter(taskId);
                        }
                    }
                };
        listener.setShowLoading(true);
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.UPDATE_STUDENT_IS_READ_URL,
                params, listener);

    }

    private void updateAdapter(String taskId){
        if (getCurrAdapterViewHelper().hasData()) {
            List<HomeworkListInfo> list = getCurrAdapterViewHelper().getData();
            for (HomeworkListInfo task : list) {
                if (task.getTaskId().equals(taskId)) {
                    task.setStudentIsRead(true);
                    break;
                }
            }
            getCurrAdapterViewHelper().update();
        }
    }

    /**
     * 更新阅读状态
     *
     * @param taskId
     * @param memberId
     */
    private void updateReadState(final String taskId, String memberId) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("TaskId", taskId);
        if (roleType == RoleType.ROLE_TYPE_PARENT && !TextUtils.isEmpty(studentId)){
            params.put("StudentId", studentId);
        } else {
            params.put("StudentId", memberId);
        }
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
                            //更新
                            isNeedToUpdateSmallRedPoint = true;
                            if (getCurrAdapterViewHelper().hasData()) {
                                List<HomeworkListInfo> list = getCurrAdapterViewHelper().getData();
                                for (HomeworkListInfo task : list) {
                                    if (task.getTaskId().equals(taskId)) {
                                        task.setStudentIsRead(true);
                                        break;
                                    }
                                }
                                getCurrAdapterViewHelper().update();
                            }
                        }
                    }
                };
        listener.setShowLoading(true);
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.STUDENT_COMMIT_HOMEWORK_URL,
                params, listener);

    }

    /**
     * 删除框Dialog
     *
     * @param data
     */
    private void showDeleteDialog(final HomeworkListInfo data) {
        ContactsMessageDialog messageDialog = new ContactsMessageDialog(getActivity(), null,
                getString(R.string.want_to_delete_sb, data.getTaskTitle()), getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }, getString(R.string.confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                deleteItem(data);
            }
        });
        messageDialog.show();
    }


    /**
     * 删除条目
     *
     * @param data
     */
    private void deleteItem(final HomeworkListInfo data) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("TaskCreateId", data.getTaskCreateId());
        params.put("TaskId", data.getTaskId());
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
                            TipsHelper.showToast(getActivity(), R.string.delete_success);
                            getCurrAdapterViewHelper().getData().remove(data);
                            getCurrAdapterViewHelper().update();
                        }
                    }
                };
        listener.setShowLoading(true);
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.DELETE_TASK_BY_TEACHER_URL,
                params, listener);

    }

    /**
     * 模拟数据
     */
    private void loadCommonData() {
        Map<String, Object> params = new HashMap();
        //学校Id，必填
        params.put("SchoolId", schoolId);
        //班级Id，必填
        params.put("ClassId", classId);
        //角色信息，必填，0-学生,1-家长，2-老师
        int role = Utils.transferRoleType(roleType);
        if (role != -1) {
            params.put("RoleType", role);
        }
        //任务状态(0-未完成,1-已完成),学生，家长角色时必填。
        if (roleType == RoleType.ROLE_TYPE_STUDENT || roleType == RoleType.ROLE_TYPE_PARENT) {
            params.put("TaskState", finishStatus);
        }
        //学生ID，非必填，学生、家长角色时必填
        if (roleType == RoleType.ROLE_TYPE_STUDENT) {
            params.put("StudentId", getMemeberId());
        } else if (roleType == RoleType.ROLE_TYPE_PARENT) {
            //家长的话，必须要加载绑定的孩子。
            if (studentId != null) {
                params.put("StudentId", studentId);
            }
        }

        //	若查询"今日作业"，传当前日期，格式:"yyyy-MM-dd" ，否则为空
        params.put("SearchTime", "");
        //任务创建者的id,老师角色时必填
        //目前要放开所有人的查看权限，该字段需要传空。
        if (roleType == RoleType.ROLE_TYPE_TEACHER || roleType == RoleType.ROLE_TYPE_STUDENT
                || roleType == RoleType.ROLE_TYPE_PARENT) {
            params.put("TaskCreateId", "");
        }
        //布置作业开始，非必填，所有角色筛选时使用，格式:"yyyy-MM-dd"
        if (!TextUtils.isEmpty(StartTimeBegin)) {
            params.put("StartTimeBegin", StartTimeBegin);
        }
        //布置作业结束，非必填，所有角色筛选时使用，格式:"yyyy-MM-dd"
        if (!TextUtils.isEmpty(StartTimeEnd)) {
            params.put("StartTimeEnd", StartTimeEnd);
        }
        //完成作业开始，非必填，所有角色筛选时使用，格式:"yyyy-MM-dd"
        if (!TextUtils.isEmpty(EndTimeBegin)) {
            params.put("EndTimeBegin", EndTimeBegin);
        }
        //完成作业结束，非必填，所有角色筛选时使用，格式:"yyyy-MM-dd"
        if (!TextUtils.isEmpty(EndTimeEnd)) {
            params.put("EndTimeEnd", EndTimeEnd);
        }
        //任务类型，非必填，所有角色筛选时使用，多个任务时，用逗号进行分隔（1,2,3）,0-看微课,1-看课件,2看作业,
        // 3-交作业,4-讨论话题
        if (!TextUtils.isEmpty(TaskTypes)) {
            params.put("TaskTypes", TaskTypes);
        }
        //查询时间查询的老师的id集合,非必填,学生、家长角色筛选时使用，多个id直接用逗号进行分割（,）
        if (roleType == RoleType.ROLE_TYPE_STUDENT || roleType == RoleType.ROLE_TYPE_PARENT) {
            if (!TextUtils.isEmpty(TeacherIds)) {
                params.put("TeacherIds", TeacherIds);
            }
        }
        //分页信息
        params.put("Pager", getPageHelper().getFetchingPagerArgs());
        params.put("Version", 1);

        RequestHelper.sendPostRequest(getActivity(),
                ServerUrl.GET_STUDENT_TASK_LIST_URL, params,
                new DefaultPullToRefreshDataListener<HomeworkListResult>(
                        HomeworkListResult.class) {
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

                        updateResourceListView(getResult());
                    }
                });

    }

    private void updateResourceListView(HomeworkListResult result) {

        if (getPageHelper().isFetchingPageIndex(result.getModel().getPager())) {
            List<HomeworkListInfo> list = result.getModel().getData();
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
                //目前隐藏：看微课，看课件，交作业。0、1、3。
                getCurrAdapterViewHelper().getData().addAll(Utils.formatHomeworkListData(list));
                getCurrAdapterView().setSelection(position);
                result.getModel().setData(getCurrAdapterViewHelper().getData());
            } else {
                getCurrAdapterViewHelper().setData(Utils.formatHomeworkListData(list));
                homeworkListResult = result;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (CampusPatrolUtils.hasStudyTaskAssigned()) {
            //布置作业完成，需要刷新页面。
            CampusPatrolUtils.setHasStudyTaskAssigned(false);
            refreshData();
        }
        //如果是扫码是任务发送到学习任务成功后刷新数据
        if (isScanTaskFinished) {
            isScanTaskFinished = false;
            refreshData();
        }

        //如果是课件发送到学习任务成功后刷新数据
        if (hasPublishedCourseToStudyTask) {
            hasPublishedCourseToStudyTask = false;
            refreshData();
        }
    }

    private void loadView() {
        if (roleType == RoleType.ROLE_TYPE_PARENT) {
            loadChildInfo();
        }
    }

    private void initViews() {
        if (getArguments() != null) {
            roleType = getArguments().getInt(Constants.EXTRA_ROLE_TYPE);
            schoolId = getArguments().getString(Constants.EXTRA_SCHOOL_ID);
            classId = getArguments().getString(Constants.EXTRA_CLASS_ID);
            isHistory = getArguments().getBoolean(Constants.EXTRA_IS_HISTORY);
            isHeadMaster = getArguments().getBoolean(Constants.EXTRA_IS_HEAD_MASTER);
            isOnlineSchoolClass = getArguments().getBoolean(Constants.EXTRA_IS_ONLINE_SCHOOL_CLASS);
            isApplicationStart = getArguments().getBoolean(ActivityUtils
                    .EXTRA_IS_APPLICATION_START,true);
            reviewInfo = (ReviewInfo) getArguments().getSerializable(ReviewInfo.class.getSimpleName());
            if (reviewInfo != null){
                isFromReviewStatistic = true;
            }
        }
        //左侧返回按钮
        ImageView imageView = (ImageView) findViewById(R.id.header_left_btn);
        if (imageView != null) {
            imageView.setOnClickListener(this);
        }

        //滑动布局
        View slideTabLayout = findViewById(R.id.layout_slide_tab);

        //已完成和未完成的选择布局,仅对教师隐藏。
        if (slideTabLayout != null) {
            if (roleType == RoleType.ROLE_TYPE_TEACHER) {
                slideTabLayout.setVisibility(View.GONE);
            } else {
                slideTabLayout.setVisibility(View.VISIBLE);
            }
        }

        //右侧筛选按钮
        TextView textView = ((TextView) findViewById(R.id.header_right_btn));
        if (textView != null) {
            textView.setOnClickListener(this);
            if (isFromReviewStatistic){
                //隐藏筛选
                textView.setVisibility(View.GONE);
            }
        }

        //标题栏,仅对教师显示。
        View view = findViewById(R.id.layout_header_title);
        if (view != null) {
            if (roleType == RoleType.ROLE_TYPE_TEACHER) {
                view.setVisibility(View.VISIBLE);
            } else {
                view.setVisibility(View.GONE);
            }
        }

        //标题
        textView = (TextView) findViewById(R.id.header_title);
        if (textView != null) {
            if (isFromReviewStatistic){
                textView.setText(getString(R.string.whose_homework,reviewInfo.getStudentName()));
            } else {
                textView.setText(R.string.learning_tasks);
            }
        }
        //未完成
        textView = (TextView) findViewById(R.id.tab_unfinished);
        if (textView != null) {
            textView.setOnClickListener(this);
        }
        this.unfinishedTab = textView;

        //已完成
        textView = (TextView) findViewById(R.id.tab_finished);
        if (textView != null) {
            textView.setOnClickListener(this);
        }
        this.finishedTab = textView;

        //今日作业
        textView = (TextView) findViewById(R.id.tv_today_homework);
        if (textView != null) {
            textView.setOnClickListener(this);
            if (isFromReviewStatistic){
                textView.setVisibility(View.GONE);
            }
        }
        //布置作业
        textView = (TextView) findViewById(R.id.tv_assign_homework);
        View flAssignHomework = findViewById(R.id.fl_assign_homework);
        if (textView != null) {
            if (isFromReviewStatistic) {
                textView.setVisibility(View.GONE);
            } else if (roleType == RoleType.ROLE_TYPE_TEACHER && !isHistory) {
                flAssignHomework.setVisibility(View.VISIBLE);
                textView.setText(R.string.assign_task_line);
            } else {
                if (roleType == RoleType.ROLE_TYPE_STUDENT && !isOnlineSchoolClass) {
                    flAssignHomework.setVisibility(View.VISIBLE);
                    textView.setText(R.string.scan_task);
                } else {
                    flAssignHomework.setVisibility(View.GONE);
                }
            }
            textView.setOnClickListener(this);
        }

        //孩子头像
        bindChildIcon = (ImageView) findViewById(R.id.iv_bind_child_head);
        if (bindChildIcon != null) {
            bindChildIcon.setVisibility(View.VISIBLE);
        }

        //孩子名称
        bindChildName = (TextView) findViewById(R.id.tv_bind_child_name);
        if (bindChildName != null) {
            bindChildName.setVisibility(View.VISIBLE);
        }
        //切换按钮
        switchChild = (TextView) findViewById(R.id.tv_switch_child);
        switchChild.setVisibility(View.GONE);
        switchChild.setOnClickListener(this);

        //家长绑定多个小孩布局,当有多个小孩的时候，显示该布局，并显示切换按钮，当只有一个孩子的时候，不显示切换按钮。
        bindChildLayout = findViewById(R.id.layout_show_bind_child);
        if (bindChildLayout != null) {
            if (roleType == RoleType.ROLE_TYPE_PARENT) {
                bindChildLayout.setVisibility(View.VISIBLE);
                //如果是家长的话，才请求孩子数据。
            } else {
                bindChildLayout.setVisibility(View.GONE);
            }
        }

        //设置默认选中Tab
        this.finishedTab.setEnabled(true);
        this.unfinishedTab.setEnabled(false);
        initListView();
        initViewPager();
    }

    private void initViewPager() {
        viewPager = (MyViewPager) findViewById(R.id.view_pager);
        //默认可以滑动
        viewPager.setCanScroll(true);
        pagerSlidingTabStrip = (PagerSlidingTabStrip) findViewById(R.id.pager_sliding_tab_strip);

        //初始化标题
        titleList.add(getString(R.string.unfinished));
        titleList.add(getString(R.string.finished));

        //初始化fragment
        Fragment fragment = null;

        //学生和家长
        if (roleType == RoleType.ROLE_TYPE_STUDENT || roleType == RoleType.ROLE_TYPE_PARENT) {
            //未完成
            fragment = new UnCompletedHomeworkListTabFragment();
            fragment.setArguments(getHomeworkListBundleInfo());
            fragmentList.add(fragment);
            //未完成赋值
            unCompletedHomeworkListTabFragment = (UnCompletedHomeworkListTabFragment) fragment;

            //已完成
            fragment = new CompletedHomeworkListTabFragment();
            fragment.setArguments(getHomeworkListBundleInfo());
            fragmentList.add(fragment);
            //已完成赋值
            completedHomeworkListTabFragment = (CompletedHomeworkListTabFragment) fragment;
        } else if (roleType == RoleType.ROLE_TYPE_TEACHER) {
            //老师列表
            fragment = new TeacherHomeworkListTabFragment();
            fragment.setArguments(getHomeworkListBundleInfo());
            fragmentList.add(fragment);
        }

        //适配器
        adapter = new MyFragmentPagerTitleAdapter(getChildFragmentManager(), titleList, fragmentList);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);

        viewPager.setOffscreenPageLimit(titleList.size());
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                controlTabByPosition(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        //和viewpager保持联动
        pagerSlidingTabStrip.setViewPager(viewPager);
    }

    private void controlTabByPosition(int position) {
        //学生和家长才有tab可切换。
        if (roleType == RoleType.ROLE_TYPE_STUDENT || roleType == RoleType.ROLE_TYPE_PARENT) {
            switchTabByPosition(position);
        }
    }

    private void switchTabByPosition(int position) {
        if (position == 0) {
            finishStatus = UNFINISHED;
            //未完成
            if (unCompletedHomeworkListTabFragment != null) {
                unCompletedHomeworkListTabFragment.refreshData();
            }
        } else if (position == 1) {
            finishStatus = FINISHED;
            //已完成
            if (completedHomeworkListTabFragment != null) {
                completedHomeworkListTabFragment.refreshData();
            }
        }
    }

    private Bundle getHomeworkListBundleInfo() {
        Bundle args = getArguments();
        //家长要传递孩子的id，学生就传自己的id就行了。
        if (roleType == RoleType.ROLE_TYPE_PARENT) {
            args.putString("childId", studentId);
            //传递孩子Id，多个孩子以逗号分隔。
            args.putString("sortStudentId", sortStudentId);
            UserInfo userInfo = getCurUserInfo();
            if (userInfo != null) {
                args.putSerializable(UserInfo.class.getSimpleName(), userInfo);
            }
            //传递孩子数组
            args.putStringArray(Constants.EXTRA_CHILD_ID_ARRAY, childIdArray);
        } else {
            //其他类型传递自己的userInfo信息
            UserInfo userInfo = getUserInfo();
            if (userInfo != null) {
                args.putSerializable(UserInfo.class.getSimpleName(), userInfo);
            }
        }
        //学生和家长角色必填完成状态
        if (roleType == RoleType.ROLE_TYPE_STUDENT
                || roleType == RoleType.ROLE_TYPE_PARENT) {
            args.putInt("TaskState", finishStatus);
        }
        return args;
    }

    /**
     * 加载家长绑定的孩子信息
     */
    private void loadChildInfo() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("ClassId", classId);
        params.put("MemberId", getMemeberId());

        RequestHelper.sendPostRequest(getActivity(),
                ServerUrl.GET_STUDENT_BY_PARENT_URL, params,
                new DefaultDataListener<HomeworkChildListResult>(
                        HomeworkChildListResult.class) {
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
                        handler.sendMessage(handler.obtainMessage(100, getResult()));
                    }
                });

    }

    /**
     * 绑定孩子
     *
     * @param result
     */
    private void bindChildren(HomeworkChildListResult result) {

        List<StudentMemberInfo> childList = result.getModel().getData();
        //绑定studentId
        sortStudentId = createStudentIdsParam(childList);
        studentMemberInfos = childList;
        if (childList != null && childList.size() > 0) {
            childNameArray = new String[childList.size()];
            imageArray = new String[childList.size()];
            childIdArray = new String[childList.size()];
            for (int i = 0; i < childList.size(); i++) {
                childIdArray[i] = childList.get(i).getMemberId();
                childNameArray[i] = childList.get(i).getRealName();
                imageArray[i] = childList.get(i).getHeadPicUrl();
            }
            //初始化View
            if (switchChild != null) {
                //如果孩子大于1个，则显示“切换”，否则隐藏。
                if (childNameArray != null && childNameArray.length > 1) {
                    switchChild.setVisibility(View.VISIBLE);
                } else {
                    switchChild.setVisibility(View.GONE);
                }
            }
            //默认显示
            getThumbnailManager().displayUserIconWithDefault(AppSettings.getFileUrl
                    (imageArray[position]), bindChildIcon, R.drawable.default_user_icon);
            bindChildName.setText(childNameArray[position]);
            //第一个孩子的Id
            studentId = childIdArray[position];
            //刷新页面
            refreshDataByChildInfo();
        }
    }

    /**
     * 按照获取的孩子id刷新
     */
    private void refreshDataByChildInfo() {
        //传递参数
        Bundle args = new Bundle();
        String childId = "";
        if (roleType == RoleType.ROLE_TYPE_STUDENT) {
            childId = getMemeberId();
        } else if (roleType == RoleType.ROLE_TYPE_PARENT) {
            childId = studentId;
        }
        args.putString("childId", childId);
        //传递孩子Id，多个孩子以逗号分隔。
        args.putString("sortStudentId", sortStudentId);
        UserInfo userInfo = getCurUserInfo();
        if (userInfo != null) {
            args.putSerializable(UserInfo.class.getSimpleName(), userInfo);
        }
        //传递孩子数组
        args.putStringArray(Constants.EXTRA_CHILD_ID_ARRAY, childIdArray);
        //家长、学生角色
        Fragment fragment = null;
        fragment = fragmentList.get(0);
        if (fragment != null && fragment instanceof UnCompletedHomeworkListTabFragment) {
            ((UnCompletedHomeworkListTabFragment) fragment).refreshPageByChildParams(args);
        }
        fragment = fragmentList.get(1);
        if (fragment != null && fragment instanceof CompletedHomeworkListTabFragment) {
            ((CompletedHomeworkListTabFragment) fragment).refreshPageByChildParams(args);
        }
    }

    private String createStudentIdsParam(List<StudentMemberInfo> list) {
        StringBuilder sb = new StringBuilder();
        String result = null;
        if (list == null || list.size() <= 0) {
            result = "";
        } else {
            for (int i = 0; i < list.size(); i++) {
                StudentMemberInfo info = list.get(i);
                if (info != null) {
                    if (i < list.size() - 1) {
                        sb.append(info.getMemberId()).append(",");
                    } else {
                        sb.append(info.getMemberId());
                    }
                }
            }
            result = sb.toString();
        }
        return result;
    }

    private void switchTab() {
        if (fragmentList != null) {
            int count = fragmentList.size();
            if (count > 0) {
                if (count == 1) {
                    //老师角色
                    Fragment fragment = fragmentList.get(0);
                    if (fragment != null && fragment instanceof TeacherHomeworkListTabFragment) {
                        ((TeacherHomeworkListTabFragment) fragment).refreshData();
                    }
                } else if (count == 2) {
                    //家长、学生角色
                    if (finishStatus == UNFINISHED) {
                        //未完成
                        Fragment fragment = fragmentList.get(0);
                        if (fragment != null && fragment instanceof
                                UnCompletedHomeworkListTabFragment) {
                            ((UnCompletedHomeworkListTabFragment) fragment).refreshData();
                        }
                    } else if (finishStatus == FINISHED) {
                        //已完成
                        Fragment fragment = fragmentList.get(1);
                        if (fragment != null && fragment instanceof
                                CompletedHomeworkListTabFragment) {
                            ((CompletedHomeworkListTabFragment) fragment).refreshData();
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.header_left_btn) {
            //返回
            if (!isApplicationStart){
                ActivityUtils.enterHomeActivity(getActivity());
            }
            finish();
        } else if (v.getId() == R.id.tab_finished) {
            //已完成
            this.finishedTab.setEnabled(false);
            this.unfinishedTab.setEnabled(true);
            finishStatus = FINISHED;
            switchTab();
        } else if (v.getId() == R.id.tab_unfinished) {
            //未完成
            this.finishedTab.setEnabled(true);
            this.unfinishedTab.setEnabled(false);
            finishStatus = UNFINISHED;
            switchTab();
        } else if (v.getId() == R.id.header_right_btn) {
            //筛选
            enterHomeworkPickerActivity();
        } else if (v.getId() == R.id.tv_today_homework) {
            //今日作业
            enterTodayHomeworkActivity();
        } else if (v.getId() == R.id.tv_assign_homework) {
            //布置作业
            int haveFree = Utils.checkStorageSpace(getActivity());
            if (haveFree == 0) {
                if (roleType == RoleType.ROLE_TYPE_STUDENT) {
                    enterScanTask();
                } else {
                    showTaskTypeDialog();
                }
            }
        } else if (v.getId() == R.id.tv_switch_child) {
            //切换按钮
            switchChild();
        }
    }

    private void showTaskTypeDialog() {
        StudyTaskUtils.handleSubjectSettingData(getActivity(),getMemeberId(),v -> {
            HandleCheckResourceActivity.start(getActivity(),schoolId,classId,isOnlineSchoolClass,
                    null);
//            ArrangeLearningTasksUtil.getInstance()
//                    .setActivity(getActivity())
//                    .setCallBackListener(new ArrangeLearningTasksUtil.ArrangeLearningTaskListener() {
//                        @Override
//                        public void selectedTypeData(String title, int type) {
//                            ActivityUtils.enterIntroductionCourseActivity(getActivity(), title, type,
//                                    schoolInfo,false,isOnlineSchoolClass,classId,schoolId,null);
//                        }
//                    })
//                    .show();
        });
    }

    private UserInfo getCurUserInfo() {
        UserInfo userInfo = null;
        if (position >= 0) {
            if (studentMemberInfos != null && studentMemberInfos.size() > 0 && position <
                    studentMemberInfos.size()) {
                StudentMemberInfo info = studentMemberInfos.get(position);
                if (info != null) {
                    userInfo = info.getUserInfo();
                }
            }
        }

        return userInfo;
    }

    private void enterTodayHomeworkActivity() {
        Intent intent = new Intent(getActivity(), TodayHomeworkActivity.class);
        intent.putExtra(Constants.EXTRA_ROLE_TYPE, roleType);
        intent.putExtra(Constants.EXTRA_SCHOOL_ID, schoolId);
        intent.putExtra(Constants.EXTRA_CLASS_ID, classId);
        intent.putExtra(Constants.EXTRA_IS_HEAD_MASTER, isHeadMaster);
        //家长要传递孩子的id，学生就传自己的id就行了。
        if (roleType == RoleType.ROLE_TYPE_PARENT) {
            intent.putExtra("childId", studentId);
            //传递孩子Id，多个孩子以逗号分隔。
            intent.putExtra("sortStudentId", sortStudentId);
            UserInfo userInfo = getCurUserInfo();
            if (userInfo != null) {
                intent.putExtra(UserInfo.class.getSimpleName(), userInfo);
            }
            //传递孩子数组
            intent.putExtra(Constants.EXTRA_CHILD_ID_ARRAY, childIdArray);
        }
        //学生和家长角色必填完成状态
        if (roleType == RoleType.ROLE_TYPE_STUDENT || roleType == RoleType.ROLE_TYPE_PARENT) {
            intent.putExtra("TaskState", finishStatus);
        }
        startActivityForResult(intent, CampusPatrolPickerFragment.REQUEST_CODE_HOMEWORK_TODAY_TASK);
    }

    private void switchChild() {
        SelectBindChildPopupView popupView = new SelectBindChildPopupView(getActivity(), position,
                this, childNameArray);
        popupView.showAtLocation(mRootView, Gravity.BOTTOM, 0, 0);
    }

    private void enterHomeworkPickerActivity() {
        Intent intent = new Intent(getActivity(), HomeworkPickerActivity.class);
        intent.putExtra("roleType", roleType);
        intent.putExtra("classId", classId);
        intent.putExtra("schoolId", schoolId);
        intent.putExtra(Constants.EXTRA_IS_HEAD_MASTER, isHeadMaster);
        //家长要传递孩子的id，学生就传自己的id就行了。
        if (roleType == RoleType.ROLE_TYPE_PARENT) {
            intent.putExtra("childId", studentId);
            //传递孩子Id，多个孩子以逗号分隔。
            intent.putExtra("sortStudentId", sortStudentId);
            UserInfo userInfo = getCurUserInfo();
            if (userInfo != null) {
                intent.putExtra(UserInfo.class.getSimpleName(), userInfo);
            }
            //传递孩子数组
            intent.putExtra(Constants.EXTRA_CHILD_ID_ARRAY, childIdArray);
        }
        intent.putExtra(ContactsPickerActivity.EXTRA_PICKER_MODE,
                ContactsPickerActivity.PICKER_MODE_MULTIPLE);
        startActivityForResult(intent, CampusPatrolPickerFragment.REQUEST_CODE_HOMEWORK_PICKER);
    }

    private void enterScanTask() {
        Intent intent = new Intent(getActivity(), CaptureActivity.class);
        intent.putExtra(SlideWawaPageActivity.SCHOOL_ID, schoolId);
        intent.putExtra(SlideWawaPageActivity.CLASS_ID, classId);
        intent.putExtra(SlideWawaPageActivity.IS_SCAN_TASK, true);
        startActivity(intent);
    }

    @Override
    public void onRelationChange(int index, String relationType) {
        if (relationType != null && !relationType.equals("")) {
            //设置孩子的名字
            bindChildName.setText(relationType);
            //设置孩子的头像
            getThumbnailManager().displayUserIconWithDefault(AppSettings.getFileUrl
                    (imageArray[index]), bindChildIcon, R.drawable.default_user_icon);
            //切换孩子的Id
            studentId = childIdArray[index];
            position = index;
            //选择完成之后，需要刷新一下数据。
            //两边都刷一下
            refreshDataByChildInfo();
        }
    }

    /**
     * 更新小红点
     *
     * @param needToUpdateSmallRedPoint
     */
    public void setNeedToUpdateSmallRedPoint(boolean needToUpdateSmallRedPoint) {
        isNeedToUpdateSmallRedPoint = needToUpdateSmallRedPoint;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100 && resultCode == 0) {
            int type = -1;
            if (data != null) {
                type = data.getIntExtra("type", -1);
                if (type == 0) {
                    //未选择任何筛选条件
                } else if (type == 1) {
                    TaskTypes = data.getStringExtra("TaskTypes");
                    if (roleType == RoleType.ROLE_TYPE_STUDENT
                            || roleType == RoleType.ROLE_TYPE_PARENT) {
                        TeacherIds = data.getStringExtra("TeacherIds");
                    }

                    StartTimeBegin = data.getStringExtra("StartTimeBegin");
                    StartTimeEnd = data.getStringExtra("StartTimeEnd");

                    EndTimeBegin = data.getStringExtra("EndTimeBegin");
                    EndTimeEnd = data.getStringExtra("EndTimeEnd");
                }
            }
            loadView();
        } else if (requestCode == REQUEST_CODE_MEDIAPAPER) {
            //布置作业的回调方法
            if (data != null) {
                Bundle args = data.getExtras();
                FragmentTransaction ft = getActivity().getSupportFragmentManager().
                        beginTransaction();
                PublishStudyTaskFragment fragment = new PublishStudyTaskFragment();
                fragment.setArguments(args);
                ft.replace(R.id.activity_body, fragment, PublishStudyTaskFragment.TAG);
                ft.addToBackStack(null);
                ft.commit();
            }
        }

        //更新讨论
        if (data == null) {
            //更新小红点
            if (isNeedToUpdateSmallRedPoint) {
                isNeedToUpdateSmallRedPoint = false;
                //刷新页面
                refreshData();
            } else {
                if (requestCode == CampusPatrolPickerFragment.REQUEST_CODE_DISCUSSION_TOPIC) {
                    //讨论话题
                    if (TopicDiscussionFragment.hasCommented()) {
                        //reset value
                        TopicDiscussionFragment.setHasCommented(false);
                        //需要刷新
                        refreshData();
                    }
                } else if (requestCode == CampusPatrolPickerFragment
                        .REQUEST_CODE_DISCUSSION_COURSE_DETAILS) {
                    //看课件
                    if (PictureBooksDetailFragment.hasCommented()) {
                        //reset value
                        PictureBooksDetailFragment.setHasCommented(false);
                        //需要刷新
                        refreshData();
                    }

                } else if (requestCode == CampusPatrolPickerFragment
                        .EDIT_NOTE_DETAILS_REQUEST_CODE) {
                    //看作业，编辑帖子。
                    if (OnlineMediaPaperActivity.hasContentChanged()) {
                        //reset value
                        OnlineMediaPaperActivity.setHasContentChanged(false);
                        //需要刷新
                        refreshData();
                    }

                } else if (requestCode == CampusPatrolPickerFragment
                        .REQUEST_CODE_HOMEWORK_COMMIT) {
                    //从提交作业列表页面返回，是否要刷新页面。
                    if (HomeworkCommitFragment.hasCommented()) {
                        //reset value
                        HomeworkCommitFragment.setHasCommented(false);
                        //需要刷新
                        refreshData();
                    }
                } else if (requestCode == CampusPatrolPickerFragment
                        .REQUEST_CODE_HOMEWORK_TODAY_TASK) {
                    //从今日任务作业列表页面返回，是否要刷新页面。
                    if (TodayHomeworkListFragment.hasCommented()) {
                        //reset value
                        TodayHomeworkListFragment.setHasCommented(false);
                        //需要刷新
                        refreshData();
                    }
                } else if (requestCode == CampusPatrolPickerFragment
                        .REQUEST_CODE_HOMEWORK_PICKER) {
                    //从筛选作业页面返回，是否要刷新页面。
                    if (HomeworkPickerFragment.hasCommented()) {
                        //reset value
                        HomeworkPickerFragment.setHasCommented(false);
                        //需要刷新
                        refreshData();
                    }
                } else if (requestCode == CampusPatrolPickerFragment
                        .REQUEST_CODE_ENGLISH_WRITING_COMMIT) {
                    //从英文写作提交作业列表页面返回，是否要刷新页面。
                    if (EnglishWritingCommitFragment.hasCommented()) {
                        //reset value
                        EnglishWritingCommitFragment.setHasCommented(false);
                        //需要刷新
                        switchTab();
                    }
                    if (EnglishWritingBuildFragment.hasCommented()) {
                        EnglishWritingBuildFragment.setHasCommented(false);
                        switchTab();
                    }
                }
            }
        }
    }

    /**
     * 加载当前学校的信息
     */
    private void loadSchools() {
        Map<String, Object> params = new HashMap();
        if (getUserInfo() != null) {
            params.put("MemberId", getUserInfo().getMemberId());
        }
        //拉取已加入的机构
        RequestHelper.sendPostRequest(getActivity(),
                ServerUrl.GET_JOIN_SCHOOL_LIST_URL, params,
                new DefaultDataListener<AddedSchoolInfoListResult>(
                        AddedSchoolInfoListResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        AddedSchoolInfoListResult result = getResult();
                        if (result == null || !result.isSuccess() || result.getModel() == null) {
                            return;
                        } else {
                            List<SchoolInfo> list = result.getModel().getData();
                            if (list != null && list.size() > 0) {
                                schoolInfo = list.get(0);
                            }
                        }
                    }
                });
    }
}
