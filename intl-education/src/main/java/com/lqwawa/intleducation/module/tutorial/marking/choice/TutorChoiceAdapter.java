package com.lqwawa.intleducation.module.tutorial.marking.choice;

import android.graphics.Paint;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.common.Common;
import com.lqwawa.intleducation.common.utils.StringUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.common.utils.image.LQwawaImageUtil;
import com.lqwawa.intleducation.factory.data.entity.tutorial.TutorChoiceEntity;

/**
 * @author mrmedici
 * @desc 帮辅选择列表的Adapter
 */
public class TutorChoiceAdapter extends RecyclerAdapter<TutorChoiceEntity> {

    @Override
    protected int getItemViewType(int position, TutorChoiceEntity tutorChoiceEntity) {
        return R.layout.item_tutor_choice_layout;
    }

    @Override
    protected ViewHolder<TutorChoiceEntity> onCreateViewHolder(View root, int viewType) {
        return new TutorChoiceHolder(root);
    }

    private class TutorChoiceHolder extends RecyclerAdapter.ViewHolder<TutorChoiceEntity>{

        private ImageView mIvAvatar;
        private TextView mTvTutorName;
        private TextView mTvTaskCount;
        private TextView mTvPrice;
        private ImageView mIvChoice;

        public TutorChoiceHolder(View itemView) {
            super(itemView);
            mIvAvatar = (ImageView) itemView.findViewById(R.id.iv_avatar);
            mTvTutorName = (TextView) itemView.findViewById(R.id.tv_tutor_name);
            mTvTaskCount = (TextView) itemView.findViewById(R.id.tv_task_count);
            mTvPrice = (TextView) itemView.findViewById(R.id.tv_price);
            mIvChoice = (ImageView) itemView.findViewById(R.id.iv_choice);
        }

        @Override
        protected void onBind(TutorChoiceEntity entity) {
            LQwawaImageUtil.loadCommonCircular(mIvAvatar.getContext(),mIvAvatar,entity.getHeadPicUrl(),4);
            StringUtil.fillSafeTextView(mTvTutorName,entity.getTutorName());
            mTvTaskCount.setText(String.format(UIUtil.getString(R.string.label_placeholder_task_have_mark),entity.getTaskNum()));
            mTvPrice.setText(Common.Constance.MOOC_MONEY_MARK + entity.getMarkingPrice());
            mTvPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            if(entity.isChecked()){
                mIvChoice.setImageResource(R.drawable.select);
            }else{
                mIvChoice.setImageResource(R.drawable.unselect);
            }
        }
    }
}
