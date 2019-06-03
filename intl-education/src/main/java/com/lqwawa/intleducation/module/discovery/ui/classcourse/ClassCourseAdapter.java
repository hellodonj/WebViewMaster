package com.lqwawa.intleducation.module.discovery.ui.classcourse;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.ImageUtil;
import com.lqwawa.intleducation.common.utils.StringUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.common.utils.image.LQwawaImageUtil;
import com.lqwawa.intleducation.factory.data.entity.course.ClassCourseEntity;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import java.util.List;

/**
 * @author mrmedici
 * @desc 班级学程的Recycler Adapter
 */
public class ClassCourseAdapter extends RecyclerAdapter<ClassCourseEntity> {
    // 是否是班主任
    private boolean isHeadMaster;
    // 当前角色信息
    private String mRoles;
    // 当前模式
    private boolean isChoiceMode;
    // 事件回调类
    private ClassCourseNavigator mNavigator;

    public ClassCourseAdapter(boolean isHeadMaster,@NonNull String roles) {
        super();
        this.isHeadMaster = isHeadMaster;
        this.mRoles = roles;
    }

    public ClassCourseAdapter(boolean isHeadMaster,@NonNull String roles,boolean choiceMode) {
        this(isHeadMaster,roles);
        this.isChoiceMode = choiceMode;
    }

    @Override
    protected int getItemViewType(int position, ClassCourseEntity entity) {
        return R.layout.item_common_course_layout;
    }

    @Override
    protected ViewHolder<ClassCourseEntity> onCreateViewHolder(View root, int viewType) {
        return new CommonCourseViewHolder(root);
    }

    public void setChoiceMode(boolean choiceMode) {
        isChoiceMode = choiceMode;
    }

    public boolean isChoiceMode() {
        return isChoiceMode;
    }

    public void setNavigator(@NonNull ClassCourseNavigator navigator){
        this.mNavigator = navigator;
    }

    /**
     * @author mrmedici
     * @desc LQ学程的基本Adapter;
     */
    private class CommonCourseViewHolder extends ViewHolder<ClassCourseEntity>{

        private ImageView mCourseIcon;
        private TextView mCourseType;
        private TextView mCourseName;
        private ImageView mIvDelete;
        private ImageView mIvChoice;
        private TextView mBuyType;

        private final int[] courseTypesBgId = new int[] {
                R.drawable.shape_course_type_read,
                R.drawable.shape_course_type_learn,
                R.drawable.shape_course_type_practice,
                R.drawable.shape_course_type_exam,
                R.drawable.shape_course_type_video
        };

        private String[] courseTypeNames;


        public CommonCourseViewHolder(View itemView) {
            super(itemView);
            mCourseIcon = (ImageView) itemView.findViewById(R.id.iv_course_icon);
            mCourseType = (TextView) itemView.findViewById(R.id.tv_course_type);
            mCourseName = (TextView) itemView.findViewById(R.id.tv_course_name);
            mIvDelete = (ImageView) itemView.findViewById(R.id.iv_delete);
            mIvChoice = (ImageView) itemView.findViewById(R.id.iv_choice);
            mBuyType = (TextView) itemView.findViewById(R.id.tv_buy_type);

            courseTypeNames =
                    itemView.getContext().getResources().getStringArray(R.array.course_type_names);
        }

        @Override
        protected void onBind(ClassCourseEntity entity) {
            StringUtil.fillSafeTextView(mCourseName,entity.getName());
            // String courseUrl = entity.getThumbnailUrl().trim();
            LQwawaImageUtil.loadCourseThumbnailH(mCourseIcon.getContext(),mCourseIcon,entity.getThumbnailUrl());
            // ImageUtil.fillCourseIcon(mCourseIcon,courseUrl);

            if(UserHelper.isStudent(mRoles)){
                // 学生 家长
                // 后面改成只有学生，显示
                if(entity.isBuyAll()){
                    // 全部购买
                    mBuyType.setText(R.string.label_buy_all);
                    mBuyType.setVisibility(View.VISIBLE);
                }else if(entity.isChapterBuy()){
                    mBuyType.setText(String.format(UIUtil.getString(R.string.label_buy_number_chapter),entity.getChaperBuyCount()));
                    mBuyType.setVisibility(View.VISIBLE);
                }else{
                    mBuyType.setVisibility(View.GONE);
                }
            }else{
                mBuyType.setVisibility(View.GONE);
            }

            if(isHeadMaster && entity.isHold()){
                // 班主任才有删除按钮
                mIvDelete.setVisibility(View.VISIBLE);
                mIvDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getAdapterPosition();
                        if(EmptyUtil.isNotEmpty(mNavigator)){
                            mNavigator.onDeleteClassCourse(position);
                        }
                    }
                });
            }else{
                mIvDelete.setVisibility(View.GONE);
            }

            if(isHeadMaster && isChoiceMode){
                mIvChoice.setVisibility(View.VISIBLE);
                mIvChoice.setActivated(entity.isChecked());
            }else{
                mIvChoice.setVisibility(View.GONE);
            }

            int courseType = entity.getAssortment();
            if (courseType >= 0 && courseType < courseTypesBgId.length) {
                mCourseType.setText(courseTypeNames[courseType]);
                mCourseType.setBackgroundResource(courseTypesBgId[courseType]);
                mCourseType.setVisibility(View.VISIBLE);
            }
        }
    }
}
