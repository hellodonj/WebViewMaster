package com.lqwawa.intleducation.module.discovery.ui;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ui.MyBaseFragment;
import com.lqwawa.intleducation.base.vo.RequestVo;
import com.lqwawa.intleducation.module.discovery.ui.coin.JavaCoinTransferDialogFragment;
import com.lqwawa.intleducation.module.discovery.ui.coin.JavaCoinTransferNavigator;
import com.lqwawa.intleducation.module.discovery.ui.coin.UserParams;
import com.lqwawa.intleducation.module.discovery.ui.coin.donation.DonationCoinActivity;
import com.lqwawa.intleducation.module.user.tool.UserHelper;
import com.lqwawa.intleducation.module.user.vo.UserInfoVo;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

/**
 *
 */
public class UserCoinFragment extends MyBaseFragment implements View.OnClickListener {

    // 转赠请求码
    private static final int KEY_BALANCE_REQUEST_CODE = 1 << 0;

    private TextView tvBalance;
    private TextView tvCharge;
    private TextView mTvGiveMoney;
    private TextView detailBtn;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_coin, container, false);

        tvBalance = (TextView) view.findViewById(R.id.balance_textView);

        tvCharge = (TextView) view.findViewById(R.id.charge_textView);
        tvCharge.setOnClickListener(this);

        mTvGiveMoney = (TextView) view.findViewById(R.id.tv_give_money);
        mTvGiveMoney.setVisibility(View.VISIBLE);
        mTvGiveMoney.setOnClickListener(this);
        detailBtn = (TextView) view.findViewById(R.id.detail_btn);
        detailBtn.setOnClickListener(this);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
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
        if (i == R.id.charge_textView) {
            //充值
            JavaCoinTransferDialogFragment.show(getChildFragmentManager(), new JavaCoinTransferNavigator() {
                @Override
                public void onChoiceConfirm(@NonNull UserParams user) {
                    Intent intent = new Intent(getContext(), ChargeCenterActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(ChargeCenterActivity.KEY_EXTRA_USER, user);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
        } else if (i == R.id.tv_give_money) {
            // 转赠他人
            DonationCoinActivity.show(this, KEY_BALANCE_REQUEST_CODE);
        } else if (i == R.id.detail_btn) {
            // 明细
            startActivity(new Intent(getContext(), CoinsDetailActivity.class));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == KEY_BALANCE_REQUEST_CODE) {
                Bundle extras = data.getExtras();
                boolean result = extras.getBoolean(DonationCoinActivity.KEY_RESULT_BALANCE_STATE);
                if (result) {
                    // 发生余额更新
                    initData();
                }
            }
        }
    }
}
