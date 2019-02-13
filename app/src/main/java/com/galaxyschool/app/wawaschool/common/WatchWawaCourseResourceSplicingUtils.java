package com.galaxyschool.app.wawaschool.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.fragment.IntroductionForReadCourseFragment;
import com.galaxyschool.app.wawaschool.fragment.MediaListFragment;
import com.galaxyschool.app.wawaschool.pojo.CourseInfo;
import com.galaxyschool.app.wawaschool.pojo.MaterialResourceType;
import com.galaxyschool.app.wawaschool.pojo.MediaInfo;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfo;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfoTag;
import com.galaxyschool.app.wawaschool.pojo.ResType;
import com.galaxyschool.app.wawaschool.pojo.ResourceInfoTag;
import com.galaxyschool.app.wawaschool.pojo.SchoolInfo;
import com.lqwawa.client.pojo.MediaType;
import com.lqwawa.client.pojo.ResourceInfo;
import com.lqwawa.intleducation.module.discovery.ui.HQCCourseListActivity;
import com.lqwawa.intleducation.module.learn.vo.SectionResListVo;
import com.osastudio.common.utils.ConstantSetting;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by E450 on 2017/07/18.
 * 看课件资源拼接工具类
 */
public class WatchWawaCourseResourceSplicingUtils {

    /**
     * 拼接整页资源
     * @param newResourceInfo
     */
    public static void splicingFullPageResources(Activity activity,
                                           NewResourceInfoTag newResourceInfo,
                                                 int mediaType) {
        if (activity != null && newResourceInfo != null) {
            String parentTitle = newResourceInfo.getTitle();
            String parentMicroId = newResourceInfo.getMicroId();
            String parentAuthorId = newResourceInfo.getAuthorId();

            ArrayList<ResourceInfoTag> resourceInfoTags = new ArrayList<>();
            //头
            ResourceInfoTag resourceInfoTag = getResourceInfoTagByNewResourceInfo
                    (newResourceInfo,parentTitle,parentMicroId,mediaType);
            //设置标题为头部的标题
            resourceInfoTag.setTitle(parentTitle);
            resourceInfoTag.setAuthorId(parentAuthorId);
            List<NewResourceInfo> splitInfo = newResourceInfo.getSplitInfoList();
            if (splitInfo != null
                    && splitInfo.size() > 0
                    && (newResourceInfo.getResourceType() == ResType.RES_TYPE_PPT
                    || newResourceInfo.getResourceType() == ResType.RES_TYPE_PDF
                    || newResourceInfo.getResourceType() == ResType.RES_TYPE_DOC)){
                resourceInfoTag.setImgPath(splitInfo.get(0).getResourceUrl());
            }
            resourceInfoTags.add(resourceInfoTag);
            processWatchWawaCourseData(activity,resourceInfoTags);
        }
    }

    /**
     * 用NewResourceInfo构建ResourceInfoTag
     * @param info
     * @param parentTitle
     * @param parentMicroId
     * @return
     */
    private static ResourceInfoTag getResourceInfoTagByNewResourceInfo(NewResourceInfo info,
                                                                String parentTitle,
                                                                String parentMicroId,
                                                                       int mediaType){
        if (info == null){
            return null;
        }
        ResourceInfoTag resourceInfoTag = new ResourceInfoTag();
        String title = info.getTitle();
        if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(parentTitle)) {
            //拼接上父类的title
            title = parentTitle + "-" + title;
        }
        resourceInfoTag.setTitle(title);
        resourceInfoTag.setImgPath(AppSettings.getFileUrl(info.getResourceUrl()));
        resourceInfoTag.setResourcePath(AppSettings.getFileUrl(info.getResourceUrl()));
        //设置类型
        resourceInfoTag.setType(mediaType);
        resourceInfoTag.setShareAddress(info.getShareAddress());
        resourceInfoTag.setResourceType(info.getResourceType());

        //resId
        String resId = parentMicroId;
        if (TextUtils.isEmpty(resId)){
            resId = info.getId();
        }
        if (!TextUtils.isEmpty(resId) && resId.contains("-")){
            resourceInfoTag.setResId(resId);
        }else {
            resourceInfoTag.setResId(resId+"-"+resourceInfoTag.getResourceType());
        }
        //设置作者id
        resourceInfoTag.setAuthorId(info.getAuthorId());
        //设置播放地址
        resourceInfoTag.setVuid(info.getVuid());
        resourceInfoTag.setLeStatus(info.getLeStatus());
        return resourceInfoTag;
    }

    /**
     * 拼接分页看课件资源
     * @param mediaInfos
     */
    public static void splicingSplitPageResources(Activity activity,
                                                  List<MediaInfo> mediaInfos,
                                                  int mediaType) {
        if (activity == null || mediaInfos == null || mediaInfos.size() == 0) {
            return;
        }
        //item数组
        ArrayList<ResourceInfoTag> resourceInfoTags = new ArrayList<>();
        //图片集数组
        List<ResourceInfo> splitInfoList = new ArrayList<>();
        ResourceInfoTag originResourceInfoTag = null;
        for (int i = 0 ; i < mediaInfos.size(); i++){
            MediaInfo mediaInfo = mediaInfos.get(i);
            if (mediaInfo != null){
                ResourceInfoTag resourceInfoTag = getResourceInfoTagByMediaInfo(mediaInfo,
                        mediaType);
                if (resourceInfoTag != null){
                    if (needAddToPictureSet(mediaType)){
                        //默认第一张加入到图片集
                        if (i == 0){
                            originResourceInfoTag = resourceInfoTag;
                        }
                        ResourceInfo resourceInfo = resourceInfoTag.toResourceInfo();
                        //图片加入到图片集数组
                        splitInfoList.add(resourceInfo);
                    }else {
                        //添加到数组
                        resourceInfoTags.add(resourceInfoTag);
                    }
                }
            }
        }
        if (needAddToPictureSet(mediaType)) {
            //装载到对象
            if (originResourceInfoTag != null) {
                originResourceInfoTag.setSplitInfoList(splitInfoList);
            }
            //添加到数组
            resourceInfoTags.add(originResourceInfoTag);
        }
        processWatchWawaCourseData(activity,resourceInfoTags);
    }

    /**
     * 用MediaInfo构建ResourceInfoTag
     * @param mediaInfo
     * @return
     */
    private static ResourceInfoTag getResourceInfoTagByMediaInfo(MediaInfo mediaInfo,
                                                                 int mediaType){
        if (mediaInfo == null){
            return null;
        }
        ResourceInfoTag resourceInfoTag = new ResourceInfoTag();
        resourceInfoTag.setTitle(mediaInfo.getTitle());
        resourceInfoTag.setImgPath(AppSettings.getFileUrl(mediaInfo.getThumbnail()));
        resourceInfoTag.setResourcePath(AppSettings.getFileUrl(mediaInfo.getPath()));
        //设置类型
        resourceInfoTag.setType(mediaType);
        resourceInfoTag.setShareAddress(mediaInfo.getShareAddress());
        resourceInfoTag.setResourceType(mediaInfo.getResourceType());
        resourceInfoTag.setResProperties(mediaInfo.getResProperties());
        CourseInfo courseData = mediaInfo.getCourseInfo();
        if (courseData != null){
            resourceInfoTag.setScreenType(courseData.getScreenType());
        }
        //resId
        String resId = mediaInfo.getMicroId();
        if (TextUtils.isEmpty(resId)){
            resId = mediaInfo.getId();
        }
        if (!TextUtils.isEmpty(resId) && resId.contains("-")){
            resourceInfoTag.setResId(resId);
        }else {
            resourceInfoTag.setResId(resId+"-"+resourceInfoTag.getResourceType());
        }
        //设置作者id
        resourceInfoTag.setAuthorId(mediaInfo.getAuthorId());
        //设置播放地址
        resourceInfoTag.setVuid(mediaInfo.getVuid());
        resourceInfoTag.setLeStatus(mediaInfo.getLeStatus());
        resourceInfoTag.setAuthorName(mediaInfo.getAuthor());
        resourceInfoTag.setPoint(mediaInfo.getPoint());
        if (TextUtils.isEmpty(mediaInfo.getUpdateTime())){
            Date date;
            try {
                date = DateUtils.longToDate(mediaInfo.getCreateTime(),
                        DateUtils.DATE_PATTERN_yyyy_MM_dd_HH_MM);
                resourceInfoTag.setCreateTime(DateUtils.dateToString(date,DateUtils.DATE_PATTERN_yyyy_MM_dd_HH_MM));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            resourceInfoTag.setCreateTime(mediaInfo.getUpdateTime());
        }
        return resourceInfoTag;
    }

    /**
     * 判断是否要加入到图片集
     * @return
     */
    private static boolean needAddToPictureSet(int mediaType){
        return mediaType == MediaType.PICTURE;
    }

    /**
     * 判断LQ学程资源是否要加入到图片集
     * @return
     */
    public static boolean isLQProgramNeedAddToPictureSet(int type){
        return type == MaterialResourceType.PICTURE;
    }

    /**
     * 处理看课件数据
     * @param resourceInfoTags
     */
    private static void processWatchWawaCourseData(Activity activity,
                                            ArrayList<ResourceInfoTag> resourceInfoTags){
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(MediaListFragment.EXTRA_RESOURCE_INFO_LIST, resourceInfoTags);
        Intent intent = new Intent();
        intent.putExtras(bundle);
        if (activity != null) {
            activity.setResult(activity.RESULT_OK, intent);
            activity.finish();
        }
    }

    /**
     * 判断是否是ppt、pdf资源
     * @return
     */
    public static boolean isPPTOrPDTResource(int resourceType){
        return resourceType == MaterialResourceType.PDF
                || resourceType == MaterialResourceType.PPT
                || resourceType == MaterialResourceType.DOC;
    }

    /**
     * 判断看课件是否支持多类型
     * @return
     */
    public static boolean watchWawaCourseSupportMultiType(Bundle bundle){
        if (bundle != null){
            return bundle.getBoolean(MediaListFragment.EXTRA_SUPPORT_MULTI_TYPE_WATCH_WAWA_COURSE);
        }
        return false;
    }

    /**
     * 看课件控制资源选取的数量（和pad统一）
     * count不赋值就是不限制
     */
    public static int controlResourcePickedMaxCount(int mediaType, int maxCount, boolean isSplit) {
        if (mediaType == MediaType.ONE_PAGE || mediaType == MediaType.MICROCOURSE){
            //LQ课件
            if (!isSplit){
                //非拆分页面
                maxCount = ConstantSetting.SELECT_COURSEWARE_CLOUD_NUM;
            }
        }else if (mediaType == MediaType.PICTURE){
            //图片
            //限制图片集最多选取9张图片
            maxCount = ConstantSetting.SELECT_PICTURE_NUM;
        }else if (mediaType == MediaType.AUDIO){
            //音频
            maxCount = ConstantSetting.SELECT_MEDIA_CLOUD_NUM;
        }else if (mediaType == MediaType.VIDEO){
            //视频
            maxCount = ConstantSetting.SELECT_MEDIA_CLOUD_NUM;
        }else if(mediaType == MediaType.PDF || mediaType == MediaType.PPT || mediaType ==
                MediaType.DOC){
            if (!isSplit){
                //非拆分页面
                maxCount = ConstantSetting.SELECT_PPT_PDF_NUM;
            }
        }else if (mediaType == MediaType.TASK_ORDER){
            maxCount = ConstantSetting.SELECT_COURSEWARE_CLOUD_NUM;
        }
        return maxCount;
    }

    /**
     * 拼接LQ学程数据
     */
    public static void splicingLQProgramResources(Activity activity,
                                                  ArrayList<SectionResListVo> list){
        if (activity == null || list == null || list.size() < 0){
            return;
        }
        //item数组
        ArrayList<ResourceInfoTag> resourceInfoTags = new ArrayList<>();
        for (int i = 0 ; i < list.size(); i++){
            SectionResListVo vo = list.get(i);
            if (vo != null){
                ResourceInfoTag resourceInfoTag = transferLQProgramData(vo);
                if (resourceInfoTag != null){
                    //图片是单个资源（单张图片当成只有一张图片的图片集处理）
                    if (isLQProgramNeedAddToPictureSet(vo.getResType())){
                        ResourceInfo resourceInfo = resourceInfoTag.toResourceInfo();
                        //图片加入到图片集数组
                        List<ResourceInfo> splitInfoList = new ArrayList<>();
                        splitInfoList.add(resourceInfo);
                        resourceInfoTag.setSplitInfoList(splitInfoList);
                        resourceInfoTag.setIsSelected(true);
                        //添加到数组
                        resourceInfoTags.add(resourceInfoTag);
                    }else {
                        //添加到数组
                        resourceInfoTag.setIsSelected(true);
                        resourceInfoTags.add(resourceInfoTag);
                    }
                }
            }
        }
        Bundle bundle = new Bundle();
        //标识选取的是LQ课程资源
        bundle.putBoolean(IntroductionForReadCourseFragment.FROM_LQ_PROGRAM,true);
        bundle.putParcelableArrayList(MediaListFragment.EXTRA_RESOURCE_INFO_LIST, resourceInfoTags);
        Intent intent = new Intent();
        intent.putExtras(bundle);
        activity.setResult(activity.RESULT_OK, intent);
        activity.finish();
    }

    /**
     * 转换LQ学程数据
     * @param vo
     * @return
     */
    public static ResourceInfoTag transferLQProgramData(SectionResListVo vo){
        if (vo == null){
            return null;
        }
        ResourceInfoTag tag = new ResourceInfoTag();
        tag.setTitle(vo.getName());
        String resourceUrl = vo.getResourceUrl();
        //截取zip包的缩略图
        if (!TextUtils.isEmpty(resourceUrl)){
            String suffix = ".zip";
            String headSuffix = "/head.jpg";
            if (resourceUrl.contains(suffix)){
                resourceUrl = resourceUrl.substring(0,resourceUrl.lastIndexOf(suffix));
                resourceUrl += headSuffix;
            }
        }
        //仅图片路径需要处理
        tag.setImgPath(AppSettings.getFileUrl(resourceUrl));
        tag.setResourcePath(AppSettings.getFileUrl(vo.getResourceUrl()));
        //设置类型
        tag.setType(vo.getType());
        tag.setShareAddress(vo.getResourceUrl());
        tag.setResourceType(vo.getResType());
        tag.setResProperties(vo.getResProperties());
        tag.setScreenType(vo.getScreenType());
        //resId
        String resId = vo.getResId();
        if (TextUtils.isEmpty(resId)){
            resId = vo.getId();
        }
        if (!TextUtils.isEmpty(resId) && resId.contains("-")){
            tag.setResId(resId);
        }else {
            tag.setResId(resId+"-"+tag.getResourceType());
        }
        //设置作者id
        tag.setAuthorId(vo.getCreateId());
        //设置播放地址
        tag.setVuid(vo.getVuid());
        if (!TextUtils.isEmpty(vo.getChapterId())) {
            tag.setResCourseId(Integer.valueOf(vo.getChapterId()));
        }
        tag.setPoint(vo.getPoint());
        return tag;
    }

    /**
     * 从LQ学程选取资源
     */
    public static void chooseLQProgramResources(Activity activity, Fragment fragment, int
            taskType, SchoolInfo schoolInfo) {
        if (activity == null || schoolInfo == null){
            return;
        }
        Intent intent = new Intent(activity,HQCCourseListActivity.class);
        intent.putExtra("Sort", "-1");
        intent.putExtra("SchoolId", schoolInfo.getSchoolId());
        intent.putExtra("tasktype",taskType);
        intent.putExtra("isForSelRes", true);
        if (fragment != null){
            fragment.startActivityForResult(intent, HQCCourseListActivity.RC_SelectCourseRes);
        }else {
            activity.startActivityForResult(intent, HQCCourseListActivity.RC_SelectCourseRes);
        }
    }
}
