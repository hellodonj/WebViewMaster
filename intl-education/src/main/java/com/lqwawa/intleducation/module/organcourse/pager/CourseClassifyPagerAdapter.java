package com.lqwawa.intleducation.module.organcourse.pager;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.utils.ResourceUtils;
import com.lqwawa.intleducation.base.utils.StringUtils;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.common.utils.DrawableUtil;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.ImageUtil;
import com.lqwawa.intleducation.common.utils.StringUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;
import com.lqwawa.intleducation.factory.data.entity.SchoolFunctionEntity;
import com.lqwawa.intleducation.module.organcourse.OrganLibraryType;
import com.lqwawa.tools.ScreenUtils;

import java.util.List;

import static com.lqwawa.intleducation.module.learn.ui.MyCourseListFragment.TAG;

/**
 * @author medici
 * @desc 学程馆分类菜单顶部的Adapter
 */
public class CourseClassifyPagerAdapter extends RecyclerAdapter<LQCourseConfigEntity> {

    private int screenWidth, itemWidth;

    public CourseClassifyPagerAdapter(Context context, List<LQCourseConfigEntity> entities, AdapterListener<LQCourseConfigEntity> listener) {
        super(entities, listener);
        screenWidth = ScreenUtils.getScreenWidth(context);
        int size = entities.size();
        if (size > 4 || size < 3) itemWidth = screenWidth / 4;
    }

    @Override
    protected int getItemViewType(int position, LQCourseConfigEntity entity) {
        return R.layout.item_lq_discovery_classify_layout;
    }

    @Override
    protected RecyclerAdapter.ViewHolder<LQCourseConfigEntity> onCreateViewHolder(View root, int viewType) {
        return new ViewHolder(root);
    }

    /**
     * 功能菜单Item的ViewHolder
     */
    private class ViewHolder extends RecyclerAdapter.ViewHolder<LQCourseConfigEntity> {

        private LinearLayout mClassifyRoot;
        private FrameLayout mAuthorizedLayout;
        private TextView mAuthorizedState;
        private ImageView mClassifyIcon;
        private TextView mClassifyName;

        public ViewHolder(View itemView) {
            super(itemView);
            mClassifyRoot = (LinearLayout) itemView.findViewById(R.id.classify_root);
            if (itemWidth != 0) mClassifyRoot.getLayoutParams().width = itemWidth;
            mAuthorizedLayout = (FrameLayout) itemView.findViewById(R.id.authorized_layout);
            mAuthorizedState = (TextView) itemView.findViewById(R.id.tv_authorized_state);
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
                    ImageUtil.fillNormalView(mClassifyIcon, entity.getThumbnail().trim());
                } else {
                    mClassifyIcon.setImageResource(ResourceUtils.getDrawableId(UIUtil.getContext(), thumbnail));
                }
            }

            mAuthorizedLayout.setVisibility(View.VISIBLE);
            // 是否授权
            mAuthorizedState.setActivated(entity.isAuthorized());
            // 配置左边Left
            DrawableUtil.initTextDrawableSize(mAuthorizedState, 16, DrawableUtil.Direction.left);
            if (entity.isAuthorized()) {
                // 已授权的大小
                StringUtil.fillSafeTextView(mAuthorizedState, UIUtil.getString(R.string.label_be_authorized));
            } else {
                // 未授权
                StringUtil.fillSafeTextView(mAuthorizedState, UIUtil.getString(R.string.label_unauthorized));
            }
            // 习学程馆显示授权状态，其他不显示
            mAuthorizedState.setVisibility(View.VISIBLE);
        }
    }
}
