package com.galaxyschool.app.wawaschool.adapter;


import android.support.annotation.Nullable;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.galaxyschool.app.wawaschool.LearningStatisticActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.pojo.StatisticBean;
import com.lqwawa.apps.views.DrawPointView;

import java.util.List;

public class StatisticDetailAdapter extends BaseQuickAdapter<StatisticBean, BaseViewHolder> {

    private OnItemClick onItemClick;

    public StatisticDetailAdapter(@Nullable List<StatisticBean> data) {
        super(R.layout.item_learning_statistics,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, StatisticBean item) {
        helper.setText(R.id.tv_score_range,item.getTitle());
        if (item.getStatisticType() == LearningStatisticActivity.STATISTIC_TYPE.COURSE_TYPE){
            helper.getView(R.id.iv_arrow_icon).setVisibility(item.isShowRightArrow() ?
                    View.VISIBLE : View.INVISIBLE);
            helper.setText(R.id.tv_num,mContext.getString(R.string.str_task_num,item.getNumber()));
        } else if (item.getStatisticType() == LearningStatisticActivity.STATISTIC_TYPE.CLASS_STATISTIC_TYPE){
            helper.getView(R.id.iv_arrow_icon).setVisibility(View.VISIBLE);
            helper.setText(R.id.tv_num,item.getNumber() + mContext.getString(R.string.str_people));
        } else {
            helper.setText(R.id.tv_num,mContext.getString(R.string.str_task_num,item.getNumber()));
        }
        DrawPointView pointView = helper.getView(R.id.spot_view);
        pointView.setPointColor(item.getColor());
        helper.getView(R.id.ll_statistic_layout).setOnClickListener(v -> {
            if (onItemClick != null){
                onItemClick.onItemClick(helper.getLayoutPosition());
            }
        });
    }

    public void setOnItemClick(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    //定义一个点击事件的接口
    public interface OnItemClick {
        void onItemClick(int position);
    }
}
