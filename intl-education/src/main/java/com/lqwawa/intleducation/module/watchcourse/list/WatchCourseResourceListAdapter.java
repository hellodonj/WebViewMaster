package com.lqwawa.intleducation.module.watchcourse.list;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.vo.BaseVo;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.ImageUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.helper.LQCourseHelper;
import com.lqwawa.intleducation.module.discovery.vo.CourseDetailsVo;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

/**
 * @author mrmedici
 * @desc 关联学程选择资源的Adapter
 */
public class WatchCourseResourceListAdapter extends RecyclerAdapter<String> {

    @Override
    protected int getItemViewType(int position, String s) {
        return R.layout.item_watch_course_resource_list_layout;
    }

    @Override
    protected ViewHolder<String> onCreateViewHolder(View root, int viewType) {
        return new CourseHolder(root);
    }

    /**
     * 课程显示的ViewHolder
     */
    public static final class CourseHolder extends ViewHolder<String> {

        private ImageView mCourseIcon;
        private TextView mCourseName;
        private TextView mCourseTeachers;
        private TextView mCourseType;

        private final int[] courseTypesBgId = new int[]{
                R.drawable.shape_course_type_read,
                R.drawable.shape_course_type_learn,
                R.drawable.shape_course_type_practice,
                R.drawable.shape_course_type_exam,
                R.drawable.shape_course_type_video
        };

        private String[] courseTypeNames;

        public CourseHolder(View itemView) {
            super(itemView);
            mCourseIcon = (ImageView) itemView.findViewById(R.id.iv_course_icon);
            mCourseName = (TextView) itemView.findViewById(R.id.tv_course_name);
            mCourseTeachers = (TextView) itemView.findViewById(R.id.tv_course_teacher);
            mCourseType = (TextView) itemView.findViewById(R.id.tv_course_type);

            courseTypeNames =
                    itemView.getContext().getResources().getStringArray(R.array.course_type_names);
        }

        @Override
        protected void onBind(String courseId) {
            // 发生网络请求
            String token = UserHelper.getUserId();
            LQCourseHelper.requestCourseDetailByCourseId(token, courseId, null, 1, 0, 0, new DataSource.Callback<CourseDetailsVo>() {
                @Override
                public void onDataNotAvailable(int strRes) {
                    UIUtil.showToastSafe(strRes);
                }

                @Override
                public void onDataLoaded(CourseDetailsVo courseDetailsVo) {
                    if(EmptyUtil.isNotEmpty(courseDetailsVo) && EmptyUtil.isNotEmpty(courseDetailsVo.getCourse())){
                        CourseVo courseVo = courseDetailsVo.getCourse().get(0);
                        mCourseName.setText(courseVo.getName());
                        mCourseTeachers.setText(courseVo.getTeachersName());
                        mCourseTeachers.setVisibility(View.GONE);
                        String courseUrl = courseVo.getThumbnailUrl().trim();
                        ImageUtil.fillCourseIcon(mCourseIcon,courseUrl);

                        int courseType = courseVo.getAssortment();
                        if (courseType >= 0 && courseType < courseTypesBgId.length) {
                            mCourseType.setText(courseTypeNames[courseType]);
                            mCourseType.setBackgroundResource(courseTypesBgId[courseType]);
                            mCourseType.setVisibility(View.VISIBLE);
                        }
                    }
                }
            });
        }
    }
}
