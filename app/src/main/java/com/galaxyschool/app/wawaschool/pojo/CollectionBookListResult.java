package com.galaxyschool.app.wawaschool.pojo;

import com.alibaba.fastjson.JSONObject;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.ModelListDataParser;

import java.util.List;

public class CollectionBookListResult extends DataModelResult<List<CollectionBook>> {

    public CollectionBookListResult parse(String jsonString) {
        CollectionBookListResult result = this;
        if (result.getModel() == null) {
            result = JSONObject.parseObject(jsonString, CollectionBookListResult.class);
        }
        ModelListDataParser parser = new ModelListDataParser(CollectionBook.class);
        List<CollectionBook> list = parser.parse(jsonString);
        if (getModel() != null && list != null) {
            getModel().setData(list);
        }
        return result;
    }

}
