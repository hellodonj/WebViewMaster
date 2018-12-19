package com.galaxyschool.app.wawaschool.views;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class MyViewPager extends ViewPager {
    private boolean mIsCanScroll = false;

    public MyViewPager(Context context) {
        super(context);
    }

    public MyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (this.mIsCanScroll) {
            return super.onTouchEvent(event);
        }

        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (this.mIsCanScroll) {
            return super.onInterceptTouchEvent(event);
        }

        return false;
    }

    public void setCanScroll(boolean isCanScroll) {
        this.mIsCanScroll = isCanScroll;
    }

    @Override
    public void setCurrentItem(int item) {
        super.setCurrentItem(item,false);
    }
}
