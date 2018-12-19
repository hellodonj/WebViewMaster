package com.lqwawa.intleducation.lqpay.pay.paystrategy;

 /**
  * ================================================
  * 作    者：Blizzard-liu
  * 版    本：1.0
  * 创建日期：2017/7/17 15:02
  * 描    述：
  * 修订历史：
  * ================================================
  */

public class PayContext {
    private PayStrategyInterf mStrategy;

    public PayContext(PayStrategyInterf strategy) {
        mStrategy = strategy;
    }

    public void pay() {
        if (mStrategy != null) {
            mStrategy.doPay();
        }
    }
}
