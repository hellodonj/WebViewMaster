package com.lqwawa.intleducation.module.discovery.tool;

import android.app.Activity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.vo.RequestVo;
import com.lqwawa.intleducation.base.vo.ResponseVo;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.module.discovery.ui.ImputAuthorizationCodeDialog;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.HashMap;
import java.util.Locale;

/**
 * 描述: 申请激活的帮助类
 * 作者|时间: djj on 2019/8/9 0009 下午 5:06
 */
public class ApplyActivationHelper {

    private Activity activity;
    private ImputAuthorizationCodeDialog imputAuthorizationCodeDialog;
    private String courseId;
    private String classId;
    private String schoolId;
    private CallbackListener listener;


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


    public ApplyActivationHelper setActivity(Activity activity) {
        this.activity = activity;
        return this;
    }

    public ApplyActivationHelper setCourseId(String courseId) {
        this.courseId = courseId;
        return this;
    }

    public ApplyActivationHelper setClassId(String classId) {
        this.classId = classId;
        return this;
    }

    public ApplyActivationHelper setSchoolId(String schoolId) {
        this.schoolId = schoolId;
        return this;
    }

    public ApplyActivationHelper setCallBackListener(CallbackListener listener) {
        this.listener = listener;
        return this;
    }


    public void requestActivationPermission() {
        String tipInfo = UIUtil.getString(R.string.label_activation_code_tip);
        if (imputAuthorizationCodeDialog == null) {
            imputAuthorizationCodeDialog = new ImputAuthorizationCodeDialog(activity, tipInfo,
                    new ImputAuthorizationCodeDialog.CommitCallBack() {
                        @Override
                        public void onCommit(String code) {
                            commitActivationCode(code);
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

    private void commitActivationCode(String code) {
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("courseId", courseId);
        requestVo.addParams("schoolId", schoolId);
        requestVo.addParams("classId", classId);
        requestVo.addParams("activeCode", code);
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.PostActivateSanxiCourse);
        params.setAsJsonContent(true);
        params.setBodyContent(requestVo.getParams());
        params.setConnectTimeout(10000);
        com.lqwawa.intleducation.common.utils.LogUtil.i(ApplyActivationHelper.class, "send request ==== " + params.getUri());
        x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                ResponseVo<String> results = JSON.parseObject(result,
                        new TypeReference<ResponseVo<String>>() {
                        });
                if (results.getCode() == 0) {
                    listener.onBack(results.getCode());
                    imputAuthorizationCodeDialog.dismiss();
                }
                else {
                    String language = Locale.getDefault().getLanguage();
                    //提示授权码错误原因然后退出
                    UIUtil.showToastSafe(language.equals("zh") ? authorizationErrorMapZh.get("" + results.getCode()) : authorizationErrorMapEn.get("" + results.getCode()));

                    if (imputAuthorizationCodeDialog != null) {
                        imputAuthorizationCodeDialog.clearPassword();
                    }
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                UIUtil.showToastSafe(R.string.net_error_tip);
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    public interface CallbackListener {
        void onBack(int result);
    }
}