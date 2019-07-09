package com.lqwawa.mooc.select;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.base.vo.RequestVo;
import com.lqwawa.intleducation.base.vo.ResponseVo;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.LogUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.StringCallback;
import com.lqwawa.intleducation.factory.data.entity.course.CourseResourceEntity;

import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.List;

/**
 * @author mrmedici
 * @desc 网络请求帮助类
 */
public class LQwawaHelper {

    /**
     * 绑定班级
     *
     * @param courseId 课程ID
     */
    public static void requestCourseBindClass(@NonNull String courseId,
                                              @NonNull String schoolId,
                                              @NonNull String classId,
                                              @NonNull final DataSource.Callback<ResponseVo> callback) {
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("courseId", courseId);
        requestVo.addParams("schoolId", schoolId);
        requestVo.addParams("classId", classId);
        final RequestParams params = new RequestParams(AppConfig.ServerUrl.GetAddCourseRelevanceClass + requestVo.getParams());
        params.setConnectTimeout(10000);

        LogUtil.i(LQwawaHelper.class, "send request ==== " + params.getUri());
        x.http().get(params, new StringCallback<String>() {
            @Override
            public void onSuccess(String str) {
                LogUtil.i(LQwawaHelper.class, "request " + params.getUri() + " result :" + str);
                ResponseVo vo = JSON.parseObject(str, new TypeReference<ResponseVo>() {
                });
                if (EmptyUtil.isNotEmpty(callback)) {
                    callback.onDataLoaded(vo);
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.w(LQwawaHelper.class, "request " + params.getUri() + " failed");
                if (!EmptyUtil.isEmpty(callback)) {
                    callback.onDataNotAvailable(com.lqwawa.intleducation.R.string.net_error_tip);
                }
            }
        });
    }

    /**
     * 获取节下面的录音课件列表
     *
     * @param chapterId 节ID
     * @param callback
     */
    public static void requestResourceListByChapterIds(@Nullable String chapterId,
                                                       @NonNull final DataSource.Callback<ResponseVo> callback) {

        RequestVo requestVo = new RequestVo();
        if (EmptyUtil.isNotEmpty(chapterId)) {
            requestVo.addParams("chapterIds", chapterId);
        }
        RequestParams params = new RequestParams(AppConfig.ServerUrl.PostGetResourceListByChapterIds);
        params.setAsJsonContent(true);
        params.setBodyContent(requestVo.getParams());
        params.setConnectTimeout(60000);
        LogUtil.i(LQwawaHelper.class, "send request ==== " + params.getUri());
        x.http().post(params, new StringCallback<String>() {
            @Override
            public void onSuccess(String str) {
                LogUtil.i(LQwawaHelper.class, "request " + params.getUri() + " result :" + str);
                try {
                    ResponseVo vo = JSON.parseObject(str, new TypeReference<ResponseVo<List<CourseResourceEntity>>>() {
                    });
                    if (vo.isSucceed()) {
                        if (EmptyUtil.isNotEmpty(callback)) {
                            callback.onDataLoaded(vo);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.w(LQwawaHelper.class, "request " + params.getUri() + " failed");
                if (!EmptyUtil.isEmpty(callback)) {
                    callback.onDataNotAvailable(com.lqwawa.intleducation.R.string.net_error_tip);
                }
            }
        });
    }

}
