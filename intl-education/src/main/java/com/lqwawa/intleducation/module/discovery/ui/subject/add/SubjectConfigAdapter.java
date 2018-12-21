package com.lqwawa.intleducation.module.discovery.ui.subject.add;

import android.view.View;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.common.utils.StringUtil;
import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;

import java.util.List;

/**
 * 老师设置科目选择分类的Adapter
 */
public class SubjectConfigAdapter extends RecyclerAdapter<LQCourseConfigEntity> {

    public SubjectConfigAdapter(List<LQCourseConfigEntity> entities) {
        super(entities, null);
    }

    @Override
    protected int getItemViewType(int position, LQCourseConfigEntity entity) {
        return R.layout.item_subject_config_layout;
    }

    @Override
    protected ViewHolder<LQCourseConfigEntity> onCreateViewHolder(View root, int viewType) {
        return new SubjectHolder(root);
    }

    static class SubjectHolder extends RecyclerAdapter.ViewHolder<LQCourseConfigEntity>{

        private TextView mTvContent;

        public SubjectHolder(View itemView) {
            super(itemView);
            mTvContent = (TextView) itemView.findViewById(R.id.tv_content);
        }

        @Override
        protected void onBind(LQCourseConfigEntity entity) {
            StringUtil.fillSafeTextView(mTvContent,entity.getConfigValue());

        }
    }
}
