package com.lqwawa.intleducation.factory.helper;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.Factory;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.vo.PagerArgs;
import com.lqwawa.intleducation.base.vo.RequestVo;
import com.lqwawa.intleducation.base.vo.ResponseVo;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.LogUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.StringCallback;
import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;
import com.lqwawa.intleducation.factory.data.entity.LQwawaBaseResponse;
import com.lqwawa.intleducation.factory.data.entity.course.TutorialGroupEntity;
import com.lqwawa.intleducation.factory.data.entity.online.OnlineStudyOrganEntity;
import com.lqwawa.intleducation.factory.data.entity.response.LQModelMultipleParamIncludePagerResponse;
import com.lqwawa.intleducation.factory.data.entity.school.OrganResponseVo;
import com.lqwawa.intleducation.factory.data.entity.school.SchoolInfoEntity;
import com.lqwawa.intleducation.factory.data.entity.tutorial.AssistStudentEntity;
import com.lqwawa.intleducation.factory.data.entity.tutorial.DateFlagEntity;
import com.lqwawa.intleducation.factory.data.entity.tutorial.LocationEntity;
import com.lqwawa.intleducation.factory.data.entity.tutorial.TaskEntity;
import com.lqwawa.intleducation.factory.data.entity.tutorial.TutorCommentEntity;
import com.lqwawa.intleducation.factory.data.entity.tutorial.TutorEntity;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.home.LanguageType;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;
import com.lqwawa.intleducation.module.tutorial.marking.list.OrderByType;
import com.lqwawa.intleducation.module.tutorial.regist.IDType;
import com.lqwawa.intleducation.module.tutorial.regist.LocationType;
import com.lqwawa.lqbaselib.net.ErrorCodeUtil;

import org.xutils.http.RequestParams;
import org.xutils.x;

import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

/**
 * @author mrmedic
 * @desc 帮辅空间的请求帮助类
 */
public class TutorialHelper {

    /**
     * 获取机构列表
     *
     * @param parentLocationId 父级别Id
     * @param parentLocationType 父级别类型
     * @return 返回机构信息接口回调
     */
    public static void requestLocationData(@Nullable String parentLocationId,
                                           @LocationType.LocationTypeRes int parentLocationType,
                                           @NonNull DataSource.Callback<LocationEntity> callback) {
        // 准备数据
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("ParentLocationId", parentLocationId);
        requestVo.addParams("ParentLocationType", parentLocationType);

        RequestParams params = new RequestParams(AppConfig.ServerUrl.PostRequestLocationDataUrl);
        params.setAsJsonContent(true);
        params.setBodyContent(requestVo.getParamsWithoutToken());
        params.setConnectTimeout(1000);
        LogUtil.i(TutorialHelper.class, "send request ==== " + params.getUri());
        x.http().post(params, new StringCallback<String>() {

            @Override
            public void onSuccess(String str) {
                LogUtil.i(TutorialHelper.class, "request " + params.getUri() + " result :" + str);
                TypeReference<LQwawaBaseResponse<LocationEntity>> typeReference = new TypeReference<LQwawaBaseResponse<LocationEntity>>() {};
                LQwawaBaseResponse<LocationEntity> response = JSON.parseObject(str, typeReference);
                if (response.isSucceed()) {
                    if (EmptyUtil.isNotEmpty(callback)) {
                        callback.onDataLoaded(response.getModel());
                    }
                } else {
                    String ErrorMessage = (String) response.getErrorMessage();
                    Map<String, String> errorHashMap = ErrorCodeUtil.getInstance().getErrorCodeMap();
                    if (errorHashMap != null && errorHashMap.size() > 0 && !TextUtils.isEmpty(ErrorMessage)
                            && errorHashMap.containsKey(ErrorMessage)) {
                        UIUtil.showToastSafe(errorHashMap.get(ErrorMessage));
                    }
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.w(TutorialHelper.class, "request " + params.getUri() + " failed");
                if (null != callback) {
                    callback.onDataNotAvailable(R.string.net_error_tip);
                }
            }
        });
    }

    /**
     * 助教拉取帮辅的学生列表
     * @param MemberId 助教会员Id
     * @param Name 过滤条件
     * @param pageIndex 当前页数
     * @param pageSize 每页条数
     */
    public static void requestPullTutorialStudents(@NonNull String MemberId,
                                                   @Nullable String Name,
                                                   int pageIndex, int pageSize,
                                                   @NonNull DataSource.Callback<List<AssistStudentEntity>> callback){
        // 准备数据
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("MemberId", MemberId);
        requestVo.addParams("Name", Name);
        requestVo.addParams("Pager", new PagerArgs(pageIndex, pageSize), true);

        RequestParams params = new RequestParams(AppConfig.ServerUrl.PostRequestPullTutorialStudents);
        params.setAsJsonContent(true);
        params.setBodyContent(requestVo.getParamsWithoutToken());
        params.setConnectTimeout(1000);
        LogUtil.i(TutorialHelper.class, "send request ==== " + params.getUri());
        x.http().post(params, new StringCallback<String>() {

            @Override
            public void onSuccess(String str) {
                LogUtil.i(TutorialHelper.class, "request " + params.getUri() + " result :" + str);
                TypeReference<LQModelMultipleParamIncludePagerResponse<AssistStudentEntity>> typeReference = new TypeReference<LQModelMultipleParamIncludePagerResponse<AssistStudentEntity>>() {};
                LQModelMultipleParamIncludePagerResponse<AssistStudentEntity> response = JSON.parseObject(str, typeReference);
                if (!response.isHasError()) {
                    if (EmptyUtil.isNotEmpty(callback)) {
                        if(EmptyUtil.isNotEmpty(response.getModel())) {
                            callback.onDataLoaded(response.getModel().getData());
                        }
                    }
                } else {
                    String ErrorMessage = (String) response.getErrorMessage();
                    Map<String, String> errorHashMap = ErrorCodeUtil.getInstance().getErrorCodeMap();
                    if (errorHashMap != null && errorHashMap.size() > 0 && !TextUtils.isEmpty(ErrorMessage)
                            && errorHashMap.containsKey(ErrorMessage)) {
                        UIUtil.showToastSafe(errorHashMap.get(ErrorMessage));
                    }
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.w(TutorialHelper.class, "request " + params.getUri() + " failed");
                if (null != callback) {
                    callback.onDataNotAvailable(R.string.net_error_tip);
                }
            }
        });
    }



    /**
     * 根据条件查询我帮辅的课程或课堂
     * @param memberId 会员Id
     * @param name 课程名称过滤条件
     * @param type 1 课程 2 课堂
     * @param pageIndex 当前页数
     * @param pageSize 每页加载条数
     */
    public static void requestTutorialCourses(@NonNull String memberId,
                                              @Nullable String name,
                                              int type, int pageIndex, int pageSize,
                                              @NonNull DataSource.Callback<List<CourseVo>> callback){
        // 准备数据
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("memberId", memberId);
        requestVo.addParams("type", type);
        requestVo.addParams("pageIndex", pageIndex);
        requestVo.addParams("pageSize",pageSize);
        try{
            requestVo.addParams("name", URLEncoder.encode(name, "UTF-8"));
        }catch (Exception e){
            e.printStackTrace();
        }
        RequestParams params = new RequestParams(AppConfig.ServerUrl.GetRequestTutorialCourses + requestVo.getParams());
        params.setConnectTimeout(10000);
        LogUtil.i(TutorialHelper.class, "send request ==== " + params.getUri());
        x.http().get(params, new StringCallback<String>() {

            @Override
            public void onSuccess(String str) {
                LogUtil.i(TutorialHelper.class, "request " + params.getUri() + " result :" + str);
                TypeReference<ResponseVo<List<CourseVo>>> typeReference = new TypeReference<ResponseVo<List<CourseVo>>>(){};
                ResponseVo<List<CourseVo>> responseVo = JSON.parseObject(str, typeReference);
                if (responseVo.isSucceed()) {
                    if (EmptyUtil.isNotEmpty(callback)) {
                        callback.onDataLoaded(responseVo.getData());
                    }
                } else {
                    Factory.decodeRspCode(responseVo.getCode(), callback);
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.w(TutorialHelper.class, "request " + params.getUri() + " failed");
                if (null != callback) {
                    callback.onDataNotAvailable(R.string.net_error_tip);
                }
            }
        });
    }

    /**
     * 学生查询关注的助教列表
     * @param memberId 用户Id
     * @param tutorName 帮辅老师过滤条件
     * @param pageIndex 当前页数
     * @param pageSize 每页加载条数
     */
    public static void requestMyTutorData(@NonNull String memberId,
                                          @Nullable String tutorName,
                                          int pageIndex, int pageSize,
                                          @NonNull DataSource.Callback<List<TutorEntity>> callback){
        // 准备数据
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("memberId", memberId);
        requestVo.addParams("pageIndex", pageIndex);
        requestVo.addParams("pageSize",pageSize);
        try{
            requestVo.addParams("tutorName", URLEncoder.encode(tutorName, "UTF-8"));
        }catch (Exception e){
            e.printStackTrace();
        }
        RequestParams params = new RequestParams(AppConfig.ServerUrl.GetRequestTutorialByStudentId + requestVo.getParams());
        params.setConnectTimeout(10000);
        LogUtil.i(TutorialHelper.class, "send request ==== " + params.getUri());
        x.http().get(params, new StringCallback<String>() {

            @Override
            public void onSuccess(String str) {
                LogUtil.i(TutorialHelper.class, "request " + params.getUri() + " result :" + str);
                TypeReference<ResponseVo<List<TutorEntity>>> typeReference = new TypeReference<ResponseVo<List<TutorEntity>>>(){};
                ResponseVo<List<TutorEntity>> responseVo = JSON.parseObject(str, typeReference);
                if (responseVo.isSucceed()) {
                    if (EmptyUtil.isNotEmpty(callback)) {
                        callback.onDataLoaded(responseVo.getData());
                    }
                } else {
                    Factory.decodeRspCode(responseVo.getCode(), callback);
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.w(TutorialHelper.class, "request " + params.getUri() + " failed");
                if (null != callback) {
                    callback.onDataNotAvailable(R.string.net_error_tip);
                }
            }
        });
    }

    /**
     * 查询是否已经关注该帮辅老师
     * @param memberId 用户Id
     * @param tutorMemberId 帮辅老师Id
     * @param callback 回调对象
     */
    public static void requestQueryAddedTutorByTutorId(@NonNull String memberId,
                                                       @NonNull String tutorMemberId,
                                                       @NonNull DataSource.Callback<Boolean> callback){
        // 准备数据
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("memberId", memberId);
        requestVo.addParams("tutorMemberId", tutorMemberId);
        RequestParams params = new RequestParams(AppConfig.ServerUrl.GetQueryAddedTutorState + requestVo.getParams());
        params.setConnectTimeout(10000);
        LogUtil.i(TutorialHelper.class, "send request ==== " + params.getUri());
        x.http().get(params, new StringCallback<String>() {

            @Override
            public void onSuccess(String str) {
                LogUtil.i(TutorialHelper.class, "request " + params.getUri() + " result :" + str);
                TypeReference<ResponseVo<Map<String,Boolean>>> typeReference = new TypeReference<ResponseVo<Map<String,Boolean>>>(){};
                ResponseVo<Map<String,Boolean>> responseVo = JSON.parseObject(str, typeReference);
                if (responseVo.isSucceed()) {
                    if (EmptyUtil.isNotEmpty(callback)) {
                        boolean added = responseVo.getData() != null ? responseVo.getData().get("added") : false;
                        callback.onDataLoaded(added);
                    }
                } else {
                    Factory.decodeRspCode(responseVo.getCode(), callback);
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.w(TutorialHelper.class, "request " + params.getUri() + " failed");
                if (null != callback) {
                    callback.onDataNotAvailable(R.string.net_error_tip);
                }
            }
        });
    }

    /**
     * 学生添加帮辅老师
     * @param memberId 学生memberId
     * @param tutorMemberId 助教memberId
     * @param tutorName 助教姓名
     */
    public static void requestAddTutorByStudentId(@NonNull String memberId,
                                                  @NonNull String tutorMemberId,
                                                  @NonNull String tutorName,
                                                  @NonNull DataSource.Callback<Boolean> callback){
        // 准备数据
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("memberId", memberId);
        requestVo.addParams("tutorMemberId", tutorMemberId);
        requestVo.addParams("tutorName",tutorName);
        RequestParams params = new RequestParams(AppConfig.ServerUrl.GetRequestAddTutor + requestVo.getParams());
        params.setConnectTimeout(10000);
        LogUtil.i(TutorialHelper.class, "send request ==== " + params.getUri());
        x.http().get(params, new StringCallback<String>() {

            @Override
            public void onSuccess(String str) {
                LogUtil.i(TutorialHelper.class, "request " + params.getUri() + " result :" + str);
                TypeReference<ResponseVo> typeReference = new TypeReference<ResponseVo>(){};
                ResponseVo responseVo = JSON.parseObject(str, typeReference);

                boolean isSucceed = responseVo.isSucceed();
                if (EmptyUtil.isNotEmpty(callback)) {
                    callback.onDataLoaded(isSucceed);
                }
                if (!isSucceed) {
                    Factory.decodeRspCode(responseVo.getCode(), callback);
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.w(TutorialHelper.class, "request " + params.getUri() + " failed");
                if (null != callback) {
                    callback.onDataNotAvailable(R.string.net_error_tip);
                }
            }
        });
    }

    /**
     * 帮辅老师评价列表
     * @param memberId 学生的Id
     * @param tutorMemberId 帮辅老师的Id
     * @param pageIndex 当前页数
     * @param pageSize 每页加载的条数
     */
    public static void requestTutorCommentData(@NonNull String memberId,
                                               @NonNull String tutorMemberId,
                                               int pageIndex, int pageSize,
                                               @NonNull DataSource.Callback<List<TutorCommentEntity>> callback){
        // 准备数据
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("memberId", memberId);
        requestVo.addParams("tutorMemberId", tutorMemberId);
        requestVo.addParams("pageIndex", pageIndex);
        requestVo.addParams("pageSize",pageSize);
        RequestParams params = new RequestParams(AppConfig.ServerUrl.GetRequestTutorCommentData + requestVo.getParams());
        params.setConnectTimeout(10000);
        LogUtil.i(TutorialHelper.class, "send request ==== " + params.getUri());
        x.http().get(params, new StringCallback<String>() {

            @Override
            public void onSuccess(String str) {
                LogUtil.i(TutorialHelper.class, "request " + params.getUri() + " result :" + str);
                TypeReference<ResponseVo<List<TutorCommentEntity>>> typeReference = new TypeReference<ResponseVo<List<TutorCommentEntity>>>(){};
                ResponseVo<List<TutorCommentEntity>> responseVo = JSON.parseObject(str, typeReference);
                if (responseVo.isSucceed()) {
                    if (EmptyUtil.isNotEmpty(callback)) {
                        callback.onDataLoaded(responseVo.getData());
                    }
                } else {
                    Factory.decodeRspCode(responseVo.getCode(), callback);
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.w(TutorialHelper.class, "request " + params.getUri() + " failed");
                if (null != callback) {
                    callback.onDataNotAvailable(R.string.net_error_tip);
                }
            }
        });
    }

    /**
     * 帮辅评价显示和隐藏
     * @param memberId 用户Id
     * @param id 评价实体主键
     * @param status 0 隐藏 1 显示
     */
    public static void requestTutorSingleCommentState(@NonNull String memberId,
                                                      int id, int status,
                                                      @NonNull DataSource.Callback<Boolean> callback){
        // 准备数据
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("memberId", memberId);
        requestVo.addParams("id", id);
        requestVo.addParams("status",status);
        RequestParams params = new RequestParams(AppConfig.ServerUrl.GetRequestUpdateTutorialCommentStatus + requestVo.getParams());
        params.setConnectTimeout(10000);
        LogUtil.i(TutorialHelper.class, "send request ==== " + params.getUri());
        x.http().get(params, new StringCallback<String>() {

            @Override
            public void onSuccess(String str) {
                LogUtil.i(TutorialHelper.class, "request " + params.getUri() + " result :" + str);
                TypeReference<ResponseVo> typeReference = new TypeReference<ResponseVo>(){};
                ResponseVo responseVo = JSON.parseObject(str, typeReference);
                if (responseVo.isSucceed()) {
                    if (EmptyUtil.isNotEmpty(callback)) {
                        callback.onDataLoaded(true);
                    }
                } else {
                    Factory.decodeRspCode(responseVo.getCode(), callback);
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.w(TutorialHelper.class, "request " + params.getUri() + " failed");
                if (null != callback) {
                    callback.onDataNotAvailable(R.string.net_error_tip);
                }
            }
        });
    }

    /**
     * 对帮辅老师的评价内容点赞
     * @param memberId 用户Id
     * @param id 评论Id
     */
    public static void requestAddPraiseByCommentId(@NonNull String memberId,
                                                   int id,
                                                   @NonNull DataSource.Callback<Boolean> callback){
        // 准备数据
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("memberId", memberId);
        requestVo.addParams("id", id);
        RequestParams params = new RequestParams(AppConfig.ServerUrl.GetTutorialCommentAddPraise + requestVo.getParams());
        params.setConnectTimeout(10000);
        LogUtil.i(TutorialHelper.class, "send request ==== " + params.getUri());
        x.http().get(params, new StringCallback<String>() {

            @Override
            public void onSuccess(String str) {
                LogUtil.i(TutorialHelper.class, "request " + params.getUri() + " result :" + str);
                TypeReference<ResponseVo> typeReference = new TypeReference<ResponseVo>(){};
                ResponseVo responseVo = JSON.parseObject(str, typeReference);
                if (responseVo.isSucceed()) {
                    if (EmptyUtil.isNotEmpty(callback)) {
                        callback.onDataLoaded(true);
                    }
                } else {
                    Factory.decodeRspCode(responseVo.getCode(), callback);
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.w(TutorialHelper.class, "request " + params.getUri() + " failed");
                if (null != callback) {
                    callback.onDataNotAvailable(R.string.net_error_tip);
                }
            }
        });
    }

    /**
     * 对帮辅老师评论
     * @param memberId 用户Id
     * @param tutorMemberId 帮辅老师Id
     * @param content 评论内容
     * @param callback 回调对象
     */
    public static void requestAddTutorialComment(@NonNull String memberId,
                                                 @NonNull String tutorMemberId,
                                                 @NonNull String content,
                                                 @NonNull DataSource.Callback<Boolean> callback){
        // 准备数据
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("memberId", memberId);
        requestVo.addParams("tutorMemberId", tutorMemberId);
        requestVo.addParams("content", content);
        RequestParams params = new RequestParams(AppConfig.ServerUrl.GetRequestAddTutorialComment + requestVo.getParams());
        params.setConnectTimeout(10000);
        LogUtil.i(TutorialHelper.class, "send request ==== " + params.getUri());
        x.http().get(params, new StringCallback<String>() {

            @Override
            public void onSuccess(String str) {
                LogUtil.i(TutorialHelper.class, "request " + params.getUri() + " result :" + str);
                TypeReference<ResponseVo> typeReference = new TypeReference<ResponseVo>(){};
                ResponseVo responseVo = JSON.parseObject(str, typeReference);
                if (responseVo.isSucceed()) {
                    if (EmptyUtil.isNotEmpty(callback)) {
                        callback.onDataLoaded(true);
                    }
                } else {
                    Factory.decodeRspCode(responseVo.getCode(), callback);
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.w(TutorialHelper.class, "request " + params.getUri() + " failed");
                if (null != callback) {
                    callback.onDataNotAvailable(R.string.net_error_tip);
                }
            }
        });
    }

    /**
     * 获取助教学生批阅列表的日期标记
     * @param MemberId 会员Id
     * @param Role 0 助教 1 学生
     * @param StartTimeBegin 检索开始时间
     * @param StartTimeEnd 检索结束时间
     * @param State 0 未批阅 1 已批阅 不传默认全部
     */
    public static void requestDateFlagForAssist(@NonNull String MemberId,
                                                @NonNull String Role,
                                                @NonNull String StartTimeBegin,
                                                @NonNull String StartTimeEnd,
                                                int State, DataSource.Callback<List<DateFlagEntity>> callback){
        // 准备数据
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("MemberId", MemberId);
        requestVo.addParams("Role", Role);
        requestVo.addParams("StartTimeBegin", StartTimeBegin);
        requestVo.addParams("StartTimeEnd", StartTimeEnd);
        requestVo.addParams("State", State);

        RequestParams params = new RequestParams(AppConfig.ServerUrl.PostRequestWorkDateFlag);
        params.setAsJsonContent(true);
        params.setBodyContent(requestVo.getParamsWithoutToken());
        params.setConnectTimeout(1000);
        LogUtil.i(SchoolHelper.class, "send request ==== " + params.getUri());
        x.http().post(params, new StringCallback<String>() {

            @Override
            public void onSuccess(String str) {
                LogUtil.i(TutorialHelper.class, "request " + params.getUri() + " result :" + str);
                TypeReference<LQModelMultipleParamIncludePagerResponse<DateFlagEntity>> typeReference = new TypeReference<LQModelMultipleParamIncludePagerResponse<DateFlagEntity>>() {};
                LQModelMultipleParamIncludePagerResponse<DateFlagEntity> response = JSON.parseObject(str, typeReference);
                if (!response.isHasError()) {
                    if (EmptyUtil.isNotEmpty(callback)) {
                        if(EmptyUtil.isNotEmpty(response.getModel())) {
                            callback.onDataLoaded(response.getModel().getData());
                        }
                    }
                } else {
                    String ErrorMessage = (String) response.getErrorMessage();
                    Map<String, String> errorHashMap = ErrorCodeUtil.getInstance().getErrorCodeMap();
                    if (errorHashMap != null && errorHashMap.size() > 0 && !TextUtils.isEmpty(ErrorMessage)
                            && errorHashMap.containsKey(ErrorMessage)) {
                        UIUtil.showToastSafe(errorHashMap.get(ErrorMessage));
                    }
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.w(TutorialHelper.class, "request " + params.getUri() + " failed");
                if (null != callback) {
                    callback.onDataNotAvailable(R.string.net_error_tip);
                }
            }
        });
    }

    /**
     * 拉取学生老师的帮辅作业列表
     *
     * @param StudentId 学生会员Id 非必填
     * @param MemberId 助教会员Id 非必填
     * @param AssistStudent_Id 拉取学生助教的作业列表，主键Id 非必填
     * @param Title 任务标题 非必填
     * @param CreateTimeBegin 作业发布时间开始
     * @param CreateTimeEnd 作业发布时间结束
     * @param StartTimeBegin 作业发布检索时间开始
     * @param StartTimeEnd 作业发布检索时间结束
     * @param State 批阅状态 0 未批阅 1 已批阅 不传默认全部
     * @param OrderByType 1 批阅状态正序 2 批阅状态倒叙 3 批阅状态正序 创建时间倒叙 4 批阅状态倒叙 创建时间倒叙 不传默认时间倒叙
     * @param pageIndex 当前页
     * @param pageSize 每页数据条数
     * @return 获取助教学生的作业列表
     */
    public static void requestWorkDataWithIdentityId(@Nullable String StudentId,
                                           @Nullable String MemberId,
                                           @Nullable String AssistStudent_Id,
                                           @Nullable String Title,
                                           @Nullable String CreateTimeBegin,
                                           @Nullable String CreateTimeEnd,
                                           @Nullable String StartTimeBegin,
                                           @Nullable String StartTimeEnd,
                                           int State,@OrderByType.OrderByTypeRes int OrderByType,
                                           int pageIndex,int pageSize,
                                           @NonNull DataSource.Callback<List<TaskEntity>> callback) {
        // 准备数据
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("StudentId", StudentId);
        requestVo.addParams("MemberId", MemberId);
        requestVo.addParams("AssistStudent_Id", AssistStudent_Id);
        requestVo.addParams("Title", Title);
        requestVo.addParams("CreateTimeBegin", CreateTimeBegin);
        requestVo.addParams("CreateTimeEnd", CreateTimeEnd);
        requestVo.addParams("StartTimeBegin", StartTimeBegin);
        requestVo.addParams("StartTimeEnd", StartTimeEnd);
        requestVo.addParams("OrderByType", OrderByType);
        requestVo.addParams("State", State);
        requestVo.addParams("Pager", new PagerArgs(pageIndex, pageSize), true);

        RequestParams params = new RequestParams(AppConfig.ServerUrl.PostRequestWorkTaskList);
        params.setAsJsonContent(true);
        params.setBodyContent(requestVo.getParamsWithoutToken());
        params.setConnectTimeout(1000);
        LogUtil.i(SchoolHelper.class, "send request ==== " + params.getUri());
        x.http().post(params, new StringCallback<String>() {

            @Override
            public void onSuccess(String str) {
                LogUtil.i(TutorialHelper.class, "request " + params.getUri() + " result :" + str);
                TypeReference<LQModelMultipleParamIncludePagerResponse<TaskEntity>> typeReference = new TypeReference<LQModelMultipleParamIncludePagerResponse<TaskEntity>>() {};
                LQModelMultipleParamIncludePagerResponse<TaskEntity> response = JSON.parseObject(str, typeReference);
                if (!response.isHasError()) {
                    if (EmptyUtil.isNotEmpty(callback)) {
                        if(EmptyUtil.isNotEmpty(response.getModel())) {
                            callback.onDataLoaded(response.getModel().getData());
                        }
                    }
                } else {
                    String ErrorMessage = (String) response.getErrorMessage();
                    Map<String, String> errorHashMap = ErrorCodeUtil.getInstance().getErrorCodeMap();
                    if (errorHashMap != null && errorHashMap.size() > 0 && !TextUtils.isEmpty(ErrorMessage)
                            && errorHashMap.containsKey(ErrorMessage)) {
                        UIUtil.showToastSafe(errorHashMap.get(ErrorMessage));
                    }
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.w(TutorialHelper.class, "request " + params.getUri() + " failed");
                if (null != callback) {
                    callback.onDataNotAvailable(R.string.net_error_tip);
                }
            }
        });
    }

    /**
     * 学生申请成为机构助教
     * @param IDType 证件类型 1 身份证 2 护照
     * @param IDNumber 身份证或者护照号码
     * @param memberId 助教memberId
     * @param tutorName 助教姓名
     * @param tutorOrganId 选择的机构Id
     * @param tutorOrganName 选择的机构名称
     * @param markingPrice 批阅价格
     * @param provinceId 省Id
     * @param provinceName 省名称
     * @param cityId 市Id
     * @param cityName 市名称
     * @param countyId 区Id
     * @param countyName 区名称
     * @param workingLife 工作年限
     * @param educationUrl 学历认证
     * @param seniorityUrl 资历认证
     */
    public static void requestApplyForTutor(@NonNull String name,
                                            @NonNull String phoneNumber,
                                            @NonNull String verificationCode,
                                            @IDType.IDTypeRes int IDType,
                                            @NonNull String IDNumber,
                                            @NonNull String memberId,
                                            @NonNull String tutorName,
                                            @NonNull String tutorOrganId,
                                            @NonNull String tutorOrganName,
                                            @NonNull String markingPrice,
                                            @NonNull String provinceId,
                                            @NonNull String provinceName,
                                            @NonNull String cityId,
                                            @NonNull String cityName,
                                            @NonNull String countyId,
                                            @NonNull String countyName,
                                            @NonNull String workingLife,
                                            @NonNull String educationUrl,
                                            @NonNull String seniorityUrl,
                                            @NonNull DataSource.Callback<Boolean> callback){
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("Name",name);
        requestVo.addParams("PhoneNumber",phoneNumber);
        requestVo.addParams("VerificationCode",verificationCode);
        requestVo.addParams("IDType",IDType);
        requestVo.addParams("IDNumber",IDNumber);
        requestVo.addParams("memberId",memberId);
        requestVo.addParams("tutorName",tutorName);
        requestVo.addParams("tutorOrganId",tutorOrganId);
        requestVo.addParams("tutorOrganName",tutorOrganName);
        requestVo.addParams("markingPrice",markingPrice);
        requestVo.addParams("provinceId",provinceId);
        requestVo.addParams("provinceName",provinceName);
        requestVo.addParams("cityId",cityId);
        requestVo.addParams("cityName",cityName);
        requestVo.addParams("countyId",countyId);
        requestVo.addParams("countyName",countyName);
        requestVo.addParams("workingLife",workingLife);
        requestVo.addParams("educationUrl",educationUrl);
        requestVo.addParams("seniorityUrl",seniorityUrl);
        RequestParams params = new RequestParams(AppConfig.ServerUrl.GetRequestApplyForTutor + requestVo.getParams());
        params.setConnectTimeout(10000);
        LogUtil.i(TutorialHelper.class,"send request ==== " +params.getUri());
        x.http().get(params, new StringCallback<String>() {

            @Override
            public void onSuccess(String str) {
                LogUtil.i(TutorialHelper.class,"request "+params.getUri()+" result :"+str);
                ResponseVo result = JSON.parseObject(str,new TypeReference<ResponseVo>() {});
                if (result.isSucceed()) {
                    if(EmptyUtil.isNotEmpty(callback)){
                        callback.onDataLoaded(true);
                    }
                }else{
                    Factory.decodeRspCode(result.getCode(),callback);
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.w(TutorialHelper.class,"request "+params.getUri()+" failed");
                if(!EmptyUtil.isEmpty(callback)){
                    callback.onDataNotAvailable(R.string.net_error_tip);
                }
            }
        });
    }

    /**
     * 获取帮辅标签数据
     * {@link LanguageType.LanguageRes}
     * @param isZh 是否中文显示 0 chinese 1 other
     * @param level 返回level级别课程
     * @param parentId 父级Id
     * @param callback 数据回调接口
     */
    public static void requestTutorialConfigData(@LanguageType.LanguageRes int isZh,
                                                 int level,int parentId,
                                                 @NonNull final DataSource.Callback<List<LQCourseConfigEntity>> callback){
        final RequestVo requestVo = new RequestVo();
        // 是否是中文字体,根据参数,后台返回相应语言
        requestVo.addParams("language",isZh);
        requestVo.addParams("level",level);
        requestVo.addParams("parentId",parentId);
        requestVo.addParams("version",0);
        requestVo.addParams("isTutorConfig",true);
        final RequestParams params = new RequestParams(AppConfig.ServerUrl.GetConfigList+requestVo.getParams());
        params.setConnectTimeout(10000);
        LogUtil.i(TutorialHelper.class,"send request ==== " +params.getUri());
        x.http().get(params, new StringCallback<String>() {
            @Override
            public void onSuccess(String str) {
                LogUtil.i(TutorialHelper.class,"request "+params.getUri()+" result :"+str);
                TypeReference<ResponseVo<List<LQCourseConfigEntity>>> typeReference =
                        new TypeReference<ResponseVo<List<LQCourseConfigEntity>>>(){};
                ResponseVo<List<LQCourseConfigEntity>> result = JSON.parseObject(str, typeReference);
                if(result.isSucceed()){
                    List<LQCourseConfigEntity> data = result.getData();
                    if(!EmptyUtil.isEmpty(callback)){
                        callback.onDataLoaded(data);
                    }
                }else{
                    Factory.decodeRspCode(result.getCode(),callback);
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.w(TutorialHelper.class,"request "+params.getUri()+" failed");
                if(!EmptyUtil.isEmpty(callback)){
                    callback.onDataNotAvailable(R.string.net_error_tip);
                }
            }
        });
    }

    /**
     * 获取符合筛选条件的帮辅群
     * @param level 课程Id
     * @param paramOneId 条件1
     * @param paramTwoId 条件2
     * @param paramThreeId 条件3
     * @param sort 排序条件
     * @param callback 请求回调对象
     */
    public static void requestTutorData(@NonNull String level,
                                                  int paramOneId,
                                                  int paramTwoId,
                                                  int paramThreeId,
                                                  @NonNull String sort,
                                                  int pageIndex,int pageSize,
                                                  @NonNull final DataSource.Callback<List<TutorialGroupEntity>> callback) {

        RequestVo requestVo = new RequestVo();
        requestVo.addParams("level", level);
        requestVo.addParams("paramOneId", paramOneId);
        requestVo.addParams("paramTwoId", paramTwoId);
        requestVo.addParams("paramThreeId", paramThreeId);
        requestVo.addParams("sort", sort);
        requestVo.addParams("pageIndex", pageIndex);
        requestVo.addParams("pageSize", pageSize);
        RequestParams params = new RequestParams(AppConfig.ServerUrl.GetTutorList + requestVo.getParams());
        params.setConnectTimeout(10000);
        LogUtil.i(TutorialHelper.class, "send request ==== " + params.getUri());
        x.http().get(params, new StringCallback<String>() {
            @Override
            public void onSuccess(String str) {
                LogUtil.i(TutorialHelper.class, "request " + params.getUri() + " result :" + str);
                TypeReference<ResponseVo<List<TutorialGroupEntity>>> typeReference = new TypeReference<ResponseVo<List<TutorialGroupEntity>>>(){};
                ResponseVo<List<TutorialGroupEntity>> responseVo = JSON.parseObject(str, typeReference);
                if(responseVo.isSucceed()) {
                    if (EmptyUtil.isNotEmpty(callback)) {
                        callback.onDataLoaded(responseVo.getData());
                    }
                }else{
                    if (EmptyUtil.isNotEmpty(callback)) {
                        Factory.decodeRspCode(responseVo.getCode(),callback);
                    }
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                if (EmptyUtil.isNotEmpty(callback)) {
                    callback.onDataNotAvailable(R.string.net_error_tip);
                }
            }
        });
    }

}
