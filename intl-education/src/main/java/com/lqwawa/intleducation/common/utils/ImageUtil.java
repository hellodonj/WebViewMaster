package com.lqwawa.intleducation.common.utils;

import android.graphics.Bitmap;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.lqwawa.intleducation.R;
import com.osastudio.common.utils.LQImageLoader;

import org.xutils.image.ImageOptions;
import org.xutils.x;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 用于LQWAWA 显示图片的工具类
 * @date 2018/04/27 16:58
 * @history v1.0
 * **********************************
 */
public class ImageUtil {
    /**
     * 用于加载分类数据图片Icon的ImageOptions配置
     */
    private static ImageOptions CLASSIFY_IMAGE_OPTIONS = null;
    /**
     * 加载一些默认配置的图片
     */
    private static ImageOptions DEFAULT_IMAGE_OPTIONS = null;
    /**
     * 加载一些什么都不需要配置的图片
     */
    private static ImageOptions NORMAL_IMAGE_OPTIONS = null;
    /**
     * 加载圆形图片的View
     */
    private static ImageOptions CROP_IMAGE_OPTIONS = null;
    /**
     * 用于加载课程图片的ImaageOptions配置
     */
    private static ImageOptions COURSE_IMAGE_OPTIONS = null;

    static {
        CLASSIFY_IMAGE_OPTIONS = new ImageOptions.Builder()
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setCrop(false)
                .setConfig(Bitmap.Config.ARGB_8888)
                .setLoadingDrawableId(R.drawable.img_def)//加载中默认显示图片
                .setFailureDrawableId(R.drawable.img_def)//加载失败后默认显示图片
                .build();

        DEFAULT_IMAGE_OPTIONS = new ImageOptions.Builder()
                .setImageScaleType(ImageView.ScaleType.FIT_XY)
                .setCrop(false)
                .setConfig(Bitmap.Config.ARGB_8888)
                .build();

        COURSE_IMAGE_OPTIONS = new ImageOptions.Builder()
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setCrop(false)
                .setLoadingDrawableId(R.drawable.default_cover_h)//加载中默认显示图片
                .setFailureDrawableId(R.drawable.default_cover_h)//加载失败后默认显示图片
                .build();

        NORMAL_IMAGE_OPTIONS = new ImageOptions.Builder()
                .setImageScaleType(ImageView.ScaleType.FIT_XY)
                .setCrop(false)
                .setConfig(Bitmap.Config.ARGB_8888)
                .build();

        CROP_IMAGE_OPTIONS = new ImageOptions.Builder()
                .setImageScaleType(ImageView.ScaleType.FIT_XY)
                .setCrop(true)
                .setCircular(true)
                .setConfig(Bitmap.Config.ARGB_8888)
                .build();
    }

    /**
     * 填充图片
     * @param view 图片控件
     * @param url 图片资源地址
     */
    public static void fillClassifyIcon(@NonNull ImageView view,String url) {
        if(verificationUrl(url)){
            x.image().bind(view,url,CLASSIFY_IMAGE_OPTIONS);
        }
    }

    public static void fillCourseIcon(@NonNull ImageView view,String url) {
        if(verificationUrl(url)){
            x.image().bind(view,url,COURSE_IMAGE_OPTIONS);
        }
    }

    /**
     * 填充默认设置图片,没有placeholder，errorholder
     * @param view 显示的View
     * @param url 图片资源地址
     */
    public static void fillDefaultView(@NonNull ImageView view,String url) {
        if(verificationUrl(url)){
            // 每一次都实例化OPTIONS 避免缓存
            ImageOptions DEFAULT_IMAGE_OPTIONS = new ImageOptions.Builder()
                    .setImageScaleType(ImageView.ScaleType.FIT_XY)
                    .setCrop(false)
                    .setConfig(Bitmap.Config.ARGB_8888)
                    .build();
            x.image().bind(view,url,DEFAULT_IMAGE_OPTIONS);
        }
    }

    /**
     * 填充默认设置图片,没有placeholder，errorholder
     * @param view 显示的View
     * @param url 图片资源地址
     */
    public static void fillNotificationView(@NonNull ImageView view,String url) {
        if(verificationUrl(url)){
            // 每一次都实例化OPTIONS 避免缓存
            ImageOptions DEFAULT_IMAGE_OPTIONS = new ImageOptions.Builder()
                    .setImageScaleType(ImageView.ScaleType.FIT_XY)
                    .setCrop(false)
                    .setConfig(Bitmap.Config.ARGB_8888)
                    .setLoadingDrawableId(R.drawable.img_def)//加载中默认显示图片
                    .setFailureDrawableId(R.drawable.img_def)//加载失败后默认显示图片
                    .build();
            x.image().bind(view,url,DEFAULT_IMAGE_OPTIONS);
        }
    }

    /**
     * 填充默认设置图片,没有placeholder，errorholder
     * @param view 显示的View
     * @param url 图片资源地址
     */
    public static void fillNormalView(@NonNull ImageView view,String url) {
        if(verificationUrl(url)){
            x.image().bind(view,url,NORMAL_IMAGE_OPTIONS);
        }
    }

    /**
     * 填充默认设置图片,没有placeholder，errorholder
     * @param view 显示的View
     * @param url 图片资源地址
     */
    public static void fillCircleView(@NonNull ImageView view,String url) {
        if(verificationUrl(url)){
            x.image().bind(view,url,CROP_IMAGE_OPTIONS);
        }
    }

    /**
     * 填充默认设置图片,没有placeholder，errorholder
     * @param view 显示的View
     * @param url 图片资源地址
     */
    public static void fillUserAvatar(@NonNull ImageView view,String url,@DrawableRes int defaultDrawableId) {
        if(verificationUrl(url)){
            ImageOptions options = new ImageOptions.Builder()
                    .setImageScaleType(ImageView.ScaleType.FIT_XY)
                    .setCrop(true)
                    .setCircular(true)
                    .setFailureDrawableId(defaultDrawableId)
                    .setConfig(Bitmap.Config.ARGB_8888)
                    .build();
            x.image().bind(view,url,options);
        }
    }

    /**
     * 填充默认设置图片,没有placeholder，errorholder
     * @param view 显示的View
     * @param url 图片资源地址
     * @param defaultDrawableId 默认显示的图片Id
     */
    public static void fillCircleView(@NonNull ImageView view, String url, @DrawableRes int defaultDrawableId) {
        if(verificationUrl(url)){
            // XUtils 默认调用了ImageView setScaleType
            LQImageLoader.DIOptBuiderParam param = new LQImageLoader.DIOptBuiderParam();
            param.mIsCacheInMemory = true;
            param.mDefaultIcon = defaultDrawableId;
            param.mHighQuality = false;
            LQImageLoader.displayImage(url, view, param);
        }
    }

    /**
     * 验证图片Url是否合法
     * @param url 图片url
     * @return true 合法
     */
    public static boolean verificationUrl(String url){
        return !EmptyUtil.isEmpty(url);
    }

}
