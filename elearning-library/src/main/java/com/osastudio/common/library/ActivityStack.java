package com.osastudio.common.library;

import android.app.Activity;

public class ActivityStack extends ObjectStack<Activity> {

   private static ActivityStack instance = new ActivityStack();

   public static ActivityStack getInstance() {
      return instance;
   }

   public ActivityStack() {
      super();
   }

   public void finishAll() {
      while (!isEmpty()) {
         pop().finish();
      }
   }

   public void finishUtil(Class cls) {
      Activity activity = null;
      while (!isEmpty()) {
         activity = getTop();
         if (activity.getClass().getCanonicalName().equals(cls.getCanonicalName())) {
            break;
         }
         pop().finish();
      }
   }

   public void finishUtilRoot() {
      while (getCount() > 1) {
         pop().finish();
      }
   }

   public void cleanup() {
      clear();
   }

}
