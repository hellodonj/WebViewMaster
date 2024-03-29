package com.lqwawa.intleducation.factory.helper;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.Factory;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.vo.RequestVo;
import com.lqwawa.intleducation.base.vo.ResponseVo;
import com.lqwawa.intleducation.common.utils.LogUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.StringCallback;
import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.home.LanguageType;
import com.lqwawa.intleducation.module.discovery.ui.subject.SetupConfigType;

import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.List;

/**
 * 标签设置请求帮助类
 */
public class LQConfigHelper {

    /**
     * 获取老师已经设置的标签
     * @param hostId 老师学生班级Id
     * @param dataType 1 老师 2学生 3班级
     * @param language {@link LanguageType#LANGUAGE_CHINESE},{@link LanguageType#LANGUAGE_OTHER}
     * @param callback 回调对象
     */
    public static void requestSetupConfigData(@Nullable String hostId,
                                                @NonNull @SetupConfigType.SetupConfigRes int dataType,
                                                @NonNull @LanguageType.LanguageRes int language,
                                                @NonNull final DataSource.Callback<List<LQCourseConfigEntity>> callback) {

        RequestVo requestVo = new RequestVo();
        requestVo.addParams("hostId", hostId);
        requestVo.addParams("language", language);
        requestVo.addParams("dataType", dataType);
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.GetSetupConfigDataUrl + requestVo.getParams());
        params.setConnectTimeout(10000);
        LogUtil.i(LQConfigHelper.class, "1send request ==== " + params.getUri());
        x.http().get(params, new StringCallback<String>() {
            @Override
            public void onSuccess(String str) {
                LogUtil.i(LQConfigHelper.class, "1request " + params.getUri() + " result :" + str);
                TypeReference<ResponseVo<List<LQCourseConfigEntity>>> typeReference =
                        new TypeReference<ResponseVo<List<LQCourseConfigEntity>>>() {
                        };
                ResponseVo<List<LQCourseConfigEntity>> result = JSON.parseObject(str, typeReference);
                if (result.isSucceed()) {
                    if (callback != null && result.getData() != null) {
                        callback.onDataLoaded(result.getData());
                    }
                } else {
                    Factory.decodeRspCode(result.getCode(), callback);
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                if (null != callback) {
                    callback.onDataNotAvailable(R.string.net_error_tip);
                }
            }
        });
    }

    /**
     * 获取老师已经设置的标签
     * @param hostId
     * @param dataType
     * @param courseId
     * @param sourceType 教案个人统计添加参数
     * @param language
     * @param callback
     */
    public static void requestSetupConfigData(@Nullable String hostId,
                                              @NonNull @SetupConfigType.SetupConfigRes int dataType,
                                              String courseId,
                                              int sourceType,
                                              @NonNull @LanguageType.LanguageRes int language,
                                              @NonNull final DataSource.Callback<List<LQCourseConfigEntity>> callback) {

        RequestVo requestVo = new RequestVo();
        requestVo.addParams("hostId", hostId);
        requestVo.addParams("language", language);
        requestVo.addParams("dataType", dataType);
        requestVo.addParams("courseId",courseId);
        requestVo.addParams("sourceType",sourceType);
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.GetSetupConfigDataUrl + requestVo.getParams());
        params.setConnectTimeout(10000);
        LogUtil.i(LQConfigHelper.class, "1send request ==== " + params.getUri());
        x.http().get(params, new StringCallback<String>() {
            @Override
            public void onSuccess(String str) {
                LogUtil.i(LQConfigHelper.class, "1request " + params.getUri() + " result :" + str);
                TypeReference<ResponseVo<List<LQCourseConfigEntity>>> typeReference =
                        new TypeReference<ResponseVo<List<LQCourseConfigEntity>>>() {
                        };
                ResponseVo<List<LQCourseConfigEntity>> result = JSON.parseObject(str, typeReference);
                if (result.isSucceed()) {
                    if (callback != null && result.getData() != null) {
                        callback.onDataLoaded(result.getData());
                    }
                } else {
                    Factory.decodeRspCode(result.getCode(), callback);
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                if (null != callback) {
                    callback.onDataNotAvailable(R.string.net_error_tip);
                }
            }
        });
    }


    /**
     * 老师选择授课标签的选择列表
     * @param memberId 老师Id
     * @param language {@link LanguageType#LANGUAGE_CHINESE},{@link LanguageType#LANGUAGE_OTHER}
     * @param callback 回调对象
     */
    public static void requestAssignConfigData(@NonNull String memberId,
                                              @NonNull @LanguageType.LanguageRes int language,
                                              @NonNull final DataSource.Callback<List<LQCourseConfigEntity>> callback) {

        RequestVo requestVo = new RequestVo();
        requestVo.addParams("memberId", memberId);
        requestVo.addParams("language", language);
        requestVo.addParams("version", 1);
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.GetAssignConfigDataUrl + requestVo.getParams());
        params.setConnectTimeout(10000);
        LogUtil.i(LQConfigHelper.class, "2send request ==== " + params.getUri());
        x.http().get(params, new StringCallback<String>() {
            @Override
            public void onSuccess(String str) {
                LogUtil.i(LQConfigHelper.class, "2request " + params.getUri() + " result :" + str);
                TypeReference<ResponseVo<List<LQCourseConfigEntity>>> typeReference =
                        new TypeReference<ResponseVo<List<LQCourseConfigEntity>>>() {
                        };
                ResponseVo<List<LQCourseConfigEntity>> result = JSON.parseObject(str, typeReference);
                if (result.isSucceed()) {
                    if (callback != null && result.getData() != null) {
                        callback.onDataLoaded(result.getData());
                    }
                } else {
                    Factory.decodeRspCode(result.getCode(), callback);
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                if (null != callback) {
                    callback.onDataNotAvailable(R.string.net_error_tip);
                }
            }
        });
    }

    /**
     * 老师选择授课标签的选择列表
     * @param memberId 老师Id
     * @param ids 科目拼接的ids
     * @param callback 回调对象
     */
    public static void requestSaveTeacherConfig(@NonNull String memberId,
                                               @NonNull String ids,
                                               @NonNull final DataSource.Callback<Boolean> callback) {

        RequestVo requestVo = new RequestVo();
        requestVo.addParams("memberId", memberId);
        requestVo.addParams("ids", ids);
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.GetSaveTeacherConfigUrl + requestVo.getParams());
        params.setConnectTimeout(10000);
        LogUtil.i(LQConfigHelper.class, "3send request ==== " + params.getUri());
        x.http().get(params, new StringCallback<String>() {
            @Override
            public void onSuccess(String str) {
                LogUtil.i(LQConfigHelper.class, "3request " + params.getUri() + " result :" + str);
                TypeReference<ResponseVo<Void>> typeReference =
                        new TypeReference<ResponseVo<Void>>() {
                        };
                ResponseVo<Void> result = JSON.parseObject(str, typeReference);
                if (result.isSucceed()) {
                    if (callback != null) {
                        callback.onDataLoaded(true);
                    }
                } else {
                    Factory.decodeRspCode(result.getCode(), callback);
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                if (null != callback) {
                    callback.onDataNotAvailable(R.string.net_error_tip);
                }
            }
        });
    }
}
