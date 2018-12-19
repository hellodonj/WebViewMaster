package com.galaxyschool.app.wawaschool;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;

import com.galaxyschool.app.wawaschool.chat.DemoApplication;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.ActClassroomFragment;
import com.galaxyschool.app.wawaschool.fragment.AirClassroomFragment;
import com.galaxyschool.app.wawaschool.fragment.BaseFragment;
import com.galaxyschool.app.wawaschool.pojo.HomeworkChildListResult;
import com.galaxyschool.app.wawaschool.pojo.StudentMemberInfo;
import com.galaxyschool.app.wawaschool.views.ToolbarTopView;
import com.lqwawa.lqbaselib.net.library.RequestHelper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActClassroomActivity extends BaseFragmentActivity implements ActClassroomFragment.Constants {
    private Fragment fragment = null;
    private List<Fragment> tabFragments;
    private boolean fromMyPerformance;
    private List<StudentMemberInfo> childMemberData;
    private List<String> tabIndicators;

    public static void start(Activity activity, boolean fromMyPerformance,List<StudentMemberInfo> childMemberData) {
        if (activity == null) {
            return;
        }
        Intent intent = new Intent(activity, ActClassroomActivity.class);
        Bundle bundle = new Bundle();
        if (childMemberData != null){
            bundle.putSerializable("childMemberList", (Serializable) childMemberData);
        }
        bundle.putBoolean(ActClassroomFragment.Constants.EXTRA_FROM_MY_PERFORMANCE, fromMyPerformance);
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_classroom);
        Bundle args = getIntent().getExtras();
        if (args != null) {
            fromMyPerformance = args.getBoolean(ActClassroomFragment.Constants
                    .EXTRA_FROM_MY_PERFORMANCE, false);
            childMemberData = (List<StudentMemberInfo>) args.getSerializable("childMemberList");
        }
        if (fromMyPerformance) {
            //我的表演-校验是否有孩子
            loadMyPerformanceData();
        } else {
            //表演课堂
            enterActClassroomFragment(args);
        }
    }

    private void enterActClassroomFragment(Bundle args) {
        fragment = new ActClassroomFragment();
        fragment.setArguments(args);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.activity_body, fragment, ActClassroomFragment.TAG);
        ft.commit();
    }

    private void loadMyPerformanceData() {
        if (childMemberData == null || childMemberData.size() == 0) {
            //没有孩子
            enterActClassroomFragment(getIntent().getExtras());
        } else {
            //有孩子
            findViewById(R.id.activity_body).setVisibility(View.GONE);
            findViewById(R.id.ll_tab_layout).setVisibility(View.VISIBLE);
            ToolbarTopView toolbarTopView = (ToolbarTopView) findViewById(R.id.toolbar_top_view);
            if (toolbarTopView != null) {
                toolbarTopView.getBackView().setVisibility(View.VISIBLE);
                toolbarTopView.getTitleView().setText(getString(R.string.str_my_performance));
                toolbarTopView.getBackView().setOnClickListener((v) -> finish());
            }
            TabLayout mTabTl = (TabLayout) findViewById(R.id.tab_layout);
            ViewPager mContentVp = (ViewPager) findViewById(R.id.vp_content);
            ViewCompat.setElevation(mTabTl, 10);
            mTabTl.setupWithViewPager(mContentVp);
            tabIndicators = new ArrayList<>();
            tabFragments = new ArrayList<>();
            //家长自己
            tabIndicators.add(getString(R.string.str_my));
            ActClassroomFragment actClassroomFragment = new ActClassroomFragment();
            Bundle bundle = new Bundle();
            bundle.putBoolean(ActClassroomFragment.Constants.EXTRA_FROM_MY_PERFORMANCE, true);
            bundle.putString(ActClassroomFragment.Constants.EXTRA_MY_PERFORMANCE_USERID,
                    DemoApplication.getInstance().getMemberId());
            actClassroomFragment.setArguments(bundle);
            tabFragments.add(actClassroomFragment);
            //孩子的tab
            for (int i = 0; i < childMemberData.size(); i++) {
                String stuName = childMemberData.get(i).getRealName();
                if (TextUtils.isEmpty(stuName)) {
                    stuName = childMemberData.get(i).getNickName();
                }
                tabIndicators.add(stuName);
                actClassroomFragment = new ActClassroomFragment();
                bundle = new Bundle();
                bundle.putBoolean(ActClassroomFragment.Constants.EXTRA_FROM_MY_PERFORMANCE, true);
                bundle.putString(ActClassroomFragment.Constants.EXTRA_MY_PERFORMANCE_USERID,
                        childMemberData.get(i).getMemberId());
                bundle.putBoolean(ActClassroomFragment.Constants.EXTRA_MY_PERFORMMANCE_PARENT,true);
                actClassroomFragment.setArguments(bundle);
                tabFragments.add(actClassroomFragment);
            }
            ContentPagerAdapter contentAdapter = new ContentPagerAdapter(getSupportFragmentManager());
            mContentVp.setAdapter(contentAdapter);
        }
    }

    class ContentPagerAdapter extends FragmentPagerAdapter {

        public ContentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return tabFragments.get(position);
        }

        @Override
        public int getCount() {
            return tabIndicators.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabIndicators.get(position);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }
}
