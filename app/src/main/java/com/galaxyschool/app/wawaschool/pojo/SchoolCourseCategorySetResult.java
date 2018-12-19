package com.galaxyschool.app.wawaschool.pojo;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lqwawa.lqbaselib.net.library.DataModelResult;

public class SchoolCourseCategorySetResult
        extends DataModelResult<SchoolCourseCategorySet> {

    public SchoolCourseCategorySetResult parse(String jsonString) {
        SchoolCourseCategorySetResult result = this;
        if (result.getModel() == null) {
            result = JSONObject.parseObject(jsonString,
                    SchoolCourseCategorySetResult.class);
        }
        JSONObject jsonObject = JSONObject.parseObject(jsonString);
        if (jsonObject == null || !jsonObject.containsKey("Model")) {
            return null;
        }
        JSONObject modelObject = jsonObject.getJSONObject("Model");
        if (modelObject == null) {
            return null;
        }
        JSONObject dataObject = modelObject.getJSONObject("Data");
        if (dataObject == null) {
            return null;
        }
        SchoolCourseCategorySet data = JSONObject.parseObject(
                dataObject.toJSONString(), SchoolCourseCategorySet.class);
        if (data == null) {
            return null;
        }
        result.getModel().setData(data);
        JSONArray dataArray = dataObject.getJSONArray("LevelList");
        if (dataArray != null) {
            data.setLevelList(JSONArray.parseArray(dataArray.toJSONString(), SchoolStage.class));
        }
        dataArray = dataObject.getJSONArray("GradeList");
        if (dataArray != null) {
            data.setGradeList(JSONArray.parseArray(dataArray.toJSONString(), SchoolGrade.class));
        }
        dataArray = dataObject.getJSONArray("SubjectList");
        if (dataArray != null) {
            data.setSubjectList(JSONArray.parseArray(dataArray.toJSONString(), SchoolSubject.class));
        }

        return result;
    }

}
