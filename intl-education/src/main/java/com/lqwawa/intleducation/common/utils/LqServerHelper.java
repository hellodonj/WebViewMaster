package com.lqwawa.intleducation.common.utils;

import com.lqwawa.intleducation.AppConfig;

/**
 * Created by XChen on 2017/6/29.
 * email:man0fchina@foxmail.com
 */

public class LqServerHelper {
    public static String getFullImgUrl(String imgUrl)
    {
        if (imgUrl == null)
        {
            return "";
        }
        if (imgUrl.length() < 7)
        {
            return AppConfig.ServerUrl.FileSeverBase + imgUrl;
        }

        if (imgUrl.substring(0, 7) == ("http://"))
        {
            return imgUrl;
        }
        else
        {
            return AppConfig.ServerUrl.FileSeverBase + imgUrl;
        }
    }
}
