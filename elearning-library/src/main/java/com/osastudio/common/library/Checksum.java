package com.osastudio.common.library;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Checksum {

    public static String md5sum(byte[] data) {
        return toHashString("MD5", data);
    }

    public static String sha1sum(byte[] data) {
        return toHashString("SHA1", data);
    }

    protected static String toHashString(String algorithm, byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            return Hex.toHexString(md.digest(data));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

}
