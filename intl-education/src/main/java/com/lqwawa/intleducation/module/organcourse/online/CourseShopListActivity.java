package com.lqwawa.intleducation.module.organcourse.online;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.CourseEmptyView;
import com.lqwawa.intleducation.base.PresenterActivity;
import com.lqwawa.intleducation.base.ToolbarActivity;
import com.lqwawa.intleducation.base.widgets.PriceArrowView;
import com.lqwawa.intleducation.base.widgets.PullRefreshView.PullToRefreshView;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.base.widgets.adapter.TabSelectedAdapter;
import com.lqwawa.intleducation.base.widgets.adapter.TextWatcherAdapter;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.KeyboardUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.module.discovery.adapter.CourseListAdapter;
import com.lqwawa.intleducation.module.discovery.ui.CourseDetailsActivity;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.filtrate.HideSortType;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.filtrate.courselist.LQCourseListActivity;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.search.SearchActivity;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;
import com.lqwawa.intleducation.module.organcourse.online.pager.CourseShopPagerFragment;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @desc 在线课堂的学程馆
 * @author medici
 */
public class CourseShopListActivity extends ToolbarActivity implements View.OnClickListener{

    private static final String KEY_EXTRA_TITLE_TEXT = "KEY_EXTRA_TITLE_TEXT";

    private static final String KEY_EXTRA_SORT_TYPE = "KEY_EXTRA_SORT_TYPE";

    private static final String KEY_EXTRA_SCHOOL_ID = "KEY_EXTRA_SCHOOL_ID";

    private static final String KEY_EXTRA_IS_SCHOOL_ENTER = "KEY_EXTRA_IS_SCHOOL_ENTER";
    // 是不是从在线课堂班级进入的
    private static final String KEY_EXTRA_IS_ONLINE_CLASS_ENTER = "KEY_EXTRA_IS_ONLINE_CLASS_ENTER";

    // 头部布局
    private TopBar mTopBar;
    private EditText mSearchEt;
    private ImageView mSearchClear;
    private TextView mSearchFilter;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    private String mTitle;
    private String mSortType;
    private String mSchoolId;
    private boolean isSchoolEnter;
    private boolean isOnlineClassEnter;

    private String[] mTabTitles;

    private List<SearchNavigator> mNavigatorList;

    private PriceArrowView mPriceArrowView;
    // 价格tab是否显示
    private boolean priceTabVisiale;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_course_shop_list;
    }

    @Override
    protected boolean initArgs(Bundle bundle) {
        mTitle = (String) bundle.get(KEY_EXTRA_TITLE_TEXT);
        mSortType = (String) bundle.get(KEY_EXTRA_SORT_TYPE);
        mSchoolId = bundle.getString(KEY_EXTRA_SCHOOL_ID);
        isSchoolEnter = bundle.getBoolean(KEY_EXTRA_IS_SCHOOL_ENTER);
        isOnlineClassEnter = bundle.getBoolean(KEY_EXTRA_IS_ONLINE_CLASS_ENTER);
        if(EmptyUtil.isEmpty(mTitle) || EmptyUtil.isEmpty(mSortType)){
            return false;
        }
        return super.initArgs(bundle);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mTopBar = (TopBar) findViewById(R.id.top_bar);
        mTopBar.setBack(true);
        mTopBar.setTitle(mTitle);

        mSearchEt = (EditText)findViewById(R.id.search_et);
        mSearchFilter = (TextView) findViewById(R.id.filter_tv);
        mSearchClear = (ImageView) findViewById(R.id.search_clear_iv);
        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);

        mSearchEt.setImeOptions(EditorInfo.IME_ACTION_NONE);
        mSearchEt.setHint(R.string.search);
        mSearchFilter.setText(getString(R.string.search));
        mSearchFilter.setOnClickListener(this);
        mSearchClear.setOnClickListener(this);


        mSearchEt.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                super.onTextChanged(s, start, before, count);
                if (s.length() > 0) {
                    mSearchClear.setVisibility(View.VISIBLE);
                } else {
                    mSearchClear.setVisibility(View.INVISIBLE);
                }

                mSearchEt.setImeOptions(s.length() > 0
                        ? EditorInfo.IME_ACTION_SEARCH
                        : EditorInfo.IME_ACTION_NONE);
                mSearchEt.setSingleLine();
                mSearchEt.setInputType(EditorInfo.TYPE_CLASS_TEXT
                        | EditorInfo.TYPE_TEXT_FLAG_AUTO_COMPLETE
                        | EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
            }
        });

        mSearchEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String searchKey = mSearchEt.getText().toString();
                    if (EmptyUtil.isEmpty(searchKey)){
                        return false;
                    }

                    for (SearchNavigator navigator:mNavigatorList) {
                        boolean isVisible = navigator.search(searchKey);
                        if(isVisible) break;
                    }
                    KeyboardUtil.hideSoftInput(CourseShopListActivity.this);
                    return true;
                }
                return false;
            }
        });

        mTabLayout.addOnTabSelectedListener(new TabSelectedAdapter(){
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                super.onTabSelected(tab);
                if(tab.getPosition() == mTabLayout.getTabCount() -1){
                    priceTabVisiale = true;
                    // 价格被选中
                    if(EmptyUtil.isNotEmpty(mPriceArrowView)){
                        int state = mPriceArrowView.triggerSwitch();
                        triggerPriceSwitch(state);
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                super.onTabUnselected(tab);
                if(tab.getPosition() == mTabLayout.getTabCount() -1){
                    priceTabVisiale = false;
                    // 价格被选中
                    if(EmptyUtil.isNotEmpty(mPriceArrowView)){
                        // 状态重置
                        mPriceArrowView.reset();
                    }
                }
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        mTabTitles = UIUtil.getStringArray(R.array.label_course_shop_tabs);
        CourseShopPagerFragment recentUpdateFragment = CourseShopPagerFragment.newInstance(HideSortType.TYPE_SORT_ONLINE_SHOP_RECENT_UPDATE,mSchoolId,isSchoolEnter,isOnlineClassEnter);
        CourseShopPagerFragment hotFragment = CourseShopPagerFragment.newInstance(HideSortType.TYPE_SORT_HOT_RECOMMEND,mSchoolId,isSchoolEnter,isOnlineClassEnter);
        CourseShopPagerFragment priceFragment = CourseShopPagerFragment.newInstance(HideSortType.TYPE_SORT_ONLINE_SHOP_PRICE_DOWN,mSchoolId,isSchoolEnter,isOnlineClassEnter);

        List<Fragment> fragments = new ArrayList<>();
        fragments.add(recentUpdateFragment);
        fragments.add(hotFragment);
        fragments.add(priceFragment);

        mNavigatorList = new ArrayList<>();
        mNavigatorList.add(recentUpdateFragment);
        mNavigatorList.add(hotFragment);
        mNavigatorList.add(priceFragment);

        TabPagerAdapter mAdapter = new TabPagerAdapter(getSupportFragmentManager(),fragments);
        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

        // 设置TabLayout最后一个节点有upDown
        TabLayout.Tab tabAt = mTabLayout.getTabAt(mTabLayout.getTabCount() - 1);
        PriceArrowView view = new PriceArrowView(this);
        view.setTabTitle(mTabTitles[mTabLayout.getTabCount() - 1]);
        tabAt.setCustomView(view.getRootView());
        mPriceArrowView = view;

        mPriceArrowView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(priceTabVisiale && event.getAction() == MotionEvent.ACTION_DOWN){
                    int state = mPriceArrowView.triggerSwitch();
                    triggerPriceSwitch(state);
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * 发生价格排序变化
     * @param state 状态  1 UP 2 Down
     */
    private void triggerPriceSwitch(int state){
        if(state == PriceArrowView.STATE_UP){
            // 升序
            for (SearchNavigator navigator:mNavigatorList) {
                boolean isVisible = navigator.triggerPriceSwitch(true);
                if(isVisible) break;
            }
        }else if(state == PriceArrowView.STATE_DOWN){
            // 降序
            for (SearchNavigator navigator:mNavigatorList) {
                boolean isVisible = navigator.triggerPriceSwitch(false);
                if(isVisible) break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if(viewId == R.id.filter_tv){
            String searchKey = mSearchEt.getText().toString();

            if (EmptyUtil.isEmpty(searchKey)){
                return;
            }

            for (SearchNavigator navigator:mNavigatorList) {
                boolean isVisible = navigator.search(searchKey);
                if(isVisible) break;
            }
        }else if(viewId == R.id.search_clear_iv) {
            mSearchEt.getText().clear();
        }
    }

    private class TabPagerAdapter extends FragmentPagerAdapter{

        private List<Fragment> fragments;

        public TabPagerAdapter(FragmentManager fm,List<Fragment> fragments) {
            super(fm);
            this.fragments = fragments;
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
            return mTabTitles[position];
        }
    }

    /**
     * 课程列表显示入口
     * @param context 上下文对象
     * @param sort 热门列表或者其它
     * @param title 标题文本
     */
    public static void show(@NonNull Context context, @NonNull @HideSortType.SortRes String sort, @NonNull String title){
        Intent intent = new Intent(context, LQCourseListActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(KEY_EXTRA_SORT_TYPE, sort);
        bundle.putString(KEY_EXTRA_TITLE_TEXT, title);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    /**
     * 课程列表显示入口 在线课堂机构信息进来的
     * @param context 上下文对象
     * @param sort 热门列表或者其它
     * @param title 标题文本
     * @param schoolId 机构Id
     * @param isSchoolEnter 是否从在线机构主页进来的
     * @param isOnlineClassEnter 是否是在线课堂班级过来的，如果是，需要隐藏在线课堂Tab
     */
    public static void show(@NonNull Context context, @NonNull @HideSortType.SortRes String sort, @NonNull String title,@NonNull String schoolId,
                            boolean isSchoolEnter,boolean isOnlineClassEnter){
        Intent intent = new Intent(context, CourseShopListActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(KEY_EXTRA_SORT_TYPE, sort);
        bundle.putString(KEY_EXTRA_TITLE_TEXT, title);
        bundle.putString(KEY_EXTRA_SCHOOL_ID,schoolId);
        bundle.putBoolean(KEY_EXTRA_IS_SCHOOL_ENTER,isSchoolEnter);
        bundle.putBoolean(KEY_EXTRA_IS_ONLINE_CLASS_ENTER,isOnlineClassEnter);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}
