
package com.lqwawa.intleducation.module.organcourse.filtrate;

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
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.PresenterActivity;
import com.lqwawa.intleducation.base.widgets.PriceArrowView;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.base.widgets.adapter.TabSelectedAdapter;
import com.lqwawa.intleducation.common.Common;
import com.lqwawa.intleducation.common.ui.ContactsMessageDialog;
import com.lqwawa.intleducation.common.utils.ActivityUtil;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.common.utils.Utils;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.LQBasicsOuterEntity;
import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;
import com.lqwawa.intleducation.factory.data.entity.response.CheckPermissionResponseVo;
import com.lqwawa.intleducation.factory.data.entity.response.LQConfigResponseVo;
import com.lqwawa.intleducation.factory.data.entity.school.CheckSchoolPermissionEntity;
import com.lqwawa.intleducation.factory.event.EventConstant;
import com.lqwawa.intleducation.factory.event.EventWrapper;
import com.lqwawa.intleducation.factory.helper.LQConfigHelper;
import com.lqwawa.intleducation.factory.helper.LQCourseHelper;
import com.lqwawa.intleducation.module.discovery.ui.ImputAuthorizationCodeDialog;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.basics.BasicsCourseActivity;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.classifylist.ClassifyListActivity;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.filtrate.HideSortType;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.filtrate.Tab;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.home.LanguageType;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.search.SearchActivity;
import com.lqwawa.intleducation.module.discovery.ui.subject.SetupConfigType;
import com.lqwawa.intleducation.module.discovery.ui.subject.add.AddSubjectActivity;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;
import com.lqwawa.intleducation.module.learn.tool.TaskSliderHelper;
import com.lqwawa.intleducation.module.learn.vo.SectionResListVo;
import com.lqwawa.intleducation.module.organcourse.OrganLibraryType;
import com.lqwawa.intleducation.module.organcourse.ShopResourceData;
import com.lqwawa.intleducation.module.organcourse.filtrate.pager.OrganCourseFiltratePagerFragment;
import com.lqwawa.intleducation.module.user.tool.UserHelper;
import com.osastudio.common.utils.TipMsgHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.lqwawa.intleducation.module.discovery.ui.CourseSelectItemFragment.RESULT_LIST;

/**
 * @author medici
 * @desc 学程馆筛选页面
 */
public class OrganCourseFiltrateActivity extends PresenterActivity<OrganCourseFiltrateContract.Presenter>
        implements OrganCourseFiltrateContract.View, View.OnClickListener {

    private static final int SEARCH_REQUEST_CODE = 1 << 0;

    private static final int SUBJECT_SETTING_REQUEST_CODE = 1 << 1;

    // 一级页面分类数据
    private static final String KEY_EXTRA_ORGAN_COURSE_ENTITY = "KEY_EXTRA_ORGAN_COURSE_ENTITY";
    // 是否是选择资源
    private static final String KEY_EXTRA_ORGAN_SELECT = "KEY_EXTRA_ORGAN_ORGAN_SELECT";
    // 搜索条件
    private static final String KEY_EXTRA_SEARCH_KEY = "KEY_EXTRA_SEARCH_KEY";
    // 选择学程馆资源的数据
    private static final String KEY_EXTRA_RESOURCE_DATA = "KEY_EXTRA_RESOURCE_DATA";
    // 是否已经获取到授权
    private static final String KEY_EXTRA_IS_AUTHORIZED = "KEY_EXTRA_IS_AUTHORIZED";
    // 该分类是否获取到授权
    private static final String KEY_EXTRA_IS_REALLY_AUTHORIZED = "KEY_EXTRA_IS_REALLY_AUTHORIZED";
    // 是否直接从学程馆直接进来的
    private static final String KEY_EXTRA_HOST_ENTER = "KEY_EXTRA_HOST_ENTER";
    // 是否是班级学程入口进来的
    private static final String KEY_EXTRA_CLASS_COURSE_ENTER = "KEY_EXTRA_CLASS_COURSE_ENTER";
    // 该机构的角色信息
    private static final String KEY_EXTRA_ROLES = "KEY_EXTRA_ROLES";
    // 学程馆类型
    private static final String KEY_EXTRA_LIBRARY_TYPE = "KEY_EXTRA_LIBRARY_TYPE";
    // 特色英语ID
    private static final int CHARACTER_ENGLISH_ID = 2096;
    // 英语国际课程ID
    private static final int ENGLISH_INTERNATIONAL_COURSE = 2001;
    // 英语国内磕碜ID
    private static final int ENGLISH_INLAND_COURSE = 2002;
    // 小语种课程ID
    private static final int MINORITY_LANGUAGE_COURSE = 2004;
    // 基础课程ID
    private static final int BASIC_COURSE = 2003;
    // 特色英语ID
    private static final int CHARACTERISTICS_ENGLISH = 2005;

    // 分类阅读
    public static final int CLASSIFIED_READING_ID = 1001;
    // 绘本
    public static final int PICTURE_BOOK_ID = 1002;
    // Q配音
    public static final int Q_DUBBING_ID = 1003;

    // LQ English Kids
    private static final int ENGLISH_INTERNATIONAL_ENGLISH_KIDS_ID = 2010;
    // LQ English Primary
    private static final int ENGLISH_INTERNATIONAL_ENGLISH_PRIMARY_ID = 2011;
    // Think
    private static final int ENGLISH_INTERNATIONAL_ENGLISH_THINK_ID = 2245;

    // iTEP
    private static final int ENGLISH_INTERNATIONAL_ENGLISH_ITEP_ID = 2244;

    // RA BRAIN
    private static final int RA_BRAIN_ID = 2351;

    private TopBar mTopBar;

    private LinearLayout mHeaderLayout;
    private LinearLayout mTabVector1, mTabVector2, mTabVector3, mTabVector4;
    private TextView mTabLabel1, mTabLabel2, mTabLabel3, mTabLabel4;
    private TabLayout mTabLayout1, mTabLayout2, mTabLayout3, mTabLayout4;
    private TabLayout mSortTabLayout;

    // 需要显示的configType
    private int mConfigType1 = 2;
    private int mConfigType2 = 3;
    private int mConfigType3 = 4;
    private int mConfigType4 = 5;


    private LinearLayout mBottomLayout;
    private LinearLayout mSubjectLayout;
    private Button mAddSubject;
    private Button mBtnConfirmAdd;
    private Button mBtnRequestAuthorized;
    private Button mBtnMoreCourse;
    private TextView mCircleMoreCourse;

    private PriceArrowView mPriceArrowView;
    private ViewPager mViewPager;

    private LQCourseConfigEntity mEntity;
    // 搜索Key
    private String mKeyString;
    // 选择资源
    private boolean mSelectResource;
    // 是否从班级学程中进来
    private boolean isClassCourseEnter;
    private ShopResourceData mResourceData;

    // 全部文本
    private String mAllText = UIUtil.getString(R.string.label_course_filtrate_all);
    // 所有的标签信息
    private List<LQCourseConfigEntity> mAllLabels;
    // 筛选集合1
    private List<Tab> mFiltrateArray1;
    // 筛选集合2
    private List<Tab> mFiltrateArray2;
    // 筛选集合3
    private List<Tab> mFiltrateArray3;
    // 筛选集合4
    private List<Tab> mFiltrateArray4;

    private FrameLayout mNewCartContainer;
    private TextView mTvWorkCart;
    private TextView mTvCartPoint;

    private boolean isAuthorized;
    // 是否真正的授权
    private boolean isReallyAuthorized;
    private boolean isHostEnter;
    // 机构角色信息
    private String mRoles;
    // 学程馆类型
    private int mLibraryType;
    private boolean isTeacher;
    // 授权码是否过期
    private boolean isExist;

    private String[] mTabTitles;
    private boolean priceTabVisible;
    private int mPosition = 0;
    private OrganCourseFiltrateParams mParams;
    private List<OrganCourseFiltrateNavigator> mNavigatorList;
    
    private ImputAuthorizationCodeDialog imputAuthorizationCodeDialog;

    private static HashMap<String, String> authorizationErrorMapZh =
            new HashMap<>();
    private static HashMap<String, String> authorizationErrorMapEn =
            new HashMap<>();

    static {
        authorizationErrorMapZh.put("1001", "授权码错误，请重新输入");
        authorizationErrorMapZh.put("1002", "授权码已过期，请重新输入");
        authorizationErrorMapZh.put("1003", "授权码尚未生效，请重新输入");
        authorizationErrorMapZh.put("1004", "授权码已被使用，请重新输入");
        authorizationErrorMapEn.put("1001", "Incorrect authorization code, please re-enter");
        authorizationErrorMapEn.put("1002", "Authorization code expired，please re-enter");
        authorizationErrorMapEn.put("1003", "Invalid authorization code, please re-enter");
        authorizationErrorMapEn.put("1004", "Authorization code has been used, please re-enter");
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_organ_course_filtrate;
    }

    @Override
    protected OrganCourseFiltrateContract.Presenter initPresenter() {
        return new OrganCourseFiltratePresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected boolean initArgs(@NonNull Bundle bundle) {
        mEntity = (LQCourseConfigEntity) bundle.getSerializable(KEY_EXTRA_ORGAN_COURSE_ENTITY);
        mKeyString = bundle.getString(KEY_EXTRA_SEARCH_KEY);
        mSelectResource = bundle.getBoolean(KEY_EXTRA_ORGAN_SELECT);
        isClassCourseEnter = bundle.getBoolean(KEY_EXTRA_CLASS_COURSE_ENTER);
        isAuthorized = bundle.getBoolean(KEY_EXTRA_IS_AUTHORIZED);
        isReallyAuthorized = bundle.getBoolean(KEY_EXTRA_IS_REALLY_AUTHORIZED);
        isHostEnter = bundle.getBoolean(KEY_EXTRA_HOST_ENTER);
        mRoles = bundle.getString(KEY_EXTRA_ROLES);
        mLibraryType = bundle.getInt(KEY_EXTRA_LIBRARY_TYPE);
        isTeacher = UserHelper.isTeacher(mRoles);
        if (mSelectResource)
            mResourceData = (ShopResourceData) bundle.getSerializable(KEY_EXTRA_RESOURCE_DATA);
        if (mSelectResource && EmptyUtil.isEmpty(mResourceData)) return false;
        if (EmptyUtil.isEmpty(mEntity)) return false;
        return super.initArgs(bundle);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mTopBar = (TopBar) findViewById(R.id.top_bar);
        mTopBar.setBack(true);
        mTopBar.setTitle(mEntity.getConfigValue());
        mTopBar.setRightFunctionImage1(R.drawable.search, view -> {
            // 搜索页面
            SearchActivity.show(OrganCourseFiltrateActivity.this,
                    HideSortType.TYPE_SORT_NEW_SCHOOL_SHOP,
                    mEntity.getConfigValue(), SEARCH_REQUEST_CODE);
        });

        mNewCartContainer = (FrameLayout) findViewById(R.id.new_cart_container);
        mTvWorkCart = (TextView) findViewById(R.id.tv_work_cart);
        mTvCartPoint = (TextView) findViewById(R.id.tv_cart_point);

        if(mSelectResource && mResourceData.isInitiativeTrigger()) {
            mNewCartContainer.setVisibility(View.VISIBLE);
            mNewCartContainer.setOnClickListener(this);
        }

        mHeaderLayout = (LinearLayout) findViewById(R.id.header_layout);
        mTabVector1 = (LinearLayout) findViewById(R.id.tab_vector_1);
        mTabVector2 = (LinearLayout) findViewById(R.id.tab_vector_2);
        mTabVector3 = (LinearLayout) findViewById(R.id.tab_vector_3);
        mTabVector4 = (LinearLayout) findViewById(R.id.tab_vector_4);
        mTabLabel1 = (TextView) findViewById(R.id.tab_label_1);
        mTabLabel2 = (TextView) findViewById(R.id.tab_label_2);
        mTabLabel3 = (TextView) findViewById(R.id.tab_label_3);
        mTabLabel4 = (TextView) findViewById(R.id.tab_label_4);
        mTabLayout1 = (TabLayout) findViewById(R.id.tab_layout_1);
        mTabLayout2 = (TabLayout) findViewById(R.id.tab_layout_2);
        mTabLayout3 = (TabLayout) findViewById(R.id.tab_layout_3);
        mTabLayout4 = (TabLayout) findViewById(R.id.tab_layout_4);

        mSortTabLayout = (TabLayout) findViewById(R.id.tab_layout_sort);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);

        if (mSelectResource) {
            // 隐藏HeaderLayout
            mHeaderLayout.setVisibility(View.GONE);
            mTopBar.findViewById(R.id.right_function1_image).setVisibility(View.GONE);
        }

        mBottomLayout = (LinearLayout) findViewById(R.id.bottom_layout);
        mSubjectLayout = (LinearLayout) findViewById(R.id.subject_layout);
        mAddSubject = (Button) findViewById(R.id.btn_add_subject);
        mAddSubject.setOnClickListener(this);
        mBtnConfirmAdd = (Button) findViewById(R.id.btn_confirm);
        mBtnRequestAuthorized = (Button) findViewById(R.id.btn_request_authorized);
        mBtnMoreCourse = (Button) findViewById(R.id.btn_more_course);
        mCircleMoreCourse = (TextView) findViewById(R.id.tv_more_course);

        if (!isClassCourseEnter) {
            // 不管是不是直接点击学程馆进入的二级页面,都需要显示更多课程和申请授权
            mBtnConfirmAdd.setVisibility(View.GONE);
            mBtnMoreCourse.setVisibility(mLibraryType == OrganLibraryType.TYPE_LQCOURSE_SHOP ?
                    View.VISIBLE : View.GONE);
            if (isHostEnter && !mSelectResource) {
                mBottomLayout.setVisibility(View.VISIBLE);
                mBtnRequestAuthorized.setOnClickListener(this);
                mBtnMoreCourse.setOnClickListener(this);
                // 显示Bottom Layout 就不显示更多课程
                mCircleMoreCourse.setVisibility(View.GONE);
            } else {
                mBottomLayout.setVisibility(View.GONE);
                if (!isHostEnter && !mSelectResource) {
                    // 如果即不是选择资源,又不是直接从学程馆进来的，二级页面
                    mCircleMoreCourse.setVisibility(mLibraryType == OrganLibraryType.TYPE_LQCOURSE_SHOP
                            ? View.VISIBLE : View.GONE);
                    mCircleMoreCourse.setOnClickListener(this);
                } else {
                    mCircleMoreCourse.setVisibility(View.GONE);
                }
            }
        } else {
            mBtnConfirmAdd.setVisibility(View.VISIBLE);
            mBtnConfirmAdd.setOnClickListener(this);
            mBottomLayout.setVisibility(View.GONE);
            mCircleMoreCourse.setVisibility(View.GONE);
        }

        if(mSelectResource){
            mSubjectLayout.setVisibility(View.GONE);
        }
        mHeaderLayout.setVisibility(View.GONE);
        mSortTabLayout.setVisibility(View.GONE);
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

        String organId = mEntity.getEntityOrganId();
        int parentId = mEntity.getId();
        String level = mEntity.getLevel();
        mPresenter.requestOrganCourseLabelData(organId, parentId, level, mLibraryType);

        if (!isClassCourseEnter) {
            if (mLibraryType == OrganLibraryType.TYPE_LQCOURSE_SHOP) {
                if (mSelectResource) {
                    // 选择资源检查授权 自动申请
                    mPresenter.requestCheckSchoolPermission(organId, 0, true);
                } else {
                    // 手动授权,检查授权
                    mPresenter.requestCheckSchoolPermission(organId, 0, false);
                }
            }
        }

        mParams = new OrganCourseFiltrateParams(organId, mLibraryType, 2, isClassCourseEnter);
        mParams.setSelectResource(mSelectResource)
                .setAuthorized(isAuthorized)
                .setReallyAuthorized(isReallyAuthorized)
                .setShopResourceData(mResourceData)
                .setBundle(getIntent().getBundleExtra(Common.Constance.KEY_EXTRAS_STUDY_TASK));
        mTabTitles = UIUtil.getStringArray(R.array.label_course_shop_tabs);
        OrganCourseFiltratePagerFragment recentUpdateFragment =
                OrganCourseFiltratePagerFragment.newInstance(mParams);
        OrganCourseFiltratePagerFragment hotFragment
                = OrganCourseFiltratePagerFragment.newInstance(mParams);
        OrganCourseFiltratePagerFragment priceFragment
                = OrganCourseFiltratePagerFragment.newInstance(mParams);

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
        mSortTabLayout.setupWithViewPager(mViewPager);
        mViewPager.setOffscreenPageLimit(fragments.size() - 1);

        // 设置TabLayout最后一个节点有upDown
        TabLayout.Tab tabAt = mSortTabLayout.getTabAt(mSortTabLayout.getTabCount() - 1);
        PriceArrowView view = new PriceArrowView(this);
        view.setTabTitle(mTabTitles[mSortTabLayout.getTabCount() - 1]);
        tabAt.setCustomView(view.getRootView());
        mPriceArrowView = view;

        mPriceArrowView.setOnTouchListener((v, event) -> {
            if (priceTabVisible && event.getAction() == MotionEvent.ACTION_DOWN) {
                int state = mPriceArrowView.triggerSwitch();
                triggerPriceSwitch(state);
                return true;
            }
            return false;
        });

        mSortTabLayout.addOnTabSelectedListener(new TabSelectedAdapter() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                super.onTabSelected(tab);
                mPosition = tab.getPosition();
                if (tab.getPosition() == mSortTabLayout.getTabCount() - 1) {
                    priceTabVisible = true;
                    // 价格被选中
                    if (EmptyUtil.isNotEmpty(mPriceArrowView)) {
                        int state = mPriceArrowView.triggerSwitch();
                        triggerPriceSwitch(state);
                    }
                } else {
                    int position = tab.getPosition();
                    if (position == 0) {
                        mParams.setSort(2);
                    } else if (position == 1) {
                        mParams.setSort(1);
                    }
                    mNavigatorList.get(mPosition).triggerUpdateData(mParams);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                super.onTabUnselected(tab);
                if (tab.getPosition() == mSortTabLayout.getTabCount() - 1) {
                    priceTabVisible = false;
                    // 价格被选中
                    if (EmptyUtil.isNotEmpty(mPriceArrowView)) {
                        // 状态重置
                        mPriceArrowView.reset();
                    }
                }
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
            mParams.setSort(5);
        } else if (state == PriceArrowView.STATE_DOWN) {
            mParams.setSort(6);
        }
        mNavigatorList.get(mPosition).triggerUpdateData(mParams);
    }

    /**
     * 触发更新
     */
    private void triggerUpdateData() {
        if (EmptyUtil.isNotEmpty(mAllLabels)) {
            clearConfigArrayStatus(mAllLabels);
        }
        requestCourseData();
    }

    /**
     * 根据筛选条件,查询课程
     */
    public void requestCourseData() {
        // 获取筛选条件
        String level = null;
        int paramOneId = 0, paramTwoId = 0, paramThreeId = 0;

        for (Tab tab : mFiltrateArray1) {
            if (tab.isSelected()) {
                level = tab.getLevel();
            }
        }

        for (Tab tab : mFiltrateArray2) {
            if (tab.isSelected()) {
                paramOneId = tab.getLabelId();
            }
        }

        for (Tab tab : mFiltrateArray3) {
            if (tab.isSelected()) {
                paramTwoId = tab.getLabelId();
            }
        }

        for (Tab tab : mFiltrateArray4) {
            if (tab.isSelected()) {
                paramThreeId = tab.getLabelId();
            }
        }

        if (EmptyUtil.isEmpty(mKeyString)) mKeyString = "";

        if (mSelectResource) {
            level = mEntity.getLevel();
        } else {
            if (TextUtils.isEmpty(level)) {
                level = mEntity.getLevel();
            }
        }
        mParams.setLevel(level)
                .setKeyString(mKeyString)
                .setParamOneId(paramOneId)
                .setParamTwoId(paramTwoId)
                .setParamThreeId(paramThreeId);
        mNavigatorList.get(mPosition).triggerUpdateData(mParams);

    }

    @Override
    public void updateOrganCourseLabelView(@NonNull List<LQCourseConfigEntity> entities) {
        mAllLabels = entities;

        mFiltrateArray1 = new ArrayList<>();
        mFiltrateArray2 = new ArrayList<>();
        mFiltrateArray3 = new ArrayList<>();
        mFiltrateArray4 = new ArrayList<>();

        if (EmptyUtil.isNotEmpty(entities)) {

            mHeaderLayout.setVisibility(View.VISIBLE);
            mSortTabLayout.setVisibility(View.VISIBLE);

            recursionConfig(entities);

            configLabel();
            // 设置TabLayout相关监听
            initTabListener();
            // 设置数据到TabLayout上
            initTabControl();
            // 设置第一个选中
            // mTabLayout1.getTabAt(0).select();
            // mTabLayout1.getTabAt(0).getCustomView().setSelected(true);
        } else{
            // 数据为空
            mHeaderLayout.setVisibility(View.GONE);
            if (mLibraryType == OrganLibraryType.TYPE_BRAIN_LIBRARY) {
                mSortTabLayout.setVisibility(View.GONE);
            } else {
                mSortTabLayout.setVisibility(View.VISIBLE);
            }
//            mRefreshLayout.setVisibility(View.GONE);
//            mEmptyLayout.setVisibility(View.VISIBLE);
            triggerUpdateData();
        }
    }


    /**
     * 递归调用
     */
    private void recursionConfig(List<LQCourseConfigEntity> entities) {
        clearArray(mConfigType1);
        recursionConfigArray(entities);
        // 递归调用之后展示全部
        assembleAllLabel();
    }

    /**
     * 组装全部
     */
    private void assembleAllLabel() {
        // 第二个筛选容器,加全部
        Tab allTab2 = Tab.buildAll(mAllText);
        if (!mFiltrateArray2.contains(allTab2) && mFiltrateArray2.size() != 1) {
            mFiltrateArray2.add(0, allTab2);
        }


        // 第三个筛选容器,加全部
        Tab allTab3 = Tab.buildAll(mAllText);
        if (!mFiltrateArray3.contains(allTab3) && mFiltrateArray3.size() != 1) {
            mFiltrateArray3.add(0, allTab3);
        }


        // 第四个筛选容器,加全部
        Tab allTab4 = Tab.buildAll(mAllText);
        if (!mFiltrateArray4.contains(allTab4) && mFiltrateArray4.size() != 1) {
            mFiltrateArray4.add(0, allTab4);
        }
    }

    /**
     * 清空默认设置科目的选择状态
     *
     * @param array 标签数据
     */
    private void clearConfigArrayStatus(@NonNull List<LQCourseConfigEntity> array) {
        if (EmptyUtil.isEmpty(array)) return;

        for (LQCourseConfigEntity entity : array) {
            entity.setSelected(false);

            // 递归调用
            List<LQCourseConfigEntity> childList = entity.getChildList();
            clearConfigArrayStatus(childList);
        }
    }

    /**
     * 递归调用
     */
    private void recursionConfigArray(@NonNull List<LQCourseConfigEntity> array) {
        if (EmptyUtil.isEmpty(array)) return;

        for (LQCourseConfigEntity entity : array) {
            if (entity.getConfigType() == mConfigType1) {
                Tab tab = Tab.build(entity);
                if (!mFiltrateArray1.contains(tab)) {
                    mFiltrateArray1.add(Tab.build(entity));
                }
            }

            if (entity.getConfigType() == mConfigType2) {
                Tab tab = Tab.build(entity);
                if (!mFiltrateArray2.contains(tab)) {
                    mFiltrateArray2.add(Tab.build(entity));
                }
            }

            if (entity.getConfigType() == mConfigType3) {
                Tab tab = Tab.build(entity);
                if (!mFiltrateArray3.contains(tab)) {
                    mFiltrateArray3.add(Tab.build(entity));
                }
            }


            if (entity.getConfigType() == mConfigType4) {
                Tab tab = Tab.build(entity);
                if (!mFiltrateArray4.contains(tab)) {
                    mFiltrateArray4.add(Tab.build(entity));
                }
            }

            // 递归调用
            List<LQCourseConfigEntity> childList = entity.getChildList();
            recursionConfigArray(childList);
        }
    }

    /**
     * 清空集合
     */
    private void clearArray(int configType) {
        // 清空所有数据
        if (configType <= mConfigType4) {
            // 清除第三个
            mFiltrateArray4.clear();
        }

        if (configType <= mConfigType3) {
            // 清除第三个
            mFiltrateArray3.clear();
        }

        if (configType <= mConfigType2) {
            // 清除第二个
            mFiltrateArray2.clear();
        }

        if (configType <= mConfigType1) {
            // 清除第一个
            mFiltrateArray1.clear();
        }
    }

    /**
     * 组装Label数据
     */
    private void configLabel() {
        int rootId = mEntity.getId();
        if (rootId == MINORITY_LANGUAGE_COURSE) {
            // 小语种课程
            mTabVector3.setVisibility(View.GONE);
            mTabVector4.setVisibility(View.GONE);

            // 语言,级别
            mTabLabel1.setText(getString(R.string.label_colon_language));
            mTabLabel2.setText(getString(R.string.label_colon_level));
        } else if (rootId == ENGLISH_INTERNATIONAL_COURSE) {
            // 英语国际课程
            mTabVector4.setVisibility(View.GONE);

            // 类型 类型 科目
            mTabLabel1.setText(getString(R.string.label_colon_type));
            mTabLabel2.setText(getString(R.string.label_colon_type));
            mTabLabel3.setText(getString(R.string.label_colon_subject));
        } else if (rootId == CHARACTERISTICS_ENGLISH) {
            // 特色课程 三级页面
            mTabVector4.setVisibility(View.GONE);

            // 学段 年级 科目
            mTabLabel1.setText(getString(R.string.label_colon_period));
            mTabLabel2.setText(getString(R.string.label_colon_grade));
            mTabLabel3.setText(getString(R.string.label_colon_subject));
        } else if (rootId == BASIC_COURSE) {
            // 类型 年级 科目 出版社
            mTabLabel1.setText(getString(R.string.label_colon_period));
            mTabLabel2.setText(getString(R.string.label_colon_grade));
            mTabLabel3.setText(getString(R.string.label_colon_subject));
            mTabLabel4.setText(getString(R.string.book_concern));
        } else if (rootId == CLASSIFIED_READING_ID) {
            //分类阅读
            mTabVector3.setVisibility(View.GONE);
            mTabVector4.setVisibility(View.GONE);

            // 科目, 级别
            mTabLabel1.setText(getString(R.string.label_colon_subject));
            mTabLabel2.setText(getString(R.string.label_colon_level));
        } else if (rootId == PICTURE_BOOK_ID) {
            //绘本 二级页面
            mTabVector3.setVisibility(View.GONE);
            mTabVector4.setVisibility(View.GONE);

            // 年龄段 语言 主题
            mTabLabel1.setText(getString(R.string.label_colon_level));
            mTabLabel2.setText(getString(R.string.label_colon_language));
        } else if (rootId == RA_BRAIN_ID) {
            // 全脑馆
            mTabVector3.setVisibility(View.GONE);
            mTabVector4.setVisibility(View.GONE);

            // 科目, 级别
            mTabLabel1.setText(getString(R.string.label_colon_type));
            mTabLabel2.setText(getString(R.string.label_colon_age));
        }
    }

    /**
     * 填充数据,设置监听
     */
    private void initTabControl() {
        initTabControl1();
        // initTabControl2();
        // initTabControl3();
        // initTabControl4();
    }

    private void initTabControl1() {
        mTabLayout1.removeAllTabs();
        // 查看是否有Selected的
        boolean haveSelected = false;
        for (Tab tab : mFiltrateArray1) {
            if (tab.isFirstSelected() && isTeacher) {
                haveSelected = true;
                break;
            }
        }

        boolean setSelected = false;
        for (Tab tab : mFiltrateArray1) {
            View tabView = UIUtil.inflate(R.layout.item_tab_control_layout);
            TextView tvContent = (TextView) tabView.findViewById(R.id.tv_content);
            tvContent.setText(tab.getConfigValue());
            // 将tab数据作为Tag设置到TabLayout的TabLayout.Tab上
            TabLayout.Tab newTab = mTabLayout1.newTab().setCustomView(tabView).setTag(tab);

            if (!setSelected) {
                setSelected = (mTabLayout1.getTabCount() == 0 && !haveSelected) || (tab.isFirstSelected() && isTeacher);
                mTabLayout1.addTab(newTab, setSelected);
            } else {
                // 已经添加过已经选择的Tab
                mTabLayout1.addTab(newTab);
            }
        }


        mTabLayout1.smoothScrollTo(0, 0);
    }

    private void initTabControl2() {
        mTabLayout2.removeAllTabs();

        // 查看是否有Selected的
        boolean haveSelected = false;
        for (Tab tab : mFiltrateArray2) {
            if (tab.isFirstSelected() && isTeacher) {
                haveSelected = true;
                break;
            }
        }

        boolean setSelected = false;
        for (Tab tab : mFiltrateArray2) {
            View tabView = UIUtil.inflate(R.layout.item_tab_control_layout);
            TextView tvContent = (TextView) tabView.findViewById(R.id.tv_content);
            tvContent.setText(tab.getConfigValue());
            TabLayout.Tab newTab = mTabLayout2.newTab().setCustomView(tabView).setTag(tab);

            if (!setSelected) {
                setSelected = (mTabLayout2.getTabCount() == 0 && !haveSelected) || (tab.isFirstSelected() && isTeacher);
                mTabLayout2.addTab(newTab, setSelected);
            } else {
                // 已经添加过已经选择的Tab
                mTabLayout2.addTab(newTab);
            }
        }

        mTabLayout2.smoothScrollTo(0, 0);
    }

    private void initTabControl3() {
        mTabLayout3.removeAllTabs();

        // 查看是否有Selected的
        boolean haveSelected = false;
        for (Tab tab : mFiltrateArray3) {
            if (tab.isFirstSelected() && isTeacher) {
                haveSelected = true;
                break;
            }
        }

        boolean setSelected = false;
        if (EmptyUtil.isNotEmpty(mFiltrateArray3)) {
            for (Tab tab : mFiltrateArray3) {
                View tabView = UIUtil.inflate(R.layout.item_tab_control_layout);
                TextView tvContent = (TextView) tabView.findViewById(R.id.tv_content);
                tvContent.setText(tab.getConfigValue());
                TabLayout.Tab newTab = mTabLayout3.newTab().setCustomView(tabView).setTag(tab);

                if (!setSelected) {
                    setSelected = (mTabLayout3.getTabCount() == 0 && !haveSelected) || (tab.isFirstSelected() && isTeacher);
                    mTabLayout3.addTab(newTab, setSelected);
                } else {
                    // 已经添加过已经选择的Tab
                    mTabLayout3.addTab(newTab);
                }
            }
        }

        mTabLayout3.smoothScrollTo(0, 0);
    }

    private void initTabControl4() {
        mTabLayout4.removeAllTabs();

        // 查看是否有Selected的
        boolean haveSelected = false;
        for (Tab tab : mFiltrateArray4) {
            if (tab.isFirstSelected() && isTeacher) {
                haveSelected = true;
                break;
            }
        }

        boolean setSelected = false;
        if (EmptyUtil.isNotEmpty(mFiltrateArray4)) {
            for (Tab tab : mFiltrateArray4) {
                View tabView = UIUtil.inflate(R.layout.item_tab_control_layout);
                TextView tvContent = (TextView) tabView.findViewById(R.id.tv_content);
                tvContent.setText(tab.getConfigValue());
                TabLayout.Tab newTab = mTabLayout4.newTab().setCustomView(tabView).setTag(tab);

                if (!setSelected) {
                    setSelected = (mTabLayout4.getTabCount() == 0 && !haveSelected) || (tab.isFirstSelected() && isTeacher);
                    mTabLayout4.addTab(newTab, setSelected);
                } else {
                    // 已经添加过已经选择的Tab
                    mTabLayout4.addTab(newTab);
                }
            }
        }

        mTabLayout4.smoothScrollTo(0, 0);
    }

    /**
     * 设置相关联动的监听
     */
    private void initTabListener() {
        mTabLayout1.addOnTabSelectedListener(new TabSelectedAdapter() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                super.onTabSelected(tab);
                // 全部发生数据联动
                Tab tabData = (Tab) tab.getTag();
                setTabItemSelected(mFiltrateArray1, tabData);
                if (tabData.getChildList() == null || tabData.getChildList().isEmpty()) {
                    initTabControl2();
                    initTabControl3();
                    assembleAllLabel();
                    return;
                }
                // 重新配置2,3数据的联动效果
                clearArray(mConfigType2);
                recursionConfigArray(tabData.getChildList());

                // 基础课程特殊一些，在点击第一TabLayout学段分类的时候,其Children并没有出版社的信息
                // 出版社的信息在学段同一级别下
                int rootId = mEntity.getId();
                if (rootId == BASIC_COURSE) {
                    // 基础课程
                    for (LQCourseConfigEntity entity : mAllLabels) {
                        if (entity.getConfigType() == mConfigType4) {
                            Tab newTab = Tab.build(entity);
                            if (!mFiltrateArray4.contains(newTab)) {
                                mFiltrateArray4.add(newTab);
                            }
                        }
                    }
                }

                assembleAllLabel();
                initTabControl2();
                initTabControl3();
                initTabControl4();

                // 数据请求
                int firstId = tabData.getId();

                if (rootId == ENGLISH_INTERNATIONAL_COURSE) {
                    if (firstId == ENGLISH_INTERNATIONAL_ENGLISH_PRIMARY_ID ||
                            firstId == ENGLISH_INTERNATIONAL_ENGLISH_ITEP_ID) {
                        // 选中了英语国际课程的 LQ English Primary Or ITEP
                        mTabVector3.setVisibility(View.VISIBLE);
                        mTabLabel2.setText(getString(R.string.label_colon_grade));
                    } else if (firstId == ENGLISH_INTERNATIONAL_ENGLISH_THINK_ID ||
                            firstId == ENGLISH_INTERNATIONAL_ENGLISH_KIDS_ID) {
                        mTabLabel2.setText(getString(R.string.label_colon_grade));
                        mTabVector3.setVisibility(View.GONE);
                    } else {
                        mTabLabel2.setText(getString(R.string.label_colon_subject));
                        mTabVector3.setVisibility(View.GONE);
                    }
                }
            }
        });

        mTabLayout2.addOnTabSelectedListener(new TabSelectedAdapter() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                super.onTabSelected(tab);
                Tab tabData = (Tab) tab.getTag();
                setTabItemSelected(mFiltrateArray2, tabData);

                // 数据请求
                int rootId = mEntity.getId();
                TabLayout.Tab tabAt = mTabLayout1.getTabAt(mTabLayout1.getSelectedTabPosition());
                if(EmptyUtil.isNotEmpty(tabAt)){
                    Tab firstTab = (Tab) tabAt.getTag();
                    int firstId = firstTab.getId();
                    if (rootId == MINORITY_LANGUAGE_COURSE ||
                            rootId == CHARACTERISTICS_ENGLISH ||
                            rootId == BASIC_COURSE ||
                            rootId == ENGLISH_INTERNATIONAL_COURSE &&
                                    (firstId == ENGLISH_INTERNATIONAL_ENGLISH_PRIMARY_ID ||
                                            firstId == ENGLISH_INTERNATIONAL_ENGLISH_ITEP_ID)) {
                        // 选中的是小语种的Id
                        triggerUpdateData();
                    } else if (rootId == RA_BRAIN_ID || rootId == PICTURE_BOOK_ID) {
                        triggerUpdateData();
                    }
                }
            }

        });

        mTabLayout3.addOnTabSelectedListener(new TabSelectedAdapter() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                super.onTabSelected(tab);
                Tab tabData = (Tab) tab.getTag();
                setTabItemSelected(mFiltrateArray3, tabData);
                // 数据请求
                triggerUpdateData();
            }
        });

        mTabLayout4.addOnTabSelectedListener(new TabSelectedAdapter() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                super.onTabSelected(tab);
                Tab tabData = (Tab) tab.getTag();
                setTabItemSelected(mFiltrateArray4, tabData);
                // 数据请求
                triggerUpdateData();
            }
        });
    }

    /**
     * 设置该Tab选中
     *
     * @param array 对应的Tab集合
     * @param tab   选择的Tab
     */
    private void setTabItemSelected(@NonNull List<Tab> array, @NonNull Tab tab) {
        if (EmptyUtil.isEmpty(array) || EmptyUtil.isEmpty(tab)) return;
        for (Tab item : array) {
            item.setSelected(false);
            if (item.equals(tab)) {
                item.setSelected(true);
            }
        }
    }

    @Override
    public void updateCheckPermissionView(@NonNull CheckSchoolPermissionEntity entity, boolean autoRequest) {
        if (EmptyUtil.isNotEmpty(entity)) {

            if (entity.isAuthorized()) {
                // 可能填的验证码是授权别的分类
                refreshAuthorizedInfo(entity);

                // 已经获取授权,并且没有失效
                isAuthorized = true;
                mParams.setAuthorized(isAuthorized);
                isExist = entity.isExist();
                // UIUtil.showToastSafe(R.string.label_old_request_authorization);
            } else {
                if (autoRequest) {

                    requestAuthorizedPermission(entity.isExist());
                }
            }
        }
    }

    /**
     * 申请授权
     */
    private void requestAuthorizedPermission(boolean isExist) {
        String tipInfo = UIUtil.getString(R.string.label_request_authorization_tip);
        if (isExist) {
            tipInfo = UIUtil.getString(R.string.authorization_out_time_tip);
        }
        if (imputAuthorizationCodeDialog == null) {
            imputAuthorizationCodeDialog = new ImputAuthorizationCodeDialog(this, tipInfo,
                    new ImputAuthorizationCodeDialog.CommitCallBack() {
                        @Override
                        public void onCommit(String code) {
                            commitAuthorizationCode(code);
                        }

                        @Override
                        public void onCancel() {
                            if (EmptyUtil.isNotEmpty(imputAuthorizationCodeDialog)) {
                                imputAuthorizationCodeDialog.dismiss();
                            }
                        }
                    });
        }
        imputAuthorizationCodeDialog.setTipInfo(tipInfo);
        if (!imputAuthorizationCodeDialog.isShowing()) {
            imputAuthorizationCodeDialog.show();
        }
    }

    /**
     * @param code 授权码
     * @desc 申请授权
     * @author medici
     */
    private void commitAuthorizationCode(@NonNull String code) {
        String schoolId = mEntity.getEntityOrganId();
        mPresenter.requestSaveAuthorization(schoolId, 0, code);
    }

    @Override
    public void updateRequestPermissionView(@NonNull CheckPermissionResponseVo<Void> responseVo) {
        if (EmptyUtil.isEmpty(responseVo)) return;
        if (responseVo.isSucceed()) {
            // 刷新权限信息
            String rightValue = responseVo.getRightValue();
            CheckSchoolPermissionEntity entity = new CheckSchoolPermissionEntity();
            entity.setRightValue(rightValue);
            entity.setAuthorized(true);
            refreshAuthorizedInfo(entity);

            isAuthorized = true;
            mParams.setAuthorized(isAuthorized);
            isExist = false;
            if (imputAuthorizationCodeDialog != null) {
                imputAuthorizationCodeDialog.setCommited(true);
                imputAuthorizationCodeDialog.dismiss();
            }
        } else {
            String language = Locale.getDefault().getLanguage();
            //提示授权码错误原因然后退出
            UIUtil.showToastSafe(language.equals("zh") ? authorizationErrorMapZh.get("" + responseVo.getCode()) : authorizationErrorMapEn.get("" + responseVo.getCode()));

            if (imputAuthorizationCodeDialog != null) {
                imputAuthorizationCodeDialog.clearPassword();
            }
        }
    }

    /**
     * 刷新授权信息的View 可能填的验证码是授权别的分类
     *
     * @param entity 授权信息的返回
     */
    private void refreshAuthorizedInfo(@NonNull CheckSchoolPermissionEntity entity) {
        String rightValue = entity.getRightValue();
        if (EmptyUtil.isEmpty(rightValue)) return;
        if (TextUtils.equals(rightValue, "0")) {
            isReallyAuthorized = true;
            mParams.setReallyAuthorized(isReallyAuthorized);
        }
        String[] values = rightValue.split(",");
        if (EmptyUtil.isNotEmpty(values)) {
            List<String> strings = Arrays.asList(values);
            if (strings.contains(Integer.toString(mEntity.getId()))) {
                isReallyAuthorized = true;
                mParams.setReallyAuthorized(isReallyAuthorized);
            }
        }
    }

    @Override
    public void showError(int str) {
        super.showError(str);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.btn_request_authorized) {
            // 点击获取授权
            if (isAuthorized) {
                // 已经获取到授权
                UIUtil.showToastSafe(R.string.label_request_authorization_succeed);
                return;
            }

            requestAuthorizedPermission(isExist);
        } else if (viewId == R.id.btn_more_course || viewId == R.id.tv_more_course) {
            if (mEntity.getId() == BASIC_COURSE) {
                // 获取最新的基础课程信息
                // 获取中英文数据
                int languageRes = Utils.isZh(UIUtil.getContext()) ? LanguageType.LANGUAGE_CHINESE : LanguageType.LANGUAGE_OTHER;

                LQCourseHelper.requestLQHomeConfigData(languageRes, 1, 0, new DataSource.Callback<LQConfigResponseVo<List<LQCourseConfigEntity>, List<LQBasicsOuterEntity>>>() {
                    @Override
                    public void onDataNotAvailable(int strRes) {
                        // 重要的数据发生异常了，才弹提示
                        UIUtil.showToastSafe(strRes);
                    }

                    @Override
                    public void onDataLoaded(LQConfigResponseVo<List<LQCourseConfigEntity>, List<LQBasicsOuterEntity>> responseVo) {
                        List<LQBasicsOuterEntity> entities = responseVo.getBasicConfig();
                        if (!EmptyUtil.isEmpty(entities)) {
                            // 基础课程 特殊一些
                            BasicsCourseActivity.show(OrganCourseFiltrateActivity.this, mEntity, entities);
                        }
                    }
                });
            } else {
                ClassifyListActivity.show(this, mEntity);
            }
        } else if (viewId == R.id.btn_confirm) {
            // 确认添加
            List<CourseVo> items = mNavigatorList.get(mPosition).getCourseVoList();
            ArrayList<CourseVo> selectArray = new ArrayList<>();
            for (CourseVo vo : items) {
                if (vo.isTag())
                    selectArray.add(vo);
            }
            if (selectArray.isEmpty()) {
                TipMsgHelper.ShowLMsg(this, R.string.label_please_choice_add_course);
                return;
            }
            // 发送事件
            EventBus.getDefault().post(new EventWrapper(selectArray, EventConstant.CLASS_COURSE_ADD_COURSE_EVENT));
        }  else if(viewId == R.id.btn_add_subject){
            // 点击确定
            AddSubjectActivity.show(this,true,SUBJECT_SETTING_REQUEST_CODE);
        }else if(viewId == R.id.new_cart_container){
            // 点击作业库
            handleSubjectSettingData(this,UserHelper.getUserId());
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
                    if(EmptyUtil.isNotEmpty(TaskSliderHelper.onWorkCartListener)){
                        Bundle extras = getIntent().getBundleExtra(Common.Constance.KEY_EXTRAS_STUDY_TASK);
                        String schoolId = mResourceData.getSchoolId();
                        String classId = mResourceData.getClassId();
                        TaskSliderHelper.onWorkCartListener.enterIntroTaskDetailActivity(OrganCourseFiltrateActivity.this,schoolId,classId,extras);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SEARCH_REQUEST_CODE) {
                // 更新字符串发生更新
                // 设置Top隐藏
                // mTopBar.findViewById(R.id.right_function1_image).setVisibility(View.GONE);
                mKeyString = data.getStringExtra(SearchActivity.KEY_EXTRA_SEARCH_KEYWORD);
                // 刷新数据
                triggerUpdateData();
            }else if(requestCode == SUBJECT_SETTING_REQUEST_CODE){
                // 科目设置成功的回调
                Bundle extras = data.getExtras();
                if(EmptyUtil.isNotEmpty(extras)){
                    boolean completed = extras.getBoolean(AddSubjectActivity.KEY_EXTRA_RESULT);
                    if(completed){
                        // 刷新标签和课程
                        String organId = mEntity.getEntityOrganId();
                        int parentId = mEntity.getId();
                        String level = mEntity.getLevel();
                        mPresenter.requestOrganCourseLabelData(organId,parentId,level, mLibraryType);
                    }
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventWrapper event) {
        if (EventWrapper.isMatch(event, EventConstant.COURSE_SELECT_RESOURCE_EVENT)) {
            if(EmptyUtil.isNotEmpty(mResourceData) && !mResourceData.isInitiativeTrigger()){
                ArrayList<SectionResListVo> vos = (ArrayList<SectionResListVo>) event.getData();
                setResult(Activity.RESULT_OK, new Intent().putExtra(RESULT_LIST, vos));
                // 杀掉所有可能的UI
                ActivityUtil.finishActivity(OrganCourseFiltrateActivity.class);
                ActivityUtil.finishActivity(SearchActivity.class);
                finish();
            }
        }
    }

    /**
     * 学程馆二级筛选页面的入口
     *
     * @param context            上下文对象
     * @param entity             实体数据对象
     * @param keyString          搜索条件
     * @param selectResource     是否是选择资源
     * @param isClassCourseEnter 是否是班级学程入口
     * @param data               选择学程馆资源的data
     * @param isHostEnter        是否直接从学程馆入口进来的
     */
    public static void showFromSearch(@NonNull Context context,
                                      @NonNull LQCourseConfigEntity entity,
                                      @NonNull String keyString,
                                      boolean selectResource,
                                      boolean isClassCourseEnter,
                                      @NonNull ShopResourceData data,
                                      @NonNull boolean isHostEnter) {
        Intent intent = new Intent(context, OrganCourseFiltrateActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_EXTRA_ORGAN_COURSE_ENTITY, entity);
        bundle.putString(KEY_EXTRA_SEARCH_KEY, keyString);
        bundle.putBoolean(KEY_EXTRA_ORGAN_SELECT, selectResource);
        bundle.putBoolean(KEY_EXTRA_HOST_ENTER, isHostEnter);
        bundle.putBoolean(KEY_EXTRA_CLASS_COURSE_ENTER, isClassCourseEnter);
        if (selectResource)
            bundle.putSerializable(KEY_EXTRA_RESOURCE_DATA, data);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }


    /**
     * 学程馆二级筛选页面的入口
     *
     * @param activity           上下文对象
     * @param entity             实体数据对象
     * @param selectResource     是否是选择资源
     * @param isClassCourseEnter 是否是班级学程入口
     * @param data               选择学程馆资源的data
     * @param isAuthorized       是否已经获取到授权
     * @param isReallyAuthorized 该分类是否获取到授权了
     * @param isHostEnter        是否直接从学程馆入口进来的
     * @param roles 用户角色
     * @param libraryType 学程馆类型 0 习课程馆 1练测馆  2 图书馆  3 视频馆
     */
    public static void show(@NonNull Activity activity,
                            @NonNull LQCourseConfigEntity entity,
                            boolean selectResource,
                            boolean isClassCourseEnter,
                            @NonNull ShopResourceData data,
                            boolean isAuthorized,
                            boolean isReallyAuthorized,
                            boolean isHostEnter,
                            String roles,
                            int libraryType) {
        show(activity,entity,selectResource,isClassCourseEnter,data,null,isAuthorized,
                isReallyAuthorized,isHostEnter,roles, libraryType);
    }

    /**
     * 学程馆二级筛选页面的入口
     *
     * @param activity           上下文对象
     * @param entity             实体数据对象
     * @param selectResource     是否是选择资源
     * @param isClassCourseEnter 是否是班级学程入口
     * @param data               选择学程馆资源的data
     * @param extras 布置任务直播参数
     * @param isAuthorized       是否已经获取到授权
     * @param isReallyAuthorized 该分类是否获取到授权了
     * @param isHostEnter        是否直接从学程馆入口进来的
     */
    public static void show(@NonNull Activity activity,
                            @NonNull LQCourseConfigEntity entity,
                            boolean selectResource,
                            boolean isClassCourseEnter,
                            @NonNull ShopResourceData data,
                            @Nullable Bundle extras,
                            boolean isAuthorized,
                            boolean isReallyAuthorized,
                            boolean isHostEnter,
                            String roles,
                            int libraryType) {
        Intent intent = new Intent(activity, OrganCourseFiltrateActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_EXTRA_ORGAN_COURSE_ENTITY, entity);
        bundle.putBoolean(KEY_EXTRA_ORGAN_SELECT, selectResource);
        bundle.putBoolean(KEY_EXTRA_IS_AUTHORIZED, isAuthorized);
        bundle.putBoolean(KEY_EXTRA_IS_REALLY_AUTHORIZED, isReallyAuthorized);
        bundle.putBoolean(KEY_EXTRA_HOST_ENTER, isHostEnter);
        bundle.putBoolean(KEY_EXTRA_CLASS_COURSE_ENTER, isClassCourseEnter);
        bundle.putString(KEY_EXTRA_ROLES,roles);
        bundle.putInt(KEY_EXTRA_LIBRARY_TYPE, libraryType);
        if (selectResource) {
            bundle.putSerializable(KEY_EXTRA_RESOURCE_DATA, data);
            bundle.putBundle(Common.Constance.KEY_EXTRAS_STUDY_TASK,extras);
        }
        intent.putExtras(bundle);
        if (selectResource) {
            activity.startActivityForResult(intent, data.getRequestCode());
        } else {
            activity.startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
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
}
