package com.lecloud.skin.ui.utils;

/**
 * ================================================
 * 作    者：Blizzard-liu
 * 版    本：1.0
 * 创建日期：2017/5/27 10:43
 * 描    述：对播放器进行一般的设置操作
 * 修订历史：
 * ================================================
 */

public class VodVideoSettingUtil {

    private boolean isTablet;//平板为true
    private boolean isNewUi ;//点播新UI
    private boolean isHideBtnMore ;//隐藏收藏pop
    private int[] colorArray;//进度条颜色
    public static final int VIDEO_TYPE = 0;//视频
    public static final int AUDIO_TYPE = 1;//音频
    private int mediaType = VIDEO_TYPE;

    private VodVideoSettingUtil() {

    }
    private static class HolderClass {
        private static final VodVideoSettingUtil instance = new VodVideoSettingUtil();
    }

    public static VodVideoSettingUtil getInstance() {

        return HolderClass.instance;
    }
    /**
     * 是否为平板,默认false
     * @param isTablet
     */
    public VodVideoSettingUtil setTablet(Boolean isTablet) {
        this.isTablet = isTablet;
        return this;
    }

    /**
     * 使用新UI 右上角显示更多按钮,平板右下角隐藏全屏按钮,默认false
     * @param isNewUi
     */
    public VodVideoSettingUtil setNewUI(Boolean isNewUi) {
        this.isNewUi = isNewUi;
        return this;
    }

    /**
     * 设置进度条颜色
     * @param color1 背景色
     * @param color2 第一进度条颜色
     * @param color3 第二进度条颜色
     */
    public VodVideoSettingUtil setSeekBarColor(int color1, int color2, int color3) {

        colorArray = new int[]{color1,color2,color3};

        return this;
    }

    public VodVideoSettingUtil setMediaType(int mediaType) {
        this.mediaType = mediaType;
        return this;
    }

    public VodVideoSettingUtil setHideBtnMore(boolean hideBtnMore) {
        isHideBtnMore = hideBtnMore;
        return this;
    }

    public int getMediaType() {
        return mediaType;
    }

    public boolean isTablet() {
        return isTablet;
    }

    public boolean isNewUi() {
        return isNewUi;
    }

    public boolean isHideBtnMore() {
        return isHideBtnMore;
    }


    public int[] getColorArray() {
        return colorArray;
    }

    public void clear() {
        isTablet = false;
        isNewUi = false;
        colorArray = null;
        isHideBtnMore = false;
        mediaType = VIDEO_TYPE;
    }


}
