package com.lqwawa.intleducation.module.onclass;

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
 * @function 判断加载课程类型
 * @date 2018/04/27 16:09
 * @history v1.0
 * **********************************
 */
public interface OnlineSortType {
    // 显示在线机构在线班级热门
    String TYPE_SORT_ONLINE_CLASS_HOT_RECOMMEND = "1";
    // 显示在线机构在线班级最新
    String TYPE_SORT_ONLINE_CLASS_RECENT_UPDATE = "2";
    // 显示在线机构在线班级价格升序
    String TYPE_SORT_ONLINE_CLASS_PRICE_UP = "5";
    // 显示在线机构在线班级价格降序
    String TYPE_SORT_ONLINE_CLASS_PRICE_DOWN = "6";


    @Target(ElementType.PARAMETER)
    @Retention(RetentionPolicy.SOURCE)
    @StringDef({TYPE_SORT_ONLINE_CLASS_HOT_RECOMMEND,
            TYPE_SORT_ONLINE_CLASS_RECENT_UPDATE,
            TYPE_SORT_ONLINE_CLASS_PRICE_UP,
            TYPE_SORT_ONLINE_CLASS_PRICE_DOWN})
    public @interface OnlineSortRes{

    }

}
