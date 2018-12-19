package com.lqwawa.intleducation.module.discovery.ui.lqcourse.home;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.utils.ResourceUtils;
import com.lqwawa.intleducation.base.utils.StringUtils;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.common.utils.DrawableUtil;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.ImageUtil;
import com.lqwawa.intleducation.common.utils.SizeUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 分类列表的Adapter
 * @date 2018/04/27 16:40
 * @history v1.0
 * **********************************
 */
public class ClassifyAdapter extends RecyclerAdapter<LQCourseConfigEntity>{

    @Override
    protected int getItemViewType(int position, LQCourseConfigEntity classifyVo) {
        return R.layout.item_lq_discovery_classify_layout;
    }

    @Override
    protected RecyclerAdapter.ViewHolder<LQCourseConfigEntity> onCreateViewHolder(View root, int viewType) {
        return new ViewHolder(root);
    }

    /**
     * 分类数据的ViewHolder
     */
    public static final class ViewHolder extends RecyclerAdapter.ViewHolder<LQCourseConfigEntity>{
        private LinearLayout mClassifyRoot;
        private ImageView mClassifyIcon;
        private TextView mClassifyName;

        public ViewHolder(View itemView) {
            super(itemView);
            mClassifyRoot = (LinearLayout) itemView.findViewById(R.id.classify_root);
            mClassifyIcon = (ImageView) itemView.findViewById(R.id.classify_img);
            mClassifyName = (TextView) itemView.findViewById(R.id.classify_name);
        }

        @Override
        protected void onBind(LQCourseConfigEntity entity) {
            // 设置分类名称
            mClassifyName.setText(entity.getConfigValue());
            // 设置分类图标
            String thumbnail = entity.getThumbnail();
            if (!EmptyUtil.isEmpty(thumbnail)) {
                if (StringUtils.isValidWebResString(thumbnail)) {
                    ImageUtil.fillClassifyIcon(mClassifyIcon,entity.getThumbnail().trim());
                } else {
                    mClassifyIcon.setImageResource(ResourceUtils.getDrawableId(UIUtil.getContext(),thumbnail));
                }
            }

            // 设置背景
            // int position = getLayoutPosition();
            // int bgColor = AppConfig.BaseConfig.ClassifybackColors[position % 6];
            // mClassifyRoot.setBackground(DrawableUtil.createDrawable(bgColor,bgColor, SizeUtil.dp2px(4)));
        }
    }
}
