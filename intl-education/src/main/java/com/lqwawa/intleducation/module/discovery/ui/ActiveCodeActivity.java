package com.lqwawa.intleducation.module.discovery.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ui.MyBaseActivity;
import com.lqwawa.intleducation.base.utils.ToastUtil;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.lqpay.LqPay;
import com.lqwawa.intleducation.lqpay.PayParams;
import com.lqwawa.intleducation.lqpay.callback.OnPayInfoRequestListener;
import com.lqwawa.intleducation.lqpay.callback.OnPayResultListener;
import com.lqwawa.intleducation.lqpay.enums.PayWay;
import com.lqwawa.intleducation.lqpay.widget.GridPasswordView;
import com.lqwawa.intleducation.module.user.tool.UserHelper;
import com.osastudio.common.utils.LogUtils;




public class ActiveCodeActivity extends MyBaseActivity {

    private TopBar mTopBar;
    private TextView mNeedPayTv;
    private Switch mPswVisibilitySwitcher;
    private GridPasswordView mPassword;
    private static final String KEY_COURSENAME = "coursename";
    private static final String KEY_PRICE = "price";
    private static final String KEY_ORDERID = "orderId";
    private static final String KEY_IS_LIVE = "isLive";
    private String mOrderId;
    private boolean isLive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_code);
        initView();
    }

    private void initView() {
        String courseName = getIntent().getStringExtra(KEY_COURSENAME);
        String mPrice = getIntent().getStringExtra(KEY_PRICE);
        mOrderId = getIntent().getStringExtra(KEY_ORDERID);
        isLive = getIntent().getBooleanExtra(KEY_IS_LIVE, false);
        mTopBar = (TopBar) findViewById(R.id.top_bar);
        mNeedPayTv = (TextView) findViewById(R.id.need_pay_tv);
        TextView courseNameTv = (TextView) findViewById(R.id.course_name);
        mPswVisibilitySwitcher = (Switch) findViewById(R.id.psw_visibility_switcher);
        mPassword = (GridPasswordView) findViewById(R.id.password);

        mTopBar.setBack(true);
        mTopBar.setTitle(getString(R.string.pay_input_code));
        mTopBar.showBottomSplitView(true);
        mNeedPayTv.setText(new StringBuffer().append("¥").append(mPrice));
        courseNameTv.setText(getString(isLive ? R.string.pay_live_str_buy
                : R.string.pay_str_buy)+" 《"+courseName+"》");
        mPswVisibilitySwitcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mPassword.togglePasswordVisibility();
            }
        });
        mPassword.setPasswordVisibility(true);

        mPassword.setOnPasswordChangedListener(new GridPasswordView.OnPasswordChangedListener() {
            @Override
            public void onTextChanged(String psw) {

            }

            @Override
            public void onInputFinish(String psw) {
                doPay(psw);
            }
        });
    }

    private void doPay(String psw) {
        PayParams params = new PayParams.Builder(this)
                .payWay(PayWay.Code)
                .orderId(mOrderId)
                .memberId(UserHelper.getUserId())
                .setActiveCode(psw)//设置激活码
                .build();

        LqPay.newInstance(params).requestPayInfo(new OnPayInfoRequestListener() {
            @Override
            public void onPayInfoRequetStart() {
                // TODO 在此处做一些loading操作,progressbar.show();
                showProgressDialog("");
            }

            @Override
            public void onPayInfoRequstSuccess(String result) {
                // TODO 可以将loading状态去掉了。请求预支付信息成功，开始跳转到客户端支付。
                closeProgressDialog();
            }

            @Override
            public void onPayInfoRequestFailure() {
                // / TODO 可以将loading状态去掉了。获取预支付信息失败，会同时得到一个支付失败的回调。可以将loading状态去掉了。
                closeProgressDialog();
            }
        }).toPay(new OnPayResultListener() {
            @Override
            public void onPaySuccess(PayWay payWay) {
                // 支付成功
                mPassword.clearPassword();
                LogUtils.logd("pay", " payWay == " + payWay.toString());
                setResult(RESULT_OK);
                finish();

            }

            @Override
            public void onPayCancel(PayWay payWay) {
                // 支付流程被用户中途取消
                LogUtils.logd("pay", " payWay == " + payWay.toString());
                ToastUtil.showToast(ActiveCodeActivity.this, getString(R.string.cancel_pay_result));
                mPassword.clearPassword();
            }

            @Override
            public void onPayFailure(PayWay payWay, int errCode) {
                // 支付失败，
//                ToastUtil.showToast(ActiveCodeActivity.this,
//                        "errorCode = " + errCode + "   payWay = " + payWay.toString());
                LogUtils.logd("pay", " payWay == " + payWay.toString() + "  errCode ==  " + errCode);

                String errorMessage = "";
                String btn = "";

                if (errCode == LqPay.CODE_ERROR) {//错误
                    errorMessage = getString(R.string.pay_code_error);
                    btn = getString(R.string.pay_btn_retry);
                } else if (errCode == LqPay.CODE_EXPIRE) {//过期
                    errorMessage = getString(R.string.pay_code_expire);
                    btn = getString(R.string.confirm_ok);
                } else if (errCode == LqPay.CODE_INOPERATIVE) {//未生效
                    errorMessage = getString(R.string.pay_code_inoperative);
                    btn = getString(R.string.confirm_ok);
                } else {
                    errorMessage = getString(R.string.pay_code_error_use);
                    btn = getString(R.string.pay_btn_retry);
                }

                mPassword.clearPassword();

                View inflate = LayoutInflater.from(getApplicationContext()).inflate(R.layout.dialog_authority, null);
                TextView message = (TextView)inflate.findViewById(R.id.tv_tip_message);
                TextView btn_tv = (TextView)inflate.findViewById(R.id.tv_btn);
                message.setText(errorMessage);
                btn_tv.setText(btn);
                final AlertDialog alertDialog = new AlertDialog.Builder(ActiveCodeActivity.this)
                        .create();
                alertDialog.show();
                Window window = alertDialog.getWindow();
                if (window != null) {
                    window.setContentView(inflate);
                }
                alertDialog.setCanceledOnTouchOutside(false);
                btn_tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

            }
        });
    }

    public static void newInstance(String orderId, String price, String coursename,
                                   boolean isLive, Context context) {
        Intent starter = new Intent(context, ActiveCodeActivity.class);
        starter.putExtra(KEY_ORDERID, orderId);
        starter.putExtra(KEY_PRICE, price);
        starter.putExtra(KEY_COURSENAME, coursename);
        starter.putExtra(KEY_IS_LIVE, isLive);
        ((Activity)context).startActivityForResult(starter,PayActivity.REQUESTCODE);
    }
}
