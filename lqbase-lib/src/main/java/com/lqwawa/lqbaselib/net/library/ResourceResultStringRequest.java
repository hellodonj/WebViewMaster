package com.lqwawa.lqbaselib.net.library;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.duowan.mobile.netroid.AuthFailureError;
import com.duowan.mobile.netroid.Delivery;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.ServerError;
import com.osastudio.common.library.Base64;
import com.osastudio.common.library.Signature;
import com.osastudio.common.utils.LogUtils;
import com.osastudio.common.utils.Utils;

import org.apache.http.HttpResponse;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class ResourceResultStringRequest extends MapParamsStringRequest {

    public ResourceResultStringRequest(String url,
            Map<String, Object> params, Listener<String> listener) {
        this(Method.POST, url, params, listener);
    }

    public ResourceResultStringRequest(int method, String url,
            Map<String, Object> params, Listener<String> listener) {
        super(method, url, params, listener);
    }

    @Override
    public String getUrl() {
        if (isEncryptEnabled()) {
            return getEncryptedUrl();
        }

        String url = super.getUrl();
        JSONObject jsonObject = new JSONObject();
        if (this.params != null && this.params.size() > 0) {
            jsonObject.putAll(this.params);
        }
        try {
            url = url + "?j=" + URLEncoder.encode(jsonObject.toJSONString(), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        LogUtils.log("TEST", "RESOURCE/NEW URL: " + url);
        return url;
    }

    private String getEncryptedUrl() {
        String url = super.getUrl();
//        Utils.log("TEST", "RESOURCE/URL: " + url);
        return url;
    }

    @Override
    public String getBodyContentType() {
        if (isEncryptEnabled()) {
            return getEncryptedBodyContentType();
        }

        return super.getBodyContentType();
    }

    private String getEncryptedBodyContentType() {
        return "application/x-www-form-urlencoded; charset=" + getParamsEncoding();
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        if (isEncryptEnabled()) {
            return getEncryptedBody();
        }

        return super.getBody();
    }


    public byte[] getEncryptedBody() throws AuthFailureError {
        String paramString = JSON.toJSONString(this.params);
        LogUtils.log("TEST", "RESOURCE/PARAMS: " + paramString);
        long timestamp = System.currentTimeMillis();
        String baseString = new String(Base64.encode(paramString.getBytes()));
        String signature = Signature.sign(baseString, timestamp);
        if (newParams == null) {
            newParams = new HashMap();
            newParams.put("j", baseString);
            newParams.put("timestamp", String.valueOf(timestamp));
            newParams.put("signature", signature);
        }
        byte[] data = encodeParams(newParams, getParamsEncoding());
        Utils.log("TEST", "RESOURCE/ENCRYPTED PARAMS: " + new String(data));
        return data;
    }

    @Override
    public byte[] handleResponse(HttpResponse response, Delivery delivery)
            throws IOException, ServerError {
        return super.handleResponse(response, delivery);
    }

    @Override
    public void run(Context context) {
        // Resource request do not support authenticate feature
        setAuthEnabled(false);
        super.run(context);
    }

}