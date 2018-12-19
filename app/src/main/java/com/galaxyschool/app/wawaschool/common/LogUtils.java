package com.galaxyschool.app.wawaschool.common;

import com.galaxyschool.app.wawaschool.config.AppSettings;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

public class LogUtils {

   private static final String APP_NAME = "wawa_school";

   private static final boolean DEBUG = AppSettings.DEBUG;
   
   
   public static void log(String tag, String info) {
      logi(tag, info);
   }

   public static void logd(String tag, String info) {
      if (DEBUG) {
         Log.d(APP_NAME + ">>>>>>>>>>" + tag, "-------->" + info);
      }
   }

   public static void loge(String tag, String info) {
      if (DEBUG) {
         Log.e(APP_NAME + ">>>>>>>>>>" + tag, "-------->" + info);
      }
   }

   public static void logi(String tag, String info) {
      if (DEBUG) {
         Log.i(APP_NAME + ">>>>>>>>>>" + tag, "-------->" + info);
      }
   }

   public static void logv(String tag, String info) {
      if (DEBUG) {
         Log.v(APP_NAME + ">>>>>>>>>>" + tag, "-------->" + info);
      }
   }

   public static void logw(String tag, String info) {
      if (DEBUG) {
         Log.w(APP_NAME + ">>>>>>>>>>" + tag, "-------->" + info);
      }
   }
   
   public static String getVersionName(Context context) {
      String versionName = "";
      
      PackageManager pm = context.getPackageManager();
      if (pm != null) {
         try {
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            if (pi != null) {
               versionName = pi.versionName;
            }
         } catch (NameNotFoundException e) {
         }
      }
      
      return versionName;
   }
}
