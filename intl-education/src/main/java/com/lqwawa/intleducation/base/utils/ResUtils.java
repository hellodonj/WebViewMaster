package com.lqwawa.intleducation.base.utils;

import android.content.Context;

/**
 * Created by XChen on 2017/1/13.
 * email:man0fchina@foxmail.com
 */

public class ResUtils {

    public static int getResourceId(Context context, String name, String type) {
        try {
            String packageName = context.getPackageName();
            return context.getResources().getIdentifier(name, type, packageName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
