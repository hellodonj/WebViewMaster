package com.lqwawa.lqbaselib.net.library;

import com.alibaba.fastjson.JSONArray;

import java.util.List;

public class JSONArrayParser<T> {

    private Class cls;

    public JSONArrayParser(Class cls) {
        this.cls = cls;
    }

    public List<T> parse(String jsonString) {
        return JSONArray.parseArray(jsonString, this.cls);
    }

}
