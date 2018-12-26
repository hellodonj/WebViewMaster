package com.galaxyschool.app.wawaschool;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Process;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import com.alibaba.fastjson.JSON;
import com.galaxyschool.app.wawaschool.bitmapmanager.ThumbnailManager;
import com.galaxyschool.app.wawaschool.common.PrefsManager;
import com.galaxyschool.app.wawaschool.common.SharedPreferencesHelper;
import com.galaxyschool.app.wawaschool.common.Utils;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.account.AccountListener;
import com.galaxyschool.app.wawaschool.jpush.PushUtils;
import com.galaxyschool.app.wawaschool.pojo.SchoolInfo;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.lecloud.skin.init.InitResultListener;
import com.lecloud.skin.init.LqInit;
import com.lqwawa.libs.appupdater.instance.DefaultUpdateService;
import com.lqwawa.libs.filedownloader.DownloadService;
import com.lqwawa.lqbaselib.net.ErrorCodeUtil;
import com.oosic.apps.iemaker.base.evaluate.EvaluateHelper;
import com.osastudio.apps.Config;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class MyApplication extends com.lqwawa.intleducation.MainApplication  {
    /**
     * It is the design ui width.
     */
    private static final int DESIGN_UI_WIDTH = 1920;

    /**
     * It is the desgin ui height.
     */
    private static final int DESIGN_UI_HEIGHT = 1080;

    /**
     * It is the display.
     */
    private static Display mDisplay;

    /**
     * The exact physical pixels per inch of the screen in the X dimension.
     */
    private static float xdpi;

    /**
     * The exact physical pixels per inch of the screen in the Y dimension.
     */
    private static float ydpi;

    /**
     * The absolute width of the display in pixels.
     */
    private static int wpixels;

    /**
     * The absolute height of the display in pixels.
     */
    private static int hpixels;

    /**
     * It is the screen width rate.
     */
    private static float mSWR;

    /**
     * It is the screen height rate.
     */
    private static float mSHR;

    /**
     * It is the screen rate. It is the smaller one between screen width rate and
     * screen height rate.
     */
    private static float mSR;

    private static float mDensity = 1.0f;
    private static boolean cdeInitSuccess;

    private static ThumbnailManager mThumbnailManager;
    private PrefsManager prefsManager;
    private String mUUid;
    private AccountListener accountListener;
    public static String SCREENING_START_DATE,SCREENING_END_DATE;

    private Thread.UncaughtExceptionHandler uncaughtExceptionHandler =
            new Thread.UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread thread, Throwable ex) {
                    StringWriter sw = new StringWriter();
                    ex.printStackTrace(new PrintWriter(sw));
                    System.err.println(sw);

                    try {
                        String timeString = new SimpleDateFormat("yyyyMMddHHmmssSSS")
                                .format(Calendar.getInstance().getTime());
                        File file = new File(Utils.getLogDir(), "crash@" +  timeString + ".log");
                        FileOutputStream fileOutputStream = new FileOutputStream(file, true);
                        PrintStream printStream = new PrintStream(fileOutputStream);
                        ex.printStackTrace(printStream);
                        printStream.flush();
                        printStream.close();
                        fileOutputStream.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Process.killProcess(Process.myPid());
                    System.exit(1);
                }
            };

//    public static boolean isDebugVersion() {
//        return ServerUrl.VERSION.equals(ServerUrl.VER_DEBUG);
//    }

    public static boolean isSimulateVersion() {
        return ServerUrl.VERSION.equals(ServerUrl.VER_SIMULATE);
    }

    public static boolean isReleaseVersion() {
        return ServerUrl.VERSION.equals(ServerUrl.VER_RELEASE);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        com.osastudio.common.utils.Utils.enableLogging(!isReleaseVersion());

        // 设置apk升级服务器的工作模式
        // 原先该设置放在权限申请成功之后，因小米，魅族，华为等手机仍沿用自己的权限控制系统，故无法设置该工作方式，
        // 导致这些手机不提示升级
        DefaultUpdateService.setDebugMode(!isReleaseVersion());

        Thread.setDefaultUncaughtExceptionHandler(uncaughtExceptionHandler);

        EvaluateHelper.setRequestEvaluateTextUrl(ServerUrl.GET_SPEECH_ASSESSMENT_TEXT_BASE_URL);

//        startUpdateService();
//        startDownloadService();
        final WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        mDisplay = wm.getDefaultDisplay();

        wpixels = mDisplay.getWidth();
        hpixels = mDisplay.getHeight();

        if (wpixels < hpixels) {
            int temp = hpixels;
            hpixels = wpixels;
            wpixels = temp;
        }

        mSWR = (float) wpixels / (float) DESIGN_UI_WIDTH;
        mSHR = (float) hpixels / (float) DESIGN_UI_HEIGHT;
        mSR = (mSWR > mSHR) ? mSHR : mSWR;

        DisplayMetrics dm = new DisplayMetrics();
        mDisplay.getMetrics(dm);
        mDensity = dm.density;
        xdpi = dm.xdpi;
        ydpi = dm.ydpi;
        wpixels = dm.widthPixels;
        hpixels = dm.heightPixels;
        if (wpixels < hpixels) {
            int temp = hpixels;
            hpixels = wpixels;
            wpixels = temp;
        }

        // initImageLoader(getApplicationContext());

//        mUUid = getMyUUID();
//        startPushService();

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mUUid = getMyUUID();
            if (!Config.UPDATE_FOR_BUGLY) {
                startUpdateService();
            }
            startDownloadService();
            LqInit.initLetvSdk(this, new InitResultListener() {
                @Override
                public void onInitResult(boolean result) {
                    cdeInitSuccess=result;
                }
            });
        }

        createNomediaFile();

        initUmengShare();

        ErrorCodeUtil.getInstance().init(getApplicationContext());
        //极光推送
        PushUtils.init(getApplicationContext());
    }

    public static float getXDPI() {
        return xdpi;
    }

    public static float getYDPI() {
        return ydpi;
    }

    public static int getWPixels() {
        return wpixels;
    }

    public static int getHPixels() {
        return hpixels;
    }

    /**
     * It gets the screen width rate.
     *
     * @return It returns the screen width rate.
     */
    public static float getSWR() {
        return mSWR;
    }

    /**
     * It gets the screen height rate.
     *
     * @return It returns the screen height rate.
     */
    public static float getSHR() {
        return mSHR;
    }

    /**
     * It gets the screen rate.
     *
     * @return It returns the screen rate.
     */
    public static float getSR() {
        return mSR;
    }

    public static float getDensity() {
        if (mDensity <= 0) {
            mDensity = 1.0f;
        }
        return mDensity;
    }

    public void setAccountListener(AccountListener listener) {
        this.accountListener = listener;
    }

    public AccountListener getAccountListener() {
        return this.accountListener;
    }

    public boolean setUserInfo(UserInfo userInfo) {
        return getPrefsManager().setUserInfo(userInfo);
    }

    public UserInfo getUserInfo() {
        return getPrefsManager().getUserInfo();
    }

    public boolean clearUserInfo() {
        return getPrefsManager().clearUserInfo();
    }

    public boolean clearLoginUserInfo() {
        getPrefsManager().clearUserLoginInfo();
        getPrefsManager().clearCurrSchoolId();
        SharedPreferencesHelper.clearUserData(this);
        return true;
    }

    public static void saveSchoolInfoList(Context context, List<SchoolInfo> schoolInfos) {
    	if (schoolInfos != null) {
    		SharedPreferencesHelper.setString(context, "SchoolInfoList", JSON.toJSONString(schoolInfos));
		}
    }

    public static List<SchoolInfo> getSchoolInfoList(Context context) {
    	String schoolInfoString = SharedPreferencesHelper.getString(context, "SchoolInfoList");
    	if (TextUtils.isEmpty(schoolInfoString)) {
			return null;
		}
    	List<SchoolInfo> list = JSON.parseArray(schoolInfoString, SchoolInfo.class);
    	return list;
    }
    public String getMemberId() {
        if (getUserInfo() != null) {
            return getUserInfo().getMemberId();
        }

        return null;
    }

    public boolean hasLogined() {
        return getUserInfo() != null
            && !TextUtils.isEmpty(getUserInfo().getMemberId());
    }

    public static ThumbnailManager getThumbnailManager(Activity activity) {
        createThumbnailManager(activity);
        return mThumbnailManager;
    }

    static void createThumbnailManager(Activity activity) {
        if (mThumbnailManager == null) {
            mThumbnailManager = new ThumbnailManager(
                activity, 0,
                (int) (getHPixels() / 4), 0);
        }
    }

    static void destroyThumbnailManager() {
        if (mThumbnailManager != null) {
            mThumbnailManager = null;
        }
    }

    public PrefsManager getPrefsManager() {
        if (prefsManager == null) {
            prefsManager = new PrefsManager(this);
        }
        return prefsManager;
    }

    public String getUUid() {
        return mUUid;
    }

    private String getMyUUID() {
        final TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
        final String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(
            getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String uniqueId = deviceUuid.toString();
        Log.d("debug", "uuid=" + uniqueId);

        return uniqueId;
    }

    public  void setMyUUID(Context context) {
        String uniqueId = null;
        if(context != null) {
            final TelephonyManager tm = (TelephonyManager) context.getSystemService
                    (Context.TELEPHONY_SERVICE);
            final String tmDevice, tmSerial, androidId;
            tmDevice = "" + tm.getDeviceId();
            tmSerial = "" + tm.getSimSerialNumber();
            androidId = "" + android.provider.Settings.Secure.getString(
                    context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
            uniqueId = deviceUuid.toString();
            Log.d("debug", "uuid=" + uniqueId);
        }
        mUUid = uniqueId;
    }

    private void createNomediaFile() {
       File dirFile = new File(Utils.DATA_FOLDER);
       if(!dirFile.exists()) {
           dirFile.mkdirs();
       }
       File file = new File(dirFile.getParent(), Utils.NO_MEDIA_FILE);
       try {
           if (!file.exists()) {
               file.createNewFile();
           }
           FileOutputStream nomediaFos = new FileOutputStream(file);
           nomediaFos.flush();
           nomediaFos.close();
       } catch (IOException e) {
           e.printStackTrace();
       }
    }

    /**
     * 配置友盟分享
     */
    private void initUmengShare() {
        UMConfigure.init(this,"5535a7fce0f55ad0d2001919","lqwawa",UMConfigure
                .DEVICE_TYPE_PHONE, "");
        PlatformConfig.setWeixin(AppSettings.WEIXIN_APPID, AppSettings.WEIXIN_APPSECRET);
        PlatformConfig.setQQZone(AppSettings.QQ_APPID, AppSettings.QQ_APPKEY);
//        PlatformConfig.setQQZone("100424468", "c7394704798a158208a74ab60104f0ba");//测试用
        UMShareAPI.get(this);
    }

    // for file downloader
    public ComponentName startDownloadService() {
        DownloadService.setRootDir(new File(Utils.DATA_FOLDER, DownloadService.ROOT_DIR_NAME));
        return startService(new Intent(getApplicationContext(),
                DownloadService.class));
    }

    public boolean bindDownloadService(Context context, ServiceConnection conn) {
        if (conn != null) {
            return bindService(new Intent(context, DownloadService.class), conn,
                    Context.BIND_AUTO_CREATE);
        }
        return false;
    }

    public void unbindDownloadService(ServiceConnection conn) {
        if (conn != null) {
            unbindService(conn);
        }
    }

    public boolean stopDownloadService() {
        return stopService(new Intent(getApplicationContext(),
                DownloadService.class));
    }

    // for app updater
    public ComponentName startUpdateService() {
        DefaultUpdateService.setDebugMode(!isReleaseVersion());
        return startService(new Intent(getApplicationContext(), DefaultUpdateService.class));
    }

    public boolean bindUpdateService(Context context, ServiceConnection conn) {
        if (conn != null) {
            return bindService(new Intent(context, DefaultUpdateService.class), conn,
                    Context.BIND_AUTO_CREATE);
        }
        return false;
    }

    public void unbindUpdateService(ServiceConnection conn) {
        if (conn != null) {
            unbindService(conn);
        }
    }

    public boolean stopUpdateService() {
        return stopService(new Intent(getApplicationContext(), DefaultUpdateService.class));
    }


    public static boolean getCdeInitSuccess(){
        return cdeInitSuccess;
    }
    public void setCdeInitSuccess(boolean cdeInitSuccess){
        this.cdeInitSuccess=cdeInitSuccess;
    }
}
