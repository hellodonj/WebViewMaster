package com.lqwawa.intleducation.module.user.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ui.MyBaseAdapter;
import com.lqwawa.intleducation.module.discovery.vo.OrganVo;
import org.xutils.image.ImageOptions;
import org.xutils.x;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by XChen on 2016/11/12.
 * email:man0fchina@foxmail.com
 */

public class AttentionOrganListAdapter extends MyBaseAdapter {
    private Activity activity;
    private List<OrganVo> list;
    private LayoutInflater inflater;
    private ImageOptions imageOptions;

    public AttentionOrganListAdapter(Activity activity) {
        this.activity = activity;
        this.inflater = LayoutInflater.from(activity);
        list = new ArrayList<OrganVo>();

        imageOptions = new ImageOptions.Builder()
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setRadius(16)
                .setCrop(false)
                .setLoadingDrawableId(R.drawable.contact_head_def)//加载中默认显示图片
                .setFailureDrawableId(R.drawable.contact_head_def)//加载失败后默认显示图片
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
        OrganVo vo = list.get(position);
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = inflater.inflate(R.layout.mod_user_person_contact_list_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        x.image().bind(holder.user_head_iv, ("" + vo.getThumbnail()).trim(), imageOptions);

        holder.name_tv.setText("" + vo.getName());
        holder.user_type_tv.setVisibility(View.GONE);
        holder.new_msg_count_tv.setVisibility(View.GONE);
        return convertView;
    }


    private class ViewHolder {
        ImageView user_head_iv;
        TextView new_msg_count_tv;
        TextView name_tv;
        TextView user_type_tv;

        public ViewHolder(View parentView) {
            user_head_iv = (ImageView) parentView.findViewById(R.id.user_head_iv);
            new_msg_count_tv = (TextView) parentView.findViewById(R.id.new_msg_count_tv);
            name_tv = (TextView) parentView.findViewById(R.id.name_tv);
            user_type_tv = (TextView) parentView.findViewById(R.id.user_type_tv);
        }
    }

    /**
     * 下拉刷新设置数据
     *
     * @param list
     */
    public void setData(List<OrganVo> list) {
        if (list != null) {
            this.list = new ArrayList<OrganVo>(list);
        } else {
            this.list.clear();
        }
    }

    /**
     * 上拉加载更多，新增数据
     *
     * @param list
     */
    public void addData(List<OrganVo> list) {
        this.list.addAll(list);
    }
}
