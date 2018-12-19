package com.galaxyschool.app.wawaschool.pojo;

import com.alibaba.fastjson.JSONObject;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.ModelDataParser;

public class SubscribeClassInfoResult extends DataModelResult<SubscribeClassInfo> {

    @Override
    public SubscribeClassInfoResult parse(String jsonString) {
        SubscribeClassInfoResult result = this;
        if (result.getModel() == null) {
            result = JSONObject.parseObject(jsonString, SubscribeClassInfoResult.class);
        }
        ModelDataParser parser = new ModelDataParser(SubscribeClassInfo.class);
        SubscribeClassInfo data = (SubscribeClassInfo) parser.parse(jsonString);
        if (getModel() != null && data != null) {
            getModel().setData(data);
        }
        return result;
    }

}
