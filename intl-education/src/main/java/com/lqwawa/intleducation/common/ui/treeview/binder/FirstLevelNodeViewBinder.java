package com.lqwawa.intleducation.common.ui.treeview.binder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.common.ui.treeview.TreeNode;
import com.lqwawa.intleducation.common.ui.treeview.base.CheckableNodeViewBinder;
import com.lqwawa.intleducation.module.discovery.vo.SxExamDetailVo;


/**
 * Created by zxy on 17/4/23.
 */

public class FirstLevelNodeViewBinder extends CheckableNodeViewBinder {
    TextView textView;
    ImageView imageView;

    public FirstLevelNodeViewBinder(View itemView) {
        super(itemView);
        textView = (TextView) itemView.findViewById(R.id.task_name);
        imageView = (ImageView) itemView.findViewById(R.id.iv_arrow);
    }

    @Override
    public int getCheckableViewId() {
        return R.id.check_box;
    }

    @Override
    public int getLayoutId() {
        return R.layout.teaching_plan_first_level_item;
    }

    @Override
    public void bindView(final TreeNode treeNode) {
        SxExamDetailVo.TaskListVO value = (SxExamDetailVo.TaskListVO) treeNode.getValue();
        textView.setText(value.taskName);
    }

    @Override
    public void onNodeToggled(TreeNode treeNode, boolean expand) {
        imageView.setBackgroundResource(expand ? R.drawable.arrow_down_gray_ico : R.drawable.arrow_up_gray_ico);
    }
}
