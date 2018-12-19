package com.galaxyschool.app.wawaschool.pojo;

import com.alibaba.fastjson.JSONObject;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.ModelListDataParser;

import java.util.List;

public class ResourceNumListResult
        extends DataModelResult<List<ResourceNum>> {

    public ResourceNumListResult parse(String jsonString) {
        ResourceNumListResult result = this;
        if (result.getModel() == null) {
             result = JSONObject.parseObject(jsonString,
                     ResourceNumListResult.class);
        }
        ModelListDataParser parser = new ModelListDataParser(ResourceNum.class);
        List<ResourceNum> list = parser.parse(jsonString);
        if (getModel() != null && list != null) {
            getModel().setData(list);
        }
        return result;
    }

}
