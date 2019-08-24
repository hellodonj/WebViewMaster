package com.galaxyschool.app.wawaschool.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.CallbackListener;
import com.galaxyschool.app.wawaschool.common.WatchWawaCourseResourceOpenUtils;
import com.galaxyschool.app.wawaschool.pojo.ResourceInfoTag;
import com.galaxyschool.app.wawaschool.pojo.StudyTaskType;

import java.util.List;

/**
 * Created by Administrator on 2016.06.19.
 * 看课件列表adapter
 */
public class WatchWawaCourseListAdapter extends BaseAdapter {
    private Context context;
    private List<ResourceInfoTag> list;
    private CallbackListener callbackListener;
    private int taskType;
    public WatchWawaCourseListAdapter(Context context,
                                      List<ResourceInfoTag> list,
                                      int taskType,
                                      CallbackListener callbackListener) {
        this.context = context;
        this.list = list;
        this.taskType = taskType;
        this.callbackListener = callbackListener;
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
            final ResourceInfoTag resourceInfoTag = list.get(position);
            if (resourceInfoTag != null){
                //默认图片
                int defaultIcon = WatchWawaCourseResourceOpenUtils.getItemDefaultIcon
                        (resourceInfoTag);
                //图片
                holder.iconImageView.setImageResource(defaultIcon);
                holder.iconImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //打开资源
                        WatchWawaCourseResourceOpenUtils.openResource(context,resourceInfoTag,
                                true,false,true);
                    }
                });
                //标题
                String title = WatchWawaCourseResourceOpenUtils.getItemTitle(resourceInfoTag);
                holder.titleTextView.setText(title);
                //删除
                holder.deleteImageView.setVisibility(View.VISIBLE);
                holder.deleteImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //删除
                        list.remove(resourceInfoTag);
                        notifyDataSetChanged();
                        if (callbackListener != null){
                            callbackListener.onBack(true);
                        }
                    }
                });
                //分割线
                if (taskType == StudyTaskType.ENGLISH_WRITING){
                    holder.dividerLineView.setVisibility(View.GONE);
                } else {
                    holder.dividerLineView.setVisibility(View.VISIBLE);
                }
            }
        }
        return convertView;
    }


    /**
     * 刷新
     * @param resultList
     */
    public void update(List<ResourceInfoTag> resultList){
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
