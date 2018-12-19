package com.galaxyschool.app.wawaschool;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

/**
 * @author: wangchao
 * @date: 2018/09/25
 * @desc:
 */
public class MultiVideoPlayActivity extends BaseFragmentActivity implements SurfaceHolder.Callback {

    public static final int DURATION_UPDATE_TIME = 1000;
    public static final int ONE_SECOND_MS = 1000;

    //用于播放视频的mediaPlayer对象
    private MediaPlayer firstPlayer,     //负责播放进入视频播放界面后的第一段视频
            nextMediaPlayer, //负责一段视频播放结束后，播放下一段视频
            cachePlayer,     //负责setNextMediaPlayer的player缓存对象
            currentPlayer;   //负责当前播放视频段落的player对象
    private SurfaceHolder surfaceHolder;

    SeekBar seekBar;
    TextView durationTxt;
    ImageView playBtn;
    RelativeLayout topLayout;
    LinearLayout bottomLayout;

    //存放所有视频端的url
    private ArrayList<VideoEntity> videoEntities = new ArrayList<>();
    //所有player对象的缓存
    private HashMap<String, MediaPlayer> playersCache = new HashMap<String, MediaPlayer>();
    //当前播放到的视频段落数
    private int currentVideoIndex;
    //所有视频的时长
    private int totalDuration = 0;
    //当前播放视频之前所有视频累加时长
    private int baseDuration = 0;

    String videoName;
    boolean isPlaying = false;

    Handler handler = new Handler();

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            int currentDuration = 0;
            if (currentVideoIndex == 0) {
                currentDuration = firstPlayer.getCurrentPosition();
            } else {
                if (currentPlayer != null) {
                    currentDuration = currentPlayer.getCurrentPosition() + baseDuration;
                }
            }
            seekBar.setProgress(currentDuration / ONE_SECOND_MS);
            durationTxt.setText(getDurationTime(currentDuration, totalDuration));
            handler.postDelayed(runnable, DURATION_UPDATE_TIME);
        }
    };

    public static void start(Context context) {
        ArrayList<String> videoPaths = new ArrayList<>();
        videoPaths.add("http://cdn.ebanshu.net/ebs_index/video/板书直播2(2018-08-10_11:33:03).mp4");
        videoPaths.add("http://cdn.ebanshu.net/ebs_index/video/板书直播2(2018-08-10_17:47:15).mp4");
        videoPaths.add("http://cdn.ebanshu.net/ebs_index/video/板书直播2(2018-09-05_09:42:21).mp4");
        videoPaths.add("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4");
        ArrayList<Integer> videoDurations = new ArrayList<>();
        videoDurations.add(52459);
        videoDurations.add(39667);
        videoDurations.add(88250);
        videoDurations.add(60000);
        start(context, "板书直播2", videoPaths, videoDurations);
    }

    public static void start(Context context, String videoName, ArrayList<String> videoPaths,
                             ArrayList<Integer> videoDurations) {
        Intent starter = new Intent(context, MultiVideoPlayActivity.class);
        starter.putExtra("videoName", videoName);
        starter.putStringArrayListExtra("videoPaths", videoPaths);
        starter.putIntegerArrayListExtra("videoDurations", videoDurations);
        context.startActivity(starter);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_video_play);
        initData();
        initViews();
    }

    private void initData() {
        videoName = getIntent().getStringExtra("videoName");
        List<String> videoPaths = getIntent().getStringArrayListExtra("videoPaths");
        List<Integer> videoDurations = getIntent().getIntegerArrayListExtra("videoDurations");

        //获取该直播间所有视频分段的url
        getVideoUrls(videoPaths, videoDurations);
    }

    protected void initViews() {
        //负责配合mediaPlayer显示视频图像播放的surfaceView
        SurfaceView surface = (SurfaceView) findViewById(R.id.surface);

        // SurfaceHolder是SurfaceView的控制接口
        surfaceHolder = surface.getHolder();
        // 因为这个类实现了SurfaceHolder.Callback接口，所以回调参数直接this
        surfaceHolder.addCallback(this);

        TextView titleTxt = (TextView) findViewById(R.id.txt_title);
        titleTxt.setText(videoName);
        durationTxt = (TextView) findViewById(R.id.txt_duration);
        playBtn = (ImageView) findViewById(R.id.btn_play);
        topLayout = (RelativeLayout) findViewById(R.id.top_layout);
        bottomLayout = (LinearLayout) findViewById(R.id.bottom_layout);

        //点击屏幕任何地点，控制底部状态栏和标题的隐藏或显示
        final FrameLayout videoLayout = (FrameLayout) findViewById(R.id.videoLayout);
        videoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                setVisibility(topLayout);
                setVisibility(bottomLayout);
            }
        });

        seekBar = (SeekBar) findViewById(R.id.seek_bar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                pause();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                updateSeekBar(seekBar.getProgress());
            }
        });

        ImageView backView = (ImageView) findViewById(R.id.btn_back);
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlaying) {
                    pause();
                } else {
                    play();
                }
            }
        });

    }

    private void pause() {
        isPlaying = false;
        switchPlayButton();
        if (currentVideoIndex == 0) {
            if (firstPlayer != null) {
                firstPlayer.pause();
            }
        } else {
            if (currentPlayer != null) {
                currentPlayer.pause();
            }
        }
    }

    private void play() {
        isPlaying = true;
        switchPlayButton();

        if (currentVideoIndex == 0) {
            if (firstPlayer != null) {
                firstPlayer.start();
            }
        } else {
            if (currentPlayer != null) {
                currentPlayer.start();
            }
        }
    }

    private void updateSeekBar(int progress) {
        //所有的MediaPlayer prepare后才允许seek
        if (playersCache.size() + 1 != videoEntities.size()) {
            return;
        }

        int duration = progress * ONE_SECOND_MS;

        int videoIndex = getVideoIndex(duration);
        int videoSum = 0;
        if (videoIndex > 0) {
            videoSum = getVideoSum(videoIndex - 1);
        }
        baseDuration = videoSum;
        if (currentVideoIndex == videoIndex) {
            if (videoIndex == 0) {
                seekTo(firstPlayer, duration);
            } else {
                seekTo(currentPlayer, duration - videoSum);
            }
            play();
        } else {
            if (currentVideoIndex == 0) {
                if (videoIndex > 0) {
                    currentVideoIndex = videoIndex;
                }
                onVideoPlayCompleted(firstPlayer);
                if (currentPlayer != null) {
                    seekTo(currentPlayer, duration - videoSum);
                }
                play();
            } else {
                currentVideoIndex = videoIndex;
                if (currentPlayer != null) {
                    currentPlayer.setDisplay(null);
                }
                if (videoIndex == 0) {
                    if (firstPlayer != null) {
                        firstPlayer.setDisplay(surfaceHolder);
                        seekTo(firstPlayer, duration);
                    }
                } else {
                    onVideoPlayCompleted(currentPlayer);
                    if (currentPlayer != null) {
                        seekTo(currentPlayer, duration - videoSum);
                    }
                }
                play();
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private String getFormatTime(long time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        return simpleDateFormat.format(time);
    }

    private String getDurationTime(long currentDuration, long duration) {
        return getFormatTime(currentDuration) + "/" +
                getFormatTime(duration);
    }

    private void setVisibility(View view) {
        if (view.getVisibility() == View.VISIBLE) {
            view.setVisibility(View.GONE);
        } else {
            view.setVisibility(View.VISIBLE);
        }
    }

    private void switchPlayButton() {
        playBtn.setImageResource(isPlaying ? R.drawable.letv_skin_v4_btn_pause :
                R.drawable.letv_skin_v4_btn_play);
    }

    private void seekTo(MediaPlayer mediaPlayer, int duration) {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(duration);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //然后初始化播放手段视频的player对象
        seekBar.setMax(totalDuration / ONE_SECOND_MS);
        durationTxt.setText(getDurationTime(0, totalDuration));
        initFirstPlayer();
        handler.postDelayed(runnable, DURATION_UPDATE_TIME);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        pause();
    }

    /**
     * 界面销毁时，release各个mediaplayer
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (firstPlayer != null) {
            if (firstPlayer.isPlaying()) {
                firstPlayer.stop();
            }
            firstPlayer.release();
        }
        if (nextMediaPlayer != null) {
            if (nextMediaPlayer.isPlaying()) {
                nextMediaPlayer.stop();
            }
            nextMediaPlayer.release();
        }

        if (currentPlayer != null) {
            if (currentPlayer.isPlaying()) {
                currentPlayer.stop();
            }
            currentPlayer.release();
        }
        currentPlayer = null;

        handler.removeCallbacks(runnable);
    }

    /**
     * 初始化播放首段视频的player
     */
    private void initFirstPlayer() {
        firstPlayer = new MediaPlayer();
        firstPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        firstPlayer.setDisplay(surfaceHolder);

        firstPlayer
                .setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        baseDuration = mp.getDuration() + baseDuration;
                        currentVideoIndex = currentVideoIndex + 1;
                        onVideoPlayCompleted(mp);
                    }
                });

        //设置cachePlayer为该player对象
        cachePlayer = firstPlayer;
        //player对象初始化完成后，开启播放
        startPlayFirstVideo();

        initNextPlayer();
    }

    private void startPlayFirstVideo() {
        showLoadingDialog();
        try {
            firstPlayer.setDataSource(videoEntities.get(currentVideoIndex).url);
            firstPlayer.prepareAsync();
            firstPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    dismissLoadingDialog();
                    firstPlayer.start();
                    isPlaying = true;
                    switchPlayButton();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 新开线程负责初始化负责播放剩余视频分段的player对象,避免UI线程做过多耗时操作
     */
    private void initNextPlayer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 1; i < videoEntities.size(); i++) {
                    nextMediaPlayer = new MediaPlayer();
                    nextMediaPlayer
                            .setAudioStreamType(AudioManager.STREAM_MUSIC);
                    nextMediaPlayer
                            .setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {
                                    baseDuration = mp.getDuration() + baseDuration;
                                    currentVideoIndex = currentVideoIndex + 1;
                                    onVideoPlayCompleted(mp);
                                }
                            });
                    try {
                        nextMediaPlayer.setDataSource(videoEntities.get(i).url);
                        nextMediaPlayer.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    //set next mediaplayer
                    cachePlayer.setNextMediaPlayer(nextMediaPlayer);
                    //set new cachePlayer
                    cachePlayer = nextMediaPlayer;
                    //put nextMediaPlayer in cache
                    playersCache.put(String.valueOf(i), nextMediaPlayer);

                }
            }
        }).start();
    }

    /**
     * 负责处理一段视频播放过后，切换player播放下一段视频
     */
    private void onVideoPlayCompleted(MediaPlayer mp) {
        if (mp != null) {
            mp.setDisplay(null);
        }
        //get next player
        currentPlayer = playersCache.get(String.valueOf(currentVideoIndex));
        if (currentPlayer != null) {
            currentPlayer.setDisplay(surfaceHolder);
        } else {
            if (isFinishing()) {
                return;
            }
            isPlaying = false;
            currentVideoIndex = 0;
            if (firstPlayer != null) {
                firstPlayer.setDisplay(surfaceHolder);
                seekTo(firstPlayer, 0);
            }
            switchPlayButton();
            //Toast.makeText(MultiVideoPlayActivity.this, "视频播放完毕..", Toast.LENGTH_SHORT)
            //        .show();
        }
    }

    private void getVideoUrls(List<String> videoPaths, List<Integer> videoDurations) {
        if (videoPaths == null || videoPaths.isEmpty() || videoDurations == null ||
                videoDurations.isEmpty() || videoPaths.size() != videoDurations.size()) {
            return;
        }
        for (int i = 0, size = videoPaths.size(); i < size; i++) {
            String path = videoPaths.get(i);
            if (!TextUtils.isEmpty(path) && path.contains("https")) {
                path = path.replace("https", "http");
            }
            videoEntities.add(new VideoEntity(path, videoDurations.get(i)));
        }

        if (videoEntities == null || videoEntities.isEmpty()) {
            return;
        }

        for (VideoEntity entity : videoEntities) {
            if (entity != null) {
                totalDuration = totalDuration + entity.duration;
                entity.sum = totalDuration;
            }
        }
    }

    private int getVideoIndex(int duration) {
        for (int i = 0; i < videoEntities.size(); i++) {
            VideoEntity videoEntity = videoEntities.get(i);
            if (videoEntity != null) {
                if (duration < videoEntity.sum) {
                    return i;
                }
            }
        }
        return 0;
    }

    private int getVideoSum(int index) {
        if (index < videoEntities.size()) {
            return videoEntities.get(index).sum;
        }
        return 0;
    }

    class VideoEntity {
        String url;
        int duration;
        int sum;

        VideoEntity(String url, int duration) {
            this.url = url;
            this.duration = duration;
        }
    }
}
