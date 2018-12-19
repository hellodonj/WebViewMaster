package com.github.mikephil.charting.charts;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class MyBarChartView extends BarChart{

    PointF downPoint = new PointF();

    public MyBarChartView(Context context) {
        super(context);
    }

    public MyBarChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyBarChartView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // 不拦截
        return super.dispatchTouchEvent(ev);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent evt) {
        switch (evt.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downPoint.x = evt.getX();
                downPoint.y = evt.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (getScaleX() > 1 && Math.abs(evt.getX() - downPoint.x) > 10) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                break;
        }
        return super.onTouchEvent(evt);
    }
}
