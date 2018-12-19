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
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.imagebrowser.GalleryActivity;
import com.galaxyschool.app.wawaschool.pojo.ResType;
import com.galaxyschool.app.wawaschool.pojo.learnTaskCardData;
import com.libs.gallery.ImageInfo;
import com.lqwawa.client.pojo.LearnTaskCardType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DoTaskOrderAnswerQuestionAdapter extends BaseAdapter {
    private Context context;
    private List<learnTaskCardData> list;
    private HashMap<Integer, View> viewMap = new HashMap<>();
    private boolean isSingle;//单选
    private int layoutId;

    public DoTaskOrderAnswerQuestionAdapter(Context context, List<learnTaskCardData> list) {
        this.context = context;
        this.list = list;
        if (this.list == null || this.list.size() == 0) {
            //主观题
            layoutId = R.layout.item_answer_subject_problem_detail;
        } else {
            //客观题
            isSingle = list.get(0).getSelectType() == 0;
            //选择题
            layoutId = R.layout.item_answercard_check_position;
        }
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public learnTaskCardData getItem(int position) {
        return list == null || list.size() == 0 ? null : list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        learnTaskCardData info = getItem(position);
        if (info == null) {
            return convertView;
        }
        if (viewMap.get(position) == null || !viewMap.containsKey(position)) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(layoutId, null);
            findViewById(holder, convertView, info);
            convertView.setTag(holder);
            viewMap.put(position, convertView);
        } else {
            convertView = viewMap.get(position);
            holder = (ViewHolder) convertView.getTag();
        }
        if (info.getQuestionType() == LearnTaskCardType.SUBJECTIVE_PROBLEM) {
            handleSubjectProblemData(holder, info, position);
            convertView.setOnClickListener(v -> openImageDetail(info,position));
        } else {
            handleChooseTypeData(holder, info, position);
        }

        return convertView;
    }

    private void findViewById(ViewHolder holder, View convertView, learnTaskCardData data) {
        if (data.getQuestionType() == LearnTaskCardType.SUBJECTIVE_PROBLEM) {
            //主观题
            holder.answerPicImageView = (ImageView) convertView.findViewById(R.id.iv_add_course_btn);
            holder.deletePicImageView = (ImageView) convertView.findViewById(R.id.iv_delete_icon);
        } else {
            //选择题
            holder.checkLayout = (LinearLayout) convertView.findViewById(R.id.ll_check);
            holder.checkSelectImageView = (ImageView) convertView.findViewById(R.id.iv_check_position);
            holder.checkSelectTitle = (TextView) convertView.findViewById(R.id.tv_check_title);
        }
    }

    private void handleChooseTypeData(ViewHolder holder, learnTaskCardData data, int position) {
        if (data.isSelect()) {
            //checked
            holder.checkSelectImageView.setImageResource(R.drawable.radiobtn_select);
        } else {
            //unChecked
            holder.checkSelectImageView.setImageResource(R.drawable.radiobtn);
        }
        holder.checkLayout.setOnClickListener((v) -> {
            setCheckItemSelect(position);
            notifyDataSetChanged();
        });
        holder.checkSelectTitle.setText(data.getItemTitle());
    }

    private void handleSubjectProblemData(ViewHolder holder, learnTaskCardData data, int position) {
        MyApplication.getThumbnailManager((Activity) context).displayImage(data.getAnswerPath
                (), holder.answerPicImageView);
        holder.deletePicImageView.setOnClickListener(v -> {
            getData().remove(position);
            notifyDataSetChanged();
        });
    }

    private void openImageDetail(learnTaskCardData info,int position) {
        List<ImageInfo> resourceInfoList = new ArrayList<>();
        if (list != null && list.size() > 0){
            for (int i = 0; i < list.size(); i++){
                learnTaskCardData data = list.get(i);
                ImageInfo newResourceInfo = new ImageInfo();
                newResourceInfo.setTitle(data.getItemTitle());
                newResourceInfo.setResourceUrl(data.getAnswerPath());
                resourceInfoList.add(newResourceInfo);
            }
        }
        GalleryActivity.newInstance(context, resourceInfoList, true, position, false, false, false);
    }


    public void setCheckItemSelect(int position) {
        for (int i = 0; i < list.size(); i++) {
            learnTaskCardData data = list.get(i);
            if (isSingle) {
                //单选
                if (i == position) {
                    data.setIsSelect(!data.isSelect());
                } else {
                    data.setIsSelect(false);
                }
            } else {
                //多选
                if (i == position) {
                    data.setIsSelect(!data.isSelect());
                }
            }
        }
    }

    public List<learnTaskCardData> getData() {
        return this.list;
    }

    static class ViewHolder {
        LinearLayout checkLayout;
        ImageView checkSelectImageView;
        TextView checkSelectTitle;
        ImageView answerPicImageView;
        ImageView deletePicImageView;
    }
}
