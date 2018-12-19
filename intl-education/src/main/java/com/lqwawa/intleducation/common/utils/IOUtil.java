package com.lqwawa.intleducation.common.utils;

import java.io.Closeable;
import java.io.IOException;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 流工具类
 * @date 2018/04/09 14:15
 * @history v1.0
 * **********************************
 */
public final class IOUtil {

    /**
     * 关闭IO
     *
     * @param closeables closeables
     */
    public static void closeIO(final Closeable... closeables) {

        if (closeables == null) return;
        for (Closeable closeable : closeables) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 安静关闭IO
     *
     * @param closeables closeables
     */
    public static void closeIOQuietly(final Closeable... closeables) {
        if (closeables == null) return;
        for (Closeable closeable : closeables) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (IOException ignored) {
                }
            }
        }
    }
}
