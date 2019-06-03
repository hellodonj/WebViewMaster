package com.galaxyschool.app.wawaschool;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.galaxyschool.app.wawaschool.fragment.ActClassroomDetailFragment;
import com.galaxyschool.app.wawaschool.fragment.ActClassroomFragment;

public class ActClassroomDetailActivity extends BaseFragmentActivity implements
        ActClassroomFragment.Constants{
    private Fragment fragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //防止播放乐视视频的时候屏幕闪屏
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        setContentView(R.layout.activity_common);

        Bundle args = getIntent().getExtras();
        fragment = new ActClassroomDetailFragment();
        fragment.setArguments(args);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.activity_body, fragment, ActClassroomDetailFragment.TAG);
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
        if (this.getResources().getConfiguration().orientation== ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
            this.finish();
        }else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }
}
