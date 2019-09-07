package com.galaxyschool.app.wawaschool.adapter;


import android.app.Activity;
import android.support.annotation.Nullable;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.pojo.StatisticBean;

import java.util.List;

public class StudentMemberListAdapter extends BaseQuickAdapter<StatisticBean, BaseViewHolder> {

    private OnItemClick onItemClick;
    private boolean fromClassStatistic;
    public StudentMemberListAdapter(@Nullable List<StatisticBean> data,boolean fromClassStatistic) {
        super(R.layout.item_student_member_list,data);
        this.fromClassStatistic = fromClassStatistic;
    }

    @Override
    protected void convert(BaseViewHolder helper, StatisticBean item) {
        helper.setText(R.id.tv_student_name,item.getStudentName());
        MyApplication.getThumbnailManager((Activity) mContext).
                displayUserIconWithDefault(AppSettings.getFileUrl(item.getHeadPicUrl()),
                        helper.getView(R.id.iv_icon),R.drawable.default_user_icon);
        if (fromClassStatistic){
            helper.getView(R.id.iv_arrow_icon).setVisibility(View.VISIBLE);
            helper.setText(R.id.tv_num,mContext.getString(R.string.str_eval_score,
                    String.valueOf(item.getAverageScore())));
        } else {
            helper.setText(R.id.tv_num, mContext.getString(R.string.str_task_num, item.getStudentNotCompletedNum()));
        }
        helper.getView(R.id.ll_root_layout).setOnClickListener(v -> {
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
