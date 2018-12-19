package com.galaxyschool.app.wawaschool;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.fragment.BookListFragment;
import com.galaxyschool.app.wawaschool.slide.SlideManagerHornForPhone;

public class BookListActivity extends BaseFragmentActivity
        implements BookListFragment.Constants {

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_common);

      Bundle args = getIntent().getExtras();
      Fragment fragment = new BookListFragment();
      fragment.setArguments(args);
      FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
      ft.replace(R.id.activity_body, fragment, BookListFragment.TAG);
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