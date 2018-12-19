package com.galaxyschool.app.wawaschool.common;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;

import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.course.MyAsyncTask;
import com.lqwawa.lqbaselib.net.FileApi;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
  * ================================================
  * 作    者：Blizzard-liu
  * 版    本：1.0
  * 创建日期：2017/11/17 14:49
  * 描    述：处理头像上传
  * 修订历史：
  * ================================================
  */

public final class UploadImageTask extends MyAsyncTask<String>{

    /**
     *上传文件server请求路径
     */
    private String mUrl;

    /**
     * 上传的本地文件集合（key是server定义的文件名，value文件对象）
     */
    private Map<String, File> mFileMap;


    public UploadImageTask(Activity activity, @NonNull String memberId,@NonNull String filePath) {
        super(activity);

        initData(memberId, filePath);
    }

    private void initData(@NonNull String memberId, @NonNull String filePath) {

        //准备url
        mUrl = ServerUrl.UPLOAD_BASE_URL +
                "ID=" + memberId +
                "&token=1&flag=pic&Extension=.jpg";

        //准备file
        mFileMap= new ArrayMap<>(1);
        mFileMap.put("iconFile", new File(filePath));
    }

    @Override
    protected String doInBackground(Void... params) {
        String iconUrl = null;
        //文件上传
        try {
            String result = FileApi.postFile(mUrl, mFileMap);
            if (result != null) {
                //解析json
                JSONObject jsonObject = new JSONObject(result);

                iconUrl = jsonObject.optString("headUrl");
                //更新用户头像
                if (iconUrl != null) {
                    UserInfo info = ((MyApplication) activity.getApplication()).getUserInfo();
                    if (info != null) {
                        info.setHeaderPic(iconUrl);
                    }
                    ((MyApplication) activity.getApplication()).setUserInfo(info);

                }
            }

        } catch (IOException | JSONException e1) {
            e1.printStackTrace();
        }

        //无论上传成功还是失败都要删除本地头像文件
        deleteFiles();

        return iconUrl;
    }

    /**
     * 删除本地头像文件
     */
    private void deleteFiles() {
        String iconPath = Utils.ICON_FOLDER + Utils.ICON_NAME;
        if (new File(iconPath).exists()) {
            Utils.deleteFile(iconPath);
        }

        String zoomIconPath = Utils.ICON_FOLDER + Utils.ZOOM_ICON_NAME;
        if (new File(zoomIconPath).exists()) {
            Utils.deleteFile(zoomIconPath);
        }
    }

}
