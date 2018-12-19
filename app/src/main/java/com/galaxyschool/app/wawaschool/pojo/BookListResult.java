package com.galaxyschool.app.wawaschool.pojo;

import com.alibaba.fastjson.JSONObject;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.ModelListDataParser;

import java.util.List;

public class BookListResult extends DataModelResult<List<Book>> {

    public BookListResult parse(String jsonString) {
        BookListResult result = this;
        if (result.getModel() == null) {
            result = JSONObject.parseObject(jsonString, BookListResult.class);
        }
        ModelListDataParser parser = new ModelListDataParser(Book.class);
        List<Book> list = parser.parse(jsonString);
        if (getModel() != null && list != null) {
            getModel().setData(list);
        }
        return result;
    }

}
