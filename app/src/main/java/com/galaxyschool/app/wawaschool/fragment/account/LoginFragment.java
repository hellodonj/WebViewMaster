package com.galaxyschool.app.wawaschool.fragment.account;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.CommonFragmentActivity;
import com.galaxyschool.app.wawaschool.CustomerServiceActivity;
import com.galaxyschool.app.wawaschool.HomeActivity;
import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.CheckInsideWiFiUtil;
import com.galaxyschool.app.wawaschool.common.DataMigrationUtils;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.common.UIUtils;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.AdShowFragment;
import com.galaxyschool.app.wawaschool.fragment.BaseFragment;
import com.galaxyschool.app.wawaschool.fragment.MySchoolSpaceFragment;
import com.galaxyschool.app.wawaschool.helper.ThirdPartyLoginHelper;
import com.galaxyschool.app.wawaschool.jpush.PushUtils;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.galaxyschool.app.wawaschool.pojo.UserInfoResult;
import com.galaxyschool.app.wawaschool.views.ToolbarTopView;
import com.icedcap.dubbing.DubbingActivity;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.lqwawa.mooc.common.MOOCHelper;
import com.oosic.apps.share.ShareHelper;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.HashMap;
import java.util.Map;

public class LoginFragment extends BaseFragment implements View.OnClickListener, TextView
        .OnEditorActionListener {

    public static final String TAG = LoginFragment.class.getSimpleName();

    public static final String EXTRA_ENTER_HOME_AFTER_LOGIN = "enterHomeAfterLogin";

    private EditText userNameTxt, passwordTxt;

    private MyApplication myApp;
    private UserInfo userInfo;
    private boolean isBackHome = true;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadIntent();
        initViews();
        if (getActivity() != null) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams
                    .SOFT_INPUT_ADJUST_PAN);
        }
    }

    private void loadIntent() {
        Bundle args = getArguments();
        if (args != null) {
            isBackHome = args.getBoolean(EXTRA_ENTER_HOME_AFTER_LOGIN, true);
        }
    }

    private void initViews() {
        View rootView = getView();
        if (rootView != null) {
            ToolbarTopView toolbarTopView = (ToolbarTopView) rootView.findViewById(R.id
                    .toolbartopview);
            toolbarTopView.getBackView().setOnClickListener(this);
            toolbarTopView.getTitleView().setText(R.string.login);
            userNameTxt = (EditText) rootView.findViewById(R.id.login_username_edittext);
            passwordTxt = (EditText) rootView.findViewById(R.id.login_password_edittext);
            passwordTxt.setOnEditorActionListener(this);

            View loginBtn = rootView.findViewById(R.id.login_btn);
            loginBtn.setOnClickListener(this);
            View forgotPasswordBtn = rootView.findViewById(R.id.login_forgot_password_btn);
            forgotPasswordBtn.setOnClickListener(this);
            View registerBtn = rootView.findViewById(R.id.login_register_btn);
            registerBtn.setOnClickListener(this);
            TextView agencyBtn = (TextView) rootView.findViewById(R.id.login_agency_btn);
            agencyBtn.setOnClickListener(this);
            agencyBtn.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
            TextView telephoneBtn = (TextView) rootView.findViewById(R.id.login_telephone_btn);
            telephoneBtn.setOnClickListener(this);
            telephoneBtn.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

            //qq登录
            ImageView qqLoginImageV = (ImageView) findViewById(R.id.iv_qq_login);
            qqLoginImageV.setOnClickListener(this);
            //微信的登录
            ImageView wxLoginImageV = (ImageView) findViewById(R.id.iv_wx_login);
            wxLoginImageV.setOnClickListener(this);


            myApp = ((MyApplication) getActivity().getApplication());
            if (myApp != null) {
                UserInfo userInfo = myApp.getUserInfo();
                if (userInfo != null) {
                    userNameTxt.setText(userInfo.getNickName());
                    if (!TextUtils.isEmpty(userInfo.getNickName())) {
                        userNameTxt.setSelection(userInfo.getNickName().length());
                    }
                }
            }

        }
        getView().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideSoftKeyboard(getActivity());
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_top_back_btn:
                getActivity().finish();
                break;
            case R.id.login_btn:
                login();
                break;
            case R.id.login_forgot_password_btn:
                showForgotPasswordFrame();
                break;
            case R.id.login_register_btn:
                showRegisterFrame();
                break;
            case R.id.login_agency_btn:
                CommonFragmentActivity.start(getActivity(), AdShowFragment.class);
                break;
            case R.id.login_telephone_btn:
                CustomerServiceActivity.start(getActivity());
                break;
            case R.id.iv_qq_login:
                showAuthLoginDialog(SHARE_MEDIA.QQ);
                break;
            case R.id.iv_wx_login:
                showAuthLoginDialog(SHARE_MEDIA.WEIXIN);
                break;
        }
    }

    private void thirdParyLogin(SHARE_MEDIA share_media) {
        ThirdPartyLoginHelper helper = new ThirdPartyLoginHelper(getActivity());
        helper.setShareMediaType(share_media)
                .setIsBackHome(isBackHome)
                .setFunctionType(ThirdPartyLoginHelper.FUNCTION_TYPE.THIRDPARTY_LOGIN)
                .start();
    }

    private void showAuthLoginDialog(SHARE_MEDIA shareMedia) {
        boolean isAppInstall = ShareHelper.isAppInstall(getActivity(), shareMedia);
        if (!isAppInstall) {
            if (shareMedia == SHARE_MEDIA.QQ){
                TipMsgHelper.ShowMsg(getActivity(),R.string.install_qq);
            } else if (shareMedia == SHARE_MEDIA.WEIXIN){
                TipMsgHelper.ShowMsg(getActivity(),R.string.install_wechat);
            }
            return;
        }
        int loginType = LoginType.LQWAWA;
        if (shareMedia == SHARE_MEDIA.WEIXIN) {
            loginType = LoginType.WECHAT;
        } else if (shareMedia == SHARE_MEDIA.QQ) {
            loginType = LoginType.QQ;
        }
        AuthLoginDialogFragment authLoginDialogFragment = AuthLoginDialogFragment.newInstance
                (loginType);
        authLoginDialogFragment.setOnAuthLoginListener(loginType1 -> authLogin(loginType1));
        authLoginDialogFragment.show(getFragmentManager(), AuthLoginDialogFragment.TAG);
    }

    private void authLogin(int loginType) {
        switch (loginType) {
            case LoginType.QQ:
                thirdParyLogin(SHARE_MEDIA.QQ);
                break;
            case LoginType.WECHAT:
                thirdParyLogin(SHARE_MEDIA.WEIXIN);
                break;
            default:
                userNameTxt.setFocusableInTouchMode(true);
                userNameTxt.requestFocus();
                break;
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        switch (actionId) {
            case EditorInfo.IME_ACTION_DONE:
                login();
                break;
        }
        return true;
    }

    private void login() {
        String userName = userNameTxt.getText().toString().trim();
        String password = passwordTxt.getText().toString().trim();

        if (TextUtils.isEmpty(userName)) {
            TipMsgHelper.ShowMsg(getActivity(), getString(R.string.pls_input_username));
            return;
        }
        if (TextUtils.isEmpty(password)) {
            TipMsgHelper.ShowMsg(getActivity(), getString(R.string.pls_input_password));
            return;
        }

        UIUtils.hideSoftKeyboard(getActivity());
        login(userName, password);
    }

    private void login(final String userName, String password) {
        Map<String, Object> mParams = new HashMap<String, Object>();
        mParams.put("NickName", userName);
        mParams.put("Password", password);
        RequestHelper.RequestModelResultListener listener = new RequestHelper.
                RequestModelResultListener<UserInfoResult>(getActivity(), UserInfoResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                if (getActivity() == null) {
                    return;
                }
                super.onSuccess(jsonString);
                UserInfoResult userInfoResult = getResult();
                if (userInfoResult != null && userInfoResult.isSuccess()) {
                    userInfo = userInfoResult.getModel();
                    if (userInfo != null) {
                        myApp.setUserInfo(userInfo);
                        myApp.getPrefsManager().setUserPassword(userInfo.getPassword());
                        TipMsgHelper.ShowMsg(getActivity(), getString(R.string.login_success));

                        //初始化mooc用户信息
                        MOOCHelper.init(userInfo);

                        actionStart();
                        //登录的时候增加对listener的监听
//                        DemoHXSDKHelper hxsdkHelper = (DemoHXSDKHelper) DemoHXSDKHelper.getInstance();
//                        if (hxsdkHelper != null) {
//                            hxsdkHelper.initEventListener();
//                        }
//                        ConversationHelper.login(userInfo.getMemberId(), userInfo.getPassword());
                        getMyApplication().startDownloadService();
                        MyApplication.saveSchoolInfoList(getActivity(), userInfo.getSchoolList());

                        //登录成功后迁移本地课件
                        DataMigrationUtils.processLocalCourseData(getActivity(), userInfo.getMemberId());
                        //通知校园空间加载学校数据
                        MySchoolSpaceFragment.sendBrocast(getActivity());
                        //校验判断校内网是不是一个连接的状态
                        checkInsideWiFiIPConnected(userInfo.getMemberId());
                        PushUtils.resumePush(getActivity());
                    }
                }
            }
        };
        listener.setShowLoading(true);
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.LOGIN_URL, mParams, listener);
    }

    /**
     * 登录成功之后校验用户的局域网IP是否是连接的一个状态
     *
     * @param memberId
     */
    private void checkInsideWiFiIPConnected(String memberId) {
        CheckInsideWiFiUtil.getInstance().
                setMemberId(memberId).
                setActivity(getActivity()).
                checkData();
    }

    private void showForgotPasswordFrame() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.account_container, new ForgotPasswordFragment(), ForgotPasswordFragment.TAG);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void showRegisterFrame() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.account_container, new RegisterFragment(), RegisterFragment.TAG);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void actionStart() {
        if (isBackHome) {
            Intent intent = new Intent();
            intent.setClass(getActivity(), HomeActivity.class);
            startActivity(intent);
        }
        if (getActivity() != null) {
            getActivity().setResult(Activity.RESULT_OK);
            getActivity().finish();
        }
    }

}
