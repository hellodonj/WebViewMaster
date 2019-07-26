package com.lqwawa.intleducation.factory.helper;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
import com.lqwawa.intleducation.factory.data.entity.BaseModelDataEntity;
import com.lqwawa.intleducation.factory.data.entity.LQwawaBaseResponse;
import com.lqwawa.intleducation.factory.data.entity.online.OnlineStudyOrganEntity;
import com.lqwawa.intleducation.factory.data.entity.response.CheckPermissionResponseVo;
import com.lqwawa.intleducation.factory.data.entity.response.LQModelMultipleParamIncludePagerResponse;
import com.lqwawa.intleducation.factory.data.entity.school.CheckSchoolPermissionEntity;
import com.lqwawa.intleducation.factory.data.entity.school.OrganResponseVo;
import com.lqwawa.intleducation.factory.data.entity.school.SchoolInfoEntity;
import com.lqwawa.intleducation.factory.data.entity.school.SchoolStarEntity;
import com.lqwawa.intleducation.factory.data.entity.tutorial.MemberSchoolEntity;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;
import com.lqwawa.intleducation.module.user.tool.UserHelper;
import com.lqwawa.lqbaselib.net.ErrorCodeUtil;

import org.xutils.http.RequestParams;
import org.xutils.x;

import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 机构相关的网络数据请求
 * @date 2018/07/03 17:24
 * @history v1.0
 * **********************************
 */
public class SchoolHelper {

    /**
     * 加载某个机构的详细信息
     *
     * @param userId   自己的userId
     * @param schoolId 机构Id
     * @return 返回机构信息接口回调
     */
    public static void requestSchoolInfo(@NonNull String userId,
                                         @NonNull String schoolId,
                                         @NonNull DataSource.Callback<SchoolInfoEntity> callback) {
        // 准备数据
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("MemberId", userId);
        jsonObject.put("SchoolId", schoolId);
        jsonObject.put("VersionCode", 1);

        RequestParams params = new RequestParams(AppConfig.ServerUrl.PostRequestSchoolInfoUrl);
        params.setAsJsonContent(true);
        params.setBodyContent(jsonObject.toJSONString());
        params.setConnectTimeout(10000);
        LogUtil.i(SchoolHelper.class, "send request ==== " + params.getUri());
        x.http().post(params, new StringCallback<String>() {

            @Override
            public void onSuccess(String str) {
                LogUtil.i(SchoolHelper.class, "request " + params.getUri() + " result :" + str);
                TypeToken<BaseModelDataEntity<SchoolInfoEntity>> typeToken = new TypeToken<BaseModelDataEntity<SchoolInfoEntity>>() {
                };
                // TypeReference<BaseModelDataEntity<SchoolInfoEntity>> typeReference = new TypeReference<BaseModelDataEntity<SchoolInfoEntity>>(){};
                // BaseModelDataEntity<SchoolInfoEntity> entity = JSON.parseObject(str, typeReference);
                Gson gson = new Gson();
                BaseModelDataEntity<SchoolInfoEntity> entity = gson.fromJson(str, typeToken.getType());
                if (!entity.isHasError()) {
                    entity.getModel();
                    if (EmptyUtil.isNotEmpty(callback)) {
                        callback.onDataLoaded(entity.getModel());
                    }
                } else {
                    String ErrorMessage = (String) entity.getErrorMessage();
                    Map<String, String> errorHashMap = ErrorCodeUtil.getInstance().getErrorCodeMap();
                    if (errorHashMap != null && errorHashMap.size() > 0 && !TextUtils.isEmpty(ErrorMessage)
                            && errorHashMap.containsKey(ErrorMessage)) {
                        UIUtil.showToastSafe(errorHashMap.get(ErrorMessage));
                    }
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.w(SchoolHelper.class, "request " + params.getUri() + " failed");
                if (null != callback) {
                    callback.onDataNotAvailable(R.string.net_error_tip);
                }
            }
        });
    }


    /**
     * 获取在线学习机构列表
     * @param pageIndex 分页数
     * @param pageSize 分页大小
     * @param keySearch 搜索关键词
     * @param callback 回调对象
     */
    public static void requestSchoolData(@NonNull int pageIndex,
                                         @NonNull int pageSize,
                                         @Nullable String keySearch,
                                         @NonNull DataSource.Callback<List<OnlineStudyOrganEntity>> callback) {
        // 准备数据
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("pageIndex", pageIndex);
        requestVo.addParams("pageSize",pageSize);
        try{
            requestVo.addParams("name", URLEncoder.encode(keySearch, "UTF-8"));
        }catch (Exception e){
            e.printStackTrace();
        }
        RequestParams params = new RequestParams(AppConfig.ServerUrl.GetOnlineStudyMoreOrganUrl + requestVo.getParams());
        params.setConnectTimeout(10000);
        LogUtil.i(SchoolHelper.class, "send request ==== " + params.getUri());
        x.http().get(params, new StringCallback<String>() {

            @Override
            public void onSuccess(String str) {
                LogUtil.i(SchoolHelper.class, "request " + params.getUri() + " result :" + str);
                TypeReference<OrganResponseVo<List<OnlineStudyOrganEntity>>> typeReference = new TypeReference<OrganResponseVo<List<OnlineStudyOrganEntity>>>(){};
                OrganResponseVo<List<OnlineStudyOrganEntity>> responseVo = JSON.parseObject(str, typeReference);
                if (responseVo.isSucceed()) {
                    if (EmptyUtil.isNotEmpty(callback)) {
                        callback.onDataLoaded(responseVo.getOrganList());
                    }
                } else {
                    Factory.decodeRspCode(responseVo.getCode(), callback);
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.w(SchoolHelper.class, "request " + params.getUri() + " failed");
                if (null != callback) {
                    callback.onDataNotAvailable(R.string.net_error_tip);
                }
            }
        });
    }

    /**
     * 获取机构评分
     *
     * @param schoolId 机构Id
     * @return 返回机构信息接口回调
     */
    public static void requestSchoolStar(@NonNull String schoolId,
                                         @NonNull DataSource.Callback<SchoolStarEntity> callback) {
        // 准备数据
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("organId", schoolId);
        RequestParams params = new RequestParams(AppConfig.ServerUrl.GetRequestSchoolStar + requestVo.getParams());
        params.setConnectTimeout(10000);
        LogUtil.i(SchoolHelper.class, "send request ==== " + params.getUri());
        x.http().get(params, new StringCallback<String>() {

            @Override
            public void onSuccess(String str) {
                LogUtil.i(SchoolHelper.class, "request " + params.getUri() + " result :" + str);
                TypeReference<SchoolStarEntity> typeReference = new TypeReference<SchoolStarEntity>() {
                };
                SchoolStarEntity entity = JSON.parseObject(str, typeReference);
                if (entity.isSucceed()) {
                    if (EmptyUtil.isNotEmpty(callback)) {
                        callback.onDataLoaded(entity);
                    }
                } else {
                    Factory.decodeRspCode(entity.getCode(), callback);
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.w(SchoolHelper.class, "request " + params.getUri() + " failed");
                if (null != callback) {
                    callback.onDataNotAvailable(R.string.net_error_tip);
                }
            }
        });
    }

    /**
     * 关注学校
     *
     * @param schoolId 机构Id
     * @return 返回机构信息接口回调
     */
    public static void requestSubscribeSchool(@NonNull String schoolId,
                                              @NonNull DataSource.Callback<Object> callback) {
        // 准备数据
        JSONObject jsonObject = new JSONObject();
        String userId = UserHelper.getUserId();
        jsonObject.put("MemberId", userId);
        jsonObject.put("SchoolId", schoolId);

        RequestParams params = new RequestParams(AppConfig.ServerUrl.PostAddSubscribeSchool);
        params.setAsJsonContent(true);
        params.setBodyContent(jsonObject.toJSONString());
        params.setConnectTimeout(10000);
        LogUtil.i(SchoolHelper.class, "send request ==== " + params.getUri());
        x.http().post(params, new StringCallback<String>() {

            @Override
            public void onSuccess(String str) {
                LogUtil.i(SchoolHelper.class, "request " + params.getUri() + " result :" + str);
                TypeReference<LQwawaBaseResponse<Object>> typeReference = new TypeReference<LQwawaBaseResponse<Object>>() {
                };
                LQwawaBaseResponse<Object> response = JSON.parseObject(str, typeReference);
                if (response.isSucceed()) {
                    if (EmptyUtil.isNotEmpty(callback)) {
                        callback.onDataLoaded(response.getModel());
                    }
                } else {
                    Factory.decodeRspCode(response.getErrorCode(), callback);
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.w(SchoolHelper.class, "request " + params.getUri() + " failed");
                if (null != callback) {
                    callback.onDataNotAvailable(R.string.net_error_tip);
                }
            }
        });
    }

    /**
     * 取消关注学校
     *
     * @param schoolId 机构Id
     * @return 返回机构信息接口回调
     */
    public static void requestUnSubscribeSchool(@NonNull String schoolId,
                                              @NonNull DataSource.Callback<Object> callback) {
        // 准备数据
        JSONObject jsonObject = new JSONObject();
        String userId = UserHelper.getUserId();
        jsonObject.put("MemberId", userId);
        jsonObject.put("SchoolId", schoolId);

        RequestParams params = new RequestParams(AppConfig.ServerUrl.PostRemoveSubscribeSchool);
        params.setAsJsonContent(true);
        params.setBodyContent(jsonObject.toJSONString());
        params.setConnectTimeout(10000);
        LogUtil.i(SchoolHelper.class, "send request ==== " + params.getUri());
        x.http().post(params, new StringCallback<String>() {

            @Override
            public void onSuccess(String str) {
                LogUtil.i(SchoolHelper.class, "request " + params.getUri() + " result :" + str);
                TypeReference<LQwawaBaseResponse<Object>> typeReference = new TypeReference<LQwawaBaseResponse<Object>>() {
                };
                LQwawaBaseResponse<Object> response = JSON.parseObject(str, typeReference);
                if (response.isSucceed()) {
                    if (EmptyUtil.isNotEmpty(callback)) {
                        callback.onDataLoaded(response.getModel());
                    }
                } else {
                    Factory.decodeRspCode(response.getErrorCode(), callback);
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.w(SchoolHelper.class, "request " + params.getUri() + " failed");
                if (null != callback) {
                    callback.onDataNotAvailable(R.string.net_error_tip);
                }
            }
        });
    }

    /**
     * 检查机构是否授权
     *
     * @param schoolId 机构Id
     * @param type 0 LQ学程是授权
     * @return 返回机构信息接口回调
     */
    public static void requestCheckSchoolPermission(@NonNull String schoolId,
                                                    int type,
                                                    @NonNull DataSource.Callback<CheckSchoolPermissionEntity> callback) {
        // 准备数据
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("organId", schoolId);
        requestVo.addParams("type",type);
        requestVo.addParams("memberId",UserHelper.getUserId());
        RequestParams params = new RequestParams(AppConfig.ServerUrl.checkAuthorization + requestVo.getParams());
        params.setConnectTimeout(10000);
        LogUtil.i(SchoolHelper.class, "send request ==== " + params.getUri());
        x.http().get(params, new StringCallback<String>() {

            @Override
            public void onSuccess(String str) {
                LogUtil.i(SchoolHelper.class, "request " + params.getUri() + " result :" + str);
                TypeReference<CheckSchoolPermissionEntity> typeReference = new TypeReference<CheckSchoolPermissionEntity>() {};
                CheckSchoolPermissionEntity entity = JSON.parseObject(str, typeReference);
                if (entity.isSucceed()) {
                    if (EmptyUtil.isNotEmpty(callback)) {
                        callback.onDataLoaded(entity);
                    }
                } else {
                    Factory.decodeRspCode(entity.getCode(), callback);
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.w(SchoolHelper.class, "request " + params.getUri() + " failed");
                if (null != callback) {
                    callback.onDataNotAvailable(R.string.net_error_tip);
                }
            }
        });
    }


    /**
     * 获取机构授权
     *
     * @param schoolId 机构Id
     * @param type 0 LQ学程是授权
     * @param code 授权码
     * @return 返回机构信息接口回调
     */
    public static void requestSavePermission(@NonNull String schoolId,
                                                    int type,
                                                    @NonNull String code,
                                                    @NonNull DataSource.Callback<CheckPermissionResponseVo<Void>> callback) {
        // 准备数据
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("organId", schoolId);
        requestVo.addParams("type",type);
        requestVo.addParams("code",code);
        requestVo.addParams("memberId",UserHelper.getUserId());
        RequestParams params = new RequestParams(AppConfig.ServerUrl.commitAuthorizationCode + requestVo.getParams());
        params.setConnectTimeout(10000);
        LogUtil.i(SchoolHelper.class, "send request ==== " + params.getUri());
        x.http().get(params, new StringCallback<String>() {

            @Override
            public void onSuccess(String str) {
                LogUtil.i(SchoolHelper.class, "request " + params.getUri() + " result :" + str);
                TypeReference<CheckPermissionResponseVo<Void>> typeReference = new TypeReference<CheckPermissionResponseVo<Void>>() {};
                CheckPermissionResponseVo<Void> vo = JSON.parseObject(str, typeReference);
                if (EmptyUtil.isNotEmpty(callback) || EmptyUtil.isNotEmpty(vo)) {
                    callback.onDataLoaded(vo);
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.w(SchoolHelper.class, "request " + params.getUri() + " failed");
                if (null != callback) {
                    callback.onDataNotAvailable(R.string.net_error_tip);
                }
            }
        });
    }


    /**
     * 获取机构列表
     *
     * @param name 关键词过滤
     * @param pageIndex 分页信息
     * @param pageSize 单分页条数
     * @param onlyIncludeOnline 是否只拉取线上机构
     * @return 返回机构信息接口回调
     */
    public static void requestFilterOrganData(@Nullable String name,
                                              int pageIndex,int pageSize,
                                              boolean onlyIncludeOnline,
                                              @NonNull DataSource.Callback<List<SchoolInfoEntity>> callback) {
        // 准备数据
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("SName", name);
        requestVo.addParams("Pager", new PagerArgs(pageIndex, pageSize), true);
        requestVo.addParams("OnlyIncludeOnline", onlyIncludeOnline);

        RequestParams params = new RequestParams(AppConfig.ServerUrl.PostRequestFilterOrganUrl);
        params.setAsJsonContent(true);
        params.setBodyContent(requestVo.getParamsWithoutToken());
        params.setConnectTimeout(10000);
        LogUtil.i(SchoolHelper.class, "send request ==== " + params.getUri());
        x.http().post(params, new StringCallback<String>() {

            @Override
            public void onSuccess(String str) {
                LogUtil.i(SchoolHelper.class, "request " + params.getUri() + " result :" + str);
                TypeReference<LQModelMultipleParamIncludePagerResponse<SchoolInfoEntity>> typeReference = new TypeReference<LQModelMultipleParamIncludePagerResponse<SchoolInfoEntity>>() {};
                LQModelMultipleParamIncludePagerResponse<SchoolInfoEntity> response = JSON.parseObject(str, typeReference);
                if (!response.isHasError()) {
                    if (EmptyUtil.isNotEmpty(callback)) {
                        if(EmptyUtil.isNotEmpty(response.getModel())) {
                            callback.onDataLoaded(response.getModel().getDataList());
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
                LogUtil.w(SchoolHelper.class, "request " + params.getUri() + " failed");
                if (null != callback) {
                    callback.onDataNotAvailable(R.string.net_error_tip);
                }
            }
        });
    }

    /**
     * 拉取用户相关的机构列表
     * @param MemberId 用户Id
     * @param SchoolName 机构过滤条件
     * @param Roles 角色信息 0 老师 1 学生 2 家长 3助教(传3时不能与其他值同时传)
     * @param callback
     */
    public static void requestLoadMemberSchoolData(@NonNull String MemberId,
                                                   @Nullable String SchoolName,
                                                   @NonNull String Roles,
                                                   @NonNull DataSource.Callback<List<MemberSchoolEntity>> callback){
        /* Lambda 从简入繁的演变过程
        View view = new View(null);
        view.setOnClickListener(View::toString);
        view.setOnClickListener(v -> v.toString());
        view.setOnClickListener((v) -> {
            v.toString();
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.toString();
            }
        });*/

        // 准备数据
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("MemberId", MemberId);
        requestVo.addParams("SchoolName", SchoolName);
        requestVo.addParams("Roles", Roles);

        RequestParams params = new RequestParams(AppConfig.ServerUrl.PostRequestTutorialOrgan);
        params.setAsJsonContent(true);
        params.setBodyContent(requestVo.getParamsWithoutToken());
        params.setConnectTimeout(10000);
        LogUtil.i(SchoolHelper.class, "send request ==== " + params.getUri());
        x.http().post(params, new StringCallback<String>() {

            @Override
            public void onSuccess(String str) {
                LogUtil.i(SchoolHelper.class, "request " + params.getUri() + " result :" + str);
                TypeReference<LQwawaBaseResponse<List<MemberSchoolEntity>>> typeReference = new TypeReference<LQwawaBaseResponse<List<MemberSchoolEntity>>>() {};
                LQwawaBaseResponse<List<MemberSchoolEntity>> response = JSON.parseObject(str, typeReference);
                if (!response.isHasError()) {
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
                LogUtil.w(SchoolHelper.class, "request " + params.getUri() + " failed");
                if (null != callback) {
                    callback.onDataNotAvailable(R.string.net_error_tip);
                }
            }
        });
    }

    /**
     * 三习关联教辅
     * @param courseId
     * @param pageIndex
     * @param pageSize
     * @param callback
     */
    public static void requestSxRelationCourseList(@Nullable String courseId,
                                                   int pageIndex,int pageSize,
                                                   @NonNull DataSource.Callback<List<CourseVo>> callback){
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("courseId",courseId);
        requestVo.addParams("pageIndex",pageIndex);
        requestVo.addParams("pageSize",pageSize);
        RequestParams params = new RequestParams(AppConfig.ServerUrl.PostGetSxRelationCourseList);
        params.setAsJsonContent(true);
        params.setBodyContent(requestVo.getParamsWithoutToken());
        params.setConnectTimeout(10000);
        LogUtil.i(SchoolHelper.class, "send request ==== " + params.getUri());
        x.http().post(params, new StringCallback<String>() {
            @Override
            public void onSuccess(String str) {
                LogUtil.i(SchoolHelper.class, "request " + params.getUri() + " result :" + str);
                TypeReference<ResponseVo<List<CourseVo>>> typeReference =
                        new TypeReference<ResponseVo<List<CourseVo>>>() {};
                ResponseVo<List<CourseVo>> result = JSON.parseObject(str, typeReference);
                if (result.isSucceed()) {
                    List<CourseVo> data = result.getData();
                    if (!EmptyUtil.isEmpty(callback)) {
                        callback.onDataLoaded(data);
                    }
                } else {
                    Factory.decodeRspCode(result.getCode(), callback);
                }
            }
            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.w(SchoolHelper.class, "request " + params.getUri() + " failed");
                if (null != callback) {
                    callback.onDataNotAvailable(R.string.net_error_tip);
                }
            }
        });
    }

}
