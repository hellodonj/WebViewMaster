package com.lqwawa.mooc;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.galaxyschool.app.wawaschool.BaseFragmentActivity;
import com.galaxyschool.app.wawaschool.R;
import com.lqwawa.mooc.modle.implementationplan.ImplementationPlanFragment;

import static com.lqwawa.mooc.modle.implementationplan.ImplementationPlanFragment.KEY_EXTRA_CHAPTER_ID;
import static com.lqwawa.mooc.modle.implementationplan.ImplementationPlanFragment.KEY_EXTRA_CLASS_ID;
import static com.lqwawa.mooc.modle.implementationplan.ImplementationPlanFragment.KEY_EXTRA_COURSE_ID;
import static com.lqwawa.mooc.modle.implementationplan.ImplementationPlanFragment.KEY_EXTRA_IS_EDIT_MODE;
import static com.lqwawa.mooc.modle.implementationplan.ImplementationPlanFragment.KEY_EXTRA_MEMBER_ID;


/**
 * 描述: 课中实施方案
 * 作者|时间: djj on 2019/9/3 0003 上午 10:14
 */
public class ImplementationPlanActivity extends BaseFragmentActivity {

    private Fragment fragment;

    public static void start(Activity activity,
                             String chapterId,
                             String memberId,
                             String courseId,
                             String classId,
                             boolean isEditMode) {
        Intent intent = new Intent(activity, ImplementationPlanActivity.class);
        Bundle args = new Bundle();
        args.putString(KEY_EXTRA_CHAPTER_ID, chapterId);
        args.putString(KEY_EXTRA_MEMBER_ID, memberId);
        args.putString(KEY_EXTRA_COURSE_ID, courseId);
        args.putString(KEY_EXTRA_CLASS_ID, classId);
        args.putBoolean(KEY_EXTRA_IS_EDIT_MODE, isEditMode);
        intent.putExtras(args);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_implementation_plan);
        Bundle args = getIntent().getExtras();
        fragment = new ImplementationPlanFragment();
        fragment.setArguments(args);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.activity_body, fragment, fragment.getClass().getSimpleName());
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
        if (fragment instanceof ImplementationPlanFragment) {
            ImplementationPlanFragment implementationPlanFragment = (ImplementationPlanFragment) fragment;
            if (implementationPlanFragment.isVisible()) {
                implementationPlanFragment.onBackPress();
                return;
            }
        }
        super.onBackPressed();
    }
}
