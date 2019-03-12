package com.icedcap.dubbing.audio;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;


import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by dsq on 2017/5/2.
 */
public class AudioPlayHelper {
    private static final String LOG_TAG = AudioPlayHelper.class.getSimpleName();
    private static final String NAME_AUDIO_PERSONAL = "personal-audio";
    private static final String NAME_AUDIO_BACKGROUND = "background-audio";
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;
    private static final int EOF = -1;

    private ShortBuffer mSamples; // the samples to play
    private int mNumSamples; // number of samples to play
    private Thread mAudioPlayback;
    private ThreadPoolExecutor mExecutorService;
    private List<String> mRecordListPath;
    private int position = 0;


    // config param
    private static final int SAMPLE_RATE = 44100;
    private boolean mShouldContinue;

    private OnAudioRecordPlaybackListener mListener;
    private MediaPlayer mMediaPlayer; // use for play background audio (mp3)
    private MediaPlayer mWaveMediaPlayer; // use for play background audio (wav)
    private AudioTrack mAudioTrack; // use for play personal audio (pcm)

    public AudioPlayHelper(OnAudioRecordPlaybackListener listener) {
        mExecutorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);
        mListener = listener;
    }

    private void setListener(OnAudioRecordPlaybackListener listener) {
        mListener = listener;
    }


    // FIXME: 04/05/2017 HERE HAS BUGS >> WHEN A RECORD APPEND THE LAST RECORD AND PLAY THE TWO PARTS
    // FIXME: 04/05/2017 THE FIRST TIME PLAY IS MISS THE SECOND PART AUDIO, WHEN THE SECOND PLAY IS WORKED
    private void playPCMAudio(File file, float volume) {
        int bufferSize = AudioTrack.getMinBufferSize(SAMPLE_RATE,
                AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
        if (bufferSize == AudioTrack.ERROR || bufferSize == AudioTrack.ERROR_BAD_VALUE) {
            bufferSize = SAMPLE_RATE * 2;
        }

        if (mAudioTrack == null) {
            mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                    SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
                    bufferSize, AudioTrack.MODE_STREAM);
            if (volume >= 0 && volume <= 1) {
                mAudioTrack.setStereoVolume(volume, volume);
            }
        }

        mAudioTrack.setPlaybackPositionUpdateListener(new AudioTrack.OnPlaybackPositionUpdateListener() {
            @Override
            public void onMarkerReached(AudioTrack track) {
                Log.v(LOG_TAG, "Audio file end reached");
                track.release();
                if (mListener != null) {
                    mListener.onCompletion();
                }

            }

            @Override
            public void onPeriodicNotification(AudioTrack track) {
                if (mListener != null && track.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
                    mListener.onProgress((track.getPlaybackHeadPosition() * 1000) / SAMPLE_RATE);
                }
            }
        });
        mAudioTrack.setPositionNotificationPeriod(SAMPLE_RATE / 30); // 30 times per second
        mAudioTrack.setNotificationMarkerPosition(mNumSamples);

        mAudioTrack.play();

        Log.v(LOG_TAG, "Audio file started");

        short[] buffer = new short[bufferSize];
        try {
            short[] samples = getSamples(file);
            mSamples = ShortBuffer.wrap(samples);
            mNumSamples = samples.length;
        } catch (IOException e) {
            e.printStackTrace();
        }
        mSamples.rewind();
        int limit = mNumSamples;
        int totalWritten = 0;
        while (mSamples.position() < limit && mShouldContinue) {
            int numSamplesLeft = limit - mSamples.position();
            int samplesToWrite;
            if (numSamplesLeft >= buffer.length) {
                mSamples.get(buffer);
                samplesToWrite = buffer.length;
            } else {
                for (int i = numSamplesLeft; i < buffer.length; i++) {
                    buffer[i] = 0;
                }
                mSamples.get(buffer, 0, numSamplesLeft);
                samplesToWrite = numSamplesLeft;
            }
            totalWritten += samplesToWrite;
            mAudioTrack.write(buffer, 0, samplesToWrite);
        }

        if (!mShouldContinue || mSamples.position() >= limit) {
            mShouldContinue = false;
            if (mAudioTrack != null) {
                mAudioTrack.release();
            }
            mAudioTrack = null;
        }

        Log.v(LOG_TAG, "Audio streaming finished. Samples written: " + totalWritten);
    }

    private void playAudio(String name, File file, float volume) {
        if (!TextUtils.isEmpty(name)) {
            if (NAME_AUDIO_BACKGROUND.equals(name)) {
                playBackground(file.getAbsolutePath(), volume);
            } else if (NAME_AUDIO_PERSONAL.equals(name)) {
                playAudio(file.getAbsolutePath(), volume);
            }
        } else {
            if (file.getAbsolutePath().endsWith("mp3")) {
                playBackground(file.getAbsolutePath(), volume);
            } else if (file.getAbsolutePath().endsWith("wav")) {
                playAudio(file.getAbsolutePath(), volume);
            } else {
                playPCMAudio(file, volume);
            }
        }

    }

    private void playBackground(String path, final float volume) {
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
        }
        try {
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    if (volume >= 0 && volume <= 1) {
                        mp.setVolume(volume, volume);
                    }
                    mp.start();
                }
            });

            mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    Log.e(LOG_TAG, "Media Player onError");
                    return false;
                }
            });

            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.release();
                    mMediaPlayer = null;
                }
            });

            FileDescriptor fd = null;
            FileInputStream fis = new FileInputStream(path);
            fd = fis.getFD();
            if (fd != null) {
                mMediaPlayer.setDataSource(fd);
                mMediaPlayer.prepare();
//                mediaPlayer.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void playAudio(String path, final float volume) {
        if (mWaveMediaPlayer == null) {
            mWaveMediaPlayer = new MediaPlayer();
        }
        try {
            mWaveMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    if (volume >= 0 && volume <= 1) {
                        mp.setVolume(volume, volume);
                    }
                    mp.start();
                }
            });

            mWaveMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    Log.e(LOG_TAG, "Media Player onError");
                    return false;
                }
            });

            mWaveMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.release();
                    mWaveMediaPlayer = null;
                    Log.e("onCompletion",position+"");
                    if (position < mRecordListPath.size() - 1) {
                        position++;
                        playAudio(mRecordListPath.get(position), volume);
                    } else {
                        position = 0;

                    }
                }
            });

            FileDescriptor fd = null;
            FileInputStream fis = new FileInputStream(path);
            fd = fis.getFD();
            if (fd != null) {
                mWaveMediaPlayer.setDataSource(fd);
                mWaveMediaPlayer.prepare();
//                mediaPlayer.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isMediaPlaying(){
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            return true;
        }
        return false;
    }

    private void mediaPlaySeekTo(int seekTime){
        if (mMediaPlayer != null && !mMediaPlayer.isPlaying()){
            mMediaPlayer.seekTo(seekTime);
            mMediaPlayer.start();
        }
    }

    private void waveMediaSeekTo(int seekTime){
        if (mWaveMediaPlayer != null && !mWaveMediaPlayer.isPlaying()){
            mWaveMediaPlayer.seekTo(seekTime);
            mWaveMediaPlayer.start();
        }
    }

    private void pauseMediaPlayer(){
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()){
            mMediaPlayer.pause();
        }
    }

    private void pauseWaveMediaPlayer(){
        if (mWaveMediaPlayer != null && mWaveMediaPlayer.isPlaying()) {
            mWaveMediaPlayer.pause();
        }
    }

    private void stopMediaPlayer() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    private void stopWaveMediaPlayer() {
        if (mWaveMediaPlayer != null && mWaveMediaPlayer.isPlaying()) {
            mWaveMediaPlayer.stop();
            mWaveMediaPlayer.release();
            mWaveMediaPlayer = null;
        }
    }

    public void setVolume(float gain, MediaPlayer mediaPlayer, AudioTrack audioTrack) {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.setVolume(gain, gain);
        }
        if (audioTrack != null && isPlaying()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                audioTrack.setVolume(gain);
            } else {
                audioTrack.setStereoVolume(gain, gain);
            }
        }
    }

    public void setBackgroundVolume(float gain) {
        if (mMediaPlayer != null) {
            mMediaPlayer.setVolume(gain, gain);
        }
    }

    public void setPersonalVolume(float gain) {
        // personal audio play by mWaveMediaPlayer
        if (mWaveMediaPlayer != null) {
            mWaveMediaPlayer.setVolume(gain, gain);
        }
    }


    /**
     * Audio is playing or not
     */
    public boolean isPlaying() {
        return mAudioPlayback != null;
    }

    /**
     * Audio playback
     */
    public void startPlay() {
        if (mAudioPlayback != null) return;
        mShouldContinue = true;
        mAudioPlayback = new Thread(new Runnable() {
            @Override
            public void run() {
//                playAudio(mFile, -1);
            }
        });
        mAudioPlayback.start();
    }

    /**
     * Audio stop play
     */
    public void stopPlay() {
        if (isPlaying()) {
            mShouldContinue = false;
            mAudioPlayback = null;
        }
    }

    public void playCombineAudio(String backgroundAudioPath,
                                 float personalVolume, float backgroundVolume, List<String> recordListPath) {
        if (mExecutorService == null) {
            mExecutorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);
        }
        this.mRecordListPath = recordListPath;
        AudioPlayTask personalAudio = new AudioPlayTask(NAME_AUDIO_PERSONAL, mRecordListPath.get(position), personalVolume);
        AudioPlayTask backgroundAudio = new AudioPlayTask(NAME_AUDIO_BACKGROUND, backgroundAudioPath, backgroundVolume);
        mShouldContinue = true;
        mExecutorService.execute(personalAudio);
        mExecutorService.execute(backgroundAudio);
    }

    /**
     * stop personal & background audio if playing
     */
    public void stopCombineAudio() {
        stopMediaPlayer();
        stopWaveMediaPlayer();
    }


    private short[] getSamples(File file) throws IOException {
        InputStream is = new BufferedInputStream(new FileInputStream(file), 8 * 1024);
        byte[] data;
        try {
            data = toByteArray(is);
        } finally {
            if (is != null) {
                is.close();
            }
        }

        ShortBuffer sb = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer();
        short[] samples = new short[sb.limit()];
        sb.get(samples);
        return samples;
    }

    private byte[] toByteArray(final InputStream input) throws IOException {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        long count = 0;
        int n;
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        while (EOF != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return output.toByteArray();
    }

    public interface OnAudioRecordPlaybackListener {

        void onAudioDataReceived(long duration, long bytesRead);

        void onProgress(int pos);

        void onCompletion();
    }

    private class AudioPlayTask implements Runnable {
        private float volume;
        private String name;
        private String audioFilePath;

        public AudioPlayTask(String name, String audioFilePath,
                             float volume) {
            this.name = name;
            this.audioFilePath = audioFilePath;
            this.volume = volume;
        }

        @Override
        public void run() {
            File file = new File(audioFilePath);
            if (file.exists()) {
                playAudio(name, file, volume);
            }
        }
    }

    public void seekTo(int seekTime){
        mediaPlaySeekTo(seekTime);
        waveMediaSeekTo(seekTime);
    }

    public void onPause(){
        pauseMediaPlayer();
        pauseWaveMediaPlayer();
    }

    public void onStop() {
        stopPlay();
        stopCombineAudio();
    }
}
