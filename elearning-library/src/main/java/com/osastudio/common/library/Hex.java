package com.osastudio.common.library;

public class Hex {

    private static final char[] HEX_CHARS = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f' };

    public static String toHexString(byte[] data) {
        byte[] buf = new byte[data.length * 2];
        int i = 0;
        for (byte b : data) {
            // look up high nibble char
            buf[i++] = (byte) HEX_CHARS[(b >> 4) & 0xf];

            // look up low nibble char
            buf[i++] = (byte) HEX_CHARS[b & 0xf];
        }
        return new String(buf);
    }

}
