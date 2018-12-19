package com.lqwawa.intleducation.factory.helper;

import android.support.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.Factory;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.vo.LqResponseDataVo;
import com.lqwawa.intleducation.base.vo.RequestVo;
import com.lqwawa.intleducation.base.vo.ResponseVo;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.LogUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.StringCallback;
import com.lqwawa.intleducation.factory.data.entity.BaseEntity;
import com.lqwawa.intleducation.factory.data.entity.LQwawaBaseEntity;
import com.lqwawa.intleducation.factory.data.entity.LQwawaBaseResponse;
import com.lqwawa.intleducation.factory.data.entity.OnlineClassEntity;
import com.lqwawa.intleducation.module.discovery.vo.BannerInfoVo;
import com.lqwawa.intleducation.module.learn.vo.ChildrenListVo;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import org.xutils.http.RequestParams;
import org.xutils.x;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * @author medici
 * @desc 我的学程相关请求接口
 */
public class MyCourseHelper {

    /**
     * 获取我的课程孩子的数据
     * @param callback 数据回调接口
     */
    public static void requestParentChildData(@NonNull final DataSource.Callback<List<ChildrenListVo>> callback){
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("MemberId", UserHelper.getUserId());
        RequestParams params = new RequestParams(AppConfig.ServerUrl.LQWW_GET_STUDENT_BY_PARENT);
        params.setAsJsonContent(true);
        params.setBodyContent(requestVo.getParams());
        params.setConnectTimeout(10000);
        LogUtil.i(MyCourseHelper.class,"send request ==== " +params.getUri());
        x.http().post(params, new StringCallback<String>() {

            @Override
            public void onSuccess(String str) {
                LogUtil.i(MyCourseHelper.class,"request "+params.getUri()+" result :"+str);
                LqResponseDataVo<List<ChildrenListVo>> result = JSON.parseObject(str,new TypeReference<LqResponseDataVo<List<ChildrenListVo>>>() {});
                if (!result.isHasError()) {
                    List<ChildrenListVo> data = result.getModel().getData();
                    if(!EmptyUtil.isEmpty(callback)){
                        // 接口回调数据到Presenter
                        callback.onDataLoaded(data);
                    }
                }else{
                    Factory.decodeRspCode(result.getErrorCode(),callback);
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.w(MyCourseHelper.class,"request "+params.getUri()+" failed");
                if(!EmptyUtil.isEmpty(callback)){
                    callback.onDataNotAvailable(R.string.net_error_tip);
                }
            }
        });
    }

    /**
     * 获取我的课程在线学习数据
     * @param keyWord 搜索关键词
     * @param pageIndex 页码
     * @param pageSize 每页加载的数目
     * @param callback 数据回调接口
     */
    public static void requestOnlineCourseData(@NonNull String curMemberId,
                                               @NonNull String keyWord,
                                               int pageIndex,
                                               int pageSize,
                                               @NonNull final DataSource.Callback<List<OnlineClassEntity>> callback){
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("token",curMemberId);
        requestVo.addParams("pageIndex",pageIndex);
        requestVo.addParams("pageSize",pageSize);
        if(EmptyUtil.isNotEmpty(keyWord)){
            try{
                requestVo.addParams("name", URLEncoder.encode(keyWord, "UTF-8"));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        RequestParams params = new RequestParams(AppConfig.ServerUrl.GetMyOnlineCourseUrl + requestVo.getParams());
        params.setConnectTimeout(10000);
        LogUtil.i(MyCourseHelper.class,"send request ==== " +params.getUri());
        x.http().get(params, new StringCallback<String>() {

            @Override
            public void onSuccess(String str) {
                LogUtil.i(MyCourseHelper.class,"request "+params.getUri()+" result :"+str);
                ResponseVo<List<OnlineClassEntity>> result = JSON.parseObject(str,new TypeReference<ResponseVo<List<OnlineClassEntity>>>() {});
                if (result.isSucceed()) {
                    List<OnlineClassEntity> data = result.getData();
                    if(!EmptyUtil.isEmpty(callback)){
                        // 接口回调数据到Presenter
                        callback.onDataLoaded(data);
                    }
                }else{
                    Factory.decodeRspCode(result.getCode(),callback);
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.w(MyCourseHelper.class,"request "+params.getUri()+" failed");
                if(!EmptyUtil.isEmpty(callback)){
                    callback.onDataNotAvailable(R.string.net_error_tip);
                }
            }
        });
    }

    /**
     * 获取我授课的在线学习
     * @param memberId 自己的Id 也是老师的Id
     * @param keyWord 搜索关键词
     * @param progressStatus 不传获取即将开始和正在进行课堂 2 获取完结课堂
     * @param pageIndex 页码
     * @param pageSize 每页加载的数目
     * @param callback 数据回调接口
     */
    public static void requestMyGiveOnlineCourseData(@NonNull String memberId,
                                               @NonNull String keyWord,
                                               int progressStatus,
                                               int pageIndex,
                                               int pageSize,
                                               @NonNull final DataSource.Callback<List<OnlineClassEntity>> callback){
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("token",memberId);
        requestVo.addParams("pageIndex",pageIndex);
        requestVo.addParams("pageSize",pageSize);
        if(EmptyUtil.isNotEmpty(keyWord)){
            try{
                requestVo.addParams("name", URLEncoder.encode(keyWord, "UTF-8"));
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        if(progressStatus == 3){
            // 获取完成授课
            requestVo.addParams("progressStatus",progressStatus);
        }

        RequestParams params = new RequestParams(AppConfig.ServerUrl.GetMyGiveOnlineCourseUrl + requestVo.getParams());
        params.setConnectTimeout(10000);
        LogUtil.i(MyCourseHelper.class,"send request ==== " +params.getUri());
        x.http().get(params, new StringCallback<String>() {

            @Override
            public void onSuccess(String str) {
                LogUtil.i(MyCourseHelper.class,"request "+params.getUri()+" result :"+str);
                ResponseVo<List<OnlineClassEntity>> result = JSON.parseObject(str,new TypeReference<ResponseVo<List<OnlineClassEntity>>>() {});
                if (result.isSucceed()) {
                    List<OnlineClassEntity> data = result.getData();
                    if(!EmptyUtil.isEmpty(callback)){
                        // 接口回调数据到Presenter
                        callback.onDataLoaded(data);
                    }
                }else{
                    Factory.decodeRspCode(result.getCode(),callback);
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.w(MyCourseHelper.class,"request "+params.getUri()+" failed");
                if(!EmptyUtil.isEmpty(callback)){
                    callback.onDataNotAvailable(R.string.net_error_tip);
                }
            }
        });
    }

}
