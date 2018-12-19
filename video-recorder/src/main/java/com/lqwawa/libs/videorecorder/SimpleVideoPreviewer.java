package com.lqwawa.libs.videorecorder;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.io.File;

public class SimpleVideoPreviewer extends VideoPlayer {

	public static final String TAG = SimpleVideoPreviewer.class.getSimpleName();

	public static final int REQUEST_CODE_PREVIEW_VIDEO = 2017;

	private ImageView cancelBtn;
	private ImageView confirmBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initViews();
	}

	@Override
	protected int getContentViewId() {
		return R.layout.vr_my_video_player;
	}

	@Override
	protected void initViews() {
		super.initViews();

		cancelBtn = (ImageView) findViewById(R.id.vr_cancel_btn);
		if (cancelBtn != null) {
            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
		}

		confirmBtn = (ImageView) findViewById(R.id.vr_confirm_btn);
		if (confirmBtn != null) {
			confirmBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.putExtra(EXTRA_VIDEO_PATH, getVideoPath());
					intent.setData(Uri.fromFile(new File(getVideoPath())));
					setResult(RESULT_OK, intent);
                    finish();
				}
			});
		}
	}

	@Override
	protected boolean isLoopingPlayback() {
		return true;
	}

}
