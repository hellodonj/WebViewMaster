package com.icedcap.dubbing;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.icedcap.dubbing.audio.AudioRecordHelper;
import com.icedcap.dubbing.listener.OnAudioEventListener;
import com.icedcap.dubbing.entity.DubbingEntity;
import com.icedcap.dubbing.entity.SrtEntity;
import com.icedcap.dubbing.listener.OnVideoEventListener;
import com.icedcap.dubbing.utils.MediaUtil;
import com.icedcap.dubbing.utils.ProcessUtils;
import com.icedcap.dubbing.utils.SrtUtils;
import com.icedcap.dubbing.view.DubbingSubtitleView;
import com.icedcap.dubbing.view.DubbingVideoView;
import com.icedcap.dubbing.view.DubbingItemView;
import com.lqwawa.apps.views.lrcview.LrcEntry;
import com.lqwawa.apps.views.lrcview.LrcView;
import com.lqwawa.apps.views.switchbutton.SwitchButton;
import com.lqwawa.client.pojo.StudyResPropType;
import com.lqwawa.tools.DialogHelper;
import com.oosic.apps.iemaker.base.onlineedit.CallbackListener;
import com.osastudio.common.utils.FileUtils;
import com.osastudio.common.utils.TipMsgHelper;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.icedcap.dubbing.view.DubbingVideoView.MODE_DUBBING;
import static com.icedcap.dubbing.view.DubbingVideoView.MODE_IDLE;

public class DubbingActivity extends AppCompatActivity implements
        View.OnClickListener, OnAudioEventListener {
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

    private static final String[] VIDEO = new String[]{
            "material/4803081086444687938.mp4",
            "material2/4911222198272423589.mp4"
    };

    private static final String[] SRT = new String[]{
            "material/5358306446323949054.srt",
            "material2/5339513282283280902.srt"
    };

    private static final String[] AUDIO = {
            "material/5314291602012189567.mp3",
            "material2/4961513952477274315.mp3"
    };


    private DubbingSubtitleView dubbingSubtitleView;
    private TextView currentTimeTextView;
    private TextView totalTimeTextView;
    private ProgressBar progressBar;
    private DubbingVideoView dubbingVideoView;
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
    private TextView teacherReviewView;
    private LinearLayout teacherOperationLayout;
    private ImageView wholeRecordImageV;
    private FrameLayout previewFl;
    private FrameLayout commitFl;
    private LrcView lrcView;
    private List<String> permissions = new ArrayList<>();
    private AudioRecordHelper audioRecordHelper;

    List<SrtEntity> srtEntityList = new ArrayList<>();

    List<DubbingEntity> dubbingEntityList = new ArrayList<>();

    private long duration;
    private boolean isReviewing = false;
    private boolean isDubbing;
    private boolean isRecording;

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
            audioRecordHelper = new AudioRecordHelper(this, new CallbackListener() {
                @Override
                public void onBack(Object result) {
                    if (result != null) {
                        int evalScore = (int) result;
                        dubbingEntityList.get(curPosition).setScore(evalScore);
                        dubbingEntityList.get(curPosition).setRecord(true);
                        isDubbing = false;
                        commonAdapter.notifyDataSetChanged();
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
            Toast.makeText(this, "存储空间不足！！\n5秒后退出程序", Toast.LENGTH_SHORT).show();
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
            dubbing();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        artProcessView.setVisibility(View.GONE);
    }

    public void setDubb() {
        if (resPropertyValue == StudyResPropType.DUBBING_BY_WHOLE){
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
                        Toast.makeText(this, "您拒绝了相应的权限，无法完成配音", Toast.LENGTH_LONG).show();
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
        if (isRecording) {
            isDubbing = false;
            dubbing();
            new AlertDialog.Builder(this)
                    .setMessage("正在录音，真的退出吗？")
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            isDubbing = true;
                            dubbing();
                        }
                    })
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DubbingActivity.this.finish();
                        }
                    })
                    .show();
        } else {
            super.onBackPressed();
        }
    }


    private void initView() {
        dubbingSubtitleView = (DubbingSubtitleView) findViewById(R.id.subtitleView);
        artProcessView = findViewById(R.id.art_process_view);
        final ProgressBar pb = (ProgressBar) findViewById(R.id.art_progress_bar);
        pb.getIndeterminateDrawable().setColorFilter(0xFFCECECE, android.graphics.PorterDuff.Mode.MULTIPLY);
        currentTimeTextView = (TextView) findViewById(R.id.tv_current_time);
        totalTimeTextView = (TextView) findViewById(R.id.tv_total_time);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        dubbingVideoView = (DubbingVideoView) findViewById(R.id.videoView);
        handleDataView();
        new AsyncTask<Void, Void, Void>() {
            AlertDialog dialog = new AlertDialog.Builder(DubbingActivity.this)
                    .setMessage("正在加载视频资源...")
                    .create();

            @Override
            protected void onPreExecute() {
                dialog.show();
            }

            @Override
            protected Void doInBackground(Void... params) {
//                videoFilePath = downloadFile(VIDEO[MATERIAL]);
//                backgroundFilePath = downloadFile(AUDIO[MATERIAL]);
//                srtEntityList = SrtUtils.processSrtFromFile(downloadFile(SRT[MATERIAL]));
                videoFilePath = downloadFile(videoFilePath);
                if (isOnlineOpen) {
                    studentCommitFilePath = downloadFile(studentCommitFilePath);
                }
                if (!TextUtils.isEmpty(backgroundFilePath)) {
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
                dialog.cancel();
                dubbingVideoView.setPara(isOnlineOpen ? studentCommitFilePath : videoFilePath, "",
                        false, 0, "", new VideoViewListener(), DubbingActivity.this);
                if (resPropertyValue == StudyResPropType.DUBBING_BY_WHOLE || isOnlineOpen) {
                    //播放状态直接到结束
                    dubbingVideoView.startPlay(0, dubbingEntityList.get(dubbingEntityList.size() - 1).getEndTime());
                } else {
                    dubbingVideoView.startPlay(0, getVideoTime(dubbingEntityList, 0));
                }
                matchingLrcText();
                updateViews();
            }
        }.execute();
    }

    protected void handleDataView() {
        qDubbingDetailLayout = (LinearLayout) findViewById(R.id.ll_q_detail);
        systemScoreView = (TextView) findViewById(R.id.tv_system_score);
        dubbingBySentenceLayout = (LinearLayout) findViewById(R.id.ll_show_dubbing_by_sentence);
        changDubbingTypeBtn = (SwitchButton) findViewById(R.id.sb_switch_btn);
        teacherScoreView = (TextView) findViewById(R.id.tv_teacher_review_score);
        teacherReviewView = (TextView) findViewById(R.id.tv_teacher_comment);
        teacherOperationLayout = (LinearLayout) findViewById(R.id.ll_teacher_operation);
        previewTextView = (TextView) findViewById(R.id.tv_preview);
        previewTextView.setOnClickListener(this);
        confirmTextView = (TextView) findViewById(R.id.tv_confirm);
        confirmTextView.setOnClickListener(this);
        //单句配音
        gridView = (GridView) findViewById(R.id.grid_view);
        //通篇配音
        lrcView = (LrcView) findViewById(R.id.lrc_view);
        wholeRecordImageV = (ImageView) findViewById(R.id.iv_dubbing_record);
        previewFl = (FrameLayout) findViewById(R.id.fl_preview);
        commitFl = (FrameLayout) findViewById(R.id.fl_commit);
        if (isOnlineOpen) {
            qDubbingDetailLayout.setVisibility(View.VISIBLE);
            if (hasVideoReview) {
                //老师点评了
                teacherOperationLayout.setVisibility(View.VISIBLE);
                systemScoreView.setText(String.valueOf(systemScore));
                teacherScoreView.setText(String.valueOf(teacherReviewScore));
                //显示老师的评语
                if (TextUtils.isEmpty(reviewComment)) {
                    reviewComment = getString(R.string.no_content);
                }
                teacherReviewView.setText(reviewComment);
            } else {
                systemScoreView.setText(String.valueOf(teacherReviewScore));
            }
            if (resPropertyValue == StudyResPropType.DUBBING_BY_SENTENCE) {
                dubbingBySentenceLayout.setVisibility(View.VISIBLE);
                changDubbingTypeBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
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
            gridView.setVisibility(View.GONE);
            lrcView.setVisibility(View.VISIBLE);
            if (!isOnlineOpen) {
                wholeRecordImageV.setVisibility(View.VISIBLE);
            }
            wholeRecordImageV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //开始配音
                    if (isRecording){
                        TipMsgHelper.ShowMsg(DubbingActivity.this,R.string.str_dubbing_recording);
                    } else {
                        onAudioRecord();
                    }
                }
            });
        }
    }

    private void changeDubbingTypeShow(boolean checkChange){
        if (isOnlineOpen) {
            if (checkDubbingBySentence) {
                gridView.setVisibility(View.VISIBLE);
                lrcView.setVisibility(View.GONE);
                if (checkChange) {
                    dubbingVideoView.startPlay(0, getVideoTime(dubbingEntityList, 0));
                }
            } else {
                gridView.setVisibility(View.GONE);
                lrcView.setVisibility(View.VISIBLE);
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
            lrcEntry = new LrcEntry(dubbingEntity.getStartTime(), dubbingEntity.getContent());
            lrcEntries.add(lrcEntry);
        }
        if (resPropertyValue == StudyResPropType.DUBBING_BY_WHOLE
                || isOnlineOpen) {
            //加载字幕
            lrcView.onLrcLoaded(lrcEntries);
        }
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
            if (resPropertyValue == StudyResPropType.DUBBING_BY_WHOLE){
                wholeRecordImageV.setImageResource(R.drawable.icon_dubbing_end_record);
            } else {
                handler.sendEmptyMessage(UPDATE_PROGRESS);
            }
        } else {
            if (resPropertyValue == StudyResPropType.DUBBING_BY_WHOLE){
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
    public void onClick(View v) {
        if (v.getId() == R.id.tv_preview) {
            //预览
            if (isRecordAll()) {
                launchDubbingPreview();
            } else {
                TipMsgHelper.ShowMsg(DubbingActivity.this, getString(R.string.str_dubbing_finish_all_tips));
            }
        } else if (v.getId() == R.id.tv_confirm) {
            if (isRecordAll()) {
                //合成
                List<String> recordFileList = getRecordFilePathList();
                ProcessUtils processUtils = new ProcessUtils();
                processUtils.process(DubbingActivity.this, null, recordFileList,
                        backgroundFilePath, videoFilePath, new ProcessUtils.OnProcessListener() {
                            @Override
                            public void onProcessBegin() {
                                showLoadingDialog();
                            }

                            @Override
                            public void onProcessEnd(String videoPath) {
                                dismissLoadingDialog();
                                if (!TextUtils.isEmpty(videoPath)) {
                                    Intent intent = new Intent();
                                    intent.putExtra(Constant.MERGE_VIDEO_PATH, videoPath);
                                    intent.putExtra(Constant.DUBBING_ENTITY_LIST_DATA, (Serializable) dubbingEntityList);
                                    setResult(Activity.RESULT_OK, intent);
                                }
                                finish();
                            }
                        });
            } else {
                TipMsgHelper.ShowMsg(DubbingActivity.this, getString(R.string.str_dubbing_finish_all_tips));
            }
        }
    }


    private void startRecord() {
        isDubbing = true;
        progress = 0;
        dubbing();
//        dubbingEntityList.get(curPosition).setRecord(true);
//        if (curPosition == (dubbingEntityList.size() - 1)) {
//            confirmTextView.setVisibility(View.VISIBLE);
//        }
        previewFl.setVisibility(View.VISIBLE);
        commitFl.setVisibility(View.VISIBLE);
    }


    private void launchDubbingPreview() {
        List<String> recordFileList = getRecordFilePathList();
        DubbingPreviewActivity.launch(DubbingActivity.this,
                videoFilePath,
                backgroundFilePath,
                srtEntityList, recordFileList);
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
//    /**
//     * 将Assets目录下文件拷贝到sdk目录下, 后续从服务器下载
//     *
//     * @param url
//     * @return
//     */
//    private String downloadFile(String url) {
//        if (TextUtils.isEmpty(url)) {
//            return null;
//        }
//        String fileName = url.split("/")[1];
//
//        File dirFile = getExternalFilesDir("material");
//        File localFile = new File(dirFile, fileName);
//        InputStream is;
//        if (!localFile.exists()) {
//            AssetManager manager = getAssets();
//            FileOutputStream fos = null;
//            try {
//                is = manager.open(url);
//                fos = new FileOutputStream(localFile);
//                byte[] bytes = new byte[1024];
//                int read;
//                while ((read = is.read(bytes)) != -1) {
//                    fos.write(bytes, 0, read);
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            } finally {
//                if (fos != null) {
//                    try {
//                        fos.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }
//        return localFile.getAbsolutePath();
//    }

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
            dubbingEntity.setSelect(i == 0);
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

        dubbingSubtitleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRecording || isReviewing) {
                    return;
                }
                if (dubbingVideoView.isPlaying()) {
                    dubbingVideoView.pause(DubbingVideoView.MODE_PREVIEW);
                }
            }
        });

        dubbingSubtitleView.init(srtEntityList);

        commonAdapter = new CommonAdapter<DubbingEntity>(this, R.layout.item_dubbing_srt, dubbingEntityList) {
            @Override
            protected void convert(ViewHolder viewHolder, DubbingEntity item, int position) {
                DubbingItemView dubbingItemView = viewHolder.getView(R.id.dubbing_srt_view);
                if (dubbingItemView != null && item != null) {
                    dubbingItemView.setBackgroundHighLight(item.isSelect());
                    dubbingItemView.setIndex(position, dubbingEntityList.size());
                    dubbingItemView.setContent(item.getContent());
                    dubbingItemView.setProgressMax(item.getProgressMax());
                    dubbingItemView.setProgress(item.getProgress());
                    dubbingItemView.setTime(item.getVideoTime());
                    dubbingItemView.getPlayBtn().setVisibility(item.isRecord() ? View.VISIBLE : View.INVISIBLE);
                    dubbingItemView.getRecordBtn().setImageLevel(item.isSelect() || item.isRecord() ? 1 : 0);
                    dubbingItemView.getRecordBtn().setEnabled(item.isSelect());
                    dubbingItemView.setScore(item.getScore(), item.isRecord());
                    dubbingItemView.setOnAudioEventListener(DubbingActivity.this);
                }
            }
        };
        gridView.setAdapter(commonAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (dubbingEntityList != null && position < dubbingEntityList.size()) {
                    if (curPosition != position) {
                        dubbingEntityList.get(curPosition).setSelect(false);
                        dubbingEntityList.get(position).setSelect(true);
                        commonAdapter.notifyDataSetChanged();
                        curPosition = position;
                        switchDubbingVideo(position, false);
                    }
                }
            }
        });
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
        if (dubbingSubtitleView != null) {
            if (videoMode == MODE_IDLE) {
                dubbingSubtitleView.refresh((int) playTime);
            } else {
                dubbingSubtitleView.processTime((int) playTime);
            }
        }
        int i = (int) (100L * playTime / totalTime);
        if (videoMode == MODE_DUBBING || videoMode == MODE_IDLE) {
            progressBar.setSecondaryProgress(i);
            return;
        }
        progressBar.setSecondaryProgress(i);
    }


    @Override
    public void onAudioRecord() {
        if (isOnlineOpen) {
            //播放原视频资源
            switchDubbingVideo(curPosition, true);
        } else {
            startRecord();
        }
    }

    @Override
    public void onAudioPlay() {
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
            DubbingActivity.this.duration = duration;
            if (currentTimeTextView != null) {
                currentTimeTextView.setText(MediaUtil.generateTime(0));
            }
            if (totalTimeTextView != null) {
                totalTimeTextView.setText(MediaUtil.generateTime(duration));
            }
        }


        @Override
        public void onDubbingComplete() {
            isDubbing = false;
            audioRecordHelper.stopRecord();
            dubbing();
            launchDubbingPreview();
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
            dubbingSubtitleView.reset();

            int i = (int) (100L * resetPos / duration);
            progressBar.setSecondaryProgress(i);
            dubbingSubtitleView.refresh(resetPos);
        }

        @Override
        public boolean onPlayTimeChanged(long playTime, long totalTime, int videoMode) {
            duration = totalTime;
            refreshTime(playTime, totalTime, videoMode);
            return true;
        }

    }

    /**
     * ACTION-BTN PERFORM CLICK
     */
    public void dubbing() {
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
            dubbingSubtitleView.setEditted(!isRecording);
        } else {
            isRecording = false;
            if (dubbingVideoView.isPlaying()) {
                audioRecordHelper.stopRecord();
            }
            dubbingVideoView.stopDubbing();
            dubbingSubtitleView.setEditted(!isRecording);
        }
        startOrStopProgress();
    }


    public void startReview() {
        isReviewing = true;
        //todo play background audio

        // play the recorded audio from indicator pos
        lastSeek = playTime;
        dubbingSubtitleView.refresh((int) playTime);
        audioRecordHelper.play(0, curPosition);
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

    private void refreshLrcLineText(long playTime) {
        if (resPropertyValue == StudyResPropType.DUBBING_BY_WHOLE
                || (isOnlineOpen && !checkDubbingBySentence)) {
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
