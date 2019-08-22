package com.lqwawa.intleducation.lqpay.network;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.MainApplication;
import com.lqwawa.intleducation.base.utils.ToastUtil;
import com.lqwawa.intleducation.base.vo.RequestVo;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.lqpay.PayParams;
import com.lqwawa.intleducation.lqpay.PayResult;
import com.lqwawa.intleducation.lqpay.enums.PayWay;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * ================================================
 * 作    者：Blizzard-liu
 * 版    本：1.0
 * 创建日期：2017/7/17 15:11
 * 描    述：网络请求简单封装.
 * 修订历史：
 * ================================================
 */

public class NetworkClient implements NetworkClientInterf {


    @Override
    public void get(final PayParams payParams, final CallBack c) {


        RequestVo requestVo = new RequestVo();
        String url = "";

        if (payParams.isCharge()) {
            //充值请求
            requestVo.addParams("recordId", payParams.getRecordId());
            if (payParams.getPayWay() == PayWay.WechatPay) {
                //微信
                url = AppConfig.ServerUrl.GET_WXPAY_REQEUST_PARAMS;

            } else {
                //支付宝
                url = AppConfig.ServerUrl.GET_CHARGE_ORDER;
            }

        } else {
            //支付请求
            if (TextUtils.isEmpty(payParams.getOrderId())) {
                requestVo.addParams("courseId", payParams.getCourseId());
                requestVo.addParams("price", payParams.getGoodsPrice());
                if(TextUtils.equals(payParams.getMemberId(),payParams.getBuyerMemberId())){
                    // 自己给自己买
                    // 只需要memberId
                    requestVo.addParams("memberId", payParams.getMemberId());
                }else{
                    // buyerId 传谁购买的，那就是自己
                    requestVo.addParams("buyerId",payParams.getMemberId());
                    requestVo.addParams("memberId", payParams.getBuyerMemberId());
                }
                requestVo.addParams("type", payParams.getType());
                if(EmptyUtil.isNotEmpty(payParams.getChapterIds())){
                    requestVo.addParams("chapterIds",payParams.getChapterIds());
                }

                if(payParams.getType() == 0){
                    // LQ学程
                    if(EmptyUtil.isNotEmpty(payParams.getSchoolId())){
                        // 添加支付统计
                        requestVo.addParams("fromOrganId", payParams.getSchoolId());
                    }
                }

                if (EmptyUtil.isNotEmpty(payParams.getRealName())) {
                    try {
                        requestVo.addParams("realName", URLEncoder.encode(payParams.getRealName().trim(), "utf-8"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                url = AppConfig.ServerUrl.COMMIT_ORDER;
            } else {
                if (payParams.getPayWay() == PayWay.WechatPay) {
                    //微信
                    url = AppConfig.ServerUrl.GET_WXPAY_BUY_PARAMS;
                    requestVo.addParams("orderId", payParams.getOrderId());
                    // 需要传递支付人的ID
                    String memberId = payParams.getMemberId();
                    requestVo.addParams("memberId",memberId);

                } else {
                    //支付宝
                    requestVo.addParams("id", payParams.getOrderId());
                    // 需要传递支付人的ID
                    String memberId = payParams.getMemberId();
                    requestVo.addParams("memberId",memberId);
                    url = AppConfig.ServerUrl.GET_ORDERINFO;
                }
            }
        }


      /*  if (payParams.getRecordId() != 0){
            requestVo.addParams("recordId", payParams.getRecordId());
        }else {
            if (TextUtils.isEmpty(payParams.getOrderId())) {
                requestVo.addParams("courseId", payParams.getCourseId());
                requestVo.addParams("price", payParams.getGoodsPrice());
                requestVo.addParams("memberId", payParams.getMemberId());
                requestVo.addParams("type", payParams.getType());
                try {
                    requestVo.addParams("realName", URLEncoder.encode(payParams.getRealName().trim(), "utf-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            } else {
                requestVo.addParams("id", payParams.getOrderId());
            }
        }*/


      /*  if (payParams.getRecordId() != 0){
            url = AppConfig.ServerUrl.GET_CHARGE_ORDER;
        }else {
            if (TextUtils.isEmpty(payParams.getOrderId())) {
                url = AppConfig.ServerUrl.COMMIT_ORDER;
            } else {
                url = AppConfig.ServerUrl.GET_ORDERINFO;
            }
        }*/

        RequestParams params = new RequestParams(url + requestVo.getPayParams());
        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {

                if (payParams.getPayWay() == PayWay.WechatPay) {
                    //微信支付
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(s);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    int code = jsonObject.optInt("code");
                    if (code == 0) {
                        JSONArray jsonArray = jsonObject.optJSONArray("data");

                        c.onSuccess(jsonArray.toString());
                    }

                    return;
                }


                PayResult result = JSON.parseObject(s,
                        new TypeReference<PayResult>() {
                        });


                if (result.getCode() == 0) {

                    if (!TextUtils.isEmpty(payParams.getOrderId()) || payParams.getRecordId() != 0) {
                        //支付字符串
                        c.onSuccess(result.getOrderStr());
                    } else {
                        //订单id
                        c.onSuccess(result.getId());
                    }


                   /* if (TextUtils.isEmpty(payParams.getOrderId())) {
                        c.onSuccess(result.getId());//订单id


                    } else {
                        c.onSuccess(result.getOrderStr());//支付字符串
                    }*/


                } else {
                    c.onFailure();
                    ToastUtil.showToast(MainApplication.getInstance(),
                            result.getMessage());
                }
            }

            @Override
            public void onCancelled(org.xutils.common.Callback.CancelledException e) {
                c.onFailure();
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                c.onFailure();

            }

            @Override
            public void onFinished() {

            }
        });

    }

    @Override
    public void post(PayParams payParams, final CallBack c) {
        RequestVo requestVo = new RequestVo();
        if (TextUtils.isEmpty(payParams.getOrderId())) {
            requestVo.addParams("courseId", payParams.getCourseId());
            requestVo.addParams("price", payParams.getGoodsPrice());
            if(TextUtils.equals(payParams.getMemberId(),payParams.getBuyerMemberId())){
                // 自己给自己买
                // 只需要memberId
                requestVo.addParams("memberId", payParams.getMemberId());
            }else{
                // buyerId 传谁购买的，那就是自己
                requestVo.addParams("buyerId",payParams.getMemberId());
                requestVo.addParams("memberId", payParams.getBuyerMemberId());
            }
            if(EmptyUtil.isNotEmpty(payParams.getChapterIds())){
                requestVo.addParams("chapterIds",payParams.getChapterIds());
            }

            if(payParams.getType() == 0){
                // LQ学程
                if(EmptyUtil.isNotEmpty(payParams.getSchoolId())){
                    // 添加支付统计
                    requestVo.addParams("fromOrganId", payParams.getSchoolId());
                }
            }
            if (EmptyUtil.isNotEmpty(payParams.getRealName())) {
                try {
                    requestVo.addParams("realName", URLEncoder.encode(payParams.getRealName().trim(), "utf-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

        } else {
            requestVo.addParams("id", payParams.getOrderId());
        }
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.GET_ORDERINFO + requestVo.getParams());
        params.setConnectTimeout(10000);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                PayResult result = JSON.parseObject(s,
                        new TypeReference<PayResult>() {
                        });
                if (result.getCode() == 0) {
                    c.onSuccess(result.getOrderStr());

                } else {
                    c.onFailure();
                    ToastUtil.showToast(MainApplication.getInstance(),
                            "errorCode = " + result.getCode()
                                    + "   message = " + result.getMessage());
                }
            }

            @Override
            public void onCancelled(org.xutils.common.Callback.CancelledException e) {
                c.onFailure();
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                c.onFailure();

            }

            @Override
            public void onFinished() {

            }
        });

    }
}
