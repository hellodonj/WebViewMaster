package com.galaxyschool.app.wawaschool.pojo;

import com.alibaba.fastjson.JSONObject;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.ModelDataParser;
public class BookDetailResult extends DataModelResult<BookDetail> {

    public BookDetailResult parse(String jsonString) {
        BookDetailResult result = this;
        if (result.getModel() == null) {
            result = JSONObject.parseObject(jsonString, BookDetailResult.class);
        }
        ModelDataParser parser = new ModelDataParser(BookDetail.class);
        BookDetail data = (BookDetail)parser.parse(jsonString);
        if (getModel() != null && data != null) {
            getModel().setData(data);
        }
        return result;
    }

}
