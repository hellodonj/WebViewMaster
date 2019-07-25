package com.lqwawa.intleducation.module.discovery.ui.lesson.sxdetail;

import com.lqwawa.intleducation.common.ui.treeview.TreeNode;
import com.lqwawa.intleducation.common.ui.treeview.TreeView;

import java.util.List;

/**
 * @author mrmedici
 * @desc 节详情Tab的功能定义
 */
public interface SxLessonSourceNavigator {

    // 触发添加到作业库的动作,开放选择功能
    void triggerChoice(boolean open);

    // 获取已经选中的作业库
    List<TreeNode> getChoiceResource();

    // 清楚所有资源选中状态
    void clearAllResourceState();

    //获取fragment的TreeView
    TreeView getTreeView();
}
