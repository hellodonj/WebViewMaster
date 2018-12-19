package com.osastudio.common.library;

import java.io.UnsupportedEncodingException;

public class EncryptUtils {

    private static final String DEFAULT_ENCODING = "UTF-8";


    public static String encryptParameters(String paramsString) {
        try {
            String result = new String(Base64.encode(paramsString.getBytes(DEFAULT_ENCODING)));
//            Utils.log("TEST", "encryptParameters: " + paramsString
//                    + " -> " + result);
            return result;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String decryptParameters(String paramsString) {
        try {
            String result = new String(Base64.decode(paramsString.getBytes(DEFAULT_ENCODING)));
//            Utils.log("TEST", "decryptParameters: " + paramsString
//                    + " -> " + result);
            return result;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String encryptPassword(String password) {
        try {
            String hexString = Hex.toHexString(password.getBytes(DEFAULT_ENCODING));
            byte[] baseBytes = Base64.encode(hexString.getBytes(DEFAULT_ENCODING));
            String baseString = new String(baseBytes);
            String hashString = Checksum.sha1sum(baseBytes);
            String result = Checksum.md5sum(hashString.getBytes(DEFAULT_ENCODING));
//            Utils.log("TEST", "encryptPassword: " + password
//                    + " -> " + hexString
//                    + " -> " + baseString
//                    + " -> " + hashString
//                    + " -> " + result);
            return result;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

}
