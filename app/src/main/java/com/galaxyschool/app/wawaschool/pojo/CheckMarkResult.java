package com.galaxyschool.app.wawaschool.pojo;

import com.alibaba.fastjson.JSONObject;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.ModelListDataParser;

import java.util.List;

/**
 * �ύ��ҵ
 */
public class CheckMarkResult
        extends DataModelResult<List<CommitTask>> {

    public CheckMarkResult parse(String jsonString) {
        CheckMarkResult result = this;
        if (result.getModel() == null) {
            result = JSONObject.parseObject(jsonString,
                    CheckMarkResult.class);
        }
        //ת��Object
        ModelListDataParser dataParser = new ModelListDataParser(CommitTask.class);
        List<CommitTask>  info=  dataParser.parse(jsonString);
        if (getModel()!=null&&info!=null){
            getModel().setData(info);
        }

        return result;
    }

}

