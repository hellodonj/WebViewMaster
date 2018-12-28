package com.lqwawa.intleducation.module.learn.ui.mycourse;

import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.utils.DisplayUtil;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.common.utils.DrawableUtil;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.ImageUtil;
import com.lqwawa.intleducation.common.utils.StringUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.entity.LQBasicsOuterEntity;
import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;

import java.util.List;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 我的习课程子分类Adapter
 * @date 2018/05/02 11:32
 * @history v1.0
 * **********************************
 */
public class MyCourseChoiceAdapter extends RecyclerAdapter<LQCourseConfigEntity>{

    private int mBgColor;

    public MyCourseChoiceAdapter(List<LQCourseConfigEntity> entities, int mBgColor) {
        super(entities, null);
        this.mBgColor = mBgColor;
    }

    @Override
    protected int getItemViewType(int position, LQCourseConfigEntity entity) {
        return R.layout.item_course_choice_layout;
    }

    @Override
    protected RecyclerAdapter.ViewHolder<LQCourseConfigEntity> onCreateViewHolder(View root, int viewType) {
        return new ViewHolder(root);
    }

    /**
     * 分类数据ViewHolder
     */
    private final class ViewHolder extends RecyclerAdapter.ViewHolder<LQCourseConfigEntity>{

        private TextView mTvContent;

        public ViewHolder(View itemView) {
            super(itemView);
            mTvContent = (TextView) itemView.findViewById(R.id.tv_content);
        }

        @Override
        protected void onBind(final LQCourseConfigEntity entity) {
            StringUtil.fillSafeTextView(mTvContent,entity.getConfigValue());
            // 设置底色
            GradientDrawable drawable = DrawableUtil.createDrawable(mBgColor,mBgColor, DisplayUtil.dip2px(UIUtil.getContext(),4));
            mTvContent.setBackground(drawable);
        }
    }
}
