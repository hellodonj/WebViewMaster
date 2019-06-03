package com.icedcap.dubbing.audio;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.text.TextUtils;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.icedcap.dubbing.R;
import com.lqwawa.client.pojo.StudyResPropType;
import com.lqwawa.libs.audio.RawAudioRecorder;
import com.lqwawa.tools.DialogHelper;
import com.oosic.apps.iemaker.base.evaluate.EvaluateHelper;
import com.oosic.apps.iemaker.base.evaluate.EvaluateItem;
import com.oosic.apps.iemaker.base.evaluate.EvaluateListener;
import com.oosic.apps.iemaker.base.evaluate.EvaluateManager;
import com.oosic.apps.iemaker.base.evaluate.EvaluateResult;
import com.oosic.apps.iemaker.base.onlineedit.CallbackListener;
import com.osastudio.common.utils.FileUtils;
import com.osastudio.common.utils.TipMsgHelper;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.PipedReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

/**
 * Created by icedcap on 14/05/2017.
 * <p>
 * the wave form by {@link Timer} when recording or playing an audio.
 */
public class AudioRecordHelper {

    private DialogHelper.LoadingDialog mLoadingDialog;
    private Context context;
    private List<File> mp3FileList;
    private List<String> audioRefText;
    private MediaPlayer mediaPlayer;
    private RawAudioRecorder audioRecorder;
    private CallbackListener listener;
    private CallbackListener evalResultListener;
    private boolean needEvalAudio = true;
    private int resPropertyValue;
    private String wholeRawFilePath;

    public List<File> getMp3FileList() {
        return mp3FileList;
    }

    public List<String> getAudioRefText() {
        return audioRefText;
    }


    public AudioRecordHelper(Context context,
                             int resPropertyValue,
                             CallbackListener listener,
                             CallbackListener evalResultListener) {
        mp3FileList = new ArrayList<>();
        audioRefText = new ArrayList<>();
        this.context = context;
        this.resPropertyValue = resPropertyValue;
        this.listener = listener;
        this.evalResultListener = evalResultListener;
    }

    public void startRecord(int position) {
        recordAudio(position);
    }


    public void stopRecord() {
        if (audioRecorder != null) {
            audioRecorder.stop();
            audioRecorder = null;
        }
    }

    public void play(final long seek, final int position, final CallbackListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                playWavFile(mp3FileList.get(position), 1, seek, listener);
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

    public void onStop() {
        stopMediaPlayer();
        stopRecord();
    }

    private void recordAudio(final int position) {
        final String savePath = mp3FileList.get(position).getAbsolutePath();
        if (audioRecorder == null) {
            audioRecorder = new RawAudioRecorder(context);
            audioRecorder.setListener(new RawAudioRecorder.RawRecorderListener() {
                @Override
                public void onRawRecordingStart() {

                }

                @Override
                public void onRawRecordingEnd(String encodedFilePath, String rawFilePath) {
                    if (needEvalAudio) {
                        if (!TextUtils.isEmpty(encodedFilePath) && !TextUtils.isEmpty(rawFilePath)) {
                            if (resPropertyValue == StudyResPropType.DUBBING_BY_WHOLE){
                                wholeRawFilePath = rawFilePath;
                                if (listener != null){
                                    listener.onBack(0);
                                }
                            } else {
                                evaluateRecordData(position, encodedFilePath, rawFilePath);
                            }
                        }
                    } else {
                        needEvalAudio = true;
                    }
                }

                @Override
                public void onRawRecordingError() {
                    dismissLoadingDialog();
                    deleteFile(savePath);
                    TipMsgHelper.ShowMsg(context, context.getString(R.string.str_record_err));
                }
            });
        }
        deleteFile(savePath);
        audioRecorder.start(savePath);
    }

    private void evaluateRecordData(int position,
                                    final String mp3FilePath,
                                    final String rawFilePath) {
        Dialog dialog = showLoadingDialog();
        dialog.setCancelable(false);
        final EvaluateManager evaluateManager = new EvaluateManager(context);
        EvaluateItem evaluateItem = EvaluateHelper.getEvaluateItem(1,
                audioRefText.get(position), rawFilePath);
        List<EvaluateItem> itemList = new ArrayList<>();
        itemList.add(evaluateItem);
        evaluateManager.evaluateAsync(itemList, new EvaluateListener() {
            @Override
            public void onEvaluateResult(EvaluateResult result) {
                dismissLoadingDialog();
                deleteFile(rawFilePath);
                if (result == null || !result.isSuccess()) {
//                    deleteFile(mp3FilePath);
//                    TipMsgHelper.ShowMsg(context, context.getString(R.string.str_evaluate_fail));
                    if (listener != null && evalResultListener != null) {
                        if (resPropertyValue == StudyResPropType.DUBBING_BY_WHOLE){
                            evalResultListener.onBack(0);
                        } else {
                            evalResultListener.onBack("");
                            listener.onBack(0);
                        }
                    }
                    return;
                }
                int evalScore = Math.round(result.getScore());
                if (listener != null && evalResultListener != null) {
                    if (resPropertyValue == StudyResPropType.DUBBING_BY_WHOLE){
                        evalResultListener.onBack(evalScore);
                    } else {
                        JSONArray jsonArray = JSONArray.parseArray(result.getResult());
                        if (jsonArray != null && jsonArray.size() > 0) {
                            evalResultListener.onBack(jsonArray.get(0).toString());
                        }
                        listener.onBack(evalScore);
                    }
                }
            }
        });
    }

    public void evalWholeDubbingVideo(){
        if (TextUtils.isEmpty(wholeRawFilePath)){
            return;
        }
        evaluateRecordData(0,null,wholeRawFilePath);
    }

    public boolean deleteFile(String path) {
        if (FileUtils.isFileExists(path)) {
            return FileUtils.deleteFile(path);
        }
        return true;
    }

    public void deleteMp3FileList() {
        if (mp3FileList != null && mp3FileList.size() > 0) {
            for (File mp3File : mp3FileList) {
                deleteFile(mp3File.getAbsolutePath());
            }
        }
    }

    private void playWavFile(File file, final float volume, final long seek, final CallbackListener listener) {
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
                    if (listener != null) {
                        listener.onBack(true);
                    }
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

    public void setNeedEvalAudio(boolean needEvalAudio) {
        this.needEvalAudio = needEvalAudio;
    }

    public Dialog showLoadingDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            return mLoadingDialog;
        }
        mLoadingDialog = DialogHelper.getIt((Activity) context).GetLoadingDialog(0);
        return mLoadingDialog;
    }

    public void dismissLoadingDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }

}
