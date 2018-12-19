package com.lqwawa.lqbaselib.net.library;

import com.alibaba.fastjson.JSONObject;

public class JSONObjectParser<T> {

    private Class cls;

    public JSONObjectParser(Class cls) {
        this.cls = cls;
    }

    public T parse(String jsonString) {
        return (T) JSONObject.parseObject(jsonString, this.cls);
    }

}
