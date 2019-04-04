package com.galaxyschool.app.wawaschool.helper;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.galaxyschool.app.wawaschool.HomeActivity;
import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.chat.DemoApplication;
import com.galaxyschool.app.wawaschool.common.CallbackListener;
import com.galaxyschool.app.wawaschool.common.CheckInsideWiFiUtil;
import com.galaxyschool.app.wawaschool.common.DataMigrationUtils;
import com.galaxyschool.app.wawaschool.common.DialogHelper;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.MySchoolSpaceFragment;
import com.galaxyschool.app.wawaschool.jpush.PushUtils;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.galaxyschool.app.wawaschool.pojo.UserInfoResult;
import com.galaxyschool.app.wawaschool.views.ContactsMessageDialog;
import com.lqwawa.lqbaselib.net.library.ModelResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.lqwawa.mooc.common.MOOCHelper;
import com.oosic.apps.share.ShareHelper;
import com.osastudio.common.utils.TipMsgHelper;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.HashMap;
import java.util.Map;

/**
 * ======================================================
 * Created by : Brave_Qu on 2018/9/4 0004 15:04
 * Describe:第三方登录辅助工具类
 * ======================================================
 */
public class ThirdPartyLoginHelper {
    private Activity mContext;
    private SHARE_MEDIA mediaType;
    private boolean isBackHome = true;
    private DialogHelper.LoadingDialog loadingDialog;
    private int functionType = FUNCTION_TYPE.THIRDPARTY_LOGIN;
    private CallbackListener callbackListener;
    private String Unionid;
    public interface FUNCTION_TYPE{
        int THIRDPARTY_LOGIN = 0;//三方登录
        int DELETE_AUTH = 1;//删除授权
        int BIND_AUTH = 2;//绑定授权
    }
    public ThirdPartyLoginHelper(Activity mContext) {
        this.mContext = mContext;
    }

    public ThirdPartyLoginHelper setShareMediaType(SHARE_MEDIA mediaType) {
        this.mediaType = mediaType;
        return this;
    }

    public ThirdPartyLoginHelper setFunctionType(int functionType) {
        this.functionType = functionType;
        return this;
    }

    public ThirdPartyLoginHelper setCallBackListener(CallbackListener listener) {
        this.callbackListener = listener;
        return this;
    }

    public ThirdPartyLoginHelper setUnionid(String Unionid) {
        this.Unionid = Unionid;
        return this;
    }

    public ThirdPartyLoginHelper setIsBackHome(boolean isBackHome) {
        this.isBackHome = isBackHome;
        return this;
    }

    public void start() {
        if (mediaType == SHARE_MEDIA.QQ){
            //校验qq的安装情况
            if (!ShareHelper.isQQInstall(mContext)){
                return;
            }
        } else if (mediaType == SHARE_MEDIA.WEIXIN){
            //校验winxin的安装情况
            if (!ShareHelper.isWeChatInstall(mContext)){
                return;
            }
        }
        deleteAuthMediaData();
    }

    private void loadThirdLogin(){
        UMShareAPI.get(mContext).doOauthVerify(mContext, mediaType, new UMAuthListener() {
            @Override
            public void onStart(SHARE_MEDIA share_media) {
                showLoadingDialog();
            }

            @Override
            public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
                dismissLoadingDialog();
                loadAuthDataDetail();
            }

            @Override
            public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
                dismissLoadingDialog();
            }

            @Override
            public void onCancel(SHARE_MEDIA share_media, int i) {
                dismissLoadingDialog();
            }
        });
    }

    private void loadAuthDataDetail() {
        UMShareAPI.get(mContext).getPlatformInfo(mContext, mediaType, new UMAuthListener() {
            @Override
            public void onStart(SHARE_MEDIA share_media) {
                showLoadingDialog();
            }

            @Override
            public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
                loginOrBindAuth(map);
                dismissLoadingDialog();
            }

            @Override
            public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
                dismissLoadingDialog();
                if (!TextUtils.isEmpty(throwable.getMessage())) {
                    TipMsgHelper.ShowMsg(mContext, throwable.getMessage());
                }
            }

            @Override
            public void onCancel(SHARE_MEDIA share_media, int i) {
                dismissLoadingDialog();
            }
        });
    }

    private void deleteAuthMediaData() {
        UMShareAPI.get(mContext).deleteOauth(mContext, mediaType, new UMAuthListener() {
            @Override
            public void onStart(SHARE_MEDIA platform) {
                showLoadingDialog();
            }

            @Override
            public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
                dismissLoadingDialog();
                if (functionType == FUNCTION_TYPE.DELETE_AUTH) {
                    deleteServerAccountData();
                } else {
                    loadAuthDataDetail();
                }
            }

            @Override
            public void onError(SHARE_MEDIA platform, int action, Throwable t) {
                dismissLoadingDialog();
                if (!TextUtils.isEmpty(t.getMessage())) {
                    TipMsgHelper.ShowMsg(mContext, t.getMessage());
                }
            }

            @Override
            public void onCancel(SHARE_MEDIA platform, int action) {
                dismissLoadingDialog();
            }
        });
    }

    private void deleteServerAccountData(){
        if (TextUtils.isEmpty(Unionid)){
            return;
        }
        Map<String,Object> param = new HashMap<>();
        param.put("MemberId",DemoApplication.getInstance().getMemberId());
        param.put("Unionid",Unionid);
        RequestHelper.RequestModelResultListener listener = new RequestHelper
                .RequestModelResultListener<ModelResult>(mContext, ModelResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                if (TextUtils.isEmpty(jsonString)){
                    return;
                }
                try {
                    JSONObject jsonObject = JSONObject.parseObject(jsonString);
                    boolean hasError = jsonObject.getBoolean("HasError");
                    if (!hasError){
                        callbackListener.onBack(true);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        RequestHelper.sendPostRequest(mContext, ServerUrl.GET_UNBIND_THIRDPARTY_AUTHORIZATION, param, listener);
    }

    private void loginOrBindAuth(Map<String, String> map) {
        String uid = map.get("uid");
        String openid = map.get("openid");//微博没有
        String unionid = map.get("unionid");//微博没有
        String access_token = map.get("access_token");
        String expires_in = map.get("expires_in");
        String name = map.get("name");
        String gender = map.get("gender");
        String iconurl = map.get("iconurl");
        StringBuilder sb = new StringBuilder();
        for (String key : map.keySet()) {
            sb.append(key).append(" : ").append(map.get(key)).append("\n");
        }
        Map<String, Object> mParams = new HashMap<String, Object>();
        if (functionType == FUNCTION_TYPE.BIND_AUTH){
            mParams.put("MemberId", DemoApplication.getInstance().getMemberId());
        }
        final String mediaUnonId =  map.get("unionid");
        mParams.put("Unionid",mediaUnonId);
        mParams.put("IdentityType", mediaType == SHARE_MEDIA.QQ ? 2 : 1);
        mParams.put("NickName", map.get("name"));
        mParams.put("HeadPicUrl", map.get("iconurl"));
        if (TextUtils.equals(map.get("gender"), "女")) {
            mParams.put("Sex", 0);
        } else {
            mParams.put("Sex", 1);
        }
        mParams.put("Country", "");
        mParams.put("Province", map.get("province"));
        mParams.put("City", map.get("city"));
        RequestHelper.RequestModelResultListener listener = new RequestHelper.RequestModelResultListener<UserInfoResult>(mContext, UserInfoResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                super.onSuccess(jsonString);
                if (TextUtils.isEmpty(jsonString)){
                    return;
                }
                UserInfoResult userInfoResult = getResult();
                if (userInfoResult != null) {
                    if (userInfoResult.isHasError()){
                        if (functionType == FUNCTION_TYPE.BIND_AUTH) {
                            //绑定授权
                            loadBindAuthUserInfo(mediaUnonId);
                        } else {
                            TipMsgHelper.ShowMsg(mContext, userInfoResult.getErrorMessage());
                        }
                        return;
                    }
                    if (functionType == FUNCTION_TYPE.THIRDPARTY_LOGIN) {
                        UserInfo userInfo = userInfoResult.getModel();
                        if (userInfo != null) {
                            loginSuccess(userInfo);
                        }
                    } else if (functionType == FUNCTION_TYPE.BIND_AUTH){
                        //提示绑定成功
                        if (callbackListener != null) {
                            callbackListener.onBack(true);
                        }
                    }
                }
            }
        };
        listener.setShowLoading(true);
        listener.setShowErrorTips(false);
        String url = ServerUrl.GET_THIRD_PARTY_AUTHORIZED_LOGIN_BASE_URL;
        if (functionType == FUNCTION_TYPE.BIND_AUTH){
            url = ServerUrl.GET_BIND_THIRDPARTY_AUTHORIZATION;
        }
        RequestHelper.sendPostRequest(mContext, url, mParams, listener);
    }

    private void loginSuccess(UserInfo userInfo){
        MyApplication myApp = ((MyApplication) mContext.getApplication());
        myApp.setUserInfo(userInfo);
        myApp.getPrefsManager().setUserPassword(userInfo.getPassword());
        TipMsgHelper.ShowMsg(mContext, mContext.getString(R.string.login_success));
        myApp.startDownloadService();
        MyApplication.saveSchoolInfoList(mContext, userInfo.getSchoolList());
        //登录成功之后给lqmooc初始化用户信息
        MOOCHelper.init(userInfo);
        //登录成功后同步迁移本地课件
        DataMigrationUtils.processLocalCourseData(mContext, userInfo.getMemberId());
        //通知校园空间加载学校数据
        MySchoolSpaceFragment.sendBrocast(mContext);
        //校验判断校内网是不是一个连接的状态
        CheckInsideWiFiUtil.getInstance().
                setMemberId(userInfo.getMemberId()).
                setActivity(mContext).
                checkData();
        PushUtils.resumePush(mContext);
        if (isBackHome) {
            Intent intent = new Intent(mContext, HomeActivity.class);
            if (userInfo.isNewCreateUser()) {
                mContext.sendBroadcast(new Intent(HomeActivity.EXTRA_THIRD_LOGIN_TIP_MESSAGE));
            }
            mContext.startActivity(intent);
        }
        if (mContext != null) {
            mContext.setResult(Activity.RESULT_OK);
            mContext.finish();
        }
    }

    private void loadBindAuthUserInfo(String unionid) {
        Map<String, Object> mParams = new HashMap<String, Object>();
        mParams.put("Unionid", unionid);
        if (mediaType == SHARE_MEDIA.QQ) {
            mParams.put("IdentityType", 2);
        } else if (mediaType == SHARE_MEDIA.WEIXIN) {
            mParams.put("IdentityType", 1);
        }
        RequestHelper.RequestModelResultListener listener = new RequestHelper.RequestModelResultListener<UserInfoResult>(mContext, UserInfoResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                if (!TextUtils.isEmpty(jsonString)) {
                    JSONObject jsonObject = JSONObject.parseObject(jsonString);
                    boolean hasError = jsonObject.getBoolean("HasError");
                    if (!hasError) {
                        JSONObject modelObj = jsonObject.getJSONObject("Model");
                        if (modelObj != null) {
                            JSONArray dataList = modelObj.getJSONArray("DataList");
                            if (dataList != null && dataList.size() > 0) {
                                JSONObject bindInfo = dataList.getJSONObject(0);
                                if (bindInfo != null) {
                                    String bindId = bindInfo.getString("MemberId");
                                    String bindName = bindInfo.getString("RealName");
                                    String bindAccount = bindInfo.getString("NickName");
                                    String bindUserName = null;
                                    if (TextUtils.isEmpty(bindName)){
                                        bindUserName = bindAccount;
                                    } else {
                                        bindUserName = bindName + " " + bindAccount;
                                    }
                                    String shareTypeString  = mContext.getString(R.string.str_qq);
                                    if (mediaType == SHARE_MEDIA.WEIXIN) {
                                        shareTypeString = mContext.getString(R.string.str_weixin);
                                    }
                                    String message = mContext.getString(R.string.str_third_account_already_bind,shareTypeString,bindUserName);
                                    if (!TextUtils.isEmpty(bindId)) {
                                        popBindSelfDialog(message, bindId,unionid);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        };
        RequestHelper.sendPostRequest(mContext, ServerUrl.LOAD_ASSOCIATED_BY_UNIONID_BASE_URL, mParams, listener);
    }

    private void popBindSelfDialog(String message,String memberId,String unionid) {
        ContactsMessageDialog messageDialog = new ContactsMessageDialog(
                mContext,
                null,
                message,
                mContext.getString(R.string.cancel),
                (dialog, which) -> {
                    dialog.dismiss();
                },
                mContext.getString(R.string.str_bind_current_account),
                (dialog, which) -> {
                    dialog.dismiss();
                    bindCurrentAccount(memberId,unionid);
                });
        messageDialog.resizeDialog(0.8f);
        messageDialog.show();
    }

    private void bindCurrentAccount(String memberId,String unionid){
        Map<String, Object> mParams = new HashMap<String, Object>();
        mParams.put("MemberId", memberId);
        mParams.put("NewMemberId", DemoApplication.getInstance().getMemberId());
        mParams.put("Unionid", unionid);
        RequestHelper.RequestModelResultListener listener = new RequestHelper.RequestModelResultListener<UserInfoResult>(mContext, UserInfoResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                if (!TextUtils.isEmpty(jsonString)) {
                    JSONObject jsonObject = JSONObject.parseObject(jsonString);
                    boolean hasError = jsonObject.getBoolean("HasError");
                    if (!hasError) {
                        if (callbackListener != null) {
                            callbackListener.onBack(true);
                        }
                    }
                }
            }
        };
        listener.setShowLoading(true);
        RequestHelper.sendPostRequest(mContext, ServerUrl.GET_BIND_NEWACCOUNT_FOR_UNIONID_BASE_URL, mParams, listener);
    }

    private void showLoadingDialog() {
        loadingDialog = DialogHelper.getIt(mContext).GetLoadingDialog(0);
    }

    private void dismissLoadingDialog() {
        try {
            if (this.loadingDialog != null && this.loadingDialog.isShowing()) {
                this.loadingDialog.dismiss();
                this.loadingDialog = null;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
