package com.lqwawa.intleducation.module.onclass.detail.base.notification;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.factory.data.entity.ClassNotificationEntity;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function Adapter事件回调定义
 * @date 2018/06/04 16:55
 * @history v1.0
 * **********************************
 */
public interface ClassNotificationNavigator {

    // 删除某个通知
    void onNotificationDelete(@NonNull ClassNotificationEntity entity);

}
