package com.lqwawa.lqbaselib.net;

import android.content.Context;
import android.text.TextUtils;

import org.apache.http.util.EncodingUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author: wangchao
 * @date: 2018/03/28
 * @desc:
 */
public class ErrorCodeUtil {

    Map<String, String> errorCodeMap = new HashMap<>();

    private ErrorCodeUtil() {

    }

    private static class ErrorCodeUtilHolder {
        private static final ErrorCodeUtil instance = new ErrorCodeUtil();
    }

    public static ErrorCodeUtil getInstance() {
        return ErrorCodeUtilHolder.instance;
    }

    public Map getErrorCodeMap() {
        return errorCodeMap;
    }

    public void init(Context context) {
        String jsonString = getFromAssets(context, getFileName());
        errorCodeMap.clear();
        if (!TextUtils.isEmpty(jsonString)) {
            try {
                JSONObject jsonObject = new JSONObject(jsonString);
                if (jsonObject != null) {
                    JSONArray jsonArray = jsonObject.optJSONArray("data");
                    if (jsonArray != null && jsonArray.length() > 0) {
                        for (int i = 0, len = jsonArray.length(); i < len; i++) {
                            JSONObject subJsonObject = jsonArray.optJSONObject(i);
                            if (subJsonObject != null) {
                                String errorCode = subJsonObject.optString("errorCode");
                                String errorMessage = subJsonObject.optString("errorMessage");
                                if (!TextUtils.isEmpty(errorCode)) {
                                    errorCodeMap.put(errorCode, errorMessage);
                                }
                            }

                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public String getFileName() {
        String language = Locale.getDefault().getLanguage();
        if (language.equals("zh")) {
            return "ErrorMessage.json";
        } else {
            return "ErrorMessage_en.json";
        }
    }

    public String getFromAssets(Context context, String fileName) {
        String result = "";
        try {
            InputStream in = context.getResources().getAssets().open(fileName);
            //获取文件的字节数
            int lenght = in.available();
            //创建byte数组
            byte[] buffer = new byte[lenght];
            //将文件中的数据读到byte数组中
            in.read(buffer);
            result = EncodingUtils.getString(buffer, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
