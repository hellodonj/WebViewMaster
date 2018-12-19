package com.lqwawa.libs.filedownloader;

public interface DownloadListener {

    void onPrepare(FileInfo fileInfo);

    void onStart(FileInfo fileInfo);

    void onProgress(FileInfo fileInfo);

    void onPause(FileInfo fileInfo);

    void onFinish(FileInfo fileInfo);

    void onError(FileInfo fileInfo);

    void onDelete(FileInfo fileInfo);

}
