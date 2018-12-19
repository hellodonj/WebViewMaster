package com.lqwawa.intleducation.module.spanceschool;

import android.support.annotation.IntDef;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 校园助手校园动态的显示状态定义
 * @date 2018/07/03 09:23
 * @history v1.0
 * **********************************
 */
public interface SchoolFunctionStateType {
    // 不显示校园助手和校园动态
    int TYPE_GONE = 0;
    // 显示校园助手
    int TYPE_PRINCIPAL_ASSISTANT = 1;
    // 显示校园动态
    int TYPE_CAMPUS_PATROL = 2;

    @Target(ElementType.PARAMETER)
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({TYPE_GONE,TYPE_PRINCIPAL_ASSISTANT,TYPE_CAMPUS_PATROL})
    public @interface FunctionStateRes{

    }
}
