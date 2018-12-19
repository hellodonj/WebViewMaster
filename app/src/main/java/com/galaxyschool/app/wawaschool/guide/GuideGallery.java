package com.galaxyschool.app.wawaschool.guide;

import com.galaxyschool.app.wawaschool.common.ActivityUtils;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Gallery;

public class GuideGallery extends Gallery {
	private boolean mEnableEnter;

	public GuideGallery(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public GuideGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public GuideGallery(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		int kEvent;
		if (isScrollingLeft(e1, e2)) {
			kEvent = KeyEvent.KEYCODE_DPAD_LEFT;
		} else {
			kEvent = KeyEvent.KEYCODE_DPAD_RIGHT;
		}
		if (kEvent == KeyEvent.KEYCODE_DPAD_RIGHT
				&& getSelectedItemPosition() == getCount() - 1) {
			mEnableEnter = true;
		} else {
			mEnableEnter = false;
		}
		return super.onScroll(e1, e2, distanceX, distanceY);
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		int kEvent;
		if (isScrollingLeft(e1, e2)) { // Check if scrolling left
			kEvent = KeyEvent.KEYCODE_DPAD_LEFT;
		} else { // Otherwise scrolling right
			kEvent = KeyEvent.KEYCODE_DPAD_RIGHT;
//			if (mEnableEnter && getSelectedItemPosition() == getCount() - 1) {
//				 ActivityUtils.gotoHome(getContext());
//				((Activity) getContext()).finish();
//			}
		}
		onKeyDown(kEvent, null);
		return true;

	}

	private boolean isScrollingLeft(MotionEvent e1, MotionEvent e2) {
		return e2.getX() > e1.getX();
	}

}
