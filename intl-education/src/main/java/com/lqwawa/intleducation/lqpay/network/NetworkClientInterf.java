package com.lqwawa.intleducation.lqpay.network;


import com.lqwawa.intleducation.lqpay.PayParams;


 /**
  * ================================================
  * 作    者：Blizzard-liu
  * 版    本：1.0
  * 创建日期：2017/7/17 15:04
  * 描    述：网络访问接口.
  * 修订历史：
  * ================================================
  */

public interface NetworkClientInterf {
    interface CallBack<R> {
        void onSuccess(R result);
        void onFailure();
    }

    void get(PayParams payParams, CallBack c);

    void post(PayParams payParams, CallBack c);
}
