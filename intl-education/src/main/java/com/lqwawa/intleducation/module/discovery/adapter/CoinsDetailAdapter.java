package com.lqwawa.intleducation.module.discovery.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.utils.DisplayUtil;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.module.discovery.ui.empty.EmptyActivity;
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

            if(info.getRechargeType() == -4){
                holder.tvCount.setVisibility(View.GONE);
            }else{
                holder.tvCount.setVisibility(View.VISIBLE);
                holder.tvCount.setTextColor(Color.parseColor("#01913a"));
                holder.tvCount.setText("+" + info.getAmount());
            }

            switch (info.getRechargeType()){
                case 0:
                    // 账户充值
                    holder.tvName.setText(context.getResources().getString(R.string.charge_account));
                    break;
                case 1:
                case 2:
                case -4:
                    // 给他人充值
                    String benefitStr = UIUtil.getString(R.string.label_other_donation_money_desc);
                    if(info.getRechargeType() == 1){
                        benefitStr = UIUtil.getString(R.string.label_other_generation_of_charge_desc);
                    }else if(info.getRechargeType() == -4){
                        // 代充
                        benefitStr = UIUtil.getString(R.string.label_generation_of_charge_desc);
                    }

                    SpannableStringBuilder spanBuilder = new SpannableStringBuilder();
                    String realName = info.getRealName();
                    if(EmptyUtil.isEmpty(realName)) {
                        realName = "";
                    }

                    String title = String.format(benefitStr,realName);
                    SpannableString spanReal = new SpannableString(title);
                    spanReal.setSpan(new ForegroundColorSpan(UIUtil.getColor(R.color.textPrimary)),
                            0,title.length(),Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                    spanBuilder.append(spanReal);

                    String userName = info.getUserName();
                    if(EmptyUtil.isNotEmpty(userName)){
                        SpannableString spanName = new SpannableString(" ("+userName+")");
                        spanName.setSpan(new ForegroundColorSpan(UIUtil.getColor(R.color.textSecond)),
                                0,spanName.length(),Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                        spanName.setSpan(new AbsoluteSizeSpan((int)DisplayUtil.sp2px(UIUtil.getContext(),14)),
                                0,spanName.length(),Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                        spanBuilder.append(spanName);
                    }

                    holder.tvName.setText(spanBuilder);
                    break;
            }
        } else {
            holder.tvCount.setVisibility(View.VISIBLE);
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
            }else if(info.getConsumeType() == 4){
                //赠送给他人
                String desc = UIUtil.getString(R.string.label_donation_money_desc);
                SpannableStringBuilder spanBuilder = new SpannableStringBuilder();
                String realName = info.getRealName();
                if(EmptyUtil.isEmpty(realName)) {
                    realName = "";
                }

                String title = String.format(desc,realName);
                SpannableString spanReal = new SpannableString(title);
                spanReal.setSpan(new ForegroundColorSpan(UIUtil.getColor(R.color.textPrimary)),
                        0, title.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                spanBuilder.append(spanReal);

                String userName = info.getUserName();
                if(EmptyUtil.isNotEmpty(userName)) {
                    SpannableString spanName = new SpannableString(" (" + userName + ")");
                    spanName.setSpan(new ForegroundColorSpan(UIUtil.getColor(R.color.textSecond)),
                            0, spanName.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                    spanName.setSpan(new AbsoluteSizeSpan((int)DisplayUtil.sp2px(UIUtil.getContext(),14)),
                            0,spanName.length(),Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                    spanBuilder.append(spanName);
                }

                holder.tvName.setText(spanBuilder);
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
