package com.galaxyschool.app.wawaschool.adapter;


import android.app.Activity;
import android.support.annotation.Nullable;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.galaxyschool.app.wawaschool.LearningStatisticActivity;
import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.helper.StatisticNetHelper;
import com.galaxyschool.app.wawaschool.pojo.StatisticBean;
import com.galaxyschool.app.wawaschool.views.CircleImageView;
import com.galaxyschool.app.wawaschool.views.MultistageProgress;
import com.lqwawa.apps.views.DrawPointView;

import java.util.List;

public class StudentListAdapter extends BaseQuickAdapter<StatisticBean, BaseViewHolder> {

    private OnItemClick onItemClick;

    public StudentListAdapter(@Nullable List<StatisticBean> data) {
        super(R.layout.item_student_list,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, StatisticBean item) {
        helper.setText(R.id.tv_student_name,item.getStudentName());
        MyApplication.getThumbnailManager((Activity) mContext).
                displayUserIconWithDefault(AppSettings.getFileUrl(item.getHeadPicUrl()),
                        helper.getView(R.id.circle_icon),R.drawable.default_user_icon);
        MultistageProgress progress = helper.getView(R.id.tv_color);
        progress.setColors(StatisticNetHelper.getProgressColorsValue(item,false),
                StatisticNetHelper.getProgressColorsValue(item,true));
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
