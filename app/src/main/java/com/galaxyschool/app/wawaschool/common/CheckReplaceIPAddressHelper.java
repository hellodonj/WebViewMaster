package com.galaxyschool.app.wawaschool.common;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;

import com.duowan.mobile.netroid.NetroidError;
import com.galaxyschool.app.wawaschool.chat.DemoApplication;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.ApplicationModelSettingFragment;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.lqwawa.lqbaselib.net.library.ResourceResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ======================================================
 * Created by : Brave_Qu on 2017/11/6 14:14
 * E-Mail Address:863378689@qq.com
 * Describe:判断是否可以替换成内网IP访问
 * ======================================================
 */

public class CheckReplaceIPAddressHelper {
    private Activity activity;
    private int resId;
    private int resType;
    private int fileSize;
    //校内网模式的IP地址
    private String headUrl;
    private CallbackListener callBackListener;

    public CheckReplaceIPAddressHelper(Activity activity) {
        this.activity = activity;
    }

    public CheckReplaceIPAddressHelper setResId(int resId) {
        this.resId = resId;
        return this;
    }

    public CheckReplaceIPAddressHelper setResType(int resType) {
        this.resType = resType;
        return this;
    }

    public CheckReplaceIPAddressHelper setFileSize(int fileSize){
        this.fileSize = fileSize;
        return this;
    }

    public CheckReplaceIPAddressHelper setCallBackListener(CallbackListener callBackListener) {
        this.callBackListener = callBackListener;
        return this;
    }

    /**
     * 替换成内网的IP形式
     *
     * @param resUrl 源resUrl
     * @return 替换后的resUrl
     */
    public String getChangeIPUrl(String resUrl) {
        if (!TextUtils.isEmpty(resUrl) && resUrl.contains("http:")) {
            String[] splitArray = resUrl.split("/");
            if (splitArray != null && splitArray.length > 0) {
                for (int i = 0, len = splitArray.length; i < len; i++) {
                    String tempData = splitArray[i];
                    if (!TextUtils.isEmpty(tempData) && tempData.contains(".com")) {
                        resUrl = resUrl.replace(tempData, headUrl);
                        break;
                    }
                }
            }
        }
        return resUrl;
    }

    public List<String> getChangeIPUrlArray(List<String> urlArray) {
        List<String> newIpUrl = new ArrayList<>();
        for (int i = 0, len = urlArray.size(); i < len; i++) {
            newIpUrl.add(getChangeIPUrl(urlArray.get(i)));
        }
        return newIpUrl;
    }

    public void checkIP() {
        //判断当前WiFi是不是打开的状态
        boolean isWiFiOpen = NetworkHelper.isWifiConnected(activity);
        if (isWiFiOpen) {
            if (judgeCurrentApplicationModel()) {
                //校内网模式
                checkResInfoStatus();
            } else {
                //判断通用模式下当前的校内网有没有通的IP可以访问
                if (CheckInsideWiFiUtil.currentInsideWiFiConnect && !TextUtils.isEmpty
                        (CheckInsideWiFiUtil.insideWiFiHeadUrl)) {
                    this.headUrl = CheckInsideWiFiUtil.insideWiFiHeadUrl;
                    checkResInfoStatus();
                } else {
                    callBackListener.onBack(false);
                }
            }
        } else {
            callBackListener.onBack(false);
        }
    }

    /**
     * 判断当前应用场景的模式
     *
     * @return model (通用模式、校内模式)
     */
    private boolean judgeCurrentApplicationModel() {
        PrefsManager prefsManager = DemoApplication.getInstance().getPrefsManager();
        String memberId = DemoApplication.getInstance().getMemberId();
        int currentModel = -1;
        if (TextUtils.isEmpty(memberId)) {
            return false;
        }
        if (prefsManager != null && !TextUtils.isEmpty(memberId)) {
            String tempData = prefsManager.getCurrentApplicationModel(activity, memberId);
            if (!TextUtils.isEmpty(tempData)) {
                currentModel = Integer.valueOf(tempData);
            }
            if (currentModel == ApplicationModelSettingFragment.ApplicationModel.CAMPUS_MODEL) {
                //校内网模式的IP
                headUrl = prefsManager.getCampusModelIp(activity, memberId);
                if (!TextUtils.isEmpty(headUrl)) {
                    return true;
                }
            } else {
                return false;
            }
        }
        return false;
    }

    /**
     * 校验资源压缩包的状态
     */
    private void checkResInfoStatus() {
        Map param = new HashMap();
        param.put("resId", resId);
        param.put("resType", resType);
        param.put("fileSize",fileSize);
        String requestUrl = "http://" + headUrl + ":8080" + ServerUrl.CHECK_RESINFO_DO_EXIST_BODY_URL;
        RequestHelper.RequestListener listener =
                new RequestHelper.RequestResourceResultListener<ResourceResult>(
                        activity, ResourceResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (jsonString == null) {
                            callBackListener.onBack(false);
                        } else {
                            try {
                                JSONObject jsonObject = new JSONObject(jsonString);
                                JSONObject data = jsonObject.getJSONObject("data");
                                int status = data.optInt("status");
                                if (status == 1) {
                                    callBackListener.onBack(true);
                                } else {
                                    callBackListener.onBack(false);
                                }
                            } catch (JSONException e) {
                                callBackListener.onBack(false);
                                e.printStackTrace();
                            }
                        }
                    }
                    @Override
                    public void onError(NetroidError error) {
                        callBackListener.onBack(false);
                    }
                };
        listener.setShowErrorTips(false);
        listener.setShowLoading(true);
        listener.setTimeOutMs(10000);
        RequestHelper.sendGetRequest(activity, requestUrl, param, listener);
    }
}
