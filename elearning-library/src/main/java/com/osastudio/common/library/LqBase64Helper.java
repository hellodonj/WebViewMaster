package com.osastudio.common.library;
import java.io.UnsupportedEncodingException;
public class LqBase64Helper {
    //加密
    public static String getEncoderString(String str) {
        String result = "";
        if( str != null) {
            try {
                result = new String(LqBase64.encode(str.getBytes("utf-8"), Base64.URL_SAFE),
                        "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    // 解密
    public static String getDecodeString(String str) {
        String result = "";
        if (str != null) {
            try {
                result = new String(LqBase64.decode(str, Base64.URL_SAFE), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
