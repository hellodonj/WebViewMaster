package com.galaxyschool.app.wawaschool.pojo;

import com.alibaba.fastjson.JSONObject;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.ModelDataParser;

/**
 * Created by Administrator on 2016/9/28.
 */

public class AuthorizationInfoResult extends DataModelResult<AuthorizationInfo> {

    public AuthorizationInfoResult parse(String jsonString) {
        AuthorizationInfoResult result = this;
        if (result.getModel() == null) {
            result = JSONObject.parseObject(jsonString,
                    AuthorizationInfoResult.class);
        }
        ModelDataParser parser = new ModelDataParser(AuthorizationInfo.class);
        AuthorizationInfo data = (AuthorizationInfo)parser.parse(jsonString);
        if (getModel() != null && data != null) {
            getModel().setData(data);
        }
        return result;
    }
}
