package com.lqwawa.libs.mediapaper;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class MyScrollView extends ScrollView {
   public boolean mbScrollable = true;
   
   public MyScrollView(Context context) {
      this(context, null);
   }

   public MyScrollView(Context context, AttributeSet attrs) {
      this(context, attrs, 0);
   }


   public MyScrollView(Context context, AttributeSet attrs, int defStyle) {
      super(context, attrs, defStyle);
   }
  
  @Override
   public boolean onInterceptTouchEvent(MotionEvent ev) {
      if (mbScrollable)
         return super.onInterceptTouchEvent(ev);
      else
         return false;
   }
  
  
   public void setMyScrollViewScrollableOrNot(boolean bScrollable) {
      mbScrollable = bScrollable;
   }
}
