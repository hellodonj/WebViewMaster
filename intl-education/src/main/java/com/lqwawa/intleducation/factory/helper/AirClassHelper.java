package com.lqwawa.intleducation.factory.helper;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.vo.LqResponseDataVo;
import com.lqwawa.intleducation.base.vo.PagerArgs;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.LogUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.StringCallback;
import com.lqwawa.intleducation.factory.data.entity.JoinClassEntity;
import com.lqwawa.intleducation.factory.data.entity.LQwawaBaseEntity;
import com.lqwawa.intleducation.module.learn.vo.LiveVo;
import com.lqwawa.intleducation.module.learn.vo.WawaLiveListVo;
import com.lqwawa.lqbaselib.net.ErrorCodeUtil;

import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.List;
import java.util.Map;

/**
 * 空中课堂相关的Api请求帮助类
 */
public class AirClassHelper {

    /**
     * 根据班级Id(批量)拉取空中课堂课程表日期标记
     *
     * @param listId    班级Id集合
     * @param beginTime 开始时间
     * @param endTime   结束时间
     * @param callback  回调对象
     */
    public static void requestTimeTableFlags(@NonNull List<String> listId,
                                             @NonNull String beginTime,
                                             @NonNull String endTime,
                                             @NonNull DataSource.Callback<List<String>> callback) {

        // 准备数据
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("ClassIdList", listId);
        jsonObject.put("StartTimeBegin", beginTime);
        jsonObject.put("StartTimeEnd", endTime);

        RequestParams params = new RequestParams(AppConfig.ServerUrl.PostRequestAirClassTimeTableFlags);
        params.setAsJsonContent(true);
        params.setBodyContent(jsonObject.toJSONString());
        params.setConnectTimeout(10000);
        LogUtil.i(AirClassHelper.class, "send request ==== " + params.getUri());
        x.http().post(params, new StringCallback<String>() {

            @Override
            public void onSuccess(String str) {
                LogUtil.i(AirClassHelper.class, "request " + params.getUri() + " result :" + str);
                TypeReference<LqResponseDataVo<List<String>>> typeReference = new TypeReference<LqResponseDataVo<List<String>>>() {
                };
                LqResponseDataVo<List<String>> vo = JSON.parseObject(str, typeReference);
                if (vo.isSucceed()) {
                    if (callback != null && EmptyUtil.isNotEmpty(vo.getModel())) {
                        callback.onDataLoaded(vo.getModel().getData());
                    }
                } else {
                    String ErrorMessage = (String) vo.getErrorMessage();
                    Map<String, String> errorHashMap = ErrorCodeUtil.getInstance().getErrorCodeMap();
                    if (errorHashMap != null && errorHashMap.size() > 0 && !TextUtils.isEmpty(ErrorMessage)
                            && errorHashMap.containsKey(ErrorMessage)) {
                        UIUtil.showToastSafe(errorHashMap.get(ErrorMessage));
                    }
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.w(AirClassHelper.class, "request " + params.getUri() + " failed");
                if (null != callback) {
                    callback.onDataNotAvailable(R.string.net_error_tip);
                }
            }
        });
    }

    /**
     * 根据班级Id(批量)拉取空中课堂课程表日期标记
     * @param pageIndex 分页数
     * @param pageSize 每页数量
     * @param listId    班级Id集合
     * @param beginTime 开始时间
     * @param endTime   结束时间
     * @param callback  回调对象
     */
    public static void requestAirClassDataWithTimeTable(int pageIndex,int pageSize,
                                             @NonNull List<String> listId,
                                             @NonNull String beginTime,
                                             @NonNull String endTime,
                                             @NonNull DataSource.Callback<List<LiveVo>> callback) {

        // 准备数据
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("ClassIdList", listId);
        jsonObject.put("StartTimeBegin", beginTime);
        jsonObject.put("StartTimeEnd", endTime);
        jsonObject.put("Pager",new PagerArgs(pageIndex, pageSize));

        RequestParams params = new RequestParams(AppConfig.ServerUrl.PostRequestAirClassData);
        params.setAsJsonContent(true);
        params.setBodyContent(jsonObject.toJSONString());
        params.setConnectTimeout(10000);
        LogUtil.i(AirClassHelper.class, "send request ==== " + params.getUri());
        x.http().post(params, new StringCallback<String>() {

            @Override
            public void onSuccess(String str) {
                LogUtil.i(AirClassHelper.class, "request " + params.getUri() + " result :" + str);
                TypeReference<LqResponseDataVo<List<LiveVo>>> typeReference = new TypeReference<LqResponseDataVo<List<LiveVo>>>() {
                };
                LqResponseDataVo<List<LiveVo>> vo = JSON.parseObject(str, typeReference);
                if (vo.isSucceed()) {
                    if (callback != null && EmptyUtil.isNotEmpty(vo.getModel())) {
                        callback.onDataLoaded(vo.getModel().getData());
                    }
                } else {
                    String ErrorMessage = (String) vo.getErrorMessage();
                    Map<String, String> errorHashMap = ErrorCodeUtil.getInstance().getErrorCodeMap();
                    if (errorHashMap != null && errorHashMap.size() > 0 && !TextUtils.isEmpty(ErrorMessage)
                            && errorHashMap.containsKey(ErrorMessage)) {
                        UIUtil.showToastSafe(errorHashMap.get(ErrorMessage));
                    }
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.w(AirClassHelper.class, "request " + params.getUri() + " failed");
                if (null != callback) {
                    callback.onDataNotAvailable(R.string.net_error_tip);
                }
            }
        });
    }

}
