package com.lqwawa.intleducation.module.discovery.ui.coursedetail;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.common.Common;
import com.lqwawa.intleducation.common.utils.StringUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.entity.PayChapterEntity;
import com.lqwawa.intleducation.wxapi.WXPayEntryActivity;

/**
 * @author medici
 * @desc
 */
public class PayChapterAdapter extends RecyclerAdapter<PayChapterEntity>{
    // 如果按章购买,有值
    private int mChapterId;

    public PayChapterAdapter(int mChapterId) {
        this.mChapterId = mChapterId;
    }

    @Override
    protected int getItemViewType(int position, PayChapterEntity payChapterEntity) {
        return R.layout.item_chapter_pay_layout;
    }

    @Override
    protected RecyclerAdapter.ViewHolder<PayChapterEntity> onCreateViewHolder(View root, int viewType) {
        return new ViewHolder(root,mChapterId);
    }

    private static class ViewHolder extends RecyclerAdapter.ViewHolder<PayChapterEntity>{

        private CheckBox mCbSwitch;
        private TextView mTvPayed;
        private TextView mTvChapterName;
        private TextView mTvPrice;

        private int mChapterId;

        public ViewHolder(View itemView,int chapterId) {
            super(itemView);
            mCbSwitch = (CheckBox) itemView.findViewById(R.id.cb_switch);
            mTvPayed = (TextView) itemView.findViewById(R.id.tv_payed);
            mTvChapterName = (TextView) itemView.findViewById(R.id.tv_chapter_name);
            mTvPrice = (TextView) itemView.findViewById(R.id.tv_price);

            this.mChapterId = chapterId;
        }

        @Override
        protected void onBind(PayChapterEntity payChapterEntity) {
            if(payChapterEntity.isBuyed()){
                // 已经购买过
                mCbSwitch.setVisibility(View.INVISIBLE);
                mTvPayed.setVisibility(View.VISIBLE);

                mTvChapterName.setTextColor(UIUtil.getColor(R.color.textSecond));
                mTvPrice.setTextColor(UIUtil.getColor(R.color.textSecond));
            }else{
                // 没有购买过
                mCbSwitch.setVisibility(View.VISIBLE);
                mTvPayed.setVisibility(View.INVISIBLE);

                mTvChapterName.setTextColor(UIUtil.getColor(R.color.textPrimary));
                mTvPrice.setTextColor(UIUtil.getColor(R.color.textMoneyRed));

                if(payChapterEntity.isHighlight() || mChapterId == payChapterEntity.getId()){
                    mTvChapterName.setTextColor(UIUtil.getColor(R.color.textAccent));
                }else{
                    mTvChapterName.setTextColor(UIUtil.getColor(R.color.textPrimary));
                }
            }

            mCbSwitch.setChecked(!payChapterEntity.isBuyed() && payChapterEntity.isSelect());

            StringUtil.fillSafeTextView(mTvChapterName,payChapterEntity.getName());
            StringUtil.fillSafeTextView(mTvPrice, Common.Constance.MOOC_MONEY_MARK.concat(Long.toString(payChapterEntity.getPrice())));
        }
    }
}
