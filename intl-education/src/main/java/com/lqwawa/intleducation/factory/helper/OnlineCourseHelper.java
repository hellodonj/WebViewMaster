package com.lqwawa.intleducation.factory.helper;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.Factory;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.vo.RequestVo;
import com.lqwawa.intleducation.base.vo.ResponseVo;
import com.lqwawa.intleducation.common.Common;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.LogUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.StringCallback;
import com.lqwawa.intleducation.factory.data.entity.BaseModelDataEntity;
import com.lqwawa.intleducation.factory.data.entity.BaseModelEntity;
import com.lqwawa.intleducation.factory.data.entity.ClassDetailEntity;
import com.lqwawa.intleducation.factory.data.entity.ClassNotificationEntity;
import com.lqwawa.intleducation.factory.data.entity.JoinClassEntity;
import com.lqwawa.intleducation.factory.data.entity.LQTeacherEntity;
import com.lqwawa.intleducation.factory.data.entity.LQwawaBaseResponse;
import com.lqwawa.intleducation.factory.data.entity.course.CourseStatisticsEntity;
import com.lqwawa.intleducation.factory.data.entity.course.LearningProgressEntity;
import com.lqwawa.intleducation.factory.data.entity.online.NewOnlineConfigEntity;
import com.lqwawa.intleducation.factory.data.entity.OnlineClassEntity;
import com.lqwawa.intleducation.factory.data.entity.OnlineCommentEntity;
import com.lqwawa.intleducation.factory.data.entity.OnlineRelevanceCourseEntity;
import com.lqwawa.intleducation.factory.data.entity.OnlineSchoolInfoEntity;
import com.lqwawa.intleducation.factory.data.entity.online.OnlineConfigEntity;
import com.lqwawa.intleducation.factory.data.entity.online.OnlineStudyEntity;
import com.lqwawa.intleducation.factory.data.entity.online.ParamResponseVo;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.home.LanguageType;
import com.lqwawa.intleducation.module.discovery.ui.study.OnlineStudyType;
import com.lqwawa.intleducation.module.onclass.OnlineSortType;
import com.lqwawa.intleducation.module.user.tool.UserHelper;
import com.lqwawa.lqbaselib.net.ErrorCodeUtil;

import org.xutils.http.RequestParams;
import org.xutils.x;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 在线课堂请求帮助类 Model
 * @date 2018/05/29 16:03
 * @history v1.0
 * **********************************
 */
public class OnlineCourseHelper {

    /**
     * 获取某机构学校在线课堂列表
     *
     * @param schoolId  机构学校Id
     * @param keyWord   搜索关键字
     * @param pageIndex 页码
     * @param pageSize  页加载数
     * @param sort      排序类型
     * @param callback  数据回调对象
     */
    public static void requestOnlineCourseData(@NonNull String schoolId,
                                               @NonNull String keyWord,
                                               int pageIndex, int pageSize,
                                               @NonNull @OnlineSortType.OnlineSortRes String sort,
                                               @NonNull DataSource.Callback<List<OnlineClassEntity>> callback) {
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("schoolId", schoolId);
        requestVo.addParams("pageIndex", pageIndex);
        requestVo.addParams("pageSize", pageSize);
        requestVo.addParams("sort", sort);
        if (!EmptyUtil.isEmpty(keyWord)) {
            try {
                requestVo.addParams("keyWord", URLEncoder.encode(keyWord, "UTF-8"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        RequestParams params = new RequestParams(AppConfig.ServerUrl.GetOnlineCourseDataInSchool + requestVo.getParams());
        params.setConnectTimeout(10000);
        LogUtil.i(OnlineCourseHelper.class, "send request ==== " + params.getUri());
        x.http().get(params, new StringCallback<String>() {

            @Override
            public void onSuccess(String str) {
                // str = "{\"total\": 1,\"data\": [{\"organId\": \"3583ae72-bfdf-40ef-82e0-9d430b31ce45\",\"createTime\": \"\",\"progressStatus\": 0,\"teachersId\": \"4b0727b7-26b6-4aed-bd33-06dcee904ae1\",\"introduce\": \"ddsadasdasdasdsadas\",\"courseId\": 0,\"endTime\": \"2018-05-20 19:22:15\",\"firstId\": 5000,\"id\": 1,\"isDelete\": false,\"startTime\": \"2018-05-10 15:38:40\",\"verifyTime\": \"\",\"thumbnailUrl\": \"http://filetestop.lqwawa.com/UploadFiles/20160516115501/00000000-0000-0000-0000-000000000000/e2439959-b359-4553-871e-99e9af552219.jpg\",\"name\": \"雅思写作暑假班\",\"secondName\": \"\",\"suitObj\": \"\",\"firstName\": \"英语特色班\",\"organName\": \"青岛两栖蛙蛙信息技术有限公司\",\"createId\": \"\",\"secondId\": 5003,\"classId\": \"0\",\"teachersName\": \"陸老师\",\"createName\": \"\",\"price\": 880,\"joinCount\": 0,\"classCode\": \"0\",\"learnGoal\": \"\",\"verifyStatus\": 1,\"deleteTime\": \"\"},{\"organId\": \"3583ae72-bfdf-40ef-82e0-9d430b31ce45\",\"createTime\": \"\",\"progressStatus\": 0,\"teachersId\": \"4b0727b7-26b6-4aed-bd33-06dcee904ae1\",\"introduce\": \"ddsadasdasdasdsadas\",\"courseId\": 0,\"endTime\": \"2018-05-20 19:22:15\",\"firstId\": 5000,\"id\": 1,\"isDelete\": false,\"startTime\": \"2018-05-10 15:38:40\",\"verifyTime\": \"\",\"thumbnailUrl\": \"http://filetestop.lqwawa.com/UploadFiles/20160516115501/00000000-0000-0000-0000-000000000000/e2439959-b359-4553-871e-99e9af552219.jpg\",\"name\": \"雅思写作暑假班\",\"secondName\": \"\",\"suitObj\": \"\",\"firstName\": \"英语特色班\",\"organName\": \"青岛两栖蛙蛙信息技术有限公司\",\"createId\": \"\",\"secondId\": 5003,\"classId\": \"0\",\"teachersName\": \"陸老师\",\"createName\": \"\",\"price\": 880,\"joinCount\": 0,\"classCode\": \"0\",\"learnGoal\": \"\",\"verifyStatus\": 1,\"deleteTime\": \"\"},{\"organId\": \"3583ae72-bfdf-40ef-82e0-9d430b31ce45\",\"createTime\": \"\",\"progressStatus\": 0,\"teachersId\": \"4b0727b7-26b6-4aed-bd33-06dcee904ae1\",\"introduce\": \"ddsadasdasdasdsadas\",\"courseId\": 0,\"endTime\": \"2018-05-20 19:22:15\",\"firstId\": 5000,\"id\": 1,\"isDelete\": false,\"startTime\": \"2018-05-10 15:38:40\",\"verifyTime\": \"\",\"thumbnailUrl\": \"http://filetestop.lqwawa.com/UploadFiles/20160516115501/00000000-0000-0000-0000-000000000000/e2439959-b359-4553-871e-99e9af552219.jpg\",\"name\": \"雅思写作暑假班\",\"secondName\": \"\",\"suitObj\": \"\",\"firstName\": \"英语特色班\",\"organName\": \"青岛两栖蛙蛙信息技术有限公司\",\"createId\": \"\",\"secondId\": 5003,\"classId\": \"0\",\"teachersName\": \"陸老师\",\"createName\": \"\",\"price\": 880,\"joinCount\": 0,\"classCode\": \"0\",\"learnGoal\": \"\",\"verifyStatus\": 1,\"deleteTime\": \"\"}],\"code\": 0}";
                LogUtil.i(OnlineCourseHelper.class, "request " + params.getUri() + " result :" + str);
                TypeReference<ResponseVo<List<OnlineClassEntity>>> typeReference =
                        new TypeReference<ResponseVo<List<OnlineClassEntity>>>() {
                        };
                ResponseVo<List<OnlineClassEntity>> responseVo = JSON.parseObject(str, typeReference);
                if (responseVo.isSucceed()) {
                    List<OnlineClassEntity> data = responseVo.getData();
                    if (!EmptyUtil.isEmpty(callback)) {
                        callback.onDataLoaded(data);
                    }
                } else {
                    Factory.decodeRspCode(responseVo.getCode(), callback);
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.w(OnlineCourseHelper.class, "request " + params.getUri() + " failed");
                if (null != callback) {
                    callback.onDataNotAvailable(R.string.net_error_tip);
                }
            }
        });
    }


    /**
     * 获取在线学习班级列表 V5.9出来的新街口
     *
     * @param schoolId  机构学校Id
     * @param keyWord   搜索关键字
     * @param pageIndex 页码
     * @param pageSize  页加载数
     * @param sort      排序类型
     * @param firstId   根据num返回将LabelId替换进去 默认不传或者0 就是全部
     * @param secondId  根据num返回将LabelId替换进去 默认不传或者0 就是全部
     * @param thirdId   根据num返回将LabelId替换进去 默认不传或者0 就是全部
     * @param fourthId  根据num返回将LabelId替换进去 默认不传或者0 就是全部
     * @param callback  数据回调对象
     */
    public static void requestOnlineStudyClassData(@NonNull String schoolId,
                                                   @NonNull String keyWord,
                                                   int pageIndex, int pageSize,
                                                   @OnlineStudyType.OnlineStudyRes int sort,
                                                   int firstId, int secondId, int thirdId, int fourthId,
                                                   @NonNull DataSource.Callback<List<OnlineClassEntity>> callback) {
        RequestVo requestVo = new RequestVo();


        // 1或者不传,过滤测试数据,0测试版本不过滤
        requestVo.addParams("isAppStore",Common.Constance.isAppStore);

        if (EmptyUtil.isNotEmpty(schoolId)) {
            requestVo.addParams("schoolId", schoolId);
        }
        requestVo.addParams("pageIndex", pageIndex);
        requestVo.addParams("pageSize", pageSize);

        if (sort != -1) {
            // sort不传就是最新
            requestVo.addParams("sort", sort);
        }

        if (!EmptyUtil.isEmpty(keyWord)) {
            try {
                requestVo.addParams("keyWord", URLEncoder.encode(keyWord, "UTF-8"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (firstId != -1) {
            requestVo.addParams("firstId", firstId);
        }

        if (secondId != -1) {
            requestVo.addParams("secondId", secondId);
        }

        if (thirdId != -1) {
            requestVo.addParams("thirdId", thirdId);
        }

        if (fourthId != -1) {
            requestVo.addParams("fourthId", fourthId);
        }
        RequestParams params = new RequestParams(AppConfig.ServerUrl.GetOnlineCourseDataInSchool + requestVo.getParams());
        params.setConnectTimeout(10000);
        LogUtil.i(OnlineCourseHelper.class, "send request ==== " + params.getUri());
        x.http().get(params, new StringCallback<String>() {

            @Override
            public void onSuccess(String str) {
                // str = "{\"total\": 1,\"data\": [{\"organId\": \"3583ae72-bfdf-40ef-82e0-9d430b31ce45\",\"createTime\": \"\",\"progressStatus\": 0,\"teachersId\": \"4b0727b7-26b6-4aed-bd33-06dcee904ae1\",\"introduce\": \"ddsadasdasdasdsadas\",\"courseId\": 0,\"endTime\": \"2018-05-20 19:22:15\",\"firstId\": 5000,\"id\": 1,\"isDelete\": false,\"startTime\": \"2018-05-10 15:38:40\",\"verifyTime\": \"\",\"thumbnailUrl\": \"http://filetestop.lqwawa.com/UploadFiles/20160516115501/00000000-0000-0000-0000-000000000000/e2439959-b359-4553-871e-99e9af552219.jpg\",\"name\": \"雅思写作暑假班\",\"secondName\": \"\",\"suitObj\": \"\",\"firstName\": \"英语特色班\",\"organName\": \"青岛两栖蛙蛙信息技术有限公司\",\"createId\": \"\",\"secondId\": 5003,\"classId\": \"0\",\"teachersName\": \"陸老师\",\"createName\": \"\",\"price\": 880,\"joinCount\": 0,\"classCode\": \"0\",\"learnGoal\": \"\",\"verifyStatus\": 1,\"deleteTime\": \"\"},{\"organId\": \"3583ae72-bfdf-40ef-82e0-9d430b31ce45\",\"createTime\": \"\",\"progressStatus\": 0,\"teachersId\": \"4b0727b7-26b6-4aed-bd33-06dcee904ae1\",\"introduce\": \"ddsadasdasdasdsadas\",\"courseId\": 0,\"endTime\": \"2018-05-20 19:22:15\",\"firstId\": 5000,\"id\": 1,\"isDelete\": false,\"startTime\": \"2018-05-10 15:38:40\",\"verifyTime\": \"\",\"thumbnailUrl\": \"http://filetestop.lqwawa.com/UploadFiles/20160516115501/00000000-0000-0000-0000-000000000000/e2439959-b359-4553-871e-99e9af552219.jpg\",\"name\": \"雅思写作暑假班\",\"secondName\": \"\",\"suitObj\": \"\",\"firstName\": \"英语特色班\",\"organName\": \"青岛两栖蛙蛙信息技术有限公司\",\"createId\": \"\",\"secondId\": 5003,\"classId\": \"0\",\"teachersName\": \"陸老师\",\"createName\": \"\",\"price\": 880,\"joinCount\": 0,\"classCode\": \"0\",\"learnGoal\": \"\",\"verifyStatus\": 1,\"deleteTime\": \"\"},{\"organId\": \"3583ae72-bfdf-40ef-82e0-9d430b31ce45\",\"createTime\": \"\",\"progressStatus\": 0,\"teachersId\": \"4b0727b7-26b6-4aed-bd33-06dcee904ae1\",\"introduce\": \"ddsadasdasdasdsadas\",\"courseId\": 0,\"endTime\": \"2018-05-20 19:22:15\",\"firstId\": 5000,\"id\": 1,\"isDelete\": false,\"startTime\": \"2018-05-10 15:38:40\",\"verifyTime\": \"\",\"thumbnailUrl\": \"http://filetestop.lqwawa.com/UploadFiles/20160516115501/00000000-0000-0000-0000-000000000000/e2439959-b359-4553-871e-99e9af552219.jpg\",\"name\": \"雅思写作暑假班\",\"secondName\": \"\",\"suitObj\": \"\",\"firstName\": \"英语特色班\",\"organName\": \"青岛两栖蛙蛙信息技术有限公司\",\"createId\": \"\",\"secondId\": 5003,\"classId\": \"0\",\"teachersName\": \"陸老师\",\"createName\": \"\",\"price\": 880,\"joinCount\": 0,\"classCode\": \"0\",\"learnGoal\": \"\",\"verifyStatus\": 1,\"deleteTime\": \"\"}],\"code\": 0}";
                LogUtil.i(OnlineCourseHelper.class, "request " + params.getUri() + " result :" + str);
                TypeReference<ResponseVo<List<OnlineClassEntity>>> typeReference =
                        new TypeReference<ResponseVo<List<OnlineClassEntity>>>() {
                        };
                ResponseVo<List<OnlineClassEntity>> responseVo = JSON.parseObject(str, typeReference);
                if (responseVo.isSucceed()) {
                    List<OnlineClassEntity> data = responseVo.getData();
                    if (!EmptyUtil.isEmpty(callback)) {
                        callback.onDataLoaded(data);
                    }
                } else {
                    Factory.decodeRspCode(responseVo.getCode(), callback);
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.w(OnlineCourseHelper.class, "request " + params.getUri() + " failed");
                if (null != callback) {
                    callback.onDataNotAvailable(R.string.net_error_tip);
                }
            }
        });
    }

    /**
     * 获取课程关联的在线课堂列表
     *
     * @param courseId  课程id
     * @param pageIndex 页码
     * @param pageSize  页加载条目数
     * @param callback  数据回调对象
     */
    public static void requestOnlineCourseDataByCourseId(@NonNull String courseId,
                                                         int pageIndex, int pageSize,
                                                         @NonNull DataSource.Callback<ParamResponseVo<List<OnlineClassEntity>>> callback) {
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("courseId", courseId);
        requestVo.addParams("pageIndex", pageIndex);
        requestVo.addParams("pageSize", pageSize);
        RequestParams params = new RequestParams(AppConfig.ServerUrl.GetOnlineClassDataByCourseId + requestVo.getParams());
        params.setConnectTimeout(10000);
        LogUtil.i(OnlineCourseHelper.class, "send request ==== " + params.getUri());
        x.http().get(params, new StringCallback<String>() {

            @Override
            public void onSuccess(String str) {
                // str = "{\"total\": 1,\"data\": [{\"organId\": \"3583ae72-bfdf-40ef-82e0-9d430b31ce45\",\"createTime\": \"\",\"progressStatus\": 0,\"teachersId\": \"4b0727b7-26b6-4aed-bd33-06dcee904ae1\",\"introduce\": \"ddsadasdasdasdsadas\",\"courseId\": 0,\"endTime\": \"2018-05-20 19:22:15\",\"firstId\": 5000,\"id\": 1,\"isDelete\": false,\"startTime\": \"2018-05-10 15:38:40\",\"verifyTime\": \"\",\"thumbnailUrl\": \"http://filetestop.lqwawa.com/UploadFiles/20160516115501/00000000-0000-0000-0000-000000000000/e2439959-b359-4553-871e-99e9af552219.jpg\",\"name\": \"雅思写作暑假班\",\"secondName\": \"\",\"suitObj\": \"\",\"firstName\": \"英语特色班\",\"organName\": \"青岛两栖蛙蛙信息技术有限公司\",\"createId\": \"\",\"secondId\": 5003,\"classId\": \"0\",\"teachersName\": \"陸老师\",\"createName\": \"\",\"price\": 880,\"joinCount\": 0,\"classCode\": \"0\",\"learnGoal\": \"\",\"verifyStatus\": 1,\"deleteTime\": \"\"},{\"organId\": \"3583ae72-bfdf-40ef-82e0-9d430b31ce45\",\"createTime\": \"\",\"progressStatus\": 0,\"teachersId\": \"4b0727b7-26b6-4aed-bd33-06dcee904ae1\",\"introduce\": \"ddsadasdasdasdsadas\",\"courseId\": 0,\"endTime\": \"2018-05-20 19:22:15\",\"firstId\": 5000,\"id\": 1,\"isDelete\": false,\"startTime\": \"2018-05-10 15:38:40\",\"verifyTime\": \"\",\"thumbnailUrl\": \"http://filetestop.lqwawa.com/UploadFiles/20160516115501/00000000-0000-0000-0000-000000000000/e2439959-b359-4553-871e-99e9af552219.jpg\",\"name\": \"雅思写作暑假班\",\"secondName\": \"\",\"suitObj\": \"\",\"firstName\": \"英语特色班\",\"organName\": \"青岛两栖蛙蛙信息技术有限公司\",\"createId\": \"\",\"secondId\": 5003,\"classId\": \"0\",\"teachersName\": \"陸老师\",\"createName\": \"\",\"price\": 880,\"joinCount\": 0,\"classCode\": \"0\",\"learnGoal\": \"\",\"verifyStatus\": 1,\"deleteTime\": \"\"},{\"organId\": \"3583ae72-bfdf-40ef-82e0-9d430b31ce45\",\"createTime\": \"\",\"progressStatus\": 0,\"teachersId\": \"4b0727b7-26b6-4aed-bd33-06dcee904ae1\",\"introduce\": \"ddsadasdasdasdsadas\",\"courseId\": 0,\"endTime\": \"2018-05-20 19:22:15\",\"firstId\": 5000,\"id\": 1,\"isDelete\": false,\"startTime\": \"2018-05-10 15:38:40\",\"verifyTime\": \"\",\"thumbnailUrl\": \"http://filetestop.lqwawa.com/UploadFiles/20160516115501/00000000-0000-0000-0000-000000000000/e2439959-b359-4553-871e-99e9af552219.jpg\",\"name\": \"雅思写作暑假班\",\"secondName\": \"\",\"suitObj\": \"\",\"firstName\": \"英语特色班\",\"organName\": \"青岛两栖蛙蛙信息技术有限公司\",\"createId\": \"\",\"secondId\": 5003,\"classId\": \"0\",\"teachersName\": \"陸老师\",\"createName\": \"\",\"price\": 880,\"joinCount\": 0,\"classCode\": \"0\",\"learnGoal\": \"\",\"verifyStatus\": 1,\"deleteTime\": \"\"}],\"code\": 0}";
                LogUtil.i(OnlineCourseHelper.class, "request " + params.getUri() + " result :" + str);
                TypeReference<ParamResponseVo<List<OnlineClassEntity>>> typeReference =
                        new TypeReference<ParamResponseVo<List<OnlineClassEntity>>>() {
                        };
                ParamResponseVo<List<OnlineClassEntity>> responseVo = JSON.parseObject(str, typeReference);
                if (responseVo.isSucceed()) {
                    if (!EmptyUtil.isEmpty(callback)) {
                        callback.onDataLoaded(responseVo);
                    }
                } else {
                    Factory.decodeRspCode(responseVo.getCode(), callback);
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.w(OnlineCourseHelper.class, "request " + params.getUri() + " failed");
                if (null != callback) {
                    callback.onDataNotAvailable(R.string.net_error_tip);
                }
            }
        });
    }

    /**
     * 参加在线课堂免费班级
     *
     * @param classId  免费班级ID
     * @param callback 数据回调
     */
    public static void joinInOnlineGratisClass(@NonNull int classId,
                                               @NonNull DataSource.Callback<Boolean> callback) {
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("onlineCourseId", classId);
        RequestParams params = new RequestParams(AppConfig.ServerUrl.GetJoinInOnlineGratisCourse + requestVo.getParams());
        params.setConnectTimeout(10000);
        LogUtil.i(OnlineCourseHelper.class, "send request ==== " + params.getUri());
        x.http().get(params, new StringCallback<String>() {

            @Override
            public void onSuccess(String str) {
                LogUtil.i(OnlineCourseHelper.class, "request " + params.getUri() + " result :" + str);
                TypeReference<ResponseVo> typeReference =
                        new TypeReference<ResponseVo>() {
                        };
                ResponseVo responseVo = JSON.parseObject(str, typeReference);
                if (responseVo.isSucceed()) {
                    if (!EmptyUtil.isEmpty(callback)) {
                        callback.onDataLoaded(true);
                    }
                } else {
                    if (!EmptyUtil.isEmpty(callback)) {
                        callback.onDataLoaded(false);
                    }
                    Factory.decodeRspCode(responseVo.getCode(), callback);
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.w(OnlineCourseHelper.class, "request " + params.getUri() + " failed");
                if (null != callback) {
                    callback.onDataNotAvailable(R.string.net_error_tip);
                }
            }
        });
    }

    /**
     * 加载班级详情信息
     *
     * @param memberId 用户id
     * @param classId  班级id
     * @param callback 数据回调对象
     */
    public static void loadOnlineClassInfo(@NonNull String memberId,
                                           @NonNull String classId,
                                           @NonNull DataSource.Callback<JoinClassEntity> callback) {

        // 准备数据
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("MemberId", memberId);
        jsonObject.put("ClassId", classId);

        RequestParams params = new RequestParams(AppConfig.ServerUrl.PostLoadClassInfo);
        params.setAsJsonContent(true);
        params.setBodyContent(jsonObject.toJSONString());
        params.setConnectTimeout(10000);
        LogUtil.i(OnlineCourseHelper.class, "send request ==== " + params.getUri());
        x.http().post(params, new StringCallback<String>() {

            @Override
            public void onSuccess(String str) {
                LogUtil.i(OnlineCourseHelper.class, "request " + params.getUri() + " result :" + str);
                TypeReference<Map<String, Object>> mapTypeReference = new TypeReference<Map<String, Object>>() {
                };
                Map<String, Object> result = JSON.parseObject(str, mapTypeReference);
                if ((int) result.get("ErrorCode") == 0) {
                    Map<String, Object> model = (Map<String, Object>) result.get("Model");
                    String dataStr = JSONObject.toJSONString(model.get("Data"));
                    JoinClassEntity entity = JSONObject.parseObject(dataStr, JoinClassEntity.class);
                    if (callback != null) {
                        callback.onDataLoaded(entity);
                    }
                } else {
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
                LogUtil.w(OnlineCourseHelper.class, "request " + params.getUri() + " failed");
                if (null != callback) {
                    callback.onDataNotAvailable(R.string.net_error_tip);
                }
            }
        });

    }

    /**
     * 获取在线课堂班级信息
     *
     * @param id       在线课堂班级ID
     * @param callback 数据回调对象
     */
    public static void requestClassDetail(int id,
                                          @NonNull DataSource.Callback<ClassDetailEntity> callback) {
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("id", id);
        RequestParams params = new RequestParams(AppConfig.ServerUrl.GetClassDetailInfo + requestVo.getParams());
        params.setConnectTimeout(10000);
        LogUtil.i(OnlineCourseHelper.class, "send request ==== " + params.getUri());
        x.http().get(params, new StringCallback<String>() {

            @Override
            public void onSuccess(String str) {
                LogUtil.i(OnlineCourseHelper.class, "request " + params.getUri() + " result :" + str);

                TypeReference<ClassDetailEntity> typeReference =
                        new TypeReference<ClassDetailEntity>() {
                        };
                ClassDetailEntity entity = JSON.parseObject(str, typeReference);
                if (entity.isSucceed()) {
                    if (!EmptyUtil.isEmpty(callback)) {
                        callback.onDataLoaded(entity);
                    }
                } else {
                    Factory.decodeRspCode(entity.getCode(), callback);
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.w(OnlineCourseHelper.class, "request " + params.getUri() + " failed");
                if (null != callback) {
                    callback.onDataNotAvailable(R.string.net_error_tip);
                }
            }
        });
    }

    /**
     * 获取在线课堂班级评论数据
     *
     * @param id        班级Id
     * @param pageIndex 分页数目
     * @param pageSize  每页加载数量
     * @param callback  回调对象
     */
    public static void requestOnlineCommentData(int id,
                                                int pageIndex,
                                                int pageSize,
                                                @NonNull DataSource.Callback<OnlineCommentEntity> callback) {
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("id", id);
        requestVo.addParams("pageIndex", pageIndex);
        requestVo.addParams("pageSize", pageSize);
        RequestParams params = new RequestParams(AppConfig.ServerUrl.GetOnlineClassCommentData + requestVo.getParams());
        params.setConnectTimeout(10000);
        LogUtil.i(OnlineCourseHelper.class, "send request ==== " + params.getUri());
        x.http().get(params, new StringCallback<String>() {

            @Override
            public void onSuccess(String str) {
                LogUtil.i(OnlineCourseHelper.class, "request " + params.getUri() + " result :" + str);
                try {
                    TypeReference<OnlineCommentEntity> typeReference = new TypeReference<OnlineCommentEntity>() {
                    };
                    OnlineCommentEntity entity = JSON.parseObject(str, typeReference);
                    if (!EmptyUtil.isEmpty(callback)) {
                        callback.onDataLoaded(entity);
                    }
                } catch (Exception e) {
                    if (null != callback) {
                        callback.onDataNotAvailable(R.string.net_error_tip);
                    }
                }

            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.w(OnlineCourseHelper.class, "request " + params.getUri() + " failed");
                if (null != callback) {
                    callback.onDataNotAvailable(R.string.net_error_tip);
                }
            }
        });
    }

    /**
     * 提交评论
     *
     * @param type      0评论 1回复
     * @param courseId  班级Id
     * @param commentId 回复人的id commitId等于-1 则不传
     * @param content   评论内容
     * @param starLevel 评星
     * @param callback  回调对象
     */
    public static void requestCommitComment(int type,
                                            int courseId,
                                            @NonNull String commentId,
                                            @NonNull String content,
                                            int starLevel,
                                            @NonNull DataSource.Callback<Boolean> callback) {
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("type", type);
        requestVo.addParams("courseId", courseId);
        try {
            // 对评论内容进行URL编码
            content = URLEncoder.encode(content, "UTF-8");
            content = content.replaceAll("%0A", "\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
        requestVo.addParams("content", content);

        if (EmptyUtil.isNotEmpty(commentId) && type == 1) {
            requestVo.addParams("commentId", commentId);
        }

        if (EmptyUtil.isEmpty(commentId) && type == 0) {
            requestVo.addParams("starLevel", starLevel);
        }

        RequestParams params = new RequestParams(AppConfig.ServerUrl.GetOnlineClassCommitComment + requestVo.getParams());
        params.setConnectTimeout(10000);
        LogUtil.i(OnlineCourseHelper.class, "send request ==== " + params.getUri());
        x.http().get(params, new StringCallback<String>() {

            @Override
            public void onSuccess(String str) {
                LogUtil.i(OnlineCourseHelper.class, "request " + params.getUri() + " result :" + str);
                TypeReference<ResponseVo> typeReference = new TypeReference<ResponseVo>() {
                };
                ResponseVo vo = JSON.parseObject(str, typeReference);
                if (!EmptyUtil.isEmpty(callback)) {
                    callback.onDataLoaded(vo.isSucceed());
                }

            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.w(OnlineCourseHelper.class, "request " + params.getUri() + " failed");
                if (null != callback) {
                    callback.onDataNotAvailable(R.string.net_error_tip);
                }
            }
        });
    }

    /**
     * 获取在线课堂班级详情通知数据
     *
     * @param classId   班级id
     * @param pageIndex 页数
     * @param pageSize  每页加载数量
     * @param callback  回调对象
     */
    public static void requestNotificationData(@NonNull String classId,
                                               int pageIndex, int pageSize,
                                               @NonNull DataSource.Callback<List<ClassNotificationEntity>> callback) {
        // 准备数据
        JSONObject jsonObject = new JSONObject();
        String memberId = UserHelper.getUserId();
        jsonObject.put("MemberId", memberId);
        // @date   :2018/6/4 0004 下午 3:59
        // @func   :先获取测试数据
        // classId = "42be1fe8-85bc-4c7b-a366-a69e00be7e1f";
        jsonObject.put("ClassId", classId);
        jsonObject.put("ByType", 1);
        jsonObject.put("ActionType", 2);
        // 传分页信息
        Map<String, Integer> pagerMap = new HashMap<>();
        pagerMap.put("PageIndex", pageIndex);
        pagerMap.put("PageSize", pageSize);
        jsonObject.put("Pager", pagerMap);

        RequestParams params = new RequestParams(AppConfig.ServerUrl.PostOnlineClassNotificationData);
        params.setAsJsonContent(true);
        params.setBodyContent(jsonObject.toJSONString());
        params.setConnectTimeout(1000);
        LogUtil.i(OnlineCourseHelper.class, "send request ==== " + params.getUri());
        x.http().post(params, new StringCallback<String>() {

            @Override
            public void onSuccess(String str) {
                // str = "{\"Model\":{\"Data\":[{\"Type\":0,\"GroupId\":null,\"IsRead\":1,\"ReadCount\":0,\"NoReadCount\":0,\"time\":\"0001-01-01T00:00:00\",\"Id\":\"eae4560c-f4d2-4d65-89f1-a6cc00f9422d\",\"Title\":\"lloop\",\"Thumbnail\":\"http://resop.lqwawa.com/course/fzkt/2016/11/28/379e85ff-e11e-4c1f-9224-9776ce6025ff/head.jpg\",\"ResourceUrl\":\"http://resop.lqwawa.com/course/fzkt/2016/11/28/379e85ff-e11e-4c1f-9224-9776ce6025ff.zip\",\"ShareAddress\":\"http://resop.lqwawa.com/weike/play?vId=26231&pType=17\",\"ResourceType\":\"17\",\"ScreenType\":0,\"ReadNumber\":\"16\",\"CommentNumber\":\"1\",\"PointNumber\":\"1\",\"CreatedTime\":\"2016-11-28 15:07:32\",\"UpdatedTime\":\"2016-11-28 15:07:31\",\"MicroId\":\"26231\",\"AuthorId\":\"4b0727b7-26b6-4aed-bd33-06dcee904ae1\",\"AuthorName\":\"陸老师\",\"CollectionNum\":0,\"Description\":\"\",\"FileSize\":\"29336\",\"LeValue\":null,\"LeStatus\":0,\"HeadPicUrl\":\"http://filetestop.lqwawa.com/UploadFiles/20170731110803/4b0727b7-26b6-4aed-bd33-06dcee904ae1/2460852e-ec74-4fab-b9fc-be384b8d97fc.png\",\"ResProperties\":null},{\"Type\":0,\"GroupId\":null,\"IsRead\":1,\"ReadCount\":0,\"NoReadCount\":0,\"time\":\"0001-01-01T00:00:00\",\"Id\":\"811a3bdf-4f49-4661-b0bb-a6ca00ff0408\",\"Title\":\"在叙 v b 吗，。浪费多少啊啊啊情人\",\"Thumbnail\":\"http://resop.lqwawa.com/course/fzkt/2016/11/26/0ab58fb6-75e5-4bbe-8dab-0c09aec66652/head.jpg?1480145308801s\",\"ResourceUrl\":\"http://resop.lqwawa.com/course/fzkt/2016/11/26/0ab58fb6-75e5-4bbe-8dab-0c09aec66652.zip\",\"ShareAddress\":\"http://resop.lqwawa.com/weike/play?vId=26078&pType=17\",\"ResourceType\":\"17\",\"ScreenType\":0,\"ReadNumber\":\"12\",\"CommentNumber\":\"0\",\"PointNumber\":\"0\",\"CreatedTime\":\"2016-11-26 15:28:29\",\"UpdatedTime\":\"2016-11-26 15:28:29\",\"MicroId\":\"26078\",\"AuthorId\":\"a7431ae3-3e36-417d-8794-64e6745c0f23\",\"AuthorName\":\"1\",\"CollectionNum\":1,\"Description\":\"\",\"FileSize\":\"374295\",\"LeValue\":null,\"LeStatus\":0,\"HeadPicUrl\":\"http://filetestop.lqwawa.com/UploadFiles/2018/02/28/a7431ae3-3e36-417d-8794-64e6745c0f23/d93084f9-5041-4c5f-b2db-9130651752f6.jpg\",\"ResProperties\":null},{\"Type\":0,\"GroupId\":null,\"IsRead\":1,\"ReadCount\":0,\"NoReadCount\":0,\"time\":\"0001-01-01T00:00:00\",\"Id\":\"06abced4-4848-4ab8-bee3-a6a301112990\",\"Title\":\"他可讲\",\"Thumbnail\":\"http://resop.lqwawa.com/course/fzkt/2016/10/18/f493df4a-1723-43ae-9e1c-045eb2189e10/head.jpg\",\"ResourceUrl\":\"http://resop.lqwawa.com/course/fzkt/2016/10/18/f493df4a-1723-43ae-9e1c-045eb2189e10.zip\",\"ShareAddress\":\"http://resop.lqwawa.com/weike/play?vId=23371&pType=17\",\"ResourceType\":\"17\",\"ScreenType\":1,\"ReadNumber\":\"31\",\"CommentNumber\":\"2\",\"PointNumber\":\"0\",\"CreatedTime\":\"2016-10-18 16:34:33\",\"UpdatedTime\":\"2016-10-18 16:34:33\",\"MicroId\":\"23371\",\"AuthorId\":\"4b0727b7-26b6-4aed-bd33-06dcee904ae1\",\"AuthorName\":\"陸老师\",\"CollectionNum\":1,\"Description\":\"\",\"FileSize\":\"29352\",\"LeValue\":null,\"LeStatus\":0,\"HeadPicUrl\":\"http://filetestop.lqwawa.com/UploadFiles/20170731110803/4b0727b7-26b6-4aed-bd33-06dcee904ae1/2460852e-ec74-4fab-b9fc-be384b8d97fc.png\",\"ResProperties\":null}],\"Pager\":{\"PageIndex\":0,\"PageSize\":30,\"RowsCount\":3,\"FirstRowIndex\":0}},\"ErrorCode\":0,\"HasError\":false,\"ErrorMessage\":null}";
                LogUtil.i(OnlineCourseHelper.class, "request " + params.getUri() + " result :" + str);
                TypeReference<Map<String, Object>> mapTypeReference = new TypeReference<Map<String, Object>>() {
                };
                Map<String, Object> result = JSON.parseObject(str, mapTypeReference);
                if ((int) result.get("ErrorCode") == 0) {
                    Map<String, Object> model = (Map<String, Object>) result.get("Model");
                    String dataStr = JSONObject.toJSONString(model.get("Data"));
                    TypeReference<List<ClassNotificationEntity>> typeReference = new TypeReference<List<ClassNotificationEntity>>() {
                    };
                    List<ClassNotificationEntity> entities = JSON.parseObject(dataStr, typeReference);
                    if (EmptyUtil.isNotEmpty(callback)) {
                        callback.onDataLoaded(entities);
                    }
                } else {
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
                LogUtil.w(OnlineCourseHelper.class, "request " + params.getUri() + " failed");
                if (null != callback) {
                    callback.onDataNotAvailable(R.string.net_error_tip);
                }
            }
        });
    }

    /**
     * 获取机构信息片段数据
     *
     * @param schoolId  机构id
     * @param callback  数据回调对象
     * @param pageIndex 页码
     * @param pageSize  页数大小
     */
    public static void requestOnlineSchoolInfoData(@NonNull String schoolId,
                                                   int pageIndex, int pageSize,
                                                   @NonNull DataSource.Callback<OnlineSchoolInfoEntity> callback) {
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("schoolId", schoolId);
        requestVo.addParams("pageIndex", pageIndex);
        requestVo.addParams("pageSize", pageSize);

        RequestParams params = new RequestParams(AppConfig.ServerUrl.GetOnlineSchoolInfoData + requestVo.getParams());
        params.setConnectTimeout(10000);
        LogUtil.i(OnlineCourseHelper.class, "send request ==== " + params.getUri());
        x.http().get(params, new StringCallback<String>() {

            @Override
            public void onSuccess(String str) {
                LogUtil.i(OnlineCourseHelper.class, "request " + params.getUri() + " result :" + str);
                TypeReference<OnlineSchoolInfoEntity> typeReference = new TypeReference<OnlineSchoolInfoEntity>() {
                };
                OnlineSchoolInfoEntity entity = JSON.parseObject(str, typeReference);
                if (entity.isSucceed()) {
                    if (!EmptyUtil.isEmpty(callback)) {
                        callback.onDataLoaded(entity);
                    }
                }

            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.w(OnlineCourseHelper.class, "request " + params.getUri() + " failed");
                if (null != callback) {
                    callback.onDataNotAvailable(R.string.net_error_tip);
                }
            }
        });
    }

    /**
     * 获取机构的老师信息
     *
     * @param schoolId  机构Id
     * @param pageIndex 分页数
     * @param pageSize  每页数量
     * @param callback  回调对象
     */
    public static void requestOnlineSchoolTeacherData(@NonNull String schoolId,
                                                      int pageIndex, int pageSize,
                                                      @NonNull DataSource.Callback<List<LQTeacherEntity>> callback) {
        // 准备数据
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("SchoolId", schoolId);
        Map<String, Integer> pagerMap = new HashMap<>();
        pagerMap.put("PageIndex", pageIndex);
        pagerMap.put("PageSize", pageSize);
        jsonObject.put("Page", pagerMap);

        RequestParams params = new RequestParams(AppConfig.ServerUrl.PostOnlineSchoolTeacherData);
        params.setAsJsonContent(true);
        params.setBodyContent(jsonObject.toJSONString());
        params.setConnectTimeout(10000);
        LogUtil.i(OnlineCourseHelper.class, "send request ==== " + params.getUri());
        x.http().post(params, new StringCallback<String>() {

            @Override
            public void onSuccess(String str) {
                LogUtil.i(OnlineCourseHelper.class, "request " + params.getUri() + " result :" + str);
                TypeReference<BaseModelEntity<LQTeacherEntity>> typeReference = new TypeReference<BaseModelEntity<LQTeacherEntity>>() {
                };
                BaseModelEntity<LQTeacherEntity> entity = JSON.parseObject(str, typeReference);
                if (!entity.isHasError()) {
                    BaseModelEntity.ModelBean<LQTeacherEntity> model = entity.getModel();
                    if (EmptyUtil.isNotEmpty(model)) {
                        List<LQTeacherEntity> entities = model.getTeacherList();
                        if (EmptyUtil.isNotEmpty(callback)) {
                            callback.onDataLoaded(entities);
                        }
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
                LogUtil.w(OnlineCourseHelper.class, "request " + params.getUri() + " failed");
                /*if(null != callback){
                    callback.onDataNotAvailable(R.string.net_error_tip);
                }*/
            }
        });
    }

    /**
     * 删除授课计划的直播
     *
     * @param id        直播id
     * @param classId   班级id
     * @param deleteAll 是否从所有发布对象中删除
     * @param callback  回调对象
     */
    public static void requestDeletePlanLive(int id,
                                             @NonNull String classId,
                                             boolean deleteAll,
                                             @NonNull DataSource.Callback<Boolean> callback) {
        // 准备数据
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Id", id);
        if (deleteAll) {
            //删除直播
            jsonObject.put("ClassId", "");
        } else {
            //删除直播对象
            jsonObject.put("ClassId", classId);
        }

        RequestParams params = new RequestParams(AppConfig.ServerUrl.PostOnlineClassDeleteLive);
        params.setAsJsonContent(true);
        params.setBodyContent(jsonObject.toJSONString());
        params.setConnectTimeout(10000);
        LogUtil.i(OnlineCourseHelper.class, "send request ==== " + params.getUri());
        x.http().post(params, new StringCallback<String>() {

            @Override
            public void onSuccess(String str) {
                LogUtil.i(OnlineCourseHelper.class, "request " + params.getUri() + " result :" + str);
                TypeReference<LQwawaBaseResponse<Object>> typeReference = new TypeReference<LQwawaBaseResponse<Object>>() {
                };
                LQwawaBaseResponse<Object> entity = JSON.parseObject(str, typeReference);
                if (entity.isSucceed()) {
                    if (EmptyUtil.isNotEmpty(callback)) {
                        callback.onDataLoaded(true);
                    }
                } else {
                    if (EmptyUtil.isNotEmpty(callback)) {
                        callback.onDataLoaded(false);
                    }

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
                LogUtil.w(OnlineCourseHelper.class, "request " + params.getUri() + " failed");
                if (null != callback) {
                    callback.onDataNotAvailable(R.string.net_error_tip);
                }
            }
        });
    }


    /**
     * 删除通知
     *
     * @param id       通知id
     * @param type     通知类型
     * @param callback 回调对象
     */
    public static void requestDeleteNotification(@NonNull String id, int type,
                                                 @NonNull DataSource.Callback<Boolean> callback) {
        // 准备数据
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("IdList", id);
        jsonObject.put("Type", type);

        RequestParams params = new RequestParams(AppConfig.ServerUrl.PostOnlineClassDeleteNotification);
        params.setAsJsonContent(true);
        params.setBodyContent(jsonObject.toJSONString());
        params.setConnectTimeout(10000);
        LogUtil.i(OnlineCourseHelper.class, "send request ==== " + params.getUri());
        x.http().post(params, new StringCallback<String>() {

            @Override
            public void onSuccess(String str) {
                LogUtil.i(OnlineCourseHelper.class, "request " + params.getUri() + " result :" + str);
                TypeReference<BaseModelEntity<Object>> typeReference = new TypeReference<BaseModelEntity<Object>>() {
                };
                BaseModelEntity<Object> entity = JSON.parseObject(str, typeReference);
                if (!entity.isHasError()) {
                    if (EmptyUtil.isNotEmpty(callback)) {
                        callback.onDataLoaded(true);
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
                LogUtil.w(OnlineCourseHelper.class, "request " + params.getUri() + " failed");
                if (null != callback) {
                    callback.onDataNotAvailable(R.string.net_error_tip);
                }
            }
        });
    }

    /**
     * 判断有没有加入该直播
     *
     * @param schoolId 学校ID
     * @param classId  班级Id
     * @param liveId   直播Id
     * @param callback 回调对象
     */
    public static void requestJudgeJoinLive(@NonNull String schoolId,
                                            @NonNull String classId,
                                            @NonNull String liveId,
                                            @NonNull DataSource.Callback<Boolean> callback) {
        // 准备数据
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("SchoolId", schoolId);
        jsonObject.put("ClassId", classId);
        jsonObject.put("MemberId", UserHelper.getUserId());
        jsonObject.put("ExtId", liveId);

        RequestParams params = new RequestParams(AppConfig.ServerUrl.PostJudgeJoinLive);
        params.setAsJsonContent(true);
        params.setBodyContent(jsonObject.toJSONString());
        params.setConnectTimeout(10000);
        LogUtil.i(OnlineCourseHelper.class, "send request ==== " + params.getUri());
        x.http().post(params, new StringCallback<String>() {

            @Override
            public void onSuccess(String str) {

                LogUtil.i(OnlineCourseHelper.class, "request " + params.getUri() + " result :" + str);
                TypeReference<BaseModelDataEntity<Boolean>> typeReference = new TypeReference<BaseModelDataEntity<Boolean>>() {
                };
                BaseModelDataEntity<Boolean> entity = JSON.parseObject(str, typeReference);
                if (!entity.isHasError()) {
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
                LogUtil.w(OnlineCourseHelper.class, "request " + params.getUri() + " failed");
                if (null != callback) {
                    callback.onDataNotAvailable(R.string.net_error_tip);
                }
            }
        });
    }

    /**
     * 获取机构信息片段数据
     *
     * @param classId  班级ID
     * @param callback 数据回调对象
     */
    public static void requestOnlineCourseWithClassId(@NonNull String classId,
                                                      @NonNull DataSource.Callback<String> callback) {
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("classId", classId);

        RequestParams params = new RequestParams(AppConfig.ServerUrl.GetClassRelevanceCourse + requestVo.getParams());
        params.setConnectTimeout(10000);
        LogUtil.i(OnlineCourseHelper.class, "send request ==== " + params.getUri());
        x.http().get(params, new StringCallback<String>() {

            @Override
            public void onSuccess(String str) {
                LogUtil.i(OnlineCourseHelper.class, "request " + params.getUri() + " result :" + str);
                TypeReference<OnlineRelevanceCourseEntity> typeReference = new TypeReference<OnlineRelevanceCourseEntity>() {
                };
                OnlineRelevanceCourseEntity entity = JSON.parseObject(str, typeReference);
                if (entity.isSucceed()) {
                    if (!EmptyUtil.isEmpty(callback)) {
                        if (entity.isRightRequest()) {
                            callback.onDataLoaded(entity.getCourseId());
                        } else {
                            if (!EmptyUtil.isEmpty(callback)) {
                                // 和后台约定 提示写死
                                Factory.decodeRspCode(-1, callback);
                            }
                        }
                    }
                } else {
                    if (!EmptyUtil.isEmpty(callback)) {
                        Factory.decodeRspCode(entity.getCode(), callback);
                    }
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.w(OnlineCourseHelper.class, "request " + params.getUri() + " failed");
                if (null != callback) {
                    callback.onDataNotAvailable(R.string.net_error_tip);
                }
            }
        });


    }

    /**
     * 获取在线学习的标签列表
     *
     * @param callback 回调对象
     */
    public static void requestOnlineStudyLabelData(@NonNull DataSource.Callback<List<OnlineConfigEntity>> callback) {
        RequestVo requestVo = new RequestVo();

        RequestParams params = new RequestParams(AppConfig.ServerUrl.GetOnlineStudyLabelUrl + requestVo.getParams());
        params.setConnectTimeout(10000);
        LogUtil.i(OnlineCourseHelper.class, "send request ==== " + params.getUri());
        x.http().get(params, new StringCallback<String>() {

            @Override
            public void onSuccess(String str) {
                LogUtil.i(OnlineCourseHelper.class, "request " + params.getUri() + " result :" + str);
                TypeReference<ResponseVo<List<OnlineConfigEntity>>> typeReference = new TypeReference<ResponseVo<List<OnlineConfigEntity>>>() {
                };
                ResponseVo<List<OnlineConfigEntity>> vo = JSON.parseObject(str, typeReference);
                if (vo.isSucceed()) {
                    List<OnlineConfigEntity> data = vo.getData();
                    if (!EmptyUtil.isEmpty(callback)) {
                        callback.onDataLoaded(data);
                    }
                } else {
                    if (!EmptyUtil.isEmpty(callback)) {
                        Factory.decodeRspCode(vo.getCode(), callback);
                    }
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.w(OnlineCourseHelper.class, "request " + params.getUri() + " failed");
                if (null != callback) {
                    callback.onDataNotAvailable(R.string.net_error_tip);
                }
            }
        });
    }

    /**
     * 获取新的在线学习的标签列表
     * @param language 0 中文 1 英文
     * @param callback 回调对象
     */
    public static void requestNewOnlineClassifyConfigData(int dataType,
                                                          @LanguageType.LanguageRes int language,
                                                          @NonNull DataSource.Callback<List<NewOnlineConfigEntity>> callback) {
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("language",language);
        requestVo.addParams("dataType",dataType);
        RequestParams params = new RequestParams(AppConfig.ServerUrl.GetNewOnlineClassifyConfigDataUrl + requestVo.getParams());
        params.setConnectTimeout(10000);
        LogUtil.i(OnlineCourseHelper.class, "send request ==== " + params.getUri());
        x.http().get(params, new StringCallback<String>() {

            @Override
            public void onSuccess(String str) {
                LogUtil.i(OnlineCourseHelper.class, "request " + params.getUri() + " result :" + str);
                TypeReference<ResponseVo<List<NewOnlineConfigEntity>>> typeReference = new TypeReference<ResponseVo<List<NewOnlineConfigEntity>>>() {
                };
                ResponseVo<List<NewOnlineConfigEntity>> vo = JSON.parseObject(str, typeReference);
                if (vo.isSucceed()) {
                    List<NewOnlineConfigEntity> data = vo.getData();
                    if (!EmptyUtil.isEmpty(callback)) {
                        callback.onDataLoaded(data);
                    }
                } else {
                    if (!EmptyUtil.isEmpty(callback)) {
                        Factory.decodeRspCode(vo.getCode(), callback);
                    }
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.w(OnlineCourseHelper.class, "request " + params.getUri() + " failed");
                if (null != callback) {
                    callback.onDataNotAvailable(R.string.net_error_tip);
                }
            }
        });
    }

    /**
     * 获取新的在线学习的标签列表
     * @param language 0 中文 1 英文
     * @param callback 回调对象
     */
    public static void requestNewOnlineStudyLabelData(@LanguageType.LanguageRes int language,
                                                      @NonNull DataSource.Callback<List<NewOnlineConfigEntity>> callback) {
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("language",language);
        RequestParams params = new RequestParams(AppConfig.ServerUrl.GetNewOnlineStudyLabelUrl + requestVo.getParams());
        params.setConnectTimeout(10000);
        LogUtil.i(OnlineCourseHelper.class, "send request ==== " + params.getUri());
        x.http().get(params, new StringCallback<String>() {

            @Override
            public void onSuccess(String str) {
                LogUtil.i(OnlineCourseHelper.class, "request " + params.getUri() + " result :" + str);
                TypeReference<ResponseVo<List<NewOnlineConfigEntity>>> typeReference = new TypeReference<ResponseVo<List<NewOnlineConfigEntity>>>() {
                };
                ResponseVo<List<NewOnlineConfigEntity>> vo = JSON.parseObject(str, typeReference);
                if (vo.isSucceed()) {
                    List<NewOnlineConfigEntity> data = vo.getData();
                    if (!EmptyUtil.isEmpty(callback)) {
                        callback.onDataLoaded(data);
                    }
                } else {
                    if (!EmptyUtil.isEmpty(callback)) {
                        Factory.decodeRspCode(vo.getCode(), callback);
                    }
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.w(OnlineCourseHelper.class, "request " + params.getUri() + " failed");
                if (null != callback) {
                    callback.onDataNotAvailable(R.string.net_error_tip);
                }
            }
        });
    }

    /**
     * 获取在线学习的机构等相关数据 热门，最新
     *
     * @param callback 回调对象
     */
    public static void requestOnlineStudyOrganData(@NonNull DataSource.Callback<OnlineStudyEntity> callback) {
        RequestVo requestVo = new RequestVo();


        // 1或者不传,过滤测试数据,0测试版本不过滤
        requestVo.addParams("isAppStore",Common.Constance.isAppStore);

        RequestParams params = new RequestParams(AppConfig.ServerUrl.GetOnlineStudyOrganUrl + requestVo.getParams());
        params.setConnectTimeout(10000);
        LogUtil.i(OnlineCourseHelper.class, "send request ==== " + params.getUri());
        x.http().get(params, new StringCallback<String>() {

            @Override
            public void onSuccess(String str) {
                LogUtil.i(OnlineCourseHelper.class, "request " + params.getUri() + " result :" + str);
                TypeReference<OnlineStudyEntity> typeReference = new TypeReference<OnlineStudyEntity>() {
                };
                OnlineStudyEntity entity = JSON.parseObject(str, typeReference);
                if (entity.isSucceed()) {
                    if (!EmptyUtil.isEmpty(callback)) {
                        callback.onDataLoaded(entity);
                    }
                } else {
                    if (!EmptyUtil.isEmpty(callback)) {
                        Factory.decodeRspCode(entity.getCode(), callback);
                    }
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.w(OnlineCourseHelper.class, "request " + params.getUri() + " failed");
                if (null != callback) {
                    callback.onDataNotAvailable(R.string.net_error_tip);
                }
            }
        });
    }

    /**
     * 在线班级完成授课
     * @param id 在线班级的Id
     * @param status 2 完成授课 3 设成历史班
     * @param callback 回调对象
     */
    public static void requestCompleteGiveOrHistory(int id,
                                                    int status,
                                                    @NonNull DataSource.Callback<Boolean> callback) {
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("id", id);
        requestVo.addParams("status", status);
        RequestParams params = new RequestParams(AppConfig.ServerUrl.GetCompleteOnlineClassGiveUrl + requestVo.getParams());
        params.setConnectTimeout(10000);
        LogUtil.i(OnlineCourseHelper.class, "send request ==== " + params.getUri());
        x.http().get(params, new StringCallback<String>() {

            @Override
            public void onSuccess(String str) {
                LogUtil.i(OnlineCourseHelper.class, "request " + params.getUri() + " result :" + str);
                TypeReference<ResponseVo> typeReference = new TypeReference<ResponseVo>() {
                };
                ResponseVo vo = JSON.parseObject(str, typeReference);
                if (!EmptyUtil.isEmpty(callback)) {
                    callback.onDataLoaded(vo.isSucceed());
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.w(OnlineCourseHelper.class, "request " + params.getUri() + " failed");
                if (null != callback) {
                    callback.onDataNotAvailable(R.string.net_error_tip);
                }
            }
        });
    }

    /**
     * 获取老师学生正在授课和即将授课班级
     *
     * @param token    家长传孩子的memberId
     * @param callback 回调对象
     */
    public static void requestOnlineClassIdsWithRole(@NonNull String token,
                                                     @NonNull int role,
                                                     @NonNull DataSource.Callback<List<String>> callback) {
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("token", token);
        requestVo.addParams("role", role);
        RequestParams params = new RequestParams(AppConfig.ServerUrl.GetOnlineClassIds + requestVo.getParams());
        params.setConnectTimeout(10000);
        LogUtil.i(OnlineCourseHelper.class, "send request ==== " + params.getUri());
        x.http().get(params, new StringCallback<String>() {

            @Override
            public void onSuccess(String str) {
                LogUtil.i(OnlineCourseHelper.class, "request " + params.getUri() + " result :" + str);
                TypeReference<ResponseVo<List<String>>> typeReference = new TypeReference<ResponseVo<List<String>>>() {
                };
                ResponseVo<List<String>> vo = JSON.parseObject(str, typeReference);
                if (vo.isSucceed()) {
                    if (!EmptyUtil.isEmpty(callback)) {
                        callback.onDataLoaded(vo.getData());
                    }
                } else {
                    if (!EmptyUtil.isEmpty(callback)) {
                        Factory.decodeRspCode(vo.getCode(), callback);
                    }
                }

            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.w(OnlineCourseHelper.class, "request " + params.getUri() + " failed");
                if (null != callback) {
                    callback.onDataNotAvailable(R.string.net_error_tip);
                }
            }
        });
    }


    /**
     * 根据ClassId获取在线课堂的Id
     * @param classId 班级Id
     * @param callback 回调对象
     */
    public static void requestOnlineIdByClassId(@NonNull String classId,
                                                @NonNull DataSource.Callback<Integer> callback) {
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("classId", classId);
        RequestParams params = new RequestParams(AppConfig.ServerUrl.GetOnlineIdByCourseId + requestVo.getParams());
        params.setConnectTimeout(10000);
        LogUtil.i(OnlineCourseHelper.class, "send request ==== " + params.getUri());
        x.http().get(params, new StringCallback<String>() {

            @Override
            public void onSuccess(String str) {
                LogUtil.i(OnlineCourseHelper.class, "request " + params.getUri() + " result :" + str);
                TypeReference<Map<String,Object>> typeReference = new TypeReference<Map<String,Object>>() {};
                Map<String,Object> vo = JSON.parseObject(str, typeReference);
                int code = (int) vo.get("code");
                if (code == 0) {
                    if (!EmptyUtil.isEmpty(callback)) {
                        int onlineId = (int) vo.get("onlineId");
                        callback.onDataLoaded(onlineId);
                    }
                } else {
                    if (!EmptyUtil.isEmpty(callback)) {
                        Factory.decodeRspCode(code, callback);
                    }
                }

            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.w(OnlineCourseHelper.class, "request " + params.getUri() + " failed");
                if (null != callback) {
                    callback.onDataNotAvailable(R.string.net_error_tip);
                }
            }
        });
    }


    /**
     * 查看一个课程的班级内学生课程进度
     * @param classId 班级Id
     * @param type 0 查询所有 1 全部完成 2 完成80-99% 3 完成 60-79% 4完成60%以下
     * @param callback 回调对象
     */
    public static void requestLearningStatisticsData(@NonNull String classId,
                                                     @NonNull String courseId,
                                                     int type,
                                                     @NonNull DataSource.Callback<List<LearningProgressEntity>> callback) {
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("classId", classId);
        requestVo.addParams("courseId", courseId);
        requestVo.addParams("type", type);
        RequestParams params = new RequestParams(AppConfig.ServerUrl.GetLearningStatisticsUserArrayUrl + requestVo.getParams());
        params.setConnectTimeout(10000);
        LogUtil.i(OnlineCourseHelper.class, "send request ==== " + params.getUri());
        x.http().get(params, new StringCallback<String>() {

            @Override
            public void onSuccess(String str) {
                LogUtil.i(OnlineCourseHelper.class, "request " + params.getUri() + " result :" + str);
                TypeReference<ResponseVo<List<LearningProgressEntity>>> typeReference = new TypeReference<ResponseVo<List<LearningProgressEntity>>>() {};
                ResponseVo<List<LearningProgressEntity>> vo = JSON.parseObject(str, typeReference);
                if (vo.isSucceed()) {
                    if (!EmptyUtil.isEmpty(callback)) {
                        callback.onDataLoaded(vo.getData());
                    }
                } else {
                    if (!EmptyUtil.isEmpty(callback)) {
                        Factory.decodeRspCode(vo.getCode(), callback);
                    }
                }

            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.w(OnlineCourseHelper.class, "request " + params.getUri() + " failed");
                if (null != callback) {
                    callback.onDataNotAvailable(R.string.net_error_tip);
                }
            }
        });
    }

    /**
     * 查看一个课程的班级内学生课程进度
     * @param classId 班级Id
     * @param callback 回调对象
     */
    public static void requestCourseStatisticsData(@NonNull String classId,
                                                   @NonNull String courseId,
                                                   @NonNull DataSource.Callback<List<CourseStatisticsEntity>> callback) {
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("classId", classId);
        requestVo.addParams("courseId", courseId);
        RequestParams params = new RequestParams(AppConfig.ServerUrl.GetCourseStatisticsUserArrayUrl + requestVo.getParams());
        params.setConnectTimeout(10000);
        LogUtil.i(OnlineCourseHelper.class, "send request ==== " + params.getUri());
        x.http().get(params, new StringCallback<String>() {

            @Override
            public void onSuccess(String str) {
                LogUtil.i(OnlineCourseHelper.class, "request " + params.getUri() + " result :" + str);
                TypeReference<ResponseVo<List<CourseStatisticsEntity>>> typeReference = new TypeReference<ResponseVo<List<CourseStatisticsEntity>>>() {};
                ResponseVo<List<CourseStatisticsEntity>> vo = JSON.parseObject(str, typeReference);
                if (vo.isSucceed()) {
                    if (!EmptyUtil.isEmpty(callback)) {
                        callback.onDataLoaded(vo.getData());
                    }
                } else {
                    if (!EmptyUtil.isEmpty(callback)) {
                        Factory.decodeRspCode(vo.getCode(), callback);
                    }
                }

            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.w(OnlineCourseHelper.class, "request " + params.getUri() + " failed");
                if (null != callback) {
                    callback.onDataNotAvailable(R.string.net_error_tip);
                }
            }
        });
    }
}
