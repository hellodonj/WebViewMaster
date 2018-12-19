package com.lqwawa.intleducation.lqpay;

import com.lqwawa.intleducation.base.vo.BaseVo;

/**
 * ================================================
 * 作    者：Blizzard-liu
 * 版    本：1.0
 * 创建日期：2017/7/19 9:41
 * 描    述：
 * 修订历史：
 * ================================================
 */

public class PayResult extends BaseVo {

    /**
     * code : 0
     * orderStr : alipay_sdk=alipay-sdk-java-dynamicVersionNo&app_id=2017060107398684&biz_content=%7B%22body%22%3A%22%E8%B4%AD%E4%B9%B0%E8%AF%BE%E7%A8%8B-%E6%B5%8B%E8%AF%95%E8%AF%BE%E7%A8%8B%E6%94%B6%E8%B4%B9%22%2C%22out_trade_no%22%3A%221500346469085%22%2C%22product_code%22%3A%22QUICK_MSECURITY_PAY%22%2C%22subject%22%3A%22%E6%B5%8B%E8%AF%95%E8%AF%BE%E7%A8%8B%E6%94%B6%E8%B4%B9%22%2C%22timeout_express%22%3A%2230m%22%2C%22total_amount%22%3A%2210%22%7D&charset=utf-8&format=json&method=alipay.trade.app.pay&notify_url=http%3A%2F%2Fpay.lqwawa.com%2Flqwawapay%2FnotifyUrl&sign=V%2Fy09ROjLF54jUwIVOSqlPF6BRqVB1TXuqG6OHODo6Oh0exeBw1nn8jFPYheL6KEEIEWq%2FXffTxeCoLakymW9hbHUdwrHeBqNDv6I4wEKFsGQGuJhFTANpg5Cvjug5t2D9SjfSpNSeoPcZDHMmbLYA9nsKWpv1jra0nHzqa%2BOvBhPEtBGrHn9F85cE5i7tBBQS%2BlPGVh6EREpb0Z%2FKxmjdSNIauIjpGOMm347FQQisEpSPDxO22fWB9I1RS4D8M%2Bomwu%2FHM7Xd7KLC6nshwmXKyLk4W0%2F6AMHJmD0vYSEoegjNN7k99Meu0nS8iyfNB3XqP8Md1ywULsjJNZv39oGA%3D%3D&sign_type=RSA2&timestamp=2017-07-18+10%3A54%3A36&version=1.0
     */

    private int code;
    private String orderStr;//支付字符串
    private String message;
    private String id;//订单id

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getOrderStr() {
        return orderStr;
    }

    public void setOrderStr(String orderStr) {
        this.orderStr = orderStr;
    }
}
