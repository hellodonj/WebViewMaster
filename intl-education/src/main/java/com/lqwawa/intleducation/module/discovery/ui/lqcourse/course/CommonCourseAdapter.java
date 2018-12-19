package com.lqwawa.intleducation.module.discovery.ui.lqcourse.course;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.common.utils.ImageUtil;
import com.lqwawa.intleducation.common.utils.StringUtil;
import com.lqwawa.intleducation.common.utils.image.LQwawaImageUtil;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;

import java.util.List;

/**
 * @author mrmedici
 * @desc LQ学程公共的Adapter
 */
public class CommonCourseAdapter extends RecyclerAdapter<CourseVo>{

    public CommonCourseAdapter(List<CourseVo> courseVos) {
        super(courseVos, null);
    }

    @Override
    protected int getItemViewType(int position, CourseVo courseVo) {
        return R.layout.item_common_course_layout;
    }

    @Override
    protected ViewHolder<CourseVo> onCreateViewHolder(View root, int viewType) {
        return new CommonCourseViewHolder(root);
    }

    /**
     * @author mrmedici
     * @desc LQ学程的基本Adapter;
     */
    private static class CommonCourseViewHolder extends ViewHolder<CourseVo>{

        private ImageView mCourseIcon;
        private TextView mCourseName;

        public CommonCourseViewHolder(View itemView) {
            super(itemView);
            mCourseIcon = (ImageView) itemView.findViewById(R.id.iv_course_icon);
            mCourseName = (TextView) itemView.findViewById(R.id.tv_course_name);
        }

        @Override
        protected void onBind(CourseVo courseVo) {
            StringUtil.fillSafeTextView(mCourseName,courseVo.getName());
            // String courseUrl = courseVo.getThumbnailUrl().trim();
            LQwawaImageUtil.loadCourseThumbnailH(mCourseIcon.getContext(),mCourseIcon,courseVo.getThumbnailUrl());
            // ImageUtil.fillCourseIcon(mCourseIcon,courseUrl);
        }
    }
}
