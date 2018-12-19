package com.galaxyschool.app.wawaschool.pojo;

import com.alibaba.fastjson.JSONObject;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.ModelListDataParser;

import java.util.List;

/**
 * Created by Administrator on 2016/9/28.
 */

public class SchoolCourseInfoListResult extends DataModelResult<List<SchoolCourseInfo>> {

    public SchoolCourseInfoListResult parse(String jsonString) {
        SchoolCourseInfoListResult result = this;
        if (result.getModel() == null) {
            result = JSONObject.parseObject(jsonString, SchoolCourseInfoListResult.class);
        }
        ModelListDataParser parser = new ModelListDataParser(SchoolCourseInfo.class);
        List<SchoolCourseInfo> list = parser.parse(jsonString);
        if (getModel() != null && list != null) {
            getModel().setData(list);
        }
        return result;
    }
}
