package com.lqwawa.intleducation.module.discovery.ui.lqcourse.filtrate;

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
 * <p>
 *     {@link #TYPE_SORT_HOT_RECOMMEND 热门推荐,
 *      @link #TYPE_SORT_LIVE_LIST 直播列表}
 * </p>
 * @date 2018/04/27 16:09
 * @history v1.0
 * **********************************
 */
public interface HideSortType {
    // 分类数据
    String TYPE_SORT_CLASSIFY = "-1";
    // 热门推荐
    String TYPE_SORT_HOT_RECOMMEND = "1";
    // 显示在线课堂关联课程
    String TYPE_SORT_ONLINE_COURSE = "2";
    // 显示在线机构学程馆最新
    String TYPE_SORT_ONLINE_SHOP_RECENT_UPDATE = "2";
    // 显示在线机构学程馆价格升序
    String TYPE_SORT_ONLINE_SHOP_PRICE_UP = "5";
    // 显示在线机构学程馆价格降序
    String TYPE_SORT_ONLINE_SHOP_PRICE_DOWN = "6";
    // 直播列表
    String TYPE_SORT_LIVE_LIST = "3";
    // 实体机构学程馆的搜索
    String TYPE_SORT_SCHOOL_SHOP = "1000";
    // 讲授课堂分类的搜索
    String TYPE_SORT_TEACH_ONLINE_CLASS = "1001";
    // 讲授课堂直接搜索
    String TYPE_SORT_TEACH_ONLINE_CLASS_SUPER = "1002";
    // 班级学程搜索入口
    String TYPE_SORT_CLASS_COURSE = "1003";
    // 实体机构学程馆新搜索
    String TYPE_SORT_NEW_SCHOOL_SHOP = "1004";
    // 国家课程学段搜索
    String TYPE_SORT_BASIC_GRADE = "1005";


    @Target(ElementType.PARAMETER)
    @Retention(RetentionPolicy.SOURCE)
    @StringDef({TYPE_SORT_HOT_RECOMMEND,TYPE_SORT_HOT_RECOMMEND,
            TYPE_SORT_CLASSIFY,TYPE_SORT_ONLINE_COURSE,
            TYPE_SORT_SCHOOL_SHOP,TYPE_SORT_ONLINE_SHOP_RECENT_UPDATE,
            TYPE_SORT_TEACH_ONLINE_CLASS,TYPE_SORT_TEACH_ONLINE_CLASS_SUPER,
            TYPE_SORT_CLASS_COURSE,TYPE_SORT_NEW_SCHOOL_SHOP,
            TYPE_SORT_ONLINE_SHOP_PRICE_UP,TYPE_SORT_ONLINE_SHOP_PRICE_DOWN,
            TYPE_SORT_BASIC_GRADE})
    public @interface SortRes{

    }

}
