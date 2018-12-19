package com.lqwawa.libs.videorecorder;

import android.os.Bundle;
import android.view.View;

public class UniversalVideoPlayer extends SimpleVideoPlayer {

	public static final String TAG = UniversalVideoPlayer.class.getSimpleName();

	public static final String EXTRA_VIDEO_TITLE = "videoTitle";

	private MediaController mediaController;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected int getContentViewId() {
		return R.layout.vr_universal_video_player;
	}

	@Override
	protected void initViews() {
		super.initViews();

		mediaController = (MediaController) findViewById(R.id.vr_media_controller);
		if (mediaController != null) {
			getVideoView().setMediaController(mediaController);

			mediaController.getBackButton().setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();
				}
			});

			if (getIntent() != null) {
				mediaController.getTitleView().setText(
						getIntent().getStringExtra(EXTRA_VIDEO_TITLE));
			}
		}
	}

	@Override
	protected boolean isLoopingPlayback() {
		return false;
	}

}
