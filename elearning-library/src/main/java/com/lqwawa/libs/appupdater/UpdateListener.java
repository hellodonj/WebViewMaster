package com.lqwawa.libs.appupdater;

public interface UpdateListener {

    void onPrepare(AppInfo appInfo);

    void onStart(AppInfo appInfo);

    void onProgress(AppInfo appInfo);

    void onFinish(AppInfo appInfo);

    void onError(AppInfo appInfo);

}
