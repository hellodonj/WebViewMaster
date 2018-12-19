package com.galaxyschool.app.wawaschool.pojo;

import com.alibaba.fastjson.JSONObject;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.ModelListDataParser;

import java.util.List;

public class PerformClassListResult extends DataModelResult<List<PerformClassList>> {

    public PerformClassListResult parse(String jsonString) {
        PerformClassListResult result = this;
        if (result.getModel() == null) {
            result = JSONObject.parseObject(jsonString, PerformClassListResult.class);
        }
        ModelListDataParser parser = new ModelListDataParser(PerformClassList.class);
        List<PerformClassList> list = parser.parse(jsonString);
        if (getModel() != null && list != null) {
            getModel().setData(list);
        }
        return result;
    }

}
