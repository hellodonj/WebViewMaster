package com.lqwawa.intleducation.factory.helper;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.Factory;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.vo.RequestVo;
import com.lqwawa.intleducation.base.vo.ResponseVo;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.LogUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.StringCallback;
import com.lqwawa.intleducation.factory.data.entity.JoinClassEntity;
import com.lqwawa.intleducation.factory.data.entity.LiveEntity;
import com.lqwawa.intleducation.module.learn.vo.LiveVo;
import com.lqwawa.lqbaselib.net.ErrorCodeUtil;

import org.json.JSONArray;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 跟直播相关的网络请求帮助类
 * @date 2018/04/12 11:53
 * @history v1.0
 * **********************************
 */
public class LiveHelper {
    /**
     * 获取学习进度,按照"节"统计
     * @param token if token 不为null,说明是家长身份,那就传token
     * @param courseId 课程Id
     * @param chapterId 章节Id
     * @param callback 请求回调对象
     */
    public static void getCourseChapterLives(@Nullable String token,
                                                @NonNull String courseId,
                                                @NonNull String chapterId,
                                                @NonNull final DataSource.Callback<List<LiveVo>> callback) {

        RequestVo requestVo = new RequestVo();
        if(!TextUtils.isEmpty(token)){
            // 是家长身份,那就传学生 memberId
            requestVo.addParams("token",token);
        }
        requestVo.addParams("chapterId",chapterId);
        requestVo.addParams("courseId", courseId);
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.GetCourseChapterLiveList + requestVo.getParams());
        params.setConnectTimeout(10000);
        x.http().get(params, new StringCallback<String>() {
            @Override
            public void onSuccess(String str) {
                TypeReference<ResponseVo<List<LiveVo>>> typeReference = new TypeReference<ResponseVo<List<LiveVo>>>(){};
                ResponseVo<List<LiveVo>> result = JSON.parseObject(str, typeReference);
                if (result.isSucceed()) {
                    List<LiveVo> data = result.getData();
                    if(callback != null){
                        callback.onDataLoaded(data);
                    }
                }else{
                    Factory.decodeRspCode(result.getCode(),callback);
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
     * 获取直播数据
     * @param pageIndex 分页页数加载
     * @param pageSize  每页加载条数
     * @param callback 回调接口对象
     */
    public static void requestLiveData(int pageIndex, int pageSize,
                                       @NonNull final DataSource.Callback<List<LiveVo>> callback){
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("pageIndex", pageIndex);
        requestVo.addParams("pageSize", pageSize);

        final RequestParams params = new RequestParams(AppConfig.ServerUrl.GetLiveList + requestVo.getParams());
        params.setConnectTimeout(10000);

        LogUtil.i(LiveHelper.class,"send request ==== " +params.getUri());
        x.http().get(params, new StringCallback<String>() {
            @Override
            public void onSuccess(String str) {
                LogUtil.i(LiveHelper.class,"request "+params.getUri()+" result :"+str);
                ResponseVo<List<LiveVo>> result = JSON.parseObject(str,new TypeReference<ResponseVo<List<LiveVo>>>() {});
                if (result.isSucceed()) {
                    List<LiveVo> liveVos = result.getData();
                    if(!EmptyUtil.isEmpty(callback)){
                        callback.onDataLoaded(liveVos);
                    }
                }else{
                    Factory.decodeRspCode(result.getCode(),callback);
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.w(LiveHelper.class,"request "+params.getUri()+" failed");
            }
        });
    }

    /**
     * 请求获取在线课堂班级的直播列表
     * @param schoolId 机构Id
     * @param classId 班级Id
     * @param pageIndex 页数
     * @param pageSize 每页加载数
     * @param callback 数据回调对象
     */
    public static void requestOnlineClassLiveData(@NonNull String schoolId,
                                                  @NonNull String classId,
                                                  int pageIndex,int pageSize,
                                                  @NonNull DataSource.Callback<List<LiveEntity>> callback){
        // 准备数据
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("SchoolId",schoolId);
        jsonObject.put("ClassId",classId);

        // 传分页信息
        Map<String,Integer> pagerMap = new HashMap<>();
        pagerMap.put("PageIndex",pageIndex);
        pagerMap.put("PageSize",pageSize);
        jsonObject.put("Pager", pagerMap);

        RequestParams params = new RequestParams(AppConfig.ServerUrl.PostOnlineClassAirLiveData);
        params.setAsJsonContent(true);
        params.setBodyContent(jsonObject.toJSONString());
        params.setConnectTimeout(10000);
        LogUtil.i(LiveHelper.class,"send request ==== " +params.getUri());
        x.http().post(params, new StringCallback<String>() {

            @Override
            public void onSuccess(String str) {
                LogUtil.i(LiveHelper.class,"request "+params.getUri()+" result :"+str);
                TypeReference<Map<String,Object>> mapTypeReference = new TypeReference<Map<String,Object>>(){};
                Map<String,Object> result = JSON.parseObject(str, mapTypeReference);
                if((int)result.get("ErrorCode") == 0){
                    Map<String,Object> model = (Map<String, Object>) result.get("Model");
                    String dataStr = JSONObject.toJSONString(model.get("Data"));
                    TypeReference<List<LiveEntity>> listTypeReference = new TypeReference<List<LiveEntity>>(){};
                    List<LiveEntity> entities = JSONObject.parseObject(dataStr,listTypeReference);
                    if(!EmptyUtil.isEmpty(callback)){
                        callback.onDataLoaded(entities);
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
                LogUtil.w(LiveHelper.class,"request "+params.getUri()+" failed");
                if(null != callback){
                    callback.onDataNotAvailable(R.string.net_error_tip);
                }
            }
        });
    }


    /**
     * 请求获取在线课堂班级的直播列表
     * @param classIds 直播发布的班级Id
     * @param callback 数据回调对象
     */
    public static void requestCheckClassStatusWithClassId(@NonNull String classIds,
                                                          @NonNull final DataSource.Callback<Boolean> callback){
        // 准备数据
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("classIds",classIds);

        RequestParams params = new RequestParams(AppConfig.ServerUrl.PostCheckOnlineClassStatus);
        params.setAsJsonContent(true);
        params.setBodyContent(jsonObject.toJSONString());
        params.setConnectTimeout(10000);
        LogUtil.i(LiveHelper.class,"send request ==== " +params.getUri());
        x.http().post(params, new StringCallback<String>() {

            @Override
            public void onSuccess(String str) {
                LogUtil.i(LiveHelper.class,"request "+params.getUri()+" result :"+str);
                // TypeReference<Map<String,Object>> mapTypeReference = new TypeReference<Map<String,Object>>(){};
                // Map<String,Object> result = JSON.parseObject(str, mapTypeReference);
                try{
                    org.json.JSONObject result = new org.json.JSONObject(str);
                    int code = (int) result.get("code");
                    if(code == 0){
                        JSONArray dataArray = result.getJSONArray("data");
                        JSONArray dataHisArray = result.getJSONArray("dataHis");
                        if(EmptyUtil.isEmpty(callback)) return;
                        if (dataArray != null && dataArray.length() > 0){
                            callback.onDataLoaded(true);
                        }else if (dataHisArray != null && dataHisArray.length() > 0){
                            callback.onDataLoaded(true);
                        }else{
                            callback.onDataLoaded(false);
                        }

                    }else{
                        Factory.decodeRspCode(code,callback);
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.w(LiveHelper.class,"request "+params.getUri()+" failed");
                if(null != callback){
                    callback.onDataNotAvailable(R.string.net_error_tip);
                }
            }
        });
    }
}
