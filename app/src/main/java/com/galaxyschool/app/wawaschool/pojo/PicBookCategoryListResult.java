package com.galaxyschool.app.wawaschool.pojo;

import com.alibaba.fastjson.JSONObject;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.ModelListDataParser;

import java.util.List;

public class PicBookCategoryListResult extends DataModelResult<List<PicBookCategory>> {

    public PicBookCategoryListResult parse(String jsonString) {
        PicBookCategoryListResult result = this;
        if (result.getModel() == null) {
            result = JSONObject.parseObject(jsonString, PicBookCategoryListResult.class);
        }
        ModelListDataParser parser = new ModelListDataParser(PicBookCategory.class);
        List<PicBookCategory> list = parser.parse(jsonString);
        if (getModel() != null && list != null) {
            getModel().setData(list);
        }
        return result;
    }

}
