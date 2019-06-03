package com.lqwawa.intleducation.module.discovery.ui.classcourse.courseselect;

import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.utils.ResourceUtils;
import com.lqwawa.intleducation.base.utils.StringUtils;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.ImageUtil;
import com.lqwawa.intleducation.common.utils.StringUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.common.utils.image.LQwawaImageUtil;
import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;
import com.lqwawa.intleducation.module.organcourse.OrganLibraryType;

/**
 * @author mrmedici
 * @desc 班级学程选择课程分类列表的Adapter
 */
public class CourseShopClassifyAdapter extends RecyclerAdapter<LQCourseConfigEntity>{

    private CourseShopClassifyNavigator mNavigator;

    public CourseShopClassifyAdapter() {
        super();
    }

    public CourseShopClassifyAdapter(CourseShopClassifyNavigator navigator) {
        this.mNavigator = navigator;
    }

    @Override
    protected int getItemViewType(int position, LQCourseConfigEntity entity) {
        return R.layout.item_horizontal_image_text_layout;
    }

    @Override
    protected RecyclerAdapter.ViewHolder<LQCourseConfigEntity> onCreateViewHolder(View root, int viewType) {
        return new ViewHolder(root);
    }

    public void setNavigator(@NonNull CourseShopClassifyNavigator navigator){
        this.mNavigator = navigator;
    }

    /**
     * @author mrmedici
     * @desc 班级学程选择课程的分类Holder
     */
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

            /*if(EmptyUtil.isEmpty(mNavigator) ||
                    !mNavigator.judgeClassifyAuthorizedInfo(entity)){*/
            if(!entity.isAuthorized()){
                // 未授权
                String configValue = entity.getConfigValue();
                String unAuthorized =
                        entity.getLibraryType() == OrganLibraryType.TYPE_LQCOURSE_SHOP ?
                                UIUtil.getString(R.string.label_unauthorized_container) : "";
                String showStr = configValue + unAuthorized;
                SpannableString spannableString = new SpannableString(configValue+unAuthorized);
                ForegroundColorSpan span = new ForegroundColorSpan(UIUtil.getColor(R.color.textSecond));
                spannableString.setSpan(span,configValue.length(),showStr.length(),Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                StringUtil.fillSafeTextView(mTvContent,spannableString);
            }else{
                StringUtil.fillSafeTextView(mTvContent,entity.getConfigValue());
            }

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
