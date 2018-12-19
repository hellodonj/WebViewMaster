package com.galaxyschool.app.wawaschool;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.fragment.BookCatalogListFragment;
import com.galaxyschool.app.wawaschool.slide.SlideManagerHornForPhone;

public class BookCatalogListActivity extends BaseFragmentActivity
        implements BookCatalogListFragment.Constants {

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_contacts);

      Bundle args = getIntent().getExtras();
      Fragment fragment = new BookCatalogListFragment();
      fragment.setArguments(args);
      FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
      ft.replace(R.id.contacts_layout, fragment, BookCatalogListFragment.TAG);
      ft.commit();
   }

   @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      super.onActivityResult(requestCode, resultCode, data);
      if (requestCode == ActivityUtils.REQUEST_CODE_RETURN_REFRESH){
         if (data != null ){
            Intent intent =new Intent();
            String filePath=data.getExtras().getString(SlideManagerHornForPhone.SAVE_PATH);
            intent.putExtra(SlideManagerHornForPhone.SAVE_PATH,filePath);
            this.setResult(Activity.RESULT_OK, intent);
            this.finish();
         }
      }
   }
}