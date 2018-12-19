package com.lqwawa.intleducation.module.onclass.detail.base.notification;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.factory.data.entity.ClassNotificationEntity;
import com.lqwawa.intleducation.factory.presenter.BaseContract;

import java.util.List;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 在线课堂班级详情通知片段的契约类
 * @date 2018/06/04 14:04
 * @history v1.0
 * **********************************
 */
public interface ClassNotificationContract{

    interface Presenter extends BaseContract.Presenter{
        // 获取通知列表数据
        void requestNotificationData(@NonNull String classId, int pageIndex);
        // 删除通知数据
        void requestDeleteNotification(@NonNull String id, int type);
    }

    interface View extends BaseContract.View<Presenter>{
        // 请求通知数据
        void updateNotificationView(@NonNull List<ClassNotificationEntity> entities);
        // 请求更多通知数据
        void updateMoreNotificationView(@NonNull List<ClassNotificationEntity> entities);
        // 删除通知的回调
        void updateDeleteNotificationView(boolean result);
    }

}
