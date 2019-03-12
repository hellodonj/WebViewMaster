package com.galaxyschool.app.wawaschool.common;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.duowan.mobile.netroid.Request;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.lqwawa.lqbaselib.net.ThisStringRequest;
import com.galaxyschool.app.wawaschool.pojo.MaterialResourceType;
import com.galaxyschool.app.wawaschool.pojo.PPTAndPDFCourseInfo;
import com.galaxyschool.app.wawaschool.pojo.PPTAndPDFCourseInfoCode;
import com.galaxyschool.app.wawaschool.pojo.ResourceInfoTag;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseData;
import com.galaxyschool.app.wawaschool.pojo.weike.SplitCourseInfo;
import com.lecloud.skin.ui.utils.VodVideoSettingUtil;
import com.libs.gallery.ImageInfo;
import com.lqwawa.client.pojo.ResourceInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by E450 on 2017/06/19.
 * 看课件资源打开工具类
 */
public class WatchWawaCourseResourceOpenUtils {

    /**
     * 打开看课件资源
     *
     * @param context
     * @param resourceInfoTag
     * @param showCourseAndReading 是否显示点读和做课件
     * @param isPersonalSpace      是否来自个人资源库
     */
    public static void openResource(final Context context, final ResourceInfoTag resourceInfoTag,
                                    final boolean isPersonalSpace,
                                    final boolean showCourseAndReading) {
        openResource(context, resourceInfoTag, isPersonalSpace, showCourseAndReading, false);

    }

    public static void openResource(final Context context, final ResourceInfoTag resourceInfoTag,
                                    final boolean isPersonalSpace,
                                    final boolean showCourseAndReading, boolean isFromMooc) {
        if (context == null || resourceInfoTag == null) {
            return;
        }
        String path = resourceInfoTag.getResourcePath();
        if (!TextUtils.isEmpty(path)) {
            String resId = resourceInfoTag.getResId();
            int resourceType = Utils.getResourceTypeBySplitingResId(resId);
            int type = resourceType % MaterialResourceType.BASE_TYPE_NUM;
            //LQ课件、音频、图片、视频、PPT、PDF
            switch (type) {

                //LQ课件/有声相册
                case MaterialResourceType.OLD_COURSE:
                case MaterialResourceType.OLD_COURSE_ANOTHER:
                case MaterialResourceType.MICRO_COURSE:
                case MaterialResourceType.ONE_PAGE:
                    openCourse(context, resourceInfoTag, isFromMooc);
                    break;

                case MaterialResourceType.PICTURE:
                    openPictureSetImage(context, resourceInfoTag, isPersonalSpace,
                            showCourseAndReading, isFromMooc);
                    break;

                case MaterialResourceType.PPT:
                case MaterialResourceType.PDF:
                case MaterialResourceType.DOC:
                    openPDFAndPPTDetails(context, resourceInfoTag, isPersonalSpace,
                            showCourseAndReading, isFromMooc);
                    break;

                case MaterialResourceType.AUDIO:
                    mediaPlay(context, resourceInfoTag, showCourseAndReading, isPersonalSpace);
                    break;
                case MaterialResourceType.VIDEO:
                case MaterialResourceType.Q_DUBBING_VIDEO:
                    //非分页信息
                    WawaCourseUtils wawaCourseUtils = new WawaCourseUtils((Activity) context);
                    wawaCourseUtils.loadCourseDetail(resId);
                    wawaCourseUtils.setOnCourseDetailFinishListener(new WawaCourseUtils.
                            OnCourseDetailFinishListener() {
                        @Override
                        public void onCourseDetailFinish(CourseData courseData) {
                            if (courseData != null) {
                                String leValue = courseData.getLeValue();
                                if (!TextUtils.isEmpty(leValue)) {
                                    if (resourceInfoTag != null) {
                                        String[] values = leValue.split("&");
                                        String uUid = values[1].split("=")[1];
                                        String vUid = values[2].split("=")[1];
                                        resourceInfoTag.setLeValue(leValue);
                                        resourceInfoTag.setVuid(vUid);
                                        resourceInfoTag.setLeStatus(courseData.getLeStatus());
                                    }
                                }
                                mediaPlay(context, resourceInfoTag, showCourseAndReading, isPersonalSpace);
                            }
                        }
                    });
                    break;
            }
        }
    }

    /**
     * 打开ppt或者pdf
     */
    public static void openPDFAndPPTDetails(final Context context, final ResourceInfoTag data,
                                            final boolean isPersonalSpace,
                                            final boolean showCourseAndReading, final boolean isShowCollect) {
        if (context == null || data == null) {
            return;
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("resId", data.getResId());
            StringBuilder builder = new StringBuilder();
            builder.append("?j=" + jsonObject.toString());
            String url = ServerUrl.WAWATV_COURSE_DETAIL_URL + builder.toString();
            final ThisStringRequest request = new ThisStringRequest(
                    Request.Method.GET, url, new Listener<String>() {
                @Override
                public void onSuccess(String jsonString) {
                    if (context == null) {
                        return;
                    }
                    if (TextUtils.isEmpty(jsonString)) {
                        return;
                    }
                    PPTAndPDFCourseInfoCode result = com.alibaba.fastjson.JSONObject.parseObject
                            (jsonString, PPTAndPDFCourseInfoCode.class);
                    if (result != null) {
                        List<PPTAndPDFCourseInfo> splitCourseInfo = result.getData();
                        if (splitCourseInfo == null || splitCourseInfo.size() == 0) {
                            TipMsgHelper.ShowLMsg(context, R.string.ppt_pdf_not_have_pic);
                            return;
                        }
                        List<SplitCourseInfo> splitList = splitCourseInfo.get(0).getSplitList();
                        if (splitList == null || splitList.size() == 0) {
                            TipMsgHelper.ShowLMsg(context, R.string.ppt_pdf_not_have_pic);
                            return;
                        }
                        if (splitList != null && splitList.size() > 0) {
                            ArrayList<ImageInfo> newResourceInfos = new ArrayList<>();
                            for (SplitCourseInfo info : splitList) {
                                if (info != null) {
                                    ImageInfo newResourceInfo = new ImageInfo();
                                    newResourceInfo.setTitle(info.getSubResName());
                                    //缩略图地址
                                    newResourceInfo.setThumbnail(info.getPlayUrl());
                                    newResourceInfo.setShareAddress(info.getShareUrl());
                                    newResourceInfo.setResourceUrl(info.getPlayUrl());
                                    newResourceInfo.setMicroId(String.valueOf(info.getParentId()));
                                    newResourceInfo.setResourceType(info.getSubResType());
                                    //设置资源作者id
                                    newResourceInfo.setAuthorId(data.getAuthorId());
                                    //装载
                                    newResourceInfos.add(newResourceInfo);
                                }
                            }
                            //打开资源
                            ActivityUtils.openImage((Activity) context, newResourceInfos, true, 0,
                                    isPersonalSpace, showCourseAndReading, isShowCollect);
                        }
                    }
                }

                @Override
                public void onError(NetroidError error) {
                    if (context == null) {
                        return;
                    }
                }
            });
            request.addHeader("Accept-Encoding", "*");
            request.start(context);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 打开课件
     *
     * @param context
     * @param info
     */
    public static void openCourse(Context context, ResourceInfo info, boolean isFromMooc) {
        if (context == null || info == null) {
            return;
        }
        CourseOpenUtils.openCourseDirectly((Activity) context, info.getResId(), true, "", isFromMooc);
    }

    /**
     * resourceInfoTag
     *
     * @param resourceInfoTag
     */
    public static void openPictureSetImage(Context context, ResourceInfoTag resourceInfoTag,
                                           boolean isPersonalSpace, boolean showReadingAndCourse, boolean isShowCollect) {
        if (context == null) {
            return;
        }
        List<ImageInfo> mediaInfos = toPictureSetMediaInfos(resourceInfoTag);
        //单张图片不显示页码，多张显示。
        boolean shouldShowPageNumber = mediaInfos != null
                && mediaInfos.size() > 1;
        ActivityUtils.openImage((Activity) context, mediaInfos, shouldShowPageNumber, 0,
                isPersonalSpace, showReadingAndCourse, isShowCollect);
    }

    private static boolean isPPTOrPDF(int resourceType) {
        return resourceType == MaterialResourceType.PPT
                || resourceType == MaterialResourceType.PDF
                || resourceType == MaterialResourceType.DOC;
    }

    public static List<ImageInfo> toPictureSetMediaInfos(ResourceInfoTag resourceInfoTag) {
        if (resourceInfoTag != null) {
            List<ImageInfo> mediaInfoList = new ArrayList<>();
            //图片集等有子类的对象需要遍历添加
            List<ResourceInfo> resourceInfoList = resourceInfoTag.getSplitInfoList();
            if (resourceInfoList != null && resourceInfoList.size() > 0) {
                for (ResourceInfo resourceInfo : resourceInfoList) {
                    if (resourceInfo != null) {
                        ImageInfo newResourceInfo = new ImageInfo();
                        newResourceInfo.setThumbnail(resourceInfo.getImgPath());
                        newResourceInfo.setTitle(resourceInfo.getTitle());
                        //处理图片
                        String resId = resourceInfo.getResId();
                        int resourceType = resourceInfo.getResourceType();
                        if (!TextUtils.isEmpty(resId)) {
                            if (resId.contains("-")) {
                                String microId = resId.substring(0, resId.indexOf("-"));
                                newResourceInfo.setMicroId(microId);
                                resourceType = Integer
                                        .parseInt(resId.substring(resId.lastIndexOf("-") + 1));
                            }
                        }
                        newResourceInfo.setResourceType(resourceType);
                        //资源地址
                        newResourceInfo.setResourceUrl(resourceInfo.getResourcePath());
                        //作者id
                        newResourceInfo.setAuthorId(resourceInfo.getAuthorId());
                        mediaInfoList.add(newResourceInfo);
                    }
                }
            }
            return mediaInfoList;
        }
        return null;
    }

    /**
     * 音频、视频播放
     *
     * @param data
     */
    public static void mediaPlay(Context context, ResourceInfoTag data, boolean showMenuList,
                                 boolean isPersonSpace) {
        if (context == null || data == null) {
            return;
        }
        int mediaType = VodVideoSettingUtil.VIDEO_TYPE;
        // 视频
        String resId = data.getResId();
        int resourceType = Utils.getResourceTypeBySplitingResId(resId);
        if (resourceType == MaterialResourceType.AUDIO) {
            mediaType = VodVideoSettingUtil.AUDIO_TYPE;
        }
        String filePath = data.getResourcePath();
        filePath = AppSettings.getFileUrl(filePath);

        new LetvVodHelperNew.VodVideoBuilder((Activity) context)
                .setNewUI(true)//使用自定义UI
                .setTablet(false)//平板为true
                .setHideBtnMore(true)//个人资源库隐藏收藏pop
                .setTitle(data.getTitle())//视频标题
                .setUrl(filePath)//路径
                .setMediaType(mediaType)
                .setResId(resId)
                .setResourceType(resourceType)
                .setAuthorId(data.getAuthorId())
                //视频播放地址,解决视频旋转的问题。
                .setVuid(data.getVuid())
                .setLeStatus(data.getLeStatus())
                .create();
    }

    /**
     * 根据类型获取默认图片
     *
     * @param resourceInfoTag
     * @return
     */
    public static int getItemDefaultIcon(ResourceInfoTag resourceInfoTag) {
        int defaultIcon = -1;
        if (resourceInfoTag != null) {
            String resId = resourceInfoTag.getResId();
            int resourceType = Utils.getResourceTypeBySplitingResId(resId);
            int type = resourceType % MaterialResourceType.BASE_TYPE_NUM;
            //LQ课件、音频、图片、视频、PPT、PDF
            switch (type) {

                //LQ课件/有声相册
                case MaterialResourceType.OLD_COURSE:
                case MaterialResourceType.OLD_COURSE_ANOTHER:
                case MaterialResourceType.MICRO_COURSE:
                case MaterialResourceType.ONE_PAGE:
                    defaultIcon = R.drawable.icon_lq_course;
                    break;

                case MaterialResourceType.AUDIO:
                    defaultIcon = R.drawable.resource_audio_ico;
                    break;

                case MaterialResourceType.PICTURE:
                    List<ResourceInfo> resourceInfoList = resourceInfoTag.getSplitInfoList();
                    if (resourceInfoList != null && resourceInfoList.size() > 0) {
                        if (resourceInfoList.size() == 1) {
                            //单张图片
                            defaultIcon = R.drawable.resource_pic_ico;
                        } else {
                            //多张图片
                            defaultIcon = R.drawable.icon_album;
                        }
                    }
                    break;

                case MaterialResourceType.VIDEO:
                case MaterialResourceType.Q_DUBBING_VIDEO:
                    defaultIcon = R.drawable.resource_video_ico;
                    break;

                case MaterialResourceType.PPT:
                    defaultIcon = R.drawable.icon_ppt;
                    break;

                case MaterialResourceType.PDF:
                    defaultIcon = R.drawable.icon_personal_resource_pdf;
                    break;

                case MaterialResourceType.DOC:
                    defaultIcon = R.drawable.icon_doc;
                    break;

                default:
                    defaultIcon = 0;
                    break;
            }
        }
        return defaultIcon;
    }

    /**
     * 获得标题
     *
     * @param resourceInfoTag
     * @return
     */
    public static String getItemTitle(ResourceInfoTag resourceInfoTag) {
        String title = "";
        if (resourceInfoTag != null) {
            int mediaType = resourceInfoTag.getResourceType();
            if (mediaType == MaterialResourceType.PICTURE) {
                List<ResourceInfo> resourceInfoList = resourceInfoTag.getSplitInfoList();
                if (resourceInfoList != null && resourceInfoList.size() > 0) {
                    ResourceInfo resourceInfo = resourceInfoList.get(0);
                    if (resourceInfo != null) {
                        //相册标题选相册里面第一张图片的标题
                        title = resourceInfo.getTitle();
                    }
                }
            } else {
                title = resourceInfoTag.getTitle();
            }
        }
        return title;
    }

    /**
     * 处理LQ课件zip包的图片
     *
     * @param imagePath
     * @return
     */
    public static String transferImagePath(String imagePath) {
        if (!TextUtils.isEmpty(imagePath)) {
            String suffix = ".zip";
            String headSuffix = "/head.jpg";
            if (imagePath.contains(suffix)) {
                imagePath = imagePath.substring(0, imagePath.lastIndexOf(suffix));
                imagePath += headSuffix;
                return imagePath;
            }
        }
        return null;
    }
}
