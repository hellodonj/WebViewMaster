package com.lqwawa.intleducation.module.discovery.adapter;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.module.discovery.vo.NormalChargeInfo;

import java.util.List;

/**
 * ================================================
 * author：xu_wenliang
 * time：2018/4/9 11:11
 * desp: 描 述：
 * ================================================
 */

public class NormalCountAdapter extends BaseAdapter {

    private List<NormalChargeInfo> datas;
    private Context context;



    public NormalCountAdapter(Context context, List<NormalChargeInfo> data) {
        this.datas = data;
        this.context = context;
    }

    @Override
    public int getCount() {
        return datas.size();
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CountHolder holder;
        if (convertView == null){
            holder = new CountHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_normal_count_layout,parent,false);

            holder.llRoot = (LinearLayout) convertView.findViewById(R.id.item_root);
            holder.tvCount = (TextView) convertView.findViewById(R.id.tv_count);
            convertView.setTag(holder);

        }else {
            holder = (CountHolder) convertView.getTag();
        }


        NormalChargeInfo chargeInfo = datas.get(position);

        if (chargeInfo.isSelected){
            holder.llRoot.setBackgroundResource(R.drawable.selected_pay);
        }else {
            holder.llRoot.setBackgroundResource(R.drawable.unselected_pay);
        }


        holder.tvCount.setText(chargeInfo.coinAmount+"");

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


    class CountHolder {
        TextView tvCount;
        LinearLayout llRoot;
    }


}
