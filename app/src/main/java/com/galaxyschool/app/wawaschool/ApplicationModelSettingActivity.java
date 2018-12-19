package com.galaxyschool.app.wawaschool;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.galaxyschool.app.wawaschool.fragment.ApplicationModelSettingFragment;

public class ApplicationModelSettingActivity extends CommonFragmentActivity {

    public static void start(Object object, int requestCode) {
        MethodObject methodObject = new MethodObject(object).invoke();
        if (!methodObject.isValid()) {
            return;
        }
        Activity activity = methodObject.getActivity();
        Fragment tempFragment = methodObject.getFragment();
        Intent starter = new Intent(activity, ApplicationModelSettingActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRA_CLASS_OBJECT, ApplicationModelSettingFragment.class);
        starter.putExtras(bundle);
        start(activity, tempFragment, starter, requestCode);
    }

    @Override
    public void onBackPressed() {
        if (fragment != null && fragment instanceof ApplicationModelSettingFragment) {
            ((ApplicationModelSettingFragment) fragment).finishCurrentView();
        }
    }

}