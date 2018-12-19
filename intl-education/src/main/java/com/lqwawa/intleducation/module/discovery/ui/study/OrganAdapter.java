package com.lqwawa.intleducation.module.discovery.ui.study;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.common.utils.ImageUtil;
import com.lqwawa.intleducation.common.utils.StringUtil;
import com.lqwawa.intleducation.factory.data.entity.online.OnlineConfigEntity;
import com.lqwawa.intleducation.factory.data.entity.online.OnlineStudyOrganEntity;

/**
 * @author medici
 * @desc 在线学习机构Adapter
 */
public class OrganAdapter extends RecyclerAdapter<OnlineStudyOrganEntity>{

    private boolean mHorizontal;

    public OrganAdapter() {
    }

    public OrganAdapter(boolean horizontal) {
        this.mHorizontal = horizontal;
    }

    @Override
    protected int getItemViewType(int position, OnlineStudyOrganEntity entity) {
        if(mHorizontal){
            return R.layout.item_horizontal_online_study_organ_layout;
        }else{
            return R.layout.item_online_study_organ_layout;
        }
    }

    @Override
    protected ViewHolder<OnlineStudyOrganEntity> onCreateViewHolder(View root, int viewType) {
        return new OrganHolder(root);
    }

    private static class OrganHolder extends ViewHolder<OnlineStudyOrganEntity>{

        private ImageView mOrganAvatar;
        private TextView mOrganName;

        public OrganHolder(View itemView) {
            super(itemView);
            mOrganAvatar = (ImageView) itemView.findViewById(R.id.iv_organ_avatar);
            mOrganName = (TextView) itemView.findViewById(R.id.tv_organ_name);
        }

        @Override
        protected void onBind(OnlineStudyOrganEntity entity) {
            StringUtil.fillSafeTextView(mOrganName,entity.getName());
            ImageUtil.fillCircleView(mOrganAvatar,entity.getThumbnail(),R.drawable.ic_default_school_avatar);
        }
    }
}
