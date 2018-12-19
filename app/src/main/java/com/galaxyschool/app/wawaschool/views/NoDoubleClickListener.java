package com.galaxyschool.app.wawaschool.views;

import android.view.View;

import java.util.Calendar;

/**
 * ================================================
 * 作    者：Blizzard-liu
 * 版    本：1.0
 * 创建日期：2017/7/7 17:55
 * 描    述：防止view快速点击
 * 修订历史：
 * ================================================
 */

public abstract class NoDoubleClickListener implements View.OnClickListener {

    private static final int MIN_CLICK_DELAY_TIME = 1500;
    private long lastClickTime = 0;
    @Override
    public void onClick(View view) {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime;
            onNoDoubleClick(view);
        }
    }
    protected abstract void onNoDoubleClick(View v);
}
