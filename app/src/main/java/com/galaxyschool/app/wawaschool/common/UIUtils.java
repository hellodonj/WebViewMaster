package com.galaxyschool.app.wawaschool.common;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.lqwawa.client.pojo.SourceFromType;

import java.util.Timer;
import java.util.TimerTask;

public class UIUtils {
    //当前资源所在的模块(默认为其他)
    public static int currentSourceFromType = SourceFromType.OTHER;

    private static long lastClickTime;
    public synchronized static boolean isFastClick() {
        long time = System.currentTimeMillis();
        if ( time - lastClickTime < 2000) {
            return true;
        }
        lastClickTime = time;
        return false;
    }
    public static void showSoftKeyboard(Activity activity) {
        try {
            ((InputMethodManager) activity
                .getSystemService(Context.INPUT_METHOD_SERVICE))
                .showSoftInputFromInputMethod(
                    activity.getCurrentFocus()
                        .getWindowToken(), InputMethodManager.SHOW_FORCED);
        } catch (Exception e) {

        }
    }

    public static void showSoftKeyboard1(Activity activity) {
        try {
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {

        }
    }

    public static void hideSoftKeyboard1(Activity activity,View view) {
        try {
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception e) {

        }
    }

    public static void hideSoftKeyboard(Activity activity) {
        try {
            ((InputMethodManager) activity
                .getSystemService(Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(
                    activity.getCurrentFocus()
                        .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {

        }
    }

    public static void showSoftKeyboardOnEditText(final EditText editText){
        if (editText==null){
            return;
        }
        InputMethodManager inputManager =
                (InputMethodManager)editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(editText, 0);
    }

    public static void showSoftKeyboardValid(Activity activity) {
        try {
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {

        }
    }
    public static void hideSoftKeyboardValid(Activity activity,EditText editText) {
        try {
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        } catch (Exception e) {

        }
    }

}
