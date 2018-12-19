package com.lqwawa.intleducation.module.onclass.detail.notjoin;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.factory.data.entity.JoinClassEntity;
import com.lqwawa.intleducation.module.onclass.detail.base.BaseClassDetailContract;

import cn.robotpen.pen.service.RobotServiceContract;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 班级详情契约类
 * @date 2018/06/01 11:52
 * @history v1.0
 * **********************************
 */
public interface ClassDetailContract {

    interface Presenter extends BaseClassDetailContract.Presenter{
        // 参加免费班级
        void joinInOnlineGratisClass(int classId);
        // 获取班级详情信息
        void requestLoadClassInfo(@NonNull String classId);
    }

    interface View extends BaseClassDetailContract.View<Presenter>{
        // 参加免费班级成功与否的回调
        void updateJoinOnlineGratisClass(boolean result);
        // 用户已经参加该课程的回调
        void onClassCheckSucceed(@NonNull JoinClassEntity entity);
    }

}
