package com.lqwawa.intleducation.factory.helper;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

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
import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.home.LanguageType;
import com.lqwawa.intleducation.module.discovery.vo.ClassifyVo;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;

import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

/**
 * @desc 实体机构学程馆相关工具类
 */
public class OrganCourseHelper {

    /**
     * 获取实体机构学程馆一级标签及课程
     * @param organId 机构Id
     * @param isZh 语言 0 中文版， 1 英文版
     */
    public static void requestOrganCourseClassifyData(@NonNull String organId,
                                                      @NonNull @LanguageType.LanguageRes int isZh,
                                                      @NonNull DataSource.Callback<List<LQCourseConfigEntity>> callback){

        RequestVo requestVo = new RequestVo();
        requestVo.addParams("organId",organId);
        // 是否是中文字体,根据参数,后台返回相应语言
        requestVo.addParams("language",isZh);
        // 特色英语课程，添加参数
        requestVo.addParams("version",1);
        final RequestParams params = new RequestParams(AppConfig.ServerUrl.GetOrganCourseClassifyUrl+requestVo.getParams());
        params.setConnectTimeout(10000);
        LogUtil.i(LQCourseHelper.class,"send request ==== " +params.getUri());
        x.http().get(params, new StringCallback<String>() {
            @Override
            public void onSuccess(String str) {
                LogUtil.i(LQCourseHelper.class,"request "+params.getUri()+" result :"+str);
                ResponseVo<List<LQCourseConfigEntity>> result = JSON.parseObject(str,new TypeReference<ResponseVo<List<LQCourseConfigEntity>>>() {});
                if (result.isSucceed()) {
                    List<LQCourseConfigEntity> entities = result.getData();
                    if (!EmptyUtil.isEmpty(callback)) {
                        callback.onDataLoaded(entities);
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
     * 获取实体机构学程馆一级标签及课程
     * @param organId 机构Id
     * @param isZh 语言 0 中文版， 1 英文版
     */
    public static void requestOrganClassifyLabelData(@NonNull String organId,
                                                      @NonNull @LanguageType.LanguageRes int isZh,
                                                      int parentId,
                                                      @NonNull String level,
                                                      @NonNull DataSource.Callback<List<LQCourseConfigEntity>> callback){

        RequestVo requestVo = new RequestVo();
        requestVo.addParams("organId",organId);
        // 是否是中文字体,根据参数,后台返回相应语言
        requestVo.addParams("language",isZh);
        requestVo.addParams("parentId",parentId);
        requestVo.addParams("level",level);
        final RequestParams params = new RequestParams(AppConfig.ServerUrl.GetOrganCourseClassifyLabelUrl+requestVo.getParams());
        params.setConnectTimeout(10000);
        LogUtil.i(LQCourseHelper.class,"send request ==== " +params.getUri());
        x.http().get(params, new StringCallback<String>() {
            @Override
            public void onSuccess(String str) {
                LogUtil.i(LQCourseHelper.class,"request "+params.getUri()+" result :"+str);
                ResponseVo<List<LQCourseConfigEntity>> result = JSON.parseObject(str,new TypeReference<ResponseVo<List<LQCourseConfigEntity>>>() {});
                if (result.isSucceed()) {
                    List<LQCourseConfigEntity> entities = result.getData();
                    if (!EmptyUtil.isEmpty(callback)) {
                        callback.onDataLoaded(entities);
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
     * 获取实体机构学程馆一级标签及课程
     * @param organId 机构Id
     * @param isZh 语言 0 中文版， 1 英文版
     */
    public static void requestOrganAllClassifyLabelData(@NonNull String organId,
                                                     @NonNull @LanguageType.LanguageRes int isZh,
                                                     int parentId,
                                                     @NonNull String level,
                                                     @NonNull DataSource.Callback<List<LQCourseConfigEntity>> callback){

        RequestVo requestVo = new RequestVo();
        requestVo.addParams("organId",organId);
        // 是否是中文字体,根据参数,后台返回相应语言
        requestVo.addParams("language",isZh);
        // requestVo.addParams("parentId",parentId);
        // requestVo.addParams("level",level);
        final RequestParams params = new RequestParams(AppConfig.ServerUrl.GetOrganCourseAllClassifyLabelUrl+requestVo.getParams());
        params.setConnectTimeout(10000);
        LogUtil.i(LQCourseHelper.class,"send request ==== " +params.getUri());
        x.http().get(params, new StringCallback<String>() {
            @Override
            public void onSuccess(String str) {
                LogUtil.i(LQCourseHelper.class,"request "+params.getUri()+" result :"+str);
                ResponseVo<List<LQCourseConfigEntity>> result = JSON.parseObject(str,new TypeReference<ResponseVo<List<LQCourseConfigEntity>>>() {});
                if (result.isSucceed()) {
                    List<LQCourseConfigEntity> entities = result.getData();
                    if (!EmptyUtil.isEmpty(callback)) {
                        callback.onDataLoaded(entities);
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
     * 获取学程馆符合筛选条件的课程数据
     * @param organId 机构Id
     * @param pageIndex 分页数
     * @param pageSize 每页数据量
     * @param keyString 搜索关键词
     * @param level 级别
     * @param paramOneId 筛选条件1
     * @param paramTwoId 筛选条件2
     * @param paramThreeId 筛选条件3
     * @param callback 回调数据
     */
    public static void requestOrganCourseData(@Nullable String organId,
                                           int pageIndex, int pageSize,
                                           @Nullable String keyString,
                                           @NonNull String level,
                                           int paramOneId, int paramTwoId,
                                           int paramThreeId,
                                           @NonNull final DataSource.Callback<List<CourseVo>> callback) {

        RequestVo requestVo = new RequestVo();
        requestVo.addParams("pageIndex", pageIndex);
        requestVo.addParams("pageSize", AppConfig.PAGE_SIZE);

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

        if(EmptyUtil.isNotEmpty(level)){
            requestVo.addParams("level", level);
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

        final RequestParams params = new RequestParams(AppConfig.ServerUrl.GetOrganCourseListUrl + requestVo.getParams());
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

}
