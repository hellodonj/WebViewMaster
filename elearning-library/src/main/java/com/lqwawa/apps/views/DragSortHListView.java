package com.lqwawa.apps.views;

import java.lang.ref.WeakReference;

import android.content.ClipData;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;

import com.lqwawa.apps.R;

/**
 * @author 作者 shouyi
 * @version 创建时间：Dec 23, 2015 1:38:31 PM 类说明
 */
public class DragSortHListView extends HorizontalListView {
	View mDragView;
	GestureDetector mGestureDetector;
	DragSortHandler mDragSortHandler;
	int mLastDragPos = -1;
	int mOriginDragPos = -1;
	boolean mDraggable = true;

	public DragSortHListView(Context context, AttributeSet attrs) {
		// TODO Auto-generated constructor stub
		super(context, attrs);
		mGestureDetector = new GestureDetector(getContext(),
				new DragGestureListener());
	}

	@Override
	protected void addAndMeasureChild(View child, int viewPos, int index) {
		// TODO Auto-generated method stub
		super.addAndMeasureChild(child, viewPos, index);
		child.setTag(R.id.tag_dragsortlist, index);
		if (mDragSortHandler != null) {
			child.setOnTouchListener(mOnTouchListener);
		}
//		child.setOnDragListener(mOnDragListener);
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
	
	protected void childStartDrag() {
		
	}

	private OnDragListener mOnDragListener = new OnDragListener() {

		@Override
		public boolean onDrag(View v, DragEvent event) {
			Log.i(VIEW_LOG_TAG, "ACTION_DRAG_ENTERED" + ", Event: " + event.getAction());
			switch (event.getAction()) {
			case DragEvent.ACTION_DRAG_STARTED:
				// Do nothing
				break;
			case DragEvent.ACTION_DRAG_ENTERED:
				v.setAlpha(0.15f);
				if (v.getTag(R.id.tag_dragsortlist) != null && mDragSortHandler != null) {
					View dragView = (View) event.getLocalState();
					if (dragView != null && dragView.getTag(R.id.tag_dragsortlist) != null) {
						if (v.getTag(R.id.tag_dragsortlist) instanceof Integer
								&& dragView.getTag(R.id.tag_dragsortlist) instanceof Integer) {
							int fromPos = (Integer) dragView.getTag(R.id.tag_dragsortlist);
							int toPos = (Integer) v.getTag(R.id.tag_dragsortlist);
							if (toPos != fromPos && fromPos > -1 && toPos > -1) {
								mDragSortHandler.dragTo(fromPos, toPos);
							}
							Log.i(VIEW_LOG_TAG, "ACTION_DRAG_ENTERED" + ", fromPos: " + fromPos + ", toPos: " + toPos);
						}
					}
				}
				break;
			case DragEvent.ACTION_DRAG_EXITED:
				Log.i(VIEW_LOG_TAG, "ACTION_DRAG_ENTERED" + v.getTag(R.id.tag_dragsortlist));
				v.setAlpha(1F);
				break;
			case DragEvent.ACTION_DRAG_LOCATION:
				break;
			case DragEvent.ACTION_DROP:
				// View view = (View) event.getLocalState();
				// for (int i = 0, j = main.getChildCount(); i < j; i++) {
				// if (main.getChildAt(i) == v) {
				// main.removeView(view);
				// main.addView(view, i);
				// break;
				// }
				// }
				break;
			case DragEvent.ACTION_DRAG_ENDED:
				v.setAlpha(1F);
			default:
				break;
			}
			return true;
		}
	};
	
	public boolean onDragEvent(DragEvent event) {
		if (event.getAction() == DragEvent.ACTION_DRAG_STARTED) {
			mDragView = (View) event.getLocalState();
			mOriginDragPos = (Integer) mDragView.getTag(R.id.tag_dragsortlist);
			mLastDragPos = mOriginDragPos;
			if (mDragSortHandler != null) {
				mDragSortHandler.onItemDrag();
			}
		} else if (event.getAction() == DragEvent.ACTION_DRAG_LOCATION) {
			int toPos = findChildIndexUnder(event.getX(), event.getY());
			toPos += (mLeftViewIndex + 1);
			Log.i(VIEW_LOG_TAG, "ACTION_DRAG_ENTERED, mLeftViewIndex: " + mLeftViewIndex);
			Log.i(VIEW_LOG_TAG, "ACTION_DRAG_ENTERED" + ", fromPos: " + mLastDragPos + ", toPos: " + toPos);
			if (toPos > -1 && toPos != mLastDragPos) {
				if (mDragSortHandler != null) {
					mDragSortHandler.dragTo(mLastDragPos, toPos);
					Log.i(VIEW_LOG_TAG, "ACTION_DRAG_ENTERED" + ", changed fromPos: " + mLastDragPos + ", toPos: " + toPos);
					mLastDragPos = toPos;
					childStartDrag();
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
	
	private class DragGestureListener extends SimpleOnGestureListener {
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

	public static class MyDragShadowBuilder extends View.DragShadowBuilder {

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

	public interface DragSortHandler {
		public void onItemDrag();
		public void dragTo(int fromPos, int toPos);
		public void onDragEnd(int originPos, int finalPos);
	}
}
