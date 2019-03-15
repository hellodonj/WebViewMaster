package com.galaxyschool.app.wawaschool.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.AirClassroomActivity;
import com.galaxyschool.app.wawaschool.IntroductionForReadCourseActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.ArrangeLearningTasksUtil;
import com.galaxyschool.app.wawaschool.common.CampusPatrolUtils;
import com.galaxyschool.app.wawaschool.common.CourseOpenUtils;
import com.galaxyschool.app.wawaschool.common.DateUtils;
import com.galaxyschool.app.wawaschool.common.ScreenUtils;
import com.galaxyschool.app.wawaschool.common.StudyTaskUtils;
import com.galaxyschool.app.wawaschool.common.Utils;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.DataAdapter;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.galaxyschool.app.wawaschool.fragment.resource.HomeworkResourceAdapterViewHelper;
import com.lqwawa.intleducation.base.utils.ToastUtil;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.Emcee;
import com.galaxyschool.app.wawaschool.pojo.EmceeList;
import com.galaxyschool.app.wawaschool.pojo.HomeworkChildListResult;
import com.galaxyschool.app.wawaschool.pojo.HomeworkListInfo;
import com.galaxyschool.app.wawaschool.pojo.HomeworkListResult;
import com.galaxyschool.app.wawaschool.pojo.PublishClass;
import com.galaxyschool.app.wawaschool.pojo.RoleType;
import com.galaxyschool.app.wawaschool.pojo.ShortSchoolClassInfo;
import com.galaxyschool.app.wawaschool.pojo.StudentMemberInfo;
import com.galaxyschool.app.wawaschool.pojo.StudyTaskType;
import com.galaxyschool.app.wawaschool.pojo.SubscribeClassInfo;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.galaxyschool.app.wawaschool.pojo.UserInfoResult;
import com.galaxyschool.app.wawaschool.views.ContactsGridDialog;
import com.galaxyschool.app.wawaschool.views.ContactsMessageDialog;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;
import com.galaxyschool.app.wawaschool.views.SelectBindChildPopupView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 空中课堂课前预习和课后练习
 */

public class AirClassStudyPracticeFragment extends ContactsListFragment implements
        SelectBindChildPopupView.OnRelationChangeListener {

    public static final String TAG = AirClassStudyPracticeFragment.class.getSimpleName();

    private TextView switchChild, bindChildName, assignHomeBtn;
    private View bindChildLayout;
    private HomeworkListResult homeworkListResult;
    private ImageView bindChildIcon;
    private int roleType;//角色信息
    private String classId;
    private String schoolId;
    private boolean isHeadMaster;
    private boolean isTeacher;
    private String sortStudentId;
    private UserInfo userInfo;
    private String[] imageArray;//家长身份孩子图像
    private String[] childNameArray;//家长身份孩子名字
    private String[] childIdArray;//家长身份孩子的studentId
    private String studentId = null;
    private int position = 0;
    private List<StudentMemberInfo> studentMemberInfos;
    private int currentStudyType = 1;
    private Emcee onlineRes;
    private SubscribeClassInfo currentClassInfo;
    private boolean isNeedToUpdateSmallRedPoint;
    //来自我的直播
    private boolean isMyLive;
    private boolean isOnlineClass;
    private boolean isHistoryClass;
    private boolean hasData;
    private boolean isFirstIn = true;


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
            refreshData();
        }
    }

    public interface Constants {
        String EXTRA_SCHOOL_ID = "schoolId";
        String EXTRA_CLASS_ID = "classId";
        String EXTRA_CHANNEL_TYPE = "channelType";
        String EXTRA_IS_TEACHER = "isTeacher";
        String EXTRA_ROLE_TYPE = "role_type";
        String EXTRA_IS_HEAD_MASTER = "isHeadMaster";
    }

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

    public AirClassStudyPracticeFragment() {

    }

    public void setCurrentStudyType(int studyType){
        this.currentStudyType = studyType;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_airclass_study_practice, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
        initData();
    }

    public void refreshData() {
        getPageHelper().clear();
        loadViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        //根据返回的条件相应的刷新数据
        if (getUserVisibleHint()) {
            accordingConditionRefresh();
        }
    }

    private void initViews() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            onlineRes = (Emcee) bundle.getSerializable(AirClassroomDetailFragment.Contants.EMECCBEAN);
            roleType = bundle.getInt(Constants.EXTRA_ROLE_TYPE);
            classId = bundle.getString(Constants.EXTRA_CLASS_ID);
            schoolId = bundle.getString(Constants.EXTRA_SCHOOL_ID);
            isHeadMaster = bundle.getBoolean(Constants.EXTRA_IS_HEAD_MASTER);
            isTeacher = bundle.getBoolean(Constants.EXTRA_IS_TEACHER, false);
            currentClassInfo = (SubscribeClassInfo) bundle.getSerializable(AirClassroomActivity.ExTRA_CLASS_INFO);
            if (currentClassInfo == null){
                getCurrentClassInfo();
            }
            isOnlineClass = bundle.getBoolean(ActivityUtils.EXTRA_IS_ONLINE_CLASS);
            isMyLive = bundle.getBoolean("isAirClassRoomLive");
            isHistoryClass = bundle.getBoolean(ActivityUtils.EXTRA_IS_HISTORY_CLASS,false);
            //来自我的直播 家长身份
            if (isMyLive && roleType == RoleType.ROLE_TYPE_PARENT){
                String passMemberId = bundle.getString("memberId");
                studentId = passMemberId;
                sortStudentId = passMemberId;
                if (!TextUtils.isEmpty(passMemberId)) {
                    loadUserInfoDetail(passMemberId);
                }
            }
        }
        userInfo = getUserInfo();
        //布置的按钮
        assignHomeBtn = (TextView) findViewById(R.id.tv_assign_homework);
        if (assignHomeBtn != null) {
            if (isReporter() && !isHistoryClass) {
                assignHomeBtn.setVisibility(View.VISIBLE);
            } else {
                assignHomeBtn.setVisibility(View.GONE);
            }
            assignHomeBtn.setOnClickListener(this);
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

        //家长绑定多个小孩布局,当有多个小孩的时候，显示该布局，并显示切换按钮，当只有一个孩子的时候，不显示切换按钮。
        bindChildLayout = findViewById(R.id.layout_show_bind_child);
        if (bindChildLayout != null) {
            if (!(isTeacher || isHeadMaster) && roleType == RoleType.ROLE_TYPE_PARENT && !isMyLive) {
                bindChildLayout.setVisibility(View.VISIBLE);
                //如果是家长的话，才请求孩子数据。
            } else {
                //给角色重新赋值
                if (isTeacher || isHeadMaster) {
                    roleType = RoleType.ROLE_TYPE_TEACHER;
                }
                bindChildLayout.setVisibility(View.GONE);
            }
        }
        //切换按钮
        switchChild = (TextView) findViewById(R.id.tv_switch_child);
        switchChild.setVisibility(View.GONE);
        switchChild.setOnClickListener(this);

        //如果双重角色优先老师
        if (!(isTeacher || isHeadMaster) && roleType == RoleType.ROLE_TYPE_PARENT && !isMyLive) {
            loadChildInfo();
        } else {
            refreshData();
        }
    }

    private void initData() {
        PullToRefreshView pullToRefreshView = (PullToRefreshView) findViewById(
                R.id.contacts_pull_to_refresh);
        setPullToRefreshView(pullToRefreshView);

        GridView listView = (GridView) findViewById(R.id.contacts_list_view);
        if (listView != null) {
            listView.setNumColumns(1);
            //作业通用列表
            AdapterViewHelper listViewHelper = new HomeworkResourceAdapterViewHelper(getActivity(),
                    listView, roleType, getMemeberId(), isHeadMaster) {
                @Override
                public void loadData() {
                    loadCommonData();
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

                    //删除
                    View deleteView = view.findViewById(R.id.layout_delete_homework);
                    if (deleteView != null) {
                        if (isHistoryClass){
                            deleteView.setVisibility(View.INVISIBLE);
                        } else {
                            if (isHeadMaster) {
                                deleteView.setVisibility(View.VISIBLE);
                            } else if (isCreator()) {
                                deleteView.setVisibility(View.VISIBLE);
                            } else if (TextUtils.equals(data.getTaskCreateId(), getMemeberId())
                                    && roleType == RoleType.ROLE_TYPE_TEACHER) {
                                deleteView.setVisibility(View.VISIBLE);
                            } else {
                                deleteView.setVisibility(View.INVISIBLE);
                            }
                        }
                        deleteView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //删除条目
                                showDeleteDialog(data);
                            }
                        });
                    }
                    //隐藏小红点
                    ImageView imageView = (ImageView) view.findViewById(R.id.red_point);
                    if (imageView != null) {
                        imageView.setVisibility(View.INVISIBLE);
                    }

                    //作业状态布局，仅对教师可见。
                    TextView homeworkStatusLayout = (TextView) view.findViewById(R.id.tv_finish_status);
                    if (homeworkStatusLayout != null) {
                        if (roleType == RoleType.ROLE_TYPE_TEACHER && !isOnlineClass) {
                            //完成情况仅老师显示
                            homeworkStatusLayout.setVisibility(View.VISIBLE);
                        } else {
                            homeworkStatusLayout.setVisibility(View.INVISIBLE);
                        }
                    }
                    //按时间作答题的任务
                    RelativeLayout rlLocking = (RelativeLayout) view.findViewById(R.id.rl_locking);
                    //显示未到开始时间的锁
                    if (lockingPermission(data)){
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
                    data.setIsOnlineSchoolClass(isOnlineClass);
                    data.setAirClassId(onlineRes.getId());
                    if (isHistoryClass) {
                        data.setIsHistoryClass(true);
                    } else {
                        if (isCreator()) {
                            data.setOnlineHost(true);
                        } else if (isReporter()) {
                            data.setOnlineReporter(true);
                        }
                    }
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
                protected void UpdateStudentIsRead(String taskId, String memberId, String taskType) {
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

    private boolean lockingPermission(HomeworkListInfo data){
        if ((roleType == RoleType.ROLE_TYPE_STUDENT
                || roleType == RoleType.ROLE_TYPE_PARENT
                || roleType == RoleType.ROLE_TYPE_VISITOR)
                && data.getSubmitType() == 1
                && !TextUtils.isEmpty(data.getServerNowTime())){
            return true;
        }
        return false;
    }

    private UserInfo getCurUserInfo() {
        UserInfo userInfo = null;
        if (studentMemberInfos != null && studentMemberInfos.size() > 0 && position <
                studentMemberInfos.size()) {
            StudentMemberInfo info = studentMemberInfos.get(position);
            if (info != null) {
                userInfo = info.getUserInfo();
            }
        }else {
            userInfo = this.userInfo;
        }

        return userInfo;
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
                            isNeedToUpdateSmallRedPoint = true;
                            updateAdapter(taskId);
                        }
                    }
                };
        listener.setShowLoading(true);
        listener.setShowErrorTips(false);
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
                            //更新小红点
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
        listener.setShowErrorTips(false);
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
                getString(R.string.want_to_delete_sb, data.getTaskTitle()), getString(R.string
                .cancel),
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
        boolean isTaskIdLink;
        if (isCreator()) {
            isTaskIdLink = true;
        } else {
            List<EmceeList> reporterClassBelongList = getReporterClassBelongList();
            if (reporterClassBelongList != null && reporterClassBelongList.size() > 1) {
                isTaskIdLink = true;
            } else {
                isTaskIdLink = false;
            }
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("IsTaskIdLink", isTaskIdLink);
        params.put("TaskId", data.getTaskId());
        params.put("ExtId", onlineRes.getId());
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
                            TipsHelper.showToast(getActivity(), R.string.delete_success);
                            getCurrAdapterViewHelper().getData().remove(data);
                            getCurrAdapterViewHelper().update();
                        }
                    }
                };
        listener.setShowLoading(true);
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.DELETE_AIRCLASS_STUDY_TASK_LIST_BASE_URL,
                params, listener);

    }

    private void loadViews() {
        loadCommonData();
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
            refreshData();
        }
    }

    private void switchChild() {
        SelectBindChildPopupView popupView = new SelectBindChildPopupView(getActivity(), position,
                this, childNameArray);
        popupView.showAtLocation(getView().getRootView(), Gravity.BOTTOM, 0, 0);
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

    /**
     * 模拟数据
     */
    private void loadCommonData() {
        Map<String, Object> params = new HashMap();
        //学校Id，必填
        params.put("SchoolId", schoolId);
        //班级Id，必填
        params.put("ClassId", classId);
        //空中课堂直播项的直播Id
        params.put("ExtId", onlineRes.getId());
        //区分练习的种类  课前预习 1 课后练习 2
        params.put("TaskFlag", currentStudyType);

        if (!(isTeacher || isHeadMaster) && roleType == RoleType.ROLE_TYPE_PARENT){
            //studentId
            params.put("StudentId", studentId);
            //角色信息
            params.put("RoleType", roleType);
        }else if (roleType == RoleType.ROLE_TYPE_STUDENT){
            //studentId
            params.put("StudentId", getMemeberId());
            //角色信息
            params.put("RoleType", roleType);
        }
        //分页信息
        params.put("Pager", getPageHelper().getFetchingPagerArgs());
        DefaultPullToRefreshDataListener listener =  new DefaultPullToRefreshDataListener<HomeworkListResult>(
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

            @Override
            public void onFinish() {
                super.onFinish();
                if (!isReporter() && isFirstIn) {
                    isFirstIn = false;
                    Fragment fragment = getParentFragment();
                    if (fragment != null && fragment instanceof AirClassroomDetailFragment) {
                        AirClassroomDetailFragment detailFragment = (AirClassroomDetailFragment) fragment;
                        detailFragment.setLoadStudyTaskFinish(currentStudyType, hasData);
                    }
                }
            }
        };
        RequestHelper.sendPostRequest(getActivity(),
                ServerUrl.GET_AIRCLASS_STUDY_TASK_LIST_BASE_URL, params, listener);
    }

    private void updateResourceListView(HomeworkListResult result) {

        if (getPageHelper().isFetchingPageIndex(result.getModel().getPager())) {
            List<HomeworkListInfo> list = result.getModel().getData();
            if (list == null || list.size() <= 0) {
                if (getPageHelper().isFetchingFirstPage()) {
                    getCurrAdapterViewHelper().clearData();
//                    TipsHelper.showToast(getActivity(),
//                            getString(R.string.no_data));
                } else {
//                    TipsHelper.showToast(getActivity(),
//                            getString(R.string.no_more_data));
                }
                return;
            }
            hasData = true;
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
                getCurrAdapterView().setSelection(position);
                result.getModel().setData(getCurrAdapterViewHelper().getData());
            } else {
                getCurrAdapterViewHelper().setData(Utils.formatHomeworkListData(list));
                homeworkListResult = result;
            }
        }
    }


    private void loadUserInfoDetail(final String childId){
        Map<String, Object> params = new HashMap();
        params.put("UserId", childId);
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.LOAD_USERINFO_URL,
                params, new DefaultListener<UserInfoResult>(UserInfoResult.class) {
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
                        UserInfo childUserInfo = getResult().getModel();
                        if (childUserInfo != null){
                            if (studentMemberInfos == null){
                                studentMemberInfos = new ArrayList<>();
                            }
                            studentMemberInfos.clear();
                            StudentMemberInfo memberInfo = new StudentMemberInfo();
                            memberInfo.setMemberId(childUserInfo.getMemberId());
                            memberInfo.setRealName(childUserInfo.getRealName());
                            memberInfo.setNickName(childUserInfo.getNickName());
                            memberInfo.setHeadPicUrl(childUserInfo.getHeaderPic());
                            memberInfo.setQRCode(childUserInfo.getQRCode());
                            memberInfo.setEmail(childUserInfo.getEmail());
                            memberInfo.setClassId(childId);
                            studentMemberInfos.add(memberInfo);
                            position = 0;
                        }
                    }
                });
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_switch_child) {
            //切换按钮
            switchChild();
        } else if (v.getId() == R.id.tv_assign_homework) {
            //布置任务
            int haveFree = Utils.checkStorageSpace(getActivity());
            if (haveFree == 0) {
                showTaskTypeDialog();
            }
        }
    }

    /**
     * 显示布置任务的Dialog
     */
    private void showTaskTypeDialog() {
        StudyTaskUtils.handleSubjectSettingData(getActivity(),getMemeberId(),v -> {
            ArrangeLearningTasksUtil.getInstance()
                    .setActivity(getActivity())
                    .setCallBackListener(new ArrangeLearningTasksUtil.ArrangeLearningTaskListener() {
                        @Override
                        public void selectedTypeData(String title, int type) {
                            enterIntroductionCourse(title,type);
                        }
                    })
                    .show();
        });
    }

    /**
     * 进入制作界面
     */
    private void enterIntroductionCourse(String title, int type) {
        Intent intent = new Intent(getActivity(), IntroductionForReadCourseActivity.class);
        intent.putExtra(ActivityUtils.EXTRA_HEADER_TITLE, title);
        intent.putExtra(ActivityUtils.EXTRA_DEFAULT_DATE, DateUtils.getCurDate());
        intent.putExtra(ActivityUtils.EXTRA_TASK_TYPE, type);
        intent.putExtra(ActivityUtils.EXTRA_STDUY_TYPE, currentStudyType);//当前练习的类型
        intent.putExtra(ActivityUtils.EXTRA_DATA_INFO, onlineRes);
        intent.putExtra(ActivityUtils.EXTRA_SCHOOL_INFO_LIST_DATA, (Serializable) ScreenSchoolListData());
        intent.putExtra(ActivityUtils.EXTRA_IS_ONLINE_CLASS,isOnlineClass);
        if (!TextUtils.isEmpty(classId)) {
            intent.putExtra(ActivityUtils.EXTRA_CLASS_ID,classId);
        }
        if (!TextUtils.isEmpty(schoolId)){
            intent.putExtra(ActivityUtils.EXTRA_SCHOOL_ID,schoolId);
        }
        startActivityForResult(intent, ActivityUtils.REQUEST_CODE_RETURN_REFRESH);
    }
    /**
     * 获取发送的班级
     *
     * @return
     */
    private List<ShortSchoolClassInfo> ScreenSchoolListData() {
        List<ShortSchoolClassInfo> schoolClassInfos = new ArrayList<>();
        ShortSchoolClassInfo info = null;
        if (isCreator()) {
            List<PublishClass> publishClassList = onlineRes.getPublishClassList();
            if (publishClassList != null && publishClassList.size() > 0) {
                for (int i = 0, len = publishClassList.size(); i < len; i++) {
                    PublishClass classData = publishClassList.get(i);
                    info = new ShortSchoolClassInfo();
                    info.setSchoolName(classData.getSchoolName());
                    info.setClassName(classData.getClassName());
                    info.setSchoolId(classData.getSchoolId());
                    info.setClassId(classData.getClassId());
                    schoolClassInfos.add(info);
                }
            }
        } else {
            //主持人发送学习任务到当前所在的班级
            List<EmceeList> reporterClassBelong = getReporterClassBelongList();
            if (reporterClassBelong != null && reporterClassBelong.size() > 1) {
                for (int i = 0, len = reporterClassBelong.size(); i < len; i++) {
                    EmceeList list = reporterClassBelong.get(i);
                    info = new ShortSchoolClassInfo();
                    info.setClassId(list.getClassIds());
                    info.setSchoolId(list.getSchoolIds());
                    schoolClassInfos.add(info);
                }
            } else {
                info = new ShortSchoolClassInfo();
                info.setSchoolName(currentClassInfo.getSchoolName());
                info.setClassName(currentClassInfo.getClassName());
                info.setClassId(currentClassInfo.getClassId());
                info.setSchoolId(currentClassInfo.getSchoolId());
                schoolClassInfos.add(info);
            }
        }
        return schoolClassInfos;
    }

    private List<EmceeList> getReporterClassBelongList() {
        Fragment fragment = getParentFragment();
        if (fragment != null && fragment instanceof AirClassroomDetailFragment) {
            AirClassroomDetailFragment detailFragment = (AirClassroomDetailFragment) fragment;
            return detailFragment.reporterClassBelong;
        }
        return null;
    }

    /**
     * 判断当前的用户是不是当前班级的创建者
     *
     * @return
     */
    private boolean isCreator() {
        if (roleType == RoleType.ROLE_TYPE_TEACHER && TextUtils.equals(getMemeberId(), onlineRes.getAcCreateId())) {
            return true;
        }
        return false;
    }

    private boolean isReporter() {
        Fragment fragment = getParentFragment();
        if (fragment != null && fragment instanceof AirClassroomDetailFragment) {
            AirClassroomDetailFragment detailFragment = (AirClassroomDetailFragment) fragment;
            return detailFragment.isOnlineReporter;
        }
        return false;
    }

    private void getCurrentClassInfo(){
        currentClassInfo = new SubscribeClassInfo();
        if (onlineRes != null){
            List<PublishClass> publishClasses = onlineRes.getPublishClassList();
            if (publishClasses != null && publishClasses.size() > 0){
                for (int i = 0;i < publishClasses.size();i++){
                    PublishClass classData = publishClasses.get(i);
                    if (TextUtils.equals(classData.getClassId(),onlineRes.getClassId())){
                        currentClassInfo.setClassId(classData.getClassId());
                        currentClassInfo.setClassName(classData.getClassName());
                        currentClassInfo.setSchoolId(classData.getSchoolId());
                        currentClassInfo.setSchoolName(classData.getSchoolName());
                        break;
                    }
                }
            }
        }
    }

    /**
     * 根据条件来刷新数据
     */
    private void accordingConditionRefresh() {
        if (isNeedToUpdateSmallRedPoint) {
            isNeedToUpdateSmallRedPoint = false;
            refreshData();
        } else if (HomeworkCommitFragment.hasCommented()) {
            HomeworkCommitFragment.setHasCommented(false);
            refreshData();
        } else if (CampusPatrolUtils.hasStudyTaskAssigned()) {
            CampusPatrolUtils.setHasStudyTaskAssigned(false);
            refreshData();
        } else if (TopicDiscussionFragment.hasCommented()) {
            TopicDiscussionFragment.setHasCommented(false);
            refreshData();
        }
    }
}
