package com.lqwawa.intleducation.module.discovery.ui.classcourse.statistics.course;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lqwawa.apps.views.DrawPointView;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.common.utils.StringUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.common.utils.image.LQwawaImageUtil;
import com.lqwawa.intleducation.factory.data.entity.course.CourseStatisticsEntity;
import com.lqwawa.intleducation.factory.data.entity.course.LearningProgressEntity;

import java.text.NumberFormat;

/**
 * @author mrmedici
 * @desc 课程统计的Adapter
 */
public class CourseStatisticsAdapter extends RecyclerAdapter<CourseStatisticsEntity> {

    @Override
    protected int getItemViewType(int position, CourseStatisticsEntity learningProgressEntity) {
        return R.layout.item_course_statistics_layout;
    }

    @Override
    protected RecyclerAdapter.ViewHolder<CourseStatisticsEntity> onCreateViewHolder(View root, int viewType) {
        return new ViewHolder(root);
    }

    private static class ViewHolder extends RecyclerAdapter.ViewHolder<CourseStatisticsEntity>{

        private DrawPointView mPointView;
        private TextView mTvScoreRange;
        private TextView mTvCount;

        public ViewHolder(View itemView) {
            super(itemView);
            mPointView = (DrawPointView) itemView.findViewById(R.id.spot_view);
            mTvScoreRange = (TextView) itemView.findViewById(R.id.tv_score_range);
            mTvCount = (TextView) itemView.findViewById(R.id.tv_num);
        }

        @Override
        protected void onBind(CourseStatisticsEntity entity) {
            StringUtil.fillSafeTextView(mTvScoreRange,entity.getName());
            StringUtil.fillSafeTextView(mTvCount,String.format(UIUtil.getString(R.string.label_many_people),entity.getCount()));
            mPointView.setPointColor(entity.getColor());
        }
    }
}
