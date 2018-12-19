package com.galaxyschool.app.wawaschool.common;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.duowan.mobile.netroid.NetroidError;
import com.galaxyschool.app.wawaschool.chat.DemoApplication;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.ApplicationModelSettingFragment;
import com.galaxyschool.app.wawaschool.pojo.SchoolInfo;
import com.galaxyschool.app.wawaschool.pojo.SubscribeSchoolListResult;
import com.lqwawa.lqbaselib.net.library.ModelResult;
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
 * Created by : Brave_Qu on 2017/11/30 10:32
 * E-Mail Address:863378689@qq.com
 * Describe:检验在通用模式下内网的ip是否可用以及校验返回的内网ip是否通
 * ======================================================
 */

public class CheckInsideWiFiUtil {
    private Activity activity;
    private String memberId;
    //获取所有学校内网IP加起来的集合
    private List<String> insideIPList = new ArrayList<>();
    //当前的内网有没有连接
    public static boolean currentInsideWiFiConnect;
    //连接上的内网IP
    public static String insideWiFiHeadUrl;
    private PrefsManager prefsManager;

    public static CheckInsideWiFiUtil getInstance() {
        return CheckInsideWiFiUtilHolder.instance;
    }

    private static class CheckInsideWiFiUtilHolder {
        private static CheckInsideWiFiUtil instance = new CheckInsideWiFiUtil();
    }

    public CheckInsideWiFiUtil setActivity(Activity activity) {
        this.activity = activity;
        return this;
    }

    public CheckInsideWiFiUtil setMemberId(String memberId) {
        this.memberId = memberId;
        return this;
    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            int what = msg.what + 1;
            if (what < insideIPList.size()) {
                checkInsideWiFiIPStatus(insideIPList.get(what), what);
            } else {
                currentInsideWiFiConnect = false;
            }
            return false;
        }
    });

    /**
     * 开启校验内网数据（首次判断当前校内网的模式 通用模式下去校验）
     */
    public void checkData() {
        if (prefsManager == null) {
            prefsManager = DemoApplication.getInstance().getPrefsManager();
        }
        if (TextUtils.isEmpty(memberId)) {
            return;
        }
        //当前的应用模式
        int currentModel = -1;
        String tempData = prefsManager.getCurrentApplicationModel(activity, memberId);
        if (!TextUtils.isEmpty(tempData)) {
            currentModel = Integer.valueOf(tempData);
        }
        //如果当前模式是校内网模式直接返回
        if (currentModel == ApplicationModelSettingFragment.ApplicationModel.CAMPUS_MODEL) {
            return;
        }
        //判断之前有没有存储过校内网的IP
        String headUrl = prefsManager.getSchoolInsideWiFiHeadUrl(activity, memberId);
        if (TextUtils.isEmpty(headUrl)) {
            //之前没有保存过
            loadHeadUrlData();
        } else {
            //之前保存过
            checkInsideWiFiIPStatus(headUrl, -1);
        }
    }

    /**
     * 加载之前保存过的内网IP记录
     */
    private void loadHeadUrlData() {
        List<SchoolInfo> allSchoolInfoList = prefsManager.getAllSchoolInfoList(memberId);
        if (allSchoolInfoList == null || allSchoolInfoList.size() == 0) {
            loadAllSchoolInfo();
        } else {
            analysisSchoolInfoIPData(allSchoolInfoList);
        }
    }

    private void loadAllSchoolInfo() {
        Map<String, Object> param = new HashMap<>();
        param.put("MemberId", memberId);
        RequestHelper.RequestModelResultListener listener = new RequestHelper
                .RequestModelResultListener(activity, ModelResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                ModelResult<SubscribeSchoolListResult.SubscribeSchoolListModel> schoolInfoResult =
                        JSON.parseObject(jsonString, new
                                TypeReference<ModelResult<SubscribeSchoolListResult.SubscribeSchoolListModel>>() {
                                });
                if (schoolInfoResult != null && schoolInfoResult.isSuccess()) {
                    List<SchoolInfo> schoolInfos = schoolInfoResult.getModel().getSubscribeNoList();
                    if (schoolInfos != null && schoolInfos.size() > 0) {
                        prefsManager.setDataList(memberId + PrefsManager.PrefsItems.ALL_SCHOOLINFO_LIST, schoolInfos);
                        analysisSchoolInfoIPData(schoolInfos);
                    }
                }

            }
        };
        listener.setShowErrorTips(false);
        RequestHelper.sendPostRequest(activity, ServerUrl.SUBSCRIBE_SCHOOL_LIST_URL, param, listener);
    }

    /**
     * 提取schoolInfo的内网IP
     *
     * @param schoolInfoList
     */
    private void analysisSchoolInfoIPData(List<SchoolInfo> schoolInfoList) {
        insideIPList.clear();
        if (schoolInfoList != null && schoolInfoList.size() > 0) {
            for (int i = 0,len = schoolInfoList.size();i < len;i++){
                SchoolInfo schoolInfo = schoolInfoList.get(i);
                if (schoolInfo != null && !TextUtils.isEmpty(schoolInfo.getSchoolIntranetIP())){
                    insideIPList.add(schoolInfo.getSchoolIntranetIP());
                }
            }
            if (insideIPList.size() == 0) return;
            checkInsideWiFiIPStatus(insideIPList.get(0), 0);
        }
    }

    /**
     * 校验内网IP是否通了
     *
     * @param headUrl  内网IP
     * @param position 在集合中的位置
     */
    private void checkInsideWiFiIPStatus(final String headUrl, final int position) {
        String requestUrl = "http://" + headUrl + ":8080" + ServerUrl.CHECKOUT_INSIDE_IP_DO_EXIST_URL;
        RequestHelper.RequestListener listener =
                new RequestHelper.RequestResourceResultListener<ResourceResult>(
                        activity, ResourceResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
//                        Log.d("TTT", "打印网络请求===》》" + headUrl);
                        if (jsonString == null) {
                            if (position == -1) {
                                loadHeadUrlData();
                            } else {
                                mHandler.sendEmptyMessage(position);
                            }
                        } else {
                            try {
                                JSONObject jsonObject = new JSONObject(jsonString);
                                int code = jsonObject.optInt("code");
                                if (code == 0) {
                                    //成功
                                    currentInsideWiFiConnect = true;
                                    insideWiFiHeadUrl = headUrl;
                                    prefsManager.saveSchoolInsideWiFiHeadUrl(activity, memberId, headUrl);
                                } else {
                                    //失败
                                    if (position == -1) {
                                        loadHeadUrlData();
                                    } else {
                                        mHandler.sendEmptyMessage(position);
                                    }
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(NetroidError error) {
//                        Log.d("TTT", "打印网络请求===》+ error+ 》" + headUrl);
                        if (position == -1) {
                            loadHeadUrlData();
                        } else {
                            mHandler.sendEmptyMessage(position);
                        }
                    }
                };
        listener.setShowErrorTips(false);
        listener.setTimeOutMs(10000);
        RequestHelper.sendGetRequest(activity, requestUrl, null, listener);
    }
}
