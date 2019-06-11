package com.lqwawa.intleducation.module.user.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.module.user.ui.all.OrderAllFragment;
import com.lqwawa.intleducation.module.user.ui.completed.OrderCompletedFragment;
import com.lqwawa.intleducation.module.user.ui.expired.OrderExpiredFragment;
import com.lqwawa.intleducation.module.user.ui.pay.PendingPayFragment;
import com.lqwawa.intleducation.module.user.ui.review.PendingReviewFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述: 我的订单主界面
 * 作者|时间: djj on 2019/6/6 0006 下午 4:03
 */
public class OrderListActivity extends AppCompatActivity {

    private TopBar mTopBar;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    private List<Fragment> fragments = new ArrayList<>();
    private OrderPagerAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);
        //初始化控件
        initView();
        //初始化数据
        initData();
    }

    private void initView() {
        mTopBar = (TopBar) findViewById(R.id.top_bar);
        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);

        mTopBar.setBack(true);
        mTopBar.setTitle("我的订单");
    }

    private void initData() {
        fragments.add(new OrderAllFragment());
        fragments.add(new PendingPayFragment());
        fragments.add(new PendingReviewFragment());
        fragments.add(new OrderCompletedFragment());
        fragments.add(new OrderExpiredFragment());

        mAdapter = new OrderPagerAdapter(getSupportFragmentManager(), fragments);
        mViewPager.setAdapter(mAdapter);//给ViewPager设置适配器
        mTabLayout.setupWithViewPager(mViewPager);//将TabLayout与Viewpager联动起来
        mTabLayout.setTabsFromPagerAdapter(mAdapter);//给TabLayout设置适配器
    }

    public static void newInstance(Context context) {
        Intent starter = new Intent(context, OrderListActivity.class);
        context.startActivity(starter);
    }
}
