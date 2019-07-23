package com.lqwawa.intleducation.module.onclass.detail.join;

import com.lqwawa.intleducation.factory.data.entity.OnlineCommentEntity;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 已加入班级详情的事件回调定义
 * @date 2018/06/08 01:45
 * @history v1.0
 * **********************************
 */
public interface JoinClassDetailNavigator{
    // 更新评论片段的显示与隐藏
    void updateCommentVisibility(boolean hidden);
}
