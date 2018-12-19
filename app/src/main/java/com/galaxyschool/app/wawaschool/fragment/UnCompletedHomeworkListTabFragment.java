package com.galaxyschool.app.wawaschool.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.CourseOpenUtils;
import com.galaxyschool.app.wawaschool.common.Utils;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.galaxyschool.app.wawaschool.fragment.resource.HomeworkResourceAdapterViewHelper;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.HomeworkListInfo;
import com.galaxyschool.app.wawaschool.pojo.HomeworkListResult;
import com.galaxyschool.app.wawaschool.pojo.RoleType;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.galaxyschool.app.wawaschool.views.ContactsMessageDialog;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;
import com.galaxyschool.app.wawaschool.views.slidelistview.SlideListView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 未完成作业列表
 */

public class UnCompletedHomeworkListTabFragment extends ContactsListFragment{

    public static final String TAG = UnCompletedHomeworkListTabFragment.class.getSimpleName();

    private HomeworkListResult homeworkListResult;
    private int roleType;
    private String classId;
    private String schoolId;
    private String childId;
    private boolean isHeadMaster;
    private String sortStudentId;
    private static boolean hasCommented;
    private UserInfo userInfo;
    public interface Constants {
        public static final String EXTRA_SCHOOL_ID = "schoolId";
        public static final String EXTRA_CLASS_ID = "classId";
        public static final String EXTRA_CHANNEL_TYPE = "channelType";
        public static final String EXTRA_IS_TEACHER = "isTeacher";
        public static final String EXTRA_ROLE_TYPE = "role_type";
        public static final String EXTRA_IS_HEAD_MASTER = "isHeadMaster";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        init();
        return inflater.inflate(R.layout.today_homework, null);
    }
    private void init() {
        if (getArguments()!=null){
            roleType=getArguments().getInt(Constants.EXTRA_ROLE_TYPE);
            classId=getArguments().getString(Constants.EXTRA_CLASS_ID);
            schoolId=getArguments().getString(Constants.EXTRA_SCHOOL_ID);
            isHeadMaster = getArguments().getBoolean(Constants.EXTRA_IS_HEAD_MASTER);
            if (roleType==RoleType.ROLE_TYPE_PARENT) {
                childId = getArguments().getString("childId");
                sortStudentId = getArguments().getString("sortStudentId");
            }
            //获得用户信息
            userInfo = (UserInfo) getArguments().getSerializable(UserInfo.class.getSimpleName());
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
    }

    @Override
    public void loadDataLazy() {
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
        //头布局
        View headerView = findViewById(R.id.contacts_header_layout);
        if (headerView != null){
            headerView.setVisibility(View.GONE);
        }

        //标题
        TextView textView = (TextView) findViewById(R.id.contacts_header_title);
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
                    listView,roleType,getMemeberId(),isHeadMaster) {
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
                    //打开学习任务列表
                    CourseOpenUtils.openStudyTask(getActivity(),data,roleType,isHeadMaster,
                            getMemeberId(),sortStudentId,childId,userInfo,false);
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
                    updateStudentReadState(taskId,memberId,taskType);
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
                            //更新小红点
                            updateSmallRedPointStatus();
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
     * 更新未读状态
     */
    private void updateSmallRedPointStatus() {
        Fragment parentFragment = getParentFragment();
        if (parentFragment != null && parentFragment instanceof HomeworkMainFragment){
            //更新小红点
            ((HomeworkMainFragment)parentFragment).setNeedToUpdateSmallRedPoint(true);
        }
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
                            //更新小红点
                            updateSmallRedPointStatus();
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
     * 删除框Dialog
     * @param data
     */
    private void showDeleteDialog(final HomeworkListInfo data) {
        ContactsMessageDialog messageDialog = new ContactsMessageDialog(getActivity(), null,
                getString(R.string.want_to_delete_sb,data.getTaskTitle()),getString(R.string
                        .cancel) ,
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
        loadCommonData();
    }

    /**
     * 根据孩子id刷新页面
     */
    public void refreshPageByChildParams(Bundle args){
        if (args != null) {
            childId = args.getString("childId");
            sortStudentId = args.getString("sortStudentId");
            //获得用户信息
            userInfo = (UserInfo) args.getSerializable(UserInfo.class.getSimpleName());
            loadViews();
        }
    }

    /**
     * 模拟数据
     */
    private void loadCommonData() {
        if (roleType==RoleType.ROLE_TYPE_PARENT){
            //家长的话，必须要加载绑定的孩子。
            if (TextUtils.isEmpty(childId)) {
                return;
            }
        }

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
            //未完成
            params.put("TaskState", HomeworkMainFragment.UNFINISHED);
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
        params.put("SearchTime","");
        //任务创建者的id,老师角色时必填
        //目前要放开所有人的查看权限，该字段需要传空。
        if (roleType == RoleType.ROLE_TYPE_TEACHER || roleType == RoleType.ROLE_TYPE_STUDENT
                || roleType == RoleType.ROLE_TYPE_PARENT) {
            params.put("TaskCreateId", "");
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
                getCurrAdapterView().setSelection(position);
                result.getModel().setData(getCurrAdapterViewHelper().getData());
            } else {
                getCurrAdapterViewHelper().setData(Utils.formatHomeworkListData(list));
                homeworkListResult = result;
            }
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
    }

    public static void setHasCommented(boolean hasCommented) {
        UnCompletedHomeworkListTabFragment.hasCommented = hasCommented;
    }

    public static boolean hasCommented() {
        return hasCommented;
    }
}
