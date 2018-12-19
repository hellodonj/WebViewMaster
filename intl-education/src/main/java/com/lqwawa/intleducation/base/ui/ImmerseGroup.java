package com.lqwawa.intleducation.base.ui;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by XChen on 2017/7/10.
 * email:man0fchina@foxmail.com
 */

public class ImmerseGroup extends FrameLayout {
    public ImmerseGroup(Context context) {
        super(context);
    }

    public ImmerseGroup(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ImmerseGroup(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight(), 0);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}