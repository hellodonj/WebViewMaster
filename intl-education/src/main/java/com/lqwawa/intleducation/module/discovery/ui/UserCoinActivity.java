package com.lqwawa.intleducation.module.discovery.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.vo.RequestVo;
import com.lqwawa.intleducation.module.discovery.ui.coin.JavaCoinTransferDialogFragment;
import com.lqwawa.intleducation.module.discovery.ui.coin.JavaCoinTransferNavigator;
import com.lqwawa.intleducation.module.discovery.ui.coin.UserParams;
import com.lqwawa.intleducation.module.discovery.ui.coin.donation.DonationCoinActivity;
import com.lqwawa.intleducation.module.user.tool.UserHelper;
import com.lqwawa.intleducation.module.user.vo.UserInfoVo;
import com.osastudio.apps.BaseFragmentActivity;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

/**
 * ================================================
 * author：xu_wenliang
 * time：2018/4/8 10:59
 * desp: 描 述：我的余额界面
 * ================================================
 */

public class UserCoinActivity extends BaseFragmentActivity implements View.OnClickListener {
    // 转赠请求码
    private static final int KEY_BALANCE_REQUEST_CODE = 1 << 0;

    private String memberId;
    private ImageView ivClose;
    private TextView tvDetail;
    private TextView tvBalance;
    private TextView tvCharge;
    private TextView mTvGiveMoney;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_coin);

        memberId = getIntent().getStringExtra("memberId");

        initView();

        initData();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initData();
    }

    private void initView() {


        ivClose = (ImageView) findViewById(R.id.header_left_btn);
        ivClose.setOnClickListener(this);

        tvDetail = (TextView) findViewById(R.id.header_right_btn);
        tvDetail.setOnClickListener(this);

        tvBalance = (TextView) findViewById(R.id.balance_textView);

        tvCharge = (TextView) findViewById(R.id.charge_textView);
        tvCharge.setOnClickListener(this);

        mTvGiveMoney = (TextView) findViewById(R.id.tv_give_money);
        mTvGiveMoney.setVisibility(View.VISIBLE);
        mTvGiveMoney.setOnClickListener(this);

    }

    private void initData() {

        RequestVo requestVo = new RequestVo();

        UserInfoVo userInfo = UserHelper.getUserInfo();
        requestVo.addParams("token", userInfo.getToken());
        RequestParams params = new RequestParams(AppConfig.ServerUrl.GET_USER_COINS_COUNT + requestVo.getParams());

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String jsonString) {
                if (TextUtils.isEmpty(jsonString)) {
                    return;
                }

                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(jsonString);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                int code = jsonObject.optInt("code");

                if (code == 0) {
                    //请求成功
                    JSONObject jsonCoins = jsonObject.optJSONObject("data");

                    if (jsonCoins == null) {
                        tvBalance.setText("0");

                    } else {
                        tvBalance.setText(jsonCoins.optString("amount"));
                    }

                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {

            }

            @Override
            public void onCancelled(CancelledException e) {

            }

            @Override
            public void onFinished() {

            }
        });

    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        Intent intent;
        if (i == R.id.header_left_btn) {

            finish();
        } else if (i == R.id.header_right_btn) {
            //明细

            intent = new Intent(this, CoinsDetailActivity.class);
            startActivity(intent);
        } else if (i == R.id.charge_textView) {
            //充值
            JavaCoinTransferDialogFragment.show(getSupportFragmentManager(), new JavaCoinTransferNavigator() {
                @Override
                public void onChoiceConfirm(@NonNull UserParams user) {
                    Intent intent = new Intent(UserCoinActivity.this, ChargeCenterActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(ChargeCenterActivity.KEY_EXTRA_USER,user);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
        }else if(i == R.id.tv_give_money){
            // 转赠他人
            DonationCoinActivity.show(this,KEY_BALANCE_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == KEY_BALANCE_REQUEST_CODE){
                Bundle extras = data.getExtras();
                boolean result = extras.getBoolean(DonationCoinActivity.KEY_RESULT_BALANCE_STATE);
                if(result){
                    // 发生余额更新
                    initData();
                }
            }
        }
    }
}
