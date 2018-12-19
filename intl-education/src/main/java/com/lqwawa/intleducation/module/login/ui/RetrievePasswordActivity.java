package com.lqwawa.intleducation.module.login.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ui.MyBaseActivity;
import com.lqwawa.intleducation.base.utils.StringUtils;
import com.lqwawa.intleducation.base.utils.ToastUtil;
import com.lqwawa.intleducation.base.vo.RequestVo;
import com.lqwawa.intleducation.base.vo.ResponseVo;
import com.lqwawa.intleducation.base.widgets.TopBar;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

/**
 * Created by zy on 2016/3/25.
 */
public class RetrievePasswordActivity extends MyBaseActivity implements View.OnClickListener{

    private TopBar topBar;
    //手机号
    private EditText recoverPhone;
    //验证码
    private EditText recoverCode;
    //获取验证码
    private Button validBtn;
    //新密码1
    private EditText recoverSecret;
    //新密码2
    private EditText recoverSecretAgain;

    private Button recover_btn;

    private TimeDown timeDown;


    public static void start(Activity activity){
        activity.startActivity(new Intent(activity, RetrievePasswordActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrieve_password);
        topBar = (TopBar)findViewById(R.id.top_bar);
        recoverPhone  = (EditText)findViewById(R.id.recover_phone);
        recoverCode = (EditText)findViewById(R.id.recover_code);
        validBtn = (Button)findViewById(R.id.recover_code_get);
        recoverSecret = (EditText)findViewById(R.id.recover_secret);
        recoverSecretAgain = (EditText)findViewById(R.id.recover_secret_again);
        recover_btn = (Button)findViewById(R.id.recover_btn);
        timeDown = new TimeDown(60000, 1000);//构造CountDownTimer对象
        initUI();
    }

    private void initUI() {
        topBar.setTitle(getResources().getString(R.string.get_back_password));
        topBar.setBack(true);
        validBtn.setOnClickListener(this);
        recover_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        if (v.getId() == R.id.recover_code_get) {
            //获取验证码按钮
            getValidNum();
        }else if(v.getId() == R.id.recover_btn){
            initData();
        }
    }

    /**
     * 获取验证码
     */
    private void getValidNum() {
        String telephone = recoverPhone.getText().toString();
        if (TextUtils.isEmpty(telephone) ) {
            Toast.makeText(activity,
                    getResources().getString(R.string.get_back_password_account_hint),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        int type = 0;
        if(StringUtils.isLongString(telephone)){//全数字
            if (telephone.length() != 11 || !StringUtils.isPhoneNumber(telephone)) {
                Toast.makeText(activity,
                        getResources().getString(R.string.enter_right_phone_please),
                        Toast.LENGTH_SHORT).show();
                return;
            }else{
                type = 0;
            }
        }else{//可能是邮箱
            if (!StringUtils.isEmailAddress(telephone)) {
                Toast.makeText(activity,
                        getResources().getString(R.string.enter_right_emil_please),
                        Toast.LENGTH_SHORT).show();
                return;
            }else{
                type = 1;
            }
        }

        RequestVo requestVo = new RequestVo();
        requestVo.addParams("mobileEmail", telephone);
        requestVo.addParams("type", type);

        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.getVerificationCode + requestVo.getParams());

        params.setConnectTimeout(10000);
        showProgressDialog(getResources().getString(R.string.loading));
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                closeProgressDialog();
                ResponseVo<String> result = JSON.parseObject(s,
                        new TypeReference<ResponseVo<String>>() {
                        });
                if (result.getCode() == 0){
                    timeDown.start();//开始计时
                    ToastUtil.showToast(activity,
                            getResources().getString(R.string.erification_code_sended));
                }else{
                    ToastUtil.showToast(activity,
                            getResources().getString(R.string.send_failed) + "："
                                    + result.getMessage());
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                closeProgressDialog();
                ToastUtil.showToast(activity, getResources().getString(R.string.net_error_tip));
            }

            @Override
            public void onFinished() {
            }
        });
    }

    /**
     * 确定按钮，提交数据
     */
    private void initData() {

        String telephone = recoverPhone.getText().toString();
        String code = recoverCode.getText().toString();
        String newPassword1 = recoverSecret.getText().toString();
        String newPassword2 = recoverSecretAgain.getText().toString();

        int type = 0;
        if(StringUtils.isLongString(telephone)){//全数字
            if (telephone.length() != 11 || !StringUtils.isPhoneNumber(telephone)) {
                Toast.makeText(activity,
                        getResources().getString(R.string.enter_right_phone_please),
                        Toast.LENGTH_SHORT).show();
                return;
            }else{
                type = 0;
            }
        }else{//可能是邮箱
            if (!StringUtils.isEmailAddress(telephone)) {
                Toast.makeText(activity,
                        getResources().getString(R.string.enter_right_emil_please),
                        Toast.LENGTH_SHORT).show();
                return;
            }else{
                type = 1;
            }
        }

        if (TextUtils.isEmpty(code) || code.length() != 6) {
            Toast.makeText(activity,
                    getResources().getString(R.string.enter_right_code_please),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(newPassword1)) {
            Toast.makeText(activity,
                    getResources().getString(R.string.enter_new_psw_please),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(newPassword2)) {
            Toast.makeText(activity,
                    getResources().getString(R.string.reenter_new_psw_please),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPassword1.equals(newPassword2)) {
            Toast.makeText(activity,
                    getResources().getString(R.string.tow_psw_not_same),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("mobileEmail", telephone);
        requestVo.addParams("type", type);
        requestVo.addParams("code", code);
        requestVo.addParams("newPwd", newPassword1);

        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.retrievePassword + requestVo.getParams());

        params.setConnectTimeout(10000);
        showProgressDialog(getResources().getString(R.string.loading));
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                closeProgressDialog();
                ResponseVo<String> result = JSON.parseObject(s,
                        new TypeReference<ResponseVo<String>>() {
                        });
                if (result.getCode() == 0){
                    ToastUtil.showToast(activity,
                            getResources().getString(R.string.reset_psw)
                    + getResources().getString(R.string.success));
                    finish();
                }else{
                    ToastUtil.showToast(activity,
                            getResources().getString(R.string.reset_psw)
                                    + getResources().getString(R.string.failed) + "："
                                    + result.getMessage());
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                closeProgressDialog();

                ToastUtil.showToast(activity, getResources().getString(R.string.net_error_tip));
            }

            @Override
            public void onFinished() {
            }
        });
    }

    /**
     * 按钮倒计时
     */
    class TimeDown extends CountDownTimer {
        public TimeDown(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);//参数依次为总时长,和计时的时间间隔
        }

        @Override
        public void onFinish() {//计时完毕时触发
            validBtn.setText("重新获取");
            validBtn.setClickable(true);
        }

        @Override
        public void onTick(long millisUntilFinished) {//计时过程显示
            validBtn.setClickable(false);
            validBtn.setText(millisUntilFinished / 1000 + " 秒");
        }
    }

}
