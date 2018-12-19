package com.galaxyschool.app.wawaschool.pojo;

import com.alibaba.fastjson.JSONObject;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.ModelListDataParser;

import java.util.List;

/**
 * ��ҵ�б����
 */
public class HomeworkListResult
        extends DataModelResult<List<HomeworkListInfo>> {

    public HomeworkListResult parse(String jsonString) {
        HomeworkListResult result = this;
        if (result.getModel() == null) {
            result = JSONObject.parseObject(jsonString,
                    HomeworkListResult.class);
        }
        ModelListDataParser parser = new ModelListDataParser(HomeworkListInfo.class);
        List<HomeworkListInfo> list = parser.parse(jsonString);
        if (getModel() != null && list != null) {
            getModel().setData(list);
        }
        return result;
    }

}
