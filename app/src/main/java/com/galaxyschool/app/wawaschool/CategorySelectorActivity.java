package com.galaxyschool.app.wawaschool;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import com.galaxyschool.app.wawaschool.fragment.category.Category;
import com.galaxyschool.app.wawaschool.fragment.category.CategorySelectorFragment;

import java.util.ArrayList;

public class CategorySelectorActivity extends BaseFragmentActivity
        implements CategorySelectorFragment.Constants {

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_contacts);

      Bundle args = getIntent().getExtras();
      Fragment fragment = new CategorySelectorFragment();
      fragment.setArguments(args);
      FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
      ft.replace(R.id.contacts_layout, fragment, CategorySelectorFragment.TAG);
      ft.commit();
   }

   @Override
   public void onActivityResult(int requestCode, int resultCode, Intent data) {
      super.onActivityResult(requestCode, resultCode, data);
      if (resultCode == Activity.RESULT_OK) {
         if (requestCode == REQUEST_CODE_SELECT_CATEGORY) {
            ArrayList<Category> result =
                    data.getExtras().getParcelableArrayList(
                            REQUEST_DATA_SELECT_CATEGORY);
         }
      }
   }

}