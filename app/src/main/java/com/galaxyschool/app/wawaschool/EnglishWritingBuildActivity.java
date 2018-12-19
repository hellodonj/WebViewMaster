package com.galaxyschool.app.wawaschool;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;

import com.galaxyschool.app.wawaschool.fragment.EnglishWritingBuildFragment;
import com.galaxyschool.app.wawaschool.fragment.HomeworkCommitFragment;


/**
 * Created by Administrator on 2016.06.17.
 */
public class EnglishWritingBuildActivity extends BaseFragmentActivity {
    private Fragment fragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common);

        Bundle args = getIntent().getExtras();
        fragment = new EnglishWritingBuildFragment();
        fragment.setArguments(args);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.activity_body, fragment, EnglishWritingBuildFragment.TAG);
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (fragment != null && fragment instanceof EnglishWritingBuildFragment){
            ((EnglishWritingBuildFragment)fragment).onKeyDown(keyCode,event);
        }
        return super.onKeyDown(keyCode,event);
    }
}
