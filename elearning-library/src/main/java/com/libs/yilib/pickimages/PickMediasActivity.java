package com.libs.yilib.pickimages;

import java.util.ArrayList;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.lqwawa.apps.R;
import com.osastudio.apps.BaseFragmentActivity;

/**
 * @author 作者 shouyi
 * @version 创建时间：Dec 2, 2015 4:23:21 PM 类说明
 */
public class PickMediasActivity extends com.osastudio.apps.BaseFragmentActivity implements
		PickMediaResultListener {
	public final static String PICKMEDIA_ORIENTATION = "pick_orientation";
	PickMediasFragment mFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		int orientation = getIntent().getIntExtra(PICKMEDIA_ORIENTATION,
				Configuration.ORIENTATION_PORTRAIT);
		if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		} else {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		setContentView(R.layout.activity_shell);
		initViews();
	}

	private void initViews() {
		FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();
		mFragment = new PickMediasFragment();
		mFragment.setPickImageResultListener(this);
		Bundle bundle = new Bundle();
		bundle.putSerializable(PickMediasFragment.PICK_IMG_PARAM, getIntent()
				.getSerializableExtra(PickMediasFragment.PICK_IMG_PARAM));
		mFragment.setArguments(bundle);
		transaction.add(R.id.container, mFragment);
		//transaction.addToBackStack(null);
		transaction.commit();
	}

	@Override
	public void onPickFinished(ArrayList<MediaInfo> imageInfos) {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		intent.putParcelableArrayListExtra(PickMediasFragment.PICK_IMG_RESULT,
				imageInfos);
		setResult(RESULT_OK, intent);
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		mFragment.goback();
	}
}
