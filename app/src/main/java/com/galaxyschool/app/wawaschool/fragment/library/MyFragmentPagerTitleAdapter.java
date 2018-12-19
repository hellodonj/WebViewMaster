package com.galaxyschool.app.wawaschool.fragment.library;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.ViewGroup;

import java.util.List;

/**
 * 带标题选择
 */
public class MyFragmentPagerTitleAdapter extends FragmentPagerAdapter {

    private List<String> titleList; // 标题数组
    private List<Fragment> fragmentList;// fragment集合
    private Fragment currFragment; // 当前fragment

    public MyFragmentPagerTitleAdapter(FragmentManager fm, List<String> titleList,
                                       List<Fragment> fragments) {
        super(fm);
        this.titleList = titleList;
        this.fragmentList = fragments;
    }

    public Fragment getCurrFragment() {
        return currFragment;
    }

    @Override
    public Fragment getItem(int i) {
        return fragmentList.get(i);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
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

    @Override
    public CharSequence getPageTitle(int position) {
        return getTitle(position);
    }

    private String getTitle(int position){
        if (titleList != null && titleList.size() > 0){
            return titleList.get(position);
        }
        return "";
    }
}
