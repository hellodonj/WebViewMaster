package com.galaxyschool.app.wawaschool;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.galaxyschool.app.wawaschool.fragment.ClassContactsDetailsFragment;
import com.galaxyschool.app.wawaschool.fragment.GroupContactsListFragment;
import com.galaxyschool.app.wawaschool.fragment.SchoolContactsDetailsFragment;
import com.galaxyschool.app.wawaschool.slide.CreateSlideHelper;

public class ContactsActivity extends BaseFragmentActivity
		implements GroupContactsListFragment.Constants {
	private Fragment fragment = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contacts);

		Bundle args = getIntent().getExtras();
		int type = args.getInt(EXTRA_CONTACTS_TYPE);

		String tag = null;
		if (type == CONTACTS_TYPE_CLASS) {
			fragment = new ClassContactsDetailsFragment();
			tag = ClassContactsDetailsFragment.TAG;
		} else if (type == CONTACTS_TYPE_SCHOOL) {
			fragment = new SchoolContactsDetailsFragment();
			tag = SchoolContactsDetailsFragment.TAG;
		}
		if (fragment != null) {
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			fragment.setArguments(args);
			ft.replace(R.id.contacts_layout, fragment, tag);
			ft.commit();
		}
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
		if (fragment != null) {
			fragment.onActivityResult(arg0, arg1, arg2);
		}
		CreateSlideHelper.processActivityResule(this, null, arg0, arg1, arg2);
	}

}