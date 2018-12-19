package com.lqwawa.intleducation.lqpay.callback;


import com.lqwawa.intleducation.lqpay.enums.PayWay;

 /**
  * ================================================
  * 作    者：Blizzard-liu
  * 版    本：1.0
  * 创建日期：2017/7/17 15:00
  * 描    述：
  * 修订历史：
  * ================================================
  */

public interface OnPayResultListener {
    void onPaySuccess(PayWay payWay);

    void onPayCancel(PayWay payWay);

    void onPayFailure(PayWay payWay, int errCode);
}
