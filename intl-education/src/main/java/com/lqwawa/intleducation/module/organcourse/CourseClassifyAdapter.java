package com.lqwawa.intleducation.module.organcourse;

import android.nfc.tech.NfcA;
import android.view.View;
import android.widget.FrameLayout;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;

import java.util.List;

/**
 * @author medici
 * @desc 学程馆课程分类的Adapter
 */
public class CourseClassifyAdapter extends RecyclerAdapter<LQCourseConfigEntity>{

    private CourseClassifyNavigator mNavigator;

    public CourseClassifyAdapter(CourseClassifyNavigator mNavigator) {
        this.mNavigator = mNavigator;
    }

    @Override
    protected int getItemViewType(int position, LQCourseConfigEntity entity) {
        return R.layout.item_course_shop_classify_layout;
    }

    @Override
    protected ViewHolder<LQCourseConfigEntity> onCreateViewHolder(View root, int viewType) {
        return new ClassifyHolder(root,mNavigator);
    }

    /**
     * @desc 学程馆分类的Holder
     */
    private static class ClassifyHolder extends ViewHolder<LQCourseConfigEntity>{

        private FrameLayout mRootLayout;
        private CourseClassifyNavigator mNavigator;

        public ClassifyHolder(View itemView,CourseClassifyNavigator navigator) {
            super(itemView);
            this.mNavigator = navigator;
            mRootLayout = (FrameLayout) itemView.findViewById(R.id.root_layout);
        }

        @Override
        protected void onBind(LQCourseConfigEntity entity) {
            CourseClassifyItemHolder itemHolder = new CourseClassifyItemHolder(UIUtil.getContext());
            itemHolder.updateClassifyView(entity);
            mRootLayout.addView(itemHolder.getRootView());
            if(EmptyUtil.isNotEmpty(mNavigator)){
                itemHolder.setNavigator(mNavigator);
            }
        }
    }
}
