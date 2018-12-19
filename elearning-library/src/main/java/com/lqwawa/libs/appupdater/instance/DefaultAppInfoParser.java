package com.lqwawa.libs.appupdater.instance;

import com.alibaba.fastjson.JSONObject;
import com.lqwawa.libs.appupdater.AppInfo;
import com.lqwawa.libs.appupdater.AppInfoParser;

public class DefaultAppInfoParser implements AppInfoParser {

    @Override
    public AppInfo parse(String resultString) {
        AppItemListResult result = JSONObject.parseObject(resultString, AppItemListResult.class);
        if (result.isSuccess() && result.getData() != null && result.getData().size() > 0) {
            return result.getData().get(0).toAppInfo();
        }
        return null;
    }

}
