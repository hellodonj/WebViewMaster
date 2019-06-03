package com.lqwawa.intleducation.module.discovery.ui.videodetail;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.utils.ResIconUtils;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.factory.data.entity.course.VideoResourceEntity;

/**
 * @author: wangchao
 * @date: 2019/05/07
 * @desc:
 */
public class VideoResourceAdapter extends RecyclerAdapter<VideoResourceEntity> {

    private String[] resourceTypeNames;

    public VideoResourceAdapter(Context context) {
        resourceTypeNames =
                context.getResources().getStringArray(R.array.video_resource_types);
    }

    @Override
    protected int getItemViewType(int position, VideoResourceEntity videoResourceEntity) {
        return R.layout.item_video_resource_list;
    }

    @Override
    protected ViewHolder<VideoResourceEntity> onCreateViewHolder(View root, int viewType) {
        return new ResourceViewHolder(root);
    }

    private class ResourceViewHolder extends ViewHolder<VideoResourceEntity> {

        private TextView resourceTypeName;
        private ImageView resourceIcon;
        private TextView resourceName;

        public ResourceViewHolder(View itemView) {
            super(itemView);
            resourceTypeName = (TextView) itemView.findViewById(R.id.tv_resource_type_name);
            resourceIcon = (ImageView) itemView.findViewById(R.id.iv_resource_icon);
            resourceName = (TextView) itemView.findViewById(R.id.tv_resource_name);
        }

        @Override
        protected void onBind(VideoResourceEntity entity) {
            resourceName.setText(entity.getName());
            int type = entity.getType();
            if (type >= 1 && type <= 3) {
                resourceTypeName.setText(resourceTypeNames[type - 1]);
            }
            resourceIcon.setImageResource(ResIconUtils.resIconSparseArray.get(entity.getResType()).resId);
        }
    }
}
