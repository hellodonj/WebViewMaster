package com.galaxyschool.app.wawaschool.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.duowan.mobile.netroid.NetroidError;
import com.galaxyschool.app.wawaschool.Note.OnlineMediaPaperActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.SchoolSpaceActivity;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.CampusPatrolUtils;
import com.galaxyschool.app.wawaschool.common.CourseOpenUtils;
import com.galaxyschool.app.wawaschool.common.StudyTaskUtils;
import com.galaxyschool.app.wawaschool.common.Utils;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.galaxyschool.app.wawaschool.fragment.resource.HomeworkResourceAdapterViewHelper;
import com.lqwawa.intleducation.base.utils.ToastUtil;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.ClassDataStaticsInfo;
import com.galaxyschool.app.wawaschool.pojo.Emcee;
import com.galaxyschool.app.wawaschool.pojo.EmceeList;
import com.galaxyschool.app.wawaschool.pojo.HomeworkListInfo;
import com.galaxyschool.app.wawaschool.pojo.HomeworkListResult;
import com.galaxyschool.app.wawaschool.pojo.RoleType;
import com.galaxyschool.app.wawaschool.pojo.SchoolInfo;
import com.galaxyschool.app.wawaschool.pojo.TeacherDataStaticsInfo;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.galaxyschool.app.wawaschool.views.ContactsMessageDialog;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;
import com.galaxyschool.app.wawaschool.views.slidelistview.SlideListView;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 筛选结果页面
 */

public class ScreeningHomeworkResultListFragment extends ContactsListFragment{

    public static final String TAG = ScreeningHomeworkResultListFragment.class.getSimpleName();

    private HomeworkListResult homeworkListResult;
    private int roleType;
    private String classId;
    private String schoolId;
    private String childId;
    //筛选条件
    private String StartTimeBegin="";
    private String StartTimeEnd="";
    private String EndTimeBegin="";
    private String EndTimeEnd="";
    private String TaskTypes="";
    private String TeacherIds="";
    private int TaskState=-1;
    private boolean isHeadMaster;
    private String sortStudentId;

    //校园巡查
    private boolean isCampusPatrolTag;
    private String resourceName,resourceCountStr;
    private String startDate,endDate;
    private TeacherDataStaticsInfo teacherDataStaticsInfo;
    private ClassDataStaticsInfo classDataStaticsInfo;
    private String campusPatrolTaskType;
    private int campusPatrolSearchType;
    private SchoolInfo schoolInfo;
    private static boolean hasCommented;
    private boolean isNeedToUpdateSmallRedPoint;
    private String[] childIdArray;//孩子Id数组
    private UserInfo userInfo;

    public interface Constants {
        String EXTRA_SCHOOL_ID = "schoolId";
        String EXTRA_CLASS_ID = "classId";
        String EXTRA_CHANNEL_TYPE = "channelType";
        String EXTRA_IS_TEACHER = "isTeacher";
        String EXTRA_ROLE_TYPE = "roleType";
        String EXTRA_IS_HEAD_MASTER = "isHeadMaster";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.today_homework, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
        refreshData();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void refreshData(){
        getPageHelper().clear();
        loadViews();
    }

    void initViews() {

        if (getArguments()!=null){
            userInfo = (UserInfo) getArguments().getSerializable(UserInfo.class.getSimpleName());
            roleType=getArguments().getInt(Constants.EXTRA_ROLE_TYPE);
            classId=getArguments().getString(Constants.EXTRA_CLASS_ID);
            schoolId=getArguments().getString(Constants.EXTRA_SCHOOL_ID);
            isHeadMaster = getArguments().getBoolean(Constants.EXTRA_IS_HEAD_MASTER);
            if (roleType==RoleType.ROLE_TYPE_PARENT) {
                childId = getArguments().getString("childId");
                sortStudentId = getArguments().getString("sortStudentId");
                //得到孩子数组
                childIdArray = (String[]) getArguments().get(HomeworkMainFragment.Constants
                        .EXTRA_CHILD_ID_ARRAY);
            }
                int type=-1;
                type = getArguments().getInt("type", -1);
                if (type == 0) {
                    //未选择任何筛选条件
                } else if (type == 1) {
                    TaskTypes = getArguments().getString("TaskTypes");
                    //现在班主任也可以筛选老师了
                    //放开老师筛选老师
                    if (roleType == RoleType.ROLE_TYPE_STUDENT ||
                            roleType == RoleType.ROLE_TYPE_PARENT
                            || roleType == RoleType.ROLE_TYPE_TEACHER) {
                        TeacherIds = getArguments().getString("TeacherIds");
                        //完成状态
                        TaskState=getArguments().getInt("TaskState",-1);
                    }

                    StartTimeBegin = getArguments().getString("StartTimeBegin");
                    StartTimeEnd = getArguments().getString("StartTimeEnd");

                    EndTimeBegin = getArguments().getString("EndTimeBegin");
                    EndTimeEnd = getArguments().getString("EndTimeEnd");
            }
            //校园巡查
            isCampusPatrolTag = getArguments().getBoolean(CampusPatrolMainFragment
                    .IS_CAMPUS_PATROL_TAG);
            resourceName = getArguments().getString(CampusPatrolMainFragment.
            CAMPUS_PATROL_RESOURCE_NAME);
            resourceCountStr = getArguments().getString(CampusPatrolMainFragment
                    .CAMPUS_PATROL_RESOURCE_COUNT_STR);
            teacherDataStaticsInfo = (TeacherDataStaticsInfo) getArguments().
                    getSerializable(TeacherDataStaticsInfo.class.getSimpleName());
            classDataStaticsInfo = (ClassDataStaticsInfo) getArguments().
                    getSerializable(ClassDataStaticsInfo.class.getSimpleName());
            startDate = getArguments().getString(CampusPatrolMainFragment.
                    CAMPUS_PATROL_SCREENING_START_DATE);
            endDate = getArguments().getString(CampusPatrolMainFragment.
                    CAMPUS_PATROL_SCREENING_END_DATE);
            campusPatrolTaskType = getArguments().getString(CampusPatrolMainFragment.
            CAMPUS_PATROL_TASK_TYPE);
            campusPatrolSearchType = getArguments().getInt(CampusPatrolMainFragment.
            CAMPUS_PATROL_SEARCH_TYPE);
            schoolInfo = (SchoolInfo) getArguments().getSerializable(
                    SchoolInfo.class.getSimpleName());
        }

        //头布局
        View view = findViewById(R.id.contacts_header_layout);

        updateTitleView(resourceCountStr);

        TextView textView = ((TextView) findViewById(R.id.contacts_header_right_btn));
        if (textView != null) {
            if (!isCampusPatrolTag) {
                textView.setVisibility(View.INVISIBLE);
            }else {
                //校园巡查逻辑
                textView.setVisibility(View.VISIBLE);
                textView.setText(getString(R.string.screening));
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ActivityUtils.enterCampusPatrolPickerActivity(getActivity());
                    }
                });
            }
        }

        PullToRefreshView pullToRefreshView = (PullToRefreshView) findViewById(
                R.id.contacts_pull_to_refresh);
        setPullToRefreshView(pullToRefreshView);

        SlideListView listView = (SlideListView) findViewById(R.id.contacts_list_view);
        if (listView != null) {
            listView.setSlideMode(SlideListView.SlideMode.NONE);

            //作业通用列表
            AdapterViewHelper listViewHelper = new HomeworkResourceAdapterViewHelper(getActivity(),
                    listView,roleType,getMemeberId(),isHeadMaster) {
                @Override
                public void loadData() {
                    loadViews();
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
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
                    //小红点
                    ImageView imageView = (ImageView) view.findViewById(R.id.red_point);
                    if (imageView != null) {
                        //校园巡查需要隐藏小红点
                        if (isCampusPatrolTag){
                            imageView.setVisibility(View.INVISIBLE);
                        }
                    }

                    //删除
                    View deleteView = view.findViewById(R.id.layout_delete_homework);
                    if (deleteView != null) {
                        if (isCampusPatrolTag){
                            //校园巡查隐藏删除按钮
                            deleteView.setVisibility(View.INVISIBLE);
                        }
                        deleteView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //删除条目
                                showDeleteDialog(data);
                            }
                        });
                    }

                    //按时间作答题的任务
                    RelativeLayout rlLocking = (RelativeLayout) view.findViewById(R.id.rl_locking);
                    if (roleType == RoleType.ROLE_TYPE_TEACHER) {
                        rlLocking.setVisibility(View.GONE);
                    } else if (data.getSubmitType() == 1 && !TextUtils.isEmpty(data.getServerNowTime())){
                        boolean arriveDoTime = StudyTaskUtils.compareStudyTaskTime(data
                                .getServerNowTime(),data.getStartTime(),true);
                        if (arriveDoTime){
                            rlLocking.setVisibility(View.GONE);
                        } else {
                            rlLocking.setVisibility(View.VISIBLE);
                            rlLocking.setOnClickListener(v -> {
                                ToastUtil.showToast(getActivity(),R.string.str_not_yet_time_not_answer);
                            });
                        }
                    } else {
                        rlLocking.setVisibility(View.GONE);
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
                    //不是来自空中课堂和任务的创建者是自己
                    if (data.getAirClassId() == 0){
                        //打开学习任务列表
                        CourseOpenUtils.openStudyTask(getActivity(),data,roleType,isHeadMaster,
                                getMemeberId(),sortStudentId,childId,userInfo,isCampusPatrolTag);
                    }else {
                        accordingAirClassIdAnalysisData(data);
                    }

                    super.onItemClick(parent,view,position,id);
                }

                /**
                 * 更新话题讨论和提交作业已读
                 * @param taskId
                 * @param memberId
                 * @param taskType
                 */
                @Override
                protected void UpdateStudentIsRead(String taskId, String memberId, String taskType) {
                    //校园巡查不能操作其他，只能查看。
                    if (!isCampusPatrolTag) {
                        updateStudentReadState(taskId, memberId, taskType);
                    }
                }

                /**
                 * 更新看微课、看课件、看作业已读
                 * @param taskId
                 * @param memberId
                 */
                @Override
                protected void updateReadStatus(String taskId, String memberId) {
                    //校园巡查不能操作其他，只能查看。
                    if (!isCampusPatrolTag) {
                        updateReadState(taskId, memberId);
                    }

                }
            };

            setCurrAdapterViewHelper(listView, listViewHelper);
        }
    }

    private void updateTitleView(String countStr){
        TextView textView = (TextView) findViewById(R.id.contacts_header_title);
        if (textView != null) {
            if (!isCampusPatrolTag) {
                textView.setText(R.string.homework_screening_result);
            }else {
                if (campusPatrolSearchType == CampusPatrolMainFragment.
                        CAMPUS_PATROL_SEARCH_TYPE_TEACHER) {
                    //老师查看单个任务，需要显示单个条目。
                    textView.setText(resourceName+ getString(R.string
                            .media_num, countStr));
                }else if (campusPatrolSearchType == CampusPatrolMainFragment
                .CAMPUS_PATROL_SEARCH_TYPE_CLASS){
                    //班级查看全部，只显示“学习任务”即可。
                    textView.setText(getString(R.string.learning_tasks) + getString(R.string
                            .media_num, countStr));
                }
            }
        }
    }

    /**
     * 更新话题讨论和交作业未读状态
     * @param taskId
     * @param memberId
     * @param taskType
     */
    private void updateStudentReadState(final String taskId, String memberId, String taskType) {

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("TaskId",taskId);
        if (roleType == RoleType.ROLE_TYPE_PARENT && !TextUtils.isEmpty(childId)){
            params.put("StudentId", childId);
        } else {
            params.put("StudentId", memberId);
        }
        params.put("TaskType",taskType);
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
                            isNeedToUpdateSmallRedPoint = true;
                            if (getCurrAdapterViewHelper().hasData()){
                                List<HomeworkListInfo> list=getCurrAdapterViewHelper().getData();
                                for (HomeworkListInfo task: list){
                                    if (task.getTaskId().equals(taskId)){
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
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.UPDATE_STUDENT_IS_READ_URL,
                params, listener);

    }

    /**
     * 更新阅读状态
     * @param taskId
     * @param memberId
     */
    private void updateReadState(final String taskId, String memberId) {

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("TaskId",taskId);
        if (roleType == RoleType.ROLE_TYPE_PARENT && !TextUtils.isEmpty(childId)){
            params.put("StudentId", childId);
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
                            isNeedToUpdateSmallRedPoint = true;
                            if (getCurrAdapterViewHelper().hasData()){
                                List<HomeworkListInfo> list=getCurrAdapterViewHelper().getData();
                                for (HomeworkListInfo task: list){
                                    if (task.getTaskId().equals(taskId)){
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
     * 根据直播的id拉取详情页数据分析权限问题
     */
    private void accordingAirClassIdAnalysisData(final HomeworkListInfo data){
        Map<String, Object> params = new HashMap<>();
        params.put("Id",data.getAirClassId());
        DefaultDataListener listener = new DefaultDataListener<DataModelResult>(
                DataModelResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                if (getActivity() == null) {
                    return;
                }
                super.onSuccess(jsonString);
                DataModelResult result = getResult();
                if (result == null || !result.isSuccess()
                        || result.getModel() == null) {
                    return;
                }
                try {
                    JSONObject jsonObject = new JSONObject(jsonString);
                    JSONObject model = jsonObject.optJSONObject("Model");
                    Emcee emcee = com.alibaba.fastjson.JSONObject.parseObject(model.toString(),
                            Emcee.class);
                    analysisCurrentUserIsReporter(data,emcee);
                }catch (Exception e){
                    e.printStackTrace();
                }


            }

            @Override
            public void onError(NetroidError error) {
                super.onError(error);
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        };
        listener.setShowLoading(true);
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.GET_AIRCLASS_DETAIL_BY_ID_BASE_URL,
                params, listener);
    }

    /**
     * 分析当前的用户是不是小编的身份
     * @param model
     */
    private void analysisCurrentUserIsReporter(HomeworkListInfo data,Emcee model){
        if (model != null) {
            List<EmceeList> reporters = model.getEmceeList();
            boolean flag = false;
            for (int i = 0; i < reporters.size(); i++) {
                EmceeList emceeMember = reporters.get(i);
                String classIds = emceeMember.getClassIds();
                String schoolIds = emceeMember.getSchoolIds();
                if (TextUtils.equals(getMemeberId(), emceeMember.getMemberId())
                        && !TextUtils.isEmpty(classIds)) {
                    EmceeList tempData = null;
                    if (classIds.contains(",")) {
                        String[] splitClassArray = classIds.split(",");
                        String[] splitSchoolArray = schoolIds.split(",");
                        for (int j = 0; j < splitClassArray.length; j++) {
                            tempData = new EmceeList();
                            tempData.setMemberId(emceeMember.getMemberId());
                            tempData.setSchoolIds(splitSchoolArray[j]);
                            tempData.setClassIds(splitClassArray[j]);
                            if (TextUtils.equals(classId, splitClassArray[j])) {
                                flag = true;
                                break;
                            }
                        }
                    } else {
                        if (TextUtils.equals(classId, classIds)) {
                            flag = true;
                            break;
                        }
                    }
                }
            }
            //如果flag为true表示是小编的身份
            if (flag) {
                //小编当中判断是不是主编
                if (TextUtils.equals(getMemeberId(), model.getAcCreateId())) {
                    data.setOnlineHost(true);
                } else {
                    data.setOnlineReporter(true);
                }
            }
        }
        CourseOpenUtils.openStudyTask(getActivity(), data, roleType, isHeadMaster,
                getMemeberId(), sortStudentId, childId, userInfo, isCampusPatrolTag);
    }


    /**
     * 删除框Dialog
     * @param data
     */
    private void showDeleteDialog(final HomeworkListInfo data) {
        ContactsMessageDialog messageDialog = new ContactsMessageDialog(getActivity(), null,
                getString(R.string.want_to_delete_sb,data.getTaskTitle()),getString(R.string.cancel) ,
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

//        Window window = messageDialog.getWindow();
//        window.setGravity(Gravity.CENTER);
//        messageDialog.show();
//        WindowManager windowManager = getActivity().getWindowManager();
//        Display display = windowManager.getDefaultDisplay();
//        WindowManager.LayoutParams lp = window.getAttributes();
//        lp.width = (int)(display.getWidth());
//        window.setAttributes(lp);

    }

    /**
     *删除条目
     * @param data
     */
    private void deleteItem(final HomeworkListInfo data) {

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("TaskCreateId",data.getTaskCreateId());
        params.put("TaskId",data.getTaskId());
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
                            //通知之前的页面，需要刷新
                            setHasCommented(true);
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

    private void loadViews() {
        if (!isCampusPatrolTag) {
            loadCommonData();
        }else {
            //校园巡查
            loadCampusPatrolMaterialData();
        }
    }

    private void loadCampusPatrolMaterialData() {

        if (schoolInfo == null){
            return;
        }
        Map<String, Object> params = new HashMap();
        //学校Id，必填
        params.put("SchoolId",schoolInfo.getSchoolId());
        //必填 3-老师,4-班级
        params.put("Type",campusPatrolSearchType);
        //非必填
        if (campusPatrolSearchType == CampusPatrolMainFragment.
                CAMPUS_PATROL_SEARCH_TYPE_TEACHER){
            //老师统计时必填
            params.put("TeacherId",teacherDataStaticsInfo.getTeacherId());
        }else if (campusPatrolSearchType == CampusPatrolMainFragment.
        CAMPUS_PATROL_SEARCH_TYPE_CLASS){
            //班级统计时必填
            params.put("ClassId",classDataStaticsInfo.getClassId());
        }
        //0-看微课,1-看课件,2看作业,3-交作业,4-讨论话题,5-复述微课,支持多选，多个类型以逗号分隔。
        params.put("TaskTypes",campusPatrolTaskType);
        //完成作业开始，非必填 格式:"yyyy-MM-dd"
        if (!TextUtils.isEmpty(startDate)) {
            params.put("StrStartTime",startDate);
        }
        //完成作业结束，非必填 格式:"yyyy-MM-dd"
        if (!TextUtils.isEmpty(endDate)) {
            params.put("StrEndTime",endDate);
        }
        //分页信息，必填
        params.put("Pager",getPageHelper().getFetchingPagerArgs());

        RequestHelper.sendPostRequest(getActivity(),
                ServerUrl.GET_ST_STATICS_LIST_LIST_URL, params,
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

    /**
     * 模拟数据
     */
    private void loadCommonData() {
        Map<String, Object> params = new HashMap();
        //学校Id，必填
        params.put("SchoolId",schoolId);
        //班级Id，必填
        params.put("ClassId",classId);
        //角色信息，必填，0-学生,1-家长，2-老师
        int role= Utils.transferRoleType(roleType);
        if (role!=-1){
            params.put("RoleType",role);
        }
        //任务状态(0-未完成,1-已完成),学生，家长角色时必填。
        if (roleType==RoleType.ROLE_TYPE_STUDENT||roleType==RoleType.ROLE_TYPE_PARENT){
            if (TaskState!=-1) {
                params.put("TaskState", TaskState);
            }
        }
        //学生ID，非必填，学生、家长角色时必填
        if (roleType==RoleType.ROLE_TYPE_STUDENT) {
            params.put("StudentId", getMemeberId());
        }else if (roleType==RoleType.ROLE_TYPE_PARENT){
            //家长的话，必须要加载绑定的孩子。
            if (childId!=null) {
                params.put("StudentId", childId);
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
            params.put("StartTimeBegin",StartTimeBegin);
        }
        //布置作业结束，非必填，所有角色筛选时使用，格式:"yyyy-MM-dd"
        if (!TextUtils.isEmpty(StartTimeEnd)) {
            params.put("StartTimeEnd",StartTimeEnd);
        }
        //完成作业开始，非必填，所有角色筛选时使用，格式:"yyyy-MM-dd"
        if (!TextUtils.isEmpty(EndTimeBegin)) {
            params.put("EndTimeBegin",EndTimeBegin);
        }
        //完成作业结束，非必填，所有角色筛选时使用，格式:"yyyy-MM-dd"
        if (!TextUtils.isEmpty(EndTimeEnd)) {
            params.put("EndTimeEnd",EndTimeEnd);
        }
        //任务类型，非必填，所有角色筛选时使用，多个任务时，用逗号进行分隔（1,2,3）,0-看微课,1-看课件,2看作业
        // ,3-交作业,4-讨论话题
        if (!TextUtils.isEmpty(TaskTypes)) {
            params.put("TaskTypes",TaskTypes);
        }
        //查询时间查询的老师的id集合,非必填,学生、家长角色筛选时使用，多个id直接用逗号进行分割（,）
        //目前班主任也可以
        //放开老师
        if (roleType==RoleType.ROLE_TYPE_STUDENT||roleType==RoleType.ROLE_TYPE_PARENT
                || roleType == RoleType.ROLE_TYPE_TEACHER) {
            if (!TextUtils.isEmpty(TeacherIds)) {
                params.put("TeacherIds",TeacherIds);
            }
        }
        //分页信息
        params.put("Pager",getPageHelper().getFetchingPagerArgs());
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
            int totalCount = getPageHelper().getTotalCount();
            List<HomeworkListInfo> list = result.getModel().getData();
            if (list == null || list.size() <= 0) {
                if (getPageHelper().isFetchingFirstPage()) {
                    getCurrAdapterViewHelper().clearData();
                    updateTitleView(String.valueOf(totalCount));
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
                getCurrAdapterViewHelper().getData().addAll(Utils.formatHomeworkListData(list));
//                getCurrAdapterViewHelper().getData().addAll(list);
                getCurrAdapterView().setSelection(position);
                result.getModel().setData(getCurrAdapterViewHelper().getData());
            } else {
                getCurrAdapterViewHelper().setData(Utils.formatHomeworkListData(list));
//                getCurrAdapterViewHelper().setData(list);
                homeworkListResult = result;
            }

            updateTitleView(String.valueOf(totalCount));
        }

    }

    private void enterSchoolSpace(SchoolInfo data) {
        Bundle args = new Bundle();
        args.putString(SchoolSpaceActivity.EXTRA_SCHOOL_ID, data.getSchoolId());
        args.putString(SchoolSpaceActivity.EXTRA_SCHOOL_NAME, data.getSchoolName());
        Intent intent = new Intent(getActivity(), SchoolSpaceActivity.class);
        intent.putExtras(args);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
    }

    public static void setHasCommented(boolean hasCommented) {
        ScreeningHomeworkResultListFragment.hasCommented = hasCommented;
    }

    public static boolean hasCommented() {
        return hasCommented;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (data != null){
            if (resultCode == CampusPatrolPickerFragment.RESULT_CODE){
                if (requestCode == CampusPatrolPickerFragment.REQUEST_CODE){
                    this.startDate = CampusPatrolUtils.getStartDate(data);
                    this.endDate = CampusPatrolUtils.getEndDate(data);
                    refreshData();
                }
            }
        }else {
            if (isNeedToUpdateSmallRedPoint){
                isNeedToUpdateSmallRedPoint = false;
                //通知之前的页面，需要刷新。
                setHasCommented(true);
                //刷新
                refreshData();
            }else {
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
                    //看课件
                    if (PictureBooksDetailFragment.hasCommented()) {
                        //通知之前的页面，需要刷新。
                        setHasCommented(true);
                        //reset value
                        PictureBooksDetailFragment.setHasCommented(false);
                        //需要刷新
                        refreshData();
                    }

                } else if (requestCode == CampusPatrolPickerFragment
                        .EDIT_NOTE_DETAILS_REQUEST_CODE) {
                    //看作业，编辑帖子。
                    if (OnlineMediaPaperActivity.hasContentChanged()) {
                        //通知之前的页面，需要刷新。
                        setHasCommented(true);
                        //reset value
                        OnlineMediaPaperActivity.setHasContentChanged(false);
                        //需要刷新
                        refreshData();
                    }

                } else if (requestCode == CampusPatrolPickerFragment
                        .REQUEST_CODE_HOMEWORK_COMMIT) {
                    //从提交作业列表页面返回，是否要刷新页面。
                    if (HomeworkCommitFragment.hasCommented()) {
                        //通知之前的页面，需要刷新。
                        setHasCommented(true);
                        //reset value
                        HomeworkCommitFragment.setHasCommented(false);
                        //需要刷新
                        refreshData();
                    }

                } else if (requestCode == CampusPatrolPickerFragment
                        .REQUEST_CODE_DISCUSSION_INTRODUCTION) {
                    //导读
                    if (SelectedReadingDetailFragment.hasCommented()) {
                        //通知之前的页面，需要刷新。
                        setHasCommented(true);
                        //reset value
                        SelectedReadingDetailFragment.setHasCommented(false);
                        //需要刷新
                        refreshData();
                    }

                }else if (requestCode == CampusPatrolPickerFragment
                        .REQUEST_CODE_ENGLISH_WRITING_COMMIT) {
                    //从英文写作提交作业列表页面返回，是否要刷新页面。
                    if (EnglishWritingCommitFragment.hasCommented()) {
                        //通知之前的页面，需要刷新。
                        setHasCommented(true);
                        //reset value
                        EnglishWritingCommitFragment.setHasCommented(false);
                        //需要刷新
                        refreshData();
                    }
                }
            }
        }
    }
}
