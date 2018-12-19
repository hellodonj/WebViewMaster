package com.lqwawa.intleducation.module.discovery.ui.coursedetail;

import android.support.annotation.IntDef;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author mrmedici
 * @desc 课程详情入口类型定义
 */
public interface CourseDetailType {
    // 慕课直接进入
    int COURSE_DETAIL_MOOC_ENTER = 0;
    // 班级学程入口进入
    int COURSE_DETAIL_CLASS_ENTER = 1;
    // 自主学习（学程馆）入口进入
    int COURSE_DETAIL_SCHOOL_ENTER = 2;

    @Target(ElementType.PARAMETER)
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({COURSE_DETAIL_MOOC_ENTER,COURSE_DETAIL_CLASS_ENTER,COURSE_DETAIL_SCHOOL_ENTER})
    public @interface CourseDetailRes{

    }
}
