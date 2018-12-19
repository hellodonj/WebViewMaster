package com.galaxyschool.app.wawaschool.pojo;

import com.alibaba.fastjson.JSONObject;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.ModelListDataParser;
import com.galaxyschool.app.wawaschool.views.categoryview.Category;

import java.util.List;

public class CategoryListResult extends DataModelResult<List<Category>> {

    public CategoryListResult parse(String jsonString) {
        CategoryListResult result = this;
        if (result.getModel() == null) {
            result = JSONObject.parseObject(jsonString, CategoryListResult.class);
        }
        ModelListDataParser parser = new ModelListDataParser(Category.class);
        List<Category> list = parser.parse(jsonString);
        if (getModel() != null && list != null) {
            getModel().setData(list);
        }
        return result;
    }

}
