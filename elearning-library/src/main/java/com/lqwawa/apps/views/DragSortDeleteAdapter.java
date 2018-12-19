package com.lqwawa.apps.views;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import com.lqwawa.tools.ResourceUtils;

/**
 * @author 作者 shouyi
 * @version 创建时间：Feb 15, 2016 1:20:01 PM 类说明
 */
public abstract class DragSortDeleteAdapter extends BaseAdapter {
	public static final int DelViewId = 100;
	DragSortDeleteHandler mDragSortDeleteHandler;
	Context mContext;
	int mDelIconId;
	LayoutParams mDelIconParams;
	boolean mIsDeleteStatus;

	public DragSortDeleteAdapter(Context context, int delIconId,
			LayoutParams delIconLParams) {
		mContext = context;
		mDelIconId = delIconId;
		mDelIconParams = delIconLParams;
	}

	protected abstract boolean isDeletable(int position);
	
	public void setDragDeleteHandler(DragSortDeleteHandler handler) {
		mDragSortDeleteHandler = handler;
	}
	
	public void setDeleteStatus(boolean value) {
		mIsDeleteStatus = value;
	}
	
	public boolean getDeleteStatus() {
		return mIsDeleteStatus;
	}
	
	public View createConvertView(int position, int layoutId) {
		DeleteGroupView item = new DeleteGroupView(mContext, layoutId,
				mDelIconId, mDelIconParams);
		if (mIsDeleteStatus && isDeletable(position)) {
//			item.findViewById(DelViewId).setVisibility(View.VISIBLE);
			item.findViewById(ResourceUtils.getId(mContext, "delete_icon"))
					.setVisibility(View.VISIBLE);
		}
		return item;
	}
	
	public void setDeleteView(View view, int position) {
		if (mIsDeleteStatus && isDeletable(position)) {
//			view.findViewById(DelViewId).setVisibility(View.VISIBLE);
			view.findViewById(ResourceUtils.getId(mContext, "delete_icon"))
					.setVisibility(View.VISIBLE);
		} else {
//			view.findViewById(DelViewId).setVisibility(View.GONE);
			view.findViewById(ResourceUtils.getId(mContext, "delete_icon"))
					.setVisibility(View.GONE);
		}
	}

	class DeleteGroupView extends RelativeLayout implements OnClickListener {
		public DeleteGroupView(Context context, int layoutId, int delIconId,
				LayoutParams delIconLParams) {
			// TODO Auto-generated constructor stub
			super(context);
			View view = LayoutInflater.from(getContext()).inflate(layoutId,
					this, false);
			addView(view);
//			ImageView delView = new ImageView(mContext);
            ImageView delView = (ImageView) view.findViewById(ResourceUtils.getId(context,
					"delete_icon"));
//			int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25, context.getResources().getDisplayMetrics());
//			delView.setPadding(0, 0, padding, padding);
//			delView.setImageResource(delIconId);
//			if (delIconLParams != null) {
//				addView(delView, delIconLParams);
//			} else {
//				addView(delView);
//			}
//			delView.setId(DelViewId);
			delView.setVisibility(View.GONE);
			delView.setOnClickListener(this);
//			View view = LayoutInflater.from(getContext()).inflate(layoutId,
//					this, false);
//			addView(view);
//			RelativeLayout delLayout = new RelativeLayout(mContext);
//			addView(delLayout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
//			ImageView delView = new ImageView(mContext);
//			delView.setImageResource(delIconId);
//			if (delIconLParams != null) {
//				delLayout.addView(delView, delIconLParams);
//			} else {
//				delLayout.addView(delView);
//			}
//			delLayout.setId(DelViewId);
//			delLayout.setVisibility(View.GONE);
//			delLayout.setOnClickListener(this);
		}
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (mDragSortDeleteHandler != null) {
				mDragSortDeleteHandler.onDeleteItem((Integer) v.getTag());
			}
		}
	}
	
	public static interface DragSortDeleteHandler {
		public void onDeleteItem(int position);
	}
}
