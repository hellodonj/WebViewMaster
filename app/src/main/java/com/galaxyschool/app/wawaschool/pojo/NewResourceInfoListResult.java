package com.galaxyschool.app.wawaschool.pojo;

import com.alibaba.fastjson.JSONObject;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.ModelListDataParser;

import java.util.List;

public class NewResourceInfoListResult
        extends DataModelResult<List<NewResourceInfo>> {

    public NewResourceInfoListResult parse(String jsonString) {
        NewResourceInfoListResult result = this;
        if (result.getModel() == null) {
             result = JSONObject.parseObject(jsonString,
                     NewResourceInfoListResult.class);
        }
        ModelListDataParser parser = new ModelListDataParser(NewResourceInfo.class);
        List<NewResourceInfo> list = parser.parse(jsonString);
        if (getModel() != null && list != null) {
            getModel().setData(list);
        }
        return result;
    }

}
