package com.galaxyschool.app.wawaschool.pojo;

import com.alibaba.fastjson.JSONObject;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.ModelListDataParser;

import java.util.List;

public class BookCategoryListResult extends DataModelResult<List<BookCategory>> {

    public BookCategoryListResult parse(String jsonString) {
        BookCategoryListResult result = this;
        if (result.getModel() == null) {
            result = JSONObject.parseObject(jsonString, BookCategoryListResult.class);
        }
        ModelListDataParser parser = new ModelListDataParser(BookCategory.class);
        List<BookCategory> list = parser.parse(jsonString);
        if (getModel() != null && list != null) {
            getModel().setData(list);
        }
        return result;
    }

}
