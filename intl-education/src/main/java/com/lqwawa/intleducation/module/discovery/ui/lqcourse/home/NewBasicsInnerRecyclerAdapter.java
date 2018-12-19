package com.lqwawa.intleducation.module.discovery.ui.lqcourse.home;

import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.utils.DisplayUtil;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.common.utils.DrawableUtil;
import com.lqwawa.intleducation.common.utils.LogUtil;
import com.lqwawa.intleducation.common.utils.StringUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.entity.LQBasicsOuterEntity;

import java.util.List;

/**
 * @author mrmedici
 * @desc 新基础课程科目cell的recycler adapter
 */
public class NewBasicsInnerRecyclerAdapter extends RecyclerAdapter<LQBasicsOuterEntity.LQBasicsInnerEntity>{

    private int[] colors = new int[]{
            R.color.basics_type_color_1,
            R.color.basics_type_color_2,
            R.color.basics_type_color_3,
            R.color.basics_type_color_4
    };


    @Override
    protected int getItemViewType(int position, LQBasicsOuterEntity.LQBasicsInnerEntity lqBasicsInnerEntity) {
        return R.layout.item_new_basics_inner_layout;
    }

    @Override
    protected RecyclerAdapter.ViewHolder<LQBasicsOuterEntity.LQBasicsInnerEntity> onCreateViewHolder(View root, int viewType) {
        return new ViewHolder(root);
    }

    private class ViewHolder extends RecyclerAdapter.ViewHolder<LQBasicsOuterEntity.LQBasicsInnerEntity>{

        private TextView mTvContent;

        public ViewHolder(View itemView) {
            super(itemView);
            mTvContent = (TextView) itemView.findViewById(R.id.tv_content);
        }

        @Override
        protected void onBind(LQBasicsOuterEntity.LQBasicsInnerEntity entity) {
            int position = getAdapterPosition();
            LogUtil.e(NewBasicsOuterAdapter.class,"position:"+position);
            StringUtil.fillSafeTextView(mTvContent,entity.getConfigValue());
            // 设置底色
            int colorPosition = position % 4;
            GradientDrawable drawable = DrawableUtil.createDrawable(colors[colorPosition],colors[colorPosition], DisplayUtil.dip2px(UIUtil.getContext(),4));
            mTvContent.setBackground(drawable);
        }
    }
}
