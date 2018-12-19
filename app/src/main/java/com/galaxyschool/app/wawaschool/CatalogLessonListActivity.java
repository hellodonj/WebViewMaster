package com.galaxyschool.app.wawaschool;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.galaxyschool.app.wawaschool.medias.fragment.SchoolResourceContainerFragment;

public class CatalogLessonListActivity extends BaseFragmentActivity
        implements SchoolResourceContainerFragment.Constants {
    private Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        Bundle args = getIntent().getExtras();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

            fragment = new SchoolResourceContainerFragment();
            fragment.setArguments(args);
            ft.replace(R.id.contacts_layout, fragment, SchoolResourceContainerFragment.TAG);


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