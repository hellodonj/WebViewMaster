package com.lqwawa.mooc.select;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.galaxyschool.app.wawaschool.BaseFragmentActivity;
import com.galaxyschool.app.wawaschool.R;

/**
 * 描述: 播放列表activity
 * 作者|时间: djj on 2019/6/26 0026 下午 3:36
 */
public class PlayListViewActivity extends BaseFragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_list_view);

        Fragment fragment = new PlayListViewFragment();
        Bundle args = getIntent().getExtras();
        if (args != null) {
            fragment.setArguments(args);
        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.activity_body, fragment);
        ft.commit();
    }
}
