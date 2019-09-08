package com.galaxyschool.app.wawaschool.common;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.text.TextUtils;
import android.widget.RemoteViews;

import com.galaxyschool.app.wawaschool.R;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static android.app.Notification.PRIORITY_DEFAULT;
import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;
import static android.content.Context.NOTIFICATION_SERVICE;

public class LinNotifyHelper {

    public static final int RequestCode = 1;
    public static final String VIBRATION_MESSAGE = "vibration_message";
    public static final String MESSAGE = "message";
    public static final String Ticker = "您有一条新的消息";
    public static final String CHECK_OP_NO_THROW = "checkOpNoThrow";
    public static final String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";
    public static int notifyId = 0;

    public static void setNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = VIBRATION_MESSAGE;
            String channelName = "震动消息通知";
            createNotificationChannel(context, channelId, channelName,
                    NotificationManager.IMPORTANCE_HIGH,true);
            channelId = MESSAGE;
            channelName = "消息通知";
            createNotificationChannel(context, channelId, channelName,
                    NotificationManager.IMPORTANCE_LOW,false);
        }
    }

    /**
     * 创建配置通知渠道
     * @param channelId   渠道id
     * @param channelName 渠道nanme
     * @param importance  优先级
     */
    @TargetApi(Build.VERSION_CODES.O)
    private static void createNotificationChannel(Context context,
                                                  String channelId,
                                                  String channelName,
                                                  int importance,
                                                  boolean isVibration) {
        NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
        channel.setShowBadge(false);//禁止该渠道使用角标
        channel.enableLights(true);
        channel.setImportance(importance);
        channel.setSound(null,null);
        channel.setLockscreenVisibility(Notification.VISIBILITY_SECRET);
        channel.setLightColor(Color.RED);
        channel.setBypassDnd(false);//设置是否可以绕过请勿打扰模式
        if (isVibration) {
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
        } else {
            channel.enableVibration(false);
            channel.setVibrationPattern(new long[]{0});
        }
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(
                NOTIFICATION_SERVICE);
        if (notificationManager != null) {
//            notificationManager.deleteNotificationChannel(channelId);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * 发送通知（刷新前面的通知，指定通知渠道）
     *
     * @param contentTitle 标题
     * @param contentText  内容
     * @param channelId    渠道id
     */
    public static void show(Context context, String contentTitle, String contentText, String channelId, Class<?> cls) {
        show(context, contentTitle, contentText, null, 0, channelId, cls);
    }

    /**
     * 发送自定义通知（刷新前面的通知，指定通知渠道）
     *
     * @param contentTitle 标题
     * @param contentText  内容
     * @param channelId    渠道id
     */
    public static void show(Context context, String contentTitle, String contentText, RemoteViews views, String channelId, Class<?> cls) {
        show(context, contentTitle, contentText, views, 0, channelId, cls);
    }

    /**
     * 发送多条通知（指定通知渠道）
     *
     * @param contentTitle 标题
     * @param contentText  内容
     * @param channelId    渠道id
     */
    public static void showMuch(Context context, String contentTitle, String contentText, String channelId, Class<?> cls) {
        show(context, contentTitle, contentText, null, ++notifyId, channelId, cls);
    }

    /**
     * 发送多条自定义通知（指定通知渠道）
     *
     * @param contentTitle 标题
     * @param contentText  内容
     * @param channelId    渠道id
     */
    public static void showMuch(Context context, String contentTitle, String contentText, String channelId, RemoteViews views, Class<?> cls) {
        show(context, contentTitle, contentText, views, ++notifyId, channelId, cls);
    }

    /**
     * 发送通知（设置默认：大图标/小图标/小标题/副标题/优先级/首次弹出文本）
     *
     * @param contentTitle 标题
     * @param contentText  内容
     * @param notifyId     通知栏id
     * @param channelId    设置渠道id
     * @param cls          意图类
     */
    public static void show(Context context, String contentTitle, String contentText, RemoteViews views, int notifyId, String channelId, Class<?> cls) {
        show(context, 0, 0, contentTitle, null, contentText, PRIORITY_DEFAULT, null, views, notifyId, channelId, cls);
    }

    /**
     * 发送通知
     *
     * @param largeIcon    大图标
     * @param smallIcon    小图标
     * @param contentTitle 标题
     * @param subText      小标题/副标题
     * @param contentText  内容
     * @param priority     优先级
     * @param ticker       通知首次弹出时，状态栏上显示的文本
     * @param notifyId     定义是否显示多条通知栏
     * @param cls          意图类
     */
    @SuppressLint("WrongConstant")
    public static void show(Context context,
                            int largeIcon,
                            int smallIcon,
                            String contentTitle,
                            String subText,
                            String contentText,
                            int priority,
                            String ticker,
                            RemoteViews view,
                            int notifyId,
                            String channelId,
                            Class<?> cls) {
        //flags
        // FLAG_ONE_SHOT:表示此PendingIntent只能使用一次的标志
        // FLAG_IMMUTABLE:指示创建的PendingIntent应该是不可变的标志
        // FLAG_NO_CREATE : 指示如果描述的PendingIntent尚不存在，则只返回null而不是创建它。
        // FLAG_CANCEL_CURRENT :指示如果所描述的PendingIntent已存在，则应在生成新的PendingIntent,取消之前PendingIntent
        // FLAG_UPDATE_CURRENT : 指示如果所描述的PendingIntent已存在，则保留它，但将其额外数据替换为此新Intent中的内容
        PendingIntent pendingIntent = null;
        //添加隐示意图
        if (cls != null) {
            Intent intent = new Intent(context, cls);
            pendingIntent = PendingIntent.getActivity(context, RequestCode, intent, FLAG_UPDATE_CURRENT);
        }
        //获取通知服务管理器
        NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        //判断应用通知是否打开
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (!openNotificationChannel(context, manager, channelId)) return;
        }

        //创建 NEW_MESSAGE 渠道通知栏  在API级别26.1.0中推荐使用此构造函数 Builder(context, 渠道名)
        NotificationCompat.Builder builder = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            builder = new NotificationCompat.Builder(context,channelId);
        } else {
            builder = new NotificationCompat.Builder(context);
        }
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), largeIcon == 0
                ? R.drawable.ic_launcher : largeIcon)) //设置自动收报机和通知中显示的大图标。
                .setSmallIcon(smallIcon == 0 ? R.drawable.ic_launcher : smallIcon) // 小图标
                .setContentText(TextUtils.isEmpty(contentText) ? null : contentText) // 内容
                .setContentTitle(TextUtils.isEmpty(contentTitle) ? null : contentTitle) // 标题
                .setSubText(TextUtils.isEmpty(subText) ? null : subText) // APP名称的副标题
                .setPriority(priority) //设置优先级 PRIORITY_DEFAULT
                .setTicker(TextUtils.isEmpty(ticker) ? Ticker : ticker) // 设置通知首次弹出时，状态栏上显示的文本
                .setCustomContentView(view)
                .setWhen(System.currentTimeMillis()) // 设置通知发送的时间戳
                .setShowWhen(true)//设置是否显示时间戳
                .setAutoCancel(true)// 点击通知后通知在通知栏上消失
                .setDefaults(Notification.PRIORITY_HIGH)// 设置默认的提示音、振动方式、灯光等 使用的默认通知选项
                .setContentIntent(pendingIntent) // 设置通知的点击事件
                //锁屏状态下显示通知图标及标题 1、VISIBILITY_PUBLIC 在所有锁定屏幕上完整显示此通知/2、VISIBILITY_PRIVATE 隐藏安全锁屏上的敏感或私人信息/3、VISIBILITY_SECRET 不显示任何部分
                .setVisibility(NotificationCompat.DEFAULT_ALL)//部分手机没效果
//                .setColorized(true)
//                .setGroupSummary(true)//将此通知设置为一组通知的组摘要
//                .setGroup(NEW_GROUP)//使用组密钥
//                .setDeleteIntent(pendingIntent)//当用户直接从通知面板清除通知时 发送意图
//                .setFullScreenIntent(pendingIntent,true)
//                .setContentInfo("大文本")//在通知的右侧设置大文本。
//                .setContent(RemoteViews RemoteView)//设置自定义通知栏
//                .setColor(Color.parseColor("#ff0000"))
//                .setLights()//希望设备上的LED闪烁的argb值以及速率
//                .setTimeoutAfter(3000)//指定取消此通知的时间（如果尚未取消）。
        ;

        // 通知栏id
        if (manager != null) {
            manager.notify(notifyId, builder.build()); // build()方法需要的最低API为16 ,
        }
    }

    /**
     * 判断应用渠道通知是否打开（适配8.0）
     *
     * @return true 打开
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static Boolean openNotificationChannel(Context context, NotificationManager manager, String channelId) {
        //判断通知是否有打开
        if (!isNotificationEnabled(context)) {
            toNotifySetting(context, null);
            return false;
        }
        //判断渠道通知是否打开
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = manager.getNotificationChannel(channelId);
            if (channel.getImportance() == NotificationManager.IMPORTANCE_NONE) {
                //没打开调往设置界面
                toNotifySetting(context, channel.getId());
                return false;
            }
        }

        return true;
    }

    /**
     * 判断应用通知是否打开
     *
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static boolean isNotificationEnabled(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
            return notificationManagerCompat.areNotificationsEnabled();
        }
        AppOpsManager mAppOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        Class appOpsClass = null;
        /* Context.APP_OPS_MANAGER */
        try {
            appOpsClass = Class.forName(AppOpsManager.class.getName());
            Method checkOpNoThrowMethod = appOpsClass.getMethod(CHECK_OP_NO_THROW, Integer.TYPE, Integer.TYPE,
                    String.class);
            Field opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION);
            int value = (Integer) opPostNotificationValue.get(Integer.class);
            return ((Integer) checkOpNoThrowMethod.invoke(mAppOps, value, context.getApplicationInfo().uid, context.getPackageName()) == AppOpsManager.MODE_ALLOWED);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 手动打开应用通知
     */
    public static void toNotifySetting(Context context, String channelId) {
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { //适配 8.0及8.0以上(8.0需要先打开应用通知，再打开渠道通知)
            if (TextUtils.isEmpty(channelId)) {
                intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
            } else {
                intent.setAction(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
                intent.putExtra(Settings.EXTRA_CHANNEL_ID, channelId);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//适配 5.0及5.0以上
            intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra("app_package", context.getPackageName());
            intent.putExtra("app_uid", context.getApplicationInfo().uid);
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {// 适配 4.4及4.4以上
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setData(Uri.fromParts("package", context.getPackageName(), null));
        } else {
            intent.setAction(Settings.ACTION_SETTINGS);
        }
        context.startActivity(intent);
    }
}
