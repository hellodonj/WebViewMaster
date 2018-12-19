package com.galaxyschool.app.wawaschool.medias.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.galaxyschool.app.wawaschool.BaseFragmentActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.fragment.CampusPatrolSplitCourseListFragment;
import com.galaxyschool.app.wawaschool.fragment.MyRemoteSplitCourseListFragment;

/**
 * 微课、PPT、PDF分页。
 */
public class MyRemoteSplitCourseListActivity extends BaseFragmentActivity {
    Fragment fragment;
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_common);

      Bundle args = getIntent().getExtras();
      fragment = new MyRemoteSplitCourseListFragment();
      fragment.setArguments(args);
      FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
      ft.replace(R.id.activity_body, fragment, MyRemoteSplitCourseListFragment.TAG);
      ft.commit();
   }

   @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      super.onActivityResult(requestCode, resultCode, data);
      if (fragment != null){
         fragment.onActivityResult(requestCode,resultCode,data);
      }
   }

}