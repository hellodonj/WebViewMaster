package com.lqwawa.apps.views;

import android.content.ClipData;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.*;
import android.widget.ListView;

import com.lqwawa.apps.R;
import com.lqwawa.apps.views.DragSortHListView.DragSortHandler;

import java.lang.ref.WeakReference;

/**
 * @author 作者 shouyi
 * @version 创建时间：Feb 15, 2016 5:58:44 PM
 * 类说明
 */
public class DragSortListView extends ListView {
	View mDragView;
	int mLastDragPos = -1;
	int mOriginDragPos = -1;
	GestureDetector mGestureDetector;
	DragSortHandler mDragSortHandler;
    boolean mDraggable = true;
	
	public DragSortListView(Context context) {
		super(context);
		mGestureDetector = new GestureDetector(getContext(),
				new DragGestureListener());
	}

	public DragSortListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mGestureDetector = new GestureDetector(getContext(),
				new DragGestureListener());
	}

	public DragSortListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mGestureDetector = new GestureDetector(getContext(),
				new DragGestureListener());
	}

	@Override
	public void onViewAdded(View child) {
		super.onViewAdded(child);
		if (mDragSortHandler != null) {
			child.setOnTouchListener(mOnTouchListener);
		}
	}

	@Override
	protected void attachViewToParent(View child, int index, ViewGroup.LayoutParams params) {
		super.attachViewToParent(child, index, params);
	}

	public void setDraggable(boolean draggable) {
		mDraggable = draggable;
	}

	public void setDragHandler(DragSortHandler handler) {
		mDragSortHandler = handler;
	}

	private OnTouchListener mOnTouchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			mDragView = v;

			if (mGestureDetector.onTouchEvent(event)) {
				return true;
			}

			switch (event.getAction() & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_UP:
				break;
			}

			return false;
		}
	};

	@Override
	public boolean onDragEvent(DragEvent event) {
		// TODO Auto-generated method stub
		if (event.getAction() == DragEvent.ACTION_DRAG_STARTED) {
			mDragView = (View) event.getLocalState();
			mOriginDragPos = (Integer) mDragView.getTag(R.id.tag_dragsortlist);
			mLastDragPos = mOriginDragPos;
			if (mDragSortHandler != null) {
				mDragSortHandler.onItemDrag();
			}
		} else if (event.getAction() == DragEvent.ACTION_DRAG_LOCATION) {
			int toPos = findChildIndexUnder(event.getX(), event.getY());
			toPos += getFirstVisiblePosition();
			Log.i(VIEW_LOG_TAG, "ACTION_DRAG_ENTERED, mLeftViewIndex: " + getFirstVisiblePosition());
			Log.i(VIEW_LOG_TAG, "ACTION_DRAG_ENTERED" + ", fromPos: " + mLastDragPos + ", toPos: " + toPos);
			if (toPos > -1 && toPos != mLastDragPos) {
				if (mDragSortHandler != null) {
					mDragSortHandler.dragTo(mLastDragPos, toPos);
					Log.i(VIEW_LOG_TAG, "ACTION_DRAG_ENTERED" + ", changed fromPos: " + mLastDragPos + ", toPos: " + toPos);
					mLastDragPos = toPos;
				}
			}
		} else if (event.getAction() == DragEvent.ACTION_DRAG_ENDED) {
			if (mDragSortHandler != null) {
				mDragSortHandler.onDragEnd(mOriginDragPos, mLastDragPos);
			}
		}
		return true;
	}
	
	protected int findChildIndexUnder(float x, float y) {
		RectF rect = new RectF();
		for (int i = 0; i < getChildCount(); i++) {
			rect.left = getChildAt(i).getLeft();
			rect.top = getChildAt(i).getTop();
			rect.right = getChildAt(i).getRight();
			rect.bottom = getChildAt(i).getBottom();
			if (rect.contains(x, y)) {
				return i;
			}
		}
		return -1;
	}
	
	private class DragGestureListener extends GestureDetector.SimpleOnGestureListener {
		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			return super.onSingleTapConfirmed(e);
		}

		@Override
		public void onLongPress(MotionEvent e) {
			super.onLongPress(e);
			if (mDraggable) {
				processChildLongPress();
			}
		}

		@Override
		public boolean onDown(MotionEvent e) {
			return false;
		}
	}

	protected void processChildLongPress() {
		ClipData data = ClipData.newPlainText("", "");
		MyDragShadowBuilder shadowBuilder = new MyDragShadowBuilder(
				mDragView);
		mDragView.startDrag(data, shadowBuilder, mDragView, 0);
	}

	public static class MyDragShadowBuilder extends DragShadowBuilder {

		private final WeakReference<View> mView;

		public MyDragShadowBuilder(View view) {
			super(view);
			mView = new WeakReference<View>(view);
		}

		@Override
		public void onDrawShadow(Canvas canvas) {
			canvas.scale(1.5F, 1.5F);
			super.onDrawShadow(canvas);
		}

		@Override
		public void onProvideShadowMetrics(Point shadowSize,
										   Point shadowTouchPoint) {
			// super.onProvideShadowMetrics(shadowSize, shadowTouchPoint);

			final View view = mView.get();
			if (view != null) {
				shadowSize.set((int) (view.getWidth() * 1.5F),
						(int) (view.getHeight() * 1.5F));
				shadowTouchPoint.set(shadowSize.x / 2, shadowSize.y / 2);
			} else {
				// Log.e(View.VIEW_LOG_TAG,
				// "Asked for drag thumb metrics but no view");
			}
		}
	}

}
