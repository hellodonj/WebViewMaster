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
import com.galaxyschool.app.wawaschool.common.StudyTaskUtils;
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
    private boolean showCheckType = false;

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

    public void setShowCheckType(boolean showCheckType) {
        this.showCheckType = showCheckType;
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
            if (taskType == StudyTaskType.RETELL_WAWA_COURSE){
                convertView = LayoutInflater.from(context).inflate(R.layout.item_add_retell_course_detail, null);
            } else {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_add_course_detail, null);
            }
            holder.courseImageView = (ImageView) convertView.findViewById(R.id.iv_add_course_btn);
            holder.evalTextView = (TextView) convertView.findViewById(R.id.tv_eval_text);
            holder.rightLayout = (LinearLayout) convertView.findViewById(R.id.ll_right_layout);
            holder.title = (TextView) convertView.findViewById(R.id.tv_course_title);
            holder.completeType = (LinearLayout) convertView.findViewById(R.id.ll_completion_mode);
            holder.multiTypeBtnRB = (RadioButton) convertView.findViewById(R.id.rb_multi_type);
            holder.rellTypeBtnRB = (RadioButton) convertView.findViewById(R.id.rb_retell_course);
            holder.completionTitle = (TextView) convertView.findViewById(R.id.tv_completion_title);
            holder.radioGroup = (RadioGroup) convertView.findViewById(R.id.radio_group);
            if (taskType == StudyTaskType.RETELL_WAWA_COURSE) {
                holder.deleteImage = (ImageView) convertView.findViewById(R.id.iv_delete_item);
                //完成方式
                holder.completionTitle.setText(R.string.str_complete_method);
            } else if (taskType == StudyTaskType.TASK_ORDER && showCheckType) {
                holder.deleteImage = (ImageView) convertView.findViewById(R.id.iv_delete_item);
                //自动批阅
                holder.rellTypeBtnRB.setText(context.getString(R.string.str_auto_mark));
                //人工点评
                holder.multiTypeBtnRB.setText(context.getString(R.string.str_manual_marking));
                //批阅方式
                holder.completionTitle.setText(R.string.str_mark_method);
            } else if(taskType == StudyTaskType.Q_DUBBING) {
                holder.deleteImage = (ImageView) convertView.findViewById(R.id.iv_delete_item);
                //按句配音
                holder.rellTypeBtnRB.setText(context.getString(R.string.str_dubbing_by_sentence));
                //通篇配音
                holder.multiTypeBtnRB.setText(context.getString(R.string.str_dubbing_by_whole));
                //配音类型
                holder.completionTitle.setText(context.getString(R.string.str_dubbing_type));
            } else {
                holder.deleteImage = (ImageView) convertView.findViewById(R.id.iv_delete_icon);
                //复述课件
                holder.rellTypeBtnRB.setText(context.getString(R.string.retell_course_new));
                //语音评测
                holder.multiTypeBtnRB.setText(context.getString(R.string.auto_mark));
                //完成方式
                holder.completionTitle.setText(R.string.str_complete_method);
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
            if (taskType == StudyTaskType.RETELL_WAWA_COURSE
                    || (taskType == StudyTaskType.TASK_ORDER && showCheckType)
                    || taskType == StudyTaskType.Q_DUBBING) {
                holder.rightLayout.setVisibility(View.GONE);
                holder.completeType.setVisibility(View.GONE);
            }
        } else {
            holder.deleteImage.setVisibility(View.VISIBLE);
            holder.courseImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            MyApplication.getThumbnailManager((Activity) context).displayImageWithDefault(StudyTaskUtils.getResourceThumbnail(info.getImgPath()),
                    holder
                    .courseImageView, R.drawable.default_cover);
            if (TextUtils.equals(info.getResProperties(), "1")
                    || !TextUtils.isEmpty(info.getPoint())) {
                holder.evalTextView.setVisibility(View.VISIBLE);
                holder.rellTypeBtnRB.setVisibility(View.VISIBLE);
                holder.multiTypeBtnRB.setVisibility(View.VISIBLE);
            } else if (taskType == StudyTaskType.Q_DUBBING) {
                holder.evalTextView.setVisibility(View.GONE);
                holder.rellTypeBtnRB.setVisibility(View.VISIBLE);
                holder.multiTypeBtnRB.setVisibility(View.VISIBLE);
            } else {
                holder.evalTextView.setVisibility(View.GONE);
                if ((taskType == StudyTaskType.TASK_ORDER && showCheckType)){
                    holder.rellTypeBtnRB.setVisibility(View.GONE);
                    holder.multiTypeBtnRB.setVisibility(View.VISIBLE);
                } else {
                    holder.rellTypeBtnRB.setVisibility(View.VISIBLE);
                    holder.multiTypeBtnRB.setVisibility(View.GONE);
                }
            }

            if (taskType == StudyTaskType.RETELL_WAWA_COURSE
                    || (taskType == StudyTaskType.TASK_ORDER && showCheckType)
                    || taskType == StudyTaskType.Q_DUBBING) {
                holder.title.setText(info.getTitle());
                holder.rightLayout.setVisibility(View.VISIBLE);
                holder.completeType.setVisibility(View.VISIBLE);
                if (holder.radioGroup != null) {
                    holder.radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
                        if (checkedId == R.id.rb_retell_course) {
                            if (taskType == StudyTaskType.TASK_ORDER) {
                                info.setResPropertyMode(1);
                            } else if (taskType == StudyTaskType.Q_DUBBING) {
                                info.setResPropType(2);
                            } else {
                                info.setCompletionMode(1);
                            }
                        } else if (checkedId == R.id.rb_multi_type) {
                            if (taskType == StudyTaskType.TASK_ORDER) {
                                info.setResPropertyMode(2);
                            } else if (taskType == StudyTaskType.Q_DUBBING) {
                                info.setResPropType(3);
                            } else {
                                info.setCompletionMode(2);
                            }
                        }
                        notifyDataSetChanged();
                    });
                }

                if (holder.radioGroup != null){
                    if (taskType == StudyTaskType.TASK_ORDER){
                        holder.radioGroup.check(info.getResPropertyMode() == 1 ? R.id.rb_retell_course :
                                R.id.rb_multi_type);
                    } else if (taskType == StudyTaskType.Q_DUBBING) {
                        holder.radioGroup.check(info.getResPropType() == 2 ? R.id.rb_retell_course :
                                R.id.rb_multi_type);
                    } else {
                        holder.radioGroup.check(info.getCompletionMode() == 1 ? R.id.rb_retell_course :
                                R.id.rb_multi_type);
                    }
                }

                if (taskType == StudyTaskType.RETELL_WAWA_COURSE){
                    if (info.getCompletionMode() == 1){
                        holder.rellTypeBtnRB.setChecked(true);
                        holder.multiTypeBtnRB.setChecked(false);
                    } else if (info.getCompletionMode() == 2){
                        holder.rellTypeBtnRB.setChecked(true);
                        holder.multiTypeBtnRB.setChecked(true);
                    } else if (info.getCompletionMode() == 3){
                        holder.multiTypeBtnRB.setChecked(true);
                        holder.rellTypeBtnRB.setChecked(false);
                    }
                    //复述
                    holder.rellTypeBtnRB.setOnClickListener(v -> analysisCompletionMode(info,holder,true));
                    //语音评测
                    holder.multiTypeBtnRB.setOnClickListener(v -> analysisCompletionMode(info,holder,false));
                }
            }
        }
        return convertView;
    }

    private void analysisCompletionMode(ResourceInfoTag info,ViewHolder holder,boolean isRetell){
        if (isRetell){
            if (info.getCompletionMode() == 1){
                return;
            }
            if (info.getCompletionMode() == 2){
                info.setCompletionMode(3);
            } else if (info.getCompletionMode() == 3){
                info.setCompletionMode(2);
            }
        } else {
            if (info.getCompletionMode() == 3){
                return;
            }
            if (info.getCompletionMode() == 1){
                info.setCompletionMode(2);
            } else if (info.getCompletionMode() == 2){
                info.setCompletionMode(1);
            }
        }
        notifyDataSetChanged();
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
        public RadioButton rellTypeBtnRB;
        public TextView completionTitle;
        public RadioGroup radioGroup;
    }
}
