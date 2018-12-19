package com.lqwawa.intleducation.module.learn.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ui.MyBaseAdapter;
import com.lqwawa.intleducation.base.utils.DateUtils;
import com.lqwawa.intleducation.module.discovery.ui.CredentialDetailsActivity;
import com.lqwawa.intleducation.module.learn.ui.MyCredentialCourseListActivity;
import com.lqwawa.intleducation.module.learn.vo.MyCredentialListVo;
import org.xutils.image.ImageOptions;
import org.xutils.x;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by XChen on 2016/11/28.
 * email:man0fchina@foxmail.com
 */

public class MyCredentialListAdapter extends MyBaseAdapter {
    private Activity activity;
    private List<MyCredentialListVo> list;
    private LayoutInflater inflater;
    private int img_width;
    private int img_height;
    ImageOptions imageOptions;

    public MyCredentialListAdapter(Activity activity) {
        this.activity = activity;
        this.inflater = LayoutInflater.from(activity);
        list = new ArrayList<MyCredentialListVo>();

        int p_width = activity.getWindowManager().getDefaultDisplay().getWidth();
        img_width = p_width / 3;
        img_height = img_width * 3 / 4;

        imageOptions = new ImageOptions.Builder()
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setCrop(false)
                //.setSize(img_width,img_height)
                .setLoadingDrawableId(R.drawable.img_def)//加载中默认显示图片
                .setFailureDrawableId(R.drawable.img_def)//加载失败后默认显示图片
                .build();
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
        final MyCredentialListVo vo = list.get(position);
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = inflater.inflate(R.layout.mod_learn_credential_list_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        holder.credential_date_tv.setText(DateUtils.getFormatByStringDate(
                "" + vo.getCertification().getCreateTime(),
                DateUtils.YYYYMMDDCH));
        holder.credential_name.setText(vo.getCertification().getCertificationName());
        holder.organ_name.setText("" + vo.getOrganName());
        x.image().bind(holder.credential_iv,
                vo.getThumbnail().toString().trim(),
                imageOptions);
        holder.credential_iv.setLayoutParams(new LinearLayout.LayoutParams(img_width, img_height));

        if (vo != null) { //再学习-证书列表里全是已加入的状态
            if (!vo.isIsFinish()) {//已加入 但没有完成课程
                holder.apply_iv.setVisibility(View.GONE);
                holder.cer_status_tv.setVisibility(View.GONE);
            } else {//已完成课程
                if (!vo.getCertification().isIsApply()) {//已完成课程但是没有认证
                    holder.apply_iv.setVisibility(View.VISIBLE);
                    holder.cer_status_tv.setVisibility(View.GONE);
                    holder.apply_iv.setText(activity.getResources().getString(R.string.apply_certification));
                } else {//已完成课程而且已经申请了认证
                    if (!vo.isHaveOrder()) {//已经申请了认证但是没有购买
                        holder.apply_iv.setVisibility(View.VISIBLE);
                        holder.cer_status_tv.setVisibility(View.GONE);
                        holder.apply_iv.setText(activity.getResources().getString(R.string.to_buy));
                    } else {//已认证而且已确认订单
                        if (!vo.getCertification().isIsPay()) {//已经确认订单但是没有支付
                            holder.apply_iv.setVisibility(View.VISIBLE);
                            holder.cer_status_tv.setVisibility(View.GONE);
                            holder.apply_iv.setText(activity.getResources().getString(R.string.to_pay));
                        } else {//已认证而且支付完成了
                            holder.apply_iv.setVisibility(View.GONE);
                            holder.cer_status_tv.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        }else{
            holder.cer_status_tv.setVisibility(View.GONE);
            holder.apply_iv.setVisibility(View.GONE);
        }
        holder.apply_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CredentialDetailsActivity.start(activity, vo.getCertification().getCertificationId());
            }
        });
        holder.view_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyCredentialCourseListActivity.start(activity
                        , vo.getCertification().getCertificationId()
                        , vo.getCertification().getCertificationName());
            }
        });
        return convertView;
    }


    private class ViewHolder {
        TextView credential_date_tv;
        ImageView credential_iv;
        TextView credential_name;
        TextView organ_name;
        TextView view_iv;
        TextView apply_iv;
        TextView cer_status_tv;
        public ViewHolder(View parentView) {
            credential_date_tv = (TextView)parentView.findViewById(R.id.credential_date_tv);
            credential_iv = (ImageView)parentView.findViewById(R.id.credential_iv);
            credential_name = (TextView) parentView.findViewById(R.id.credential_name);
            organ_name = (TextView)parentView.findViewById(R.id.organ_name);
            view_iv = (TextView)parentView.findViewById(R.id.view_iv);
            apply_iv = (TextView)parentView.findViewById(R.id.apply_iv);
            cer_status_tv = (TextView)parentView.findViewById(R.id.cer_status_tv);
        }
    }

    /**
     * 下拉刷新设置数据
     *
     * @param list
     */
    public void setData(List<MyCredentialListVo> list) {
        if (list != null) {
            this.list = new ArrayList<MyCredentialListVo>(list);
        } else {
            this.list.clear();
        }
    }

    /**
     * 上拉加载更多，新增数据
     *
     * @param list
     */
    public void addData(List<MyCredentialListVo> list) {
        this.list.addAll(list);
    }
}
