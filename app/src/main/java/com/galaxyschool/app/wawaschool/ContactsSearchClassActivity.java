package com.galaxyschool.app.wawaschool;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import com.galaxyschool.app.wawaschool.fragment.ContactsSearchClassFragment;

public class ContactsSearchClassActivity extends BaseFragmentActivity {

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_contacts);

      Bundle args = getIntent().getExtras();
      Fragment fragment = new ContactsSearchClassFragment();
      fragment.setArguments(args);
      FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
      ft.replace(R.id.contacts_layout, fragment, ContactsSearchClassFragment.TAG);
      ft.commit();
   }

}