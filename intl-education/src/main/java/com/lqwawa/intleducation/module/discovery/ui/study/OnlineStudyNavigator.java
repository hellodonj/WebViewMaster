package com.lqwawa.intleducation.module.discovery.ui.study;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.factory.data.entity.OnlineClassEntity;
import com.lqwawa.intleducation.factory.data.entity.online.OnlineStudyOrganEntity;

/**
 * @author mrmedici
 * @desc 在线学习事件回调定义
 */
public interface OnlineStudyNavigator {
    // 点击标题
    void onClickTitleLayout(@NonNull @OnlineStudyType.OnlineStudyRes int sort);
    // 点击班级
    void onClickClass(@NonNull OnlineClassEntity entity);
    // 点击的机构
    void onClickOrgan(@NonNull OnlineStudyOrganEntity entity);
}
