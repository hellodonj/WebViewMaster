package com.galaxyschool.app.wawaschool.pojo;

import com.alibaba.fastjson.JSONObject;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.ModelDataParser;

/**
 * �ύ��ҵ
 */
public class HomeworkCommitObjectResult
        extends DataModelResult<HomeworkCommitObjectInfo> {

    public HomeworkCommitObjectResult parse(String jsonString) {
        HomeworkCommitObjectResult result = this;
        if (result.getModel() == null) {
            result = JSONObject.parseObject(jsonString,
                    HomeworkCommitObjectResult.class);
        }
        //ת��Object
        ModelDataParser dataParser = new ModelDataParser(HomeworkCommitObjectInfo.class);
        HomeworkCommitObjectInfo info= (HomeworkCommitObjectInfo) dataParser.parse(jsonString);
        if (getModel()!=null&&info!=null){
            getModel().setData(info);
        }

        return result;
    }

}
