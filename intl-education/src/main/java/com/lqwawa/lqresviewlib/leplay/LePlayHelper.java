package com.lqwawa.lqresviewlib.leplay;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.lecloud.sdk.config.LeCloudPlayerConfig;
import com.lecloud.sdk.listener.OnInitCmfListener;

import java.util.List;

/**
 * Created by XChen on 2016/12/29.
 * email:man0fchina@foxmail.com
 */

public class LePlayHelper {

    public static boolean cdeInitSuccess;

    public static void init(final Application application) {
        String processName = getProcessName(application.getApplicationContext(), android.os.Process.myPid());
//设置域名LeCloudPlayerConfig.HOST_DEFAULT代表国内版
//LeCloudPlayerConfig.HOST_US代表国际版
//LeCloudPlayerConfig.HOST_GUOGUANG代表国广版
//国内用户都使用默认的LeCloudPlayerConfig.HOST_DEFAULT
        int host = LeCloudPlayerConfig.HOST_DEFAULT;
        if (application.getApplicationInfo().packageName.equals(processName)) {
            //CrashHandler是一个抓取崩溃log的工具类（可选）
            //CrashHandler.getInstance(application);
            try {
                LeCloudPlayerConfig.setHostType(host);
                LeCloudPlayerConfig.init(application.getApplicationContext());
                LeCloudPlayerConfig.setmInitCmfListener(new OnInitCmfListener() {
                    @Override
                    public void onCdeStartSuccess() {
//cde启动成功,可以开始播放
//如果使用remote版本这个方法会回调的晚一些，因为有个下载过程
//只有回调了该方法，才可以正常播放视频（点播，直播）
//建议用户通过cde初始化的回调进行控制，点击开始播放是否创建播放器
                        cdeInitSuccess = true;
                        Log.d("huahua", "onCdeStartSuccess: ");
                    }

                    @Override
                    public void onCdeStartFail() { //cde启动失败,不能正常播放;
//如果使用remote版本则可能是remote下载失败;
//如果使用普通版本,则可能是so文件加载失败导致
                        cdeInitSuccess = false;
                        Log.d("huahua", "onCdeStartFail: ");
                    }

                    @Override
                    public void onCmfCoreInitSuccess() {
//不包含cde的播放框架需要处理
                    }

                    @Override
                    public void onCmfCoreInitFail() {
//不包含cde的播放框架需要处理
                    }

                    @Override
                    public void onCmfDisconnected() {
//cde服务断开,会导致播放失败,重启一次服务
                        try {
                            cdeInitSuccess = false;
                            LeCloudPlayerConfig.init(application.getApplicationContext());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (Exception e) {
// TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    //获取当前进程名字
    private static String getProcessName(Context cxt, int pid) {
        ActivityManager am = (ActivityManager)
                cxt.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps != null) {
            for (ActivityManager.RunningAppProcessInfo procInfo : runningApps) {
                if (procInfo.pid == pid) {
                    return procInfo.processName;
                }
            }
        }
        return null;
    }
}
