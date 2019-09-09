package com.lqwawa.mooc.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.StudyTaskUtils;
import com.galaxyschool.app.wawaschool.pojo.ResourceInfoTag;
import com.lqwawa.intleducation.common.utils.EmptyUtil;

import java.util.List;

/**
 * 描述: 选取图片list的adapter
 * 作者|时间: djj on 2019/9/4 0004 上午 11:40
 */
public class SelectPictureListAdapter extends RecyclerView.Adapter<SelectPictureListAdapter.ViewHolder> {

    private Context context;
    private List<ResourceInfoTag> list;

    public SelectPictureListAdapter(Context context,
                                    List<ResourceInfoTag> list) {
        this.context = context;
        this.list = list;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_picture_info, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.deleteImageView.setVisibility(View.GONE);
        //在list.size<9时，最后一个默认显示“加号”图片
        if (position == list.size()) {
            holder.iconImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            holder.iconImageView.setImageResource(R.drawable.add_course_icon);
            if (mOnItemClickListener != null) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnItemClickListener.onItemClick(holder.itemView, position);
                    }
                });
            }
        } else {
            if (list != null && list.size() > 0) {
                ResourceInfoTag infoTag = list.get(position);
                if (infoTag != null) {
                    if (EmptyUtil.isEmpty(infoTag.getTitle())){
                        if (position == list.size()){
                            holder.deleteImageView.setVisibility(View.GONE);
                            holder.iconImageView.setVisibility(View.GONE);
                        }
                    }
                    holder.iconImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    MyApplication.getThumbnailManager((Activity) context).displayImageWithDefault(StudyTaskUtils.getResourceThumbnail(infoTag.getImgPath()),
                            holder.iconImageView, R.drawable.default_cover);
                    //非最后一张显示删除角标
                    if (position < list.size()) {
                        //图片删除角标
                        holder.deleteImageView.setVisibility(View.VISIBLE);
                        holder.deleteImageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //从数据源list中移除
                                list.remove(position);
                                notifyDataSetChanged();
                            }
                        });
                    }
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        //含有10张图片，直接展示，不需要“加号”图片
        if (list.size() == 10) {
            return 10;
        }
        //小于9张需要“加号”图片
        return list.size() + 1;
    }

    /**
     * 刷新
     *
     * @param resultList
     */
    public void update(List<ResourceInfoTag> resultList) {
        if (resultList != null && resultList.size() > 0) {
            this.list = resultList;
            notifyDataSetChanged();
        }
    }

    /**
     * 清除list
     *
     * @param resultList
     */
    public void removeAll(List<ResourceInfoTag> resultList) {
        if (resultList != null && resultList.size() > 0) {
            this.list.removeAll(resultList);
            notifyDataSetChanged();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView iconImageView;
        public ImageView deleteImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            iconImageView = (ImageView) itemView.findViewById(R.id.attachments_img);
            deleteImageView = (ImageView) itemView.findViewById(R.id.delete_iv);
        }
    }

    /**
     * 定义接口回调
     */
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

}
