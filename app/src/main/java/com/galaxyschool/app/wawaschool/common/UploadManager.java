package com.galaxyschool.app.wawaschool.common;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.net.course.UserApis;
import com.galaxyschool.app.wawaschool.pojo.UploadParameter;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseData;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseUploadResult;
import com.galaxyschool.app.wawaschool.pojo.weike.MediaUploadList;
import com.lqwawa.client.pojo.MediaType;
import com.lqwawa.libs.mediapaper.PaperUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: wangchao
 * Time: 2015/10/30 16:31
 */
public class UploadManager {

    private Context context;
    private static UploadManager instance = null;
    private Map<String, Thread> threadList = new HashMap<String, Thread>();
    private boolean isUploading = false;

    public static UploadManager getDefault(Context context) {
        if (instance == null) {
            instance = new UploadManager(context);
        }
        return instance;
    }

    private UploadManager(Context context) {
        this.context = context;
    }

    public void uploadResource(
        final Activity activity, final UploadParameter uploadParameter, CallbackListener listener) {
        if (uploadParameter != null) {
            String filePath = null;
            if (!TextUtils.isEmpty(uploadParameter.getZipFilePath())) {
                filePath = uploadParameter.getZipFilePath();
            }
            if (!TextUtils.isEmpty(uploadParameter.getFilePath())) {
                filePath = uploadParameter.getFilePath();
            }
            if (TextUtils.isEmpty(filePath)) {
                return;
            }
            if (threadList.containsKey(filePath)) {
                if(activity != null) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TipMsgHelper.ShowLMsg(activity, R.string.uploading_file);
                        }
                    });
                }
                return;
            } else {
                UploadResourceThread uploadResourceThread = new UploadResourceThread(activity,
                        uploadParameter, filePath, listener);
                uploadResourceThread.start();
                threadList.put(filePath, uploadResourceThread);
            }
        }
    }

    public void uploadMedia(UploadParameter uploadParameter, CallbackListener listener) {
        if (!isUploading) {
            isUploading = true;
            UploadMediaThread uploadMediaThread = new UploadMediaThread(uploadParameter, listener);
            uploadMediaThread.start();
        }
    }


    private class UploadResourceThread extends Thread {
        Activity mActivity = null;
        UploadParameter mUploadParameter;
        String mFilePath;
        CallbackListener mListener;

        public UploadResourceThread(
            Activity activity, UploadParameter uploadParameter, String filePath,
            CallbackListener listener) {
            mActivity = activity;
            mUploadParameter = uploadParameter;
            mFilePath = filePath;
            mListener = listener;
        }

        @Override
        public void run() {
            super.run();
            CourseUploadResult upload_result = null;
            try {
                File zipFile = null;
                if (!TextUtils.isEmpty(mUploadParameter.getZipFilePath())) {
                    zipFile = new File(mUploadParameter.getZipFilePath());
                }
                if (zipFile == null || !zipFile.exists()) {
                    File rsc = new File(mUploadParameter.getFilePath());
                    zipFile = zipFile(rsc, mUploadParameter.getFileName());
                }
                if (zipFile.exists()) {
                    String path = zipFile.getPath();
                    long size = new File(path).length();
                    mUploadParameter.setZipFilePath(path);
                    mUploadParameter.setSize(size);
                    upload_result = UserApis.uploadResource(
                            mActivity, mUploadParameter);
                    if (!TextUtils.isEmpty(mFilePath)) {
                        threadList.remove(mFilePath);
                    }
                    if (upload_result != null && upload_result.code == 0) {
                        if (upload_result.data != null && upload_result.data.size() > 0) {
                            CourseData courseData = upload_result.data.get(0);
                            if (courseData != null) {

                            }
                        }
                    }
                }
            } catch (Exception e) {
                if (!TextUtils.isEmpty(mFilePath)) {
                    threadList.remove(mFilePath);
                }
                e.printStackTrace();
            }
            if (mListener != null) {
                mListener.onBack(upload_result);
            }
        }
    }


    private class UploadMediaThread extends Thread {
        private UploadParameter uploadParameter;
        private CallbackListener listener;

        public UploadMediaThread(UploadParameter uploadParameter, CallbackListener listener) {
            this.uploadParameter = uploadParameter;
            this.listener = listener;
        }

        @Override
        public void run() {
            if (uploadParameter != null) {
                if (uploadParameter.getMediaType() == MediaType.PICTURE) {
                    resizeImage(uploadParameter.getPaths());
                }
            }
            MediaUploadList dataResult = UserApis.uploadMedia(context, uploadParameter);
            if(dataResult != null && dataResult.code == 0) {
                if (uploadParameter.getMediaType() == MediaType.PICTURE) {
                    deleteTempFiles(uploadParameter.getPaths());
                }
            }
            isUploading = false;
            if (listener != null) {
                listener.onBack(dataResult);
            }
        }
    }

    public static void resizeImage(List<String> paths) {
        if (paths != null && paths.size() > 0) {
            for (int i = 0, size = paths.size(); i < size; i++) {
                String path = paths.get(i);
                if (!TextUtils.isEmpty(path)) {
                    Bitmap bmp = PaperUtils.loadBitmap(path, Utils.IMAGE_LONG_SIZE, 0);
                    if (bmp != null && !bmp.isRecycled()) {
                        String filename = Utils.getFileNameFromPath(path);
                        filename = filename.substring(0, filename.lastIndexOf(".")) + ".jpg";
                        String cachePath = Utils.PICTURE_FOLDER + filename;
                        boolean rtn = PaperUtils.writeToCacheJPEG(bmp, cachePath, 90);
                        paths.set(i, cachePath);
                    }
                }
            }
        }
    }

    public static void deleteTempFiles(List<String> paths) {
        if (paths != null && paths.size() > 0) {
            for (int i = 0, size = paths.size(); i < size; i++) {
                String path = paths.get(i);
                if (!TextUtils.isEmpty(path) && new File(path).exists()) {
                    Utils.deleteFile(path);
                }
            }
        }
    }

    private File zipFile(File rsc, String fileName) {
        boolean rtn = false;
        File zipFile = null;

        String zipFileName = Utils.getFileTitle(fileName + Utils.COURSE_SUFFIX, Utils.TEMP_FOLDER, Utils.COURSE_SUFFIX);
        zipFile = new File(Utils.TEMP_FOLDER, zipFileName);
        File parent = new File(Utils.TEMP_FOLDER);
        if (!parent.exists()) {
            parent.mkdirs();
        }
        if (zipFile.exists()) {
            zipFile.delete();
        }
        try {
            zipFile.createNewFile();
            rtn = Utils.zip(rsc, zipFile);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (rtn) {
            return zipFile;
        } else {
            if (zipFile.exists()) {
                zipFile.delete();
            }
            return null;
        }
    }
}
