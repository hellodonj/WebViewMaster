package com.galaxyschool.app.wawaschool.pojo;

import com.alibaba.fastjson.JSONObject;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.ModelListDataParser;

import java.util.List;

public class CustomerServerListResult extends DataModelResult<List< CustomerServer>> {

    public CustomerServerListResult parse(String jsonString) {
        CustomerServerListResult result = this;
        if (result.getModel() == null) {
            result = JSONObject.parseObject(jsonString, CustomerServerListResult.class);
        }
        ModelListDataParser parser = new ModelListDataParser( CustomerServer.class);
        List< CustomerServer> list = parser.parse(jsonString);
        if (getModel() != null && list != null) {
            getModel().setData(list);
        }
        return result;
    }

}
