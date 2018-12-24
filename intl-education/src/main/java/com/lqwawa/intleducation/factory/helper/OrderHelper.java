package com.lqwawa.intleducation.factory.helper;

import android.support.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.Factory;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.vo.RequestVo;
import com.lqwawa.intleducation.base.vo.ResponseVo;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.LogUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.StringCallback;
import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.home.LanguageType;

import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.List;

/**
 * @desc 订单接口的相关帮助类
 * @author medici
 */
public class OrderHelper {

    /**
     * @desc 检查订单状态
     * @param orderId 订单Id
     */
    public static void checkOrder(@NonNull int orderId,
                                  @NonNull DataSource.Callback<ResponseVo<Object>> callback){

        RequestVo requestVo = new RequestVo();
        requestVo.addParams("orderId",orderId);
        final RequestParams params = new RequestParams(AppConfig.ServerUrl.GetCheckOrder+requestVo.getParams());
        params.setConnectTimeout(10000);
        LogUtil.i(OrderHelper.class,"send request ==== " +params.getUri());
        x.http().get(params, new StringCallback<String>() {
            @Override
            public void onSuccess(String str) {
                LogUtil.i(OrderHelper.class,"request "+params.getUri()+" result :"+str);
                ResponseVo<Object> result = JSON.parseObject(str,new TypeReference<ResponseVo<Object>>() {});
                if (!EmptyUtil.isEmpty(callback)) {
                    callback.onDataLoaded(result);
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.w(OrderHelper.class,"request "+params.getUri()+" failed");
                if(!EmptyUtil.isEmpty(callback)){
                    callback.onDataNotAvailable(R.string.net_error_tip);
                }
            }
        });

    }

    /**
     * @desc 检查订单状态
     * @param memberId 赠送人Id
     * @param beneficiaryId 受益人Id
     * @param consumeSource 受益设备来源 2 Android Phone 3 Android Pad
     * @param amount 转赠金额
     * @param callback 回调对象
     */
    public static void requestUserBalanceDonation(@NonNull String memberId,
                                  @NonNull String beneficiaryId,
                                  int consumeSource,
                                  int amount,
                                  @NonNull DataSource.Callback<Boolean> callback){

        RequestVo requestVo = new RequestVo();
        requestVo.addParams("memberId",memberId);
        requestVo.addParams("beneficiaryId",beneficiaryId);
        requestVo.addParams("consumeSource",consumeSource);
        requestVo.addParams("amount",amount);
        final RequestParams params = new RequestParams(AppConfig.ServerUrl.GetWaWaGiveUrl+requestVo.getParams());
        params.setConnectTimeout(10000);
        LogUtil.i(OrderHelper.class,"send request ==== " +params.getUri());
        x.http().get(params, new StringCallback<String>() {
            @Override
            public void onSuccess(String str) {
                LogUtil.i(OrderHelper.class,"request "+params.getUri()+" result :"+str);
                ResponseVo<Void> vo = JSON.parseObject(str,new TypeReference<ResponseVo<Void>>() {});
                if (EmptyUtil.isNotEmpty(callback)) {
                    callback.onDataLoaded(vo.isSucceed());
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.w(OrderHelper.class,"request "+params.getUri()+" failed");
                if(!EmptyUtil.isEmpty(callback)){
                    callback.onDataNotAvailable(R.string.net_error_tip);
                }
            }
        });

    }

}
