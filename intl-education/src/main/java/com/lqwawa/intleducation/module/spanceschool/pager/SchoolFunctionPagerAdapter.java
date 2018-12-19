package com.lqwawa.intleducation.module.spanceschool.pager;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.common.utils.ImageUtil;
import com.lqwawa.intleducation.common.utils.StringUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.entity.SchoolFunctionEntity;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 学校功能列表Adapter
 * @date 2018/06/25 10:26
 * @history v1.0
 * **********************************
 */
public class SchoolFunctionPagerAdapter extends RecyclerAdapter<SchoolFunctionEntity>{


    @Override
    protected int getItemViewType(int position, SchoolFunctionEntity schoolFunctionEntity) {
        return R.layout.item_school_function_layout;
    }

    @Override
    protected RecyclerAdapter.ViewHolder<SchoolFunctionEntity> onCreateViewHolder(View root, int viewType) {
        return new ViewHolder(root);
    }

    /**
     * 功能菜单Item的ViewHolder
     */
    private class ViewHolder extends RecyclerAdapter.ViewHolder<SchoolFunctionEntity>{

        private ImageView mFunctionIcon;
        private TextView mFunctionDesc;

        public ViewHolder(View itemView) {
            super(itemView);
            mFunctionIcon = (ImageView) itemView.findViewById(R.id.iv_icon);
            mFunctionDesc = (TextView) itemView.findViewById(R.id.tv_desc);
        }

        @Override
        protected void onBind(SchoolFunctionEntity schoolFunctionEntity) {
            StringUtil.fillSafeTextView(mFunctionDesc, UIUtil.getString(schoolFunctionEntity.getTitleId()));
            mFunctionIcon.setImageResource(schoolFunctionEntity.getResId());
        }
    }
}
