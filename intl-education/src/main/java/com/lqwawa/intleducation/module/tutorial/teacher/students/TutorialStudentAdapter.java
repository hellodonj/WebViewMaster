package com.lqwawa.intleducation.module.tutorial.teacher.students;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.ImageUtil;
import com.lqwawa.intleducation.common.utils.LqServerHelper;
import com.lqwawa.intleducation.common.utils.StringUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.entity.tutorial.AssistStudentEntity;
import com.lqwawa.intleducation.factory.data.entity.tutorial.TutorEntity;

/**
 * @author mrmedici
 * @desc 学生帮辅列表的Adapter
 */
public class TutorialStudentAdapter extends RecyclerAdapter<AssistStudentEntity> {

    @Override
    protected int getItemViewType(int position, AssistStudentEntity entity) {
        return R.layout.item_tutorial_student_layout;
    }

    @Override
    protected ViewHolder<AssistStudentEntity> onCreateViewHolder(View root, int viewType) {
        return new StudentTutorialHolder(root);
    }

    private static class StudentTutorialHolder extends ViewHolder<AssistStudentEntity>{

        private ImageView mIvAvatar;
        private TextView mTvContent;
        private TextView mTvNotMark;
        private ImageView mIvArrow;

        public StudentTutorialHolder(View itemView) {
            super(itemView);
            mIvAvatar = (ImageView) itemView.findViewById(R.id.iv_avatar);
            mTvContent = (TextView) itemView.findViewById(R.id.tv_content);
            mTvNotMark = (TextView) itemView.findViewById(R.id.tv_not_mark_count);
            mIvArrow = (ImageView) itemView.findViewById(R.id.iv_right_arrow);
        }

        @Override
        protected void onBind(AssistStudentEntity entity) {
            mIvAvatar.setVisibility(View.VISIBLE);
            // 显示用户头像
            String studentUrl = LqServerHelper.getFullImgUrl(entity.getStuHeadPicUrl() + "").trim();
            ImageUtil.fillUserAvatar(mIvAvatar,studentUrl,R.drawable.user_header_def);

            if(EmptyUtil.isNotEmpty(entity.getStuRealName())) {
                StringUtil.fillSafeTextView(mTvContent, entity.getStuRealName());
            }else{
                StringUtil.fillSafeTextView(mTvContent, entity.getStuNickName());
            }

            int taskCount = entity.getTotalTaskNum() - entity.getReviewedTaskNum();
            if(taskCount == 0){
                mTvNotMark.setVisibility(View.GONE);
            }else{
                mTvNotMark.setVisibility(View.VISIBLE);
                String counter = String.format(UIUtil.getString(R.string.label_tutorial_student_review_count),taskCount);
                StringUtil.fillSafeTextView(mTvNotMark,counter);
            }
        }
    }
}
