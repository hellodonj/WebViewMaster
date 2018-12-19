package com.galaxyschool.app.wawaschool.pojo;

import com.alibaba.fastjson.JSONObject;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.ModelListDataParser;

import java.util.List;

public class CampusPatrolSchoolOutlineMaterialListResult
        extends DataModelResult<List<CampusPatrolSchoolOutlineMaterial>> {

    public CampusPatrolSchoolOutlineMaterialListResult parse(String jsonString) {
        CampusPatrolSchoolOutlineMaterialListResult result = this;
        if (result.getModel() == null) {
             result = JSONObject.parseObject(jsonString,
                     CampusPatrolSchoolOutlineMaterialListResult.class);
        }
        ModelListDataParser parser = new ModelListDataParser(CampusPatrolSchoolOutlineMaterial.class);
        List<CampusPatrolSchoolOutlineMaterial> list = parser.parse(jsonString);
        if (getModel() != null && list != null) {
            getModel().setData(list);
        }
        return result;
    }

}
