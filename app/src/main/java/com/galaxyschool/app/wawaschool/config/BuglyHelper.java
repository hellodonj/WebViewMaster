package com.galaxyschool.app.wawaschool.config;

import android.content.Context;
import android.content.Intent;

import com.galaxyschool.app.wawaschool.HomeActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.UpdateActivity;
import com.osastudio.apps.Config;
import com.osastudio.common.utils.LogUtils;
import com.osastudio.common.utils.Utils;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;
import com.tencent.bugly.beta.UpgradeInfo;
import com.tencent.bugly.beta.upgrade.UpgradeListener;
import com.tencent.bugly.beta.upgrade.UpgradeStateListener;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.File;

/**
 * <br/>================================================
 * <br/> 作    者：Blizzard-liu
 * <br/> 版    本：1.0
 * <br/> 创建日期：2017/12/21 11:41
 * <br/> 描    述：
 * <br/> 修订历史：
 * <br/>================================================
 */

public class BuglyHelper {
    private static final String TAG = "BuglyHelper";

    private static final String ROOT_DIR_NAME = ".AppUpdater";
    /**
     * 为了保证运营数据的准确性，建议不要在异步线程初始化Bugly。
     * @param context
     */
    public static void init(final Context context) {
        if (!Config.UPLOAD_BUGLY_EXCEPTION) {
            return;
        }
        /***** Beta高级设置 *****/

        /**
         * true表示app启动自动初始化升级模块; false不会自动初始化;
         * 开发者如果担心sdk初始化影响app启动速度，可以设置为false，
         * 在后面某个时刻手动调用Beta.init(getApplicationContext(),false);
         */
        Beta.autoInit = true;

        /**
         * true表示初始化时自动检查升级; false表示不会自动检查升级,需要手动调用Beta.checkUpgrade()方法;
         */
        Beta.autoCheckUpgrade = true;

        /**
         * 设置升级检查周期为60s(默认检查周期为0s)，60s内SDK不重复向后台请求策略);
         */
        Beta.upgradeCheckPeriod = 60 * 1000;
        /**
         * 设置启动延时为1s（默认延时3s），APP启动1s后初始化SDK，避免影响APP启动速度;
         */
        Beta.initDelay = 3 * 1000;
        /**
         * 设置通知栏大图标，largeIconId为项目中的图片资源;
         */
        Beta.largeIconId = R.drawable.ic_launcher;
        /**
         * 设置状态栏小图标，smallIconId为项目中的图片资源Id;
         */
        Beta.smallIconId = R.drawable.ic_launcher;
        /**
         * 设置更新弹窗默认展示的banner，defaultBannerId为项目中的图片资源Id;
         * 当后台配置的banner拉取失败时显示此banner，默认不设置则展示“loading“;
         */
        Beta.defaultBannerId = R.drawable.ic_launcher;
        /**
         * 设置sd卡的Download为更新资源保存目录;
         * 后续更新资源会保存在此目录，需要在manifest中添加WRITE_EXTERNAL_STORAGE权限;
         */
        Beta.storageDir =  new File(context.getExternalCacheDir(), ROOT_DIR_NAME);
        /**
         * 已经确认过的弹窗在APP下次启动自动检查更新时会再次显示;
         */
        Beta.showInterruptedStrategy = true;
        /**
         * 设置Wifi下自动下载,默认值为false。
         */
        Beta.autoDownloadOnWifi = true;
        /**
         * 设置是否显示消息通知 如果你不想在通知栏显示下载进度，你可以将这个接口设置为false，默认值为true。
         */
        Beta.enableNotification = false;
        /**
         * 升级SDK默认是开启热更新能力的，如果你不需要使用热更新，可以将这个接口设置为false。
         */
        Beta.enableHotfix = false;
        /**
         * 只允许在MainActivity上显示更新弹窗，其他activity上不显示弹窗; 不设置会默认所有activity都可以显示弹窗;
         */
        Beta.canShowUpgradeActs.add(HomeActivity.class);

         /*在application中初始化时设置监听，监听策略的收取*/
        Beta.upgradeListener = new UpgradeListener() {
            @Override
            public void onUpgrade(int ret, UpgradeInfo strategy, boolean isManual, boolean isSilence) {
                if (strategy != null) {
                    Intent i = new Intent();
                    i.setClass(context, UpdateActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(i);
                }
            }
        };
        /* 设置更新状态回调接口 */
        Beta.upgradeStateListener = new UpgradeStateListener() {
            /**
             * 更新成功
             * @param isManual  true:手动检查 false:自动检查
             */
            @Override
            public void onUpgradeSuccess(boolean isManual) {
                LogUtils.logd(TAG,"UPGRADE_SUCCESS");
            }
            /**
             * 更新失败
             * @param isManual  true:手动检查 false:自动检查
             */
            @Override
            public void onUpgradeFailed(boolean isManual) {
                LogUtils.logd(TAG,"UPGRADE_FAILED");
            }
            /**
             * 正在更新
             * @param isManual
             */
            @Override
            public void onUpgrading(boolean isManual) {
                LogUtils.logd(TAG,"UPGRADE_CHECKING");
            }

            @Override
            public void onDownloadCompleted(boolean b) {
                LogUtils.logd(TAG,"下载失败");
            }
            /**
             * 没有更新
             * @param isManual
             */
            @Override
            public void onUpgradeNoVersion(boolean isManual) {
                LogUtils.logd(TAG,"UPGRADE_NO_VERSION");
            }
        };

        // 获取当前包名
        String packageName = context.getApplicationContext().getPackageName();
        // 获取当前进程名
        String processName = Utils.getProcessName(android.os.Process.myPid());
        // 设置是否为上报进程
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(context);
        strategy.setUploadProcess(processName == null || processName.equals(packageName));

        // 初始化Bugly
        /**
         * 第三个参数为SDK调试模式开关，调试模式的行为特性如下：

                 输出详细的Bugly SDK的Log；
                 每一条Crash都会被立即上报；
                 自定义日志将会在Logcat中输出。
                 建议在测试阶段建议设置成true，发布时设置为false。

          第四个参数为增加上报进程控制:
                 如果App使用了多进程且各个进程都会初始化Bugly（例如在Application类onCreate()中初始化Bugly），那么每个进程下的Bugly都会进行数据上报，造成不必要的资源浪费。

                 因此，为了节省流量、内存等资源，建议初始化的时候对上报进程进行控制，只在主进程下上报数据：判断是否是主进程（通过进程名是否为包名来判断），并在初始化Bugly时增加一个上报进程的策略配置。
         */
        Bugly.init(context, AppSettings.BUGLY_APPID,AppSettings.DEBUG,strategy);
    }
}
