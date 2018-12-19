package com.galaxyschool.app.wawaschool.pojo;

import com.alibaba.fastjson.JSONObject;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.ModelDataParser;

/**
 * Created by KnIghT on 16-6-21.
 */
public class TaskDetailResult extends DataModelResult<TaskDetail> {

    public TaskDetailResult parse(String jsonString) {
        TaskDetailResult result = this;
        if (result.getModel() == null) {
            result = JSONObject.parseObject(jsonString, TaskDetailResult.class);
        }
        ModelDataParser parser = new ModelDataParser(TaskDetail.class);
        TaskDetail data = (TaskDetail)parser.parse(jsonString);
        if (getModel() != null && data != null) {
            getModel().setData(data);
        }
        return result;
    }

}