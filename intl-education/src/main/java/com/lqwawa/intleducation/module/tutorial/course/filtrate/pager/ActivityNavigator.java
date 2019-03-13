package com.lqwawa.intleducation.module.tutorial.course.filtrate.pager;

import android.support.annotation.Size;

/**
 * 帮辅群筛选页面的接口定义
 */
public interface ActivityNavigator {
    // 获取筛选参数
    String getLevel();
    @Size(3)
    int[] getFiltrateParams();

    String getKeyWord();
}