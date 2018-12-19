package com.galaxyschool.app.wawaschool;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

/**
 * @author: wangchao
 * @date: 2017/06/15 11:08
 */

public abstract class SingleFragmentActivity extends BaseFragmentActivity {

    public static final String EXTRA_FRAGMENT_TAG = "fragment_tag";
    public static final String EXTRA_IS_ADD_TO_BACKSTACK = "is_add_to_backstack";

    protected abstract Fragment createFragment(Bundle args);

    private Fragment fragment;
    private boolean isAddToBackStack;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common);
        Bundle args = getIntent().getExtras();
        fragment = createFragment(args);
        fragment.setArguments(args);
        String fragmentTag = null;
        if (args != null && args.containsKey(EXTRA_FRAGMENT_TAG)) {
            fragmentTag = args.getString(EXTRA_FRAGMENT_TAG);
        }
        if (args != null && args.containsKey(EXTRA_IS_ADD_TO_BACKSTACK)) {
            isAddToBackStack = args.getBoolean(EXTRA_IS_ADD_TO_BACKSTACK);
        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.activity_body, fragment, fragmentTag);
        if (isAddToBackStack) {
            ft.addToBackStack(null);
        }
        ft.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }


    @Override
    public void onBackPressed() {
        if (isAddToBackStack && getSupportFragmentManager().getBackStackEntryCount() == 1) {
            finish();
        }
        super.onBackPressed();
    }



}
