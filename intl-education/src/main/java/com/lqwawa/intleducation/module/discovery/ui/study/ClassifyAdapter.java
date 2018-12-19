package com.lqwawa.intleducation.module.discovery.ui.study;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.common.utils.ImageUtil;
import com.lqwawa.intleducation.common.utils.StringUtil;
import com.lqwawa.intleducation.common.utils.image.LQwawaImageUtil;
import com.lqwawa.intleducation.factory.data.entity.online.NewOnlineConfigEntity;

/**
 * @author medici
 * @desc 在线学习Header 分类Adapter
 */
public class ClassifyAdapter extends RecyclerAdapter<NewOnlineConfigEntity>{

    @Override
    protected int getItemViewType(int position, NewOnlineConfigEntity onlineConfigEntity) {
        return R.layout.item_online_study_classify_layout;
    }

    @Override
    protected ViewHolder<NewOnlineConfigEntity> onCreateViewHolder(View root, int viewType) {
        return new ClassifyHolder(root);
    }

    private static class ClassifyHolder extends ViewHolder<NewOnlineConfigEntity>{

        private ImageView mClassifyAvatar;
        private TextView mClassifyName;

        public ClassifyHolder(View itemView) {
            super(itemView);
            mClassifyAvatar = (ImageView) itemView.findViewById(R.id.iv_classify_avatar);
            mClassifyName = (TextView) itemView.findViewById(R.id.tv_classify_name);
        }

        @Override
        protected void onBind(NewOnlineConfigEntity entity) {
            StringUtil.fillSafeTextView(mClassifyName,entity.getConfigValue());
            LQwawaImageUtil.loadCommonIcon(mClassifyAvatar.getContext(),mClassifyAvatar,entity.getThumbnail());
            // ImageUtil.fillNormalView(mClassifyAvatar,entity.getThumbnail());
        }
    }
}
