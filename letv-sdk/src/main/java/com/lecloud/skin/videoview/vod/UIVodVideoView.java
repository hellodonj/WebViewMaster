package com.lecloud.skin.videoview.vod;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.playerlibrelease.R;
import com.lecloud.sdk.api.md.entity.vod.VideoHolder;
import com.lecloud.sdk.constant.PlayerEvent;
import com.lecloud.sdk.constant.PlayerParams;
import com.lecloud.sdk.constant.StatusCode;
import com.lecloud.sdk.player.IAdPlayer;
import com.lecloud.sdk.player.IMediaDataPlayer;
import com.lecloud.sdk.player.IPlayer;
import com.lecloud.sdk.surfaceview.ISurfaceView;
import com.lecloud.sdk.utils.NetworkUtils;
import com.lecloud.sdk.videoview.vod.VodVideoView;
import com.lecloud.skin.ui.ILetvVodUICon;
import com.lecloud.skin.ui.LetvVodUIListener;
import com.lecloud.skin.ui.impl.LetvUICon;
import com.lecloud.skin.ui.impl.LetvVodUICon;
import com.lecloud.skin.ui.utils.ScreenUtils;
import com.lecloud.skin.ui.utils.VodVideoSettingUtil;
import com.lecloud.skin.ui.utils.timer.IChange;
import com.lecloud.skin.ui.utils.timer.LeTimerManager;
import com.lecloud.skin.ui.view.V4TopTitleView;
import com.lecloud.skin.ui.view.VideoNoticeView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class UIVodVideoView extends VodVideoView {
    public static final String TAG = "UIVodVideoView";

    private static final String PREF_SEEK_INFO = "seek_info";

    protected ILetvVodUICon letvVodUICon;
    protected LetvVodUICon letvIcon;
    protected  LetvUICon uicon;
    //    protected int width = -1;
//    protected int height = -1;
    protected LeTimerManager leTimerManager;
    TextView timeTextView;
    private long lastPosition;
    private LinkedHashMap<String, String> vtypeList;
    //是否正在seeking
    private boolean isSeeking = false;
    private String titleSet = null;
    private boolean isConfigurationCanBeChanged=true;

    private FullScreenPlay listener;
    private boolean tempData=false;
    private boolean flag=false;

    private boolean isNeedTopTitle=true;
    private ISurfaceView surfaceView;
    private boolean isTablet;
    private boolean isNewUi;

    protected void setVideoView(ISurfaceView surfaceView) {
        this.surfaceView = surfaceView;
        super.setVideoView(surfaceView);
    }
    public ISurfaceView getSurfaceView() {
        return surfaceView;
    }

    public void setTitle(String title){
        titleSet = title;
        letvVodUICon.setTitle(titleSet);
    }
    public UIVodVideoView(Context context,boolean isConfigurationCanBeChanged,FullScreenPlay listener) {
        super(context);
        this.isConfigurationCanBeChanged=isConfigurationCanBeChanged;
        this.listener=listener;
        initUICon(context);
        getLeTimerManager(500);
    }
    public UIVodVideoView(Context context,boolean isConfigurationCanBeChanged) {
        super(context);
        this.isConfigurationCanBeChanged=isConfigurationCanBeChanged;
        initUICon(context);
        getLeTimerManager(500);
        initNewUI();
    }
    public UIVodVideoView(Context context ) {
        super(context);
        initUICon(context);
        getLeTimerManager(500);
        initNewUI();

    }
    public UIVodVideoView(Context context,boolean isNeedTopTitle,boolean fromWhere ) {
        super(context);
        this.isNeedTopTitle=isNeedTopTitle;
        initUICon(context);
        getLeTimerManager(500);
        initNewUI();

    }

    public LeTimerManager getLeTimerManager(long delaymillts) {
        if (leTimerManager == null) {
            leTimerManager = new LeTimerManager(new IChange() {

                @Override
                public void onChange() {
                    if (letvVodUICon != null && player != null) {
                        post(new Runnable() {

                            @Override
                            public void run() {
                                letvVodUICon.setDuration(player.getDuration());
                                Log.d("meng", "isSeeking" + isSeeking);
                                if (!isSeeking && player.getCurrentPosition() <= player.getDuration()) {
                                    letvVodUICon.setCurrentPosition(player.getCurrentPosition());
                                }
                                letvVodUICon.setPlayState(player.isPlaying());
                            }
                        });
                    }
                }
            }, delaymillts);
        }
        return leTimerManager;
    }

    private void stopTimer() {
        if (leTimerManager != null) {
            leTimerManager.stop();
            leTimerManager = null;
        }
    }

    private void startTimer() {
        if (leTimerManager == null) {
            getLeTimerManager(500);
        }
        if (leTimerManager != null) {
            leTimerManager.start();
        }
    }

    private void initUICon(final Context context) {

        letvVodUICon = new LetvVodUICon(context);
        this.letvIcon= (LetvVodUICon) letvVodUICon;
        this.uicon= (LetvUICon) letvVodUICon;
        if(!isConfigurationCanBeChanged){
            letvVodUICon.setGravitySensor(false);
            letvIcon.setTitleVisible(false);
            uicon.setIsFromPingBan(true,true);
        }else{
            if (isNeedTopTitle){
                letvIcon.setTitleVisible(true);
                uicon.setIsNeedTopTitle(true);
            }
            letvVodUICon.setGravitySensor(false);
        }
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addView(letvVodUICon.getView(), params);
        letvVodUICon.setRePlayListener(new VideoNoticeView.IReplayListener() {

            @Override
            public Bundle getReportParams() {
                return ((IMediaDataPlayer) player).getReportParams();
            }

            @Override
            public void onRePlay() {
                setLastPostion();
                player.retry();
            }

            @Override
            public void continuePlay() {
                ((Activity)context).finish();
            }

            @Override
            public void showViewText(VideoNoticeView view,int status) {
                TextView tvContinueOrBack= (TextView) view.findViewById(R.id.btn_error_report);
                if (tvContinueOrBack!=null){
                    tvContinueOrBack.setText(context.getString(R.string.le_back));
                }
            }

            @Override
            public void endAirclassOnline() {

            }
        });
        letvVodUICon.setLetvVodUIListener(new LetvVodUIListener() {

            @Override
            public void setRequestedOrientation(int requestedOrientation) {
                if(isConfigurationCanBeChanged){
                    if (context instanceof Activity) {
                        ((Activity) context).setRequestedOrientation(requestedOrientation);
                    }
                }else {
                    if (listener!=null) {
                        //平板会走此方法 listener回调设置屏幕的大小 tempData表示对返回键界面的跳转处理
                       listener.setFullScreenPlay(tempData);
                       tempData=false;
                        if (uicon!=null){
                            uicon.setIsFromPingBan(true,flag);
                            flag=!flag;
                            letvIcon.setTitleVisible(flag);
                            letvIcon.setCurrentFullBtnStatus(!flag);
                        }
                    }
                }
            }

            @Override
            public void resetPlay() {
                // LeLog.dPrint(TAG, "--------resetPlay");
            }

            @Override
            public void onSetDefination(int positon) {
//                UIVodVideoView.super.onInterceptMediaDataSuccess(event, bundle);
                stopTimer();
                letvVodUICon.showLoadingProgress();
                if (vtypeList != null && vtypeList.size() > 0) {
                    setLastPostion();
                    ((IMediaDataPlayer) player).setDataSourceByRate(new ArrayList<String>(vtypeList.keySet()).get(positon));
                }
            }

            @Override
            public void onSeekTo(float sec) {
                Log.d("meng", "onSeekTo" + isSeeking);
                long msec = (long) Math.floor((sec * player.getDuration()));
                if (player != null) {
                    player.seekTo(msec);
                    if (isComplete()) {
                        player.retry();
                    } else if (!player.isPlaying()) {
                        player.start();
                    }
                    ((LetvVodUICon) letvVodUICon).syncSeekProgress((int) msec);
                }
            }

            @Override
            public void onClickPlay() {
                if (player.isPlaying()) {
                    player.pause();
                    releaseAudioFocus();
                } else if (isComplete()) {
                    player.seekTo(0);
                    player.retry();
                } else {
                    requestAudioFocus();
                    player.start();
                }
            }

            @Override
            public void onUIEvent(int event, Bundle bundle) {
                // TODO Auto-generated method stub

            }

            @Override
            public int onSwitchPanoControllMode(int controllMode) {
                return switchControllMode(controllMode);
            }

            @Override
            public int onSwitchPanoDisplayMode(int displayMode) {
                return switchDisplayMode(displayMode);
            }

            @Override
            public void onProgressChanged(int progress) {
                // TODO Auto-generated method stub
//				((LetvVodUICon) letvVodUICon).syncSeekProgress(progress);
            }

            @Override
            public void onBackPress(boolean flag) {
                tempData=flag;
            }

            @Override
            public void onEndOnline() {

            }

            @Override
            public void onStartSeek() {
                // TODO Auto-generated method stub
                isSeeking = true;
                Log.d("meng", "startSeek" + isSeeking);
            }

            @Override
            public void onEndSeek() {
                isSeeking = false;
            }

        });
    }

    protected int switchControllMode(int interactiveMode) {
        return -1;
    }

    protected int switchDisplayMode(int displayMode) {
        return -1;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        //平板不支持横竖屏的切换
        if (isConfigurationCanBeChanged) {
            if (ScreenUtils.getOrientation(getContext()) == Configuration.ORIENTATION_PORTRAIT) {
                letvVodUICon.setRequestedOrientation(ILetvVodUICon.SCREEN_PORTRAIT, UIVodVideoView.this);
            } else {
                letvVodUICon.setRequestedOrientation(ILetvVodUICon.SCREEN_LANDSCAPE, UIVodVideoView.this);
            }
        }
        super.onConfigurationChanged(newConfig);
    }
    //    public static RelativeLayout.LayoutParams computeContainerSize(Context context, int mWidth,
//                                                                   int mHeight) {
//        int width = GetDeviceInfo.getScreenWidth(context);
//        int height = width * mHeight / mWidth;
//        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
//                RelativeLayout.LayoutParams.MATCH_PARENT,
//                RelativeLayout.LayoutParams.MATCH_PARENT);
//        params.width = width;
//        params.height = height;
//        params.addRule(RelativeLayout.CENTER_IN_PARENT);
//        return params;
//    }
    @Override
    protected void onInterceptVodMediaDataSuccess(int event, Bundle bundle) {
        super.onInterceptVodMediaDataSuccess(event, bundle);
        VideoHolder videoHolder = bundle.getParcelable(PlayerParams.KEY_RESULT_DATA);
        vtypeList = videoHolder.getVtypes();
        String title = videoHolder.getTitle();
        if(titleSet != null){
            letvVodUICon.setTitle(titleSet);
        }else if (!TextUtils.isEmpty(title)) {
            letvVodUICon.setTitle(title);
        }
        String currentDefiniton = vtypeList.get(onInterceptSelectDefiniton(vtypeList, videoHolder.getDefaultVtype()));
        List<String> ratetypes = new ArrayList<String>(videoHolder.getVtypes().values());
        letvVodUICon.setRateTypeItems(ratetypes, currentDefiniton);
        letvVodUICon.showWaterMark(((VideoHolder) bundle.getParcelable(PlayerParams.KEY_RESULT_DATA)).getCoverConfig());
    }

    @Override
    protected void onInterceptMediaDataError(int event, Bundle bundle) {
        super.onInterceptMediaDataError(event, bundle);
        letvVodUICon.hideLoading();
        letvVodUICon.hideWaterMark();
        letvVodUICon.processMediaState(event, bundle);
    }


    @Override
    protected void notifyPlayerEvent(int event, Bundle bundle) {
        super.notifyPlayerEvent(event, bundle);
        letvVodUICon.processPlayerState(event, bundle);

        switch (event) {
            case PlayerEvent.PLAY_COMPLETION://202
                //btn pause
                letvVodUICon.setPlayState(false);
                //update progress
                if (letvVodUICon != null && player != null) {
                    letvVodUICon.setDuration(player.getDuration());
                    letvVodUICon.setCurrentPosition(player.getCurrentPosition());
                }
                lastPosition = 0;

                stopTimer();
                break;
            case PlayerEvent.PLAY_INFO://206
                int code = bundle.getInt(PlayerParams.KEY_RESULT_STATUS_CODE);
                if (code == StatusCode.PLAY_INFO_VIDEO_RENDERING_START) {
                    startTimer();
                }

                switch (code) {
                    case StatusCode.PLAY_INFO_BUFFERING_START://500004
                        if (NetworkUtils.hasConnect(context) && !letvVodUICon.isShowLoading()) {
                            letvVodUICon.showLoadingProgress();
                        }
                        break;
                    case StatusCode.PLAY_INFO_BUFFERING_END://500005
                        letvVodUICon.hideLoading();
                        break;
                    case StatusCode.PLAY_INFO_VIDEO_RENDERING_START://500006
                        letvVodUICon.showWaterMark();
                        letvVodUICon.hideLoading();
                        break;
                    default:
                        break;
                }
                break;
            case PlayerEvent.PLAY_PREPARED: {//208
                if (NetworkUtils.hasConnect(context) && !letvVodUICon.isShowLoading()) {
                    letvVodUICon.showLoadingProgress();
                }

                if (lastPosition > 0) {
                    player.seekToLastPostion(lastPosition);
                    letvVodUICon.setDuration(player.getDuration());
                    letvVodUICon.setCurrentPosition(lastPosition);
                }

                if (VodVideoSettingUtil.getInstance().getMediaType() == VodVideoSettingUtil.AUDIO_TYPE) {
                    startTimer();
                }
                break;
            }
            case PlayerEvent.PLAY_SEEK_COMPLETE: {//209
                setLastPostion();
                isSeeking = false;
//                Log.e("meng", "PlayerEvent.PLAY_SEEK_COMPLETE: " + lastPosition);
                break;
            }
            case PlayerEvent.PLAY_ERROR://205
                removeView(timeTextView);
                letvVodUICon.getView().setVisibility(VISIBLE);
                letvVodUICon.hideLoading();
                letvVodUICon.hideWaterMark();
                break;
            case PlayerEvent.PLAY_BUFFERING://201 点播时候二级进度条缓冲回调事件
                int bufferPercent = bundle.getInt(PlayerParams.KEY_PLAY_BUFFERPERCENT);
                setBufferPercentage(bufferPercent);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onInterceptAdEvent(int code, Bundle bundle) {

        switch (code) {
            case PlayerEvent.AD_START:
                letvVodUICon.getView().setVisibility(GONE);
                letvVodUICon.hideLoading();
                LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                if (timeTextView == null) {
                    timeTextView = new TextView(context);
                    timeTextView.setBackgroundColor(Color.BLACK);
                    timeTextView.setAlpha(0.7f);
                    timeTextView.setTextColor(Color.WHITE);
                    timeTextView.setPadding(20, 20, 20, 20);
                }
                if (!timeTextView.isShown()) {
                    addView(timeTextView, lp);
                    bringChildToFront(timeTextView);
                }
                break;
            case PlayerEvent.AD_ERROR:
                if (!NetworkUtils.hasConnect(context)){
                    letvVodUICon.processPlayerState(code, bundle);
                }
            case IAdPlayer.AD_PLAY_ERROR:
            case PlayerEvent.AD_COMPLETE:
                removeView(timeTextView);
                letvVodUICon.getView().setVisibility(VISIBLE);
//                by heyuekuai 2016/09/19 fix bug ad play complete not show loading pic
//                letvVodUICon.hideLoading();
//                letvVodUICon.hideWaterMark();
                break;
            case PlayerEvent.AD_PROGRESS:
                timeTextView.setText(getContext().getResources().getString(R.string.ad) + bundle.getInt(IAdPlayer.AD_TIME) + "s");
                break;

            default:
                break;
        }
        super.onInterceptAdEvent(code, bundle);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopTimer();
    }

    @Override
    public void onPause() {
        super.onPause();
        setLastPostion();
    }


    @Override
    public void onResume() {
        //横屏设置ILetvVodUICon.SCREEN_LANDSCAPE,竖屏设置ILetvVodUICon.SCREEN_PORTRAIT
        if (isNewUi) {
            if (isTablet) {
                letvVodUICon.setRequestedOrientation(ILetvVodUICon.SCREEN_LANDSCAPE, this);

            }else{
                letvVodUICon.setRequestedOrientation(ILetvVodUICon.SCREEN_PORTRAIT, this);
            }
        } else {
            if (isConfigurationCanBeChanged) {
                if (isFirstPlay) {
                    letvVodUICon.setRequestedOrientation(ILetvVodUICon.SCREEN_PORTRAIT, this);
                    isFirstPlay = false;
                }
            }

        }

        super.onResume();
    }

    public boolean isComplete() {
        //TODO
        return player != null && player.getStatus() == IPlayer.PLAYER_STATUS_EOS;
    }

    private void setLastPostion() {
        if(player == null || player.getCurrentPosition()==0){
            return;
        }
        lastPosition = player.getCurrentPosition();
    }

    @Override
    public void resetPlayer() {
        super.resetPlayer();
        stopTimer();
        if (timeTextView != null) {
            timeTextView.setText("");
            removeView(timeTextView);
        }

        lastPosition = 0;
        vtypeList = null;
        isSeeking = false;
    }
    public interface FullScreenPlay{
        void setFullScreenPlay(boolean isBack);
    }


    /**
     * 设置二级进度条
     * @param bufferPercentage
     */
    public void setBufferPercentage(long bufferPercentage) {
        if (letvIcon != null) {

            letvIcon.setBufferPercentage(bufferPercentage * 10);
        }
    }


    private void initNewUI() {

        this.isTablet = VodVideoSettingUtil.getInstance().isTablet();
        this.isNewUi = VodVideoSettingUtil.getInstance().isNewUi();
        if (!VodVideoSettingUtil.getInstance().isHideBtnMore()) {//个人资源库不显示收藏

            letvIcon.showTopRightMoreButton(isNewUi);
        }
        letvIcon.showFullBtn(!isTablet);
    }

    public void setButtonMoreListener(V4TopTitleView.OnButtonMoreListener moreListener) {
        letvIcon.setButtonMoreListener(moreListener);
    }






    public void onClickContinuePlay() {
        if (player.isPlaying()) {
            player.pause();
            releaseAudioFocus();
        } else if (isComplete()) {
            player.seekTo(0);
            player.retry();
        } else {
            requestAudioFocus();
            player.start();
        }
    }


}




