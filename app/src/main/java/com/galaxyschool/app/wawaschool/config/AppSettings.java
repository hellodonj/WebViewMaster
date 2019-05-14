package com.galaxyschool.app.wawaschool.config;

import android.text.TextUtils;
import com.galaxyschool.app.wawaschool.common.Utils;
import com.osastudio.apps.Config;

import java.util.ArrayList;

/**
 * @author 作者 shouyi:
 * @version 创建时间：Mar 31, 2015 3:14:50 PM
 *          类说明
 */
public class AppSettings {
    public static final boolean DEBUG = Config.DEBUG;
    public static final boolean LOGIN_NUTRITION = true;//控制是否要展示营养膳食模块,true表示展示

    //微信APPID & APPKEY
    public static final String WEIXIN_APPID = "wx8708a6401c83d49b";
    public static final String WEIXIN_APPSECRET = "03af568b2149e993ee5d511258e4bf01";
    //QQ APPID & APPKEY
    public static final String QQ_APPID = "1104520819";
    public static final String QQ_APPKEY = "I5FD9iUfLbRhawsG";

    //bugly APPID & APPKEY
    public static final String BUGLY_APPID = "eea1375c3e";
    public static final String BUGLY_APPKEY = "a07b42dc-cb09-4c1f-9de1-289f839b35a9";

    public static String getFileUrl(String url) {
        return getFileUrlWithParams(url, "");
    }

    public static String getFileUrlWithParams(String url, String params) {
        if (TextUtils.isEmpty(url)) {
            return "";
        }
    	if (!TextUtils.isEmpty(url) && (url.startsWith("http:") || url.contains("https:"))) {
			return url;
		}
        if (!TextUtils.isEmpty(url) && url.contains("\\")) {
            url = url.replace("\\", "/");
        }
        if (!TextUtils.isEmpty(url) && url.contains("//")) {
            url = url.replace("//", "/");
        }
        return ServerUrl.IMG_ROOT_URL + url + params;
    }
    
    public static ArrayList<String> getScanFilesSkipKeys() {
    	ArrayList<String> keys = new ArrayList<String>();
    	keys.add(Utils.LQWAWA_KEY);
    	return keys;
    }

}
