package com.lqwawa.intleducation.module.learn.adapter;

import android.app.Activity;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ui.MyBaseAdapter;
import com.lqwawa.intleducation.base.utils.DateUtils;
import com.lqwawa.intleducation.base.utils.DisplayUtil;
import com.lqwawa.intleducation.base.utils.StringUtils;
import com.lqwawa.intleducation.module.learn.vo.MyCourseChapterVo;
import com.lqwawa.intleducation.module.learn.vo.MyCredentialCourseListVo;
import com.osastudio.common.utils.XImageLoader;

import org.xutils.image.ImageOptions;
import org.xutils.x;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by XChen on 2016/11/28.
 * email:man0fchina@foxmail.com
 */
@Deprecated
public class MyCredentialCourseListAdapter extends MyBaseAdapter {
    private Activity activity;
    private List<MyCredentialCourseListVo> list;
    private LayoutInflater inflater;
    private static final int[] courseStatusResId = new int[]{
            R.string.course_status_0,
            R.string.course_status_1,
            R.string.course_status_2};

    public MyCredentialCourseListAdapter(Activity activity) {
        this.activity = activity;
        this.inflater = LayoutInflater.from(activity);
        list = new ArrayList<MyCredentialCourseListVo>();
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
        // @date   :2018/4/17 0017 上午 9:54
        // @func   :V5.5弃用
        /*ViewHolder holder;
        MyCredentialCourseListVo vo = list.get(position);
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = inflater.inflate(R.layout.mod_learn_course_list_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        *//*holder.course_date_tv.setText(DateUtils.getFormatByStringDate("" + vo.getCourse().getCreateTime(),
                DateUtils.YYYYMMDDCH));*//*
        holder.course_date_tv.setVisibility(View.GONE);
        holder.course_name.setText(vo.getCourse().getName());
        holder.organ_name.setText(vo.getCourse().getOrganName());
        holder.teacher_name.setText(vo.getCourse().getTeachersName());
        Resources resources = activity.getResources();
        *//*holder.course_process_tv.setText(resources.getString(R.string.progress_on)
                + vo.getCourse().getProgress()
                + vo.getChapterName() + ","
                + resources.getString(R.string.week_all)
                + vo.getCourse().getWeekCount() + vo.getChapterName());*//*
        holder.course_process_tv.setText(resources.getString(R.string.week_all)
                + vo.getCourse().getWeekCount() + vo.getChapterName());
        XImageLoader.loadImage(holder.course_iv,
                ("" + vo.getCourse().getThumbnailUrl()).trim(),
                imageOptions);
        holder.course_iv.setLayoutParams(new LinearLayout.LayoutParams(img_width, img_height));
        holder.learn_process_lay.removeAllViews();
        if (vo.isIsAdd() || vo.isIsBuy()) {
            holder.learn_process_lay.setVisibility(View.VISIBLE);
            if (vo.getChapters() != null && vo.getChapters().size() > 0) {
                for (int i = 0; i < vo.getChapters().size() && i < 4; i++) {
                    addWeek(vo, holder, i);
                }

                if (vo.getChapters().size() > 5) {
                    TextView textView = new TextView(activity);
                    textView.setText("…");
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                            activity.getResources().getDimensionPixelSize(R.dimen.com_font_size_3));
                    holder.learn_process_lay.addView(textView);
                    addWeek(vo, holder, vo.getChapters().size() - 1);
                } else if (vo.getChapters().size() == 5) {
                    addWeek(vo, holder, 4);
                }
            }
        }else{
            holder.learn_process_lay.setVisibility(View.INVISIBLE);
        }

        if (vo.getCourse().getPrice() > 0){
            if (vo.isIsBuy()){
                holder.price_lay.setVisibility(View.GONE);
                holder.course_date_tv.setVisibility(View.VISIBLE);
                holder.course_date_tv.setText(
                        DateUtils.getFormatByStringDate("" + vo.getCourse().getCreateTime(),
                        DateUtils.YYYYMMDD));
            }else{
                holder.price_lay.setVisibility(View.VISIBLE);
                holder.course_date_tv.setVisibility(View.GONE);
                holder.price_title_tv.setVisibility(View.VISIBLE);
                holder.course_price.setText("￥" + vo.getCourse().getPrice());
            }
        }else{
            if (vo.isIsAdd()){
                holder.price_lay.setVisibility(View.GONE);
                holder.course_date_tv.setVisibility(View.VISIBLE);
                holder.course_date_tv.setText(
                        DateUtils.getFormatByStringDate("" + vo.getCourse().getCreateTime(),
                                DateUtils.YYYYMMDD));
            }else{
                holder.price_lay.setVisibility(View.VISIBLE);
                holder.course_date_tv.setVisibility(View.GONE);
                holder.price_title_tv.setVisibility(View.GONE);
                holder.course_price.setText(activity.getResources().getString(R.string.free));
            }
        }*/

        return convertView;
    }

    private void addWeek(MyCredentialCourseListVo vo, ViewHolder holder, int week){
        MyCourseChapterVo chapterVo = vo.getChapters().get(week);
        TextView textView = new TextView(activity);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                activity.getResources().getDimensionPixelSize(R.dimen.com_font_size_3));
        if (chapterVo.getStatus() == 0){
            textView.setBackgroundColor(activity.getResources().getColor(R.color.com_bg_trans_gray));
        }else if(chapterVo.getStatus() == 5){
            textView.setBackgroundColor(activity.getResources().getColor(R.color.com_bg_dark_pink));
        }else if(chapterVo.getStatus() == 10){
            textView.setBackgroundColor(activity.getResources().getColor(R.color.com_bg_green));
        }
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(DisplayUtil.dip2px(activity, 1), 0,
                DisplayUtil.dip2px(activity, 1), 0);
        textView.setLayoutParams(layoutParams);
        textView.setPadding(DisplayUtil.dip2px(activity, 2),
                DisplayUtil.dip2px(activity, 1),
                DisplayUtil.dip2px(activity, 2),
                DisplayUtil.dip2px(activity, 1));
        textView.setSingleLine();
        textView.setText(StringUtils.getChapterNumString(activity, chapterVo.getChapterName(),
                chapterVo.getWeekNum()));
        holder.learn_process_lay.addView(textView);
    }


    private class ViewHolder {
        TextView course_date_tv;
        ImageView course_iv;
        TextView course_name;
        TextView organ_name;
        TextView teacher_name;
        TextView course_process_tv;
        LinearLayout learn_process_lay;
        LinearLayout price_lay;
        TextView price_title_tv;
        TextView course_price;
        public ViewHolder(View parentView) {
            // @date   :2018/4/17 0017 上午 10:01
            // @func   :V5.5弃用
            /*course_date_tv = (TextView)parentView.findViewById(R.id.course_date_tv);
            course_iv = (ImageView)parentView.findViewById(R.id.course_iv);
            course_name = (TextView)parentView.findViewById(R.id.course_name);
            organ_name = (TextView)parentView.findViewById(R.id.organ_name);
            teacher_name = (TextView)parentView.findViewById(R.id.teacher_name);
            course_process_tv = (TextView)parentView.findViewById(R.id.course_process_tv);
            learn_process_lay = (LinearLayout)parentView.findViewById(R.id.learn_process_lay);
            price_lay = (LinearLayout)parentView.findViewById(R.id.price_lay);
            price_title_tv = (TextView)parentView.findViewById(R.id.price_title_tv);
            course_price = (TextView)parentView.findViewById(R.id.course_price);*/
        }
    }

    /**
     * 下拉刷新设置数据
     *
     * @param list
     */
    public void setData(List<MyCredentialCourseListVo> list) {
        if (list != null) {
            this.list = new ArrayList<MyCredentialCourseListVo>(list);
        } else {
            this.list.clear();
        }
    }

    /**
     * 上拉加载更多，新增数据
     *
     * @param list
     */
    public void addData(List<MyCredentialCourseListVo> list) {
        this.list.addAll(list);
    }
}
