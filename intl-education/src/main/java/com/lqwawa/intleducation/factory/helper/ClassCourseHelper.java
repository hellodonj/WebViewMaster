package com.lqwawa.intleducation.factory.helper;

import android.support.annotation.NonNull;

import com.alibaba.fastjson.JSON;
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
import com.lqwawa.intleducation.factory.data.entity.course.ClassCourseEntity;

import org.xutils.http.RequestParams;
import org.xutils.x;

import java.net.URLEncoder;
import java.util.List;

/**
 * @author mrmedici
 * @desc 班级学程的相关帮助类
 */
public class ClassCourseHelper {
    /**
     * 获取班级学程课程
     * @param classId 班级Id
     * @param role 角色信息 1表示学生 0表示老师
     * @param name 搜索关键字
     * @param level 级别
     * @param paramOneId configType3
     * @param paramTwoId configType4
     * @param pageIndex 页码
     * @param pageSize 每页加载的数目
     * @param callback 数据回调接口
     */
    public static void requestClassCourseData(@NonNull String classId,int status,
                                              int role,@NonNull String name,
                                              @NonNull String level,
                                              int paramOneId,int paramTwoId,
                                              int pageIndex,int pageSize,int courseType,
                                               @NonNull final DataSource.Callback<List<ClassCourseEntity>> callback){
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("classId",classId);
        requestVo.addParams("status",status);
        requestVo.addParams("role",role);
        requestVo.addParams("level",level);
        requestVo.addParams("paramOneId",paramOneId);
        requestVo.addParams("paramTwoId",paramTwoId);
        requestVo.addParams("pageIndex",pageIndex);
        requestVo.addParams("pageSize",pageSize);
        requestVo.addParams("courseType",courseType);
        if(EmptyUtil.isNotEmpty(name)){
            try{
                requestVo.addParams("name", URLEncoder.encode(name, "UTF-8"));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        RequestParams params = new RequestParams(AppConfig.ServerUrl.GetClassCourseListUrl + requestVo.getParams());
        params.setConnectTimeout(10000);
        LogUtil.i(ClassCourseHelper.class,"send request ==== " +params.getUri());
        x.http().get(params, new StringCallback<String>() {

            @Override
            public void onSuccess(String str) {
                LogUtil.i(ClassCourseHelper.class,"request "+params.getUri()+" result :"+str);
                ResponseVo<List<ClassCourseEntity>> result = JSON.parseObject(str,new TypeReference<ResponseVo<List<ClassCourseEntity>>>() {});
                if (result.isSucceed()) {
                    List<ClassCourseEntity> data = result.getData();
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
                LogUtil.w(ClassCourseHelper.class,"request "+params.getUri()+" failed");
                if(!EmptyUtil.isEmpty(callback)){
                    callback.onDataNotAvailable(R.string.net_error_tip);
                }
            }
        });
    }

    /**
     * 获取选择学习任务班级学程课程
     * @param classId 班级Id
     * @param name 搜索关键字
     * @param pageIndex 页码
     * @param pageSize 每页加载的数目
     * @param callback 数据回调接口
     */
    public static void requestStudyTaskClassCourseData(@NonNull String classId,@NonNull String name,
                                              int pageIndex,int pageSize,
                                              @NonNull final DataSource.Callback<List<ClassCourseEntity>> callback){
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("classId",classId);
        requestVo.addParams("pageIndex",pageIndex);
        requestVo.addParams("pageSize",pageSize);
        if(EmptyUtil.isNotEmpty(name)){
            try{
                requestVo.addParams("name", URLEncoder.encode(name, "UTF-8"));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        RequestParams params = new RequestParams(AppConfig.ServerUrl.GetStudyTaskClassCourseListUrl + requestVo.getParams());
        params.setConnectTimeout(10000);
        LogUtil.i(ClassCourseHelper.class,"send request ==== " +params.getUri());
        x.http().get(params, new StringCallback<String>() {

            @Override
            public void onSuccess(String str) {
                LogUtil.i(ClassCourseHelper.class,"request "+params.getUri()+" result :"+str);
                ResponseVo<List<ClassCourseEntity>> result = JSON.parseObject(str,new TypeReference<ResponseVo<List<ClassCourseEntity>>>() {});
                if (result.isSucceed()) {
                    List<ClassCourseEntity> data = result.getData();
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
                LogUtil.w(ClassCourseHelper.class,"request "+params.getUri()+" failed");
                if(!EmptyUtil.isEmpty(callback)){
                    callback.onDataNotAvailable(R.string.net_error_tip);
                }
            }
        });
    }

    /**
     * 获取班级学程课程
     * @param schoolId 机构Id
     * @param classId 班级Id
     * @param courseIds 添加课程的Id's
     * @param callback 数据回调接口
     */
    public static void requestAddCourseFromClass(@NonNull String schoolId,
                                                    @NonNull String classId,
                                                    @NonNull String courseIds,
                                                    @NonNull final DataSource.Callback<Boolean> callback){
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("schoolId",schoolId);
        requestVo.addParams("classId",classId);
        requestVo.addParams("courseIds",courseIds);
        RequestParams params = new RequestParams(AppConfig.ServerUrl.GetAddCourseFromClassUrl + requestVo.getParams());
        params.setConnectTimeout(10000);
        LogUtil.i(ClassCourseHelper.class,"send request ==== " +params.getUri());
        x.http().get(params, new StringCallback<String>() {

            @Override
            public void onSuccess(String str) {
                LogUtil.i(ClassCourseHelper.class,"request "+params.getUri()+" result :"+str);
                ResponseVo<Void> result = JSON.parseObject(str,new TypeReference<ResponseVo<Void>>() {});
                if (result.isSucceed()) {
                    if(!EmptyUtil.isEmpty(callback)){
                        // 接口回调数据到Presenter
                        callback.onDataLoaded(true);
                    }
                }else{
                    Factory.decodeRspCode(result.getCode(),callback);
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.w(ClassCourseHelper.class,"request "+params.getUri()+" failed");
                if(!EmptyUtil.isEmpty(callback)){
                    callback.onDataNotAvailable(R.string.net_error_tip);
                }
            }
        });
    }

    /**
     * 获取班级学程课程
     * @param token 令牌，传登录人的memberId
     * @param classId 班级Id
     * @param ids 删除课程的Id's
     * @param callback 数据回调接口
     */
    public static void requestDeleteCourseFromClass(@NonNull String token,
                                              @NonNull String classId,
                                              @NonNull String ids,
                                              @NonNull final DataSource.Callback<Boolean> callback){
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("token",token);
        requestVo.addParams("classId",classId);
        requestVo.addParams("ids",ids);
        RequestParams params = new RequestParams(AppConfig.ServerUrl.GetDeleteCourseFromClassUrl + requestVo.getParams());
        params.setConnectTimeout(10000);
        LogUtil.i(ClassCourseHelper.class,"send request ==== " +params.getUri());
        x.http().get(params, new StringCallback<String>() {

            @Override
            public void onSuccess(String str) {
                LogUtil.i(ClassCourseHelper.class,"request "+params.getUri()+" result :"+str);
                ResponseVo<Void> result = JSON.parseObject(str,new TypeReference<ResponseVo<Void>>() {});
                if (result.isSucceed()) {
                    if(!EmptyUtil.isEmpty(callback)){
                        // 接口回调数据到Presenter
                        callback.onDataLoaded(true);
                    }
                }else{
                    Factory.decodeRspCode(result.getCode(),callback);
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.w(ClassCourseHelper.class,"request "+params.getUri()+" failed");
                if(!EmptyUtil.isEmpty(callback)){
                    callback.onDataNotAvailable(R.string.net_error_tip);
                }
            }
        });
    }

    /**
     * 添加历史学程
     * @param classId 班级id
     * @param courseIds 历史学生Ids集合
     * @param callback 回调对象
     */
    public static void requestAddClassHistoryCourse(@NonNull String classId,
                                                    @NonNull String courseIds,
                                                    @NonNull DataSource.SucceedCallback<Boolean> callback){
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("classId", classId);
        requestVo.addParams("courseId", courseIds);
        RequestParams params = new RequestParams(AppConfig.ServerUrl.PostAddClassHistoryCourse);
        params.setAsJsonContent(true);
        params.setBodyContent(requestVo.getParams());
        params.setConnectTimeout(10000);
        LogUtil.i(ClassCourseHelper.class, "send request ==== " + params.getUri());
        x.http().post(params, new StringCallback<String>() {
            @Override
            public void onSuccess(String str) {
                LogUtil.i(ClassCourseHelper.class, "request " + params.getUri() + " result :" + str);
                TypeReference<ResponseVo> typeReference = new TypeReference<ResponseVo>(){};
                ResponseVo responseVo = JSON.parseObject(str, typeReference);
                if (EmptyUtil.isNotEmpty(callback)) {
                    callback.onDataLoaded(responseVo.isSucceed());
                }

                if(!responseVo.isSucceed()) {
                    if (EmptyUtil.isNotEmpty(callback)) {
                        String message = responseVo.getMessage();
                        UIUtil.showToastSafe(message);
                    }
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                if (EmptyUtil.isNotEmpty(callback)) {
                    UIUtil.showToastSafe(R.string.net_error_tip);
                }
            }
        });
    }

    /**
     * 添加历史学程
     * @param classId 班级id
     * @param courseIds 历史学生Ids集合
     * @param callback 回调对象
     */
    public static void requestRemoveClassHistoryCourse(@NonNull String classId,
                                                    @NonNull String courseIds,
                                                    @NonNull DataSource.SucceedCallback<Boolean> callback){
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("classId", classId);
        requestVo.addParams("courseId", courseIds);
        RequestParams params = new RequestParams(AppConfig.ServerUrl.PostRemoveClassHistoryCourse);
        params.setAsJsonContent(true);
        params.setBodyContent(requestVo.getParams());
        params.setConnectTimeout(10000);
        LogUtil.i(ClassCourseHelper.class, "send request ==== " + params.getUri());
        x.http().post(params, new StringCallback<String>() {
            @Override
            public void onSuccess(String str) {
                LogUtil.i(ClassCourseHelper.class, "request " + params.getUri() + " result :" + str);
                TypeReference<ResponseVo> typeReference = new TypeReference<ResponseVo>(){};
                ResponseVo responseVo = JSON.parseObject(str, typeReference);
                if (EmptyUtil.isNotEmpty(callback)) {
                    callback.onDataLoaded(responseVo.isSucceed());
                }

                if(!responseVo.isSucceed()) {
                    if (EmptyUtil.isNotEmpty(callback)) {
                        String message = responseVo.getMessage();
                        UIUtil.showToastSafe(message);
                    }
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                if (EmptyUtil.isNotEmpty(callback)) {
                    UIUtil.showToastSafe(R.string.net_error_tip);
                }
            }
        });
    }
}
