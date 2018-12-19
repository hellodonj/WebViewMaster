package com.galaxyschool.app.wawaschool.views;
/**
 * @author 作者 shouyi:
 * @version 创建时间：Jul 30, 2015 02:53:32 PM 类说明
 */

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import com.galaxyschool.app.wawaschool.R;

import java.util.List;

public class PopupFullScreenMenu extends PopupWindow {
	Activity mContext;
	View mRootView;
	List<PopupMenuData> mItemsDatas;
	int mWidth = 0;
	float mOffset = 0;
	OnItemClickListener mItemClickListener;
	List<Integer> mGroupSeperatePos;

	public PopupFullScreenMenu(final Activity context,
			OnItemClickListener itemClickListener, List<PopupMenuData> items) {
		mContext = context;
		mItemClickListener = itemClickListener;
		mItemsDatas = items;
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mRootView = inflater.inflate(R.layout.popup_full_screen_window, null);
		initView();
		setProperty();
	}

	public void initView() {
		ListView listView = (ListView) mRootView.findViewById(R.id.listview);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if (mItemClickListener != null) {
					mItemClickListener.onItemClick(parent, view, position, mItemsDatas.get(position).mId);
				}
				dismiss();
			}
		});
		mRootView.findViewById(R.id.popup_cancel).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dismiss();
			}
		});
		TheAdapter adapter = new TheAdapter();
		listView.setAdapter(adapter);
	}

	private void setProperty() {
		int h = mContext.getWindowManager().getDefaultDisplay().getHeight();
		int w = mContext.getWindowManager().getDefaultDisplay().getWidth();
		mWidth = w;
		this.setContentView(mRootView);
		this.setAnimationStyle(R.style.AnimBottom);
		this.setWidth((int) (LayoutParams.MATCH_PARENT));
		this.setHeight(LayoutParams.MATCH_PARENT);
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
			// TODO Auto-generated method stub
			return mItemsDatas == null ? 0 : mItemsDatas.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View view = LayoutInflater.from(mContext).inflate(
					R.layout.icon_blue_text_item, null);
			ImageView imageView = (ImageView) view.findViewById(R.id.icon);
			TextView textView = (TextView) view.findViewById(R.id.text);

			textView.setText(mItemsDatas.get(position).mTextId);
			if (mItemsDatas.get(position).mIcon == 0) {
				imageView.setVisibility(View.GONE);
				textView.setGravity(Gravity.CENTER);
			} else {
				imageView.setImageResource(mItemsDatas.get(position).mIcon);
			}
			return view;
		}
	}

	public static class PopupMenuData {
		private int mIcon;
		private int mTextId;
		private int mId;

		public PopupMenuData(int icon, int textId) {
			mIcon = icon;
			mTextId = textId;
		}
		
		public PopupMenuData(int icon, int textId, int id) {
			mIcon = icon;
			mTextId = textId;
			mId = id;
		}
	}
}
