package com.frank.ffmpeg;

import android.text.TextUtils;

import java.util.List;

public class FFmpegCmd {

    public interface OnHandleListener {
        void onBegin();

        void onEnd(int result);
    }

    static {
        System.loadLibrary("media-handle");
    }

    //开子线程调用native方法进行音视频处理
    public static void execute(final String[] commands, final OnHandleListener onHandleListener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (onHandleListener != null) {
                    onHandleListener.onBegin();
                }
                //调用ffmpeg进行处理
                int result = handle(commands);
                if (onHandleListener != null) {
                    onHandleListener.onEnd(result);
                }
            }
        }).start();
    }

    //开子线程调用native方法进行音视频处理
    public static int execute(final List<String> cmdList) {
        //调用ffmpeg进行处理
        int result = -1;
        for (String item : cmdList) {
            if (!TextUtils.isEmpty(item)) {
                String[] commands = FFmpegUtil.split(item);
                result = handle(commands);
                if (result != 0) {
                    break;
                }
            }
        }
        return result;
    }

    private native static int handle(String[] commands);

}