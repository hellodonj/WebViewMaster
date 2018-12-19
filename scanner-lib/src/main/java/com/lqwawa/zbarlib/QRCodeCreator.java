package com.lqwawa.zbarlib;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;


public class QRCodeCreator {
   
   public static int QRCODE_IMAGE_SIZE = 400;

    public static Bitmap encodeAsBitmap(String contents, BarcodeFormat format,
            int desiredWidth, int desiredHeight) throws WriterException {
        final int WHITE = 0xFFFFFFFF; //可以指定其他颜色，让二维码变成彩色效果
        final int BLACK = 0xFF000000;
        
        HashMap<EncodeHintType, Object> hints = null;
        String encoding = guessAppropriateEncoding(contents);
        if (encoding != null) {
            hints = new HashMap<EncodeHintType, Object>();
            hints.put(EncodeHintType.CHARACTER_SET, encoding);
        }
        if (hints == null) {
            hints = new HashMap<EncodeHintType, Object>();
        }
        hints.put(EncodeHintType.MARGIN, Integer.valueOf(1));

        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix result = writer.encode(contents, format, desiredWidth,
                desiredHeight, hints);
        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        // All are 0, or black, by default
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    private static String guessAppropriateEncoding(CharSequence contents) {
        // Very crude at the moment
        for (int i = 0; i < contents.length(); i++) {
            if (contents.charAt(i) > 0xFF) {
                return "UTF-8";
            }
        }
        return null;
    }
    
    @SuppressLint("NewApi")
    public static class QRCodeCreateThread extends AsyncTask<Void, Void, String> {
       QRCodeCreateFinishHandler mQRCodeCreateFinishHandler = null;
       String mUrl = null;
       String mSavePath = null;
       Bitmap mQRCodeBmp = null;
       
       public QRCodeCreateThread(Context context, String url, String savePath, QRCodeCreateFinishHandler QRCodeCreateFinishHandler) {
          mUrl = url;
          mSavePath = savePath;
          mQRCodeCreateFinishHandler = QRCodeCreateFinishHandler;
       }

       @Override
       protected String doInBackground(Void... params) {
          // TODO Auto-generated method stub
          boolean bSuccess = false;
          File QRCodeFile = null;
           if (mSavePath != null) {
               QRCodeFile = new File(mSavePath);

               if (!QRCodeFile.getParentFile().exists()) {
                   QRCodeFile.getParentFile().mkdirs();
               }
               if (QRCodeFile.exists()) {
                   QRCodeFile.delete();
               }
           }
          try {
             mQRCodeBmp = encodeAsBitmap(mUrl, BarcodeFormat.QR_CODE,
                  QRCODE_IMAGE_SIZE, QRCODE_IMAGE_SIZE);
            if (mQRCodeBmp != null && QRCodeFile != null) {
               bSuccess = writeToCacheJPEG(mQRCodeBmp, QRCodeFile.getPath());
            }
         } catch (WriterException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
          if (bSuccess) {
             return QRCodeFile.getPath();
          } else {
             return null;
          }
       }
       
       @Override
      protected void onPostExecute(String QRCodeFilePath) {
         // TODO Auto-generated method stub
         super.onPostExecute(QRCodeFilePath);
         if (mQRCodeCreateFinishHandler != null) {
            mQRCodeCreateFinishHandler.onQRCodeCreate(mQRCodeBmp, QRCodeFilePath);
         }
      }
       
       
    }
    

    public interface QRCodeCreateFinishHandler {
       public void onQRCodeCreate(Bitmap QRCodeBmp, String filePath);
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
}
