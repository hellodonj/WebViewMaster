package com.galaxyschool.app.wawaschool.views;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.bitmapmanager.ThumbnailManager;
import com.galaxyschool.app.wawaschool.config.AppSettings;

import java.util.ArrayList;

public class SingleChoiceDialog extends Dialog {
	private Context mContext = null;
	private ConfirmCallback mConfirmCallback = null;
	private View mCancelBtn = null;
	private View mConfirmBtn = null;
	private ListView mListView = null;
	private String mTitle = null;
	private int mCurrentSelect = 0;
	private ArrayList<ChoiceItemData> mChoiceArray = null;
	private OnCancelListener mOnCancelListener = null;

	// private int mWidth = 0;
	// private int mHeight = 0;

	public static class ChoiceItemData {
		String id;
		String mLogoUrl;
		String mText;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getmLogoUrl() {
			return mLogoUrl;
		}

		public void setmLogoUrl(String mLogoUrl) {
			this.mLogoUrl = mLogoUrl;
		}

		public String getmText() {
			return mText;
		}

		public void setmText(String mText) {
			this.mText = mText;
		}

	}
	
	public SingleChoiceDialog(Context context, String title,
			ArrayList<ChoiceItemData> choiceArray, int selectPosition,
			ConfirmCallback callback) {
		this(context, title, choiceArray, selectPosition, callback, null);
	}

	public SingleChoiceDialog(Context context, String title,
			ArrayList<ChoiceItemData> choiceArray, int selectPosition,
			ConfirmCallback callback, OnCancelListener cancelListener) {
		super(context, R.style.Theme_PageDialogFullScreen);
		mContext = context;
		mTitle = title;
		mChoiceArray = choiceArray;
		mCurrentSelect = selectPosition;
		mConfirmCallback = callback;
		mOnCancelListener = cancelListener;
	}

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.singlechoice_view);
		setupViews();
		if (mOnCancelListener != null) {
			setOnCancelListener(mOnCancelListener);
		}

//		View baseLayout = findViewById(R.id.base_layout);
//		if (baseLayout != null) {
//			LayoutParams lp = baseLayout.getLayoutParams();
//			if (lp != null) {
//				lp.height = MyApplication.getWPixels();
//				lp.width = MyApplication.getHPixels();
//
//				// Rect frame = new Rect();
//				// 　　mContext.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
//
//				// lp.width = mWidth;
//				// lp.height = mHeight;
//				// baseLayout.setLayoutParams(lp);
//			}
//		}
		getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
	}

	private void setupViews() {
		mCancelBtn = findViewById(R.id.cancel_btn);
		mConfirmBtn = findViewById(R.id.confirm_btn);
		mListView = (ListView) findViewById(R.id.listview);
		TextView title = (TextView) findViewById(R.id.top_title);
		if (title != null) {
			title.setText(mTitle);
		}

		if (mListView != null && mChoiceArray != null
				&& mChoiceArray.size() > 0) {
			mListView.setAdapter(new ChoiceListAdapter());
			mListView.setItemsCanFocus(false);
			mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			mListView.setItemChecked(mCurrentSelect, true);
		}

		if (mListView != null) {
			mListView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					mCurrentSelect = arg2;
					mListView.setItemChecked(mCurrentSelect, true);
				}
			});
		}

		if (mCancelBtn != null) {
			mCancelBtn.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					SingleChoiceDialog.this.cancel();

				}
			});
		}

		if (mConfirmBtn != null) {
			mConfirmBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (mConfirmCallback != null) {
						int select = mListView.getCheckedItemPosition();
						if (select >= 0 && select < mChoiceArray.size()) {
							mConfirmCallback.onConfirm(SingleChoiceDialog.this,
									select, mChoiceArray.get(select));
						}
					}
					SingleChoiceDialog.this.dismiss();
				}
			});
		}
	}

	private class ChoiceListAdapter extends BaseAdapter {
		protected ThumbnailManager mThumbnailManager = null;
		protected LayoutInflater mInflater = null;

		public ChoiceListAdapter() {
			mThumbnailManager = MyApplication
					.getThumbnailManager((Activity) mContext);
			mInflater = LayoutInflater.from(mContext);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mChoiceArray.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return mChoiceArray.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.single_choice_item,
						parent, false);
			}

			int selectPosition = mListView.getCheckedItemPosition();

			ImageView logo = (ImageView) convertView.findViewById(R.id.logo);
			if (logo != null) {
				if (mChoiceArray.get(position).mLogoUrl == null) {
					logo.setVisibility(View.GONE);
				} else {
					logo.setVisibility(View.VISIBLE);
					mThumbnailManager.displayThumbnail(AppSettings
							.getFileUrl(mChoiceArray.get(position).mLogoUrl),
							logo);
				}
			}
			CheckedTextView text = (CheckedTextView) convertView
					.findViewById(R.id.text1);
			if (text != null) {
				text.setText(mChoiceArray.get(position).mText);
			}
			if (selectPosition == position) {
				text.setChecked(true);
			} else {
				text.setChecked(false);
			}
			return convertView;
		}

	}

	public interface ConfirmCallback {
		public void onConfirm(Dialog dialog, int selectPosition,
                              ChoiceItemData selectData);
	}
}
