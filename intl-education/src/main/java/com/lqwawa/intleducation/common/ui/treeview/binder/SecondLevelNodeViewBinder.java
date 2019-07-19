package com.lqwawa.intleducation.common.ui.treeview.binder;

import android.util.SparseArray;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.utils.ResIconUtils;
import com.lqwawa.intleducation.common.ui.treeview.TreeNode;
import com.lqwawa.intleducation.common.ui.treeview.base.CheckableNodeViewBinder;
import com.lqwawa.intleducation.module.discovery.vo.SxExamDetailVo;
import com.lqwawa.intleducation.module.learn.vo.SectionResListVo;


/**
 * Created by zxy on 17/4/23.
 */

public class SecondLevelNodeViewBinder extends CheckableNodeViewBinder {

    TextView resName;
    ImageView resIcon;
    private SparseArray<ResIconUtils.ResIcon> resIconSparseArray = ResIconUtils.resIconSparseArray;
    private boolean needFlagRead;
    private int lessonStatus;

    public SecondLevelNodeViewBinder(View itemView) {
        super(itemView);
        resName = (TextView) itemView.findViewById(R.id.tv_res_name);
        resIcon = (ImageView) itemView.findViewById(R.id.iv_res_icon);
    }

    @Override
    public int getCheckableViewId() {
        return R.id.checkbox;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_course_lesson_detail_source_layout;
    }

    @Override
    public void bindView(final TreeNode treeNode) {
        SxExamDetailVo.TaskListVO.DetailVo detailVo = (SxExamDetailVo.TaskListVO.DetailVo) treeNode.getValue();
        setResIcon(resIcon,detailVo,detailVo.resType);
    }

    @Override
    public void onNodeToggled(TreeNode treeNode, boolean expand) {

    }

    private void setResIcon(ImageView imageView,  SxExamDetailVo.TaskListVO.DetailVo vo, int resType) {
        if (vo.isShield) {
            imageView.setImageResource(resIconSparseArray.get(resType).shieldResId);
        } else {
            if (needFlagRead ) {
                imageView.setImageResource(resIconSparseArray.get(resType).readResId);
            } else if (needFlagRead  && lessonStatus == 1 && (resType != 24
                    || resType != 25)) {
                // txt word 不设置
                imageView.setImageResource(resIconSparseArray.get(resType).newResId);
            } else {
                imageView.setImageResource(resIconSparseArray.get(resType).resId);
            }
        }
    }
}
