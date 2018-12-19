package com.lqwawa.intleducation.module.discovery.ui.lqcourse.home;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;
import com.lqwawa.intleducation.module.learn.vo.LiveVo;

import java.util.List;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 记得写注释喲
 * @date 2018/05/02 16:18
 * @history v1.0
 * **********************************
 */
public class LiveHolder extends LinearLayout {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    // 热门推荐标题布局
    private LinearLayout mLiveTitleLayout;

    // 热门推荐
    private LiveAdapter mLiveAdapter;

    // 回调对象
    private LQCourseNavigator mNavigator;

    public LiveHolder(Context context) {
        this(context,null);
    }

    public LiveHolder(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public LiveHolder(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        initView();
    }

    /**
     * 课程列表的View的相关初始化工作
     */
    private void initView(){
        View mLiveView = mLayoutInflater.inflate(R.layout.item_discovery_course_layout,this);
        // 标题文本
        TextView liveTitleText = (TextView) mLiveView.findViewById(R.id.title_name);
        liveTitleText.setText(UIUtil.getString(R.string.live_room));
        // 标题布局
        mLiveTitleLayout = (LinearLayout) mLiveView.findViewById(R.id.title_layout);
        // 热门推荐课程列表
        RecyclerView mLiveRecycler = (RecyclerView) mLiveView.findViewById(R.id.recycler);
        GridLayoutManager mLiveLayoutManager = new GridLayoutManager(mContext,2){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        mLiveRecycler.setLayoutManager(mLiveLayoutManager);
        mLiveAdapter = new LiveAdapter();
        mLiveRecycler.setAdapter(mLiveAdapter);

        mLiveTitleLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!EmptyUtil.isEmpty(mNavigator)){
                    mNavigator.onClickLiveTitleLayout();
                }
            }
        });

        mLiveAdapter.setListener(new RecyclerAdapter.AdapterListenerImpl<LiveVo>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, LiveVo liveVo) {
                super.onItemClick(holder, liveVo);
                if(!EmptyUtil.isEmpty(mNavigator)){
                    // 点击直播选项
                    mNavigator.onClickLive(liveVo);
                }
            }
        });

    }

    /**
     * 设置回调对象,对课程等点击事件的监听
     * @param navigator
     */
    public void setNavigator(LQCourseNavigator navigator){
        this.mNavigator = navigator;
    }

    /**
     * 更新直播数据
     * @param liveVos 直播数据源
     */
    public void updateLiveData(List<LiveVo> liveVos){
        mLiveAdapter.replace(liveVos);
    }

}
