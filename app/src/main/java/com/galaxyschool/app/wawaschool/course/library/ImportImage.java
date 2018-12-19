package com.galaxyschool.app.wawaschool.course.library;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.common.DateUtils;
import com.galaxyschool.app.wawaschool.common.Utils;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseType;
import com.galaxyschool.app.wawaschool.pojo.weike.LocalCourseInfo;

import java.io.File;
import java.io.FileFilter;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class ImportImage {
   /** Message */
   public static final int MSG_IMPORT_FINISH = 202;
   private static int total = 1;
   private static String mSaveFolder;

    public static void ImportImageRun(Context context, String memberId,
                                      final List<String> paths, final ProgressDialog progress,
                                      final Handler dHandler, String destPath, String title) {
        mSaveFolder = Utils.getUserCourseRootPath(memberId, CourseType.COURSE_TYPE_IMPORT, false);
        if (!TextUtils.isEmpty(destPath)) {
//            mSaveFolder = new File(destPath).getParent() + File.separator;
            mSaveFolder = destPath;
        }
        LocalCourseInfo info = coverToImportedFolder(paths, progress);
        if (info != null) {
            info.mTitle = title;
        }
        Message dmsg = new Message();
        dmsg.what = MSG_IMPORT_FINISH;
        dmsg.obj = info;
        dHandler.sendMessage(dmsg);
    }


    public static LocalCourseInfo coverToImportedFolder(final List<String> paths,
                                                        final ProgressDialog progress) {

        if (paths == null || paths.size() == 0)
            return null;
        total = paths.size();

        int largeSide = Utils.IMPORT_PAGE_SIZE_WIDTH;
        int shortSide = Utils.IMPORT_PAGE_SIZE_HEIGHT;


        String folderPath = mSaveFolder + DateUtils.millSecToDateStr(System.currentTimeMillis())
                + File.separator;

        if(!new File(folderPath).exists()) {
            Utils.createLocalDiskPath(folderPath);
        }

        // create empty
        LocalCourseInfo info = new LocalCourseInfo(folderPath, mSaveFolder,
            0, total, new Date().getTime());

        if (progress != null)
            progress.setMax(total);

        for (int i = 0; i < total; i++) {

            Bitmap bmp = Utils.loadBitmap(paths.get(i), largeSide, shortSide, false);

            if(bmp!=null) {
                String jpgpath = folderPath + Utils.PDF_PAGE_NAME + (i+1) + ".jpg";

                boolean rtn = Utils.writeToCacheJPEG(bmp, jpgpath);
                if (i == 0) {
                    int thumbWidth, thumbHeight = MyApplication.getHPixels() >> 2;
                    if (thumbHeight < Utils.DEFAULT_THUMB_HEIGHT) {
                        thumbHeight = Utils.DEFAULT_THUMB_HEIGHT;
                    } else if (thumbHeight > bmp.getHeight()){
                        thumbHeight = bmp.getHeight();
                    }
                    thumbWidth = thumbHeight * bmp.getWidth()/bmp.getHeight();

                    Bitmap thumb = Bitmap.createScaledBitmap(bmp, thumbWidth, thumbHeight, false);

                    Utils.writeToCacheJPEG(thumb, folderPath + Utils.PDF_THUMB_NAME);

                    thumb.recycle();
                }
                bmp.recycle();
            }
            if(progress!=null)
                progress.setProgress(i);
        }

        return info;
    }
}