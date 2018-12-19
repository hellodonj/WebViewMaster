package com.lqwawa.intleducation.lqpay.pay.paystrategy;


import com.lqwawa.intleducation.lqpay.LqPay;
import com.lqwawa.intleducation.lqpay.PayParams;

/**
  * ================================================
  * 作    者：Blizzard-liu
  * 版    本：1.0
  * 创建日期：2017/7/17 15:03
  * 描    述：支付策略类抽象类.
  * 修订历史：
  * ================================================
  */

public abstract class BasePayStrategy implements PayStrategyInterf{
    protected PayParams mPayParams;
    protected String mPrePayInfo;
    protected LqPay.PayCallBack mOnPayResultListener;

    public BasePayStrategy(PayParams params, String prePayInfo, LqPay.PayCallBack resultListener) {
        mPayParams = params;
        mPrePayInfo = prePayInfo;
        mOnPayResultListener = resultListener;
    }

    public abstract void doPay();
}