package com.galaxyschool.app.wawaschool.fragment;

import android.app.Activity;
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

import com.galaxyschool.app.wawaschool.EnglishWritingBuildActivity;
import com.galaxyschool.app.wawaschool.EnglishWritingCommentBySentenceActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.DateUtils;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.CommitTask;
import com.galaxyschool.app.wawaschool.pojo.HomeworkCommitObjectInfo;
import com.galaxyschool.app.wawaschool.pojo.HomeworkCommitObjectResult;
import com.galaxyschool.app.wawaschool.pojo.StudyTask;
import com.galaxyschool.app.wawaschool.pojo.StudyTaskCommentDiscussPerson;
import com.galaxyschool.app.wawaschool.pojo.StudyTaskCommentDiscussPersonResult;
import com.galaxyschool.app.wawaschool.pojo.StudytaskComment;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by E450 on 2017/3/14.
 * 通用的英文写作详情fragment---自动点评
 */

public class AutoCommentTabsFragment extends ContactsListFragment {
    public static String TAG = AutoCommentTabsFragment.class.getSimpleName();
    private View rootView;
    private TextView commonHeaderTitleTextView;
    private ImageView commonHeaderRightImageView;
    private TextView modifiedCountTextView;
    private TextView wordsCountTextView;
    private TextView scoreTextView;
    private TextView contentTextView;
    private TextView autoCommentTabTextView, personalCommentTabTextView,
            compositionRequirementsTabTextView;

    private int roleType = -1;

    //自动点评
    private View autoCommentView;
    private TextView autoCommentTitleTextView,autoCommentContentTextView,commentBySentenceTextView;

    //人工点评
    private View personalCommentView;
    private ListView listView;

    //作文要求
    private View compositionRequirementsView;
    private TextView compositionRequirementsTitleTextView;
    //tab切换的下标
    private int tabIndex=0;
    private String studentId,sortStudentId,taskId;
    private String correctResult;//批改网返回的json数据
    private String score,wordCount,commitTimes;
    private CommitTask commitTask;
    //任务
    private StudyTask studyTask;
    private boolean isTaskBelongsToChildrenOrOwner;//判断是否是自己（孩子）的作业
    private boolean shouldShowModifyButton;//是否应该显示修改按钮
    private static boolean hasContentChanged;
    private boolean isHistoryClass;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.layout_common_english_writing_details_fragment, null);
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

    @Override
    public void onResume() {
        super.onResume();
    }

    void initViews() {
        if (getArguments() != null) {
            roleType = getArguments().getInt(EnglishWritingCompletedFragment.Constant.ROLETYPE);
            taskId = getArguments().getString(EnglishWritingCompletedFragment.Constant.TASKID);
            studentId = getArguments().getString(EnglishWritingCompletedFragment.Constant.
                    STUDENTID);
            sortStudentId = getArguments().getString(EnglishWritingCompletedFragment.Constant
                    .SORTSTUDENTID);
            isTaskBelongsToChildrenOrOwner =getArguments().getBoolean(
                    EnglishWritingCompletedFragment.Constant.IS_TASK_BELONGS_TO_CHILDREN_OR_OWNER);
            isHistoryClass = getArguments().getBoolean(ActivityUtils.EXTRA_IS_HISTORY_CLASS);
        }
        View headerView = findViewById(R.id.contacts_header_layout);
        if (headerView != null){
            headerView.setVisibility(View.GONE);
        }

        //标题
        commonHeaderTitleTextView = (TextView) findViewById(R.id.contacts_header_title);
        //编辑图标
        commonHeaderRightImageView = (ImageView) findViewById(R.id.contacts_header_right_ico);
        if (commonHeaderRightImageView != null) {
            commonHeaderRightImageView.setVisibility(View.INVISIBLE);
            commonHeaderRightImageView.setScaleType(ImageView.ScaleType.FIT_XY);
            commonHeaderRightImageView.setBackgroundResource(R.drawable.english_writing_new);
            commonHeaderRightImageView.setOnClickListener(this);
        }
        //改统计
        modifiedCountTextView = (TextView) findViewById(R.id.tv_count_modify);

        //字统计
        wordsCountTextView = (TextView) findViewById(R.id.tv_count_word);

        //分数
        scoreTextView = (TextView) findViewById(R.id.tv_score);

        //内容
        contentTextView = (TextView) findViewById(R.id.tv_content);

        //自动点评
        autoCommentTabTextView = (TextView) findViewById(R.id.left_tab);
        autoCommentTabTextView.setOnClickListener(this);

        //人工点评
        personalCommentTabTextView = (TextView) findViewById(R.id.middle_tab);
        personalCommentTabTextView.setOnClickListener(this);

        //作文要求
        compositionRequirementsTabTextView = (TextView) findViewById(R.id.right_tab);
        compositionRequirementsTabTextView.setOnClickListener(this);

        //自动点评---
        autoCommentView = findViewById(R.id.layout_auto_comment);
        autoCommentTitleTextView = (TextView) findViewById(R.id.tv_title_auto_comment);
        autoCommentContentTextView = (TextView) findViewById(R.id.tv_content_auto_comment);
        commentBySentenceTextView = (TextView) findViewById(R.id.tv_comment_by_sentence);
        if (commentBySentenceTextView != null){
            //按句点评
            if (isHistoryClass){
                commentBySentenceTextView.setVisibility(View.GONE);
            } else {
                commentBySentenceTextView.setVisibility(View.VISIBLE);
            }
            commentBySentenceTextView.setOnClickListener(this);
        }

        //人工点评---
        personalCommentView = findViewById(R.id.layout_personal_comment);
        listView = (ListView) findViewById(R.id.contacts_list_view);

        //作文要求---
        compositionRequirementsView = findViewById(R.id.layout_article_request);
        compositionRequirementsTitleTextView = (TextView) findViewById(R.id.tv_title_article_request);

        initTabs();
        //下拉刷新
        PullToRefreshView pullToRefreshView = (PullToRefreshView) findViewById(
                R.id.contacts_pull_to_refresh);
        setPullToRefreshView(pullToRefreshView);

        if (listView != null) {

            AdapterViewHelper helper = new AdapterViewHelper(getActivity(),listView
                    ,R.layout.item_expendlistview_group_comment) {

                @Override
                public void loadData() {
                    loadCommonData();
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position,convertView, parent);
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

                    View devider = (View) view.findViewById(R.id.parent_children_devider);
                    if(data.getChildren().size()>0){
                        devider.setVisibility(View.VISIBLE);
                    }else{
                        devider.setVisibility(View.GONE);
                    }

                    //头部分隔线
                    View topLineView = view.findViewById(R.id.top_line);
                    if (topLineView != null){
                        if (position == 0){
                            topLineView.setVisibility(View.GONE);
                        }else {
                            topLineView.setVisibility(View.VISIBLE);
                        }
                    }
                    ImageView imageView = (ImageView) view.findViewById(R.id.head_img);
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
                    TextView textView = (TextView) view.findViewById(R.id.comment_name);
                    if (textView != null) {
                        textView.setText(data.getCommentName());
                    }
                    textView = (TextView) view.findViewById(R.id.comment_time);
                    if (textView != null) {
                        textView.setText(DateUtils.getDateStr(data.getCommentTime(),
                                DateUtils.DATE_PATTERN_yyyy_MM_dd_HH_MM_SS,
                                DateUtils.DATE_PATTERN_yyyy_MM_dd_HH_MM));
                    }
                    textView = (TextView) view.findViewById(R.id.comment_cotent);
                    if (textView != null) {
                        textView.setText(data.getComments());
                    }

                    //隐藏回复和点赞
                    View praiseView = view.findViewById(R.id.layout_praise);
                    if (praiseView != null){
                        praiseView.setVisibility(View.GONE);
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
     * 初始化tabs
     */
    private void initTabs() {
        //显示自动点评，隐藏人工点评和作文要求。
        autoCommentView.setVisibility(View.VISIBLE);
        personalCommentView.setVisibility(View.GONE);
        compositionRequirementsView.setVisibility(View.GONE);
    }

    /**
     * 拉取数据
     */
    private void loadCommonData() {
        loadCommitDetails();
    }


    private void loadViews() {
        loadCommonData();
    }

    /**
     * 拉取点评记录
     * @param taskId
     */
    private void loadCommentRecords(String taskId) {
        if (TextUtils.isEmpty(taskId)){
            return;
        }
        Map<String, Object> params = new HashMap();
        params.put("TaskId", taskId);
        //视情况:当不传或传0的时候为任务讨论，当传1时为英文写作的老师点评
        params.put("Type",1);

        RequestHelper.sendPostRequest(getActivity(),
                ServerUrl.GET_TASK_COMMENT_LIST_URL, params,
                new RequestHelper.RequestDataResultListener<StudyTaskCommentDiscussPersonResult>
                        (getActivity(),StudyTaskCommentDiscussPersonResult.class) {
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
                        //判断是否可以编辑
                        judgeCanEdit(personData);
                        if (personData != null){
                            List<StudytaskComment> data = personData.getCommentList();
                            if (data != null) {
                                getCurrAdapterViewHelper().setData(data);
                            }
                        }
                    }
                });

    }
    /**
     * 根据条件判断是否可以编辑
     * @param personData
     */
    private void judgeCanEdit(StudyTaskCommentDiscussPerson personData){
        boolean isCommentRecordExists = false;//是否有点评记录
        if(personData!=null){
            List<StudytaskComment> data = personData.getCommentList();
            if (data!=null&&data.size()>0){
                //有点评记录
                isCommentRecordExists = true;
            }
        }
        //判断是否允许修改
        shouldShowModifyButton = !isCommentRecordExists && isTaskBelongsToChildrenOrOwner;
        if (shouldShowModifyButton){
            commonHeaderRightImageView.setVisibility(View.VISIBLE);
        }else {
            commonHeaderRightImageView.setVisibility(View.INVISIBLE);
        }
    }
    /**
     * 拉取提交详情数据
     */
    private void loadCommitDetails() {

        Map<String, Object> params = new HashMap();
        //任务Id，必填
        if (!TextUtils.isEmpty(taskId)) {
            params.put("TaskId", taskId);
        }
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
        if (info != null){
            studyTask = info.getTaskInfo();
            List<CommitTask> list = info.getListCommitTask();
            if (list == null || list.size() <= 0) {
                return;
            } else {
                updateHeaderView(studyTask,list);
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
        if (commonHeaderTitleTextView != null) {
            commonHeaderTitleTextView.setText(task.getTaskTitle());
        }
        //更新头部内容
        //改
        commitTimes = String.valueOf(commitTask.getModifyTimes());
        modifiedCountTextView.setText(commitTimes);
        //字
        wordCount = String.valueOf(commitTask.getWordCount());
        wordsCountTextView.setText(wordCount);
        //分数
        score = commitTask.getScore();
        scoreTextView.setText(score);
        //内容
        contentTextView.setText(commitTask.getWritingContent());
        //自动点评
        String correctResult = commitTask.getCorrectResult();
        if (!TextUtils.isEmpty(correctResult)){
            this.correctResult = correctResult;
            parseCorrectResultJSON(correctResult);
        }
        //作文要求

        if (task != null) {
            compositionRequirementsTitleTextView.setText(task.getWritingRequire());
        }
        //调用点评记录
        loadCommentRecords(String.valueOf(commitTask.getCommitTaskId()));
    }

    private void parseCorrectResultJSON(String correctResult) {

        try {
            JSONObject root = new JSONObject(correctResult);
            if (root != null){
                JSONObject data = root.optJSONObject("data");
                if (data != null) {
                    String comment = data.optString("comment");
                    if (!TextUtils.isEmpty(comment)) {
                        //自动点评的内容
                        autoCommentContentTextView.setText(comment);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.left_tab) {
            //自动点评
            if (tabIndex!=0) {
                switchTab(0);
            }

        } else if (v.getId() == R.id.middle_tab) {
            //人工点评
            if (tabIndex!=1){
                switchTab(1);
            }

        }else if (v.getId() == R.id.right_tab) {
            //作文要求
            if (tabIndex!=2) {
                switchTab(2);
            }

        } else if (v.getId() == R.id.tv_comment_by_sentence) {
            //按句点评
            enterAccordingToSentence();

        }else if (v.getId() == R.id.contacts_header_right_ico) {
            //编辑按钮 当前的编辑可以对当前的已经提交的作文重新的提交
            enterArticleDetails();

        }else if (v.getId()==R.id.contacts_header_left_btn){
            Intent intent=new Intent();
            intent.putExtra("isNeedRefresh",true);
            getActivity().setResult(Activity.RESULT_OK,intent);
            getActivity().finish();
        }
    }

    /**
     * 进入学生写作的详情界面
     */
    private void enterArticleDetails(){
        Intent intent=new Intent(getActivity(), EnglishWritingBuildActivity.class);
        Bundle bundle=new Bundle();
        if (commitTask!=null){
            bundle.putSerializable(EnglishWritingCompletedFragment.Constant.COMMITTASK,commitTask);
        }
        if (studyTask != null){
            bundle.putSerializable("studyTask",studyTask);
        }
        bundle.putString(EnglishWritingCompletedFragment.Constant.TASKID,taskId);
        bundle.putString(EnglishWritingCompletedFragment.Constant.STUDENTID,studentId);
        bundle.putString(EnglishWritingCompletedFragment.Constant.SORTSTUDENTID,sortStudentId);
        intent.putExtras(bundle);
        startActivity(intent);
    }
    /**
     * 跳转到按句子点评的界面
     */
    private void enterAccordingToSentence(){
        Intent intent=new Intent(getActivity(), EnglishWritingCommentBySentenceActivity.class);
        Bundle bundle = getArguments();
        //是否应该显示修改按钮
        bundle.putBoolean(EnglishWritingCompletedFragment.Constant.SHOULD_SHOW_MODIFY_BUTTON,
                shouldShowModifyButton);
        intent.putExtras(bundle);
        startActivityForResult(intent, CampusPatrolPickerFragment
                .REQUEST_CODE_ENGLISH_WRITING_COMMENT_DETAILS);
    }
    /**
     * 自动点评、人工点评以及作文要求之间的切换显示
     * @param index
     */
    private void switchTab(int index) {
        if (index < 0){
            return;
        }
        tabIndex=index;
        switch (index){
            //自动点评
            case 0 :
                personalCommentView.setVisibility(View.GONE);
                compositionRequirementsView.setVisibility(View.GONE);
                autoCommentView.setVisibility(View.VISIBLE);
                autoCommentTabTextView.setEnabled(false);
                personalCommentTabTextView.setEnabled(true);
                compositionRequirementsTabTextView.setEnabled(true);
                break;
            //人工点评
            case 1 :
                autoCommentView.setVisibility(View.GONE);
                compositionRequirementsView.setVisibility(View.GONE);
                personalCommentView.setVisibility(View.VISIBLE);
                autoCommentTabTextView.setEnabled(true);
                personalCommentTabTextView.setEnabled(false);
                compositionRequirementsTabTextView.setEnabled(true);
                break;
            //作业要求
            case 2 :
                autoCommentView.setVisibility(View.GONE);
                personalCommentView.setVisibility(View.GONE);
                compositionRequirementsView.setVisibility(View.VISIBLE);

                autoCommentTabTextView.setEnabled(true);
                personalCommentTabTextView.setEnabled(true);
                compositionRequirementsTabTextView.setEnabled(false);
                break;

            default:
                break;
        }
    }

    public static void setHasContentChanged(boolean hasContentChanged) {
        AutoCommentTabsFragment.hasContentChanged = hasContentChanged;
    }

    public static boolean hasContentChanged() {
        return hasContentChanged;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null){
            if (requestCode == CampusPatrolPickerFragment
                    .REQUEST_CODE_ENGLISH_WRITING_COMMENT_DETAILS){
                if (EnglishWritingCommentBySentenceFragment.hasContentChanged()) {
                    EnglishWritingCommentBySentenceFragment.setHasContentChanged(false);
                    //通知刷新
                    setHasContentChanged(true);
                    //刷新数据
                    refreshData();
                }
            }
        }
    }
}
