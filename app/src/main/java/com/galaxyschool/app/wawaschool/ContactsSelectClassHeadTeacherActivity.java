package com.galaxyschool.app.wawaschool;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import com.galaxyschool.app.wawaschool.fragment.ContactsSelectClassHeadTeacherFragment;

public class ContactsSelectClassHeadTeacherActivity extends BaseFragmentActivity
        implements ContactsSelectClassHeadTeacherFragment.Constants {

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_common);

      Bundle args = getIntent().getExtras();
      Fragment fragment = new ContactsSelectClassHeadTeacherFragment();
      fragment.setArguments(args);
      FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
      ft.replace(R.id.activity_body, fragment, ContactsSelectClassHeadTeacherFragment.TAG);
      ft.commit();
   }

}