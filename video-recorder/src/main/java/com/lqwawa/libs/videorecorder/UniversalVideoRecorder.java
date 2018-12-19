package com.lqwawa.libs.videorecorder;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.animation.*;
import android.widget.*;

import java.io.File;
import java.util.Formatter;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class UniversalVideoRecorder extends VideoRecorder
		implements CameraView.RecorderListener, CameraView.OrientationListener {

	public static final String TAG = UniversalVideoRecorder.class.getSimpleName();

    public static final String EXTRA_PREVIEW_ON_RECORDED = "previewOnRecorded";

	private static final int MSG_RECORDING_PROGRESS = 0;
	private static final int MSG_START_RECORDING = 1;
	private static final int MSG_FINISH_RECORDING = 2;
	private static final int MSG_CANCEL_RECORDING = 3;

	private static final int RECORDING_PROGRESS_INTERVAL = 1000; //milliseconds

	private Button recordBtn;
	private ImageView toggleBtn;

	private Timer recordingTimer;
	private int recordingTimerCount;
	private StringBuilder formatBuilder;
	private Formatter formatter;
	private boolean previewOnRecorded = false;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == MSG_START_RECORDING) {
				getCameraView().startRecorder();
			} else if (msg.what == MSG_FINISH_RECORDING) {
				getCameraView().finishRecording();
			} else if (msg.what == MSG_CANCEL_RECORDING) {
				getCameraView().cancelRecording();
			} else if (msg.what == MSG_RECORDING_PROGRESS) {
				if (recordBtn != null) {
					String timeStr = stringForTime(msg.arg1 * RECORDING_PROGRESS_INTERVAL);
					SpannableString str = new SpannableString(
							new StringBuilder(timeStr)
                                .append("\n")
                                .append(getString(R.string.vr_click_to_stop_recording)).toString());
					str.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.vr_green)),
							0, timeStr.length(),
							Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					recordBtn.setText(str);
				}
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
	}

	@Override
	protected int getContentViewId() {
		return R.layout.vr_universal_video_recorder;
	}

	protected void previewVideo(String filePath) {
		if (TextUtils.isEmpty(filePath)) {
			return;
		}
		Intent intent = new Intent(this, UniversalVideoPlayer.class);
		intent.putExtra(UniversalVideoPlayer.EXTRA_VIDEO_PATH, filePath);
		startActivity(intent);
	}

	private void init() {
		if (getIntent() != null) {
			previewOnRecorded = getIntent().getBooleanExtra(EXTRA_PREVIEW_ON_RECORDED, false);
		}

		formatBuilder = new StringBuilder();
		formatter = new Formatter(formatBuilder, Locale.getDefault());

		initViews();
	}

	private void initViews() {
		recordBtn = (Button) findViewById(R.id.vr_record_btn);
		if (recordBtn != null) {
			recordBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
                    boolean isRecording = v.isSelected();
                    if (isRecording) {
						handler.sendEmptyMessage(MSG_FINISH_RECORDING);
					} else {
						handler.sendEmptyMessage(MSG_START_RECORDING);
					}
					updateViews(!isRecording);

					// enable recording again after a short while to prevent camera problems
					recordBtn.setEnabled(false);
					recordBtn.setTextColor(getResources().getColor(R.color.vr_gray));
					handler.postDelayed(new Runnable() {
						@Override
						public void run() {
							recordBtn.setEnabled(true);
							recordBtn.setTextColor(getResources().getColor(R.color.vr_green));
						}
					}, 3000);
				}
			});
			recordBtn.setSelected(false);
		}
	}

	private void updateViews(boolean isRecording) {
        if (isRecording) {
            if (recordBtn != null) {
				recordBtn.setSelected(true);
				recordBtn.setText(getString(R.string.vr_click_to_stop_recording));
				recordBtn.setBackgroundResource(R.drawable.vr_record_btn_hl_bg);
			}
		} else {
			if (recordBtn != null) {
				recordBtn.setSelected(false);
				recordBtn.setText(getString(R.string.vr_click_to_record));
				recordBtn.setBackgroundResource(R.drawable.vr_record_btn_bg);
			}
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onRecorderStart() {
		super.onRecorderStart();

		startRecordingProgress();
	}

	@Override
	public void onRecorderStop() {
		super.onRecorderStop();

		stopRecordingProgress();
	}

	@Override
	public void onRecorderFinish(String filePath) {
		super.onRecorderFinish(filePath);
		if (!TextUtils.isEmpty(filePath)) {
			if (recordBtn != null) {
				recordBtn.setTag(filePath);
				handler.post(new Runnable() {
					@Override
					public void run() {
						if (previewOnRecorded) {
							previewVideo((String) recordBtn.getTag());
						} else {
							String filePath = (String) recordBtn.getTag();
							if (!TextUtils.isEmpty(filePath)) {
								Intent intent = new Intent();
								intent.putExtra(EXTRA_VIDEO_PATH, filePath);
								intent.setData(Uri.fromFile(new File(filePath)));
								setResult(RESULT_OK, intent);
							}
							finish();
						}
					}
				});
			}
		}

        updateViews(false);

		// reset camera to prevent recording problems
		getCameraView().reset();
	}

	@Override
	public void onRecorderCancel() {
		super.onRecorderCancel();

		stopRecordingProgress();

		// reset camera to prevent recording problems
		getCameraView().reset();
	}

	@Override
	public void onRecorderError(Exception e) {
		super.onRecorderError(e);

		stopRecordingProgress();
	}

	private void startRecordingProgress() {
        recordingTimerCount = 0;
		recordingTimer = new Timer();
		recordingTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				recordingTimerCount++;

				Message message = new Message();
				message.what = MSG_RECORDING_PROGRESS;
				message.arg1 = recordingTimerCount;
				handler.sendMessage(message);
			}
		}, 0, RECORDING_PROGRESS_INTERVAL);
	}

	private void stopRecordingProgress() {
		updateViews(false);

		if (recordingTimer != null) {
			recordingTimer.cancel();
		}
	}


	@Override
	public void onOrientationChanged(int fromOrientation, int toOrientation) {
		super.onOrientationChanged(fromOrientation, toOrientation);
		RotateAnimation animation = getCameraView().getRotateAnimation(fromOrientation, toOrientation);
		if (recordBtn != null) {
			recordBtn.startAnimation(animation);
		}
	}

	private String stringForTime(int timeMs) {
		int totalSeconds = timeMs / 1000;
		int seconds = totalSeconds % 60;
		int minutes = (totalSeconds / 60) % 60;
		int hours   = totalSeconds / 3600;

		formatBuilder.setLength(0);
		if (hours > 0) {
			return formatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
		} else {
			return formatter.format("%02d:%02d", minutes, seconds).toString();
		}
	}

	/**
	 * 按下时候动画效果
	 */
	public void pressAnimations() {
		if (recordBtn != null) {
			PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat("scaleX", 1.0f, 1.25f);
			PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat("scaleY", 1.0f, 1.25f);
			ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(recordBtn, scaleX, scaleY);
			animator.setDuration(200);
			animator.start();
		}
	}

	/**
	 * 释放时候动画效果
	 */
	public void releaseAnimations() {
		if (recordBtn != null) {
			PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat("scaleX", 1.25f, 1.0f);
			PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat("scaleY", 1.25f, 1.0f);
			ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(recordBtn, scaleX, scaleY);
			animator.setDuration(200);
			animator.start();
		}
	}

}
