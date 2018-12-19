package com.galaxyschool.app.wawaschool.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.EnglishWritingBuildActivity;
import com.galaxyschool.app.wawaschool.EnglishWritingCommentDetailsActivity;
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
import com.galaxyschool.app.wawaschool.pojo.HomeworkCommitObjectInfo;
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
 * 英文写作---已提交页面
 */

public class EnglishWritingCompletedFragment extends ContactsListFragment {


    public static final String TAG = EnglishWritingCompletedFragment.class.getSimpleName();

    public interface Constant {
        String STUDENTID = "StudentId";
        String SORTSTUDENTID = "sortStudentId";
        String ROLETYPE = "roleType";
        String TASKID = "taskId";
        String TASKTYPE = "taskType";
        String COMMITTASK="commitTask";
        String IS_TASK_BELONGS_TO_CHILDREN_OR_OWNER ="isTaskBelongsToChildrenOrOwner";
        String COMMITTASKID="commitTaskId";
        String SHOULD_SHOW_MODIFY_BUTTON = "should_show_modify_button";
    }

    private View rootView, englishWritingHeaderView;
    private TextView commonHeaderTitleTextView, englishWritingTitleTextView, englishWritingContent;
    private TextView assignTimeTextView;
    private TextView finishTimeTextView;
    private TextView headRightView;
    private TextView startWritingTextView, discussionCountTextView,showLimitCount;
    private LinearLayout layoutArticleLimit;
    private ListView listView;
    private int roleType = -1;
    private String studentId, sortStudentId, taskId;
    private String taskType;
    private StudyTask studyTask;
    private boolean isNeedRefresh=true;
    private String[] childIdArray;//孩子id数组

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.layout_english_writing_completed, null);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getIntent();
        initViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isNeedRefresh) {
            refreshData();
        }
    }

    private void getIntent() {
        if (getArguments() != null) {
            Bundle bundle = getArguments();
            roleType = bundle.getInt(Constant.ROLETYPE);
            studentId = bundle.getString(Constant.STUDENTID);
            sortStudentId = bundle.getString(Constant.SORTSTUDENTID);
            taskId = bundle.getString(Constant.TASKID);
            taskType = bundle.getString(Constant.TASKTYPE);
            if (roleType == RoleType.ROLE_TYPE_PARENT) {
                //得到孩子Id数组
                childIdArray = bundle.getStringArray(HomeworkMainFragment.Constants.
                        EXTRA_CHILD_ID_ARRAY);
            }
        }
    }

    private void refreshData() {
        loadViews();
    }

    void initViews() {
        //标题
        commonHeaderTitleTextView = (TextView) findViewById(R.id.contacts_header_title);
        if (commonHeaderTitleTextView != null) {
            commonHeaderTitleTextView.setText(R.string.english_writing);
        }

        englishWritingHeaderView = findViewById(R.id.layout_english_writing_assign_homework);
        englishWritingHeaderView.setOnClickListener(this);

        //作业标题
        englishWritingTitleTextView = (TextView) englishWritingHeaderView.findViewById(R.id.tv_title);

        //布置时间
        assignTimeTextView = (TextView) englishWritingHeaderView.findViewById(R.id.tv_start_time);

        //完成时间
        finishTimeTextView = (TextView) englishWritingHeaderView.findViewById(R.id.tv_end_time);
        //作业的内容
        englishWritingContent = (TextView) findViewById(R.id.tv_content);
        //标题栏头部的分享按钮
        headRightView= (TextView) findViewById(R.id.contacts_header_right_btn);
        if (headRightView!=null){
            headRightView.setOnClickListener(this);
            //进来直接可以看见分享
            headRightView.setVisibility(View.VISIBLE);
            headRightView.setText(getString(R.string.share));
            headRightView.setTextColor(getResources().getColor(R.color.text_green));
        }
        //是否要显示限制字数的数量
        layoutArticleLimit= (LinearLayout) findViewById(R.id.article_num_count);
        showLimitCount= (TextView) findViewById(R.id.show_limit_range);

        //开始写作按钮，完成后不显示。
        startWritingTextView = (TextView) findViewById(R.id.writing_btn);
        if (startWritingTextView != null) {
            startWritingTextView.setOnClickListener(this);
        }
        //讨论
        discussionCountTextView = (TextView) findViewById(R.id.discussion_btn);
        if (discussionCountTextView != null) {
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

                    //显示
                    TextView textView = (TextView) view.findViewById(R.id.tv_title);
                    if (textView!=null){
                        if (studyTask!=null){
                            textView.setText(studyTask.getTaskTitle());
                        }
                    }
                    //英文写作没有作业类型 隐藏显示的缩略图
                    //作业图片布局
                    View iconLayout = view.findViewById(R.id.layout_icon);
                    if (iconLayout!=null){
                        iconLayout.setVisibility(View.GONE);
                    }

                   /* //分割线
                    View splitView = view.findViewById(R.id.layout_split);
                    if (splitView != null){
                        //家长和学生需要显示分割线
                        if (data.isNeedSplit()){
                            splitView.setVisibility(View.VISIBLE);
                        }else {
                            splitView.setVisibility(View.GONE);
                        }
                    }*/

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
                            //进入学生提交的作业之英文写作详情界面
                        enterEnglishWritingDetails(data);
                    }
                }
                protected void updateLookTaskStatus(int commitTaskId, boolean isRead) {
                    //只有发布的老师查看学生作业才调,过滤班主任。
                    if (!isRead) {
                        updateStatus(commitTaskId);
                    }
                }
            };

            setCurrAdapterViewHelper(listView, listViewHelper);
        }
    }
    /**
     * 进入英文写作的详情界面
     * @param data
     */
    private void  enterEnglishWritingDetails(CommitTask data){
        Intent intent =new Intent(getActivity(), EnglishWritingCommentDetailsActivity.class);
        Bundle bundle=getArguments();
        if (bundle != null) {
            //复用之前的bundle，需要先清除之前的老数据。
            if (bundle.containsKey(Constant.STUDENTID)) {
                bundle.remove(Constant.STUDENTID);
            }
            if (bundle.containsKey(Constant.SORTSTUDENTID)) {
                bundle.remove(Constant.SORTSTUDENTID);
            }
            String studentId = data.getStudentId();
            if (!TextUtils.isEmpty(studentId)) {
                bundle.putString(Constant.STUDENTID, studentId);
                bundle.putString(Constant.SORTSTUDENTID, studentId);
                boolean isTaskBelongsToChildrenOrOwner = isTaskBelongsToChildrenOrOwner(studentId);
                bundle.putBoolean(Constant.IS_TASK_BELONGS_TO_CHILDREN_OR_OWNER,
                        isTaskBelongsToChildrenOrOwner);
            }
            intent.putExtras(bundle);
        }
        getActivity().startActivityForResult(intent,CampusPatrolPickerFragment
                .REQUEST_CODE_ENGLISH_WRITING_COMMIT);
    }

    /**
     * 判断作业是否属于自己的或者是孩子的
     * @return
     * @param studentId 学生id
     */
    private boolean isTaskBelongsToChildrenOrOwner(String studentId){

        boolean isTaskBelongsToChildrenOrOwner = false;
        //家长需要判断孩子是不是自己的
        if (roleType == RoleType.ROLE_TYPE_STUDENT){
            if (!TextUtils.isEmpty(studentId)
                    && !TextUtils.isEmpty(getMemeberId())
                    && studentId.equals(getMemeberId())){
                //是自己的可以编辑
                isTaskBelongsToChildrenOrOwner = true;
            }
        }else if (roleType == RoleType.ROLE_TYPE_PARENT){
            if (childIdArray != null && childIdArray.length > 0){
                for (int i = 0; i < childIdArray.length ; i++) {
                    String childId = childIdArray[i];
                    if (!TextUtils.isEmpty(childId)
                            && !TextUtils.isEmpty(studentId)
                            && childId.equals(studentId)){
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
     * 拉取数据
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

    private void updateViews(HomeworkCommitObjectResult result) {
        updateEnglishWritingDetail(result);
        HomeworkCommitObjectInfo homeworkCommitObjectInfo = result.getModel().getData();
        if (homeworkCommitObjectInfo != null) {
            studyTask = homeworkCommitObjectInfo.getTaskInfo();
        }

        List<CommitTask> list = result.getModel().getData().getListCommitTask();
        if (list == null || list.size() <= 0) {
            return;
        } else {
            int containsCount = 0;
            for (int i = 0 ; i < list.size(); i++){
                CommitTask data = list.get(i);
                if (data != null){
                    //后台返回数据默认学生自己或者是家长的孩子排在上面，下面是其他的学生或者其他的家长的孩子。
                    if (roleType == RoleType.ROLE_TYPE_STUDENT){
                        boolean isContains = false;
                        isContains = !TextUtils.isEmpty(data.getStudentId()) &&
                                data.getStudentId().equals(getMemeberId()) ;
                        if (isContains){
                            containsCount ++;
                        }
                        if (containsCount > 0 && !isContains){
                            //其他的学生
                            data.setNeedSplit(true);
                            break;
                        }

                    }else if (roleType == RoleType.ROLE_TYPE_PARENT){
                        boolean isContains = false;
                        isContains = !TextUtils.isEmpty(data.getStudentId()) && (
                                !TextUtils.isEmpty(sortStudentId) && sortStudentId
                                        .contains(data.getStudentId())) ;
                        if (isContains){
                            containsCount ++;
                        }
                        if (containsCount > 0 && !isContains){
                            //其他家长的孩子
                            data.setNeedSplit(true);
                            break;
                        }
                    }
                }
            }
            getCurrAdapterViewHelper().setData(list);
        }
    }

    private void updateEnglishWritingDetail(HomeworkCommitObjectResult result) {
        HomeworkCommitObjectInfo data = result.getModel().getData();
        if (data != null) {
            studyTask = data.getTaskInfo();
            //作业标题
            englishWritingTitleTextView.setText(studyTask.getTaskTitle());
            //布置时间
            assignTimeTextView.setText(getString(R.string.assign_date_string, DateUtils.getDateStr(studyTask
                    .getStartTime(), 0) + "-" + DateUtils.getDateStr(studyTask.getStartTime(), 1) + "-" +
                    DateUtils
                            .getDateStr(studyTask.getStartTime(), 2)));
            //完成时间
            finishTimeTextView.setText(getString(R.string.finish_date_string, DateUtils.getDateStr(studyTask
                    .getEndTime(), 0) + "-" + DateUtils.getDateStr(studyTask.getEndTime(), 1) + "-" + DateUtils
                    .getDateStr(studyTask.getEndTime(), 2)));
            //作业要求
            englishWritingContent.setText(studyTask.getWritingRequire());
            //更新讨论数量
            discussionCountTextView.setText(getString(R.string.discussion, String.valueOf(studyTask
                    .getCommentCount())));
            //判断是否要显示显示字数的数量
            int maxLimit=studyTask.getWordCountMax();
            if (maxLimit>=1){
                layoutArticleLimit.setVisibility(View.VISIBLE);
                showLimitCount.setText(""+studyTask.getWordCountMin()+"  -  "+studyTask
                        .getWordCountMax());
            }
            List<CommitTask> commitTask=data.getListCommitTask();
            boolean flag=true;
            if (commitTask.size()>0){
                for(int i=0;i<commitTask.size();i++){
                    String studentIdFromTask=commitTask.get(i).getStudentId();
                    if (studentId.equals(studentIdFromTask)){
                        startWritingTextView.setVisibility(View.INVISIBLE);
//                        headRightView.setVisibility(View.VISIBLE);
//                        headRightView.setText(getString(R.string.share));
//                        headRightView.setTextColor(getResources().getColor(R.color.text_green));
                        flag=false;
                    }
                }
            }
            if (flag){
                startWritingTextView.setVisibility(View.VISIBLE);
//                headRightView.setVisibility(View.GONE);
            }
        }

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


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.writing_btn) {
            //开始写作
            enterArticleDetails();
        } else if (v.getId() == R.id.discussion_btn) {
            //进入原始的话题讨论页面
            enterTopicDiscussionActivity();
        }else if (v.getId()==R.id.contacts_header_right_btn) {
            //分享
            share();
        }else {
            super.onClick(v);
        }
    }

    /**
     * 进入讨论的界面
     */
    private void enterTopicDiscussionActivity() {
        Intent intent = new Intent(getActivity(), TopicDiscussionActivity.class);
        Bundle bundle=new Bundle();
        bundle.putInt("TaskId", Integer.valueOf(taskId));
        bundle.putInt("roleType", roleType);
        bundle.putString("fromType", "commitHomework");
        intent.putExtras(bundle);
        startActivityForResult(intent, CampusPatrolPickerFragment.REQUEST_CODE_DISCUSSION_TOPIC);
    }

    /**
     * 进入分享的界面
     */
    private void share() {
        if (studyTask == null){
            return;
        }
        ShareInfo shareInfo = new ShareInfo();
        String title = studyTask.getTaskTitle();
        String url = studyTask.getShareUrl();
        String thumbnail = studyTask.getResThumbnailUrl();
        String schoolName = studyTask.getSchoolName();
        String className = studyTask.getClassName();
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

    /**
     * 进入学生写作的详情界面
     */
    private void enterArticleDetails() {
        Intent intent = new Intent(getActivity(), EnglishWritingBuildActivity.class);
        intent.putExtra("studyTask",studyTask);
        Bundle bundle = getArguments();
        if (bundle != null) {
            //复用之前的bundle，需要先清除之前的老数据。
            if (bundle.containsKey(Constant.STUDENTID)) {
                bundle.remove(Constant.STUDENTID);
            }
            if (bundle.containsKey(Constant.SORTSTUDENTID)) {
                bundle.remove(Constant.SORTSTUDENTID);
            }
            if (!TextUtils.isEmpty(studentId)) {
                //还原studentId,单个孩子。
                bundle.putString(Constant.STUDENTID, studentId);
                bundle.putString(Constant.SORTSTUDENTID, studentId);
                boolean isTaskBelongsToChildrenOrOwner = isTaskBelongsToChildrenOrOwner(studentId);
                bundle.putBoolean(Constant.IS_TASK_BELONGS_TO_CHILDREN_OR_OWNER,
                        isTaskBelongsToChildrenOrOwner);
            }
            intent.putExtras(bundle);
        }
        getActivity().startActivityForResult(intent,CampusPatrolPickerFragment
                .REQUEST_CODE_ENGLISH_WRITING_COMMIT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            if (requestCode == CampusPatrolPickerFragment.REQUEST_CODE_DISCUSSION_TOPIC) {
                //讨论话题
                if (TopicDiscussionFragment.hasCommented()) {
                    TopicDiscussionFragment.setHasCommented(false);
                    //需要刷新
                    refreshData();
                }
            }
        }
        if (data!=null){
            if (requestCode==CampusPatrolPickerFragment.REQUEST_CODE_ENGLISH_WRITING_COMMIT){
                isNeedRefresh=data.getBooleanExtra("isNeedRefresh",true);
            }
        }
    }

}
