package com.galaxyschool.app.wawaschool;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lqwawa.libs.appupdater.UpdateDialog;
import com.osastudio.common.utils.LogUtils;
import com.osastudio.common.utils.Utils;
import com.tencent.bugly.beta.Beta;
import com.tencent.bugly.beta.download.DownloadListener;
import com.tencent.bugly.beta.download.DownloadTask;

import java.io.File;


/**
 * <br/>================================================
 * <br/> 作    者：Blizzard-liu
 * <br/> 版    本：1.0
 * <br/> 创建日期：2017/12/21 14:59
 * <br/> 描    述：自定义版本升级页面
 * <br/> 修订历史：
 * <br/>================================================
 */

public class UpdateActivity extends com.osastudio.apps.BaseActivity {

    private static final String TAG = "UpdateActivity";
    private static final String ROOT_DIR_NAME = ".AppUpdater";
    private static final String PREFS_FILE_NAME = "app_updater";
    private UpdateDialog updateDialog;
    private TextView tipsView;
    private ProgressBar progressView;
    private Button retryView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.au_activity_update);

        init();

    }
    private void init() {
        if (isVersionIgnored(getApplicationContext()) && Beta.getUpgradeInfo().upgradeType != 2) {
            //版本忽略
            finish();
            return;
        }

        if (ActivityCompat.checkSelfPermission(this,  Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            finish();
            return;
        }

        initViews();
        initListener();
        showUpdateDialog();
    }

    private void initListener() {


          /*注册下载监听，监听下载事件*/
        Beta.registerDownloadListener(new DownloadListener() {
            @Override
            public void onReceive(DownloadTask task) {
                updateBtn(task);
                Log.d(TAG, "开始下载");

                progressView.setProgress((int) ((float) task.getSavedLength()
                        / (float) Beta.getUpgradeInfo().fileSize * 100f));
                progressView.setMax(100);
                LogUtils.logd(TAG,task.getSavedLength() + "");
            }

            @Override
            public void onCompleted(DownloadTask task) {
                updateBtn(task);
                Log.d(TAG, "安装");
                LogUtils.logd(TAG,task.getSavedLength() + "");
            }

            @Override
            public void onFailed(DownloadTask task, int code, String extMsg) {
                updateBtn(task);
                tipsView.setText(R.string.au_download_error);
                retryView.setVisibility(View.VISIBLE);
                LogUtils.logd(TAG,"下载失败");

            }
        });



    }



    private void initViews() {
        tipsView = (TextView) findViewById(R.id.au_update_tips);
        retryView = (Button) findViewById(R.id.au_update_retry);
        progressView = (ProgressBar) findViewById(R.id.au_update_progress);

        retryView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //重新下载
                retryView.setVisibility(View.GONE);
                tipsView.setText(R.string.au_downloading);
                DownloadTask task = Beta.startDownload();
                updateBtn(task);
            }
        });
    }

    private void showUpdateDialog() {
        if (updateDialog == null) {
            updateDialog = new UpdateDialog(this, null)
                    .setOnIgnoreClickListener(new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ignoreVersion(getApplicationContext(), true);
                            finish();
                        }
                    })
                    .setOnConfirmClickListener(new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            tipsView.setText(R.string.au_downloading);
                            findViewById(R.id.au_update_progress_layout).setVisibility(View.VISIBLE);
                            //开始下载
                            DownloadTask task = Beta.startDownload();
                            updateBtn(task);


                        }
                    })
                    .setOnCancelClickListener(new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Beta.cancelDownload();
                            finish();
                        }
                    });
            updateDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    finish();
                }
            });
        }

        //upgradeType = 1;//升级策略 1建议 2强制 3手工
        int upgradeType = Beta.getUpgradeInfo().upgradeType;
        switch (upgradeType) {
            case 1:
                // 不显示忽略按钮
                updateDialog.getIgnoreButton().setVisibility(View.GONE);
                break;
            case 2:
                // 仅显示立即更新按钮
                updateDialog.getIgnoreButton().setVisibility(View.GONE);
                updateDialog.getCancelButton().setVisibility(View.GONE);
                break;
            case 3:

                break;
            default:
                
                break;
                }


        //升级信息
        StringBuilder builder = new StringBuilder()
                .append(getString(R.string.au_new_version))
                .append(Beta.getUpgradeInfo().versionName).append("\n")
                .append(getString(R.string.au_file_size))
                .append(Utils.formatFileSize(Beta.getUpgradeInfo().fileSize)).append("\n")
                .append(getString(R.string.au_release_notes))
                .append(Beta.getUpgradeInfo().newFeature).append("\n");

        updateDialog.getUpdateTextView().setText(builder);
        updateDialog.setCancelable(false);
        updateDialog.show();
    }


    public void updateBtn(DownloadTask task) {

            /*根据下载任务状态设置按钮*/
        switch (task.getStatus()) {
            case DownloadTask.INIT:
            case DownloadTask.DELETED:
            case DownloadTask.FAILED: {
                LogUtils.logd(TAG,"开始下载");

            }
            break;
            case DownloadTask.COMPLETE: {
                LogUtils.logd(TAG,"安装");

            }
            break;
            case DownloadTask.DOWNLOADING: {
                LogUtils.logd(TAG,"暂停");
            }
            break;
            case DownloadTask.PAUSED: {
                LogUtils.logd(TAG,"继续下载");
            }
            break;
            default:
                break;
        }
    }

    private String generateFileName() {
        StringBuilder builder = new StringBuilder(getPackageName());
        builder.append("-").append(Beta.getUpgradeInfo().versionCode);
        if (!TextUtils.isEmpty(Beta.getUpgradeInfo().versionName)) {
            builder.append("-").append(Beta.getUpgradeInfo().versionName);
        }
        return builder.append(".apk").toString();
    }

    private String generateFilePath() {
        return new File(new File(getExternalCacheDir(), ROOT_DIR_NAME), generateFileName()).getAbsolutePath();
    }

    private boolean isVersionIgnored(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_FILE_NAME,
                Context.MODE_PRIVATE);
        return prefs.getBoolean(generateFileName(), false);
    }

    private void ignoreVersion(Context context, boolean ignore) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_FILE_NAME,
                Context.MODE_PRIVATE);
        prefs.edit().putBoolean(generateFileName(), ignore).apply();
    }

    @Override
    public void onBackPressed() {
        if (Beta.getUpgradeInfo().upgradeType == 2) {
            return;
        }

        super.onBackPressed();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
   /*注销下载监听*/
        Beta.unregisterDownloadListener();
    }

}
