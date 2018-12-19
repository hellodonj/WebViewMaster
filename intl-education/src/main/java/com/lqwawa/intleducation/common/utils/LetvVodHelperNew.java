package com.lqwawa.intleducation.common.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.lecloud.sdk.constant.PlayerParams;
import com.lecloud.skin.ui.utils.VodVideoSettingUtil;
import com.lecloud.skin.videoview.VideoPlayActivity;
import com.libs.mediaplay.MediaPlayerActivity;

/**
 * ================================================
 * 作    者：Blizzard-liu
 * 版    本：1.0
 * 创建日期：2017/6/2 17:40
 * 描    述：
 * 修订历史：
 * ================================================
 */

public class LetvVodHelperNew {
    public static final String KEY_UUID = "b68e945493";
    private String url;
    private String title;
    private String vuid;
    private String uuid = KEY_UUID;
    private String packageName;
    private String className;
    private int LeStatus;
    ///////////////////////////////////////////////////////////////////////////
    // 收藏需传以下参数
    private String resId;
    private String authorId;
    private int resourceType;
    ///////////////////////////////////////////////////////////////////////////
    private boolean mIsPublic;//标志资源是否为公开资源
    private boolean isHideBtnMore;
    private Activity activity;


    private LetvVodHelperNew(Activity activity) {
        this.activity = activity;
    }

    public static class VodVideoBuilder{
        private LetvVodHelperNew mLetvVodHelper;
        private VodVideoSettingUtil mVodVideoSettingUtil;

        public VodVideoBuilder(Activity activity){
            mLetvVodHelper = new LetvVodHelperNew(activity);
            mVodVideoSettingUtil = VodVideoSettingUtil.getInstance();
        }
        /**
         * 是否为平板,默认false
         * @param isTablet
         */
        public VodVideoBuilder setTablet(Boolean isTablet) {
            mVodVideoSettingUtil.setTablet(isTablet);
            return this;
        }

        /**
         * 使用新UI 右上角显示更多按钮,平板右下角隐藏全屏按钮,默认false
         * @param isNewUi
         */
        public VodVideoBuilder setNewUI(Boolean isNewUi) {
            mVodVideoSettingUtil.setNewUI(isNewUi);
            return this;
        }

        /**
         * 设置进度条颜色
         * @param color1 背景色
         * @param color2 第一进度条颜色
         * @param color3 第二进度条颜色
         */
        public VodVideoBuilder setSeekBarColor(int color1, int color2, int color3) {
            mVodVideoSettingUtil.setSeekBarColor( color1,  color2, color3);
            return this;
        }


        public VodVideoBuilder setMediaType(int mediaType) {
            VodVideoSettingUtil.getInstance().setMediaType(mediaType);
            return this;
        }

        public VodVideoBuilder setResId(String resId) {
            mLetvVodHelper.resId = resId;
            return this;
        }
        public VodVideoBuilder setAuthorId(String authorId) {
            mLetvVodHelper.authorId = authorId;
            return this;
        }
        public VodVideoBuilder setResourceType(int resourceType) {
            mLetvVodHelper.resourceType = resourceType;
            return this;
        }

        public VodVideoBuilder setUrl(String url) {
            mLetvVodHelper.url = url;
            return this;
        }
        public VodVideoBuilder setTitle(String title) {
            mLetvVodHelper.title = title;
            return this;
        }
        public VodVideoBuilder setVuid(String vuid) {
            mLetvVodHelper.vuid = vuid;
            return this;
        }
        public VodVideoBuilder setUuid(String uuid) {
            mLetvVodHelper.uuid = uuid;
            return this;
        }

        public VodVideoBuilder setPackageName(String packageName) {
            mLetvVodHelper.packageName = packageName;
            return this;
        }

        public VodVideoBuilder setClassName(String className) {
            mLetvVodHelper.className = className;
            return this;
        }
        public VodVideoBuilder setLeStatus(int LeStatus) {
            mLetvVodHelper.LeStatus = LeStatus;
            return this;
        }
        public VodVideoBuilder setIsPublic(boolean isPublic){
            mLetvVodHelper.mIsPublic = isPublic;
            return this;
        }

        /**
         * 隐藏收藏弹窗
         * @param hideBtnMore
         * @return
         */
        public VodVideoBuilder setHideBtnMore(boolean hideBtnMore) {
            mLetvVodHelper.isHideBtnMore = hideBtnMore;
            mVodVideoSettingUtil.setHideBtnMore(hideBtnMore);
            return this;
        }

        public LetvVodHelperNew create() {
            mLetvVodHelper.build();
            return mLetvVodHelper;
        }

    }

    private void build() {
        Intent intent = new Intent();
        intent.setClassName(packageName,className);
        Bundle mBundle = new Bundle();

        mBundle.putString(VideoPlayActivity.KEY_TITLE, title);
        mBundle.putString(VideoPlayActivity.KEY_AUTHORID, authorId);
        mBundle.putString(VideoPlayActivity.KEY_RESID, resId);
        mBundle.putInt(VideoPlayActivity.KEY_RESOURCETYPE, resourceType);
        mBundle.putBoolean(VideoPlayActivity.KEY_IS_PUBLIC, mIsPublic);

        if (!TextUtils.isEmpty(vuid) && VodVideoSettingUtil.getInstance().getMediaType() == VodVideoSettingUtil.VIDEO_TYPE
                && LeStatus == 3) {
            mBundle.putInt(PlayerParams.KEY_PLAY_MODE, PlayerParams.VALUE_PLAYER_VOD);
            mBundle.putString(PlayerParams.KEY_PLAY_UUID, uuid);
            mBundle.putString(PlayerParams.KEY_PLAY_VUID, vuid);
            mBundle.putString(PlayerParams.KEY_PLAY_CHECK_CODE, "");
            mBundle.putString(PlayerParams.KEY_PLAY_PAYNAME, "0");
            mBundle.putString(PlayerParams.KEY_PLAY_USERKEY, "151398");
            mBundle.putString(PlayerParams.KEY_PLAY_PU, "0");
            //KEY_PLAY_USEHLS = true,表示使用hls协议播放;KEY_PLAY_USEHLS = false,表示使用rtmp协议播放;
            //默认使用rtmp协议播放
            mBundle.putBoolean(PlayerParams.KEY_PLAY_USEHLS, false);
        } else if (VodVideoSettingUtil.getInstance().getMediaType() == VodVideoSettingUtil.AUDIO_TYPE) {
            //音频播放
            mBundle.putString(VideoPlayActivity.KEY_PLAY_PATH, url);
        } else {
            //url 播放
            intent.setClassName(packageName,"com.galaxyschool.app.wawaschool.medias.activity.MediaPlayerActivity");

            mBundle.putBoolean(MediaPlayerActivity.EXTRA_ISHIDEBTNMORE, isHideBtnMore);
            mBundle.putString(MediaPlayerActivity.EXTRA_VIDEO_PATH, url);
            intent.putExtra(MediaPlayerActivity.DATA, mBundle);
            activity.startActivity(intent);
            return;
        }


        intent.putExtra(VideoPlayActivity.DATA, mBundle);
        activity.startActivity(intent);
    }
}
