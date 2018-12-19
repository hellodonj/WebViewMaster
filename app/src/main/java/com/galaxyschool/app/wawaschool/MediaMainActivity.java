package com.galaxyschool.app.wawaschool;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.galaxyschool.app.wawaschool.fragment.MediaListFragment;
import com.galaxyschool.app.wawaschool.fragment.MediaMainFragment;
import com.galaxyschool.app.wawaschool.fragment.TopicDiscussionFragment;


/**
 * Created by Administrator on 2016.06.18.
 */
public class MediaMainActivity extends BaseFragmentActivity{
    private Fragment fragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cs_shell);

        Bundle args = getIntent().getExtras();
        fragment = new MediaMainFragment();
        fragment.setArguments(args);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container, fragment, MediaMainFragment.TAG);
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
