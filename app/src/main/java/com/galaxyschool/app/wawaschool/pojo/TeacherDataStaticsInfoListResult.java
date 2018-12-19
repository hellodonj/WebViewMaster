package com.galaxyschool.app.wawaschool.pojo;

import com.alibaba.fastjson.JSONObject;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.ModelListDataParser;

import java.util.List;

/**
 * Created by E450 on 2016/12/13.
 * 校园巡查老师列表
 */
public class TeacherDataStaticsInfoListResult extends DataModelResult<List<TeacherDataStaticsInfo>> {

    public TeacherDataStaticsInfoListResult parse(String jsonString) {
        TeacherDataStaticsInfoListResult result = this;
        if (result.getModel() == null) {
            result = JSONObject.parseObject(jsonString,
                    TeacherDataStaticsInfoListResult.class);
        }
        ModelListDataParser parser = new ModelListDataParser(TeacherDataStaticsInfo.class);
        List<TeacherDataStaticsInfo> list = parser.parse(jsonString);
        if (getModel() != null && list != null) {
            getModel().setData(list);
        }
        return result;
    }
}
