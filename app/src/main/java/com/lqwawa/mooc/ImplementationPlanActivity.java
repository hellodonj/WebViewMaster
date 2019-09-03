package com.lqwawa.mooc;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.galaxyschool.app.wawaschool.BaseFragmentActivity;
import com.galaxyschool.app.wawaschool.R;
import com.lqwawa.mooc.modle.implementationplan.ImplementationPlanFragment;

/**
 * 描述: 课中实施方案
 * 作者|时间: djj on 2019/9/3 0003 上午 10:14
 */
public class ImplementationPlanActivity extends BaseFragmentActivity {

    private Fragment fragment;

    public static void start(Activity activity,
                             String chapterId){
        Intent intent = new Intent(activity,ImplementationPlanActivity.class);
        Bundle args = new Bundle();
        args.putString("chapterId",chapterId);
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
        ft.addToBackStack(null);
        ft.commit();
    }
}
