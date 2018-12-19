package com.galaxyschool.app.wawaschool;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import com.galaxyschool.app.wawaschool.fragment.ReporterIdentityFragment;

/**
 * Created by Administrator on 2017/2/6.
 */

public class ReporterIdentityActivity extends BaseFragmentActivity implements
        ReporterIdentityFragment.Constants{
    private Fragment fragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common);
        Bundle args = getIntent().getExtras();
        fragment = new ReporterIdentityFragment();
        fragment.setArguments(args);
        FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.activity_body, fragment, ReporterIdentityFragment.TAG);
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
