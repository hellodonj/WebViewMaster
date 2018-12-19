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
    // 事件回调类
    private ClassCourseNavigator mNavigator;

    public ClassCourseAdapter(boolean isHeadMaster,@NonNull String roles) {
        super();
        this.isHeadMaster = isHeadMaster;
        this.mRoles = roles;
    }

    @Override
    protected int getItemViewType(int position, ClassCourseEntity entity) {
        return R.layout.item_common_course_layout;
    }

    @Override
    protected ViewHolder<ClassCourseEntity> onCreateViewHolder(View root, int viewType) {
        return new CommonCourseViewHolder(root);
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
        private TextView mCourseName;
        private ImageView mIvDelete;
        private TextView mBuyType;

        public CommonCourseViewHolder(View itemView) {
            super(itemView);
            mCourseIcon = (ImageView) itemView.findViewById(R.id.iv_course_icon);
            mCourseName = (TextView) itemView.findViewById(R.id.tv_course_name);
            mIvDelete = (ImageView) itemView.findViewById(R.id.iv_delete);
            mBuyType = (TextView) itemView.findViewById(R.id.tv_buy_type);
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
        }
    }
}
