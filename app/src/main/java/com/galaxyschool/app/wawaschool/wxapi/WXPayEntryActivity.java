package com.galaxyschool.app.wawaschool.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.lqpay.pay.paystrategy.WeChatPayStrategy;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;


public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {
    private static final String TAG = "WXPayEntryActivity";

    private IWXAPI mWxApi;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWxApi = WXAPIFactory.createWXAPI(this, AppConfig.WEIXIN_APPID);
        mWxApi.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        mWxApi.handleIntent(intent, this);
    }


    @Override
    public void onReq(BaseReq baseReq) {
        Log.d(TAG, "请求发出的回调");
    }

    @Override
    public void onResp(BaseResp baseResp) {
        int errCode = baseResp.errCode;
        Log.v("ashin", "微信支付结果回调" + errCode);
        sendPayResultBroadcast(errCode);
    }

    /**
     * 发送微信支付结果的本地广播
     * 本地广播比全局广播效率高，更安全
     *
     * @param resultCode
     */
    private void sendPayResultBroadcast(int resultCode) {
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());
        Intent payResult = new Intent();
        payResult.setAction(WeChatPayStrategy.WECHAT_PAY_RESULT_ACTION);
        payResult.putExtra(WeChatPayStrategy.WECHAT_PAY_RESULT_EXTRA, resultCode);
        broadcastManager.sendBroadcast(payResult);
        finish();
    }
}
