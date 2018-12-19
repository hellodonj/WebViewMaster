package com.galaxyschool.app.wawaschool;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import com.galaxyschool.app.wawaschool.fragment.MyCollectionListFragment;

public class MyCollectionListActivity extends BaseFragmentActivity {

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_common);

      Bundle args = getIntent().getExtras();
      Fragment fragment = new MyCollectionListFragment();
      fragment.setArguments(args);
      FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
      ft.replace(R.id.activity_body, fragment, MyCollectionListFragment.TAG);
      ft.commit();
   }

}