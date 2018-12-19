package com.osastudio.apps;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.util.DisplayMetrics;

import com.osastudio.common.library.ActivityStack;
import com.osastudio.common.utils.LQImageLoader;
import com.osastudio.common.utils.Utils;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class BaseApplication extends MultiDexApplication {

   private boolean mInitialized = false;
   private ActivityStack mActivityStack;

   @Override
   protected void attachBaseContext(Context base) {
      super.attachBaseContext(base);
      MultiDex.install(this);
   }

   @Override
   public void onCreate() {
      super.onCreate();

      createActivityStack();
      LQImageLoader.initImageLoader(getApplicationContext());
   }

   public ActivityStack getActivityStack() {
      createActivityStack();
      return mActivityStack;
   }

   void createActivityStack() {
      if (mActivityStack == null) {
         mActivityStack = ActivityStack.getInstance();
      }
   }

   void destroyActivityStack() {
      if (mActivityStack != null) {
         mActivityStack.cleanup();
         mActivityStack = null;
      }
   }

   public boolean hasIntialized() {
      return mInitialized;
   }

   /**
    * Prepare environment while application is launched
    */
   public void prepareEnvironment() {
      Utils.log("Application", "cleanupEnvironment");
      createActivityStack();
      mInitialized = true;
   }

   /**
    * Cleanup environment while application exits
    */
   public void cleanupEnvironment() {
      Utils.log("Application", "cleanupEnvironment");
      destroyActivityStack();
      mInitialized = false;
   }

   public void exit() {
      getActivityStack().finishAll();
      cleanupEnvironment();
//      System.exit(0);
   }

   public static int getScreenWidth(Activity context) {
      return context.getWindowManager().getDefaultDisplay().getWidth();
   }

   public static int getScreenHeight(Activity context) {
      return context.getWindowManager().getDefaultDisplay().getHeight();
   }

   public static float getScreenDensity(Activity context) {
      DisplayMetrics dm = new DisplayMetrics();
      context.getWindowManager().getDefaultDisplay().getMetrics(dm);
      return dm.density;
   }

   public static DisplayMetrics getDisplayMetrics(Activity context) {
      DisplayMetrics dm = new DisplayMetrics();
      context.getWindowManager().getDefaultDisplay().getMetrics(dm);
      return dm;
   }

}
