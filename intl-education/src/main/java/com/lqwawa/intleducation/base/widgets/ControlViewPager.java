package com.lqwawa.intleducation.base.widgets;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * @author mrmedici
 * @time 2017/12/05 14:38
 * @desc 自由控制的ViewPager
 */
public class ControlViewPager extends ViewPager {

	private boolean isCanScroll = true;

	public ControlViewPager(Context context) {
		super(context);
	}

	public ControlViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * 设置其是否能滑动换页
	 * @param isCanScroll false 不能换页， true 可以滑动换页
	 */
	public void setScanScroll(boolean isCanScroll) {
		this.isCanScroll = isCanScroll;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return isCanScroll && super.onInterceptTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		return isCanScroll && super.onTouchEvent(ev);

	}
}
