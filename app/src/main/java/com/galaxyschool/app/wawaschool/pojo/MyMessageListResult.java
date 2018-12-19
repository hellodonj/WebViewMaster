package com.galaxyschool.app.wawaschool.pojo;

import com.alibaba.fastjson.JSONObject;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.ModelListDataParser;

import java.util.List;

public class MyMessageListResult extends DataModelResult<List<MyMessage>> {

    public MyMessageListResult parse(String jsonString) {
        MyMessageListResult result = this;
        if (result.getModel() == null) {
            result = JSONObject.parseObject(jsonString, MyMessageListResult.class);
        }
        ModelListDataParser parser = new ModelListDataParser(MyMessage.class);
        List<MyMessage> list = parser.parse(jsonString);
        if (getModel() != null && list != null) {
            getModel().setData(list);
        }
        return result;
    }

}
