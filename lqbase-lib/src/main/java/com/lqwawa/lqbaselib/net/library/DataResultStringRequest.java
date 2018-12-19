package com.lqwawa.lqbaselib.net.library;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.duowan.mobile.netroid.NetworkResponse;
import com.duowan.mobile.netroid.Response;
import com.lqwawa.lqbaselib.net.ErrorCodeUtil;
import com.osastudio.common.utils.LogUtils;

import java.util.Map;

public class DataResultStringRequest extends MapParamsStringRequest {

    public DataResultStringRequest(String url,
            Map<String, Object> params, Listener<String> listener) {
        super(Method.POST, url, params, listener);
    }

    public DataResultStringRequest(int method, String url,
            Map<String, Object> params, Listener<String> listener) {
        super(method, url, params, listener);
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        DataResult result = null;
        try {
            String jsonString = new String(response.data, response.charset);
            result = JSON.parseObject(jsonString, DataResult.class);
            if (result != null) {
                if (result.isSuccess()) {
                    return Response.success(jsonString, response);
                } else {
                    Map<String, String> errorHashMap = ErrorCodeUtil.getInstance().getErrorCodeMap();
                    if(errorHashMap != null && errorHashMap.size() > 0
                            && !TextUtils.isEmpty(result.getErrorMessage())
                            && errorHashMap.containsKey(result.getErrorMessage())) {
                        result.setErrorMessage(errorHashMap.get(result.getErrorMessage()));
                    }
                    jsonString = JSON.toJSONString(result);
                    return Response.success(jsonString, new NetworkResponse(
                            jsonString.getBytes(), response.charset));
                }
            } else {
                result = new DataResult(-1, "Parse Error");
                return Response.error(new NetroidError(JSON.toJSONString(result)));
            }
        } catch (Exception e) {
            result = new DataResult(-1, "Parse Error");
            Response.error(new NetroidError(JSON.toJSONString(result)));
        }
        result = new DataResult(-1, "Network Error");
        return Response.error(new NetroidError(JSON.toJSONString(result)));
    }

}