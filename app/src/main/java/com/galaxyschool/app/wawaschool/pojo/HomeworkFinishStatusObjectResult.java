package com.galaxyschool.app.wawaschool.pojo;

import com.alibaba.fastjson.JSONObject;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.ModelDataParser;

/**
 * 作业完成和未完成信息
 */
public class HomeworkFinishStatusObjectResult
        extends DataModelResult<HomeworkFinishStatusObjectInfo> {

    public HomeworkFinishStatusObjectResult parse(String jsonString) {
        HomeworkFinishStatusObjectResult result = this;
        if (result.getModel() == null) {
            result = JSONObject.parseObject(jsonString,
                    HomeworkFinishStatusObjectResult.class);
        }
        //ת��Object
        ModelDataParser dataParser = new ModelDataParser(HomeworkFinishStatusObjectInfo.class);
        HomeworkFinishStatusObjectInfo info= (HomeworkFinishStatusObjectInfo) dataParser
                .parse(jsonString);
        if (getModel()!=null&&info!=null){
            getModel().setData(info);
        }

        return result;
    }

}
