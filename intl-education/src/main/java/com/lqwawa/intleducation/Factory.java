package com.lqwawa.intleducation;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.telecom.Call;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lqwawa.intleducation.base.vo.ResponseVo;
import com.lqwawa.intleducation.factory.data.DataSource;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 数据请求错误统一分发处理
 * @date 2018/04/09 11:47
 * @history v1.0
 * **********************************
 */
public class Factory {

    private static final String TAG = Factory.class.getSimpleName();
    /**
     * 单例模式
     */
    private static final Factory instance;

    static {
        instance = new Factory();
    }

    private Factory() {
    }

    /**
     * 统一处理网络请求错误码
     * @param code 请求错误码
     * @param callback 失败回调对象
     */
    public static void decodeRspCode(int code, @NonNull DataSource.FailedCallback callback) {
        int stringRes = R.string.data_rsp_error_unknown;
        switch (code) {
            case ResponseVo.ERROR_UNKNOWN:
                stringRes = R.string.data_rsp_error_unknown;
                break;
        }

        if(null != callback){
            callback.onDataNotAvailable(stringRes);
        }
    }
}
