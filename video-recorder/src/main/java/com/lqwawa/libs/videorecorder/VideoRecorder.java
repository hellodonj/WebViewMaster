package com.lqwawa.libs.videorecorder;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.*;
import android.view.animation.*;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class VideoRecorder extends BaseActivity
		implements CameraView.RecorderListener, CameraView.OrientationListener {

	public static final String TAG = VideoRecorder.class.getSimpleName();

	public static final int REQUEST_CODE_CAPTURE_VIDEO = 2016;

	public static final String EXTRA_VIDEO_PATH = "videoPath";
//	public static final String EXTRA_VIDEO_THUMBNAIL_PATH = "videoThumbPath";

	private CameraView cameraView;
	private ImageView toggleBtn;
	private PowerManager.WakeLock wakeLock;
    private boolean isCancel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initViews();

		checkPermissions();
	}

	@Override
	protected int getContentViewId() {
		return R.layout.vr_video_recorder;
	}

	private void checkPermissions() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			List<String> permissionList = new ArrayList();
			if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
					!= PackageManager.PERMISSION_GRANTED) {
                permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
			}
			if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
					!= PackageManager.PERMISSION_GRANTED) {
				permissionList.add(Manifest.permission.RECORD_AUDIO);
			}
			if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
					!= PackageManager.PERMISSION_GRANTED) {
                permissionList.add(Manifest.permission.CAMERA);
			}
			if (permissionList.size() > 0) {
				ActivityCompat.requestPermissions(this, permissionList.toArray(new String[0]), 1);
			}
		}
	}

	private void initViews() {
		cameraView = (CameraView) findViewById(R.id.vr_camera_view);
		initCameraView();

		toggleBtn = (ImageView) findViewById(R.id.vr_toggle_btn);
		if (toggleBtn != null) {
			toggleBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (cameraView != null && !cameraView.isRecording()) {
						cameraView.switchCamera();
					}
				}
			});
            showToggleView(true);
		}
	}

	private void initCameraView() {
		if (cameraView != null) {
//			WindowManager wm = (WindowManager) getApplicationContext()
//					.getSystemService(Context.WINDOW_SERVICE);
//			Display display = wm.getDefaultDisplay();
//			int width = display.getWidth();
//			int height = display.getHeight();
//			int displayOrientation = CameraView.getScreenRotation(display.getRotation());
//
//			ViewGroup.LayoutParams lp = cameraView.getLayoutParams();
//			int orientation = CameraView.getCameraDisplayOrientation(
//					displayOrientation, cameraView.getCameraId());
//			int defaultWidth = CameraView.DEFAULT_VIDEO_WIDTH;
//			int defaultHeight = CameraView.DEFAULT_VIDEO_HEIGHT;
//			if (orientation == 90 || orientation == 270) {
//				defaultWidth = CameraView.DEFAULT_VIDEO_HEIGHT;
//				defaultHeight = CameraView.DEFAULT_VIDEO_WIDTH;
//			}
//			double ratio = (double) width / (double) defaultWidth;
//			lp.width = width;
//			lp.height = (int) ((double) defaultHeight * ratio);
//			cameraView.setLayoutParams(lp);

			cameraView.setRecordingFilePath(getIntent().getStringExtra(EXTRA_VIDEO_PATH));
			cameraView.setRecorderListener(this);
			cameraView.setOrientationListener(this);
		}
	}

	protected CameraView getCameraView() {
		return cameraView;
	}

	protected ImageView getToggleView() {
		return toggleBtn;
	}

	protected void showToggleView(boolean show) {
		if (toggleBtn != null) {
            // It's strange that changing visibility of view do not take effect here, so I try to
			// set alpha value to show or hide this view.
//            toggleBtn.setVisibility(show && getCameraView().hasMultipleCameras() ?
//                    View.VISIBLE : View.GONE);
			toggleBtn.setImageAlpha(show && getCameraView().hasMultipleCameras() ? 255 : 0);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

        acquireWakeLock();

		if (cameraView != null) {
			cameraView.resume();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();

		releaseWakeLock();

		if (cameraView != null) {
			cameraView.pause();
		}
	}

	@Override
	protected void onStop() {
		super.onStop();

		if (cameraView != null) {
            cameraView.stop(isCancel);
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		isCancel = true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (cameraView != null) {
			cameraView.release();
		}
	}

	protected void cancelRecording() {
		if (cameraView != null) {
			cameraView.cancelRecording();
		}
	}

	protected void finishRecording() {
		if (cameraView != null) {
			if (!cameraView.isRecording()) {
				return;
			}

			cameraView.finishRecording();
		}
	}

	@Override
	public void onRecorderPrepare() {
		VideoUtils.log(TAG, "onRecorderPrepare");
		showToggleView(false);
	}

	@Override
	public void onRecorderStart() {
		VideoUtils.log(TAG, "onRecorderStart");
		showToggleView(false);
	}

	@Override
	public void onRecorderFinish(String filePath) {
		VideoUtils.log(TAG, "onRecorderFinish: " + filePath);
		showToggleView(true);
	}

	@Override
	public void onRecorderStop() {
		VideoUtils.log(TAG, "onRecorderStop");
		showToggleView(true);
	}

	@Override
	public void onRecorderCancel() {
		VideoUtils.log(TAG, "onRecorderCancel");
		showToggleView(true);
	}

	@Override
	public void onRecorderError(Exception e) {
		VideoUtils.log(TAG, "onRecorderError");
		e.printStackTrace();
		showToggleView(true);
		if (!isFinishing()) {
			Toast.makeText(this, R.string.vr_recording_error, Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onOrientationChanged(int from, int to) {
		RotateAnimation animation = getCameraView().getRotateAnimation(from, to);
		if (toggleBtn != null && toggleBtn.getVisibility() == View.VISIBLE) {
			toggleBtn.startAnimation(animation);
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == 1) {
            if (permissions.length > 0) {
                for (int i = 0; i < permissions.length; i++) {
                    if (Manifest.permission.CAMERA.equals(permissions[i])) {
						if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
							// Permission Granted
							if (cameraView != null) {
								cameraView.reset();
							}
						} else {
							// Permission Denied
							finish();
						}
						break;
					}
				}
			}
		}
	}


	protected void acquireWakeLock() {
		if (wakeLock == null) {
			PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
			wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK,
					this.getClass().getCanonicalName());
			 wakeLock.acquire();
		}
	}

	protected void releaseWakeLock() {
		if (wakeLock != null && wakeLock.isHeld()) {
			wakeLock.release();
			wakeLock = null;
		}
	}

}
