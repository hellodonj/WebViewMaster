package com.galaxyschool.app.wawaschool.pojo;

import com.alibaba.fastjson.JSONObject;
import com.galaxyschool.app.wawaschool.db.dto.BookStoreBook;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.ModelListDataParser;

import java.util.List;

public class BookStoreBookListResult extends DataModelResult<List<BookStoreBook>> {

    public BookStoreBookListResult parse(String jsonString) {
        BookStoreBookListResult result = this;
        if (result.getModel() == null) {
            result = JSONObject.parseObject(jsonString, BookStoreBookListResult.class);
        }
        ModelListDataParser parser = new ModelListDataParser(BookStoreBook.class);
        List<BookStoreBook> list = parser.parse(jsonString);
        if (getModel() != null && list != null) {
            getModel().setData(list);
        }
        return result;
    }

}
