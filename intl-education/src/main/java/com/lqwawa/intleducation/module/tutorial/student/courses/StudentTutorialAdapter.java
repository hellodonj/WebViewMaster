package com.lqwawa.intleducation.module.tutorial.student.courses;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.common.utils.ImageUtil;
import com.lqwawa.intleducation.common.utils.LqServerHelper;
import com.lqwawa.intleducation.common.utils.StringUtil;
import com.lqwawa.intleducation.factory.data.entity.tutorial.TutorEntity;

/**
 * @author mrmedici
 * @desc 学生帮辅列表的Adapter
 */
public class StudentTutorialAdapter extends RecyclerAdapter<TutorEntity> {

    @Override
    protected int getItemViewType(int position, TutorEntity entity) {
        return R.layout.item_student_tutorial_layout;
    }

    @Override
    protected ViewHolder<TutorEntity> onCreateViewHolder(View root, int viewType) {
        return new StudentTutorialHolder(root);
    }

    private static class StudentTutorialHolder extends ViewHolder<TutorEntity>{

        private ImageView mIvAvatar;
        private TextView mTvContent;
        private ImageView mIvArrow;

        public StudentTutorialHolder(View itemView) {
            super(itemView);
            mIvAvatar = (ImageView) itemView.findViewById(R.id.iv_avatar);
            mTvContent = (TextView) itemView.findViewById(R.id.tv_content);
            mIvArrow = (ImageView) itemView.findViewById(R.id.iv_right_arrow);
        }

        @Override
        protected void onBind(TutorEntity entity) {
            mIvAvatar.setVisibility(View.VISIBLE);
            // 显示用户头像
            ImageUtil.fillUserAvatar(mIvAvatar,entity.getHeadPicUrl(),R.drawable.user_header_def);
            StringUtil.fillSafeTextView(mTvContent,entity.getTutorName());
        }
    }
}
