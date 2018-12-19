package com.lqwawa.intleducation.module.discovery.ui;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ui.MyBaseActivity;
import com.lqwawa.intleducation.base.utils.ToastUtil;
import com.lqwawa.intleducation.base.vo.RequestVo;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.lqpay.LqPay;
import com.lqwawa.intleducation.lqpay.PayParams;
import com.lqwawa.intleducation.lqpay.callback.OnPayInfoRequestListener;
import com.lqwawa.intleducation.lqpay.callback.OnPayResultListener;
import com.lqwawa.intleducation.lqpay.enums.PayWay;
import com.lqwawa.intleducation.module.discovery.adapter.NormalCountAdapter;
import com.lqwawa.intleducation.module.discovery.vo.NormalChargeInfo;
import com.lqwawa.intleducation.module.user.tool.UserHelper;
import com.osastudio.common.utils.LogUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.List;


/**
 * ================================================
 * author：xu_wenliang
 * time：2018/4/8 10:59
 * desp: 描 述：充值中心界面
 * ================================================
 */

public class ChargeCenterActivity extends MyBaseActivity implements View.OnClickListener {


    private TopBar topBar;
    private EditText etCount;

    private TextView tvCost;
    private TextView tvConfirm;
    private GridView gridView;
    private List<NormalChargeInfo> chargeList;
    private NormalCountAdapter adapter;


    public boolean isWXpay = true;
    public int coinNum;
    private PayWay mPayWay = PayWay.WechatPay;
    private String maxCount;
    private LinearLayout llWx;
    private LinearLayout llAli;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charge_center);
        topBar = (TopBar) findViewById(R.id.top_bar);


        initData();

        initView();

    }

    private void initData() {

        showProgressDialog("");

        RequestVo requestVo = new RequestVo();

        requestVo.addParams("pageIndex", 0);
        requestVo.addParams("pageSize", 10);

        RequestParams params = new RequestParams(AppConfig.ServerUrl.GET_CHARGE_NORMAL_COUNT + requestVo.getParams());

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

                    closeProgressDialog();

                    JSONArray jsonArray = jsonObject.optJSONArray("data");
                    chargeList = JSON.parseArray(jsonArray.toString(), NormalChargeInfo.class);
                    adapter = new NormalCountAdapter(ChargeCenterActivity.this, chargeList);
                    gridView.setAdapter(adapter);
                }

            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {

            }

            @Override
            public void onFinished() {
            }
        });

    }

    private void initView() {

        topBar.setBack(true);
        topBar.setTitle(getResources().getString(R.string.charge_center));


        etCount = (EditText) findViewById(R.id.count_edittext);

        etCount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (TextUtils.isEmpty(s.toString())) {

                    tvCost.setText("0");
                    return;
                }


                if (s.toString().length() > 6) {
                    ToastUtil.showToast(ChargeCenterActivity.this, R.string.wrong_input_count);
                    etCount.setText(maxCount);
                    etCount.setSelection(maxCount.length());
                    coinNum = Integer.valueOf(maxCount);
                    tvCost.setText(maxCount);
                    return;
                }

                tvCost.setText(s.toString());

                coinNum = Integer.valueOf(s.toString());

                maxCount = s.toString();


                for (int i = 0; i < chargeList.size(); i++) {
                    NormalChargeInfo info = chargeList.get(i);

                    info.isSelected = false;
                }

                adapter.notifyDataSetChanged();
            }
        });


        llWx = (LinearLayout) findViewById(R.id.ll_wx);

        llAli = (LinearLayout) findViewById(R.id.ll_ali);

        tvCost = (TextView) findViewById(R.id.pay_cost_textview);

        tvConfirm = (TextView) findViewById(R.id.pay_confirm_tv);


        llWx.setOnClickListener(this);
        llAli.setOnClickListener(this);


        tvConfirm.setOnClickListener(this);

        gridView = (GridView) findViewById(R.id.normal_count_gridvew);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener()

        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                update(position);
                adapter.notifyDataSetChanged();
            }
        });

    }

    private void update(int position) {

        for (int i = 0; i < chargeList.size(); i++) {
            NormalChargeInfo info = chargeList.get(i);
            if (i == position) {
                info.isSelected = true;
            } else {
                info.isSelected = false;
            }
        }

        NormalChargeInfo info = chargeList.get(position);

        coinNum = info.coinAmount;

        etCount.setText("");

        tvCost.setText(String.valueOf(info.coinAmount));

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.ll_wx) {
            //微信支付
            isWXpay = true;
            updatePayWay();

        } else if (i == R.id.ll_ali) {
            //支付宝
            isWXpay = false;
            updatePayWay();

        } else if (i == R.id.pay_confirm_tv) {
            //确认支付

            payConfirm();
        }

    }

    private void payConfirm() {

        if (coinNum == 0) {
            ToastUtil.showToast(this, "充值数量不能为空");
            return;
        }

        if (isWXpay) {
            mPayWay = PayWay.WechatPay;
        } else {
            mPayWay = PayWay.ALiPay;
        }

        //请求预支付信息

        RequestVo requestVo = new RequestVo();
        requestVo.addParams("memberId", UserHelper.getUserId());
        requestVo.addParams("coinNum", coinNum);
        requestVo.addParams("realName", UserHelper.getUserName());
        requestVo.addParams("userName", UserHelper.getAccount());
        requestVo.addParams("payType", isWXpay ? 2 : 1);

        RequestParams params = new RequestParams(AppConfig.ServerUrl.GET_CHARGE_PRE_INFO + requestVo.getParams());

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
                    int recordId = jsonObject.optInt("recordId");

                    doPay(recordId);

                }

            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {

            }

            @Override
            public void onFinished() {
            }
        });

    }

    private void doPay(int recordId) {

        PayParams params;
        if (mPayWay == PayWay.WechatPay) {
            params = new PayParams.Builder(this)
                    .wechatAppID(AppConfig.WEIXIN_APPID)// 仅当支付方式选择微信支付时需要此参数
                    .payWay(mPayWay)
                    .recordId(recordId)
                    .setCharge(true)
                    .build();
        } else {
            params = new PayParams.Builder(this)
                    .payWay(mPayWay)
                    .recordId(recordId)
                    .setCharge(true)
                    .build();
        }

        LqPay.newInstance(params).requestPayInfo(new OnPayInfoRequestListener() {
            @Override
            public void onPayInfoRequetStart() {
                showProgressDialog("");
            }

            @Override
            public void onPayInfoRequstSuccess(String result) {
                //可以将loading状态去掉了。请求预支付信息成功，开始跳转到客户端支付。
                closeProgressDialog();
            }

            @Override
            public void onPayInfoRequestFailure() {
                closeProgressDialog();
            }
        }).toPay(new OnPayResultListener() {
            @Override
            public void onPaySuccess(PayWay payWay) {
                // 支付成功
                LogUtils.logd("pay", " payWay == " + payWay.toString());
                finish();
            }

            @Override
            public void onPayCancel(PayWay payWay) {
                // 支付流程被用户中途取消
                LogUtils.logd("pay", " payWay == " + payWay.toString());
                ToastUtil.showToast(ChargeCenterActivity.this, getString(R.string.cancel_pay_result));
                //initResultView(false);
            }

            @Override
            public void onPayFailure(PayWay payWay, int errCode) {
                // 支付失败，
                LogUtils.logd("pay", " payWay == " + payWay.toString() + "  errCode ==  " + errCode);
                //initResultView(false);
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void updatePayWay() {

        if (isWXpay) {

            llWx.setBackgroundResource(R.drawable.selected_pay);
            llAli.setBackgroundResource(R.drawable.unselected_pay);

        } else {

            llWx.setBackgroundResource(R.drawable.unselected_pay);
            llAli.setBackgroundResource(R.drawable.selected_pay);
        }

    }
}
