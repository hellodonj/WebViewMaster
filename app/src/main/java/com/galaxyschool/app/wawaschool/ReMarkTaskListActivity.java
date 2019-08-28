package com.galaxyschool.app.wawaschool;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.fragment.ReMarkTaskListFragment;

/**
 * ======================================================
 * Describe:批阅任务列表
 * ======================================================
 */
public class ReMarkTaskListActivity extends FragmentActivity {

    private Fragment fragment = null;

    public static void start(Activity activity,
                             int courseId,
                             String classId){
        if (activity == null){
            return;
        }
        Intent intent = new Intent(activity,ReMarkTaskListActivity.class);
        Bundle args = new Bundle();
        args.putString(LearningStatisticActivity.Constants.CLASS_ID,classId);
        args.putInt(LearningStatisticActivity.Constants.COURSE_ID,courseId);
        intent.putExtras(args);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common);

        Bundle args = getIntent().getExtras();
        fragment = new ReMarkTaskListFragment();
        fragment.setArguments(args);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.activity_body, fragment, ReMarkTaskListFragment.TAG);
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
