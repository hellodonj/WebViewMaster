package com.osastudio.common.utils;

import android.graphics.Bitmap;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.ImageView;

import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.x;

/**
 * @desc:
 * @author: wangchao
 * @date: 2019/08/14
 */
public class XImageLoader {
    /**
     * 解决重定向builder
     */
    private static ImageOptions.ParamsBuilder PARAMS_BUILDER = null;

    static {
        PARAMS_BUILDER = (requestParams, imageOptions) -> {
            requestParams.setRedirectHandler(uriRequest -> {
                RequestParams uriRequestParams = uriRequest.getParams();
                String location = uriRequest.getResponseHeader("Location"); //协定的重定向地址
                if (!TextUtils.isEmpty(location)) {
                    uriRequestParams.setUri(location);
                }
                return uriRequestParams;
            });
            return requestParams;
        };
    }

    /**
     * @param view         显示的View
     * @param url          图片资源地址
     * @param imageOptions 加载图片配置信息
     */
    public static void loadImage(@NonNull ImageView view, String url,
                                 ImageOptions imageOptions) {
        if (imageOptions == null) {
            imageOptions =
                    buildImageOptions(ImageView.ScaleType.FIT_XY, 0, false, false,
                            Bitmap.Config.ARGB_8888);
        }
        x.image().bind(view, url, imageOptions);
    }

    /**
     * 在线图片加载入口
     * 填充默认设置图片,没有placeholder，errorholder
     *
     * @param view              显示的View
     * @param url               图片资源地址
     * @param defaultDrawableId 默认显示的图片Id
     */
    public static void loadImage(@NonNull ImageView view, String url,
                                 @DrawableRes int defaultDrawableId) {
        ImageOptions imageOptions =
                buildImageOptions(ImageView.ScaleType.FIT_XY, defaultDrawableId, false, false,
                        Bitmap.Config.ARGB_8888);
        x.image().bind(view, url, imageOptions);
    }

    public static ImageOptions buildImageOptions(ImageView.ScaleType scaleType,
                                                 @DrawableRes int defaultDrawableId,
                                                 boolean isCrop, boolean isCircular,
                                                 Bitmap.Config bitmapConfig) {
        return buildImageOptions(scaleType, defaultDrawableId, isCrop, isCircular, bitmapConfig, 0, 0);

    }

    public static ImageOptions buildImageOptions(ImageView.ScaleType scaleType,
                                                 @DrawableRes int defaultDrawableId,
                                                 boolean isCrop,
                                                 boolean isCircular,
                                                 Bitmap.Config bitmapConfig,
                                                 int width,
                                                 int height) {
        ImageOptions.Builder builder = new ImageOptions.Builder()
                .setCrop(isCrop)
                .setCircular(isCircular)
                .setLoadingDrawableId(defaultDrawableId) //加载中默认显示图片
                .setFailureDrawableId(defaultDrawableId) //加载失败默认显示图片
                .setSize(width, height)
                .setParamsBuilder(PARAMS_BUILDER);

        if (scaleType != null) {
            builder.setImageScaleType(scaleType);
        }
        if (bitmapConfig != null) {
            builder.setConfig(bitmapConfig);
        }

        return builder.build();
    }
}
