package com.lqwawa.libs.videorecorder;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;

public class VideoUtils {

    private static boolean DEBUG = true;

    public static void enableLogging(boolean logging) {
        DEBUG = logging;
    }

    public static void log(String tag, String info) {
        if (DEBUG) {
            Log.i("VideoRecorder", tag + "---->----" + info);
        }
    }

    public static Bitmap getVideoThumbnail(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return null;
        }
        File file = new File(filePath);
        if (!file.exists() || file.length() <= 0) {
            return null;
        }
        
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(filePath);
            bitmap = retriever.getFrameAtTime();
        } catch(IllegalArgumentException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        if (bitmap != null && bitmap.isRecycled()) {
            bitmap = null;
        }
        return bitmap;
    }

}
