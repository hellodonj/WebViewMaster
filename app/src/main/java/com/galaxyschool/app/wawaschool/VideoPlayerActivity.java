package com.galaxyschool.app.wawaschool;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

/**
 * Created by wangchao on 12/28/15.
 */
public class VideoPlayerActivity extends BaseActivity implements View.OnClickListener, MediaPlayer.OnCompletionListener {

    public static final String EXTRA_VIDEO_PATH = "video_path";

    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_play_view_mp);

        videoView = (VideoView)findViewById(com.lqwawa.libs.mediapaper.R.id.videoview);
        View closeBtn = findViewById(com.lqwawa.libs.mediapaper.R.id.close_btn);
        closeBtn.setOnClickListener(this);

        String path = getIntent().getStringExtra(EXTRA_VIDEO_PATH);
        if(!TextUtils.isEmpty(path)) {
            initVideoView(path);
        }
    }

    private void initVideoView(String path) {
        MediaController mc = new MediaController(VideoPlayerActivity.this, false);
        mc.setMediaPlayer(videoView);
        mc.show(0);
        videoView.setMediaController(mc);
        videoView.setVideoPath(path);
        videoView.setOnCompletionListener(this);
        videoView.start();
    }

    private void stopPlay() {
        if (videoView.isPlaying()) {
            videoView.stopPlayback();
        }
        finish();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.close_btn) {
            stopPlay();
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        stopPlay();
    }
}
