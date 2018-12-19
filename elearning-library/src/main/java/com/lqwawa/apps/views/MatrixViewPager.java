package com.lqwawa.apps.views;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class MatrixViewPager extends ViewPager implements MatrixImageView.MatrixDragListener {

    /**  当前子控件是否处理拖动状态  */
    private boolean isDragging;

    public MatrixViewPager(Context context) {
        super(context);
    }

    public MatrixViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(isDragging) {
            return false;
        }
        return super.onInterceptTouchEvent(ev);
    }
    @Override
    public void startDrag() {
        isDragging = true;
    }


    @Override
    public void stopDrag() {
        isDragging = false;
    }

}
