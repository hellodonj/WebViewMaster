package com.lqwawa.intleducation.module.discovery.ui.lqcourse.home;

import android.view.View;
import android.widget.ImageView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.common.utils.ImageUtil;
import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;

import java.util.List;

/**
 * @author medici
 * @desc LQ学程Holder显示的Adapter
 */
public class CourseHolderAdapter extends RecyclerAdapter<LQCourseConfigEntity>{

    public CourseHolderAdapter() {
    }

    public CourseHolderAdapter(List<LQCourseConfigEntity> entities, AdapterListener<LQCourseConfigEntity> listener) {
        super(entities, listener);
    }

    @Override
    protected int getItemViewType(int position, LQCourseConfigEntity entity) {
        return R.layout.item_course_holder_layout;
    }

    @Override
    protected RecyclerAdapter.ViewHolder<LQCourseConfigEntity> onCreateViewHolder(View root, int viewType) {
        return new ViewHolder(root);
    }

    private static class ViewHolder extends RecyclerAdapter.ViewHolder<LQCourseConfigEntity>{

        private ImageView mIvCourse;

        public ViewHolder(View itemView) {
            super(itemView);
            mIvCourse = (ImageView) itemView.findViewById(R.id.iv_course);
        }

        @Override
        protected void onBind(LQCourseConfigEntity entity) {
            ImageUtil.fillDefaultView(mIvCourse,entity.getThumbnail());
        }
    }
}
