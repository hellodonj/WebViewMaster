package com.lqwawa.intleducation.common.net;

import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.base.vo.RequestVo;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;

/**
 * Created by XChen on 2016/11/30.
 * email:man0fchina@foxmail.com
 */

public class UploadImg {
    /**
     * 提交图片
     */
    public static void uploadImage(String path, Callback.CommonCallback<String> callback) {
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.UploadImg);
        params.setConnectTimeout(60*10000);
        params.addBodyParameter("file", new File(path));
        params.setMultipart(true);
        x.http().post(params, callback);
    }
}
