package com.lqwawa.intleducation.module.discovery.ui.mycourse;

import android.support.annotation.IntDef;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author mrmedici
 * @desc 我的课程类型定义
 */
public interface TabType {

    // 我的自主学习
    int TAB_AUTONOMOUSLY = 0;
    // 我的在线学习
    int TAB_ONLINE = 1;

    @Target(ElementType.PARAMETER)
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({TAB_AUTONOMOUSLY,TAB_ONLINE})
    public @interface TabRes{

    }
}
