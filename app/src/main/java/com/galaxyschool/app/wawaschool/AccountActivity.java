package com.galaxyschool.app.wawaschool;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;

import com.galaxyschool.app.wawaschool.fragment.account.LoginFragment;
import com.galaxyschool.app.wawaschool.fragment.account.SettingFragment;

public class AccountActivity extends BaseFragmentActivity {

    public static String EXTRA_EXIT_APPLICATION_ON_CANCEL = "exitOnCancel";
    public static String EXTRA_HAS_LOGINED = "isLogin";
    public static final String EXTRA_ENTER_HOME_AFTER_LOGIN =
            LoginFragment.EXTRA_ENTER_HOME_AFTER_LOGIN;

    private String fragmentTag;
    private boolean isLogin = true;
    private MyApplication myApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        myApp = (MyApplication)getApplication();
        initData();
        if (fragmentTag == null) {
            fragmentTag = LoginFragment.TAG;
        }
//        String memberId = myApp.getMemberId();
//        if (!TextUtils.isEmpty(memberId) && isLogin) {
//            UserInfo userInfo = myApp.getUserInfo();
//            ConversationHelper.login(userInfo.getMemberId(), userInfo.getPassword());
//            Intent intent = new Intent();
//            intent.setClass(AccountActivity.this, HomeActivity.class);
//            startActivity(intent);
//            finish();
//            return;
//        }
        initFragment(fragmentTag);
    }

    @Override
    public void onBackPressed() {
        Bundle args = getIntent().getExtras();
        if (args != null && args.getBoolean(EXTRA_EXIT_APPLICATION_ON_CANCEL, false)) {
//            android.os.Process.killProcess(android.os.Process.myPid());
//            System.exit(0);
            getActivityStack().finishAll();
            return;
        }
        super.onBackPressed();
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            fragmentTag = bundle.getString("fragmentTag");
            isLogin = bundle.getBoolean("isLogin");
        }
        if (fragmentTag == null) {
            fragmentTag = LoginFragment.TAG;
        }
    }

    private void initFragment(String fragmentTag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = null;
        if (fragmentTag.equals(LoginFragment.TAG)) {
            fragment = new LoginFragment();
            fragment.setArguments(getIntent().getExtras());
            fragmentTransaction.replace(R.id.account_container, fragment, LoginFragment.TAG);
        } else if (fragmentTag.equals(SettingFragment.TAG)) {
            fragment = new SettingFragment();
            fragmentTransaction.replace(R.id.account_container, fragment, SettingFragment.TAG);
        }
        fragmentTransaction.commit();
    }
    public boolean onTouchEvent(MotionEvent event) {
        if (null !=this.getCurrentFocus()) {
            InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            return mInputMethodManager.hideSoftInputFromWindow(this
                    .getCurrentFocus().getWindowToken(), 0);
        }
        return super.onTouchEvent(event);
    }
}
