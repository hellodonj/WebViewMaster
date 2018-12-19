package com.lqwawa.intleducation.module.home.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.widget.RadioGroup;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.MainApplication;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.utils.ToastUtil;
import com.lqwawa.intleducation.common.ui.CustomDialog;
import com.lqwawa.intleducation.module.discovery.ui.DiscoveryFragment;
import com.lqwawa.intleducation.module.learn.ui.LearnFragment;
import com.lqwawa.intleducation.base.ui.MyBaseFragmentActivity;
import com.lqwawa.intleducation.module.login.ui.LoginActivity;
import com.lqwawa.intleducation.module.user.tool.UserHelper;
import com.osastudio.common.utils.PermissionsUtils;

public class HomeActivity extends MyBaseFragmentActivity {

    private RadioGroup rg_tab;
    private DiscoveryFragment discoveryFragment;
    private LearnFragment learnFragment;

    static {
        try {
            System.loadLibrary("sdspv3_jni");
        } catch (UnsatisfiedLinkError ule) {
            System.err.println("WARNING: Could not load so");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        rg_tab = (RadioGroup) findViewById(R.id.rg_tab);
        registerBoradcastReceiver();
        initViews();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PermissionsUtils.requestAllPermissions(
                    HomeActivity.this,
                    PermissionsUtils.mAllGroupPermission);
        }
        startService(new Intent(HomeActivity.this, com.letvcloud.cmf.MediaService.class));
    }

    private void initViews() {
        discoveryFragment = new DiscoveryFragment();
        learnFragment = new LearnFragment();

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, discoveryFragment);
        fragmentTransaction.add(R.id.fragment_container, learnFragment);
        fragmentTransaction.hide(learnFragment);
        fragmentTransaction.show(discoveryFragment);
        discoveryFragment.setUserVisibleHint(true);
        learnFragment.setUserVisibleHint(true);
        fragmentTransaction.commit();

        rg_tab.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                changeTab(checkedId);
            }
        });
    }


    @Override
    protected void onNewIntent(Intent intent) {
        boolean isConflict = intent.getBooleanExtra("conflict", false);
        if (isConflict){
            changeTab(R.id.rb_discovery);
            CustomDialog.Builder builder = new CustomDialog.Builder(activity);
            builder.setMessage("您的账号已在别处登录！");
            builder.setTitle(activity.getResources().getString(R.string.tip));
            builder.setNegativeButton("重新登录",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            LoginActivity.loginForResult(
                                    MainApplication.getInstance().getLastActivity());
                        }
                    });

            builder.setPositiveButton(activity.getResources().getString(R.string.cancel),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            builder.create().show();
        }
    }

    private void changeTab(int checkedId) {
        FragmentTransaction fragmentTransaction =
                getSupportFragmentManager().beginTransaction();
        fragmentTransaction.hide(discoveryFragment);
        fragmentTransaction.hide(learnFragment);
        discoveryFragment.setUserVisibleHint(false);
        if (checkedId == R.id.rb_discovery) {
            fragmentTransaction.show(discoveryFragment);
            rg_tab.check(R.id.rb_discovery);
        } else if (checkedId == R.id.rb_learn) {
            rg_tab.check(R.id.rb_learn);
            if (UserHelper.isLogin()) {
                fragmentTransaction.show(learnFragment);
            } else {
                LoginActivity.loginForResult(activity);
            }
        }
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LoginActivity.REQUEST_CODE_LOGIN) {
            if (rg_tab.getCheckedRadioButtonId() == R.id.rb_learn) {
                if (resultCode == Activity.RESULT_OK) {
                    FragmentTransaction fragmentTransaction =
                            getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.hide(discoveryFragment);
                    fragmentTransaction.hide(learnFragment);
                    fragmentTransaction.show(learnFragment);
                    fragmentTransaction.commitAllowingStateLoss();
                } else {
                    FragmentTransaction fragmentTransaction =
                            getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.hide(discoveryFragment);
                    fragmentTransaction.hide(learnFragment);
                    fragmentTransaction.show(learnFragment);
                    fragmentTransaction.commitAllowingStateLoss();
                    rg_tab.check(R.id.rb_discovery);
                }
            }
        }
    }

    //切换至指定的tab页
    public void showTab(int tabIndex) {
        rg_tab.check((tabIndex == 0) ? R.id.rb_discovery : R.id.rb_learn);
    }

    //再按一次退出程序
    private Handler handler = new Handler() {
        public void dispatchMessage(android.os.Message msg) {
            super.dispatchMessage(msg);
        }
    };

    @Override
    public void onBackPressed() {
        if (!this.exitAllowed
                && getSupportFragmentManager().getBackStackEntryCount() <= 0) {
            this.exitAllowed = true;
            ToastUtil.showToastBottom(this, R.string.more_press_exit);
            this.handler.postDelayed(this.exitRunnable, 1000);
            return;
        } else {
            super.onBackPressed();
        }
    }

    private boolean exitAllowed;
    private Runnable exitRunnable = new Runnable() {
        @Override
        public void run() {
            exitAllowed = false;
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        activity.unregisterReceiver(mBroadcastReceiver);
    }

    /**
     * BroadcastReceiver
     ************************************************/
    protected BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(AppConfig.ServerUrl.Logout)) {//登录成功刷新列表

            }
        }
    };

    /**
     * 注册广播事件
     */
    protected void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        //要接收的类型
        myIntentFilter.addAction(AppConfig.ServerUrl.Logout);//退出登录
        //注册广播
        activity.registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

}
