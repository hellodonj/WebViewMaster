package com.lqwawa.intleducation.module.login.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
//import com.hyphenate.EMCallBack;
//import com.hyphenate.chat.EMClient;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.MainApplication;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ui.StatusBarUtil;
import com.lqwawa.intleducation.base.utils.LogUtil;
import com.lqwawa.intleducation.base.utils.ToastUtil;
import com.lqwawa.intleducation.base.vo.RequestVo;
import com.lqwawa.intleducation.base.ui.MyBaseActivity;
import com.lqwawa.intleducation.base.widgets.TopBar;
//import com.lqwawa.intleducation.module.chat.EaseHelper;
//import com.lqwawa.intleducation.module.chat.db.DemoDBManager;
import com.lqwawa.intleducation.module.user.tool.UserHelper;
import com.lqwawa.intleducation.module.user.vo.UserInfoVo;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

/**
 * Created by XChen on 2016/11/13.
 * email:man0fchina@foxmail.com
 * 登录
 */
public class LoginActivity extends MyBaseActivity implements View.OnClickListener {
    public static final int REQUEST_CODE_LOGIN = 1001;
    private static final String TAG = "LoginActivity";
    private static final boolean AUTO_LOGIN = false;


    private TopBar topBar;

    private EditText editTextAccount;
    private EditText editTextPassword;

    private TextView textViewLogin;
    private TextView textViewForgotPassowrd;

    private UserInfoVo userInfo;

    public static boolean isShow = false;

    public static void login(Activity activity) {
        if (!isShow) {
            if (!MainApplication.appIsLQMOOC()){
                return;
            }
            isShow = true;
            activity.startActivity(new Intent(activity, LoginActivity.class));
        }
    }

    public static void loginForResult(Activity activity) {
        if (!isShow) {
            if (!MainApplication.appIsLQMOOC()){
                return;
            }
            isShow = true;
            activity.startActivityForResult(
                    new Intent(activity, LoginActivity.class),
                    REQUEST_CODE_LOGIN);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_login);
        StatusBarUtil.StatusBarLightMode(this);

        topBar  = (TopBar)findViewById(R.id.top_bar);
        editTextAccount  = (EditText)findViewById(R.id.login_account_tv);
        editTextPassword = (EditText)findViewById(R.id.login_password_tv);
        textViewLogin = (TextView)findViewById(R.id.login_tv);
        textViewForgotPassowrd = (TextView)findViewById(R.id.forgot_password_tv);

        initViews();
    }

    private void initViews() {
        setResult(Activity.RESULT_CANCELED);
        editTextAccount.setText(UserHelper.getLastAccount());
        editTextAccount.setSelection(editTextAccount.getText().length());
        topBar.setBackgroundColor(
                getResources().getColor(R.color.com_bg_white));
        topBar.setTitle(getString(R.string.login));
        topBar.setTitleColor(R.color.com_text_green);
        topBar.setBack(true, R.drawable.ic_back_green);
        textViewLogin.setOnClickListener(this);
        textViewForgotPassowrd.setOnClickListener(this);

        if(AUTO_LOGIN){//仅用于开发时自动登录
            editTextAccount.setText("student");
            editTextPassword.setText("123456");
            //doLogin();
        }
        editTextPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == KeyEvent.ACTION_DOWN || actionId == EditorInfo.IME_ACTION_DONE){
                    doLogin();
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.login_tv) {
            doLogin();
        }else if(v.getId() == R.id.forgot_password_tv){
            RetrievePasswordActivity.start(activity);
        }
    }

    private void doLogin() {
        hideKeyboard();
        String accountString = editTextAccount.getText().toString();
        String passwordString = editTextPassword.getText().toString();

        if (TextUtils.isEmpty(accountString)) {
            Toast.makeText(LoginActivity.this, "账号不能为空!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(passwordString)) {
            Toast.makeText(LoginActivity.this, "请输入密码!", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestVo requestVo = new RequestVo();
        requestVo.addParams("account", accountString);
        requestVo.addParams("password", passwordString);
        LogUtil.d(TAG, requestVo.getParams());
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.Login + requestVo.getParams());

        params.setConnectTimeout(10000);
        showProgressDialog(getResources().getString(R.string.login_process));
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                userInfo = JSON.parseObject(s,
                        new TypeReference<UserInfoVo>() {
                        });
                if (userInfo.getCode() == 0){
                    doLoginEase();
                }else{
                    closeProgressDialog();
                    ToastUtil.showToast(activity, "登录失败,原因：" + userInfo.getMessage());
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.d(TAG, "登录失败！:" + throwable.getMessage());
                closeProgressDialog();
                ToastUtil.showToast(activity, getResources().getString(R.string.net_error_tip));
            }

            @Override
            public void onFinished() {
            }
        });
    }

    private void doLoginEase(){
//        if (userInfo == null)
//            return;
//        String accountString = userInfo.getHxAccount();
//        String passwordString = userInfo.getHxPassword();
//        // After logout，the DemoDB may still be accessed due to async callback, so the DemoDB will be re-opened again.
//        // close it before login to make sure DemoDB not overlap
//        DemoDBManager.getInstance().closeDB();
//
//        // reset current user name before login
//        EaseHelper.getInstance().setCurrentUserName(accountString);
//
//        final long start = System.currentTimeMillis();
//        // call login method
//        Log.d(TAG, "EMClient.getInstance().login");
//        EMClient.getInstance().login(accountString, passwordString, new EMCallBack() {
//
//            @Override
//            public void onSuccess() {
//                Log.d(TAG, "login: onSuccess");
//
//                // ** manually load all local groups and conversation
//                EMClient.getInstance().groupManager().loadAllGroups();
//                EMClient.getInstance().chatManager().loadAllConversations();
//
//                // update current user's display name for APNs
//                boolean updatenick = EMClient.getInstance().updateCurrentUserNick(
//                        userInfo.getUserName());
//                if (!updatenick) {
//                    Log.e("LoginActivity", "update current user nick fail");
//                }
//
//                if (!LoginActivity.this.isFinishing() && progressDialog.isShowing()) {
//                    closeProgressDialog();
//                }
//                // get user's info (this should be get from App's server or 3rd party service)
//                EaseHelper.getInstance().getUserProfileManager().asyncGetCurrentUserInfo();
//                userInfo.setAccount(editTextAccount.getText().toString());
//                UserHelper.setUserInfo(userInfo);
//                runOnUiThread(new Runnable() {
//                                  public void run() {
//                                      ToastUtil.showToast(getApplicationContext(),
//                                              getResources().getString(R.string.login_success));
//                                  }
//                              });
//                setResult(Activity.RESULT_OK);
//                //登录成功通知相关界面刷新
//                sendBroadcast(new Intent().setAction(AppConfig.ServerUrl.Login));
//
//                finish();
//            }
//
//            @Override
//            public void onProgress(int progress, String status) {
//                Log.d(TAG, "login: onProgress");
//            }
//
//            @Override
//            public void onError(final int code, final String message) {
//                Log.d(TAG, "login: onError: " + code);
//                if (!progressDialog.isShowing()) {
//                    return;
//                }
//                runOnUiThread(new Runnable() {
//                    public void run() {
//                        progressDialog.dismiss();
//                        ToastUtil.showToast(getApplicationContext(),
//                                getString(R.string.Login_failed) + message);
//                    }
//                });
//            }
//        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isShow = false;
    }

    // -------------------------------------隐藏输入法-----------------------------------------------------
    // 获取点击事件
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // TODO Auto-generated method stub
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View view = getCurrentFocus();
            if (isHideInput(view, ev)) {
                HideSoftInput(view.getWindowToken());
            }
        }
        return super.dispatchTouchEvent(ev);
    }
    // 判定是否需要隐藏
    private boolean isHideInput(View v, MotionEvent ev) {
        if (v != null && (v instanceof EditText)) {
            int[] l = { 0, 0 };
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
                    + v.getWidth();
            if (ev.getX() > left && ev.getX() < right && ev.getY() > top
                    && ev.getY() < bottom) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    // 隐藏软键盘
    private void HideSoftInput(IBinder token) {
        if (token != null) {
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(token,
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
