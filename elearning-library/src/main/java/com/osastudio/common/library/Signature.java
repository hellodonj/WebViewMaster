package com.osastudio.common.library;

import java.io.UnsupportedEncodingException;

public class Signature {

    private static final String DEFAULT_ENCODING = "UTF-8";


    public static String sign(String data, long timestamp) {
        try {
            byte[] baseBytes = Base64.encode(String.valueOf(timestamp).getBytes(DEFAULT_ENCODING));
            String baseString = new String(baseBytes);
            String dataString = new StringBuilder(data).append(baseString).toString();
            String hashString = Checksum.sha1sum(dataString.getBytes(DEFAULT_ENCODING));
            String result = Checksum.md5sum(hashString.getBytes(DEFAULT_ENCODING));
//            Utils.log("TEST", "getSignature: " + data + " " + timestamp
//                    + " -> " + dataString
//                    + " -> " + hashString
//                    + " -> " + result);
            return result;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

}
