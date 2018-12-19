package com.lqwawa.intleducation.base.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.media.MediaMetadataRetriever;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.StatFs;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.CollationKey;
import java.text.Collator;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class BaseUtils {
   public static boolean isDemo = false;
   public static final String TAG = "IEMaker";

   public static final String IEMAKER_SETTINGS = "Settings";

   public static final String ACCOUNT = "ShareManager Account";
   public static final String USER_EMAIL = "User Email";
   public static final String USER_PASSWORD = "User Password";
   public static final String USER_TOKEN = "User Web Token";
   public static final String USER_ID = "User Web ID";
   public static final String USER_NAME = "User Nick Name";
   // public static final String INSERT_BRIEF = "insert brief";
   public static final String BRIEF_NAME = "brief name";

   public static final String ROOT = Environment.getExternalStorageDirectory()
         .toString();

   public static final String RECORD_AUDIO = "/Audio/";
   public static final String RECORD_PDF = "/Pdf/";
   public static final String RECORD_VIDEO = "/Video/";
   public static final String RECORD_HEAD_IMAGE_NAME = "head.jpg";
   public static final String RECORD_AUDIO_FILE_NAME = "track";
   public static final String RECORD_XML_NAME = "course_index.xml";
   public static final String RECORD_SHARE_ZIP = "audio.zip";


   public static final String PAGE_INDEX_FILE_NAME = "page_index.xml";

   // public static final String DATA_FOLDER = ROOT
   // + "/Android/data/com.oosic.apps.icourseplayer/";
   // public static final String THUMB_FOLDER = DATA_FOLDER + "Thumb/";
   // public static final String TEMP_FOLDER = DATA_FOLDER + "temp/";
   //
   // public static final String ONLINE_COURSE_TEMP_CACHE = APP_FOLDER
   // + "/online/";

   public static final String PDF_PAGE_NAME = "pdf_page_";
   public static final String SLIDE_PAGE_NAME = "slidepage_";
   public static final String MEDIA_HEAD = "slide_media_";
   public static final String PDF_THUMB_NAME = "thumbnail.jpg";
   public final static int IMPORT_PAGE_SIZE_WIDTH = 1280;
   public final static int IMPORT_PAGE_SIZE_HEIGHT = 1280;
   public static final int DEFAULT_THUMB_HEIGHT = 240;

   public static final int MSG_RECORD_FINISH = 500;
   public static final int MSG_VIDDEO_STARTED = 501;
   public static final int MSG_VIDDEO_COMPLETION = 502;
   public static final int MSG_AUDIO_STARTED = 503;
   public static final int MSG_AUDIO_COMPLETION = 504;
   public static final int MSG_SHARE_DEVICE_DISCONNECTED = 505;
   public static final int MSG_ONLINECOURSE_HEAD_OK = 506;
   public static final int MSG_SAVE_COURSE_OK = 507;
   public static final int MSG_REMOVE_COURSE = 508;
   public static final int MSG_SHAREPLAY_STATUS = 509;
   public static final int MAS_BGMUSIC_COMPLETE = 510;
   public static final int MSG_PLAYBACKSLIDE_SAVE_FINISH = 511;

   /**
    * Constant used to indicate the dimension of mini thumbnail.
    * 
    * @hide Only used by media framework and media provider internally.
    */
   public static final int TARGET_SIZE_MINI_THUMBNAIL = 320;

   /**
    * Constant used to indicate the dimension of micro thumbnail.
    * 
    * @hide Only used by media framework and media provider internally.
    */
   public static final int TARGET_SIZE_MICRO_THUMBNAIL = 90;
   private static final int MAX_NUM_PIXELS_THUMBNAIL = 512 * 384;
   private static final int MAX_NUM_PIXELS_MICRO_THUMBNAIL = 90 * 90;
   private static final int OPTIONS_SCALE_UP = 0x1;
   public static final int OPTIONS_RECYCLE_INPUT = 0x2;
   private static final int UNCONSTRAINED = -1;
   public static final int THUMBNAIL_W = 80;
   public static final int THUMBNAIL_H = 80;
   public final static int HEAD_IMAGE_WIDTH = 320;

   public final static int MINIMUM_SPACE = 200; // 50M

   public enum SORT_TYPE {
      DATE, NAME
   }

   public static SORT_TYPE mSortType = SORT_TYPE.DATE;

   private final static String[] URL_VALID_KEY = { "?url=", "?vId=" };

   public final static int RES_TYPE_COURSE = 16;
   public final static int RES_TYPE_ONEPAGE = 18;
   public final static int RES_TYPE_COURSE_SPEAKER = 19;

   public final static float PAGEFILE_VERSION_JUDGE = 2.1f;

   public static String getFileNameFromPath(String path) {
      if (TextUtils.isEmpty(path)) {
         return null;
      }
      String tempPath = path;
      if (path.endsWith(File.separator)) {
         tempPath = path.substring(0, path.length() - 1);
      }
      int firstIndex = tempPath.lastIndexOf('/');
      if (firstIndex < tempPath.length()  && firstIndex > 0) {
         String name = null;
         try {
            name = tempPath.subSequence(firstIndex + 1, tempPath.length())
                    .toString();
         } catch (Exception e) {
            name = null;
            e.printStackTrace();
         }
         return name;
      } else {
         return path;
      }
   }

   public static String getFileTitle(String srcPath, String destFolder,
         String suffix) {
      int firstIndex = srcPath.lastIndexOf('/');
      int lastIndex = srcPath.length();
      if (suffix != null) {
         lastIndex = srcPath.lastIndexOf('.');
         if (lastIndex <= firstIndex)
            lastIndex = srcPath.length();
      }
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
         while (new File(destFolder, name).exists()) {
            name = temp + "-" + i;
            i++;
         }
      } else {
         while (new File(destFolder, name + suffix).exists()) {
            name = temp + "-" + i;
            i++;
         }
         name = name + suffix;
      }

      return name;
   }

   /**
    * clear the files of the folder with the path,if the folder is not exist
    * then create it.
    * 
    * @param path
    * @param filter
    */
   public static void cleanupFolder(String path, FileFilter filter) {
      File folder = new File(path);
      try {
         if (!folder.exists()) {
            folder.mkdirs();
         } else {
            File[] files = folder.listFiles(filter);
            int size = files.length;
            for (int index = 0; index < size; index++) {
               // when is a file
               if (files[index].isFile()) {
                  files[index].delete();
               }
            }
         }
      } catch (Exception e) {

      }
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
         }
      } catch (Exception e) {
      }
   }

   /**
    * create the file with selected path.
    * 
    * @param path
    */
   public static void createLocalFile(String path) {
      File f = new File(path);
      try {
         if (!f.exists())
            f.createNewFile();
      } catch (Exception e) {

      }
   }

   /**
    * copy the file from src to dst. 1.if src is directory or is not exist or
    * failure , return -1. 2.if copy is finished correctly ,return 0.
    * 
    * @param srcFile
    * @param dstFile
    * @return
    */
   public static int copyFile(File srcFile, File dstFile) {
      int result = 0;
      if (srcFile.isDirectory()) {
         return -1;
      } else {
         if (!srcFile.exists()) {
            return -1;
         } else {
            if (dstFile.exists()) {
               dstFile.delete();
            }
            if (!dstFile.getParentFile().exists()) {
               dstFile.getParentFile().mkdirs();
            }

            InputStream in = null;
            OutputStream out = null;
            try {
               in = new FileInputStream(srcFile);
               out = new FileOutputStream(dstFile);

               byte[] buf = new byte[1024];
               int len;
               while ((len = in.read(buf)) > 0) {
                  out.write(buf, 0, len);
               }
            } catch (IOException e) {
               result = -1;
            } finally {
               try {
                  if (in != null)
                     in.close();
                  if (out != null)
                     out.close();
               } catch (IOException e) {
                  result = -1;
               }
            }
         }
      }
      return result;
   }


   public static void copyDirectory(File from, File to){
      if (from.exists() && from.isDirectory()) {
         for (File file:  from.listFiles()) {
            if (file.isDirectory()) {
               File destDir = new File(to, file.getName());
               if (!destDir.exists()) {
                  destDir.mkdirs();
               }
               copyDirectory(file, destDir);
            } else {
               copyFile(file, new File(to, file.getName()));
            }
         }
      }
   }

   /**
    * writeToCache : write bitmap to path as JPG file
    */
   public static boolean writeToCacheJPEG(Bitmap bitmap, String path) {
      boolean rtn = false;
      if (bitmap != null && path != null) {
         rtn = writeToCacheJPEG(bitmap, path, 60);
      }
      return rtn;
   }

   /**
    * writeToCache : write bitmap to path as PNG file
    */
   public static boolean writeToCachePNG(Bitmap bitmap, String path) {
      boolean rtn = false;
      if (bitmap != null && path != null) {
         rtn = writeToCachePNG(bitmap, path, 60);
      }
      return rtn;
   }

   public static boolean writeToCachePNG(Bitmap bitmap, String path, int quality) {
      boolean rtn = false;
      if (bitmap != null && path != null) {
         try {
            File file = new File(path);
            if (!file.getParentFile().exists()) {
               file.getParentFile().mkdirs();
            }
            file.createNewFile();
            final FileOutputStream fos = new FileOutputStream(file);
            final BufferedOutputStream bos = new BufferedOutputStream(fos,
                  16384);
            bitmap.compress(Bitmap.CompressFormat.PNG, quality, bos);
            bos.flush();
            bos.close();
            fos.close();
            rtn = true;
         } catch (Exception e) {
            e.printStackTrace();
         }
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
               // rtn = file.createNewFile();
               // if (rtn) {
               // final FileOutputStream fos = new FileOutputStream(file);
               // final BufferedOutputStream bos = new BufferedOutputStream(fos,
               // 16384);
               // rtn = bitmap.compress(Bitmap.CompressFormat.JPEG, quality,
               // bos);
               // bos.flush();
               // bos.close();
               // fos.close();
               // }
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

   public synchronized static ImageInfo GetImageInfo(String pathName) {
      ImageInfo info = new ImageInfo();
      short degree = 0;
      ExifInterface exif = null;
      try {
         exif = new ExifInterface(pathName);
      } catch (IOException ex) {
         Log.e(TAG, "cannot read exif", ex);
      }
      if (exif != null) {
         int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
               -1);
         if (orientation != -1) {
            // We only recognize a subset of orientation tag values.
            switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
               degree = 90;
               break;
            case ExifInterface.ORIENTATION_ROTATE_180:
               degree = 180;
               break;
            case ExifInterface.ORIENTATION_ROTATE_270:
               degree = 270;
               break;
            }

         }
         info.orientation = degree;
         info.width = exif.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, 0);
         info.height = exif.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, 0);
      }
      if (info.width <= 0 || info.height <= 0) {
         BitmapFactory.Options options = new BitmapFactory.Options();
         options.inScaled = false;
         options.inPreferredConfig = Bitmap.Config.RGB_565;
         options.inDither = false;
         options.inJustDecodeBounds = true;

         BitmapFactory.decodeFile(pathName, options);
         info.width = options.outWidth;
         info.height = options.outHeight;
         info.orientation = 0;
      }
      return info;
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

   public static class ImageInfo {
      public int dbindex;
      public int width;
      public int height;
      public short orientation;
   }

   public static class ItemInfo implements Comparable<ItemInfo> {
      public String mTitle = "";
      public long mLastViewTime = 0;

      @Override
      public int compareTo(ItemInfo another) {
         if (mSortType.equals(SORT_TYPE.DATE)) {
            long viewTime1 = mLastViewTime > 0 ? mLastViewTime : 0;
            long viewTime2 = another.mLastViewTime > 0 ? another.mLastViewTime
                  : 0;
            return new Long(viewTime2).compareTo(new Long(viewTime1));
         } else {
            if (mTitle != null && another.mTitle != null) {
               Collator collator = Collator.getInstance(Locale.CHINESE);
               CollationKey key1 = collator.getCollationKey(mTitle);
               CollationKey key2 = collator.getCollationKey(another.mTitle);
               return key1.compareTo(key2);
            } else {
               return 0;
            }
         }
      }

   }

   public static BitmapFactory.Options loadBitmapOptions(String pathName) {
      BitmapFactory.Options options = new BitmapFactory.Options();
      options.inJustDecodeBounds = true;
      options.inScaled = false;
      options.inPreferredConfig = Bitmap.Config.ARGB_8888;
      options.inDither = false;

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
      return options;
   }

   private static Bitmap createBitmap(Bitmap bmp, int outputW, int outputH) {
      if (bmp == null) {
         return null;
      }

      int bh = bmp.getHeight();
      if (outputH > 0 && bh > outputH) {
         outputW = outputH * bmp.getWidth() / bmp.getHeight();
         Bitmap sbmp = null;
         try {
            sbmp = Bitmap.createBitmap(outputW, outputH,
                    Bitmap.Config.ARGB_8888);
         } catch (OutOfMemoryError e) {
            BaseUtils.outLog("DecodeCache",
                    "createStretchBitmap, OutOfMemoryError!");
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
      return bmp;
   }

   //shouyi changed, call BitmapUtils.loadBitmap and make this function brief.
   public synchronized static Bitmap loadBitmap(String pathName, int outputW,
         int outputH, int rotate) {
      Bitmap bmp = BitmapUtils.loadBitmap(pathName, outputW, outputH);

      if (bmp == null) {
         return null;
      }

      rotate %= 360;
      if (rotate != 0) {
         Matrix matrix = new Matrix();
         matrix.setRotate(rotate);

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

      bmp = createBitmap(bmp, outputW, outputH);
      System.gc();

      return bmp;

   }

   public synchronized static Bitmap loadBitmap(String pathName, int largeSide,
                                                int shortSide) {
      return loadBitmap(pathName, largeSide, shortSide, false);
   }

   //shouyi changed, call BitmapUtils.loadBitmap and make this function brief.
   public synchronized static Bitmap loadBitmap(String pathName, int width,
         int height, boolean bAccurateSize) {
      Bitmap bmp = BitmapUtils.loadBitmap(pathName, width, height);

      if (bAccurateSize && bmp != null) {
         bmp = createBitmap(bmp, width, height);
      }
      System.gc();
      return bmp;
   }

   public static String convertUrl(String title) {
      try {
         title = URLEncoder.encode(title.trim(), "UTF-8");
      } catch (UnsupportedEncodingException e) {
         title = title.replace("%", "%25");
         title = title.replace(" ", "%20");
         title = title.replace("!", "%21");
         title = title.replace("#", "%23");
         title = title.replace("$", "%24");
         title = title.replace("&", "%26");
         title = title.replace(";", "%3b");
         title = title.replace("[", "%5b");
         title = title.replace("]", "%5d");
      }
      title = title.replace("+", "%20");

      return title;
   }

   public static Bitmap combineBitmap(Bitmap background, Bitmap foreground, int fillColor) {

      int cacheW = 0;
      int cacheH = 0;

      if (background == null && foreground == null) {
         return null;
      } else if (background != null && foreground == null) {
         return background;
      } else if (foreground != null) {
         cacheW = foreground.getWidth();
         cacheH = foreground.getHeight();
      }

      if (cacheW > 0 && cacheH > 0) {
         Paint paint = new Paint();
         paint.setAntiAlias(true);
         paint.setDither(true);
         paint.setStyle(Paint.Style.STROKE);
         paint.setStrokeJoin(Paint.Join.ROUND);
         paint.setStrokeCap(Paint.Cap.ROUND);

         Bitmap canvasBmp = Bitmap
                 .createBitmap(cacheW, cacheH, Bitmap.Config.ARGB_8888);
         Canvas canvas = new Canvas(canvasBmp);
         canvas.drawColor(fillColor);
         if (background != null) {

            int w = 0, h = 0;
            float s;
            int bw = background.getWidth();
            int bh = background.getHeight();
            if ((float) bw / (float) bh >= (float) cacheW / (float) cacheH) {
               w = bw;
               h = cacheH * w / cacheW;
               s = cacheW / (float) bw;
            } else {
               h = bh;
               w = cacheW * h / cacheH;
               s = cacheH / (float) bh;
            }

            if (s != 1.0) {
               Matrix m = new Matrix();
               m.postScale(s, s);

               if (w > 0 && h > 0) {
                  Bitmap bmp = null;
                  try {
                     bmp = Bitmap.createBitmap(background,0,0,background.getWidth(),background.getHeight(),m,true);
                  } catch (OutOfMemoryError e) {

                  }
                  if (bmp != null) {
                     int bgWidth = bmp.getWidth();
                     int bgHeight = bmp.getHeight();
                     canvas.drawBitmap(bmp, (cacheW - bgWidth) / 2, (cacheH - bgHeight) / 2,
                             paint);
                     canvas.save(Canvas.ALL_SAVE_FLAG);
                     canvas.restore();
                     bmp.recycle();
                  }
               }
            }else {
               int bgWidth = background.getWidth();
               int bgHeight = background.getHeight();
               canvas.drawBitmap(background, (cacheW - bgWidth) / 2, (cacheH - bgHeight) / 2,
                       paint);
               canvas.save(Canvas.ALL_SAVE_FLAG);
               canvas.restore();

            }
         }
         if (foreground != null) {
            canvas.drawBitmap(foreground, 0, 0, paint);
         }
         return canvasBmp;
      }
      return null;
   }

   private final static float[] mMatrixValues = new float[9];

   private static float getValue(Matrix matrix, int whichValue) {
      matrix.getValues(mMatrixValues);
      return mMatrixValues[whichValue];
   }

   public static float getScale(Matrix matrix) {
      return getValue(matrix, Matrix.MSCALE_X);
   }

   public static String getSerialNumber() {
      String str = "", cpuStr = "", cpuAddr = "0000000000000000";

      try {
         Process pp = Runtime.getRuntime().exec("cat /proc/cpuinfo");
         InputStreamReader isreader = new InputStreamReader(pp.getInputStream());
         LineNumberReader lnreader = new LineNumberReader(isreader);

         for (int i = 1; i < 100; i++) {
            str = lnreader.readLine();
            if (str != null) {
               if (str.indexOf("Serial") > -1 || str.indexOf("serial") > -1) {
                  cpuStr = str.substring(str.indexOf(":") + 1, str.length());
                  cpuAddr = cpuStr.trim();
                  break;
               }
            } else {
               break;
            }
         }
      } catch (IOException e) {
         e.printStackTrace();
      }

      return cpuAddr;
   }

   public static boolean zip(File input, File output) throws Exception {
      try {
         File outputFile = output;
         FileOutputStream outputStream = new FileOutputStream(outputFile);
         ZipOutputStream outZip = new ZipOutputStream(outputStream);

         File inputfile = input;
         if (inputfile.exists()) {
            if (ZipFiles(inputfile.getParent() + File.separator,
                  inputfile.getName(), outZip) < 0) {
               outZip.finish();
               outZip.close();
               return false;
            } else {
               outZip.finish();
               outZip.close();
            }
         }

      } catch (Exception e) {
         e.printStackTrace();
         return false;
      }

      return true;
   }

   private static int ZipFiles(String folderString, String fileString,
         ZipOutputStream zipOutputSteam) throws Exception {
      if (zipOutputSteam == null)
         return -1;

      File file = new File(folderString, fileString);
      if (file.isFile()) {
         ZipEntry zipEntry = new ZipEntry(fileString);
         FileInputStream inputStream = new FileInputStream(file);
         zipOutputSteam.putNextEntry(zipEntry);
         int len;
         byte[] buffer = new byte[4096];
         while ((len = inputStream.read(buffer)) != -1) {
            zipOutputSteam.write(buffer, 0, len);
         }
         zipOutputSteam.closeEntry();
      } else {
         String fileList[] = file.list();
         if (fileList.length <= 0) {
            ZipEntry zipEntry = new ZipEntry(fileString + File.separator);
            zipOutputSteam.putNextEntry(zipEntry);
            zipOutputSteam.closeEntry();
         }
         for (int i = 0; i < fileList.length; i++) {
            ZipFiles(folderString, fileString + File.separator + fileList[i],
                  zipOutputSteam);
         }
      }
      return 0;

   }

   static public void unzip(String inputFilePath, String outputFilePath)
         throws Exception {
      if (TextUtils.isEmpty(inputFilePath) || TextUtils.isEmpty(outputFilePath)) {
         return;
      }
      if (!outputFilePath.endsWith(File.separator)) {
         outputFilePath = outputFilePath + File.separator;
      }
      ZipInputStream in = new ZipInputStream(new FileInputStream(inputFilePath));
      ZipEntry z;
      String name = "";
      int counter = 0;

      while ((z = in.getNextEntry()) != null) {
         name = z.getName();
         int index = name.indexOf(File.separator);
         if (index > 0 && ++index < name.length()) {
            name = name.substring(index);
         }
         if (z.isDirectory()) {

            name = name.substring(0, name.length() - 1);

            File folder = new File(outputFilePath + name);
            folder.mkdir();
            if (counter == 0) {
            }
            counter++;
         } else {
            File file = new File(outputFilePath + name);
            if (!file.getParentFile().exists()) {
               file.getParentFile().mkdirs();
            }
            if (!file.exists()) {
               file.createNewFile();
            }
            // get the output stream of the file
            FileOutputStream out = new FileOutputStream(file);
            int ch;
            byte[] buffer = new byte[1024];
            // read (ch) bytes into buffer
            while ((ch = in.read(buffer)) > 0) {
               // write (ch) byte from buffer at the position 0
               out.write(buffer, 0, ch);
               out.flush();
            }
            out.close();
         }
      }

      in.close();
   }

   static public void unzip(InputStream inputStream, String outputFilePath)
         throws Exception {
      if (!outputFilePath.endsWith(File.separator)) {
         outputFilePath = outputFilePath + File.separator;
      }
      ZipInputStream in = new ZipInputStream(inputStream);
      ZipEntry z;
      String name = "";
      int counter = 0;

      while ((z = in.getNextEntry()) != null) {
         name = z.getName();
         int index = name.indexOf(File.separator);
         if (index > 0 && ++index < name.length()) {
            name = name.substring(index);
         }
         if (z.isDirectory()) {
            int separatorIndex = name.indexOf(File.separator);
            if (separatorIndex >= 0 && separatorIndex < name.length() - 1) {
               name = name.substring(0, name.length() - 1);

               File folder = new File(outputFilePath + name);
               folder.mkdir();
               if (counter == 0) {
               }
               counter++;
            }
         } else {
            File file = new File(outputFilePath + name);
            if (!file.getParentFile().exists()) {
               file.getParentFile().mkdirs();
            }
            if (!file.exists()) {
               file.createNewFile();
            }
            // get the output stream of the file
            FileOutputStream out = new FileOutputStream(file);
            int ch;
            byte[] buffer = new byte[1024];
            // read (ch) bytes into buffer
            while ((ch = in.read(buffer)) > 0) {
               // write (ch) byte from buffer at the position 0
               out.write(buffer, 0, ch);
               out.flush();
            }
            out.close();
         }
      }

      in.close();
   }

   private static class SizedThumbnailBitmap {
      public Bitmap mBitmap;
   }

   private static int computeInitialSampleSize(BitmapFactory.Options options,
         int minSideLength, int maxNumOfPixels) {
      double w = options.outWidth;
      double h = options.outHeight;

      int lowerBound = (maxNumOfPixels == UNCONSTRAINED) ? 1 : (int) Math
            .ceil(Math.sqrt(w * h / maxNumOfPixels));
      int upperBound = (minSideLength == UNCONSTRAINED) ? 128 : (int) Math.min(
            Math.floor(w / minSideLength), Math.floor(h / minSideLength));

      if (upperBound < lowerBound) {
         // return the larger one when there is no overlapping zone.
         return lowerBound;
      }

      if ((maxNumOfPixels == UNCONSTRAINED) && (minSideLength == UNCONSTRAINED)) {
         return 1;
      } else if (minSideLength == UNCONSTRAINED) {
         return lowerBound;
      } else {
         return upperBound;
      }
   }

   private static int computeSampleSize(BitmapFactory.Options options,
         int minSideLength, int maxNumOfPixels) {
      int initialSize = computeInitialSampleSize(options, minSideLength,
            maxNumOfPixels);

      int roundedSize;
      if (initialSize <= 8) {
         roundedSize = 1;
         while (roundedSize < initialSize) {
            roundedSize <<= 1;
         }
      } else {
         roundedSize = (initialSize + 7) / 8 * 8;
      }

      return roundedSize;
   }

   private static void createThumbnailFromEXIF(String filePath, int targetSize,
         int maxPixels, SizedThumbnailBitmap sizedThumbBitmap) {
      if (filePath == null)
         return;

      ExifInterface exif = null;
      byte[] thumbData = null;
      try {
         exif = new ExifInterface(filePath);
         if (exif != null) {
            thumbData = exif.getThumbnail();
         }
      } catch (IOException ex) {
         Log.w(TAG, ex);
      }

      BitmapFactory.Options fullOptions = new BitmapFactory.Options();
      BitmapFactory.Options exifOptions = new BitmapFactory.Options();

      // Compute exifThumbWidth.
      if (thumbData != null) {
         exifOptions.inJustDecodeBounds = true;
         BitmapFactory.decodeByteArray(thumbData, 0, thumbData.length,
               exifOptions);
      }

      // Compute fullThumbWidth.
      fullOptions.inJustDecodeBounds = true;
      BitmapFactory.decodeFile(filePath, fullOptions);
      fullOptions.inSampleSize = computeSampleSize(fullOptions, targetSize,
            maxPixels);

      // Choose the larger thumbnail as the returning sizedThumbBitmap.
      if (thumbData != null) {// && exifThumbWidth >= fullThumbWidth

         exifOptions.inJustDecodeBounds = false;
         sizedThumbBitmap.mBitmap = BitmapFactory.decodeByteArray(thumbData, 0,
               thumbData.length, exifOptions);
      } else {
         fullOptions.inJustDecodeBounds = false;
         sizedThumbBitmap.mBitmap = BitmapFactory.decodeFile(filePath,
               fullOptions);
      }
   }

   public static Bitmap extractThumbnail(Bitmap source, int width, int height,
         int options) {
      if (source == null) {
         return null;
      }

      float scale;
      if (source.getWidth() < source.getHeight()) {
         scale = width / (float) source.getWidth();
      } else {
         scale = height / (float) source.getHeight();
      }
      Matrix matrix = new Matrix();
      matrix.setScale(scale, scale);
      Bitmap thumbnail = transform(matrix, source, width, height,
            OPTIONS_SCALE_UP | options);

      return thumbnail;
   }

   /**
    * Transform source Bitmap to targeted width and height.
    */
   private static Bitmap transform(Matrix scaler, Bitmap source,
         int targetWidth, int targetHeight, int options) {
      boolean scaleUp = (options & OPTIONS_SCALE_UP) != 0;
      boolean recycle = (options & OPTIONS_RECYCLE_INPUT) != 0;

      int deltaX = source.getWidth() - targetWidth;
      int deltaY = source.getHeight() - targetHeight;
      if (!scaleUp && (deltaX < 0 || deltaY < 0)) {
         /*
          * In this case the bitmap is smaller, at least in one dimension, than
          * the target. Transform it by placing as much of the image as possible
          * into the target and leaving the top/bottom or left/right (or both)
          * black.
          */
         Bitmap b2 = Bitmap.createBitmap(targetWidth, targetHeight,
               source.getConfig());
         Canvas c = new Canvas(b2);

         int deltaXHalf = Math.max(0, deltaX / 2);
         int deltaYHalf = Math.max(0, deltaY / 2);
         Rect src = new Rect(deltaXHalf, deltaYHalf, deltaXHalf
               + Math.min(targetWidth, source.getWidth()), deltaYHalf
               + Math.min(targetHeight, source.getHeight()));
         int dstX = (targetWidth - src.width()) / 2;
         int dstY = (targetHeight - src.height()) / 2;
         Rect dst = new Rect(dstX, dstY, targetWidth - dstX, targetHeight
               - dstY);
         c.drawBitmap(source, src, dst, null);
         if (recycle) {
            source.recycle();
         }
         return b2;
      }
      float bitmapWidthF = source.getWidth();
      float bitmapHeightF = source.getHeight();

      float bitmapAspect = bitmapWidthF / bitmapHeightF;
      float viewAspect = (float) targetWidth / targetHeight;

      if (bitmapAspect > viewAspect) {
         float scale = targetHeight / bitmapHeightF;
         if (scale < .9F || scale > 1F) {
            scaler.setScale(scale, scale);
         } else {
            scaler = null;
         }
      } else {
         float scale = targetWidth / bitmapWidthF;
         if (scale < .9F || scale > 1F) {
            scaler.setScale(scale, scale);
         } else {
            scaler = null;
         }
      }

      Bitmap b1;
      if (scaler != null) {
         // this is used for minithumb and crop, so we want to filter here.
         b1 = Bitmap.createBitmap(source, 0, 0, source.getWidth(),
               source.getHeight(), scaler, true);
      } else {
         b1 = source;
      }

      if (recycle && b1 != source) {
         source.recycle();
      }

      int dx1 = Math.max(0, b1.getWidth() - targetWidth);
      int dy1 = Math.max(0, b1.getHeight() - targetHeight);

      Bitmap b2 = Bitmap.createBitmap(b1, dx1 / 2, dy1 / 2, targetWidth,
            targetHeight);

      if (b2 != b1) {
         if (recycle || b1 != source) {
            b1.recycle();
         }
      }

      return b2;
   }

   // ///////////////////////////Dialog show////////////////////////////
   public interface DialogConfirmCallback {
      void onConfirm(DialogInterface dialog);
   }
   
   
   private static Toast mToast = null;
   public static void showMessage(Context context, String msg) {
      if (mToast != null) {
         mToast.cancel();
         mToast = null;
      }
      mToast = new Toast(context);
      LayoutInflater inflate = (LayoutInflater) context
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      View v = inflate.inflate(ResourceUtils.getLayoutId(context, "ecourse_message_view"), null);
      TextView tv = (TextView) v.findViewById(ResourceUtils.getId(context, "msg"));
      tv.setText(msg);
      mToast.setView(v);
      mToast.setGravity(Gravity.CENTER, 0, 0);
      mToast.setDuration(Toast.LENGTH_SHORT);
      mToast.show();
      
   }

   public static void ShowConfirmDialog(Context context, String msg,
         DialogConfirmCallback cb) {
      if (((Activity) context).isFinishing()) {
         return;
      }
      ShowConfirmDialog(context, msg, ResourceUtils.getStringId(context, "confirm"), cb);
   }

   public static void ShowConfirmDialog(Context context, String msg,
         int positiveBtnText, DialogConfirmCallback cb) {
      ShowConfirmDialog(context, msg, positiveBtnText, cb, 0);

   }

   public static void ShowConfirmDialog(Context context, String msg,
         int positiveBtnText, final DialogConfirmCallback cb,
         int negativeBtnText) {
      if (((Activity) context).isFinishing()) {
         return;
      }
      BaseDialog dialog = new BaseDialog(context).setMessage(msg);
      if (positiveBtnText > 0) {
         dialog.setPositiveButton(positiveBtnText,
               new DialogInterface.OnClickListener() {
                  public void onClick(DialogInterface dialog, int which) {
                     if (cb != null) {
                        cb.onConfirm(dialog);
                     }

                  }
               });
      }
      if (negativeBtnText > 0) {
         dialog.setNegativeButton(negativeBtnText, null);
      }

      dialog.setCancelable(true);
      dialog.show();
   }



   public static void ShowConfirmDialog(Context context, String msg,
                                        int positiveBtnText, final DialogConfirmCallback positiveCb,
                                        int negativeBtnText, final DialogConfirmCallback negativeCb) {
      if (((Activity) context).isFinishing()) {
         return;
      }
      BaseDialog dialog = new BaseDialog(context).setMessage(msg);
      if (positiveBtnText > 0) {
         dialog.setPositiveButton(positiveBtnText,
                 new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                       if (positiveCb != null) {
                          positiveCb.onConfirm(dialog);
                       }

                    }
                 });
      }
      if (negativeBtnText > 0) {
         dialog.setNegativeButton(negativeBtnText, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
               if (negativeCb != null) {
                  negativeCb.onConfirm(dialog);
               }

            }
         });
      }

      dialog.setCancelable(true);
      dialog.show();
   }

   public static void ShowNetworkErrorDialog(final Context context,
         final DialogConfirmCallback cb) {
      new AlertDialog.Builder(context)
            .setMessage(ResourceUtils.getStringId(context, "net_isonline_tip_msg"))
            .setPositiveButton(ResourceUtils.getStringId(context, "confirm"),
                  new DialogInterface.OnClickListener() {
                     public void onClick(DialogInterface dialog, int which) {
                        if (cb != null) {
                           cb.onConfirm(dialog);
                        }

                     }
                  }).setCancelable(false).show();
   }

   public static ProgressDialog showProgressDlg(Context context, String msg) {
      ProgressDialog dlg = null;
      if (msg == null) {
         msg = context.getString(ResourceUtils.getStringId(context, "wait"));
      }
      try {
         dlg = new ProgressDialog(context);
         dlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
         dlg.setMax(100);
         dlg.setMessage(msg);
         dlg.setCancelable(false);
         dlg.show();
      } catch (Exception e) {
         e.printStackTrace();
      }
      return dlg;
   }

   private static PowerManager pm = null;
   private static WakeLock mWakeLock = null;
   private static boolean mbWakeLockAcquire = false;
   private static Context mWakeLockContext = null;

   public static void acquirWakeLock(Context context) {
      mWakeLockContext = context;
      if (pm == null) {
         pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
         mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK,
               context.getClass().getName());
         mWakeLock.setReferenceCounted(false);
      }
      if (!mbWakeLockAcquire) {
         mWakeLock.acquire();
         mbWakeLockAcquire = true;
      }
   }

   public static void releaseWakeLock(Context context) {
      if (mbWakeLockAcquire
            && context.getClass().getName()
                  .equals(mWakeLockContext.getClass().getName())) {

         mWakeLock.release();
         mbWakeLockAcquire = false;
         mWakeLockContext = null;
      }
   }

   public static String getTimeStringByMilliSec(long time) {
      int s = (int) (time / 1000);
      int H = s / 3600;
      s = s % 3600;
      int M = s / 60;
      s = s % 60;
      int S = s;
      return String.format("%02d:%02d:%02d", H, M, S);
   }

   public static String getFileExt(String filename) {
      String extName = null;
      int pos = filename.lastIndexOf('.');
      if (pos >= 0) {
         extName = filename.substring(pos, filename.length());
      }
      return extName;
   }

   public static boolean checkFileName(Context context, String name) {
      String regEx = "[*\\\\:\"����\'<>/?|]";
      String tip = "/ \\ : * ? \" < > |";
      Pattern p = Pattern.compile(regEx);
      Matcher mat = p.matcher(name);
      boolean rs = mat.find();
      if (rs) {
         Toast toast = Toast.makeText(context, "", Toast.LENGTH_LONG);
         TextView tv = new TextView(context);
         tv.setGravity(Gravity.CENTER);
         tv.setTextColor(Color.WHITE);
         tv.setText(context.getResources().getString(ResourceUtils.getStringId(context, "filename_error"))
               + "\n" + tip);
         tv.setBackgroundColor(Color.BLACK);
         toast.setView(tv);
         toast.show();
         return true;
      }
      return false;
   }

   public static boolean checkEditString(Context context, String string) {
      String regEx = "[*\\\\:\"����\'<>/?|]";
      String tip = "/ \\ : * ? \" < > |";
      Pattern p = Pattern.compile(regEx);
      Matcher mat = p.matcher(string);
      boolean rs = mat.find();
      if (rs) {
         Toast toast = Toast.makeText(context, "", Toast.LENGTH_LONG);
         TextView tv = new TextView(context);
         tv.setGravity(Gravity.CENTER);
         tv.setTextColor(Color.WHITE);
         tv.setText(context.getResources().getString(ResourceUtils.getStringId(context, "edit_error"))
               + "\n" + tip);
         tv.setBackgroundColor(Color.BLACK);
         toast.setView(tv);
         toast.show();
         return true;
      }
      return false;
   }

   public static int getCurrentDate() {
      SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
      Date curDate = new Date(System.currentTimeMillis());
      String date = formatter.format(curDate);
      return Integer.valueOf(date);
   }

   public static boolean isOverdue() {
      if (isDemo && getCurrentDate() > 20140501) {
         return true;
      }
      return false;
   }

   public static int haveSpace() {
      long ret = readSDCard();
      int value = 0;
      long temp = ret / 1024 / 1024;
      if (temp > MINIMUM_SPACE)
         value = 0;
      else if (ret == -1)
         value = -1;
      else
         value = 1;
      return value;
   }

   static public long readSDCard() {
      String state = Environment.getExternalStorageState();

      if (Environment.MEDIA_MOUNTED.equals(state)) {
         File sdcardDir = Environment.getExternalStorageDirectory();
         StatFs sf = new StatFs(sdcardDir.getPath());
         long blockSize = sf.getBlockSize();
         long blockCount = sf.getBlockCount();
         long availCount = sf.getAvailableBlocks();
         long freespace = availCount * blockSize;// / 1024 / 1024;
         return freespace;
      } else {
         return -1;
      }
   }

   public static String getOnlineFileCacheName(String url) {
      String cacheFileName = Md5FileNameGenerator.generate(url);
      return cacheFileName;
   }

   public static boolean checkValidSharelink(String urlStr) {
      boolean bValid = false;
      for (int i = 0; i < URL_VALID_KEY.length; i++) {
         if (urlStr.contains(URL_VALID_KEY[i])) {
            bValid = true;
            break;
         }
      }
      return bValid;
   }

   public static boolean checkNetworkConnection(Context context) {
      ConnectivityManager connectionManager = (ConnectivityManager) context
            .getSystemService(Context.CONNECTIVITY_SERVICE);
      NetworkInfo networkInfo = connectionManager.getActiveNetworkInfo();
      if (networkInfo != null) {
         if (networkInfo.isConnected()) {
            if (networkInfo.getTypeName().toLowerCase().equals("wifi")
                  || networkInfo.getTypeName().toLowerCase().equals("mobile")) {
               return true;
            }
         }
      }
      showMessage(context, context.getString(ResourceUtils.getStringId(context, "base_network_error")));
      return false;
   }

   public static boolean checkWifiConnection(Context context) {
      ConnectivityManager connectionManager = (ConnectivityManager) context
            .getSystemService(Context.CONNECTIVITY_SERVICE);
      NetworkInfo networkInfo = connectionManager.getActiveNetworkInfo();
      if (networkInfo != null) {
         if (networkInfo.isConnected()) {
            if (networkInfo.getTypeName().toLowerCase().equals("wifi")) {
               return true;
            }
         }
      }
      return false;
   }
   
   public static boolean isTable(Context context) {
	      boolean rtn = true;
	      float density = 0;
	      WindowManager wm = (WindowManager)context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
	      Display display = wm.getDefaultDisplay();
	      DisplayMetrics dm = new DisplayMetrics();
	      display.getMetrics(dm);
	      density = dm.density;

	      int wpixels = display.getWidth();
	      int hpixels = display.getHeight();
	      if (wpixels < hpixels) {
	         int temp = hpixels;
	         hpixels = wpixels;
	         wpixels = temp;
	      }
	      
	      if ((float)wpixels / density < 900) {
	         rtn = false;
	      }
	      return rtn;
	   }

   public static String joinFilePath(String prefix, String suffix) {
      int prefixLength = prefix.length();
      boolean haveSlash = (prefixLength > 0 && prefix.charAt(prefixLength - 1) == File.separatorChar);
      if (!haveSlash) {
         haveSlash = (suffix.length() > 0 && suffix.charAt(0) == File.separatorChar);
      }
      return haveSlash ? (prefix + suffix) : (prefix + File.separatorChar + suffix);
   }


   public static void saveAssetsFileToLocal(Context context, String assetsFileName, String saveFilePath) {
      try {
         InputStream myInput = null;
         myInput = context.getAssets().open(assetsFileName);
         OutputStream myOutput = null;
         File outFile = new File(saveFilePath);

         myOutput = new FileOutputStream(outFile);
         byte[] buffer = new byte[1024];
         int length;
         while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
         }
         // Close the streams
         myOutput.flush();
         myOutput.close();
         myInput.close();
      } catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }

   }


   public static void saveRawFileToLocal(Context context, int fileId, String saveFilePath) {
      try {
         InputStream myInput = null;
         myInput = context.getResources().openRawResource(fileId);
         OutputStream myOutput = null;
         File outFile = new File(saveFilePath);

         myOutput = new FileOutputStream(outFile);
         byte[] buffer = new byte[1024];
         int length;
         while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
         }
         // Close the streams
         myOutput.flush();
         myOutput.close();
         myInput.close();
      } catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }

   }

   public static boolean downloadByUrl(String urlStr, File distFile) {
      if (urlStr == null) {
         // This should never happened
         return false;
      }

      boolean done = true;
      URL url = null;
      InputStream inputStream = null;
      FileOutputStream outputStream = null;

      try {
         url = new URL(urlStr);
         HttpURLConnection mConnection = (HttpURLConnection) url
                 .openConnection();
         mConnection.setRequestProperty("Connection", "close");
         mConnection.setConnectTimeout(30000);
         mConnection.connect();
         inputStream = mConnection.getInputStream();
         if (inputStream == null) {
            return false;
         }

         if (!distFile.getParentFile().exists()) {
            boolean rtn = distFile.getParentFile().mkdirs();
         }
         outputStream = new FileOutputStream(distFile);

         byte[] buffer = new byte[8192];
         int length;
         CRC32 crc = new CRC32();
         for (;;) {
            length = inputStream.read(buffer);
            if (length == -1)
               break;
            else {
               outputStream.write(buffer, 0, length);
               crc.update(buffer, 0, length);
            }

         }
      } catch (IOException e) {
         done = false;
      } finally {
         try {
            if (outputStream != null) {
               outputStream.flush();
               outputStream.close();
               outputStream = null;
            }

            if (inputStream != null) {
               inputStream.close();
               inputStream = null;
            }

         } catch (IOException e) {
            // Ignore
         } finally {
            outputStream = null;
            inputStream = null;

         }
      }
      return done;
   }

   public static Bitmap getVideoThumbnail(String filePath) {
      Bitmap bitmap = null;
      MediaMetadataRetriever retriever = new MediaMetadataRetriever();
      try {
         retriever.setDataSource(filePath);
         bitmap = retriever.getFrameAtTime();
      } catch(IllegalArgumentException e) {
         e.printStackTrace();
      }
      catch (RuntimeException e) {
         e.printStackTrace();
      }
      finally {
         try {
            retriever.release();
         }
         catch (RuntimeException e) {
            e.printStackTrace();
         }
      }
      if (bitmap != null && bitmap.isRecycled()) {
         bitmap = null;
      }
      return bitmap;
   }

   public static long getFileSizes(File f){//取得文件大小
      long s=0;
      if (f.exists()) {
         FileInputStream fis = null;
         try {
            fis = new FileInputStream(f);
         } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
         try {
            s= fis.available();
         } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
      }
      return s;
   }

   public static int getCoursetType(String coursePath) {
      int type = -1;
      if (coursePath == null || !new File(coursePath).exists()) {
         return -1;
      }
      String pageIndexFile = BaseUtils.joinFilePath(coursePath, BaseUtils.PAGE_INDEX_FILE_NAME);
      String courseIndexFile = BaseUtils.joinFilePath(coursePath, BaseUtils.RECORD_XML_NAME);

      if (new File(pageIndexFile).exists() && new File(courseIndexFile).exists()) {
         type = RES_TYPE_COURSE_SPEAKER;
      } else if (new File(courseIndexFile).exists() && !new File(pageIndexFile).exists()) {
         type = RES_TYPE_COURSE;
      } else if (!new File(courseIndexFile).exists() && new File(pageIndexFile).exists()) {
         type = RES_TYPE_ONEPAGE;
      }
      return type;
   }
   
   public static int[] getDrawableArray(Context context, int drawableArr) {
	   TypedArray ar = context.getResources().obtainTypedArray(drawableArr);
	   int len = ar.length();
	   int[] resIds = new int[len];
	   for (int i = 0; i < len; i++) {
		   resIds[i] = ar.getResourceId(i, 0);
	   }
	   ar.recycle();
	   return resIds;
   }

   public static void outLog(String tag, String msg) {
      Log.d(TAG, tag + " >>>>>>>>>>>>>>>>>> " + msg);
   }

   /**
    * 返回当前程序版本名
    */
   public static String getAppVersionName(Context context) {
      String versionName = "";
      try {
         // ---get the package info---
         PackageManager pm = context.getPackageManager();
         PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
         versionName = pi.versionName;
         if (versionName == null || versionName.length() <= 0) {
            return "";
         }
      } catch (Exception e) {
         LogUtil.e("VersionInfo", "Exception", e);
      }
      return versionName;
   }
}
