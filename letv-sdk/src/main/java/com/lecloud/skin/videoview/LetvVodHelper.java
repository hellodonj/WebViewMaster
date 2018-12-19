package com.lecloud.skin.videoview;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.lecloud.sdk.constant.PlayerParams;
import com.lecloud.skin.ui.utils.VodVideoSettingUtil;

/**
 * ================================================
 * 作    者：Blizzard-liu
 * 版    本：1.0
 * 创建日期：2017/5/27 17:22
 * 描    述：点播视频帮助类,使用Builder模式，链式调用
 * 修订历史：
 * ================================================
 */
public class LetvVodHelper {

   private String url;
   private String title;
   private String vuid;
   private String uuid;

   private Activity activity;


   private LetvVodHelper(Activity activity) {
       this.activity = activity;
   }

   public static class VodVideoBuilder{
       private LetvVodHelper mLetvVodHelper;
       private VodVideoSettingUtil mVodVideoSettingUtil;

       public VodVideoBuilder(Activity activity){
           mLetvVodHelper = new LetvVodHelper(activity);
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

       /**
        * 隐藏收藏弹窗
        * @param hideBtnMore
        * @return
        */
       public VodVideoBuilder setHideBtnMore(boolean hideBtnMore) {
           mVodVideoSettingUtil.setHideBtnMore(hideBtnMore);
           return this;
       }

       public LetvVodHelper create() {
           mLetvVodHelper.build();
           return mLetvVodHelper;
       }

   }

   private void build() {
       Intent intent = new Intent(activity, VideoPlayActivity.class);
       Bundle mBundle = new Bundle();
       if (TextUtils.isEmpty(url)) {
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
       } else {
           mBundle.putString(VideoPlayActivity.KEY_PLAY_PATH, url);
       }


       mBundle.putString(VideoPlayActivity.KEY_TITLE, title);
     

       intent.putExtra(VideoPlayActivity.DATA, mBundle);
       activity.startActivity(intent);
   }


}
