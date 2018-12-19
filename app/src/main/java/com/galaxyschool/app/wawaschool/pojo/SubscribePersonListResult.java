package com.galaxyschool.app.wawaschool.pojo;

import com.alibaba.fastjson.JSONObject;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.ModelListDataParser;

import java.util.List;

public class SubscribePersonListResult
        extends DataModelResult<List<PersonInfo>> {

    public SubscribePersonListResult parse(String jsonString) {
        SubscribePersonListResult result = this;
        if (result.getModel() == null) {
            result = JSONObject.parseObject(jsonString,
                    SubscribePersonListResult.class);
        }
        ModelListDataParser parser = new ModelListDataParser(PersonInfo.class);
        List<PersonInfo> list = parser.parse(jsonString);
        if (getModel() != null && list != null) {
            getModel().setData(list);
        }
        return result;
    }

}
