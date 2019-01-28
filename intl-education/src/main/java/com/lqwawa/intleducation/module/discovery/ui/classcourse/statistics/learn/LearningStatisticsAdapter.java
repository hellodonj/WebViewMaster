package com.lqwawa.intleducation.module.discovery.ui.classcourse.statistics.learn;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.common.utils.StringUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.common.utils.image.LQwawaImageUtil;
import com.lqwawa.intleducation.factory.data.entity.course.LearningProgressEntity;

import java.text.NumberFormat;

/**
 * @author mrmedici
 * @desc 学习统计的Adapter
 */
public class LearningStatisticsAdapter extends RecyclerAdapter<LearningProgressEntity> {

    @Override
    protected int getItemViewType(int position, LearningProgressEntity learningProgressEntity) {
        return R.layout.item_course_learning_rate_layout;
    }

    @Override
    protected RecyclerAdapter.ViewHolder<LearningProgressEntity> onCreateViewHolder(View root, int viewType) {
        return new ViewHolder(root);
    }

    private static class ViewHolder extends RecyclerAdapter.ViewHolder<LearningProgressEntity>{

        private ImageView mIvAvatar;
        private TextView mTvUsername;
        private ProgressBar mPbPercent;
        private TextView mPercentage;

        public ViewHolder(View itemView) {
            super(itemView);
            mIvAvatar = (ImageView) itemView.findViewById(R.id.iv_avatar);
            mTvUsername = (TextView) itemView.findViewById(R.id.tv_username);
            mPbPercent = (ProgressBar) itemView.findViewById(R.id.pb_percent);
            mPercentage = (TextView) itemView.findViewById(R.id.tv_percentage);
        }

        @Override
        protected void onBind(LearningProgressEntity learningProgressEntity) {
            StringUtil.fillSafeTextView(mTvUsername,learningProgressEntity.getUserName());
            StringUtil.fillSafeTextView(mPercentage,learningProgressEntity.getCoursePercent());
            LQwawaImageUtil.loadCommonIcon(UIUtil.getContext(),mIvAvatar,learningProgressEntity.getThumbnail());
            NumberFormat format = NumberFormat.getInstance();
            try{
                Number number = format.parse(learningProgressEntity.getCoursePercent());
                mPbPercent.setProgress(number.intValue());
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
