package com.lqwawa.libs.videorecorder;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.MediaRecorder.AudioEncoder;
import android.media.MediaRecorder.AudioSource;
import android.media.MediaRecorder.OnErrorListener;
import android.media.MediaRecorder.OutputFormat;
import android.media.MediaRecorder.VideoEncoder;
import android.media.MediaRecorder.VideoSource;
import android.os.Environment;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CameraView extends FrameLayout implements Callback, OnErrorListener {

    public static final String TAG = CameraView.class.getSimpleName();

    public static final int DEFAULT_CAMERA_ID = Camera.CameraInfo.CAMERA_FACING_BACK;
    public static final List<Size> ALTERNATIVE_VIDEO_SIZES = new ArrayList();
    static {
        ALTERNATIVE_VIDEO_SIZES.add(new Size(800, 600));
        ALTERNATIVE_VIDEO_SIZES.add(new Size(320, 240));
    }
    public static final int MAX_VIDEO_WIDTH = ALTERNATIVE_VIDEO_SIZES.get(0).width;
    public static final int MAX_VIDEO_HEIGHT = ALTERNATIVE_VIDEO_SIZES.get(0).height;
    public static final int MIN_VIDEO_WIDTH = ALTERNATIVE_VIDEO_SIZES.get(
            ALTERNATIVE_VIDEO_SIZES.size() - 1).width;
    public static final int MIN_VIDEO_HEIGHT = ALTERNATIVE_VIDEO_SIZES.get(
            ALTERNATIVE_VIDEO_SIZES.size() - 1).height;
    public static final int DEFAULT_VIDEO_WIDTH = MIN_VIDEO_WIDTH;
    public static final int DEFAULT_VIDEO_HEIGHT = MIN_VIDEO_HEIGHT;

    private Context context;
    private Handler handler = new Handler();

    private int cameraId = DEFAULT_CAMERA_ID;
    private Camera camera;
    private SurfaceView cameraSurfaceView;
    private SurfaceHolder cameraSurfaceHolder;
    private int screenWidth, screenHeight;
    private List<Camera.Size> supportedVideoSizes;
    private List<Camera.Size> supportedPreViewSizes;

    private String recordingFilePath;
    private File recordingFile;
    private MediaRecorder mediaRecorder;
    private RecorderListener recorderListener;
    private boolean isRecording;

    private boolean useSingleWorkerThread = true;
    private List<Runnable> cameraTasks;
    private Thread cameraWorker;
    private boolean isStopped;
    private boolean isDestroyed;
    private boolean hasSurface;

//    private SurfaceView previewSurfaceView;

    public CameraView(Context context) {
        super(context);
        
        this.context = context;
        init();
    }

    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.context = context;
        init();
    }

    public CameraView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        this.context = context;
        init();
    }

    public int getScreenRotation() {
        return getScreenRotation(context);
    }
    
    public static int getScreenRotation(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        return CameraView.getScreenRotation(wm.getDefaultDisplay().getRotation());
    }

    public int getCameraId() {
        return cameraId;
    }

    public String getRecordingFilePath() {
        return recordingFilePath;
    }

    public void setRecordingFilePath(String filePath) {
        recordingFilePath = filePath;
    }

    public RecorderListener getRecorderListener() {
        return recorderListener;
    }

    public void setRecorderListener(RecorderListener recorderListener) {
        this.recorderListener = recorderListener;
    }

    private void init() {
        cameraSurfaceView = new SurfaceView(context);
        addView(cameraSurfaceView);
        ViewGroup.LayoutParams lp = cameraSurfaceView.getLayoutParams();
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
        cameraSurfaceView.setLayoutParams(lp);

        cameraSurfaceHolder = cameraSurfaceView.getHolder();
        cameraSurfaceHolder.addCallback(this);
        cameraSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        initOrientationEventListener();

        if (useSingleWorkerThread) {
            startWorkerThread();
        }
    }

    public void setUseSingleWorkerThread(boolean use) {
        useSingleWorkerThread = use;
    }

    private void startWorkerThread() {
        cameraTasks = new ArrayList<Runnable>();
        cameraWorker = new Thread() {
            @Override
            public void run() {
                while (!isDestroyed) {
                    if (cameraTasks.size() > 0) {
                        Runnable task = null;
                        synchronized (cameraTasks) {
                            task = cameraTasks.remove(0);
                        }
                        if (task != null) {
                            task.run();
                        }
                    }
                }
            }
        };
        cameraWorker.start();
    }

    private void stopWorkerThread() {
        isDestroyed = true;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        VideoUtils.log(TAG, "surfaceCreated");
        hasSurface = true;
        cameraSurfaceHolder = holder;
        cameraSurfaceHolder.addCallback(this);
        cameraSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        LayoutParams lp = (LayoutParams) cameraSurfaceView.getLayoutParams();
        cameraSurfaceView.setLayoutParams(lp);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        VideoUtils.log(TAG, "surfaceChanged " + width + "x" + height);
        screenWidth = width;
        screenHeight = height;

        try {
            if (camera == null) {
                camera = Camera.open(cameraId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        Parameters params = camera.getParameters();
        Camera.Size size = getMyOptimalSize(params.getSupportedPreviewSizes(),
                screenWidth, screenHeight);
        if (size != null) {
            int w = size.width;
            int h = size.height;
            if ((screenWidth > screenHeight && size.width < size.height)
                    || (screenWidth < screenHeight && size.width > size.height)) {
                w = size.height;
                h = size.width;
            }
            // screenWidth / screenHeight == size.width / size.height;
            int mw = screenWidth * h;
            int mh = screenHeight * w;
            if (mw != mh) {
                LayoutParams lp = (LayoutParams) cameraSurfaceView.getLayoutParams();
                lp.width = mw > mh ? screenHeight * w / h : screenWidth;
                lp.height = mw > mh ? screenHeight : screenWidth * h / w;
                lp.gravity = Gravity.CENTER;
                cameraSurfaceView.setLayoutParams(lp);
            }
        }

        handler.post(new Runnable() {
            @Override
            public void run() {
                initCamera();
            }
        });
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        VideoUtils.log(TAG, "surfaceDestroyed");
        hasSurface = false;
    }

    private void initCamera() {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                initCameraImmediately();
            }
        };
        if (useSingleWorkerThread) {
            cameraTasks.add(task);
        } else {
            new Thread(task).start();
        }
    }

    private void initCameraImmediately() {
        try {
            if (camera == null) {
                camera = Camera.open(cameraId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        setCameraParams();
        setCameraDisplayOrientation(getScreenRotation(), cameraId, camera);
        setCameraFocusMode();

        try {
            camera.setPreviewDisplay(cameraSurfaceHolder);
            camera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setCameraParams() {
        if (camera != null) {
            Parameters params = camera.getParameters();
            params.set("orientation", "portrait");
            Camera.Size size = getMyOptimalSize(params.getSupportedPreviewSizes(),
                    screenWidth, screenHeight);
//                    DEFAULT_VIDEO_WIDTH, DEFAULT_VIDEO_HEIGHT);
            if (size != null) {
                VideoUtils.log(TAG, "previewSize: " + size.width + "x" + size.height);
                params.setPreviewSize(size.width, size.height);
            }
            camera.setParameters(params);

            {
                VideoUtils.log(TAG, "FOCUS: mode=" + params.getFocusMode());
                Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
                Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK, cameraInfo);
                VideoUtils.log(TAG, "BACK: orientation=" + cameraInfo.orientation);
                cameraInfo = new Camera.CameraInfo();
                Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_FRONT, cameraInfo);
                VideoUtils.log(TAG, "FRONT: orientation=" + cameraInfo.orientation);

                VideoUtils.log(TAG, "NumberOfCameras=" + Camera.getNumberOfCameras());
                VideoUtils.log(TAG, "BACK");
                for (int i = 0; i < 8; i++) {
                    try {
                        CamcorderProfile profile = CamcorderProfile.get(
                                Camera.CameraInfo.CAMERA_FACING_BACK, i);
                        if (profile != null) {
                            VideoUtils.log(TAG, i + ": " + profile.videoFrameWidth
                                    + "x" + profile.videoFrameHeight);
                        }
                    } catch (Exception e) {
                        VideoUtils.log(TAG, i + ": error");
                    }
                }
                VideoUtils.log(TAG, "FRONT");
                for (int i = 0; i < 8; i++) {
                    try {
                        CamcorderProfile profile = CamcorderProfile.get(
                                Camera.CameraInfo.CAMERA_FACING_FRONT, i);
                        if (profile != null) {
                            VideoUtils.log(TAG, i + ": " + profile.videoFrameWidth
                                    + "x" + profile.videoFrameHeight);
                        }
                    } catch (Exception e) {
                        VideoUtils.log(TAG, i + ": error");
                    }
                }

                try {
                    VideoUtils.log(TAG, "SupportedPreviewSize");
                    if (params != null) {
                        List<Camera.Size> sizes = params.getSupportedPreviewSizes();
                        if (sizes != null && sizes.size() > 0) {
                            for (int i = 0; i < sizes.size(); i++) {
                                VideoUtils.log(TAG, i + ": " + sizes.get(i).width
                                        + "x" + sizes.get(i).height);
                            }
                        }
                        supportedPreViewSizes = sizes;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    VideoUtils.log(TAG, "getSupportedPreviewSizes() error");
                }

                try {
                    VideoUtils.log(TAG, "SupportedVideoSize");
                    if (params != null) {
                        List<Camera.Size> sizes = params.getSupportedVideoSizes();
                        if (sizes != null && sizes.size() > 0) {
                            for (int i = 0; i < sizes.size(); i++) {
                                VideoUtils.log(TAG, i + ": " + sizes.get(i).width
                                        + "x" + sizes.get(i).height);
                            }
                        }
                        supportedVideoSizes = sizes;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    VideoUtils.log(TAG, "getSupportedVideoSizes() error");
                }

                try {
                    VideoUtils.log(TAG, "PreviewSize");
                    if (params != null) {
                        size = params.getPreviewSize();
                        if (size != null) {
                            VideoUtils.log(TAG, size.width + "x" + size.height);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    VideoUtils.log(TAG, "getPreviewSize() error");
                }

                try {
                    VideoUtils.log(TAG, "PreferredPreviewSizeForVideo");
                    if (params != null) {
                        size = params.getPreferredPreviewSizeForVideo();
                        if (size != null) {
                            VideoUtils.log(TAG, size.width + "x" + size.height);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    VideoUtils.log(TAG, "getPreferredPreviewSizeForVideo() error");
                }
            }
        }
    }

    private void setCameraFocusMode() {
        Parameters params = camera.getParameters();
        if (params.getFocusMode() == Parameters.FOCUS_MODE_CONTINUOUS_VIDEO) {
            return;
        }
        List<String> focusModes = params.getSupportedFocusModes();
        if (focusModes != null) {
            if (focusModes != null && focusModes.size() > 0) {
                VideoUtils.log(TAG, "SupportedFocusMode");
                for (int i = 0; i < focusModes.size(); i++) {
                    VideoUtils.log(TAG, i + ": " + focusModes.get(i));
                }
            }
            if (focusModes.contains(Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                try {
                    camera.cancelAutoFocus();
                    params.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                    camera.setParameters(params);
                } catch (Exception e) {
                    camera.autoFocus(null);
                }
            }
        }

    }

    public boolean hasMultipleCameras() {
        return Camera.getNumberOfCameras() > 1;
    }

    public void switchCamera() {
        int numberOfCameras = Camera.getNumberOfCameras();
        if (numberOfCameras > 1) {
            for (int i = 0; i < numberOfCameras; i++) {
                Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
                Camera.getCameraInfo(i, cameraInfo);
                if (cameraInfo.facing != cameraId) {
                    cameraId = cameraInfo.facing;
                    break;
                }
            }

            Runnable task = new Runnable() {
                @Override
                public void run() {
                    releaseCameraImmediately();
                    initCameraImmediately();
                }
            };
            if (useSingleWorkerThread) {
                cameraTasks.add(task);
            } else {
                new Thread(task).start();
            }
        }
    }

    public void releaseCamera() {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                releaseCameraImmediately();
            }
        };
        if (useSingleWorkerThread) {
            cameraTasks.add(task);
        } else {
            new Thread(task).start();
        }
    }

    private void releaseCameraImmediately() {
        if (camera != null) {
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    private Camera.Size getMyOptimalSize(List<Camera.Size> sizes, int w, int h) {
        if (sizes == null || sizes.size() <= 0) {
            return null;
        }

        final double RATIO_TOLERANCE = 0.01f;
        double targetRatio = 1.0f;
        if (w > h) {
            targetRatio = ((double) w) / ((double) h);
        } else if (w < h) {
            targetRatio = ((double) h) / ((double) w);
        }
        List<Camera.Size> targetSizes = new ArrayList();
        Camera.Size optimalSize = null;
        double minRatioDiff = Double.MAX_VALUE;
        Camera.Size minDiffSize = null;

        // Try to find an size match aspect ratio
        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (size.height < MIN_VIDEO_HEIGHT || size.height > MAX_VIDEO_HEIGHT) {
                continue;
            }
            if (Math.abs(ratio - targetRatio) < minRatioDiff) {
                minDiffSize = size;
                minRatioDiff = Math.abs(ratio - targetRatio);
            }
            if (Math.abs(ratio - targetRatio) > RATIO_TOLERANCE) {
                continue;
            }
            targetSizes.add(size);
        }
        if (targetSizes.size() > 0) {
            optimalSize = targetSizes.get(0);
        }

        // Cannot find the one match the aspect ratio
        if (optimalSize == null) {
            optimalSize = minDiffSize;
        }

//        if (optimalSize != null) {
//            VideoUtils.log(TAG, "getMyOptimalSize: " + optimalSize.width + "x" + optimalSize.height);
//        }
        return optimalSize;
    }

    private Camera.Size getOptimalSize(List<Camera.Size> sizes, int w, int h) {
        if (sizes == null || sizes.size() <= 0) {
            return null;
        }

        final double RATIO_TOLERANCE = 0.1f;
        double targetRatio = 1.0f;
        if (w > h) {
            targetRatio = ((double) w) / ((double) h);
        } else if (w < h) {
            targetRatio = ((double) h) / ((double) w);
        }
        int targetHeight = h;
        double minDiff = Double.MAX_VALUE;
        Camera.Size optimalSize = null;

        // Try to find an size match aspect ratio and size
        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > RATIO_TOLERANCE) {
                continue;
            }
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }

//        if (optimalSize != null) {
//            VideoUtils.log(TAG, "getOptimalSize: " + optimalSize.width + "x" + optimalSize.height);
//        }
        return optimalSize;
    }

    public void startCameraPreviewImmediately() {
        if (camera != null) {
            try {
                camera.setPreviewDisplay(cameraSurfaceHolder);
                camera.startPreview();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            initCameraImmediately();
        }
    }

    public void stopCameraPreviewImmediately() {
        if (camera != null) {
//            camera.setPreviewCallback(null);
            camera.stopPreview();
        }
    }

    private boolean createRecordingFile() {
        if (recordingFilePath != null) {
            recordingFile = new File(recordingFilePath);
            if (!recordingFile.getParentFile().exists()) {
                recordingFile.getParentFile().mkdirs();
            }
            if (recordingFile.exists()) {
                recordingFile.delete();
            }
            try {
                recordingFile.createNewFile();
                return true;
            } catch (final Exception e) {
                if (recorderListener != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            recorderListener.onRecorderError(e);
                        }
                    });
                }
            }
        } else {
            File dir = new File(Environment.getExternalStorageDirectory(), "Videos");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            try {
                recordingFile = File.createTempFile(
                        "recording-" + System.currentTimeMillis(), ".mp4", dir);
                return true;
            } catch (final Exception e) {
                if (recorderListener != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            recorderListener.onRecorderError(e);
                        }
                    });
                }
            }
        }
        return false;
    }

    private void initRecorderImmediately() throws Exception {
        if (camera != null) {
            camera.unlock();
        }

        if (mediaRecorder == null) {
            mediaRecorder = new MediaRecorder();
        } else {
            mediaRecorder.reset();
        }
        if (camera != null) {
            mediaRecorder.setCamera(camera);
        }
        mediaRecorder.setAudioSource(AudioSource.CAMCORDER); //音频源
        mediaRecorder.setVideoSource(VideoSource.CAMERA); //视频源
//        mediaRecorder.setAudioEncoder(AudioEncoder.AAC); //音频格式
//        mediaRecorder.setOutputFormat(OutputFormat.MPEG_4); //视频输出格式
//        mediaRecorder.setVideoEncoder(VideoEncoder.H264); //视频录制格式
//        mediaRecorder.setVideoSize(320, 240); //设置分辨率

        CamcorderProfile profile = CamcorderProfile.get(
                cameraId, CamcorderProfile.QUALITY_HIGH);
        profile.audioCodec = AudioEncoder.AAC;
        profile.videoCodec = VideoEncoder.H264;
        profile.videoFrameRate = 30;
        profile.fileFormat = OutputFormat.MPEG_4;

        Camera.Size size = getMyOptimalSize(supportedVideoSizes,
                screenWidth, screenHeight);
        if (size != null) {
            VideoUtils.log(TAG, "videoSize: " + size.width + "x" + size.height);
            profile.videoFrameWidth = size.width;
            profile.videoFrameHeight = size.height;
        }

        try {
            mediaRecorder.setProfile(profile);
        } catch (Exception exception) {
            profile.videoFrameWidth = DEFAULT_VIDEO_WIDTH;
            profile.videoFrameHeight = DEFAULT_VIDEO_HEIGHT;
            try {
                mediaRecorder.setProfile(profile);
            } catch (final Exception e) {
                if (recorderListener != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            recorderListener.onRecorderError(e);
                        }
                    });
                }
            }
        }

        mediaRecorder.setVideoEncodingBitRate(1024 * 1024);
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, cameraInfo);
        mediaRecorder.setOrientationHint(
                getCameraOrientationHint(deviceOrientation, cameraId));
        mediaRecorder.setMaxDuration(0);

        mediaRecorder.setOutputFile(recordingFile.getAbsolutePath());
        mediaRecorder.setPreviewDisplay(cameraSurfaceHolder.getSurface());
        mediaRecorder.setOnErrorListener(this);
    }

    private boolean prepareRecorderImmediately() {
        if (mediaRecorder != null) {
            try {
                mediaRecorder.prepare();

                if (recorderListener != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            recorderListener.onRecorderPrepare();
                        }
                    });
                }

                mediaRecorder.start();
                setRecording(true);

                return true;
            } catch (final Exception e) {
                if (recorderListener != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            recorderListener.onRecorderError(e);
                        }
                    });
                }
            }
        }
        return false;
    }

    public void startRecorder() {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                startRecorderImmediately();
            }
        };
        if (useSingleWorkerThread) {
            cameraTasks.add(task);
        } else {
            new Thread(task).start();
        }
    }

    private void startRecorderImmediately() {
        if (isRecording()) {
            return;
        }

        boolean success = createRecordingFile();
        if (!success) {
            return;
        }

        try {
            initRecorderImmediately();
        } catch (final Exception e) {
            if (recorderListener != null) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        recorderListener.onRecorderError(e);
                    }
                });
            }
            return;
        }

        success = prepareRecorderImmediately();
        if (!success) {
            // maybe camera problems, try to fix it by reset
            reset();
            return;
        }

        handler.post(new Runnable() {
            @Override
            public void run() {
//                if (previewSurfaceView != null) {
//                    previewSurfaceView.setVisibility(View.GONE);
//                }
                cameraSurfaceView.setVisibility(View.VISIBLE);

                if (recorderListener != null) {
                    recorderListener.onRecorderStart();
                }
            }
        });
    }

    private boolean stopRecordingImmediately() {
        if (mediaRecorder != null) {
            mediaRecorder.setOnErrorListener(null);
            mediaRecorder.setPreviewDisplay(null);
            try {
                mediaRecorder.stop();
                setRecording(false);
                mediaRecorder.reset();

                if (recorderListener != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            recorderListener.onRecorderStop();
                        }
                    });
                }
                return true;
            } catch (final Exception e) {
                if (recorderListener != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            recorderListener.onRecorderError(e);
                        }
                    });
                }
            }
        }
        return false;
    }

    private boolean releaseRecorderImmediately() {
        if (mediaRecorder != null) {
            mediaRecorder.setOnErrorListener(null);
            try {
                mediaRecorder.release();
                mediaRecorder = null;
                return true;
            } catch (final Exception e) {
                if (recorderListener != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            recorderListener.onRecorderError(e);
                        }
                    });
                }
            }
        }
        if (camera != null) {
            camera.lock();
        }
        return false;
    }

    public boolean isRecording() {
        return isRecording;
    }

    private void setRecording(boolean recording) {
        isRecording = recording;
    }

    public void finishRecording() {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                finishRecordingImmediately();
            }
        };
        if (useSingleWorkerThread) {
            cameraTasks.add(task);
        } else {
            new Thread(task).start();
        }
    }

    private void finishRecordingImmediately() {
        if (!isRecording()) {
            return;
        }

        boolean success = stopRecordingImmediately();
        releaseRecorderImmediately();
        if (!success) {
            return;
        }

        if (recorderListener != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    recorderListener.onRecorderFinish(recordingFile.getAbsolutePath());
                }
            });
        }
    }

    public void cancelRecording() {
        cancelRecording(0);
    }

    public void cancelRecording(final long millis) {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                if (!useSingleWorkerThread && millis > 0) {
                    try {
                        Thread.sleep(millis);
                    } catch (InterruptedException e) {

                    }
                }
                cancelRecordingImmediately();
            }
        };
        if (useSingleWorkerThread) {
            cameraTasks.add(task);
        } else {
            new Thread(task).start();
        }
    }

    private void cancelRecordingImmediately() {
        handler.post(new Runnable() {
            @Override
            public void run() {
//                if (previewSurfaceView != null) {
//                    previewSurfaceView.setVisibility(View.GONE);
//                }
                cameraSurfaceView.setVisibility(View.VISIBLE);
            }
        });

        if (!isRecording()) {
            return;
        }

        boolean success = stopRecordingImmediately();

        if (recordingFile != null && recordingFile.exists()) {
            recordingFile.delete();
        }

        releaseRecorderImmediately();
        if (!success) {
            return;
        }


        if (recorderListener != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    recorderListener.onRecorderCancel();
                }
            });
        }
    }

    public void resume() {
        isStopped = false;
        enableOrientationEventListener();

        if (hasSurface) {
            initCamera();
        }
    }

    public void pause() {
        disableOrientationEventListener();
    }

    public void reset() {
        if (isStopped) {
            return;
        }

        Runnable task = new Runnable() {
            @Override
            public void run() {
                stopRecordingImmediately();
                releaseRecorderImmediately();
                releaseCameraImmediately();
                initCameraImmediately();
            }
        };
        if (useSingleWorkerThread) {
            cameraTasks.add(task);
        } else {
            new Thread(task).start();
        }
    }

    public void stop() {
        stop(false);
    }

    public void stop(final boolean cancelRecording) {
        isStopped = true;
        Runnable task = new Runnable() {
            @Override
            public void run() {
                if (cancelRecording) {
                    cancelRecordingImmediately();
                } else {
                    finishRecordingImmediately();
                }
                releaseCameraImmediately();
            }
        };
        if (useSingleWorkerThread) {
            cameraTasks.add(task);
        } else {
            new Thread(task).start();
        }
    }

    public void release() {
        stopWorkerThread();
    }

    @Override
    public void onError(MediaRecorder mr, int what, int extra) {
        try {
            if (mr != null) {
                mr.reset();
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public void releaseMediaPlayerImmediately() {
//        if (mediaPlayer != null) {
//            mediaPlayer.setDisplay(null);
//            mediaPlayer.reset();
//            mediaPlayer.release();
//            mediaPlayer = null;
//        }
//    }

//    public void previewRecording() {
//        cameraSurfaceView.setVisibility(View.GONE);
//        previewSurfaceView.setVisibility(View.VISIBLE);
//        if (mediaPlayer == null) {
//            mediaPlayer = new MediaPlayer();
//        }
//        try {
//            mediaPlayer.reset();
//            mediaPlayer.setDataSource(recordingFile.getAbsolutePath());
//            mediaPlayer.setDisplay(previewSurfaceView.getHolder());
//            mediaPlayer.prepare();
//            mediaPlayer.start();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mp) {
//                if (previewBtn != null) {
//                    previewBtn.setVisibility(View.VISIBLE);
//                }
//            }
//        });
//    }

//    public void stopPreviewRecording() {
//        try {
//            if (mediaPlayer.isPlaying()) {
//                mediaPlayer.pause();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public static int getScreenRotation(int screenRotation) {
        int degrees = 0;
        switch (screenRotation) {
        case Surface.ROTATION_0:
            degrees = 0;
            break;
        case Surface.ROTATION_90:
            degrees = 90;
            break;
        case Surface.ROTATION_180:
            degrees = 180;
            break;
        case Surface.ROTATION_270:
            degrees = 270;
            break;
        }
        return degrees;
    }

    public static int getCameraOrientation(int cameraId) {
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, cameraInfo);
        return cameraInfo.orientation;
    }

    public static int getCameraOrientationHint(int deviceOrientation, int cameraId) {
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, cameraInfo);

        if (deviceOrientation < 0) {
            deviceOrientation = 0;
        }
        int screenRotation = 360 - deviceOrientation;
        int degrees = screenRotation;

        int result;
        if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (cameraInfo.orientation + degrees) % 360;
        } else {  // back-facing
            result = (cameraInfo.orientation - degrees + 360) % 360;
        }
        VideoUtils.log(TAG, "getCameraOrientationHint: device=" + deviceOrientation
                + " camera=" + cameraId + ":" + cameraInfo.orientation + "->" + result);
        return result;
    }

    public static int getCameraDisplayOrientation(int screenRotation, int cameraId) {
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, cameraInfo);
        int degrees = screenRotation;
        int result;
        if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (cameraInfo.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (cameraInfo.orientation - degrees + 360) % 360;
        }
        VideoUtils.log(TAG, "getCameraDisplayOrientation: screen=" + screenRotation
                + " camera=" + cameraId + ":" + cameraInfo.orientation + "->" + result);
        return result;
    }

    public static void setCameraDisplayOrientation(int screenRotation,
            int cameraId, Camera camera) {
        int orientation = getCameraDisplayOrientation(screenRotation, cameraId);
        camera.setDisplayOrientation(orientation);
    }

    private OrientationEventListener orientationEventListener;
    public static final int ORIENTATION_HYSTERESIS = 15;
    private int deviceOrientation = OrientationEventListener.ORIENTATION_UNKNOWN;
    private OrientationListener orientationListener;

    private void initOrientationEventListener() {
        orientationEventListener = new OrientationEventListener(context) {
            @Override
            public void onOrientationChanged(int orientation) {
                if (isRecording || orientation == ORIENTATION_UNKNOWN) {
                    return;
                }
                VideoUtils.log("TEST", "onOrientationChanged "
                        + deviceOrientation + "->" + orientation);
                notifyDeviceOrientationChanged(orientation);
            }
        };
        enableOrientationEventListener();
    }

    public void setOrientationListener(OrientationListener listener) {
        orientationListener = listener;
    }

    public void enableOrientationEventListener() {
        orientationEventListener.enable();
    }

    public void disableOrientationEventListener() {
        orientationEventListener.disable();
    }

    public static int normalizeDeviceOrientation(int newOrientation, int lastOrientation) {
        boolean isOrientationChanged = false;
        if (lastOrientation == OrientationEventListener.ORIENTATION_UNKNOWN) {
            isOrientationChanged = true;
        } else {
            int dist = Math.abs(newOrientation - lastOrientation);
            dist = Math.min(dist, 360 - dist);
            isOrientationChanged = (dist >= 45 + ORIENTATION_HYSTERESIS);
        }
        int result = lastOrientation;
        if (isOrientationChanged) {
            result = ((newOrientation + 45) / 90 * 90) % 360;
        }
        return result;
    }

    private void notifyDeviceOrientationChanged(int newOrientation) {
        if (newOrientation == deviceOrientation) {
            return;
        }

        int lastOrientation = deviceOrientation;
        deviceOrientation = normalizeDeviceOrientation(newOrientation, lastOrientation);
        if (deviceOrientation != lastOrientation) {
            VideoUtils.log("TEST", "screen=" + getScreenRotation()
                    + " " + lastOrientation + "->" + deviceOrientation);
        }

        if (orientationListener != null) {
            orientationListener.onOrientationChanged(lastOrientation, deviceOrientation);
        }
    }

    public float getRotationOffset(float deviceOrientation) {
        return getRotationOffset(context, deviceOrientation);
    }

    public static float getRotationOffset(Context context, float deviceOrientation) {
        deviceOrientation = (deviceOrientation + getScreenRotation(context)) % 360;
        return (360 - deviceOrientation) % 360;
    }

    public RotateAnimation getRotateAnimation(float fromDegrees, float toDegrees) {
        return getRotateAnimation(context, fromDegrees, toDegrees);
    }

    public static RotateAnimation getRotateAnimation(Context context, float fromDegrees,
                                                     float toDegrees) {
        fromDegrees = getRotationOffset(context, fromDegrees);
        toDegrees = getRotationOffset(context, toDegrees);

        RotateAnimation animation = new RotateAnimation(fromDegrees, toDegrees,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setFillAfter(true);
        animation.setFillEnabled(true);
        animation.setDuration(200);
        return animation;
    }

    public interface RecorderListener {
        public void onRecorderPrepare();

        public void onRecorderStart();

        public void onRecorderFinish(String filePath);

        public void onRecorderStop();

        public void onRecorderCancel();

        public void onRecorderError(Exception e);
    }

    public interface OrientationListener {
        public void onOrientationChanged(int from, int to);
    }

    private static class Size {
        public int width;
        public int height;

        public Size(int w, int h) {
            width = w;
            height = h;
        }
    }

}




