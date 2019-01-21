package com.lqwawa.intleducation.module.discovery.ui.study;

import android.support.annotation.IntDef;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author mrmedici
 * @desc 在线学习数据定义
 */
public interface OnlineStudyType {
    // sort: 1 热门 2时间  5 价格升序6价格降序
    // 最新数据
    int SORT_LATEST = 2;
    // 热门数据
    int SORT_HOT = 1;
    // 价格升序
    int SORT_PRICE_TOP = 5;
    // 价格降序
    int SORT_PRICE_DOWN = 6;
    // 其它类型标志机构列表
    int SORT_ORGAN = 100;
    // 搜索标志
    int SORT_ONLINE_STUDY_SEARCH = 200;
    // 国家课程Tab的名师课
    int SORT_ONLINE_CLASS = 300;

    @Target(ElementType.PARAMETER)
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({SORT_LATEST,SORT_HOT,SORT_PRICE_TOP,SORT_PRICE_DOWN,SORT_ORGAN,SORT_ONLINE_STUDY_SEARCH,SORT_ONLINE_CLASS})
    public @interface OnlineStudyRes{

    }

}
