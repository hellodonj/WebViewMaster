package com.lqwawa.intleducation.module.watchcourse;

import android.support.annotation.IntDef;
import android.support.annotation.StringDef;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 选取学程资源类型定义
 * @date 2018/06/26 11:37
 * @history v1.0
 * **********************************
 */
public interface WatchResourceType {
    // 看课件
    int TYPE_WATCH_COURSE = 9;
    // 复述课件
    int TYPE_RETELL_COURSE = 5;
    // 任务单
    int TYPE_TASK_ORDER = 8;

    @Target(ElementType.PARAMETER)
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({TYPE_WATCH_COURSE,TYPE_RETELL_COURSE,TYPE_TASK_ORDER})
    public @interface WatchResourceRes{

    }
}
