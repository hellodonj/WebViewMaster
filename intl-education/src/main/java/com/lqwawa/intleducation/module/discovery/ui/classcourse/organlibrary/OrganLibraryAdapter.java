package com.lqwawa.intleducation.module.discovery.ui.classcourse.organlibrary;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.utils.ResourceUtils;
import com.lqwawa.intleducation.base.utils.StringUtils;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.StringUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.common.utils.image.LQwawaImageUtil;
import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;

public class OrganLibraryAdapter extends RecyclerAdapter<LQCourseConfigEntity>{

    public OrganLibraryAdapter() {
        super();
    }

    @Override
    protected int getItemViewType(int position, LQCourseConfigEntity entity) {
        return R.layout.item_horizontal_image_text_layout;
    }

    @Override
    protected RecyclerAdapter.ViewHolder<LQCourseConfigEntity> onCreateViewHolder(View root, int viewType) {
        return new ViewHolder(root);
    }

    private class ViewHolder extends RecyclerAdapter.ViewHolder<LQCourseConfigEntity>{

        private View mRoot;
        private ImageView mIvAvatar;
        private TextView mTvContent;

        public ViewHolder(View itemView) {
            super(itemView);
            mRoot = itemView;
            mIvAvatar = (ImageView) itemView.findViewById(R.id.iv_avatar);
            mTvContent = (TextView) itemView.findViewById(R.id.tv_content);
        }

        @Override
        protected void onBind(LQCourseConfigEntity entity) {
            mRoot.setBackgroundColor(UIUtil.getColor(R.color.colorLight));

            StringUtil.fillSafeTextView(mTvContent,entity.getConfigValue());

            // 设置分类图标
            String thumbnail = entity.getThumbnail();
            if (!EmptyUtil.isEmpty(thumbnail)) {
                if (StringUtils.isValidWebResString(thumbnail)) {
                    LQwawaImageUtil.loadCommonIcon(mIvAvatar.getContext(),mIvAvatar,thumbnail);
                } else {
                    mIvAvatar.setImageResource(ResourceUtils.getDrawableId(UIUtil.getContext(),thumbnail));
                }
            }
        }
    }
}
