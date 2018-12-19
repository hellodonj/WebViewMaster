package com.lqwawa.lqbaselib.net;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;

import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.Network;
import com.duowan.mobile.netroid.RequestQueue;
import com.duowan.mobile.netroid.cache.DiskCache;
import com.duowan.mobile.netroid.stack.HttpClientStack;
import com.duowan.mobile.netroid.stack.HttpStack;
import com.duowan.mobile.netroid.stack.HurlStack;
import com.duowan.mobile.netroid.toolbox.BasicNetwork;
import com.duowan.mobile.netroid.toolbox.FileDownloader;

import org.apache.http.protocol.HTTP;

import java.io.File;

public class Netroid {
    public static final int HTTP_DISK_CACHE_SIZE = 8 * 1024 * 1024; // 8MB

    public static RequestQueue requestQueue = null;

    // 文件下载管理器，私有该实例，提供方法对外服务。
    private static FileDownloader fileDownloader;

    /**
     * Creates a default instance of the worker pool and calls {@link RequestQueue#start()} on it.
     * @param context A {@link Context} to use for creating the cache dir.
     * @return A started {@link RequestQueue} instance.
     */
    public static RequestQueue newRequestQueue(Context context) {
        if(requestQueue != null){
            return requestQueue;
        }
		//int poolSize = RequestQueue.DEFAULT_NETWORK_THREAD_POOL_SIZE;
		int poolSize = 10;
		HttpStack stack;
		String userAgent = "netroid/0";
		try {
			String packageName = context.getPackageName();
			PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
			userAgent = packageName + "/" + info.versionCode;
		} catch (NameNotFoundException e) {
		}

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            stack = new HurlStack(userAgent, null);
        } else {
            // Prior to Gingerbread, HttpUrlConnection was unreliable.
            // See: http://android-developers.blogspot.com/2011/09/androids-http-clients.html
            stack = new HttpClientStack(userAgent);
        }
		String SDPath = Environment.getExternalStorageDirectory().getPath();
       final DiskCache cache = new DiskCache(new File(SDPath + "/temp"), HTTP_DISK_CACHE_SIZE);
        Network network = new BasicNetwork(stack, HTTP.UTF_8);
        requestQueue = new RequestQueue(network, poolSize, cache);
        fileDownloader = new FileDownloader(requestQueue, 1);
        requestQueue.start();
        return requestQueue;
    }

    // 执行文件下载请求
    public static FileDownloader.DownloadController downloadFile(Context context,
                String fileUrl, String filePath, Listener<Void> listener) {
        newRequestQueue(context);
        return fileDownloader.add(filePath, fileUrl, listener);
    }
}

