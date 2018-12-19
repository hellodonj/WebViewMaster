package com.lqwawa.intleducation.module.discovery.ui.study;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerSpaceItemDecoration;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.ImageUtil;
import com.lqwawa.intleducation.common.utils.LogUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;
import com.lqwawa.intleducation.factory.data.entity.OnlineClassEntity;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.home.EnglishCourseInternationalHolder;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.home.LQCourseNavigator;
import com.lqwawa.intleducation.module.onclass.OnlineClassAdapter;

import java.util.List;

/**
 * @author mrmedici
 * @desc 在线学习，最新课堂，热门数据Item
 */
public class OnlineStudyItemHolder extends FrameLayout{

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private View mRootView;
    private LinearLayout mTitleLayout;
    private TextView mTitleContent;
    private RecyclerView mClassRecycler;
    private OnlineClassAdapter mClassAdapter;
    private OnlineStudyNavigator mNavigator;

    private int mSort;

    public OnlineStudyItemHolder(Context context) {
        this(context,null);
    }

    public OnlineStudyItemHolder(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public OnlineStudyItemHolder(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        initView();
    }

    /**
     * 初始化View相关信息
     */
    private void initView(){
        final View view = mRootView = mLayoutInflater.inflate(R.layout.holder_online_study_class_layout,this);
        // 标题文本
        mTitleContent = (TextView) view.findViewById(R.id.title_content);
        // mTitleContent.setText(UIUtil.getString(R.string.label_english_international_course));
        // 标题布局
        mTitleLayout = (LinearLayout) view.findViewById(R.id.title_layout);
        mClassRecycler = (RecyclerView) mRootView.findViewById(R.id.recycler);
        mClassRecycler.setNestedScrollingEnabled(false);
        GridLayoutManager mLayoutManager = new GridLayoutManager(getContext(),2){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        mClassRecycler.setLayoutManager(mLayoutManager);
        mClassRecycler.addItemDecoration(new RecyclerSpaceItemDecoration(2,8,false));
        mClassAdapter = new OnlineClassAdapter();
        mClassRecycler.setAdapter(mClassAdapter);

        mClassAdapter.setListener(new RecyclerAdapter.AdapterListenerImpl<OnlineClassEntity>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, OnlineClassEntity onlineClassEntity) {
                super.onItemClick(holder, onlineClassEntity);
                if(EmptyUtil.isNotEmpty(mNavigator)){
                    mNavigator.onClickClass(onlineClassEntity);
                }
            }
        });

        mTitleLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(EmptyUtil.isNotEmpty(mNavigator)){
                    mNavigator.onClickTitleLayout(mSort);
                }
            }
        });

        mRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int width = mRootView.getMeasuredWidth();
                int height = mRootView.getMeasuredHeight();
                View view = new View(UIUtil.getContext());
                view.setBackgroundColor(UIUtil.getColor(R.color.windowsBackground));
                LayoutParams params = new LayoutParams(width,height);
                view.setLayoutParams(params);
                // OnlineStudyItemHolder.this.addView(view,0);

                mRootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    /**
     * 获取根布局
     * @return 根布局View
     */
    public View getRootView(){
        return mRootView;
    }

    /**
     * 获取到数据,刷新数据
     * @param sort 规则
     * @param entities 班级数据
     */
    public void updateView(@NonNull @OnlineStudyType.OnlineStudyRes int sort, @NonNull List<OnlineClassEntity> entities){
        mSort = sort;
        if(sort == OnlineStudyType.SORT_HOT){
            // 热门数据
            mTitleContent.setText(UIUtil.getString(R.string.label_online_study_hot_data));
        }else if(sort == OnlineStudyType.SORT_LATEST){
            // 最新数据
            mTitleContent.setText(UIUtil.getString(R.string.label_online_study_latest_data));
        }
        mClassAdapter.replace(entities);
    }

    /**
     * 设置点击事件的监听
     * @param navigator 回调接口对象
     */
    public void setOnlineStudyNavigator(OnlineStudyNavigator navigator){
        this.mNavigator = navigator;
    }

}
