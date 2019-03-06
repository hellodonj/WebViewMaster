package com.icedcap.dubbing;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.icedcap.dubbing.audio.AudioPlayHelper;
import com.icedcap.dubbing.entity.DubbingEntity;
import com.icedcap.dubbing.entity.SrtEntity;
import com.icedcap.dubbing.listener.OnVideoEventListener;
import com.icedcap.dubbing.utils.ProcessUtils;
import com.icedcap.dubbing.utils.MediaUtil;
import com.icedcap.dubbing.view.DubbingVideoView;
import com.icedcap.dubbing.view.PreviewSubtitleView;
import com.lqwawa.apps.views.lrcview.LrcEntry;
import com.lqwawa.apps.views.lrcview.LrcView;
import com.lqwawa.client.pojo.StudyResPropType;

import java.util.ArrayList;
import java.util.List;

import static com.icedcap.dubbing.view.DubbingVideoView.MODE_FINALLY_REVIEW;


public class DubbingPreviewActivity extends Activity implements View.OnClickListener, AudioPlayHelper.OnAudioRecordPlaybackListener {

    private static final String EXTRA_VIDEO_FILE_PATH_KEY = "extra-video-file-path-key";
    private static final String EXTRA_BACKGROUND_FILE_PATH_KEY = "extra-background-file-path-key";
    private static final String EXTRA_SRT_SUBTITLE_KEY = "extra-srt-subtitle-key";
    private static final String EXTRA_RECORD_LIST_FILE_PATH_KEY = "extra-record-list-file-path-key";

    private String mVideoFilePath;
    private String mBackgroundFilePath;
    private List<SrtEntity> mSRTEntities;
    private long mDuration;
    private float mPersonalVolume = 0.5f;
    private float mBackgroundVolume = 0.5f;
    private AudioPlayHelper mAudioHelper;
    private DubbingVideoView mDubbingVideoView;
    private TextView mTime;
    private TextView mRbTime;
    private ProgressBar mProgressBar;
    private PreviewSubtitleView mSubtitleView;
    private LrcView lrcView;
    private SeekBar seekBar;
    private View mArtProcess;
    private List<String> mRecordListPath;
    private int videoTime;
    private static long start;

    public static void launch(Activity who, String videoFile,
                              String backgroundFile, List<SrtEntity> entity, List<String> listRecordFile) {
        start = System.currentTimeMillis();
        Intent intent = new Intent(who, DubbingPreviewActivity.class);
        intent.putExtra(EXTRA_VIDEO_FILE_PATH_KEY, videoFile);
        intent.putExtra(EXTRA_BACKGROUND_FILE_PATH_KEY, backgroundFile);
        intent.putStringArrayListExtra(EXTRA_RECORD_LIST_FILE_PATH_KEY, (ArrayList<String>) listRecordFile);
        intent.putParcelableArrayListExtra(EXTRA_SRT_SUBTITLE_KEY, (ArrayList) entity);
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
        mRbTime = (TextView) findViewById(R.id.rb_video_time);
        mProgressBar = (ProgressBar) findViewById(R.id.progress);
        mSubtitleView = (PreviewSubtitleView) findViewById(R.id.preview_subtitle_text_view);
        //通篇配音
        lrcView = (LrcView) findViewById(R.id.lrc_view);
        seekBar = (SeekBar) findViewById(R.id.seekbar);
        mProgressBar.setVisibility(View.VISIBLE);
        seekBar.setVisibility(View.GONE);
    }

    private void initData() {
        Intent extraData = getIntent();
        mVideoFilePath = extraData.getStringExtra(EXTRA_VIDEO_FILE_PATH_KEY);
        mBackgroundFilePath = extraData.getStringExtra(EXTRA_BACKGROUND_FILE_PATH_KEY);
        mSRTEntities = extraData.getParcelableArrayListExtra(EXTRA_SRT_SUBTITLE_KEY);
        mRecordListPath = extraData.getStringArrayListExtra(EXTRA_RECORD_LIST_FILE_PATH_KEY);
        mSubtitleView.setSRTEntities(mSRTEntities);
        mAudioHelper = new AudioPlayHelper(this);
    }

    private void matchingLrcText() {
        if (mSRTEntities == null || mSRTEntities.size() == 0){
            return;
        }
        List<LrcEntry> lrcEntries = new ArrayList<>();
        LrcEntry lrcEntry = null;
        for (int i = 0; i < mSRTEntities.size(); i++) {
            SrtEntity dubbingEntity = mSRTEntities.get(i);
            lrcEntry = new LrcEntry(dubbingEntity.getStartTime(), dubbingEntity.getContent());
            lrcEntries.add(lrcEntry);
        }
        videoTime = mSRTEntities.get(mSRTEntities.size()-1).getEndTime();
        lrcView.onLrcLoaded(lrcEntries);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mArtProcess.setVisibility(View.GONE);
        mDubbingVideoView.onResume();
        long end = System.currentTimeMillis();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mDubbingVideoView.onPause();
        mAudioHelper.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mArtProcess.setVisibility(View.GONE);
    }

    private void initVideoView() {
        mDubbingVideoView.setPara(mVideoFilePath, mBackgroundFilePath);
        mDubbingVideoView.findViewById(R.id.preview_text_view).setVisibility(View.GONE);
        mDubbingVideoView.setMode(MODE_FINALLY_REVIEW);
        mDubbingVideoView.setType(1);
        mDubbingVideoView.onResume();
        mDubbingVideoView.setOnEventListener(new OnVideoEventListener() {
            @Override
            public void onVideoPrepared(long duration) {
                seekBar.setProgress(0);
                mDuration = duration;
                mTime.setText(MediaUtil.generateTime(0, duration));
                mRbTime.setText(MediaUtil.generateTime(0, duration));
            }

            @Override
            public boolean onPlayTimeChanged(long playTime, long totalTime, int videoMode) {
                refreshTime(playTime, totalTime);
                return true;
            }

            @Override
            public void onFinalReviewComplete() {
                resetTime();
            }

            @Override
            public void onWhiteVideoPlay() {
                mAudioHelper.playCombineAudio(mBackgroundFilePath,
                        mPersonalVolume,
                        mBackgroundVolume, mRecordListPath);
            }

            @Override
            public void onWhiteVideoStop() {
                mAudioHelper.stopCombineAudio();
                resetTime();
            }

            @Override
            public void reset(boolean keepStatus) {
                mProgressBar.setProgress(0);
            }

            @Override
            public int fixThePlayMode() {
                return MODE_FINALLY_REVIEW;
            }
        });
    }

    private void initSeekBarView(){
        seekBar.setMax(videoTime);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                lrcView.updateTime(seekBar.getProgress());
            }
        });
    }

    private void refreshTime(long playTime, long totalTime) {
        String str = MediaUtil.generateTime(playTime, totalTime);
        mTime.setText(str);
        mRbTime.setText(str);
        int i = (int) (100L * playTime / totalTime);
        mProgressBar.setProgress(i);
//        mSubtitleView.processTime((int) playTime);
        lrcView.updateTime(playTime);
        seekBar.setProgress((int) playTime);
    }

    private void resetTime() {
        mTime.setText(MediaUtil.generateTime(0, mDuration));
        mRbTime.setText(MediaUtil.generateTime(0, mDuration));
        mProgressBar.setProgress(0);
//        mSubtitleView.reset();
        lrcView.updateTime(0);
        seekBar.setProgress(0);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.back) {
            onBackPressed();
        } else if (i == R.id.complete) {
            mDubbingVideoView.onPause();
            mAudioHelper.onPause();

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
