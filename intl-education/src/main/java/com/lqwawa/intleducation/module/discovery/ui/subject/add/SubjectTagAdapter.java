package com.lqwawa.intleducation.module.discovery.ui.subject.add;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.common.utils.StringUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;

import java.util.List;

/**
 * 流布局Adapter
 */
public class SubjectTagAdapter extends TagAdapter<LQCourseConfigEntity> {

    public SubjectTagAdapter(List<LQCourseConfigEntity> entities) {
        super(entities);
    }

    @Override
    public View getView(FlowLayout parent, int position, LQCourseConfigEntity entity) {
        View view = UIUtil.inflate(R.layout.item_subject_config_layout);
        SubjectHolder holder = new SubjectHolder(view);
        holder.onBind(entity);
        return view;
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
            mTvContent.setActivated(true);
        }
    }
}
