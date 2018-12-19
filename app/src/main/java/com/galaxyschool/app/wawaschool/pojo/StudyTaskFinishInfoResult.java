package com.galaxyschool.app.wawaschool.pojo;

import com.alibaba.fastjson.JSONObject;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.ModelDataParser;

/**
 * Created by KnIghT on 16-6-21.
 */
public class StudyTaskFinishInfoResult extends DataModelResult<StudyTaskFinishInfo> {

    public StudyTaskFinishInfoResult parse(String jsonString) {
        StudyTaskFinishInfoResult result = this;
        if (result.getModel() == null) {
            result = JSONObject.parseObject(jsonString, StudyTaskFinishInfoResult.class);
        }
        ModelDataParser parser = new ModelDataParser(StudyTaskFinishInfo.class);
        StudyTaskFinishInfo data = (StudyTaskFinishInfo)parser.parse(jsonString);
        if (getModel() != null && data != null) {
            getModel().setData(data);
        }
        return result;
    }

}