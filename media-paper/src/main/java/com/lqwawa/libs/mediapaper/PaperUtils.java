package com.lqwawa.libs.mediapaper;

import android.content.Context;
import android.database.Cursor;
import android.graphics.*;
import android.media.ExifInterface;
import android.os.Environment;
import android.os.StatFs;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.osastudio.common.utils.Utils;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PaperUtils {
   public final static int COMMON_MODE  =  1;
   public final static int HOMEWORK_MODE  =  2;

   public final static int NOT_SUPPORT_TYPE                = 0x0;
   public final static int SUPPORT_PERSONAL_MATERIAL_TYPE  = 0x00000001;

   public final static String SDCARD_PATH = Environment.getExternalStorageDirectory().getPath();
   public final static String SUB_IMAGE="images";
   public final static String SUB_VIDEO="video";
   public final static String SUB_RECORD="audio";
   public final static String SUB_THUMB="thumb";
   public final static String PATH_SUFFIX = "~mp";
//   protected static String mNewFileName = null;
//   protected static String mFilePath = null;
   public static final String USERTITLE = "UserTitle";
   public static final String EDITVIEW = "editview";
   public static final String IMAGEVIEW = "imageview";
   public static final String VIDEOVIEW = "videoview";
   public static final String SKETCHPAD = "sketchpad";
   public static final String RECORDVIEW = "recordview";
   public static final String COURSEVIEW = "courseview";
   public static final String COURSEVIEW2 = "courseview2";
   public static final String HWPAGEVIEW = "hwpageview";
   public static final String PAGEBREAK = "pagebreak";

   public static final String[] TYPE_GROUP = {
           USERTITLE, EDITVIEW, IMAGEVIEW, RECORDVIEW,
           VIDEOVIEW, COURSEVIEW, HWPAGEVIEW, COURSEVIEW2
   };

//	public static final int IMAGECOUNTLIMIT = 30;
   public static final int IMAGECOUNTLIMIT = 10000; //设置大值目的是不限制
//   public final static int EditViewHight = 0;
//   public final static int ImageViewHight = 0;
//   public final static int VideoViewHight = 0;
//   public final static int RecordViewHight = 0;
//   public final static int SketchPadViewHight = 0;

   //wm add for page preview
   public final static int PageHight = 700;
   public final static int IMAGE_LONG_SIZE = 800;

   public final static int VideoWidth = 320;
   public final static int VideoHeight = 240;
//   public static ArrayList<Bitmap> mTestBmp = new ArrayList<Bitmap>();
   //end

   public static final int PIC_DISP_W = 320;
   public static final int PIC_DISP_H = 240;

   public static final int EDIT_LINE_HEIGHT = 24;

   public static int SCROLL_BOTTOM = 0;
   public static int SCROLL_UP = 1;
   public static int SCROLL_DOWN = 2;
   public static int SCROLL_STOP = 3;
   public static int SCROLL_TOP = 4;
   public static int SCROLL_PIXEL = 5;

   public static int SLIP_ACTIONUP = 0;
   public static int SLIP_ACTIONDOWN = 1;

   public static int DRAG_ACTION_VIEW = 0;
   public static int DRAG_ACTION_TOOLBUTTON = 1;


   public static int bottomMargin = 5;
   public static int minFocusLines = 4;
   public static int minNoFocusLines = 1;

   public static int AddViewFirst = 0;
   public static int AddViewLoad = 1;


   public static final int MIN_WIDTH = 50;
   public static final int MIN_HEIGHT = 50;
   public static final int MAX_WIDTH = 480;
   public static final int MAX_HEIGHT = 480;


   public final static int MINIMUM_SPACE = 50; //50M
   public final static int RECORD_MAX = 3600; //s  1 hour

   public final static String FIRST_DIARY_SUFFIX = "01";
   public final static int INIT_YEAR = 1900;
   public final static int MAX_DIARY_INDEX_IN_ONEDAY = 99;


   public static class childViewData {
      public String mViewName = null;
      public int mViewId = 0;
      public int mTextColor = Color.BLACK;
      public int mMarginTop = 0;
      public int width = 0;
      public int height = 0;
      public String mViewData = null;   //content
      public String mViewData2 = null;  //thumbnail
      public String mViewData3 = null;  //title
      public String mViewData4 = null;  //sharePlayUrl
      public int mViewData5 = 0; //courseScreenOrientation

      public childViewData() {
      }

      public childViewData(String name) {
         this.mViewName = name;
      }
   }

   public static class SplitText {
      public int mTextIndex = 0;
      public String mText = null;

      public void setTextIndex(int value) {
         mTextIndex = value;
      }

      public int getTextIndex() {
         return mTextIndex;
      }
      public void setText(String value) {
         mText = value;
      }

      public String getText() {
         return mText;
      }
      public SplitText() {

      }
   }

   public static class PageData {
      public int mCtrlId = 0;
      public int mStartLine = 0;
      public int mEndLine = 0;
      public int mHeight = 0;

      public void setCtrlId(int value) {
         mCtrlId = value;
      }

      public int getCtrlId() {
         return mCtrlId;
      }

      public void setStartLine(int value) {
         mStartLine = value;
      }

      public int getStartLine() {
         return mStartLine;
      }
      public void setEndLine(int value) {
         mEndLine = value;
      }

      public int getEndLine() {
         return mEndLine;
      }

      public void setHeight(int value) {
         mHeight = value;
      }

      public int getHeight() {
         return mHeight;
      }

      public PageData(int id, int start, int end ,int height) {
         this.mCtrlId = id;
         this.mStartLine = start;
         this.mEndLine = end;
         this.mHeight = height;
       }
   }

   public static class PageInfo {
      public int mPageNum = 0;
      public int mContentHeight = 0;
      public int mStartPixel = 0;
      public ArrayList<PageData> mPageData = null;

      public void setPageNum(int value) {
         mPageNum = value;
      }

      public int getPageNum() {
         return mPageNum;
      }

      public void setContentHeight(int value) {
         mContentHeight = value;
      }

      public int getContentHeight() {
         return mContentHeight;
      }
      public void setStartPixel(int value) {
         mStartPixel = value;
//         outLog("WMTEST****startPixel*****" + value);
      }

      public int getStartPixel() {
         return mStartPixel;
      }
      public PageInfo() {

      }
      public PageInfo(int num, int height, ArrayList<PageData> data) {
         this.mPageNum = num;
         this.mContentHeight = height;
         this.mPageData = data;
      }
   }


   public static void CreateLocalDiskPath(String path) {
      File folder = new File(path);
      if (!folder.exists()) {
         folder.mkdirs();
      }
   }

   public static void CreateLocalDiskPath(File folder) {
      if (!folder.exists()) {
         folder.mkdirs();
      }
   }

   public static void CreateLocalFile(String path) {
      File f = new File(path);
      try {
         if (!f.exists())
            f.createNewFile();
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   public static void CreateLocalFile(File f) {
      try {
         if (!f.exists())
            f.createNewFile();
      } catch (IOException e) {
         e.printStackTrace();
      }
   }
   public static String createNew(Context context, String pathname) {
      return createNew(context, pathname, false);
   }

   /**
    *
    * @param context
    * @param pathname
    * @param isReserveRoot
    * @return
    */
   public static String createNew(Context context, String pathname, boolean isReserveRoot){
      String rtn_path = null;
      String filePath = pathname;

      if (!filePath.endsWith(File.separator)) {
         filePath = filePath + File.separator;
      }
      File distFile = new File(filePath);
      if (distFile.exists()) {
         if (!isReserveRoot) {
            deleteDirectory(filePath);
         }
//         distFile.delete();
      }
      distFile.mkdirs();
      /*create picture folder*/
      String picFolderPath = filePath + SUB_IMAGE;
      distFile = new File(picFolderPath);
      distFile.mkdirs();
      /*create audio folder*/
      String audioFolderPath = filePath + SUB_RECORD;
      distFile = new File(audioFolderPath);
      distFile.mkdirs();
      /*create video folder*/
      String videoFolderPath = filePath + SUB_VIDEO;
      distFile = new File(videoFolderPath);
      distFile.mkdirs();

//      /*create thumb folder*/
//      String thumbFolderPath = filePath + SUB_THUMB;
//      distFile = new File(thumbFolderPath);
//      distFile.mkdirs();
      /*create preview folder*/
      String previewFolderPath = filePath + SUB_THUMB;
      distFile = new File(previewFolderPath);
      distFile.mkdirs();

//      mFilePath = filePath;
//      mNewFileName = filename;
      rtn_path = filePath;
      saveAssetsFileToLocal(context, filePath, "stylesheet.css");
      saveAssetsFileToLocal(context, picFolderPath, "micro_bar.jpg");
      saveAssetsFileToLocal(context, picFolderPath, "video_bar.jpg");

      return rtn_path;
   }

//   public String getNewFileName() {
//      return mNewFileName;
//   }

//   public synchronized static Bitmap LoadBitmap(String pathName, int outputW, int outputH, int rotate) {
//      BitmapFactory.Options options = new BitmapFactory.Options();
//      options.inJustDecodeBounds = true;
//      options.inScaled = false;
//      options.inPreferredConfig = Bitmap.Config.RGB_565;
//      options.inDither = false;
//      int width, height;
//      Bitmap bmp = null;
//
//      if(pathName == null || pathName.equals(""))
//         return null;
//
//      File file = new File(pathName);
//      if (!file.exists())
//         return null;
//
//      options.inJustDecodeBounds = true;
//      try {
//         BitmapFactory.decodeFile(pathName, options);
//      } catch (OutOfMemoryError e) {
//         return null;
//
//      }
//      width = options.outWidth;
//      height = options.outHeight;
//
//      if (width == 0 || height == 0)
//         return null;
//
//
//      options.inDither = true;
//      options.inJustDecodeBounds = false;
//
//      if (outputW != 0 && outputH == 0){
//         outputH = height * outputW / width;
//      } else if (outputW == 0 && outputH != 0) {
//         outputW = width * outputH / height;
//      }
//
//      if (outputW != 0 && outputH != 0) {
//         int ratio1 = 0;
//         float ratioSrc = (float)width / (float)height;
//         float ratioDes = (float)outputW / (float)outputH;
//         if (ratioSrc < ratioDes && height > outputH) {
//            outputW = outputH * width / height;
//         }
//         ratio1 = width / outputW;
//         options.inSampleSize = ratio1;
//      } else {
//         options.inSampleSize = 1;
//      }
//
//      try {
//         bmp = BitmapFactory.decodeFile(pathName, options);
//      } catch (OutOfMemoryError e) {
//         return null;
//      }
//      if (bmp == null) {
//         return null;
//      }
//
////      int orientation =  utils.getPicOrientation(pathName);
//      ExifInterface exif = null;
//      int orientation = 0;
//      try {
//         exif = new ExifInterface(pathName);
//      } catch (IOException e1) {
//         // TODO Auto-generated catch block
//         e1.printStackTrace();
//      }
//      if (exif != null) {
//         orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
//         switch(orientation) {
//            case ExifInterface.ORIENTATION_ROTATE_90:
//               orientation = 90;
//               break;
//            case ExifInterface.ORIENTATION_ROTATE_180:
//               orientation = 180;
//               break;
//            case ExifInterface.ORIENTATION_ROTATE_270:
//               orientation = 270;
//               break;
//            default:
//               orientation = 0;
//         }
//      }
////      if(rotate != 0 && bmp.getWidth() > bmp.getHeight()) {
////         rotate = 90;
////      } else
////         rotate = 0;
//      orientation += rotate;
//      orientation %= 360;
//
//      if (orientation != 0) {
//         Matrix matrix = new Matrix();
//         matrix.setRotate(orientation);
//
//         Bitmap bmp2 = null;
//         try {
//            bmp2 = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, false);
//         } catch (OutOfMemoryError e) {
//            // Log.e("ZiiPhoto","can not create bitmap, out of memory!");
//         }
//         if (bmp2 != null) {
//            bmp.recycle();
//            bmp = bmp2;
//         }
//      }
//
//      int bh = bmp.getHeight();
//      if (outputH > 0 && bh > outputH) {
//         outputW = outputH * bmp.getWidth() / bmp.getHeight();
//         Bitmap sbmp = null;
//         try {
//            sbmp = Bitmap.createBitmap(outputW, outputH, Bitmap.Config.RGB_565);
//         } catch (OutOfMemoryError e) {
//
//         }
//         if (sbmp != null) {
//            Canvas canvas = new Canvas(sbmp);
//            RectF dst = new RectF(0, 0, outputW, outputH);
//            canvas.drawBitmap(bmp, null, dst, null);
//            bmp.recycle();
//            bmp = sbmp;
//            canvas = null;
//         }
//      }
//
//      return bmp;
//
//   }


   public static String getMIMEType(File f) {
       String end = f.getName().substring(f.getName().lastIndexOf(".") + 1,
               f.getName().length()).toLowerCase();
       String type = "";
       if (end.equals("mp3") || end.equals("aac") || end.equals("wma")
               || end.equals("amr") || end.equals("wav") || end.equals("ogg")) {
           type = "audio";
       } else if (end.equals("jpg") || end.equals("bmp") || end.equals("png")
               || end.equals("jpeg")) {
           type = "image";
       } else if (end.equals("3gp") || end.equals("avi") || end.equals("mp4")|| end.equals("wmv")) {
           type = "video";
       } else {
           type = "*";
       }
       type += "/*";
       return type;
   }

   public static boolean deleteDirectory(String dir) {
      File dirFile = new File(dir);
      if (!dirFile.exists() || !dirFile.isDirectory()) {
         return false;
      }

      String path = dir.endsWith("/") ? dir.substring(0, dir.length() - 1)
              : dir;
      File file = new File(path);
      File to = new File(path + System.currentTimeMillis());
      if (file.renameTo(to)) {
         return deleteDir(to.getAbsolutePath());
      }
      return false;
   }

   private static boolean deleteDir(String dir) {
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
         }
         else {
            flag = deleteDir(files[i].getAbsolutePath());
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

   public static boolean deleteFile(String fileName) {
      File file = new File(fileName);
      if (file.isFile() && file.exists()) {
         file.delete();
         return true;
      } else {
         return false;
      }
   }



//   public synchronized static boolean copyFileByChannel(File f1,File f2) throws Exception {
//
//      long time = new Date().getTime();
//
////      int length=51200;
//      FileInputStream in=new FileInputStream(f1);
//      FileOutputStream out=new FileOutputStream(f2);
////      FileChannel inC=in.getChannel();
////      FileChannel outC=out.getChannel();
////      ByteBuffer b=null;
////      while(true){
////         if(inC.position()==inC.size()){
////            inC.close();
////            outC.close();
////            long t = new Date().getTime() - time;
////            Log.d("pp", "copy end "+t);
////            return true;
////         }
////         if((inC.size()-inC.position())<length){
////            length=(int)(inC.size()-inC.position());
////         }else {
////            length=51200;
////         }
////         try {
////            b=ByteBuffer.allocateDirect(length);
////         }catch (Exception e) {
////
////            return false;
////         }
////         inC.read(b);
////         b.flip();
////         outC.write(b);
////         outC.force(false);
////
////      }
////
//      try {
//         byte[] buffer = new byte[1024];
//         int length;
//         while ((length = in.read(buffer)) > 0) {
//            out.write(buffer, 0, length);
//         }
//         // Close the streams
//         out.flush();
//         out.close();
//         in.close();
//         long t = new Date().getTime() - time;
//         Log.d("pp", "<<<<<<<<<<<<<<<<<copy end " + t + ">>>>>>>>>>>>>>>>>>>>>>>>>");
//         return true;
//      } catch (Exception e) {
//         e.printStackTrace();
//         return false;
//      }
//
//   }

   public static String getFileNameFromPaperPath(String pathName) {
      if (pathName == null)
         return null;

      String paperName = null;
      if (pathName.endsWith(File.separator)) {
         pathName = pathName.substring(0, pathName.length() - 1);
      }
      int i = 0;
      i = pathName.lastIndexOf("/");
      if (i > 0)
         paperName = pathName .substring(i + 1, pathName.length());
      if (paperName != null && paperName.endsWith(PaperUtils.PATH_SUFFIX)) {
         paperName = paperName.substring(0, paperName.length() - PaperUtils.PATH_SUFFIX.length());
      }
      return paperName;
   }

   interface setEditModeListener{
   	void setEditMode();
   }

   /*Add for emotion 20111107*/


   /*
   private static String[] mEmotTexts = null;
   private static EmotionParser emotionParser = null;
   private final static int DEFAULT_SMILEY_TEXTS = R.array.default_emotion_texts;
   public static String[] getEmotionTexts(Context context) {
      if (mEmotTexts == null || mEmotTexts.length <= 0) {
         mEmotTexts = context.getResources().getStringArray(DEFAULT_SMILEY_TEXTS);
      }
      return mEmotTexts;
   }

   public static void textViewSetTextAndEmotion(Context context, View editText, CharSequence text) {
      if (emotionParser == null) {
         emotionParser = new EmotionParser(context);
      }
      ((TextView)editText).setText(emotionParser.replace(text));
   }

   public static class EmotionParser {
      private Context mContext;
      private Pattern mPattern;
      private String[] mETexts;
      private HashMap<String, Integer> mEmotionToRes;
      public static final int[] DEFAULT_EMOTION_RES_ID = {
         R.drawable.emotion_000,         R.drawable.emotion_001,         R.drawable.emotion_002,
         R.drawable.emotion_003,         R.drawable.emotion_004,         R.drawable.emotion_005,
         R.drawable.emotion_006,         R.drawable.emotion_007,         R.drawable.emotion_008,
         R.drawable.emotion_009,         R.drawable.emotion_010,         R.drawable.emotion_011,
         R.drawable.emotion_012,         R.drawable.emotion_013,         R.drawable.emotion_014,
         R.drawable.emotion_015,         R.drawable.emotion_016,         R.drawable.emotion_017,
         R.drawable.emotion_018,         R.drawable.emotion_019,         R.drawable.emotion_020,
         R.drawable.emotion_021,         R.drawable.emotion_022,         R.drawable.emotion_023,
         R.drawable.emotion_024,         R.drawable.emotion_025,         R.drawable.emotion_026,
         R.drawable.emotion_027,         R.drawable.emotion_028,         R.drawable.emotion_029,
         R.drawable.emotion_030,         R.drawable.emotion_031,         R.drawable.emotion_032,
         R.drawable.emotion_033,         R.drawable.emotion_034,         R.drawable.emotion_035,
         R.drawable.emotion_036,         R.drawable.emotion_037,         R.drawable.emotion_038,
         R.drawable.emotion_039,         R.drawable.emotion_040,         R.drawable.emotion_041,
         R.drawable.emotion_042,         R.drawable.emotion_043,         R.drawable.emotion_044,
         R.drawable.emotion_045,         R.drawable.emotion_046,         R.drawable.emotion_047,
         R.drawable.emotion_048,         R.drawable.emotion_049,         R.drawable.emotion_050,
         R.drawable.emotion_051
      };

      public EmotionParser(Context context) {
          mContext = context;
          mETexts = getEmotionTexts(context);
          mEmotionToRes = buildEmotionToRes();
          mPattern = buildPattern();
      }


      private HashMap<String, Integer> buildEmotionToRes() {
          if (DEFAULT_EMOTION_RES_ID.length != mETexts.length) {
              throw new IllegalStateException("Emotion resource ID/text mismatch");
          }

          HashMap<String, Integer> emotionToRes = new HashMap<String, Integer>(mETexts.length);
          for (int i = 0; i < mETexts.length; i++) {
             emotionToRes.put(mETexts[i], DEFAULT_EMOTION_RES_ID[i]);
          }

          return emotionToRes;
      }


      private Pattern buildPattern() {
          StringBuilder patternString = new StringBuilder(mETexts.length * 3);
          patternString.append('(');
          for (String s : mETexts) {
              patternString.append(Pattern.quote(s));
              patternString.append('|');
          }
          patternString.replace(patternString.length() - 1, patternString.length(), ")");

          return Pattern.compile(patternString.toString());
      }

      //Replace emotText to ImageSpan
      public CharSequence replace(CharSequence text) {
         if (text == null) {
            return null;
         }
          SpannableStringBuilder builder = new SpannableStringBuilder(text);
          Matcher matcher = mPattern.matcher(text);
          while (matcher.find()) {
              int resId = mEmotionToRes.get(matcher.group());
              builder.setSpan(new ImageSpan(mContext, resId),matcher.start(), matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
          }
          return builder;
      }
  }

  */
   /*Add for emotion end*/




   public static void saveAssetsFileToLocal(Context context, String savePath, String FileName) {
      try {
         InputStream myInput = null;
         myInput = context.getAssets().open(FileName);
         OutputStream myOutput = null;
         File outFile = new File(savePath, FileName);

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


   public static int haveSpace() {
      long ret = readSDCard();
      int value = 0;
      long temp = ret / 1024 / 1024;
     if (temp > MINIMUM_SPACE)
         value = 0;
      else if(ret == -1)
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
         long freespace = availCount * blockSize ;/// 1024 / 1024;
         return freespace;
      } else {
         return -1;
      }

   }

   private static Toast mToast = null;
   public static void showMessage(Context context, String msg) {
      if (mToast != null) {
         mToast.cancel();
         mToast = null;
      }
      mToast = new Toast(context);
//      LayoutInflater inflate = (LayoutInflater) context
//              .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//      View v = inflate.inflate(R.layout.message_view, null);
//      TextView tv = (TextView) v.findViewById(R.id.msg);
//      tv.setText(msg);
//      mToast.setView(v);
//      mToast.setGravity(Gravity.CENTER, 0, 0);
//      mToast.setDuration(Toast.LENGTH_SHORT);
//      mToast.show();
      //use default style
      mToast.makeText(context, msg, Toast.LENGTH_LONG).show();

   }

   public static Date getDateByDiaryTitle(String title) {
//      String s_date = title.substring(0,
//              title.length() - FIRST_DIARY_SUFFIX.length());
//      int date = Integer.parseInt(s_date);
//      int day = date % 100;
//      date = date / 100;
//      int month = date % 100 - 1;
//      int year = date / 100 ;
//      if (year > 1900) {
//         year -= 1900;
//      }
//      Date d = new Date(year, month, day);
      long dateMSec = Long.parseLong(title);
      Date d = new Date(dateMSec);
      return d;
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

   public synchronized static Bitmap loadBitmap(String pathName, int outputW,
                                                int outputH, int rotate) {
      BitmapFactory.Options options = new BitmapFactory.Options();
      options.inJustDecodeBounds = true;
      options.inScaled = false;
      options.inPreferredConfig = Bitmap.Config.ARGB_8888;
      options.inDither = false;
      int width, height;
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

      if (outputW != 0 && outputH == 0) {
         outputH = height * outputW / width;
      } else if (outputW == 0 && outputH != 0) {
         outputW = width * outputH / height;
      }

      if (outputW != 0 && outputH != 0) {
         int ratio1 = 0;
         float ratioSrc = (float) width / (float) height;
         float ratioDes = (float) outputW / (float) outputH;
         if (ratioSrc < ratioDes && height > outputH) {
            outputW = outputH * width / height;
         }
         ratio1 = width / outputW;
         options.inSampleSize = ratio1;
      } else {
         options.inSampleSize = 1;
      }
      try {
         bmp = BitmapFactory.decodeFile(pathName, options);
      } catch (OutOfMemoryError e) {
         return null;
      }
      if (bmp == null) {
         return null;
      }

      // int orientation = utils.getPicOrientation(pathName);
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
      orientation += rotate;
      orientation %= 360;

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

      int bh = bmp.getHeight();
      if (outputH > 0 && bh > outputH) {
         outputW = outputH * bmp.getWidth() / bmp.getHeight();
         Bitmap sbmp = null;
         try {
            sbmp = Bitmap.createBitmap(outputW, outputH,
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

      return bmp;

   }

   public synchronized static Bitmap loadBitmap(String pathName, int largeSide,
                                                int shortSide) {
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
      if (largeSide <= 0) {
         largeSide = (int) (width > height ? width : height);
      }

      options.inDither = true;
      options.inJustDecodeBounds = false;

      float outputW = width;
      float outputH = height;
      float zoomFit = 1.0f;
      double zoomGal = -1.0f;

      if (largeSide != 0) {
         if (width > height) {
            outputW = largeSide;
            outputH = (int) (height * outputW / width);
            if (shortSide > 0 && outputH > shortSide) {
               outputH = shortSide;
               outputW = (int) (width * outputH / height);
            }
         } else {
            outputH = largeSide;
            outputW = (int) (width * outputH / height);
            if (shortSide > 0 && outputW > shortSide) {
               outputW = shortSide;
               outputH = (int) (height * outputW / width);
            }
         }
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

         String memStr = "alloc memory: " + Runtime.getRuntime().totalMemory()
                 + "; free memory:" + Runtime.getRuntime().freeMemory()
                 + "; max memory: " + Runtime.getRuntime().maxMemory();
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

      return bmp;
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
    * writeToCache : write bitmap to path as JPG file
    */
   public static boolean writeToCacheJPEG(Bitmap bitmap, String path) {
      boolean rtn = false;
      if (bitmap != null && path != null) {
         rtn = writeToCacheJPEG(bitmap, path, 85);
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
            if (dstFile.exists())
               dstFile.delete();

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

   public static String getDate(int day, int month, int year) {
      String sMonth, sDay;
      if(month < 10)
         sMonth = "0" + String.valueOf(month);
      else
         sMonth = String.valueOf(month);

      if(day < 10)
         sDay = "0" + String.valueOf(day);
      else
         sDay = String.valueOf(day);

      if(year > 1900)
         year = year - INIT_YEAR;

      String str = String.valueOf(year) + sMonth + sDay;
      return str;

   }

   public static String getNewDiaryTitleInDate(Context context, String dateStr) {
      String diaryTitle = dateStr + FIRST_DIARY_SUFFIX;
      Cursor cursor = null;
//      if(Utils.getDiaryDB(context) != null) {
//         cursor = Utils.getDiaryDB(context).getDiaryByDate(dateStr);
//         int count = cursor.getCount();
//         if (cursor != null && count > 0) {
//            String lastDiaryTitle = cursor.getString(cursor
//                    .getColumnIndex(DbUtils.KEY_DIARYNAME));
//            int lastId = Integer.parseInt(lastDiaryTitle);
//            int index = lastId % 100;
//            if (index >= MAX_DIARY_INDEX_IN_ONEDAY) {
//               diaryTitle = null;
//               showMessage(context.getString(R.string.diaries_limit_per_day),
//                       context);
//            } else if (lastId > 0) {
//               diaryTitle = String.valueOf(lastId + 1);
//            }
//         }
//         if(cursor != null) {
//            cursor.close();
//         }
//      }
      return diaryTitle;
   }

   public static String translatePaperChildTypeIdToViewName(String idStr) {
      int id = Integer.parseInt(idStr);
      if (id >= 0 && id < TYPE_GROUP.length) {
         return TYPE_GROUP[id];
      }
      return null;
   }

   public static String translatePaperChildViewNameToTypeId(String viewName) {
      int id = -1;
      for (int i = 0; i < TYPE_GROUP.length; i++) {
         if (TYPE_GROUP[i].equals(viewName)) {
            id = i;
            break;
         }
      }
      if (id >= 0 && id < TYPE_GROUP.length) {
         return String.valueOf(id);
      }
      return null;
   }

   public static String getFile(String url, final String filePath) {
      URL newurl = null;
      try {
         url +="?"+ System.currentTimeMillis();
         newurl = new URL(url);

         URLConnection conn = newurl.openConnection();
         int fileSize = conn.getContentLength();

//			if (fileSize <= 0) {
//				return null;
//			}

         conn.setConnectTimeout(60 * 1000);

         InputStream is = conn.getInputStream();

         File output = new File(filePath);
         if (output.exists()) {
            output.delete();
         }
         if (!output.getParentFile().exists()) {
            output.getParentFile().mkdirs();
         }
         output.createNewFile();
         FileOutputStream out = new FileOutputStream(output);
         int ch;
         byte[] buffer = new byte[1024];
         // read (ch) bytes into buffer
         while ((ch = is.read(buffer)) > 0) {
            // write (ch) byte from buffer at the position 0
            out.write(buffer, 0, ch);
            out.flush();
         }
         out.close();
         return filePath;
      } catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }

      return null;
   }


   public static String getFileExt(String filename) {
      String extName = null;
      int pos = filename.lastIndexOf('.');
      if (pos >= 0) {
         extName = filename.substring(pos, filename.length());
      }
      return extName;
   }


   public static void outLog(String tag, String msg) {
	  if(MediaPaper.LOG_OPEN)
		  Log.d("MediaPaper", tag + " >>>>>>>>>>>>>>>>>> " + msg);
   }

}
