package com.lqwawa.intleducation.module.discovery.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ui.MyBaseFragmentActivity;
import com.lqwawa.intleducation.base.utils.ToastUtil;
import com.lqwawa.intleducation.base.vo.RequestVo;
import com.lqwawa.intleducation.base.vo.ResponseVo;
import com.lqwawa.intleducation.module.discovery.vo.AuthorizationVo;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;
import com.lqwawa.intleducation.module.discovery.vo.SchoolListVo;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static android.app.AlertDialog.THEME_HOLO_LIGHT;

/**
 * Created by XChen on 2016/11/13.
 * email:man0fchina@foxmail.com
 * 精品课程课程列表
 */
public class HQCCourseListActivity extends MyBaseFragmentActivity{
    public static int RC_SelectCourseRes = 1206;
    public static void start(Activity activity, String schoolId) {
        activity.startActivity(new Intent(activity, HQCCourseListActivity.class)
                .putExtra("Sort", "-1").putExtra("SchoolId", schoolId));
    }

    public static void startForSelRes(Activity activity, String schoolId, int tasktype) {
        activity.startActivityForResult(new Intent(activity, HQCCourseListActivity.class)
                .putExtra("Sort", "-1")
                .putExtra("SchoolId", schoolId)
                .putExtra("tasktype",tasktype)
                .putExtra("isForSelRes", true), RC_SelectCourseRes);
    }

    private HQCCourseFragment lqCourseCourseFragment;
    private SearchFragment searchFragment;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_for_fragment);
        lqCourseCourseFragment = new HQCCourseFragment();
        lqCourseCourseFragment.setOnCourseListener(new HQCCourseListFragment.OnCourseSelListener() {
            @Override
            public void onCourseSel(CourseVo vo) {
                if(vo != null) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("CourseVo",vo);
                    bundle.putInt("tasktype",getIntent().getIntExtra("tasktype",1));
                    CourseSelectFragment courseSelectFragment = new CourseSelectFragment();
                    courseSelectFragment.setArguments(bundle);
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.hide(lqCourseCourseFragment);
                    fragmentTransaction.add(R.id.root_fragment_container, courseSelectFragment);
                    fragmentTransaction.show(courseSelectFragment);
                    fragmentTransaction.commit();
                    fragmentTransaction.addToBackStack(null);
                }
            }

            @Override
            public void onSearch() {
                gotoSearch();
            }
        });

        loadSchools();
    }

    public void refreshFragment(){
        if(this.lqCourseCourseFragment != null){
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.root_fragment_container, lqCourseCourseFragment);
            fragmentTransaction.show(lqCourseCourseFragment);
            fragmentTransaction.commit();
        }
    }

    public void gotoSearch(){
        if(searchFragment == null) {
            searchFragment = new SearchFragment();
            searchFragment.setOnSearchStatusChangeListener(onSearchStatusChangeListener);
            FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.root_fragment_container, searchFragment);
            fragmentTransaction.hide(lqCourseCourseFragment);
            fragmentTransaction.show(searchFragment);
            fragmentTransaction.commit();
            fragmentTransaction.addToBackStack(null);
        }else{
            FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.hide(lqCourseCourseFragment);
            fragmentTransaction.show(searchFragment);
            fragmentTransaction.commitAllowingStateLoss();
        }
        searchFragment.reInitSearch();
    }

    private SearchFragment.OnSearchStatusChangeListener onSearchStatusChangeListener
            = new SearchFragment.OnSearchStatusChangeListener() {
        @Override
        public void onSearch(String keyWord) {
            hideKeyboard();
            FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.hide(searchFragment);
            fragmentTransaction.show(lqCourseCourseFragment);
            fragmentTransaction.commitAllowingStateLoss();
            lqCourseCourseFragment.updateForSearch(keyWord);
        }

        @Override
        public void onCancel() {
            FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.hide(searchFragment);
            fragmentTransaction.show(lqCourseCourseFragment);
            fragmentTransaction.commitAllowingStateLoss();
        }
    };

    /**
     * 机构权限
     */
    private void loadSchools() {
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.GetSubscribeNoList);
        params.addBodyParameter("MemberId", UserHelper.getUserId());
        params.setConnectTimeout(10000);
        showProgressDialog(activity.getResources().getString(R.string.loading));
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                SchoolListVo result = JSON.parseObject(s,
                        new TypeReference<SchoolListVo>() {
                        });
                if (!result.isHasError()) {
                    List<SchoolListVo.ModelBean.SubscribeNoListBean> subscribeNoList = result.getModel().getSubscribeNoList();
                    if (subscribeNoList != null) {
                        boolean flag = false;
                        for (SchoolListVo.ModelBean.SubscribeNoListBean subscribeNoListBean : subscribeNoList) {
                            if ((subscribeNoListBean.getSchoolId()).equalsIgnoreCase(getIntent().getStringExtra("SchoolId"))) {
                                if (subscribeNoListBean.getState() == 2) {
                                    flag = true;
                                }
                                break;
                            }
                        }

                        if (flag) {
                            checkAuthorization();
                            return;
                        }
                    }

                    closeProgressDialog();
                    showDialog();
                }else{
                    closeProgressDialog();
                    try {
                        ToastUtil.showToast(activity, result.getErrorMessage().toString());
                    }catch (Exception e){

                    }
                }
            }

            private void showDialog() {
                View inflate = LayoutInflater.from(getApplicationContext()).inflate(R.layout.dialog_authority, null);
                TextView message = (TextView)inflate.findViewById(R.id.tv_tip_message);
                TextView btn = (TextView)inflate.findViewById(R.id.tv_btn);
                message.setText(getString(R.string.lq_check_authority));
                btn.setText(getString(R.string.lq_dialog_str));
                final AlertDialog alertDialog = new AlertDialog.Builder(HQCCourseListActivity.this,THEME_HOLO_LIGHT)
                        .setCancelable(false)
                        .create();
                alertDialog.show();
                Window window = alertDialog.getWindow();
                if (window != null) {
                    window.setContentView(inflate);
                }
                alertDialog.setCanceledOnTouchOutside(false);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                        finish();
                    }
                });
            }

            @Override
            public void onCancelled(CancelledException e) {
                finish();
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                closeProgressDialog();
                showDialog();
            }

            @Override
            public void onFinished() {
            }
        });
    }

    /**
     * 检查机构权限
     */
    public void checkAuthorization(){
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("memberId", UserHelper.getUserId());
        requestVo.addParams("organId", getIntent().getStringExtra("SchoolId"));
        RequestParams params = new RequestParams(
                AppConfig.ServerUrl.checkAuthorization + requestVo.getParams());
        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                closeProgressDialog();
                AuthorizationVo result = JSON.parseObject(s,
                        new TypeReference<AuthorizationVo>() {
                        });
                if (result.getCode() == 0) {
                    if(result.isIsAuthorized()){//有授权
                        refreshFragment();
                    }else{
                        String errorInfo = activity.getResources()
                                .getString(R.string.imput_authorization_title);
                        if(result.isIsExist()){
                            errorInfo = activity.getResources()
                                    .getString(R.string.authorization_out_time_tip);
                        }
                        showAuthorizationImputDialog(errorInfo);
                    }
                }else{
                    //提示获取授权失败然后退出
                    ToastUtil.showToast(activity,
                            getResources().getString(R.string.get_authorization_fail_tip));
                    finish();
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
                finish();
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                closeProgressDialog();
                //提示获取授权失败然后退出
                ToastUtil.showToast(activity, getResources().getString(R.string.net_error_tip));
                finish();
            }

            @Override
            public void onFinished() {
            }
        });
    }

    private void showAuthorizationImputDialog(String tipInfo){
        if(imputAuthorizationCodeDialog == null) {
            imputAuthorizationCodeDialog =
                    new ImputAuthorizationCodeDialog(activity, tipInfo,
                            new ImputAuthorizationCodeDialog.CommitCallBack() {
                                @Override
                                public void onCommit(String code) {
                                    commitAuthorizationCode(code);
                                }

                                @Override
                                public void onCancel() {
                                    activity.finish();
                                }
                            });
        }
        imputAuthorizationCodeDialog.setTipInfo(tipInfo);
        if(!imputAuthorizationCodeDialog.isShowing()) {
            imputAuthorizationCodeDialog.show();
        }
    }

    /**
     * 提交授权码
     * @param code 授权码
     */
    private void commitAuthorizationCode(String code){
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("memberId", UserHelper.getUserId());
        requestVo.addParams("organId", getIntent().getStringExtra("SchoolId"));
        requestVo.addParams("code", code);
        RequestParams params = new RequestParams(
                AppConfig.ServerUrl.commitAuthorizationCode + requestVo.getParams());
        params.setConnectTimeout(10000);
        showProgressDialog(activity.getResources().getString(R.string.loading));
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                closeProgressDialog();
                ResponseVo<String> result = JSON.parseObject(s,
                        new TypeReference<ResponseVo<String>>() {
                        });
                if (result.getCode() == 0) {//授权成功
                    if(imputAuthorizationCodeDialog != null){
                        imputAuthorizationCodeDialog.setCommited(true);
                        imputAuthorizationCodeDialog.dismiss();
                    }
                    refreshFragment();
                }else{
                    String language = Locale.getDefault().getLanguage();
                    //提示授权码错误原因然后退出
                    ToastUtil.showToast(activity,
                            language.equals("zh") ?
                                    authorizationErrorMapZh.get("" + result.getCode())
                                    : authorizationErrorMapEn.get("" + result.getCode()));

                    if(imputAuthorizationCodeDialog != null){
                        imputAuthorizationCodeDialog.clearPassword();
                    }
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
                finish();
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                closeProgressDialog();
                //提示提交授权码失败并退出
                ToastUtil.showToast(activity, getResources().getString(R.string.net_error_tip));
                finish();
            }

            @Override
            public void onFinished() {
            }
        });


    }

    @Override
    public void onBackPressed() {
        if(searchFragment != null && searchFragment.isVisible()){
            if(onSearchStatusChangeListener != null){
                onSearchStatusChangeListener.onCancel();
                return;
            }
        }
        super.onBackPressed();
    }
}
