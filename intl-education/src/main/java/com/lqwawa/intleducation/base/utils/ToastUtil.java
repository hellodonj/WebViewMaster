package com.lqwawa.intleducation.base.utils;
import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

public class ToastUtil {

    /**
     * 自定义Toast
     *
     * @param context
     * @param message
     */
    public static void showToast(Context context, String message) {
        Toast toast = Toast.makeText(context, message, 0);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static void showToastBottom(Context context, String message) {
        Toast toast = Toast.makeText(context, message, 0);
        toast.setGravity(Gravity.BOTTOM, 0, 20);
        toast.show();
    }

    /**
     * 自定义Toast
     *
     * @param context
     * @param resId
     */
    public static void showToast(Context context, int resId) {
        Toast toast = Toast.makeText(context, context.getResources().getString(resId), 0);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static void showToastBottom(Context context, int resId) {
        Toast toast = Toast.makeText(context, context.getResources().getString(resId), 0);
        toast.setGravity(Gravity.BOTTOM, 0, 20);
        toast.show();
    }

    /**
     * 自定义Toast
     *
     * @param activity
     * @param message
     */
    public static void showToast(Activity activity, String message) {
        showToast(activity, message, 1);
    }

    public static void showToastBottom(Activity activity, String message) {
        showToastBottom(activity, message, 1);
    }

    /**
     * 自定义Toast
     *
     * @param activity
     * @param message
     * @param time
     */
    public static void showToast(Activity activity, String message, int time) {
        showToast(activity, message, time, Gravity.CENTER, 0, 0);
    }

    public static void showToastBottom(Activity activity, String message, int time) {
        showToast(activity, message, time, Gravity.BOTTOM, 0, 20);
    }

    /**
     * 自定义Toast
     *
     * @param activity
     * @param message
     * @param time
     * @param gravity
     * @param xOffset
     * @param yOffset
     */
    public static void showToast(Activity activity,
                                 String message,
                                 int time,
                                 int gravity,
                                 int xOffset,
                                 int yOffset) {

        try {
            Toast toast = Toast.makeText(activity, message, 0);
            toast.setGravity(gravity, xOffset, yOffset);
            toast.show();
        }catch (Exception e){

        }
    }
}
