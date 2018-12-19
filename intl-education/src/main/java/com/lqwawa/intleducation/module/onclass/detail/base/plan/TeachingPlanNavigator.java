package com.lqwawa.intleducation.module.onclass.detail.base.plan;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.factory.data.entity.LiveEntity;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 授课计划列表点击删除等的接口回调
 * @date 2018/06/02 16:10
 * @history v1.0
 * **********************************
 */
public interface TeachingPlanNavigator {
    // 删除某个直播时候调用
    void onDelete(@NonNull LiveEntity entity);

}
