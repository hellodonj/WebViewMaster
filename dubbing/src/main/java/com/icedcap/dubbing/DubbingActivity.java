package com.icedcap.dubbing;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.icedcap.dubbing.view.DubbingVideoView.MODE_DUBBING;
import static com.icedcap.dubbing.view.DubbingVideoView.MODE_IDLE;

public class DubbingActivity extends AppCompatActivity implements
        View.OnClickListener, OnAudioEventListener {
    private static final int PERMISSION_REQUEST_CODE = 0x520;
    private static final int UPDATE_PROGRESS = 0x1024;
    private static final int MATERIAL = 1;

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

    private String videoFilePath;
    private String backgroundFilePath;

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

    public static void start(Context context) {
        Intent starter = new Intent(context, DubbingActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_dubbing);
        audioRecordHelper = new AudioRecordHelper();

        checkPermissions();

        if (permissions.size() > 0) {
            String[] permissions = new String[this.permissions.size()];
            ActivityCompat.requestPermissions(this, this.permissions.toArray(permissions), PERMISSION_REQUEST_CODE);
        } else {
            initView();

        }
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
        previewTextView = (TextView) findViewById(R.id.tv_preview);
        previewTextView.setOnClickListener(this);
        confirmTextView = (TextView) findViewById(R.id.tv_confirm);
        confirmTextView.setOnClickListener(this);
        gridView = (GridView) findViewById(R.id.grid_view);


        new AsyncTask<Void, Void, Void>() {
            AlertDialog dialog = new AlertDialog.Builder(DubbingActivity.this)
                    .setMessage("正在处理...")
                    .create();

            @Override
            protected void onPreExecute() {
                dialog.show();
            }

            @Override
            protected Void doInBackground(Void... params) {
                videoFilePath = downloadFile(VIDEO[MATERIAL]);
                backgroundFilePath = downloadFile(AUDIO[MATERIAL]);
                srtEntityList = SrtUtils.processSrtFromFile(downloadFile(SRT[MATERIAL]));
                if (srtEntityList != null && !srtEntityList.isEmpty()) {
                    dubbingEntityList.clear();
                    for (SrtEntity entity : srtEntityList) {
                        if (entity != null) {
                            DubbingEntity dubbingEntity = new DubbingEntity(entity);
                            dubbingEntityList.add(dubbingEntity);
                        }

                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                dialog.cancel();
                dubbingVideoView.setPara(videoFilePath, "", false, 0, "", new VideoViewListener(), DubbingActivity.this);
                dubbingVideoView.startPlay(0, getVideoTime(dubbingEntityList, 0));

                updateViews();
            }
        }.execute();

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
            handler.sendEmptyMessage(UPDATE_PROGRESS);
        } else {
            handler.removeMessages(UPDATE_PROGRESS);
        }
    }

    public boolean isRecordAll() {
        for (DubbingEntity entity : dubbingEntityList) {
            if (entity != null && !entity.isRecord()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_preview) {
            launchDubbingPreview();
        } else if (v.getId() == R.id.tv_confirm) {
            if (isRecordAll()) {
                List<String> recordFileList = getRecordFilePathList();
                ProcessUtils processUtils = new ProcessUtils();
                processUtils.process(DubbingActivity.this, null, recordFileList,
                        backgroundFilePath, videoFilePath, new ProcessUtils.OnProcessListener() {
                            @Override
                            public void onProcessBegin() {

                            }

                            @Override
                            public void onProcessEnd(String videoPath) {

                            }
                        });
            }
        }
    }


    private void startRecord() {
        isDubbing = true;
        progress = 0;
        dubbing();
        dubbingEntityList.get(curPosition).setRecord(true);

//        if (curPosition == (dubbingEntityList.size() - 1)) {
//            confirmTextView.setVisibility(View.VISIBLE);
//        }
        confirmTextView.setVisibility(View.VISIBLE);
        previewTextView.setVisibility(View.VISIBLE);
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

    /**
     * 将Assets目录下文件拷贝到sdk目录下, 后续从服务器下载
     *
     * @param url
     * @return
     */
    private String downloadFile(String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        String fileName = url.split("/")[1];

        File dirFile = getExternalFilesDir("material");
        File localFile = new File(dirFile, fileName);
        InputStream is;
        if (!localFile.exists()) {
            AssetManager manager = getAssets();
            FileOutputStream fos = null;
            try {
                is = manager.open(url);
                fos = new FileOutputStream(localFile);
                byte[] bytes = new byte[1024];
                int read;
                while ((read = is.read(bytes)) != -1) {
                    fos.write(bytes, 0, read);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return localFile.getAbsolutePath();
    }

    /**
     * THIS SRT SUBTITLE SHOULD FETCH FROM SDCARD BY PRE-ACTIVITY DOWNLOADED
     */
    private void updateViews() {
        if (dubbingEntityList == null || dubbingEntityList.size() == 0) {
            return;
        }

        for (int i = 0; i < dubbingEntityList.size(); i++) {
            DubbingEntity dubbingEntity = dubbingEntityList.get(i);
            dubbingEntity.setSelect(i == 0);
            dubbingEntity.setProgressMax(getProgressMax(dubbingEntityList, i));
            dubbingEntity.setVideoTime(getVideoTime(dubbingEntityList, i));

            File file = new File(getExternalCacheDir(), "tmp" + i + ".mp3");
            audioRecordHelper.getMp3FileList().add(file);
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

                        switchDubbingVideo(position);
                    }
                }
            }
        });


    }

    private void switchDubbingVideo(int position) {
        DubbingEntity dubbingEntity = dubbingEntityList.get(position);

        int currentStart = dubbingEntity.getStartTime();
        int currentEnd = dubbingEntity.getEndTime();
        int frontEnd;
        int backStart;
        if (position == 0) {
            newPlayTime = 0;
            backStart = dubbingEntityList.get(position + 1).getStartTime();
            dubbingVideoView.startPlay(0, currentEnd + (backStart - currentEnd) / 2);
        } else if (position == dubbingEntityList.size() - 1) {
            frontEnd = dubbingEntityList.get(position - 1).getEndTime();
            newPlayTime = frontEnd + (currentStart - frontEnd) / 2;
            dubbingVideoView.startPlay(frontEnd + (currentStart - frontEnd) / 2, dubbingEntity.getEndTime());
        } else {
            frontEnd = dubbingEntityList.get(position - 1).getEndTime();
            backStart = dubbingEntityList.get(position + 1).getStartTime();
            newPlayTime = frontEnd + (currentStart - frontEnd) / 2;
            dubbingVideoView.startPlay(frontEnd + (currentStart - frontEnd) / 2,
                    dubbingEntity.getEndTime() + (backStart - currentEnd) / 2);
        }
    }

    /**
     * REFRESH TIME >> INCLUDE: PROGRESSBAR TIME-INDICATOR SRT-SUBTITLE
     */
    private void refreshTime(long playTime, long totalTime, int videoMode) {
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
        startRecord();
    }

    @Override
    public void onAudioPlay() {
        startReview();
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
            audioRecordHelper.startRecord(DubbingActivity.this, curPosition);
            dubbingVideoView.startDubbing(newPlayTime);
            if (lastSeek >= duration) {
                lastSeek = 0;
            }
            isRecording = true;
            dubbingSubtitleView.setEditted(!isRecording);
        } else {
            if (dubbingVideoView.isPlaying()) {
                audioRecordHelper.stopRecord();
            }
            dubbingVideoView.stopDubbing();
            isRecording = false;
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
}
