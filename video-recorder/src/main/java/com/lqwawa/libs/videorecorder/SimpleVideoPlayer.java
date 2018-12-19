package com.lqwawa.libs.videorecorder;

import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class SimpleVideoPlayer extends VideoPlayer {

	public static final String TAG = SimpleVideoPlayer.class.getSimpleName();

	public static final String EXTRA_EXIT_PLAYBACK_ON_COMPLETION = "exitPlaybackOnCompletion";

	private ImageView playBtn;
	private ImageView thumbnailView;
	private boolean exitPlaybackOnCompletion = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		init();
	}

	@Override
	protected int getContentViewId() {
		return R.layout.vr_simple_video_player;
	}

	private void init() {
		if (getIntent() != null) {
			exitPlaybackOnCompletion = getIntent().getBooleanExtra(
					EXTRA_EXIT_PLAYBACK_ON_COMPLETION, true);
		}

		initViews();
	}

	@Override
	protected void initViews() {
        super.initViews();

		thumbnailView = (ImageView) findViewById(R.id.vr_video_thumbnail);
		if (thumbnailView != null) {
			Bitmap bitmap = VideoUtils.getVideoThumbnail(getVideoPath());
			if (bitmap != null) {
				thumbnailView.setImageBitmap(bitmap);
			}
			thumbnailView.setVisibility(View.GONE);
		}

		playBtn = (ImageView) findViewById(R.id.vr_play_btn);
		if (playBtn != null) {
			playBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					playVideo();
				}
			});
			playBtn.setVisibility(View.GONE);
		}
	}

	@Override
	protected boolean isLoopingPlayback() {
		return false;
	}

	@Override
	protected void playVideo() {
		if (playBtn != null) {
			playBtn.setVisibility(View.GONE);
		}
		if (thumbnailView != null) {
			thumbnailView.setVisibility(View.GONE);
		}
		getVideoView().setVisibility(View.VISIBLE);
		super.playVideo();
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		if (exitPlaybackOnCompletion) {
			finish();
		} else {
			getVideoView().setVisibility(View.GONE);
			if (playBtn != null) {
				playBtn.setVisibility(View.VISIBLE);
			}
			if (thumbnailView != null) {
				thumbnailView.setVisibility(View.VISIBLE);
			}
		}
	}

}
