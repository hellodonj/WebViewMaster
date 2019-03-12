package com.lqwawa.intleducation.module.box;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.utils.ResourceUtils;
import com.lqwawa.intleducation.base.utils.StringUtils;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.common.utils.image.LQwawaImageUtil;
import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;

/**
 * @author mrmedici
 * @desc 功能菜单的Adapter
 */
public class FunctionAdapter extends RecyclerAdapter<FunctionEntity> {

    @Override
    protected int getItemViewType(int position, FunctionEntity functionEntity) {
        return R.layout.item_funcation_layout;
    }

    @Override
    protected RecyclerAdapter.ViewHolder<FunctionEntity> onCreateViewHolder(View root, int viewType) {
        return new ViewHolder(root);
    }

    /**
     * 功能菜单的ViewHolder
     */
    public static final class ViewHolder extends RecyclerAdapter.ViewHolder<FunctionEntity>{
        private LinearLayout mRootLayout;
        private ImageView mIcon;
        private TextView mText;

        public ViewHolder(View itemView) {
            super(itemView);
            mRootLayout = (LinearLayout) itemView.findViewById(R.id.root_layout);
            mIcon = (ImageView) itemView.findViewById(R.id.icon);
            mText = (TextView) itemView.findViewById(R.id.text);
        }

        @Override
        protected void onBind(FunctionEntity entity) {
            // 设置分类名称
            mText.setText(entity.getTitleId());
            // 设置分类图标
            mIcon.setImageResource(entity.getDrawableId());
        }
    }
}
