package com.galaxyschool.app.wawaschool;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.WindowManager;

import com.galaxyschool.app.wawaschool.fragment.CampusTVPromotionPageFragment;

/**
 * 校园电视台开通宣传页
 */
public class CampusTVPromotionPageActivity extends BaseFragmentActivity
        implements CampusTVPromotionPageFragment.Constants ,KeyEvent.Callback{
private  Fragment fragment=null;
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_common);

      FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
      fragment = new CampusTVPromotionPageFragment();
      Bundle args = getIntent().getExtras();
      fragment.setArguments(args);
      ft.replace(R.id.activity_body, fragment, CampusTVPromotionPageFragment.TAG);
      ft.commit();

      getWindow().setFlags(
              WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
              WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
   }
   @Override
   public boolean onKeyDown(int keyCode, KeyEvent event)  {
      //按下的如果是BACK，同时没有重复
      if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
          finish();
          return true;
      }
      return super.onKeyDown(keyCode, event);
   }

}