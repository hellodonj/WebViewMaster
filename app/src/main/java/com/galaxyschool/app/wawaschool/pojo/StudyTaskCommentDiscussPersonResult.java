package com.galaxyschool.app.wawaschool.pojo;

import com.alibaba.fastjson.JSONObject;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.ModelDataParser;

/**
 * Created by KnIghT on 16-6-21.
 */
public class StudyTaskCommentDiscussPersonResult extends DataModelResult<StudyTaskCommentDiscussPerson> {

    public StudyTaskCommentDiscussPersonResult parse(String jsonString) {
        StudyTaskCommentDiscussPersonResult result = this;
        if (result.getModel() == null) {
            result = JSONObject.parseObject(jsonString, StudyTaskCommentDiscussPersonResult.class);
        }
        ModelDataParser parser = new ModelDataParser(StudyTaskCommentDiscussPerson.class);
        StudyTaskCommentDiscussPerson data = (StudyTaskCommentDiscussPerson)parser.parse(jsonString);
        if (getModel() != null && data != null) {
            getModel().setData(data);
        }
        return result;
    }

}