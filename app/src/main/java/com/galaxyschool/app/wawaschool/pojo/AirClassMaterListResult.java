package com.galaxyschool.app.wawaschool.pojo;

import com.alibaba.fastjson.JSONObject;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.ModelListDataParser;

import java.util.List;

public class AirClassMaterListResult extends DataModelResult<List<AirClassroomMateria>> {

    public AirClassMaterListResult parse(String jsonString) {
        AirClassMaterListResult result = this;
        if (result.getModel() == null) {
            result = JSONObject.parseObject(jsonString, AirClassMaterListResult.class);
        }
        ModelListDataParser parser = new ModelListDataParser(AirClassroomMateria.class);
        List<AirClassroomMateria> list = parser.parse(jsonString);
        if (getModel() != null && list != null) {
            getModel().setData(list);
        }
        return result;
    }

}
