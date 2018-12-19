package com.galaxyschool.app.wawaschool;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.galaxyschool.app.wawaschool.fragment.HomeworkFinishStatusFragment;


/**
 * Created by Administrator on 2016.06.17.
 */
public class HomeworkFinishStatusActivity extends BaseFragmentActivity {
    private Fragment fragment = null;

    public interface Constants{
        public String ROLE_TYPE = "roleType";
        public String TASK_ID = "taskId" ;
        public String TASK_TYPE = "taskType";
        public String TASK_TITLE = "taskTitle";
        public String STUDENT_ID = "studentId";
        public String SORT_STUDENT_ID = "sortStudentId";
        public String IS_OWNER_TASK = "isOwnerTask";
        public String IS_FROM_ENGLISH_WRITING = "isFromEnglishWriting";
        public String HIDDEN_HEADER_VIEW = "hiddenHeaderView";
        public String NEED_FILTER_DATA = "need_filter_data";//需要过滤数据
        public String SHOULD_CHANGE_BG_COLOR = "shouldChangeBgColor";//需要改变背景颜色
        public String SHOULD_SHOW_COMMIT_BTN = "shouldSowCommitBtn";//需要显示提交按钮
        //来自学习任务完成列表
        public String FROM_HOMEWORK_FINISH_STAUS_LIST = "from_homework_finish_status_list";
        String EXTRA_ISONLINEREPORTER ="isOnlineReporter";//是不是来自直播的小编或者主持人
        String EXTRA_STUDENT_FINISH_STUDY_TASK_LIST = "student_finish_task_list";
        String EXTRA_IS_SUPER_CHILD_TASK = "is_super_child_task";
        String EXTRA_IS_SUPER_THIRD_TASK = "is_super_third_task";
        String EXTRA_IS_TASK_COURSE_RESID = "task_course_res_id";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common);

        Bundle args = getIntent().getExtras();
        fragment = new HomeworkFinishStatusFragment();
        fragment.setArguments(args);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.activity_body, fragment, HomeworkFinishStatusFragment.TAG);
        ft.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }
}
