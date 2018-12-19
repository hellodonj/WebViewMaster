package com.galaxyschool.app.wawaschool;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.galaxyschool.app.wawaschool.fragment.resource.MyAttendedSchooListFragment;

/**
 * @author: wangchao
 * @date: 2017/06/15 11:06
 */

public class MyAttendedSchoolListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment(Bundle args) {
        MyAttendedSchooListFragment fragment = new MyAttendedSchooListFragment();
        if (args == null) {
            args = new Bundle();
        }
        args.putString(SingleFragmentActivity.EXTRA_FRAGMENT_TAG,
                MyAttendedSchooListFragment.TAG);
        args.putBoolean(SingleFragmentActivity.EXTRA_IS_ADD_TO_BACKSTACK, true);
        fragment.setArguments(args);
        return fragment;
    }
}
