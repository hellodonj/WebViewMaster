/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lqwawa.libs.mediapaper;

import android.content.Context;
import android.graphics.*;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

/**
 * A ViewGroup that coordinated dragging across its dscendants
 */
public class DragLayer extends FrameLayout implements DragController{

	private static final String TAG = "Drayer";

	private static final int SCROLL_DELAY = 600;
	private static final int SCROLL_ZONE = 40;
	private static final int VIBRATE_DURATION = 35;
	private static final int ANIMATION_SCALE_UP_DURATION = 110;

	// Number of pixels to add to the dragged item for scaling
	private static final float DRAG_SCALE = 24.0f;

	private boolean mDragging = false;
	private boolean mShouldDrop;
	private float mLastMotionX;
	private float mLastMotionY;

	/**
	 * The bitmap that is currently being dragged
	 */
	private Bitmap mDragBitmap = null;
	private View mOriginator;

	private int mBitmapOffsetX;
	private int mBitmapOffsetY;
	
	private VelocityTracker mVelocityTracker;
	/**
	 * ID of the active pointer. This is used to retain consistency during
	 * drags/flings if multiple pointers are used.
	 */
	private int mActivePointerId = INVALID_POINTER;
	
	/**
	 * Sentinel value for no current active pointer. Used by
	 * {@link #mActivePointerId}.
	 */
	private static final int INVALID_POINTER = -1;


	/**
	 * X offset from where we touched on the cell to its upper-left corner
	 */
	private float mTouchOffsetX;

	/**
	 * Y offset from where we touched on the cell to its upper-left corner
	 */
	private float mTouchOffsetY;
	private float mTouchOffsetYbottom;

	/**
	 * Utility rectangle
	 */
	private Rect mDragRect = new Rect();

	/**
	 * Where the drag originated
	 */
	private DragSource mDragSource;

	/**
	 * The data associated with the object being dragged
	 */
	private Object mDragInfo;

	// private View mLayoutView;

	private PaperManger mPaperManger;

	private final Rect mRect = new Rect();
	private final int[] mDropCoordinates = new int[2];

	private DragListener mListener;

	private DragScroller mDragScroller;

	private static final int SCROLL_OUTSIDE_ZONE = 0;
	private static final int SCROLL_WAITING_IN_ZONE = 1;

	private static final int SCROLL_LEFT = 0;
	private static final int SCROLL_RIGHT = 1;

	private int mScrollState = SCROLL_OUTSIDE_ZONE;

	private View mIgnoredDropTarget;

	private RectF mDragRegion;
	private boolean mEnteredRegion;
	private boolean mEnteredRegionForDrag;
	private DropTarget mLastDropTarget;

	private final Paint mTrashPaint = new Paint();
	private Paint mDragPaint;
	private Paint mPaint;
	private Canvas mCanvas;

	private static final int ANIMATION_STATE_STARTING = 1;
	private static final int ANIMATION_STATE_RUNNING = 2;
	private static final int ANIMATION_STATE_DONE = 3;

	private static final int ANIMATION_TYPE_SCALE = 1;

	private float mAnimationFrom;
	private float mAnimationTo;
	private int mAnimationDuration;
	private long mAnimationStartTime;
	private int mAnimationType;
	private int mAnimationState = ANIMATION_STATE_DONE;

	private InputMethodManager mInputMethodManager;

	private MediaPaper mMediaPaper;
	private int mToolBarHeight;
	private int mDragAction;

	private View mTempView;

	private int mCurDragViewSp = 0;

	private int mTouchSlop = 0;

	/**
	 * Used to create a new DragLayer from XML.
	 * 
	 * @param context
	 *           The application's context.
	 * @param attrs
	 *           The attribtues set containing the Workspace's customization
	 *           values.
	 */
	public DragLayer(Context context, AttributeSet attrs) {
		super(context, attrs);

		final int srcColor = context.getResources().getColor(
				R.color.delete_color_filter);
		mTrashPaint.setColorFilter(new PorterDuffColorFilter(srcColor,
				PorterDuff.Mode.SRC_ATOP));

		// Make estimated paint area in gray
		int snagColor = context.getResources().getColor(
				R.color.snag_callout_color);
		Paint estimatedPaint = new Paint();
		estimatedPaint.setColor(snagColor);
		estimatedPaint.setStrokeWidth(3);
		estimatedPaint.setAntiAlias(true);

		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setColor(Color.RED);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);

		mCanvas = new Canvas();

		final ViewConfiguration configuration = ViewConfiguration.get(context);
      mTouchSlop = configuration.getScaledTouchSlop();
	}

	//
	public void startDrag(View v, DragSource source, Object dragInfo,
			int dragAction) {

		mDragAction = dragAction;

		mToolBarHeight = mMediaPaper.getToolBarHeight();
	
//		PaperUtils.outLog("" + mToolBarHeight);

		mPaperManger.getLayoutBottom();

		// v = mPaperManger.mLayout.getChildAt(0);
		//		
		// Hide soft keyboard, if visible
		if (mInputMethodManager == null) {
			mInputMethodManager = (InputMethodManager) getContext()
					.getSystemService(Context.INPUT_METHOD_SERVICE);
		}
		mInputMethodManager.hideSoftInputFromWindow(getWindowToken(), 0);
		// mInputMethodManager.showInputMethodPicker();

		if (mListener != null) {
			mListener.onDragStart(v, source, dragInfo, dragAction);
		}

		Rect r = mDragRect;
		r.set(v.getScrollX(), v.getScrollY(), 0, 0);

		offsetDescendantRectToMyCoords(v, r);
		mTouchOffsetX = mLastMotionX - r.left;
		mTouchOffsetY = mLastMotionY - r.top;
		mTouchOffsetYbottom = v.getHeight() - mTouchOffsetY;

		// Utils.outLog(""+ v.getHeight()+
		// " "+mTouchOffsetY+" "+mTouchOffsetYbottom);

		// mMediaPaper.setOtherFocus();
		// v.clearFocus();
		v.setFocusableInTouchMode(true);
		v.setFocusable(true);
		v.requestFocus();
		v.setPressed(false);

		// Utils.outLog(""+ Integer.toHexString(mv.getId()));

		boolean willNotCache = v.willNotCacheDrawing();
		v.setWillNotCacheDrawing(false);

		// Reset the drawing cache background color to fully transparent
		// for the duration of this operation
		int color = v.getDrawingCacheBackgroundColor();
		v.setDrawingCacheBackgroundColor(0);

		if (color != 0) {
			v.destroyDrawingCache();
		}
		v.buildDrawingCache();
		Bitmap viewBitmap = v.getDrawingCache();
		if (viewBitmap == null) {
		   return;
		}
		int width = viewBitmap.getWidth();
		int height = viewBitmap.getHeight();

		// Utils.outLog("--------------------" + width + " " + height);

		Matrix scale = new Matrix();
		float scaleFactor = v.getWidth();
		scaleFactor = (scaleFactor + DRAG_SCALE) / scaleFactor;
		scale.setScale(scaleFactor, scaleFactor);

		mAnimationTo = 1.0f;
		mAnimationFrom = 1.0f / scaleFactor;
		mAnimationDuration = ANIMATION_SCALE_UP_DURATION;
		mAnimationState = ANIMATION_STATE_STARTING;
		mAnimationType = ANIMATION_TYPE_SCALE;

		try{
   		mDragBitmap = Bitmap.createBitmap(viewBitmap, 0, 0, width, height, scale,
				true);
		}catch (OutOfMemoryError e) {
         return;
      }
		v.destroyDrawingCache();
		v.setWillNotCacheDrawing(willNotCache);
		v.setDrawingCacheBackgroundColor(color);

		// mDragBitmap = mPaperManger.getBitmapForPreview();

		final Bitmap dragBitmap = mDragBitmap;

		mBitmapOffsetX = (dragBitmap.getWidth() - width) / 2;
		mBitmapOffsetY = (dragBitmap.getHeight() - height) / 2;

		// mBitmapOffsetX = 0;
		// mBitmapOffsetY = 0;

		if (dragAction == PaperUtils.DRAG_ACTION_VIEW) {
			v.setVisibility(INVISIBLE);
			// v.setFocusable(false);
			// mPaperManger.mLayout.removeView(v);
		}

		mDragPaint = null;
		mDragging = true;
		mShouldDrop = true;
		if (dragAction == PaperUtils.DRAG_ACTION_TOOLBUTTON) {
			mOriginator = mTempView;
			toolFlag = false;
		} else {
			mOriginator = v;
		}
		mDragSource = source;
		mDragInfo = dragInfo;

//		((Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE))
//				.vibrate(VIBRATE_DURATION);

		mEnteredRegion = false;

		invalidate();
		int top = mOriginator.getTop();
		mCurDragViewSp = (int) mLastMotionY - top
				+ mPaperManger.mScrollView.getScrollY();

		final ViewConfiguration configuration = ViewConfiguration
				.get(mPaperManger.mContext);
		mTouchSlop = configuration.getScaledTouchSlop();
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		return mDragging || super.dispatchKeyEvent(event);
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);

		// drawLineInMoving(canvas);

		if (mDragging && mDragBitmap != null) {

			if (mAnimationState == ANIMATION_STATE_STARTING) {
				mAnimationStartTime = SystemClock.uptimeMillis();
				mAnimationState = ANIMATION_STATE_RUNNING;
			}

			if (mAnimationState == ANIMATION_STATE_RUNNING) {
				float normalized = (float) (SystemClock.uptimeMillis() - mAnimationStartTime)
						/ mAnimationDuration;
				if (normalized >= 1.0f) {
					mAnimationState = ANIMATION_STATE_DONE;
				}
				normalized = Math.min(normalized, 1.0f);
				final float value = mAnimationFrom
						+ (mAnimationTo - mAnimationFrom) * normalized;

				switch (mAnimationType) {
				case ANIMATION_TYPE_SCALE:
					final Bitmap dragBitmap = mDragBitmap;
					canvas.save();
					canvas.translate(getScrollX() + mLastMotionX - mTouchOffsetX
							- mBitmapOffsetX, getScrollY() + mLastMotionY
							- mTouchOffsetY - mBitmapOffsetY);
					// canvas.translate((dragBitmap.getWidth() * (1.0f - value))
					// / 2,
					// (dragBitmap.getHeight() * (1.0f - value)) / 2);
					// canvas.scale(value, value);
					canvas.drawBitmap(dragBitmap, 0.0f, 0.0f, mDragPaint);
					canvas.restore();
					break;
				}
			} else {
				// Draw actual icon being dragged
				canvas.drawBitmap(mDragBitmap, getScrollX() + mLastMotionX
						- mTouchOffsetX - mBitmapOffsetX, getScrollY() + mLastMotionY
						- mTouchOffsetY - mBitmapOffsetY, mDragPaint);
			}
		}
	}

	private void endDrag() {
		if (mDragging) {
			mDragging = false;
			if (mDragBitmap != null) {
				mDragBitmap.recycle();
			}
			if (mOriginator != null) {
				mOriginator.setFocusableInTouchMode(true);
				if (mDragAction == PaperUtils.DRAG_ACTION_VIEW) {
//					mPaperManger.resetMargin(mOriginator, mOriginator.getHeight(),
//							getSp());
					mOriginator.setVisibility(VISIBLE);
					if (!(mOriginator instanceof EditText)) {
						mOriginator.setFocusable(true);
						mOriginator.requestFocus();
					}
				} else if (mDragAction == PaperUtils.DRAG_ACTION_TOOLBUTTON) {
//					if(!toolFlag)
//						mPaperManger.removeViewFromList(mOriginator, PaperUtils.DRAG_ACTION_VIEW,
//							mEnteredRegionForDrag);
						
					mPaperManger.removeViewFromList(mOriginator, mDragAction,
							mEnteredRegionForDrag);
					// Utils.outLog(""+mPaperManger.mLayout.getChildCount() + " " +
					// mPaperManger.mPaperItems.size());
				}
				if (!(mOriginator instanceof EditText)) {
					mOriginator.setFocusable(true);
					mOriginator.requestFocus();
				}
			}
			if (mListener != null) {
				mListener.onDragEnd();
			}
			mPaperManger.handleScroll(PaperUtils.SCROLL_STOP);
		}
	}

	public int getSp() {
		int i = mPaperManger.getPosByViewId(mOriginator.getId());
		if (i <= 0)
			return (int) mLastMotionY - mCurDragViewSp;
		else
			return (int) mLastMotionY
					- mCurDragViewSp
					- (mPaperManger.mLayout.getChildAt(i - 1).getBottom() - mPaperManger.mScrollView
							.getScrollY());
	}

	public boolean isEditText(int y) {
		int count = mPaperManger.mLayout.getChildCount();

		for (int i = 0; i < count; i++) {
			View iv = mPaperManger.mLayout.getChildAt(i);
			if (iv.getTop() < (y + mPaperManger.mScrollView.getScrollY())
					&& (iv.getBottom() > y + +mPaperManger.mScrollView.getScrollY())) {
				if (iv instanceof EditText) {
					return true;
				}
			}
		}
		return false;
	}

	public View getFocusedViewByPosY(int y) {
		int count = mPaperManger.mLayout.getChildCount();

		for (int i = 0; i < count; i++) {
			View iv = mPaperManger.mLayout.getChildAt(i);
			if (iv.getTop() < (y + mPaperManger.mScrollView.getScrollY())
					&& (iv.getBottom() > y + +mPaperManger.mScrollView.getScrollY())) {
				// if (iv instanceof SketchHold) {
				// return iv;
				// }
				return mPaperManger.getViewFromNeedFocusViewListById(iv.getId());
			}
		}
		return null;
	}

	public void editTextLoseFocus(int y) {
		View v = mPaperManger.mLayout.getFocusedChild();

//		if (!isEditText(y) && MediaPaper.numEdit >= 0) {
//			MediaPaper.numEdit = -1;
//			InputMethodManager imm = (InputMethodManager) mPaperManger.mContext
//					.getSystemService(Context.INPUT_METHOD_SERVICE);
//			if (imm != null) {
//				imm.showSoftInput(mOriginator, 0);
//			}
//		}

		if (v == null) {
			return;
		}

		// Utils.outLog(""+v.getTop() + " " + v.getBottom() + " "+
		// (y+mPaperManger.mScrollView.getScrollY()));

		if (v instanceof EditText) {
			if ((v.getTop() > (y + mPaperManger.mScrollView.getScrollY()) || (y + mPaperManger.mScrollView
					.getScrollY()) > v.getBottom())) {

				v.clearFocus();
				v.setFocusable(false);
				mMediaPaper.setOtherFocus();
				EditText editText = (EditText) v;
				if (editText.getText().toString().equals("")
						|| editText.getText().toString() == null) {
					mPaperManger.removeViewFromList(v, PaperUtils.DRAG_ACTION_VIEW,
							mEnteredRegionForDrag);
				}
			}
		}
	}
	private void PopupKeyboard(int y){

//		Utils.outLog(""+ y + " "+ mPaperManger.getPaperBottom() + " "+ mPaperManger.mLayout.getBottom());
		InputMethodManager imm = (InputMethodManager) mPaperManger.mContext
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm != null) {
			imm.showSoftInput(mOriginator, 0);
		}

		return;
	}
	
	public void createNewEditTextForClick(int y, boolean mode) {
		if(y > mToolBarHeight && y < mMediaPaper.getScrollViewBottom()) {
   		createNewEditTextForClick(y - mToolBarHeight 
   						+ mPaperManger.mScrollView.getScrollY());
		}
	}
	
	public void createNewEditTextForClick(int y) {
//	   if (!mMediaPaper.isEditMode()) {
//	      return;
//	   }
//		int count = mPaperManger.mLayout.getChildCount();
//		View v = null;
//
//		if (count == 0) {
//			mPaperManger.createNewEditText(-1);
//		} else {
//
//			v = mPaperManger.mLayout.getChildAt(count - 1);
//			int  paperBottom = v.getBottom();
//
//			if (y > paperBottom) {
//				if (!(v instanceof EditText)) {
//					mPaperManger.createNewEditText(-1);
//					v = mPaperManger.mLayout.getChildAt(mPaperManger.mLayout.getChildCount() - 1);
//				} else if (v instanceof EditText) {
//					v.setFocusableInTouchMode(true);
//					v.setFocusable(true);
//					v.requestFocus();
//				}
//				InputMethodManager imm = (InputMethodManager) mPaperManger.mContext
//						.getSystemService(Context.INPUT_METHOD_SERVICE);
//				if (imm != null) {
//					imm.showSoftInput(v, 0);
//				}
//			} else {
//				for (int i = 0; i < count; i++) {
//					View iv = mPaperManger.mLayout.getChildAt(i);
//					LinearLayout.LayoutParams llp = (LinearLayout.LayoutParams) iv
//							.getLayoutParams();
//					if ((iv.getTop() - llp.topMargin < y && y < iv.getTop())) {
//						if (i == 0) {
//							if (iv instanceof EditText) {
//								iv.setFocusableInTouchMode(true);
//								iv.setFocusable(true);
//								iv.requestFocus();
//							} else {
//								mPaperManger.createNewEditText(0);
//							}
//						} else {
//							if (iv instanceof EditText) {
//								iv.setFocusableInTouchMode(true);
//								iv.setFocusable(true);
//								iv.requestFocus();
//							} else {
//								View iv1 = mPaperManger.mLayout.getChildAt(i - 1);
//								if (iv1 instanceof EditText) {
//									iv1.setFocusableInTouchMode(true);
//									iv1.setFocusable(true);
//									iv1.requestFocus();
//								} else {
//									mPaperManger.createNewEditText(i);
//									llp.topMargin = llp.topMargin - 55;
//									iv.setLayoutParams(llp);
//									PaperUtils.childViewData mData = null;
//									mData = mPaperManger.mPaperItems.get(i + 1);
//									mData.mMarginTop = llp.topMargin;
//								}
//							}
//						}
//						break;
//					}
//				}
//			}
//			// Utils.outLog(""+y +
//			// " "+mPaperManger.mLayout.getChildAt(count-1).getBottom() + " "
//			// +mPaperManger.mLayout.getBottom());
//		}
	}

	public void focusedViewLostFocus(int y) {
//	   if (y < 0) {
//	      mPaperManger.ClearAllNeedFocusViewFocus();
//	   } else if (mPaperManger.isViewFocusMode()) {
//   		View view = getFocusedViewByPosY(y);
//   		if (view == null
//   				|| (view != null && view.getId() != mPaperManger
//   						.getCurrentFocusViewFromNeedFocusViewList().getId())) {
//   			mPaperManger.ClearAllNeedFocusViewFocus();
//   		}
//
//	   }
	}
	
	private boolean clickFlag = false;
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		final int action = ev.getAction();

		final float x = ev.getX();
		final float y = ev.getY();

		switch (action) {
		case MotionEvent.ACTION_MOVE:
//			PaperUtils.outLog("move");
//			int deltaX = (int) (mLastMotionX - x);
//			int deltaY = (int) (mLastMotionY - y);
//         PaperUtils.outLog("dddddddddddd" + " " + mTouchSlop + " " + Math.abs(deltaY));
//         if (Math.abs(deltaX) < mTouchSlop && Math.abs(deltaY) < mTouchSlop) {
//         	clickFlag = true;
//            break;
//         }  
//         clickFlag = false;
			break;

		case MotionEvent.ACTION_DOWN:
			// Remember location of down touch
			mLastMotionX = x;
			mLastMotionY = y;
			mLastDropTarget = null;
			mToolBarHeight = mMediaPaper.getToolBarHeight();
			focusedViewLostFocus((int) y - mToolBarHeight);
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
//			if((int)y > mToolBarHeight && clickFlag){
//				createNewEditTextForClick((int) y - mToolBarHeight
//						+ mPaperManger.mScrollView.getScrollY());
//				clickFlag = false;
//			}
//				PopupKeyboard((int) y - mToolBarHeight
//						+ mPaperManger.mScrollView.getScrollY());
//				editTextLoseFocus((int) y - mToolBarHeight);
			if (mShouldDrop) {
				mShouldDrop = false;
				invalidate();
			}
			endDrag();
			break;
		}

		return mDragging;
	}

	static int startY = -1;
	static int num = 0;
//	static int oldY = 99999999;
	static int slipAction = -1;
	static int ii = 0;
	private boolean toolFlag = false;

	@Override
	public boolean onTouchEvent(MotionEvent ev) {

		if (!mDragging) {
			return false;
		}

		final int action = ev.getAction();
		final float x = ev.getX();
		final float y = ev.getY();
		int dragY = -1;
		boolean flagDrag = false;

		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(ev);

		switch (action) {
		case MotionEvent.ACTION_DOWN: {

			// Remember where the motion event started
			mLastMotionX = x;
			mLastMotionY = y;
			mActivePointerId = ev.getPointerId(0);

			if ((y < SCROLL_ZONE) || (y > getHeight() - SCROLL_ZONE)) {
				mScrollState = SCROLL_WAITING_IN_ZONE;
			} else {
				mScrollState = SCROLL_OUTSIDE_ZONE;
			}
			break;
		}
		case MotionEvent.ACTION_MOVE: {

//			final VelocityTracker velocityTracker = mVelocityTracker;
//			velocityTracker.computeCurrentVelocity(1000);
////			int initialVelocitx = (int) velocityTracker
////					.getXVelocity(mActivePointerId);
//			int initialVelocity = (int) velocityTracker
//					.getYVelocity(mActivePointerId);
//			Utils.outLog(""+initialVelocity);
			
			final int scrollX = getScrollX();
			final int scrollY = getScrollY();

			final float touchX = mTouchOffsetX;
			final float touchY = mTouchOffsetY;

			final int offsetX = mBitmapOffsetX;
			final int offsetY = mBitmapOffsetY;

			int left = (int) (scrollX + mLastMotionX - touchX - offsetX);
			int top = (int) (scrollY + mLastMotionY - touchY - offsetY);

			final Bitmap dragBitmap = mDragBitmap;
			final int width = dragBitmap.getWidth();
			final int height = dragBitmap.getHeight();

			final Rect rect = mRect;
			rect.set(left - 1, top - 1, left + width + 1, top + height + 1);

			left = (int) (scrollX + x - touchX - offsetX);
			top = (int) (scrollY + y - touchY - offsetY);

			// Invalidate current icon position
			rect.union(left - 1, top - 1, left + width + 1, top + height + 1);

			final int[] coordinates = mDropCoordinates;

			DropTarget dropTarget = findDropTarget((int) x, (int) y, coordinates);
			if (dropTarget != null) {
				if (mLastDropTarget == dropTarget) {
					dropTarget.onDragOver(mDragSource, coordinates[0],
							coordinates[1], (int) mTouchOffsetX, (int) mTouchOffsetY,
							mDragInfo);
				} else {
					if (mLastDropTarget != null) {
						mLastDropTarget.onDragExit(mDragSource, coordinates[0],
								coordinates[1], (int) mTouchOffsetX,
								(int) mTouchOffsetY, mDragInfo);
					}
					dropTarget.onDragEnter(mDragSource, coordinates[0],
							coordinates[1], (int) mTouchOffsetX, (int) mTouchOffsetY,
							mDragInfo);
				}
			} else {
				if (mLastDropTarget != null) {
					mLastDropTarget.onDragExit(mDragSource, coordinates[0],
							coordinates[1], (int) mTouchOffsetX, (int) mTouchOffsetY,
							mDragInfo);
				}
			}

			invalidate(rect);

			mLastDropTarget = dropTarget;

			boolean inDragRegion = false;
			if (mDragRegion != null && mDragAction == PaperUtils.DRAG_ACTION_VIEW) {
				final RectF region = mDragRegion;

				final boolean inRegion = region
						.contains(ev.getRawX(), ev.getRawY());
				if (!mEnteredRegion && inRegion) {
					mDragPaint = mTrashPaint;
					mEnteredRegion = true;
					inDragRegion = true;
					mListener.onDragEnter();
				} else if (mEnteredRegion && !inRegion) {
					mDragPaint = null;
					mEnteredRegion = false;
					mListener.onDragExit();
				}
			}


			dragY = (int) y;

			if(mDragAction == PaperUtils.DRAG_ACTION_TOOLBUTTON ){
				if(y < mToolBarHeight ){
					toolFlag = false;
				}else{
					toolFlag = true;
				}
			}

			if (!inDragRegion
					&& ((y - mTouchOffsetY - offsetY) <= mToolBarHeight + SCROLL_ZONE )
					&& !mPaperManger.isScrollTop()) {
				mPaperManger.handleScroll(PaperUtils.SCROLL_UP);
//				slipAction = PaperUtils.SLIP_ACTIONUP;
//				flagDrag = true;
			} else if (!inDragRegion
					&& (y + mTouchOffsetYbottom + mBitmapOffsetY) > getHeight()
							- SCROLL_ZONE && !mPaperManger.isScrollBottom()) {
				mPaperManger.handleScroll(PaperUtils.SCROLL_DOWN);
//				slipAction = PaperUtils.SLIP_ACTIONDOWN;
//				flagDrag = true;
			} else {
				mPaperManger.handleScroll(PaperUtils.SCROLL_STOP);
			}

			if(y < mToolBarHeight){
				dragY = mPaperManger.mScrollView.getScrollY();
			}else{
				dragY = (int) y - mToolBarHeight
						+ mPaperManger.mScrollView.getScrollY();
			}

			mLastMotionX = x;
			mLastMotionY = y;


			if (y > 0) {// && mPaperManger.getScrollState() ==
				// Utils.SCROLL_STOP) {
				mEnteredRegionForDrag = true;
				int startY0 = -1, startY1 = -1;
				startY0 = mOriginator.getTop();
				startY1 = mOriginator.getTop() + mOriginator.getHeight() ;

				if (mDragAction == PaperUtils.DRAG_ACTION_VIEW) {// || (mDragAction ==
					if (!flagDrag) {
						if (startY0 > (int) (dragY - mTouchOffsetY - offsetY)) {
							slipAction = PaperUtils.SLIP_ACTIONUP;
						}else if(startY1 < (int) (dragY + mTouchOffsetYbottom + mBitmapOffsetY)){
							slipAction = PaperUtils.SLIP_ACTIONDOWN;
						}
					}
					if (slipAction == PaperUtils.SLIP_ACTIONUP) {
						dragY = (int) (dragY - mTouchOffsetY - offsetY + 12);
						if (dragY < 0) {
							dragY = 1;
						}

					} else if (slipAction == PaperUtils.SLIP_ACTIONDOWN) {
						dragY = (int) (dragY + mTouchOffsetYbottom + mBitmapOffsetY - 12);
					}


				} else if (mDragAction == PaperUtils.DRAG_ACTION_TOOLBUTTON) {
					if(!toolFlag)	break;
					int startY = -1;
					startY = mOriginator.getTop() + mOriginator.getHeight() / 2;
					dragY = (int) y - mToolBarHeight
							+ mPaperManger.mScrollView.getScrollY();
					if (startY > dragY) {
						slipAction = PaperUtils.SLIP_ACTIONUP;
					} else if (startY < dragY) {
						slipAction = PaperUtils.SLIP_ACTIONDOWN;
					}
				}
//				if (ii > 0) {
					mPaperManger.updateView(mOriginator, dragY , slipAction,
							mDragAction);
//					ii = 0;
//				}
				// Utils.outLog(""+slipAction);
			} else {
				mEnteredRegionForDrag = false;
			}


			// Utils.outLog("===" + mToolBarHeight);
			// mPaperManger.getPositonByView(mOriginator, (int) (y -
			// mToolBarHeight));
			// mPaperManger.handleViewTrans(mOriginator, (int)y, true);
			// drawLineInMoving(null);

			// Utils.outLog("drayer", "" + ev.getY() + " " + ev.getRawY() + " " +
			// getScrollY());

			break;
		}
		case MotionEvent.ACTION_UP:
			mLastMotionY = y;
			if (mShouldDrop) {
				drop(ev.getRawX(), ev.getRawY(), (int) mLastMotionY);
				mShouldDrop = false;
			}
			endDrag();

			toolFlag = false;
			PaperManger.first = 0;
			startY = -1;
			break;
		case MotionEvent.ACTION_CANCEL:
			endDrag();
		}

		return true;
	}

	private boolean drop(float x, float y, int top) {
		invalidate();

		// getLocationByView(mOriginator , top);

		if (mEnteredRegion) {
			mPaperManger.removeViewFromList(mOriginator, mDragAction,
					mEnteredRegionForDrag);
			return true;
		}
		return false;
	}

	DropTarget findDropTarget(int x, int y, int[] dropCoordinates) {
		return findDropTarget(this, x, y, dropCoordinates);
	}

	private DropTarget findDropTarget(ViewGroup container, int x, int y,
			int[] dropCoordinates) {
		final Rect r = mDragRect;
		final int count = container.getChildCount();
		final int scrolledX = x + container.getScrollX();
		final int scrolledY = y + container.getScrollY();
		final View ignoredDropTarget = mIgnoredDropTarget;

		for (int i = count - 1; i >= 0; i--) {
			final View child = container.getChildAt(i);
			if (child.getVisibility() == VISIBLE && child != ignoredDropTarget) {
				child.getHitRect(r);
				if (r.contains(scrolledX, scrolledY)) {
					DropTarget target = null;
					if (child instanceof ViewGroup) {
						x = scrolledX - child.getLeft();
						y = scrolledY - child.getTop();
						target = findDropTarget((ViewGroup) child, x, y,
								dropCoordinates);
					}
					if (target == null) {
						if (child instanceof DropTarget) {
							// Only consider this child if they will accept
							DropTarget childTarget = (DropTarget) child;
							if (childTarget.acceptDrop(mDragSource, x, y, 0, 0,
									mDragInfo)) {
								dropCoordinates[0] = x;
								dropCoordinates[1] = y;
								return (DropTarget) child;
							} else {
								return null;
							}
						}
					} else {
						return target;
					}
				}
			}
		}

		return null;
	}

	public void setDragScoller(DragScroller scroller) {
		mDragScroller = scroller;
	}
	public void setDragListener(DragListener l) {
		mListener = l;
	}

	@SuppressWarnings( { "UnusedDeclaration" })
	public void removeDragListener(DragListener l) {
		mListener = null;
	}

	/**
	 * Specifies the view that must be ignored when looking for a drop target.
	 * 
	 * @param view
	 *           The view that will not be taken into account while looking for a
	 *           drop target.
	 */
	void setIgnoredDropTarget(View view) {
		mIgnoredDropTarget = view;
	}

	/**
	 * Specifies the delete region.
	 * 
	 * @param region
	 *           The rectangle in screen coordinates of the delete region.
	 */
	void setDeleteRegion(RectF region) {
		mDragRegion = region;
	}

	/*
	 * void setLayoutView(View v) { mLayoutView = v; }
	 */
	void setPaperManger(PaperManger mPaperManger) {
		this.mPaperManger = mPaperManger;
	}

	void setMediaPaper(MediaPaper mMediaPaper) {
		this.mMediaPaper = mMediaPaper;
	}

	void setTempView(View v) {
		mTempView = v;

		mPaperManger.first = 1;
		// mPaperManger.addTempView(v);
	}

}
