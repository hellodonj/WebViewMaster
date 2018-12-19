package com.galaxyschool.app.wawaschool;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.galaxyschool.app.wawaschool.fragment.account.CustomerServiceFragment;

public class CustomerServiceActivity extends CommonFragmentActivity
        implements CustomerServiceFragment.Constatnts{

    public static void start(Object object) {
        start(object, SOURCE_TYPE_CUSTOMER_SERVICE);
    }
    
    public static void start(Object object, int sourceType) {
        MethodObject methodObject = new MethodObject(object).invoke();
        if (!methodObject.isValid()) {
            return;
        }
        Activity activity = methodObject.getActivity();
        Fragment tempFragment = methodObject.getFragment();
        Intent starter = new Intent(activity, CustomerServiceActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRA_CLASS_OBJECT, CustomerServiceFragment.class);
        bundle.putInt(SOURCE_TYPE, sourceType);
        starter.putExtras(bundle);
        start(activity, tempFragment, starter, 0);
    }
}
