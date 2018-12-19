package com.lqwawa.intleducation.base;

import android.support.annotation.StringRes;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 基础的占位布局接口定义
 * @date 2018/04/09 16:34
 * @history v1.0
 * **********************************
 */
public interface PlaceHolderView {

    /**
     * 没有数据
     * 显示空布局，隐藏当前数据布局
     */
    void triggerEmpty();

    /**
     * 网络错误
     * 显示一个网络错误的图标
     */
    void triggerNetError();

    /**
     * 加载错误，并显示错误信息
     *
     * @param strRes 错误信息
     */
    void triggerError(@StringRes int strRes);

    /**
     * 显示正在加载的状态
     */
    void triggerLoading();

    /**
     * 是否支持显示Loading
     * @return true 支持显示
     */
    boolean isShowLoading();

    /**
     * 数据加载成功，
     * 调用该方法时应该隐藏当前占位布局
     */
    void triggerOk();

    /**
     * 该方法如果传入的isOk为True则为成功状态，
     * 此时隐藏布局，反之显示空数据布局
     *
     * @param isOk 是否加载成功数据
     */
    void triggerOkOrEmpty(boolean isOk);

    /**
     * 设置网络错误回调重新加载的监听
     * @param listener 监听对象
     */
    void setOnReloadDataListener(NetErrorView.OnReloadDataListener listener);
}
