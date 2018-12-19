package com.galaxyschool.app.wawaschool;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import com.galaxyschool.app.wawaschool.fragment.PublishResourceFragment;

public class PublishResourceActivity extends BaseFragmentActivity
        implements PublishResourceFragment.Constants {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment fragment = new PublishResourceFragment();
        fragment.setArguments(getIntent().getExtras());
        ft.replace(R.id.activity_body, fragment, PublishResourceFragment.TAG);
        ft.commit();
    }

}
