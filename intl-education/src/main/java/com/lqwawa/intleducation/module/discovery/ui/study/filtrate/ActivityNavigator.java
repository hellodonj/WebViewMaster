package com.lqwawa.intleducation.module.discovery.ui.study.filtrate;

/**
 * 将授课堂类型筛选页面的接口定义
 */
public interface ActivityNavigator {
    // 获取筛选参数
    int[] getFiltrateParams();

    String getKeyWord();
}
