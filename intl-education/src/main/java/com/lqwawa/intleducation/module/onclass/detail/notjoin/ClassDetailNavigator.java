package com.lqwawa.intleducation.module.onclass.detail.notjoin;


import com.lqwawa.intleducation.factory.data.entity.OnlineCommentEntity;

/**
 * **********************************
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 班级未加入详情页回调接口的定义
 * @date 2018/06/01 17:52
 * @history v1.0
 * **********************************
 */
public interface ClassDetailNavigator {
    // 回调评论片段的显示与隐藏
    void onCommentChanged(boolean hidden);
}
