package com.galaxyschool.app.wawaschool.pojo;

import com.alibaba.fastjson.JSONObject;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.ModelListDataParser;

import java.util.List;

public class SubscribeUserListResult
        extends DataModelResult<List<SubscribeUserInfo>> {

    public SubscribeUserListResult parse(String jsonString) {
        SubscribeUserListResult result = this;
        if (result.getModel() == null) {
             result = JSONObject.parseObject(jsonString,
                     SubscribeUserListResult.class);
        }
        ModelListDataParser parser = new ModelListDataParser(SubscribeUserInfo.class);
        List<SubscribeUserInfo> list = parser.parse(jsonString);
        if (getModel() != null && list != null) {
            getModel().setData(list);
        }
        return result;
    }

}
