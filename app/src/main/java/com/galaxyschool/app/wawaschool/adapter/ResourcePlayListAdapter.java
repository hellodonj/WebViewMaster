package com.galaxyschool.app.wawaschool.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.WatchWawaCourseResourceOpenUtils;
import com.galaxyschool.app.wawaschool.pojo.ResourceInfoTag;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.entity.course.CourseResourceEntity;

import java.util.List;
public class ResourcePlayListAdapter extends BaseAdapter {
    private Context context;
    private List<CourseResourceEntity> list;

    public ResourcePlayListAdapter(Context context, List<CourseResourceEntity> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public Object getItem(int position) {
        return list == null ? null : list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_course_info, null);
            holder.iconImageView = (ImageView) convertView.findViewById(R.id.iv_icon);
            holder.titleTextView = (TextView) convertView.findViewById(R.id.tv_title);
            holder.deleteImageView = (ImageView) convertView.findViewById(R.id.iv_delete);
            holder.dividerLineView = convertView.findViewById(R.id.divider_line);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (list != null && list.size() > 0) {
            final CourseResourceEntity data = list.get(position);
            if (data != null) {
                //默认图片
                ResourceInfoTag infoTag = new ResourceInfoTag();
                infoTag.setResId(data.getResId() + "-" + data.getResType());
                int defaultIcon = WatchWawaCourseResourceOpenUtils.getItemDefaultIcon(infoTag);
                //图片
                holder.iconImageView.setImageResource(defaultIcon);
                holder.iconImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //打开资源
//                        WatchWawaCourseResourceOpenUtils.openResource(context,resourceInfoTag,
//                                true,false,true);
                    }
                });
                //标题
                holder.titleTextView.setText(data.getNickName());
                if (data.isSelected()){
                    holder.titleTextView.setTextColor(ContextCompat.getColor(UIUtil.getContext(),
                            R.color.text_light_gray));
                } else {
                    holder.titleTextView.setTextColor(ContextCompat.getColor(UIUtil.getContext(),
                            R.color.text_black));
                }
                //删除
                holder.deleteImageView.setVisibility(View.GONE);
                holder.deleteImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //删除
                        list.remove(data);
                        notifyDataSetChanged();
                    }
                });
                //分割线
                holder.dividerLineView.setVisibility(View.VISIBLE);
            }
        }
        return convertView;
    }


    /**
     * 刷新
     *
     * @param resultList
     */
    public void update(List<CourseResourceEntity> resultList) {
        if (resultList != null && resultList.size() > 0) {
            this.list = resultList;
            notifyDataSetChanged();
        }
    }

    static class ViewHolder {
        public ImageView iconImageView;
        public TextView titleTextView;
        public ImageView deleteImageView;
        public View dividerLineView;
    }
}
