package com.galaxyschool.app.wawaschool.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.CallbackListener;
import com.galaxyschool.app.wawaschool.pojo.ResourceInfoTag;
import com.galaxyschool.app.wawaschool.pojo.StudyTaskType;
import java.util.HashMap;
import java.util.List;

public class ListenReadAndWriteCourseAdapter extends BaseAdapter {
    private Context context;
    private CallbackListener deleteListener;
    private CallbackListener openListener;
    private List<ResourceInfoTag> list;
    private HashMap<Integer, View> viewMap = new HashMap<>();
    private int taskType;

    public ListenReadAndWriteCourseAdapter(Context context,
                                           List<ResourceInfoTag> list,
                                           int taskType,
                                           CallbackListener deleteListener,
                                           CallbackListener openListener) {
        this.context = context;
        this.deleteListener = deleteListener;
        this.openListener = openListener;
        this.taskType = taskType;
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
        if (viewMap.get(position) == null || !viewMap.containsKey(position)) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_add_course_detail,null);
            holder.courseImageView = (ImageView) convertView.findViewById(R.id.iv_add_course_btn);
            holder.evalTextView = (TextView) convertView.findViewById(R.id.tv_eval_text);
            holder.rightLayout = (LinearLayout) convertView.findViewById(R.id.ll_right_layout);
            holder.title = (TextView) convertView.findViewById(R.id.tv_course_title);
            holder.completeType = (LinearLayout) convertView.findViewById(R.id.ll_completion_mode);
            holder.multiTypeBtnRB = (RadioButton) convertView.findViewById(R.id.rb_multi_type);
            holder.radioGroup = (RadioGroup) convertView.findViewById(R.id.radio_group);
            if (taskType == StudyTaskType.RETELL_WAWA_COURSE) {
                holder.deleteImage = (ImageView) convertView.findViewById(R.id.iv_delete_item);
            } else {
                holder.deleteImage = (ImageView) convertView.findViewById(R.id.iv_delete_icon);
            }
            convertView.setTag(holder);
            viewMap.put(position, convertView);
        } else {
            convertView = viewMap.get(position);
            holder = (ViewHolder) convertView.getTag();
        }

        ResourceInfoTag info = getItem(position);
        holder.deleteImage.setOnClickListener(v -> {
            deleteListener.onBack(position);
        });
        holder.courseImageView.setOnClickListener(v -> {
            openListener.onBack(position);
        });
        if (TextUtils.isEmpty(info.getResId())) {
            holder.deleteImage.setVisibility(View.GONE);
            holder.courseImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            holder.courseImageView.setImageResource(R.drawable.add_course_icon);
            holder.evalTextView.setVisibility(View.GONE);
            if (taskType == StudyTaskType.RETELL_WAWA_COURSE) {
                holder.rightLayout.setVisibility(View.GONE);
                holder.completeType.setVisibility(View.GONE);
            }
        } else {
            holder.deleteImage.setVisibility(View.VISIBLE);
            holder.courseImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            MyApplication.getThumbnailManager((Activity) context).displayImageWithDefault(info.getImgPath(), holder
                    .courseImageView, R.drawable.default_cover);
            if (TextUtils.equals(info.getResProperties(), "1")) {
                holder.evalTextView.setVisibility(View.VISIBLE);
                holder.multiTypeBtnRB.setVisibility(View.VISIBLE);
            } else if (!TextUtils.isEmpty(info.getPoint())) {
                holder.evalTextView.setVisibility(View.VISIBLE);
                holder.multiTypeBtnRB.setVisibility(View.GONE);
            } else {
                holder.evalTextView.setVisibility(View.GONE);
                holder.multiTypeBtnRB.setVisibility(View.GONE);
            }

            if (taskType == StudyTaskType.RETELL_WAWA_COURSE) {
                holder.title.setText(info.getTitle());
                holder.rightLayout.setVisibility(View.VISIBLE);
                holder.completeType.setVisibility(View.VISIBLE);
                holder.radioGroup.setOnCheckedChangeListener((group,checkedId) -> {
                    if (checkedId == R.id.rb_retell_course){
                        info.setCompletionMode(0);
                    } else if (checkedId == R.id.rb_multi_type){
                        info.setCompletionMode(1);
                    }
                });
                holder.radioGroup.check(info.getCompletionMode() == 0 ? R.id.rb_retell_course :
                        R.id.rb_multi_type);
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
        public TextView title;
        public LinearLayout completeType;
        public LinearLayout rightLayout;
        public RadioButton multiTypeBtnRB;
        public RadioGroup radioGroup;
    }
}
