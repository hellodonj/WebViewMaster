package com.lqwawa.intleducation.base.widgets;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import com.lqwawa.intleducation.base.utils.LogUtil;
/**
 * Created by XChen on 2017/2/9.
 * email:man0fchina@foxmail.com
 */

public class AutoTransButton extends Button {

    public AutoTransButton(Context context) {
        super(context);
        init();
    }

    public AutoTransButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AutoTransButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(21)
    public AutoTransButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    CountTimeThread countTimeThread = null;
    private void init(){
        countTimeThread = new CountTimeThread(3);
        countTimeThread.start();
    }

    private void doTrans(final int alpha){
        ((Activity) getContext()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AutoTransButton.this.getBackground().setAlpha(alpha);
            }
        });
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        LogUtil.e("AutoTransButton", "" + visibility);
        if (visibility == View.VISIBLE) {
            doTrans(255);
            countTimeThread.reset();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            countTimeThread.pause();
            doTrans(255);
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            countTimeThread.reset();
            doTrans(255);
        }
        return super.dispatchTouchEvent(event);
    }
    /**
     * 用进程监听按钮控件的显示时间，定时隐藏
     *
     * @author zxl
     */
    private class CountTimeThread extends Thread {
        private final long maxVisibleTime;
        private long startVisibleTime;
        private boolean isPause = false;

        /**
         * 设置控件显示时间 second单位是秒
         *
         * @param second
         */
        public CountTimeThread(int second) {
            maxVisibleTime = second * 1000;//换算为毫秒
            setDaemon(true);//设置为后台进程
        }

        @Override
        public synchronized void start() {
            isPause = false;
            super.start();
        }

        /**
         * 如果用户有操作，则重新开始计时隐藏时间
         */
        private synchronized void reset() {
            isPause = false;
            startVisibleTime = System.currentTimeMillis();
        }

        public void pause(){
            isPause = true;
        }

        @Override
        public void run() {
            startVisibleTime = System.currentTimeMillis();//初始化开始时间

            while (true) {
                if (getContext() == null){
                    break;
                }
                if (isPause){
                    continue;
                }
                //如果时间达到最大时间，则发送隐藏消息
                if (startVisibleTime + maxVisibleTime < System.currentTimeMillis()) {
                    doTrans(60);
                    startVisibleTime = System.currentTimeMillis();
                }

                try {
                    Thread.sleep(1000);
                } catch (Exception e) {

                }
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }
}
