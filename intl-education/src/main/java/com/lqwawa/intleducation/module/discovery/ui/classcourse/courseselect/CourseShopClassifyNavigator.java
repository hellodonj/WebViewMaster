package com.lqwawa.intleducation.module.discovery.ui.classcourse.courseselect;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;

/**
 * 回调方法的定义
 */
public interface CourseShopClassifyNavigator {

    boolean judgeClassifyAuthorizedInfo(@NonNull LQCourseConfigEntity entity);

}
