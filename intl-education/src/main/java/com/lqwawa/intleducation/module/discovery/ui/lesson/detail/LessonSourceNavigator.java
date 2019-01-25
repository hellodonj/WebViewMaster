package com.lqwawa.intleducation.module.discovery.ui.lesson.detail;

import com.lqwawa.intleducation.module.learn.vo.SectionResListVo;

import java.util.List;

/**
 * @author mrmedici
 * @desc 节详情Tab的功能定义
 */
public interface LessonSourceNavigator {

    // 触发添加到作业库的动作,开放选择功能
    void triggerChoice(boolean open);

    // 获取已经选中的作业库
    List<SectionResListVo> takeChoiceResource();

    // 清楚所有资源选中状态
    void clearAllResourceState();

}
