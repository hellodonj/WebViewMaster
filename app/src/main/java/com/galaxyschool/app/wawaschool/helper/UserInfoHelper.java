package com.galaxyschool.app.wawaschool.helper;
import android.content.Context;
import com.galaxyschool.app.wawaschool.chat.DemoApplication;
import com.galaxyschool.app.wawaschool.common.CallbackListener;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.galaxyschool.app.wawaschool.pojo.UserInfoResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import java.util.HashMap;
import java.util.Map;

/**
 * ======================================================
 * Describe:用户信息辅助类
 * ======================================================
 */
public class UserInfoHelper {
    private Context mContext;
    private CallbackListener listener;
    private UserInfo userInfo;

    public UserInfoHelper(Context mContext) {
        this.mContext = mContext;
    }

    public UserInfoHelper setCallBackListener(CallbackListener listener){
        this.listener = listener;
        return this;
    }

    public void check(){
        loadUserInfo();
    }

    private void loadUserInfo() {
        Map<String, Object> params = new HashMap<>();
        params.put("UserId", DemoApplication.getInstance().getMemberId());
        RequestHelper.RequestModelResultListener resultListener = new RequestHelper
                .RequestModelResultListener<UserInfoResult>(mContext,UserInfoResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                super.onSuccess(jsonString);
                if (getResult() == null || !getResult().isSuccess()
                        || getResult().getModel() == null) {
                    return;
                }
                userInfo = getResult().getModel();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (listener != null){
                    listener.onBack(userInfo);
                }
            }
        };
        resultListener.setShowLoading(false);
        resultListener.setShowErrorTips(false);
        RequestHelper.sendPostRequest(mContext, ServerUrl.LOAD_USERINFO_URL, params,resultListener);
    }
}
