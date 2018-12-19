package com.lqwawa.intleducation.base.vo;

import com.alibaba.fastjson.JSONObject;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import org.xutils.http.RequestParams;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016-05-04.
 */
public class RequestVo extends BaseVo {
    private Map<String, Object> jsonMap;

    public RequestVo() {
    }

    public void addParams(String key, Object value, boolean isObj) {
        if (jsonMap == null) {
            jsonMap = new HashMap<String, Object>();
        }
        if (isObj) {
            jsonMap.put(key, value);
        } else {
            jsonMap.put(key, value + "");
        }
    }

    public void addParams(String key, Object value) {
        if (jsonMap == null) {
            jsonMap = new HashMap<String, Object>();
        }
        jsonMap.put(key, value + "");
    }


    /**
     * 获取参数组装字符串
     *
     * @return
     */
    public String getParams() {
        if (jsonMap == null) {
            jsonMap = new HashMap<String, Object>();
        }
        if (UserHelper.isLogin()) {
            if (jsonMap.get("token") == null && UserHelper.isLogin()) {
                addParams("token", UserHelper.getUserInfo().getToken());
            }

        }
        return JSONObject.toJSONString(jsonMap);
    }


    public String getPayParams() {
        if (jsonMap == null) {
            jsonMap = new HashMap<String, Object>();
        }
        return JSONObject.toJSONString(jsonMap);
    }

    public String getParamsWithoutToken() {
        if (jsonMap == null) {
            jsonMap = new HashMap<String, Object>();
        }
        return JSONObject.toJSONString(jsonMap);
    }

    /**
     * 获取参数组装实体
     *
     * @return
     */
    public RequestParams getRequestParams() {
        RequestParams requestParams = new RequestParams();
        try {
            requestParams.setAsJsonContent(true);
            requestParams.setBodyContent(getParams());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return requestParams;
    }

    private String getTime() {
        long timeSeconds = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String time = sdf.format(timeSeconds);
        return time;
    }
}
