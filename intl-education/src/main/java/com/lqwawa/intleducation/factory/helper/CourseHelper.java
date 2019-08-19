package com.lqwawa.intleducation.factory.helper;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.Factory;
import com.lqwawa.intleducation.MainApplication;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.utils.ToastUtil;
import com.lqwawa.intleducation.base.vo.RequestVo;
import com.lqwawa.intleducation.base.vo.ResponseVo;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.LogUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.StringCallback;
import com.lqwawa.intleducation.factory.data.entity.CourseRateEntity;
import com.lqwawa.intleducation.factory.data.entity.LQCourseBindClassEntity;
import com.lqwawa.intleducation.factory.data.entity.course.CourseRouteEntity;
import com.lqwawa.intleducation.factory.data.entity.course.NotPurchasedChapterEntity;
import com.lqwawa.intleducation.factory.data.entity.course.TutorialGroupEntity;
import com.lqwawa.intleducation.factory.data.entity.response.CourseTutorResponseVo;
import com.lqwawa.intleducation.factory.data.entity.tutorial.TutorChoiceEntity;
import com.lqwawa.intleducation.factory.data.entity.tutorial.TutorEntity;
import com.lqwawa.intleducation.factory.event.EventConstant;
import com.lqwawa.intleducation.factory.event.EventWrapper;
import com.lqwawa.intleducation.module.discovery.vo.CourseDetailsVo;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;
import com.lqwawa.intleducation.module.learn.vo.MyCourseVo;
import com.lqwawa.intleducation.module.learn.vo.NoticeVo;

import org.greenrobot.eventbus.EventBus;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 课程相关的网络数据请求
 * @date 2018/04/09 11:11
 * @history v1.0
 * **********************************
 */
public class CourseHelper {

    /**
     * 获取学习进度,按照"节"统计
     *
     * @param token    if token 不为null,说明是家长身份,那就传token
     * @param courseId 课程Id
     * @param callback 请求回调对象
     */
    public static void getCourseLearningProcess(@Nullable String token,
                                                @NonNull String courseId,
                                                @NonNull final DataSource.Callback<CourseRateEntity> callback) {

        RequestVo requestVo = new RequestVo();
        if (!TextUtils.isEmpty(token)) {
            // 是家长身份,那就传学生 memberId
            requestVo.addParams("token", token);
        }
        requestVo.addParams("courseId", courseId);
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.getCourseLearningProgress + requestVo.getParams());
        params.setConnectTimeout(10000);
        x.http().get(params, new StringCallback<String>() {
            @Override
            public void onSuccess(String str) {
                CourseRateEntity result = JSON.parseObject(str, CourseRateEntity.class);
                if (result.isSucceed()) {
                    if (callback != null) {
                        callback.onDataLoaded(result);
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
     * 获取课程公告数据
     *
     * @param token    if token 不为null,说明是家长身份,那就传token
     * @param courseId 课程Id
     * @param callback 请求回调对象
     */
    public static void getCourseNoticeData(@NonNull String token,
                                           @NonNull String courseId,
                                           @NonNull final DataSource.Callback<List<NoticeVo>> callback) {
        RequestVo requestVo = new RequestVo();
        if (!TextUtils.isEmpty(token)) {
            // 是家长身份,那就传学生 memberId
            requestVo.addParams("token", token);
        }
        requestVo.addParams("courseId", courseId);
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.GetCourseNoticesList + requestVo.getParams());
        params.setConnectTimeout(10000);
        x.http().get(params, new StringCallback<String>() {
            @Override
            public void onSuccess(String str) {
                TypeReference<ResponseVo<List<NoticeVo>>> typeReference =
                        new TypeReference<ResponseVo<List<NoticeVo>>>() {
                        };
                ResponseVo<List<NoticeVo>> result = JSON.parseObject(str, typeReference);
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
     * 根据课程Id,获取课程信息
     *
     * @param token     如果不为空,那么token 就是memberId 家长身份
     * @param id        课程Id
     * @param dataType  1
     * @param schoolIds
     */
    public static void getCourseDetailsById(String token, @NonNull String id,
                                            int dataType, @NonNull String schoolIds,
                                            final DataSource.Callback<CourseDetailsVo> callback) {
        RequestVo requestVo = new RequestVo();
        if (!TextUtils.isEmpty(token)) {
            // 是家长身份,那就传学生 memberId
            requestVo.addParams("token", token);
        }
        requestVo.addParams("dataType", dataType);
        requestVo.addParams("id", id);
        requestVo.addParams("schoolIds", schoolIds);
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.GetCourseDetailsById + requestVo.getParams());
        params.setConnectTimeout(10000);
        x.http().get(params, new StringCallback<String>() {

            @Override
            public void onSuccess(String str) {
                CourseDetailsVo courseDetailsVo = JSON.parseObject(str,
                        new TypeReference<CourseDetailsVo>() {
                        });
                if (courseDetailsVo.getCode() == 0) {
                    if (!EmptyUtil.isEmpty(callback)) {
                        callback.onDataLoaded(courseDetailsVo);
                    }
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
     * 获取课程详情数据
     *
     * @param token     if token == null 那就是自己身份,否则就是家长身份
     * @param dataType  数据类型
     * @param courseId  课程Id
     * @param schoolIds 学校Id
     * @param callback  回调对象
     */
    public static void getCourseDetailsData(@Nullable String token,
                                            int dataType,
                                            @NonNull final String courseId,
                                            @NonNull String schoolIds,
                                            @NonNull final DataSource.Callback<CourseVo> callback) {

        RequestVo requestVo = new RequestVo();
        if (!TextUtils.isEmpty(token)) {
            // 是家长身份,那就传学生 memberId
            requestVo.addParams("token", token);
        }
        requestVo.addParams("dataType", dataType);
        requestVo.addParams("id", courseId);
        if (EmptyUtil.isNotEmpty(schoolIds)) {
            requestVo.addParams("schoolIds", schoolIds);
        }
        RequestParams params = new RequestParams(AppConfig.ServerUrl.GetCourseDetailsById + requestVo.getParams());
        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String str) {
                CourseDetailsVo vo = JSON.parseObject(str, new TypeReference<CourseDetailsVo>() {
                });
                if (vo.getCode() == 0) {
                    List<CourseVo> voList = vo.getCourse();
                    if (voList != null && voList.size() > 0) {
                        CourseVo courseVo = voList.get(0);
                        if (!EmptyUtil.isEmpty(callback)) {
                            callback.onDataLoaded(courseVo);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.i(CourseHelper.class, "获取课程详情失败:type" + ",msg:" + throwable.getMessage());
            }

            @Override
            public void onFinished() {
            }
        });

    }


    /**
     * 获取课程的购买信息
     */
    public static void getPayCourseDetail(@NonNull String memberId,
                                          @Nullable String courseId,
                                          @Nullable String id,
                                          @NonNull final DataSource.Callback<NotPurchasedChapterEntity> callback) {

        RequestVo requestVo = new RequestVo();
        if (EmptyUtil.isEmpty(courseId) && EmptyUtil.isEmpty(id)) {
            return;
        }

        // String memberId = UserHelper.getUserId();
        requestVo.addParams("memberId", memberId);

        if (EmptyUtil.isNotEmpty(courseId)) {
            requestVo.addParams("courseId", courseId);
        }

        if (EmptyUtil.isNotEmpty(id)) {
            requestVo.addParams("id", id);
        }
        RequestParams params = new RequestParams(AppConfig.ServerUrl.GetNotPurchasedChapter + requestVo.getParams());
        params.setConnectTimeout(10000);
        x.http().get(params, new StringCallback<String>() {

            @Override
            public void onSuccess(String str) {
                NotPurchasedChapterEntity vo = JSON.parseObject(str, new TypeReference<NotPurchasedChapterEntity>() {
                });
                if (vo.isSucceed()) {
                    List<CourseVo> voList = vo.getCourse();
                    if (voList != null && voList.size() > 0) {
                        CourseVo courseVo = voList.get(0);
                        if (!EmptyUtil.isEmpty(callback) && !EmptyUtil.isEmpty(courseVo)) {
                            callback.onDataLoaded(vo);
                        }
                    }
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
     * LQ学程是否已经绑定班级
     *
     * @param token    都只是传孩子的memberId,不许家长和老师访问
     * @param courseId 课程Id
     * @param callback 请求回调对象
     */
    public static void isBindClass(@Nullable String token,
                                   @NonNull String courseId,
                                   @NonNull final DataSource.Callback<LQCourseBindClassEntity> callback) {

        RequestVo requestVo = new RequestVo();
        if (!TextUtils.isEmpty(token)) {
            // 是家长身份,那就传学生 memberId
            requestVo.addParams("token", token);
        }
        requestVo.addParams("courseId", courseId);
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.GetCourseIsBindClass + requestVo.getParams());
        params.setConnectTimeout(10000);
        x.http().get(params, new StringCallback<String>() {
            @Override
            public void onSuccess(String str) {
                LQCourseBindClassEntity entity = JSON.parseObject(str, LQCourseBindClassEntity.class);
                if (entity.isSucceed()) {
                    if (callback != null) {
                        callback.onDataLoaded(entity);
                    }
                } else {
                    Factory.decodeRspCode(entity.getCode(), callback);
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
     * 获取课程状态
     *
     * @param memberId 如果是家长，token不为空,家长传孩子的memberId
     * @param courseId 课程Id
     * @param callback 请求回调对象
     */
    public static void requestCourseStatus(@Nullable String memberId,
                                           @NonNull String courseId,
                                           @NonNull final DataSource.Callback<CourseRouteEntity> callback) {

        RequestVo requestVo = new RequestVo();
        requestVo.addParams("courseId", courseId);
        if (EmptyUtil.isNotEmpty(memberId)) {
            // 是家长身份,那就传学生 memberId
            requestVo.addParams("token", memberId);
        }
        requestVo.addParams("version", 1);

        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.GetCourseStatus + requestVo.getParams());
        params.setConnectTimeout(10000);
        x.http().get(params, new StringCallback<String>() {
            @Override
            public void onSuccess(String str) {
                TypeReference<ResponseVo<CourseRouteEntity>> typeReference = new TypeReference<ResponseVo<CourseRouteEntity>>() {
                };
                ResponseVo<CourseRouteEntity> responseVo = JSON.parseObject(str, typeReference);
                if (responseVo.isSucceed()) {
                    if (callback != null) {
                        callback.onDataLoaded(responseVo.getData());
                    }
                } else {
                    Factory.decodeRspCode(responseVo.getCode(), callback);
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
     * 更新全本
     *
     * @param memberId 如果是家长，token不为空,家长传孩子的memberId
     * @param courseId 课程Id
     * @param schoolId 机构Id
     * @param classId  班级Id
     * @param callback 请求回调对象
     */
    public static void requestJoinInCourse(@Nullable String memberId,
                                           @NonNull String courseId,
                                           @NonNull String schoolId,
                                           @NonNull String classId,
                                           @NonNull final DataSource.SucceedCallback<Boolean> callback) {

        RequestVo requestVo = new RequestVo();
        requestVo.addParams("courseId", courseId);
        requestVo.addParams("schoolId", schoolId);
        requestVo.addParams("classId", classId);
        if (EmptyUtil.isNotEmpty(memberId)) {
            // 是家长身份,那就传学生 memberId
            requestVo.addParams("token", memberId);
        }
        requestVo.addParams("version", 1);

        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.GetJoinInCourse + requestVo.getParams());
        params.setConnectTimeout(10000);
        x.http().get(params, new StringCallback<String>() {
            @Override
            public void onSuccess(String str) {
                TypeReference<ResponseVo<Boolean>> typeReference = new TypeReference<ResponseVo<Boolean>>() {
                };
                ResponseVo<Boolean> responseVo = JSON.parseObject(str, typeReference);
                if (EmptyUtil.isNotEmpty(callback)) {
                    callback.onDataLoaded(responseVo.isSucceed());
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {

            }
        });
    }

    /**
     * 加入课程
     *
     * @param courseId     课程Id
     * @param type         类型 精品学程传3
     * @param isBackground 是否静默加入
     * @param schoolId     机构id
     * @param classId      班级id
     * @param callback     请求回调对象
     */
    public static void requestJoinInCourse(@NonNull String courseId,
                                           int type,
                                           final boolean isBackground,
                                           @Nullable String schoolId,
                                           @NonNull String classId,
                                           @NonNull final DataSource.SucceedCallback<Boolean> callback) {

        RequestVo requestVo = new RequestVo();
        requestVo.addParams("courseId", courseId);
        requestVo.addParams("type", type);
        requestVo.addParams("shcoolId", schoolId);
        requestVo.addParams("classId", classId);

        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.joinInCourse + requestVo.getParams());
        params.setConnectTimeout(10000);
        x.http().get(params, new StringCallback<String>() {
            @Override
            public void onSuccess(String str) {
                TypeReference<ResponseVo<Boolean>> typeReference = new TypeReference<ResponseVo<Boolean>>() {
                };
                ResponseVo<Boolean> responseVo = JSON.parseObject(str, typeReference);

                if (!isBackground) {
                    if (responseVo.isSucceed()) {
                        UIUtil.showToastSafe(R.string.join_success);
                    } else {
                        UIUtil.showToastSafe(R.string.join_failed);
                    }
                }

                if (EmptyUtil.isNotEmpty(callback)) {
                    callback.onDataLoaded(responseVo.isSucceed());
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {

            }
        });
    }


    /**
     * 检查用户是否可以购买某个课堂
     *
     * @param courseId 课程Id
     * @param memberId 用户Id
     * @param type     0 课程 3 空中课堂
     * @param callback 请求回调对象
     */
    public static void requestCheckCourseBuy(@NonNull int courseId,
                                             @NonNull String memberId,
                                             int type,
                                             @NonNull final DataSource.Callback<Boolean> callback) {

        RequestVo requestVo = new RequestVo();
        requestVo.addParams("courseId", courseId);
        requestVo.addParams("memberId", memberId);
        requestVo.addParams("type", type);

        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.GetCheckCourseBuyUrl + requestVo.getParams());
        params.setConnectTimeout(10000);
        x.http().get(params, new StringCallback<String>() {
            @Override
            public void onSuccess(String str) {
                TypeReference<ResponseVo<Boolean>> typeReference = new TypeReference<ResponseVo<Boolean>>() {
                };
                ResponseVo<Boolean> responseVo = JSON.parseObject(str, typeReference);
                if (EmptyUtil.isNotEmpty(callback)) {
                    if (responseVo.isSucceed()) {
                        callback.onDataLoaded(true);
                    } else {
                        UIUtil.showToastSafe(responseVo.getMessage());
                    }
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
     * 加入课程
     *
     * @param courseId     课程Id
     * @param type         类型 精品学程传3
     * @param isBackground 是否静默加入
     * @param callback     请求回调对象
     */
    public static void requestJoinInCourse(@NonNull String courseId,
                                           int type,
                                           final boolean isBackground,
                                           @NonNull final DataSource.SucceedCallback<Boolean> callback) {
        requestJoinInCourse(courseId, type, isBackground, null, null, callback);
    }

    /**
     * 加入课程
     *
     * @param courseId     课程Id
     * @param isBackground 是否静默加入
     * @param callback     请求回调对象
     */
    public static void requestReJoinInCourse(@NonNull String courseId,
                                             final boolean isBackground,
                                             @NonNull final DataSource.SucceedCallback<Boolean> callback) {

        RequestVo requestVo = new RequestVo();
        requestVo.addParams("courseId", courseId);

        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.reJoinInCourse + requestVo.getParams());
        params.setConnectTimeout(10000);
        x.http().get(params, new StringCallback<String>() {
            @Override
            public void onSuccess(String str) {
                TypeReference<ResponseVo<Boolean>> typeReference = new TypeReference<ResponseVo<Boolean>>() {
                };
                ResponseVo<Boolean> responseVo = JSON.parseObject(str, typeReference);

                if (!isBackground) {
                    if (responseVo.isSucceed()) {
                        UIUtil.showToastSafe(R.string.join_success);
                    } else {
                        UIUtil.showToastSafe(R.string.join_failed);
                    }
                }

                if (EmptyUtil.isNotEmpty(callback)) {
                    callback.onDataLoaded(responseVo.isSucceed());
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {

            }
        });
    }

    /**
     * 获取课程助教列表
     *
     * @param courseId 课程Id
     * @param memberId 用户Id
     * @param callback 请求回调对象
     * @type 1 课程 2 课堂
     */
    public static void requestTutorDataByCourseId(@NonNull String courseId,
                                                  @NonNull String memberId,
                                                  int type,
                                                  int pageIndex, int pageSize,
                                                  @NonNull final DataSource.Callback<List<TutorialGroupEntity>> callback) {

        RequestVo requestVo = new RequestVo();
        requestVo.addParams("courseId", courseId);
        if (EmptyUtil.isNotEmpty(memberId)) {
            requestVo.addParams("memberId", memberId);
        }
        requestVo.addParams("type", type);
        requestVo.addParams("pageIndex", pageIndex);
        requestVo.addParams("pageSize", pageSize);
        RequestParams params = new RequestParams(AppConfig.ServerUrl.GetTutorListByCourseId + requestVo.getParams());
        params.setConnectTimeout(10000);
        LogUtil.i(CourseHelper.class, "send request ==== " + params.getUri());
        x.http().get(params, new StringCallback<String>() {
            @Override
            public void onSuccess(String str) {
                LogUtil.i(CourseHelper.class, "request " + params.getUri() + " result :" + str);
                TypeReference<ResponseVo<List<TutorialGroupEntity>>> typeReference = new TypeReference<ResponseVo<List<TutorialGroupEntity>>>() {
                };
                ResponseVo<List<TutorialGroupEntity>> responseVo = JSON.parseObject(str, typeReference);
                if (responseVo.isSucceed()) {
                    if (EmptyUtil.isNotEmpty(callback)) {
                        callback.onDataLoaded(responseVo.getData());
                    }
                } else {
                    if (EmptyUtil.isNotEmpty(callback)) {
                        Factory.decodeRspCode(responseVo.getCode(), callback);
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


    /**
     * 查看课程的帮辅列表
     *
     * @param courseId  课程Id
     * @param chapterId 资源Id
     * @param memberId  用户Id
     * @param callback  请求回调对象
     */
    public static void requestTutorsByCourseId(@NonNull String memberId,
                                               @Nullable String courseId,
                                               @Nullable String chapterId,
                                               int pageIndex, int pageSize,
                                               @NonNull final DataSource.Callback<List<TutorChoiceEntity>> callback) {

        RequestVo requestVo = new RequestVo();
        if (EmptyUtil.isNotEmpty(courseId)) {
            requestVo.addParams("courseId", courseId);
        }

        if (EmptyUtil.isNotEmpty(chapterId)) {
            requestVo.addParams("chapterId", chapterId);
        }
        requestVo.addParams("memberId", memberId);
        requestVo.addParams("pageIndex", pageIndex);
        requestVo.addParams("pageSize", pageSize);
        RequestParams params = new RequestParams(AppConfig.ServerUrl.PostTutorsByCourseId);
        params.setAsJsonContent(true);
        params.setBodyContent(requestVo.getParamsWithoutToken());
        params.setConnectTimeout(10000);
        LogUtil.i(CourseHelper.class, "send request ==== " + params.getUri());
        x.http().post(params, new StringCallback<String>() {
            @Override
            public void onSuccess(String str) {
                LogUtil.i(CourseHelper.class, "request " + params.getUri() + " result :" + str);
                TypeReference<ResponseVo<List<TutorChoiceEntity>>> typeReference = new TypeReference<ResponseVo<List<TutorChoiceEntity>>>() {
                };
                ResponseVo<List<TutorChoiceEntity>> responseVo = JSON.parseObject(str, typeReference);
                if (responseVo.isSucceed()) {
                    if (EmptyUtil.isNotEmpty(callback)) {
                        callback.onDataLoaded(responseVo.getData());
                    }
                } else {
                    if (EmptyUtil.isNotEmpty(callback)) {
                        Factory.decodeRspCode(responseVo.getCode(), callback);
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

    /**
     * 查看该用户是否是某课程的帮辅老师
     *
     * @param courseId 课程Id
     * @param memberId 用户Id
     * @param callback 请求回调对象
     */
    public static void isTutorCourseBycourseId(@NonNull String memberId,
                                               @Nullable String courseId,
                                               @NonNull final DataSource.SucceedCallback<CourseTutorResponseVo.CourseTutorEntity> callback) {

        RequestVo requestVo = new RequestVo();
        requestVo.addParams("courseId", courseId);
        requestVo.addParams("memberId", memberId);
        RequestParams params = new RequestParams(AppConfig.ServerUrl.PostIsTutorCourseByCourseId);
        params.setAsJsonContent(true);
        params.setBodyContent(requestVo.getParamsWithoutToken());
        params.setConnectTimeout(10000);
        LogUtil.i(CourseHelper.class, "send request ==== " + params.getUri());
        x.http().post(params, new StringCallback<String>() {
            @Override
            public void onSuccess(String str) {
                LogUtil.i(CourseHelper.class, "request " + params.getUri() + " result :" + str);
                TypeReference<CourseTutorResponseVo> typeReference = new TypeReference<CourseTutorResponseVo>() {
                };
                CourseTutorResponseVo responseVo = JSON.parseObject(str, typeReference);
                if (responseVo.isSucceed()) {
                    if (EmptyUtil.isNotEmpty(callback)) {
                        CourseTutorResponseVo.CourseTutorEntity entity = new CourseTutorResponseVo.CourseTutorEntity();
                        entity.setTutorCourse(responseVo.isTutorCourse());
                        entity.setIsOrganTutorStatus(responseVo.getIsOrganTutorStatus());
                        callback.onDataLoaded(entity);
                    }
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                UIUtil.showToastSafe(R.string.net_error_tip);
            }
        });
    }

    /**
     * 申请课程帮辅老师
     *
     * @param memberId           帮辅Id
     * @param courseId           课程Id
     * @param type               1 课程 2 课堂
     * @param isOrganTutorStatus 是否已经是该机构的帮辅老师  -1 未申请过，0 正在申请当中 1已经是机构的帮辅老师
     * @param realName           真实姓名,
     * @param markingPrice       批阅价格
     * @param provinceId         省份Id
     * @param provinceName       省份名称
     * @param cityId             城市Id
     * @param cityName           城市名称
     * @param countyId           区Id
     * @param countyName         区名称
     * @param isLqOrganTutor     是否已经是机构帮辅
     */
    public static void requestApplyForCourseTutor(@NonNull String memberId,
                                                  @NonNull int courseId,
                                                  int type, int isOrganTutorStatus,
                                                  @NonNull String realName, @NonNull String markingPrice,
                                                  @NonNull String provinceId, @NonNull String provinceName,
                                                  @NonNull String cityId, @NonNull String cityName,
                                                  @NonNull String countyId, @NonNull String countyName,
                                                  boolean isLqOrganTutor,
                                                  @NonNull final DataSource.Callback<Boolean> callback) {

        RequestVo requestVo = new RequestVo();
        requestVo.addParams("memberId", memberId);
        requestVo.addParams("courseId", courseId);
        requestVo.addParams("type", type);
        requestVo.addParams("source", 0);

        // requestVo.addParams("isOrganTutorStatus", isOrganTutorStatus);
        try {
            String encodeRealName = URLEncoder.encode(realName, "utf-8");
            requestVo.addParams("realName", encodeRealName);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        // requestVo.addParams("isLqOrganTutor", isLqOrganTutor);
        /*if(MainApplication.getInstance() != null){
            requestVo.addParams("isLqOrganTutor", MainApplication.isIsAssistant());
        }*/
        /*requestVo.addParams("isLqOrganTutor", isLqOrganTutor);
        if(isOrganTutorStatus == -1){
            requestVo.addParams("markingPrice", markingPrice);
            requestVo.addParams("provinceId", provinceId);
            requestVo.addParams("provinceName", provinceName);
            if(EmptyUtil.isNotEmpty(cityId) && EmptyUtil.isNotEmpty(cityName)){
                requestVo.addParams("cityId",cityId);
                requestVo.addParams("cityName",cityName);
            }

            if(EmptyUtil.isNotEmpty(countyId) && EmptyUtil.isNotEmpty(countyName)){
                requestVo.addParams("countyId",countyId);
                requestVo.addParams("countyName",countyName);
            }
        }*/
        RequestParams params = new RequestParams(AppConfig.ServerUrl.GetApplyForCourseTutor + requestVo.getParams());
        params.setConnectTimeout(10000);
        LogUtil.i(CourseHelper.class, "send request ==== " + params.getUri());
        x.http().get(params, new StringCallback<String>() {
            @Override
            public void onSuccess(String str) {
                LogUtil.i(CourseHelper.class, "request " + params.getUri() + " result :" + str);
                TypeReference<CourseTutorResponseVo> typeReference = new TypeReference<CourseTutorResponseVo>() {
                };
                CourseTutorResponseVo responseVo = JSON.parseObject(str, typeReference);

                if (EmptyUtil.isNotEmpty(callback)) {
                    // 不管成功还是失败，都dismiss窗体
                    callback.onDataLoaded(responseVo.isTutor());
                }

                if (!responseVo.isSucceed() || !responseVo.isTutor()) {
                    if (EmptyUtil.isNotEmpty(responseVo.getMessage())) {
                        UIUtil.showToastSafe(responseVo.getMessage());
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

    public static void exitCourse(String courseId) {
        if (TextUtils.isEmpty(courseId)) {
            return;
        }

        RequestVo requestVo = new RequestVo();
        requestVo.addParams("courseId", courseId);
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.courseDelete + requestVo.getParams());
        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {

            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {

            }

            @Override
            public void onFinished() {
            }
        });
    }
}
