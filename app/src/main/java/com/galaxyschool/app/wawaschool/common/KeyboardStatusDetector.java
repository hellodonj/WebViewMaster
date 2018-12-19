package com.galaxyschool.app.wawaschool.common;

import android.app.Activity;
import android.graphics.Rect;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewTreeObserver;

/**
 * ======================================================
 * Created by : Brave_Qu on 2017/12/16 10:32
 * E-Mail Address:863378689@qq.com
 * Describe:软键盘的收起和弹出监听
 * ======================================================
 */

public class KeyboardStatusDetector {
    private static final int SOFT_KEY_BOARD_MIN_HEIGHT = 100;

    private KeyboardVisibilityListener mVisibilityListener;

    boolean keyboardVisible = false;

    public static KeyboardStatusDetector getInstance() {
        return KeyboardStatusDetectorHolder.instance;
    }

    private static class KeyboardStatusDetectorHolder {
        private static KeyboardStatusDetector instance = new KeyboardStatusDetector();
    }

    public KeyboardStatusDetector registerFragment(Fragment f) {
        registerView(f.getView());
        return this;
    }

    public KeyboardStatusDetector registerActivity(Activity a) {
        registerView(a.getWindow().getDecorView().findViewById(android.R.id.content));
        return this;
    }

    public KeyboardStatusDetector registerView(final View v) {
        v.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                v.getWindowVisibleDisplayFrame(r);
                int heightDiff = v.getRootView().getHeight() - (r.bottom - r.top);
                if (heightDiff > SOFT_KEY_BOARD_MIN_HEIGHT) {
                    if (!keyboardVisible) {
                        keyboardVisible = true;
                        if (mVisibilityListener != null) {
                            mVisibilityListener.onVisibilityChanged(true);
                        }
                    }
                } else {
                    if (keyboardVisible) {
                        keyboardVisible = false;
                        if (mVisibilityListener != null) {
                            mVisibilityListener.onVisibilityChanged(false);
                        }
                    }
                }
            }
        });

        return this;
    }

    public KeyboardStatusDetector setmVisibilityListener(KeyboardVisibilityListener listener) {
        mVisibilityListener = listener;
        return this;
    }

    public interface KeyboardVisibilityListener {
        void onVisibilityChanged(boolean keyboardVisible);
    }
}
