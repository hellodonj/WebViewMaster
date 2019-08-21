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
import com.lqwawa.intleducation.module.organcourse.OrganLibraryType;
import com.lqwawa.intleducation.module.organcourse.filtrate.OrganCourseFiltrateActivity;


public class LibrarySecondNodeViewBinder extends CheckableNodeViewBinder {

    private TextView name, subTitle;
    private ImageView thumbnail, arrowRight;
    private View leftLine;

    public LibrarySecondNodeViewBinder(View itemView) {
        super(itemView);
        name = (TextView) itemView.findViewById(R.id.name);
        subTitle = (TextView) itemView.findViewById(R.id.sub_title);
        thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
        arrowRight = (ImageView) itemView.findViewById(R.id.arrow_right);
        leftLine = itemView.findViewById(R.id.left_line);
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
        arrowRight.setRotation(-90);
        int size = treeNode.getParent().getChildren().size();
        if (treeNode.getIndex() == size ) leftLine.setVisibility(View.GONE);
    }

    //item的点击事件
    @Override
    public void onNodeToggled(int position, TreeNode treeNode, boolean expand, Context context) {
        LQCourseConfigEntity entity = (LQCourseConfigEntity) treeNode.getValue();
        int id = entity.getId();
        int parentId = entity.getParentId();
        //parentId在 OrganLibraryType类中 里有注释
        if (id == OrganCourseFiltrateActivity.MINORITY_LANGUAGE_COURSE) {
            //configValue=小语种课程

        } else if (id == OrganCourseFiltrateActivity.ENGLISH_INTERNATIONAL_COURSE) {
            //configValue=国际英语

        } else if (id == OrganCourseFiltrateActivity.CHARACTERISTICS_ENGLISH) {
            //configValue=英语学科

        } else if (id == OrganCourseFiltrateActivity.BASIC_COURSE) {
            //configValue=国家课程

        } else if (id == OrganCourseFiltrateActivity.CLASSIFIED_READING_ID) {
            //configValue=分级阅读

        } else if (id == OrganCourseFiltrateActivity.PICTURE_BOOK_ID) {
            //configValue=绘本

        } else if (id == OrganCourseFiltrateActivity.Q_DUBBING_ID) {
            //configValue=Q配音

        } else if (id == OrganCourseFiltrateActivity.RA_BRAIN_ID) {
            //configValue=右脑潜能开发

        }
    }
}
