package com.lqwawa.intleducation.module.learn.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ui.MyBaseAdapter;
import com.lqwawa.intleducation.module.learn.vo.ExamDetailVo;
import com.lqwawa.intleducation.module.learn.vo.ExamItemVo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XChen on 2016/11/29.
 * email:man0fchina@foxmail.com
 */

public class ExamResultGridAdapter extends MyBaseAdapter {
    private Activity activity;
    private List<ExamItemVo> list;
    private ExamDetailVo examDetailVo;
    private LayoutInflater inflater;

    public ExamResultGridAdapter(Activity activity) {
        this.activity = activity;
        this.inflater = LayoutInflater.from(activity);
        list = new ArrayList<ExamItemVo>();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        final ExamItemVo vo = list.get(position);
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = inflater.inflate(R.layout.mod_learn_exam_result_grid_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        holder.pager_index_tv.setText("" + (position + 1) + "");
        if (vo.getCexer().getExerciseType() != 4) {
            holder.result_iv.setVisibility(View.VISIBLE);
            holder.result_tv.setVisibility(View.GONE);
            if (vo.getUexer() == null || !vo.getCexer().getAnswer().equals(vo.getUexer().getAnswer())) {
                holder.result_iv.setImageDrawable(
                        activity.getResources().getDrawable(R.drawable.ic_question_wrong));
            } else {
                holder.result_iv.setImageDrawable(
                        activity.getResources().getDrawable(R.drawable.ic_question_right));
            }
        } else {
            holder.result_tv.setVisibility(View.VISIBLE);
            holder.result_iv.setVisibility(View.GONE);
            //与ios逻辑保持一致，问答题根据试卷详情的分数判断，小于0未批阅，大于等于0显示分数
            if (examDetailVo != null) {
                if (examDetailVo.getScore() >= 0) {
                    holder.result_tv.setText("" + vo.getUexer().getScore() +
                        activity.getResources().getString(R.string.points));
                    holder.result_tv.setTextColor(activity.getResources().getColor(R.color.text_black));
                } else {
                    holder.result_tv.setTextColor(activity.getResources().getColor(R.color
                         .com_text_red));
                    holder.result_tv.setText(activity.getResources().getString(R.string.not_mark));
                }
            }
//            if (vo.getUexer() == null){//答案为空
//                holder.result_tv.setTextColor(activity.getResources().getColor(R.color.com_text_red));
//                holder.result_tv.setText(activity.getResources().getString(R.string.you_are_not_answer));
//            }else if(vo.getUexer().getScore() >= 0) {
//                holder.result_tv.setText("" + vo.getUexer().getScore() +
//                        activity.getResources().getString(R.string.points));
//                if(vo.getUexer().getScore() > 0) {
//                    holder.result_tv.setTextColor(activity.getResources().getColor(R.color.text_black));
//                }else{
//                    holder.result_tv.setTextColor(activity.getResources().getColor(R.color.com_text_red));
//                }
//            }else{
//                holder.result_tv.setTextColor(activity.getResources().getColor(R.color.com_text_red));
//                holder.result_tv.setText(activity.getResources().getString(R.string.not_mark));
//            }
        }
        return convertView;
    }


    private class ViewHolder {
        TextView pager_index_tv;
        ImageView result_iv;
        TextView result_tv;

        public ViewHolder(View parentView) {
            pager_index_tv = (TextView) parentView.findViewById(R.id.pager_index_tv);
            result_iv = (ImageView) parentView.findViewById(R.id.result_iv);
            result_tv = (TextView) parentView.findViewById(R.id.result_tv);
        }
    }

    /**
     * 下拉刷新设置数据
     *
     * @param list
     */
    public void setData(List<ExamItemVo> list) {
        if (list != null) {
            this.list = new ArrayList<ExamItemVo>(list);
        } else {
            this.list.clear();
        }
    }

    /**
     * 上拉加载更多，新增数据
     *
     * @param list
     */
    public void addData(List<ExamItemVo> list) {
        this.list.addAll(list);
    }

    public void setExamDetailVo(ExamDetailVo examDetailVo) {
        this.examDetailVo = examDetailVo;
    }
}
