package com.galaxyschool.app.wawaschool;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.galaxyschool.app.wawaschool.fragment.TaskFinishInfoFragment;

/**
 * Created by KnIghT on 16-6-20.
 */
public class TaskFinishInfoActivity extends BaseFragmentActivity implements TaskFinishInfoFragment.Constants{
    private Fragment fragment=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common);

        Bundle args = getIntent().getExtras();
        fragment = new TaskFinishInfoFragment();
        fragment.setArguments(args);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.activity_body, fragment, TaskFinishInfoFragment.TAG);
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
