package com.lqwawa.intleducation.module.discovery.adapter;

import android.content.Context;
import android.graphics.Color;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ui.BaseRecyclerAdapter;
import com.lqwawa.intleducation.base.ui.BaseViewHolder;
import com.lqwawa.intleducation.module.discovery.vo.CoinsDetailInfo;

import java.util.List;

public class VoucherDetailAdapter extends BaseRecyclerAdapter<CoinsDetailInfo> {

    public VoucherDetailAdapter(List<CoinsDetailInfo> data, Context context, int... layoutId) {
        super(data, context, layoutId);
    }

    @Override
    protected void bindData(BaseViewHolder viewHolder, int position, CoinsDetailInfo item) {
        TextView tvName = viewHolder.getView(R.id.tv_name);
        TextView tvTime = viewHolder.getView(R.id.tv_time);
        TextView tvCount = viewHolder.getView(R.id.tv_count);

        if (item.getRecordType() == 1) {
            tvCount.setTextColor(Color.parseColor("#01913a"));
            tvCount.setText("+" + item.getAmount());
            tvName.setText("奖励代金券");
        } else {
            tvCount.setTextColor(Color.parseColor("#161616"));
            tvCount.setText("-" + item.getAmount());
            tvName.setText("购买【" + item.getCourseName() + "】第一章");
        }
        tvTime.setText(item.getCreateDate());
    }
}
