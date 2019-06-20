package com.lqwawa.intleducation.module.discovery.ui.classcourse.organlibrary;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.PresenterActivity;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerItemDecoration;
import com.lqwawa.intleducation.common.Common;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;
import com.lqwawa.intleducation.factory.data.entity.response.CheckPermissionResponseVo;
import com.lqwawa.intleducation.factory.data.entity.school.CheckSchoolPermissionEntity;
import com.lqwawa.intleducation.factory.data.entity.school.SchoolInfoEntity;
import com.lqwawa.intleducation.factory.helper.SchoolHelper;
import com.lqwawa.intleducation.module.discovery.ui.ImputAuthorizationCodeDialog;
import com.lqwawa.intleducation.module.discovery.ui.classcourse.courseselect.CourseShopClassifyActivity;
import com.lqwawa.intleducation.module.discovery.ui.classcourse.courseselect.CourseShopClassifyParams;
import com.lqwawa.intleducation.module.organcourse.OrganLibraryType;
import com.lqwawa.intleducation.module.organcourse.OrganLibraryUtils;
import com.lqwawa.intleducation.module.organcourse.filtrate.OrganCourseFiltrateActivity;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class OrganLibraryTypeActivity extends PresenterActivity<OrganLibraryTypeContract.Presenter>
        implements OrganLibraryTypeContract.View {


    private TopBar mTopBar;
    private RecyclerView mRecycler;
    private OrganLibraryAdapter mAdapter;
    private List<LQCourseConfigEntity> mEntityList = new ArrayList<>();

    private CourseShopClassifyParams mParams;

    // 授权信息
    private CheckSchoolPermissionEntity mPermissionEntity;

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
    // 是否获取到授权
    private boolean isAuthorized;
    private boolean isExist;

    @Override
    protected OrganLibraryTypeContract.Presenter initPresenter() {
        return new OrganLibraryTypePresenter(this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_organ_library_type;
    }

    @Override
    protected boolean initArgs(@NonNull Bundle bundle) {
        mParams = (CourseShopClassifyParams) bundle.getSerializable(ACTIVITY_BUNDLE_OBJECT);
        if (EmptyUtil.isEmpty(mParams)) {
            return false;
        }

        return super.initArgs(bundle);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mTopBar = (TopBar) findViewById(R.id.top_bar);
        mRecycler = (RecyclerView) findViewById(R.id.recycler);

        mTopBar.setBack(true);
        mTopBar.setTitle(R.string.title_add_course);
        mTopBar.setRightFunctionText1(R.string.label_request_authorization,
                v -> {
                    // 点击获取授权
                    if (isAuthorized) {
                        // 已经获取到授权
                        UIUtil.showToastSafe(R.string.label_request_authorization_succeed);
                        return;
                    }
                    // 获取授权
                    requestAuthorizedPermission(isExist);
                });

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecycler.setLayoutManager(mLayoutManager);
        mAdapter = new OrganLibraryAdapter();
        mRecycler.setAdapter(mAdapter);

        mAdapter.setListener(new RecyclerAdapter.AdapterListenerImpl<LQCourseConfigEntity>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, LQCourseConfigEntity entity) {
                super.onItemClick(holder, entity);
                if (mParams != null && entity != null) {
                    mParams.setLibraryType(entity.getLibraryType());
                    mParams.setIsAddCourseClass(true);
                }
                if (entity.getLibraryType() == OrganLibraryType.TYPE_BRAIN_LIBRARY) {
                    super.onItemClick(holder, entity);
                    if (!isAuthorized) {
                        UIUtil.showToastSafe(R.string.label_please_request_authorization);
                        return;
                    }

                    boolean isReallyAuthorized = judgeClassifyAuthorizedInfo(entity);
                    if (!isReallyAuthorized) {
                        // 未授权不允许授权
                        UIUtil.showToastSafe(R.string.label_unauthorized);
                        return;
                    }
                    Bundle extras = getIntent().getBundleExtra(Common.Constance.KEY_EXTRAS_STUDY_TASK);
                    SchoolHelper.requestSchoolInfo(UserHelper.getUserId(), mParams.getOrganId(),
                            new DataSource.Callback<SchoolInfoEntity>() {
                                @Override
                                public void onDataNotAvailable(int strRes) {
                                    showError(strRes);
                                }

                                @Override
                                public void onDataLoaded(SchoolInfoEntity schoolInfoEntity) {
                                    String roles = schoolInfoEntity.getRoles();
                                    entity.setId(OrganLibraryUtils.BRAIN_LIBRARY_ID);
                                    entity.setLevel(OrganLibraryUtils.BRAIN_LIBRARY_LEVEL);
                                    entity.setEntityOrganId(mParams.getOrganId());
                                    OrganCourseFiltrateActivity.show(
                                            OrganLibraryTypeActivity.this,
                                            entity, false, true,
                                            null, true, true, false, roles,
                                            mParams.getLibraryType());
                                }
                            });

                } else {
                    Bundle extras = getIntent().getBundleExtra(Common.Constance.KEY_EXTRAS_STUDY_TASK);
                    CourseShopClassifyActivity.show(OrganLibraryTypeActivity.this, mParams,
                            extras);
                }
            }
        });

        mRecycler.addItemDecoration(new RecyclerItemDecoration(this, RecyclerItemDecoration.VERTICAL_LIST));
    }

    @Override
    protected void initData() {
        super.initData();

        initEntityList();
        mAdapter.replace(mEntityList);

    }

    @Override
    protected void onResume() {
        super.onResume();
        // 获取授权状态
        mPresenter.requestCheckSchoolPermission(mParams.getOrganId(), 0,
                false);
    }

    private void initEntityList() {
        LQCourseConfigEntity entity = new LQCourseConfigEntity();
        entity.setLibraryType(OrganLibraryType.TYPE_LQCOURSE_SHOP);
        entity.setThumbnail("ic_lqcourse_circle");
        entity.setConfigValue(getString(R.string.common_course_library));
        mEntityList.add(entity);

        entity = new LQCourseConfigEntity();
        entity.setLibraryType(OrganLibraryType.TYPE_VIDEO_LIBRARY);
        entity.setThumbnail("ic_video_library_circle");
        entity.setConfigValue(getString(R.string.common_video_library));
        mEntityList.add(entity);

        entity = new LQCourseConfigEntity();
        entity.setLibraryType(OrganLibraryType.TYPE_LIBRARY);
        entity.setThumbnail("ic_library_circle");
        entity.setConfigValue(getString(R.string.common_library));
        mEntityList.add(entity);

        entity = new LQCourseConfigEntity();
        entity.setLibraryType(OrganLibraryType.TYPE_PRACTICE_LIBRARY);
        entity.setThumbnail("ic_practice_library_circle");
        entity.setConfigValue(getString(R.string.common_practice_library));
        mEntityList.add(entity);

        entity = new LQCourseConfigEntity();
        entity.setId(OrganLibraryUtils.BRAIN_LIBRARY_ID);
        entity.setLibraryType(OrganLibraryType.TYPE_BRAIN_LIBRARY);
        entity.setThumbnail("ic_brain_library_circle");
        entity.setConfigValue(getString(R.string.common_brain_library));
        mEntityList.add(entity);
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
        mPresenter.requestSaveAuthorization(mParams.getOrganId(), 0, code);
    }


    /**
     * 班级学程列表选择的页面
     *
     * @param context 上下文对象
     */
    public static void show(@NonNull Context context,
                            @NonNull CourseShopClassifyParams params,
                            @Nullable Bundle extras) {
        Intent intent = new Intent(context, OrganLibraryTypeActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ACTIVITY_BUNDLE_OBJECT, params);
        bundle.putBundle(Common.Constance.KEY_EXTRAS_STUDY_TASK, extras);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    public void updateCheckPermissionView(@NonNull CheckSchoolPermissionEntity entity, boolean autoRequest) {
        mPermissionEntity = entity;

        if (EmptyUtil.isNotEmpty(entity)) {
            List<LQCourseConfigEntity> items = mAdapter.getItems();
            entity.assembleAuthorizedInClassify(items);
            mAdapter.notifyDataSetChanged();

            if (entity.isAuthorized()) {
                // 已经获取授权,并且没有失效
                isAuthorized = true;
                isExist = entity.isExist();
            } else {
                if (autoRequest) {
                    // 点击获取授权
                    requestAuthorizedPermission(entity.isExist());
                }
            }
        }
    }

    @Override
    public void updateRequestPermissionView(@NonNull CheckPermissionResponseVo responseVo) {
        if (EmptyUtil.isEmpty(responseVo)) return;
        if (responseVo.isSucceed()) {
            // 刷新权限信息
            String rightValue = responseVo.getRightValue();
            CheckSchoolPermissionEntity entity = new CheckSchoolPermissionEntity();
            entity.setRightValue(rightValue);
            entity.setAuthorized(true);
            entity.setExist(false);
            mPermissionEntity = entity;

            List<LQCourseConfigEntity> items = mAdapter.getItems();
            entity.assembleAuthorizedInClassify(items);
            mAdapter.notifyDataSetChanged();

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
}
