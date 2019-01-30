package com.galaxyschool.app.wawaschool.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.galaxyschool.app.wawaschool.CompletedHomeworkListActivity;
import com.galaxyschool.app.wawaschool.EnglishWritingCommentRecordActivity;
import com.galaxyschool.app.wawaschool.HomeworkCommitActivity;
import com.galaxyschool.app.wawaschool.HomeworkFinishStatusActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.StudentFinishedHomeworkListActivity;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.*;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 作业完成状态列表
 */

public class HomeworkFinishStatusFragment extends ContactsListFragment {

    public static final String TAG = HomeworkFinishStatusFragment.class.getSimpleName();

    private View rootView;
    private String taskId;
    private String title;
    //已完成
    private View finishLayout;
    private TextView finishText;
    private ImageView finishImage;
    private GridView finishGridView;
    private boolean isFinishLayoutExpand = true;
    private String finishGridViewTag;
    //未完成
    private View unFinishLayout;
    private TextView unFinishText, unReadTip;
    private ImageView unFinishImage;
    private GridView unFinishGridView;
    private boolean isUnFinishLayoutExpand = true;
    private String unFinishGridViewTag;
    private boolean isFromEnglishWriting, isAllFinish;
    private static boolean hasContentChanged;
    private boolean isOwnerTask;
    private int roleType = -1;
    private int taskType = -1;
    private boolean isCampusPatrolTag;//校园巡查标识
    private boolean isSuperChildTask;
    private boolean isSuperThirdTask;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.homework_finish_status_list, null);
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
            isCampusPatrolTag = getArguments().getBoolean(CampusPatrolMainFragment
                    .IS_CAMPUS_PATROL_TAG);
            roleType = getArguments().getInt(HomeworkFinishStatusActivity.Constants.ROLE_TYPE);
            taskId = getArguments().getString(HomeworkFinishStatusActivity.Constants.TASK_ID);
            title = getArguments().getString(HomeworkFinishStatusActivity.Constants.TASK_TITLE);
            taskType = getArguments().getInt(HomeworkFinishStatusActivity.Constants.TASK_TYPE);
            isFromEnglishWriting = getArguments().getBoolean(HomeworkFinishStatusActivity.Constants
                    .IS_FROM_ENGLISH_WRITING, false);
            isOwnerTask = getArguments().getBoolean(HomeworkFinishStatusActivity.Constants
                    .IS_OWNER_TASK);
            isSuperChildTask = getArguments().getBoolean(HomeworkFinishStatusActivity.Constants
                    .EXTRA_IS_SUPER_CHILD_TASK);
            isSuperThirdTask = getArguments().getBoolean(HomeworkFinishStatusActivity.Constants
                    .EXTRA_IS_SUPER_THIRD_TASK);
        }


        //头布局
        View view = findViewById(R.id.contacts_header_layout);

        //标题
        TextView textView = (TextView) findViewById(R.id.contacts_header_title);
        if (textView != null) {
            textView.setText(title);
        }

        //右侧的布局
        textView = (TextView) findViewById(R.id.contacts_header_right_btn);
        if (textView != null) {
            textView.setVisibility(View.GONE);
            textView.setOnClickListener(this);
            textView.setTextAppearance(getActivity(), R.style.txt_wawa_big_green);
            textView.setText("");
        }


        //下拉刷新
//        PullToRefreshView pullToRefreshView = (PullToRefreshView) findViewById(
//                R.id.pull_to_refresh);
//        setPullToRefreshView(pullToRefreshView);

        //已完成布局
        finishLayout = findViewById(R.id.finish_status_contacts_list_item_layout);
        finishLayout.setOnClickListener(this);

        finishText = (TextView) findViewById(R.id.finish_status_contacts_item_title);
        finishText.setText(getString(R.string.finish_count, 0));

        finishImage = (ImageView) findViewById(R.id.finish_status_contacts_item_arrow);

        //未完成布局
        unFinishLayout = findViewById(R.id.unfinish_status_contacts_list_item_layout);
        unFinishLayout.setOnClickListener(this);

        unFinishText = (TextView) findViewById(R.id.unfinish_status_contacts_item_title);
        unFinishText.setText(getString(R.string.unfinish_count, 0));

        unFinishImage = (ImageView) findViewById(R.id.unfinish_status_contacts_item_arrow);

        //未读提醒
        unReadTip = (TextView) findViewById(R.id.contacts_header_right_btn);
        //放开未读提醒
        if (unReadTip != null) {
            if (isCampusPatrolTag) {
                //校园巡查要隐藏未读提醒按钮
                unReadTip.setVisibility(View.GONE);
            } else {
                //只有发布任务的老师才显示，否则隐藏。
                if (isOwnerTask) {
                    unReadTip.setVisibility(View.GONE);
                } else {
                    unReadTip.setVisibility(View.GONE);
                }
            }
            unReadTip.setText(getString(R.string.un_read_tip));
            unReadTip.setTextColor(getResources().getColor(R.color.text_green));
            unReadTip.setOnClickListener(this);
        }
        initFinishGridViewHelper();
        initUnFinishGridViewHelper();
    }

    private void initFinishGridViewHelper() {
        finishGridView = (GridView) findViewById(R.id.finish_status_grid_view);
        if (finishGridView != null) {
            AdapterViewHelper finishGridViewHelper = new AdapterViewHelper(getActivity(),
                    finishGridView, R.layout.item_finish_status_gridview) {
                @Override
                public void loadData() {
                    loadCommonData();
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    final CompletedListInfo data = (CompletedListInfo) getDataAdapter().getItem
                            (position);
                    if (data == null) {
                        return view;
                    }
                    //头像
                    ImageView imageView = (ImageView) view.findViewById(R.id.icon_head);
                    if (imageView != null) {
                        getThumbnailManager().displayThumbnailWithDefault(
                                AppSettings.getFileUrl(data.getHeadPicUrl()), imageView,
                                R.drawable.default_user_icon);

                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //已完成点头像进入点评记录页面
                                int taskType = data.getTaskType();
                                if (taskType == StudyTaskType.WATCH_WAWA_COURSE
                                        || taskType == StudyTaskType.WATCH_HOMEWORK
                                        || taskType == StudyTaskType.NEW_WATACH_WAWA_COURSE) {
                                    //目前看作业和看课件没有完成情况
                                    return;
                                } else if (taskType == StudyTaskType.LISTEN_READ_AND_WRITE
                                        || taskType == StudyTaskType.SUPER_TASK
                                        || taskType == StudyTaskType.MULTIPLE_RETELL_COURSE
                                        || taskType == StudyTaskType.MULTIPLE_TASK_ORDER
                                        || isSuperChildTask) {
                                    //听说+读写
                                    enterStudentListenReadAndWriteListActivity(data);
                                } else {
                                    enterStudentFinishedHomeworkListActivity(data);
                                }
                            }
                        });
                    }
                    //标题
                    TextView textView = (TextView) view.findViewById(R.id.title);
                    if (textView != null) {
                        textView.setText(data.getStudentName());
                    }

                    //时间
                    textView = (TextView) view.findViewById(R.id.time);
                    if (textView != null) {
                        //暂时隐藏时间
                        textView.setVisibility(View.INVISIBLE);
                        textView.setText(data.getCommitTime());
                    }

                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) {
                        holder = new ViewHolder();
                    }
                    holder.data = data;
                    view.setTag(holder);
                    return view;
                }

                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null || holder.data == null) {
                        return;
                    }
                    CompletedListInfo data = (CompletedListInfo) holder.data;
                    if (data != null) {

                    }
                }
            };
            //根据tag来区分不同的数据源
            this.finishGridViewTag = String.valueOf(finishGridView.getId());
            addAdapterViewHelper(this.finishGridViewTag, finishGridViewHelper);
        }

    }

    private void enterStudentFinishedHomeworkListActivity(CompletedListInfo data) {
        if (data == null) {
            return;
        }
        Intent intent = new Intent(getActivity(), StudentFinishedHomeworkListActivity.class);
        Bundle args = getArguments();
        //来自学习任务完成列表
        args.putBoolean(HomeworkFinishStatusActivity.Constants.FROM_HOMEWORK_FINISH_STAUS_LIST, true);
        //名称是学生姓名
        args.putString(HomeworkFinishStatusActivity.Constants.TASK_TITLE, data.getStudentName());
        args.putString(HomeworkFinishStatusActivity.Constants.STUDENT_ID, data.getStudentId());
        args.putString(HomeworkFinishStatusActivity.Constants.SORT_STUDENT_ID, data.getStudentId());
        //请求自己的数据，需要过滤数据。
        args.putBoolean(HomeworkFinishStatusActivity.Constants.NEED_FILTER_DATA, true);
        args.putBoolean(HomeworkFinishStatusActivity.Constants.EXTRA_ISONLINEREPORTER,
                getArguments().getBoolean(HomeworkFinishStatusActivity.Constants.EXTRA_ISONLINEREPORTER));
        intent.putExtras(args);
        startActivityForResult(intent, CampusPatrolPickerFragment
                .RESULT_CODE_COMPLETED_HOMEWORK_LIST_FRAGMENT);
    }

    private void enterStudentListenReadAndWriteListActivity(CompletedListInfo data) {
        if (data == null) {
            return;
        }
        Intent intent = new Intent(getActivity(), HomeworkCommitActivity.class);
        Bundle args = getArguments();
        if (isSuperChildTask){
            args.putInt("TaskType", StudyTaskType.LISTEN_READ_AND_WRITE);
        } else {
            args.putInt("TaskType", taskType);
        }
        if (taskType == StudyTaskType.MULTIPLE_TASK_ORDER
                || taskType == StudyTaskType.MULTIPLE_RETELL_COURSE){
            args.putString("TaskId", taskId);
        } else {
            args.putString("TaskId", String.valueOf(data.getTaskId()));
        }
        args.putString(HomeworkFinishStatusActivity.Constants.TASK_TITLE, data.getStudentName());
        args.putBoolean(HomeworkFinishStatusActivity.Constants.EXTRA_STUDENT_FINISH_STUDY_TASK_LIST, true);
        args.putString(HomeworkFinishStatusActivity.Constants.SORT_STUDENT_ID, data.getStudentId());
        //请求自己的数据，需要过滤数据。
        args.putBoolean(HomeworkFinishStatusActivity.Constants.NEED_FILTER_DATA, true);
        //来自学习任务完成列表
        args.putBoolean(HomeworkFinishStatusActivity.Constants.FROM_HOMEWORK_FINISH_STAUS_LIST, true);
        args.putBoolean(HomeworkFinishStatusActivity.Constants.EXTRA_ISONLINEREPORTER,
                getArguments().getBoolean(HomeworkFinishStatusActivity.Constants.EXTRA_ISONLINEREPORTER));
        intent.putExtras(args);
        startActivity(intent);
    }

    /**
     * 更新小红点
     *
     * @param data
     */
    private void updateStatus(final CompletedListInfo data) {

        if (!isOwnerTask || data == null || data.isRead()) {
            return;
        }
        Map<String, Object> params = new HashMap<String, Object>();
        final int commitTaskId = data.getCommitTaskId();
        params.put("CommitTaskId", commitTaskId);
        int taskType = data.getTaskType();
        //英文写作任务类型
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
                            //更新成功
                            setHasContentChanged(true);
                        }
                    }
                };
        listener.setShowLoading(true);
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.UPDATE_TEACHER_READ_TASK_URL,
                params, listener);

    }

    public static void setHasContentChanged(boolean hasContentChanged) {
        HomeworkFinishStatusFragment.hasContentChanged = hasContentChanged;
    }

    public static boolean hasContentChanged() {
        return hasContentChanged;
    }

    /**
     * 进入英文写作点评记录页面
     *
     * @param data
     */
    private void enterEnglishWritingCommentRecordActivity(CompletedListInfo data) {
        if (data == null) {
            return;
        }
        Intent intent = new Intent(getActivity(), EnglishWritingCommentRecordActivity.class);
        Bundle bundle = getArguments();
        //角色类型
        bundle.putInt("roleType", RoleType.ROLE_TYPE_TEACHER);
        bundle.putString(EnglishWritingCompletedFragment.Constant.STUDENTID,
                data.getStudentId());
        bundle.putString(EnglishWritingCompletedFragment.Constant.SORTSTUDENTID,
                data.getStudentId());
        //任务的id。
        bundle.putInt(EnglishWritingCompletedFragment.Constant.TASKID, data.getTaskId());
        intent.putExtras(getArguments());
        startActivity(intent);
    }

    private void initUnFinishGridViewHelper() {
        unFinishGridView = (GridView) findViewById(R.id.unfinish_status_grid_view);
        if (unFinishGridView != null) {
            AdapterViewHelper unFinishGridViewHelper = new AdapterViewHelper(getActivity(),
                    unFinishGridView, R.layout.item_finish_status_gridview) {
                @Override
                public void loadData() {
                    loadCommonData();
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    final UnCompletedListInfo data = (UnCompletedListInfo) getDataAdapter()
                            .getItem(position);
                    if (data == null) {
                        return view;
                    }
                    //头像
                    ImageView imageView = (ImageView) view.findViewById(R.id.icon_head);
                    if (imageView != null) {
                        getThumbnailManager().displayThumbnailWithDefault(
                                AppSettings.getFileUrl(data.getHeadPicUrl()), imageView,
                                R.drawable.default_user_icon);
                    }
                    //标题
                    TextView textView = (TextView) view.findViewById(R.id.title);
                    if (textView != null) {
                        textView.setText(data.getStudentName());
                    }

                    //时间
                    textView = (TextView) view.findViewById(R.id.time);
                    if (textView != null) {
//                        textView.setText(data.getCommitTime());
                        textView.setVisibility(View.INVISIBLE);
                    }

                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) {
                        holder = new ViewHolder();
                    }
                    holder.data = data;
                    view.setTag(holder);
                    return view;
                }

                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null || holder.data == null) {
                        return;
                    }
                    UnCompletedListInfo data = (UnCompletedListInfo) holder.data;

                    if (taskType == StudyTaskType.LISTEN_READ_AND_WRITE
                            || taskType == StudyTaskType.SUPER_TASK
                            || taskType == StudyTaskType.MULTIPLE_RETELL_COURSE
                            || taskType == StudyTaskType.MULTIPLE_TASK_ORDER
                            || isSuperChildTask) {
                        enterStudentListenReadAndWriteListActivity(data.toCompleteListInfo());
                    }
                }
            };
            //根据tag来区分不同的数据源
            this.unFinishGridViewTag = String.valueOf(unFinishGridView.getId());
            addAdapterViewHelper(this.unFinishGridViewTag, unFinishGridViewHelper);
        }

    }

    private void loadViews() {
        loadCommonData();
    }

    /**
     * 模拟数据
     */
    private void loadCommonData() {
        String url = ServerUrl.GET_TASK_DETAIL_URL;
        if (taskType == StudyTaskType.SUPER_TASK || isSuperChildTask || isSuperThirdTask){
            url = ServerUrl.GET_TOGETHER_TASK_STUDENT_FINISH_DETAIL;
        }
        Map<String, Object> params = new HashMap();
        //TaskId，必填
        params.put("TaskId", taskId);
        RequestHelper.sendPostRequest(getActivity(), url, params,
                new DefaultPullToRefreshDataListener<HomeworkFinishStatusObjectResult>(
                        HomeworkFinishStatusObjectResult.class) {
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

    private void updateViews(HomeworkFinishStatusObjectResult result) {
        //已完成
        List<CompletedListInfo> finishList = result.getModel().getData().getCompletedList();
        if (finishList == null || finishList.size() <= 0) {
            isAllFinish = false;
        } else {
            finishText.setText(getString(R.string.finish_count, finishList.size()));
            getAdapterViewHelper(finishGridViewTag).setData(finishList);
        }


        //未完成
        List<UnCompletedListInfo> unFinishList = result.getModel().getData().getUnCompletedList();
        if (unFinishList == null || unFinishList.size() <= 0) {
            isAllFinish = true;
        } else {
            unFinishText.setText(getString(R.string.unfinish_count, unFinishList.size()));
            getAdapterViewHelper(unFinishGridViewTag).setData(unFinishList);
        }

    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.finish_status_contacts_list_item_layout) {
            //已完成
            isFinishLayoutExpand = !isFinishLayoutExpand;
            updateFinishStatusLayout(isFinishLayoutExpand, finishImage, finishGridView);

        } else if (v.getId() == R.id.unfinish_status_contacts_list_item_layout) {
            //未完成
            isUnFinishLayoutExpand = !isUnFinishLayoutExpand;
            updateFinishStatusLayout(isUnFinishLayoutExpand, unFinishImage, unFinishGridView);

        } else if (v.getId() == R.id.contacts_header_right_btn) {
            upReadTipEvent(taskId);
        } else {
            super.onClick(v);
        }
    }

    /**
     * 未读提醒
     *
     * @param taskId
     */
    private void upReadTipEvent(String taskId) {
        if (isAllFinish) {
            TipMsgHelper.ShowMsg(getActivity(), getString(R.string.all_read));
            return;
        }
        if (TextUtils.isEmpty(taskId)) {
            return;
        }
        Map<String, Object> params = new HashMap();
        params.put("TaskId", taskId);
        RequestHelper.sendPostRequest(getActivity(),
                ServerUrl.PUSH_UNFINISH_TASKS_URL, params,
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
                            TipMsgHelper.ShowMsg(getActivity(), getString(R.string.not_read_tip_failure));
                            return;
                        }
                        TipMsgHelper.ShowMsg(getActivity(), getString(R.string.not_read_tip_success));
                    }
                });
    }

    private void updateFinishStatusLayout(boolean isFinishStatusLayoutExpand, ImageView
            finishStatusImage, GridView finishStatusGridView) {
        finishStatusImage.setImageResource(isFinishStatusLayoutExpand ? R.drawable.list_exp_up :
                R.drawable.list_exp_down);
        finishStatusGridView.setVisibility(isFinishStatusLayoutExpand ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            if (requestCode == CampusPatrolPickerFragment
                    .RESULT_CODE_COMPLETED_HOMEWORK_LIST_FRAGMENT) {
                if (CompletedHomeworkListFragment.hasContentChanged()) {
                    CompletedHomeworkListFragment.setHasContentChanged(false);
                    setHasContentChanged(true);
                    refreshData();
                }
            }
        }
    }
}
