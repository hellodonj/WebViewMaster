package com.galaxyschool.app.wawaschool.bitmapmanager;

import android.app.Activity;
import android.widget.ImageView;

import com.galaxyschool.app.wawaschool.R;
import com.osastudio.common.utils.LQImageLoader;

public class ThumbnailManager {

    private int mOutputWidth = 0;
    private int mOutputHeight = 0;
    private Activity mActivity = null;

    public ThumbnailManager(
        Activity activity, int thumbWidth, int thumbHeight,
        int memoryCacheLimit) {
        mOutputWidth = thumbWidth;
        mOutputHeight = thumbHeight;
        mActivity = activity;
    }


    public void displayUserIcon(String path, ImageView imageView) {
        displayUserIconWithDefault(path, imageView, R.drawable.default_avatar);
    }

    public void displayUserIconWithDefault(String path, ImageView imageView, int defaultDrawable) {
        displayThumbnailWithDefault(path, imageView, defaultDrawable, 120, 120);
    }

    public void displayThumbnail(String path, ImageView imageView) {
        displayThumbnailWithDefault(path, imageView, R.drawable.ic_launcher);
    }

    public void displayThumbnailWithDefault(String path, ImageView imageView, int defaultDrawable) {
        displayThumbnailWithDefault(path, imageView, defaultDrawable,
                LQImageLoader.OUT_WIDTH, LQImageLoader.OUT_HEIGHT);
    }

    public void displayThumbnailWithDefault(String path, ImageView imageView, int defaultDrawable,
                                            int maxWidth, int maxHeight) {
        displayImageWithDefault(path, imageView, defaultDrawable,
                maxWidth, maxHeight, false);
    }

    public void displayImage(String path, ImageView imageView) {
        displayImageWithDefault(path, imageView, R.drawable.ic_launcher);
    }

    public void displayImageWithDefault(String path, ImageView imageView, int defaultDrawable) {
        displayImageWithDefault(path, imageView, defaultDrawable,
                LQImageLoader.OUT_WIDTH, LQImageLoader.OUT_HEIGHT);
    }

    public void displayImageWithDefault(String path, ImageView imageView, int defaultDrawable,
                                        int maxWidth, int maxHeight) {
        displayImageWithDefault(path, imageView, defaultDrawable,
                maxWidth, maxHeight, true);
    }

    public void displayImageWithDefault(String path, ImageView imageView, int defaultDrawable,
                                        int maxWidth, int maxHeight, boolean highQuality) {
        LQImageLoader.DIOptBuiderParam param = new LQImageLoader.DIOptBuiderParam();
        param.mIsCacheInMemory = true;
        param.mOutWidth = maxWidth; //LQImageLoader.OUT_WIDTH;
        param.mOutHeight = maxHeight; //LQImageLoader.OUT_HEIGHT;
        param.mDefaultIcon = defaultDrawable;
        param.mHighQuality = highQuality;
        LQImageLoader.displayImage(path, imageView, param);

    }

}
