package com.galaxyschool.app.wawaschool.guide;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout.LayoutParams;

import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.ScreenUtils;

public class GuideActivity extends Activity {
	private GuideGallery mGallery;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_guide);

		findViews();
	}

	private void findViews() {
		mGallery = (GuideGallery) findViewById(R.id.help_gallery);
		mGallery.setAdapter(new ImageAdapter(this));
		LayoutParams lpG = (LayoutParams) mGallery.getLayoutParams();
		lpG.width = ScreenUtils.getScreenWidth(GuideActivity.this);
		lpG.height = ScreenUtils.getScreenHeight(GuideActivity.this);
		mGallery.setLayoutParams(lpG);
		mGallery.setUnselectedAlpha(1.0f);
	}

}