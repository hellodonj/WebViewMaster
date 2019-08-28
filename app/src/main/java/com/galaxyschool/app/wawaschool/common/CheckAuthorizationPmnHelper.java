package com.galaxyschool.app.wawaschool.common;

import android.app.Activity;
import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.galaxyschool.app.wawaschool.R;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.base.utils.ToastUtil;
import com.lqwawa.intleducation.base.vo.RequestVo;
import com.lqwawa.intleducation.base.vo.ResponseVo;
import com.lqwawa.intleducation.module.discovery.ui.ImputAuthorizationCodeDialog;
import com.lqwawa.intleducation.module.discovery.vo.AuthorizationVo;
import com.lqwawa.tools.DialogHelper;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.HashMap;
import java.util.Locale;

/**
 * ======================================================
 * Created by : Brave_Qu on 2017/10/16 18:04
 * E-Mail Address:863378689@qq.com
 * Describe:校验精品资源库授权
 * ======================================================
 */

public class CheckAuthorizationPmnHelper {
    private DialogHelper.LoadingDialog progressDialog;
    private ImputAuthorizationCodeDialog imputAuthorizationCodeDialog;
    private Context mContext;
    private String schoolId;
    private String memberId;
    private boolean isNeedInputCode = true;
    private checkResultListener listener;

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

    public CheckAuthorizationPmnHelper(Context context) {
        this.mContext = context;
    }
    public CheckAuthorizationPmnHelper setSchoolId(String schoolId){
        this.schoolId = schoolId;
        return this;
    }
    public CheckAuthorizationPmnHelper setMemberId(String memberId){
        this.memberId = memberId;
        return this;
    }
    public CheckAuthorizationPmnHelper setListener(checkResultListener listener){
        this.listener = listener;
        return this;
    }
    public CheckAuthorizationPmnHelper setIsNeedInputCode(boolean isNeedInputCode){
        this.isNeedInputCode = isNeedInputCode;
        return this;
    }

    public void check(){
        checkAuthorization();
    }

    /**
     * 检查机构权限
     */
    private void checkAuthorization(){
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("memberId", memberId);
        requestVo.addParams("organId", schoolId);
        requestVo.addParams("type", 1);
        RequestParams params = new RequestParams(
                AppConfig.ServerUrl.checkAuthorization + requestVo.getParams());
        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                closeProgressDialog();
                AuthorizationVo result = JSON.parseObject(s,
                        new TypeReference<AuthorizationVo>() { });
                if (result.getCode() == 0) {
                    if(result.isIsAuthorized()){//有授权
                        listener.onResult(true);
                    }else{
                        String errorInfo = mContext.getResources()
                                .getString(R.string.imput_authorization_title);
                        if (result.isIsExist()) {
                            errorInfo = mContext.getResources()
                                    .getString(R.string.authorization_out_time_tip);
                        }
                        //判断是否需要输入授权码
                        if (isNeedInputCode) {
                            showAuthorizationImputDialog(errorInfo);
                        }else {
                            listener.onResult(false);
                        }
                    }
                }else{
                    //提示获取授权失败然后退出
                    ToastUtil.showToast(mContext,
                            mContext.getResources().getString(R.string.get_authorization_fail_tip));
                    listener.onResult(false);
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                closeProgressDialog();
                //提示获取授权失败然后退出
                ToastUtil.showToast(mContext, mContext.getResources().getString(com.lqwawa
                        .intleducation
                        .R.string.net_error_tip));
                listener.onResult(false);
            }

            @Override
            public void onFinished() {
            }
        });
    }

    private void showAuthorizationImputDialog(String tipInfo){
        if(imputAuthorizationCodeDialog == null) {
            imputAuthorizationCodeDialog =
                    new ImputAuthorizationCodeDialog(mContext, tipInfo,1,
                            new ImputAuthorizationCodeDialog.CommitCallBack() {
                                @Override
                                public void onCommit(String code) {
                                    commitAuthorizationCode(code);
                                }

                                @Override
                                public void onCancel() {
                                    imputAuthorizationCodeDialog.setCommited(true);
                                    imputAuthorizationCodeDialog.dismiss();
                                    listener.onResult(false);
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
        requestVo.addParams("memberId",memberId);
        requestVo.addParams("organId", schoolId);
        requestVo.addParams("code", code);
        requestVo.addParams("type", 1);
        RequestParams params = new RequestParams(
                AppConfig.ServerUrl.commitAuthorizationCode + requestVo.getParams());
        params.setConnectTimeout(10000);
        showProgressDialog(mContext.getResources().getString(R.string.loading));
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
                    listener.onResult(true);
                }else{
                    String language = Locale.getDefault().getLanguage();
                    ToastUtil.showToast(mContext,
                            language.equals("zh") ?
                                    authorizationErrorMapZh.get("" + result.getCode())
                                    : authorizationErrorMapEn.get("" + result.getCode()));
                    if(imputAuthorizationCodeDialog != null){
                        imputAuthorizationCodeDialog.clearPassword();
//                        imputAuthorizationCodeDialog.dismiss();
                    }
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                closeProgressDialog();
                //提示提交授权码失败并退出
                ToastUtil.showToast(mContext, mContext.getResources().getString(R.string.net_error_tip));
                listener.onResult(false);
            }

            @Override
            public void onFinished() {

            }
        });


    }
    /**
     * 关闭提示框
     */
    private void closeProgressDialog() {
        if (progressDialog != null)
            progressDialog.dismiss();
    }


    /**
     * 显示提示框
     */
    private void showProgressDialog(String msg) {

        if (progressDialog == null) {
            progressDialog = DialogHelper.getIt((Activity) mContext).GetLoadingDialog(0);
        }
        this.progressDialog.setContent(msg);
        this.progressDialog.setCanceledOnTouchOutside(false);
        this.progressDialog.show();
    }

    public interface checkResultListener{
        void onResult(boolean isSuccess);
    }
}
