package com.icedcap.dubbing.audio;

import android.content.Context;
import android.media.MediaPlayer;
import android.text.TextUtils;


import com.lqwawa.libs.audio.RawAudioRecorder;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

/**
 * Created by icedcap on 14/05/2017.
 * <p>
 * the wave form by {@link Timer} when recording or playing an audio.
 */
public class AudioRecordHelper {

    private List<File> mp3FileList;
    private MediaPlayer mediaPlayer;

    private RawAudioRecorder audioRecorder;

    public List<File> getMp3FileList() {
        return mp3FileList;
    }


    public AudioRecordHelper() {
        mp3FileList = new ArrayList<>();
    }

    public void startRecord(Context context, int position) {
        recordAudio(context, position);
    }


    public void stopRecord() {
        if (audioRecorder != null) {
            audioRecorder.stop();
            audioRecorder = null;
        }
    }

    public void play(final long seek, final int position) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                playWavFile(mp3FileList.get(position), 1, seek);
            }
        }).start();

    }

    public void stopMediaPlayer() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer = null;
        }
    }

    public void onResume() {

    }

    public void onPause() {
        stopMediaPlayer();
        stopRecord();
    }

    private void recordAudio(Context context, final int position) {
        final String savePath = mp3FileList.get(position).getAbsolutePath();
        if (audioRecorder == null) {
            audioRecorder = new RawAudioRecorder(context);
            audioRecorder.setListener(new RawAudioRecorder.RawRecorderListener() {
                @Override
                public void onRawRecordingStart() {

                }

                @Override
                public void onRawRecordingEnd(String encodedFilePath, String rawFilePath) {
                    if (!TextUtils.isEmpty(encodedFilePath) && !TextUtils.isEmpty(rawFilePath)) {
//                            showLoadingDialog();
//                            evaluateRecordData(encodedFilePath, rawFilePath);
                    }
                }

                @Override
                public void onRawRecordingError() {
//                        deleteFile(savePath);
//                        TipsHelper.showToast(SpeechAssessmentActivity.this, R.string.str_record_err);
                }
            });
        }
//            deleteFile(savePath);
        audioRecorder.start(savePath);
    }

    private void playWavFile(File file, final float volume, final long seek) {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }

        try {
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    if (volume >= 0 && volume <= 1) {
                        mp.setVolume(volume, volume);
                    }
                    mp.seekTo((int) seek);
                    mp.start();
                }
            });

            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    return false;
                }
            });

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.release();
                    mediaPlayer = null;
                }
            });

            FileDescriptor fd = null;
            FileInputStream fis = new FileInputStream(file);
            fd = fis.getFD();
            if (fd != null) {
                mediaPlayer.setDataSource(fd);
                mediaPlayer.prepare();
//                mediaPlayer.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
