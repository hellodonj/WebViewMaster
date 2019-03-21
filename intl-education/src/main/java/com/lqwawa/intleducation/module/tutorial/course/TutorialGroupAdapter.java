package com.lqwawa.intleducation.module.tutorial.course;

import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lqwawa.intleducation.MainApplication;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.utils.DisplayUtil;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.common.Common;
import com.lqwawa.intleducation.common.utils.DrawableUtil;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.StringUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.common.utils.image.LQwawaImageUtil;
import com.lqwawa.intleducation.factory.data.entity.course.TutorialGroupEntity;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

public class TutorialGroupAdapter extends RecyclerAdapter<TutorialGroupEntity> {

    private EntityCallback mCallback;

    public TutorialGroupAdapter(EntityCallback mCallback) {
        this.mCallback = mCallback;
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
        private TextView mTvTutorialFree;
        private TextView mTvPrice;

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
            mTvTutorialFree = (TextView) itemView.findViewById(R.id.tv_free);
            mTvPrice = (TextView) itemView.findViewById(R.id.tv_price);
        }

        @Override
        protected void onBind(TutorialGroupEntity entity) {
            LQwawaImageUtil.loadCommonIcon(mIvGroupAvatar.getContext(),mIvGroupAvatar,entity.getHeadPicUrl());
            StringUtil.fillSafeTextView(mTvTutorName,entity.getCreateName());
            mTvTaskCount.setText(String.format(UIUtil.getString(R.string.label_placeholder_task_have_mark),entity.getTaskNum()));
            mTvPrice.setText(Common.Constance.MOOC_MONEY_MARK + entity.getMarkingPrice());
            mTvPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);

            mTvAddTutorial.setOnClickListener(v -> {
                if(EmptyUtil.isNotEmpty(mCallback)){
                    int position = getAdapterPosition();
                    mCallback.onAddTutorial(position,entity);
                }
            });

            boolean tutorialMode = MainApplication.isTutorialMode();
            if(tutorialMode || entity.isAddedTutor() || TextUtils.equals(entity.getCreateId(),UserHelper.getUserId())){
                mTvAddTutorial.setVisibility(View.GONE);
            }else{
                mTvAddTutorial.setVisibility(View.VISIBLE);
            }
        }
    }

    public interface EntityCallback{
        void onAddTutorial(int position, @NonNull TutorialGroupEntity entity);
    }
}
