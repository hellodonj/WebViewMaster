package com.galaxyschool.app.wawaschool.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.UIUtils;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.CommitTask;
import com.galaxyschool.app.wawaschool.pojo.HomeworkCommitObjectInfo;
import com.galaxyschool.app.wawaschool.pojo.HomeworkCommitObjectResult;
import com.galaxyschool.app.wawaschool.pojo.RoleType;
import com.galaxyschool.app.wawaschool.pojo.StudyTask;
import com.galaxyschool.app.wawaschool.pojo.StudyTaskCommentDiscussPerson;
import com.galaxyschool.app.wawaschool.pojo.StudyTaskCommentDiscussPersonResult;
import com.galaxyschool.app.wawaschool.pojo.StudytaskComment;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;
import com.lqwawa.apps.views.ContainsEmojiEditText;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 英文写作---点评记录
 */

public class EnglishWritingCommentRecordFragment extends ContactsListFragment {

    public static final String TAG = EnglishWritingCommentRecordFragment.class.getSimpleName();

    private View rootView;
    private TextView commonHeaderTitleTextView;
    private TextView authorNameTextView;
    private TextView contentTextView;
    private ListView listView;
    private int roleType = -1;
    private View sendView;
    private TextView sendTextView;
    private ContainsEmojiEditText commentEditText;
    private String studentId,sortStudentId;
    private int taskId;//任务Id
    private CommitTask commitTask;
    private ImageView studentIcon;
    private TextView commitTimeTextView;
    private boolean isOnlineReporter;//判断是不是直播的主持人和小编
    private boolean isHistoryClass;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.layout_english_writing_comment_record, null);
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
            //单个孩子id
            studentId = getArguments().getString(EnglishWritingCompletedFragment.
                    Constant.STUDENTID);
            //学生Id串，以逗号分隔。
            sortStudentId = getArguments().getString(EnglishWritingCompletedFragment.
                    Constant.SORTSTUDENTID);
            //任务id
            taskId = getArguments().getInt(EnglishWritingCompletedFragment.
                    Constant.TASKID);
            isOnlineReporter = getArguments().getBoolean("isOnlineReporter",false);
            isHistoryClass = getArguments().getBoolean(ActivityUtils.EXTRA_IS_HISTORY_CLASS);
        }

        //标题
        commonHeaderTitleTextView = (TextView) findViewById(R.id.contacts_header_title);

        //头像
        studentIcon = (ImageView) findViewById(R.id.iv_student_icon);

        //提交时间
        commitTimeTextView = (TextView) findViewById(R.id.tv_commit_time);

        //作者
        authorNameTextView = (TextView) findViewById(R.id.tv_author_name);

        //内容
        contentTextView = (TextView) findViewById(R.id.tv_content);

        //只有发布作业的老师才能提交点评
        sendView = findViewById(R.id.send_talks_layout);
        //外边框
        View outBorderView = findViewById(R.id.layout_border);
        if (outBorderView != null){
            outBorderView.setBackgroundColor(getResources().getColor(R.color.main_bg_color));
        }
        //输入框
        commentEditText = (ContainsEmojiEditText) findViewById(R.id.edit_btn);
        if (commentEditText != null) {
            //设置背景色
            commentEditText.setBackgroundResource(R.drawable.gray_10dp_line_gray_color);
            commentEditText.setMaxlen(40);
        }
        //发送按钮
        sendTextView = (TextView) findViewById(R.id.send_btn);
        if (sendTextView != null) {
            sendTextView.setOnClickListener(this);
        }

        //下拉刷新
        PullToRefreshView pullToRefreshView = (PullToRefreshView) findViewById(
                R.id.contacts_pull_to_refresh);
        setPullToRefreshView(pullToRefreshView);
        listView = (ListView) findViewById(R.id.contacts_list_view);
        if (listView != null) {

            AdapterViewHelper helper = new AdapterViewHelper(getActivity(),listView
                    ,R.layout.item_commited_homework) {

                @Override
                public void loadData() {
                    loadCommonData();
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position,convertView, parent);
                    //灰色背景
                    view.setBackgroundColor(getResources().getColor(R.color.main_bg_color));
                    final StudytaskComment data = (StudytaskComment)getDataAdapter()
                            .getItem(position);
                    if (data == null){
                        return view;
                    }

                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) {
                        holder = new ViewHolder();
                    }
                    holder.data = data;

                    //头部分割线
                    View topLineView = view.findViewById(R.id.top_line);
                    if (topLineView != null){
                        topLineView.setVisibility(View.VISIBLE);
                    }

                    ImageView imageView = (ImageView) view.findViewById(R.id.iv_student_icon);
                    if (imageView != null) {
                        if (data.getCommentHeadPicUrl() != null) {
                            getThumbnailManager().displayUserIcon(AppSettings.getFileUrl(
                                    data.getCommentHeadPicUrl()), imageView);
                        }
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ActivityUtils.enterPersonalSpace(getActivity(),
                                        data.getCommentId());
                            }
                        });
                    }
                    TextView textView = (TextView) view.findViewById(R.id.tv_student_name);
                    if (textView != null) {
                        textView.setText(data.getCommentName());
                    }
                    textView = (TextView) view.findViewById(R.id.tv_commit_time);
                    if (textView != null) {
                        textView.setVisibility(View.VISIBLE);
                        String commitTime = data.getCommentTime();
                        if (!TextUtils.isEmpty(commitTime)) {
                            if (commitTime.contains(":")) {
                                //精确到分
                                commitTime = commitTime.substring(0, commitTime.lastIndexOf(":"));
                            }
                            textView.setText(commitTime);
                        }
                    }
                    textView = (TextView) view.findViewById(R.id.tv_title);
                    if (textView != null) {
                        textView.setText(data.getComments());
                    }

                    //隐藏作业图标
                    View iconLayout = view.findViewById(R.id.layout_icon);
                    if (iconLayout != null){
                        iconLayout.setVisibility(View.GONE);
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
                    StudytaskComment data = (StudytaskComment)holder.data;
                    if (data == null){
                        return;
                    }
                }
            };

            setCurrAdapterViewHelper(listView, helper);
        }
    }

    /**
     * 判断是否允许评论
     * @return
     * @param task
     */
    private boolean isAllowComment(StudyTask task){
        if (isHistoryClass){
            return false;
        }
        boolean isAllowComment = false;
        if (roleType == RoleType.ROLE_TYPE_TEACHER){
            if (task != null){
                String teacherId = task.getTaskCreateId();
                String memberId = getMemeberId();

                if (!TextUtils.isEmpty(teacherId)
                        && !TextUtils.isEmpty(memberId)
                        && teacherId.equals(memberId)){
                    isAllowComment = true;
                }
                if (isOnlineReporter){
                    isAllowComment = true;
                }
            }
        }
        return isAllowComment;
    }

    /**
     * 拉取数据
     */
    private void loadCommonData() {
        loadCommitDetails();
    }

    /**
     * 拉取提交详情数据
     */
    private void loadCommitDetails() {

        Map<String, Object> params = new HashMap();
        //任务Id，必填
        params.put("TaskId", taskId);
        //学生id，
        if (!TextUtils.isEmpty(studentId)) {
            params.put("StudentId", studentId);
        }
        //非必填,如果填写就把该学生对应的任务排在前面,支持多个ID排序,多个ID时用逗号分隔传值.
        //这里只拉取一个学生的
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
        HomeworkCommitObjectInfo info = result.getModel().getData();
        StudyTask task;
        if (info != null){
            task = info.getTaskInfo();
            List<CommitTask> list = info.getListCommitTask();
            if (list == null || list.size() <= 0) {
                return;
            } else {
                updateHeaderView(task,list);
            }
        }
    }

    /**
     **更新头部内容
     * @param task
     * @param result
     */
    private void updateHeaderView(StudyTask task, List<CommitTask> result) {
        if (result == null || result.size() <= 0) {
            return;
        }
        //截取第一个
        commitTask = result.get(0);
        if (commitTask == null){
            return;
        }
        //标题
        if (commonHeaderTitleTextView != null) {
            if (task != null) {
                commonHeaderTitleTextView.setText(task.getTaskTitle());
            }
        }

        //头像
        if (studentIcon != null){
            getThumbnailManager().displayThumbnailWithDefault(
                    AppSettings.getFileUrl(commitTask.getHeadPicUrl()), studentIcon,
                    R.drawable.default_user_icon);
        }

        //时间,精确到秒
        if (commitTimeTextView != null) {
            String commitTime = commitTask.getUpdateTime();
            if (TextUtils.isEmpty(commitTime)){
                commitTime = commitTask.getCommitTime();
            }
            commitTimeTextView.setText(commitTime);
        }

        //作者
        if (authorNameTextView != null){
            authorNameTextView.setText(commitTask.getStudentName());
        }

        //内容
        contentTextView = (TextView) findViewById(R.id.tv_content);
        if (contentTextView != null){
            contentTextView.setText(commitTask.getWritingContent());
        }

        //只有发布作业的老师才能提交点评
        if (isAllowComment(task)){
            sendView.setVisibility(View.VISIBLE);
        }else {
            sendView.setVisibility(View.GONE);
        }
        //调用点评记录
        loadTaskComments(commitTask.getCommitTaskId());
    }

    /**
     * 拉取评论接口
     */
    private void loadTaskComments(int commitTaskId) {

        Map<String, Object> params = new HashMap();
        //这里应该用已提交作业的id
        params.put("TaskId", commitTaskId);
        //视情况:当不传或传0的时候为任务讨论，当传1时为英文写作的老师点评
        params.put("Type",1);

        RequestHelper.sendPostRequest(getActivity(),
                ServerUrl.GET_TASK_COMMENT_LIST_URL, params,
                new DefaultPullToRefreshDataListener<StudyTaskCommentDiscussPersonResult>
                        (StudyTaskCommentDiscussPersonResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        StudyTaskCommentDiscussPersonResult result = getResult();
                        if (result == null || !result.isSuccess()
                                || result.getModel() == null) {
                            return;
                        }
                        StudyTaskCommentDiscussPerson personData = result.getModel().getData();
                        if (personData != null){
                            List<StudytaskComment> data = personData.getCommentList();
                            if (data != null) {
                                getCurrAdapterViewHelper().setData(data);
                            }
                        }
                    }
                });
    }

    private void loadViews() {
        loadCommonData();
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.send_btn) {
            //发送
            sendComment();

        } else {
            super.onClick(v);
        }
    }

    private void sendComment() {
        if (commitTask == null) return;
        String content = commentEditText.getText().toString();
        if (content.length() == 0) {
            TipsHelper.showToast(getActivity(),getString(R.string.pls_input_comment_content));
            return;
        }
        String commentId = null;
        String commentName = null;
        UserInfo userInfo = getUserInfo();
        if (userInfo != null) {
            commentId = userInfo.getMemberId();
            if(!TextUtils.isEmpty(userInfo.getRealName())) {
                commentName = userInfo.getRealName();
            } else {
                commentName = userInfo.getNickName();
            }
        }
        Map<String, Object> params = new HashMap();
        //英文写作需要传递commitTaskId
        params.put("TaskId", commitTask.getCommitTaskId());
        params.put("Comments", content);
        params.put("CommentId", commentId);
        params.put("CommentName", commentName);
        //视情况:当不传或传0的时候为任务讨论，当传1时为英文写作的老师点评
        params.put("Type",1);

//        当评论的对象是针对已存在的学生评论时必填，否则传空
//        params.put("ParentId", commitTask.getStudentId());
//        params.put("CommentToId", commitTask.getStudentId());
//        params.put("CommentToName", commitTask.getStudentName());

        RequestHelper.sendPostRequest(getActivity(),
                ServerUrl.ADD_TASK_COMMENT_URL, params,
                new RequestHelper.RequestDataResultListener<DataModelResult>(getActivity(),
                        DataModelResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        DataModelResult result = getResult();
                        if (result == null || !result.isSuccess()) {
                            TipsHelper.showToast(getActivity(),
                                    getString(R.string.upload_comment_error));
                            return;
                        }
                        TipsHelper.showToast(getActivity(),
                                getString(R.string.upload_comment_success));
                        UIUtils.hideSoftKeyboard1(getActivity(), commentEditText);
                        resetEditText();
                        //重新拉取一下数据
                        loadCommonData();
                    }
                });
    }

    /**
     * 重置输入框
     */
    private void resetEditText() {
        if (commentEditText != null){
            commentEditText.setText("");
            commentEditText.setHint(getString(R.string.say_something));
        }
    }
}
