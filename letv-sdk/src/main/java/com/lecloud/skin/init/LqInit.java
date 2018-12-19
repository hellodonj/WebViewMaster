package com.lecloud.skin.init;
import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.lecloud.sdk.config.LeCloudPlayerConfig;
import com.lecloud.sdk.listener.OnInitCmfListener;
import java.util.List;

/**
 * Created by E450 on 2017/3/30.
 */

public class LqInit {

    public static String getProcessName(Context cxt, int pid) {
        ActivityManager am = (ActivityManager) cxt.getSystemService(Context.ACTIVITY_SERVICE);
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


    public static void initLetvSdk(final  Context context,final InitResultListener listener){
        String processName = getProcessName(context, android.os.Process.myPid());
        if (context.getApplicationInfo().packageName.equals(processName)) {
            SharedPreferences preferences = context.getSharedPreferences("host", Context
                    .MODE_PRIVATE);
            int host = preferences.getInt("host", LeCloudPlayerConfig.HOST_DEFAULT);
            try {
                LeCloudPlayerConfig.setLogOutputType(LeCloudPlayerConfig.LOG_LOGCAT);
                LeCloudPlayerConfig.setHostType(host);
//                LeCloudPlayerConfig.USE_CDE_PORT = true;
                LeCloudPlayerConfig.setmInitCmfListener(new OnInitCmfListener() {

                    @Override
                    public void onCdeStartSuccess() {
                        //cde启动成功,可以开始播放
                        if(listener!=null){
                            listener.onInitResult(true);
                        }
                        Log.d("huahua", "onCdeStartSuccess: ");
                    }

                    @Override
                    public void onCdeStartFail() {
                        //cde启动失败,不能正常播放;如果使用remote版本则可能是remote下载失败;
                        //如果使用普通版本,则可能是so文件加载失败导致
                        if(listener!=null){
                            listener.onInitResult(false);
                        }
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
                            if(listener!=null){
                                listener.onInitResult(false);
                            }
                            LeCloudPlayerConfig.init(context.getApplicationContext());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                LeCloudPlayerConfig.init(context.getApplicationContext());

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
