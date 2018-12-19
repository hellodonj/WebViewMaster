package com.galaxyschool.app.wawaschool.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.EnglishWritingCommentRecordActivity;
import com.galaxyschool.app.wawaschool.EnglishWritingCompositionRequirementsActivity;
import com.galaxyschool.app.wawaschool.HomeworkFinishStatusActivity;
import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.TopicDiscussionActivity;
import com.galaxyschool.app.wawaschool.common.DateUtils;
import com.galaxyschool.app.wawaschool.common.ShareUtils;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.galaxyschool.app.wawaschool.fragment.resource.HomeworkCommitResourceAdapterViewHelper;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.CommitTask;
import com.galaxyschool.app.wawaschool.pojo.HomeworkCommitObjectResult;
import com.galaxyschool.app.wawaschool.pojo.RoleType;
import com.galaxyschool.app.wawaschool.pojo.StudyTask;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;
import com.oosic.apps.share.ShareInfo;
import com.oosic.apps.share.SharedResource;
import com.umeng.socialize.media.UMImage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 英文写作提交
 */

public class EnglishWritingCommitFragment extends ContactsListFragment {

    public static final String TAG = EnglishWritingCommitFragment.class.getSimpleName();

    private View rootView, englishWritingHeaderView;
    private TextView commonHeaderTitleTextView, englishWritingTitleTextView,
            commonHeaderRightTextView;
    private TextView assignTimeTextView;
    private TextView finishTimeTextView;
    private ImageView rightIconImageView;
    private View finishStatusView;
    private TextView finishStatusTextView, discussionCountTextView;
    private ListView listView;
    private int roleType = -1;
    private String taskId;
    private String taskTitle;
    private String studentId,sortStudentId;
    private StudyTask task;
    private static boolean hasCommented;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.layout_english_writing_commit_homework, null);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
        refreshData();
    }

    private void refreshData() {
        loadViews();
    }

    void initViews() {
        if (getArguments() != null) {
            roleType = getArguments().getInt("roleType");
            taskId = getArguments().getString("taskId");
            taskTitle = getArguments().getString("taskTitle");
            //单个孩子id
            studentId = getArguments().getString("studentId");
            //学生Id串，以逗号分隔。
            sortStudentId = getArguments().getString("sortStudentId");
        }

        //标题
        commonHeaderTitleTextView = (TextView) findViewById(R.id.contacts_header_title);
        if (commonHeaderTitleTextView != null) {
            commonHeaderTitleTextView.setText(R.string.english_writing);
        }

        //分享
        commonHeaderRightTextView = (TextView) findViewById(R.id.contacts_header_right_btn);
        if (commonHeaderRightTextView != null){
            commonHeaderRightTextView.setVisibility(View.VISIBLE);
            commonHeaderRightTextView.setText(getString(R.string.share));
            commonHeaderRightTextView.setOnClickListener(this);
        }

        englishWritingHeaderView = findViewById(R.id.layout_english_writing_assign_homework);
        englishWritingHeaderView.setOnClickListener(this);

        //作业标题
        englishWritingTitleTextView = (TextView) englishWritingHeaderView.findViewById(R.id.tv_title);

        //布置时间
        assignTimeTextView = (TextView) englishWritingHeaderView.findViewById(R.id.tv_start_time);

        //右边的图标
        rightIconImageView = (ImageView) englishWritingHeaderView.findViewById(R.id.iv_right_icon);
        if (rightIconImageView != null){
            rightIconImageView.setVisibility(View.INVISIBLE);
        }

        //完成时间
        finishTimeTextView = (TextView) englishWritingHeaderView.findViewById(R.id.tv_end_time);
        //完成状态布局
        finishStatusView = englishWritingHeaderView.findViewById(R.id.layout_finish_state);
        if (finishStatusView != null){
            finishStatusView.setVisibility(View.VISIBLE);
        }
        //完成状态仅对老师显示
        finishStatusTextView = (TextView) englishWritingHeaderView.findViewById(R.id.tv_finish_status);
        if (finishStatusTextView != null) {
            finishStatusTextView.setOnClickListener(this);
            if (roleType == RoleType.ROLE_TYPE_TEACHER) {
                finishStatusTextView.setVisibility(View.VISIBLE);
            } else {
                finishStatusTextView.setVisibility(View.INVISIBLE);
            }
        }

        //讨论
        discussionCountTextView = (TextView) englishWritingHeaderView.findViewById(R.id.tv_discuss_count);
        if (discussionCountTextView != null) {
            discussionCountTextView.setVisibility(View.VISIBLE);
            discussionCountTextView.setOnClickListener(this);
        }

        //下拉刷新
        PullToRefreshView pullToRefreshView = (PullToRefreshView) findViewById(
                R.id.contacts_pull_to_refresh);
        setPullToRefreshView(pullToRefreshView);
        listView = (ListView) findViewById(R.id.contacts_list_view);
        if (listView != null) {

            //作业通用列表
            AdapterViewHelper listViewHelper = new HomeworkCommitResourceAdapterViewHelper(getActivity(),
                    listView, roleType, getMemeberId()) {
                @Override
                public void loadData() {
                    loadCommonData();
                }
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    CommitTask data = (CommitTask) getDataAdapter().getItem(position);
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
                    if (imageView != null){
//                        if (isCampusPatrolTag){
//                            //校园巡查需要隐藏小红点
//                            imageView.setVisibility(View.INVISIBLE);
//                        }
                    }

                    //标题
                    TextView textView = (TextView) view.findViewById(R.id.tv_title);
                    if (textView != null) {
                        if (task!=null) {
                            textView.setText(task.getTaskTitle());
                        }
                    }

                    //作业图片布局
                    View iconLayout = view.findViewById(R.id.layout_icon);
                    if (iconLayout != null) {
                        iconLayout.setVisibility(View.GONE);
                    }

                    view.setTag(holder);
                    return view;
                }

                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {
//                    super.onItemClick(parent, view, position, id);
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) {
                        return;
                    }
                    CommitTask data = (CommitTask) holder.data;
                    if (data != null) {
                        //进入点评记录页面
                        updateLookTaskStatus(data.getCommitTaskId(),data.isRead());
                        enterEnglishWritingCommentRecordActivity(data);
                    }
                }


                protected void updateLookTaskStatus(int commitTaskId, boolean isRead) {
                    //只有发布的老师查看学生作业才调。
                    if (task != null
                            && !TextUtils.isEmpty(task.getTaskCreateId())
                            && !TextUtils.isEmpty(getMemeberId())) {

                        if (!isRead && task.getTaskCreateId().equals(getMemeberId())) {
                            updateStatus(commitTaskId,task.getType());
                        }
                    }
                }
            };

            setCurrAdapterViewHelper(listView, listViewHelper);
        }
    }

    private void enterEnglishWritingCommentRecordActivity(CommitTask data) {
        if (data == null){
            return;
        }
        Intent intent = new Intent(getActivity(), EnglishWritingCommentRecordActivity.class);
        Bundle bundle=getArguments();
        String studentId=data.getStudentId();
        if (!this.studentId.equals(studentId)){
            bundle.putString(EnglishWritingCompletedFragment.Constant.STUDENTID,
                    data.getStudentId());
            bundle.putString(EnglishWritingCompletedFragment.Constant.SORTSTUDENTID,
                    data.getStudentId());
        }
        //任务的id。
        bundle.putInt(EnglishWritingCompletedFragment.Constant.TASKID,data.getTaskId());
        intent.putExtras(getArguments());
        startActivity(intent);
    }

    /**
     * 拉取数据
     */
    private void loadCommonData() {
        Map<String, Object> params = new HashMap();
        //学校Id，必填
        if (!TextUtils.isEmpty(taskId)) {
            params.put("TaskId", taskId);
        }
        //非必填,如果填写就把该学生对应的任务排在前面,支持多个ID排序,多个ID时用逗号分隔传值.
        if (!TextUtils.isEmpty(sortStudentId)) {
            params.put("SortStudentId", sortStudentId);
        }
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
        updateHeaderView(result);
        List<CommitTask> list = result.getModel().getData().getListCommitTask();
        if (list == null || list.size() <= 0) {
            return;
        } else {
            getCurrAdapterViewHelper().setData(list);
        }
    }

    /**
     **更新头部内容
     * @param result
     */
    private void updateHeaderView(HomeworkCommitObjectResult result) {

        if (result == null) {
            return;
        }

        task = result.getModel().getData().getTaskInfo();
        if (task == null) {
            return;
        }
        //讨论数
        if (discussionCountTextView != null) {
            discussionCountTextView.setText(getString(R.string.discussion, String.valueOf(task
                    .getCommentCount())));
        }
        //作业标题
        if (englishWritingTitleTextView != null) {
            englishWritingTitleTextView.setText(task.getTaskTitle());
        }

        //布置时间
        if (assignTimeTextView != null) {
            assignTimeTextView.setText(getString(R.string.assign_date) + "：" +
                    DateUtils.getDateStr(task.getStartTime(), 0) + "-" +
                    DateUtils.getDateStr(task.getStartTime(), 1) + "-" + DateUtils
                    .getDateStr(task.getStartTime(), 2));
        }

        //完成时间
        if (finishTimeTextView != null) {
            finishTimeTextView.setText(getString(R.string.finish_date) + "：" +
                    DateUtils.getDateStr(task.getEndTime(), 0) + "-" +
                    DateUtils.getDateStr(task.getEndTime(), 1) + "-" + DateUtils
                    .getDateStr(task.getEndTime(), 2));
        }

        //完成状态
        if (finishStatusTextView != null) {
            //只有老师才显示完成情况，才去计算。
            if (roleType == RoleType.ROLE_TYPE_TEACHER) {
                int taskNum = -1;
                int taskFinishCount = -1;
                int taskUnFinishCount = -1;
                taskNum = task.getTaskNum();
                taskFinishCount = task.getFinishTaskCount();
                taskUnFinishCount = taskNum - taskFinishCount;
                boolean isFinishAll = ((taskNum > 0) && (taskNum == taskFinishCount));
                if (!isFinishAll) {
                    //未完成
                    finishStatusTextView.setText(getString(R.string.n_finish,
                            String.valueOf(taskFinishCount)) + "/" + getString(R.string.n_unfinish,
                            String.valueOf(taskUnFinishCount)));
                } else {

                    //全部完成
                    finishStatusTextView.setText(getString(R.string.n_finish_all,
                            String.valueOf(taskNum)));
                }
            }
        }
    }

    /**
     * 更新小红点
     * @param commitTaskId
     * @param taskType
     */
    private void updateStatus(final int commitTaskId, int taskType) {

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("CommitTaskId", commitTaskId);
        //英文写作任务类型
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

    private void enterTopicDiscussionActivity() {
        Intent intent = new Intent(getActivity(), TopicDiscussionActivity.class);
        intent.putExtra("TaskId", Integer.parseInt(taskId));
        intent.putExtra("roleType", roleType);
        startActivityForResult(intent, CampusPatrolPickerFragment.REQUEST_CODE_DISCUSSION_TOPIC);
    }

    private void enterHomeworkFinishStatusActivity() {
        Intent intent = new Intent(getActivity(), HomeworkFinishStatusActivity.class);
        Bundle bundle = new Bundle();
        //角色信息
        bundle.putInt(HomeworkFinishStatusActivity.Constants.ROLE_TYPE,roleType);
        bundle.putString(HomeworkFinishStatusActivity.Constants.TASK_ID, taskId);
        boolean isOwnerTask = isOwnerTask(task);
        bundle.putBoolean(HomeworkFinishStatusActivity.Constants.IS_OWNER_TASK,isOwnerTask);
        bundle.putString(HomeworkFinishStatusActivity.Constants.TASK_TITLE, taskTitle);
        //类型信息
        bundle.putInt(HomeworkFinishStatusActivity.Constants.TASK_TYPE, task.getType());
        bundle.putBoolean(HomeworkFinishStatusActivity.Constants.IS_FROM_ENGLISH_WRITING,true);
        intent.putExtras(bundle);
        startActivityForResult(intent,CampusPatrolPickerFragment.REQUEST_CODE_FINISH_STATUS);
    }

    private boolean isOwnerTask(StudyTask task) {

        boolean isOwnerTask = false;
        if (task != null){
            if (!TextUtils.isEmpty(task.getTaskCreateId())
                    && !TextUtils.isEmpty(getMemeberId())
                    && task.getTaskCreateId().equals(getMemeberId())){
                isOwnerTask = true;
            }
        }

        return isOwnerTask;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_finish_status) {
            //查看作业完成状态
            //进入新的已完成/未完成人数页面
            enterHomeworkFinishStatusActivity();

        } else if (v.getId() == R.id.layout_english_writing_assign_homework) {
            //headerView
            //进入作文要求页面
            enterEnglishWritingCompositionRequirementsActivity();

        } else if (v.getId() == R.id.contacts_header_right_btn){
            //分享
            share();
        }else if (v.getId() == R.id.tv_discuss_count){
            //讨论
            //进入原始的话题讨论页面
            enterTopicDiscussionActivity();
        }else {
            super.onClick(v);
        }
    }

    private void share() {
        if (task == null){
            return;
        }
        ShareInfo shareInfo = new ShareInfo();
        String title = task.getTaskTitle();
        String url = task.getShareUrl();
        String thumbnail = task.getResThumbnailUrl();
        String schoolName = task.getSchoolName();
        String className = task.getClassName();
        String description = schoolName + className;
        shareInfo.setTitle(title);
        shareInfo.setContent(description);
        shareInfo.setTargetUrl(url);
        UMImage umImage = null;
        if (!TextUtils.isEmpty(thumbnail)) {
            umImage = new UMImage(getActivity(), AppSettings.getFileUrl(thumbnail));
        } else {
            umImage = new UMImage(getActivity(), R.drawable.default_cover);
        }
        shareInfo.setuMediaObject(umImage);
        //蛙蛙好友分享资源
        SharedResource resource = new SharedResource();
        resource.setTitle(title);
        resource.setDescription(description);
        resource.setShareUrl(url);
        if (!TextUtils.isEmpty(thumbnail)) {
            resource.setThumbnailUrl(AppSettings.getFileUrl(thumbnail));
        }
        resource.setType(SharedResource.RESOURCE_TYPE_HTML);
        shareInfo.setSharedResource(resource);
        ShareUtils shareUtils = new ShareUtils(getActivity());
        shareUtils.share(getView(), shareInfo);
    }


    private void enterEnglishWritingCompositionRequirementsActivity() {

        if (task == null){
            return;
        }
        Intent intent = new Intent(getActivity(), EnglishWritingCompositionRequirementsActivity
                .class);
        Bundle bundle=new Bundle();
        bundle.putSerializable("task",task);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public static void setHasCommented(boolean hasCommented) {
        EnglishWritingCommitFragment.hasCommented = hasCommented;
    }

    public static boolean hasCommented() {
        return hasCommented;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //讨论返回后刷新逻辑
        if (data == null) {
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
            }else if (requestCode == CampusPatrolPickerFragment.REQUEST_CODE_FINISH_STATUS){
                //已完成/未完成头像
                if (HomeworkFinishStatusFragment.hasContentChanged()){
                    HomeworkFinishStatusFragment.setHasContentChanged(false);
                    refreshData();
                }
            }
        }
    }
}
