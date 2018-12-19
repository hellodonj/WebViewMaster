package com.lqwawa.libs.appupdater;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.lqwawa.apps.R;
import com.osastudio.common.utils.FileProviderHelper;
import com.osastudio.common.utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public abstract class UpdateService extends Service {

    private static final String TAG = UpdateService.class.getSimpleName();

    private static final String ROOT_DIR_NAME = ".AppUpdater";
    private static final String PREFS_FILE_NAME = "app_updater";

    private static boolean DEBUG = true;

    private static File rootDir =
            new File(Environment.getExternalStorageDirectory(), ROOT_DIR_NAME);

    private static int notificationIcon = 0;

    private final IBinder binder = new UpdateBinder();
    private Map<String, UpdateListener> listeners = new HashMap<String, UpdateListener>();
    private UpdateListener listener;
    private AppInfo appInfo;
    private UpdateTask updateTask;

    public class UpdateBinder extends Binder {

        public UpdateService getService() {
            return UpdateService.this;
        }

    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (DEBUG)
            Utils.log(TAG, "____________onCreate");

        rootDir = new File(getExternalCacheDir(), ROOT_DIR_NAME);
        notificationIcon = Utils.getApplicationIcon(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        if (DEBUG)
            Utils.log(TAG, "____________onBind: "
                    + intent.getComponent().getClassName());
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if (DEBUG)
            Utils.log(TAG, "____________onUnbind: "
                    + intent.getComponent().getClassName());
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (DEBUG)
            Utils.log(TAG, "____________onDestroy");
    }

    public static void setDebugMode(boolean debug) {
        DEBUG = debug;
    }

    public static boolean isDebugMode() {
        return DEBUG;
    }

    public static void setRootDir(File dir) {
        if (dir != null) {
            if (!dir.exists() && !dir.mkdirs()) {
                return;
            }
            rootDir = dir;
        }
    }

    public static void setNotificationIcon(int resId) {
        notificationIcon = resId;
    }

    static String generateVersionId(AppInfo appInfo) {
        StringBuilder builder = new StringBuilder();
        builder.append(appInfo.getVersionCode());
        if (!TextUtils.isEmpty(appInfo.getVersionName())) {
            builder.append("-").append(appInfo.getVersionName());
        }
        return builder.toString();
    }

    static boolean isVersionIgnored(Context context, AppInfo appInfo) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_FILE_NAME,
                Context.MODE_PRIVATE);
        return prefs.getBoolean(generateVersionId(appInfo), false);
    }

    static void ignoreVersion(Context context, AppInfo appInfo, boolean ignore) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_FILE_NAME,
                Context.MODE_PRIVATE);
        prefs.edit().putBoolean(generateVersionId(appInfo), ignore).commit();
    }

    static boolean isNewVersion(Context context, AppInfo appInfo) {
        return appInfo.getVersionCode() > Utils.getVersionCode(context);
    }

    public void checkUpdate() {
        checkUpdate(false);
    }

    public void forceUpdate() {
        checkUpdate(true);
    }

    protected abstract void checkUpdate(boolean forceUpdate);

    public void checkUpdate(AppInfo appInfo, boolean forceUpdate) {
        if (!isNewVersion(this, appInfo)) {
            //设置---版本检测回调
            if (mINewVersion != null) {
                mINewVersion.isNewVersion(true,appInfo);
            }

            if (forceUpdate) {
                Toast.makeText(this, R.string.au_already_newest_version,
                        Toast.LENGTH_SHORT).show();
            }
            return;
        }
        //设置---版本检测回调
        if (mINewVersion != null) {
            mINewVersion.isNewVersion(false,appInfo);
            return;
        }

        if (isVersionIgnored(this, appInfo) && !forceUpdate) {
            return;
        }
        if (updateTask != null) {
            return;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        showUpdateDialog(appInfo, forceUpdate);
    }

    private void showUpdateDialog(AppInfo appInfo, boolean forceUpdate) {
        Bundle args = new Bundle();
        args.putParcelable(UpdateActivity.EXTRA_APP_INFO, appInfo);
        args.putBoolean(UpdateActivity.EXTRA_FORCE_UPDATE, forceUpdate);
        Intent intent = new Intent(this, UpdateActivity.class);
        intent.putExtras(args);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    void addUpdateListener(Class clazz, UpdateListener listener) {
        listeners.put(clazz.getSimpleName(), listener);
    }

    void removeUpdateListener(Class clazz) {
        listeners.remove(clazz.getSimpleName());
    }

    void download(AppInfo appInfo) {
        if (updateTask == null) {
            updateTask = new UpdateTask(appInfo);
            if (listener == null) {
                listener = new MyUpdateListener();
                addUpdateListener(UpdateService.class, listener);
            }
            new Thread(updateTask).start();
        }
    }

    private RemoteViews remoteViews;
    private Notification notification;

    private void notifyUser(AppInfo appInfo) {
        Intent intent = null;
        if (remoteViews == null) {
            remoteViews = new RemoteViews(getPackageName(),
                    R.layout.au_notification);
        }
        if (appInfo.isDownloadWaiting() || appInfo.isDownloadStarted() || appInfo.isDownloading()) {
            int progress = (int) ((float) appInfo.getDownloadedSize()
                            / (float) appInfo.getFileSize() * 100f);
            remoteViews.setTextViewText(R.id.au_notify_tips, getString(R.string.au_downloading));
            remoteViews.setViewVisibility(R.id.au_notify_progress, View.VISIBLE);
            remoteViews.setProgressBar(R.id.au_notify_progress, 100, progress, false);
        } else if (appInfo.isDownloaded()) {
            remoteViews.setTextViewText(R.id.au_notify_tips,
                    getString(R.string.au_new_version_downloaded));
            remoteViews.setViewVisibility(R.id.au_notify_progress, View.GONE);

            intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            FileProviderHelper.setIntentDataAndType(getApplicationContext(),
                    intent, "application/vnd.android.package-archive", new File(appInfo.getFilePath()), true);

        } else if (appInfo.isDownloadFailed()) {
            remoteViews.setTextViewText(R.id.au_notify_tips,
                    getString(R.string.au_download_error));
            remoteViews.setViewVisibility(R.id.au_notify_progress, View.GONE);
        }

        if (notification == null) {
            notification = new Notification();
        }
        notification.icon = notificationIcon;
        if (notification.icon <= 0) {
            if (appInfo.isDownloadWaiting() || appInfo.isDownloadStarted()
                    || appInfo.isDownloading()) {
                notification.icon = android.R.drawable.stat_sys_download;
            } else {
                notification.icon = android.R.drawable.stat_sys_download_done;
            }
        }
        remoteViews.setImageViewResource(R.id.au_notify_icon, notification.icon);
        notification.contentView = remoteViews;
        notification.contentIntent = PendingIntent.getActivity(this, 0,
                intent != null ? intent : new Intent(),
                PendingIntent.FLAG_CANCEL_CURRENT);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
//        notification.when = System.currentTimeMillis();

        NotificationManager nm = (NotificationManager) getSystemService(
                Context.NOTIFICATION_SERVICE);
        nm.notify(appInfo.getId().hashCode(), notification);
    }

    private void notifyDownloadStateChanged(AppInfo appInfo) {
        Message message = handler.obtainMessage();
        message.obj = appInfo;
        handler.sendMessage(message);
    }

    private UpdateHandler handler = new UpdateHandler();

    @SuppressLint("HandlerLeak")
    private class UpdateHandler extends Handler {
        private UpdateHandler() {
            super(Looper.getMainLooper());
        }

        @Override
        public void handleMessage(Message msg) {
            if (listeners.size() <= 0) {
                return;
            }
            AppInfo appInfo = (AppInfo) msg.obj;
            UpdateListener listener = null;
            for (Map.Entry<String, UpdateListener> entry : listeners.entrySet()) {
                listener = entry.getValue();
                switch (appInfo.getDownloadState()) {
                case AppInfo.STATE_STARTED:
                    listener.onStart(appInfo);
                    break;
                case AppInfo.STATE_WAITING:
                    listener.onPrepare(appInfo);
                    break;
                case AppInfo.STATE_DOWNLOADING:
                    listener.onProgress(appInfo);
                    break;
                case AppInfo.STATE_DOWNLOADED:
                    listener.onFinish(appInfo);
                    break;
                case AppInfo.STATE_ERROR:
                    listener.onError(appInfo);
                    break;
                }
            }
        }
    }

    private class UpdateTask implements Runnable {
        public AppInfo appInfo;

        public UpdateTask(AppInfo appInfo) {
            this.appInfo = appInfo;
            this.appInfo.setRootDir(rootDir);
        }

        @Override
        public void run() {
            appInfo.setDownloadState(AppInfo.STATE_STARTED);
            notifyDownloadStateChanged(appInfo);

            long downloadedSize = 0;
            File file = new File(appInfo.getFilePath());
            if (file.exists()) {
                file.delete();
            }
            appInfo.setDownloadedSize(0);
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                URL url = new URL(appInfo.getFileUrl());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(60 * 1000);
                connection.setReadTimeout(60 * 1000);
                connection.setRequestMethod("GET");

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    @SuppressWarnings("resource")
                    OutputStream outputStream = new FileOutputStream(file, true);
                    InputStream is = connection.getInputStream();
                    byte[] buffer = new byte[102400];
                    int length = -1;
                    appInfo.setDownloadState(AppInfo.STATE_DOWNLOADING);
                    notifyDownloadStateChanged(appInfo);

                    long lastMillis = System.currentTimeMillis();
                    while ((length = is.read(buffer)) != -1) {
                        UpdateService.this.appInfo = appInfo;
                        outputStream.write(buffer, 0, length);
                        downloadedSize += length;
                        appInfo.setDownloadedSize(downloadedSize);
                        long currMillis = System.currentTimeMillis();
                        if (currMillis - lastMillis > 250) {
                            lastMillis = currMillis;
                            notifyDownloadStateChanged(appInfo);
                        }
                    }
                    if (appInfo.getFileSize() == appInfo.getDownloadedSize()) {
                        appInfo.setDownloadState(AppInfo.STATE_DOWNLOADED);
                        notifyDownloadStateChanged(appInfo);
                    } else {
                        appInfo.setDownloadState(AppInfo.STATE_ERROR);
                        appInfo.setDownloadedSize(downloadedSize);
                        notifyDownloadStateChanged(appInfo);
                    }
                }
            } catch (IOException e) {
                appInfo.setDownloadState(AppInfo.STATE_ERROR);
                notifyDownloadStateChanged(appInfo);
            }
            UpdateService.this.updateTask = null;
            UpdateService.this.appInfo = null;
        }
    }

    private class MyUpdateListener implements UpdateListener {

        @Override
        public void onPrepare(AppInfo appInfo) {
            if (!appInfo.isForcedUpdate()) {
                notifyUser(appInfo);
            }
        }

        @Override
        public void onStart(AppInfo appInfo) {
            if (!appInfo.isForcedUpdate()) {
                notifyUser(appInfo);
            }
        }

        @Override
        public void onProgress(AppInfo appInfo) {
            if (!appInfo.isForcedUpdate()) {
                notifyUser(appInfo);
            }
        }

        @Override
        public void onFinish(AppInfo appInfo) {
            notifyUser(appInfo);
            Utils.installApp(UpdateService.this, appInfo.getFilePath());
        }

        @Override
        public void onError(AppInfo appInfo) {
            if (!appInfo.isForcedUpdate()) {
                notifyUser(appInfo);
            }
        }
    }

    public interface INewVersion {
        /**
         * 回调最新版本信息
         *
         * @param isNewVersion 是否为最新版
         * @param appInfo
         */
        void isNewVersion(boolean isNewVersion, AppInfo appInfo);
    }

    private INewVersion mINewVersion;

    public void setINewVersion(INewVersion newVersion) {
        this.mINewVersion = newVersion;
    }

}
