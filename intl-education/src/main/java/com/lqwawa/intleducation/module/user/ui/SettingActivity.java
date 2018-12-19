package com.lqwawa.intleducation.module.user.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
//import com.hyphenate.EMCallBack;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.MainApplication;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.helper.SharedPreferencesHelper;
import com.lqwawa.intleducation.base.ui.MyBaseActivity;
import com.lqwawa.intleducation.base.utils.BaseUtils;
import com.lqwawa.intleducation.base.utils.LogUtil;
import com.lqwawa.intleducation.base.utils.ToastUtil;
import com.lqwawa.intleducation.base.vo.RequestVo;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.common.ui.CustomDialog;
//import com.lqwawa.intleducation.module.chat.EaseHelper;
import com.lqwawa.intleducation.module.user.tool.UserHelper;
import com.lqwawa.intleducation.module.user.vo.UserInfoVo;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

/**
 * Created by XChen on 2016/12/2.
 * email:man0fchina@foxmail.com
 * 设置
 */
public class SettingActivity extends MyBaseActivity implements View.OnClickListener,
        CompoundButton.OnCheckedChangeListener {
    public static final int REQUEST_CODE_SETTING = 1010;
    private static final String TAG = "SettingActivity";

    private TopBar topBar;

    private CheckBox checkBoxAllow4G;
    private CheckBox checkBoxAllowPrivateMeg;
    private CheckBox checkBoxAllowNotice;
    private TextView textViewAboutProduct;
    private TextView textViewCheckForUpdates;
    private Button buttonLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        topBar = (TopBar) findViewById(R.id.top_bar);
        checkBoxAllow4G = (CheckBox) findViewById(R.id.allow_4g_cb);
        checkBoxAllowPrivateMeg = (CheckBox) findViewById(R.id.allow_private_msg_cb);
        checkBoxAllowNotice = (CheckBox) findViewById(R.id.allow_notice_cb);
        textViewAboutProduct = (TextView) findViewById(R.id.about_product_tv);
        textViewCheckForUpdates = (TextView) findViewById(R.id.check_for_updates_tv);
        buttonLogout = (Button) findViewById(R.id.logout_bt);
        initViews();
    }

    private void initViews() {
        topBar.setTitle(activity.getResources().getString(R.string.setting));
        topBar.setBack(true);
        textViewCheckForUpdates.setText(getResources().getString(R.string.check_for_updates)
                + "(" + BaseUtils.getAppVersionName(getApplicationContext()) + ")");

        checkBoxAllow4G.setOnCheckedChangeListener(this);
        checkBoxAllow4G.setChecked(SharedPreferencesHelper.getBoolean(this,
                AppConfig.BaseConfig.KEY_ALLOW_4G, false));
        checkBoxAllowPrivateMeg.setOnCheckedChangeListener(this);
        /*checkBoxAllowPrivateMeg.setChecked(SharedPreferencesHelper.getBoolean(this,
                AppConfig.BaseConfig.KEY_ALLOW_PRIVATE_MSG, false));*/
//        checkBoxAllowPrivateMeg.setChecked(!EaseHelper.getInstance().getModel().getSettingMsgSound());

        checkBoxAllowNotice.setOnCheckedChangeListener(this);
        checkBoxAllowNotice.setChecked(!SharedPreferencesHelper.getBoolean(this,
                AppConfig.BaseConfig.KEY_ALLOW_NOTICE, false));

        textViewAboutProduct.setOnClickListener(this);
        textViewCheckForUpdates.setOnClickListener(this);
        buttonLogout.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.about_product_tv) {
            AboutActivity.start(activity);
        }else if(view.getId() == R.id.check_for_updates_tv) {
        }else if(view.getId() == R.id.logout_bt) {
            CustomDialog.Builder builder = new CustomDialog.Builder(activity);
            builder.setMessage(activity.getResources().getString(R.string.confirm_logout)
                    + "?");
            builder.setTitle(activity.getResources().getString(R.string.tip));
            builder.setPositiveButton(activity.getResources().getString(R.string.exit),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            doLogout();
                        }
                    });

            builder.setNegativeButton(activity.getResources().getString(R.string.cancel),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

            builder.create().show();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(buttonView.getId() == R.id.allow_4g_cb) {
            SharedPreferencesHelper.setBoolean(this,
                    AppConfig.BaseConfig.KEY_ALLOW_4G, isChecked);
        }else if(buttonView.getId() == R.id.allow_private_msg_cb) {
//                SharedPreferencesHelper.setBoolean(this,
//                        AppConfig.BaseConfig.KEY_ALLOW_PRIVATE_MSG, isChecked);
//            EaseHelper.getInstance().getModel().setSettingMsgSound(!isChecked);
//            EaseHelper.getInstance().getModel().setSettingMsgNotification(!isChecked);
//            EaseHelper.getInstance().getModel().setSettingMsgSpeaker(!isChecked);
//            EaseHelper.getInstance().getModel().setSettingMsgVibrate(!isChecked);
        }else if(buttonView.getId() == R.id.allow_notice_cb) {
            SharedPreferencesHelper.setBoolean(this,
                    AppConfig.BaseConfig.KEY_ALLOW_NOTICE, !isChecked);
        }
    }

    private void doLogout() {
        showProgressDialog(activity.getResources().getString(R.string.loading));
        RequestVo requestVo = new RequestVo();
        LogUtil.d(TAG, requestVo.getParams());
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.Logout + requestVo.getParams());

        params.setConnectTimeout(10000);
        showProgressDialog(getResources().getString(R.string.logout_process));
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                closeProgressDialog();
                UserInfoVo userInfo = JSON.parseObject(s,
                        new TypeReference<UserInfoVo>() {
                        });
                if (userInfo.getCode() == 0) {
                    UserHelper.logout();
                    logoutEase();
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.d(TAG, "退出账号失败！:" + throwable.getMessage());
                closeProgressDialog();
                ToastUtil.showToast(activity, getResources().getString(R.string.net_error_tip));
            }

            @Override
            public void onFinished() {
            }
        });
    }

    private void logoutEase() {
        String st = getResources().getString(R.string.Are_logged_out);
        showProgressDialog(st);
//        EaseHelper.getInstance().logout(false, new EMCallBack() {
//
//            @Override
//            public void onSuccess() {
//                activity.runOnUiThread(new Runnable() {
//                    public void run() {
//                        closeProgressDialog();
//                        MainApplication.getInstance().finishActivity(MyActivity.class);
//                        finish();
//                    }
//                });
//            }
//
//            @Override
//            public void onProgress(int progress, String status) {
//
//            }
//
//            @Override
//            public void onError(int code, String message) {
//                activity.runOnUiThread(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        // TODO Auto-generated method stub
//                        closeProgressDialog();
//                        Toast.makeText(activity, "unbind devicetokens failed", Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
//        });
    }

}
