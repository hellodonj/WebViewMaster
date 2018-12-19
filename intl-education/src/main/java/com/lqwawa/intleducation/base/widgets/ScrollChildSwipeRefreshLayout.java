package com.lqwawa.intleducation.base.widgets;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.View;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.utils.DisplayUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * ***************************************
 *
 * 根据google官方Demo 集成的SwipeRefreshLayout 可使用在所有滑动的View上
 * Extends {@link SwipeRefreshLayout} to support non-direct descendant scrolling views.
 * <p>
 * {@link SwipeRefreshLayout} works as expected when a scroll view is a direct child: it triggers
 * the refresh only when the view is on top. This class adds a way {@link #setScrollUpChild} to
 * define which view controls this behavior.
 * @author：mrmedici
 * @time: 2018/2/28 0028 15:56
 *
 * ***************************************
 */
public class ScrollChildSwipeRefreshLayout extends SwipeRefreshLayout {
    // 进度圈的最大偏移量
    private static final int CIRCLE_VIEW_MAX_OFFSET_DP = 100;

    private View mScrollUpChild;

    public ScrollChildSwipeRefreshLayout(Context context) {
        super(context);
        initSetting();
    }

    public ScrollChildSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initSetting();
    }

    /**
     * 初始化配置
     */
    private void initSetting(){
        // 设置下拉刷新的样式
        setColorSchemeResources(R.color.colorAccent);
        // 设置下拉刷新的高度
        int end = DisplayUtil.dip2px(getContext(),CIRCLE_VIEW_MAX_OFFSET_DP);
        // 第一个参数scale就是就是刷新那个圆形进度是是否缩放,
        // 如果为true表示缩放,圆形进度图像就会从小到大展示出来,为false就不缩放
        // 第二个参数start和end就是那刷新进度条展示的相对于默认的展示位置,
        // start和end组成一个范围，在这个y轴范围就是那个圆形进度ProgressView展示的位置
        setProgressViewOffset(false,0,end);
    }

    @Override
    public boolean canChildScrollUp() {
        if (mScrollUpChild != null) {
            return ViewCompat.canScrollVertically(mScrollUpChild, -1);
        }
        return super.canChildScrollUp();
    }

    public void setScrollUpChild(View view) {
        mScrollUpChild = view;
    }

    /**
     * 自动刷新功能
     */
    public void autoRefresh() {
        try {
            Field mCircleView = SwipeRefreshLayout.class.getDeclaredField("mCircleView");
            mCircleView.setAccessible(true);
            View progress = (View) mCircleView.get(this);
            progress.setVisibility(VISIBLE);

            Method setRefreshing = SwipeRefreshLayout.class.getDeclaredMethod("setRefreshing", boolean.class, boolean.class);
            setRefreshing.setAccessible(true);
            setRefreshing.invoke(this, true, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
