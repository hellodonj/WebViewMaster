package com.icedcap.dubbing.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.frank.ffmpeg.FFmpegCmd;
import com.frank.ffmpeg.FFmpegUtil;
import com.naman14.androidlame.AndroidLame;
import com.naman14.androidlame.LameBuilder;
import com.naman14.androidlame.WaveReader;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @desc:
 * @author: wangchao
 * @date: 2019/02/25
 */
public class ProcessUtils {

    private static boolean isWorking = false;
    private static final int OUTPUT_STREAM_BUFFER = 8192;
    private static final int CHUNK_SIZE = 8192;

    public interface OnProcessListener {
        /**
         * 处理开始
         * 显示外部加载loading
         */
        void onProcessBegin();

        /**
         * 处理结束
         * 隐藏外部加载loading
         *
         * @param videoPath 最终视频链接
         */
        void onProcessEnd(String videoPath);
    }

    public void process(Context context, List<String> wavFilePaths, List<String> mp3FilePaths,
                        String backgroundFilePath, String videoFilePath,
                        OnProcessListener listener) {
        if (!isWorking) {
            isWorking = true;
            ProcessAsyncTask processAsyncTask = new ProcessAsyncTask(context,
                    wavFilePaths, mp3FilePaths, backgroundFilePath, videoFilePath, listener);
            processAsyncTask.execute((Void) null);
        }
    }

    private List<String> convertWavToMp3(Context context, List<String> wavFilePaths) {
        if (wavFilePaths == null || wavFilePaths.size() == 0) {
            return null;
        }
        List<String> mp3FilePaths = new ArrayList<>();
        for (int i = 0; i < wavFilePaths.size(); i++) {
            BufferedOutputStream outputStream = null;
            File input = new File(wavFilePaths.get(i));
            final File output = new File(context.getExternalCacheDir(), "tmp" + i + ".mp3");
            final WaveReader waveReader = new WaveReader(input);
            try {
                waveReader.openWave();
            } catch (IOException e) {
                e.printStackTrace();
            }
            AndroidLame androidLame = new LameBuilder()
                    .setInSampleRate(waveReader.getSampleRate())
                    .setOutChannels(waveReader.getChannels())
                    .setOutBitrate(128)
                    .setOutSampleRate(waveReader.getSampleRate())
                    .setQuality(5)
                    .build();
            try {
                outputStream = new BufferedOutputStream(new FileOutputStream(output), OUTPUT_STREAM_BUFFER);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            int bytesRead = 0;

            short[] buffer_l = new short[CHUNK_SIZE];
            short[] buffer_r = new short[CHUNK_SIZE];
            byte[] mp3Buf = new byte[CHUNK_SIZE];

            int channels = waveReader.getChannels();
            while (true) {
                try {
                    if (channels == 2) {
                        bytesRead = waveReader.read(buffer_l, buffer_r, CHUNK_SIZE);
                        if (bytesRead > 0) {
                            int bytesEncoded = 0;
                            bytesEncoded = androidLame.encode(buffer_l, buffer_r, bytesRead, mp3Buf);
                            if (bytesEncoded > 0) {
                                try {
                                    outputStream.write(mp3Buf, 0, bytesEncoded);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            break;
                        }
                    } else {
                        bytesRead = waveReader.read(buffer_l, CHUNK_SIZE);

                        if (bytesRead > 0) {
                            int bytesEncoded = 0;

                            bytesEncoded = androidLame.encode(buffer_l, buffer_l, bytesRead, mp3Buf);

                            if (bytesEncoded > 0) {
                                try {
                                    outputStream.write(mp3Buf, 0, bytesEncoded);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            break;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            int outputMp3buf = androidLame.flush(mp3Buf);
            if (outputMp3buf > 0) {
                try {
                    outputStream.write(mp3Buf, 0, outputMp3buf);
                    outputStream.close();
                    mp3FilePaths.add(output.getAbsolutePath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        deleteFiles(wavFilePaths);
        return mp3FilePaths;
    }

    private String concatMp3(Context context, List<String> mp3FilePaths, String backgroundFilePath,
                             String videoFilePath) {
        if (mp3FilePaths == null || mp3FilePaths.isEmpty() || TextUtils.isEmpty(videoFilePath)) {
            return null;
        }
        String extractVideoFilePath
                = context.getExternalCacheDir() + File.separator + "extractVideo.mp4";
        String mixFilePath = context.getExternalCacheDir() + File.separator + "mix.aac";
        String muxFilePath = context.getExternalCacheDir() + File.separator + "extract.mp4";

        String wholeMp3FilePath;
        String command;
        List<String> tmpMixFilePaths = new ArrayList<>();
        List<String> commandList = new ArrayList<>();
        int size = mp3FilePaths.size() - 1;

        // 两个及以上mp3文件，先依次连接，再与背景音乐混合；从视频中抽出无声视频，与混合后的音频文件混合为最终的视频文件。
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                File file = new File(context.getExternalCacheDir(), "tmpmix" + i + ".mp3");
                tmpMixFilePaths.add(file.getAbsolutePath());
            }
            command = FFmpegUtil.concatAudio(mp3FilePaths.get(0), mp3FilePaths.get(1),
                    tmpMixFilePaths.get(0));
            commandList.add(command);
            for (int i = 1; i < tmpMixFilePaths.size(); i++) {
                command = FFmpegUtil.concatAudio(tmpMixFilePaths.get(i - 1),
                        mp3FilePaths.get(i + 1), tmpMixFilePaths.get(i));
                commandList.add(command);
            }
            wholeMp3FilePath = tmpMixFilePaths.get(tmpMixFilePaths.size() - 1);
        } else {
            wholeMp3FilePath = mp3FilePaths.get(0);
        }
        if (TextUtils.isEmpty(backgroundFilePath)) {
            mixFilePath = wholeMp3FilePath;
        } else {
            command = FFmpegUtil.mixAudio(backgroundFilePath,
                    wholeMp3FilePath, mixFilePath);
            commandList.add(command);
        }
        command = FFmpegUtil.extractVideo(videoFilePath,
                extractVideoFilePath);
        commandList.add(command);
        command = FFmpegUtil.mediaMux(extractVideoFilePath, mixFilePath, muxFilePath);
        commandList.add(command);
        if (!commandList.isEmpty()) {
            int result = FFmpegCmd.execute(commandList);
            if (result == 0) {
                deleteFiles(mp3FilePaths);
                deleteFiles(tmpMixFilePaths);
                deleteFile(mixFilePath);
                deleteFile(extractVideoFilePath);
                return muxFilePath;
            }
        }
        return null;
    }

    /**
     * 删除单个文件
     *
     * @param filePath 要删除的文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    private void deleteFile(String filePath) {
        File file = new File(filePath);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {

            }
        }
    }

    private void deleteFiles(List<String> filePaths) {
        if (filePaths == null || filePaths.isEmpty()) {
            return;
        }
        for (String path : filePaths) {
            deleteFile(path);
        }
    }


    @SuppressLint("StaticFieldLeak")
    class ProcessAsyncTask extends AsyncTask<Void, Integer, String> {
        private Context context;
        private List<String> wavFilePaths;
        private List<String> mp3FilePaths;
        private String backgroundFilePath;
        private String videoFilePath;
        private OnProcessListener listener;

        ProcessAsyncTask(Context context, List<String> wavFilePaths, List<String> mp3FilePaths,
                         String backgroundFilePath, String videoFilePath,
                         OnProcessListener listener) {
            this.context = context;
            this.wavFilePaths = wavFilePaths;
            this.mp3FilePaths = mp3FilePaths;
            this.backgroundFilePath = backgroundFilePath;
            this.videoFilePath = videoFilePath;
            this.listener = listener;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (listener != null) {
                listener.onProcessBegin();
            }
        }

        @Override
        protected String doInBackground(Void... voids) {
            if (wavFilePaths != null && !wavFilePaths.isEmpty()) {
                List<String> mp3Paths = convertWavToMp3(context, wavFilePaths);
                return concatMp3(context, mp3Paths, backgroundFilePath, videoFilePath);
            } else if (mp3FilePaths != null && !mp3FilePaths.isEmpty()) {
                return concatMp3(context, mp3FilePaths, backgroundFilePath, videoFilePath);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            isWorking = false;
            if (listener != null) {
                listener.onProcessEnd(result);
            }
        }
    }

}
