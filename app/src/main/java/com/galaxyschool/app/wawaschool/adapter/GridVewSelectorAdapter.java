package com.galaxyschool.app.wawaschool.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.fragment.library.SelectorHelper;
import com.galaxyschool.app.wawaschool.pojo.StudentMemberInfo;

import java.util.List;

/**
 * Created by Administrator on 2016.06.19.
 * GridView选择适配器
 */
public class GridVewSelectorAdapter extends BaseAdapter {
    private Context context;
    private List<StudentMemberInfo> list;
    //选择器
    private SelectorHelper selectorHelper;
    //单选还是多选
    private int pickerMode;

    public GridVewSelectorAdapter(Context context, List<StudentMemberInfo> list, int pickerMode) {
        this.context = context;
        selectorHelper = new SelectorHelper(list);
        this.list = list;
        this.pickerMode = pickerMode;
    }

    public int getPickerMode() {
        return pickerMode;
    }

    public SelectorHelper getSelectorHelper() {
        return selectorHelper;
    }

    /**
     * 更新单选布局
     */
    public void updateSinglePickerLayout(int singlePickerPosition) {
        for (int i = 0; i < list.size(); i++) {
            //当前position上的item设置为选中状态，其他position上的item设置为非选中状态。
            if (i != singlePickerPosition) {
                selectorHelper.selectItem(i, false);
            } else {
                selectorHelper.selectItem(i, true);
            }
        }
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
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(this.context).inflate(R.layout.item_screening_gridview, null);
            holder.icon_head = (ImageView) convertView.findViewById(R.id.icon_head);
            holder.icon_selector = (ImageView) convertView.findViewById(R.id.icon_selector);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (list != null && list.size() > 0) {
            MyApplication.getThumbnailManager((Activity) context).displayUserIconWithDefault(AppSettings.getFileUrl(list.get(position).getHeadPicUrl()),
                   holder.icon_head,R.drawable.default_user_icon );
            holder.title.setText(list.get(position).getRealName());
            //条目是否被选中
            boolean isItemSelected = selectorHelper.isItemSelected(position);
            //设置选中/非选中图片
            holder.icon_selector.setSelected(isItemSelected);
        }
        return convertView;
    }

    static class ViewHolder {
        public ImageView icon_head;
        public ImageView icon_selector;
        public TextView title;
    }
}
