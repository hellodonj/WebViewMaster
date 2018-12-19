package com.lqwawa.intleducation.module.user.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ui.MyBaseAdapter;
import com.lqwawa.intleducation.module.user.vo.AreaVo;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by XChen on 2016/12/1.
 * email:man0fchina@foxmail.com
 */

public class AreaAdapter extends MyBaseAdapter {
    private Activity activity;
    private List<AreaVo> list;
    private LayoutInflater inflater;

    public AreaAdapter(Activity activity) {
        this.activity = activity;
        this.inflater = LayoutInflater.from(activity);
        list = new ArrayList<AreaVo>();
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
        AreaVo vo = list.get(position);
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = inflater.inflate(R.layout.area_list_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        holder.right_arrow_iv.setVisibility(vo.getLevelNum() == 3 ? View.GONE : View.VISIBLE);
        holder.area_tv.setText(vo.getName());
        return convertView;
    }


    private class ViewHolder {
        TextView area_tv;
        ImageView right_arrow_iv;
        public ViewHolder(View parentView) {
            area_tv = (TextView)parentView.findViewById(R.id.area_tv);
            right_arrow_iv = (ImageView)parentView.findViewById(R.id.right_arrow_iv);
        }
    }

    /**
     * 下拉刷新设置数据
     *
     * @param list
     */
    public void setData(List<AreaVo> list) {
        if (list != null) {
            this.list = new ArrayList<AreaVo>(list);
        } else {
            this.list.clear();
        }
    }

    /**
     * 上拉加载更多，新增数据
     *
     * @param list
     */
    public void addData(List<AreaVo> list) {
        this.list.addAll(list);
    }
}
