package com.lqwawa.intleducation.module.discovery.ui.lqcourse.classifylist;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 分类列表的回调接口定义
 * @date 2018/05/02 15:23
 * @history v1.0
 * **********************************
 */
public interface ClassifyListNavigator {
    /**
     * 点击某个课程分类的回调
     * @param entity 课程分类实体
     */
    void onItemClick(@NonNull LQCourseConfigEntity entity);

}
