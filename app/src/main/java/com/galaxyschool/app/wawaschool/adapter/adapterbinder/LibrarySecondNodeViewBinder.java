package com.galaxyschool.app.wawaschool.adapter.adapterbinder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.UIUtils;
import com.lqwawa.intleducation.common.ui.treeview.TreeNode;
import com.lqwawa.intleducation.common.ui.treeview.base.CheckableNodeViewBinder;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;
import com.lqwawa.intleducation.factory.data.entity.LibraryLabelEntity;


public class LibrarySecondNodeViewBinder extends CheckableNodeViewBinder {

    private TextView name, subTitle;
    private ImageView thumbnail,arrowRight;

    public LibrarySecondNodeViewBinder(View itemView) {
        super(itemView);
        name = (TextView) itemView.findViewById(R.id.name);
        subTitle = (TextView) itemView.findViewById(R.id.sub_title);
        thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
        arrowRight = (ImageView) itemView.findViewById(R.id.arrow_right);
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
        LQCourseConfigEntity entity = (LQCourseConfigEntity) treeNode.getValue();
        Glide.with(context).load(entity.getThumbnail()).into(thumbnail);
        name.setText(entity.getConfigValue());
        subTitle.setText(entity.isAuthorized() ? context.getString(R.string.label_be_authorized_container) :
                context.getString(R.string.label_unauthorized_container));
        subTitle.setTextColor(entity.isAuthorized() ? UIUtil.getColor(R.color.textBlue) : UIUtil.getColor(R.color.textSecond));
        arrowRight.setRotation( -90);
    }

    //item的点击事件
    @Override
    public void onNodeToggled(int position, TreeNode treeNode, boolean expand, Context context) {

    }
}
