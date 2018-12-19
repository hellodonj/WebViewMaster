package com.galaxyschool.app.wawaschool.course;

import android.app.Activity;
import android.text.TextUtils;

import com.galaxyschool.app.wawaschool.bitmapmanager.Md5FileNameGenerator;
import com.galaxyschool.app.wawaschool.common.LogUtils;
import com.galaxyschool.app.wawaschool.common.Utils;
import com.lqwawa.lqbaselib.net.FileApi;
import com.lqwawa.client.pojo.ResourceInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: wangchao
 * @date: 2017/08/29 16:22
 */

public class CacheCloudImageTask extends MyAsyncTask<List<String>>{
    List<ResourceInfo> resourceInfos;
    String title;

    public CacheCloudImageTask(Activity activity, List<ResourceInfo> resourceInfos, String title) {
        super(activity);
        this.resourceInfos = resourceInfos;
        this.title = title;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected List<String> doInBackground(Void... params) {
        List<String> paths = new ArrayList<String>();
        File downloadfile = new File(Utils.PIC_TEMP_FOLDER);
        if (!downloadfile.exists()) {
            downloadfile.mkdirs();
        }
        if (resourceInfos != null && resourceInfos.size() > 0) {
            for (ResourceInfo info : resourceInfos) {
                if (info != null && !TextUtils.isEmpty(info.getImgPath())) {
                    String imagePath = checkImageUrl(info.getImgPath());
                    String filename = Md5FileNameGenerator.generate(imagePath);
                    File file = new File(Utils.PIC_TEMP_FOLDER, filename);
                    if (file.exists() && file.canRead()) {
                        paths.add(file.getAbsolutePath());
                    } else {
                        String path = FileApi.getFile(imagePath, file.getAbsolutePath());
                        if (!TextUtils.isEmpty(path) && new File(path).exists()) {
                            paths.add(path);
                        }
                    }
                }
            }
        }
        return paths;
    }

    @Override
    protected void onPostExecute(List<String> paths) {
        super.onPostExecute(paths);
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
