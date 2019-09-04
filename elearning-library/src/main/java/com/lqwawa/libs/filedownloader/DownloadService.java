package com.lqwawa.libs.filedownloader;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.lqwawa.apps.R;
import com.osastudio.common.utils.Utils;

import java.io.File;
import java.util.List;

public class DownloadService extends Service {

    private static final String TAG = DownloadService.class.getSimpleName();

    public static final boolean DEBUG = false;

    public static final String ROOT_DIR_NAME = DownloadManager.ROOT_DIR_NAME;

    private static int notificationIcon = 0;

    private final IBinder binder = new DownloadBinder();
    private DownloadManager downloadManager;

    public class DownloadBinder extends Binder {

        public DownloadService getService() {
            return DownloadService.this;
        }

    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (DEBUG)
            Utils.log(TAG, "____________onCreate");

        notificationIcon = Utils.getApplicationIcon(this);

        initDownloadManager();
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

        destroyDownloadManager();
    }

    public static void setRootDir(File dir) {
        DownloadManager.setRootDir(dir);
    }

    public static void setNotificationIcon(int resId) {
        notificationIcon = resId;
    }

    private void initDownloadManager() {
        if (downloadManager == null) {
            downloadManager = new DownloadManager(this);
        }
    }

    private void destroyDownloadManager() {
        if (downloadManager != null) {
            downloadManager.destroy();
            downloadManager = null;
        }
    }

    public void addDownloadListener(Class clazz, String id, DownloadListener listener) {
        addDownloadListener(clazz, id, listener, true);
    }

    public void addDownloadListener(Class clazz, String id, DownloadListener listener,
                                    boolean addDefaultListener) {
        downloadManager.addListener(clazz, id, listener);
        if (addDefaultListener) {
            downloadManager.addListener(this.getClass(), id, new MyDownloadListener());
        }
    }

    public void removeDownloadListener(Class clazz, String id) {
        downloadManager.removeListener(clazz, id);
    }

    public void removeDownloadListeners(Class clazz) {
        downloadManager.removeListeners(clazz);
    }


    public List<FileInfo> getFileInfoList(String userId) {
        return downloadManager.getFileInfoList(userId);
    }

    public List<FileInfo> getFileInfoList(String userId, boolean fixCorruption) {
        return downloadManager.getFileInfoList(userId, fixCorruption);
    }

    public List<FileInfo> getFileInfoList(String userId, String fileName) {
        return downloadManager.getFileInfoList(userId, fileName);
    }

    public List<FileInfo> getFileInfoList(String userId, String fileName, boolean fixCorruption) {
        return downloadManager.getFileInfoList(userId, fileName, fixCorruption);
    }

    public FileInfo getFileInfo(String userId, String fileId) {
        return downloadManager.getFileInfo(userId, fileId);
    }

    public FileInfo getFileInfo(String userId, String fileId, boolean fixCorruption) {
        return downloadManager.getFileInfo(userId, fileId, fixCorruption);
    }

    public void downloadFile(FileInfo fileInfo) {
        downloadManager.downloadFile(fileInfo);
    }

    public void deleteFile(FileInfo fileInfo) {
        downloadManager.deleteFile(fileInfo);
    }

    private void notifyFileDownloaded(FileInfo fileInfo) {
//      Notification n = new Notification();
//      n.icon = notificationIcon;
//      if (n.icon <= 0) {
//         n.icon = android.R.drawable.stat_sys_download_done;
//      }
//      PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(),
//              PendingIntent.FLAG_CANCEL_CURRENT);
//      n.setLatestEventInfo(this, fileInfo.getFileName(), getString(R.string.fd_downloaded), pi);
//      n.defaults = Notification.DEFAULT_LIGHTS;
//      n.flags |= Notification.FLAG_SHOW_LIGHTS;
//      n.flags |= Notification.FLAG_AUTO_CANCEL;
//      n.when = System.currentTimeMillis();
//
//      NotificationManager nm = (NotificationManager) getSystemService(
//              Context.NOTIFICATION_SERVICE);
//      nm.notify(fileInfo.getId().hashCode(), n);

        Notification n = null;
        int icon = notificationIcon;
        if (icon <= 0) {
            icon = android.R.drawable.stat_sys_download_done;
        }
        PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(),
                PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationCompat.Builder builder = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            builder = new NotificationCompat.Builder(getApplicationContext(), "message");
        } else {
            builder = new NotificationCompat.Builder(getApplicationContext());
        }
        builder.setContentTitle(fileInfo.getFileName())
                .setContentText(getString(R.string.fd_downloaded))
                .setContentIntent(pi)
                .setSmallIcon(icon)
                .setWhen(System.currentTimeMillis());
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            n = builder.getNotification();
        } else {
            n = builder.build();
        }
        n.defaults = Notification.DEFAULT_LIGHTS;
        n.flags |= Notification.FLAG_AUTO_CANCEL;
        n.when = System.currentTimeMillis();
//      n.flags |= Notification.FLAG_NO_CANCEL;
//      n.flags |= Notification.FLAG_ONGOING_EVENT;
        NotificationManager nm = (NotificationManager) getSystemService(
                Context.NOTIFICATION_SERVICE);
        nm.cancel(Notification.FLAG_AUTO_CANCEL);
        nm.notify(fileInfo.getId().hashCode(), n);
    }

    private class MyDownloadListener implements DownloadListener {
        @Override
        public void onPrepare(FileInfo fileInfo) {

        }

        @Override
        public void onStart(FileInfo fileInfo) {

        }

        @Override
        public void onProgress(FileInfo fileInfo) {

        }

        @Override
        public void onPause(FileInfo fileInfo) {

        }

        @Override
        public void onFinish(FileInfo fileInfo) {
            notifyFileDownloaded(fileInfo);
        }

        @Override
        public void onError(FileInfo fileInfo) {

        }

        @Override
        public void onDelete(FileInfo fileInfo) {

        }
    }

}
