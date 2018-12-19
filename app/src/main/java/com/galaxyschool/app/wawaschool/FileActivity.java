package com.galaxyschool.app.wawaschool;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.WindowManager;

import com.galaxyschool.app.wawaschool.fragment.FileFragment;

public class FileActivity extends BaseFragmentActivity
        implements FileFragment.Constants ,KeyEvent.Callback{
private  Fragment fragment=null;
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_common);

      FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
      fragment = new FileFragment();
      Bundle args = getIntent().getExtras();
      if (args != null){
         //是否显示关闭布局
         boolean showCloseLayout = args.getBoolean(FileFragment.Constants.EXTRA_SHOW_CLOSE_LAYOUT);
         if (showCloseLayout) {
            args.putInt(FileFragment.Constants.EXTRA_ROOT_LAYOUT_ID, R.id.activity_body);
         }
      }
      fragment.setArguments(args);
      ft.replace(R.id.activity_body, fragment, FileFragment.TAG);
      ft.commit();

      getWindow().setFlags(
              WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
              WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
   }
   @Override
   public boolean onKeyDown(int keyCode, KeyEvent event)  {
      if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { //按下的如果是BACK，同时没有重复
//         WebView webView=((FileFragment)fragment).getWebView();
//         if(webView.canGoBack()){
//            webView.goBack();
//         }else{
//            finish();
//         }
          finish();
          return true;
      }
      return super.onKeyDown(keyCode, event);
   }

}