package com.galaxyschool.app.wawaschool;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.fragment.ContactsExtendedPickerEntryFragment;
import com.galaxyschool.app.wawaschool.fragment.ContactsPickerEntryFragment;
import com.galaxyschool.app.wawaschool.slide.SlideManagerHornForPhone;

import java.util.ArrayList;
import java.util.List;

public class ContactsPickerActivity extends BaseFragmentActivity
        implements ContactsPickerEntryFragment.Constants,
        FragmentManager.OnBackStackChangedListener {
    Fragment fragment = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common);

        Bundle args = getIntent().getExtras();
        if (args.getBoolean(EXTRA_USE_EXTENDED_PICKER)) {
            fragment = new ContactsExtendedPickerEntryFragment();
        } else {
            fragment = new ContactsPickerEntryFragment();
        }
        fragment.setArguments(args);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.activity_body, fragment, ContactsPickerEntryFragment.TAG);
        ft.commit();
    }

    @Override
    public void onBackStackChanged() {
        processFragmentVisibility();
    }

    private void processFragmentVisibility() {
        List<Fragment> fragments = new ArrayList();
        for (Fragment f : getSupportFragmentManager().getFragments()) {
            if (f != null) {
                fragments.add(f);
            }
        }
        if (fragments.size() <= 1) {
            return;
        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//        fragments.get(fragments.size() - 1).setUserVisibleHint(true);
        ft.show(fragments.get(fragments.size() - 1));
        if (fragments.size() > 2) {
//            fragments.get(fragments.size() - 2).setUserVisibleHint(false);
            ft.hide(fragments.get(fragments.size() - 2));
        }
        ft.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ActivityUtils.REQUEST_CODE_RETURN_REFRESH){
            if (data != null ){
                Intent intent =new Intent();
                String filePath=data.getExtras().getString(SlideManagerHornForPhone.SAVE_PATH);
                intent.putExtra(SlideManagerHornForPhone.SAVE_PATH,filePath);
                this.setResult(Activity.RESULT_OK, intent);
                this.finish();
            }
        }
    }

}