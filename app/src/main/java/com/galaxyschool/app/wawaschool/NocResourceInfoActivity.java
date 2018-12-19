package com.galaxyschool.app.wawaschool;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.WindowManager;

import com.galaxyschool.app.wawaschool.fragment.NocResourceInfoFragment;

public class NocResourceInfoActivity extends BaseFragmentActivity {
  private  Fragment fragment;
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
       getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
      setContentView(R.layout.activity_common);
      Bundle args = getIntent().getExtras();
      fragment = new NocResourceInfoFragment();
      fragment.setArguments(args);
      FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
      ft.replace(R.id.activity_body, fragment, NocResourceInfoFragment.TAG);
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