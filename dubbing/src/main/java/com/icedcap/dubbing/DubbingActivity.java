package com.icedcap.dubbing;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.icedcap.dubbing.audio.AudioRecordHelper;
import com.icedcap.dubbing.entity.DubbingEntity;
import com.icedcap.dubbing.entity.SrtEntity;
import com.icedcap.dubbing.listener.OnVideoEventListener;
import com.icedcap.dubbing.utils.MediaUtil;
import com.icedcap.dubbing.utils.ProcessUtils;
import com.icedcap.dubbing.utils.SrtUtils;
import com.icedcap.dubbing.view.DubbingVideoView;
import com.icedcap.dubbing.view.DubbingItemView;
import com.lqwawa.apps.views.ExpandableTextView;
import com.lqwawa.apps.views.lrcview.LrcEntry;
import com.lqwawa.apps.views.lrcview.LrcView;
import com.lqwawa.apps.views.switchbutton.SwitchButton;
import com.lqwawa.client.pojo.StudyResPropType;
import com.lqwawa.tools.DensityUtils;
import com.lqwawa.tools.DialogHelper;
import com.oosic.apps.iemaker.base.evaluate.EvaluateHelper;
import com.oosic.apps.iemaker.base.evaluate.EvaluateItemResult;
import com.oosic.apps.iemaker.base.evaluate.EvaluateManager;
import com.oosic.apps.iemaker.base.onlineedit.CallbackListener;
import com.osastudio.common.utils.FileUtils;
import com.osastudio.common.utils.LogUtils;
import com.osastudio.common.utils.TipMsgHelper;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DubbingActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int PERMISSION_REQUEST_CODE = 0x520;
    private static final int UPDATE_PROGRESS = 0x1024;
    private static final int MATERIAL = 1;

    public interface Constant {
        String VIDEO_RESOURCE_URL_PATH = "video_resource_url_path";
        String VIDEO_BACKGROUND_VOICE = "video_background_voice";
        String VIDEO_SRT_TEXT = "video_srt_text";
        String MERGE_VIDEO_PATH = "merge_video_path";
        String DUBBING_ENTITY_LIST_DATA = "dubbing_entity_list";
        String HAS_REVIEW_COMMENT_PERMISSION = "has_review_comment_permission";
        String VIDEO_RES_PROPERTIES_VALUE = "video_res_properties_value";
    }

    private TextView currentTimeTextView;
    private TextView totalTimeTextView;
    private SeekBar seekBar;
    protected DubbingVideoView dubbingVideoView;
    private View artProcessView;
    private TextView previewTextView;
    private TextView confirmTextView;
    private GridView gridView;
    private CommonAdapter commonAdapter;
    private LinearLayout qDubbingDetailLayout;
    private TextView systemScoreView;
    private LinearLayout dubbingBySentenceLayout;
    private SwitchButton changDubbingTypeBtn;
    private TextView teacherScoreView;
    protected ExpandableTextView teacherReviewView;
    protected LinearLayout teacherOperationLayout;
    private ImageView wholeRecordImageV;
    private FrameLayout previewFl;
    private FrameLayout commitFl;
    private FrameLayout teacherReviewFl;
    private TextView teacherReviewTextV;
    private LrcView lrcView;
    private TextView listenSoundTextV;
    private ImageView backImageV;
    private List<String> permissions = new ArrayList<>();
    private AudioRecordHelper audioRecordHelper;

    List<SrtEntity> srtEntityList = new ArrayList<>();

    List<DubbingEntity> dubbingEntityList = new ArrayList<>();

    private long duration;
    private boolean isReviewing = false;
    protected boolean isDubbing;
    protected boolean isRecording;

    private long lastSeek;
    private long playTime;
    private long newPlayTime = 0;

    private int curPosition = 0;
    private int progress = 0;

    protected String videoFilePath;
    protected String studentCommitFilePath;
    protected String backgroundFilePath;
    protected String srtTextUrl;
    protected DialogHelper.LoadingDialog mLoadingDialog;
    protected boolean hasReviewPermission;
    protected boolean isOnlineOpen;
    protected JSONArray pageScoreArray;
    protected int teacherReviewScore;
    protected int systemScore;
    protected boolean hasVideoReview;
    protected String reviewComment;
    protected int resPropertyValue;
    private boolean checkDubbingBySentence;
    private int listenType; //0 表示播放的配音  1 表示原音
    private boolean isCheckGridViewItem;
    private boolean isSupportPause = true;
    private boolean loadVideoComplete;
    private boolean showDownTimeFlag = false;//显示倒计时
    private boolean isStopShow = true;//正在进行中

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_PROGRESS:
                    int progressMax = getProgressMax(dubbingEntityList, curPosition);
                    if (progress < progressMax) {
                        progress++;
                        dubbingEntityList.get(curPosition).setProgress(progress);
                        commonAdapter.notifyDataSetChanged();
                        handler.sendEmptyMessageDelayed(UPDATE_PROGRESS, 100);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        loadIntentData();
        setContentView(R.layout.activity_dubbing);
        if (!isOnlineOpen) {
            audioRecordHelper = new AudioRecordHelper(this, resPropertyValue,
                    new CallbackListener() {
                @Override
                public void onBack(Object result) {
                    if (result != null) {
                        int evalScore = (int) result;
                        dubbingEntityList.get(curPosition).setScore(evalScore);
                        dubbingEntityList.get(curPosition).setRecord(true);
                        dubbingEntityList.get(curPosition).setIsRecording(false);
                        isRecording = false;
                        isDubbing = false;
                        showDownTimeFlag = false;
                        isStopShow = true;
                        commonAdapter.notifyDataSetChanged();
                        if (resPropertyValue == StudyResPropType.DUBBING_BY_WHOLE) {
                            isSupportPause = true;
                            dubbingVideoView.setIsSupportPause(true);
                            if (lrcView != null) {
                                lrcView.resetLineColor();
                            }
                        }
                    }
                }
            }, new CallbackListener() {
                @Override
                public void onBack(Object result) {
                    if (result == null) {
                        return;
                    }
                    if (resPropertyValue == StudyResPropType.DUBBING_BY_WHOLE){
                        int evalScore = (int) result;
                        dubbingEntityList.get(curPosition).setScore(evalScore);
                        processQDubbingVideo();
                    } else {
                        String evalResult = (String) result;
                        if (!TextUtils.isEmpty(evalResult)) {
                            EvaluateItemResult evaluateItemResult =
                                    EvaluateManager.parseItemResult(3, evalResult);
                            SpannableString spannableString = null;
                            if (evaluateItemResult != null) {
                                spannableString = EvaluateHelper.getSpannableEvaluateItemText(evaluateItemResult, getResources().getColor(R.color.eval_default_color));
                            }
                            dubbingEntityList.get(curPosition).setEvalResult(spannableString);
                        }
                    }
                }
            });
        }

        checkPermissions();

        if (permissions.size() > 0) {
            String[] permissions = new String[this.permissions.size()];
            ActivityCompat.requestPermissions(this, this.permissions.toArray(permissions), PERMISSION_REQUEST_CODE);
        } else {
            initView();

        }
    }

    protected void loadIntentData() {
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.RECORD_AUDIO);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (artProcessView != null) {
            artProcessView.setVisibility(View.GONE);
        }
        if (dubbingVideoView != null) {
            dubbingVideoView.onResume();
        }
        if (!MediaUtil.isHasEnoughSdcardSpace(MediaUtil.getAvailableExternalMemorySize())) {
            TipMsgHelper.ShowMsg(DubbingActivity.this, R.string.str_save_space_small);
            dubbingVideoView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    System.exit(0);
                }
            }, 2000);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (dubbingVideoView != null) {
            dubbingVideoView.onPause();
        }
        if (isRecording) {
            isDubbing = false;
            dubbing(true);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (artProcessView != null) {
            artProcessView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dubbingVideoView != null) {
            dubbingVideoView.stop();
        }
        if (audioRecordHelper != null) {
            //删除应配音缓存的配音文件
            audioRecordHelper.deleteMp3FileList();
            if (isRecording) {
                audioRecordHelper.onStop();
            }
        }
    }

    public void setDubb() {
        if (resPropertyValue == StudyResPropType.DUBBING_BY_WHOLE) {
            wholeRecordImageV.setImageResource(R.drawable.icon_dubbing_recording);
        }
        audioRecordHelper.stopRecord();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE: {
                for (int result : grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        TipMsgHelper.ShowMsg(this, R.string.str_refuse_permission);
                        finish();
                        return;
                    }
                }
                initView();
            }
        }
    }

    @Override
    public void onBackPressed() {
        backPress();
    }

    private void backPress() {
        if (isOnlineOpen) {
            finish();
        } else {
            backDubbing();
        }
    }

    protected void backDubbing() {

    }

    private void initView() {
        artProcessView = findViewById(R.id.art_process_view);
        seekBar = (SeekBar) findViewById(R.id.seekbar);
        currentTimeTextView = (TextView) findViewById(R.id.tv_current_time);
        totalTimeTextView = (TextView) findViewById(R.id.tv_total_time);
        dubbingVideoView = (DubbingVideoView) findViewById(R.id.videoView);
        handleDataView();
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                Dialog dialog = showLoadingDialog();
                dialog.setCancelable(false);
            }

            @Override
            protected Void doInBackground(Void... params) {
                if (!isOnlineOpen) {
                    videoFilePath = downloadFile(videoFilePath);
                }
                if (!TextUtils.isEmpty(backgroundFilePath) && !isOnlineOpen) {
                    backgroundFilePath = downloadFile(backgroundFilePath);
                }
                srtEntityList = SrtUtils.processSrtFromFile(downloadFile(srtTextUrl));
                if (srtEntityList != null && !srtEntityList.isEmpty()) {
                    dubbingEntityList.clear();
                    for (int i = 0; i < srtEntityList.size(); i++) {
                        SrtEntity entity = srtEntityList.get(i);
                        if (entity != null) {
                            DubbingEntity dubbingEntity = new DubbingEntity(entity);
                            if (isOnlineOpen) {
                                handleOnlineVideoData(dubbingEntity, i);
                            }
                            dubbingEntityList.add(dubbingEntity);
                        }
                    }
                    updateDubbingItemProgress();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                dismissLoadingDialog();
                dubbingVideoView.setPara(isOnlineOpen ? studentCommitFilePath : videoFilePath, "",
                        false, 0, "", new VideoViewListener(), DubbingActivity.this);
//                if (resPropertyValue == StudyResPropType.DUBBING_BY_WHOLE || isOnlineOpen) {
                //播放状态直接到结束
                dubbingVideoView.setIsSupportPause(true);
                dubbingVideoView.startPlay(0, dubbingEntityList.get(dubbingEntityList.size() - 1).getEndTime());
//                } else {
//                    dubbingVideoView.startPlay(0, getVideoTime(dubbingEntityList, 0));
//                }
                matchingLrcText();
                updateViews();
                configOnClick();
            }
        }.execute();
    }

    private void configOnClick() {
        if (wholeRecordImageV == null) {
            return;
        }
        wholeRecordImageV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //开始配音
                if (isRecording) {
                    TipMsgHelper.ShowMsg(DubbingActivity.this, R.string.str_dubbing_recording);
                } else {
                    if (loadVideoComplete) {
                        isSupportPause = false;
                        dubbingVideoView.setIsSupportPause(false);
                        lrcView.resetLineColor();
                        lrcView.scrollTo(0);
                        onAudioRecord();
                    }
                }
            }
        });
    }

    protected void handleDataView() {
        qDubbingDetailLayout = (LinearLayout) findViewById(R.id.ll_q_detail);
        systemScoreView = (TextView) findViewById(R.id.tv_system_score);
        dubbingBySentenceLayout = (LinearLayout) findViewById(R.id.ll_show_dubbing_by_sentence);
        changDubbingTypeBtn = (SwitchButton) findViewById(R.id.sb_switch_btn);
        teacherScoreView = (TextView) findViewById(R.id.tv_teacher_review_score);
        teacherReviewView = (ExpandableTextView) findViewById(R.id.tv_teacher_comment);
        teacherOperationLayout = (LinearLayout) findViewById(R.id.ll_teacher_operation);
        previewTextView = (TextView) findViewById(R.id.tv_preview);
        previewTextView.setOnClickListener(this);
        confirmTextView = (TextView) findViewById(R.id.tv_confirm);
        confirmTextView.setOnClickListener(this);
        //单句配音
        gridView = (GridView) findViewById(R.id.grid_view);
        //通篇配音
        lrcView = (LrcView) findViewById(R.id.lrc_view);
        lrcView.setOnPlayClickListener(new LrcView.OnPlayClickListener() {
            @Override
            public boolean onPlayClick(long time) {
                return true;
            }
        });
        wholeRecordImageV = (ImageView) findViewById(R.id.iv_dubbing_record);
        previewFl = (FrameLayout) findViewById(R.id.fl_preview);
        commitFl = (FrameLayout) findViewById(R.id.fl_commit);
        teacherReviewFl = (FrameLayout) findViewById(R.id.fl_teacher_review);
        teacherReviewTextV = (TextView) findViewById(R.id.tv_teacher_review);
        //听原音
        listenSoundTextV = (TextView) findViewById(R.id.tv_listen_sound);
        listenSoundTextV.setOnClickListener(this);
        //返回键
        backImageV = (ImageView) findViewById(R.id.back);
        backImageV.setOnClickListener(this);
        showViewData();
        initSeekBarData();
    }

    private void initSeekBarData() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (dubbingVideoView != null) {
                    dubbingVideoView.onPause();
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (lrcView != null) {
                    updateSrtTime(seekBar.getProgress());
                }
                if (dubbingVideoView != null) {
                    dubbingVideoView.seekTo(seekBar.getProgress());
                    dubbingVideoView.play();
                }
                seekBar.setProgress(seekBar.getProgress());
            }
        });
        seekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return !isSupportPause;
            }
        });
    }

    protected void showViewData() {
        if (isOnlineOpen) {
            //在线打开隐藏预览原声的文字显示
            dubbingVideoView.findViewById(R.id.preview_text_view).setVisibility(View.GONE);
            qDubbingDetailLayout.setVisibility(View.VISIBLE);
            teacherReviewTextV.setOnClickListener(this);
            showReviewViewData();
            if (resPropertyValue == StudyResPropType.DUBBING_BY_SENTENCE) {
                dubbingBySentenceLayout.setVisibility(View.VISIBLE);
                changDubbingTypeBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        stopPlayRecordingAudio();
                        if (isChecked) {
                            //按句显示
                            checkDubbingBySentence = true;
                        } else {
                            //通篇显示
                            checkDubbingBySentence = false;
                        }
                        changeDubbingTypeShow(true);
                    }
                });
            }
        }

        if (resPropertyValue == StudyResPropType.DUBBING_BY_SENTENCE) {
            changeDubbingTypeShow(false);
        } else {
            isSupportPause = true;
            dubbingVideoView.setIsSupportPause(true);
            gridView.setVisibility(View.GONE);
            lrcView.setVisibility(View.VISIBLE);
            if (isOnlineOpen) {
                listenSoundTextV.setVisibility(View.VISIBLE);
            } else {
                wholeRecordImageV.setVisibility(View.VISIBLE);
                listenSoundTextV.setVisibility(View.GONE);
            }
        }
    }

    protected void showReviewViewData() {
        if (hasVideoReview) {
            teacherReviewFl.setVisibility(View.GONE);
            //老师点评了
            teacherOperationLayout.setVisibility(View.VISIBLE);
            systemScoreView.setText(String.valueOf(systemScore));
            teacherScoreView.setText(String.valueOf(teacherReviewScore));
            //显示老师的评语
            if (TextUtils.isEmpty(reviewComment)) {
                reviewComment = getString(R.string.no_content);
            }
            teacherReviewView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    teacherReviewView.setText(reviewComment);
                }
            }, 500);
            teacherReviewView.setNeedOnClickExpand(false);
            teacherReviewView.setExpandListener(new ExpandableTextView.OnExpandListener() {
                @Override
                public void onExpand(ExpandableTextView view) {
                    showTeacherReviewPopWindow(getWindowHeight());
                }

                @Override
                public void onShrink(ExpandableTextView view) {
                    showTeacherReviewPopWindow(getWindowHeight());
                }
            });
        } else {
            systemScoreView.setText(String.valueOf(teacherReviewScore));
            if (hasReviewPermission) {
                teacherReviewFl.setVisibility(View.VISIBLE);
            }
        }
    }

    private void changeDubbingTypeShow(boolean checkChange) {
        if (isOnlineOpen) {
            if (checkDubbingBySentence) {
                //重置学生提交的path
                dubbingVideoView.setVideoPath(studentCommitFilePath);
                isSupportPause = false;
                dubbingVideoView.setIsSupportPause(false);
                gridView.setVisibility(View.VISIBLE);
                lrcView.setVisibility(View.GONE);
                listenSoundTextV.setVisibility(View.GONE);
                if (checkChange) {
                    dubbingEntityList.get(curPosition).setSelect(false);
                    dubbingEntityList.get(0).setSelect(true);
                    commonAdapter.notifyDataSetChanged();
                    curPosition = 0;
                    isStopShow = false;
                    switchDubbingVideo(0, false);
                }
            } else {
                isSupportPause = true;
                dubbingVideoView.setIsSupportPause(true);
                if (listenType == 0) {
                    dubbingVideoView.setVideoPath(studentCommitFilePath);
                } else {
                    dubbingVideoView.setVideoPath(videoFilePath);
                }
                gridView.setVisibility(View.GONE);
                lrcView.setVisibility(View.VISIBLE);
                listenSoundTextV.setVisibility(View.VISIBLE);
                if (checkChange) {
                    dubbingVideoView.startPlay(0, dubbingEntityList.get(dubbingEntityList.size() - 1).getEndTime());
                }
            }
        } else {
            gridView.setVisibility(View.VISIBLE);
            lrcView.setVisibility(View.GONE);
        }
    }

    private void handleOnlineVideoData(DubbingEntity entity, int position) {
        if (pageScoreArray != null && position < pageScoreArray.size()) {
            String pageScore = pageScoreArray.get(position).toString();
            if (!TextUtils.isEmpty(pageScore)) {
                entity.setScore(Integer.valueOf(pageScore));
            }
            entity.setRecord(true);
        }
    }

    private void updateDubbingItemProgress() {
        if (isOnlineOpen) {
            for (int i = 0; i < dubbingEntityList.size(); i++) {
                dubbingEntityList.get(i).setProgress(getProgressMax(dubbingEntityList, i));
            }
        }
    }

    /**
     * 匹配字幕文件
     */
    private void matchingLrcText() {
        List<LrcEntry> lrcEntries = new ArrayList<>();
        LrcEntry lrcEntry = null;
        for (int i = 0; i < dubbingEntityList.size(); i++) {
            DubbingEntity dubbingEntity = dubbingEntityList.get(i);
            String content = dubbingEntity.getContent();
            if (!TextUtils.isEmpty(content)) {
                content = getSubStringContent(content);
            }
            dubbingEntity.setContent(content);
            lrcEntry = new LrcEntry(dubbingEntity.getStartTime(), content);
            lrcEntries.add(lrcEntry);
        }
        if (resPropertyValue == StudyResPropType.DUBBING_BY_WHOLE
                || isOnlineOpen) {
            //加载字幕
            lrcView.onLrcLoaded(lrcEntries);
        }
    }

    private String getSubStringContent(String content) {
        if (content.endsWith("\n")) {
            content = content.substring(0, content.lastIndexOf("\n"));
            content = getSubStringContent(content);
        }
        return content;
    }

    public int getProgressMax(List<DubbingEntity> dubbingEntityList, int position) {
        int videoTime = getVideoTime(dubbingEntityList, position);
        if (videoTime > 0) {
            return videoTime / 100;
        }
        return -1;
    }

    public int getVideoTime(List<DubbingEntity> dubbingEntityList, int position) {
        if (dubbingEntityList == null || dubbingEntityList.isEmpty() || position > dubbingEntityList.size()) {
            return -1;
        }
        int videoTime;
        DubbingEntity dubbingEntity = dubbingEntityList.get(position);

        if (position == 0) {
            videoTime =
                    dubbingEntity.getEndTime() + (dubbingEntityList.get(position + 1).getStartTime() - dubbingEntity.getEndTime()) / 2;
        } else if (position == (dubbingEntityList.size() - 1)) {
            videoTime =
                    (dubbingEntity.getStartTime() - dubbingEntityList.get(position - 1).getEndTime()) / 2 + dubbingEntity.getEndTime() - dubbingEntity.getStartTime();
        } else {
            videoTime =
                    (dubbingEntity.getStartTime() - dubbingEntityList.get(position - 1).getEndTime()) / 2 + dubbingEntity.getEndTime() - dubbingEntity.getStartTime() + (dubbingEntityList.get(position + 1).getStartTime() - dubbingEntity.getEndTime()) / 2;
        }
        return videoTime;
    }

    public void startOrStopProgress() {
        if (isRecording) {
            if (resPropertyValue == StudyResPropType.DUBBING_BY_WHOLE) {
                wholeRecordImageV.setImageResource(R.drawable.icon_dubbing_end_record);
            } else {
                dubbingEntityList.get(curPosition).setIsRecording(true);
                commonAdapter.notifyDataSetChanged();
                handler.sendEmptyMessage(UPDATE_PROGRESS);
            }
        } else {
            if (resPropertyValue == StudyResPropType.DUBBING_BY_WHOLE) {
                wholeRecordImageV.setImageResource(R.drawable.icon_dubbing_recording);
            } else {
                handler.removeMessages(UPDATE_PROGRESS);
            }
        }
    }

    public boolean isRecordAll() {
        for (int i = 0; i < dubbingEntityList.size(); i++) {
            DubbingEntity entity = dubbingEntityList.get(i);
            if (i == 0 && resPropertyValue == StudyResPropType.DUBBING_BY_WHOLE) {
                if (entity != null && entity.isRecord()) {
                    return true;
                } else {
                    return false;
                }
            } else if (entity != null && !entity.isRecord()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onClick(final View v) {
        if (v.getId() == R.id.tv_preview) {
            //预览

            if (isRecording) {
                TipMsgHelper.ShowMsg(DubbingActivity.this,R.string.str_dubbing_recording);
            } else if (isRecordAll()) {
                launchDubbingPreview();
            } else {
                TipMsgHelper.ShowMsg(DubbingActivity.this, getString(R.string.str_dubbing_preview_all_dubbing));
            }
        } else if (v.getId() == R.id.tv_confirm) {
            if (isRecording) {
                TipMsgHelper.ShowMsg(DubbingActivity.this,R.string.str_dubbing_recording);
            } else if (isRecordAll()) {
                //合成
                if (resPropertyValue == StudyResPropType.DUBBING_BY_WHOLE){
                    if (audioRecordHelper != null){
                        audioRecordHelper.evalWholeDubbingVideo();
                    }
                } else {
                    processQDubbingVideo();
                }
            } else {
                TipMsgHelper.ShowMsg(DubbingActivity.this, getString(R.string.str_dubbing_finish_all_tips));
            }
        } else if (v.getId() == R.id.tv_listen_sound) {
            //播放原音
            listenToTheSoundVideo();
        } else if (v.getId() == R.id.back) {
            backPress();
        } else if (v.getId() == R.id.tv_teacher_review) {
            //进入老师点评的界面
            dubbingVideoView.onPause();
            enterTeacherReviewActivity();
        }
    }

    private void processQDubbingVideo(){
        List<String> recordFileList = getRecordFilePathList();
        ProcessUtils processUtils = new ProcessUtils();
        processUtils.process(DubbingActivity.this, null, recordFileList,
                backgroundFilePath, videoFilePath, new ProcessUtils.OnProcessListener() {
                    @Override
                    public void onProcessBegin() {
                        Dialog dialog = showLoadingDialog();
                        dialog.setCancelable(false);
                    }

                    @Override
                    public void onProcessEnd(String videoPath) {
                        dismissLoadingDialog();
                        if (!TextUtils.isEmpty(videoPath)) {
                            Log.d("TTT", "videPath=" + videoPath);
                            Intent intent = new Intent();
                            intent.putExtra(Constant.MERGE_VIDEO_PATH, videoPath);
                            if (resPropertyValue == StudyResPropType.DUBBING_BY_WHOLE) {
                                //通篇配音
                                List<DubbingEntity> entityList = new ArrayList<>();
                                entityList.add(dubbingEntityList.get(0));
                                intent.putExtra(Constant.DUBBING_ENTITY_LIST_DATA, (Serializable) entityList);
                            } else {
                                intent.putExtra(Constant.DUBBING_ENTITY_LIST_DATA, (Serializable) dubbingEntityList);
                            }
                            setResult(Activity.RESULT_OK, intent);
                        }
                        finish();
                    }
                });
    }

    protected void enterTeacherReviewActivity() {

    }

    /**
     * 播放原音
     */
    private void listenToTheSoundVideo() {
        if (isOnlineOpen && lrcView != null){
            lrcView.resetLineColor();
            lrcView.scrollTo(0);
        }
        isSupportPause = true;
        dubbingVideoView.setLastTime(0);
        dubbingVideoView.setIsSupportPause(true);
        if (listenType == 0) {
            //配音
            listenSoundTextV.setText(getString(R.string.str_listen_dubbing_voice));
            dubbingVideoView.setVideoPath(videoFilePath);
//            dubbingVideoView.startPlayTaskVideo(0, DubbingActivity.this.duration, videoFilePath);
            listenType = 1;
        } else {
            //原音
            listenSoundTextV.setText(getString(R.string.str_listen_voice));
            dubbingVideoView.setVideoPath(studentCommitFilePath);
//            dubbingVideoView.startPlayTaskVideo(0, DubbingActivity.this.duration, studentCommitFilePath);
            listenType = 0;
        }
        dubbingVideoView.startPlay(0, DubbingActivity.this.duration);
    }

    private void startRecord() {
        isDubbing = true;
        progress = 0;
        dubbing(false);
//        dubbingEntityList.get(curPosition).setRecord(true);
//        if (curPosition == (dubbingEntityList.size() - 1)) {
//            confirmTextView.setVisibility(View.VISIBLE);
//        }
        previewFl.setVisibility(View.VISIBLE);
        commitFl.setVisibility(View.VISIBLE);
    }

    private void launchDubbingPreview() {
        List<String> recordFileList = getRecordFilePathList();
        File audioPath = new File(getExternalCacheDir(),"recordAudio.mp3");
        FileUtils.uniteAMRFile(recordFileList,audioPath.getAbsolutePath());
        if (!TextUtils.isEmpty(FileUtils.getFileSize(audioPath))){
            DubbingPreviewActivity.launch(DubbingActivity.this,
                    videoFilePath,
                    backgroundFilePath,
                    srtEntityList,
                    recordFileList,
                    audioPath.getAbsolutePath(),
                    resPropertyValue);
        }
    }

    private List<String> getRecordFilePathList() {
        List<String> recordFileList = new ArrayList<>();
        for (int i = 0; i < audioRecordHelper.getMp3FileList().size(); i++) {
            File file = audioRecordHelper.getMp3FileList().get(i);
            if (dubbingEntityList.get(i).isRecord() && file != null) {
                recordFileList.add(file.getAbsolutePath());
            }
        }
        return recordFileList;
    }

    protected String downloadFile(String resUrl) {
        return null;
    }

    /**
     * THIS SRT SUBTITLE SHOULD FETCH FROM SDCARD BY PRE-ACTIVITY DOWNLOADED
     */
    private void updateViews() {
        if (dubbingEntityList == null || dubbingEntityList.size() == 0) {
            return;
        }

        StringBuilder srtBuilder = new StringBuilder();
        for (int i = 0; i < dubbingEntityList.size(); i++) {
            DubbingEntity dubbingEntity = dubbingEntityList.get(i);
            dubbingEntity.setSelect(false);
            dubbingEntity.setProgressMax(getProgressMax(dubbingEntityList, i));
            dubbingEntity.setVideoTime(getVideoTime(dubbingEntityList, i));
            if (!isOnlineOpen) {
                if (resPropertyValue == StudyResPropType.DUBBING_BY_SENTENCE) {
                    File file = new File(getExternalCacheDir(), "tmp" + i + ".mp3");
                    audioRecordHelper.getMp3FileList().add(file);
                    audioRecordHelper.getAudioRefText().add(dubbingEntity.getContent());
                }
                if (i == 0) {
                    srtBuilder.append(dubbingEntity.getContent());
                } else {
                    srtBuilder.append(" ").append(dubbingEntity.getContent());
                }
            }
        }

        if (!isOnlineOpen && resPropertyValue == StudyResPropType.DUBBING_BY_WHOLE) {
            //通篇配音配置audioPlayer的参数
            File file = new File(getExternalCacheDir(), "tmp" + 0 + ".mp3");
            audioRecordHelper.getMp3FileList().add(file);
            audioRecordHelper.getAudioRefText().add(srtBuilder.toString());
        }

        commonAdapter = new CommonAdapter<DubbingEntity>(this, R.layout.item_dubbing_srt, dubbingEntityList) {
            @Override
            protected void convert(ViewHolder viewHolder, DubbingEntity item, final int position) {
                DubbingItemView dubbingItemView = viewHolder.getView(R.id.dubbing_srt_view);
                if (dubbingItemView != null && item != null) {
                    dubbingItemView.setBackgroundHighLight(item.isSelect());
                    dubbingItemView.setIndex(position, dubbingEntityList.size());
                    dubbingItemView.setProgressMax(item.getProgressMax());
                    dubbingItemView.setProgress(item.getProgress());
                    dubbingItemView.setTime(item.getVideoTime());
                    dubbingItemView.getPlayBtn().setVisibility(item.isRecord() ? View.VISIBLE : View.INVISIBLE);
                    dubbingItemView.getPlayBtn().setImageResource(item.isRecordPlaying() ?
                            R.drawable.icon_record_playing : R.drawable.icon_dubbing_play);
                    dubbingItemView.getRecordBtn().setImageLevel(item.isSelect() || item.isRecord() ? 1 : 0);
//                    dubbingItemView.getRecordBtn().setEnabled(item.isSelect());
                    if (isOnlineOpen) {
                        //播放原视频
                        dubbingItemView.getRecordBtn().setImageResource(item.isRecording() ?
                                R.drawable.icon_record_playing : R.drawable.icon_q_play);
                    } else {
                        //录制
                        dubbingItemView.getRecordBtn().setImageResource(item.isRecording() ?
                                R.drawable.icon_record_pause : R.drawable.icon_record);
                    }
                    dubbingItemView.setScore(item.getScore(), item.isRecord());

                    //显示播放中字体的颜色
                    if (showDownTimeFlag && position == curPosition) {
                        dubbingItemView.getDownCountTimeView().setVisibility(View.VISIBLE);
                        dubbingItemView.getContentTextView().setText(item.getContent());
                        dubbingItemView.getContentTextView().setTextColor(ContextCompat.getColor(DubbingActivity.this, R.color.color_orange));
                    } else {
                        if (!isOnlineOpen
                                && item.isSelect()
                                && (item.isRecordPlaying() || item.isRecording() || item.isItemVideoPlaying())) {
                            //播放的时候重置字体的颜色
                            dubbingItemView.getContentTextView().setText(item.getContent());
                            dubbingItemView.getContentTextView().setTextColor(ContextCompat.getColor(DubbingActivity.this, R.color.text_black));
                        } else if (item.isRecord() && !isOnlineOpen && item.getEvalResult() != null) {
                            //录制完成
                            dubbingItemView.getContentTextView().setText(item.getEvalResult());
                        } else {
                            //没有录制
                            dubbingItemView.getContentTextView().setText(item.getContent());
                            dubbingItemView.getContentTextView().setTextColor(ContextCompat.getColor(DubbingActivity.this, R.color.text_black));
                        }
                        dubbingItemView.getDownCountTimeView().setVisibility(View.GONE);
                    }
                    dubbingItemView.getPlayBtn().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onViewClick(position, false, false, true);
                        }
                    });

                    dubbingItemView.getRecordBtn().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onViewClick(position, false, true, false);
                        }
                    });
                }
            }
        };
        gridView.setAdapter(commonAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onViewClick(position, true, false, false);
            }
        });
    }

    private void onViewClick(int position,
                             boolean onItemClick,
                             boolean audioRecord,
                             boolean audioPlay) {
        if (isRecording) {
            TipMsgHelper.ShowMsg(DubbingActivity.this, R.string.str_dubbing_recording);
            return;
        }
        stopPlayRecordingAudio();
        isStopShow = false;
        if (dubbingEntityList != null && position < dubbingEntityList.size()) {
            dubbingEntityList.get(curPosition).setSelect(false);
            dubbingEntityList.get(position).setSelect(true);
            commonAdapter.notifyDataSetChanged();
            curPosition = position;
            isCheckGridViewItem = true;
            isSupportPause = false;
            dubbingVideoView.setIsSupportPause(false);
            if (onItemClick) {
                dubbingEntityList.get(position).setItemVideoPlaying(true);
                switchDubbingVideo(position, false);
            } else if (!isOnlineOpen) {
                //不是在线状态
                updatePlayAndRecordTime(position);
            }
        }
        if (audioRecord) {
            onAudioRecord();
        } else if (audioPlay) {
            onAudioPlay();
        }
    }

    private void updatePlayAndRecordTime(int position) {
        DubbingEntity dubbingEntity = dubbingEntityList.get(position);
        int currentStart = dubbingEntity.getStartTime();
        int currentEnd = dubbingEntity.getEndTime();
        int frontEnd;
        int backStart;
        int newPlayEndTime;
        if (position == 0) {
            newPlayTime = 0;
            backStart = dubbingEntityList.get(position + 1).getStartTime();
            newPlayEndTime = currentEnd + (backStart - currentEnd) / 2;
        } else if (position == dubbingEntityList.size() - 1) {
            frontEnd = dubbingEntityList.get(position - 1).getEndTime();
            newPlayTime = frontEnd + (currentStart - frontEnd) / 2;
            newPlayEndTime = dubbingEntity.getEndTime();
        } else {
            frontEnd = dubbingEntityList.get(position - 1).getEndTime();
            backStart = dubbingEntityList.get(position + 1).getStartTime();
            newPlayTime = frontEnd + (currentStart - frontEnd) / 2;
            newPlayEndTime = dubbingEntity.getEndTime() + (backStart - currentEnd) / 2;
        }
        dubbingVideoView.setSeekPlay(newPlayTime, newPlayEndTime);
    }

    /**
     * @param position
     * @param openTaskVideo 是不是播放当前段任务的视频
     */
    private void switchDubbingVideo(int position, boolean openTaskVideo) {
        DubbingEntity dubbingEntity = dubbingEntityList.get(position);
        int currentStart = dubbingEntity.getStartTime();
        int currentEnd = dubbingEntity.getEndTime();
        int frontEnd;
        int backStart;
        if (position == 0) {
            newPlayTime = 0;
            backStart = dubbingEntityList.get(position + 1).getStartTime();
            if (openTaskVideo) {
                dubbingVideoView.startPlayTaskVideo(0, currentEnd + (backStart - currentEnd) / 2,
                        videoFilePath);
            } else {
                dubbingVideoView.startPlay(0, currentEnd + (backStart - currentEnd) / 2);
            }
        } else if (position == dubbingEntityList.size() - 1) {
            frontEnd = dubbingEntityList.get(position - 1).getEndTime();
            newPlayTime = frontEnd + (currentStart - frontEnd) / 2;
            if (openTaskVideo) {
                dubbingVideoView.startPlayTaskVideo(frontEnd + (currentStart - frontEnd) / 2,
                        dubbingEntity.getEndTime(), videoFilePath);
            } else {
                dubbingVideoView.startPlay(frontEnd + (currentStart - frontEnd) / 2, dubbingEntity.getEndTime());
            }
        } else {
            frontEnd = dubbingEntityList.get(position - 1).getEndTime();
            backStart = dubbingEntityList.get(position + 1).getStartTime();
            newPlayTime = frontEnd + (currentStart - frontEnd) / 2;
            if (openTaskVideo) {
                dubbingVideoView.startPlayTaskVideo(frontEnd + (currentStart - frontEnd) / 2,
                        dubbingEntity.getEndTime() + (backStart - currentEnd) / 2, videoFilePath);
            } else {
                dubbingVideoView.startPlay(frontEnd + (currentStart - frontEnd) / 2,
                        dubbingEntity.getEndTime() + (backStart - currentEnd) / 2);
            }
        }
    }

    /**
     * REFRESH TIME >> INCLUDE: PROGRESSBAR TIME-INDICATOR SRT-SUBTITLE
     */
    private void refreshTime(long playTime, long totalTime, int videoMode) {
        refreshLrcLineText(playTime);
        currentTimeTextView.setText(MediaUtil.generateTime(playTime));
        totalTimeTextView.setText(MediaUtil.generateTime(totalTime));
        seekBar.setProgress((int) playTime);
        updateDownTimeView(playTime);
    }

    public void onAudioRecord() {
        if (isOnlineOpen) {
            //播放原视频资源
            startPlayRecordAudio(false);
            switchDubbingVideo(curPosition, true);
        } else {
            if (isRecording) {
                TipMsgHelper.ShowMsg(this, R.string.str_dubbing_recording);
            } else {
                if (audioRecordHelper != null) {
                    audioRecordHelper.stopMediaPlayer();
                }
                startRecord();
            }
        }
    }

    public void onAudioPlay() {
        startPlayRecordAudio(true);
        if (isOnlineOpen) {
            //播放这个时间点的视频
            switchDubbingVideo(curPosition, false);
        } else {
            //回放录制的视频
            startReview();
        }
    }


    final class VideoViewListener extends OnVideoEventListener {

        @Override
        public void onVideoPrepared(long duration) {
            loadVideoComplete = true;
            DubbingActivity.this.duration = duration;
            if (currentTimeTextView != null) {
                currentTimeTextView.setText(MediaUtil.generateTime(0));
            }
            if (totalTimeTextView != null) {
                totalTimeTextView.setText(MediaUtil.generateTime(duration));
            }
            seekBar.setProgress(0);
            seekBar.setMax((int) duration);
            dubbingEntityList.get(dubbingEntityList.size()-1).setEndTime((int) duration);
            if (duration > 0 && isSupportPause){
                dubbingVideoView.setEndTime((int) duration);
            }
        }


        @Override
        public void onDubbingComplete() {
            isDubbing = false;
            audioRecordHelper.stopRecord();
            dubbing(false);
//            launchDubbingPreview();
        }


        @Override
        public int onPreviewPrepared() {
            return (int) playTime;
        }

        @Override
        public void onPreviewPlay() {

        }

        @Override
        public void onPreviewStop(int resetPos) {
            seekBar.setProgress(resetPos);
        }

        @Override
        public boolean onPlayTimeChanged(long playTime, long totalTime, int videoMode) {
            LogUtils.log("TTT", "playTime=" + playTime + " totalTime=" + totalTime);
            duration = totalTime;
            refreshTime(playTime, totalTime, videoMode);
            return true;
        }

        @Override
        public void onVideoCompletion() {
            stopPlayRecordingAudio();
            if (isOnlineOpen && !checkDubbingBySentence){
                lrcView.resetLineColor();
            }
        }
    }

    /**
     * ACTION-BTN PERFORM CLICK
     */
    public void dubbing(boolean isPause) {
        if (isDubbing) {
            isRecording = true;
            audioRecordHelper.startRecord(curPosition);
            if (resPropertyValue == StudyResPropType.DUBBING_BY_WHOLE) {
                dubbingVideoView.startDubbing(0);
            } else {
                dubbingVideoView.startDubbing(newPlayTime);
                if (lastSeek >= duration) {
                    lastSeek = 0;
                }
            }
        } else {
            isRecording = false;
            if (dubbingVideoView.isPlaying()) {
                if (isPause) {
                    //不要评测分数
                    audioRecordHelper.setNeedEvalAudio(false);
                    dubbingEntityList.get(curPosition).setProgress(0);
                    dubbingEntityList.get(curPosition).setIsRecording(false);
                    commonAdapter.notifyDataSetChanged();
                }
                audioRecordHelper.stopRecord();
            }
            dubbingVideoView.stopDubbing();
        }
        startOrStopProgress();
    }


    public void startReview() {
        isReviewing = true;
        //todo play background audio

        // play the recorded audio from indicator pos
        lastSeek = playTime;
        audioRecordHelper.stopMediaPlayer();
        audioRecordHelper.play(0, curPosition, new CallbackListener() {
            @Override
            public void onBack(Object result) {
                stopPlayRecordingAudio();
            }
        });
//        dubbingVideoView.startDubbing(newPlayTime);
        dubbingVideoView.startReview(newPlayTime);
    }

    private void stopReview() {
        //stop audio play
        audioRecordHelper.stopMediaPlayer();
        //stop video view
        dubbingVideoView.stopReview((int) lastSeek);
        isReviewing = false;
    }

    private void startPlayRecordAudio(boolean isLeftBtnPlay) {
        if (resPropertyValue == StudyResPropType.DUBBING_BY_SENTENCE) {
            if (isLeftBtnPlay) {
                dubbingEntityList.get(curPosition).setIsRecordPlaying(true);
            } else {
                dubbingEntityList.get(curPosition).setIsRecording(true);
            }
            commonAdapter.notifyDataSetChanged();
        }
    }

    private void stopPlayRecordingAudio() {
        if (resPropertyValue == StudyResPropType.DUBBING_BY_SENTENCE) {
            if (dubbingEntityList.get(curPosition).isRecordPlaying()) {
                dubbingEntityList.get(curPosition).setIsRecordPlaying(false);
            } else if (dubbingEntityList.get(curPosition).isRecording()) {
                dubbingEntityList.get(curPosition).setIsRecording(false);
            }
            showDownTimeFlag = false;
            isStopShow = true;
            dubbingEntityList.get(curPosition).setItemVideoPlaying(false);
            commonAdapter.notifyDataSetChanged();
        }
    }

    private void updateDownTimeView(long playTime) {
        if (resPropertyValue == StudyResPropType.DUBBING_BY_SENTENCE) {
            DubbingEntity entity = dubbingEntityList.get(curPosition);
            long startTime = entity.getStartTime();
            if (!showDownTimeFlag) {
                if (startTime - 1000 < playTime && !isStopShow) {
                    showDownTimeFlag = true;
                    commonAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    protected void showTeacherReviewPopWindow(int height) {

    }

    private int getWindowHeight() {
        return dubbingVideoView.getMeasuredHeight() + teacherOperationLayout.getMeasuredHeight() + DensityUtils.dp2px(this, 10);
    }

    private void refreshLrcLineText(long playTime) {
        if (resPropertyValue == StudyResPropType.DUBBING_BY_WHOLE
                || (isOnlineOpen && !checkDubbingBySentence)) {
            updateSrtTime(playTime);
        }
    }

    private void updateSrtTime(long playTime){
        int startTime = dubbingEntityList.get(0).getStartTime();
        if (startTime - 1000 < playTime) {
            lrcView.updateTime(playTime);
        }
    }

    public Dialog showLoadingDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            return mLoadingDialog;
        }
        mLoadingDialog = DialogHelper.getIt(this).GetLoadingDialog(0);
        return mLoadingDialog;
    }

    public void dismissLoadingDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }
}
