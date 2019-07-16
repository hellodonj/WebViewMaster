package com.lqwawa.intleducation.module.user.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.common.utils.UIUtil;

import java.util.ArrayList;
import java.util.List;

public class OrderPagerAdapter extends FragmentPagerAdapter {

    private String[] mTabTitles = UIUtil.getStringArray(R.array.label_order_list_tabs);
    private List<Fragment> fragments = new ArrayList<>();

    public OrderPagerAdapter(FragmentManager fm, List<Fragment> fragmentList) {
        super(fm);
        this.fragments = fragmentList;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return mTabTitles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTabTitles[position];
    }
}
