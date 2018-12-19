package com.galaxyschool.app.wawaschool.pojo;

import com.alibaba.fastjson.JSONObject;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.ModelListDataParser;

import java.util.List;

public class BookCatalogListResult extends DataModelResult<List<BookCatalog>> {

    public BookCatalogListResult parse(String jsonString) {
        BookCatalogListResult result = this;
        if (result.getModel() == null) {
            result = JSONObject.parseObject(jsonString, BookCatalogListResult.class);
        }
        ModelListDataParser parser = new ModelListDataParser(BookCatalog.class);
        List<BookCatalog> list = parser.parse(jsonString);
        if (getModel() != null && list != null) {
            getModel().setData(list);
        }
        return result;
    }

}
