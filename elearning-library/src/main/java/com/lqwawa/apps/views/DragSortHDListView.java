package com.lqwawa.apps.views;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * @author 作者 shouyi
 * @version 创建时间：Feb 1, 2016 11:15:45 AM
 * 类说明
 */
public class DragSortHDListView extends DragSortHListView {
	DeleteHandler mDeleteHandler;
	
	public DragSortHDListView(Context context, AttributeSet attrs) {
		// TODO Auto-generated constructor stub
		super(context, attrs);
	}
	
	public void setDeleteHandler(DeleteHandler handler) {
		mDeleteHandler = handler;
	}
	
	@Override
	protected void processChildLongPress() {
		// TODO Auto-generated method stub
		super.processChildLongPress();
		if (mDeleteHandler != null) {
			mDeleteHandler.showDeleteBtn(true);
		}
		requestFocus();
	}
	
	@Override
	protected void onFocusChanged(boolean gainFocus, int direction,
			Rect previouslyFocusedRect) {
		// TODO Auto-generated method stub
		super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
		if (!gainFocus && mDeleteHandler != null) {
			mDeleteHandler.showDeleteBtn(false);
		}
	}
	
	@Override
	protected void childStartDrag() {
		// TODO Auto-generated method stub
		super.childStartDrag();
		if (mDeleteHandler != null) {
			mDeleteHandler.showDeleteBtn(false);
		}
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
//			if (mDeleteHandler != null) {
//				mDeleteHandler.showDeleteBtn(false);
//			}
		}
		return super.dispatchTouchEvent(ev);
	}
	
	public interface DeleteHandler {
		void showDeleteBtn(boolean isShow);
		void onDelete(int pos);
	}
}
