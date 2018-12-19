package com.lqwawa.intleducation.module.organcourse.online;

import android.support.annotation.NonNull;

/**
 * @desc 在线机构学程馆Activity对Fragment Search控制定义
 */
public interface SearchNavigator {

    // 搜索
    boolean search(@NonNull String searchKey);
    // 价格排序发生变化
    boolean triggerPriceSwitch(@NonNull boolean up);

}
