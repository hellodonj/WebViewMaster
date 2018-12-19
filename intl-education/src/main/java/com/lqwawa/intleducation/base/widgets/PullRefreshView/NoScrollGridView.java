package com.lqwawa.intleducation.base.widgets.PullRefreshView;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.GridView;

/**
 * NoScrollGridView，解决ScrollView中嵌套gridview显示不正常的问题（1行）
 */
public class NoScrollGridView extends GridView {
    // 是否允许测量最大高度
    private boolean mMeasureHeightFlag = true;

    public NoScrollGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NoScrollGridView(Context context) {
        super(context);
    }

    public NoScrollGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if(mMeasureHeightFlag){
            int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                    MeasureSpec.AT_MOST);
            super.onMeasure(widthMeasureSpec, expandSpec);
        }else{
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    //通过重新dispatchTouchEvent方法来禁止滑动
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // TODO Auto-generated method stub
        if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            return true;//禁止Gridview进行滑动
        }
        return super.dispatchTouchEvent(ev);
    }

    public boolean isMeasureHeightFlag() {
        return mMeasureHeightFlag;
    }

    public void setMeasureHeightFlag(boolean flag) {
        this.mMeasureHeightFlag = flag;
    }

}

