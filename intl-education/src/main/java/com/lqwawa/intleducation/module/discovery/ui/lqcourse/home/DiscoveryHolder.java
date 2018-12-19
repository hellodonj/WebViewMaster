package com.lqwawa.intleducation.module.discovery.ui.lqcourse.home;

import android.content.Context;
import android.support.annotation.NonNull;
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
import com.lqwawa.intleducation.factory.presenter.BaseContract;
import com.lqwawa.intleducation.module.discovery.ui.LQCourseCourseListActivity;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;

import java.util.ArrayList;
import java.util.List;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function LQ学程课程相关Holder
 * @date 2018/04/27 18:16
 * @history v1.0
 * **********************************
 */
public class DiscoveryHolder extends LinearLayout{

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    // 热门推荐标题布局
    private LinearLayout mHotTitleLayout;
    // 热门标题
    private TextView mHotTitle;

    // 热门推荐
    private CourseAdapter mHotAdapter;

    // 回调对象
    private LQCourseNavigator mNavigator;

    public DiscoveryHolder(Context context) {
        this(context,null);
    }

    public DiscoveryHolder(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public DiscoveryHolder(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        initView();
    }

    /**
     * 课程列表的View的相关初始化工作
     */
    private void initView(){
        initHotCourseView();
    }

    /**
     * 初始化热门推荐课程View
     */
    private void initHotCourseView(){
        View mHotView = mLayoutInflater.inflate(R.layout.item_discovery_course_layout,this);
        // 标题文本
        mHotTitle = (TextView) mHotView.findViewById(R.id.title_name);
        mHotTitle.setText(UIUtil.getString(R.string.hot_recommended));
        // 标题布局
        mHotTitleLayout = (LinearLayout) mHotView.findViewById(R.id.title_layout);
        // 热门推荐课程列表
        RecyclerView mHotRecycler = (RecyclerView) mHotView.findViewById(R.id.recycler);
        GridLayoutManager mHotLayoutManager = new GridLayoutManager(mContext,3){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        mHotRecycler.setLayoutManager(mHotLayoutManager);
        mHotAdapter = new CourseAdapter();
        mHotRecycler.setAdapter(mHotAdapter);

        mHotTitleLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!EmptyUtil.isEmpty(mNavigator)){
                    mNavigator.onClickCourseTitleLayout();
                }
            }
        });

        mHotAdapter.setListener(new RecyclerAdapter.AdapterListenerImpl<CourseVo>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, CourseVo courseVo) {
                super.onItemClick(holder, courseVo);
                if(!EmptyUtil.isEmpty(mNavigator)){
                    // 点击课程
                    mNavigator.onClickCourse(courseVo);
                }
            }
        });
    }

    /**
     * 设置标题 目前在机构主页课堂中动态设置使用
     * @param title 标题文本
     */
    public void setDiscoveryTitle(@NonNull String title){
        mHotTitle.setText(title);
    }

    /**
     * 设置回调对象,对课程等点击事件的监听
     * @param navigator
     */
    public void setNavigator(LQCourseNavigator navigator){
        this.mNavigator = navigator;
    }

    /**
     * 更新热门推荐数据
     * @param courseVos 热门推荐数据源
     */
    public void updateHotCourseData(List<CourseVo> courseVos){
        mHotAdapter.replace(courseVos);
    }
}
