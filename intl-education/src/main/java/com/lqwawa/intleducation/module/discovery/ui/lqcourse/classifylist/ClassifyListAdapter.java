package com.lqwawa.intleducation.module.discovery.ui.lqcourse.classifylist;

import android.view.View;
import android.widget.ImageView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.ImageUtil;
import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 分类数据Adapter
 * @date 2018/05/02 11:32
 * @history v1.0
 * **********************************
 */
public class ClassifyListAdapter extends RecyclerAdapter<LQCourseConfigEntity>{

    private ClassifyListNavigator mNavigator;

    @Override
    protected int getItemViewType(int position, LQCourseConfigEntity entity) {
        return R.layout.item_classify_list_layout;
    }

    @Override
    protected RecyclerAdapter.ViewHolder<LQCourseConfigEntity> onCreateViewHolder(View root, int viewType) {
        return new ViewHolder(root);
    }

    /**
     * 设置回调监听
     * @param navigator 回调对象
     */
    public void setNavigator(ClassifyListNavigator navigator){
        mNavigator = navigator;
    }

    /**
     * 分类数据ViewHolder
     */
    private final class ViewHolder extends RecyclerAdapter.ViewHolder<LQCourseConfigEntity>{

        private ImageView mClassifyView;

        public ViewHolder(View itemView) {
            super(itemView);
            mClassifyView = (ImageView) itemView.findViewById(R.id.iv_classify);
        }

        @Override
        protected void onBind(final LQCourseConfigEntity entity) {
            ImageUtil.fillDefaultView(mClassifyView,entity.getPicture());
            mClassifyView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!EmptyUtil.isEmpty(mNavigator)){
                        mNavigator.onItemClick(entity);
                    }
                }
            });
        }
    }
}
