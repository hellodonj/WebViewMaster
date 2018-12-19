package com.osastudio.common.library;

public class Md5FileNameGenerator {

    public static String generate(String key) {
        return Checksum.md5sum(key.getBytes());
    }

}
