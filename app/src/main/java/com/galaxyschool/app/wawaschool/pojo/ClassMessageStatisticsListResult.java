package com.galaxyschool.app.wawaschool.pojo;

import com.alibaba.fastjson.JSONObject;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.ModelListDataParser;

import java.util.List;

public class ClassMessageStatisticsListResult
        extends DataModelResult<List<ClassMessageStatistics>> {

    public ClassMessageStatisticsListResult parse(String jsonString) {
        ClassMessageStatisticsListResult result = this;
        if (result.getModel() == null) {
            result = JSONObject.parseObject(jsonString, ClassMessageStatisticsListResult.class);
        }
        ModelListDataParser parser = new ModelListDataParser(ClassMessageStatistics.class);
        List<ClassMessageStatistics> list = parser.parse(jsonString);
        if (getModel() != null && list != null) {
            getModel().setData(list);
        }
        return result;
    }

}
