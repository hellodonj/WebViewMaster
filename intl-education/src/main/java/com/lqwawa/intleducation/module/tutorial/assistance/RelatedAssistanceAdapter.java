package com.lqwawa.intleducation.module.tutorial.assistance;

import android.app.Activity;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.common.Common;
import com.lqwawa.intleducation.common.utils.StringUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;
import com.osastudio.common.utils.XImageLoader;

import org.xutils.image.ImageOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述: 三习教案的帮辅适配器
 * 作者|时间: djj on 2019/7/19 0019 上午 11:50
 */

public class RelatedAssistanceAdapter extends RecyclerView.Adapter<RelatedAssistanceAdapter.ViewHolder> {

    private Activity activity;
    private LayoutInflater inflater;
    private List<CourseVo> list;
    private int img_width;
    private int img_height;
    ImageOptions imageOptions;

    private static final int[] courseStatusResId = new int[]{
            R.string.course_status_0,
            R.string.course_status_1,
            R.string.course_status_2};

    private final int[] courseTypesBgId = new int[]{
            R.drawable.shape_course_type_read,
            R.drawable.shape_course_type_learn,
            R.drawable.shape_course_type_practice,
            R.drawable.shape_course_type_exam,
            R.drawable.shape_course_type_video,
            R.drawable.shape_course_type_lesson
    };

    private String[] courseTypeNames;

    public RelatedAssistanceAdapter(Activity activity) {
        this.activity = activity;
        this.inflater = LayoutInflater.from(activity);
        list = new ArrayList<CourseVo>();

        int p_width = activity.getWindowManager().getDefaultDisplay().getWidth();
        img_width = p_width / 4;
        img_height = img_width * 297 / 210;

        imageOptions = XImageLoader.buildImageOptions(ImageView.ScaleType.CENTER_CROP,
                R.drawable.default_cover_h, false, false, null);
        courseTypeNames = activity.getResources().getStringArray(R.array.course_type_names);
    }

//    public RelatedAssistanceAdapter(boolean tutorialMode, @NonNull @AuditType.AuditTypeRes int type, Activity activity) {
//        this(activity);
//        this.tutorialMode = tutorialMode;
//        this.type = type;
//    }

//    public RelatedAssistanceAdapter(Activity activity, boolean isClassCourseEnter) {
//        this(activity);
//        this.isClassCourseEnter = isClassCourseEnter;
//    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.mod_course_list_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CourseVo vo = list.get(position);
        // 设置选中未选中
        holder.cbSelect.setChecked(vo.isTag());
//        if (isClassCourseEnter) {
//            holder.cbSelect.setVisibility(View.VISIBLE);
//        }

        holder.courseName.setText(vo.getName() == null ? vo.getCourseName() : vo.getName());
        holder.organName.setText(vo.getOrganName());
        holder.teacherName.setText(vo.getTeachersName());
        int courseStatus = vo.getProgressStatus();
        String statusString;
        if (courseStatus >= 0 && courseStatus < 3) {
            statusString = activity.getString(courseStatusResId[courseStatus]);
            if (courseStatus == 0) {
                holder.courseStatus.setBackgroundResource(R.drawable.radio_bg_pink);
            } else if (courseStatus == 1) {
                holder.courseStatus.setBackgroundResource(R.drawable.radio_bg_flag_red);
            } else {
                holder.courseStatus.setBackgroundResource(R.drawable.radio_bg_sky_blue);
            }
        } else {
            statusString = activity.getString(R.string.value_error) + ":" + courseStatus;
        }
        holder.courseStatus.setText(statusString);

        holder.courseStatus.setVisibility(View.GONE);
        int courseType = vo.getAssortment();
        if (courseType >= 0 && courseType < courseTypesBgId.length) {
            holder.courseType.setText(courseTypeNames[courseType]);
            holder.courseType.setBackgroundResource(courseTypesBgId[courseType]);
        }
        if (vo.getPrice() == 0) {
            // 免费
            holder.mOrginalPriceLayout.setVisibility(View.GONE);
            holder.mPriceLayout.setVisibility(View.VISIBLE);
            StringUtil.fillSafeTextView(holder.coursePrice, UIUtil.getString(R.string.label_class_gratis));
        } else {
            // 收费
            StringUtil.fillSafeTextView(holder.coursePrice, Common.Constance.MOOC_MONEY_MARK + vo.getPrice());
            if (vo.isDiscount()) {
                // 有打折价
                holder.mOrginalPriceLayout.setVisibility(View.VISIBLE);
                holder.mOrginalPrice.setVisibility(View.VISIBLE);
                // 中划线
                holder.mOrginalPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                StringUtil.fillSafeTextView(holder.mOrginalPrice, Common.Constance.MOOC_MONEY_MARK + vo.getOriginalPrice());
            } else {
                // 无打折价
                holder.mOrginalPriceLayout.setVisibility(View.GONE);
                holder.mOrginalPrice.setVisibility(View.GONE);
            }
        }
        XImageLoader.loadImage(holder.courseIv,
                vo.getThumbnailUrl().trim(),
                imageOptions);
        holder.coverLay.setLayoutParams(new LinearLayout.LayoutParams(img_width, img_height));
        holder.mBodyLayout.setVisibility(View.VISIBLE);
        holder.mBottomLayout.setVisibility(View.GONE);

        // 如果设置了回调，则设置点击事件
        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position1 = holder.getLayoutPosition();
                    mOnItemClickListener.onItemClick(holder.itemView, position1);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private FrameLayout coverLay;
        private ImageView courseIv;
        private TextView courseType;
        private TextView courseName;
        private TextView organName;
        private TextView teacherName;
        private TextView courseStatus;
        private LinearLayout mOrginalPriceLayout;
        private TextView mOrginalTitle;
        private TextView mOrginalPrice;
        private LinearLayout mPriceLayout;
        private TextView priceTitleTv;
        private TextView coursePrice;
        private LinearLayout mBodyLayout;
        private FrameLayout mBottomLayout;
        private TextView course_date_tv;
        private TextView tvType;
        private CheckBox cbSelect;

        public ViewHolder(View itemView) {
            super(itemView);
            coverLay = (FrameLayout) itemView.findViewById(R.id.cover_lay);
            courseIv = (ImageView) itemView.findViewById(R.id.course_iv);
            courseType = (TextView) itemView.findViewById(R.id.course_type);
            courseName = (TextView) itemView.findViewById(R.id.course_name);
            organName = (TextView) itemView.findViewById(R.id.organ_name);
            teacherName = (TextView) itemView.findViewById(R.id.teacher_name);
            courseStatus = (TextView) itemView.findViewById(R.id.course_status);
            mOrginalPriceLayout = (LinearLayout) itemView.findViewById(R.id.original_price_layout);
            mOrginalTitle = (TextView) itemView.findViewById(R.id.tv_original_desc);
            mOrginalPrice = (TextView) itemView.findViewById(R.id.tv_original_price);
            mPriceLayout = (LinearLayout) itemView.findViewById(R.id.price_layout);
            priceTitleTv = (TextView) itemView.findViewById(R.id.price_title_tv);
            coursePrice = (TextView) itemView.findViewById(R.id.course_price);
            mBodyLayout = (LinearLayout) itemView.findViewById(R.id.body_layout);
            mBottomLayout = (FrameLayout) itemView.findViewById(R.id.bottom_layout);
            course_date_tv = (TextView) itemView.findViewById(R.id.course_date_tv);
            tvType = (TextView) itemView.findViewById(R.id.tv_type);
            cbSelect = (CheckBox) itemView.findViewById(R.id.cb_select);
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
     *
     * @return 集合
     */
    public List<CourseVo> getItems() {
        return list;
    }

    /**
     * 获取实体
     * @param position
     * @return
     */
    public CourseVo getItem(int position) {
        return list.get(position);
    }

}
