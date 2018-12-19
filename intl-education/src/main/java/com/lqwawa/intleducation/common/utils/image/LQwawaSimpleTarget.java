package com.lqwawa.intleducation.common.utils.image;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bumptech.glide.request.transition.Transition;

/**
 * @author medici
 * @desc Glide加载SimpleTarget的回调定义
 */
public abstract class LQwawaSimpleTarget {

    private int width;
    private int height;

    public LQwawaSimpleTarget(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    // 回调地址
    public abstract void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition);
}
