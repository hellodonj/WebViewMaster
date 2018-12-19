package com.lqwawa.lqbaselib.net.library;

import com.alibaba.fastjson.JSONObject;

public class ModelDataParser<T> extends JSONObjectParser {

    private Class cls;

    public ModelDataParser(Class cls) {
        super(cls);
        this.cls = cls;
    }

    @Override
    public T parse(String jsonString) {
        JSONObject jsonObject = JSONObject.parseObject(jsonString);
        if (jsonObject == null || !jsonObject.containsKey("Model")) {
            return null;
        }
        JSONObject modelObject = jsonObject.getJSONObject("Model");
        if (modelObject == null) {
            return null;
        }
        JSONObject dataObject = modelObject.getJSONObject("Data");
        if (dataObject == null) {
            return null;
        }
        return (T)super.parse(dataObject.toJSONString());
    }

}
