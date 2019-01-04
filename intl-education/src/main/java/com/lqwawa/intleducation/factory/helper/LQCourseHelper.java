package com.lqwawa.intleducation.factory.helper;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.Factory;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.utils.ToastUtil;
import com.lqwawa.intleducation.base.vo.RequestVo;
import com.lqwawa.intleducation.base.vo.ResponseVo;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.LogUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.common.utils.Utils;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.StringCallback;
import com.lqwawa.intleducation.factory.data.entity.LQBasicsOuterEntity;
import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;
import com.lqwawa.intleducation.factory.data.entity.response.LQConfigResponseVo;
import com.lqwawa.intleducation.module.discovery.adapter.CourseChapterAdapter;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.coursedetails.CourseDetailItemParams;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.home.LanguageType;
import com.lqwawa.intleducation.module.discovery.vo.BannerInfoVo;
import com.lqwawa.intleducation.module.discovery.vo.ChapterVo;
import com.lqwawa.intleducation.module.discovery.vo.ClassifyVo;
import com.lqwawa.intleducation.module.discovery.vo.CourseDetailsVo;
import com.lqwawa.intleducation.module.discovery.vo.CourseIntroduceVo;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;
import com.lqwawa.intleducation.module.discovery.vo.DiscoveryItemVo;
import com.lqwawa.intleducation.module.discovery.vo.OrganVo;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function LQ学程相关网络请求帮助类
 * @date 2018/04/27 15:16
 * @history v1.0
 * **********************************
 */
public class LQCourseHelper {

    /**
     * 获取LQ学程轮播数据
     * @param callback 数据回调接口
     */
    public static void requestLQCourseBanners(@NonNull final DataSource.Callback<List<String>> callback){
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("dataType",0);
        RequestParams params = new RequestParams(AppConfig.ServerUrl.GetBanners + requestVo.getParams());
        LogUtil.i(LQCourseHelper.class,"send request ==== " +params.getUri());
        params.setConnectTimeout(10000);
        x.http().get(params, new StringCallback<String>() {

            @Override
            public void onSuccess(String str) {
                LogUtil.i(LQCourseHelper.class,"request "+params.getUri()+" result :"+str);
                ResponseVo<List<BannerInfoVo>> result = JSON.parseObject(str,new TypeReference<ResponseVo<List<BannerInfoVo>>>() {});
                if (result.isSucceed()) {
                    List<BannerInfoVo> bannerInfoList = result.getData();
                    if(!EmptyUtil.isEmpty(bannerInfoList)){
                        List<String> urlBanners = new ArrayList<>();
                        for (int i = 0; i < bannerInfoList.size(); i++) {
                            if (bannerInfoList.get(i).getThumbnail() != null) {
                                urlBanners.add(bannerInfoList.get(i).getThumbnail());
                            }
                        }

                        if(!EmptyUtil.isEmpty(urlBanners) && !EmptyUtil.isEmpty(callback)){
                            // 接口回调数据到Presenter
                            callback.onDataLoaded(urlBanners);
                        }
                    }
                }else{
                    Factory.decodeRspCode(result.getCode(),callback);
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.w(LQCourseHelper.class,"request "+params.getUri()+" failed");
                if(!EmptyUtil.isEmpty(callback)){
                    callback.onDataNotAvailable(R.string.net_error_tip);
                }
            }
        });
    }

    /**
     * 获取LQ学程分类数据
     * {@link LanguageType.LanguageRes}
     * @param isZh 是否中文显示 0 chinese 1 other
     * @param callback 回调接口
     */
    @Deprecated
    public static void requestLQCourseClassifyData(@LanguageType.LanguageRes int isZh, @NonNull final DataSource.Callback<List<ClassifyVo>> callback){
        RequestVo requestVo = new RequestVo();
        // 是否是中文字体,根据参数,后台返回相应语言
        requestVo.addParams("language",isZh);
        final RequestParams params = new RequestParams(AppConfig.ServerUrl.GetClassList+requestVo.getParams());
        params.setConnectTimeout(10000);
        LogUtil.i(LQCourseHelper.class,"send request ==== " +params.getUri());
        x.http().get(params, new StringCallback<String>() {
            @Override
            public void onSuccess(String str) {
                LogUtil.i(LQCourseHelper.class,"request "+params.getUri()+" result :"+str);
                ResponseVo<List<ClassifyVo>> result = JSON.parseObject(str,new TypeReference<ResponseVo<List<ClassifyVo>>>() {});
                if (result.isSucceed()) {
                    List<ClassifyVo> classVos = result.getData();
                    if (!EmptyUtil.isEmpty(callback)) {
                        callback.onDataLoaded(classVos);
                    }
                }else{
                    Factory.decodeRspCode(result.getCode(),callback);
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.w(LQCourseHelper.class,"request "+params.getUri()+" failed");
                if(!EmptyUtil.isEmpty(callback)){
                    callback.onDataNotAvailable(R.string.net_error_tip);
                }
            }
        });
    }

    /**
     * 获取LQ学程首页分类数据 V5.12使用
     * {@link LanguageType.LanguageRes}
     * @param isZh 是否中文显示 0 chinese 1 other
     * @param level 返回level级别课程
     * @param parentId 父级Id
     * @param callback 数据回调接口
     */
    public static void requestLQHomeConfigData(@LanguageType.LanguageRes int isZh,
                                               int level,int parentId,
                                               @NonNull final DataSource.Callback<LQConfigResponseVo<List<LQCourseConfigEntity>,List<LQBasicsOuterEntity>>> callback){
        final RequestVo requestVo = new RequestVo();
        // 是否是中文字体,根据参数,后台返回相应语言
        requestVo.addParams("language",isZh);
        requestVo.addParams("level",level);
        requestVo.addParams("parentId",parentId);
        requestVo.addParams("version",1);
        final RequestParams params = new RequestParams(AppConfig.ServerUrl.GetConfigList+requestVo.getParams());
        params.setConnectTimeout(10000);
        LogUtil.i(LQCourseHelper.class,"send request ==== " +params.getUri());
        x.http().get(params, new StringCallback<String>() {
            @Override
            public void onSuccess(String str) {
                LogUtil.i(LQCourseHelper.class,"request "+params.getUri()+" result :"+str);
                TypeReference<LQConfigResponseVo<List<LQCourseConfigEntity>,List<LQBasicsOuterEntity>>> typeReference =
                        new TypeReference<LQConfigResponseVo<List<LQCourseConfigEntity>,List<LQBasicsOuterEntity>>>(){};
                LQConfigResponseVo<List<LQCourseConfigEntity>,List<LQBasicsOuterEntity>> result = JSON.parseObject(str, typeReference);
                if(result.isSucceed()){
                    if(!EmptyUtil.isEmpty(callback)){
                        callback.onDataLoaded(result);
                    }
                }else{
                    Factory.decodeRspCode(result.getCode(),callback);
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.w(LQCourseHelper.class,"request "+params.getUri()+" failed");
                if(!EmptyUtil.isEmpty(callback)){
                    callback.onDataNotAvailable(R.string.net_error_tip);
                }
            }
        });
    }

    /**
     * 获取LQ学程分类数据
     * {@link LanguageType.LanguageRes}
     * @param isZh 是否中文显示 0 chinese 1 other
     * @param level 返回level级别课程
     * @param parentId 父级Id
     * @param callback 数据回调接口
     */
    public static void requestLQCourseConfigData(@LanguageType.LanguageRes int isZh,
                                                   int level,int parentId,
                                                   @NonNull final DataSource.Callback<List<LQCourseConfigEntity>> callback){
        final RequestVo requestVo = new RequestVo();
        // 是否是中文字体,根据参数,后台返回相应语言
        requestVo.addParams("language",isZh);
        requestVo.addParams("level",level);
        requestVo.addParams("parentId",parentId);
        requestVo.addParams("version",1);
        final RequestParams params = new RequestParams(AppConfig.ServerUrl.GetConfigList+requestVo.getParams());
        params.setConnectTimeout(10000);
        LogUtil.i(LQCourseHelper.class,"send request ==== " +params.getUri());
        x.http().get(params, new StringCallback<String>() {
            @Override
            public void onSuccess(String str) {
                LogUtil.i(LQCourseHelper.class,"request "+params.getUri()+" result :"+str);
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
                LogUtil.w(LQCourseHelper.class,"request "+params.getUri()+" failed");
                if(!EmptyUtil.isEmpty(callback)){
                    callback.onDataNotAvailable(R.string.net_error_tip);
                }
            }
        });
    }

    /**
     * 获取LQ学程分类数据
     * {@link LanguageType.LanguageRes}
     * @param isZh 是否中文显示 0 chinese 1 other
     * @param callback 数据回调接口
     */
    public static void requestLQBasicsConfigData(@LanguageType.LanguageRes int isZh,
                                                 @NonNull final DataSource.Callback<List<LQBasicsOuterEntity>> callback){
        final RequestVo requestVo = new RequestVo();
        // 是否是中文字体,根据参数,后台返回相应语言
        requestVo.addParams("language",isZh);
        final RequestParams params = new RequestParams(AppConfig.ServerUrl.GetNewBasicsConfigList+requestVo.getParams());
        params.setConnectTimeout(10000);
        LogUtil.i(LQCourseHelper.class,"send request ==== " +params.getUri());
        x.http().get(params, new StringCallback<String>() {
            @Override
            public void onSuccess(String str) {
                LogUtil.i(LQCourseHelper.class,"request "+params.getUri()+" result :"+str);
                TypeReference<ResponseVo<List<LQBasicsOuterEntity>>> typeReference =
                        new TypeReference<ResponseVo<List<LQBasicsOuterEntity>>>(){};
                ResponseVo<List<LQBasicsOuterEntity>> result = JSON.parseObject(str, typeReference);
                if(result.isSucceed()){
                    List<LQBasicsOuterEntity> data = result.getData();
                    if(!EmptyUtil.isEmpty(callback)){
                        callback.onDataLoaded(data);
                    }
                }else{
                    Factory.decodeRspCode(result.getCode(),callback);
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.w(LQCourseHelper.class,"request "+params.getUri()+" failed");
                if(!EmptyUtil.isEmpty(callback)){
                    callback.onDataNotAvailable(R.string.net_error_tip);
                }
            }
        });
    }

    /**
     * 获取LQ学程,热门推荐信息
     * @param needShowPay 是否显示收费课程
     * @param callback 回调数据
     */
    public static void requestDiscoveryData(boolean needShowPay, @NonNull final DataSource.Callback<DiscoveryItemVo> callback) {
        RequestVo requestVo = new RequestVo();
        if(!needShowPay){
            //只显示免费课程
            requestVo.addParams("payType", 0);
        }
        final RequestParams params = new RequestParams(AppConfig.ServerUrl.GetDiscoveryItemList+requestVo.getParams());
        params.setConnectTimeout(10000);
        LogUtil.i(LQCourseHelper.class,"send request ==== " +params.getUri());
        x.http().get(params, new StringCallback<String>() {

            @Override
            public void onSuccess(String str) {
                LogUtil.i(LQCourseHelper.class,"request "+params.getUri()+" result :"+str);
                DiscoveryItemVo result = JSON.parseObject(str,new TypeReference<DiscoveryItemVo>() {});
                if (result.isSucceed()) {
                    if(!EmptyUtil.isEmpty(callback)){
                        callback.onDataLoaded(result);
                    }
                }else{
                    Factory.decodeRspCode(result.getCode(),callback);
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.w(LQCourseHelper.class,"request "+params.getUri()+" failed");
                if(!EmptyUtil.isEmpty(callback)){
                    callback.onDataNotAvailable(R.string.net_error_tip);
                }
            }
        });
    }

    /**
     * 获取符合筛选条件的课程数据
     * @param organId 机构Id
     * @param pageIndex 分页数
     * @param pageSize 每页数据量
     * @param level 分类级别
     * @param payType 收费类型 如果是不正常的收费类型,也不传
     * @param sort 热门数据还是其它 如果是筛选数据就传 "0"
     * @param keyString 搜索关键词
     * @param paramOneId 筛选条件1
     * @param paramTwoId 筛选条件2
     * @param paramThreeId 筛选条件3
     * @param callback 回调数据
     */
    public static void requestLQCourseData(@Nullable String organId,
                                           int pageIndex, int pageSize,
                                           @NonNull String level, @NonNull String sort,
                                           String keyString,
                                           int payType,int paramOneId,
                                           int paramTwoId, int paramThreeId,
                                           @NonNull final DataSource.Callback<List<CourseVo>> callback) {

        RequestVo requestVo = new RequestVo();
        requestVo.addParams("pageIndex", pageIndex);
        requestVo.addParams("pageSize", AppConfig.PAGE_SIZE);
        requestVo.addParams("level", level);
        // 筛选数据就不传
        if(!TextUtils.equals(sort,"0")){
            requestVo.addParams("sort", sort);
        }

        if(!EmptyUtil.isEmpty(organId)){
            requestVo.addParams("organId",organId);
        }

        if(!EmptyUtil.isEmpty(keyString)){
            try {
                requestVo.addParams("courseName", URLEncoder.encode(keyString.trim(), "utf-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        // 传获取的收费类型
        if(payType != Integer.MAX_VALUE){
            requestVo.addParams("payType",payType);
        }

        if(paramOneId != 0){
            requestVo.addParams("paramOneId", paramOneId);
        }

        if(paramTwoId != 0){
            requestVo.addParams("paramTwoId", paramTwoId);
        }

        if(paramThreeId != 0){
            requestVo.addParams("paramThreeId", paramThreeId);
        }
        requestVo.addParams("progressStatus", -1);

        final RequestParams params = new RequestParams(AppConfig.ServerUrl.GetCourseList + requestVo.getParams());
        params.setConnectTimeout(10000);

        LogUtil.i(LQCourseHelper.class,"send request ==== " +params.getUri());
        x.http().get(params, new StringCallback<String>() {

            @Override
            public void onSuccess(String str) {
                LogUtil.i(LQCourseHelper.class,"request "+params.getUri()+" result :"+str);
                ResponseVo<List<CourseVo>> result = JSON.parseObject(str,new TypeReference<ResponseVo<List<CourseVo>>>() {});
                if (result.isSucceed()) {
                    if(!EmptyUtil.isEmpty(callback)){
                        List<CourseVo> data = result.getData();
                        callback.onDataLoaded(data);
                    }
                }else{
                    Factory.decodeRspCode(result.getCode(),callback);
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.w(LQCourseHelper.class,"request "+params.getUri()+" failed");
                if(!EmptyUtil.isEmpty(callback)){
                    callback.onDataNotAvailable(R.string.net_error_tip);
                }
            }
        });

    }

    /**
     * @param token token信息，如果是家长，就传孩子的memberId
     * @param id 课程Id
     * @param schoolIds 课程详情页加载课程详情传参 //来自LQ精品学程
     * @param dataType 数据类型
     * @param pageIndex 只有课程评论才需要传分页信息
     * @param pageSize
     * 获取LQ学程的课程基本信息
     */
    public static void requestCourseDetailByCourseId(@NonNull String token,
                                                     @NonNull String id,
                                                     @Nullable String schoolIds,
                                                     int dataType,
                                                     int pageIndex,int pageSize,
                                                     @NonNull DataSource.Callback<CourseDetailsVo> callback){
        RequestVo requestVo = new RequestVo();
        if(EmptyUtil.isNotEmpty(token))
        requestVo.addParams("token", token);
        requestVo.addParams("id", id);
        if(EmptyUtil.isNotEmpty(schoolIds))
        requestVo.addParams("schoolId",schoolIds);
        requestVo.addParams("dataType", dataType);
        if(dataType == CourseDetailItemParams.COURSE_DETAIL_ITEM_COURSE_COMMENT){
            requestVo.addParams("pageIndex", pageIndex);
            requestVo.addParams("pageSize", pageSize);
        }
        RequestParams params = new RequestParams(AppConfig.ServerUrl.GetCourseDetailsById + requestVo.getParams());
        params.setConnectTimeout(10000);

        LogUtil.i(LQCourseHelper.class,"send request ==== " +params.getUri());
        x.http().get(params, new StringCallback<String>() {
            @Override
            public void onSuccess(String str) {
                CourseDetailsVo vo = JSON.parseObject(str,new TypeReference<CourseDetailsVo>() {});
                if (vo.isSucceed()) {
                    if(EmptyUtil.isNotEmpty(callback)){
                        callback.onDataLoaded(vo);
                    }
                }else{
                    Factory.decodeRspCode(vo.getCode(),callback);
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.w(LQCourseHelper.class,"request "+params.getUri()+" failed");
                if(!EmptyUtil.isEmpty(callback)){
                    callback.onDataNotAvailable(R.string.net_error_tip);
                }
            }
        });
    }

    /**
     * @param token token信息，如果是家长，就传孩子的memberId
     * @param courseId 课程Id
     * @param schoolIds 仅在登陆用户是教师身份的情况下才传SchoolIds 以便server用于判断是否显示联合备课内容
     * 获取LQ学程的课程基本信息
     */
    public static void requestChapterByCourseId(@NonNull String token,
                                                @NonNull String courseId,
                                                @NonNull String schoolIds,
                                                @NonNull DataSource.Callback<CourseDetailsVo> callback){
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("token", token);
        requestVo.addParams("courseId", courseId);
        if(EmptyUtil.isNotEmpty(schoolIds))
        requestVo.addParams("schoolIds", schoolIds);
        RequestParams params = new RequestParams(AppConfig.ServerUrl.courseChapterList + requestVo.getParams());
        params.setConnectTimeout(50000);

        LogUtil.i(LQCourseHelper.class,"send request ==== " +params.getUri());
        x.http().get(params, new StringCallback<String>() {
            @Override
            public void onSuccess(String str) {
                CourseDetailsVo vo = JSON.parseObject(str,new TypeReference<CourseDetailsVo>() {});
                if (vo.isSucceed()) {
                    if(EmptyUtil.isNotEmpty(callback)){
                        callback.onDataLoaded(vo);
                    }
                }else{
                    Factory.decodeRspCode(vo.getCode(),callback);
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.w(LQCourseHelper.class,"request "+params.getUri()+" failed");
                if(!EmptyUtil.isEmpty(callback)){
                    callback.onDataNotAvailable(R.string.net_error_tip);
                }
            }
        });
    }

    /**
     * 提交课程评论
     * @param courseId 课程ID
     * @param type 1 回复类型 0 评论类型
     * @param isJoin 是否已经加入 估计是多余的
     * @param commentId 回复对象的Id
     * @param content 评论内容
     * @param starLevel 评分级别
     */
    public static void requestCommitCourseComment(@NonNull String courseId,
                                                  int type, boolean isJoin,
                                                  @Nullable String commentId,
                                                  @NonNull String content,
                                                  int starLevel,
                                                  @NonNull DataSource.Callback<ResponseVo> callback){
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("courseId", courseId);
        requestVo.addParams("isJoin", isJoin);
        requestVo.addParams("type",type);
        if(EmptyUtil.isNotEmpty(commentId)){
            requestVo.addParams("commentId", commentId);
        }

        try {
            String encodeContent = URLEncoder.encode(content, "utf-8");
            encodeContent = encodeContent.replaceAll("%0A", "\n");
            requestVo.addParams("content", encodeContent);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if(type == 0){
            // 评论类型,才有评分
            requestVo.addParams("starLevel", starLevel);
        }

        RequestParams params = new RequestParams(AppConfig.ServerUrl.AddCommentOrReply + requestVo.getParams());
        params.setConnectTimeout(10000);

        LogUtil.i(LQCourseHelper.class,"send request ==== " +params.getUri());
        x.http().get(params, new StringCallback<String>() {
            @Override
            public void onSuccess(String str) {
                ResponseVo vo = JSON.parseObject(str,new TypeReference<ResponseVo>() {});
                if (vo.isSucceed()) {
                    if(EmptyUtil.isNotEmpty(callback)){
                        callback.onDataLoaded(vo);
                    }
                }else{
                    Factory.decodeRspCode(vo.getCode(),callback);
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.w(LQCourseHelper.class,"request "+params.getUri()+" failed");
                if(!EmptyUtil.isEmpty(callback)){
                    callback.onDataNotAvailable(R.string.net_error_tip);
                }
            }
        });
    }
}