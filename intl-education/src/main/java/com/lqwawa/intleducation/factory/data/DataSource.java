package com.lqwawa.intleducation.factory.data;

import android.support.annotation.StringRes;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 网络请求数据回调接口定义
 * @date 2018/04/09 11:27
 * @history v1.0
 * **********************************
 */
public interface DataSource {

    /**
     * 同时包括了成功与失败的回调接口
     *
     * @param <T> 任意类型
     */
    interface Callback<T> extends SucceedCallback<T>, FailedCallback {

    }

    /**
     * 只关注成功的接口
     *
     * @param <T> 任意类型
     */
    interface SucceedCallback<T> {
        /**
         * 数据加载成功, 网络请求成功
         * @param t 任意类型
         */
        void onDataLoaded(T t);

    }

    /**
     * 只关注失败的接口
     */
    interface FailedCallback {
        /**
         * 数据加载失败, 网络请求失败
         * @param strRes
         */
        void onDataNotAvailable(@StringRes int strRes);
    }


    /**
     * 销毁操作
     */
    void dispose();

}
