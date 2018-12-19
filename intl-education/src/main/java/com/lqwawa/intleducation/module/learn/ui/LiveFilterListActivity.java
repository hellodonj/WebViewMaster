package com.lqwawa.intleducation.module.learn.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ui.MyBaseFragmentActivity;

/**
 * Created by XChen on 2018/1/16.
 * email:man0fchina@foxmail.com
 */

public class LiveFilterListActivity extends MyBaseFragmentActivity {
    private Fragment fragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.com_fragment_activity);
        Bundle args = getIntent().getExtras();
        if (args == null) {
            return;
        }
        try {
            fragment = MyLiveFilterListFragment.class.newInstance();
            if (fragment != null) {
                fragment.setArguments(args);
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, fragment, MyLiveFilterListFragment.class.getSimpleName());
                ft.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
