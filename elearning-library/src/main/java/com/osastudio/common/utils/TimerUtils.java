package com.osastudio.common.utils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * ======================================================
 * Created by : Brave_Qu on 2017/12/4 10:04
 * E-Mail Address:863378689@qq.com
 * Describe:实时的记录当前观看或者制作的时间
 * ======================================================
 */

public class TimerUtils {
    //定时器需要的参数
    private Timer mTimer;
    private TimerTask mTimeTask;
    private int currentTotalTime;//当前记录的时间
    private boolean isRecording = true;//容许当前可以记录时间的条件
    /**
     * @return TimerUtils的实例
     */
    public static TimerUtils getInstance() {
        return TimerUtilHolder.instance;
    }

    /**
     * 实例唯一
     */
    private static class TimerUtilHolder {
        private static TimerUtils instance = new TimerUtils();
    }

    /**
     * 开始计时器的操作
     */
    public void startTimer() {
        currentTotalTime = 0;
        if (mTimer == null) {
            mTimer = new Timer();
        }
        if (mTimeTask != null){
            mTimeTask.cancel();
        }
        mTimeTask = new TimerTask() {
            @Override
            public void run() {
                //是否可以记录时间的条件
                if (isRecording) {
                    ++currentTotalTime;
                }
            }
        };
        if (mTimer != null && mTimeTask != null) {
            mTimer.schedule(mTimeTask, 1, 1000);
        }
    }

    /**
     * 暂停定时器的运行
     */
    public void pauseTimer(){
        isRecording = false;
    }
    /**
     * 恢复定时器的运行
     */
    public void resumeTimer(){
        isRecording = true;
    }
    /**
     * 停止定时器的运行
     */
    public void stopTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (mTimeTask != null) {
            mTimeTask.cancel();
            mTimeTask = null;
        }
        isRecording = true;
    }

    /**
     * @return 返回当前的isRecording的状态
     */
    public boolean getRecording(){
        return isRecording;
    }

    /**
     * @return 记录的时间
     */
    public int getCurrentTotalTime(){
        return currentTotalTime;
    }
}
