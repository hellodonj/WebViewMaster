package com.galaxyschool.app.wawaschool.pojo;

import com.alibaba.fastjson.JSONObject;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.ModelListDataParser;

import java.util.List;

public class AddedSchoolInfoListResult
        extends DataModelResult<List<SchoolInfo>> {

    public AddedSchoolInfoListResult parse(String jsonString) {
        AddedSchoolInfoListResult result = this;
        if (result.getModel() == null) {
            result = JSONObject.parseObject(jsonString,
                    AddedSchoolInfoListResult.class);
        }
        ModelListDataParser parser = new ModelListDataParser(SchoolInfo.class);
        List<SchoolInfo> list = parser.parse(jsonString);
        if (getModel() != null && list != null) {
            getModel().setData(list);
        }
        return result;
    }

}
