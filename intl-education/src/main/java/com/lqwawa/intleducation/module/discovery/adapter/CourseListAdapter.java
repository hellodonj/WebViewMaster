package com.lqwawa.intleducation.module.discovery.adapter;

import android.app.Activity;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ui.MyBaseAdapter;
import com.lqwawa.intleducation.common.Common;
import com.lqwawa.intleducation.common.utils.StringUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;
import org.xutils.image.ImageOptions;
import org.xutils.x;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by XChen on 2016/11/12.
 * email:man0fchina@foxmail.com
 */

public class CourseListAdapter extends MyBaseAdapter {
    private Activity activity;
    private List<CourseVo> list;
    private LayoutInflater inflater;
    private int img_width;
    private int img_height;
    ImageOptions imageOptions;

    // 是否是班级学程入口，用来选择课程的
    private boolean isClassCourseEnter;

    private static final int[] courseStatusResId = new int[]{
            R.string.course_status_0,
            R.string.course_status_1,
            R.string.course_status_2};

    public CourseListAdapter(Activity activity) {
        this.activity = activity;
        this.inflater = LayoutInflater.from(activity);
        list = new ArrayList<CourseVo>();

        int p_width = activity.getWindowManager().getDefaultDisplay().getWidth();
        img_width = p_width / 4;
        img_height = img_width * 297 / 210;

        imageOptions = new ImageOptions.Builder()
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setCrop(false)
                //.setSize(img_width,img_height)
                .setLoadingDrawableId(R.drawable.default_cover_h)//加载中默认显示图片
                .setFailureDrawableId(R.drawable.default_cover_h)//加载失败后默认显示图片
                .build();
    }

    public CourseListAdapter(Activity activity,boolean isClassCourseEnter) {
        this(activity);
        this.isClassCourseEnter = isClassCourseEnter;
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
        CourseVo vo = list.get(position);
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = inflater.inflate(R.layout.mod_course_list_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }


        // 设置选中未选中
        holder.cbSelect.setChecked(vo.isTag());
        if(isClassCourseEnter){
            holder.cbSelect.setVisibility(View.VISIBLE);
        }

        holder.courseName.setText(vo.getName() == null ? vo.getCourseName() : vo.getName());
        holder.organName.setText(vo.getOrganName());
        holder.teacherName.setText(vo.getTeachersName());
        int courseStatus = vo.getProgressStatus();
        String statusString;
        if (courseStatus >= 0 && courseStatus < 3){
            statusString = activity.getString(courseStatusResId[courseStatus]);
            if(courseStatus == 0){
                holder.courseStatus.setBackgroundResource(R.drawable.radio_bg_pink);
            }else if(courseStatus == 1){
                holder.courseStatus.setBackgroundResource(R.drawable.radio_bg_flag_red);
            }else{
                holder.courseStatus.setBackgroundResource(R.drawable.radio_bg_sky_blue);
            }
        }else{
            statusString = activity.getString(R.string.value_error) + ":" + courseStatus;
        }
        holder.courseStatus.setText(statusString);
        /*if (vo.getPrice() > 0) {
            holder.priceTitleTv.setVisibility(View.VISIBLE);

            holder.coursePrice.setText("¥" + vo.getPrice());

            holder.coursePrice.setText(Common.Constance.MOOC_MONEY_MARK + vo.getPrice());

        }else{
            holder.priceTitleTv.setVisibility(View.GONE);
            holder.coursePrice.setText(activity.getResources().getString(R.string.free));
        }*/
        if(vo.getPrice() == 0){
            // 免费
            holder.mOrginalPriceLayout.setVisibility(View.GONE);
            holder.mPriceLayout.setVisibility(View.VISIBLE);
            StringUtil.fillSafeTextView(holder.coursePrice,UIUtil.getString(R.string.label_class_gratis));
        }else{
            // 收费
            StringUtil.fillSafeTextView(holder.coursePrice,Common.Constance.MOOC_MONEY_MARK + vo.getPrice());
            if(vo.isDiscount()){
                // 有打折价
                holder.mOrginalPriceLayout.setVisibility(View.VISIBLE);
                holder.mOrginalPrice.setVisibility(View.VISIBLE);
                // 中划线
                holder.mOrginalPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                StringUtil.fillSafeTextView(holder.mOrginalPrice,Common.Constance.MOOC_MONEY_MARK + vo.getOriginalPrice());
            }else{
                // 无打折价
                holder.mOrginalPriceLayout.setVisibility(View.GONE);
                holder.mOrginalPrice.setVisibility(View.GONE);
            }
        }

        x.image().bind(holder.courseIv,
                vo.getThumbnailUrl().trim(),
                imageOptions);
        holder.coverLay.setLayoutParams(new LinearLayout.LayoutParams(img_width, img_height));
        return convertView;
    }

    private class ViewHolder {
        FrameLayout coverLay;
        ImageView courseIv;
        TextView courseName;
        TextView organName;
        TextView teacherName;
        TextView courseStatus;
        LinearLayout mOrginalPriceLayout;
        TextView mOrginalTitle;
        TextView mOrginalPrice;
        LinearLayout mPriceLayout;
        TextView priceTitleTv;
        TextView coursePrice;

        CheckBox cbSelect;
        
        public ViewHolder(View parent) {
            coverLay = (FrameLayout) parent.findViewById(R.id.cover_lay);
            courseIv = (ImageView) parent.findViewById(R.id.course_iv);
            courseName = (TextView) parent.findViewById(R.id.course_name);
            organName = (TextView) parent.findViewById(R.id.organ_name);
            teacherName = (TextView) parent.findViewById(R.id.teacher_name);
            courseStatus = (TextView) parent.findViewById(R.id.course_status);

            mOrginalPriceLayout  = (LinearLayout) parent.findViewById(R.id.original_price_layout);
            mOrginalTitle = (TextView) parent.findViewById(R.id.tv_original_desc);
            mOrginalPrice = (TextView) parent.findViewById(R.id.tv_original_price);
            mPriceLayout = (LinearLayout) parent.findViewById(R.id.price_layout);
            priceTitleTv = (TextView) parent.findViewById(R.id.price_title_tv);
            coursePrice = (TextView) parent.findViewById(R.id.course_price);
            cbSelect = (CheckBox) parent.findViewById(R.id.cb_select);
        }

    }

    /**
     * 下拉刷新设置数据
     *
     * @param list
     */
    public void setData(List<CourseVo> list) {
        if (list != null) {
            this.list = new ArrayList<CourseVo>(list);
        } else {
            this.list.clear();
        }
    }

    /**
     * 上拉加载更多，新增数据
     *
     * @param list
     */
    public void addData(List<CourseVo> list) {
        this.list.addAll(list);
    }

    /**
     * 返回显示cell的集合
     * @return 集合
     */
    public List<CourseVo> getItems(){
        return list;
    }
}
