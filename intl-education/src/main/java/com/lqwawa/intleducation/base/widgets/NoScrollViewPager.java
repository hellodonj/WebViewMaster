package com.lqwawa.intleducation.base.widgets;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * @author mrmedici
 * @time 2017/12/05 14:38
 * @desc 不能滑动的ViewPager
 */
public class NoScrollViewPager extends ViewPager {

	public NoScrollViewPager(Context context) {
		super(context);
	}

	public NoScrollViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		return false;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return false;
	}
}
