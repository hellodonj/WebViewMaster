package com.lqwawa.lqbaselib.net.library;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

public class ModelListDataParser<T> extends JSONArrayParser {

    private Class cls;

    public ModelListDataParser(Class cls) {
        super(cls);
        this.cls = cls;
    }

    @Override
    public List<T> parse(String jsonString) {
        JSONObject jsonObject = JSONObject.parseObject(jsonString);
        if (jsonObject == null || !jsonObject.containsKey("Model")) {
            return null;
        }
        JSONObject modelObject = jsonObject.getJSONObject("Model");
        if (modelObject == null) {
            return null;
        }
        JSONArray dataArray = modelObject.getJSONArray("Data");
        if (dataArray == null) {
            return null;
        }
        return super.parse(dataArray.toJSONString());
    }

}
