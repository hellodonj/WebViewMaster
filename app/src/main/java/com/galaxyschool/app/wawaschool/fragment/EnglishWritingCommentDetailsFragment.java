package com.galaxyschool.app.wawaschool.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.EnglishWritingBuildActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.MyFragmentPagerTitleAdapter;
import com.galaxyschool.app.wawaschool.pojo.RoleType;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.CommitTask;
import com.galaxyschool.app.wawaschool.pojo.HomeworkCommitObjectInfo;
import com.galaxyschool.app.wawaschool.pojo.HomeworkCommitObjectResult;
import com.galaxyschool.app.wawaschool.pojo.StudyTask;
import com.galaxyschool.app.wawaschool.pojo.StudyTaskCommentDiscussPerson;
import com.galaxyschool.app.wawaschool.pojo.StudyTaskCommentDiscussPersonResult;
import com.galaxyschool.app.wawaschool.pojo.StudytaskComment;
import com.galaxyschool.app.wawaschool.views.MyViewPager;
import com.galaxyschool.app.wawaschool.views.PagerSlidingTabStrip;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 英文写作---评论详情页面
 */

public class EnglishWritingCommentDetailsFragment extends ContactsListFragment {

    public static final String TAG = EnglishWritingCommentDetailsFragment.class.getSimpleName();

    private View rootView;
    private TextView commonHeaderTitleTextView;
    private ImageView commonHeaderRightImageView;
    private TextView modifiedCountTextView;
    private TextView wordsCountTextView;
    private TextView scoreTextView;
    private TextView contentTextView;

    private int roleType = -1;
    private String studentId, sortStudentId, taskId;
    //学生提交的作业
    private CommitTask commitTask;
    //任务
    private StudyTask studyTask;
    private boolean isTaskBelongsToChildrenOrOwner;//判断是否是自己（孩子）的作业
    private boolean shouldShowModifyButton;//是否应该显示修改按钮

    //tab切换新需求
    private MyViewPager viewPager;
    private PagerSlidingTabStrip pagerSlidingTabStrip;
    private MyFragmentPagerTitleAdapter adapter;
    private List<Fragment> fragmentList = new ArrayList<>();
    private List<String> titleList = new ArrayList<>();
    private boolean isHistoryClass;
    private boolean isOnlineReporter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.layout_english_writing_comment_details, null);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshData();
    }

    private void refreshData() {
        loadViews();
    }

    void initViews() {
        if (getArguments() != null) {
            roleType = getArguments().getInt("roleType");
            taskId = getArguments().getString(EnglishWritingCompletedFragment.Constant.TASKID);
            studentId = getArguments().getString(EnglishWritingCompletedFragment.Constant.
                    STUDENTID);
            sortStudentId = getArguments().getString(EnglishWritingCompletedFragment.Constant
                    .SORTSTUDENTID);
            isTaskBelongsToChildrenOrOwner = getArguments().getBoolean(
                    EnglishWritingCompletedFragment.Constant.IS_TASK_BELONGS_TO_CHILDREN_OR_OWNER);
            isHistoryClass = getArguments().getBoolean(ActivityUtils.EXTRA_IS_HISTORY_CLASS);
            isOnlineReporter = getArguments().getBoolean("isOnlineReporter", false);
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
        initViewPager();
    }

    private void initViewPager() {
        viewPager = (MyViewPager) findViewById(R.id.view_pager);
        //设置是否可以滑动
        viewPager.setCanScroll(true);
        pagerSlidingTabStrip = (PagerSlidingTabStrip) findViewById(R.id.pager_sliding_tab_strip);

        //初始化标题
        titleList.add(getString(R.string.auto_comment));
        titleList.add(getString(R.string.str_teacher_review));
//        titleList.add(getString(R.string.task_requirements));

        //初始化fragment
        Fragment fragment = null;

        //自动点评
        fragment = new AutoCommentTabsFragment();
        fragment.setArguments(getEnglishWritingBundleInfo());
        fragmentList.add(fragment);

        //人工点评
        fragment = new PersonalCommentTabsFragment();
        fragment.setArguments(getEnglishWritingBundleInfo());
        fragmentList.add(fragment);

//        //作文要求
//        fragment = new CompositionRequirementsTabsFragment();
//        fragment.setArguments(getEnglishWritingBundleInfo());
//        fragmentList.add(fragment);

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
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        //和viewpager保持联动
        pagerSlidingTabStrip.setViewPager(viewPager);
        initTabItemViews();
    }

    /**
     * 获得Bundle信息
     *
     * @return
     */
    private Bundle getEnglishWritingBundleInfo() {
        Bundle args = getArguments();
        return args;
    }

    /**
     * 初始化Tabs
     */
    private void initTabItemViews() {
        if (pagerSlidingTabStrip != null) {
            int count = pagerSlidingTabStrip.getTabCount();
            if (count > 0) {
                if (count == 1) {
                    LinearLayout layout = pagerSlidingTabStrip.getTabsContainer();
                    if (layout != null) {
                        TextView itemView = (TextView) layout.getChildAt(0);
                        if (itemView != null) {
                            itemView.setBackgroundResource(R.drawable.selector_bg_tab_task);
                        }
                    }
                } else if (count >= 2) {
                    LinearLayout layout = pagerSlidingTabStrip.getTabsContainer();
                    if (layout != null) {
                        for (int i = 0; i < count; i++) {
                            View itemView = layout.getChildAt(i);
                            if (itemView != null) {
                                if (i == 0) {
                                    //第一个条目
                                    itemView.setBackgroundResource(R.drawable.selector_bg_tab_task_left);
                                } else if (i == count - 1) {
                                    //最后一个条目
                                    itemView.setBackgroundResource(R.drawable.selector_bg_tab_task_right);
                                } else {
                                    //中间其他的条目
                                    itemView.setBackgroundResource(R.drawable.selector_bg_tab_task_middle);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 拉取数据
     */
    private void loadCommonData() {
        loadCommitDetails();
    }

    /**
     * 拉取点评记录
     *
     * @param taskId
     */
    private void loadCommentRecords(String taskId) {
        if (TextUtils.isEmpty(taskId)) {
            return;
        }
        Map<String, Object> params = new HashMap();
        params.put("TaskId", taskId);
        //视情况:当不传或传0的时候为任务讨论，当传1时为英文写作的老师点评
        params.put("Type", 1);

        RequestHelper.sendPostRequest(getActivity(),
                ServerUrl.GET_TASK_COMMENT_LIST_URL, params,
                new RequestHelper.RequestDataResultListener<StudyTaskCommentDiscussPersonResult>
                        (getActivity(), StudyTaskCommentDiscussPersonResult.class) {
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
                        //如果老师没有点评显示可以编辑
                        StudyTaskCommentDiscussPerson personData = result.getModel().getData();
                        //判断是否可以编辑
                        judgeCanEdit(personData);
                        if (personData != null) {
                            List<StudytaskComment> data = personData.getCommentList();
                            if (data != null) {
//                                getCurrAdapterViewHelper().setData(data);
                            }
                        }
                    }
                });

    }

    /**
     * 根据条件判断是否可以编辑
     *
     * @param personData
     */
    private void judgeCanEdit(StudyTaskCommentDiscussPerson personData) {
        boolean isCommentRecordExists = false;//是否有点评记录
        if (personData != null) {
            List<StudytaskComment> data = personData.getCommentList();
            if (data != null && data.size() > 0) {
                //有点评记录
                isCommentRecordExists = true;
            }
        }
        //判断是否允许修改
        shouldShowModifyButton = !isCommentRecordExists && isTaskBelongsToChildrenOrOwner;
        if (roleType == RoleType.ROLE_TYPE_TEACHER || isOnlineReporter){
            shouldShowModifyButton = false;
        }
        if (shouldShowModifyButton && !isHistoryClass) {
            commonHeaderRightImageView.setVisibility(View.VISIBLE);
        } else {
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
        if (info != null) {
            studyTask = info.getTaskInfo();
            List<CommitTask> list = info.getListCommitTask();
            if (list == null || list.size() <= 0) {
                return;
            } else {
                updateHeaderView(studyTask, list);
            }
        }
    }

    /**
     * *更新头部内容
     *
     * @param task
     * @param result
     */
    private void updateHeaderView(StudyTask task, List<CommitTask> result) {
        if (result == null || result.size() <= 0) {
            return;
        }
        //截取第一个
        commitTask = result.get(0);
        if (commitTask == null) {
            return;
        }

        //更新头部内容
        if (commonHeaderTitleTextView != null) {
            commonHeaderTitleTextView.setText(task.getTaskTitle());
        }
        //改
        modifiedCountTextView.setText(commitTask.getModifyTimes() + "");
        //字
        wordsCountTextView.setText(commitTask.getWordCount() + "");
        //分数
        scoreTextView.setText(commitTask.getScore());
        //内容
        contentTextView.setText(commitTask.getWritingContent());
        //自动点评
        String correctResult = commitTask.getCorrectResult();
        if (!TextUtils.isEmpty(correctResult)) {
            parseCorrectResultJSON(correctResult);
        }
        //调用点评记录
        loadCommentRecords(String.valueOf(commitTask.getCommitTaskId()));
    }

    private void parseCorrectResultJSON(String correctResult) {

        try {
            JSONObject root = new JSONObject(correctResult);
            if (root != null) {
                JSONObject data = root.optJSONObject("data");
                if (data != null) {
                    String comment = data.optString("comment");
                    if (!TextUtils.isEmpty(comment)) {
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadViews() {
        loadCommonData();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.contacts_header_right_ico) {
            //编辑按钮 当前的编辑可以对当前的已经提交的作文重新的提交
            enterArticleDetails();

        } else if (v.getId() == R.id.contacts_header_left_btn) {
            Intent intent = new Intent();
            intent.putExtra("isNeedRefresh", true);
            getActivity().setResult(Activity.RESULT_OK, intent);
            getActivity().finish();
        }
    }

    /**
     * 进入学生写作的详情界面
     */
    private void enterArticleDetails() {
        Intent intent = new Intent(getActivity(), EnglishWritingBuildActivity.class);
        Bundle bundle = new Bundle();
        if (commitTask != null) {
            bundle.putSerializable(EnglishWritingCompletedFragment.Constant.COMMITTASK, commitTask);
        }
        if (studyTask != null) {
            bundle.putSerializable("studyTask", studyTask);
        }
        bundle.putString(EnglishWritingCompletedFragment.Constant.TASKID, taskId);
        bundle.putString(EnglishWritingCompletedFragment.Constant.STUDENTID, studentId);
        bundle.putString(EnglishWritingCompletedFragment.Constant.SORTSTUDENTID, sortStudentId);
        intent.putExtras(bundle);
        getActivity().startActivityForResult(intent, CampusPatrolPickerFragment
                .REQUEST_CODE_ENGLISH_WRITING_COMMIT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            if (requestCode == CampusPatrolPickerFragment
                    .REQUEST_CODE_ENGLISH_WRITING_COMMENT_DETAILS) {
                if (EnglishWritingCommentBySentenceFragment.hasContentChanged()) {
                    EnglishWritingCommentBySentenceFragment.setHasContentChanged(false);
                    //刷新数据
                    refreshData();
                }
            }
        }
    }
}
