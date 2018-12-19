package com.galaxyschool.app.wawaschool.pojo;

import com.alibaba.fastjson.JSONObject;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.ModelListDataParser;

import java.util.List;

/**
 * 拉取个人空间和我的收藏素材列表集合
 */
public class NewResourceInfoTagListResult
        extends DataModelResult<List<NewResourceInfoTag>> {

    public NewResourceInfoTagListResult parse(String jsonString) {
        NewResourceInfoTagListResult result = this;
        if (result.getModel() == null) {
             result = JSONObject.parseObject(jsonString,
                     NewResourceInfoTagListResult.class);
        }
        ModelListDataParser parser = new ModelListDataParser(NewResourceInfoTag.class);
        List<NewResourceInfoTag> list = parser.parse(jsonString);
        if (getModel() != null && list != null) {
            getModel().setData(list);
        }
        return result;
    }

}
