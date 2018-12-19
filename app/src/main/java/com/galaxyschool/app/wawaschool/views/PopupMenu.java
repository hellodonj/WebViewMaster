package com.galaxyschool.app.wawaschool.views;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.DensityUtils;

import java.util.List;

public class PopupMenu extends PopupWindow {
	Activity mContext;
	View mRootView;
	List<PopupMenuData> mItemsDatas;
	int mWidth = 0;
	float mOffset = 0;
	OnItemClickListener mItemClickListener;
	List<Integer> mGroupSeperatePos;
	private TheAdapter adapter;
	private static final float DEFAULT_SCALE=0.15f;
	private float scale=DEFAULT_SCALE;
	private boolean isChangeTextTitleAttr;

	public PopupMenu(final Activity context,
			OnItemClickListener itemClickListener, List<PopupMenuData> items) {
		mContext = context;
		mItemClickListener = itemClickListener;
		mItemsDatas = items;
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mRootView = inflater.inflate(R.layout.popup_window, null);
		initView();
		scale=DEFAULT_SCALE;
		setProperty();
	}
	public PopupMenu(final Activity context,
					 OnItemClickListener itemClickListener, List<PopupMenuData> items,int mWidth) {
		mContext = context;
		mItemClickListener = itemClickListener;
		mItemsDatas = items;
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mRootView = inflater.inflate(R.layout.popup_window, null);
		initView();
		scale=DEFAULT_SCALE;
		setProperty(mWidth);
	}

	public PopupMenu(final Activity context,
					 OnItemClickListener itemClickListener, List<PopupMenuData> items,float scale) {
		mContext = context;
		mItemClickListener = itemClickListener;
		mItemsDatas = items;
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mRootView = inflater.inflate(R.layout.popup_window, null);
		initView();
		this.scale=scale;
		setProperty();
	}
	public PopupMenu(final Activity context, int width, int height,
			OnItemClickListener itemClickListener, List<PopupMenuData> items) {
		this(context, itemClickListener, items);
		this.setWidth(width);
		this.setHeight(height);
	}



	public void initView( ) {
		ListView listView = (ListView) mRootView.findViewById(R.id.listview);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if (mItemClickListener != null) {
					mItemClickListener.onItemClick(parent, view, position,
							mItemsDatas.get(position).mId);
				}
				dismiss();
			}
		});
		adapter = new TheAdapter();
		listView.setAdapter(adapter);
	}

	public void updateView() {
		if (this.adapter != null) {
			this.adapter.notifyDataSetChanged();
		}
	}

	public void setData(List<PopupMenuData> items){
		this.mItemsDatas = items;
		updateView();
	}
	private void setProperty() {
		int h = mContext.getWindowManager().getDefaultDisplay().getHeight();
		int w = mContext.getWindowManager().getDefaultDisplay().getWidth();
		mWidth = w;
		this.setContentView(mRootView);
		this.setWidth((int) (w * 0.5 - mOffset));
		this.setHeight(LayoutParams.WRAP_CONTENT);
		this.setFocusable(true);
		this.setTouchable(true);
		this.setOutsideTouchable(true);
		this.update();
		ColorDrawable dw = new ColorDrawable(0000000000);
		this.setBackgroundDrawable(dw);
	}
	private void setProperty(int mWidth) {
		int h = mContext.getWindowManager().getDefaultDisplay().getHeight();
		int w = mContext.getWindowManager().getDefaultDisplay().getWidth();
		this.setContentView(mRootView);
		this.setWidth( (mWidth));
		this.setHeight(LayoutParams.WRAP_CONTENT);
		this.setFocusable(true);
		this.setTouchable(true);
		this.setOutsideTouchable(true);
		this.update();
		ColorDrawable dw = new ColorDrawable(0000000000);
		this.setBackgroundDrawable(dw);
	}
	public void setGroupSeperatePos(List<Integer> speratePosList) {
		mGroupSeperatePos = speratePosList;
	}

	public void showPopupMenu(View parent) {
		if (!this.isShowing()) {
			this.showAsDropDown(
					parent,
					-(int) (mWidth / 2 - mOffset) + parent.getWidth() / 16 * 15,
					26);
		} else {
			this.dismiss();
		}
	}

	class TheAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return mItemsDatas == null ? 0 : mItemsDatas.size();
		}

		@Override
		public Object getItem(int position) {
			return mItemsDatas.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = LayoutInflater.from(mContext).inflate(R.layout.icon_text_item, null);
			PopupMenuData data = mItemsDatas.get(position);
			ImageView imageView = (ImageView) view.findViewById(R.id.icon);
			TextView textView = (TextView) view.findViewById(R.id.text);
			if (data.mTextId > 0) {
				textView.setText(data.mTextId);
			} else {
				textView.setText(data.mText);
			}
			if (mItemsDatas.get(position).mIcon == 0) {
				imageView.setVisibility(View.GONE);
				textView.setGravity(Gravity.CENTER);
			} else {
				imageView.setImageResource(mItemsDatas.get(position).mIcon);
			}
			//更改textView的Attr
			if (isChangeTextTitleAttr){
				LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.ll_layout);
				linearLayout.setGravity(Gravity.START);
				textView.setSingleLine(false);
				textView.setMaxLines(2);
				textView.setPadding(DensityUtils.dp2px(mContext,10),0,DensityUtils.dp2px(mContext,10),0);
				textView.setGravity(Gravity.START|Gravity.CENTER);
				textView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
				if (data.isOnlineSchool) {
					Drawable rightDrawable = mContext.getResources().getDrawable(R.drawable.icon_online_school_tag);
					rightDrawable.setBounds(0, 0, rightDrawable.getMinimumWidth(), rightDrawable.getMinimumHeight());
					textView.setCompoundDrawablesWithIntrinsicBounds(null, null, rightDrawable, null);
					textView.setCompoundDrawablePadding(8);
				} else {
					textView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
				}
			}
			if (mGroupSeperatePos != null && mGroupSeperatePos.size() > 0) {
				for (int i = 0; i < mGroupSeperatePos.size(); i++) {
					if (position + 1 == mGroupSeperatePos.get(i)) {
						view.findViewById(R.id.divider_black).setVisibility(
								View.GONE);
						view.findViewById(R.id.divider_green).setVisibility(
								View.VISIBLE);
						break;
					}
				}
			}
			if (position == mItemsDatas.size() - 1) {
				view.findViewById(R.id.divider_black).setVisibility(View.GONE);
			}
			view.setTag(data);
			return view;
		}
	}

	public static class PopupMenuData {
		private int mIcon;
		private int mTextId;
		private int mId;
		private String mIconUrl;
		private String mText;
		private boolean isSelect = false;
		private boolean isOnlineSchool;

		public PopupMenuData() {

		}

		public PopupMenuData(int icon, int textId) {
			mIcon = icon;
			mTextId = textId;
		}

		public PopupMenuData(int icon, int textId, int id) {
			mIcon = icon;
			mTextId = textId;
			mId = id;
		}

		public int getIcon() {
			return mIcon;
		}

		public void setIcon(int icon) {
			this.mIcon = icon;
		}

		public int getTextId() {
			return mTextId;
		}

		public void setTextId(int textId) {
			this.mTextId = textId;
		}

		public int getId() {
			return mId;
		}

		public void setId(int id) {
			this.mId = id;
		}

		public String getText() {
			return mText;
		}

		public void setText(String text) {
			this.mText = text;
		}

		public String getIconUrl() {
			return mIconUrl;
		}

		public void setIconUrl(String iconUrl) {
			this.mIconUrl = iconUrl;
		}

		public boolean isSelect() {
			return isSelect;
		}

		public void setIsSelect(boolean isSelect) {
			this.isSelect = isSelect;
		}

		public void setIsOnlineSchool(boolean onlineSchool) {
			isOnlineSchool = onlineSchool;
		}
	}

	public void setChangeTextTitleAttr(boolean changeTextTitleAttr) {
		isChangeTextTitleAttr = changeTextTitleAttr;
		if (adapter != null){
			adapter.notifyDataSetChanged();
		}
	}
}
