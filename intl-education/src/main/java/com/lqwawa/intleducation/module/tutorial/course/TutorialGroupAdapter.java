package com.lqwawa.intleducation.module.tutorial.course;

import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.lqwawa.intleducation.MainApplication;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.utils.ButtonUtils;
import com.lqwawa.intleducation.base.utils.DisplayUtil;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.common.Common;
import com.lqwawa.intleducation.common.utils.DrawableUtil;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.SizeUtil;
import com.lqwawa.intleducation.common.utils.StringUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.common.utils.image.LQwawaImageUtil;
import com.lqwawa.intleducation.factory.data.entity.course.TutorialGroupEntity;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

public class TutorialGroupAdapter extends RecyclerAdapter<TutorialGroupEntity> {

    private EntityCallback mCallback;
    private boolean isClassTutor;

    public TutorialGroupAdapter(EntityCallback mCallback) {
        this.mCallback = mCallback;
    }

    public void setIsClassTutor(boolean isClassTutor) {
       this.isClassTutor = isClassTutor;
    }

    @Override
    protected int getItemViewType(int position, TutorialGroupEntity tutorialGroupEntity) {
        return R.layout.item_tutorial_group_layout;
    }

    @Override
    protected ViewHolder<TutorialGroupEntity> onCreateViewHolder(View root, int viewType) {
        return new GroupHolder(root);
    }

    public void setCallback(@NonNull EntityCallback callback){
        this.mCallback = callback;
    }

    private class GroupHolder extends RecyclerAdapter.ViewHolder<TutorialGroupEntity>{

        private ImageView mIvGroupAvatar;
        private TextView mTvAddTutorial;
        private TextView mTvTutorName;
        private TextView mTvTaskCount;
        //private TextView mTvTutorialFree;
        private TextView mTvPrice;
        private RatingBar mRatingBar;

        public GroupHolder(View itemView) {
            super(itemView);
            mIvGroupAvatar = (ImageView) itemView.findViewById(R.id.iv_group_avatar);
            mTvAddTutorial = (TextView) itemView.findViewById(R.id.tv_add_tutorial);
            mTvAddTutorial.setBackground(DrawableUtil.createDrawable(
                    UIUtil.getColor(R.color.colorAccentAlpha),
                    UIUtil.getColor(R.color.colorAccentAlpha),
                    DisplayUtil.dip2px(UIUtil.getContext(),12)));

            mTvTutorName = (TextView) itemView.findViewById(R.id.tv_tutor_name);
            mTvTaskCount = (TextView) itemView.findViewById(R.id.tv_task_count);
            //mTvTutorialFree = (TextView) itemView.findViewById(R.id.tv_free);
            mTvPrice = (TextView) itemView.findViewById(R.id.tv_price);
            mRatingBar = (RatingBar) itemView.findViewById(R.id.group_rating_bar);
        }

        @Override
        protected void onBind(TutorialGroupEntity entity) {
            GradientDrawable drawable = DrawableUtil.createDrawable(UIUtil.getColor(R.color.colorLight), UIUtil.getColor(R.color.colorAccent), SizeUtil.dp2px(1),SizeUtil.dp2px(4));
            mIvGroupAvatar.setBackground(drawable);

            LQwawaImageUtil.loadCommonIcon(mIvGroupAvatar.getContext(),mIvGroupAvatar,entity.getHeadPicUrl());
            if(EmptyUtil.isNotEmpty(entity.getCreateName())){
                String tutorName = String.format(UIUtil.getString(R.string.label_placeholder_tutorial_group_teacher),entity.getCreateName());
                StringUtil.fillSafeTextView(mTvTutorName,tutorName);
            }else{
                StringUtil.fillSafeTextView(mTvTutorName,entity.getCreateName());
            }
            mTvTaskCount.setText(String.format(UIUtil.getString(R.string.label_placeholder_task_have_mark),entity.getTaskNum()));
            mTvPrice.setText(Common.Constance.MOOC_MONEY_MARK + entity.getMarkingPrice());
            //mTvPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            
            mTvAddTutorial.setText(!isClassTutor ? R.string.label_add_tutorial : R.string.add_class_tutor);

            mTvAddTutorial.setOnClickListener(v -> {
                if (ButtonUtils.isFastDoubleClick()) {
                    return;
                }
                if(EmptyUtil.isNotEmpty(mCallback)){
                    int position = getAdapterPosition();
                    mCallback.onAddTutorial(position,entity);
                }
            });

            boolean tutorialMode = MainApplication.isTutorialMode();
            boolean isHide =
                    tutorialMode || entity.isAddedTutor() || TextUtils.equals(entity.getCreateId(),UserHelper.getUserId());
            if (isClassTutor) {
                isHide = entity.isAddedTutor();
            }
            if(isHide){
                mTvAddTutorial.setVisibility(View.GONE);
            }else{
                mTvAddTutorial.setVisibility(View.VISIBLE);
            }
            //帮辅群界面老师添加星级评价
            mRatingBar.setRating(entity.getStarLevel());
        }
    }

    public interface EntityCallback{
        void onAddTutorial(int position, @NonNull TutorialGroupEntity entity);
    }
}
