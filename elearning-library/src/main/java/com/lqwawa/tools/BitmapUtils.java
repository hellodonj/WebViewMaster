package com.lqwawa.tools;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.text.TextUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class BitmapUtils {
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

    public synchronized static Bitmap loadBitmap(
        String pathName, int outputW,
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
            orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
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
                bmp2 = Bitmap.createBitmap(
                    bmp, 0, 0, bmp.getWidth(),
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
                sbmp = Bitmap.createBitmap(
                    outputW, outputH,
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

    public synchronized static Bitmap loadBitmap(
        String pathName, int largeSide,
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
            orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
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
                bmp2 = Bitmap.createBitmap(
                    bmp, 0, 0, bmp.getWidth(),
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
                sbmp = Bitmap.createBitmap(
                    (int) outputW, (int) outputH,
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


    public static void saveBitmap(Bitmap bitmap, String path) {
        if (bitmap == null || TextUtils.isEmpty(path)) {
            return;
        }
        File f = new File(path);
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取bitmap
     *
     * @param is        输入流
     * @param maxWidth  最大宽度
     * @param maxHeight 最大高度
     * @return bitmap
     */
    public static Bitmap getBitmap(final InputStream is, final int maxWidth, final int maxHeight) throws IOException {
        if (is == null) return null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        byte[] data = inputStream2ByteArr(is);//将InputStream转为byte数组，可以多次读取
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
        options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight);
        options.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
        return bitmap;
    }

    /**
     * 计算采样大小
     *
     * @param options   选项
     * @param maxWidth  最大宽度
     * @param maxHeight 最大高度
     * @return 采样大小
     */
    private static int calculateInSampleSize(final BitmapFactory.Options options, final int maxWidth, final int maxHeight) {
        if (maxWidth == 0 || maxHeight == 0) return 1;
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;
        while ((height >>= 1) > maxHeight && (width >>= 1) > maxWidth) {
            inSampleSize <<= 1;
        }
        return inSampleSize;
    }

    private static byte[] inputStream2ByteArr(InputStream inputStream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buff = new byte[1024*2];
        int len = 0;
        while ( (len = inputStream.read(buff)) != -1) {
            outputStream.write(buff, 0, len);
        }
        inputStream.close();
        outputStream.close();
        return outputStream.toByteArray();
    }

}
