package com.lqwawa.libs.videorecorder;

import android.animation.*;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.Toast;
import com.lqwawa.libs.videorecorder.views.CircularProgressBar;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class SimpleVideoRecorder extends VideoRecorder
		implements CameraView.RecorderListener, CameraView.OrientationListener {

	public static final String TAG = SimpleVideoRecorder.class.getSimpleName();

	public static final String EXTRA_VIDEO_DURATION = "videoDuration";
	public static final int DEFAULT_VIDEO_DURATION = 15; //seconds

	private static final int MSG_RECORDING_PROGRESS = 0;
	private static final int MSG_START_RECORDING = 1;
	private static final int MSG_FINISH_RECORDING = 2;
	private static final int MSG_CANCEL_RECORDING = 3;

	private static final int RECORDING_PROGRESS_INTERVAL = 40; //milliseconds

    private CircularProgressBar recordBtn;
	private CircularProgressBar progressBar;
	private ImageView cancelBtn, confirmBtn;
	private ImageView thumbnailView;
	private View thumbnailLayout;
	private boolean isCancel;

	private Timer recordingTimer;
	private int recordingTimerCount;
	private int minRecordTime = 3000; //milliseconds
	private int maxRecordTime = DEFAULT_VIDEO_DURATION * 1000; //milliseconds
    private int minRecordTimerCount;
	private int maxRecordTimerCount;
	private boolean isRecording = false;

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
                if (progressBar != null) {
                    int degrees = 360 * msg.arg1 / maxRecordTimerCount;
                    progressBar.setProgress(degrees);
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
		return R.layout.vr_simple_video_recorder;
	}

    protected void previewVideo(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return;
        }
		Intent intent = new Intent(this, SimpleVideoPreviewer.class);
		intent.putExtra(VideoPlayer.EXTRA_VIDEO_PATH, filePath);
//		startActivity(intent);
		startActivityForResult(intent, SimpleVideoPreviewer.REQUEST_CODE_PREVIEW_VIDEO);
	}

	private void init() {
		if (getIntent() != null) {
			maxRecordTime = getIntent().getIntExtra(
					EXTRA_VIDEO_DURATION, DEFAULT_VIDEO_DURATION) * 1000;
            if (maxRecordTime < minRecordTime) {
				maxRecordTime = minRecordTime;
			}
		}
		maxRecordTime += 125; //TODO fix possible duration loss of recorded video
		maxRecordTimerCount = maxRecordTime / RECORDING_PROGRESS_INTERVAL;
		if (maxRecordTime % RECORDING_PROGRESS_INTERVAL > 0) {
			maxRecordTimerCount += 1;
		}
		minRecordTimerCount = minRecordTime / RECORDING_PROGRESS_INTERVAL;
		if (minRecordTime % RECORDING_PROGRESS_INTERVAL > 0) {
			minRecordTimerCount += 1;
		}
		initViews();
	}

	private void initViews() {
		recordBtn = (CircularProgressBar) findViewById(R.id.vr_record_btn);
		if (recordBtn != null) {
			recordBtn.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						pressAnimations();
						isCancel = false;
						if (recordBtn != null) {
							recordBtn.setText(getString(R.string.vr_release_to_finish_record));
						}
						handler.sendEmptyMessage(MSG_START_RECORDING);
						isRecording = true;
						break;
					case MotionEvent.ACTION_MOVE:
						if (event.getX() > 0
								&& event.getX() < recordBtn.getWidth()
								&& event.getY() > 0
								&& event.getY() < recordBtn.getHeight()) {
							showPressMessage();
							isCancel = false;
						} else {
							cancelAnimations();
							isCancel = true;
						}
						break;
					case MotionEvent.ACTION_UP:
						releaseAnimations();
//                        message.setVisibility(View.GONE);
//						if (isCancel) {
//							handler.sendEmptyMessage(MSG_CANCEL_RECORDING);
//						} else {
//							handler.sendEmptyMessage(MSG_FINISH_RECORDING);
//						}
						stopRecordingProgress();
						if (recordBtn != null) {
							recordBtn.setText(getString(R.string.vr_press_and_hold_to_record));
						}
						if (isFinishRecordingAllowed()) {
							handler.sendEmptyMessage(MSG_FINISH_RECORDING);
						} else {
							// cancel recording after a short while to prevent camera problems
                            handler.sendEmptyMessageDelayed(MSG_CANCEL_RECORDING, 1500);
							Toast.makeText(SimpleVideoRecorder.this,
									getString(R.string.vr_record_time_too_short, minRecordTime / 1000),
									Toast.LENGTH_SHORT).show();
						}

						// enable recording again after a short while after every recording
						// to prevent camera problems
						recordBtn.setEnabled(false);
						recordBtn.setTextColor(getResources().getColor(R.color.vr_gray));
						handler.postDelayed(new Runnable() {
							@Override
							public void run() {
								recordBtn.setEnabled(true);
                                recordBtn.setTextColor(getResources().getColor(R.color.vr_green));
							}
						}, 3000);
						isRecording = false;
						break;
					default:
						break;
					}
					return true;
				}
			});
		}

		cancelBtn = (ImageView) findViewById(R.id.vr_cancel_btn);
		if (cancelBtn != null) {
			cancelBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();
				}
			});
		}

//		confirmBtn = (ImageView) findViewById(R.id.vr_confirm_btn);
		if (confirmBtn != null) {
			confirmBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					String filePath = (String) v.getTag();
					if (!TextUtils.isEmpty(filePath)) {
						Intent intent = new Intent();
						intent.putExtra(EXTRA_VIDEO_PATH, filePath);
						intent.setData(Uri.fromFile(new File(filePath)));
						setResult(RESULT_OK, intent);
					}
					finish();
				}
			});
		}

//		thumbnailLayout = findViewById(R.id.vr_thumbnail_layout);
//		thumbnailView = (ImageView) findViewById(R.id.vr_thumbnail);
		if (thumbnailView != null) {
			thumbnailView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					previewVideo((String) v.getTag());
				}
			});
		}

		progressBar = (CircularProgressBar) findViewById(R.id.vr_record_btn);
		if (progressBar != null) {
			int size = (int) (getResources().getDimension(R.dimen.vr_record_button_size) / 2);
			RadialGradient rg = new RadialGradient(size, size, size,
					0x5fffffff, 0x5f000000, Shader.TileMode.CLAMP);
			progressBar.setCircleShader(rg);
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
	protected void finishRecording() {
		if (!getCameraView().isRecording()) {
			return;
		}

		if (!isFinishRecordingAllowed()) {
			Toast.makeText(this,
					getString(R.string.vr_record_time_too_short, minRecordTime / 1000),
					Toast.LENGTH_SHORT).show();
            getCameraView().cancelRecording();
		} else {
			getCameraView().finishRecording();
		}
	}

	@Override
	public void onRecorderStart() {
		super.onRecorderStart();

		startRecordingProgress();

		if (thumbnailLayout != null) {
			thumbnailLayout.clearAnimation();
			thumbnailLayout.setVisibility(View.GONE);
		}
		if (confirmBtn != null) {
			confirmBtn.clearAnimation();
			confirmBtn.setVisibility(View.INVISIBLE);
		}
//		if (recordBtn != null) {
//			recordBtn.setText(getString(R.string.vr_release_to_finish_record));
//		}
	}

	@Override
	public void onRecorderStop() {
		super.onRecorderStop();

		stopRecordingProgress();

		if (recordBtn != null) {
			recordBtn.setText(getString(R.string.vr_press_and_hold_to_record));
		}
	}

	@Override
	public void onRecorderFinish(String filePath) {
		super.onRecorderFinish(filePath);
		if (!TextUtils.isEmpty(filePath)) {
			previewVideo(filePath);

			if (thumbnailView != null) {
				thumbnailView.setTag(filePath);
				handler.post(new Runnable() {
					@Override
					public void run() {
						String filePath = (String) thumbnailView.getTag();
						Bitmap bitmap = VideoUtils.getVideoThumbnail(filePath);
						thumbnailView.setImageBitmap(bitmap);
						if (thumbnailLayout != null) {
							thumbnailLayout.clearAnimation();
							thumbnailLayout.setVisibility(bitmap != null ? View.VISIBLE : View.GONE);
						}
					}
				});
			}

			if (confirmBtn != null) {
				confirmBtn.clearAnimation();
				confirmBtn.setVisibility(View.VISIBLE);
				confirmBtn.setTag(filePath);
			}
		}

		if (recordBtn != null) {
			recordBtn.setText(getString(R.string.vr_press_and_hold_to_record));
		}

		// reset camera to prevent recording problems
		getCameraView().reset();
	}

	@Override
	public void onRecorderCancel() {
		super.onRecorderCancel();

		if (thumbnailLayout != null) {
			thumbnailLayout.clearAnimation();
			thumbnailLayout.setVisibility(View.GONE);
		}
		if (confirmBtn != null) {
			confirmBtn.clearAnimation();
			confirmBtn.setVisibility(View.INVISIBLE);
		}
		if (recordBtn != null) {
			recordBtn.setText(getString(R.string.vr_press_and_hold_to_record));
		}

		stopRecordingProgress();

		// reset camera to prevent recording problems
		getCameraView().reset();
	}

	@Override
	public void onRecorderError(Exception e) {
		super.onRecorderError(e);

		stopRecordingProgress();

        if (recordBtn != null) {
            recordBtn.setText(getString(R.string.vr_press_and_hold_to_record));
        }
	}

	private boolean isFinishRecordingAllowed() {
		return recordingTimerCount >= minRecordTimerCount;
	}

	private void startRecordingProgress() {
		if (progressBar != null) {
			progressBar.setProgress(0);
		}
		recordingTimerCount = 0;
		recordingTimer = new Timer();
		recordingTimer.schedule(new TimerTask() {
			@Override
			public void run() {
                if (!isRecording) {
					return;
				}
				recordingTimerCount++;
				if (recordingTimerCount > maxRecordTimerCount) {
					Message message = new Message();
					message.what = MSG_FINISH_RECORDING;
					handler.sendMessage(message);
					isRecording = false;
				} else {
					Message message = new Message();
					message.what = MSG_RECORDING_PROGRESS;
					message.arg1 = recordingTimerCount;
					handler.sendMessage(message);
				}
			}
		}, 0, RECORDING_PROGRESS_INTERVAL);
	}

	private void stopRecordingProgress() {
		if (recordingTimer != null) {
			recordingTimer.cancel();
		}
		if (progressBar != null) {
			progressBar.setProgress(0);
		}
	}

	/**
	 * 移动取消弹出动画
	 */
	public void cancelAnimations() {
//		message.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
//		message.setTextColor(getResources().getColor(android.R.color.white));
//		message.setText("松手取消");
	}

	/**
	 * 显示提示信息
	 */
	public void showPressMessage() {
//		message.setVisibility(View.VISIBLE);
//		message.setBackgroundColor(getResources().getColor(android.R.color.transparent));
//		message.setTextColor(getResources().getColor(android.R.color.holo_green_light));
//		message.setText("上移取消");
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

	@Override
	public void onOrientationChanged(int fromOrientation, int toOrientation) {
		super.onOrientationChanged(fromOrientation, toOrientation);
		RotateAnimation animation = getCameraView().getRotateAnimation(fromOrientation, toOrientation);
		if (thumbnailLayout != null && thumbnailLayout.getVisibility() == View.VISIBLE) {
			thumbnailLayout.startAnimation(animation);
		}
		if (recordBtn != null) {
//			animation = getCameraView().getRotateAnimation(fromOrientation, toOrientation);
//			recordBtn.startAnimation(animation);
			recordBtn.setCanvasRotation(getCameraView().getRotationOffset(toOrientation));
		}
		if (confirmBtn != null && confirmBtn.getVisibility() == View.VISIBLE) {
			animation = getCameraView().getRotateAnimation(fromOrientation, toOrientation);
			confirmBtn.startAnimation(animation);
		}
		if (cancelBtn != null) {
			animation = getCameraView().getRotateAnimation(fromOrientation, toOrientation);
			cancelBtn.startAnimation(animation);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == SimpleVideoPreviewer.REQUEST_CODE_PREVIEW_VIDEO) {
				if (data != null) {
					String filePath = data.getStringExtra(VideoRecorder.EXTRA_VIDEO_PATH);
					File file = new File(filePath);
					if (!TextUtils.isEmpty(filePath)
							&& file.exists() && file.canRead() && file.length() > 0) {
						Intent intent = new Intent();
						intent.putExtra(EXTRA_VIDEO_PATH, filePath);
						intent.setData(Uri.fromFile(new File(filePath)));
						setResult(RESULT_OK, intent);
						finish();
						return;
					}
				}
			}
		}
	}

}
