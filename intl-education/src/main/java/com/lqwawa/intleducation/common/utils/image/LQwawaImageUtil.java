package com.lqwawa.intleducation.common.utils.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.common.utils.EmptyUtil;

/**
 * @author medici
 * @desc 图片加载工具类,使用Glide加载
 */
public class LQwawaImageUtil {

    /**
     * 最基础的网络图片加载，并且回调到target接口中
     * @param context 上下文对象
     * @param url 图片加载地址
     * @param target 目标回调
     */
    public static void loadSimpleTarget(@NonNull Context context, @NonNull String url, @NonNull LQwawaSimpleTarget target){
        if(!verificationUrl(url) || EmptyUtil.isEmpty(target)) return;
        int width = target.getWidth();
        int height = target.getHeight();
        Glide.with(context)
                .asBitmap()
                .load(url)
                .into(new SimpleTarget<Bitmap>(width,height) {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        target.onResourceReady(resource,transition);
                    }
                });
    }

    /**
     * 加载课程图片 竖向占位的缩略图
     * @param context 上下文对象
     * @param v 要显示到的VIEW
     * @param url 图片Url地址
     */
    public static void loadCourseThumbnailH(@NonNull Context context, @NonNull ImageView v, @NonNull String url){
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.default_cover_h)
                .error(R.drawable.default_cover_h)
                .fallback(R.drawable.default_cover_h);
        Glide.with(context)
                .applyDefaultRequestOptions(options)
                .load(url)
                .into(v);
    }

    /**
     * 加载课程图片 横向占位的缩略图
     * @param context 上下文对象
     * @param v 要显示到的VIEW
     * @param url 图片Url地址
     */
    public static void loadCourseThumbnail(@NonNull Context context, @NonNull ImageView v, @NonNull String url){
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.default_cover)
                .error(R.drawable.default_cover)
                .fallback(R.drawable.default_cover);
        Glide.with(context)
                .applyDefaultRequestOptions(options)
                .load(url)
                .into(v);
    }

    /**
     * 加载图片的基本方式
     * @param context 上下文对象
     * @param v 要显示到的VIEW
     * @param url 图片Url地址
     */
    public static void loadCommonIcon(@NonNull Context context, @NonNull ImageView v,@NonNull String url){
        if(!verificationUrl(url)) return;
        RequestOptions options = new RequestOptions();
        Glide.with(context)
                .applyDefaultRequestOptions(options)
                .load(url)
                .into(v);
    }

    /**
     * 加载图片的基本方式,带占位图效果
     * @param context 上下文对象
     * @param v 要显示到的VIEW
     * @param url 图片Url地址
     * @param resourceId 占位图
     */
    public static void loadCommonIcon(@NonNull Context context, @NonNull ImageView v,@NonNull String url,@NonNull int resourceId){
        RequestOptions options = new RequestOptions()
                .placeholder(resourceId)
                .error(resourceId)
                .fallback(resourceId);

        Glide.with(context)
                .applyDefaultRequestOptions(options)
                .load(url)
                .into(v);
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
