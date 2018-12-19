package com.galaxyschool.app.wawaschool;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.galaxyschool.app.wawaschool.fragment.CampusPatrolPickerFragment;
import com.galaxyschool.app.wawaschool.fragment.PictureBooksDetailFragment;


public class PictureBooksDetailActivity extends BaseFragmentActivity  implements PictureBooksDetailFragment.Constants{
   private Fragment fragment=null;
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_common);

      Bundle args = getIntent().getExtras();
      fragment = new PictureBooksDetailFragment();
      fragment.setArguments(args);
      FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
      ft.replace(R.id.activity_body, fragment, PictureBooksDetailFragment.TAG);
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
   protected void onDestroy() {
      //给media list fragment回调，用于刷新页面。
      setResult(CampusPatrolPickerFragment.OPEN_COURSE_DETAILS_RESULT_CODE,new Intent());
      finish();
      super.onDestroy();
   }
}