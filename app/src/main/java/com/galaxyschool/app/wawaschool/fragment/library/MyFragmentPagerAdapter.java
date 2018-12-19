package com.galaxyschool.app.wawaschool.fragment.library;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.ViewGroup;

import java.util.List;

public class MyFragmentPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragments;
    private Fragment currFragment;

    public MyFragmentPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    public Fragment getCurrFragment() {
        return currFragment;
    }

    @Override
    public Fragment getItem(int i) {
        return fragments.get(i);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        Fragment fragment = (Fragment) object;
        if (fragment != this.currFragment) {
            if (this.currFragment != null) {
                currFragment.onPause();
            }

            if (fragment != null && fragment.isAdded()) {
                fragment.onResume();
            }

            this.currFragment = fragment;
        }
    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }
}
