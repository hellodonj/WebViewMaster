package com.lqwawa.intleducation.module.discovery.ui.lesson.sxdetail;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.utils.DisplayUtil;
import com.lqwawa.intleducation.base.utils.ResIconUtils;
import com.lqwawa.intleducation.base.utils.ToastUtil;
import com.lqwawa.intleducation.common.utils.DrawableUtil;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.RefreshUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.common.utils.image.LQwawaImageUtil;
import com.lqwawa.intleducation.module.discovery.adapter.CourseResListAdapter;
import com.lqwawa.intleducation.module.discovery.ui.CourseSelectItemFragment;
import com.lqwawa.intleducation.module.discovery.ui.lesson.select.ResourceSelectListener;
import com.lqwawa.intleducation.module.learn.vo.SectionResListVo;
import com.lqwawa.intleducation.module.learn.vo.SectionTaskListVo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 描述:  三习课程资源列表的adapter
 * 作者|时间: djj on 2019/7/23 0023 下午 2:36
 */
public class SxCourseResListAdapter extends BaseExpandableListAdapter {

    private Activity activity;
    private List<SectionTaskListVo> group;
    private Map<String, List<SectionResListVo>> children;
    private List<SectionResListVo> childs;
    private List<SectionResListVo>  selectList = new ArrayList<SectionResListVo>();
    private LayoutInflater inflater; // 是否显示已读标识
    boolean needFlagRead;
    boolean isVideoCourse;
    private int lessonStatus;
    private CourseResListAdapter.OnItemClickListener onItemClickListener = null;
    private ResourceSelectListener mSelectListener;
    private boolean isCourseSelect;
    private boolean mChoiceMode;
    private boolean mClassTeacher;
    private int maxSelect = 1;
    private int mTaskType = 9;
    private SparseArray<ResIconUtils.ResIcon> resIconSparseArray = new SparseArray<>();

    /**
     * 构造函数
     *
     * @param activity
     */
    public SxCourseResListAdapter(Activity activity, List<SectionTaskListVo> group, Map<String, List<SectionResListVo>> children) {
        this.activity = activity;
        this.group = group;
        this.children = children;
    }

    public SxCourseResListAdapter(Activity activity, boolean needFlagRead, boolean isVideoCourse) {
        this.activity = activity;
        this.needFlagRead = needFlagRead;
        this.isVideoCourse = isVideoCourse;
        this.inflater = LayoutInflater.from(activity);
        group = new ArrayList<SectionTaskListVo>();
        children = new HashMap<String, List<SectionResListVo>>();
        lessonStatus = activity.getIntent().getIntExtra("status", 0);
        resIconSparseArray = ResIconUtils.resIconSparseArray;
    }

    @Override
    public int getGroupCount() {
        if (group != null) {
            return group.size();
        }
        return 0;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        childs = children.get(group.get(groupPosition).getData());
        if (childs != null) {
            return childs.size();
        }
        return 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return group.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        childs = children.get(group.get(groupPosition).getData());
        return childs.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        final GroupViewHolder gholder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_course_list_title, null);
            gholder = new GroupViewHolder(convertView);
            convertView.setTag(gholder);
        } else {
            gholder = (GroupViewHolder) convertView.getTag();
        }
        final SectionTaskListVo group = (SectionTaskListVo) getGroup(groupPosition);
        if (group != null) {
            gholder.mTvTypeName.setText(group.getTaskName());
            gholder.mIvHideLesson.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 暴露组选接口
                    checkInterface.checkGroup(groupPosition, false);
                }
            });
        }
        notifyDataSetChanged();
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final ChildViewHolder cholder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_course_list_content, null);
            cholder = new ChildViewHolder(convertView);
            convertView.setTag(cholder);
        } else {
            cholder = (ChildViewHolder) convertView.getTag();
        }

        final SectionResListVo vo = (SectionResListVo) getChild(groupPosition, childPosition);
        cholder.itemRootLay.setVisibility(View.VISIBLE);
        int resType = vo.getResType();
        if (resType > 10000) {
            resType -= 10000;
        }
        if (isVideoCourse) {
            cholder.resIconIv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            LQwawaImageUtil.loadCourseThumbnail(UIUtil.getContext(), cholder.resIconIv, vo.getThumbnail());
        } else {
            cholder.resIconIv.setScaleType(ImageView.ScaleType.FIT_CENTER);
            setResIcon(cholder.resIconIv, vo, resType);

        }
        setResIconLayout(cholder.resIconIv, isVideoCourse);

        cholder.mIvPlayIcon.setVisibility(isVideoCourse ? View.VISIBLE : View.GONE);

        if (needFlagRead && vo.isIsRead()) {
            cholder.mIvNeedCommit.setImageResource(R.drawable.ic_task_completed);
        } else {
            int resId = isVideoCourse ? 0 : R.drawable.ic_need_to_commit;
            cholder.mIvNeedCommit.setImageResource(resId);
        }

        String assigned = activity.getString(R.string.label_assigned);
        if (mClassTeacher && vo.isAssigned()) {
            SpannableString spannableString = new SpannableString(assigned + vo.getName().trim());
            ForegroundColorSpan colorSpan =
                    new ForegroundColorSpan(activity.getResources().getColor(R.color.textAccent));
            spannableString.setSpan(colorSpan, 0, assigned.length(),
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            cholder.resNameTv.setText(spannableString);
        } else {
            cholder.resNameTv.setText(("" + vo.getName()).trim());
        }

        if (mChoiceMode) {
            cholder.checkbox.setVisibility(mChoiceMode ? View.VISIBLE : View.GONE);
            cholder.checkbox.setActivated(vo.isActivated());
            cholder.checkbox.setChecked(false);
        } else {
            cholder.checkbox.setVisibility(isCourseSelect ? View.VISIBLE : View.GONE);
            cholder.checkbox.setChecked(vo.isChecked());
        }

        if (isCourseSelect) {
            cholder.checkbox.setOnClickListener(new ItemClickListener(childPosition, convertView, vo));
        } else {
            cholder.itemRootLay.setOnClickListener(new ItemClickListener(childPosition, convertView, vo));
            if (mChoiceMode) {
                cholder.checkbox.setOnClickListener(new ItemClickListener(childPosition, convertView, vo));
            }
        }

//        if (isCourseSelect || mChoiceMode) {
        // 是课件选取,添加自动批阅
        // v5.10 LQ学程浏览的时候也要添加语音评测,还有一个直播课前课后的调用
        // v5.10 LQ学程浏览的时候不需要添加语音评测
        if (((mTaskType == CourseSelectItemFragment.KEY_RELL_COURSE || vo.getTaskType() == 2) ||
                (mTaskType == CourseSelectItemFragment.KEY_LECTURE_COURSE
                        || vo.getTaskType() == 5 && (isCourseSelect || mChoiceMode)) || vo.getTaskType() == 1) &&
                SectionResListVo.EXTRAS_AUTO_READ_OVER.equals(vo.getResProperties())) {
            // 只有听说课,才显示语音评测  参考视屏也显示
            cholder.mTvAutoMask.setVisibility(View.VISIBLE);
            cholder.mTvAutoMask.setText(R.string.label_voice_evaluating);
        } else if ((mTaskType == CourseSelectItemFragment.KEY_TASK_ORDER || vo.getTaskType() == 3) &&
                EmptyUtil.isNotEmpty(vo.getPoint())) {
            // 只有读写单,才显示自动批阅
            cholder.mTvAutoMask.setVisibility(View.VISIBLE);
            cholder.mTvAutoMask.setText(R.string.label_auto_mark);
        } else {
            cholder.mTvAutoMask.setVisibility(View.INVISIBLE);
        }

        int color = UIUtil.getColor(R.color.alertImportant);
        int radius = DisplayUtil.dip2px(UIUtil.getContext(), 10);
        cholder.mTvAutoMask.setBackground(DrawableUtil.createDrawable(color, color, radius));

        if (!mChoiceMode) {
            int taskType = vo.getTaskType();
            if (taskType == 1 || taskType == 4) {
                // 看课件 视频课
                cholder.mIvNeedCommit.setVisibility(isVideoCourse ? View.VISIBLE : View.GONE);
            } else {
                // 听读课,读写单
                cholder.mIvNeedCommit.setVisibility(!isCourseSelect ? View.VISIBLE : View.GONE);
            }
        } else {
            cholder.mIvNeedCommit.setVisibility(View.GONE);
        }

        cholder.mViewCount.setText(activity.getString(R.string.some_study,
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

    private void setResIconLayout(ImageView resIconIv, boolean isVideoLibrary) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) resIconIv.getLayoutParams();
        int iconWidth = DisplayUtil.dip2px(UIUtil.getContext(), 48);
        int iconHeight = iconWidth;
        if (isVideoLibrary) {
            iconHeight = DisplayUtil.dip2px(UIUtil.getContext(), 72);
            iconWidth = iconHeight * 16 / 9;
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        } else {
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);

        }
        layoutParams.width = iconWidth;
        layoutParams.height = iconHeight;
        resIconIv.setLayoutParams(layoutParams);
    }


    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    /**
     * 组元素绑定器
     */
    public static class GroupViewHolder {

        private TextView mTvTypeName;
        private ImageView mIvHideLesson;

        public GroupViewHolder(View view) {
            mTvTypeName = (TextView) view.findViewById(R.id.tv_lesson_type_name);
            mIvHideLesson = (ImageView) view.findViewById(R.id.iv_hide_lesson);
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



    /**
     * 子元素绑定器
     */
    public static class ChildViewHolder {
        private CheckBox checkbox;
        private LinearLayout itemRootLay;
        private ImageView resIconIv;
        private TextView resNameTv;
        private TextView mTvAutoMask;
        private TextView mViewCount;
        private ImageView mIvNeedCommit;
        private ImageView mIvPlayIcon;

        public ChildViewHolder(View view) {
            itemRootLay = (LinearLayout) view.findViewById(R.id.item_root_lay);
            resIconIv = (ImageView) view.findViewById(R.id.iv_res_icon);
            resNameTv = (TextView) view.findViewById(R.id.tv_res_name);
            checkbox = (CheckBox) view.findViewById(R.id.checkbox);
            mIvNeedCommit = (ImageView) view.findViewById(R.id.iv_need_commit);
            mTvAutoMask = (TextView) view.findViewById(R.id.tv_auto_mark);
            mViewCount = (TextView) view.findViewById(R.id.tv_view_count);
            mIvPlayIcon = (ImageView) view.findViewById(R.id.iv_res_play);
        }
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
        return childs;
    }

    public List<SectionResListVo> getSelectData() {
        childs.clear();
        for (int i = 0; i < childs.size(); i++) {
            SectionResListVo sectionResListVo = childs.get(i);
            if (sectionResListVo.isChecked()) {
                selectList.add(sectionResListVo);
            }
        }
        return selectList;
    }


    private CheckInterface checkInterface;

    public void setCheckInterface(CheckInterface checkInterface) {
        this.checkInterface = checkInterface;
    }

    /**
     * 复选框接口
     */
    public interface CheckInterface {
        /**
         * 组选框状态改变触发的事件
         *
         * @param groupPosition 组元素位置
         * @param isHide        组元素选中与否
         */
        void checkGroup(int groupPosition, boolean isHide);

        /**
         * 子选框状态改变时触发的事件
         *
         * @param groupPosition 组元素位置
         * @param childPosition 子元素位置
         * @param isChecked     子元素选中与否
         */
        void checkChild(int groupPosition, int childPosition, boolean isChecked);

    }


}
