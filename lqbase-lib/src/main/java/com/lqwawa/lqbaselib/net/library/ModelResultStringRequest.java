package com.lqwawa.lqbaselib.net.library;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.duowan.mobile.netroid.NetworkResponse;
import com.duowan.mobile.netroid.Response;
import com.google.gson.JsonObject;
import com.lqwawa.lqbaselib.net.ErrorCodeUtil;
import com.osastudio.common.utils.LogUtils;

import java.util.Map;

public class ModelResultStringRequest extends MapParamsStringRequest {

    public ModelResultStringRequest(String url,
            Map<String, Object> params, Listener<String> listener) {
        super(Method.POST, url, params, listener);
    }

    public ModelResultStringRequest(int method, String url,
            Map<String, Object> params, Listener<String> listener) {
        super(method, url, params, listener);
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        Result result = null;
        try {
            String jsonString = new String(response.data, response.charset);
            result = JSON.parseObject(jsonString, Result.class);
            if (result != null) {
                if (result.isSuccess()) {
                    return Response.success(jsonString, response);
                } else {
                    JSONObject jsonObject = JSONObject.parseObject(jsonString);
                    Map<String, String> errorHashMap = ErrorCodeUtil.getInstance().getErrorCodeMap();
                    if(errorHashMap != null && errorHashMap.size() > 0
                            && !TextUtils.isEmpty(result.getErrorMessage())
                            && errorHashMap.containsKey(result.getErrorMessage())) {
                        jsonObject.put("ErrorCode", result.getErrorMessage());
                        jsonObject.put("ErrorMessage", errorHashMap.get(result.getErrorMessage()));
                    }
                    jsonString = jsonObject.toJSONString();
                    return Response.success(jsonString, new NetworkResponse(
                            jsonString.getBytes(), response.charset));
                }
            } else {
                result = new Result(true, "Parse Error");
                return Response.error(new NetroidError(JSON.toJSONString(result)));
            }
        } catch (Exception e) {
            result = new Result(true, "Parse Error");
            Response.error(new NetroidError(JSON.toJSONString(result)));
        }
        result = new Result(true, "Network Error");
        return Response.error(new NetroidError(JSON.toJSONString(result)));
    }

}