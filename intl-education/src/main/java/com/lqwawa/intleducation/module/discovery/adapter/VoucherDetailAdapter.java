package com.lqwawa.intleducation.module.discovery.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.module.discovery.vo.CoinsDetailInfo;

public class VoucherDetailAdapter extends RecyclerAdapter<CoinsDetailInfo> {

    private Context context;
    
    public VoucherDetailAdapter(Context context) {
        this.context = context;
    }

    @Override
    protected int getItemViewType(int position, CoinsDetailInfo coinsDetailInfo) {
        return R.layout.item_coins_detail;
    }

    @Override
    protected ViewHolder<CoinsDetailInfo> onCreateViewHolder(View root, int viewType) {
        return new VoucherDetailHolder(root);
    }


    private class VoucherDetailHolder extends RecyclerAdapter.ViewHolder<CoinsDetailInfo> {

        private TextView tvName;
        private TextView tvCount;
        private TextView tvTime;

        public VoucherDetailHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvCount = (TextView) itemView.findViewById(R.id.tv_time);
            tvTime = (TextView) itemView.findViewById(R.id.tv_count);
        }

        @Override
        protected void onBind(CoinsDetailInfo coinsDetailInfo) {
            if (coinsDetailInfo.getRecordType() == 1) {
                tvCount.setTextColor(Color.parseColor("#01913a"));
                tvCount.setText("+" + coinsDetailInfo.getAmount());
                tvName.setText(R.string.reward_vouchers);
            } else {
                tvCount.setTextColor(Color.parseColor("#161616"));
                tvCount.setText("-" + coinsDetailInfo.getAmount());
                tvName.setText(context.getString(R.string.n_buy_first_chapter,
                        coinsDetailInfo.getCourseName()));
            }
            tvTime.setText(coinsDetailInfo.getCreateDate());
        }
    }

}
