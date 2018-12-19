package com.lqwawa.intleducation.base.widgets;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 抢占焦点和滑动事件的RecyclerView
 * @date 2018/06/02 13:52
 * @history v1.0
 * **********************************
 */
public class FocusRecyclerView extends RecyclerView{

    private MotionEvent mOldEvent;

    public FocusRecyclerView(Context context) {
        super(context);
    }

    public FocusRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FocusRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        return super.onTouchEvent(e);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        return super.onInterceptTouchEvent(e);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // 水平方向请求不允许拦截Touch事件
        // @date   :2018/6/8 0008 上午 1:40
        // @func   :从RecyclerView禁止滑动解决问题
        /*switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                mOldEvent = ev;
                break;
            case MotionEvent.ACTION_MOVE:
                int currentX = (int) ev.getX();
                int currentY = (int) ev.getY();
                int distanceX = (int) (currentX - mOldEvent.getX());
                int distanceY = (int) (currentY - mOldEvent.getY());
                if(Math.abs(distanceY) < Math.abs(distanceX) && Math.abs(distanceX) > 12){
                    // 竖直方向的坐标差没有左右方向坐标差大，视为左右滑动
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                break;
        }*/
        return super.dispatchTouchEvent(ev);
    }
}
