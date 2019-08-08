package com.galaxyschool.app.wawaschool.pojo;

import com.alibaba.fastjson.JSONObject;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.ModelListDataParser;

import java.util.List;

public class StatisticBeanListResult extends DataModelResult<List<StatisticBean>> {

    public StatisticBeanListResult parse(String jsonString) {
        StatisticBeanListResult result = this;
        if (result.getModel() == null) {
            result = JSONObject.parseObject(jsonString, StatisticBeanListResult.class);
        }
        ModelListDataParser parser = new ModelListDataParser(StatisticBean.class);
        List<StatisticBean> list = parser.parse(jsonString);
        if (getModel() != null && list != null) {
            getModel().setData(list);
        }
        return result;
    }

}
