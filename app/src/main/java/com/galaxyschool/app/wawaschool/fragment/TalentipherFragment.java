package com.galaxyschool.app.wawaschool.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.pojo.CheckMarkInfo;
import com.galaxyschool.app.wawaschool.views.MyViewPager;
import com.galaxyschool.app.wawaschool.views.PagerSlidingTabStrip;

import java.util.ArrayList;
import java.util.List;

import static com.galaxyschool.app.wawaschool.fragment.TalentipherFragment.Constatnts.EXTRA_DATA;


/**
 * ================================================
 * 作    者：Blizzard-liu
 * 版    本：1.0
 * 创建日期：2017/11/23 16:28
 * 描    述：天赋密码
 * 修订历史：
 * ================================================
 */

public class TalentipherFragment extends ContactsListFragment {

    public static final String TAG = TalentipherFragment.class.getSimpleName();


    public interface Constatnts {
        String EXTRA_DATA = "datalist";
        String EXTRA_MEMBERID = "memberID";
    }

    private PagerSlidingTabStrip pickerTabs;
    private MyViewPager viewPager;
    private MyPagerAdapter adapter;
    /**
     * 天赋密码数据集合
     */
    private ArrayList<CheckMarkInfo.ModelBean> talentList = new ArrayList<>();
    /**
     * 是否是家长
     */
    private boolean isParent;
    /**
     * 标题集合
     */
    private ArrayList<String> titleList = new ArrayList<>();


    public static TalentipherFragment newInstance(Bundle args) {

        Bundle bundle = new Bundle();
        bundle = args;

        TalentipherFragment fragment = new TalentipherFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_picker_classandgroup, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initData();
        initView();
    }

    private void initData() {

        ArrayList<CheckMarkInfo.ModelBean> list = getArguments().getParcelableArrayList(EXTRA_DATA);
        if (list != null) {
            talentList.clear();
            talentList.addAll(list);
        }
        isParent = getUserInfo().isParent();

//        if (getUserInfo().isStudent()) {
//            //孩子标题显示天赋密码
//            titleList.add(getString(R.string.str_talent_cipher));
//
//        } else {
            if (talentList.size() == 1) {
                CheckMarkInfo.ModelBean bean = talentList.get(0);
                if (bean.getChildId().equalsIgnoreCase(getMemeberId())) {
                    //自己
                    titleList.add(getString(R.string.str_talent_cipher));
                } else {
                    String realName = bean.getRealName();
                    if (TextUtils.isEmpty(realName)) {
                        realName = bean.getNickName();
                    }
                    titleList.add(realName);
                }


            } else {
                for (CheckMarkInfo.ModelBean bean : talentList) {
                    String realName = bean.getRealName();
                    if (TextUtils.isEmpty(realName)) {
                        realName = bean.getNickName();
                    }
                    titleList.add(realName);
                }
            }

//        }

    }

    private void initView() {
        pickerTabs = (PagerSlidingTabStrip) findViewById(R.id.picker_tabs);
        viewPager = (MyViewPager) findViewById(R.id.picker_viewpager);
        adapter = new MyPagerAdapter(getFragmentManager());
        viewPager.setAdapter(adapter);
        pickerTabs.setViewPager(viewPager);
        viewPager.setCurrentItem(0);
        viewPager.setCanScroll(true);
        viewPager.setOffscreenPageLimit(adapter.getCount() -1);
        if (titleList.size() == 1) {
            pickerTabs.setIndicatorHeight(0);
        }
        pickerTabs.setTextColorStateListResource(R.color.talent_tab_text);
        findViewById(R.id.header_left_btn).setOnClickListener(this);
        View tvShave = findViewById(R.id.tv_share);
        tvShave.setVisibility(View.VISIBLE);
        tvShave.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.header_left_btn) {
           finish();
        } else if (v.getId() == R.id.tv_share) {
            doShare();
        }
    }

    public void doShare() {
        TalentipherItemFragment fragment = (TalentipherItemFragment) adapter.getItem(viewPager.getCurrentItem());
        fragment.doShare();
    }

    private class MyPagerAdapter extends FragmentStatePagerAdapter {

        private List<Fragment> mFragmentList = new ArrayList<>(titleList.size());

        MyPagerAdapter(FragmentManager fm) {
            super(fm);
            initFrg(mFragmentList);

        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titleList.get(position);
        }

        @Override
        public int getCount() {
            return titleList.size();
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }
    }

    private void initFrg(List<Fragment> mFragmentList) {

        for (CheckMarkInfo.ModelBean bean : talentList) {
            String realName = bean.getRealName();
            if (TextUtils.isEmpty(realName)) {
                realName = bean.getNickName();
            }

            if (bean.getChildId().equalsIgnoreCase(getMemeberId())) {
                realName = getUserInfo().getRealName();
            }
            TalentipherItemFragment itemFragment = TalentipherItemFragment.newInstance(bean.getChildId(),realName);
            mFragmentList.add(itemFragment);
        }

    }

}
