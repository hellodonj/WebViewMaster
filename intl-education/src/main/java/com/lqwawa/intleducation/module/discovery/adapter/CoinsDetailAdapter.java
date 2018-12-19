package com.lqwawa.intleducation.module.discovery.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.module.discovery.vo.CoinsDetailInfo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * ================================================
 * author：xu_wenliang
 * time：2018/4/9 18:54
 * desp: 描 述：
 * ================================================
 */

public class CoinsDetailAdapter extends BaseAdapter {

    private List<CoinsDetailInfo> datas;
    private Context context;


    public CoinsDetailAdapter(Context context, List<CoinsDetailInfo> data) {
        this.datas = data;
        this.context = context;
    }

    @Override
    public int getCount() {
        return datas.size();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DetailHolder holder;
        if (convertView == null) {
            holder = new DetailHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_coins_detail, parent, false);
            holder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tvTime = (TextView) convertView.findViewById(R.id.tv_time);
            holder.tvCount = (TextView) convertView.findViewById(R.id.tv_count);
            convertView.setTag(holder);
        } else {
            holder = (DetailHolder) convertView.getTag();
        }
        CoinsDetailInfo info = datas.get(position);
        holder.tvName.setText(info.getCourseName());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //获取当前时间
        Date date = new Date(info.getCreateTime());
        holder.tvTime.setText(simpleDateFormat.format(date));
        if (info.getVtype() == 0) {
            //充值
            holder.tvCount.setTextColor(Color.parseColor("#01913a"));
            holder.tvCount.setText("+" + info.getAmount());
            holder.tvName.setText(context.getResources().getString(R.string.charge_account));
        } else {
            holder.tvCount.setTextColor(Color.parseColor("#161616"));
            holder.tvCount.setText("-" + info.getAmount());
            if (info.getConsumeType() == 0){
                //购买课程
                holder.tvName.setText(String.format(context.getResources().getString(R.string.buy_course),info.getCourseName()));
            }else if (info.getConsumeType() == 1){
                //购买直播
                holder.tvName.setText(String.format(context.getResources().getString(R.string.buy_live),info.getCourseName()));
            }else if (info.getConsumeType() == 3){
               //购买在线课堂
                holder.tvName.setText(String.format(context.getResources().getString(R.string.buy_online_school),info.getCourseName()));
            }else {
                //学程馆借买书籍
            }
        }

        return convertView;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    class DetailHolder {

        TextView tvName;
        TextView tvTime;
        TextView tvCount;

    }


}
