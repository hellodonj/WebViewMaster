package com.lqwawa.intleducation.module.user.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ui.MyBaseAdapter;
import com.lqwawa.intleducation.module.user.vo.MyNoticeVo;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by XChen on 2016/11/29.
 * email:man0fchina@foxmail.com
 */

public class MyNoticesListAdapter extends MyBaseAdapter {
    private Activity activity;
    private List<MyNoticeVo> list;
    private LayoutInflater inflater;

    public MyNoticesListAdapter(Activity activity) {
        this.activity = activity;
        this.inflater = LayoutInflater.from(activity);
        list = new ArrayList<MyNoticeVo>();
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
        MyNoticeVo vo = list.get(position);
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = inflater.inflate(R.layout.mod_my_notice_list_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        holder.notice_title_tv.setText("" + vo.getContent());
        holder.notice_date_tv.setText(vo.getCreateTime() + "");
        if(vo.isIsRead()){
            holder.not_read_flag.setVisibility(View.GONE);
        }else{
            holder.not_read_flag.setVisibility(View.VISIBLE);
        }
        return convertView;
    }



    private class ViewHolder {
        TextView notice_title_tv;
        TextView notice_date_tv;
        View not_read_flag;

        public ViewHolder(View parentView) {
            notice_title_tv = (TextView) parentView.findViewById(R.id.notice_title_tv);
            notice_date_tv = (TextView) parentView.findViewById(R.id.notice_date_tv);
            not_read_flag = (View) parentView.findViewById(R.id.not_read_flag);
        }
    }
    /**
     * 下拉刷新设置数据
     *
     * @param list
     */
    public void setData(List<MyNoticeVo> list) {
        if (list != null) {
            this.list = new ArrayList<MyNoticeVo>(list);
        } else {
            this.list.clear();
        }
    }

    /**
     * 上拉加载更多，新增数据
     *
     * @param list
     */
    public void addData(List<MyNoticeVo> list) {
        this.list.addAll(list);
    }
}
