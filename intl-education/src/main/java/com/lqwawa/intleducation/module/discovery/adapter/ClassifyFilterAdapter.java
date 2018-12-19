package com.lqwawa.intleducation.module.discovery.adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.module.discovery.vo.ClassifyVo;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by XChen on 2017/3/24.
 * email:man0fchina@foxmail.com
 */

public class ClassifyFilterAdapter extends BaseAdapter {
    private List<ClassifyVo> list;
    Activity parentActivity;
    private int selectIndex = 0;
    private OnSelectItemChangeListener onSelectItemChangeListener;

    public ClassifyFilterAdapter(Activity activity, OnSelectItemChangeListener listener) {
        list = new ArrayList<ClassifyVo>();
        this.onSelectItemChangeListener = listener;
        parentActivity = activity;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    public ClassifyVo getSelect(){
        if (list != null && selectIndex >= 0 && selectIndex < list.size()) {
            return list.get(selectIndex);
        }else{
            return null;
        }
    }

    public String setSelectLevel(String level) {
        if (this.list != null) {
            selectIndex = 0;
            for (int i = 0; i < this.list.size(); i++) {
                if (this.list.get(i).getLevel().equals(level)) {
                    this.selectIndex = i;
                    notifyDataSetChanged();
                    return level;
                }
            }
            notifyDataSetChanged();
        }
        return "";
    }

    public String setSelectLevel(String level, int labelId) {
        if (this.list != null) {
            selectIndex = 0;
            for (int i = 0; i < this.list.size(); i++) {
                if ((this.list.get(i).getLevel().equals(level) && !level.isEmpty())
                        || this.list.get(i).getLabelId() == labelId) {
                    this.selectIndex = i;
                    notifyDataSetChanged();
                    return level;
                }
            }
        }
        return "";
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ClassifyVo vo = list.get(position);
        if (convertView == null) {
            convertView = parentActivity.getLayoutInflater()
                    .inflate(R.layout.mod_common_filter_text, null, false);
        }
        TextView textViewItem = (TextView) convertView.findViewById(R.id.item_tv);
        textViewItem.setMaxLines(1);
        textViewItem.setEllipsize(TextUtils.TruncateAt.END);
        if (position == selectIndex) {
            textViewItem.setTextColor(parentActivity.getResources().getColor(R.color.com_text_white));
            textViewItem.setBackgroundResource(R.drawable.com_green_radio_bt_normal);
        } else {
            textViewItem.setTextColor(parentActivity.getResources().getColor(R.color.com_text_dark_gray));
            textViewItem.setBackgroundResource(R.drawable.com_white_radio_bt_normal);
        }
        textViewItem.setText(vo.getConfigValue());
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position != selectIndex) {
                    selectIndex = position;
                    notifyDataSetChanged();
                    if (onSelectItemChangeListener != null){
                        onSelectItemChangeListener.OnSelectItemChanged(vo);
                    }
                }
            }
        });
        return convertView;
    }

    public void setData(List<ClassifyVo> list) {
        if (list != null) {
            this.list = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                addData(list.get(i));
            }
        } else {
            this.list.clear();
        }
    }

    public void setData(List<ClassifyVo> list, int parentId) {
        if (list != null) {
            this.list = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getLevel().isEmpty()
                        || list.get(i).getParentId() == parentId) {
                    addData(list.get(i));
                }
            }
        } else {
            this.list.clear();
        }
    }

    public void addData(ClassifyVo classifyVo) {
        if (this.list == null) {
            this.list = new ArrayList<>();
        }
        if (classifyVo != null) {
            if (classifyVo.getLabelId() != 0) {
                for (ClassifyVo vo : this.list) {
                    if (vo.getLabelId() == classifyVo.getLabelId()) {
                        return;
                    }
                }
            }
            this.list.add(classifyVo);
        }
    }

    public interface OnSelectItemChangeListener{
        void OnSelectItemChanged(Object obj);
    }

    public List<ClassifyVo> getList(){
        return this.list;
    }
}