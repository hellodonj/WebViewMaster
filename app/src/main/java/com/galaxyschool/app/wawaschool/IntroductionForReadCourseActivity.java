package com.galaxyschool.app.wawaschool;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.fragment.IntroductionForReadCourseFragment;
import com.galaxyschool.app.wawaschool.fragment.IntroductionSuperTaskFragment;
import com.galaxyschool.app.wawaschool.pojo.StudyTaskType;

/**
 * Created by Administrator on 2016/12/23.
 */
public class IntroductionForReadCourseActivity extends BaseFragmentActivity{
    private Fragment fragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common);
        Bundle args = getIntent().getExtras();
        int taskType = 0;
        if (args != null){
            taskType = args.getInt(ActivityUtils.EXTRA_TASK_TYPE);
        }
        if (taskType == StudyTaskType.SUPER_TASK){
            fragment = new IntroductionSuperTaskFragment();
        } else {
            fragment = new IntroductionForReadCourseFragment();
        }
        fragment.setArguments(args);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.activity_body, fragment, fragment.getClass().getSimpleName());
        ft.addToBackStack(null);
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
    public void onBackPressed() {
        if (fragment instanceof IntroductionSuperTaskFragment) {
            IntroductionSuperTaskFragment superTaskFragment = (IntroductionSuperTaskFragment) fragment;
            if (superTaskFragment.isVisible()) {
                superTaskFragment.backPress();
                return;
            }
        }
        if(getSupportFragmentManager().getBackStackEntryCount() == 1) {
            finish();
        }
        super.onBackPressed();
    }
}
