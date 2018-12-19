package com.lqwawa.lqbaselib.net.library;

import android.content.Context;

import com.alibaba.fastjson.JSONObject;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetworkResponse;
import com.duowan.mobile.netroid.Response;
import com.osastudio.common.utils.LogUtils;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public class TokenResultStringRequest extends MapParamsStringRequest {

    private static final String URL =
            "http://121.42.155.135:8099/api/Mobile/WorkSpace/CreateSpace/Token/GetToken";

    private MapParamsStringRequest hostRequest;

    public TokenResultStringRequest(Map<String, Object> params, Listener<String> listener) {
        super(Method.POST, URL, params, listener);
        setEncryptEnabled(true);
    }

    public void setHostRequest(MapParamsStringRequest request) {
        hostRequest = request;
    }

    public MapParamsStringRequest getHostRequest() {
        return hostRequest;
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        if (response.data != null && response.data.length > 0) {
            try {
                String jsonString = new String(response.data, response.charset);
                LogUtils.log("TEST", "RESULT: " + jsonString);
                JSONObject jsonObject = JSONObject.parseObject(jsonString);
                String accessToken = jsonObject.getString("access_token");
                String tokenType = jsonObject.getString("token_type");
                if (this.hostRequest != null) {
                    this.hostRequest.setAccessToken(tokenType + " " + accessToken);
                    this.hostRequest.setForceUpdate(true);
                    this.hostRequest.run(getContext());
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return super.parseNetworkResponse(response);
    }

    @Override
    public void run(Context context) {
        // Avoid endless looping token requests
        setAuthEnabled(false);
        super.run(context);
    }

}