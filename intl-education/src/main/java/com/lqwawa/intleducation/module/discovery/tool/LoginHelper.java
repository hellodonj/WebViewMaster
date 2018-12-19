package com.lqwawa.intleducation.module.discovery.tool;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by XChen on 2017/9/5.
 * email:man0fchina@foxmail.com
 */

public class LoginHelper {
    public static int RS_LOGIN = 1025;
    public static void enterLogin(Activity activity){
        if(activity == null){
            return;
        }
        Bundle args = new Bundle();
        args.putBoolean("isLogin", false);
        args.putBoolean("enterHomeAfterLogin", false);
        Intent intent = new Intent();
        intent.setClassName(activity.getPackageName(),
                "com.galaxyschool.app.wawaschool" + ".AccountActivity");
        intent.putExtras(args);
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivityForResult(intent, RS_LOGIN);
    }
}
