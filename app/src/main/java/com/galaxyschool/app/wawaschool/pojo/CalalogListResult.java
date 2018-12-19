package com.galaxyschool.app.wawaschool.pojo;

import com.alibaba.fastjson.JSONObject;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.ModelListDataParser;

import java.util.List;

public class CalalogListResult extends DataModelResult<List<Calalog>> {

    public CalalogListResult parse(String jsonString) {
        CalalogListResult result = this;
        if (result.getModel() == null) {
            result = JSONObject.parseObject(jsonString, CalalogListResult.class);
        }
        ModelListDataParser parser = new ModelListDataParser(Calalog.class);
        List<Calalog> list = parser.parse(jsonString);
        if (getModel() != null && list != null) {
            getModel().setData(list);
        }
        return result;
    }

}
