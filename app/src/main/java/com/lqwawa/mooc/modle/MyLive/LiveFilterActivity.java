package com.lqwawa.mooc.modle.MyLive;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.galaxyschool.app.wawaschool.BaseFragmentActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.fragment.OnlinePickerFragment;

public class LiveFilterActivity extends BaseFragmentActivity {
    private Fragment fragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common);

        Bundle args = getIntent().getExtras();
        if (args == null) {
            return;
        }
        try {
            fragment = OnlinePickerFragment.class.newInstance();
            if (fragment != null) {
                fragment.setArguments(args);
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.activity_body, fragment, OnlinePickerFragment.class.getSimpleName());
                ft.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
