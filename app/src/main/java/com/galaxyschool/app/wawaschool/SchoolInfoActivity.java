package com.galaxyschool.app.wawaschool;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import com.galaxyschool.app.wawaschool.fragment.SchoolInfoFragment;

public class SchoolInfoActivity extends BaseFragmentActivity
		implements SchoolInfoFragment.Constants {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_common);

		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		Fragment fragment = new SchoolInfoFragment();
		fragment.setArguments(getIntent().getExtras());
		ft.replace(R.id.activity_body, fragment, SchoolInfoFragment.TAG);
		ft.commit();
	}

}
