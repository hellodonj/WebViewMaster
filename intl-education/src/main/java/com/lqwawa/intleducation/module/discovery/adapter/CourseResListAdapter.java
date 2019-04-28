package com.lqwawa.intleducation.module.discovery.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ui.MyBaseAdapter;
import com.lqwawa.intleducation.base.utils.DisplayUtil;
import com.lqwawa.intleducation.base.utils.ToastUtil;
import com.lqwawa.intleducation.common.utils.DrawableUtil;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.RefreshUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.module.discovery.ui.CourseSelectItemFragment;
import com.lqwawa.intleducation.module.discovery.ui.lesson.select.ResourceSelectListener;
import com.lqwawa.intleducation.module.learn.vo.SectionResListVo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XChen on 2017/1/4.
 * email:man0fchina@foxmail.com
 */

public class CourseResListAdapter extends MyBaseAdapter {

    private Activity activity;
    private List<SectionResListVo> list;
    private List<SectionResListVo> selectList = new ArrayList<>();
    private LayoutInflater inflater;
    boolean needFlagRead;
    private int lessonStatus;
    private OnItemClickListener onItemClickListener = null;
    private ResourceSelectListener mSelectListener;
    private boolean isCourseSelect;
    private int maxSelect = 1;
    private int mTaskType = 9;

    private boolean mChoiceMode;
    private boolean mClassTeacher;

    private SparseArray<ResIcon> resIconSparseArray = new SparseArray<>();


    public CourseResListAdapter(Activity activity, boolean needFlagRead) {
        this.activity = activity;
        this.needFlagRead = needFlagRead;
        this.inflater = LayoutInflater.from(activity);
        list = new ArrayList<SectionResListVo>();
        lessonStatus = activity.getIntent().getIntExtra("status", 0);
        intData();
    }

    private void intData() {
        ResIcon resIcon = new ResIcon(R.drawable.ic_pic, R.drawable.ic_pic_read,
                R.drawable.ic_pic_shield, R.drawable.ic_pic_new);
        resIconSparseArray.put(1, resIcon);

        resIcon = new ResIcon(R.drawable.ic_audio, R.drawable.ic_audio_read,
                R.drawable.ic_audio_shield, R.drawable.ic_audio_new);
        resIconSparseArray.put(2, resIcon);

        resIcon = new ResIcon(R.drawable.ic_video, R.drawable.ic_video_read,
                R.drawable.ic_video_shield, R.drawable.ic_video_new);
        resIconSparseArray.put(3, resIcon);
        resIconSparseArray.put(30, resIcon);

        resIcon = new ResIcon(R.drawable.ic_pdf, R.drawable.ic_pdf_read,
                R.drawable.ic_pdf_shield, R.drawable.ic_pdf_new);
        resIconSparseArray.put(6, resIcon);

        resIcon = new ResIcon(R.drawable.ic_ppt, R.drawable.ic_ppt_read,
                R.drawable.ic_ppt_shield, R.drawable.ic_ppt_new);
        resIconSparseArray.put(20, resIcon);

        resIcon = new ResIcon(R.drawable.ic_word, R.drawable.ic_word_read,
                R.drawable.ic_word_shield, 0);
        resIconSparseArray.put(24, resIcon);

        resIcon = new ResIcon(R.drawable.ic_txt, R.drawable.ic_txt_read,
                R.drawable.ic_txt_shield, 0);
        resIconSparseArray.put(25, resIcon);

        resIcon = new ResIcon(R.drawable.ic_lqc, R.drawable.ic_lqc_read,
                R.drawable.ic_lqc_shield, R.drawable.ic_lqc_new);
        resIconSparseArray.put(5, resIcon);
        resIconSparseArray.put(16, resIcon);
        resIconSparseArray.put(17, resIcon);
        resIconSparseArray.put(18, resIcon);
        resIconSparseArray.put(19, resIcon);

        resIcon = new ResIcon(R.drawable.ic_task_not_flag, R.drawable.ic_task_read,
                R.drawable.ic_task_shield, R.drawable.ic_task_new);
        resIconSparseArray.put(23, resIcon);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
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

    public void setNeedFlagRead(boolean needFlagRead) {
        this.needFlagRead = needFlagRead;
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        final SectionResListVo vo = list.get(position);
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = inflater.inflate(R.layout.item_course_lesson_detail_source_layout, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        holder.itemRootLay.setVisibility(View.VISIBLE);
        int resType = vo.getResType();
        if (resType > 10000) {
            resType -= 10000;
        }

        setResIcon(holder.resIconIv, vo, resType);

        if (needFlagRead && vo.isIsRead()) {
            holder.mIvNeedCommit.setImageResource(R.drawable.ic_task_completed);
        } else {
            holder.mIvNeedCommit.setImageResource(R.drawable.ic_need_to_commit);
        }

        String assigned = activity.getString(R.string.label_assigned);
        if (mClassTeacher && vo.isAssigned()) {
            SpannableString spannableString = new SpannableString(assigned + vo.getName().trim());
            ForegroundColorSpan colorSpan =
                    new ForegroundColorSpan(activity.getResources().getColor(R.color.textAccent));
            spannableString.setSpan(colorSpan, 0, assigned.length(),
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            holder.resNameTv.setText(spannableString);
        } else {
            holder.resNameTv.setText(("" + vo.getName()).trim());
        }

        if (mChoiceMode) {
            holder.checkbox.setVisibility(mChoiceMode ? View.VISIBLE : View.GONE);
            holder.checkbox.setActivated(vo.isActivated());
            holder.checkbox.setChecked(false);
        } else {
            holder.checkbox.setVisibility(isCourseSelect ? View.VISIBLE : View.GONE);
            holder.checkbox.setChecked(vo.isChecked());
        }

        if (isCourseSelect) {
            holder.checkbox.setOnClickListener(new ItemClickListener(position, convertView, vo));
        } else {
            holder.itemRootLay.setOnClickListener(new ItemClickListener(position, convertView, vo));
            if (mChoiceMode) {
                holder.checkbox.setOnClickListener(new ItemClickListener(position, convertView, vo));
            }
        }

        if (isCourseSelect || mChoiceMode) {
            // 是课件选取,添加自动批阅
            // v5.10 LQ学程浏览的时候也要添加语音评测,还有一个直播课前课后的调用
            // v5.10 LQ学程浏览的时候不需要添加语音评测
            if (((mTaskType == CourseSelectItemFragment.KEY_RELL_COURSE || vo.getTaskType() == 2) ||
                    (mTaskType == CourseSelectItemFragment.KEY_LECTURE_COURSE || vo.getTaskType() == 5)) &&
                    SectionResListVo.EXTRAS_AUTO_READ_OVER.equals(vo.getResProperties())) {
                // 只有听说课,才显示语音评测
                holder.mTvAutoMask.setVisibility(View.VISIBLE);
                holder.mTvAutoMask.setText(R.string.label_voice_evaluating);
            } else if ((mTaskType == CourseSelectItemFragment.KEY_TASK_ORDER || vo.getTaskType() == 3) &&
                    EmptyUtil.isNotEmpty(vo.getPoint())) {
                // 只有读写单,才显示自动批阅
                holder.mTvAutoMask.setVisibility(View.VISIBLE);
                holder.mTvAutoMask.setText(R.string.label_auto_mark);
            } else {
                holder.mTvAutoMask.setVisibility(View.GONE);
            }
        } else {
            holder.mTvAutoMask.setVisibility(View.GONE);
        }

        int color = UIUtil.getColor(R.color.alertImportant);
        int radius = DisplayUtil.dip2px(UIUtil.getContext(), 10);
        holder.mTvAutoMask.setBackground(DrawableUtil.createDrawable(color, color, radius));

        if (!mChoiceMode) {
            int taskType = vo.getTaskType();
            if (taskType == 1 || taskType == 4) {
                // 看课件
                holder.mIvNeedCommit.setVisibility(View.GONE);
            } else {
                // 听读课,读写单
                holder.mIvNeedCommit.setVisibility(View.VISIBLE);
            }
        } else {
            holder.mIvNeedCommit.setVisibility(View.GONE);
        }

        holder.mViewCount.setText(activity.getString(R.string.some_study,
                String.valueOf(vo.getViewCount())));

        return convertView;
    }

    private void setResIcon(ImageView imageView, SectionResListVo vo, int resType) {
        if (vo.isIsShield()) {
            imageView.setImageResource(resIconSparseArray.get(resType).shieldResId);
        } else {
            if (needFlagRead && vo.isIsRead()) {
                imageView.setImageResource(resIconSparseArray.get(resType).readResId);
            } else if (needFlagRead && !vo.isIsRead() && lessonStatus == 1 && (resType != 24
                    || resType != 25)) {
                // txt word 不设置
                imageView.setImageResource(resIconSparseArray.get(resType).newResId);
            } else {
                imageView.setImageResource(resIconSparseArray.get(resType).resId);
            }
        }
    }

    class ItemClickListener implements View.OnClickListener {

        private int position;
        private View convertView;
        private SectionResListVo vo;

        ItemClickListener(int position, View convertView, SectionResListVo vo) {
            this.position = position;
            this.convertView = convertView;
            this.vo = vo;
        }

        @Override
        public void onClick(View view) {
            if (mChoiceMode) {
                // 回调出去
                if (onItemClickListener != null) {
                    if (view.getId() == R.id.checkbox) {
                        onItemClickListener.onItemChoice(position, convertView);
                    } else {
                        onItemClickListener.onItemClick(position, convertView);
                    }
                }
            } else {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(position, convertView);
                }
                //课件选取
                if (isCourseSelect) {
                    if (maxSelect == 1) {//单选模式
                        if (EmptyUtil.isNotEmpty(mSelectListener)) {
                            boolean onSelect = mSelectListener.onSelect(vo);
                            if (!onSelect) {
                                vo.setChecked(!vo.isChecked());
                                notifyDataSetChanged();
                                if (vo.isChecked()) {
                                    RefreshUtil.getInstance().addId(vo.getId());
                                } else {
                                    RefreshUtil.getInstance().removeId(vo.getId());
                                }
                            }
                        }
                    } else {//多选模式
                        if (EmptyUtil.isNotEmpty(mSelectListener)) {
                            boolean onSelect = mSelectListener.onSelect(vo);
                            if (!onSelect) {
                                vo.setChecked(!vo.isChecked());
                                notifyDataSetChanged();
                                if (vo.isChecked()) {
                                    RefreshUtil.getInstance().addId(vo.getId());
                                } else {
                                    RefreshUtil.getInstance().removeId(vo.getId());
                                }
                            } else {
                                ToastUtil.showToast(activity, activity.getString(R.string.str_select_count_tips, maxSelect));
                                notifyDataSetChanged();
                            }
                        }
                    }

                }
            }
        }
    }


    protected class ViewHolder {
        private CheckBox checkbox;
        private LinearLayout itemRootLay;
        private ImageView resIconIv;
        private TextView resNameTv;
        private TextView mTvAutoMask;
        private TextView mViewCount;
        private ImageView mIvNeedCommit;

        public ViewHolder(View view) {
            itemRootLay = (LinearLayout) view.findViewById(R.id.item_root_lay);
            resIconIv = (ImageView) view.findViewById(R.id.res_icon_iv);
            resNameTv = (TextView) view.findViewById(R.id.res_name_tv);
            checkbox = (CheckBox) view.findViewById(R.id.checkbox);
            mIvNeedCommit = (ImageView) view.findViewById(R.id.iv_need_commit);
            mTvAutoMask = (TextView) view.findViewById(R.id.tv_auto_mark);
            mViewCount = (TextView) view.findViewById(R.id.tv_view_count);
        }
    }

    /**
     * 下拉刷新设置数据
     *
     * @param list
     */
    public void setData(List<SectionResListVo> list) {
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
    public void addData(List<SectionResListVo> list) {
        if (this.list == null) {
            this.list = new ArrayList<>();
        }
        this.list.addAll(list);
    }

    public interface OnItemClickListener {
        void onItemClick(int position, View convertView);

        void onItemChoice(int position, View convertView);
    }

    /**
     * 课件选取
     *
     * @param b
     */
    public void setCourseSelect(boolean b, int tasktype) {
        isCourseSelect = b;
        mTaskType = tasktype;
        if (mTaskType == 9) {
            maxSelect = 9;
        } else {
            maxSelect = 1;
        }
    }

    public void setOnResourceSelectListener(@NonNull ResourceSelectListener listener) {
        this.mSelectListener = listener;
    }


    /**
     * @param isClassTeacher true 班级学程的老师
     * @author 是否是班级学程的老师
     */
    public void setClassTeacher(@NonNull boolean isClassTeacher) {
        this.mClassTeacher = isClassTeacher;
    }

    /**
     * 打开
     */
    public void triggerChoiceMode(boolean open) {
        mChoiceMode = open;
        notifyDataSetChanged();
    }

    /**
     * @return 返回当前选择状态
     * @author mrmedici
     */
    public boolean getChoiceMode() {
        return mChoiceMode;
    }

    /**
     * 设置多选条目
     *
     * @param multipleChoiceCount 条目个数
     *                            <p>需要在{@link CourseResListAdapter#setCourseSelect}后调用</p>
     */
    public void setMultipleChoiceCount(int multipleChoiceCount) {
        maxSelect = multipleChoiceCount;
    }

    public List<SectionResListVo> getData() {
        return list;
    }

    public List<SectionResListVo> getSelectData() {
        selectList.clear();
        for (int i = 0; i < list.size(); i++) {
            SectionResListVo sectionResListVo = list.get(i);
            if (sectionResListVo.isChecked()) {
                selectList.add(sectionResListVo);
            }
        }
        return selectList;
    }

    public class ResIcon {
        int resId;
        int readResId;
        int shieldResId;
        int newResId;

        public ResIcon(int resId, int readResId, int shieldResId, int newResId) {
            this.resId = resId;
            this.readResId = readResId;
            this.shieldResId = shieldResId;
            this.newResId = newResId;
        }
    }
}
