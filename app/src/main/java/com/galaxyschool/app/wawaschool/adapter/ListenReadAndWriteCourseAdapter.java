package com.galaxyschool.app.wawaschool.adapter;
import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.CallbackListener;
import com.galaxyschool.app.wawaschool.pojo.ResourceInfoTag;

import java.util.HashMap;
import java.util.List;

public class ListenReadAndWriteCourseAdapter extends BaseAdapter {
    private Context context;
    private CallbackListener listener;
    private List<ResourceInfoTag> list;
    private HashMap<Integer, View> viewMap = new HashMap<>();

    public ListenReadAndWriteCourseAdapter(Context context, List<ResourceInfoTag> list,
                                           CallbackListener listener) {
        this.context = context;
        this.listener = listener;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public ResourceInfoTag getItem(int position) {
        return list == null ? null : list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        if (viewMap.get(position) == null || !viewMap.containsKey(position)){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_add_course_detail, null);
            holder.courseImageView = (ImageView) convertView.findViewById(R.id.iv_add_course_btn);
            holder.deleteImage = (ImageView) convertView.findViewById(R.id.iv_delete_icon);
            holder.evalTextView = (TextView) convertView.findViewById(R.id.tv_eval_text);
            convertView.setTag(holder);
            viewMap.put(position, convertView);
        } else {
            convertView = viewMap.get(position);
            holder = (ViewHolder) convertView.getTag();
        }

        ResourceInfoTag info = getItem(position);
        holder.deleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onBack(position);
            }
        });
        if (TextUtils.isEmpty(info.getResId())){
            holder.deleteImage.setVisibility(View.GONE);
            holder.courseImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            holder.courseImageView.setImageResource(R.drawable.add_course_icon);
            holder.evalTextView.setVisibility(View.GONE);
        } else {
            holder.deleteImage.setVisibility(View.VISIBLE);
            holder.courseImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            MyApplication.getThumbnailManager((Activity) context).displayImageWithDefault(info.getImgPath(), holder
                    .courseImageView,R.drawable.default_cover);
            if (TextUtils.equals(info.getResProperties(),"1")){
                holder.evalTextView.setVisibility(View.VISIBLE);
            } else if (!TextUtils.isEmpty(info.getPoint())) {
               holder.evalTextView.setVisibility(View.VISIBLE);
            } else {
               holder.evalTextView.setVisibility(View.GONE);
            }
        }
        return convertView;
    }


    /**
     * 刷新
     *
     * @param resultList
     */
    public void update(List<ResourceInfoTag> resultList) {
        if (resultList != null && resultList.size() > 0) {
            this.list = resultList;
        }
        notifyDataSetChanged();
    }

    static class ViewHolder {
        public ImageView courseImageView;
        public ImageView deleteImage;
        public TextView evalTextView;
    }
}
