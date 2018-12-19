package com.galaxyschool.app.wawaschool.pojo;

import com.alibaba.fastjson.JSONObject;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.ModelListDataParser;

import java.util.List;

/**
 * 班级通讯录信息
 */
public class FamilyMemberInfoListResult
        extends DataModelResult<List<FamilyMemberInfo>> {

    public FamilyMemberInfoListResult parse(String jsonString) {
        FamilyMemberInfoListResult result = this;
        if (result.getModel() == null) {
            result = JSONObject.parseObject(jsonString,
                    FamilyMemberInfoListResult.class);
        }
        ModelListDataParser parser = new ModelListDataParser(FamilyMemberInfo.class);
        List<FamilyMemberInfo> list = parser.parse(jsonString);
        if (getModel() != null && list != null) {
            getModel().setData(list);
        }
        return result;
    }

}
