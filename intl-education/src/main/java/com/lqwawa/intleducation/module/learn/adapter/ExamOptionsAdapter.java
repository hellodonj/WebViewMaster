package com.lqwawa.intleducation.module.learn.adapter;

import android.app.Activity;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ui.MyBaseAdapter;
import com.lqwawa.intleducation.module.learn.vo.ExamOptionsVo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveTask;

/**
 * Created by XChen on 2017/7/19.
 * email:man0fchina@foxmail.com
 */

public class ExamOptionsAdapter extends MyBaseAdapter {
    public static int ITEM_MAX_COUNT = 10;
    public static int STATUS_VIEW = 0;//查看
    public static int STATUS_EDIT = 1;//编辑
    public static int STATUS_RESULT = 2;//看结果
    private Activity activity;
    private boolean isSingleSelect = true;
    private List<ExamOptionsVo> list;
    private LayoutInflater inflater;
    private int status;

    public ExamOptionsAdapter(Activity activity, boolean isSingleSelect, int status) {
        this.activity = activity;
        this.inflater = LayoutInflater.from(activity);
        this.isSingleSelect = isSingleSelect;
        this.status = status;
        list = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return this.list.size();
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

        final ExamOptionsVo vo = list.get(position);
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = inflater.inflate(R.layout.mod_learn_exam_options_list_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        holder.itemName.setText(vo.getName());
        holder.itemContent.setText(vo.getContent());
        if (status == STATUS_EDIT) {//可以选择编辑
            holder.itemName.setBackground(vo.isSelected() ?
                    activity.getResources().getDrawable(R.drawable.exam_radio_bg_checked) :
                    activity.getResources().getDrawable(R.drawable.exam_radio_bg_normal));
            holder.itemName.setTextColor(vo.isSelected() ?
                    activity.getResources().getColor(R.color.white) :
                    activity.getResources().getColor(R.color.com_bg_sky_blue));
            holder.itemName.setTextSize(TypedValue.COMPLEX_UNIT_PX, vo.isSelected() ?
                    activity.getResources().getDimension(R.dimen.com_font_size_7) :
                    activity.getResources().getDimension(R.dimen.com_font_size_6));
        } else if(status == STATUS_RESULT){
            if(vo.isSelected() && !vo.isRightAnswer()){//错误选项
                holder.itemName.setBackground(
                        activity.getResources().getDrawable(R.drawable.exam_radio_bg_wrong));
                holder.itemName.setTextColor(
                        activity.getResources().getColor(R.color.white));
                holder.itemName.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                        activity.getResources().getDimensionPixelSize(R.dimen.com_font_size_7));
            }else if(vo.isRightAnswer()){
                if(vo.isSelected()) {
                    holder.itemName.setBackground(
                            activity.getResources().getDrawable(R.drawable.exam_radio_bg_right));
                    holder.itemName.setTextColor(
                            activity.getResources().getColor(R.color.white));
                    holder.itemName.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                            activity.getResources().getDimensionPixelSize(R.dimen.com_font_size_7));
                }else{
                    holder.itemName.setBackground(
                            activity.getResources().getDrawable(
                                    R.drawable.exam_radio_bg_right_but_not_select));
                    holder.itemName.setTextColor(
                            activity.getResources().getColor(R.color.com_text_green));
                    holder.itemName.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                            activity.getResources().getDimensionPixelSize(R.dimen.com_font_size_7));
                }
            }else{
                holder.itemName.setBackground(
                        activity.getResources().getDrawable(R.drawable.exam_radio_bg_normal));
                holder.itemName.setTextColor(
                        activity.getResources().getColor(R.color.com_bg_sky_blue));
                holder.itemName.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                        activity.getResources().getDimensionPixelSize(R.dimen.com_font_size_6));
            }
        }else{ //仅仅是查看
            holder.itemName.setBackground(
                    activity.getResources().getDrawable(R.drawable.exam_radio_bg_normal));
            holder.itemName.setTextColor(
                    activity.getResources().getColor(R.color.com_bg_sky_blue));
            holder.itemName.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    activity.getResources().getDimension(R.dimen.com_font_size_6));
        }
        return convertView;
    }

    public void setSelected(int index) {
        if(this.status != STATUS_EDIT){
            return;
        }
        if (isSingleSelect) {
            for(int i = 0; i < list.size(); i++){
                list.get(i).setSelected(index == i);
            }
        } else {
            list.get(index).setSelected(!list.get(index).isSelected());
        }
        notifyDataSetChanged();
    }

    public boolean isSelected(int index) {
        return list.get(index).isSelected();
    }

    protected class ViewHolder {
        private TextView itemName;
        private TextView itemContent;

        public ViewHolder(View view) {
            itemName = (TextView) view.findViewById(R.id.item_name_tv);
            itemContent = (TextView) view.findViewById(R.id.item_content_tv);
        }
    }

    public void setData(List<ExamOptionsVo> list) {
        this.list = new ArrayList<>(list);
    }

    public void addData(ExamOptionsVo option) {
        this.list.add(option);
    }

}
