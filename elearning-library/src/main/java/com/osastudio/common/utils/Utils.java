package com.osastudio.common.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Utils {
    public static final String ROOT = Environment.getExternalStorageDirectory()
            .toString();
    public static final String LQWAWA_KEY = "lqwawa";
    public static final String DATA_FOLDER = ROOT + "/" + LQWAWA_KEY + "/actorschool/";
    public final static String THUMB_FOLDER = DATA_FOLDER + "thumb/";
    private static final String APP_NAME = "elearning";




    private static boolean DEBUG = false;

    public static void enableLogging(boolean logging) {
        DEBUG = logging;
    }

    public static void log(String tag, String info) {
        if (DEBUG) {
            Log.i(APP_NAME, tag + "---->----" + info);
        }
    }

    public static PackageInfo getPackageInfo(Context context, String pkgName) {
        try {
            PackageManager pm = context.getPackageManager();
            return pm.getPackageInfo(pkgName, 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static PackageInfo getPackageInfo(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            return pm.getPackageInfo(context.getPackageName(), 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int getVersionCode(Context context) {
        return getPackageInfo(context).versionCode;
    }

    public static String getVersionName(Context context) {
        return getPackageInfo(context).versionName;
    }

    public static ApplicationInfo getApplicationInfo(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            return pm.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int getApplicationIcon(Context context) {
        ApplicationInfo applicationInfo = getApplicationInfo(context);
        return applicationInfo != null ? applicationInfo.icon : 0;
    }

    public static void openApp(Context context, String packageName)
            throws ActivityNotFoundException {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        context.startActivity(intent);
    }

    public static void installApp(Context context, String filePath) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        FileProviderHelper.setIntentDataAndType(context,
                intent, "application/vnd.android.package-archive", new File(filePath), true);
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }


    public static void uninstallApp(Context context, String packageName) {
        Intent intent = new Intent(Intent.ACTION_DELETE, Uri.parse("package:" + packageName));
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static String getApplicationStamp(Context context) {
        PackageInfo packageInfo = getPackageInfo(context);
        if (packageInfo == null) {
            return null;
        }
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return new StringBuilder(packageInfo.packageName)
                .append(",").append(packageInfo.versionName)
                .append(",").append(packageInfo.versionCode)
                .append(",").append("android")
                .append(",").append(Build.VERSION.RELEASE)
                .append(",").append(Build.VERSION.SDK_INT)
                .append(",").append(Build.MANUFACTURER)
                .append(",").append(Build.MODEL)
                .append(",").append(Build.SERIAL)
                .append(",").append(displayMetrics.widthPixels)
                .append("x").append(displayMetrics.heightPixels)
                .append(",").append(displayMetrics.densityDpi)
                .append(",").append(displayMetrics.density)
                .toString();
    }

    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    public static float getScreenDensity(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }

    public static float getScreenDpi(Context context) {
        return context.getResources().getDisplayMetrics().densityDpi;
    }

    public static List<String> getVolumeList(Context context) {
        StorageManager storageManager = (StorageManager) context
                .getSystemService(Context.STORAGE_SERVICE);
        if (storageManager == null) {
            return null;
        }

        List<String> pathList = null;
        StringBuilder builder = new StringBuilder();
        try {
            Method getVolumePaths = storageManager.getClass().getMethod(
                    "getVolumePaths");
            String[] paths = (String[]) getVolumePaths.invoke(storageManager);
            if (paths != null && paths.length > 0) {
                pathList = new ArrayList();
                for (String path : paths) {
                    pathList.add(path);
                    builder.append(path).append(" ");
                }
            }
        } catch (Exception e) {
            try {
                Method getVolumeList = storageManager.getClass().getMethod(
                        "getVolumeList");
                Object[] volumeList = (Object[]) getVolumeList.invoke(storageManager);
                if (volumeList != null && volumeList.length > 0) {
                    String path = null;
                    pathList = new ArrayList();
                    for (Object volume : volumeList) {
                        try {
                            Method getPath = volume.getClass().getMethod("getPath");
                            path = (String) getPath.invoke(volume);
                            pathList.add(path);
                            builder.append(path).append(" ");
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            continue;
                        }
                    }
                }
            } catch (Exception exc) {
                exc.printStackTrace();
            }
        }


        log("TEST", "getVolumeList: " + builder.toString());
        return pathList;
    }

    public static String formatFileSize(long fileSize) {
        return formatFileSize(fileSize, 2);
    }

    public static String formatFileSize(long fileSize, int decimalDigits) {
        if (fileSize <= 0) {
            return "0M";
        }
        StringBuilder builder = new StringBuilder("#");
        if (decimalDigits > 0) {
            builder.append(".");
            while (decimalDigits-- > 0) {
                builder.append("0");
            }
        }
        DecimalFormat df = new DecimalFormat(builder.toString());
        String fileSizeString = "";
        if (fileSize < 1024) {
            fileSizeString = df.format((double) fileSize) + "B";
        } else if (fileSize < 1048576) {
            fileSizeString = df.format((double) fileSize / 1024) + "KB";
        } else if (fileSize < 1073741824) {
            fileSizeString = df.format((double) fileSize / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileSize / 1073741824) + "GB";
        }
        return fileSizeString;
    }

    public static String removeFileNameSuffix(String filename) {
//        if (TextUtils.isEmpty(filename)) {
//            return null;
//        }
//        int index = filename.lastIndexOf('.');
//        String name = filename;
//        if(index > 0) {
//            name = filename.substring(0, index);
//        }
        return filename;
    }

    public static String patchPackageNameForUrl(Context context, String url) {
        if (context == null || TextUtils.isEmpty(url)) {
            return null;
        }
        StringBuilder builder = new StringBuilder(url);
        if(!url.contains("pkgname=")) {
            if (url.contains("?")) {
                builder.append("&pkgname=");
                builder.append(context.getPackageName());
            } else {
                builder.append("?pkgname=");
                builder.append(context.getPackageName());
            }
        }
        return builder.toString();
    }

    /**
     * 获取进程号对应的进程名
     *
     * @param pid 进程号
     * @return 进程名
     */
    public static String getProcessName(int pid) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
            String processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }
}
