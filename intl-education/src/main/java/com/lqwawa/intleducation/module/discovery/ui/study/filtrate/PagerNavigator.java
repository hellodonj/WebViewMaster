package com.lqwawa.intleducation.module.discovery.ui.study.filtrate;

import android.support.annotation.NonNull;

/**
 * @author mrmedici
 * @desc Pager页面的接口定义
 */
public interface PagerNavigator {
    // 价格排序发生变化
    boolean triggerPriceSwitch(@NonNull boolean up);

    void reloadData();
}
