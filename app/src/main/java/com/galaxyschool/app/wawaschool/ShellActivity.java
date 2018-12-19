package com.galaxyschool.app.wawaschool;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import com.galaxyschool.app.wawaschool.fragment.*;
import com.galaxyschool.app.wawaschool.fragment.account.AboutFragment;
import com.galaxyschool.app.wawaschool.fragment.account.FeedbackFragment;
import com.galaxyschool.app.wawaschool.fragment.resource.ResourceTypeListFragment;

/**
 * @author 作者 shouyi:
 * @version 创建时间：Jul 30, 2015 08:25:32 PM 类说明
 */
public class ShellActivity extends BaseFragmentActivity {

	public static final String EXTRA_ORIENTAION = "orientation";


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		if(bundle != null) {
			int orientation = bundle.getInt(EXTRA_ORIENTAION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			if(orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			} else {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			}
		}

		setContentView(R.layout.activity_cs_shell);
		initViews();
	}

	private void initViews() {
		String window = getIntent().getStringExtra("Window");
		FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();
		Fragment fragment = null;
		if ("about".equals(window)) {
			fragment = new AboutFragment();
		} else if ("feedback".equals(window)) {
			fragment = new FeedbackFragment();
		} else if ("createClass".equals(window)) {
			fragment = new ContactsCreateClassFragment();
			Bundle b = new Bundle();
			b.putString("SchoolId", getIntent().getStringExtra("SchoolId"));
			fragment.setArguments(b);
		}  else if ("noteComment".equals(window)) {
            fragment = new WawatvCommentFragment();
            Bundle args = getIntent().getExtras();
            fragment.setArguments(args);
        } else if("localPostBar".equals(window)) {
			Bundle args = getIntent().getExtras();
			fragment = new WawaNoteFragment();
			fragment.setArguments(args);
		} else if("resourceTypeList".equals(window)) {
			fragment = new ResourceTypeListFragment();
		} else if("topicSpaceCourseList".equals(window)) {
			Bundle args = getIntent().getExtras();
			fragment = new WawatvCommentFragment();
			fragment.setArguments(args);
		} else if("media_type_list".equals(window)) {
			Bundle args = getIntent().getExtras();
			fragment = new MediaTypeListFragment();
			fragment.setArguments(args);
		} else if("media_list".equals(window)) {
			Bundle args = getIntent().getExtras();
			fragment = new MediaMainFragment();
			fragment.setArguments(args);
		}
		if (fragment != null) {
			transaction.add(R.id.container, fragment);
			transaction.commit();
		}
	}

}
