package com.lqwawa.intleducation.factory.helper;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.vo.LqResponseDataVo;
import com.lqwawa.intleducation.base.vo.RequestVo;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.StringCallback;
import com.lqwawa.intleducation.factory.data.entity.LQwawaBaseResponse;
import com.lqwawa.intleducation.factory.data.entity.response.LQModelMultipleParamIncludePagerResponse;
import com.lqwawa.intleducation.factory.data.entity.response.LQResourceDetailVo;
import com.lqwawa.intleducation.module.learn.vo.LqTaskCommitListVo;
import com.lqwawa.intleducation.module.learn.vo.LqTaskInfoVo;
import com.lqwawa.lqbaselib.net.ErrorCodeUtil;

import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.List;
import java.util.Map;

/**
 * @author mrmedici
 * @desc 学习任务请求的帮助类
 */
public class LearningTaskHelper {

    /**
     * 根据任务Id,获取到相应的已经提交的任务
     * @param CommitTaskOnlineId 任务Id
     * @param callback 回调对象
     */
    public static void requestDeleteTask(@NonNull int CommitTaskOnlineId,
                                         @NonNull final DataSource.Callback<Boolean> callback) {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("CommitTaskOnlineId",CommitTaskOnlineId);
        RequestParams params = new RequestParams(AppConfig.ServerUrl.PostRequstCommittedTask);
        params.setAsJsonContent(true);
        params.setBodyContent(jsonObject.toJSONString());
        params.setConnectTimeout(10000);
        x.http().post(params, new StringCallback<String>() {
            @Override
            public void onSuccess(String str) {
                TypeReference<Map<String,Object>> mapTypeReference = new TypeReference<Map<String,Object>>(){};
                Map<String,Object> result = JSON.parseObject(str, mapTypeReference);
                if((int)result.get("ErrorCode") == 0){
                    if(callback != null){
                        callback.onDataLoaded(true);
                    }
                }else{
                    String ErrorMessage = (String) result.get("ErrorMessage");
                    Map<String, String> errorHashMap = ErrorCodeUtil.getInstance().getErrorCodeMap();
                    if (errorHashMap != null && errorHashMap.size() > 0 && !TextUtils.isEmpty(ErrorMessage)
                            && errorHashMap.containsKey(ErrorMessage)) {
                        UIUtil.showToastSafe(errorHashMap.get(ErrorMessage));
                    }
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                if(null != callback){
                    callback.onDataNotAvailable(R.string.net_error_tip);
                }
            }
        });
    }


    /**
     * 根据资源Id获取资源详情
     * @param resId 资源Id
     * @param needAnswer needAnswer  true 拿答题卡信息
     * @param callback 回调对象
     */
    public static void requestResourceDetailById(@NonNull String resId,
                                         @NonNull boolean needAnswer,
                                         @NonNull final DataSource.Callback<LQResourceDetailVo> callback) {

        RequestVo requestVo = new RequestVo();

        requestVo.addParams("resId",resId);
        requestVo.addParams("needAnswer",needAnswer);
        RequestParams params = new RequestParams(AppConfig.ServerUrl.GetResourceDetailByIdUrl + requestVo.getParams());
        params.setConnectTimeout(10000);
        x.http().get(params, new StringCallback<String>() {
            @Override
            public void onSuccess(String str) {
                TypeReference<LQResourceDetailVo> typeReference = new TypeReference<LQResourceDetailVo>(){};
                LQResourceDetailVo responseVo = JSON.parseObject(str, typeReference);
                if(responseVo.isSucceed()){
                    if(EmptyUtil.isNotEmpty(callback) && EmptyUtil.isNotEmpty(responseVo.getData())){
                        callback.onDataLoaded(responseVo);
                    }
                }else{
                    String message = responseVo.getMessage();
                    UIUtil.showToastSafe(message);
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                if(null != callback){
                    callback.onDataNotAvailable(R.string.net_error_tip);
                }
            }
        });
    }


    /**
     * 根据资源Id获取资源详情
     * @param taskId taskId
     * @param callback 回调对象
     */
    public static void requestTaskInfoByTaskId(@NonNull String taskId,
                                                 @NonNull final DataSource.Callback<LqTaskInfoVo> callback) {

        RequestVo requestVo = new RequestVo();

        requestVo.addParams("TaskId",taskId);
        RequestParams params = new RequestParams(AppConfig.ServerUrl.PostTaskInfoByTaskId);
        params.setAsJsonContent(true);
        params.setBodyContent(requestVo.getParams());
        params.setConnectTimeout(10000);
        x.http().post(params, new StringCallback<String>() {
            @Override
            public void onSuccess(String str) {
                TypeReference<LqResponseDataVo<List<LqTaskInfoVo>>> typeReference = new TypeReference<LqResponseDataVo<List<LqTaskInfoVo>>>(){};
                LqResponseDataVo<List<LqTaskInfoVo>> responseVo = JSON.parseObject(str, typeReference);
                if(responseVo.isSucceed()){
                    if(EmptyUtil.isNotEmpty(callback) &&
                            EmptyUtil.isNotEmpty(responseVo.getModel()) &&
                            EmptyUtil.isNotEmpty(responseVo.getModel().getData())){
                        callback.onDataLoaded(responseVo.getModel().getData().get(0));
                    }
                }else{
                    String ErrorMessage = (String) responseVo.getErrorMessage();
                    Map<String, String> errorHashMap = ErrorCodeUtil.getInstance().getErrorCodeMap();
                    if (errorHashMap != null && errorHashMap.size() > 0 && !TextUtils.isEmpty(ErrorMessage)
                            && errorHashMap.containsKey(ErrorMessage)) {
                        UIUtil.showToastSafe(errorHashMap.get(ErrorMessage));
                    }
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                if(null != callback){
                    callback.onDataNotAvailable(R.string.net_error_tip);
                }
            }
        });
    }
}
