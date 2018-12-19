package com.lqwawa.lqbaselib.net;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.duowan.mobile.netroid.AuthFailureError;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.duowan.mobile.netroid.NetworkResponse;
import com.duowan.mobile.netroid.Response;
import com.osastudio.common.utils.LogUtils;

import org.apache.http.entity.StringEntity;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Map;

/**
 * <p>
 * 参数信息，辅助帮助类. 传人map数据集合 直接返回Model json串
 * </p>
 *
 * @author shouyi
 * @version 1.0 2014/11/22 17:06
 * @since JDK 1.6
 */
public class PostByMapParamsModelRequest extends ThisStringRequest {
    private Map<String, String> mParams;

    // 传入Post参数的Map集合
    public PostByMapParamsModelRequest(String url, Map<String, String> params, Listener<String> listener) {
        super(Method.POST, url, listener);
        mParams = params;
        LogUtils.logi("", "NetRquest: url: " + url + ", params: " + params);
    }

    // @Override
    // public Map<String, String> getParams() throws AuthFailureError {
    // return mParams;
    // }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        // TODO Auto-generated method stub
        NetErrorResult result = null;
        try {
            String json = new String(response.data, response.charset);
            // String jsString =
            // JSON.toJSONString(json,SerializerFeature.WriteMapNullValue.WriteNullStringAsEmpty);
            LogUtils.logi("", "network back result: \n" + json);
            JSONObject json2 = JSON.parseObject(json);
            result = (NetErrorResult) JSON.parseObject(json, NetErrorResult.class);
            if (result != null) {
                if (!result.HasError) {
                    JSONObject jObject = json2.getJSONObject("Model");
                    return Response.success(jObject.toString(), response);
                } else {
                    Map<String, String> errorHashMap = ErrorCodeUtil.getInstance().getErrorCodeMap();
                    if (errorHashMap != null && errorHashMap.size() > 0 && !TextUtils.isEmpty(result.ErrorMessage)
                        && errorHashMap.containsKey(result.ErrorMessage)) {
                        result.ErrorMessage = errorHashMap.get(result.ErrorMessage);
                    }
                    return Response.error(new NetroidError(JSON.toJSONString(result)));
                }
            } else {
                result = new NetErrorResult(true, "pasre data error");
                return Response.error(new NetroidError(JSON.toJSONString(result)));
            }
        } catch (Exception e) {
            // TODO: handle exception
            result = new NetErrorResult(true, "pasre data error");
            Response.error(new NetroidError(JSON.toJSONString(result)));
        }
        result = new NetErrorResult(true, "network error");
        return Response.error(new NetroidError(JSON.toJSONString(result)));
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        // TODO Auto-generated method stub
        String bodyString = "";
        String paramStr = JSON.toJSONString(mParams);
        try {
            StringEntity entity = new StringEntity(paramStr, "utf-8");
            entity.setContentType("application/json");
            bodyString = InputStreamTOString(entity.getContent());
        } catch (Exception e) {
            // TODO: handle exception
        }
        return bodyString.getBytes();
    }

    @Override
    public String getBodyContentType() {
        // TODO Auto-generated method stub
        return "application/json";
    }

    public static String InputStreamTOString(InputStream in) throws Exception {
        int BUFFER_SIZE = 8 * 1024;
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] data = new byte[BUFFER_SIZE];
        int count = -1;
        while ((count = in.read(data, 0, BUFFER_SIZE)) != -1)
            outStream.write(data, 0, count);

        data = null;
        return new String(outStream.toByteArray(), "utf-8");
    }
}