package com.galaxyschool.app.wawaschool;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.MotionEvent;

import com.galaxyschool.app.wawaschool.fragment.CheckMarkFragment;
import com.galaxyschool.app.wawaschool.fragment.CompletedHomeworkListFragment;
import com.galaxyschool.app.wawaschool.fragment.EvalHomeworkListFragment;
import com.galaxyschool.app.wawaschool.fragment.HomeworkCommitFragment;
import com.galaxyschool.app.wawaschool.fragment.IntroductionSuperTaskFragment;
import com.galaxyschool.app.wawaschool.fragment.ListenReadAndWriteStudyTaskFragment;
import com.galaxyschool.app.wawaschool.pojo.StudyTaskType;


/**
 * Created by Administrator on 2016.06.17.
 */
public class HomeworkCommitActivity extends BaseFragmentActivity {
    private Fragment fragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common);

        Bundle args = getIntent().getExtras();
        int taskType = args.getInt("TaskType");
        String TAG;
        if (taskType == StudyTaskType.LISTEN_READ_AND_WRITE
                || taskType == StudyTaskType.MULTIPLE_TASK_ORDER
                || taskType == StudyTaskType.MULTIPLE_RETELL_COURSE
                || taskType == StudyTaskType.MULTIPLE_Q_DUBBING
                || taskType == StudyTaskType.MULTIPLE_OTHER
                || taskType == StudyTaskType.MULTIPLE_OTHER_SUBMIT) {
            fragment = new ListenReadAndWriteStudyTaskFragment();
            TAG = ListenReadAndWriteStudyTaskFragment.TAG;
        } else if (taskType == StudyTaskType.SUPER_TASK) {
            fragment = new IntroductionSuperTaskFragment();
            TAG = IntroductionSuperTaskFragment.TAG;
        } else {
            fragment = new HomeworkCommitFragment();
            TAG = HomeworkCommitFragment.TAG;
        }
        fragment.setArguments(args);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.activity_body, fragment, TAG);
        ft.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            if (fragment != null && fragment instanceof HomeworkCommitFragment) {
                HomeworkCommitFragment commitFragment = (HomeworkCommitFragment) fragment;
                CompletedHomeworkListFragment listFragment = commitFragment
                        .getCompletedHomeworkListFragment();
                if (listFragment != null && listFragment.isVisible()) {
                    listFragment.upDateDeleteButtonShowStatus(null, false);
                }

                EvalHomeworkListFragment evalHomeworkListFragment = commitFragment
                        .getEvalHomeworkListFragment();
                if (evalHomeworkListFragment != null && evalHomeworkListFragment.isVisible()) {
                    evalHomeworkListFragment.upDateDeleteButtonShowStatus(null, false);
                }

                CheckMarkFragment checkMarkFragment = (CheckMarkFragment) getSupportFragmentManager().findFragmentByTag(CheckMarkFragment.TAG);
                if (checkMarkFragment != null && checkMarkFragment.isVisible()) {
                    checkMarkFragment.upDateDeleteButtonShowStatus(null, false);
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onBackPressed() {
        if (fragment instanceof HomeworkCommitFragment) {
            HomeworkCommitFragment homeworkCommitFragment = (HomeworkCommitFragment) fragment;
            if (homeworkCommitFragment.isVisible()) {
                homeworkCommitFragment.onBackPress();
                return;
            }
            CheckMarkFragment checkMarkFragment = (CheckMarkFragment) getSupportFragmentManager().findFragmentByTag(CheckMarkFragment.TAG);
            if (checkMarkFragment != null && checkMarkFragment.isVisible()) {
                checkMarkFragment.backPress();
                return;
            }
        }
        super.onBackPressed();
    }
}
