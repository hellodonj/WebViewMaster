package com.galaxyschool.app.wawaschool.pojo;

import com.alibaba.fastjson.JSONObject;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.ModelListDataParser;

import java.util.List;

/**
 * Created by E450 on 2016/12/13.
 * 校园巡查班级列表
 */
public class ClassDataStaticsInfoListResult extends DataModelResult<List<ClassDataStaticsInfo>> {

    public ClassDataStaticsInfoListResult parse(String jsonString) {
        ClassDataStaticsInfoListResult result = this;
        if (result.getModel() == null) {
            result = JSONObject.parseObject(jsonString,
                    ClassDataStaticsInfoListResult.class);
        }
        ModelListDataParser parser = new ModelListDataParser(ClassDataStaticsInfo.class);
        List<ClassDataStaticsInfo> list = parser.parse(jsonString);
        if (getModel() != null && list != null) {
            getModel().setData(list);
        }
        return result;
    }
}
