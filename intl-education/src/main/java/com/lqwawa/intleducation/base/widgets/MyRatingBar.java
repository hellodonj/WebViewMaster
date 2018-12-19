package com.lqwawa.intleducation.base.widgets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RatingBar;

/**
 * Created by XChen on 2017/1/20.
 * email:man0fchina@foxmail.com
 */

public class MyRatingBar extends RatingBar {
    private float minStar = 0.0f;
    @SuppressLint("NewApi")
    public MyRatingBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr, 0);
    }
    @SuppressLint("NewApi")
    public MyRatingBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public MyRatingBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyRatingBar(Context context) {
        this(context, null);
    }

    public void setMinStar(float minStar){
        if (minStar >=0 && minStar < getMax()){
            this.minStar = minStar;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
       if (super.onTouchEvent(event)){
           if (getRating() < minStar){
               setRating(minStar);
           }
           return true;
       }else{
           return false;
       }
    }
}
