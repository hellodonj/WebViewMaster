package com.galaxyschool.app.wawaschool;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import com.galaxyschool.app.wawaschool.fragment.ContactsClassRequestListFragment;

public class ContactsClassRequestListActivity extends BaseFragmentActivity
        implements ContactsClassRequestListFragment.Constants {

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_contacts);

      Bundle args = getIntent().getExtras();
      Fragment fragment = new ContactsClassRequestListFragment();
      fragment.setArguments(args);
      FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
      ft.replace(R.id.contacts_layout, fragment, ContactsClassRequestListFragment.TAG);
      ft.commit();
   }

}