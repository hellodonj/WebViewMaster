package com.galaxyschool.app.wawaschool.pojo;

import com.alibaba.fastjson.JSONObject;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.ModelListDataParser;

import java.util.List;

public class ClassInfoListResult
        extends DataModelResult<List<SubscribeClassInfo>> {

    public ClassInfoListResult parse(String jsonString) {
        ClassInfoListResult result = this;
        if (result.getModel() == null) {
            result = JSONObject.parseObject(jsonString,
                    ClassInfoListResult.class);
        }
        ModelListDataParser parser = new ModelListDataParser(SubscribeClassInfo.class);
        List<SubscribeClassInfo> list = parser.parse(jsonString);
        if (getModel() != null && list != null) {
            getModel().setData(list);
        }
        return result;
    }

}
