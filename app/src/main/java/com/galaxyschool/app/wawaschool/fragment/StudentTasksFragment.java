package com.galaxyschool.app.wawaschool.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.CallbackListener;
import com.galaxyschool.app.wawaschool.common.DateUtils;
import com.galaxyschool.app.wawaschool.common.StudyTaskOpenHelper;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.common.WawaCourseUtils;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.ResType;
import com.galaxyschool.app.wawaschool.pojo.StudyTaskInfo;
import com.galaxyschool.app.wawaschool.pojo.TaskDetail;
import com.galaxyschool.app.wawaschool.pojo.TaskDetailResult;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentTasksFragment extends ContactsListFragment implements View.OnClickListener {

    public static final String TAG = StudentTasksFragment.class.getSimpleName();
    private TextView headTitleView;
    private GridView gridview;
    private String studentId = "";
    private StudyTaskInfo taskInfo;
    private static boolean hasReaded;
    public interface Constants {
        public static final int MAX_BOOKS_PER_ROW = 2;
        public static final String STUDENTID = "studentId";
        public static final String STUDY_TASK_INFO = "StudyTaskInfo";
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_student_tasks, null);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
        getIntentData();
    }

    private void getIntentData() {
        Intent intent = getActivity().getIntent();
        taskInfo= (StudyTaskInfo) intent.getSerializableExtra(Constants.STUDY_TASK_INFO);
        studentId = intent.getStringExtra(Constants.STUDENTID);
    }


    private void loadStudentHomeWorks() {
        if(taskInfo==null){
            return;
        }
        if (getCurrAdapterViewHelper().hasData()) {
            getCurrAdapterViewHelper().clearData();
        }
        Map<String, Object> params = new HashMap();
        params.put("StudentId", studentId);
        params.put("TaskId", taskInfo.getTaskId());
        RequestHelper.sendPostRequest(getActivity(),
                ServerUrl.GET_STUDENT_TASK_FINISH_INFO_URL, params,
                new RequestHelper.RequestDataResultListener<TaskDetailResult>(getActivity(),
                        TaskDetailResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        TaskDetailResult result = getResult();
                        if (result == null || !result.isSuccess()
                                || result.getModel() == null) {

                            return;
                        }
                        TaskDetail data = result.getModel().getData();
                        if (data != null) {
                            if (data.getStudentCommitTaskList() != null) {
                                getCurrAdapterViewHelper().setData(data.getStudentCommitTaskList());
                            }
                            headTitleView.setText(getString(R.string.whose_homework, data.getStudentName()));
                        }
                    }
                });
    }


    @Override
    public void onResume() {
        super.onResume();
        if (getUserVisibleHint()) {
            loadStudentHomeWorks();
        }
    }


    private void initViews() {
        initGridview();
        headTitleView = (TextView) findViewById(R.id.contacts_header_title);
        ImageView imageView = (ImageView) getView().findViewById(R.id.contacts_header_left_btn);
        imageView.setOnClickListener(this);

    }

    private void initGridview() {
        gridview = (GridView) findViewById(R.id.pull_refresh_gridview);
        if (gridview != null) {
            gridview.setNumColumns(Constants.MAX_BOOKS_PER_ROW);
            AdapterViewHelper gridViewHelper = new AdapterViewHelper(getActivity(),
                    gridview, R.layout.item_student_tasks) {
                @Override
                public void loadData() {
                    loadStudentHomeWorks();
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    TaskDetail data = (TaskDetail) getDataAdapter().getItem(position);
                    if (data == null) {
                        return view;
                    }
                    ImageView imageView = (ImageView) view.findViewById(R.id.user_img);
                    if (imageView != null) {
                        if (data.getThumbnailUrl() != null) {
                            getThumbnailManager().displayThumbnail(AppSettings.getFileUrl(
                                    data.getThumbnailUrl()), imageView);
                        }
                    }
                    TextView textView = (TextView) view.findViewById(R.id.finish_time);
                    if (textView != null) {
                        textView.setVisibility(View.VISIBLE);
                        textView.setText(getString(R.string.finish_time,
                                DateUtils.getDateStr(data.getCommitTime(),
                                        DateUtils.DATE_PATTERN_yyyy_MM_dd_HH_MM_SS,
                                        DateUtils.DATE_PATTERN_yyyy_MM_dd_HH_MM)));
                    }
                    imageView = (ImageView) view.findViewById(R.id.resource_indicator);
                    if (data.getIsRead()) {
                        imageView.setVisibility(View.INVISIBLE);
                    } else {
                        imageView.setVisibility(View.VISIBLE);
                    }
                    //显示任务类型
                    textView= (TextView) view.findViewById(R.id.commit_type);
                    if (textView!=null){
                        int commitType=data.getCommitType();
                        View view1 = view.findViewById(R.id.commit_introducation_type);
                        if (commitType==0){
                            view1.setVisibility(View.GONE);
                        }else{
                            view1.setVisibility(View.VISIBLE);
                            if (commitType==1){
                                textView.setText(R.string.retel_couese);
                            }else if (commitType==2){
                                textView.setText(R.string.make_task);
                            }else if (commitType==3){
                                textView.setText(R.string.ask_question);
                            }else if (commitType==4){
                                textView.setText(R.string.create_course);
                            }
                        }
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
                    TaskDetail data = (TaskDetail) holder.data;
                    openStudentTask(data.getResId(), data.getCommitTaskId());
                }
            };
            setCurrAdapterViewHelper(gridview, gridViewHelper);
        }
    }

    private void openStudentTask(String resId, final int CommitTaskId) {
        WawaCourseUtils wawaCourseUtils = new WawaCourseUtils(getActivity());
        wawaCourseUtils.loadCourseDetail(resId);
        wawaCourseUtils.setOnCourseDetailFinishListener(new WawaCourseUtils.OnCourseDetailFinishListener() {
            @Override
            public void onCourseDetailFinish(CourseData courseData) {
                openTask(courseData, CommitTaskId);
            }
        });
    }

    private void openTask(CourseData courseData, final int CommitTaskId) {
        if(courseData == null) {
            return;
        }
        if(taskInfo==null){
            return;
        }
        StudyTaskOpenHelper.openTask(getActivity(), courseData,null,1,"");
        updateTeacherReadTask(CommitTaskId);
    }

    private void updateTeacherReadTask(final int CommitTaskId) {
        Map<String, Object> params = new HashMap();
        params.put("CommitTaskId", CommitTaskId);
        RequestHelper.sendPostRequest(getActivity(),
                ServerUrl.UPDATE_TASK_TEACHER_READ_URL, params,
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
                            return;
                        }
                        //已经更新了小红点
                        setHasReaded(true);
                        if (getCurrAdapterViewHelper().hasData()) {
                            List<TaskDetail> details = getCurrAdapterViewHelper().getData();
                            for (TaskDetail detail : details) {
                                if (detail.getCommitTaskId() == CommitTaskId) {
                                    detail.setIsRead(true);
                                    break;
                                }
                            }
                            getCurrAdapterViewHelper().update();
                        }

                    }
                });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.contacts_header_left_btn:
                getActivity().finish();
                break;
        }
    }

    public static void setHasReaded(boolean hasReaded) {
        StudentTasksFragment.hasReaded = hasReaded;
    }

    public static boolean hasReaded() {
        return hasReaded;
    }
}
