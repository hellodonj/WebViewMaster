package com.lqwawa.intleducation.module.onclass.detail.join;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.factory.data.entity.ClassNotificationEntity;
import com.lqwawa.intleducation.factory.data.entity.JoinClassEntity;
import com.lqwawa.intleducation.module.onclass.detail.base.BaseClassDetailContract;
import com.lqwawa.intleducation.module.onclass.detail.notjoin.ClassDetailContract;

import java.util.List;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 已加入在线课堂班级详情页
 * @date 2018/06/04 13:48
 * @history v1.0
 * **********************************
 */
public class JoinClassDetailContract {

    interface Presenter extends BaseClassDetailContract.Presenter{
        // 获取通知列表数据
        void requestNotificationData(@NonNull String classId, int pageIndex);
        // 请求设置成历史班
        void requestSettingHistory(@NonNull int id);
    }

    interface View extends BaseClassDetailContract.View<Presenter>{
        // 请求通知数据
        void updateNotificationView(@NonNull List<ClassNotificationEntity> entities);
        // 设置成历史班成功
        void updateSettingHistory(boolean complete);
    }

}
