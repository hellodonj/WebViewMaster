package com.galaxyschool.app.wawaschool.adapter;

import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.DensityUtils;
import com.galaxyschool.app.wawaschool.pojo.ViewBaseBean;
import com.lecloud.skin.ui.utils.ScreenUtils;
import java.util.List;

public class DataStatisticAdapter extends BaseQuickAdapter<ViewBaseBean, BaseViewHolder> {

    private OnItemClick itemClick;
    public DataStatisticAdapter(@Nullable List<ViewBaseBean> data) {
        super(R.layout.item_data_statistic, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ViewBaseBean item) {
        helper.setText(R.id.tv_title, item.getTitle());
        ImageView imageView = helper.getView(R.id.iv_image);
        imageView.setImageResource(item.getDrawableId());
        LinearLayout rootLayout = helper.getView(R.id.ll_root_layout);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) rootLayout.getLayoutParams();
        int width = ScreenUtils.getWidth(mContext) / 2 - DensityUtils.dp2px(mContext, 30);
        layoutParams.width = width;
        layoutParams.height = width;
        rootLayout.setLayoutParams(layoutParams);
        rootLayout.setOnClickListener(v -> {
            if (itemClick != null){
                itemClick.onItemClick(helper.getLayoutPosition());
            }
        });
    }


    public void setOnItemClick(OnItemClick onItemClick) {
        this.itemClick = onItemClick;
    }

    //定义一个点击事件的接口
    public interface OnItemClick {
        void onItemClick(int position);
    }
}
