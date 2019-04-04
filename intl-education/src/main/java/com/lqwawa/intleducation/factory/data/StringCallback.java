package com.lqwawa.intleducation.factory.data;

import org.xutils.common.Callback;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 对网络请求回调CommonCallback的简单实现
 * @date 2018/04/09 11:36
 * @history v1.0
 * **********************************
 */
public abstract class StringCallback<Data> implements Callback.CommonCallback<Data>{

    @Override
    public void onCancelled(CancelledException e) {

    }

    @Override
    public void onFinished() {

    }

    @Override
    public void onError(Throwable throwable, boolean b) {
        System.out.println();
    }
}
