package com.lqwawa.intleducation.module.discovery.ui.classcourse.courseselect.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.PresenterActivity;
import com.lqwawa.intleducation.base.PresenterFragment;
import com.lqwawa.intleducation.base.widgets.NoPermissionView;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerItemDecoration;
import com.lqwawa.intleducation.common.utils.ActivityUtil;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;
import com.lqwawa.intleducation.factory.data.entity.response.CheckPermissionResponseVo;
import com.lqwawa.intleducation.factory.data.entity.school.CheckSchoolPermissionEntity;
import com.lqwawa.intleducation.factory.event.EventConstant;
import com.lqwawa.intleducation.factory.event.EventWrapper;
import com.lqwawa.intleducation.module.discovery.ui.ImputAuthorizationCodeDialog;
import com.lqwawa.intleducation.module.discovery.ui.classcourse.courseselect.CourseShopClassifyActivity;
import com.lqwawa.intleducation.module.discovery.ui.classcourse.courseselect.CourseShopClassifyAdapter;
import com.lqwawa.intleducation.module.discovery.ui.classcourse.courseselect.CourseShopClassifyContract;
import com.lqwawa.intleducation.module.discovery.ui.classcourse.courseselect.CourseShopClassifyParams;
import com.lqwawa.intleducation.module.discovery.ui.classcourse.courseselect.CourseShopClassifyPresenter;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.search.SearchActivity;
import com.lqwawa.intleducation.module.learn.vo.SectionResListVo;
import com.lqwawa.intleducation.module.organcourse.ShopResourceData;
import com.lqwawa.intleducation.module.organcourse.filtrate.OrganCourseFiltrateActivity;

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
public class CourseShopClassifyFragment extends PresenterFragment<CourseShopClassifyContract.Presenter>
        implements CourseShopClassifyContract.View{

    private RecyclerView mRecycler;
    private CourseShopClassifyAdapter mAdapter;
    private NoPermissionView mNoPermissionView;

    private CourseShopClassifyParams mParams;
    private String mSchoolId;
    private boolean mSelectResource;
    private ShopResourceData mResourceData;
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
    protected boolean initArgs(@NonNull Bundle bundle) {
        mParams = (CourseShopClassifyParams) bundle.getSerializable(FRAGMENT_BUNDLE_OBJECT);
        if(EmptyUtil.isEmpty(mParams)){
            return false;
        }

        mSchoolId = mParams.getOrganId();
        mSelectResource = mParams.isSelectResource();
        mResourceData = mParams.getData();
        if(EmptyUtil.isEmpty(mSchoolId)) return false;
        if(mSelectResource && EmptyUtil.isEmpty(mResourceData)) return false;
        return super.initArgs(bundle);
    }

    @Override
    protected CourseShopClassifyContract.Presenter initPresenter() {
        return new CourseShopClassifyPresenter(this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_course_shop_classify;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mRecycler = (RecyclerView) mRootView.findViewById(R.id.recycler);
        mNoPermissionView = (NoPermissionView) mRootView.findViewById(R.id.permission_view);
        mNoPermissionView.setDescription(getString(R.string.label_organ_shop_permission_description));

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
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

                if(mSelectResource){
                    OrganCourseFiltrateActivity.show(
                            getActivity(),
                            entity,mSelectResource,false,
                            mResourceData,isAuthorized,isReallyAuthorized,false);
                }else{
                    OrganCourseFiltrateActivity.show(
                            getActivity(),
                            entity,false,true,
                            null,isAuthorized,isReallyAuthorized,false);
                }
            }
        });

        mRecycler.addItemDecoration(new RecyclerItemDecoration(getActivity(),RecyclerItemDecoration.VERTICAL_LIST));
    }

    @Override
    protected void initData() {
        super.initData();
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }

        String organId = mParams.getOrganId();
        if(mSelectResource){
            mPresenter.requestCourseShopClassifyResourceData(organId);
        }else{
            mPresenter.requestCourseShopClassifyData(organId);
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
            imputAuthorizationCodeDialog = new ImputAuthorizationCodeDialog(getActivity(), tipInfo,
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
        // 获取授权状态
        mPresenter.requestCheckSchoolPermission(mSchoolId,0,true);

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
                // 授权码过期
                isExist = entity.isExist();
                // UIUtil.showToastSafe(R.string.label_old_request_authorization);
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventWrapper event){
        if(EventWrapper.isMatch(event, EventConstant.COURSE_SELECT_RESOURCE_EVENT)){
            /*ArrayList<SectionResListVo> vos = (ArrayList<SectionResListVo>) event.getData();
            setResult(Activity.RESULT_OK,new Intent().putExtra(RESULT_LIST, vos));
            // 杀掉所有可能的UI
            ActivityUtil.finishActivity(OrganCourseFiltrateActivity.class);
            ActivityUtil.finishActivity(SearchActivity.class);
            finish();*/
        }
    }

    /**
     * 学程馆学程学习任务选择的入口
     * @param params 核心参数
     */
    public static CourseShopClassifyFragment newInstance(@NonNull CourseShopClassifyParams params){
        CourseShopClassifyFragment fragment = new CourseShopClassifyFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(FRAGMENT_BUNDLE_OBJECT,params);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if(EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }
    }
}
