package com.lqwawa.libs.appupdater.instance;

import android.os.Handler;
import com.lqwawa.libs.appupdater.AppInfo;
import com.lqwawa.libs.appupdater.UpdateService;
import com.osastudio.common.utils.Utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DefaultUpdateService extends UpdateService {

    private static final String TAG = DefaultUpdateService.class.getSimpleName();

    private Handler handler = new Handler();
    private AppInfoTask appInfoTask;

    @Override
    protected void checkUpdate(boolean forceUpdate) {
        if (appInfoTask == null) {
            appInfoTask = new AppInfoTask(forceUpdate);
            new Thread(appInfoTask).start();
        }
    }

    private String getAppInfoUrl() {
        String serverUrl = isDebugMode() ? "http://resop.lqwawa.com/" : "http://upgradeapk.inoot.cn/";
        return new StringBuilder(serverUrl)
                .append("authorizationmanagement/webapi/launcherRes/getAllRes.htm").toString();
    }

    private class AppInfoTask implements Runnable {
        private boolean forceUpdate;

        public AppInfoTask(boolean forceUpdate) {
            this.forceUpdate = forceUpdate;
        }

        @Override
        public void run() {
            try {
                URL url = new URL(new StringBuilder(getAppInfoUrl()).append("?pkgName=")
                        .append(getPackageName()).toString());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(60 * 1000);
                connection.setReadTimeout(60 * 1000);
                connection.setRequestMethod("GET");
                connection.connect();
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    InputStream is = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                    StringBuilder builder = new StringBuilder();
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                    }
                    String resultString = builder.toString();
                    if (isDebugMode()) {
                        Utils.log("UPDATER", "RESULT=" + resultString);
                    }
                    final AppInfo appInfo = new DefaultAppInfoParser().parse(resultString);
                    if (appInfo != null) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                checkUpdate(appInfo, forceUpdate);
                            }
                        });
                    }
                    reader.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            DefaultUpdateService.this.appInfoTask = null;
        }
    }

}
