package com.lqwawa.intleducation.module.onclass.detail.base.introduction;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.common.Common;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.ImageUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.entity.ClassDetailEntity;

import org.xutils.x;

import java.util.List;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 关联课程的Adapter
 * @date 2018/06/02 11:29
 * @history v1.0
 * **********************************
 */
public class RelatedCourseAdapter extends RecyclerAdapter<ClassDetailEntity.RelatedCourseBean>{

    public RelatedCourseAdapter(List<ClassDetailEntity.RelatedCourseBean> relatedCourseBeans) {
        super(relatedCourseBeans, null);
    }

    @Override
    protected int getItemViewType(int position, ClassDetailEntity.RelatedCourseBean relatedCourseBean) {
        return R.layout.item_online_class_detail_related_course_layout;
    }

    @Override
    protected RecyclerAdapter.ViewHolder<ClassDetailEntity.RelatedCourseBean> onCreateViewHolder(View root, int viewType) {
        return new ViewHolder(root);
    }

    private class ViewHolder extends RecyclerAdapter.ViewHolder<ClassDetailEntity.RelatedCourseBean>{

        private final int[] courseStatusResId = new int[]{
                R.string.course_status_0,
                R.string.course_status_1,
                R.string.course_status_2};

        private FrameLayout mCoverLay;
        private ImageView mCourseIv;
        private TextView mCourseName;
        private TextView mOrganName;
        private TextView mTeacherName;
        private TextView mCourseStatus;
        private TextView mPriceTitleTv;
        private TextView mCoursePrice;

        public ViewHolder(View itemView) {
            super(itemView);

            mCoverLay = (FrameLayout) itemView.findViewById(R.id.cover_lay);
            mCourseIv = (ImageView) itemView.findViewById(R.id.course_iv);
            mCourseName = (TextView) itemView.findViewById(R.id.course_name);
            mOrganName = (TextView) itemView.findViewById(R.id.organ_name);
            mTeacherName = (TextView) itemView.findViewById(R.id.teacher_name);
            mCourseStatus = (TextView) itemView.findViewById(R.id.course_status);
            mPriceTitleTv = (TextView) itemView.findViewById(R.id.price_title_tv);
            mCoursePrice = (TextView) itemView.findViewById(R.id.course_price);
        }

        @Override
        protected void onBind(ClassDetailEntity.RelatedCourseBean relatedCourseBean) {
            mCourseName.setText(EmptyUtil.isEmpty(relatedCourseBean.getName()) ? "" : relatedCourseBean.getName());
            mOrganName.setText(relatedCourseBean.getOrganName());
            mTeacherName.setText(relatedCourseBean.getTeachersName());
            int courseStatus = relatedCourseBean.getProgressStatus();
            String statusString;
            if (courseStatus >= 0 && courseStatus < 3){
                statusString = UIUtil.getString(courseStatusResId[courseStatus]);
                if(courseStatus == 0){
                    mCourseStatus.setBackgroundResource(R.drawable.radio_bg_pink);
                }else if(courseStatus == 1){
                    mCourseStatus.setBackgroundResource(R.drawable.radio_bg_flag_red);
                }else{
                    mCourseStatus.setBackgroundResource(R.drawable.radio_bg_sky_blue);
                }
            }else{
                statusString = UIUtil.getString(R.string.value_error) + ":" + courseStatus;
            }
            mCourseStatus.setText(statusString);
            if (relatedCourseBean.getPrice() > 0) {
                mPriceTitleTv.setVisibility(View.VISIBLE);

                mCoursePrice.setText(Common.Constance.MOOC_MONEY_MARK + relatedCourseBean.getPrice());

                mCoursePrice.setText(Common.Constance.MOOC_MONEY_MARK + relatedCourseBean.getPrice());

            }else{
                mPriceTitleTv.setVisibility(View.GONE);
                mCoursePrice.setText(UIUtil.getString(R.string.free));
            }

            ImageUtil.fillCourseIcon(mCourseIv,relatedCourseBean.getThumbnailUrl().trim());
        }
    }
}
