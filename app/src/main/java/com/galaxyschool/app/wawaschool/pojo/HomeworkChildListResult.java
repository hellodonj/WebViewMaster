package com.galaxyschool.app.wawaschool.pojo;

import com.alibaba.fastjson.JSONObject;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.ModelListDataParser;

import java.util.List;

/**
 * ��ҵ�ҳ��󶨵ĺ������
 */
public class HomeworkChildListResult
        extends DataModelResult<List<StudentMemberInfo>> {

    public HomeworkChildListResult parse(String jsonString) {
        HomeworkChildListResult result = this;
        if (result.getModel() == null) {
            result = JSONObject.parseObject(jsonString,
                    HomeworkChildListResult.class);
        }
        ModelListDataParser parser = new ModelListDataParser(StudentMemberInfo.class);
        List<StudentMemberInfo> list = parser.parse(jsonString);
        if (getModel() != null && list != null) {
            getModel().setData(list);
        }
        return result;
    }

}
