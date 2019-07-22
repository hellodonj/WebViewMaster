package com.lqwawa.intleducation.module.discovery.ui.lqcourse.home;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.common.utils.ImageUtil;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;

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
        private TextView mCourseName;
        private TextView mCourseType;
        private TextView mCourseTeachers;

        private final int[] courseTypesBgId = new int[]{
                R.drawable.shape_course_type_read,
                R.drawable.shape_course_type_learn,
                R.drawable.shape_course_type_practice,
                R.drawable.shape_course_type_exam,
                R.drawable.shape_course_type_video,
                R.drawable.shape_course_type_lesson
        };

        private String[] courseTypeNames;

        public CourseHolder(View itemView) {
            super(itemView);

            mCourseIcon = (ImageView) itemView.findViewById(R.id.iv_course_icon);
            mCourseType = (TextView) itemView.findViewById(R.id.tv_course_type);
            mCourseName = (TextView) itemView.findViewById(R.id.tv_course_name);
            mCourseTeachers = (TextView) itemView.findViewById(R.id.tv_course_teacher);

            courseTypeNames =
                    itemView.getContext().getResources().getStringArray(R.array.course_type_names);

        }

        @Override
        protected void onBind(CourseVo courseVo) {
            mCourseName.setText(courseVo.getName());
            mCourseTeachers.setText(courseVo.getTeachersName());
            String courseUrl = courseVo.getThumbnailUrl().trim();
            ImageUtil.fillCourseIcon(mCourseIcon, courseUrl);

            int courseType = courseVo.getAssortment();
            if (courseType >= 0 && courseType < courseTypesBgId.length) {
                mCourseType.setText(courseTypeNames[courseType]);
                mCourseType.setBackgroundResource(courseTypesBgId[courseType]);
            }
        }
    }
}
