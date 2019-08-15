package com.lqwawa.intleducation.common.utils;

import android.graphics.Bitmap;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.ImageView;

import com.lqwawa.intleducation.R;
import com.osastudio.common.utils.LQImageLoader;
import com.osastudio.common.utils.XImageLoader;

import org.xutils.http.RequestParams;
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
     * 填充图片
     *
     * @param view 图片控件
     * @param url  图片资源地址
     */
    public static void fillClassifyIcon(@NonNull ImageView view, String url) {
        if (verificationUrl(url)) {
            ImageOptions imageOptions =
                    XImageLoader.buildImageOptions(ImageView.ScaleType.CENTER_CROP,
                            R.drawable.img_def, false, false, Bitmap.Config.ARGB_8888, 0, 0);
            XImageLoader.loadImage(view, url, imageOptions);
        }
    }

    public static void fillCourseIcon(@NonNull ImageView view, String url) {
        if (verificationUrl(url)) {
            ImageOptions imageOptions =
                    XImageLoader.buildImageOptions(ImageView.ScaleType.CENTER_CROP,
                            R.drawable.default_cover_h, false, false, null, 0, 0);
            XImageLoader.loadImage(view, url, imageOptions);
        }
    }

    /**
     * 填充默认设置图片,没有placeholder，errorholder
     *
     * @param view 显示的View
     * @param url  图片资源地址
     */
    public static void fillDefaultView(@NonNull ImageView view, String url) {
        if (verificationUrl(url)) {
            ImageOptions imageOptions =
                    XImageLoader.buildImageOptions(ImageView.ScaleType.FIT_XY, 0, false, false,
                            Bitmap.Config.ARGB_8888);
            XImageLoader.loadImage(view, url, imageOptions);
        }
    }

    /**
     * 填充默认设置图片,没有placeholder，errorholder
     *
     * @param view 显示的View
     * @param url  图片资源地址
     */
    public static void fillNotificationView(@NonNull ImageView view, String url) {
        if (verificationUrl(url)) {
            ImageOptions imageOptions =
                    XImageLoader.buildImageOptions(ImageView.ScaleType.FIT_XY, R.drawable.img_def, false, false,
                            Bitmap.Config.ARGB_8888);
            XImageLoader.loadImage(view, url, imageOptions);
        }
    }


    /**
     * 填充默认设置图片,没有placeholder，errorholder
     *
     * @param view 显示的View
     * @param url  图片资源地址
     */
    public static void fillNormalView(@NonNull ImageView view, String url) {
        if (verificationUrl(url)) {
            ImageOptions imageOptions =
                    XImageLoader.buildImageOptions(ImageView.ScaleType.FIT_XY, 0, false, false,
                            Bitmap.Config.ARGB_8888);
            XImageLoader.loadImage(view, url, imageOptions);
        }
    }

    /**
     * 填充LQ学程首页关于分类的相关图片
     *
     * @param view   显示的View
     * @param url    图片资源地址
     * @param width  宽度 如果传0，就加载高清图
     * @param height 高度 如果传0，就加载高清图
     */
    public static void fillClassifyDetailsView(@NonNull ImageView view, String url, int width, int height) {
        if (verificationUrl(url)) {
            ImageOptions imageOptions =
                    XImageLoader.buildImageOptions(ImageView.ScaleType.FIT_XY,
                            0, false, false, Bitmap.Config.ARGB_8888, width, height);
            XImageLoader.loadImage(view, url, imageOptions);
        }
    }

    /**
     * 填充默认设置图片,没有placeholder，errorholder
     *
     * @param view 显示的View
     * @param url  图片资源地址
     */
    public static void fillCircleView(@NonNull ImageView view, String url) {
        if (verificationUrl(url)) {
            ImageOptions imageOptions = XImageLoader.buildImageOptions(ImageView.ScaleType.FIT_XY,
                    0, true, true, Bitmap.Config.ARGB_8888, 0, 0);
            XImageLoader.loadImage(view, url, imageOptions);
        }
    }

    /**
     * 填充默认设置图片,没有placeholder，errorholder
     *
     * @param view 显示的View
     * @param url  图片资源地址
     */
    public static void fillUserAvatar(@NonNull ImageView view, String url, @DrawableRes int defaultDrawableId) {
        if (verificationUrl(url)) {
            ImageOptions imageOptions = XImageLoader.buildImageOptions(ImageView.ScaleType.FIT_XY,
                    defaultDrawableId, true, true, Bitmap.Config.ARGB_8888, 0, 0);
            XImageLoader.loadImage(view, url, imageOptions);
        }
    }

    /**
     * 填充默认设置图片,没有placeholder，errorholder
     *
     * @param view              显示的View
     * @param url               图片资源地址
     * @param defaultDrawableId 默认显示的图片Id
     */
    public static void fillCircleView(@NonNull ImageView view, String url, @DrawableRes int defaultDrawableId) {
        if (verificationUrl(url)) {
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
     *
     * @param url 图片url
     * @return true 合法
     */
    public static boolean verificationUrl(String url) {
        return !EmptyUtil.isEmpty(url);
    }

}
