package com.icedcap.dubbing;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import com.icedcap.dubbing.audio.AudioPlayHelper;
import com.icedcap.dubbing.entity.SrtEntity;
import com.icedcap.dubbing.listener.OnVideoEventListener;
import com.icedcap.dubbing.utils.AudioMedia;
import com.icedcap.dubbing.utils.ProcessUtils;
import com.icedcap.dubbing.utils.MediaUtil;
import com.icedcap.dubbing.view.DubbingVideoView;
import com.lqwawa.apps.views.lrcview.LrcEntry;
import com.lqwawa.apps.views.lrcview.LrcView;
import com.lqwawa.client.pojo.StudyResPropType;
import com.osastudio.common.utils.FileUtils;
import java.util.ArrayList;
import java.util.List;

import static com.icedcap.dubbing.view.DubbingVideoView.MODE_FINALLY_REVIEW;


public class DubbingPreviewActivity extends Activity implements View.OnClickListener, AudioPlayHelper.OnAudioRecordPlaybackListener {

    private static final String EXTRA_VIDEO_FILE_PATH_KEY = "extra-video-file-path-key";
    private static final String EXTRA_BACKGROUND_FILE_PATH_KEY = "extra-background-file-path-key";
    private static final String EXTRA_SRT_SUBTITLE_KEY = "extra-srt-subtitle-key";
    private static final String EXTRA_RECORD_LIST_FILE_PATH_KEY = "extra-record-list-file-path-key";
    private static final String EXTRA_RECORD_AUDIO_PATH = "extra_record_audio_path";
    private String mVideoFilePath;
    private String mBackgroundFilePath;
    private List<SrtEntity> mSRTEntities;
    private long mDuration;
    private float mPersonalVolume = 0.5f;
    private float mBackgroundVolume = 0.5f;
    private DubbingVideoView mDubbingVideoView;
    private TextView mTime;
    private TextView currentTimeTextView;
    private TextView totalTimeTextView;
    private LrcView lrcView;
    private SeekBar seekBar;
    private View mArtProcess;
    private List<String> mRecordListPath;
    private int videoTime;
    private static long start;
    private int resPropertyValue;
    private long playTime;
    private String recordAudioPath;
    private AudioMedia recordAudioMedia;

    public static void launch(Activity who,
                              String videoFile,
                              String backgroundFile,
                              List<SrtEntity> entity,
                              List<String> listRecordFile,
                              String recordAudioPath,
                              int resPropertyValue) {
        start = System.currentTimeMillis();
        Intent intent = new Intent(who, DubbingPreviewActivity.class);
        intent.putExtra(EXTRA_VIDEO_FILE_PATH_KEY, videoFile);
        intent.putExtra(EXTRA_BACKGROUND_FILE_PATH_KEY, backgroundFile);
        intent.putStringArrayListExtra(EXTRA_RECORD_LIST_FILE_PATH_KEY, (ArrayList<String>) listRecordFile);
        intent.putParcelableArrayListExtra(EXTRA_SRT_SUBTITLE_KEY, (ArrayList) entity);
        intent.putExtra(DubbingActivity.Constant.VIDEO_RES_PROPERTIES_VALUE, resPropertyValue);
        intent.putExtra(EXTRA_RECORD_AUDIO_PATH,recordAudioPath);
        who.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_dubbing_preview);
        init();
        initData();
        matchingLrcText();
        initAudioMedia();
        initVideoView();
        initSeekBarView();
    }

    private void init() {
        mArtProcess = findViewById(R.id.art_process_view);
        final ProgressBar pb = (ProgressBar) findViewById(R.id.art_progress_bar);
        pb.getIndeterminateDrawable().setColorFilter(0xFFCECECE, android.graphics.PorterDuff.Mode.MULTIPLY);
        findViewById(R.id.back).setOnClickListener(this);
        findViewById(R.id.complete).setOnClickListener(this);

        mDubbingVideoView = (DubbingVideoView) findViewById(R.id.videoView);
        mTime = (TextView) findViewById(R.id.video_time);
        currentTimeTextView = (TextView) findViewById(R.id.tv_current_time);
        totalTimeTextView = (TextView) findViewById(R.id.tv_total_time);
        //通篇配音
        lrcView = (LrcView) findViewById(R.id.lrc_view);
        lrcView.setOnPlayClickListener(new LrcView.OnPlayClickListener() {
            @Override
            public boolean onPlayClick(long time) {
                return true;
            }
        });
        seekBar = (SeekBar) findViewById(R.id.seekbar);
        seekBar.setVisibility(View.VISIBLE);
    }

    private void initData() {
        Intent extraData = getIntent();
        if (extraData != null) {
            mVideoFilePath = extraData.getStringExtra(EXTRA_VIDEO_FILE_PATH_KEY);
            mBackgroundFilePath = extraData.getStringExtra(EXTRA_BACKGROUND_FILE_PATH_KEY);
            mSRTEntities = extraData.getParcelableArrayListExtra(EXTRA_SRT_SUBTITLE_KEY);
            mRecordListPath = extraData.getStringArrayListExtra(EXTRA_RECORD_LIST_FILE_PATH_KEY);
            resPropertyValue =
                    extraData.getIntExtra(DubbingActivity.Constant.VIDEO_RES_PROPERTIES_VALUE, StudyResPropType.DUBBING_BY_SENTENCE);
            recordAudioPath = extraData.getStringExtra(EXTRA_RECORD_AUDIO_PATH);
        }
    }

    private void matchingLrcText() {
        if (mSRTEntities == null || mSRTEntities.size() == 0) {
            return;
        }
        List<LrcEntry> lrcEntries = new ArrayList<>();
        LrcEntry lrcEntry = null;
        for (int i = 0; i < mSRTEntities.size(); i++) {
            SrtEntity dubbingEntity = mSRTEntities.get(i);
            String content = dubbingEntity.getContent();
            if (!TextUtils.isEmpty(content)) {
                content = getSubStringContent(content);
            }
            dubbingEntity.setContent(content);
            lrcEntry = new LrcEntry(dubbingEntity.getStartTime(), dubbingEntity.getContent());
            lrcEntries.add(lrcEntry);
        }
        videoTime = mSRTEntities.get(mSRTEntities.size() - 1).getEndTime();
        lrcView.onLrcLoaded(lrcEntries);
    }

    private String getSubStringContent(String content) {
        if (content.endsWith("\n")) {
            content = content.substring(0, content.lastIndexOf("\n"));
            content = getSubStringContent(content);
        }
        return content;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mArtProcess.setVisibility(View.GONE);
        long end = System.currentTimeMillis();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mDubbingVideoView.isPlaying()) {
            mDubbingVideoView.onPause();
        }
        if (recordAudioMedia != null){
            recordAudioMedia.pause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mArtProcess.setVisibility(View.GONE);
        mDubbingVideoView.stop();
        recordAudioMedia.stop();
    }

    private void initAudioMedia(){
        recordAudioMedia = new AudioMedia(recordAudioPath);
    }

    private void initVideoView() {
        mDubbingVideoView.setPara(mVideoFilePath, mBackgroundFilePath);
        mDubbingVideoView.findViewById(R.id.preview_text_view).setVisibility(View.GONE);
        mDubbingVideoView.setMode(MODE_FINALLY_REVIEW);
        mDubbingVideoView.setType(1);
        mDubbingVideoView.setIsSupportPause(true);
//        mDubbingVideoView.onResume();
        mDubbingVideoView.setOnEventListener(new OnVideoEventListener() {
            @Override
            public void onVideoPrepared(long duration) {
                seekBar.setProgress(0);
                seekBar.setMax((int) duration);
                mDuration = duration;
                mTime.setText(MediaUtil.generateTime(0, duration));
                currentTimeTextView.setText(MediaUtil.generateTime(0));
                totalTimeTextView.setText(MediaUtil.generateTime(duration));
                mSRTEntities.get(mSRTEntities.size() - 1).setEndTime((int) duration);
                if (duration > 0) {
                    mDubbingVideoView.setEndTime((int) duration);
                }
            }

            @Override
            public boolean onPlayTimeChanged(long playTime, long totalTime, int videoMode) {
                refreshTime(playTime, totalTime);
                return true;
            }

            @Override
            public void onFinalReviewComplete() {
                recordAudioMedia.stop();
                mDubbingVideoView.stop();
                resetTime();
            }

            @Override
            public void onVideoPause() {
                //暂停播放
                if (recordAudioMedia.isPlaying()){
                    recordAudioMedia.pause();
                }
            }

            @Override
            public void onVideoResume() {
                recordAudioMedia.seekTo((int) playTime);
                recordAudioMedia.start();
            }

            @Override
            public void onWhiteVideoPlay() {
               recordAudioMedia.start();
            }

            @Override
            public void reset(boolean keepStatus) {
                seekBar.setProgress(0);
            }

            @Override
            public int fixThePlayMode() {
                return MODE_FINALLY_REVIEW;
            }

            @Override
            public int onPreviewPrepared() {
                return (int) playTime;
            }
        });
    }

    private void initSeekBarView() {
        seekBar.setMax(videoTime);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (mDubbingVideoView.isPlaying()) {
                    mDubbingVideoView.onPause();
                }
                if (recordAudioMedia.isPlaying()){
                    recordAudioMedia.pause();
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                updateSrtTime(seekBar.getProgress());
                recordAudioMedia.seekTo(seekBar.getProgress());
                recordAudioMedia.start();
                mDubbingVideoView.dubbingSeekTo(seekBar.getProgress());
                mDubbingVideoView.continuePlay();
            }
        });
    }

    private void refreshTime(long playTime, long totalTime) {
        this.playTime = playTime;
        String str = MediaUtil.generateTime(playTime, totalTime);
        mTime.setText(str);
        currentTimeTextView.setText(MediaUtil.generateTime(playTime));
        totalTimeTextView.setText(MediaUtil.generateTime(totalTime));
        seekBar.setProgress((int) playTime);
        updateSrtTime(playTime);
        seekBar.setProgress((int) playTime);
    }

    private void updateSrtTime(long playTime){
        int startTime = mSRTEntities.get(0).getStartTime();
        if (startTime - 1000 < playTime) {
            lrcView.updateTime(playTime);
        }
    }

    private void resetTime() {
        mTime.setText(MediaUtil.generateTime(0, mDuration));
        currentTimeTextView.setText(MediaUtil.generateTime(0));
        totalTimeTextView.setText(MediaUtil.generateTime(mDuration));
        seekBar.setProgress(0);
        lrcView.updateTime(0);
        seekBar.setProgress(0);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.back) {
            backPress();
        } else if (i == R.id.complete) {
            mDubbingVideoView.onPause();
            recordAudioMedia.pause();

            ProcessUtils processUtils = new ProcessUtils();
            processUtils.process(DubbingPreviewActivity.this, mRecordListPath, null,
                    mBackgroundFilePath, mVideoFilePath, new ProcessUtils.OnProcessListener() {
                        @Override
                        public void onProcessBegin() {
                            mArtProcess.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onProcessEnd(String videoPath) {
                            mArtProcess.setVisibility(View.GONE);

                        }
                    });

        }
    }

    private void backPress() {
        mDubbingVideoView.stop();
        recordAudioMedia.release();
        FileUtils.deleteFile(recordAudioPath);
        finish();
    }

    @Override
    public void onAudioDataReceived(long duration, long bytesRead) {

    }

    @Override
    public void onProgress(int pos) {

    }

    @Override
    public void onCompletion() {

    }
}
