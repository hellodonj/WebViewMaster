package com.galaxyschool.app.wawaschool;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;

import com.galaxyschool.app.wawaschool.fragment.CampusPatrolMainFragment;
import com.galaxyschool.app.wawaschool.fragment.SubscribeMainFragment;

public class CampusPatrolMainActivity extends BaseFragmentActivity {

   Fragment fragment = null;
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_common);

      Bundle args = getIntent().getExtras();
      fragment = new CampusPatrolMainFragment();
      fragment.setArguments(args);
      FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
      ft.replace(R.id.activity_body, fragment, CampusPatrolMainFragment.TAG);
      ft.commit();
   }

   @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      super.onActivityResult(requestCode, resultCode, data);
      if (fragment != null){
         fragment.onActivityResult(requestCode,resultCode,data);
      }
   }

   @Override
   protected void onDestroy() {
      super.onDestroy();
      //页面销毁后重置筛选日期
      if (!TextUtils.isEmpty(MyApplication.SCREENING_START_DATE)){
         MyApplication.SCREENING_START_DATE = null;
      }
      if (!TextUtils.isEmpty(MyApplication.SCREENING_END_DATE)){
         MyApplication.SCREENING_END_DATE = null;
      }
   }
}