package com.lqwawa.libs.videorecorder;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;

public class VideoPlayer extends BaseActivity implements MediaPlayer.OnCompletionListener {

	public static final String TAG = VideoPlayer.class.getSimpleName();

	public static final String EXTRA_VIDEO_PATH = "videoPath";
	public static final String EXTRA_IS_LOOPING_PLAYBACK = "isLoopingPlayback";

	private String videoPath;
    private boolean isLoopingPlayback;
	private VideoView videoView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		init();
	}

	@Override
	protected int getContentViewId() {
		return R.layout.vr_video_player;
	}

	private void init() {
		if (getIntent() != null) {
			videoPath = getIntent().getStringExtra(EXTRA_VIDEO_PATH);
			isLoopingPlayback = getIntent().getBooleanExtra(EXTRA_IS_LOOPING_PLAYBACK, false);
		}
		if (TextUtils.isEmpty(videoPath)) {
			finish();
			return;
		}
		initViews();
		playVideo();
	}

	protected void initViews() {
        videoView = (VideoView) findViewById(R.id.vr_video_view);
		if (videoView != null) {
			videoView.setOnCompletionListener(this);
		}
	}

	protected String getVideoPath() {
		return videoPath;
	}

	protected VideoView getVideoView() {
		return videoView;
	}

	protected boolean isLoopingPlayback() {
		return isLoopingPlayback;
	}

	@Override
	protected void onStop() {
		super.onStop();

		if (videoView != null) {
			videoView.stopPlayback();
		}
	}

	protected void playVideo() {
        if (videoView != null) {
			videoView.setVideoPath(videoPath, isLoopingPlayback());
			videoView.start();
		}
	}

	protected  void stopVideo() {
		if (videoView != null) {
			videoView.stopPlayback();
		}
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		if (!isLoopingPlayback()) {
			finish();
		}
	}

}
