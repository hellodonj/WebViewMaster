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
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.TransitionDrawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.*;
import android.widget.ImageView;

public class DeleteZone extends ImageView implements DropTarget,
		DragController.DragListener {
	private static final int ORIENTATION_HORIZONTAL = 1;
	private static final int TRANSITION_DURATION = 250;
	private static final int ANIMATION_DURATION = 200;

	private final int[] mLocation = new int[2];

	private boolean mTrashMode;

	private AnimationSet mInAnimation;
	private AnimationSet mOutAnimation;
	private Animation mHandleInAnimation;
	private Animation mHandleOutAnimation;

	private int mOrientation;

	private DragLayer mDragLayer;

	private final RectF mRegion = new RectF();
	private TransitionDrawable mTransition;
	
	private int mDragAction;

	// private View mHandle;

	public DeleteZone(Context context) {
		super(context);
	}

	public DeleteZone(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public DeleteZone(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.DeleteZone, defStyle, 0);
		mOrientation = a.getInt(R.styleable.DeleteZone_direction,
				ORIENTATION_HORIZONTAL);
		a.recycle();
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		mTransition = (TransitionDrawable) getBackground();
	}

	public boolean acceptDrop(DragSource source, int x, int y, int xOffset,
			int yOffset, Object dragInfo) {
		return true;
	}

	public Rect estimateDropLocation(DragSource source, int x, int y,
			int xOffset, int yOffset, Object dragInfo, Rect recycle) {
		return null;
	}

	static final int MSG_UNINSTALL = 0;
	static final int UNINSTALL_DURATION = 1000;

	/**
	 * Try removing the dropped icon/widget
	 */
	private void removeDropped() {
		if (mFlagUninstall && mUninstallUri != null) {
			mFlagUninstall = false;
			try {
				Intent it = new Intent(Intent.ACTION_DELETE, mUninstallUri);
				getContext().startActivity(it);
			} catch (Exception e) {
			}
		}
	}

	public void onDrop(DragSource source, int x, int y, int xOffset,
			int yOffset, Object dragInfo) {

	}

	boolean mFlagUninstall = false;
	Uri mUninstallUri = null;

	public void onDragEnter(DragSource source, int x, int y, int xOffset,
			int yOffset, Object dragInfo) {
		mTransition.reverseTransition(TRANSITION_DURATION);
	}

	public void onDragOver(DragSource source, int x, int y, int xOffset,
			int yOffset, Object dragInfo) {
	}

	public void onDragExit(DragSource source, int x, int y, int xOffset,
			int yOffset, Object dragInfo) {
		mTransition.reverseTransition(TRANSITION_DURATION);
	}

	static boolean sDragging = false;

	public void onDragStart(View v, DragSource source, Object info,
			int dragAction) {
		mTrashMode = true;
		mDragAction = dragAction;
		createAnimations();
		final int[] location = mLocation;
		getLocationOnScreen(location);
		mRegion.set(location[0], location[1], location[0] + getRight()
				- getLeft(), location[1] + getBottom() - getTop());
		mDragLayer.setDeleteRegion(mRegion);
		if (mDragAction == PaperUtils.DRAG_ACTION_VIEW) {
			mTransition.resetTransition();
			startAnimation(mInAnimation);
			setVisibility(VISIBLE);
		}
		sDragging = true;
	}

	public void onDragEnd() {
		if (mTrashMode) {
			mTrashMode = false;
			mDragLayer.setDeleteRegion(null);
			if (mDragAction == PaperUtils.DRAG_ACTION_VIEW) {
				startAnimation(mOutAnimation);
				setVisibility(INVISIBLE);
			}
		}
		sDragging = false;
	}

	private void createAnimations() {
		if (mInAnimation == null) {
			mInAnimation = new FastAnimationSet();
			final AnimationSet animationSet = mInAnimation;
			animationSet.setInterpolator(new AccelerateInterpolator());
			animationSet.addAnimation(new AlphaAnimation(0.0f, 1.0f));
			if (mOrientation == ORIENTATION_HORIZONTAL) {
				animationSet.addAnimation(new TranslateAnimation(
						Animation.ABSOLUTE, 0.0f, Animation.ABSOLUTE, 0.0f,
						Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF,
						0.0f));
			} else {
				animationSet.addAnimation(new TranslateAnimation(
						Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF,
						0.0f, Animation.ABSOLUTE, 0.0f, Animation.ABSOLUTE, 0.0f));
			}
			animationSet.setDuration(ANIMATION_DURATION);
		}
		if (mOutAnimation == null) {
			mOutAnimation = new FastAnimationSet();
			final AnimationSet animationSet = mOutAnimation;
			animationSet.setInterpolator(new AccelerateInterpolator());
			animationSet.addAnimation(new AlphaAnimation(1.0f, 0.0f));
			if (mOrientation == ORIENTATION_HORIZONTAL) {
				animationSet.addAnimation(new FastTranslateAnimation(
						Animation.ABSOLUTE, 0.0f, Animation.ABSOLUTE, 0.0f,
						Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
						1.0f));
			} else {
				animationSet.addAnimation(new FastTranslateAnimation(
						Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
						1.0f, Animation.ABSOLUTE, 0.0f, Animation.ABSOLUTE, 0.0f));
			}
			animationSet.setDuration(ANIMATION_DURATION);
		}
	}

	void setDragController(DragLayer dragLayer) {
		mDragLayer = dragLayer;
	}

	// void setHandle(View view) {
	// mHandle = view;
	// }

	private static class FastTranslateAnimation extends TranslateAnimation {
		public FastTranslateAnimation(int fromXType, float fromXValue,
				int toXType, float toXValue, int fromYType, float fromYValue,
				int toYType, float toYValue) {
			super(fromXType, fromXValue, toXType, toXValue, fromYType, fromYValue,
					toYType, toYValue);
		}

		@Override
		public boolean willChangeTransformationMatrix() {
			return true;
		}

		@Override
		public boolean willChangeBounds() {
			return false;
		}
	}

	private static class FastAnimationSet extends AnimationSet {
		FastAnimationSet() {
			super(false);
		}

		@Override
		public boolean willChangeTransformationMatrix() {
			return true;
		}

		@Override
		public boolean willChangeBounds() {
			return false;
		}
	}

	@Override
	public void onDragEnter() {
		mTransition.reverseTransition(TRANSITION_DURATION);
	}

	@Override
	public void onDragExit() {
		mTransition.reverseTransition(TRANSITION_DURATION);
	}
}
