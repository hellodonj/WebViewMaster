package com.galaxyschool.app.wawaschool.jpush;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.galaxyschool.app.wawaschool.chat.DemoApplication;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.helper.PushOpenResourceHelper;
import com.galaxyschool.app.wawaschool.pojo.PushMessageInfo;
import com.lqwawa.lqbaselib.net.library.ModelResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import java.util.HashMap;
import java.util.Map;
import cn.jpush.android.api.JPushInterface;

public class PushUtils {

    public static void init(Context context){
        JPushInterface.init(context);
    }

    public static void stopPush(Context context){
        JPushInterface.stopPush(context);
    }

    public static void resumePush(Context context){
        JPushInterface.resumePush(context);
        initUserDeviceData(context, DemoApplication.getInstance().getMemberId());
    }

    public static boolean isPushStopped(Context context){
        return JPushInterface.isPushStopped(context);
    }

    public static String getRegistration(Context context){
        return JPushInterface.getRegistrationID(context);
    }

    /**
     * 初始化设备信息
     */
    public static void initUserDeviceData(Context context,String memberId){
        if (TextUtils.isEmpty(memberId)){
            return;
        }
        Map<String,Object> param = new HashMap<>();
        param.put("MemberId",memberId);
        param.put("RegistrationId",getRegistration(context));
        param.put("ApplicationType",1);
        RequestHelper.RequestModelResultListener listener = new RequestHelper
                .RequestModelResultListener(context,ModelResult.class){

        };
        listener.setShowErrorTips(false);
        listener.setShowLoading(false);
        RequestHelper.sendPostRequest(context, ServerUrl.ADD_MEMBER_JREGIST_BASE_URL,param, listener);
    }

    public static void openPushMessage(Context context,Bundle bundle){
        if (bundle != null){
            String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
            try {
                if (!TextUtils.isEmpty(extras)){
                    PushMessageInfo pushMessageInfo = JSONObject.parseObject(extras, PushMessageInfo.class);
                    if (pushMessageInfo != null){
                        PushOpenResourceHelper.getInstance().setContext(context)
                                .setPushMessageInfo(pushMessageInfo).open();

                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
