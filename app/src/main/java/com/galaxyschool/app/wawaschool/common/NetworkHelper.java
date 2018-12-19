package com.galaxyschool.app.wawaschool.common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
/**
 * @author 作者 shouyi:
 * @version 创建时间：Aug 24, 2015 17:53:00
 * 类说明 网络情况帮助器
 */
public class NetworkHelper {

    /**
     * 网络是否已经连接，可能是手机网络也可能是WIFI
     * @param context
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (mConnectivityManager != null) {
            	NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            	if (mNetworkInfo != null) {
            		return mNetworkInfo.isAvailable();
            	}
			}
        }
        return false;
    }

    /**
     * WIFI是否可用
     * @param context
     * @return
     */
    public static boolean isWifiConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWiFiNetworkInfo = mConnectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (mWiFiNetworkInfo != null) {
                return mWiFiNetworkInfo.isAvailable() && mWiFiNetworkInfo.isConnected();
            }
        }
        return false;
    }

    /**
     * 手机网络是否可用，2G,3G,4G
     * @param context
     * @return
     */
    public boolean isMobileConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mMobileNetworkInfo = mConnectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (mMobileNetworkInfo != null) {
                return mMobileNetworkInfo.isAvailable();
            }
        }
        return false;
    }
}
