package com.galaxyschool.app.wawaschool;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.galaxyschool.app.wawaschool.fragment.resource.ResourcePicFragment;
import com.galaxyschool.app.wawaschool.medias.fragment.SchoolResourceContainerFragment;

public class CatalogResourceListActivity extends BaseFragmentActivity
        implements SchoolResourceContainerFragment.Constants {

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_contacts);

      Bundle args = getIntent().getExtras();
      Fragment fragment = new ResourcePicFragment();
      fragment.setArguments(args);
      FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
      ft.replace(R.id.contacts_layout, fragment, ResourcePicFragment.TAG);
      ft.commit();
   }

}