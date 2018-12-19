package com.galaxyschool.app.wawaschool.fragment.account;

import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.fragment.BaseFragment;
import com.galaxyschool.app.wawaschool.views.MyViewPager;
import com.galaxyschool.app.wawaschool.views.ToolbarTopView;

import java.util.ArrayList;
import java.util.List;

public class ForgotPasswordFragment extends BaseFragment implements
		View.OnClickListener {

	public static final String TAG = ForgotPasswordFragment.class
			.getSimpleName();

	private ToolbarTopView toolbarTopView;
	private TextView mPhoneTab, mMailTab;
	private MyViewPager mViewPager;
	List<Fragment> mFragments;
	FragmentPagerAdapter fragmentAdapter;
	ImageView cursor;
	int offset, bmpW = 10;
	int currIndex = 0;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_forgot_password, container,
				false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initViews();
		if(getActivity() != null) {
			getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		}
	}

	private void initViews() {
		View rootView = getView();
		toolbarTopView = (ToolbarTopView) rootView
				.findViewById(R.id.toolbartopview);
		toolbarTopView.getBackView().setOnClickListener(this);
		toolbarTopView.getTitleView().setText(R.string.forgot_password);
		mPhoneTab = (TextView) rootView.findViewById(R.id.phone_tab);
		mMailTab = (TextView) rootView.findViewById(R.id.mail_tab);
		mPhoneTab.setOnClickListener(this);
		mMailTab.setOnClickListener(this);
		InitImageView();
		initFragment();
	}

	private void initFragment() {
		mViewPager = (MyViewPager) getView().findViewById(R.id.viewpager);
		this.mFragments = new ArrayList<Fragment>();
		this.mFragments.add(new RetrievePWByPhoneFragment());
		this.mFragments.add(new RetrievePWByMailFragment());
		this.fragmentAdapter = new FragmentPagerAdapter(
				getChildFragmentManager()) {
			@Override
			public Fragment getItem(int i) {
				return mFragments.get(i);
			}

			@Override
			public int getCount() {
				return mFragments.size();
			}
		};
		mViewPager.setAdapter(this.fragmentAdapter);
		mViewPager.setOnPageChangeListener(new MyOnPageChangeListener());
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.toolbar_top_back_btn:
			FragmentManager fragmentManager = getFragmentManager();
			if (fragmentManager != null) {
				fragmentManager.popBackStack();
			}
			break;
		case R.id.phone_tab:
			mViewPager.setCurrentItem(0);
			changeTabColor(0);
			break;
		case R.id.mail_tab:
			mViewPager.setCurrentItem(1);
			changeTabColor(1);
			break;
		}
	}

	private void changeTabColor(int index) {
		if (index == 0) {
			mPhoneTab.setTextColor(getResources().getColor(R.color.text_green));
			mMailTab.setTextColor(getResources().getColor(R.color.text_black));
		} else if (index == 1) {
			mPhoneTab.setTextColor(getResources().getColor(R.color.text_black));
			mMailTab.setTextColor(getResources().getColor(R.color.text_green));
		}
	}

	private void InitImageView() {
		cursor = (ImageView) findViewById(R.id.cursor);
		DisplayMetrics dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels ;
		bmpW = screenW / 2;
		offset = 0;
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset, 0);
		cursor.setImageMatrix(matrix);
	}

	public class MyOnPageChangeListener implements OnPageChangeListener {

		int one = offset * 2 + bmpW;
		int two = one * 2;

		@Override
		public void onPageSelected(int arg0) {
			Animation animation = null;
			switch (arg0) {
			case 0:
				if (currIndex == 1) {
					animation = new TranslateAnimation(one, 0, 0, 0);
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, 0, 0, 0);
				}
				break;
			case 1:
				if (currIndex == 0) {
					animation = new TranslateAnimation(offset, one, 0, 0);
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, one, 0, 0);
				}
				break;
			}
			currIndex = arg0;
			animation.setFillAfter(true);
			animation.setDuration(300);
			cursor.startAnimation(animation);
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	}
}
