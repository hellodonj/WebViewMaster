package com.galaxyschool.app.wawaschool.adapter;


import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.pojo.StatisticBean;

import java.util.List;

public class ColorSortAdapter extends BaseQuickAdapter<StatisticBean, BaseViewHolder> {

    public ColorSortAdapter(@Nullable List<StatisticBean> data) {
        super(R.layout.item_color_sort, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, StatisticBean item) {
        helper.setText(R.id.tv_color_title, item.getTitle());
        helper.getView(R.id.tv_color).setBackgroundColor(item.getColor());
    }
}
