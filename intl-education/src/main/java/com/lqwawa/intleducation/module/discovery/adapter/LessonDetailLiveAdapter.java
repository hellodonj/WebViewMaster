package com.lqwawa.intleducation.module.discovery.adapter;

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
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.module.learn.vo.LiveVo;

import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 课程节详情直播列表的Adapter
 * @date 2018/04/12 14:53
 * @history v1.0
 * **********************************
 */
public class LessonDetailLiveAdapter extends MyBaseAdapter{

    // 预告
    private static final int STATE_LIVE_TRAILER = 0;
    // 直播中
    private static final int STATE_LIVE_LIVING = 1;
    // 回顾
    private static final int STATE_LIVE_REVIEW = 2;

    private List<LiveVo> listData;
    private LayoutInflater mInflater;
    ImageOptions imageOptions;

    public LessonDetailLiveAdapter() {
        listData = new ArrayList<>();
        mInflater = LayoutInflater.from(UIUtil.getContext());
        imageOptions = new ImageOptions.Builder()
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setCrop(false)
                .setLoadingDrawableId(R.drawable.img_def)//加载中默认显示图片
                .setFailureDrawableId(R.drawable.img_def)//加载失败后默认显示图片
                .build();
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = mInflater.inflate(R.layout.item_course_lesson_detail_live_layout, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        LiveVo vo = listData.get(position);
        //当前直播的状态
        int status = vo.getState();
        switch (status){
            case STATE_LIVE_TRAILER:
                holder.mLiveState.setBackgroundResource(R.drawable.live_trailer);
                holder.mLiveState.setText(R.string.live_trailer);
                break;
            case STATE_LIVE_LIVING:
                holder.mLiveState.setBackgroundResource(R.drawable.live_living);
                holder.mLiveState.setText(R.string.live_living);
                break;
            case STATE_LIVE_REVIEW:
                holder.mLiveState.setBackgroundResource(R.drawable.live_review);
                holder.mLiveState.setText(R.string.live_review);
                break;
        }

        // 设置直播标题
        holder.mLiveName.setText(vo.getTitle());
        // 设置直播时间
        holder.mLiveTime.setText(getCurrentOnlineTime(vo));
        // 设置直播讲师
        holder.mLiveTeacherName.setText(vo.getCreateName());

        String coverUrl = vo.getCoverUrl();
        if (!TextUtils.isEmpty(coverUrl) && coverUrl.contains("coverUrl")) {
            JSONObject jsonObject = JSON.parseObject(coverUrl, Feature.AutoCloseSource);
            coverUrl = jsonObject.getString("coverUrl");
        }
        // 设置课程Logo
        x.image().bind(holder.mCourseAvatar,coverUrl,imageOptions);

        return convertView;
    }

    /**
     * 获取当前直播显示的起止时间
     *
     * @param data
     * @return
     */
    private String getCurrentOnlineTime(LiveVo data) {
        String beginTime = data.getStartTime();
        String endTime = data.getEndTime();
        if (!TextUtils.isEmpty(beginTime)) {
            beginTime = beginTime.substring(0, beginTime.length() - 3);
        }
        if (!TextUtils.isEmpty(endTime)) {
            endTime = endTime.substring(endTime.length() - 8, endTime.length() - 3);
        }
        String showTime = beginTime + " -- " + endTime;
        if (TextUtils.isEmpty(showTime)) {
            return "";
        }
        return showTime;
    }

    /**
     * 下拉刷新设置数据
     *
     * @param list 新的Adapter数据
     */
    public void setData(List<LiveVo> list) {
        if (list != null) {
            listData = new ArrayList<>(list);
        } else {
            listData.clear();
        }
    }

    /**
     * 上拉加载更多，新增数据
     *
     * @param list
     */
    public void addData(List<LiveVo> list) {
        listData.addAll(list);
    }

    /**
     * 课程节显示直播列表Item的ViewHolder
     */
    private static class ViewHolder {
        // 课程Logo
        private ImageView mCourseAvatar;
        // 直播状态
        private TextView mLiveState;
        // 直播标题
        private TextView mLiveName;
        // 直播老师姓名
        private TextView mLiveTeacherName;
        // 直播时间
        private TextView mLiveTime;

        public ViewHolder(View view) {
            mCourseAvatar = (ImageView) view.findViewById(R.id.iv_course);
            mLiveState = (TextView) view.findViewById(R.id.tv_live_state);
            mLiveName = (TextView) view.findViewById(R.id.tv_live_name);
            mLiveTeacherName = (TextView) view.findViewById(R.id.tv_live_teacher);
            mLiveTime = (TextView) view.findViewById(R.id.tv_live_time);
        }
    }
}
