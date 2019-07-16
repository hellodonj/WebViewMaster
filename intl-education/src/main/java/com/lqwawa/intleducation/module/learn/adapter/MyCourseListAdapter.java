package com.lqwawa.intleducation.module.learn.adapter;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ui.MyBaseAdapter;
import com.lqwawa.intleducation.base.utils.DateUtils;
import com.lqwawa.intleducation.base.utils.DisplayUtil;
import com.lqwawa.intleducation.base.utils.StringUtils;
import com.lqwawa.intleducation.base.widgets.PullRefreshView.NoScrollGridView;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.CourseRateEntity;
import com.lqwawa.intleducation.factory.helper.CourseHelper;
import com.lqwawa.intleducation.module.learn.vo.MyCourseChapterVo;
import com.lqwawa.intleducation.module.learn.vo.MyCourseVo;

import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XChen on 2016/11/28.
 * email:man0fchina@foxmail.com
 */

public class MyCourseListAdapter extends MyBaseAdapter {
    private Activity activity;
    private List<MyCourseVo> list;
    private LayoutInflater inflater;
    private int img_width;
    private int img_height;
    ImageOptions imageOptions;


    private boolean isTeacher;
    private boolean canEdit;
    private String curMemberId;

    /**
     * 传入身份信息，孩子的学程加载课程进度信息需要
     *
     * @param canEdit
     * @param curMemberId
     */
    public void setRoleInfo(boolean canEdit, @NonNull String curMemberId) {
        this.canEdit = canEdit;
        this.curMemberId = curMemberId;
    }

    private static final int[] courseStatusResId = new int[]{
            R.string.course_status_0,
            R.string.course_status_1,
            R.string.course_status_2};

    private final int[] courseTypesBgId = new int[]{
            R.drawable.shape_course_type_read,
            R.drawable.shape_course_type_learn,
            R.drawable.shape_course_type_practice,
            R.drawable.shape_course_type_exam,
            R.drawable.shape_course_type_video
    };

    private String[] courseTypeNames;

    public MyCourseListAdapter(Activity activity, boolean isTeacher) {
        this(activity);
        this.isTeacher = isTeacher;
    }

    public MyCourseListAdapter(Activity activity) {
        this.activity = activity;
        this.inflater = LayoutInflater.from(activity);
        list = new ArrayList<MyCourseVo>();

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

        courseTypeNames =
                activity.getResources().getStringArray(R.array.course_type_names);

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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        final MyCourseVo vo = list.get(position);
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = inflater.inflate(R.layout.mod_learn_course_list_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        holder.course_date_tv.setText(DateUtils.getFormatByStringDate(vo.getCreateTime(),
                DateUtils.YYYYMMDDCH));
        holder.course_name.setText(vo.getCourseName());
        holder.organ_name.setText(vo.getOrganName());
        holder.teacher_name.setText(vo.getTeachersName());
        holder.mTvInClass.setActivated(vo.isInClass());
        if (!isTeacher && canEdit) {
            // 学生身份才显示
            holder.mTvInClass.setVisibility(View.VISIBLE);
        } else {
            holder.mTvInClass.setVisibility(View.GONE);
        }

        if (vo.isInClass()) {
            // 已经指定到班级
            holder.mTvInClass.setText(UIUtil.getString(R.string.label_old_in_class));
            holder.mTvInClass.setEnabled(false);
        } else {
            // 未指定到班级
            holder.mTvInClass.setText(UIUtil.getString(R.string.label_course_in_class));
            holder.mTvInClass.setEnabled(true);
            holder.mTvInClass.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 去指定到班级
                    Intent intent = new Intent();
                    intent.setClassName(activity.getPackageName(), "com.lqwawa.mooc.select.SchoolClassSelectActivity");
                    Bundle bundle = new Bundle();
                    bundle.putString("courseId", vo.getCourseId());
                    intent.putExtras(bundle);
                    activity.startActivity(intent);
                }
            });
        }
        Resources resources = activity.getResources();
        /*holder.course_process_tv.setText(resources.getString(R.string.progress_on)
                + vo.getProgress()
                + vo.getChapterName() + ","
                + resources.getString(R.string.week_all)
                + vo.getWeekCount() + vo.getChapterName());*/
        // holder.course_process_tv.setText(activity.getResources().getString(R.string.week_all));
        SpannableStringBuilder builder = new SpannableStringBuilder("" + vo.getWeekCount());
        ForegroundColorSpan greenSpan = new ForegroundColorSpan(
                activity.getResources().getColor(R.color.com_text_green));
        builder.setSpan(greenSpan, 0, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        // holder.course_process_tv.append(builder);
        // holder.course_process_tv.append(vo.getChapterName());

        x.image().bind(holder.course_iv,
                ("" + vo.getThumbnailUrl()).trim(),
                imageOptions);
        holder.coverLay.setLayoutParams(new LinearLayout.LayoutParams(img_width, img_height));

        if (!isTeacher /*&& canEdit*/ && vo.isCharge()) {
            // 学生 家长
            holder.mBuyType.setVisibility(View.VISIBLE);
            if (vo.isBuyAll()) {
                // 全部购买
                holder.mBuyType.setText(R.string.label_buy_all);
            } else {
                holder.mBuyType.setText(String.format(UIUtil.getString(R.string.label_buy_number_chapter), vo.getBuyChapterNum()));
            }
        } else {
            holder.mBuyType.setVisibility(View.GONE);
        }

        int courseStatus = vo.getProgressStatus();
        String statusString;
        if (courseStatus >= 0 && courseStatus < 3) {
            statusString = activity.getString(courseStatusResId[courseStatus]);
            if (courseStatus == 0) {
                holder.mTvState.setBackgroundResource(R.drawable.radio_bg_pink);
            } else if (courseStatus == 1) {
                holder.mTvState.setBackgroundResource(R.drawable.radio_bg_flag_red);
            } else {
                holder.mTvState.setBackgroundResource(R.drawable.radio_bg_sky_blue);
            }
        } else {
            statusString = activity.getString(R.string.value_error) + ":" + courseStatus;
        }
        holder.mTvState.setText(statusString);

        // 设置状态显示与隐藏
        if (isTeacher || true) {
            // 所有角色都显示状态
            holder.mTvState.setVisibility(View.VISIBLE);
        } else {
            holder.mTvState.setVisibility(View.GONE);
        }

        holder.mTvState.setVisibility(View.GONE);
        int courseType = vo.getAssortment();
        if (courseType >= 0 && courseType < courseTypesBgId.length) {
            holder.mCourseType.setText(courseTypeNames[courseType]);
            holder.mCourseType.setBackgroundResource(courseTypesBgId[courseType]);
        }

        String chapterName = vo.getChapterName();
        // @date   :2018/4/17 0017 上午 9:37
        // @func   :V5.5改版,更改进度显示
        /*if (vo.getChapters() != null && vo.getChapters().size() > 0) {
            WeekAdapter firstWeekAdapter = new WeekAdapter(activity, chapterName);
            firstWeekAdapter.setData(vo.getChapters(), 0, 4);
            holder.learn_process_lay.setAdapter(firstWeekAdapter);
            firstWeekAdapter.notifyDataSetChanged();

            if (vo.getChapters().size() > 5) {
                holder.hide_arrow.setVisibility(View.VISIBLE);

                holder.hide_arrow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        list.get(position).setIsShowMoreWeek(!list.get(position).isShowMoreWeek());
                        notifyDataSetChanged();
                    }
                });
                if(vo.isShowMoreWeek()) {
                    holder.hide_arrow.setImageResource(R.drawable.arrow_up_gray_ico);
                    WeekAdapter secondWeekAdapter = new WeekAdapter(activity, chapterName);
                    secondWeekAdapter.setData(vo.getChapters(), 5, vo.getChapters().size() - 1);
                    holder.learn_process_more_lay.setAdapter(secondWeekAdapter);
                    secondWeekAdapter.notifyDataSetChanged();
                }else{
                    holder.hide_arrow.setImageResource(R.drawable.arrow_down_gray_ico);
                    WeekAdapter secondWeekAdapter = new WeekAdapter(activity, chapterName);
                    secondWeekAdapter.clearData();
                    holder.learn_process_more_lay.setAdapter(secondWeekAdapter);
                    secondWeekAdapter.notifyDataSetChanged();
                }
            }else{
                holder.hide_arrow.setVisibility(View.GONE);
                holder.hide_arrow.setImageResource(R.drawable.arrow_down_gray_ico);
                WeekAdapter secondWeekAdapter = new WeekAdapter(activity, chapterName);
                secondWeekAdapter.clearData();
                holder.learn_process_more_lay.setAdapter(secondWeekAdapter);
                secondWeekAdapter.notifyDataSetChanged();
            }
        }*/


        // 加载课程信息
        String token = null;
        if (!canEdit) {
            token = curMemberId;
        }
        holder.mProgressBar.setProgress(vo.getLearnRate());
        holder.mProgressPercent.setText(String.format(
                UIUtil.getString(R.string.label_course_progress_percent).toString(), vo.getLearnRate()));
        if (!isTeacher) {
            // 不是老师身份
            holder.mProgressLayout.setVisibility(View.VISIBLE);
        } else {
            holder.mProgressLayout.setVisibility(View.GONE);
        }

        return convertView;
    }

    private class ViewHolder {
        LinearLayout mProgressLayout;
        FrameLayout coverLay;
        TextView course_date_tv;
        ImageView course_iv;
        // 状态
        TextView mTvState;
        TextView mCourseType;
        TextView course_name;
        TextView organ_name;
        TextView teacher_name;
        // 学程指定到班级的状态
        TextView mTvInClass;
        // TextView course_process_tv;
        // NoScrollGridView learn_process_lay;
        // NoScrollGridView learn_process_more_lay;
        // ImageView hide_arrow;
        // 进度条
        ProgressBar mProgressBar;
        // 进度百分比
        TextView mProgressPercent;
        // 购买类型
        TextView mBuyType;

        public ViewHolder(View parentView) {
            coverLay = (FrameLayout) parentView.findViewById(R.id.cover_lay);
            course_date_tv = (TextView) parentView.findViewById(R.id.course_date_tv);
            course_iv = (ImageView) parentView.findViewById(R.id.course_iv);
            mTvState = (TextView) parentView.findViewById(R.id.tv_course_state);
            mCourseType = (TextView) parentView.findViewById(R.id.tv_course_type);
            course_name = (TextView) parentView.findViewById(R.id.course_name);
            organ_name = (TextView) parentView.findViewById(R.id.organ_name);
            teacher_name = (TextView) parentView.findViewById(R.id.teacher_name);
            mTvInClass = (TextView) parentView.findViewById(R.id.tv_in_class);
            mProgressLayout = (LinearLayout) parentView.findViewById(R.id.progress_layout);
            mProgressBar = (ProgressBar) parentView.findViewById(R.id.pb_course_progress);
            mProgressPercent = (TextView) parentView.findViewById(R.id.txt_progress_percent);
            mBuyType = (TextView) parentView.findViewById(R.id.tv_buy_type);
            // @date   :2018/4/17 0017 上午 9:37
            // @func   :V5.5版本,更改进度显示
            /*course_process_tv = (TextView) parentView.findViewById(R.id.course_process_tv);
            learn_process_lay = (NoScrollGridView) parentView.findViewById(R.id.learn_process_lay);
            learn_process_more_lay = (NoScrollGridView) parentView.findViewById(R.id.learn_process_more_lay);
            hide_arrow = (ImageView) parentView.findViewById(R.id.hide_arrow);*/

        }
    }

    /**
     * 下拉刷新设置数据
     *
     * @param list
     */
    public void setData(List<MyCourseVo> list) {
        if (list != null) {
            this.list = new ArrayList<MyCourseVo>(list);
        } else {
            this.list.clear();
        }
    }

    /**
     * 上拉加载更多，新增数据
     *
     * @param list
     */
    public void addData(List<MyCourseVo> list) {
        this.list.addAll(list);
    }

    public class WeekAdapter extends BaseAdapter {
        private List<MyCourseChapterVo> weeklist;
        private Activity parentActivity;
        private String chapterName;

        public WeekAdapter(Activity parent, String parentChapterName) {
            parentActivity = parent;
            chapterName = parentChapterName;
        }

        @Override
        public int getCount() {
            return weeklist.size();
        }

        @Override
        public Object getItem(int position) {
            return weeklist.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            MyCourseChapterVo chapterVo = weeklist.get(position);
            TextView textView = new TextView(activity);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    activity.getResources().getDimensionPixelSize(R.dimen.com_font_size_3));
            if (chapterVo.getStatus() == 0) {
                textView.setBackground(activity.getResources().getDrawable(R.drawable.chapter_status0));
            } else if (chapterVo.getStatus() == 5) {
                textView.setBackground(activity.getResources().getDrawable(R.drawable.chapter_status5));
            } else if (chapterVo.getStatus() == 10) {
                textView.setBackground(activity.getResources().getDrawable(R.drawable.chapter_status10));
            }
            AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            /*layoutParams.setMargins(DisplayUtil.dip2px(activity, 1), 0,
                    DisplayUtil.dip2px(activity, 1), 0);*/
            textView.setLayoutParams(layoutParams);
            textView.setPadding(DisplayUtil.dip2px(activity, 2),
                    DisplayUtil.dip2px(activity, 1),
                    DisplayUtil.dip2px(activity, 2),
                    DisplayUtil.dip2px(activity, 1));
            textView.setGravity(Gravity.CENTER);
            textView.setSingleLine();
            textView.setText(StringUtils.getChapterNumString(activity, chapterName,
                    chapterVo.getWeekNum()));
            return textView;
        }

        public void setData(List<MyCourseChapterVo> list, int start, int end) {
            weeklist = new ArrayList<>();
            for (int i = start; i <= end && i < list.size(); i++) {
                weeklist.add(list.get(i));
            }
        }

        public void clearData() {
            weeklist = new ArrayList<>();
        }
    }
}
