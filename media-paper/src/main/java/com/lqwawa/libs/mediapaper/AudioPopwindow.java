package com.lqwawa.libs.mediapaper;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import com.lqwawa.apps.views.ContainsEmojiEditText;
import com.osastudio.common.utils.TipMsgHelper;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by E450 on 2016/12/19.
 */

public class AudioPopwindow extends PopupWindow implements View.OnClickListener {

    public static final String DATE_PATTERN_yyyyMMdd_HHmmss = "yyyyMMdd_HHmmss";

    public static final int RECORD_STATE_PREPARE = 0;
    public static final int RECORD_STATE_RECORDING = 1;
    public static final int RECORD_STATE_FINISH = 2;
    public static final int PLAY_STATE_PREPARE = 0;
    public static final int PLAY_STATE_PLAYING = 1;
    public static final int PLAY_STATE_PAUSE = 2;
    private int recordState = 0;
    private int playState = 0;

    private Activity mContext;
    private LayoutInflater inflater;
    private View mRootView;
    private TextView cancleBtn;
    private LinearLayout reRecodBtn;
    private TextView uploadBtn;
    private ImageView recordIcon;
    private TextView recordStateiew;
    private Chronometer mRecDuration;
    private LinearLayout playTimeLayout;
    private LinearLayout fileNameLayout;
    private ContainsEmojiEditText fileNameEditText;
    private TextView playTimeView;
    private TextView recordTimeView;
    private SeekBar seekBar;

    private MediaRecorder mMediaRecorder = null;
    private MediaPlayer mediaPlayer = null;
    private File myRecAudioFile = null;
    public OnUploadListener onUploadListener;
    private OnDistroyListener onDistroyListener;
    private boolean isFromMediapaper;
    private String mPath = null;
    private int mMiss = 0;

    public AudioPopwindow(final Activity context, String saveFolderPath, OnUploadListener onUploadListener) {
        mContext = context;
        mPath = saveFolderPath;
        this.onUploadListener = onUploadListener;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRootView = inflater.inflate(R.layout.layout_recordview_popwindow, null);
        initView();
        setProperty();
    }

    public AudioPopwindow(final Activity context, String saveFolderPath, OnUploadListener
            onUploadListener, OnDistroyListener onDistroyListener) {
        this(context, saveFolderPath, onUploadListener);
        this.onDistroyListener = onDistroyListener;
    }

    public AudioPopwindow(final Activity context, String saveFolderPath, OnUploadListener
            onUploadListener, boolean isFromMediapaper) {
        this(context, saveFolderPath, onUploadListener);
        this.isFromMediapaper = isFromMediapaper;
    }


    public void checkViewStage() {
        seekBar.setVisibility(View.GONE);
        if (recordState == RECORD_STATE_PREPARE) {
            playTimeLayout.setVisibility(View.GONE);
            mRecDuration.setVisibility(View.VISIBLE);
            recordStateiew.setText(R.string.mp_click_record);
            recordIcon.setImageResource(R.drawable.prepare_record_icon);
            fileNameLayout.setVisibility(View.GONE);
            reRecodBtn.setVisibility(View.GONE);
            uploadBtn.setVisibility(View.GONE);
        } else if (recordState == RECORD_STATE_RECORDING) {
            playTimeLayout.setVisibility(View.GONE);
            mRecDuration.setVisibility(View.VISIBLE);
            recordStateiew.setText(R.string.mp_finish_record);
            recordIcon.setImageResource(R.drawable.mp_recording_icon);
            fileNameLayout.setVisibility(View.GONE);
            reRecodBtn.setVisibility(View.GONE);
            uploadBtn.setVisibility(View.GONE);
        } else {
            mRecDuration.setVisibility(View.GONE);
            playTimeLayout.setVisibility(View.VISIBLE);
            recordStateiew.setText(R.string.mp_play_recodr);
            seekBar.setVisibility(View.VISIBLE);
            if (isFromMediapaper) {
                fileNameLayout.setVisibility(View.GONE);
            } else {
                fileNameLayout.setVisibility(View.VISIBLE);
            }
            reRecodBtn.setVisibility(View.VISIBLE);
            uploadBtn.setVisibility(View.VISIBLE);
            if (playState == PLAY_STATE_PREPARE) {
                recordIcon.setImageResource(R.drawable.mp_play_record_icon);
            } else if (playState == PLAY_STATE_PAUSE) {
                recordIcon.setImageResource(R.drawable.mp_play_record_icon);
            } else {
                recordIcon.setImageResource(R.drawable.mp_audio_pause_logo);
            }
        }
    }

    public void initView() {
        cancleBtn = (TextView) mRootView.findViewById(R.id.cancel_btn);
        mRootView.findViewById(R.id.spare_part).setOnClickListener(this);
        reRecodBtn = (LinearLayout) mRootView.findViewById(R.id.rerecord_btn);
        uploadBtn = (TextView) mRootView.findViewById(R.id.upload_btn);
        if (isFromMediapaper) {
            uploadBtn.setText(R.string.mp_ok);
        }
        recordIcon = (ImageView) mRootView.findViewById(R.id.record_icon);
        recordStateiew = (TextView) mRootView.findViewById(R.id.record_state_tview);
        mRecDuration = (Chronometer) mRootView.findViewById(R.id.recduration);
        playTimeLayout = (LinearLayout) mRootView.findViewById(R.id.record_play_time_layout);
        fileNameLayout = (LinearLayout) mRootView.findViewById(R.id.file_name_layout);

        fileNameEditText = (ContainsEmojiEditText) mRootView.findViewById(R.id.file_name_edittext);

        playTimeView = (TextView) mRootView.findViewById(R.id.palay_time_view);
        recordTimeView = (TextView) mRootView.findViewById(R.id.record_time_view);
        seekBar = (SeekBar) mRootView.findViewById(R.id.audioseek);
        mRecDuration.setText(FormatMiss(0));
        playTimeView.setText(FormatMiss(0));
        cancleBtn.setOnClickListener(this);
        recordIcon.setOnClickListener(this);
        reRecodBtn.setOnClickListener(this);
        uploadBtn.setOnClickListener(this);
        checkViewStage();
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(final SeekBar seekBar, int progress, boolean fromUser) {
                if (!fromUser) return;
                if (mediaPlayer == null) {
                    if (TextUtils.isEmpty(mPath)) {
                        TipMsgHelper.ShowLMsg(mContext, mContext.getString(R.string
                                .mp_should_have_path));
                    }
                    File file = new File(mPath);
                    if (!file.exists()) {
                        TipMsgHelper.ShowLMsg(mContext, mContext.getString(R.string
                                .mp_File_does_not_exist));
                    }
                    mediaPlayer = new MediaPlayer();
                    try {
                        mediaPlayer.setDataSource(mPath);
                        mediaPlayer.prepareAsync();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //为播放器注册
                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        public void onPrepared(MediaPlayer mp) {
                            // TODO Auto-generated method stub
                            seekBar.setMax(mediaPlayer.getDuration());
                            handler.post(updateThread);
                            seekBar.setEnabled(true);
                        }
                    });
                    // 注册播放完毕后的监听事件
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        public void onCompletion(MediaPlayer mp) {
                            mediaPlayer.release();
                            mediaPlayer = null;
                            seekBar.setProgress(0);
                            playState = RECORD_STATE_PREPARE;
                            checkViewStage();
                            playTimeView.setText(FormatMiss(0));
                        }
                    });
                } else {
                    mediaPlayer.seekTo(seekBar.getProgress());
                    mediaPlayer.start();
                    resetPlayTimeViewTime();
                    if (playState == PLAY_STATE_PAUSE) {
                        playState = PLAY_STATE_PLAYING;
                    }
                    if (playState == PLAY_STATE_PREPARE) {
                        playState = PLAY_STATE_PLAYING;
                    }
                    checkViewStage();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                pauseAudio();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        //为自身的dismiss的时候结束录制的状态
        this.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                stopRecord();
                releaseAudio();
            }
        });


    }


    private void setProperty() {
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        this.checkViewStage();
        this.setContentView(mRootView);
        this.setWidth(ViewGroup.LayoutParams.FILL_PARENT);
        this.setHeight(ViewGroup.LayoutParams.FILL_PARENT);
        this.setFocusable(true);
        this.setTouchable(true);
        this.setOutsideTouchable(true);
        this.update();
        ColorDrawable dw = new ColorDrawable(0000000000);
        this.setBackgroundDrawable(dw);
    }

    public void showPopupMenu(View v) {
        if (!this.isShowing()) {
            this.showAtLocation(mRootView, Gravity.NO_GRAVITY, 0, 0);
        } else {
            this.dismiss();
        }
    }

    public void showPopupMenu() {
        if (!this.isShowing()) {
            this.showAtLocation(mRootView, Gravity.NO_GRAVITY, 0, 0);
        } else {
            this.dismiss();
        }
    }

    public void stopRecord() {
        try {
            if (mMediaRecorder != null) {

                //下面三个参数必须加，不加的话会奔溃，在mediarecorder.stop();
                //报错为：RuntimeException:stop failed
                mMediaRecorder.setOnErrorListener(null);
                mMediaRecorder.setOnInfoListener(null);
                mMediaRecorder.setPreviewDisplay(null);
                mMediaRecorder.stop();
                mMediaRecorder.release();
                mMediaRecorder = null;
                mRecDuration.stop();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopRecordOutSide() {

        try {
            if (mMediaRecorder != null) {
                recordTimeView.setText(FormatMiss(mMiss));
                recordState = RECORD_STATE_FINISH;
                checkViewStage();
                createFileName();
                //下面三个参数必须加，不加的话会奔溃，在mediarecorder.stop();
                //报错为：RuntimeException:stop failed
                mMediaRecorder.setOnErrorListener(null);
                mMediaRecorder.setOnInfoListener(null);
                mMediaRecorder.setPreviewDisplay(null);
                mMediaRecorder.stop();
                mMediaRecorder.release();
                mMediaRecorder = null;
                mRecDuration.stop();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startRecord() {
        try {
            long dateTaken = System.currentTimeMillis();
            String strTempFile = Long.toString(dateTaken);

            myRecAudioFile = new File(mPath.substring(0, mPath.lastIndexOf(File.separator) + 1) + strTempFile + ".m4a");
            if (!myRecAudioFile.getParentFile().exists()) {
                myRecAudioFile.getParentFile().mkdirs();
            }
            mMediaRecorder = new MediaRecorder();
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mMediaRecorder.setOutputFile(myRecAudioFile.getAbsolutePath());
            mMediaRecorder.prepare();

            mRecDuration.setBase(SystemClock.elapsedRealtime());
            mRecDuration.start();
            mRecDuration.setText(FormatMiss(0));
            mMiss = 0;
            mRecDuration.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {

                @Override
                public void onChronometerTick(Chronometer arg0) {
                    mMiss++;
                    mRecDuration.setText(FormatMiss(mMiss));
                }
            });

            mMediaRecorder.start();
            mPath = myRecAudioFile.getPath();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            PaperUtils.showMessage(mContext, mContext.getString(R.string.error_record_msg));
            stopRecord();
            deleteRecordFile();
            e.printStackTrace();
        }
    }

    private void deleteRecordFile() {
        if (myRecAudioFile != null && myRecAudioFile.exists()) {
            myRecAudioFile.delete();
            myRecAudioFile = null;
        }
    }

    // 将秒转化成小时分钟秒
    public String FormatMiss(int miss) {
        String hh = miss / 3600 > 9 ? miss / 3600 + "" : "0" + miss / 3600;
        String mm = (miss % 3600) / 60 > 9 ? (miss % 3600) / 60 + "" : "0" + (miss % 3600) / 60;
        String ss = (miss % 3600) % 60 > 9 ? (miss % 3600) % 60 + "" : "0" + (miss % 3600) % 60;
        return hh + ":" + mm + ":" + ss;
    }

    private void destroyView(View view) {
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (AudioPopwindow.this != null)
                    AudioPopwindow.this.dismiss();
                continuePlaytMedia();
            }
        }, 500);
    }


    private void createFileName() {
        if (TextUtils.isEmpty(mPath)) {
            return;
        }
        File file = new File(mPath);
        if (file.exists()) {
            long time ;
            String fileName = getFileNameFromPath(mPath);
            String title = fileName.substring(0, fileName.lastIndexOf("."));
            if (!TextUtils.isEmpty(title) && TextUtils.isDigitsOnly(title)) {
                time = Long.parseLong(title);
                Date date = new Date(time);
                SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN_yyyyMMdd_HHmmss);
                fileName = "AUD_" + sdf.format(date) + ".m4a";
                File newFile = new File(file.getParent(), fileName);
                file.renameTo(newFile);
                mPath = newFile.getAbsolutePath();
                String shortFileName = removeFileNameSuffix(fileName);
                fileNameEditText.setText(shortFileName);
                fileNameEditText.setSelection(shortFileName.length());
            }
        }

    }

    Handler handler = new Handler();
    Runnable updateThread = new Runnable() {
        public void run() {
            // 获得歌曲现在播放位置并设置成播放进度条的值
            if (mediaPlayer != null) {
                seekBar.setProgress(mediaPlayer.getCurrentPosition());
                playTimeView.setText(FormatMiss(mediaPlayer.getCurrentPosition() / 1000));
                // 每次延迟100毫秒再启动线程
                handler.postDelayed(updateThread, 100);
            } else {
                if (playState == PLAY_STATE_PREPARE) {
                    mRecDuration.setText(FormatMiss(0));
                    recordTimeView.setText(FormatMiss(0));
                    playTimeView.setText(FormatMiss(0));
                    seekBar.setProgress(0);
                } else {
                    seekBar.setProgress(seekBar.getMax());
                    playTimeView.setText(FormatMiss(seekBar.getMax() / 1000));
                    handler.removeCallbacks(updateThread);
                }
            }
        }
    };

    private void resetPlayTimeViewTime() {
        if (mediaPlayer != null) {
            int i = mediaPlayer.getCurrentPosition();
            i /= 1000;
            int minute = i / 60;
            int hour = minute / 60;
            int second = i % 60;
            minute %= 60;
            minute = minute + hour * 60;
            if (minute < 0 || second < 0) {
                playTimeView.setText("00:00:00");
            } else {
                playTimeView.setText(String.format("%02d:%02d:%02d", hour, minute, second));
            }
        }

    }


    void playAudio() throws Exception {
        if (TextUtils.isEmpty(mPath)) {
            TipMsgHelper.ShowLMsg(mContext, mContext.getString(R.string.mp_should_have_path));
        }
        File file = new File(mPath);
        if (!file.exists()) {
            TipMsgHelper.ShowLMsg(mContext, mContext.getString(R.string.mp_File_does_not_exist));
        }
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setDataSource(mPath);
        mediaPlayer.prepareAsync();
        //为播放器注册
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            public void onPrepared(MediaPlayer mp) {
                // TODO Auto-generated method stub
                mediaPlayer.start();
                seekBar.setMax(mediaPlayer.getDuration());
                handler.post(updateThread);
                seekBar.setEnabled(true);
            }
        });

        // 注册播放完毕后的监听事件
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            public void onCompletion(MediaPlayer mp) {
                //延迟200ms，确保播放结束进度条更新到最右端
                mediaPlayer.release();
                mediaPlayer = null;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        seekBar.setProgress(0);
                        playState = RECORD_STATE_PREPARE;
                        checkViewStage();
                        playTimeView.setText(FormatMiss(0));
                    }
                }, 200);

            }
        });

    }

    private void releaseAudio() {
        // 暂停
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void pauseAudio() {
        // 暂停
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }

        //seekBar.setEnabled(false);
    }

    public void pauseAudioOutSide() {
        // 暂停
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            playState = PLAY_STATE_PAUSE;
            checkViewStage();
        }
        //seekBar.setEnabled(false);
    }

    private void rePlayAudio() {
        //续播
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
//       seekBar.setEnabled(true);

    }

    private void exit(View v) {
        stopRecord();
        releaseAudio();
        deleteRecordFile();
        destroyView(v);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.spare_part) {
            //点击外部不消失
            //exit(v);
        } else if (v.getId() == R.id.cancel_btn) {
            //点击外部不消失
            exit(v);
        } else if (v.getId() == R.id.record_icon) {
            if (recordState == RECORD_STATE_PREPARE) {
                recordState = RECORD_STATE_RECORDING;
                checkViewStage();
                startRecord();
            } else if (recordState == RECORD_STATE_RECORDING) {
                recordTimeView.setText(FormatMiss(mMiss));
                recordState = RECORD_STATE_FINISH;
                checkViewStage();
                if (!isFromMediapaper) {
                    createFileName();
                }
                stopRecord();
            } else {
                if (playState == PLAY_STATE_PREPARE) {
                    playState = PLAY_STATE_PLAYING;
                    try {
                        playAudio();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (playState == PLAY_STATE_PLAYING) {
                    playState = PLAY_STATE_PAUSE;
                    pauseAudio();
                } else if (playState == PLAY_STATE_PAUSE) {
                    playState = PLAY_STATE_PLAYING;
                    rePlayAudio();
                }
                checkViewStage();
            }
        } else if (v.getId() == R.id.rerecord_btn) {
            recordState = RECORD_STATE_PREPARE;
            playState = PLAY_STATE_PREPARE;
            checkViewStage();
            deleteRecordFile();
            mRecDuration.setText(FormatMiss(0));
            recordTimeView.setText(FormatMiss(0));
            playTimeView.setText(FormatMiss(0));
            seekBar.setProgress(0);
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }
        } else if (v.getId() == R.id.upload_btn) {
            pauseAudioOutSide();//上传时，停止播放
            File file = new File(mPath);
            if (!isFromMediapaper) {
                String rawFileName = getFileNameFromPath(mPath);
                String fileName = fileNameEditText.getText().toString() + ".m4a";
                if (!rawFileName.equals(fileName)) {
                    File newFile = new File(file.getParent(), fileName);
                    file.renameTo(newFile);
                    mPath = newFile.getAbsolutePath();
                }
            }
            if (onUploadListener != null) {
                onUploadListener.onUpload(mPath);
            }
            destroyView(v);
        }
    }

    public interface OnUploadListener {
        void onUpload(String path);
    }

    private void continuePlaytMedia() {
        if (onDistroyListener != null) {
            onDistroyListener.onDestroy();

        }
    }

    public interface OnDistroyListener {
        void onDestroy();
    }

    public static String getFileNameFromPath(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        String tempPath = path;
        if (path.endsWith(File.separator)) {
            tempPath = path.substring(0, path.length() - 1);
        }
        int firstIndex = tempPath.lastIndexOf('/');
        String name = null;
        try {
            name = tempPath.subSequence(firstIndex + 1, tempPath.length())
                    .toString();
        } catch (Exception e) {
            name = null;
            e.printStackTrace();
        }
        return name;
    }

    private String removeFileNameSuffix(String filename) {
        if (TextUtils.isEmpty(filename)) {
            return null;
        }
        int index = filename.lastIndexOf('.');
        String name = filename;
        if(index > 0) {
            name = filename.substring(0, index);
        }
        return name;
    }

    /**
     * 重新设定窗口大小
     *
     * @param ratio
     */
    public void resizePopupWindowWith(float ratio) {

        if (ratio <= 0) {
            return;
        }
        Display display = mContext.getWindowManager().getDefaultDisplay();
        int width = (int) (display.getWidth() * ratio);
        setWidth(width);
    }

    /**
     * 停止录制音频
     */
    public void stopRecordingAudio() {
        if (recordState == RECORD_STATE_RECORDING) {
            recordTimeView.setText(FormatMiss(mMiss));
            recordState = RECORD_STATE_FINISH;
            checkViewStage();
            if (!isFromMediapaper) {
                createFileName();
            }
            stopRecord();
        }
    }
}
