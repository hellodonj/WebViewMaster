package com.lqwawa.intleducation.module.discovery.ui.lqcourse.home;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.widgets.PullRefreshView.NoScrollGridView;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerSpaceItemDecoration;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.LogUtil;
import com.lqwawa.intleducation.common.utils.StringUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.entity.LQBasicsOuterEntity;

import java.util.List;

/**
 * @author mrmedici
 * @desc 基础课程外层的Adapter
 */
public class NewBasicsOuterAdapter extends RecyclerAdapter<LQBasicsOuterEntity>{

    private boolean isHolder;
    private LQCourseNavigator mNavigator;

    private int[] colors = new int[]{
            R.color.basics_type_color_1,
            R.color.basics_type_color_2,
            R.color.basics_type_color_3,
            R.color.basics_type_color_4
    };

    public NewBasicsOuterAdapter(boolean isHolder) {
        super();
        this.isHolder = isHolder;
    }

    @Override
    protected int getItemViewType(int position, LQBasicsOuterEntity outerEntity) {
        return R.layout.item_new_basics_outer_layout;
    }

    @Override
    protected RecyclerAdapter.ViewHolder<LQBasicsOuterEntity> onCreateViewHolder(View root, int viewType) {
        return new ViewHolder(root);
    }

    /**
     * 设置基础课程科目点击回调的监听
     * @param navigator 回调对象
     */
    public void setNavigator(@NonNull LQCourseNavigator navigator){
        this.mNavigator = navigator;
    }

    /**
     * 是否是片段
     * @param isHolder true LQCourseFragment使用
     */
    public void setHolder(boolean isHolder){
        this.isHolder = isHolder;
    }

    public class ViewHolder extends RecyclerAdapter.ViewHolder<LQBasicsOuterEntity>{

        private TextView mConfigTitle;
        // private RecyclerView mRecycler;
        // private NewBasicsInnerRecyclerAdapter mAdapter;
        private NoScrollGridView mGridView;
        private NewBasicsInnerAdapter mAdapter;
        private ImageView mIvArrow;
        private View mDivider;

        public ViewHolder(View itemView) {
            super(itemView);
            mConfigTitle = (TextView) itemView.findViewById(R.id.tv_config_title);
            // mRecycler = (RecyclerView) itemView.findViewById(R.id.recycler);
            mGridView = (NoScrollGridView) itemView.findViewById(R.id.grid_view);
            // 新需求调整,首页展开
            /*if(isHolder){
                mGridView.setMeasureHeightFlag(false);
            }*/
            mIvArrow = (ImageView) itemView.findViewById(R.id.iv_arrow);
            // 初始化激活
            mIvArrow.setActivated(true);
            mDivider = itemView.findViewById(R.id.divider);
        }

        @Override
        protected void onBind(LQBasicsOuterEntity outerEntity) {
            LogUtil.e(NewBasicsOuterAdapter.class,"position:"+getAdapterPosition());
            StringUtil.fillSafeTextView(mConfigTitle,outerEntity.getDataName());
            int position = getAdapterPosition();
            // 默认第一次切割,最多只显示三个
            List<LQBasicsOuterEntity.LQBasicsInnerEntity> newList = outerEntity.getList();
            /*if(outerEntity.getList().size() > 3){
                newList = outerEntity.getList().subList(0,3);
            }*/
            mAdapter = new NewBasicsInnerAdapter(newList,UIUtil.getColor(colors[position % 4]));
            mGridView.setAdapter(mAdapter);

            mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // 点击具体的科目
                    if(EmptyUtil.isNotEmpty(mNavigator)){
                        List<LQBasicsOuterEntity.LQBasicsInnerEntity> list = outerEntity.getList();
                        LQBasicsOuterEntity.LQBasicsInnerEntity entity = list.get(position);
                        mNavigator.onClickBasicsSubject(entity);
                    }
                }
            });

            if(position == getItemCount() - 1){
                mDivider.setVisibility(View.GONE);
            }else{
                mDivider.setVisibility(View.VISIBLE);
            }

            if(isHolder && outerEntity.getList().size() > 3){
                // 是否显示箭头
                // 是片段，显示arrow
                mIvArrow.setVisibility(View.VISIBLE);
                mIvArrow.setEnabled(true);
            }else{
                mIvArrow.setEnabled(false);
                mIvArrow.setVisibility(View.INVISIBLE);
            }

            mIvArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 更换激活状态
                    mIvArrow.setActivated(!mIvArrow.isActivated());
                    // 开始启动动画
                    /*if(mIvArrow.isActivated()){
                        mAdapter = new NewBasicsInnerAdapter(outerEntity.getList(),UIUtil.getColor(colors[position % 4]));
                        mGridView.setAdapter(mAdapter);
                    }else{
                        if(outerEntity.getList().size() > 3){
                            List<LQBasicsOuterEntity.LQBasicsInnerEntity> newList = outerEntity.getList().subList(0,3);
                            mAdapter = new NewBasicsInnerAdapter(newList,UIUtil.getColor(colors[position % 4]));
                            mGridView.setAdapter(mAdapter);
                        }
                    }*/
                    startAnim(mGridView,outerEntity.getList().size(),mIvArrow.isActivated());
                }
            });

            /*GridLayoutManager mLayoutManager = new GridLayoutManager(UIUtil.getContext(),3){
                @Override
                public boolean canScrollVertically() {
                    return false;
                }
            };
            mRecycler.setNestedScrollingEnabled(false);
            mRecycler.setLayoutManager(mLayoutManager);
            mRecycler.addItemDecoration(new RecyclerSpaceItemDecoration(3,8,false));
            mAdapter = new NewBasicsInnerRecyclerAdapter();
            mRecycler.setLayoutManager(mLayoutManager);
            mAdapter.replace(outerEntity.getList());*/
        }
    }

    /**
     * 开始动画
     * @param targetView 作用动画的View
     * @param itemCount adapter count
     * @param expandAnim 是否是展开动画
     */
    private void startAnim(final NoScrollGridView targetView,int itemCount,boolean expandAnim){
        int totalLine = (int) Math.ceil(itemCount / 3.0);
        float minPercent = 1.0f / totalLine;
        ValueAnimator animator = null;
        if(expandAnim){
            targetView.setMeasureHeightFlag(true);
            animator = ValueAnimator.ofFloat(1.0f,minPercent);
        }else{
            targetView.setMeasureHeightFlag(false);
            animator = ValueAnimator.ofFloat(1.0f,minPercent);
        }

        targetView.postInvalidate();

        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(300);
        final int measureHeight = targetView.getMeasuredHeight();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                LogUtil.i(NewBasicsOuterAdapter.class,"animationValue:"+value);
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) targetView.getLayoutParams();
                if(expandAnim){
                    layoutParams.height = (int)(measureHeight / value);
                }else{
                    layoutParams.height = (int)(measureHeight * value);
                    layoutParams.height = (int)(measureHeight * minPercent);
                }
                targetView.setLayoutParams(layoutParams);
                targetView.postInvalidate();
            }
        });
        animator.start();
    }
}
