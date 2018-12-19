package com.lqwawa.intleducation.module.learn.adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.StringUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.module.discovery.adapter.LiveAdapter;
import com.lqwawa.intleducation.module.learn.vo.EmceeListVo;
import com.lqwawa.intleducation.module.learn.vo.LiveListVo;
import com.lqwawa.intleducation.module.learn.vo.LiveVo;

import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * ================================================
 * 作    者：Blizzard-liu
 * 版    本：1.0
 * 创建日期：2017/8/11 11:17
 * 描    述：直播
 * 修订历史：V5.5 所有的直播列表 直播时间都是居中显示
 * ================================================
 */

public class LiveRoomAdapter extends MyBaseAdapter {
    private  ImageOptions imageOptions;
    private Activity activity;
    private List<LiveVo> list;
    private LayoutInflater inflater;
    // 是否显示来源
    private boolean showSource;
    private boolean showTopBar;
    private int dp8;

    public LiveRoomAdapter(Activity activity, boolean showTopBar) {
        this.activity = activity;
        this.showTopBar = showTopBar;
        this.inflater = LayoutInflater.from(activity);
        list = new ArrayList<>();
        imageOptions = new ImageOptions.Builder()
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setCrop(false)
                .setLoadingDrawableId(R.drawable.live_list_default)//加载中默认显示图片
                .setFailureDrawableId(R.drawable.live_list_default)//加载失败后默认显示图片
                .build();
        dp8 = DisplayUtil.dip2px(activity, 8);
    }

    public void setShowSource(boolean showSource){
        this.showSource = showSource;
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
        LiveVo data = list.get(position);
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = inflater.inflate(R.layout.live_resource_item, null);
            holder = new ViewHolder(convertView);
            if(!showTopBar){
                holder.root_layout.setPadding(dp8, 0, dp8, dp8);
            }
            convertView.setTag(holder);
        }

        String coverUrl = data.getCoverUrl();
        if (!TextUtils.isEmpty(coverUrl) && coverUrl.contains("coverUrl")) {
            JSONObject jsonObject = JSON.parseObject(coverUrl, Feature.AutoCloseSource);
            coverUrl = jsonObject.getString("coverUrl");
        }

        // 直接用coverUrl
        coverUrl = data.getCoverUrlSrc();
        x.image().bind(holder.resource_thumbnail,
                coverUrl,
                imageOptions);
        //直播时间 以区间的形式来显示
        holder.online_time.setText(getCurrentOnlineTime(data));
        //直播的标题
        holder.resource_title.setText(data.getTitle());
        //当前直播的状态
        int status = data.getState();
        if(data.isIsDelete()){
            holder.show_online_state.setBackgroundResource(R.drawable.live_invalid);
            holder.show_online_state.setText(R.string.live_invalid);
            holder.resource_title.setTextColor(activity.getResources().getColor(R.color.com_text_light_gray));
            holder.organ_name_tv.setTextColor(activity.getResources().getColor(R.color.com_text_light_gray));
            holder.show_online_author.setTextColor(activity.getResources().getColor(R.color.com_text_light_gray));
            holder.live_join_count_tv.setTextColor(activity.getResources().getColor(R.color.com_text_light_gray));
            holder.price_tv.setTextColor(activity.getResources().getColor(R.color.com_text_light_gray));
        }else {
            holder.resource_title.setTextColor(activity.getResources().getColor(R.color.com_text_black));
            holder.organ_name_tv.setTextColor(activity.getResources().getColor(R.color.com_text_gray));
            holder.show_online_author.setTextColor(activity.getResources().getColor(R.color.com_text_gray));
            holder.live_join_count_tv.setTextColor(activity.getResources().getColor(R.color.com_text_gray));
            holder.price_tv.setTextColor(activity.getResources().getColor(R.color.com_text_red));
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
        }

        if(showSource){
            // 课程表的直播显示老师
            List<EmceeListVo> emceeList = data.getEmceeList();
            if(EmptyUtil.isNotEmpty(emceeList)){
                ListIterator<EmceeListVo> it = emceeList.listIterator();
                StringBuilder sb = new StringBuilder();
                while (it.hasNext()){
                    String realName = it.next().getRealName();
                    if(EmptyUtil.isEmpty(realName)) realName = "";
                    sb.append(realName);
                    if(it.hasNext()){
                        sb.append(",");
                    }
                }

                StringUtil.fillSafeTextView(holder.organ_name_tv,sb.toString());
            }else{
                StringUtil.fillSafeTextView(holder.organ_name_tv,"");
            }

            // 直播的主持人
            // holder.show_online_author.setText(data.getEmceeNames());
            if(!showSource){
                holder.show_online_author.setText(data.isFromMooc() ? data.getSchoolName() : data.getClassName());
            }else{
                String showName = data.isFromMooc() ? data.getSchoolName() : data.getClassName();
                holder.show_online_author.setText(String.format(UIUtil.getString(R.string.label_live_source),showName));
            }
        }else{
            //直播的主持人
            holder.show_online_author.setText(data.getEmceeNames());
            if(!showSource){
                holder.organ_name_tv.setText(data.isFromMooc() ? data.getSchoolName() : data.getClassName());
            }else{
                String showName = data.isFromMooc() ? data.getSchoolName() : data.getClassName();
                holder.organ_name_tv.setText(String.format(UIUtil.getString(R.string.label_live_source),showName));
            }
        }


        if(data.isFromMooc()) {
            holder.price_tv.setVisibility(View.VISIBLE);
            holder.price_tv.setText(data.getPayType() == 0 ?
                    activity.getResources().getString(R.string.free)
                    : "¥" + data.getPrice());
        }else{
            holder.price_tv.setVisibility(View.GONE);
        }

        if(data.isFromMooc()) {
            holder.live_join_count_tv.setText(
                    new StringBuilder()
                            .append(String.format(activity.getResources().getString(R.string.join_count),
                                    StringUtils.getThousandText(data.getJoinCount())))
                            .append(" | ")
                            .append(LiveAdapter.getLiveTypeText(
                                    activity, data.isIsEbanshuLive())).toString());
        }else{
            StringBuilder flagStringBuilder = new StringBuilder();
            if(status == 1){//尽在直播状态才显示在线人数
                flagStringBuilder.append(String.format(activity.getResources().getString(R.string.some_user_online),
                                StringUtils.getThousandText(data.getOnlineNum())))
                        .append(" | ");
            }
            holder.live_join_count_tv.setText(flagStringBuilder
                            .append(String.format(activity.getResources().getString(R.string.some_browse),
                                    StringUtils.getThousandText(data.getBrowseCount())))
                            .append(" | ")
                            .append(LiveAdapter.getLiveTypeText(
                                    activity, data.isIsEbanshuLive())).toString());
        }

        return convertView;
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

    public static class ViewHolder {
        public View rootView;
        public ImageView resource_thumbnail;
        public TextView online_time;
//        public ImageView resource_indicator;
//        public ImageView resource_delete;
//        public TextView resource_type;
        public TextView resource_title;
        public TextView show_online_state;
        //public TextView show_online_count;
        public TextView show_online_author;
//        public TextView resource_time;
//        public TextView resource_tips;
        public TextView organ_name_tv;
        public TextView price_tv;
        private TextView live_type_tv;
        private TextView live_join_count_tv;
        private LinearLayout root_layout;

        public ViewHolder(View rootView) {
            this.rootView = rootView;
            this.resource_thumbnail = (ImageView) rootView.findViewById(R.id.resource_thumbnail);
            this.online_time = (TextView) rootView.findViewById(R.id.online_time);
//            this.resource_indicator = (ImageView) rootView.findViewById(R.id.resource_indicator);
//            this.resource_delete = (ImageView) rootView.findViewById(R.id.resource_delete);
//            this.resource_type = (TextView) rootView.findViewById(R.id.resource_type);
            this.resource_title = (TextView) rootView.findViewById(R.id.resource_title);
            this.show_online_state = (TextView) rootView.findViewById(R.id.show_online_state);
//            this.show_online_count = (TextView) rootView.findViewById(R.id.show_online_count);
            this.show_online_author = (TextView) rootView.findViewById(R.id.show_online_author);
//            this.resource_time = (TextView) rootView.findViewById(R.id.resource_time);
//            this.resource_tips = (TextView) rootView.findViewById(R.id.resource_tips);
            this.organ_name_tv = (TextView) rootView.findViewById(R.id.organ_name_tv);
            this.price_tv = (TextView) rootView.findViewById(R.id.price_tv);
            this.live_type_tv = (TextView) rootView.findViewById(R.id.live_type_tv);
            this.live_join_count_tv = (TextView) rootView.findViewById(R.id.live_join_count_tv);
            this.root_layout = (LinearLayout) rootView.findViewById(R.id.root_layout);
        }

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
     * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指
     * 定精度，以后的数字四舍五入。
     * @param v1 被除数
     * @param v2 除数
     * @param scale 表示表示需要精确到小数点以后几位。
     * @return 两个参数的商
     */
    public double div(double v1, double v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException(
                    "The scale must be a positive integer or zero");
        }
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }
}
