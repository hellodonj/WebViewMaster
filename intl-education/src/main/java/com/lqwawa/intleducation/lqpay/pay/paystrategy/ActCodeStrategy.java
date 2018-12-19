package com.lqwawa.intleducation.lqpay.pay.paystrategy;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.base.vo.RequestVo;
import com.lqwawa.intleducation.lqpay.LqPay;
import com.lqwawa.intleducation.lqpay.PayParams;
import com.lqwawa.intleducation.lqpay.PayResult;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;


/**
 * ================================================
 * 作    者：Blizzard-liu
 * 版    本：1.0
 * 创建日期：2017/7/17 15:15
 * 描    述：激活码支付
 * 修订历史：
 * ================================================
 */

public class ActCodeStrategy extends BasePayStrategy {


   public ActCodeStrategy(PayParams params, String prePayInfo, LqPay.PayCallBack resultListener) {
       super(params, prePayInfo, resultListener);
   }

   @Override
   public void doPay() {

       RequestVo requestVo = new RequestVo();

       requestVo.addParams("id", mPayParams.getOrderId());
       requestVo.addParams("activeCode", mPayParams.getActiveCode());
       // 传购买人的Id
       String memberId = mPayParams.getMemberId();
       requestVo.addParams("memberId",memberId);


       RequestParams params =
               new RequestParams(AppConfig.ServerUrl.PAY_USEACTIVATIONCODE + requestVo.getParams());
       params.setConnectTimeout(10000);
       x.http().get(params, new Callback.CommonCallback<String>() {
           @Override
           public void onSuccess(String s) {
               PayResult result = JSON.parseObject(s,
                       new TypeReference<PayResult>() {
                       });
               if (result.getCode() == 0) {
                   mOnPayResultListener.onPayCallBack(LqPay.COMMON_PAY_OK);
               } else if (result.getCode() == 1001) {//错误
                   mOnPayResultListener.onPayCallBack(LqPay.CODE_ERROR);
               }else if (result.getCode() == 1002) {//过期
                   mOnPayResultListener.onPayCallBack(LqPay.CODE_EXPIRE);
               }else if (result.getCode() == 1003) {//未生效
                   mOnPayResultListener.onPayCallBack(LqPay.CODE_INOPERATIVE);
               } else {
                   mOnPayResultListener.onPayCallBack(LqPay.COMMON_PAY_ERR);
                   //                   ToastUtil.showToast(MainApplication.getInstance(),
                   //                           "errorCode = " + result.getCode()
                   //                                   + "   message = "+result.getMessage());
               }
           }

           @Override
           public void onCancelled(org.xutils.common.Callback.CancelledException e) {

           }

           @Override
           public void onError(Throwable throwable, boolean b) {
               mOnPayResultListener.onPayCallBack(LqPay.COMMON_PAY_ERR);
           }

           @Override
           public void onFinished() {

           }
       });

   }


}
