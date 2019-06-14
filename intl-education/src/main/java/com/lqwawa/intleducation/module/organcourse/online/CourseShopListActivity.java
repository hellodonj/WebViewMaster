package com.lqwawa.intleducation.module.organcourse.online;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import android.widget.FrameLayout;
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
import com.lqwawa.intleducation.common.Common;
import com.lqwawa.intleducation.common.ui.ContactsMessageDialog;
import com.lqwawa.intleducation.common.utils.ActivityUtil;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.KeyboardUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.common.utils.Utils;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;
import com.lqwawa.intleducation.factory.event.EventConstant;
import com.lqwawa.intleducation.factory.event.EventWrapper;
import com.lqwawa.intleducation.factory.helper.LQConfigHelper;
import com.lqwawa.intleducation.module.discovery.adapter.CourseListAdapter;
import com.lqwawa.intleducation.module.discovery.ui.CourseDetailsActivity;
import com.lqwawa.intleducation.module.discovery.ui.classcourse.courseselect.CourseShopClassifyActivity;
import com.lqwawa.intleducation.module.discovery.ui.classcourse.courseselect.CourseShopClassifyParams;
import com.lqwawa.intleducation.module.discovery.ui.classcourse.history.HistoryClassCourseActivity;
import com.lqwawa.intleducation.module.discovery.ui.coursedetail.CourseDetailType;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.filtrate.HideSortType;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.filtrate.courselist.LQCourseListActivity;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.home.LanguageType;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.search.SearchActivity;
import com.lqwawa.intleducation.module.discovery.ui.subject.SetupConfigType;
import com.lqwawa.intleducation.module.discovery.ui.subject.add.AddSubjectActivity;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;
import com.lqwawa.intleducation.module.learn.tool.TaskSliderHelper;
import com.lqwawa.intleducation.module.learn.vo.SectionResListVo;
import com.lqwawa.intleducation.module.organcourse.ShopResourceData;
import com.lqwawa.intleducation.module.organcourse.filtrate.OrganCourseFiltrateActivity;
import com.lqwawa.intleducation.module.organcourse.online.pager.CourseShopPagerFragment;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.lqwawa.intleducation.module.discovery.ui.CourseSelectItemFragment.RESULT_LIST;

/**
 * @author medici
 * @desc 在线课堂的学程馆
 */
public class CourseShopListActivity extends ToolbarActivity implements View.OnClickListener {

    private static final int SUBJECT_SETTING_REQUEST_CODE = 1 << 1;

    private static final String KEY_EXTRA_TITLE_TEXT = "KEY_EXTRA_TITLE_TEXT";

    private static final String KEY_EXTRA_SORT_TYPE = "KEY_EXTRA_SORT_TYPE";

    private static final String KEY_EXTRA_SCHOOL_ID = "KEY_EXTRA_SCHOOL_ID";

    private static final String KEY_EXTRA_CLASS_ID = "KEY_EXTRA_CLASS_ID";

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

    private FrameLayout mNewCartContainer;
    private TextView mTvWorkCart;
    private TextView mTvCartPoint;

    private String mTitle;
    private String mSortType;
    private String mSchoolId;
    private String mClassId;
    private boolean isSchoolEnter;
    private boolean isOnlineClassEnter;

    private CourseShopClassifyParams mParams;
    private boolean mSelectResource;
    private ShopResourceData mResourceData;

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

        mParams = (CourseShopClassifyParams) bundle.getSerializable(ACTIVITY_BUNDLE_OBJECT);
        if (EmptyUtil.isNotEmpty(mParams)) {
            mSchoolId = mParams.getOrganId();
            mClassId = mParams.getClassId();
            mSelectResource = mParams.isSelectResource();
            mResourceData = mParams.getData();
            if (EmptyUtil.isEmpty(mSchoolId)) return false;
            if (mSelectResource && EmptyUtil.isEmpty(mResourceData)) return false;

            if (mSelectResource) {
                mResourceData.setInitiativeTrigger(mParams.isInitiativeTrigger());
                mResourceData.setSchoolId(mSchoolId);
                mResourceData.setClassId(mClassId);
                mResourceData.setEnterType(CourseDetailType.COURSE_DETAIL_SCHOOL_ENTER);
            }
        }

        if (EmptyUtil.isEmpty(mTitle) || EmptyUtil.isEmpty(mSortType)) {
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
        View.OnClickListener onClickListener = null;
        if (mSelectResource) {
            onClickListener = v -> {
                AddSubjectActivity.show(this, true, SUBJECT_SETTING_REQUEST_CODE);
            };
        }
        mTopBar.setRightFunctionText1(R.string.title_subject_setting, onClickListener);

        mSearchEt = (EditText) findViewById(R.id.search_et);
        mSearchFilter = (TextView) findViewById(R.id.filter_tv);
        mSearchClear = (ImageView) findViewById(R.id.search_clear_iv);
        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);

        mNewCartContainer = (FrameLayout) findViewById(R.id.new_cart_container);
        mTvWorkCart = (TextView) findViewById(R.id.tv_work_cart);
        mTvCartPoint = (TextView) findViewById(R.id.tv_cart_point);

        if (mSelectResource && mResourceData.isInitiativeTrigger()) {
            mNewCartContainer.setVisibility(View.VISIBLE);
            mNewCartContainer.setOnClickListener(this);
        }

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
                    if (EmptyUtil.isEmpty(searchKey)) {
                        return false;
                    }

                    for (SearchNavigator navigator : mNavigatorList) {
                        boolean isVisible = navigator.search(searchKey);
                        if (isVisible) break;
                    }
                    KeyboardUtil.hideSoftInput(CourseShopListActivity.this);
                    return true;
                }
                return false;
            }
        });

        mTabLayout.addOnTabSelectedListener(new TabSelectedAdapter() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                super.onTabSelected(tab);
                if (tab.getPosition() == mTabLayout.getTabCount() - 1) {
                    priceTabVisiale = true;
                    // 价格被选中
                    if (EmptyUtil.isNotEmpty(mPriceArrowView)) {
                        int state = mPriceArrowView.triggerSwitch();
                        triggerPriceSwitch(state);
                    }
                }

                // 解决在线机构在线班级列表，搜索不联动的问题
                if(EmptyUtil.isEmpty(mNavigatorList)) return;
                String searchKey = mSearchEt.getText().toString().trim();
                for (SearchNavigator navigator : mNavigatorList) {
                    boolean isVisible = navigator.search(searchKey);
                    if (isVisible) break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                super.onTabUnselected(tab);
                if (tab.getPosition() == mTabLayout.getTabCount() - 1) {
                    priceTabVisiale = false;
                    // 价格被选中
                    if (EmptyUtil.isNotEmpty(mPriceArrowView)) {
                        // 状态重置
                        mPriceArrowView.reset();
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshCartPoint();
    }

    /**
     * 刷新红点
     */
    private void refreshCartPoint() {
        if (EmptyUtil.isNotEmpty(TaskSliderHelper.onWorkCartListener)) {
            int count = TaskSliderHelper.onWorkCartListener.takeTaskCount();
            mTvCartPoint.setText(Integer.toString(count));
            if (count == 0) {
                mTvCartPoint.setVisibility(View.GONE);
            } else {
                mTvCartPoint.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void initData() {
        super.initData();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        mTabTitles = UIUtil.getStringArray(R.array.label_course_shop_tabs);
        Bundle extra = getIntent().getBundleExtra(Common.Constance.KEY_EXTRAS_STUDY_TASK);
        CourseShopPagerFragment recentUpdateFragment = null;
        if (mSelectResource) {
            recentUpdateFragment = CourseShopPagerFragment.newInstance(HideSortType.TYPE_SORT_ONLINE_SHOP_RECENT_UPDATE, mParams, extra);
        } else {
            recentUpdateFragment = CourseShopPagerFragment.newInstance(HideSortType.TYPE_SORT_ONLINE_SHOP_RECENT_UPDATE, mSchoolId, isSchoolEnter, isOnlineClassEnter);
        }


        CourseShopPagerFragment hotFragment = null;
        if (mSelectResource) {
            hotFragment = CourseShopPagerFragment.newInstance(HideSortType.TYPE_SORT_HOT_RECOMMEND, mParams, extra);
        } else {
            hotFragment = CourseShopPagerFragment.newInstance(HideSortType.TYPE_SORT_HOT_RECOMMEND, mSchoolId, isSchoolEnter, isOnlineClassEnter);
        }


        CourseShopPagerFragment priceFragment = null;
        if (mSelectResource) {
            priceFragment = CourseShopPagerFragment.newInstance(HideSortType.TYPE_SORT_ONLINE_SHOP_PRICE_DOWN, mParams, extra);
        } else {
            priceFragment = CourseShopPagerFragment.newInstance(HideSortType.TYPE_SORT_ONLINE_SHOP_PRICE_DOWN, mSchoolId, isSchoolEnter, isOnlineClassEnter);
        }

        List<Fragment> fragments = new ArrayList<>();
        fragments.add(recentUpdateFragment);
        fragments.add(hotFragment);
        fragments.add(priceFragment);

        mNavigatorList = new ArrayList<>();
        mNavigatorList.add(recentUpdateFragment);
        mNavigatorList.add(hotFragment);
        mNavigatorList.add(priceFragment);

        TabPagerAdapter mAdapter = new TabPagerAdapter(getSupportFragmentManager(), fragments);
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
                if (priceTabVisiale && event.getAction() == MotionEvent.ACTION_DOWN) {
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
     *
     * @param state 状态  1 UP 2 Down
     */
    private void triggerPriceSwitch(int state) {
        if (state == PriceArrowView.STATE_UP) {
            // 升序
            for (SearchNavigator navigator : mNavigatorList) {
                boolean isVisible = navigator.triggerPriceSwitch(true);
                if (isVisible) break;
            }
        } else if (state == PriceArrowView.STATE_DOWN) {
            // 降序
            for (SearchNavigator navigator : mNavigatorList) {
                boolean isVisible = navigator.triggerPriceSwitch(false);
                if (isVisible) break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.filter_tv) {
            String searchKey = mSearchEt.getText().toString();

            /*if (EmptyUtil.isEmpty(searchKey)){
                return;
            }*/

            for (SearchNavigator navigator : mNavigatorList) {
                boolean isVisible = navigator.search(searchKey);
                if (isVisible) break;
            }
        } else if (viewId == R.id.search_clear_iv) {
            mSearchEt.getText().clear();

            String searchKey = mSearchEt.getText().toString();

            /*if (EmptyUtil.isEmpty(searchKey)){
                return;
            }*/

            for (SearchNavigator navigator : mNavigatorList) {
                boolean isVisible = navigator.search(searchKey);
                if (isVisible) break;
            }
        } else if (viewId == R.id.new_cart_container) {
            // 点击作业库
            handleSubjectSettingData(this, UserHelper.getUserId());
        }
    }


    public void handleSubjectSettingData(Context context,
                                         String memberId) {
        int languageRes = Utils.isZh(UIUtil.getContext()) ? LanguageType.LANGUAGE_CHINESE : LanguageType.LANGUAGE_OTHER;
        LQConfigHelper.requestSetupConfigData(memberId, SetupConfigType.TYPE_TEACHER, languageRes, new DataSource.Callback<List<LQCourseConfigEntity>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                //没有数据
                popChooseSubjectDialog(context);
            }

            @Override
            public void onDataLoaded(List<LQCourseConfigEntity> entities) {
                if (entities == null || entities.size() == 0) {
                    popChooseSubjectDialog(context);
                } else {
                    //有数据
                    if (EmptyUtil.isNotEmpty(TaskSliderHelper.onWorkCartListener)) {
                        Bundle extras = getIntent().getBundleExtra(Common.Constance.KEY_EXTRAS_STUDY_TASK);
                        TaskSliderHelper.onWorkCartListener.enterIntroTaskDetailActivity(CourseShopListActivity.this, mSchoolId, mClassId, extras);
                    }
                }
            }
        });
    }

    private static void popChooseSubjectDialog(Context context) {
        ContactsMessageDialog messageDialog = new ContactsMessageDialog(
                context,
                null,
                context.getString(R.string.label_unset_choose_subject),
                context.getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                },
                context.getString(R.string.label_choose_subject),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        AddSubjectActivity.show((Activity) context, false, SUBJECT_SETTING_REQUEST_CODE);
                    }
                });
        messageDialog.show();
    }

    private class TabPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragments;

        public TabPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventWrapper event) {
        if (EventWrapper.isMatch(event, EventConstant.COURSE_SELECT_RESOURCE_EVENT)) {
            if (EmptyUtil.isNotEmpty(mResourceData) && !mResourceData.isInitiativeTrigger()) {
                ArrayList<SectionResListVo> vos = (ArrayList<SectionResListVo>) event.getData();
                setResult(Activity.RESULT_OK, new Intent().putExtra(RESULT_LIST, vos));
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SUBJECT_SETTING_REQUEST_CODE) {
                // 科目设置成功的回调
                Bundle extras = data.getExtras();
                if (EmptyUtil.isNotEmpty(extras)) {
                    boolean completed = extras.getBoolean(AddSubjectActivity.KEY_EXTRA_RESULT);
                    if (completed) {
                        for (SearchNavigator navigator : mNavigatorList) {
                            boolean isVisible = navigator.search("");
                            if (isVisible) break;
                        }
                    }
                }
            }
        }
    }

    /**
     * 课程列表显示入口
     *
     * @param context 上下文对象
     * @param sort    热门列表或者其它
     * @param title   标题文本
     */
    public static void show(@NonNull Context context,
                            @NonNull @HideSortType.SortRes String sort,
                            @NonNull String title) {
        Intent intent = new Intent(context, LQCourseListActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(KEY_EXTRA_SORT_TYPE, sort);
        bundle.putString(KEY_EXTRA_TITLE_TEXT, title);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    /**
     * 课程列表显示入口 在线课堂机构信息进来的
     *
     * @param context            上下文对象
     * @param sort               热门列表或者其它
     * @param title              标题文本
     * @param schoolId           机构Id
     * @param isSchoolEnter      是否从在线机构主页进来的
     * @param isOnlineClassEnter 是否是在线课堂班级过来的，如果是，需要隐藏在线课堂Tab
     */
    public static void show(@NonNull Context context,
                            @NonNull @HideSortType.SortRes String sort,
                            @NonNull String title, @NonNull String schoolId,
                            boolean isSchoolEnter, boolean isOnlineClassEnter) {
        Intent intent = new Intent(context, CourseShopListActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(KEY_EXTRA_SORT_TYPE, sort);
        bundle.putString(KEY_EXTRA_TITLE_TEXT, title);
        bundle.putString(KEY_EXTRA_SCHOOL_ID, schoolId);
        bundle.putBoolean(KEY_EXTRA_IS_SCHOOL_ENTER, isSchoolEnter);
        bundle.putBoolean(KEY_EXTRA_IS_ONLINE_CLASS_ENTER, isOnlineClassEnter);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    /**
     * 学程馆学程学习任务选择的入口
     *
     * @param activity 上下文对象
     */
    public static void show(@NonNull Activity activity,
                            @NonNull String title,
                            @NonNull @HideSortType.SortRes String sort,
                            @NonNull CourseShopClassifyParams params,
                            @Nullable Bundle extras) {
        Intent intent = new Intent(activity, CourseShopListActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(KEY_EXTRA_TITLE_TEXT, title);
        bundle.putString(KEY_EXTRA_SORT_TYPE, sort);
        bundle.putString(KEY_EXTRA_SCHOOL_ID, params.getOrganId());
        bundle.putString(KEY_EXTRA_CLASS_ID, params.getClassId());
        bundle.putSerializable(ACTIVITY_BUNDLE_OBJECT, params);
        bundle.putBundle(Common.Constance.KEY_EXTRAS_STUDY_TASK, extras);
        intent.putExtras(bundle);
        if (params.isSelectResource()) {
            ShopResourceData data = params.getData();
            activity.startActivityForResult(intent, data.getRequestCode());
        } else {
            activity.startActivity(intent);
        }
    }
}
