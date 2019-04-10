package com.lqwawa.intleducation.module.discovery.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.osastudio.apps.BaseFragmentActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * ================================================
 * author：xu_wenliang
 * time：2018/4/8 10:59
 * desp: 描 述：我的余额界面
 * ================================================
 */

public class UserCoinActivity extends BaseFragmentActivity implements View.OnClickListener {
    // 转赠请求码
    private static final int KEY_BALANCE_REQUEST_CODE = 1 << 0;

    private String memberId;
    private ImageView ivClose;
    private TextView tvDetail;

    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_coin);

        memberId = getIntent().getStringExtra("memberId");

        initView();

    }

    private void initView() {

        ivClose = (ImageView) findViewById(R.id.header_left_btn);
        ivClose.setOnClickListener(this);

        tvDetail = (TextView) findViewById(R.id.header_right_btn);
        tvDetail.setOnClickListener(this);

        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new UserCoinFragment());
        fragments.add(new UserVoucherFragment());
        TabPagerAdapter tabPagerAdapter = new TabPagerAdapter(getSupportFragmentManager(), fragments,
                UIUtil.getStringArray(R.array.label_user_coin_tabs));
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setAdapter(tabPagerAdapter);
        viewPager.setOffscreenPageLimit(fragments.size());


        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        Intent intent;
        if (i == R.id.header_left_btn) {

            finish();
        } else if (i == R.id.header_right_btn) {
            //明细
            if (viewPager.getCurrentItem() == 0) {
                intent = new Intent(this, CoinsDetailActivity.class);
            } else {
                intent = new Intent(this, VoucherDetailActivity.class);
            }
            startActivity(intent);
        }
    }

    private class TabPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragments;
        private String[] tabTitles;

        public TabPagerAdapter(FragmentManager fm, List<Fragment> fragments, String[] tabTitles) {
            super(fm);
            this.fragments = fragments;
            this.tabTitles = tabTitles;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }
    }
}
