package com.galaxyschool.app.wawaschool.adapter.adapterbinder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.galaxyschool.app.wawaschool.R;
import com.lqwawa.intleducation.common.ui.treeview.TreeNode;
import com.lqwawa.intleducation.common.ui.treeview.base.CheckableNodeViewBinder;
import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;
import com.lqwawa.intleducation.factory.data.entity.LibraryLabelEntity;
import com.lqwawa.intleducation.module.organcourse.OrganLibraryType;

public class LibraryLevelNodeViewBinder extends CheckableNodeViewBinder {

    private ImageView arrowRight;
    private TextView name;
    private ImageView thumbnail;

    public LibraryLevelNodeViewBinder(View itemView) {
        super(itemView);
        name = (TextView) itemView.findViewById(R.id.name);
        thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
        arrowRight = (ImageView) itemView.findViewById(R.id.arrow_right);
    }

    @Override
    public int getLayoutId() {
        return R.layout.library_first_level_item;
    }

    @Override
    public int getCheckableViewId() {
        return R.id.check_box;
    }

    @Override
    public int getToggleTriggerViewId() {
        return R.id.root_view;
    }

    @Override
    public void bindView(TreeNode treeNode, Context context) {
        LQCourseConfigEntity entity = (LQCourseConfigEntity) treeNode.getValue();
        Glide.with(context).load(entity.getThumbnail()).into(thumbnail);
        name.setText(entity.getName());
        arrowRight.setRotation(90);
    }

    //item的点击事件
    @Override
    public void onNodeToggled(int position, TreeNode treeNode, boolean expand, Context context) {
        arrowRight.setRotation(expand ? -90 : 90);
    }
}
