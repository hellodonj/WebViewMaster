
package com.lqwawa.intleducation.module.organcourse.filtrate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.CourseEmptyView;
import com.lqwawa.intleducation.base.PresenterActivity;
import com.lqwawa.intleducation.base.widgets.PullRefreshView.PullToRefreshView;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.base.widgets.adapter.TabSelectedAdapter;
import com.lqwawa.intleducation.base.widgets.adapter.TextWatcherAdapter;
import com.lqwawa.intleducation.common.utils.ActivityUtil;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.KeyboardUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.common.utils.Utils;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.LQBasicsOuterEntity;
import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;
import com.lqwawa.intleducation.factory.data.entity.response.CheckPermissionResponseVo;
import com.lqwawa.intleducation.factory.data.entity.response.LQConfigResponseVo;
import com.lqwawa.intleducation.factory.data.entity.school.CheckSchoolPermissionEntity;
import com.lqwawa.intleducation.factory.data.entity.school.SchoolInfoEntity;
import com.lqwawa.intleducation.factory.event.EventConstant;
import com.lqwawa.intleducation.factory.event.EventWrapper;
import com.lqwawa.intleducation.factory.helper.LQCourseHelper;
import com.lqwawa.intleducation.factory.helper.SchoolHelper;
import com.lqwawa.intleducation.module.discovery.adapter.CourseListAdapter;
import com.lqwawa.intleducation.module.discovery.ui.CourseDetailsActivity;
import com.lqwawa.intleducation.module.discovery.ui.ImputAuthorizationCodeDialog;
import com.lqwawa.intleducation.module.discovery.ui.coursedetail.CourseDetailParams;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.basics.BasicsCourseActivity;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.classifylist.ClassifyListActivity;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.filtrate.HideSortType;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.filtrate.SortLinePopupWindow;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.filtrate.Tab;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.home.LanguageType;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.search.SearchActivity;
import com.lqwawa.intleducation.module.discovery.ui.subject.add.AddSubjectActivity;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;
import com.lqwawa.intleducation.module.learn.vo.SectionResListVo;
import com.lqwawa.intleducation.module.organcourse.ShopResourceData;
import com.lqwawa.intleducation.module.user.tool.UserHelper;
import com.lqwawa.intleducation.module.watchcourse.WatchCourseResourceActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.lqwawa.intleducation.module.discovery.ui.CourseSelectItemFragment.RESULT_LIST;

/**
 * @author medici
 * @desc 学程馆筛选页面
 */
public class OrganCourseFiltrateActivity extends PresenterActivity<OrganCourseFiltrateContract.Presenter>
        implements OrganCourseFiltrateContract.View, CompoundButton.OnCheckedChangeListener,
        View.OnClickListener {

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

    // LQ English Primary
    private static final int ENGLISH_INTERNATIONAL_ENGLISH_PRIMARY_ID = 2011;

    // iTEP
    private static final int ENGLISH_INTERNATIONAL_ENGLISH_ITEP_ID = 2244;

    private TopBar mTopBar;

    // Tab集合
    private List<CheckBox> mSortButtons;

    // 选择容器 mLaySort4 基础课程使用
    private LinearLayout mLaySort1, mLaySort2, mLaySort3, mLaySort4;
    // 选择Button mCbSort4 基础课程使用
    private CheckBox mCbSort1, mCbSort2, mCbSort3, mCbSort4;
    // 分割线
    private View mVerticalLine1, mVerticalLine2, mVerticalLine3;

    private LinearLayout mHeaderLayout;
    private LinearLayout mTabVector1, mTabVector2, mTabVector3, mTabVector4;
    private TextView mTabLabel1, mTabLabel2, mTabLabel3, mTabLabel4;
    private TabLayout mTabLayout1, mTabLayout2, mTabLayout3, mTabLayout4;

    // 需要显示的configType
    private int mConfigType1 = 2;
    private int mConfigType2 = 3;
    private int mConfigType3 = 4;
    private int mConfigType4 = 5;

    private String allText;

    // 所有的筛选条件
    private List<Tab> mFirstTabs;
    private List<Tab> mSecondTabs;
    private List<Tab> mThirdTabs;
    private List<Tab> mFourTabs;

    // PopupWindow
    private SortLinePopupWindow mPopupWindow;

    // 搜索
    private EditText mSearchContent;
    private ImageView mSearchClear;
    private TextView mSearchFilter;

    // 刷新布局
    private PullToRefreshView mRefreshLayout;
    // 空布局
    private CourseEmptyView mEmptyLayout;
    // 列表布局
    private ListView mListView;
    // Adapter
    private CourseListAdapter mCourseListAdapter;

    private LinearLayout mBottomLayout;
    private LinearLayout mSubjectLayout;
    private Button mAddSubject;
    private Button mBtnConfirmAdd;
    private Button mBtnRequestAuthorized;
    private Button mBtnMoreCourse;
    private TextView mCircleMoreCourse;

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
    // 分页数
    private int currentPage;

    private boolean isAuthorized;
    // 是否真正的授权
    private boolean isReallyAuthorized;
    private boolean isHostEnter;
    // 机构角色信息
    private String mRoles;
    private boolean isTeacher;

    // 授权码是否过期
    private boolean isExist;
    // 是否是空Label
    private boolean isEmptyLabel;
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
        // 不是搜索页面过来的
        /*mTopBar.setRightFunctionImage1(R.drawable.search, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 筛选页面 mSortType 定死的-1;
                if(mSelectResource){
                    SearchActivity.show(
                            OrganCourseFiltrateActivity.this,
                            mEntity,
                            HideSortType.TYPE_SORT_SCHOOL_SHOP,
                            mEntity.getConfigValue(),
                            mSelectResource,
                            mResourceData,isHostEnter);
                }else{
                    SearchActivity.show(OrganCourseFiltrateActivity.this,
                            mEntity,
                            HideSortType.TYPE_SORT_SCHOOL_SHOP,
                            mEntity.getConfigValue(),
                            isHostEnter,isClassCourseEnter);
                }
            }
        });*/

        mSearchContent = (EditText) findViewById(R.id.et_search);
        mSearchClear = (ImageView) findViewById(R.id.iv_search_clear);
        mSearchFilter = (TextView) findViewById(R.id.tv_filter);

        mSearchContent.setHint(R.string.search_hit);

        mSearchFilter.setVisibility(View.VISIBLE);
        mSearchClear.setOnClickListener(this);
        mSearchFilter.setOnClickListener(this);

        // @date   :2018/6/13 0013 上午 11:31
        // @func   :V5.7改用键盘搜索的方式
        mSearchContent.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                super.onTextChanged(s, start, before, count);
                if (s.length() > 0) {
                    mSearchClear.setVisibility(View.VISIBLE);
                } else {
                    mSearchClear.setVisibility(View.INVISIBLE);
                }
                mSearchContent.setImeOptions(s.length() > 0
                        ? EditorInfo.IME_ACTION_SEARCH
                        : EditorInfo.IME_ACTION_DONE);
                mSearchContent.setMaxLines(1);
                mSearchContent.setInputType(EditorInfo.TYPE_CLASS_TEXT
                        | EditorInfo.TYPE_TEXT_FLAG_AUTO_COMPLETE
                        | EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
            }
        });

        mSearchContent.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    // 搜索，收起软件盘
                    KeyboardUtil.hideSoftInput(OrganCourseFiltrateActivity.this);
                    requestCourseData(false);
                }
                return true;
            }
        });

        mRefreshLayout = (PullToRefreshView) findViewById(R.id.refresh_layout);
        mEmptyLayout = (CourseEmptyView) findViewById(R.id.empty_layout);
        mListView = (ListView) findViewById(R.id.listView);

        mSortButtons = new ArrayList<>();
        mLaySort1 = (LinearLayout) findViewById(R.id.lay_sort1);
        mLaySort2 = (LinearLayout) findViewById(R.id.lay_sort2);
        mLaySort3 = (LinearLayout) findViewById(R.id.lay_sort3);
        mLaySort4 = (LinearLayout) findViewById(R.id.lay_sort4);
        mCbSort1 = (CheckBox) findViewById(R.id.cb_sort1);
        mCbSort2 = (CheckBox) findViewById(R.id.cb_sort2);
        mCbSort3 = (CheckBox) findViewById(R.id.cb_sort3);
        mCbSort4 = (CheckBox) findViewById(R.id.cb_sort4);
        mSortButtons.add(mCbSort1);
        mSortButtons.add(mCbSort2);
        mSortButtons.add(mCbSort3);
        mSortButtons.add(mCbSort4);
        // 添加状态改变事件
        for (CheckBox btnSort : mSortButtons) {
            btnSort.setOnCheckedChangeListener(this);
        }

        mVerticalLine1 = findViewById(R.id.split_view1);
        mVerticalLine2 = findViewById(R.id.split_view2);
        mVerticalLine3 = findViewById(R.id.split_view3);

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
                    mCircleMoreCourse.setVisibility(View.VISIBLE);
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


        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CourseVo vo = (CourseVo) mCourseListAdapter.getItem(position);

                if (!isClassCourseEnter) {
                    if (mSelectResource) {
                        if (!isAuthorized) {
                            UIUtil.showToastSafe(R.string.label_please_request_authorization);
                            return;
                        }
                        // 进入选择资源的Activity
                        WatchCourseResourceActivity.show(
                                OrganCourseFiltrateActivity.this,
                                vo.getId(),
                                mResourceData.getTaskType(),
                                mResourceData.getMultipleChoiceCount(),
                                mResourceData.getFilterArray(), 0);
                    } else {
                        // 线下机构学程馆,是从空中学校进入的 isSchoolEnter = true;
                        String organId = mEntity.getEntityOrganId();
                        // String roles = UserHelper.getUserInfo().getRoles();
                        // 这里使用 isReallyAuthorized 传值 isAuthorized可能授权的是别的分类
                        SchoolHelper.requestSchoolInfo(UserHelper.getUserId(), mEntity.getEntityOrganId(), new DataSource.Callback<SchoolInfoEntity>() {
                            @Override
                            public void onDataNotAvailable(int strRes) {
                                showError(strRes);
                            }

                            @Override
                            public void onDataLoaded(SchoolInfoEntity schoolInfoEntity) {
                                String roles = schoolInfoEntity.getRoles();
                                CourseDetailParams params = new CourseDetailParams(isReallyAuthorized, organId, roles);
                                CourseDetailsActivity.start(isReallyAuthorized, params, true, OrganCourseFiltrateActivity.this, vo.getId(), true, UserHelper.getUserId());
                            }
                        });
                    }
                } else {
                    // 班级学程的入口进来的，控制选择
                    vo.setTag(!vo.isTag());
                    mCourseListAdapter.notifyDataSetChanged();

                    // 查看有无学程选择,没有学程选择，按钮不许点击
                    List<CourseVo> items = mCourseListAdapter.getItems();
                    for (CourseVo courseVo : items) {
                        mBtnConfirmAdd.setEnabled(false);
                        if (courseVo.isTag()) {
                            // 有选中的,设置enable
                            mBtnConfirmAdd.setEnabled(true);
                            break;
                        }
                    }
                }
            }
        });

        // 下拉刷新
        mRefreshLayout.setLastUpdated(new Date().toLocaleString());
        mRefreshLayout.showRefresh();
        mRefreshLayout.setOnHeaderRefreshListener(new PullToRefreshView.OnHeaderRefreshListener() {
            @Override
            public void onHeaderRefresh(PullToRefreshView view) {
                requestCourseData(false);
            }
        });

        mRefreshLayout.setOnFooterRefreshListener(new PullToRefreshView.OnFooterRefreshListener() {
            @Override
            public void onFooterRefresh(PullToRefreshView view) {
                requestCourseData(true);
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        allText = getString(R.string.label_course_filtrate_all);
        // 初始化筛选数组
        mFirstTabs = new ArrayList<>();
        mSecondTabs = new ArrayList<>();
        mThirdTabs = new ArrayList<>();
        mFourTabs = new ArrayList<>();


        String organId = mEntity.getEntityOrganId();
        int parentId = mEntity.getId();
        String level = mEntity.getLevel();
        mPresenter.requestOrganCourseLabelData(organId, parentId, level);

        if (!isClassCourseEnter) {

            if (mSelectResource) {
                // 选择资源检查授权 自动申请
                mPresenter.requestCheckSchoolPermission(organId, 0, true);
            } else {
                // 手动授权,检查授权
                mPresenter.requestCheckSchoolPermission(organId, 0, false);
            }
        }
    }

    /**
     * 触发更新
     */
    private void triggerUpdateData() {
        if (EmptyUtil.isNotEmpty(mAllLabels)) {
            clearConfigArrayStatus(mAllLabels);
        }
        requestCourseData(false);
    }

    /**
     * 根据筛选条件,查询课程
     */
    public void requestCourseData(boolean isMoreLoaded) {
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

        /*for (Tab tab:mFirstTabs) {
            if(tab.isSelected()){
                level = tab.getLevel();
            }
        }

        for (Tab tab:mSecondTabs) {
            if(tab.isSelected()){
                paramOneId = tab.getLabelId();
            }
        }

        for (Tab tab:mThirdTabs) {
            if(tab.isSelected()){
                paramTwoId = tab.getLabelId();
            }
        }

        for (Tab tab:mFourTabs) {
            if(tab.isSelected()){
                paramThreeId = tab.getLabelId();
            }
        }*/

        // mKeyString = mSearchContent.getText().toString().trim();
        if (EmptyUtil.isEmpty(mKeyString)) mKeyString = "";
        // 重新加载数据
        // 重新设置状态
        mBtnConfirmAdd.setEnabled(false);

        if (isMoreLoaded) {
            currentPage++;
        } else {
            currentPage = 0;
        }

        if (mSelectResource) {
            level = mEntity.getLevel();
            mPresenter.requestCourseResourceData(isMoreLoaded, mEntity.getEntityOrganId(), currentPage, AppConfig.PAGE_SIZE, mKeyString, level);
        } else {
            mPresenter.requestCourseData(isMoreLoaded, mEntity.getEntityOrganId(), currentPage, AppConfig.PAGE_SIZE, mKeyString, level, paramOneId, paramTwoId, paramThreeId);
        }

    }

    @Override
    public void updateOrganCourseLabelView(@NonNull List<LQCourseConfigEntity> entities) {
        mAllLabels = entities;
        if (EmptyUtil.isNotEmpty(entities)) {
            mFiltrateArray1 = new ArrayList<>();
            mFiltrateArray2 = new ArrayList<>();
            mFiltrateArray3 = new ArrayList<>();
            mFiltrateArray4 = new ArrayList<>();

            if (EmptyUtil.isEmpty(entities)) return;
            recursionConfig(entities);

            configLabel();
            // 设置TabLayout相关监听
            initTabListener();
            // 设置数据到TabLayout上
            initTabControl();
            // 设置第一个选中
            // mTabLayout1.getTabAt(0).select();
            // mTabLayout1.getTabAt(0).getCustomView().setSelected(true);
        }else{
            // 数据为空
            mHeaderLayout.setVisibility(View.GONE);
            mRefreshLayout.setVisibility(View.GONE);
            mEmptyLayout.setVisibility(View.VISIBLE);
        }



        /*if(EmptyUtil.isNotEmpty(entities)){
            fillAllTabs(entities,0,false);
            fillTopFiltrateText(mEntity,false);
            requestCourseData(false);
        }else{
            isEmptyLabel = true;
            // 标签为空，这一般都是服务器返回错误的问题
            Tab firstAllTab = Tab.buildAll(allText);
            Tab secondAllTab = Tab.buildAll(allText);
            if(!mFirstTabs.contains(secondAllTab) && (mFirstTabs.size() == 0 || mFirstTabs.size() > 1)){
                firstAllTab.setSelected(true);
                mFirstTabs.add(0,firstAllTab);
            }
            if(!mSecondTabs.contains(secondAllTab) && (mSecondTabs.size() == 0 || mSecondTabs.size() > 1)){
                secondAllTab.setSelected(true);
                mSecondTabs.add(0,secondAllTab);
            }


            fillTopFiltrateText(mEntity,true);
            requestCourseData(false);
        }*/
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
                    if (firstId == ENGLISH_INTERNATIONAL_ENGLISH_PRIMARY_ID) {
                        // 选中了英语国际课程的 LQ English Primary
                        mTabVector3.setVisibility(View.VISIBLE);
                        mTabLabel2.setText(getString(R.string.label_colon_type));
                    } else if (firstId == ENGLISH_INTERNATIONAL_ENGLISH_ITEP_ID) {
                        // 选中了英语国际课程的 iTEP
                        mTabVector3.setVisibility(View.VISIBLE);
                        mTabLabel2.setText(getString(R.string.label_colon_subject));
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
                // 所有标签都会发生二级，甚至三级联动
                /*TabLayout.Tab tabAt = mTabLayout1.getTabAt(mTabLayout1.getSelectedTabPosition());
                if(EmptyUtil.isNotEmpty(tabAt)){
                    if(tabData.isAll()){
                        List<LQCourseConfigEntity> entities = new ArrayList<>();
                        for (Tab item:mFiltrateArray2) {
                            if(!item.isAll() && EmptyUtil.isNotEmpty(item.getChildList())){
                                entities.addAll(item.getChildList());
                            }
                        }
                        tabData.setChildList(entities);
                    }

                    // 重新配置34数据的联动效果
                    clearArray(mConfigType3);
                    recursionConfigArray(tabData.getChildList());
                    assembleAllLabel();
                    initTabControl3();
                    initTabControl4();
                }*/

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
    public void onCourseLoaded(List<CourseVo> courseVos) {
        // 判断有无更多数据,打开或者关闭加载更多
        mRefreshLayout.onHeaderRefreshComplete();
        mRefreshLayout.setLoadMoreEnable(courseVos.size() >= AppConfig.PAGE_SIZE);
        // 设置数据
        mCourseListAdapter = new CourseListAdapter(this, isClassCourseEnter);
        mCourseListAdapter.setData(courseVos);
        mListView.setAdapter(mCourseListAdapter);

        if (EmptyUtil.isEmpty(courseVos)) {
            // 数据为空
            mRefreshLayout.setVisibility(View.GONE);
            mEmptyLayout.setVisibility(View.VISIBLE);
        } else {
            // 数据不为空
            mRefreshLayout.setVisibility(View.VISIBLE);
            mEmptyLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onMoreCourseLoaded(List<CourseVo> courseVos) {
        // 关闭加载更多
        mRefreshLayout.onFooterRefreshComplete();
        mRefreshLayout.setLoadMoreEnable(courseVos.size() >= AppConfig.PAGE_SIZE);
        // 设置数据
        mCourseListAdapter.addData(courseVos);
        mCourseListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCourseResourceLoaded(List<CourseVo> courseVos) {
        onCourseLoaded(courseVos);
    }

    @Override
    public void onMoreCourseResourceLoaded(List<CourseVo> courseVos) {
        onMoreCourseLoaded(courseVos);
    }

    @Override
    public void updateCheckPermissionView(@NonNull CheckSchoolPermissionEntity entity, boolean autoRequest) {
        if (EmptyUtil.isNotEmpty(entity)) {

            if (entity.isAuthorized()) {
                // 可能填的验证码是授权别的分类
                refreshAuthorizedInfo(entity);

                // 已经获取授权,并且没有失效
                isAuthorized = true;
                isExist = entity.isExist();
                // UIUtil.showToastSafe(R.string.label_old_request_authorization);
            } else {
                if (autoRequest) {
                    // 点击获取授权
                    /*if(entity.isExist()){
                        // 授权过期的状态
                        UIUtil.showToastSafe(R.string.authorization_out_time_tip);
                    }*/
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
        if (TextUtils.equals(rightValue, "0")) isReallyAuthorized = true;
        String[] values = rightValue.split(",");
        if (EmptyUtil.isNotEmpty(values)) {
            List<String> strings = Arrays.asList(values);
            if (strings.contains(Integer.toString(mEntity.getId()))) {
                isReallyAuthorized = true;
            }
        }
    }

    /**
     * @param entities  点击的分类childList
     * @param rootIndex 第一个标签显示的索引 -1 代表点击的其它标签
     * @param refresh   是否是点击刷新
     * @return 返回第一个Label的文本
     * @desc 转换生成所有Tabs
     */
    private void fillAllTabs(@NonNull List<LQCourseConfigEntity> entities, int rootIndex, boolean refresh) {
        if (!refresh) {
            mFirstTabs.clear();
            mSecondTabs.clear();
            mThirdTabs.clear();
            mFourTabs.clear();
        }

        // 基础课程 小学 年级 科目 出版社
        if (EmptyUtil.isNotEmpty(entities)) {
            for (int index = 0; index < entities.size(); index++) {
                LQCourseConfigEntity entity = entities.get(index);
                if (entity.getConfigType() == mConfigType1 && !refresh) {
                    Tab tab = Tab.build(entity);
                    // 将下级标签set进去
                    tab.setChildList(entity.getChildList());
                    mFirstTabs.add(tab);
                    if (rootIndex == mFirstTabs.size() - 1) {
                        tab.setSelected(true);
                    }
                }

                if (entity.getConfigType() == mConfigType4) {
                    Tab tab = Tab.build(entity);
                    mFourTabs.add(tab);
                }

            }
        }

        // 第二个数据根据第一个数据联动
        // 找到点击的索引
        Tab mFirstSelectedTab = mFirstTabs.get(rootIndex);
        List<LQCourseConfigEntity> secondLabelList = mFirstSelectedTab.getChildList();

        if (mFirstSelectedTab.getId() != CHARACTER_ENGLISH_ID) {
            // 选中的不是特色英语Id
            for (LQCourseConfigEntity secondEntity : secondLabelList) {
                if (secondEntity.getConfigType() == mConfigType2) {
                    Tab secondTab = Tab.build(secondEntity);
                    mSecondTabs.add(secondTab);

                }

                if (secondEntity.getConfigType() == mConfigType3) {
                    Tab secondTab = Tab.build(secondEntity);
                    mThirdTabs.add(secondTab);
                }

                if (secondEntity.getConfigType() == mConfigType4) {
                    Tab secondTab = Tab.build(secondEntity);
                    mFourTabs.add(secondTab);
                }
            }
        } else {
            // 选中的是特色英语的Id
            for (LQCourseConfigEntity secondEntity : secondLabelList) {
                if (secondEntity.getConfigType() == mConfigType2 && !refresh) {
                    Tab secondTab = Tab.build(secondEntity);
                    secondTab.setChildList(secondEntity.getChildList());
                    mSecondTabs.add(secondTab);
                }
            }

            // 找出选择的Tab
            Tab mSecondSelectedTab = null;
            for (Tab tab : mSecondTabs) {
                if (tab.isSelected()) {
                    mSecondSelectedTab = tab;
                    break;
                }
            }
            if (mSecondSelectedTab.isAll() && mSecondSelectedTab.isSelected()) {
                // 没有选择,显示全部
                for (LQCourseConfigEntity secondEntity : secondLabelList) {
                    for (LQCourseConfigEntity thirdEntity : secondEntity.getChildList()) {
                        if (thirdEntity.getConfigType() == mConfigType3) {
                            Tab thirdTab = Tab.build(thirdEntity);
                            if (!mThirdTabs.contains(thirdTab)) {
                                mThirdTabs.add(thirdTab);
                            }
                        }
                    }
                }

            } else {
                // 已经选择 找到选择的Tab
                for (LQCourseConfigEntity thirdEntity : mSecondSelectedTab.getChildList()) {
                    if (thirdEntity.getConfigType() == mConfigType3 && thirdEntity.getParentId() == mSecondSelectedTab.getId()) {
                        Tab thirdTab = Tab.build(thirdEntity);
                        if (!mThirdTabs.contains(thirdTab)) {
                            mThirdTabs.add(thirdTab);
                        }
                    }
                }
            }
        }


        // 第二个筛选加入全部
        // V5.9版本末更改需求
        // 如果其它节点下,只有一个数据，那么不显示全部
        Tab secondAllTab = Tab.buildAll(allText);
        if (!mSecondTabs.contains(secondAllTab) && (mSecondTabs.size() == 0 || mSecondTabs.size() > 1)) {
            secondAllTab.setSelected(true);
            mSecondTabs.add(0, secondAllTab);
        } else {
            // 取出第一个Tab,设置为选择
            mSecondTabs.get(0).setSelected(true);
        }

        // 第三个筛选加入全部
        Tab thirdAllTab = Tab.buildAll(allText);
        if (!mThirdTabs.contains(thirdAllTab) && (mThirdTabs.size() == 0 || mThirdTabs.size() > 1)) {
            thirdAllTab.setSelected(true);
            mThirdTabs.add(0, thirdAllTab);
        } else {
            // 取出第一个Tab,设置为选择
            mThirdTabs.get(0).setSelected(true);
        }
        // 第四个筛选加入全部
        Tab fourAllTab = Tab.buildAll(allText);
        if (!mFourTabs.contains(fourAllTab) && (mFourTabs.size() == 0 || mFourTabs.size() > 1)) {
            fourAllTab.setSelected(true);
            mFourTabs.add(0, fourAllTab);
        } else {
            // 取出第一个Tab,设置为选择
            mFourTabs.get(0).setSelected(true);
        }
    }

    /**
     * 设置标题文本
     *
     * @param entity Label数据
     * @param empty  是否是空数据
     * @Desc 上一级页面的标签 英语国际课程，英语国内课程，小语种课程，基础课程
     */
    private void fillTopFiltrateText(@NonNull LQCourseConfigEntity entity, boolean empty) {

        if (!empty) {
            for (Tab tab : mFirstTabs) {
                if (tab.isSelected()) {
                    // 基础课程 小学 年级 科目 出版社
                    mLaySort1.setVisibility(View.VISIBLE);
                    mCbSort1.setText(tab.getConfigValue());
                    mVerticalLine1.setVisibility(View.VISIBLE);
                }
            }
        } else {
            mLaySort1.setVisibility(View.VISIBLE);
            mCbSort1.setText(R.string.type);
            mVerticalLine1.setVisibility(View.VISIBLE);
        }


        mTabLabel1.setText(R.string.type);

        // 设置文本
        if (entity.getId() == ENGLISH_INTERNATIONAL_COURSE) {
            // 英语国际课程
            mLaySort2.setVisibility(View.VISIBLE);
            mCbSort2.setText(R.string.course_subject);

            mTabLabel2.setText(R.string.course_subject);
        } else if (entity.getId() == ENGLISH_INLAND_COURSE) {
            // 英语国内课程
            mLaySort2.setVisibility(View.VISIBLE);
            mCbSort2.setText(R.string.grade);
            mVerticalLine2.setVisibility(View.VISIBLE);

            mTabLabel2.setText(R.string.grade);

            mLaySort3.setVisibility(View.VISIBLE);
            mCbSort3.setText(R.string.course_subject);

            mTabLabel3.setText(R.string.course_subject);
            mTabLayout3.setVisibility(View.VISIBLE);
        } else if (entity.getId() == CHARACTERISTICS_ENGLISH) {
            // 特色英语课程
            mLaySort2.setVisibility(View.VISIBLE);
            mCbSort2.setText(R.string.grade);
            mVerticalLine2.setVisibility(View.VISIBLE);

            mTabLabel2.setText(R.string.grade);

            mLaySort3.setVisibility(View.VISIBLE);
            mCbSort3.setText(R.string.course_subject);

            mTabLabel3.setText(R.string.course_subject);
            mTabLayout3.setVisibility(View.VISIBLE);
        } else if (entity.getId() == MINORITY_LANGUAGE_COURSE) {
            // 小语种课程
            mLaySort2.setVisibility(View.VISIBLE);
            mCbSort2.setText(R.string.label_level);

            mTabLabel2.setText(R.string.label_level);
        } else if (entity.getId() == BASIC_COURSE) {
            // 基础课程
            mLaySort2.setVisibility(View.VISIBLE);
            mCbSort2.setText(R.string.grade);
            mVerticalLine2.setVisibility(View.VISIBLE);

            mTabLabel2.setText(R.string.grade);

            mLaySort3.setVisibility(View.VISIBLE);
            mCbSort3.setText(R.string.course_subject);
            mVerticalLine3.setVisibility(View.VISIBLE);

            mTabLabel3.setText(R.string.course_subject);
            mTabLayout3.setVisibility(View.VISIBLE);

            // 出版社
            mLaySort4.setVisibility(View.VISIBLE);
            mCbSort4.setText(R.string.book_concern);


            mTabLabel4.setText(R.string.book_concern);
            mTabLayout4.setVisibility(View.VISIBLE);
        }

        for (Tab tab : mFirstTabs) {
            if (!tab.isAll() && tab.isSelected()) {
                mCbSort1.setText(tab.getConfigValue());
            }
        }

        for (Tab tab : mSecondTabs) {
            if (!tab.isAll() && tab.isSelected()) {
                mCbSort2.setText(tab.getConfigValue());
            }
        }

        for (Tab tab : mThirdTabs) {
            if (!tab.isAll() && tab.isSelected()) {
                mCbSort3.setText(tab.getConfigValue());
            }
        }

        for (Tab tab : mFourTabs) {
            if (!tab.isAll() && tab.isSelected()) {
                mCbSort4.setText(tab.getConfigValue());
            }
        }
    }

    @Override
    public void showError(int str) {
        super.showError(str);
        mRefreshLayout.onHeaderRefreshComplete();
        mRefreshLayout.onFooterRefreshComplete();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int viewId = buttonView.getId();
        List<Tab> tabs = doCheckButton(viewId);
        if (EmptyUtil.isEmpty(tabs)) return;
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
            return;
        }

        if (!isChecked) return;

        SortLinePopupWindow mPopupWindow = new SortLinePopupWindow(this, buttonView, tabs, new SortLinePopupWindow.PopupWindowListener() {
            @Override
            public void onItemClickListener(View buttonView, int position, Tab tab) {
                int viewId = buttonView.getId();
                if (viewId == R.id.cb_sort1) {
                    int rootIndex = mFirstTabs.indexOf(tab);
                    if (EmptyUtil.isNotEmpty(mAllLabels)) {
                        fillAllTabs(mAllLabels, rootIndex, false);
                    }
                } else if (viewId == R.id.cb_sort2) {
                    clearPopupTabs(mSecondTabs);
                    Tab selectedTab = null;
                    for (Tab firstTab : mFirstTabs) {
                        if (firstTab.isSelected()) {
                            selectedTab = firstTab;
                        }
                    }
                    if (selectedTab.getId() == CHARACTER_ENGLISH_ID) {
                        // 点击的是特色英语
                        int rootIndex = mFirstTabs.indexOf(selectedTab);
                        if (EmptyUtil.isNotEmpty(mAllLabels)) {
                            clearPopupTabs(mSecondTabs);
                            tab.setSelected(true);
                            mThirdTabs.clear();
                            fillAllTabs(mAllLabels, rootIndex, true);
                        }
                    } else {
                        // 不是特色英语
                        clearPopupTabs(mSecondTabs);
                        tab.setSelected(true);
                    }
                } else if (viewId == R.id.cb_sort3) {
                    clearPopupTabs(mThirdTabs);
                    tab.setSelected(true);
                } else if (viewId == R.id.cb_sort4) {
                    clearPopupTabs(mFourTabs);
                    tab.setSelected(true);
                }

                fillTopFiltrateText(mEntity, isEmptyLabel);

                mRefreshLayout.showRefresh();
                requestCourseData(false);
            }

            @Override
            public void onDismissListener() {
                buttonView.setChecked(false);
            }
        });
        // 弹出筛选框
        mPopupWindow.showPopupWindow(buttonView, true);
    }

    /**
     * 初始化选中的Tabs
     */
    private void clearPopupTabs(@NonNull List<Tab> tabs) {
        for (Tab tab : tabs) {
            tab.setSelected(false);
        }
    }

    /**
     * 获取Tabs数据源
     */
    private List<Tab> doCheckButton(int viewId) {
        // 获取到Tab数据
        if (viewId == R.id.cb_sort1) {
            return mFirstTabs;
        } else if (viewId == R.id.cb_sort2) {
            return mSecondTabs;
        } else if (viewId == R.id.cb_sort3) {
            return mThirdTabs;
        } else if (viewId == R.id.cb_sort4) {
            return mFourTabs;
        }

        return null;
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
            // 更多课程
            // Intent broadIntent = new Intent();
            // broadIntent.setAction(OrganCourseClassifyActivity.ACTION_MORE_COURSE_ENTER);
            // sendBroadcast(broadIntent);
            // LQ学程的二级页面
            if (mEntity.getId() == BASIC_COURSE) {
                // 获取最新的基础课程信息
                // 获取中英文数据
                int languageRes = Utils.isZh(UIUtil.getContext()) ? LanguageType.LANGUAGE_CHINESE : LanguageType.LANGUAGE_OTHER;
                // 获取分类数据 英语国际课程,英语国内课程 等 获取第一级别
                /*LQCourseHelper.requestLQBasicsConfigData(languageRes, new DataSource.Callback<List<LQBasicsOuterEntity>>() {
                    @Override
                    public void onDataNotAvailable(int strRes) {
                        // 重要的数据发生异常了，才弹提示
                        UIUtil.showToastSafe(strRes);
                    }

                    @Override
                    public void onDataLoaded(List<LQBasicsOuterEntity> entities) {
                        if(!EmptyUtil.isEmpty(entities)){
                            // 基础课程 特殊一些
                            BasicsCourseActivity.show(OrganCourseFiltrateActivity.this,mEntity,entities);
                        }
                    }
                });*/

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
            List<CourseVo> items = mCourseListAdapter.getItems();
            ArrayList<CourseVo> selectArray = new ArrayList<>();
            for (CourseVo vo : items) {
                if (vo.isTag())
                    selectArray.add(vo);
            }

            // 发送事件
            EventBus.getDefault().post(new EventWrapper(selectArray, EventConstant.CLASS_COURSE_ADD_COURSE_EVENT));
        } else if (viewId == R.id.tv_filter) {
            // 搜索 兼容其它平板问题，收起软件盘
            KeyboardUtil.hideSoftInput(this);
            requestCourseData(false);
        } else if (viewId == R.id.iv_search_clear) {
            // 删除关键字
            mSearchContent.getText().clear();
            requestCourseData(false);
        } else if (viewId == R.id.et_search) {
            // 点击搜索框
        }else if(viewId == R.id.btn_add_subject){
            // 点击确定
            AddSubjectActivity.show(this,true,SUBJECT_SETTING_REQUEST_CODE);
        }
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
                        mPresenter.requestOrganCourseLabelData(organId,parentId,level);
                    }
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventWrapper event) {
        if (EventWrapper.isMatch(event, EventConstant.COURSE_SELECT_RESOURCE_EVENT)) {
            ArrayList<SectionResListVo> vos = (ArrayList<SectionResListVo>) event.getData();
            setResult(Activity.RESULT_OK, new Intent().putExtra(RESULT_LIST, vos));
            // 杀掉所有可能的UI
            ActivityUtil.finishActivity(OrganCourseFiltrateActivity.class);
            ActivityUtil.finishActivity(SearchActivity.class);
            finish();
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
     */
    public static void show(@NonNull Activity activity,
                            @NonNull LQCourseConfigEntity entity,
                            boolean selectResource,
                            boolean isClassCourseEnter,
                            @NonNull ShopResourceData data,
                            boolean isAuthorized,
                            boolean isReallyAuthorized,
                            boolean isHostEnter,
                            String roles) {
        Intent intent = new Intent(activity, OrganCourseFiltrateActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_EXTRA_ORGAN_COURSE_ENTITY, entity);
        bundle.putBoolean(KEY_EXTRA_ORGAN_SELECT, selectResource);
        bundle.putBoolean(KEY_EXTRA_IS_AUTHORIZED, isAuthorized);
        bundle.putBoolean(KEY_EXTRA_IS_REALLY_AUTHORIZED, isReallyAuthorized);
        bundle.putBoolean(KEY_EXTRA_HOST_ENTER, isHostEnter);
        bundle.putBoolean(KEY_EXTRA_CLASS_COURSE_ENTER, isClassCourseEnter);
        bundle.putString(KEY_EXTRA_ROLES,roles);
        if (selectResource)
            bundle.putSerializable(KEY_EXTRA_RESOURCE_DATA, data);
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
}
