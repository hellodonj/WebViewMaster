package com.galaxyschool.app.wawaschool.pojo;

import com.alibaba.fastjson.JSONObject;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.ModelDataParser;
import com.lqwawa.lqbaselib.net.library.ModelListDataParser;

import java.util.List;

public class ReporterListResult extends DataModelResult<List<Reporter>> {

    public ReporterListResult parse(String jsonString) {
        ReporterListResult result = this;
        if (result.getModel() == null) {
            result = JSONObject.parseObject(jsonString, ReporterListResult.class);
        }
        ModelListDataParser parser = new ModelListDataParser(Reporter.class);
        List<Reporter> list = parser.parse(jsonString);
        if (getModel() != null && list != null) {
            getModel().setData(list);
        }
        return result;
    }

}
