package com.galaxyschool.app.wawaschool.pojo;

import com.alibaba.fastjson.JSONObject;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.ModelListDataParser;

import java.util.List;

public class EmceeListResult extends DataModelResult<List<Emcee>> {

    public EmceeListResult parse(String jsonString) {
        EmceeListResult result = this;
        if (result.getModel() == null) {
            result = JSONObject.parseObject(jsonString, EmceeListResult.class);
        }
        ModelListDataParser parser = new ModelListDataParser(Emcee.class);
        List<Emcee> list = parser.parse(jsonString);
        if (getModel() != null && list != null) {
            getModel().setData(list);
        }
        return result;
    }

}
