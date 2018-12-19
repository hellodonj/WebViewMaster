package com.osastudio.common.utils;

import com.osastudio.apps.Config;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.TextUtils;
import android.util.Log;

public class LogUtils {

   private static final String APP_NAME = "wawa_school";

   private static final boolean DEBUG = Config.DEBUG;
   
   
   public static void log(String tag, String info) {
      logi(tag, info);
   }

   public static void logd(String tag, String info) {
      if (DEBUG) {
         if (!printLog(tag,info)){
            Log.d(APP_NAME + ">>>>>>>>>>" + tag, "-------->" + info);
         }
      }
   }

   public static void loge(String tag, String info) {
      if (DEBUG) {
         Log.e(APP_NAME + ">>>>>>>>>>" + tag, "-------->" + info);
         if (!printLog(tag,info)){
            Log.e(APP_NAME + ">>>>>>>>>>" + tag, "-------->" + info);
         }
      }
   }

   public static void logi(String tag, String info) {
      if (DEBUG) {
         if (!printLog(tag,info)){
            Log.i(APP_NAME + ">>>>>>>>>>" + tag, "-------->" + info);
         }
      }
   }

   public static void logv(String tag, String info) {
      if (DEBUG) {
         if (!printLog(tag,info)){
            Log.v(APP_NAME + ">>>>>>>>>>" + tag, "-------->" + info);
         }
      }
   }

   public static void logw(String tag, String info) {
      if (DEBUG) {
         if (!printLog(tag,info)){
            Log.w(APP_NAME + ">>>>>>>>>>" + tag, "-------->" + info);
         }
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

   public static boolean printLog(String tag,String message){
      if (TextUtils.isEmpty(message)){
         return false;
      }
      String resultTag = "RESULT: ";
      String paramTag = "PARAMS: ";
      String urlTag = "URL: ";
      if (message.contains(resultTag)){
         Log.e(tag,"=========================打印result===============================");
         message = message.substring(resultTag.length());
      } else if (message.contains(paramTag)){
         Log.e(tag,"=========================打印param================================");
         message = message.substring(paramTag.length());
      } else if (message.contains(urlTag)){
         Log.e(tag,"=========================打印url==================================");
         message = message.substring(urlTag.length());
      }
      if ((message.startsWith("{") && message.endsWith("}"))
              || (message.startsWith("[") && message.endsWith("]"))) {
         message = JsonUtil.formatJson(JsonUtil.decodeUnicode(message));
         Log.d(tag,message);
         return true;
      }
      return false;
   }

}
