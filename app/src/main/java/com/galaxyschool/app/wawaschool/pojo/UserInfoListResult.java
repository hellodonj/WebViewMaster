package com.galaxyschool.app.wawaschool.pojo;

import com.alibaba.fastjson.JSONObject;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.ModelListDataParser;

import java.util.List;

public class UserInfoListResult
        extends DataModelResult<List<UserInfo>> {

    public UserInfoListResult parse(String jsonString) {
        UserInfoListResult result = this;
        if (result.getModel() == null) {
             result = JSONObject.parseObject(jsonString,
                     UserInfoListResult.class);
        }
        ModelListDataParser parser = new ModelListDataParser(UserInfo.class);
        List<UserInfo> list = parser.parse(jsonString);
        if (getModel() != null && list != null) {
            getModel().setData(list);
        }
        return result;
    }

}
