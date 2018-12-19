package com.lqwawa.intleducation.lqpay;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.lqwawa.intleducation.base.utils.NetWorkUtils;
import com.lqwawa.intleducation.lqpay.callback.OnPayInfoRequestListener;
import com.lqwawa.intleducation.lqpay.callback.OnPayResultListener;
import com.lqwawa.intleducation.lqpay.enums.HttpType;
import com.lqwawa.intleducation.lqpay.enums.PayWay;
import com.lqwawa.intleducation.lqpay.network.NetworkClientFactory;
import com.lqwawa.intleducation.lqpay.network.NetworkClientInterf;
import com.lqwawa.intleducation.lqpay.pay.paystrategy.ALiPayStrategy;
import com.lqwawa.intleducation.lqpay.pay.paystrategy.ActCodeStrategy;
import com.lqwawa.intleducation.lqpay.pay.paystrategy.PayContext;
import com.lqwawa.intleducation.lqpay.pay.paystrategy.UPPayStrategy;
import com.lqwawa.intleducation.lqpay.pay.paystrategy.WeChatPayStrategy;
import com.osastudio.common.utils.LogUtils;

/**
 * ================================================
 * 作    者：Blizzard-liu
 * 版    本：1.0
 * 创建日期：2017/7/17 15:29
 * 描    述：支付SDK封装工具类.
 * 修订历史：
 * ================================================
 */

public final class LqPay {
    private OnPayInfoRequestListener mOnPayInfoRequestListener;
    private OnPayResultListener mOnPayResultListener;
    private PayParams mPayParams;

    private static LqPay sInstance;

    // 通用结果码
    public static final int COMMON_PAY_OK = 0;
    public static final int COMMON_PAY_ERR = -1;//支付失败-原因未知，需要开发者手动排查
    public static final int COMMON_USER_CACELED_ERR = -2;
    public static final int COMMON_NETWORK_NOT_AVAILABLE_ERR = 1;//当前网络无连接（尚未进入支付阶段）
    public static final int COMMON_REQUEST_TIME_OUT_ERR = 2;//请求APP服务器超时（尚未进入支付阶段）

    // 微信结果码
    public static final int WECHAT_SENT_FAILED_ERR = -3;//微信接收支付请求失败
    public static final int WECHAT_AUTH_DENIED_ERR = -4;//微信支付认证失败，拒绝支付交易
    public static final int WECHAT_UNSUPPORT_ERR = -5;//微信版本低，不支持交易
    public static final int WECHAT_BAN_ERR = -6;//微信拒绝了支付交易
    public static final int WECHAT_NOT_INSTALLED_ERR = -7;//未安装微信客户端，交易失败

    // 支付宝结果码
    public static final int ALI_PAY_WAIT_CONFIRM_ERR = 8000;//正在处理中，支付结果未知（有可能已经支付成功），请查询商户订单列表中订单的支付状态
    public static final int ALI_PAY_NET_ERR = 6002;//网络差导致支付失败
    public static final int ALI_PAY_UNKNOW_ERR = 6004;//支付结果未知
    public static final int ALI_PAY_OTHER_ERR = 6005;//支付失败，原因未知

    // 银联结果码
    public static final int UPPAY_PLUGIN_NOT_INSTALLED = -10;//银联支付插件未安装
    public static final int UPPAy_PLUGIN_NEED_UPGRADE = -11;//银联支付插件需要升级，基本不会碰到

    // 激活码结果码
    public static final int CODE_EXPIRE = -401;//激活码过期
    public static final int CODE_INOPERATIVE = -402;//激活码未生效
    public static final int CODE_ERROR = -403;//激活码错误

    private LqPay(PayParams params) {
        mPayParams = params;
    }

    public static LqPay newInstance(PayParams params) {
        if (params != null) {
            sInstance = new LqPay(params);
            return sInstance;
        }
        return sInstance;
    }

    public String getWeChatAppID() {
        return mPayParams.getWeChatAppID();
    }

    public void toPay(@NonNull OnPayResultListener onPayResultListener) {
        mOnPayResultListener = onPayResultListener;
        if (!NetWorkUtils.isNetworkAvailable(mPayParams.getActivity().getApplicationContext())) {
            sendPayResult(COMMON_NETWORK_NOT_AVAILABLE_ERR);
        }
    }

    /**
     * 进行支付策略分发
     *
     * @param prePayInfo
     */
    private void doPay(String prePayInfo) {
        PayContext pc = null;
        PayWay way = mPayParams.getPayWay();
        PayCallBack callBack = new PayCallBack() {
            @Override
            public void onPayCallBack(int code) {
                sendPayResult(code);
            }
        };

        switch (way) {
            case WechatPay:
                pc = new PayContext(new WeChatPayStrategy(mPayParams, prePayInfo, callBack));
                break;

            case ALiPay:
                pc = new PayContext(new ALiPayStrategy(mPayParams, prePayInfo, callBack));
                break;

            case UPPay:
                pc = new PayContext(new UPPayStrategy(mPayParams, prePayInfo, callBack));
                break;
            case Code:
                pc = new PayContext(new ActCodeStrategy(mPayParams, prePayInfo, callBack));
                break;

            default:
                break;
        }
        pc.pay();
    }

    /**
     * 请求APP服务器获取预支付信息：微信，支付宝，银联都需要此步骤
     *
     * @param onPayInfoRequestListener
     * @return
     */
    public LqPay requestPayInfo(OnPayInfoRequestListener onPayInfoRequestListener) {
        if (mPayParams.getPayWay() == null && !TextUtils.isEmpty(mPayParams.getOrderId())) {
            throw new NullPointerException("请设置支付方式");
        }

        if (!TextUtils.isEmpty(mPayParams.getActiveCode())) {//激活码支付
            doPay(mPayParams.getOrderId());
            return this;
        }
        mOnPayInfoRequestListener = onPayInfoRequestListener;
        mOnPayInfoRequestListener.onPayInfoRequetStart();

        final NetworkClientInterf client = NetworkClientFactory.newClient();
        //获取支付字符串
        final NetworkClientInterf.CallBack orderInfocallBack = new NetworkClientInterf.CallBack<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtils.logd("pay", " result == " + result);
                mOnPayInfoRequestListener.onPayInfoRequstSuccess(result);
                doPay(result);
            }

            @Override
            public void onFailure() {
                mOnPayInfoRequestListener.onPayInfoRequestFailure();
                //                sendPayResult(COMMON_REQUEST_TIME_OUT_ERR);
            }
        };

        //获取订单id
        NetworkClientInterf.CallBack orderIdCallBack = new NetworkClientInterf.CallBack<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtils.logd("pay", " result == " + result);
                mOnPayInfoRequestListener.onPayInfoRequstSuccess(result);
            }

            @Override
            public void onFailure() {
                mOnPayInfoRequestListener.onPayInfoRequestFailure();
            }
        };

        NetworkClientInterf.CallBack callBack;

        if (mPayParams.getRecordId() != 0 || mPayParams.getOrderId() != null) {
            callBack = orderInfocallBack;
        } else {
            callBack = orderIdCallBack;
        }


        HttpType type = mPayParams.getHttpType();
        switch (type) {
            case Get:
                client.get(mPayParams, callBack);
                break;

            case Post:
            default:
                client.get(mPayParams, callBack);
                break;
        }
        return this;
    }

    /**
     * 回调支付结果到请求界面
     *
     * @param code
     */
    private void sendPayResult(int code) {
        if (mPayParams == null)
            return;

        switch (code) {
            case COMMON_PAY_OK:
                mOnPayResultListener.onPaySuccess(mPayParams.getPayWay());
                break;

            case COMMON_USER_CACELED_ERR:
                mOnPayResultListener.onPayCancel(mPayParams.getPayWay());
                break;

            default:
                mOnPayResultListener.onPayFailure(mPayParams.getPayWay(), code);
                break;
        }
        releaseMomery();
    }

    private void releaseMomery() {
        if (mPayParams == null)
            return;
        mPayParams = null;
        sInstance = null;
    }

    public interface PayCallBack {
        void onPayCallBack(int code);
    }

}
