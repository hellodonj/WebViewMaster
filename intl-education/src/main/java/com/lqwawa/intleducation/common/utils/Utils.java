package com.lqwawa.intleducation.common.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.os.Environment;
import android.text.TextUtils;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.utils.LogUtil;
import com.osastudio.common.utils.TipMsgHelper;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by XChen on 2016/11/30.
 * email:man0fchina@foxmail.com
 */

public class Utils {
    public static final String ROOT = Environment.getExternalStorageDirectory()
            .toString();

    public static final String LQWAWA_KEY = "lqwawa";
    public static final String DATA_FOLDER = ROOT + "/" + LQWAWA_KEY + "/intleducation/";
    public static final String ICON_FOLDER = DATA_FOLDER + "icon/";
    public static final String IMAGE_FOLDER = DATA_FOLDER + "image/";
    public static final String ICON_NAME = "icon.jpg";
    public static final String PNG_ICON_NAME = "icon.png";
    public static final String ZOOM_ICON_NAME = "zoom_icon.jpg";
    public static final String ZOOM_PNG_ICON_NAME = "zoom_icon.png";
    public static final String TEMP_IMAGE_NAME = "temp_image.jpg";
    public static final String TEMP_PNG_IMAGE_NAME = "temp_image.png";
    public static final String NO_MEDIA_FILE = ".nomedia";
    public static final String COURSE_SUFFIX = ".zip";
    public static final String TEMP_FOLDER = DATA_FOLDER + "temp/";
    public static final String CACHE_FOLDER = DATA_FOLDER + "cache/";
    public static final String LOG_FOLDER = DATA_FOLDER + "log/";
    public static final String PIC_TEMP_FOLDER = TEMP_FOLDER + "pic/";
    public static final String DOWNLOAD_TEMP_FOLDER = TEMP_FOLDER + "download/";

    public static final int USER_ICON_SIZE = 512;
    //add for local course
    public static final String IMPORTED_FOLDER = DATA_FOLDER + "Imported/";
    public static final String RECORD_FOLDER = DATA_FOLDER + "Recorded/";
    public static final String ONLINE_FOLDER = DATA_FOLDER + "online/";
    public static final String RECORD_AUDIO = "/Audio/";
    public static final String RECORD_PDF = "/Pdf/";
    public static final String RECORD_VIDEO = "/Video/";
    public static final String FILE = "File/";
    public static final String FOLDER = "Folder/";
    public static final String PDF_PAGE_NAME = "pdf_page_";
    public static final String PDF_THUMB_NAME = "thumbnail.jpg";
    public static final String RECORD_HEAD_IMAGE_NAME = "head.jpg";
    public static final String RECORD_XML_NAME = "course_index.xml";

    public final static int IMPORT_PAGE_SIZE_WIDTH = 1280;
    public final static int IMPORT_PAGE_SIZE_HEIGHT = 800;
    public static final int DEFAULT_THUMB_HEIGHT = 240;

    public final static int MINIMUM_SPACE = 50; // 50M

    public static String getCacheDir() {
        try {
            File dir = new File(CACHE_FOLDER);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            return dir.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * create the folder with selected path.
     *
     * @param path
     */
    public static void createLocalDiskPath(String path) {
        File folder = new File(path);
        try {
            if (!folder.exists()) {

                boolean rtn = folder.mkdirs();
                LogUtil.i("createLocalDiskPath", "rtn=" + rtn);
            }
        } catch (Exception e) {
            LogUtil.i("createLocalDiskPath", "Exception");
        }
    }

    public static boolean safeDeleteDirectory(String dir) {
        File dirFile = new File(dir);
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }

        String path = dir.endsWith("/") ? dir.substring(0, dir.length() - 1)
                : dir;
        File file = new File(path);
        File to = new File(path + System.currentTimeMillis());
        if (file.renameTo(to)) {
            return deleteDirectory(to.getAbsolutePath());
        }
        return false;
    }

    private static boolean deleteDirectory(String dir) {
        if (!dir.endsWith(File.separator)) {
            dir = dir + File.separator;
        }
        File dirFile = new File(dir);
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        boolean flag = true;
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag) {
                    break;
                }
            } else {
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag) {
                    break;
                }
            }
        }

        if (!flag) {
            System.out.println("delete dir fail");
            return false;
        }

        if (dirFile.delete()) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean deleteFile(String filePath) {
        if (filePath == null) {
            return false;
        }
        File file = new File(filePath);
        if (file.isFile() && file.exists()) {
            file.delete();
            return true;
        } else {
            return false;
        }
    }

    public static String getFileTitle(String srcPath, String destFolder,
                                      String suffix) {
        int firstIndex = srcPath.lastIndexOf('/');
        int lastIndex = srcPath.lastIndexOf('.');
        if (lastIndex <= firstIndex)
            lastIndex = srcPath.length();
        String temp = "New File";
        try {
            temp = srcPath.subSequence(firstIndex + 1, lastIndex).toString();
        } catch (Exception e) {
            temp = "New File";
            e.printStackTrace();
        }
        String name = temp;
        int i = 1;

        if (suffix == null) {
            while (new File(destFolder + name).exists()) {
                name = temp + "-" + i;
                i++;
            }
        } else {
            if (!suffix.startsWith(".")) {
                suffix = "." + suffix;
            }
            while (new File(destFolder + name + suffix).exists()) {
                name = temp + "-" + i;
                i++;
            }
            name = name + suffix;
        }

        //return zip file with suffix
        return name;
    }

    public  static boolean checkEditTextValid(Activity activity, String str) {
        String regEx = "[~¥#&*<>《》()\\[\\]{}【】^@/￡¤¥|§¨「」『』￠￢￣~@#¥&*（）——+|《》$_€：:?？\\\\/“”；;.]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        if( m.find()){
            TipMsgHelper.ShowMsg(activity, R.string.invalid_characters);
            return false;
        }
        return true;
    }

    public static String getFileNameFromPath(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        String tempPath = path;
        if (path.endsWith(File.separator)) {
            tempPath = path.substring(0, path.length() - 1);
        }
        int firstIndex = tempPath.lastIndexOf('/');
        String name = null;
        try {
            name = tempPath.subSequence(firstIndex + 1, tempPath.length())
                    .toString();
        } catch (Exception e) {
            name = null;
            e.printStackTrace();
        }
        return name;
    }


    public synchronized static Bitmap loadBitmap(String pathName, int largeSide,
                                                 int shortSide, boolean bAccurateSize) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inScaled = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inDither = false;
        float width, height;
        Bitmap bmp = null;

        if (pathName == null || pathName.equals(""))
            return null;

        File file = new File(pathName);
        if (!file.exists())
            return null;

        options.inJustDecodeBounds = true;
        try {
            BitmapFactory.decodeFile(pathName, options);
        } catch (OutOfMemoryError e) {
            return null;

        }
        width = options.outWidth;
        height = options.outHeight;

        if (width == 0 || height == 0)
            return null;

        options.inDither = true;
        options.inJustDecodeBounds = false;

        float outputW = width;
        float outputH = height;
        float zoomFit = 1.0f;
        double zoomGal = -1.0f;

        if (width > height) {
            outputW = largeSide;
            outputH = (int) (height * outputW / width);
        } else {
            outputW = shortSide;
            outputH = (int) (height * outputW / width);
        }
        zoomFit = (float) Math.min(outputH / height, outputW / width);
        if (zoomGal < 0) {
            zoomGal = zoomFit;
        }

        int ratio1 = 0;
        ratio1 = (int) (width / outputW);
        if (ratio1 < 1) {
            ratio1 = 1;
        }
        options.inSampleSize = ratio1;
        try {
            bmp = BitmapFactory.decodeFile(pathName, options);
        } catch (OutOfMemoryError e) {
            return null;
        }
        if (bmp == null) {
            return null;
        }

        ExifInterface exif = null;
        int orientation = 0;
        try {
            exif = new ExifInterface(pathName);
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        if (exif != null) {
            orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    orientation = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    orientation = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    orientation = 270;
                    break;
                default:
                    orientation = 0;
            }
        }

        if (orientation != 0) {
            Matrix matrix = new Matrix();
            matrix.setRotate(orientation);

            Bitmap bmp2 = null;
            try {
                bmp2 = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(),
                        bmp.getHeight(), matrix, false);
            } catch (OutOfMemoryError e) {
                // Log.e("ZiiPhoto","can not create bitmap, out of memory!");
            }
            if (bmp2 != null) {
                bmp.recycle();
                bmp = bmp2;
            }
        }

        if (bAccurateSize) {
            int bh = bmp.getHeight();
            if (outputH > 0 && bh > outputH) {
                outputW = outputH * bmp.getWidth() / bmp.getHeight();
                Bitmap sbmp = null;
                try {
                    sbmp = Bitmap.createBitmap((int) outputW, (int) outputH,
                            Bitmap.Config.ARGB_8888);
                } catch (OutOfMemoryError e) {
                }
                if (sbmp != null) {
                    Canvas canvas = new Canvas(sbmp);
                    RectF dst = new RectF(0, 0, outputW, outputH);
                    canvas.drawBitmap(bmp, null, dst, null);
                    bmp.recycle();
                    bmp = sbmp;
                    canvas = null;
                }
            }
        }

        return bmp;
    }

    /**
     * writeToCache : write bitmap to path as JPG file
     */
    public static boolean writeToCacheJPEG(Bitmap bitmap, String path) {
        boolean rtn = false;
        if (bitmap != null && path != null) {
            rtn = writeToCacheJPEG(bitmap, path, 100);
        }
        return rtn;
    }
    /**
     * writeToCache : write bitmap to path as JPG file
     */
    public static boolean writeToCacheJPEG(Bitmap bitmap, String path,
                                           int quality) {
        boolean rtn = false;
        if (bitmap != null && path != null) {
            try {
                File file = new File(path);
                rtn = true;
                String parent = file.getParent();
                File parentFile = new File(parent);
                if (!parentFile.exists()) {
                    rtn = parentFile.mkdirs();
                }
                if (rtn) {
                    final FileOutputStream fos = new FileOutputStream(file);
                    final BufferedOutputStream bos = new BufferedOutputStream(fos,
                            16384);
                    rtn = bitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos);
                    bos.flush();
                    bos.close();
                    fos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return rtn;
    }
    /**
     * 判断当前系统语言
     * @param context
     * @return
     */
    public static boolean isZh(Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        return language.endsWith("zh");
    }
}
