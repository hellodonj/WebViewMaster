package com.lqwawa.intleducation.module.discovery.ui.lqcourse.home;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.common.utils.ImageUtil;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;

import org.xutils.x;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 课程Adapter
 * @date 2018/04/27 18:13
 * @history v1.0
 * **********************************
 */
public class CourseAdapter extends RecyclerAdapter<CourseVo> {

    @Override
    protected int getItemViewType(int position, CourseVo courseVo) {
        return R.layout.item_course_layout;
    }

    @Override
    protected ViewHolder<CourseVo> onCreateViewHolder(View root, int viewType) {
        return new CourseHolder(root);
    }

    /**
     * 课程显示的ViewHolder
     */
    public static final class CourseHolder extends ViewHolder<CourseVo> {

        private ImageView mCourseIcon;
        private TextView mCourseName,mCourseTeachers;

        public CourseHolder(View itemView) {
            super(itemView);
            mCourseIcon = (ImageView) itemView.findViewById(R.id.iv_course_icon);
            mCourseName = (TextView) itemView.findViewById(R.id.tv_course_name);
            mCourseTeachers = (TextView) itemView.findViewById(R.id.tv_course_teacher);
        }

        @Override
        protected void onBind(CourseVo courseVo) {
            mCourseName.setText(courseVo.getName());
            mCourseTeachers.setText(courseVo.getTeachersName());
            String courseUrl = courseVo.getThumbnailUrl().trim();
            ImageUtil.fillCourseIcon(mCourseIcon,courseUrl);
        }
    }
}
