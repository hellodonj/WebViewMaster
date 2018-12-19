package com.lqwawa.intleducation.base.widgets;
import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ScrollView;


/**
 * Created by XChen on 2016/11/25.
 * email:man0fchina@foxmail.com
 */
public class ScrollViewEx extends ScrollView {
    private ScrollViewListener scrollViewListener = null;

    public ScrollViewEx(Context context) {
        super(context);
    }

    public ScrollViewEx(Context context, AttributeSet attrs,
                                int defStyle) {
        super(context, attrs, defStyle);
    }

    public ScrollViewEx(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScrollViewListener(ScrollViewListener scrollViewListener) {
        this.scrollViewListener = scrollViewListener;
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        super.onScrollChanged(x, y, oldx, oldy);
        if (scrollViewListener != null) {
            scrollViewListener.onScrollChanged(this, x, y, oldx, oldy);
        }
    }

    public interface ScrollViewListener {
        void onScrollChanged(ScrollView scrollView, int x, int y, int oldx, int oldy);
    }
}