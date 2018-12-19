package com.lqwawa.intleducation.lqpay.enums;

/**
 * ================================================
 * 作    者：Blizzard-liu
 * 版    本：1.0
 * 创建日期：2017/7/17 15:01
 * 描    述：
 * 修订历史：
 * ================================================
 */

public enum PayWay {
    WechatPay(0),//微信
    ALiPay(1),//支付宝
    UPPay(2),//银行卡
    Code(3),//激活码
    WaWa(4);//蛙蛙币


    int payway;

    PayWay(int way) {
        payway = way;
    }

    @Override
    public String toString() {
        return String.valueOf(payway);
    }

}
