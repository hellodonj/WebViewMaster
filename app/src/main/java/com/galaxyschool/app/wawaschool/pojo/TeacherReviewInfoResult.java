package com.galaxyschool.app.wawaschool.pojo;

import com.alibaba.fastjson.JSONObject;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.ModelDataParser;
public class TeacherReviewInfoResult extends DataModelResult<TeacherReviewStatisInfo> {

    public TeacherReviewInfoResult parse(String jsonString) {
        TeacherReviewInfoResult result = this;
        if (result.getModel() == null) {
            result = JSONObject.parseObject(jsonString, TeacherReviewInfoResult.class);
        }
        ModelDataParser parser = new ModelDataParser(TeacherReviewStatisInfo.class);
        TeacherReviewStatisInfo data = (TeacherReviewStatisInfo)parser.parse(jsonString);
        if (getModel() != null && data != null) {
            getModel().setData(data);
        }
        return result;
    }

}