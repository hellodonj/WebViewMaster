package com.galaxyschool.app.wawaschool.pojo;

import com.alibaba.fastjson.JSONObject;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.ModelListDataParser;

import java.util.List;

public class AirClassInterActionListResult extends DataModelResult<List<AirClassInterAction>> {

    public AirClassInterActionListResult parse(String jsonString) {
        AirClassInterActionListResult result = this;
        if (result.getModel() == null) {
            result = JSONObject.parseObject(jsonString, AirClassInterActionListResult.class);
        }
        ModelListDataParser parser = new ModelListDataParser(AirClassInterAction.class);
        List<AirClassInterAction> list = parser.parse(jsonString);
        if (getModel() != null && list != null) {
            getModel().setData(list);
        }
        return result;
    }

}
