package com.lqwawa.intleducation.module.discovery.ui.lqcourse.home.common;

import android.support.annotation.IntDef;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 查看类型
 */
public interface ViewerType {

    int VIEWER_TYPE_TWO = 1 << 2;
    int VIEWER_TYPE_THREE = 1 << 3;
    int VIEWER_TYPE_FOUR = 1 << 4;
    int VIEWER_TYPE_FIVE = 1 << 5;
    int VIEWER_TYPE_SIX = 1 << 6;
    int VIEWER_TYPE_SEVEN = 1 << 7;
    int VIEWER_TYPE_EIGHT = 1 << 8;
    int VIEWER_TYPE_MORE = 1 << 31;

    @Target(ElementType.PARAMETER)
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({VIEWER_TYPE_TWO,
            VIEWER_TYPE_THREE,
            VIEWER_TYPE_FOUR,
            VIEWER_TYPE_FIVE,
            VIEWER_TYPE_SIX,
            VIEWER_TYPE_SEVEN,
            VIEWER_TYPE_EIGHT,
            VIEWER_TYPE_MORE})
    public @interface ViewerRes{

    }


}
