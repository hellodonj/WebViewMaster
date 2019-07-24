package com.galaxyschool.app.wawaschool.adapter.adapterbinder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.galaxyschool.app.wawaschool.R;
import com.lqwawa.intleducation.common.ui.treeview.TreeNode;
import com.lqwawa.intleducation.common.ui.treeview.base.CheckableNodeViewBinder;
import com.lqwawa.intleducation.factory.data.entity.LibraryLabelEntity;


public class LibrarySecondNodeViewBinder extends CheckableNodeViewBinder {

    private TextView name, subTitle;
    private ImageView thumbnail;

    public LibrarySecondNodeViewBinder(View itemView) {
        super(itemView);
        name = (TextView) itemView.findViewById(R.id.name);
        subTitle = (TextView) itemView.findViewById(R.id.sub_title);
        thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
    }

    @Override
    public int getLayoutId() {
        return R.layout.authorized_lesson_item;
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
        LibraryLabelEntity entity = (LibraryLabelEntity) treeNode.getValue();
        Glide.with(context).load(entity.getThumbnail()).into(thumbnail);
        name.setText(entity.getConfigValue());
        subTitle.setText(entity.isAuthorized() ? context.getString(R.string.label_be_authorized_container) :
                context.getString(R.string.label_unauthorized_container));
    }

    //item的点击事件
    @Override
    public void onNodeToggled(int position, TreeNode treeNode, boolean expand, Context context) {

    }
}
