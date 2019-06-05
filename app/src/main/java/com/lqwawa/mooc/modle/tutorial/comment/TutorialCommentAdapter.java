package com.lqwawa.mooc.modle.tutorial.comment;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.R;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.ImageUtil;
import com.lqwawa.intleducation.common.utils.StringUtil;
import com.lqwawa.intleducation.factory.data.entity.tutorial.TutorCommentEntity;

/**
 * @author mrmedici
 * @desc 帮辅评论
 */
public class TutorialCommentAdapter extends RecyclerAdapter<TutorCommentEntity> {

    private boolean isTutor;
    private TutorialCommentCallback mCallback;

    public TutorialCommentAdapter(boolean isTutor, TutorialCommentCallback mCallback) {
        super();
        this.isTutor = isTutor;
        this.mCallback = mCallback;
    }

    @Override
    protected int getItemViewType(int position, TutorCommentEntity tutorCommentEntity) {
        return R.layout.item_tutorial_comment_layout;
    }

    @Override
    protected ViewHolder<TutorCommentEntity> onCreateViewHolder(View root, int viewType) {
        return new CommentViewHolder(root);
    }

    public void setCallback(@NonNull TutorialCommentCallback callback){
        this.mCallback = callback;
    }

    private class CommentViewHolder extends ViewHolder<TutorCommentEntity>{

        private ImageView mIvAvatar;
        private TextView mTvName;
        private TextView mTvZan;
        private TextView mTvContent;
        private TextView mTvDate;
        private TextView mTvStatus;
        private RatingBar mCommentRatingBar;

        public CommentViewHolder(View itemView) {
            super(itemView);
            mIvAvatar = (ImageView) itemView.findViewById(R.id.iv_avatar);
            mTvName = (TextView) itemView.findViewById(R.id.tv_name);
            mCommentRatingBar = (RatingBar) itemView.findViewById(R.id.comment_rating_bar);
            mTvZan = (TextView) itemView.findViewById(R.id.tv_zan);
            mTvContent = (TextView) itemView.findViewById(R.id.tv_content);
            mTvDate = (TextView) itemView.findViewById(R.id.tv_date);
            mTvStatus = (TextView) itemView.findViewById(R.id.tv_status);
        }

        @Override
        protected void onBind(TutorCommentEntity entity) {
            ImageUtil.fillCircleView(mIvAvatar,entity.getHeadPicUrl());
            StringUtil.fillSafeTextView(mTvName,entity.getCreateName());
            StringUtil.fillSafeTextView(mTvZan,Integer.toString(entity.getPraiseNum()));
            StringUtil.fillSafeTextView(mTvContent,entity.getContent());
            StringUtil.fillSafeTextView(mTvDate,entity.getCreateTime());
            mCommentRatingBar.setRating(entity.getStarLevel());
            // 恢复可用
            mTvZan.setEnabled(true);
            mTvStatus.setEnabled(true);

            if(isTutor) {
                mTvStatus.setVisibility(View.VISIBLE);
                if (entity.isShowing()) {
                    mTvStatus.setText(R.string.label_hide);
                    mTvStatus.setActivated(true);
                } else {
                    mTvStatus.setText(R.string.label_showing);
                    mTvStatus.setActivated(false);
                }
            }else{
                mTvStatus.setVisibility(View.GONE);
            }

            // 点赞更新
            mTvZan.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    if(EmptyUtil.isNotEmpty(mCallback)){
                        int position = getAdapterPosition();
                        mCallback.onZanChanged(position,entity);
                        // 点击之后设置不可用
                        mTvZan.setEnabled(false);
                    }
                }
            });

            // 显示与隐藏
            // 当前是否是帮辅老师
            if(isTutor){
                mTvStatus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(EmptyUtil.isNotEmpty(mCallback)){
                            int position = getAdapterPosition();
                            mCallback.onStatusChanged(position,entity);
                            // 点击之后设置不可用
                            mTvStatus.setEnabled(false);
                        }
                    }
                });
            }
        }
    }

    public interface TutorialCommentCallback{
        void onStatusChanged(int position, @NonNull TutorCommentEntity entity);
        void onZanChanged(int position, @NonNull TutorCommentEntity entity);
    }
}
