package com.lecloud.skin.videoview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.playerlibrelease.R;
import com.lecloud.sdk.constant.PlayerEvent;
import com.lecloud.sdk.constant.PlayerParams;
import com.lecloud.sdk.surfaceview.ISurfaceView;
import com.lecloud.sdk.surfaceview.impl.BaseSurfaceView;
import com.lecloud.sdk.videoview.VideoViewListener;
import com.lecloud.skin.ui.utils.ScreenUtils;
import com.lecloud.skin.ui.utils.VodVideoSettingUtil;
import com.lecloud.skin.ui.view.V4TopTitleView;
import com.lecloud.skin.videoview.vod.UIVodVideoView;

import java.util.LinkedHashMap;
import java.util.Map;



/**
  * ================================================
  * 作    者：Blizzard-liu
  * 版    本：1.0
  * 创建日期：2017/5/27 11:06
  * 描    述：点播音视频播放界面,支持声音,亮度,进度调节,增加右上角收藏弹出框
             需配合LetvVodHelper类使用:eg:
                                        new LetvVodHelper.VodVideoBuilder(getActivity())
                                        .setNewUI(true)//使用自定义UI
                                        .setTablet(true)//平板为true
                                        .setTitle(data.getTitle())//视频标题
                                        .setUrl(filePath)//路径
                                        .setMediaType(VodVideoSettingUtil.AUDIO_TYPE)//设置媒体类型
                                        .create();
              播放方式1:传入url
              播放方式2:设置vuid,uuid
  * 修订历史：
  * ================================================
  */
public class VideoPlayActivity extends Activity implements UIVodVideoView
        .FullScreenPlay, V4TopTitleView.OnButtonMoreListener {
    public final static String DATA = "data";
    public static final String KEY_PLAY_PATH = "PLAY_PATH";// Url可以是在线视频，也可以是本地视频
    public static final String KEY_TITLE = "TITLE";
    public static final String KEY_RESOURCETYPE = "resourceType";
    public static final String KEY_RESID = "resId";
    public static final String KEY_AUTHORID = "authorId";
    public static final String KEY_IS_PUBLIC = "isPublic";
    public static final String KEY_IS_PAYED = "isPayed";

    private UIVodVideoView videoView;

    public UIVodVideoView getVideoView() {//added by rmpan for actor school
        return videoView;
    }

    LinkedHashMap<String, String> rateMap  = new LinkedHashMap<String, String>();
    VideoViewListener mVideoViewListener = new VideoViewListener() {
        @Override
        public void onStateResult(int event, Bundle bundle) {
            handleVideoInfoEvent(event, bundle);// 处理视频信息事件
            handlePlayerEvent(event, bundle);// 处理播放器事件
            handleLiveEvent(event, bundle);// 处理直播类事件,如果是点播，则这些事件不会回调
        }

        @Override
        public String onGetVideoRateList(LinkedHashMap<String, String> map) {
            rateMap = map;
            for (Map.Entry<String, String> rates : map.entrySet()) {
                if (rates.getValue().equals("高清")) {
                    return rates.getKey();
                }
            }
            return "";
        }
    };

    private Bundle mBundle;
    private int mPlayMode;
    private ISurfaceView surfaceView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.letv_skin_vod_video_play);
        initData();
        initView();
    }

    private void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            mBundle = intent.getBundleExtra(DATA);
            if (mBundle == null) {
                Toast.makeText(this, "no data", Toast.LENGTH_LONG).show();
            } else {
                mPlayMode = mBundle.getInt(PlayerParams.KEY_PLAY_MODE, -1);

            }
        }
    }

    private void initView() {
        videoView =  new UIVodVideoView(this);
        videoView.setVideoViewListener(mVideoViewListener);
        videoView.setButtonMoreListener(this);

        final RelativeLayout videoContainer = (RelativeLayout) findViewById(R.id.videoContainer);

        videoContainer.addView((View) videoView, computeContainerFullSize(this));
        String title = mBundle.getString(KEY_TITLE,"");
        String url = mBundle.getString(KEY_PLAY_PATH,"");
        videoView.setTitle(title);
        if (TextUtils.isEmpty(url)) {
            videoView.setDataSource(mBundle);
        } else {
            videoView.setDataSource(url);

        }


    }


    @Override
    protected void onResume() {
        super.onResume();
        if (videoView != null) {
            videoView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (videoView != null) {
            videoView.onPause();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (videoView != null) {
            videoView.onDestroy();
        }
        VodVideoSettingUtil.getInstance().clear();

    }


    /**
     * 处理播放器本身事件，具体事件可以参见IPlayer类
     */
    private void handlePlayerEvent(int state, Bundle bundle) {

        switch (state) {
            case PlayerEvent.ACTION_LIVE_PLAY_PROTOCOL:
                setActionLiveParameter(bundle.getBoolean(PlayerParams.KEY_PLAY_USEHLS));
                break;
            case PlayerEvent.PLAY_VIDEOSIZE_CHANGED:
                /**
                 播放器返回视频的宽、高
                 根据返回视频的宽、高计算出Surfaceview的大小(按比例缩放)
                 */
                if (videoView != null && videoView instanceof UIVodVideoView) {
                    /**
                     需要在UIVodVideoView中添加如下代码
                     private ISurfaceView surfaceView;
                     protected void setVideoView(ISurfaceView surfaceView) {
                     this.surfaceView = surfaceView;
                     super.setVideoView(surfaceView);
                     }
                     public ISurfaceView getSurfaceView() {
                     return surfaceView;
                     }
                     */
                    surfaceView = ((UIVodVideoView) videoView).getSurfaceView();
                    ((BaseSurfaceView) surfaceView).setDisplayMode(BaseSurfaceView.DISPLAY_MODE_SCALE_ZOOM);
                    int width = bundle.getInt(PlayerParams.KEY_WIDTH);
                    int height = bundle.getInt(PlayerParams.KEY_HEIGHT);
                    ((BaseSurfaceView) surfaceView).onVideoSizeChanged(width, height);
                }
                break;


            case PlayerEvent.PLAY_PREPARED:
                // 播放器准备完成，此刻调用start()就可以进行播放了

                if (videoView != null) {

                    videoView.onStart();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 处理直播类事件
     */
    private void handleLiveEvent(int state, Bundle bundle) {
    }

    /**
     * 处理视频信息类事件
     */
    private void handleVideoInfoEvent(int state, Bundle bundle) {
    }

    private void setActionLiveParameter(boolean hls) {
        if (hls) {
            videoView.setCacheWatermark(1000, 100);
            videoView.setMaxDelayTime(50000);
            videoView.setCachePreSize(1000);
            videoView.setCacheMaxSize(40000);
        } else {
            //rtmp
            videoView.setCacheWatermark(500, 100);
            videoView.setMaxDelayTime(1000);
            videoView.setCachePreSize(200);
            videoView.setCacheMaxSize(10000);
        }
    }

    @Override
    public void setFullScreenPlay(boolean isBack) {
        if (isBack) {
            finish();
        }
    }

    /**
     * 点击右上角更多按钮
     * @param view
     */
     @Override
     public void showPopwindow(View view) {

     }


    private  RelativeLayout.LayoutParams computeContainerSize(Context context, int mWidth, int mHeight) {
        int width = ScreenUtils.getWidth(context);
        int height = width * mHeight / mWidth;
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        params.width = width;
        params.height = height;
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        return params;
    }

    private  RelativeLayout.LayoutParams computeContainerFullSize(Context context) {
        int width = ScreenUtils.getWidth(context);
        int height = ScreenUtils.getHeight(context);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout
                .LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        params.width = width;
        params.height = height;
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        return params;
    }


 }

