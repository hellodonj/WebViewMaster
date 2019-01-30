package com.galaxyschool.app.wawaschool.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.duowan.mobile.netroid.NetroidError;
import com.galaxyschool.app.wawaschool.HomeworkCommitActivity;
import com.galaxyschool.app.wawaschool.Note.OnlineMediaPaperActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.SchoolSpaceActivity;
import com.galaxyschool.app.wawaschool.TopicDiscussionActivity;
import com.galaxyschool.app.wawaschool.common.CourseOpenUtils;
import com.galaxyschool.app.wawaschool.common.Utils;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.galaxyschool.app.wawaschool.fragment.resource.HomeworkResourceAdapterViewHelper;
import com.galaxyschool.app.wawaschool.pojo.ReviewInfo;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.Emcee;
import com.galaxyschool.app.wawaschool.pojo.EmceeList;
import com.galaxyschool.app.wawaschool.pojo.EmceeListResult;
import com.galaxyschool.app.wawaschool.pojo.HomeworkListInfo;
import com.galaxyschool.app.wawaschool.pojo.HomeworkListResult;
import com.galaxyschool.app.wawaschool.pojo.LookResDto;
import com.galaxyschool.app.wawaschool.pojo.RoleType;
import com.galaxyschool.app.wawaschool.pojo.SchoolInfo;
import com.galaxyschool.app.wawaschool.pojo.StudyTaskType;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.galaxyschool.app.wawaschool.views.ContactsMessageDialog;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;
import com.galaxyschool.app.wawaschool.views.slidelistview.SlideListView;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 老师作业列表
 */

public class TeacherHomeworkListTabFragment extends ContactsListFragment {

    public static final String TAG = TeacherHomeworkListTabFragment.class.getSimpleName();

    private HomeworkListResult homeworkListResult;
    private int roleType;
    private String classId;
    private String schoolId;
    private String childId;
    private boolean isHeadMaster;
    private String sortStudentId;
    private static boolean hasCommented;
    private boolean isNeedToUpdateSmallRedPoint;
    private String[] childIdArray;//孩子Id数组
    private UserInfo userInfo;
    private boolean isFromReviewStatistic;//来自点评统计
    private ReviewInfo reviewInfo;

    public interface Constants {
        String EXTRA_SCHOOL_ID = "schoolId";
        String EXTRA_CLASS_ID = "classId";
        String EXTRA_CHANNEL_TYPE = "channelType";
        String EXTRA_IS_TEACHER = "isTeacher";
        String EXTRA_ROLE_TYPE = "role_type";
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

    public void refreshData() {
        getPageHelper().clear();
        loadViews();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    void initViews() {

        if (getArguments() != null) {
            roleType = getArguments().getInt(Constants.EXTRA_ROLE_TYPE);
            classId = getArguments().getString(Constants.EXTRA_CLASS_ID);
            schoolId = getArguments().getString(Constants.EXTRA_SCHOOL_ID);
            isHeadMaster = getArguments().getBoolean(Constants.EXTRA_IS_HEAD_MASTER);
            if (roleType == RoleType.ROLE_TYPE_PARENT) {
                childId = getArguments().getString("childId");
                sortStudentId = getArguments().getString("sortStudentId");
                //孩子数组
                childIdArray = (String[]) getArguments().get(HomeworkMainFragment.Constants
                        .EXTRA_CHILD_ID_ARRAY);
            }
            userInfo = (UserInfo) getArguments().getSerializable(UserInfo.class.getSimpleName());
            reviewInfo = (ReviewInfo) getArguments().getSerializable(ReviewInfo.class.getSimpleName());
            if (reviewInfo != null) {
                isFromReviewStatistic = true;
            }
        }

        //头布局
        View headerView = findViewById(R.id.contacts_header_layout);
        if (headerView != null) {
            headerView.setVisibility(View.GONE);
        }

        //标题
        final TextView textView = (TextView) findViewById(R.id.contacts_header_title);
        if (textView != null) {
            textView.setText(R.string.today_assignment);
        }

        PullToRefreshView pullToRefreshView = (PullToRefreshView) findViewById(
                R.id.contacts_pull_to_refresh);
        setPullToRefreshView(pullToRefreshView);

        SlideListView listView = (SlideListView) findViewById(R.id.contacts_list_view);
        if (listView != null) {
            listView.setSlideMode(SlideListView.SlideMode.NONE);

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
                    //不是来自空中课堂和任务的创建者是自己
                    if (data.getAirClassId() == 0) {
                        //打开学习任务列表
                        CourseOpenUtils.openStudyTask(getActivity(), data, roleType, isHeadMaster,
                                getMemeberId(), sortStudentId, childId, userInfo, false);
                    } else {
                        accordingAirClassIdAnalysisData(data);
                    }
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

    /**
     * 更新话题讨论和交作业未读状态
     *
     * @param taskId
     * @param memberId
     * @param taskType
     */
    private void updateStudentReadState(final String taskId, String memberId, String taskType) {

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("TaskId", taskId);
        params.put("StudentId", memberId);
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
                            //更新小红点
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
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.UPDATE_STUDENT_IS_READ_URL,
                params, listener);

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
                            //更新小红点
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

    /**
     * 根据直播的id拉取详情页数据分析权限问题
     */
    private void accordingAirClassIdAnalysisData(final HomeworkListInfo data) {
        Map<String, Object> params = new HashMap<>();
        params.put("Id", data.getAirClassId());
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
                    analysisCurrentUserIsReporter(data, emcee);
                } catch (Exception e) {
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
     *
     * @param model
     */
    private void analysisCurrentUserIsReporter(HomeworkListInfo data, Emcee model) {
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
                getMemeberId(), sortStudentId, childId, userInfo, false);
    }

    private void loadViews() {
        loadCommonData();
    }

    /**
     * 模拟数据
     */
    private void loadCommonData() {
        Map<String, Object> params = new HashMap<>();
        if (isFromReviewStatistic) {
            params.put("TaskIds", reviewInfo.getTaskIdStr());
            //学生的studentId
            params.put("StudentId", reviewInfo.getStudentId());
        } else {
            //学校Id，必填
            params.put("SchoolId", schoolId);
            //班级Id，必填
            params.put("ClassId", classId);
            //角色信息，必填，0-学生,1-家长，2-老师
            int role = Utils.transferRoleType(roleType);
            if (role != -1) {
                params.put("RoleType", role);
            }
//        //任务状态(0-未完成,1-已完成),学生，家长角色时必填。
//        if (roleType==RoleType.ROLE_TYPE_STUDENT||roleType==RoleType.ROLE_TYPE_PARENT){
//            //已完成
//            params.put("TaskState", HomeworkMainFragment.FINISHED);
//        }
            //学生ID，非必填，学生、家长角色时必填
            if (roleType == RoleType.ROLE_TYPE_STUDENT) {
                params.put("StudentId", getMemeberId());
            } else if (roleType == RoleType.ROLE_TYPE_PARENT) {
                //家长的话，必须要加载绑定的孩子。
                if (childId != null) {
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
            params.put("Version", 1);
        }
        //分页信息
        params.put("Pager", getPageHelper().getFetchingPagerArgs());
        DefaultPullToRefreshDataListener listener = new DefaultPullToRefreshDataListener<HomeworkListResult>(
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
        };
        listener.setShowErrorTips(false);
        RequestHelper.sendPostRequest(getActivity(),
                isFromReviewStatistic ? ServerUrl.GET_TEACHER_REVIEW_STATIS_LIST_BASE_URL :
                        ServerUrl.GET_STUDENT_TASK_LIST_URL, params, listener);

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
                getCurrAdapterViewHelper().getData().addAll(Utils.formatHomeworkListData(list));
//                getCurrAdapterViewHelper().getData().addAll(list);
                getCurrAdapterView().setSelection(position);
                result.getModel().setData(getCurrAdapterViewHelper().getData());
            } else {
                getCurrAdapterViewHelper().setData(Utils.formatHomeworkListData(list));
//                getCurrAdapterViewHelper().setData(list);
                homeworkListResult = result;
            }
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
        TeacherHomeworkListTabFragment.hasCommented = hasCommented;
    }

    public static boolean hasCommented() {
        return hasCommented;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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
                        refreshData();
                    }
                    if (EnglishWritingBuildFragment.hasCommented()) {
                        EnglishWritingBuildFragment.setHasCommented(false);
                        refreshData();
                    }
                }
            }
        }
    }
}
