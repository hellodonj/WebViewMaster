package com.lqwawa.intleducation.module.discovery.ui.classcourse.courseselect;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.PresenterActivity;
import com.lqwawa.intleducation.base.vo.ResponseVo;
import com.lqwawa.intleducation.base.widgets.NoPermissionView;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerItemDecoration;
import com.lqwawa.intleducation.common.Common;
import com.lqwawa.intleducation.common.ui.ContactsMessageDialog;
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
import com.lqwawa.intleducation.factory.helper.LQConfigHelper;
import com.lqwawa.intleducation.factory.helper.SchoolHelper;
import com.lqwawa.intleducation.module.discovery.ui.CourseDetailsActivity;
import com.lqwawa.intleducation.module.discovery.ui.ImputAuthorizationCodeDialog;
import com.lqwawa.intleducation.module.discovery.ui.classcourse.ClassCourseActivity;
import com.lqwawa.intleducation.module.discovery.ui.coursedetail.CourseDetailParams;
import com.lqwawa.intleducation.module.discovery.ui.coursedetail.CourseDetailType;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.home.LanguageType;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.search.SearchActivity;
import com.lqwawa.intleducation.module.discovery.ui.subject.SetupConfigType;
import com.lqwawa.intleducation.module.discovery.ui.subject.add.AddSubjectActivity;
import com.lqwawa.intleducation.module.learn.tool.TaskSliderHelper;
import com.lqwawa.intleducation.module.learn.vo.SectionResListVo;
import com.lqwawa.intleducation.module.organcourse.ShopResourceData;
import com.lqwawa.intleducation.module.organcourse.filtrate.OrganCourseFiltrateActivity;
import com.lqwawa.intleducation.module.organcourse.online.CourseShopListActivity;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

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
 * @author mrmedici
 * @desc 学程馆分类显示的页面，用来班级学程的选择，V5.11添加两栖蛙蛙学习任务用于学程馆的选择
 */
public class CourseShopClassifyActivity extends PresenterActivity<CourseShopClassifyContract.Presenter>
    implements CourseShopClassifyContract.View,View.OnClickListener{

    private static final int SUBJECT_SETTING_REQUEST_CODE = 1 << 1;

    private TopBar mTopBar;
    private RecyclerView mRecycler;
    private CourseShopClassifyAdapter mAdapter;
    private NoPermissionView mNoPermissionView;

    private FrameLayout mNewCartContainer;
    private TextView mTvWorkCart;
    private TextView mTvCartPoint;

    private LinearLayout mSubjectLayout;
    private Button mAddSubject;

    private CourseShopClassifyParams mParams;
    private String mSchoolId;
    private String mClassId;
    private boolean mSelectResource;
    private ShopResourceData mResourceData;
    private int libraryType = 0;
    // 授权信息
    private CheckSchoolPermissionEntity mPermissionEntity;

    private ImputAuthorizationCodeDialog imputAuthorizationCodeDialog;

    private static HashMap<String, String> authorizationErrorMapZh =
            new HashMap<>();
    private static HashMap<String, String> authorizationErrorMapEn =
            new HashMap<>();

    static{
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }
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
    protected CourseShopClassifyContract.Presenter initPresenter() {
        return new CourseShopClassifyPresenter(this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_course_shop_classify;
    }

    @Override
    protected boolean initArgs(@NonNull Bundle bundle) {
        mParams = (CourseShopClassifyParams) bundle.getSerializable(ACTIVITY_BUNDLE_OBJECT);
        if(EmptyUtil.isEmpty(mParams)){
            return false;
        }

        mSchoolId = mParams.getOrganId();
        mClassId = mParams.getClassId();
        mSelectResource = mParams.isSelectResource();
        mResourceData = mParams.getData();
        if(EmptyUtil.isEmpty(mSchoolId)) return false;
        if(mSelectResource && EmptyUtil.isEmpty(mResourceData)) return false;

        if(mSelectResource) {
            mResourceData.setInitiativeTrigger(mParams.isInitiativeTrigger());
            mResourceData.setSchoolId(mSchoolId);
            mResourceData.setClassId(mClassId);
            mResourceData.setEnterType(CourseDetailType.COURSE_DETAIL_SCHOOL_ENTER);
        }

        return super.initArgs(bundle);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mTopBar = (TopBar) findViewById(R.id.top_bar);
        mRecycler = (RecyclerView) findViewById(R.id.recycler);
        mNoPermissionView = (NoPermissionView) findViewById(R.id.permission_view);
        mNoPermissionView.setDescription(getString(R.string.label_organ_shop_permission_description));

        mNewCartContainer = (FrameLayout) findViewById(R.id.new_cart_container);
        mTvWorkCart = (TextView) findViewById(R.id.tv_work_cart);
        mTvCartPoint = (TextView) findViewById(R.id.tv_cart_point);

        mSubjectLayout = (LinearLayout) findViewById(R.id.subject_layout);
        mAddSubject = (Button) findViewById(R.id.btn_add_subject);
        mAddSubject.setOnClickListener(this);

        if(mSelectResource){
            if(mResourceData.isInitiativeTrigger()) {
                mNewCartContainer.setVisibility(View.VISIBLE);
                mNewCartContainer.setOnClickListener(this);
            }

            // 更改，不显示
            mSubjectLayout.setVisibility(View.GONE);
        }

        mTopBar.setBack(true);
        mTopBar.setTitle(R.string.title_course_shop);

        if(mSelectResource){
            // mTopBar.findViewById(R.id.right_function1_text).setVisibility(View.GONE);
            mTopBar.setRightFunctionText1(R.string.title_subject_setting, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 科目设置
                    AddSubjectActivity.show(CourseShopClassifyActivity.this,true,SUBJECT_SETTING_REQUEST_CODE);
                }
            });
        }else{
            mTopBar.setRightFunctionText1(R.string.label_request_authorization, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 点击获取授权
                    if(isAuthorized){
                        // 已经获取到授权
                        UIUtil.showToastSafe(R.string.label_request_authorization_succeed);
                        return;
                    }
                    // 获取授权
                    requestAuthorizedPermission(isExist);
                }
            });
        }

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecycler.setLayoutManager(mLayoutManager);
        mAdapter = new CourseShopClassifyAdapter();
        mRecycler.setAdapter(mAdapter);

        mAdapter.setListener(new RecyclerAdapter.AdapterListenerImpl<LQCourseConfigEntity>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, LQCourseConfigEntity entity) {
                super.onItemClick(holder, entity);
                if(!isAuthorized){
                    UIUtil.showToastSafe(R.string.label_please_request_authorization);
                    return;
                }

                boolean isReallyAuthorized = judgeClassifyAuthorizedInfo(entity);
                if(!isReallyAuthorized){
                    // 未授权不允许授权
                    UIUtil.showToastSafe(R.string.label_unauthorized);
                    return;
                }

                SchoolHelper.requestSchoolInfo(UserHelper.getUserId(), mSchoolId, new DataSource.Callback<SchoolInfoEntity>() {
                    @Override
                    public void onDataNotAvailable(int strRes) {
                        showError(strRes);
                    }

                    @Override
                    public void onDataLoaded(SchoolInfoEntity schoolInfoEntity) {
                        String roles = schoolInfoEntity.getRoles();
                        if(mSelectResource){
                            Bundle extras = getIntent().getBundleExtra(Common.Constance.KEY_EXTRAS_STUDY_TASK);
                            OrganCourseFiltrateActivity.show(
                                    CourseShopClassifyActivity.this,
                                    entity,mSelectResource,false,
                                    mResourceData,extras,isAuthorized,isReallyAuthorized,false,
                                    roles, 0);
                        }else{
                            OrganCourseFiltrateActivity.show(
                                    CourseShopClassifyActivity.this,
                                    entity,false,true,
                                    null,isAuthorized,isReallyAuthorized,false,roles, 0);
                        }
                    }
                });

            }
        });

        mRecycler.addItemDecoration(new RecyclerItemDecoration(this,RecyclerItemDecoration.VERTICAL_LIST));
    }

    @Override
    protected void initData() {
        super.initData();
        this.showLoading();
        String organId = mParams.getOrganId();
        if(mSelectResource){
            mPresenter.requestCourseShopClassifyResourceData(organId, -1);
        }else{
            mPresenter.requestCourseShopClassifyData(organId, -1);
        }
    }

    /**
     * 申请授权
     */
    private void requestAuthorizedPermission(boolean isExist){
        String tipInfo = UIUtil.getString(R.string.label_request_authorization_tip);
        if(isExist){
            tipInfo = UIUtil.getString(R.string.authorization_out_time_tip);
        }
        if(imputAuthorizationCodeDialog == null) {
            imputAuthorizationCodeDialog = new ImputAuthorizationCodeDialog(this, tipInfo,
                    new ImputAuthorizationCodeDialog.CommitCallBack() {
                        @Override
                        public void onCommit(String code) {
                            commitAuthorizationCode(code);
                        }

                        @Override
                        public void onCancel() {
                            if(EmptyUtil.isNotEmpty(imputAuthorizationCodeDialog)){
                                imputAuthorizationCodeDialog.dismiss();
                            }
                        }
                    });
        }
        imputAuthorizationCodeDialog.setTipInfo(tipInfo);
        if(!imputAuthorizationCodeDialog.isShowing()) {
            imputAuthorizationCodeDialog.show();
        }
    }

    /**
     * @desc 申请授权
     * @author medici
     * @param code 授权码
     */
    private void commitAuthorizationCode(@NonNull String code){
        mPresenter.requestSaveAuthorization(mSchoolId,0,code);
    }

    @Override
    public void updateCourseShopClassifyView(@NonNull List<LQCourseConfigEntity> entities) {
        this.hideLoading();
        // 获取授权状态
        mPresenter.requestCheckSchoolPermission(mSchoolId,0,mSelectResource && !isActivityResult);

        mAdapter.replace(entities);
        if(EmptyUtil.isEmpty(entities)){
            mNoPermissionView.setVisibility(View.VISIBLE);
            mRecycler.setVisibility(View.GONE);
        }else{
            mNoPermissionView.setVisibility(View.GONE);
            mRecycler.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void updateCourseShopClassifyResourceView(@NonNull List<LQCourseConfigEntity> entities) {
        updateCourseShopClassifyView(entities);
    }

    @Override
    public void updateCheckPermissionView(@NonNull CheckSchoolPermissionEntity entity, boolean autoRequest) {
        mPermissionEntity = entity;

        if(EmptyUtil.isNotEmpty(entity)){
            List<LQCourseConfigEntity> items = mAdapter.getItems();
            entity.assembleAuthorizedInClassify(items);
            mAdapter.notifyDataSetChanged();

            if(entity.isAuthorized()){
                // 已经获取授权,并且没有失效
                isAuthorized = true;
                isExist = entity.isExist();
            }else{
                if(autoRequest){
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



    @Override
    public void updateRequestPermissionView(@NonNull CheckPermissionResponseVo responseVo) {
        if(EmptyUtil.isEmpty(responseVo)) return;
        if(responseVo.isSucceed()){
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
            if(imputAuthorizationCodeDialog != null){
                imputAuthorizationCodeDialog.setCommited(true);
                imputAuthorizationCodeDialog.dismiss();
            }
        }else{
            String language = Locale.getDefault().getLanguage();
            //提示授权码错误原因然后退出
            UIUtil.showToastSafe(language.equals("zh") ? authorizationErrorMapZh.get("" + responseVo.getCode()): authorizationErrorMapEn.get("" + responseVo.getCode()));

            if(imputAuthorizationCodeDialog != null){
                imputAuthorizationCodeDialog.clearPassword();
            }
        }
    }

    /**
     * 判断某个分类是否有授权
     * @param entity 分类信息
     * @return true 该分类已授权
     */
    private boolean judgeClassifyAuthorizedInfo(@NonNull LQCourseConfigEntity entity){
        boolean reallyAuthorized = false;
        if(EmptyUtil.isNotEmpty(mPermissionEntity)){
            String rightValue = mPermissionEntity.getRightValue();
            if(EmptyUtil.isEmpty(rightValue)) return false;
            if(TextUtils.equals(rightValue,"0")) reallyAuthorized = true;
            String[] values = rightValue.split(",");
            if(EmptyUtil.isNotEmpty(values)){
                List<String> strings = Arrays.asList(values);
                if(strings.contains(Integer.toString(entity.getId()))){
                    reallyAuthorized = true;
                }
            }
        }
        return reallyAuthorized;
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if(viewId == R.id.btn_add_subject){
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
                        TaskSliderHelper.onWorkCartListener.enterIntroTaskDetailActivity(CourseShopClassifyActivity.this,mSchoolId,mClassId,extras);
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

    private boolean isActivityResult;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == SUBJECT_SETTING_REQUEST_CODE){
                // 科目设置成功的回调
                Bundle extras = data.getExtras();
                if(EmptyUtil.isNotEmpty(extras)){
                    boolean completed = extras.getBoolean(AddSubjectActivity.KEY_EXTRA_RESULT);
                    if(completed){
                        // 刷新标签和课程
                        isActivityResult = true;
                        initData();
                    }
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventWrapper event){
        if(EventWrapper.isMatch(event, EventConstant.COURSE_SELECT_RESOURCE_EVENT)){
            if(EmptyUtil.isNotEmpty(mResourceData) && !mResourceData.isInitiativeTrigger()) {
                ArrayList<SectionResListVo> vos = (ArrayList<SectionResListVo>) event.getData();
                setResult(Activity.RESULT_OK, new Intent().putExtra(RESULT_LIST, vos));
                // 杀掉所有可能的UI
                ActivityUtil.finishActivity(OrganCourseFiltrateActivity.class);
                ActivityUtil.finishActivity(SearchActivity.class);
                finish();
            }
        }
    }

    public static void show(@NonNull Activity activity,@NonNull CourseShopClassifyParams params){
        show(activity,params,null);
    }

    /**
     * 班级学程列表选择的页面
     * @param context 上下文对象
     */
    public static void show(@NonNull Context context,
                            @NonNull CourseShopClassifyParams params,
                            @Nullable Bundle extras,
                            boolean addClassCourse){
        Intent intent = new Intent(context,CourseShopClassifyActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ACTIVITY_BUNDLE_OBJECT,params);
        bundle.putBundle(Common.Constance.KEY_EXTRAS_STUDY_TASK,extras);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    /**
     * 学程馆学程学习任务选择的入口
     * @param activity 上下文对象
     */
    public static void show(@NonNull Activity activity, @NonNull CourseShopClassifyParams params, @Nullable Bundle extras){
        Intent intent = new Intent(activity,CourseShopClassifyActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ACTIVITY_BUNDLE_OBJECT,params);
        bundle.putBundle(Common.Constance.KEY_EXTRAS_STUDY_TASK,extras);
        intent.putExtras(bundle);
        if(params.isSelectResource()){
            ShopResourceData data = params.getData();
            activity.startActivityForResult(intent,data.getRequestCode());
        }else{
            activity.startActivity(intent);
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }
    }
}
