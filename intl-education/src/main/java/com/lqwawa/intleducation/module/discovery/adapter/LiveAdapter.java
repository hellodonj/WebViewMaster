package com.lqwawa.intleducation.module.discovery.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ui.MyBaseAdapter;
import com.lqwawa.intleducation.base.utils.DisplayUtil;
import com.lqwawa.intleducation.base.utils.StringUtils;
import com.lqwawa.intleducation.module.learn.vo.LiveListVo;
import com.lqwawa.intleducation.module.learn.vo.LiveVo;
import com.osastudio.common.utils.XImageLoader;

import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XChen on 2016/11/8.
 * email:man0fchina@foxmail.com
 */

public class LiveAdapter extends MyBaseAdapter {
    private Activity activity;
    private List<LiveVo> list;
    private LayoutInflater inflater;
    private int img_width;
    private int img_height;
    ImageOptions imageOptions;
    private boolean needPadding = false;

    public LiveAdapter(Activity activity, boolean needPadding) {
        this.activity = activity;
        this.needPadding = needPadding;
        this.inflater = LayoutInflater.from(activity);
        list = new ArrayList<>();

        int p_width = activity.getWindowManager().getDefaultDisplay().getWidth();
        img_width = (p_width - DisplayUtil.dip2px(activity, 60)) / 2;
        img_height = img_width * 9 / 16;

        imageOptions =  XImageLoader.buildImageOptions(ImageView.ScaleType.CENTER_CROP,
                R.drawable.img_def, false, false, null, img_width, img_height);
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
        LiveVo vo = list.get(position);
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = inflater.inflate(R.layout.mod_discovery_course_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
            holder.courseName.setMaxLines(1);
            holder.courseName.setLines(1);
        }
        //当前直播的状态
        int status = vo.getState();
        if (status == 0) {
            holder.show_online_state.setBackgroundResource(R.drawable.live_trailer);
            holder.show_online_state.setText(R.string.live_trailer);
        } else if (status == 1) {
            holder.show_online_state.setBackgroundResource(R.drawable.live_living);
            holder.show_online_state.setText(R.string.live_living);
        } else if (status == 2) {
            holder.show_online_state.setBackgroundResource(R.drawable.live_review);
            holder.show_online_state.setText(R.string.live_review);
        }

        holder.courseName.setText(vo.getTitle());
        holder.organName.setText(vo.getSchoolName());
        holder.organName.setVisibility(View.VISIBLE);
        holder.teacherName.setText(String.format(activity.getResources().getString(R.string.join_count),
                StringUtils.getThousandText(vo.getJoinCount())) + " | "
                + getLiveTypeText(activity, vo.isIsEbanshuLive()));
        String coverUrl = vo.getCoverUrl();
        if (!TextUtils.isEmpty(coverUrl) && coverUrl.contains("coverUrl")) {
            JSONObject jsonObject = JSON.parseObject(coverUrl, Feature.AutoCloseSource);
            coverUrl = jsonObject.getString("coverUrl");
        }
        XImageLoader.loadImage(holder.courseImg,
                coverUrl,
                imageOptions);
        holder.courseImg.setLayoutParams(new FrameLayout.LayoutParams(img_width, img_height));
        if(needPadding) {
            if (position % 2 == 0) {
                holder.courseRoot.setPadding(DisplayUtil.dip2px(activity, 8f), 0, 0, 0);
            } else {
                holder.courseRoot.setPadding(0, 0, DisplayUtil.dip2px(activity, 8f), 0);
            }
        }
        holder.live_type_spl_view.setVisibility(View.GONE);
        holder.live_type_tv.setVisibility(View.GONE);
        //changeLiveTypeFlagStatus(activity, holder.live_type_tv, vo.isIsEbanshuLive(), false);
        return convertView;
    }

    public static String getLiveTypeText(Context context, boolean isEbanshuLive){
        return context.getResources().getString(
                isEbanshuLive ? R.string.live_type_blackboard
                        : R.string.live_type_video);
    }

    public static void changeLiveTypeFlagStatus(Context context,
            TextView view, boolean isEbanshuLive, boolean isDeleted){
        view.setText(
                context.getResources().getString(
                        isEbanshuLive ? R.string.live_type_blackboard
                                : R.string.live_type_video));
        view.setBackground(
                context.getResources().getDrawable(
                        isDeleted ? R.drawable.live_type_deleted :
                                (isEbanshuLive ? R.drawable.live_type_blackboard
                                        : R.drawable.live_type_video)));
        view.setTextColor(
                context.getResources().getColor(
                        isDeleted ? R.color.com_text_light_gray :
                                (isEbanshuLive ? R.color.com_text_yellow_small
                                        : R.color.com_text_blue_small)));
    }


    protected class ViewHolder {
        private LinearLayout itemRoot;
        private LinearLayout courseRoot;
        private ImageView courseImg;
        private TextView organName;
        private TextView courseName;
        private TextView teacherName;
        private TextView show_online_state;
        private TextView live_type_tv;
        private TextView live_type_spl_view;

        public ViewHolder(View view) {
            itemRoot = (LinearLayout) view.findViewById(R.id.item_root);
            courseRoot = (LinearLayout) view.findViewById(R.id.course_root);
            courseImg = (ImageView) view.findViewById(R.id.course_img);
            organName = (TextView) view.findViewById(R.id.organ_name);
            courseName = (TextView) view.findViewById(R.id.course_name);
            teacherName = (TextView) view.findViewById(R.id.teacher_name);
            this.show_online_state = (TextView) view.findViewById(R.id.show_online_state);
            this.live_type_tv = (TextView) view.findViewById(R.id.live_type_tv);
            this.live_type_spl_view = (TextView) view.findViewById(R.id.live_type_spl_view);
        }
    }

    /**
     * 下拉刷新设置数据
     *
     * @param list
     */
    public void setData(List<LiveVo> list) {
        if (list != null) {
            this.list = new ArrayList<>(list);
        } else {
            this.list.clear();
        }
    }

    /**
     * 上拉加载更多，新增数据
     *
     * @param list
     */
    public void addData(List<LiveVo> list) {
        this.list.addAll(list);
    }
}