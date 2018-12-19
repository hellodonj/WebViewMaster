package com.lqwawa.intleducation.lqpay.pay.paystrategy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lqwawa.intleducation.MainApplication;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.utils.ToastUtil;
import com.lqwawa.intleducation.lqpay.LqPay;
import com.lqwawa.intleducation.lqpay.PayParams;
import com.lqwawa.intleducation.lqpay.pay.PrePayInfo;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.util.List;


/**
 * ================================================
 * 作    者：Blizzard-liu
 * 版    本：1.0
 * 创建日期：2017/7/17 15:12
 * 描    述：微信支付策略.
 * 修订历史：
 * ================================================
 */

public class WeChatPayStrategy extends BasePayStrategy {
    private LocalBroadcastManager mBroadcastManager;
    private Context mContext;

    public static final String WECHAT_PAY_RESULT_ACTION = "com.tencent.mm.opensdk.WECHAT_PAY_RESULT_ACTION";
    public static final String WECHAT_PAY_RESULT_EXTRA = "com.tencent.mm.opensdk.WECHAT_PAY_RESULT_EXTRA";

    public WeChatPayStrategy(PayParams params, String prePayInfo, LqPay.PayCallBack resultListener) {
        super(params, prePayInfo, resultListener);
        mContext = params.getActivity();
    }

    @Override
    public void doPay() {
        IWXAPI wxapi = WXAPIFactory.createWXAPI(mContext.getApplicationContext(), mPayParams.getWeChatAppID(), true);
        if (!wxapi.isWXAppInstalled()) {
            ToastUtil.showToast(MainApplication.getInstance(), R.string.plz_install_ex);
            //super.mOnPayResultListener.onPayCallBack(LqPay.WECHAT_NOT_INSTALLED_ERR);
            return;
        }

        if (!wxapi.isWXAppSupportAPI()) {
            super.mOnPayResultListener.onPayCallBack(LqPay.WECHAT_UNSUPPORT_ERR);
            return;
        }
        wxapi.registerApp(mPayParams.getWeChatAppID());
        registPayResultBroadcast();

        // TODO 需要做正式解析，修改PrePayInfo.java类，并解开此处注释
        Gson gson = new Gson();
        //PrePayInfo payInfo = gson.fromJson(mPrePayInfo, PrePayInfo.class);

        List<PrePayInfo> retList = gson.fromJson(mPrePayInfo,
                new TypeToken<List<PrePayInfo>>() {
                }.getType());

        PrePayInfo payInfo = retList.get(0);

        PayReq req = new PayReq();
        req.appId = payInfo.appid;
        req.partnerId = payInfo.partnerid;
        req.prepayId = payInfo.prepayid;
        req.packageValue = payInfo.packageValue;
        req.nonceStr = payInfo.noncestr;
        req.timeStamp = payInfo.timestamp;
        req.sign = payInfo.sign;

        // 发送支付请求：跳转到微信客户端
        boolean b = wxapi.sendReq(req);
    }

    private void registPayResultBroadcast() {
        mBroadcastManager = LocalBroadcastManager.getInstance(mContext.getApplicationContext());
        IntentFilter filter = new IntentFilter(WECHAT_PAY_RESULT_ACTION);
        mBroadcastManager.registerReceiver(mReceiver, filter);
    }

    private void unRegistPayResultBroadcast() {
        if (mBroadcastManager != null && mReceiver != null) {
            mBroadcastManager.unregisterReceiver(mReceiver);
            mBroadcastManager = null;
            mReceiver = null;
        }
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int result = intent.getIntExtra(WECHAT_PAY_RESULT_EXTRA, -100);
            mOnPayResultListener.onPayCallBack(result);
            unRegistPayResultBroadcast();
        }
    };
}
