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
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.StringCallback;
import com.lqwawa.intleducation.factory.data.entity.course.VideoResourceEntity;
import com.lqwawa.intleducation.module.discovery.vo.CommentVo;

import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.List;

/**
 * @author: wangchao
 * @date: 2019/05/07
 * @desc:
 */
public class VideoDetailHelper {

    /**
     * 获取视频下配套教材
     *
     * @param chapterId 章节Id
     * @param callback
     */
    public static void getSRListByChapterId(long chapterId,
                                            @NonNull final DataSource.Callback<List<VideoResourceEntity>> callback) {
        RequestVo requestVo = new RequestVo();
        if (EmptyUtil.isNotEmpty(chapterId)) {
            requestVo.addParams("chapterId", chapterId);
        }
        RequestParams params = new RequestParams(AppConfig.ServerUrl.PostGetSRListByChapterId);
        params.setAsJsonContent(true);
        params.setBodyContent(requestVo.getParamsWithoutToken());
        params.setConnectTimeout(10000);
        LogUtil.i(CourseHelper.class, "send request ==== " + params.getUri());
        x.http().post(params, new StringCallback<String>() {
            @Override
            public void onSuccess(String str) {
                LogUtil.i(CourseHelper.class, "request " + params.getUri() + " result :" + str);
                TypeReference<ResponseVo<List<VideoResourceEntity>>> typeReference =
                        new TypeReference<ResponseVo<List<VideoResourceEntity>>>() {
                        };
                ResponseVo<List<VideoResourceEntity>> responseVo = JSON.parseObject(str, typeReference);
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
     * 获取视频下评论列表
     *
     * @param courseId  课程Id
     * @param pageIndex
     * @param pageSize
     * @param callback
     */
    public static void getVideoCommentList(long courseId, int pageIndex, int pageSize,
                                    @NonNull final DataSource.Callback<List<CommentVo>> callback) {
        RequestVo requestVo = new RequestVo();
        if (EmptyUtil.isNotEmpty(courseId)) {
            requestVo.addParams("courseId", courseId);
        }
        requestVo.addParams("pageIndex", pageIndex);
        requestVo.addParams("pageSize", pageSize);
        RequestParams params = new RequestParams(AppConfig.ServerUrl.PostGetVideoCommentList);
        params.setAsJsonContent(true);
        params.setBodyContent(requestVo.getParamsWithoutToken());
        params.setConnectTimeout(10000);
        LogUtil.i(CourseHelper.class, "send request ==== " + params.getUri());
        x.http().post(params, new StringCallback<String>() {
            @Override
            public void onSuccess(String str) {
                LogUtil.i(CourseHelper.class, "request " + params.getUri() + " result :" + str);
                TypeReference<ResponseVo<List<CommentVo>>> typeReference =
                        new TypeReference<ResponseVo<List<CommentVo>>>() {
                        };
                ResponseVo<List<CommentVo>> responseVo = JSON.parseObject(str, typeReference);
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
     * 发送视频评论
     * @param memberId
     * @param courseId 课程Id
     * @param content
     * @param callback
     */
    public static void addVideoComment(@NonNull String memberId, long courseId,
                                   @NonNull String content,
                                       @NonNull final DataSource.Callback<Boolean> callback) {
        RequestVo requestVo = new RequestVo();

        if (EmptyUtil.isNotEmpty(courseId)) {
            requestVo.addParams("courseId", courseId);
        }
        requestVo.addParams("memberId", memberId);
        requestVo.addParams("content", content);
        RequestParams params = new RequestParams(AppConfig.ServerUrl.PostAddVideoComment);
        params.setAsJsonContent(true);
        params.setBodyContent(requestVo.getParamsWithoutToken());
        params.setConnectTimeout(10000);
        LogUtil.i(CourseHelper.class, "send request ==== " + params.getUri());
        x.http().post(params, new StringCallback<String>() {
            @Override
            public void onSuccess(String str) {
                LogUtil.i(CourseHelper.class, "request " + params.getUri() + " result :" + str);
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
                if (EmptyUtil.isNotEmpty(callback)) {
                    callback.onDataNotAvailable(R.string.net_error_tip);
                }
            }
        });
    }


}
