package com.galaxyschool.app.wawaschool.views;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.galaxyschool.app.wawaschool.R;
import com.osastudio.common.utils.LQImageLoader;

/**
 * Created by pp on 15/12/16.
 */
public class OrientationSelectDialog extends Dialog implements
		View.OnClickListener {
	int mOrientation = 0;
	private String mImgPath;
	private ImageView mPortImageView;
	private ImageView mLandImageView;
	private CheckBox mLandCheckBox, mPortCheckBox;
	SelectHandler mSelectHandler = null;

	public OrientationSelectDialog(Context context, SelectHandler selectHandler) {
		super(context, R.style.Theme_PageDialog);
		mSelectHandler = selectHandler;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.orientation_select_view);
		View port = findViewById(R.id.port);
		port.setOnClickListener(this);
		View land = findViewById(R.id.land);
		land.setOnClickListener(this);
		findViewById(R.id.confirm_btn).setOnClickListener(this);
		mLandCheckBox = (CheckBox) findViewById(R.id.land_checkbox);
		mLandCheckBox.setOnClickListener(this);
		mPortCheckBox = (CheckBox) findViewById(R.id.port_checkbox);
		mPortCheckBox.setOnClickListener(this);
		mPortImageView = (ImageView) findViewById(R.id.port_image);
		mLandImageView = (ImageView) findViewById(R.id.land_image);
		if (mImgPath != null) {
			LQImageLoader.DIOptBuiderParam param = new LQImageLoader.DIOptBuiderParam();
			param.mIsCacheInMemory = true;
			param.mOutWidth = LQImageLoader.OUT_WIDTH;
			param.mOutHeight = LQImageLoader.OUT_HEIGHT;
			LQImageLoader.displayImage(mImgPath, mLandImageView, param);
			LQImageLoader.displayImage(mImgPath, mPortImageView, param);
		}
		setCancelable(false);
		setCanceledOnTouchOutside(false);
	}

	public void setImage(String imgPath) {
		mImgPath = imgPath;
	}
	
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.land || v.getId() == R.id.land_checkbox) {
			mOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
			mLandImageView.setImageResource(R.drawable.create_course_dialog_checked_l);
			mPortImageView.setImageResource(R.drawable.create_course_dialog_unchecked_p);
			mLandCheckBox.setChecked(true);
			mPortCheckBox.setChecked(false);
		} else if (v.getId() == R.id.port || v.getId() == R.id.port_checkbox) {
			mOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
			mLandImageView.setImageResource(R.drawable.create_course_dialog_unchecked_l);
			mPortImageView.setImageResource(R.drawable.create_course_dialog_checked_p);
			mLandCheckBox.setChecked(false);
			mPortCheckBox.setChecked(true);
		} else if (v.getId() == R.id.confirm_btn) {
			if (mOrientation >= 0 && mSelectHandler != null) {
				mSelectHandler.orientationSelect(mOrientation);
				OrientationSelectDialog.this.dismiss();
			}
		}
	}

	public interface SelectHandler {
		public void orientationSelect(int orientation);
	}
}
