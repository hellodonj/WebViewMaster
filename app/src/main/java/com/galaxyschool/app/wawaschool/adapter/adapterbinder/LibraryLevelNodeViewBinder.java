package com.galaxyschool.app.wawaschool.adapter.adapterbinder;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.galaxyschool.app.wawaschool.R;
import com.lqwawa.intleducation.common.ui.treeview.TreeNode;
import com.lqwawa.intleducation.common.ui.treeview.base.CheckableNodeViewBinder;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;
import com.lqwawa.intleducation.factory.data.entity.LibraryLabelEntity;
import com.lqwawa.intleducation.module.organcourse.OrganLibraryType;

import java.util.List;

public class LibraryLevelNodeViewBinder extends CheckableNodeViewBinder {

    private ImageView arrowRight;
    private TextView name, subTitle;
    private ImageView thumbnail;
    private String TAG = getClass().getSimpleName();

    public LibraryLevelNodeViewBinder(View itemView) {
        super(itemView);
        name = (TextView) itemView.findViewById(R.id.name);
        thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
        subTitle = (TextView) itemView.findViewById(R.id.sub_title);
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
        name.setText(entity.getName());
        arrowRight.setRotation(entity.isDirectAccessNextPage() ? 180 : -90);
        //false是无法展开，然后直接进入下一个页面
        List<LQCourseConfigEntity> entityList = entity.getList();
        if (entityList != null && entityList.size() == 1) {
            entity.setAuthorized(entityList.get(0).isAuthorized());//如果一级item只有一个子item，那么他的权限就是这个子item的权限
        }
        if (entity.getType() == OrganLibraryType.TYPE_SCHOOL_LIBRARY
                || entity.getType() == OrganLibraryType.TPYE_CHOICE_LIBRARY
                || entity.getType() == OrganLibraryType.TYPE_CLASS_COURSE
                || entity.getType() == OrganLibraryType.TYPE_CONNECT_COURSE
                || entity.getType() == OrganLibraryType.TYPE_ONLINE_COMMON_LIBRARY){
            subTitle.setVisibility(View.GONE);
            Glide.with(context).load(entity.getDrawableId()).into(thumbnail);
        } else {
            Glide.with(context).load(entity.getThumbnail()).into(thumbnail);
            subTitle.setVisibility(entity.isDirectAccessNextPage() ? View.GONE : View.VISIBLE);
            subTitle.setText(entity.isAuthorized() ? context.getString(R.string.label_be_authorized_container) :
                    context.getString(R.string.label_unauthorized_container));
            subTitle.setTextColor(entity.isAuthorized() ? UIUtil.getColor(R.color.textBlue) : UIUtil.getColor(R.color.textSecond));
        }
    }

    //item的点击事件
    @Override
    public void onNodeToggled(int position, TreeNode treeNode, boolean expand, Context context) {
        LQCourseConfigEntity entity = (LQCourseConfigEntity) treeNode.getValue();
        //true是可展开，一级item不响应跳转
        if (entity.isDirectAccessNextPage()) {
            arrowRight.setRotation(expand ? 180 : 0);
            return;
        }
        //
        int type = entity.getType();
        if (type == OrganLibraryType.TYPE_LQCOURSE_SHOP) {
            //课本馆

        } else if (type == OrganLibraryType.TYPE_PRACTICE_LIBRARY) {
            //练测馆

        } else if (type == OrganLibraryType.TYPE_LIBRARY) {
            //图书馆

        } else if (type == OrganLibraryType.TYPE_VIDEO_LIBRARY) {
            //视频馆

        } else if (type == OrganLibraryType.TYPE_BRAIN_LIBRARY) {
            //全脑馆

        } else if (type == OrganLibraryType.TYPE_TEACHING_PLAN) {
            //三习教案馆

        }
    }
}
