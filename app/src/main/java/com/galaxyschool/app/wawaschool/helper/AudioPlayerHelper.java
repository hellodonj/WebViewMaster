package com.galaxyschool.app.wawaschool.helper;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import com.galaxyschool.app.wawaschool.common.CallbackListener;
import com.osastudio.common.utils.LogUtils;
import java.io.IOException;

/**
 * ======================================================
 * Created by : Brave_Qu on 2018/9/13 0013 16:51
 * Describe:音频播放辅助类
 * ======================================================
 */
public class AudioPlayerHelper {
    private Context context;
    private MediaPlayer mediaPlayer = null;
    private String playUrl;
    private CallbackListener callbackListener;
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            return false;
        }
    });

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            releaseAudio();
            if (callbackListener != null){
                callbackListener.onBack(true);
            }
            handler.removeCallbacks(runnable);
        }
    };

    public AudioPlayerHelper(Context context) {
        this.context = context;
    }

    public AudioPlayerHelper setPlayUrl(String playUrl) {
        this.playUrl = playUrl;
        return this;
    }

    public void setCompleteListener(CallbackListener callbackListener){
        this.callbackListener = callbackListener;
    }

    public void play() {
        startPlay(-1,0);
    }

    public void seekTo(int startPosition,int endPosition) {
        startPlay(startPosition,endPosition);
    }

    private void startPlay(final int startPosition, final int endPosition) {
        if (TextUtils.isEmpty(playUrl)) {
            return;
        }

        if (mediaPlayer == null){
            mediaPlayer = new MediaPlayer();
        }
        try {
            mediaPlayer.setDataSource(playUrl);
            mediaPlayer.prepareAsync();
            //为播放器注册
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                public void onPrepared(MediaPlayer mp) {
                    // TODO Auto-generated method stub
                    if (startPosition >= 0) {
                        mediaPlayer.seekTo(startPosition);
                        if (endPosition > 0){
                            handler.postDelayed(runnable,endPosition - startPosition);
                        }
                    }
                    mediaPlayer.start();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            public void onCompletion(MediaPlayer mp) {
                //延迟200ms，确保播放结束进度条更新到最右端
                if (mediaPlayer != null) {
                    mediaPlayer.release();
                    mediaPlayer = null;
                }
                if (callbackListener != null){
                    callbackListener.onBack(true);
                }
            }
        });
    }

    public void releaseAudio() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            handler.removeCallbacks(runnable);
            mediaPlayer = null;
        }
    }
}
