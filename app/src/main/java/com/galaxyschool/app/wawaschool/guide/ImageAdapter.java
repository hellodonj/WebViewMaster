package com.galaxyschool.app.wawaschool.guide;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery.LayoutParams;
import android.widget.ImageView;

import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;

import java.lang.ref.SoftReference;
import java.util.HashMap;

public class ImageAdapter extends BaseAdapter {
	private Context mContext;
	private int[] mImage = {
			R.drawable.guide1,
//			R.drawable.guide_happy_study,
			R.drawable.guide2_online_study,
			R.drawable.guide3
	};
	private HashMap<String, SoftReference<Bitmap>> cache;
	private View mEnterAppBtn;

	public ImageAdapter(Context c) {
		mContext = c;
		cache = new HashMap<>();
	}

	@Override
	public int getCount() {
		return mImage.length;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View itemView;
		if (convertView == null) {
			itemView = LayoutInflater.from(mContext).inflate(
					R.layout.guide_image, null);
		} else {
			itemView = convertView;
		}
		
		LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		itemView.setLayoutParams(layoutParams);

		ImageView i = (ImageView) itemView.findViewById(R.id.guide_image);
		i.setImageBitmap(getBitmap(position));
		i.setScaleType(ImageView.ScaleType.FIT_CENTER);

		mEnterAppBtn = itemView.findViewById(R.id.guide_enter_button);
		if (position == mImage.length - 1) {
			mEnterAppBtn.setVisibility(View.VISIBLE);
			mEnterAppBtn.setOnTouchListener(new OnTouchListener() {
				
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					// TODO Auto-generated method stub
					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						if (mEnterAppBtn != null) {
							mEnterAppBtn.setBackgroundResource(R.drawable.start_pre_ico);
						}
					}
					if (event.getAction() == MotionEvent.ACTION_UP) {
						if (mEnterAppBtn != null) {
							mEnterAppBtn.setBackgroundResource(R.drawable.start_ico);
						}
						ActivityUtils.gotoHome(mContext);
						((Activity) mContext).finish();
					}
					return true;
				}
			});
		} else {
			mEnterAppBtn.setVisibility(View.GONE);
		}

		return itemView;
	}

	private Bitmap getBitmap(int position) {
		Bitmap bitmap = null;
		if (this.cache.containsKey(String.valueOf(position))) {
			bitmap = this.cache.get(String.valueOf(position)).get();
		}
		if (bitmap == null) {
			Options opts = new Options();
			opts.inSampleSize = 1;
			opts.inPreferredConfig = Bitmap.Config.RGB_565;
			opts.outHeight = 1000;
			opts.outWidth = 1000;

			Bitmap tempBmpBitmap = BitmapFactory.decodeResource(
					mContext.getResources(), mImage[position], opts);

			this.cache.put(String.valueOf(position), new SoftReference<Bitmap>(
					tempBmpBitmap));
			bitmap = this.cache.get(String.valueOf(position)).get();
		}

		return bitmap;
	}
}
