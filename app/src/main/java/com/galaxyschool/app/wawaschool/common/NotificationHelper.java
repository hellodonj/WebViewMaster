package com.galaxyschool.app.wawaschool.common;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.widget.RemoteViews;

import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.ShareScreenActivity;
import com.oosic.apps.iemaker.base.ooshare.ConnectedDevice;

/**
 * Created by E450 on 2016/12/8.
 */

public class NotificationHelper {
    private Notification notification;
    private PrefsManager prefsManager;

    public NotificationHelper(PrefsManager prefsManager, Notification notification){
        this.prefsManager=prefsManager;
        this.notification=notification;
    }

    public void interceptNotification(){
        if(prefsManager!=null){
            if( prefsManager.getNoticeAvoidDisturbVoiceOpenState()){
                notification.defaults |= Notification.DEFAULT_SOUND;
            }else{
                notification.sound = null;
            }
            if( prefsManager.getNoticeAvoidDisturbShakeOpenState()){
                notification.defaults |= Notification.DEFAULT_VIBRATE;
            }else{
                notification.vibrate = null;
            }
        }
    }

    public static void showShareScreenNotification(Activity activity, ConnectedDevice data, int
            notificationId) {
        if(data == null || activity == null) {
            return;
        }
        String deviceName = getShareScreenDeviceName(data);
        NotificationManager nm = (NotificationManager)activity.getSystemService(
                Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(activity, ShareScreenActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(activity,
                notificationId, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        Notification notification = new Notification(R.drawable.ic_launcher, "",
                System.currentTimeMillis());

        RemoteViews contentView = new RemoteViews(activity.getPackageName(), R.layout
                .sharescreen_notification);
        contentView.setImageViewResource(R.id.icon, R.drawable.ic_launcher);
        contentView.setTextViewText(R.id.title, activity.getString(R.string.sharescreen));

        if(!TextUtils.isEmpty(deviceName)) {
            String deviceStr = activity.getString(R.string.n_current_device, deviceName);
            SpannableString spannableString = new SpannableString(deviceStr);
            int greenColor = activity.getResources().getColor(R.color.text_green);
            ForegroundColorSpan span = new ForegroundColorSpan(greenColor);
            int startIndex = deviceStr.indexOf(deviceName);
            int endIndex = deviceStr.length();
            spannableString.setSpan(span, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            contentView.setTextViewText(R.id.sub_title, spannableString);
        }
        notification.contentView = contentView;

        notification.contentIntent = pendingIntent;
        MyApplication myApp = null;
        PrefsManager prefsManager = null;
        myApp = (MyApplication)activity.getApplication();
        if(myApp!=null){
            prefsManager = myApp.getPrefsManager();
        }
        if(prefsManager != null && notification != null){
            NotificationHelper notificationHelper = new NotificationHelper(prefsManager,notification);
            notificationHelper.interceptNotification();
        }
        //  notification.defaults = Notification.DEFAULT_ALL;
        notification.flags |= Notification.FLAG_SHOW_LIGHTS;
//        n.flags |= Notification.FLAG_AUTO_CANCEL;
//		n.flags |= Notification.FLAG_ONGOING_EVENT;
        notification.flags |= Notification.FLAG_NO_CLEAR;
        notification.flags |= Notification.FLAG_ONGOING_EVENT;
//		n.iconLevel = 5;
        notification.when = System.currentTimeMillis();
        nm.notify(notificationId, notification);
    }

    public static void removeShareScreenNotification(Activity activity, int notificationId) {
        if(activity == null || notificationId <= 0) {
            return;
        }

        NotificationManager mNotificationManager =
                (NotificationManager)activity.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(notificationId);
    }

    private static String getShareScreenDeviceName(ConnectedDevice data) {
        if (data == null || data.device == null) {
            return null;
        }
        String deviceName = data.device.getName();
        int index = deviceName.lastIndexOf("_");
        if (index <= 0) {
            index = deviceName.length();
        }
        deviceName = deviceName.substring(0, index);
        return deviceName;
    }



}
