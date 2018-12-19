package com.lqwawa.intleducation.lqpay.callback;

 /**
  * ================================================
  * 作    者：Blizzard-liu
  * 版    本：1.0
  * 创建日期：2017/7/17 15:00
  * 描    述：
  * 修订历史：
  * ================================================
  */

public interface OnPayInfoRequestListener {
    void onPayInfoRequetStart();

    void onPayInfoRequstSuccess(String result);

    void onPayInfoRequestFailure();
}
