package com.galaxyschool.app.wawaschool.fragment.account;


import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.ApplicationModelSettingActivity;
import com.galaxyschool.app.wawaschool.CommonFragmentActivity;
import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.UpdateActivity;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.AppUtils;
import com.galaxyschool.app.wawaschool.common.PrefsManager;
import com.galaxyschool.app.wawaschool.common.ShareUtils;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.common.Utils;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.ApplicationModelSettingFragment;
import com.galaxyschool.app.wawaschool.fragment.BaseFragment;
import com.galaxyschool.app.wawaschool.fragment.NoticeAvoidDisturbFragment;
import com.galaxyschool.app.wawaschool.fragment.WawatvHelpListFragment;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.views.ToolbarTopView;
import com.lqwawa.apps.views.switchbutton.SwitchButton;
import com.lqwawa.intleducation.lqpay.util.ThreadManager;
import com.lqwawa.libs.appupdater.AppInfo;
import com.lqwawa.libs.appupdater.UpdateService;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.oosic.apps.share.ShareInfo;
import com.oosic.apps.share.ShareItem;
import com.oosic.apps.share.ShareType;
import com.osastudio.apps.Config;
import com.osastudio.common.utils.CleanUtils;
import com.osastudio.common.utils.FileUtils;
import com.tencent.bugly.beta.Beta;
import com.tencent.bugly.beta.UpgradeInfo;
import com.umeng.socialize.media.UMImage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SettingFragment extends BaseFragment implements View.OnClickListener,
        CompoundButton.OnCheckedChangeListener {

    public static final String TAG = SettingFragment.class.getSimpleName();

    public static final int REQUEST_CODE_APPLICATION_MODEL_SETTING = 10000;

    public static final String CACHE_FOLDER = Utils.CACHE_FOLDER;
    public static final String PIC_TEMP_FOLDER = Utils.TEMP_FOLDER;
    public static final String DOWNLOAD_TEMP_FOLDER = Utils.TEMP_FOLDER;
    public static final String ONLINE_FOLDER = Utils.ONLINE_FOLDER;
    public static final String LQ_TEMP_FOLDER = Utils.LQ_TEMP_FOLDER;
    public static final String COURSESLIDETEMP = Utils.ROOT+"/CourseSlideTemp/";
    public static final String COURSESLIDETEMP2 = Utils.ROOT+"/.CourseSlideTemp/";
    public static final String APPUPDATER = Utils.ROOT+"/.AppUpdater/";
    public static String DATA_CACHE_FOLDER;


    public static final int MSG_CACHE_SIZE = 101;
    public static final int MSG_CLEAR_CACHE = 102;

    private TextView currentModelTxt;
    private TextView cacheSizeTxt;
    private View ivUpdate;
    
    private MyApplication myApp;
    private PrefsManager prefsManager;
    private boolean isAutoUpload;
    private AppInfo appInfo;
    private boolean isNeedClear = true;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ThreadManager.shutdown();
            switch (msg.what) {
                case MSG_CACHE_SIZE:
                    cacheSizeTxt.setText((CharSequence) msg.obj);
                    String key = "0.00B";
                    if (key.equals(msg.obj)) {
                        isNeedClear = false;
                    }
                    dismissLoadingDialog();
                    break;
                case MSG_CLEAR_CACHE:
                    TipsHelper.showToast(getMyApplication(),getString(R.string.str_already_clear_cache));
                    initCacheSize();
                default:
                    break;
            }

        }
    };
    private UpgradeInfo upgradeInfo;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_setting, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        myApp = getMyApplication();
        prefsManager = myApp.getPrefsManager();
        File cacheFile = StorageUtils.getCacheDirectory(getActivity());
        if (cacheFile != null) {
            String cachePath = cacheFile.getAbsolutePath();
            if (!TextUtils.isEmpty(cachePath)) {
                DATA_CACHE_FOLDER = cachePath;
            }
        }
        initViews();
        if (!Config.UPDATE_FOR_BUGLY) {
            bindUpdateService();
        } else {
            /***** 获取升级信息 *****/
            loadUpgradeInfo();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void initViews() {
        View rootView = getView();
        if (rootView != null) {
            ToolbarTopView toolbarTopView = (ToolbarTopView) rootView.findViewById(R.id
                    .toolbartopview);
            toolbarTopView.getBackView().setVisibility(View.VISIBLE);
            toolbarTopView.getTitleView().setText(R.string.setting);
            toolbarTopView.getBackView().setOnClickListener(this);

            View logoutLayout = rootView.findViewById(R.id.setting_logout_btn);
            logoutLayout.setOnClickListener(this);
            View avoidDisturbLayout = rootView.findViewById(R.id.avoid_disturb_layout);
            avoidDisturbLayout.setOnClickListener(this);
            SwitchButton switchButton = (SwitchButton) rootView.findViewById(R.id
                    .setting_switch_btn);
            switchButton.setOnCheckedChangeListener(this);
            isAutoUpload = prefsManager.getAutoUploadResource();
            switchButton.setChecked(isAutoUpload);

            //应用场景
            View applicationModelLayout = rootView.findViewById(R.id.application_model_layout);
            applicationModelLayout.setOnClickListener(this);
            currentModelTxt = (TextView) rootView.findViewById(R.id.choose_model);

            //服务与帮助
            View helpLayout = rootView.findViewById(R.id.layout_help);
            helpLayout.setOnClickListener(this);

            //推荐
            View recommendLayout = rootView.findViewById(R.id.layout_recommend);
            recommendLayout.setOnClickListener(this);

            //关于我们
            View aboutLayout = rootView.findViewById(R.id.layout_about_us);
            aboutLayout.setOnClickListener(this);

            //新版本检测
            View updateLayout = rootView.findViewById(R.id.layout_check_update);
            updateLayout.setOnClickListener(this);

            //小红点
            ivUpdate = rootView.findViewById(R.id.iv_check_update);
            //当前版本
            TextView versionCodeTxt = (TextView) rootView.findViewById(R.id.tv_check_code);
            versionCodeTxt.setText(AppUtils.getVersionName(getContext()));

            //清理缓存
            rootView.findViewById(R.id.layout_clean_cache).setOnClickListener(this);
            cacheSizeTxt = (TextView) rootView.findViewById(R.id.tv_cache_size);
            //计算缓存大小
            initCacheSize();

            //初始化应用场景的数据
            initApplicationData();
        }
    }


    /**
     * 计算缓存大小
     */
    private void initCacheSize() {
        Runnable thread = new Runnable() {
            @Override
            public void run() {
                //判断目录是否存在,不存在则创建
                FileUtils.createOrExistsDir(CACHE_FOLDER);
                FileUtils.createOrExistsDir(PIC_TEMP_FOLDER);
                FileUtils.createOrExistsDir(DOWNLOAD_TEMP_FOLDER);
                FileUtils.createOrExistsDir(ONLINE_FOLDER);
                FileUtils.createOrExistsDir(LQ_TEMP_FOLDER);

                //路径可能为空
                long dirLength = FileUtils.getDirLength(DATA_CACHE_FOLDER);
                long dirLength1 = FileUtils.getDirLength(COURSESLIDETEMP);
                long dirLength2 = FileUtils.getDirLength(COURSESLIDETEMP2);
                long dirLength3 = FileUtils.getDirLength(APPUPDATER);

                dirLength = getDirLength(dirLength);
                dirLength1 = getDirLength(dirLength1);
                dirLength2 = getDirLength(dirLength2);
                dirLength3 = getDirLength(dirLength3);

                //计算文件夹大小
                String cacheSize = FileUtils.byte2FitMemorySize
                                 (FileUtils.getDirLength(CACHE_FOLDER)
                                + FileUtils.getDirLength(PIC_TEMP_FOLDER)
                                + FileUtils.getDirLength(ONLINE_FOLDER)
                                + FileUtils.getDirLength(LQ_TEMP_FOLDER)
                                + dirLength
                                + dirLength1
                                + dirLength2
                                + dirLength3
                                + FileUtils.getDirLength(DOWNLOAD_TEMP_FOLDER));


                Message message = mHandler.obtainMessage();
                message.what = MSG_CACHE_SIZE;
                message.obj = cacheSize;
                mHandler.sendMessage(message);
            }
        };

        ThreadManager.execute(thread);
    }

    private long getDirLength(long dirLength) {
        if (dirLength == -1) {
            dirLength = 0;
        }
        return dirLength;
    }

    private void initApplicationData() {
        if (prefsManager != null) {
            String tempData = prefsManager.getCurrentApplicationModel(getActivity(), getMemeberId());
            int currentChooseModel = 0;
            if (!TextUtils.isEmpty(tempData)) {
                currentChooseModel = Integer.valueOf(tempData);
            }
            if (currentChooseModel == ApplicationModelSettingFragment.ApplicationModel.CAMPUS_MODEL) {
                currentModelTxt.setText(getString(R.string.campus_model));
            } else {
                currentModelTxt.setText(getString(R.string.general_model));
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_top_back_btn:
                finishActivity();
                break;
            case R.id.avoid_disturb_layout:
                CommonFragmentActivity.start(getActivity(), NoticeAvoidDisturbFragment.class);
                break;
            case R.id.setting_logout_btn:
                //退出账户
                ActivityUtils.exit(getActivity(), false);
                break;
            //服务与帮助
            case R.id.layout_help:
                CommonFragmentActivity.start(SettingFragment.this, WawatvHelpListFragment.class);
                break;
            //推荐
            case R.id.layout_recommend:
                shareApp();
                break;
            //关于我们
            case R.id.layout_about_us:
                CommonFragmentActivity.start(SettingFragment.this, AboutFragment.class);
                break;
            //新版本检测
            case R.id.layout_check_update:
                if (!Config.UPDATE_FOR_BUGLY) {
                    if (updateService != null && appInfo != null) {

                        updateService.checkUpdate(appInfo, true);
                    }

                } else {
                    /***** 检查更新 *****/
                    if (upgradeInfo != null) {
                        Intent i = new Intent();
                        i.setClass(getActivity(), UpdateActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getActivity().startActivity(i);
                    } else {
                        TipMsgHelper.getInstance().showOneTips(getContext(),getString(R.string.au_already_newest_version));
                    }
                }

                break;
            //应用场景
            case R.id.application_model_layout:
                ApplicationModelSettingActivity.start(SettingFragment.this,
                        REQUEST_CODE_APPLICATION_MODEL_SETTING);
                break;
            //清理缓存
            case R.id.layout_clean_cache:
                clearCache();
                break;
            default:
                break;
        }
    }

    /**
     * 清理缓存
     */
    private void clearCache() {
        if (!isNeedClear) {
            //缓存为空,不用清理
            return;
        }
        showLoadingDialog(getString(R.string.str_clear_cache), false);
        Runnable thread = new Runnable() {
            @Override
            public void run() {
                CleanUtils.cleanCustomCache(CACHE_FOLDER);
                CleanUtils.cleanCustomCache(PIC_TEMP_FOLDER);
                CleanUtils.cleanCustomCache(DOWNLOAD_TEMP_FOLDER);
                CleanUtils.cleanCustomCache(ONLINE_FOLDER);
                CleanUtils.cleanCustomCache(DATA_CACHE_FOLDER);
                CleanUtils.cleanCustomCache(LQ_TEMP_FOLDER);
                //删除文件夹
                FileUtils.deleteDir(COURSESLIDETEMP);
                FileUtils.deleteDir(COURSESLIDETEMP2);
                FileUtils.deleteDir(APPUPDATER);

                Message message = mHandler.obtainMessage();
                message.what = MSG_CLEAR_CACHE;
                mHandler.sendMessage(message);

            }
        };

        ThreadManager.execute(thread);
    }

    private void shareApp() {
        ShareInfo shareInfo = new ShareInfo();
        shareInfo.setTitle(getString(R.string.recommend_app));
        shareInfo.setContent(getString(R.string.app_name));
        shareInfo.setTargetUrl(ServerUrl.RECOMMEND_APP_URL);
        UMImage umImage = new UMImage(getActivity(), R.drawable.ic_launcher);
        shareInfo.setuMediaObject(umImage);
        ShareUtils shareUtils = new ShareUtils(getActivity());
        List<ShareItem> shareItems = new ArrayList<ShareItem>();
        shareItems.add(new ShareItem(com.oosic.apps.share.R.string.wechat_friends,
                com.oosic.apps.share.R.drawable.umeng_share_wechat_btn,
                ShareType.SHARE_TYPE_WECHAT));
        shareItems.add(new ShareItem(com.oosic.apps.share.R.string.wxcircle,
                com.oosic.apps.share.R.drawable.umeng_share_wxcircle_btn,
                ShareType.SHARE_TYPE_WECHATMOMENTS));
        shareItems.add(new ShareItem(com.oosic.apps.share.R.string.qq_friends,
                com.oosic.apps.share.R.drawable.umeng_share_qq_btn,
                ShareType.SHARE_TYPE_QQ));
        shareItems.add(new ShareItem(R.string.qzone,
                R.drawable.umeng_share_qzone_btn, ShareType.SHARE_TYPE_QZONE));
        shareUtils.setShareItems(shareItems);
        shareUtils.share(getView(), shareInfo);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        isAutoUpload = isChecked;
        prefsManager.setAutoUploadResource(isAutoUpload);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_APPLICATION_MODEL_SETTING) {
            initApplicationData();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mHandler.removeCallbacksAndMessages(null);
        //结束线程,防止内存泄露
        ThreadManager.shutdown();
        if (!Config.UPDATE_FOR_BUGLY) {

            unbindUpdateService();
        }
    }

    private void bindUpdateService() {
        ((MyApplication) getActivity().getApplication()).bindUpdateService(getActivity(), updateServiceConn);
    }

    private void unbindUpdateService() {
        ((MyApplication) getActivity().getApplication()).unbindUpdateService(updateServiceConn);
    }

    private UpdateService updateService;
    private ServiceConnection updateServiceConn = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            if (updateService != null) {
                updateService.setINewVersion(null);
                updateService = null;
            }
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            updateService = ((UpdateService.UpdateBinder) service).getService();
            updateService.setINewVersion(new UpdateService.INewVersion() {
                @Override
                public void isNewVersion(boolean isNewVersion, AppInfo appInfo) {
                    SettingFragment.this.appInfo = appInfo;
                    if (updateService != null) {
                        updateService.setINewVersion(null);
                    }

                    if (ivUpdate != null && !isNewVersion) {
                        ivUpdate.setVisibility(View.VISIBLE);
                    }
                }
            });
            updateService.checkUpdate();
        }
    };

    /**
     * 检查更新
     */
    private void loadUpgradeInfo() {
        if (ivUpdate == null) {
            return;
        }

        /***** 获取升级信息 *****/
        upgradeInfo = Beta.getUpgradeInfo();

        if (upgradeInfo == null) {
            return;
        }

        ivUpdate.setVisibility(View.VISIBLE);

    }
}
