package com.galaxyschool.app.wawaschool;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.galaxyschool.app.wawaschool.fragment.StudyTaskFragment;

public class MyStudyTaskActivity extends BaseFragmentActivity {

   Fragment fragment;
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_common);

      Bundle args = getIntent().getExtras();
      fragment = new StudyTaskFragment();
      fragment.setArguments(args);
      FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
      ft.replace(R.id.activity_body, fragment, StudyTaskFragment.TAG);
      ft.addToBackStack(null);
      ft.commit();
   }


   @Override
   public void onBackPressed() {
      if(getSupportFragmentManager().getBackStackEntryCount() == 1) {
         finish();
      }
      super.onBackPressed();
   }

   @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      super.onActivityResult(requestCode,resultCode,data);
      if (fragment != null) {
         fragment.onActivityResult(requestCode, resultCode, data);
      }
   }
}