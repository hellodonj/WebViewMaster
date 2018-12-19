package com.galaxyschool.app.wawaschool.course;
import android.app.Activity;
import android.text.TextUtils;

import com.lecloud.xutils.cache.MD5FileNameGenerator;
import com.lqwawa.lqbaselib.net.FileApi;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: wangchao
 * @date: 2017/08/29 16:18
 */

public class CacheCourseImagesTask extends MyAsyncTask<List<String>> {
    List<String> paths;
    String downloadPath;
    String title;

    public CacheCourseImagesTask(Activity activity, List<String> paths, String downloadPath,
                                 String title) {
        super(activity);
        this.paths = paths;
        this.downloadPath = downloadPath;
        this.title = title;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected List<String> doInBackground(Void... params) {
        File downloadDirFile = new File(downloadPath);
        if (!downloadDirFile.exists()) {
            downloadDirFile.mkdirs();
        }
        List<String> imagePaths = new ArrayList<String>();
        for (String path : paths) {
            if (!TextUtils.isEmpty(path)) {
                path = checkImageUrl(path);
                String imageFilename = new MD5FileNameGenerator().generate(path);
                if (!TextUtils.isEmpty(imageFilename)) {
                    imageFilename = imageFilename + ".jpg";
                    File downloadImageFile = new File(downloadDirFile, imageFilename);
                    if (downloadDirFile != null) {
                        String imagePath = downloadImageFile.getAbsolutePath();
                        File imageFile = new File(imagePath);
                        if (!imageFile.exists()) {
                            FileApi.getFile(path, imagePath);
                        }
                        if (imageFile.exists() && imageFile.canRead()) {
                            imagePaths.add(imagePath);
                        }
                    }
                }
            }
        }
        return imagePaths;
    }

    @Override
    protected void onPostExecute(List<String> imagePaths) {
        super.onPostExecute(imagePaths);
    }

    private String checkImageUrl(String imageUrl) {
        if (TextUtils.isEmpty(imageUrl)) {
            return null;
        }
        String result = imageUrl;
        if (imageUrl.contains("?")) {
            int index = imageUrl.lastIndexOf("?");
            if (index > 0) {
                result = imageUrl.substring(0, index);
            }
        }
        return result;
    }
}
