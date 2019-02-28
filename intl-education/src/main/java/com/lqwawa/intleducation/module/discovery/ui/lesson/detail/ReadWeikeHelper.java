package com.lqwawa.intleducation.module.discovery.ui.lesson.detail;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.lecloud.skin.ui.utils.VodVideoSettingUtil;
import com.libs.gallery.ImageBrowserActivity;
import com.libs.gallery.ImageInfo;
import com.lqwawa.client.pojo.SourceFromType;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.MainApplication;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.helper.SharedPreferencesHelper;
import com.lqwawa.intleducation.base.utils.NetWorkUtils;
import com.lqwawa.intleducation.common.ui.CustomDialog;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.LetvVodHelperNew;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.module.learn.tool.TaskSliderHelper;
import com.lqwawa.intleducation.module.learn.ui.MyCourseDetailsActivity;
import com.lqwawa.intleducation.module.learn.vo.SectionResListVo;
import com.lqwawa.lqresviewlib.LqResViewHelper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * @author mrmedici
 * @desc 看微课代码的抽取
 */
public class ReadWeikeHelper {
    
    private WeakReference<Activity> mActivityReference;
    
    public ReadWeikeHelper(@NonNull Activity activity){
        mActivityReference = new WeakReference<>(activity);
    }

    /**
     * 释放资源
     */
    public void release(){
        mActivityReference.clear();
    }

    /**
     * 看微课
     * @param resVo 看微课资源实体
     */
    public void readWeike(final SectionResListVo resVo) {
        Activity mActivity = mActivityReference.get();
        if(EmptyUtil.isEmpty(resVo) || EmptyUtil.isEmpty(mActivity)){
            return;
        }
        
        int resType = resVo.getResType();
        if (resType > 10000) {
            resType -= 10000;
        }
        switch (resType) {
            case 1:
                showPic(resVo);
//
//                ImageDetailActivity.showStatic(activity,
//                        resVo.getResourceUrl().trim(), resVo.getName());
                break;
            case 2:
                playMedia(resVo, VodVideoSettingUtil.AUDIO_TYPE);
                break;
            case 6:
            case 20:
                if (TaskSliderHelper.onTaskSliderListener != null) {
                    TaskSliderHelper.onTaskSliderListener
                            .viewPdfOrPPT(mActivity, "" + resVo.getResId(), resVo.getResType(),
                                    resVo.getOriginName(), resVo.getCreateId(),
                                    mActivity.getIntent().getBooleanExtra(MyCourseDetailsActivity
                                            .KEY_IS_FROM_MY_COURSE, false)
                                            ? SourceFromType.LQ_MY_COURSE : SourceFromType.LQ_COURSE);
                }
                break;
            case 24:
            case 25:
                LqResViewHelper.playBaseRes(resVo.getResType(), mActivity,
                        resVo.getResourceUrl().trim(), resVo.getName());
                break;
            case 5:
            case 16:
            case 17:
            case 18:
            case 19:
            case 3:
            case 23:
            case 30:
                // 30 V5.14新添加的Q配音资源
                if (!SharedPreferencesHelper.getBoolean(mActivity,
                        AppConfig.BaseConfig.KEY_ALLOW_4G, false)) {
                    if (NetWorkUtils.isWifiActive(mActivity.getApplication().getApplicationContext())) {
                        if (resVo.getResType() == 3) {
//                            LqResViewHelper.playBaseRes(resVo.getResType(), activity, resVo.getVuid().trim(), resVo.getName());
                            playMedia(resVo, VodVideoSettingUtil.VIDEO_TYPE);
                        } else {
                            /*LqResViewHelper.playWeike(activity,
                                    UserHelper.getUserId(),
                                    UserHelper.getUserName(),
                                    resVo.getResourceUrl().trim(),
                                    resVo.getOriginName(),
                                    1,
                                    Utils.getCacheDir(),
                                    resVo.getScreenType(),
                                    resVo.getResType());*/
                            if (TaskSliderHelper.onTaskSliderListener != null) {
                                TaskSliderHelper.onTaskSliderListener.viewCourse(mActivity,
                                        resVo.getResId(), resVo.getResType(),
                                        mActivity.getIntent().getStringExtra("schoolId"),
                                        mActivity.getIntent().getBooleanExtra("isPublic", false),
                                        mActivity.getIntent().getBooleanExtra(MyCourseDetailsActivity
                                                .KEY_IS_FROM_MY_COURSE, false)
                                                ? SourceFromType.LQ_MY_COURSE : SourceFromType.LQ_COURSE);
                            }
                        }
                    } else {
                        UIUtil.showToastSafe(R.string.can_not_use_4g);
                    }
                } else {
                    if (NetWorkUtils.isWifiActive(mActivity.getApplication().getApplicationContext())) {
                        if (resVo.getResType() == 3) {
//                            LqResViewHelper.playBaseRes(resVo.getResType(), activity, resVo.getVuid().trim(), resVo.getName());
                            playMedia(resVo, VodVideoSettingUtil.VIDEO_TYPE);
                        } else {
                            /*LqResViewHelper.playWeike(activity,
                                    UserHelper.getUserId(),
                                    UserHelper.getUserName(),
                                    resVo.getResourceUrl().trim(),
                                    resVo.getOriginName(),
                                    1,
                                    Utils.getCacheDir(),
                                    resVo.getScreenType(),
                                    resVo.getResType());*/
                            if (TaskSliderHelper.onTaskSliderListener != null) {
                                TaskSliderHelper.onTaskSliderListener.viewCourse(mActivity,
                                        resVo.getResId(), resVo.getResType(),
                                        mActivity.getIntent().getStringExtra("schoolId"),
                                        mActivity.getIntent().getBooleanExtra("isPublic", false),
                                        mActivity.getIntent().getBooleanExtra(MyCourseDetailsActivity
                                                .KEY_IS_FROM_MY_COURSE, false)
                                                ? SourceFromType.LQ_MY_COURSE : SourceFromType.LQ_COURSE);
                            }
                        }
                    } else {
                        CustomDialog.Builder builder = new CustomDialog.Builder(mActivity);
                        builder.setMessage(UIUtil.getString(R.string.play_use_4g) + "?");
                        builder.setTitle(UIUtil.getString(R.string.tip));
                        builder.setPositiveButton(UIUtil.getResources().getString(R.string.continue_play),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        if (resVo.getResType() == 3) {
//                                            LqResViewHelper.playBaseRes(resVo.getResType(), activity, resVo.getVuid().trim(), resVo.getName());
                                            playMedia(resVo, VodVideoSettingUtil.VIDEO_TYPE);
                                        } else {
                                            /*LqResViewHelper.playWeike(activity,
                                                    UserHelper.getUserId(),
                                                    UserHelper.getUserName(),
                                                    resVo.getResourceUrl().trim(),
                                                    resVo.getOriginName(),
                                                    1,
                                                    Utils.getCacheDir(),
                                                    resVo.getScreenType(),
                                                    resVo.getResType());*/
                                            if (TaskSliderHelper.onTaskSliderListener != null) {
                                                TaskSliderHelper.onTaskSliderListener.viewCourse(mActivity,
                                                        resVo.getResId(), resVo.getResType(),
                                                        mActivity.getIntent().getStringExtra("schoolId"),
                                                        mActivity.getIntent().getBooleanExtra("isPublic", false),
                                                        mActivity.getIntent().getBooleanExtra(MyCourseDetailsActivity
                                                                .KEY_IS_FROM_MY_COURSE, false)
                                                                ? SourceFromType.LQ_MY_COURSE : SourceFromType.LQ_COURSE);
                                            }
                                        }
                                    }
                                });
                        builder.setNegativeButton(UIUtil.getResources().getString(R.string.cancel),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });

                        builder.create().show();
                    }
                }
                break;
            default:
                break;
        }
    }

    /**
     * 图片浏览
     *
     * @param resVo
     */
    private void showPic(SectionResListVo resVo) {
        Activity mActivity = mActivityReference.get();
        if(EmptyUtil.isEmpty(resVo) || EmptyUtil.isEmpty(mActivity)){
            return;
        }

        List<ImageInfo> resourceInfoList = new ArrayList<>();
        ImageInfo newResourceInfo = new ImageInfo();
        newResourceInfo.setTitle(resVo.getName());
        newResourceInfo.setResourceUrl(resVo.getResourceUrl().trim());
        newResourceInfo.setResourceId(resVo.getResId() + "-" + resVo.getResType());
        newResourceInfo.setAuthorId(resVo.getCreateId());
        newResourceInfo.setResourceType(resVo.getResType());
        resourceInfoList.add(newResourceInfo);


        Intent intent = new Intent();
        intent.setClassName(MainApplication.getInstance().getPackageName(), "com.galaxyschool.app.wawaschool.imagebrowser.GalleryActivity");
        intent.putParcelableArrayListExtra(ImageBrowserActivity.EXTRA_IMAGE_INFOS, (ArrayList<? extends Parcelable>) resourceInfoList);
        intent.putExtra(ImageBrowserActivity.EXTRA_CURRENT_INDEX, 0);
        intent.putExtra(ImageBrowserActivity.ISPDF, false);

        intent.putExtra(ImageBrowserActivity.KEY_ISHIDEMOREBTN, false);
        intent.putExtra(ImageBrowserActivity.KEY_ISSHOWCOURSEANDREADING, true);
        intent.putExtra(ImageBrowserActivity.KEY_ISSHOWCOLLECT, false);//隐藏收藏功能
        mActivity.startActivity(intent);
    }

    /**
     * 音视频播放
     *
     * @param resVo
     * @param type
     */
    private void playMedia(SectionResListVo resVo, int type) {
        Activity mActivity = mActivityReference.get();
        if(EmptyUtil.isEmpty(resVo) || EmptyUtil.isEmpty(mActivity)){
            return;
        }

        new LetvVodHelperNew.VodVideoBuilder(mActivity)
                .setNewUI(true)//使用自定义UI
                .setTitle(resVo.getName())//视频标题
                .setAuthorId(resVo.getCreateId())
                .setResId(resVo.getResId() + "-" + resVo.getResType())
                .setResourceType(resVo.getResType())
                .setVuid(resVo.getVuid())
                .setUrl(resVo.getResourceUrl())
                .setMediaType(type)//设置媒体类型
                .setPackageName(MainApplication.getInstance().getPackageName())
                .setClassName("com.galaxyschool.app.wawaschool.medias.activity.VodPlayActivity")
                .setHideBtnMore(true)
                .setLeStatus(resVo.getLeStatus())
                .setIsPublic(mActivity.getIntent().getBooleanExtra("isPublic", false))
                .create();
    }
    
}
