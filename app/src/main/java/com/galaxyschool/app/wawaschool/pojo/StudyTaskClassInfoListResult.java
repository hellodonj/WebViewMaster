package com.galaxyschool.app.wawaschool.pojo;

import com.alibaba.fastjson.JSONObject;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.ModelListDataParser;

import java.util.List;

public class StudyTaskClassInfoListResult
        extends DataModelResult<List<StudyTaskClassInfo>> {

    public StudyTaskClassInfoListResult parse(String jsonString) {
        StudyTaskClassInfoListResult result = this;
        if (result.getModel() == null) {
             result = JSONObject.parseObject(jsonString,
                     StudyTaskClassInfoListResult.class);
        }
        ModelListDataParser parser = new ModelListDataParser(StudyTaskClassInfo.class);
        List<StudyTaskClassInfo> list = parser.parse(jsonString);
        if (getModel() != null && list != null) {
            getModel().setData(list);
        }
        return result;
    }

}
