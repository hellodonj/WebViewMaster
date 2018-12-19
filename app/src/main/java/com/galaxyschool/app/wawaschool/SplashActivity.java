package com.galaxyschool.app.wawaschool;

import java.util.Timer;
import java.util.TimerTask;
import android.content.Intent;
import android.os.Bundle;
import com.galaxyschool.app.wawaschool.common.SharedPreferencesHelper;
import com.galaxyschool.app.wawaschool.guide.GuideActivity;

/**
 * @author 作者 shouyi
 * @version 创建时间：Oct 8, 2015 10:56:16 AM 类说明
 */
public class SplashActivity extends BaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		processSkip();
	}

	private void processSkip() {
		new Timer().schedule(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (isFirstOpen()) {
					gotoGuide();
					setIsFirstOpen(false);
				} else {
					gotoHome();
				}
				finish();
			}
		}, 1500);
	}

	private boolean isFirstOpen() {
		return SharedPreferencesHelper.getBoolean(this, "isFirstOpen", true);
	}

	private void setIsFirstOpen(boolean value) {
		SharedPreferencesHelper.setBoolean(this, "isFirstOpen", false);
	}

	private void gotoGuide() {
		Intent intent = new Intent(this, GuideActivity.class);
		startActivity(intent);
	}

	private void gotoHome() {
		Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);
		overridePendingTransition(android.R.anim.fade_in,
				android.R.anim.fade_out);
	}
}
