package com.lqwawa.intleducation.module.discovery.ui.lesson.sxdetail.binder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.common.ui.treeview.TreeNode;
import com.lqwawa.intleducation.common.ui.treeview.base.CheckableNodeViewBinder;
import com.lqwawa.intleducation.module.learn.vo.SectionTaskListVo;


/**
 * 描述: 三习 普通课程馆
 * 作者|时间: djj on 2019/7/25 0025 上午 11:57
 */

public class SxFirstLevelNodeViewBinder extends CheckableNodeViewBinder {

    TextView textView;
    ImageView imageView;

    public SxFirstLevelNodeViewBinder(View itemView) {
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
    public void bindView(final TreeNode treeNode, Context context) {
        SectionTaskListVo value = (SectionTaskListVo) treeNode.getValue();
        textView.setText(value.getTaskName());
    }

    @Override
    public void onNodeToggled(int position, TreeNode treeNode, boolean expand, Context context) {
        imageView.setBackgroundResource(expand ? R.drawable.arrow_up_gray_ico : R.drawable.arrow_down_gray_ico);
    }
}
