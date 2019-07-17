package com.lqwawa.intleducation.module.organcourse;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.PresenterActivity;
import com.lqwawa.intleducation.base.utils.DisplayUtil;
import com.lqwawa.intleducation.base.widgets.NoPermissionView;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.base.widgets.adapter.PagerChangedAdapter;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.common.utils.ActivityUtil;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.common.utils.Utils;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;
import com.lqwawa.intleducation.factory.data.entity.response.CheckPermissionResponseVo;
import com.lqwawa.intleducation.factory.data.entity.school.CheckSchoolPermissionEntity;
import com.lqwawa.intleducation.factory.data.entity.school.SchoolInfoEntity;
import com.lqwawa.intleducation.factory.event.EventConstant;
import com.lqwawa.intleducation.factory.event.EventWrapper;
import com.lqwawa.intleducation.factory.helper.OrganCourseHelper;
import com.lqwawa.intleducation.factory.helper.SchoolHelper;
import com.lqwawa.intleducation.module.discovery.ui.CourseDetailsActivity;
import com.lqwawa.intleducation.module.discovery.ui.CourseSelectItemFragment;
import com.lqwawa.intleducation.module.discovery.ui.ImputAuthorizationCodeDialog;
import com.lqwawa.intleducation.module.discovery.ui.coursedetail.CourseDetailParams;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.home.LanguageType;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.search.SearchActivity;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;
import com.lqwawa.intleducation.module.learn.vo.SectionResListVo;
import com.lqwawa.intleducation.module.organcourse.filtrate.NewOrganCourseFiltrateActivity;
import com.lqwawa.intleducation.module.organcourse.pager.CourseClassifyPagerFragment;
import com.lqwawa.intleducation.module.user.tool.UserHelper;
import com.lqwawa.intleducation.module.watchcourse.WatchCourseResourceActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.lqwawa.intleducation.module.discovery.ui.CourseSelectItemFragment.RESULT_LIST;

/**
 * @author medici
 * @desc 实体机构学程馆页面
 */
public class OrganCourseClassifyActivity extends PresenterActivity<OrganCourseClassifyContract.Presenter>
        implements OrganCourseClassifyContract.View, View.OnClickListener {

    public static final String ACTION_MORE_COURSE_ENTER = "ACTION_MORE_COURSE_ENTER";

    // 一级页面分类数据
    private static final String KEY_EXTRA_ORGAN_COURSE_OBJECT = "KEY_EXTRA_ORGAN_COURSE_OBJECT";
    // 机构Id
    private static final String KEY_EXTRA_ORGAN_ID = "KEY_EXTRA_ORGAN_ID";
    // 是否是选择资源Tab
    private static final String KEY_EXTRA_ORGAN_SELECT_RESOURCE = "KEY_EXTRA_ORGAN_SELECT_RESOURCE";
    // 学程馆选择资源的数据
    private static final String KEY_EXTRA_ORGAN_RESOURCE_DATA = "KEY_EXTRA_ORGAN_RESOURCE_DATA";
    // 角色信息
    private static final String KEY_EXTRA_ROLES = "KEY_EXTRA_ROLES";
    // 学程馆类型
    private static final String KEY_EXTRA_LIBRARY_TYPE = "KEY_EXTRA_LIBRARY_TYPE";
    // 每页的分类Tab数目
    private static final int PAGER_TAB_COUNT = 4;

    private TopBar mTopBar;
    private ViewPager mViewPager;
    private LinearLayout mViewPagerIndicator;
    private RecyclerView mClassifyRecycler;
    private CourseClassifyAdapter mClassifyAdapter;
    private ScrollView mContentLayout;
    private NoPermissionView mEmptyView;

    // 更多课程
    private TextView mTvMoreCourse;

    private List<LQCourseConfigEntity> mConfigEntities;
    private HolderPagerAdapter mPagerAdapter;
    private List<Fragment> mPagerFragments;

    private boolean mSelectResource;
    private ShopResourceData mResourceData;
    private String mRoles;
    private int mLibraryType;
    private String[] mLibraryNames;
    private boolean isTeacher;

    // 是否获取到授权
    private String mSchoolId;
    // 授权信息
    private CheckSchoolPermissionEntity mPermissionEntity;
    private boolean isAuthorized;
    // 授权码是否过期
    private boolean isExist;
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
    protected OrganCourseClassifyContract.Presenter initPresenter() {
        return new OrganCourseClassifyPresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_organ_course_classify;
    }

    @Override
    protected boolean initArgs(@NonNull Bundle bundle) {
        Serializable serializable = bundle.getSerializable(KEY_EXTRA_ORGAN_COURSE_OBJECT);
        if (EmptyUtil.isNotEmpty(serializable)) {
            mConfigEntities = (List<LQCourseConfigEntity>) bundle.getSerializable(KEY_EXTRA_ORGAN_COURSE_OBJECT);
        }
        mSelectResource = bundle.getBoolean(KEY_EXTRA_ORGAN_SELECT_RESOURCE);
        mSchoolId = bundle.getString(KEY_EXTRA_ORGAN_ID);
        if (mSelectResource)
            mResourceData = (ShopResourceData) bundle.getSerializable(KEY_EXTRA_ORGAN_RESOURCE_DATA);
        mRoles = bundle.getString(KEY_EXTRA_ROLES);
        mLibraryType = bundle.getInt(KEY_EXTRA_LIBRARY_TYPE);
        isTeacher = UserHelper.isTeacher(mRoles);
        mLibraryNames = getResources().getStringArray(R.array.organ_library_names);
        if (mSelectResource && EmptyUtil.isEmpty(mResourceData)) return false;
        if (EmptyUtil.isEmpty(mSchoolId)) return false;
        return super.initArgs(bundle);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mTopBar = (TopBar) findViewById(R.id.top_bar);
        mTopBar.setBack(true);
        if (mLibraryType >= OrganLibraryType.TYPE_LQCOURSE_SHOP
                && mLibraryType <= OrganLibraryType.TYPE_TEACHING_PLAN) {
            mTopBar.setTitle(mLibraryNames[mLibraryType] );
        }
        mTopBar.setTitleColor(R.color.colorDark);
        mContentLayout = (ScrollView) findViewById(R.id.lay_content);
        mEmptyView = (NoPermissionView) findViewById(R.id.empty_view);
        mTvMoreCourse = (TextView) findViewById(R.id.tv_more_course);

        // mTopBar.setRightFunctionText1TextColor(R.color.colorAccent);
        if (!mSelectResource) {
            View.OnClickListener onClickListener = v -> {
                    // 点击获取授权
                    if (isAuthorized) {
                        // 已经获取到授权
                        UIUtil.showToastSafe(R.string.label_request_authorization_succeed);
                        return;
                    }
                    requestAuthorizedPermission(isExist);
                };
            mTopBar.setRightFunctionText1(R.string.label_request_authorization, onClickListener);
        }

        if (!mSelectResource) {
            mTvMoreCourse.setVisibility(mLibraryType == OrganLibraryType.TYPE_LQCOURSE_SHOP ?
                    View.VISIBLE : View.GONE);
            mTvMoreCourse.setOnClickListener(this);
        } else {
            mTvMoreCourse.setVisibility(View.GONE);
        }

        if (EmptyUtil.isEmpty(mConfigEntities)) {
            // 没有任何权限数据 显示空页面
            mEmptyView.setDescription(getString(R.string.label_organ_course_permission_description, mLibraryNames[mLibraryType]));
            mEmptyView.setVisibility(View.VISIBLE);
            mContentLayout.setVisibility(View.GONE);
        } else {
            mEmptyView.setVisibility(View.GONE);
            mContentLayout.setVisibility(View.VISIBLE);
        }

        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mViewPagerIndicator = (LinearLayout) findViewById(R.id.indicator_layout);

        mClassifyRecycler = (RecyclerView) findViewById(R.id.recycler);
        mClassifyRecycler.setNestedScrollingEnabled(false);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        mClassifyRecycler.setLayoutManager(mLayoutManager);
        mClassifyAdapter = new CourseClassifyAdapter(new CourseClassifyNavigator() {
            @Override
            public void onClickConfigTitleLayout(@NonNull LQCourseConfigEntity entity) {
                /*if(mSelectResource && !isAuthorized){
                    UIUtil.showToastSafe(R.string.label_please_request_authorization);
                    return;
                }*/

                // 获取该分类是否获取到授权
                boolean reallyAuthorized = judgeClassifyAuthorizedInfo(entity);
                NewOrganCourseFiltrateActivity.show(OrganCourseClassifyActivity.this, entity,
                        mSelectResource, false, mResourceData, isAuthorized, reallyAuthorized, false,
                        mRoles, mLibraryType);
            }

            @Override
            public void onClickCourse(@NonNull LQCourseConfigEntity entity, @NonNull CourseVo courseVo) {
                if (mSelectResource) {
                    if (!isAuthorized) {
                        UIUtil.showToastSafe(R.string.label_please_request_authorization);
                        return;
                    }

                    // 进入选择资源的Activity
                    WatchCourseResourceActivity.show(
                            OrganCourseClassifyActivity.this,
                            courseVo.getId(),
                            mResourceData.getTaskType(),
                            mResourceData.getMultipleChoiceCount(),
                            mResourceData.getFilterArray(),
                            mResourceData.isInitiativeTrigger(),
                            null,
                            mResourceData.getSchoolId(),
                            mResourceData.getClassId(),
                            mResourceData.getEnterType(),
                            0);
                } else {
                    // 获取该分类是否获取到授权
                    boolean reallyAuthorized = judgeClassifyAuthorizedInfo(entity);
                    // 进入课程详情
                    // 线下机构学程馆,是从空中学校进入的 isSchoolEnter = true;
                    // String roles = UserHelper.getUserInfo().getRoles();
//                    mSchoolId = "5e069b1a-9d90-49ed-956c-946e9f934b68";
                    SchoolHelper.requestSchoolInfo(UserHelper.getUserId(), mSchoolId, new DataSource.Callback<SchoolInfoEntity>() {
                        @Override
                        public void onDataNotAvailable(int strRes) {
                            showError(strRes);
                        }

                        @Override
                        public void onDataLoaded(SchoolInfoEntity schoolInfoEntity) {
                            String roles = schoolInfoEntity.getRoles();
                            CourseDetailParams params = new CourseDetailParams(reallyAuthorized, mSchoolId, roles);
                            // 传递该分类下，是否真的授权了
                            CourseDetailsActivity.start(OrganCourseClassifyActivity.this,
                                    courseVo.getId(), true, UserHelper.getUserId(), reallyAuthorized, params, true);
                        }
                    });
                }
            }
        });
        mClassifyRecycler.setAdapter(mClassifyAdapter);

    }

    @Override
    protected void initData() {
        super.initData();
        // 如果一个学程馆权限都没有，置空
        if (EmptyUtil.isEmpty(mConfigEntities)) mConfigEntities = new ArrayList<>();

        updateLibraryType();

        // 初始化Pager
        initHeaderPager();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 页面显示的时候调用
        if (mSelectResource) {
            // 选择资源检查授权 自动申请
            mPresenter.requestCheckSchoolPermission(mSchoolId, 0, true);
        } else {
            // 手动授权,检查授权
            mPresenter.requestCheckSchoolPermission(mSchoolId, 0, false);
        }
    }

    private void updateLibraryType() {
        if (mConfigEntities != null && !mConfigEntities.isEmpty()) {
            for (LQCourseConfigEntity entity : mConfigEntities) {
                if (entity != null) {
                    entity.setLibraryType(mLibraryType);
                }
            }
        }
    }

    /**
     * 初始化学程馆顶部的分类Pager
     */
    private void initHeaderPager() {
        List<Fragment> fragments = new ArrayList<>();

        mClassifyAdapter.replace(mConfigEntities);

        // 计算展示的Pager页
        int pagerSize = mConfigEntities.size() / PAGER_TAB_COUNT + ((mConfigEntities.size() % PAGER_TAB_COUNT) > 0 ? 1 : 0);
        for (int index = 0; index < pagerSize; index++) {
            int startPosition = index * PAGER_TAB_COUNT;
            int endPosition = (index + 1) * PAGER_TAB_COUNT;
            if (endPosition > mConfigEntities.size()) {
                endPosition = mConfigEntities.size();
            }
            List<LQCourseConfigEntity> tempList = mConfigEntities.subList(startPosition, endPosition);

            CourseClassifyPagerFragment fragment = CourseClassifyPagerFragment.newInstance(new ArrayList<LQCourseConfigEntity>(tempList));
            fragment.setNavigator(new CourseClassifyNavigatorImpl() {
                @Override
                public void onClickConfigTitleLayout(@NonNull LQCourseConfigEntity entity) {
                    /*if(!isAuthorized){
                        UIUtil.showToastSafe(R.string.label_please_request_authorization);
                        return;
                    }*/
                    // 获取该分类是否获取到授权
                    boolean reallyAuthorized = judgeClassifyAuthorizedInfo(entity);
                    NewOrganCourseFiltrateActivity.show(OrganCourseClassifyActivity.this, entity,
                            mSelectResource, false, mResourceData, isAuthorized, reallyAuthorized,
                            false, mRoles, mLibraryType);
                }
            });
            fragments.add(fragment);
        }

        this.mPagerFragments = fragments;

        mPagerAdapter = new HolderPagerAdapter(getSupportFragmentManager(), mPagerFragments);
        mViewPager.setAdapter(mPagerAdapter);

        if (mPagerFragments.size() <= 1) {
            mViewPagerIndicator.setVisibility(View.GONE);
        } else {
            for (int index = 0; index < mPagerFragments.size(); index++) {
                CheckedTextView indicatorView = new CheckedTextView(this);
                LinearLayout.LayoutParams layoutParams =
                        new LinearLayout.LayoutParams(DisplayUtil.dip2px(UIUtil.getContext(), 8), DisplayUtil.dip2px(UIUtil.getContext(), 8));
                if (index != 0)
                    layoutParams.leftMargin = DisplayUtil.dip2px(UIUtil.getContext(), 8);
                if (index == 0) indicatorView.setChecked(true);
                indicatorView.setLayoutParams(layoutParams);
                indicatorView.setBackgroundResource(R.drawable.bg_space_school_function_indicator);
                mViewPagerIndicator.addView(indicatorView);
            }
        }

        if (mPagerFragments.size() > 1) {
            mViewPager.addOnPageChangeListener(new PagerChangedAdapter() {
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    // 更改指示器的背景
                    for (int index = 0; index < mViewPagerIndicator.getChildCount(); index++) {
                        CheckedTextView view = (CheckedTextView) mViewPagerIndicator.getChildAt(index);
                        view.setChecked(index == position);
                    }
                }
            });
        }
    }

    @Override
    public void updateCheckPermissionView(@NonNull CheckSchoolPermissionEntity entity, boolean autoRequest) {
        mPermissionEntity = entity;
        if (EmptyUtil.isNotEmpty(entity)) {
            refreshAuthorizedInfo(entity);

            if (entity.isAuthorized()) {
                // 已经获取授权,并且没有失效
                isAuthorized = true;
                // 授权码过期
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
        mPresenter.requestSaveAuthorization(mSchoolId, 0, code);
    }

    @Override
    public void updateRequestPermissionView(@NonNull CheckPermissionResponseVo<Void> responseVo) {
        if (EmptyUtil.isEmpty(responseVo)) return;

        if (responseVo.isSucceed()) {
            UIUtil.showToastSafe(R.string.label_request_authorization_succeed);
            
            // 刷新权限信息
            String rightValue = responseVo.getRightValue();
            CheckSchoolPermissionEntity entity = new CheckSchoolPermissionEntity();
            entity.setRightValue(rightValue);
            entity.setAuthorized(true);
            mPermissionEntity = entity;
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
     * 刷新授权信息的View
     */
    private void refreshAuthorizedInfo(@NonNull CheckSchoolPermissionEntity entity) {
        if (entity.isAuthorized()) {
            // 授权之后才组装数据
            for (Fragment fragment : mPagerFragments) {
                CourseClassifyPagerFragment pagerFragment = (CourseClassifyPagerFragment) fragment;
                RecyclerAdapter<LQCourseConfigEntity> adapter = pagerFragment.getRecyclerAdapter();
                List<LQCourseConfigEntity> items = adapter.getItems();
                entity.assembleAuthorizedInClassify(items);
                adapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * 判断某个分类是否有授权
     *
     * @param entity 分类信息
     * @return true 该分类已授权
     */
    private boolean judgeClassifyAuthorizedInfo(@NonNull LQCourseConfigEntity entity) {
        boolean reallyAuthorized = false;
        if (EmptyUtil.isNotEmpty(mPermissionEntity)) {
            String rightValue = mPermissionEntity.getRightValue();
            if (EmptyUtil.isEmpty(rightValue)) return false;
            if (TextUtils.equals(rightValue, "0")) reallyAuthorized = true;
            String[] values = rightValue.split(",");
            if (EmptyUtil.isNotEmpty(values)) {
                List<String> strings = Arrays.asList(values);
                if (strings.contains(Integer.toString(entity.getId()))) {
                    reallyAuthorized = true;
                }
            }
        }
        return reallyAuthorized;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventWrapper event) {
        if (EventWrapper.isMatch(event, EventConstant.COURSE_SELECT_RESOURCE_EVENT)) {
            if (EmptyUtil.isNotEmpty(mResourceData) && !mResourceData.isInitiativeTrigger()) {
                ArrayList<SectionResListVo> vos = (ArrayList<SectionResListVo>) event.getData();
                setResult(Activity.RESULT_OK, new Intent().putExtra(RESULT_LIST, vos));
                // 杀掉所有可能的UI
                ActivityUtil.finishActivity(NewOrganCourseFiltrateActivity.class);
                ActivityUtil.finishActivity(SearchActivity.class);
                finish();
            }
        }
    }

    /**
     * 学程馆选取资源的入口
     *
     * @param activity    上下文对象
     * @param organId     机构Id
     * @param data        学程馆选取资源的参数
     * @param libraryType 学程馆类型
     *                    <p>onActivityResult回调选择数据,resultCode = {@link Activity.RESULT_OK}</p>
     *                    <p>data 为List<SectionResListVo> Key = {@link CourseSelectItemFragment.RESULT_LIST}</p>
     */
    public static void show(@NonNull final Activity activity,
                            @NonNull final String organId,
                            final boolean selectResource,
                            @NonNull final ShopResourceData data,
                            @NonNull final String roles,
                            int libraryType) {

        final String finalOrganId = "5e069b1a-9d90-49ed-956c-946e9f934b68";
//        final String finalOrganId = organId;
        if (libraryType == OrganLibraryType.TYPE_BRAIN_LIBRARY) {
            LQCourseConfigEntity lqCourseConfigEntity = new LQCourseConfigEntity();
            lqCourseConfigEntity.setId(OrganLibraryUtils.BRAIN_LIBRARY_ID);
            lqCourseConfigEntity.setLevel(OrganLibraryUtils.BRAIN_LIBRARY_LEVEL);
            lqCourseConfigEntity.setEntityOrganId(finalOrganId);
            lqCourseConfigEntity.setConfigValue(activity.getString(R.string.common_brain_library));
            enterOrganCourseFiltrate(activity, lqCourseConfigEntity, libraryType,
                    selectResource, data, roles);
            return;
        }
        // 获取中英文数据
        int languageRes = Utils.isZh(UIUtil.getContext()) ? LanguageType.LANGUAGE_CHINESE : LanguageType.LANGUAGE_OTHER;
        OrganCourseHelper.requestOrganCourseClassifyData(finalOrganId, languageRes, libraryType,
                new DataSource.Callback<List<LQCourseConfigEntity>>() {
                    @Override
                    public void onDataNotAvailable(int strRes) {
                        UIUtil.showToastSafe(strRes);
                    }

                    @Override
                    public void onDataLoaded(List<LQCourseConfigEntity> lqCourseConfigEntities) {
                        if (EmptyUtil.isEmpty(lqCourseConfigEntities) || lqCourseConfigEntities.size() > 1) {
                            // 不止一个分类
                            Intent intent = new Intent(activity, OrganCourseClassifyActivity.class);
                            Bundle bundle = new Bundle();
                            if (EmptyUtil.isEmpty(lqCourseConfigEntities)) {
                                bundle.putSerializable(KEY_EXTRA_ORGAN_COURSE_OBJECT, null);
                            } else {
                                bundle.putSerializable(KEY_EXTRA_ORGAN_COURSE_OBJECT, (Serializable) lqCourseConfigEntities);
                            }
                            bundle.putBoolean(KEY_EXTRA_ORGAN_SELECT_RESOURCE, selectResource);
                            bundle.putString(KEY_EXTRA_ORGAN_ID, finalOrganId);
                            if (selectResource)
                                bundle.putSerializable(KEY_EXTRA_ORGAN_RESOURCE_DATA, data);
                            bundle.putString(KEY_EXTRA_ROLES, roles);
                            bundle.putInt(KEY_EXTRA_LIBRARY_TYPE, libraryType);
                            intent.putExtras(bundle);
                            if (selectResource) {
                                activity.startActivityForResult(intent, data.getRequestCode());
                            } else {
                                activity.startActivity(intent);
                            }
                        } else {
                            enterOrganCourseFiltrate(activity, lqCourseConfigEntities.get(0), libraryType,
                                    selectResource, data, roles);
                        }
                    }
                });
    }

    private static void enterOrganCourseFiltrate(@NonNull Activity activity, LQCourseConfigEntity lqCourseConfigEntity, int libraryType,
                                                 boolean selectResource, @NonNull ShopResourceData data, @NonNull String roles) {
        NewOrganCourseFiltrateActivity.show(activity, lqCourseConfigEntity, selectResource, false, data,
                false, false, true, roles, libraryType);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.tv_more_course) {
            // 更多课程
            Intent broadIntent = new Intent();
            broadIntent.setAction(ACTION_MORE_COURSE_ENTER);
            sendBroadcast(broadIntent);
        }
    }

    /**
     * 学程馆的入口
     *
     * @param activity 上下文对象
     * @param organId  机构Id
     */
    public static void show(@NonNull final Activity activity,
                            @NonNull String organId, @NonNull String roles, int libraryType) {
        show(activity, organId, false, null, roles, libraryType);
    }

    private static class HolderPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> mFragments;

        public HolderPagerAdapter(FragmentManager fm, @NonNull List<Fragment> fragments) {
            super(fm);
            this.mFragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
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
