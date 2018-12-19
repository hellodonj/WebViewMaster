package com.galaxyschool.app.wawaschool;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import com.galaxyschool.app.wawaschool.fragment.MyCollectionBookListFragment;

public class MyCollectionBookListActivity extends BaseFragmentActivity {

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_common);

      Bundle args = getIntent().getExtras();
         Fragment fragment = new MyCollectionBookListFragment();
      fragment.setArguments(args);
      FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
      ft.replace(R.id.activity_body, fragment, MyCollectionBookListFragment.TAG);
      ft.commit();
   }

}