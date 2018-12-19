package com.galaxyschool.app.wawaschool;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import com.galaxyschool.app.wawaschool.fragment.ClassResourceListFragment;
import com.galaxyschool.app.wawaschool.fragment.HomeworkMainFragment;

public class ClassResourceListActivity extends BaseFragmentActivity
    implements ClassResourceListFragment.Constants {
    Fragment fragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common);

        Bundle args = getIntent().getExtras();
        if (args != null) {
            String tag;
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            int channelType = args.getInt(ClassResourceListActivity.EXTRA_CHANNEL_TYPE);
            if (channelType == ClassResourceListActivity.CHANNEL_TYPE_HOMEWORK) {
                fragment = new HomeworkMainFragment();
                tag = HomeworkMainFragment.TAG;
            } else {
                fragment = new ClassResourceListFragment();
                tag = ClassResourceListFragment.TAG;
            }
            fragment.setArguments(args);
            ft.replace(R.id.activity_body, fragment, tag);
            ft.addToBackStack(null);
            ft.commit();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (fragment != null){
            fragment.onActivityResult(requestCode,resultCode,data);
        }
    }

    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().getBackStackEntryCount()  == 1) {
            finish();
        }
        super.onBackPressed();
    }
}