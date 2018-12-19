package com.lqwawa.intleducation.lqpay;

/**
 * ================================================
 * 作    者：Blizzard-liu
 * 版    本：1.0
 * 创建日期：2017/7/21 9:17
 * 描    述：
 * 修订历史：
 * ================================================
 */

public interface PayStatus {
    /**
     * 未支付
     */
    int PAY_CANCEL = 0;
    /**
     * 成功
     */
    int PAY_OK = 1;
    /**
     * 失败
     */
    int PAY_FAILURE = 2;
}
