package com.lqwawa.intleducation.base.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by shouyi on 8/12/16.
 */
public class BitmapUtils {

    //shouyi add
    public synchronized static Bitmap loadBitmap(String pathName, int largeSide,
                                                 int shortSide) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inScaled = false;
        options.inDither = false;
        float outputW, outputH;
        Bitmap bmp = null;

        if (pathName == null || pathName.equals("")) {
            return null;
        }

        File file = new File(pathName);
        if (!file.exists()) {
            return null;
        }

        options.inJustDecodeBounds = true;
        options.inPreferQualityOverSpeed = true;
        try {
            BitmapFactory.decodeFile(pathName, options);
        } catch (OutOfMemoryError e) {
            return null;
        }
        outputW = options.outWidth;
        outputH = options.outHeight;
        if (outputW == 0 || outputH == 0) {
            return null;
        }

        options.inDither = true;
        options.inJustDecodeBounds = false;
        options.inSampleSize = calculateInSampleSize(options, largeSide, shortSide);//ratio1;
//        options.inSampleSize = calculateInSampleSizeByPixels(options, largeSide * shortSide);
        bmp = decodeFileRecursively(pathName, options, 2);
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

        return bmp;
    }

    private static Bitmap decodeFile(String pathName, BitmapFactory.Options options) {
        Bitmap bmp = null;
        try {
            System.gc();
            bmp = BitmapFactory.decodeFile(pathName, options);
        } catch (OutOfMemoryError e) {

            String memStr = "alloc memory: " + Runtime.getRuntime().totalMemory()
                    + "; free memory:" + Runtime.getRuntime().freeMemory()
                    + "; max memory: " + Runtime.getRuntime().maxMemory();
            BaseUtils.outLog("decodeBitmap OutOfMemoryError", memStr);
            BaseUtils.outLog("decodeBitmap OutOfMemoryError",
                    "options.inSampleSize=" + options.inSampleSize + " bw=" + options.outWidth
                            + " bh=" + options.outHeight);
        }
        return bmp;
    }

    public static Bitmap decodeFileRecursively(String pathName, BitmapFactory.Options options, int maxRedecodeCount) {
        if (TextUtils.isEmpty(pathName) || options == null) {
            return null;
        }

        Bitmap bmp = decodeFile(pathName, options);
        while (maxRedecodeCount > 0 && bmp == null) {
            options.inSampleSize *= 2;
            bmp = decodeFile(pathName, options);
            maxRedecodeCount--;
        }

        return bmp;
    }

    //shouyi add
    //get max inSampleSize which not extends request side twice.
    //to keep high quality
    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (reqWidth > 0 || reqHeight > 0) {
            if (reqWidth == 0) {
                reqWidth = reqHeight;
            }
            if (reqHeight == 0) {
                reqHeight = reqWidth;
            }
            if (height > reqHeight || width > reqWidth) {
                int halfHeight = height / 2;
                int halfWidth = width / 2;

                // Calculate the largest inSampleSize value that is a power of 2 and
                // keeps both
                // height and width larger than the requested height and width.
                while ((halfHeight / inSampleSize) > reqHeight
                        || (halfWidth / inSampleSize) > reqWidth) {
                    inSampleSize *= 2;
                }
            }
        }

        return inSampleSize;
    }

    //shouyi add
    //get max inSampleSize which not extends request side twice.
    //to keep high quality
    public static int calculateInSampleSizeByPixels(BitmapFactory.Options options,
                                                    long maxPixels) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (maxPixels > 0) {
            long pCount = height * width / 2;
            // Calculate the largest inSampleSize value that is a power of 2 and
            // keeps both
            // height and width larger than the requested height and width.
            while (pCount > maxPixels) {
                inSampleSize *= 2;
                pCount /= 2;
            }
        }

        return inSampleSize;
    }
}
