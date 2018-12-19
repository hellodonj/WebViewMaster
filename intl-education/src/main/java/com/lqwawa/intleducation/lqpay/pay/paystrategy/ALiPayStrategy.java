package com.lqwawa.intleducation.lqpay.pay.paystrategy;

import android.os.Handler;
import android.os.Message;

import com.alipay.sdk.app.PayTask;
import com.lqwawa.intleducation.lqpay.LqPay;
import com.lqwawa.intleducation.lqpay.PayParams;
import com.lqwawa.intleducation.lqpay.pay.ALiPayResult;
import com.lqwawa.intleducation.lqpay.util.ThreadManager;

import java.util.Map;


 /**
  * ================================================
  * 作    者：Blizzard-liu
  * 版    本：1.0
  * 创建日期：2017/7/17 15:14
  * 描    述：支付宝策略.
  * 修订历史：
  * ================================================
  */

public class ALiPayStrategy extends BasePayStrategy {
    private static final int PAY_RESULT_MSG = 0;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what != PAY_RESULT_MSG) {
                return;
            }
            ThreadManager.shutdown();
            @SuppressWarnings("unchecked")
            ALiPayResult result = new ALiPayResult((Map<String, String>) msg.obj);
            switch (result.getResultStatus()) {
                case ALiPayResult.PAY_OK_STATUS:
                    mOnPayResultListener.onPayCallBack(LqPay.COMMON_PAY_OK);
                    break;

                case ALiPayResult.PAY_CANCLE_STATUS:
                    mOnPayResultListener.onPayCallBack(LqPay.COMMON_USER_CACELED_ERR);
                    break;

                case ALiPayResult.PAY_FAILED_STATUS:
                    mOnPayResultListener.onPayCallBack(LqPay.COMMON_PAY_ERR);
                    break;

                case ALiPayResult.PAY_WAIT_CONFIRM_STATUS:
                    mOnPayResultListener.onPayCallBack(LqPay.ALI_PAY_WAIT_CONFIRM_ERR);
                    break;

                case ALiPayResult.PAY_NET_ERR_STATUS:
                    mOnPayResultListener.onPayCallBack(LqPay.ALI_PAY_NET_ERR);
                    break;

                case ALiPayResult.PAY_UNKNOWN_ERR_STATUS:
                    mOnPayResultListener.onPayCallBack(LqPay.ALI_PAY_UNKNOW_ERR);
                    break;

                default:
                    mOnPayResultListener.onPayCallBack(LqPay.ALI_PAY_OTHER_ERR);
                    break;
            }
            mHandler.removeCallbacksAndMessages(null);
        }
    };

    public ALiPayStrategy(PayParams params, String prePayInfo, LqPay.PayCallBack resultListener) {
        super(params, prePayInfo, resultListener);
    }

     /**
      * 支付宝支付业务
      *
      *
      */
    @Override
    public void doPay() {
        Runnable payRun = new Runnable() {
            @Override
            public void run() {
                PayTask task = new PayTask(mPayParams.getActivity());
                Map<String, String> result = task.payV2(mPrePayInfo, true);
                Message message = mHandler.obtainMessage();
                message.what = PAY_RESULT_MSG;
                message.obj = result;
                mHandler.sendMessage(message);
            }
        };
        ThreadManager.execute(payRun);
    }

 }
