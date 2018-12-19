package com.lqwawa.intleducation.module.organcourse;

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
import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.home.CourseAdapter;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.home.LQCourseNavigator;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;

import java.util.List;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function LQ学程课程相关Holder
 * @date 2018/08/02 18:16
 * @history v1.0
 * **********************************
 */
public class CourseClassifyItemHolder extends LinearLayout{

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private View mRootView;
    // 标题布局
    private LinearLayout mTitleLayout;
    // 标题
    private TextView mTitle;

    // 课程的Adapter
    private CourseAdapter mAdapter;

    // 回调对象
    private CourseClassifyNavigator mNavigator;
    // 数据实体
    private LQCourseConfigEntity mConfigEntity;

    public CourseClassifyItemHolder(Context context) {
        this(context,null);
    }

    public CourseClassifyItemHolder(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CourseClassifyItemHolder(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        initView();
    }

    /**
     * 课程列表的View的相关初始化工作
     */
    private void initView(){
        initClassifyView();
    }

    /**
     * 初始化热门推荐课程View
     */
    private void initClassifyView(){
        final View view = mRootView = mLayoutInflater.inflate(R.layout.holder_course_shop_classify_layout,this);
        // 标题文本
        mTitle = (TextView) view.findViewById(R.id.title_name);
        // 标题布局
        mTitleLayout = (LinearLayout) view.findViewById(R.id.title_layout);
        // 热门推荐课程列表
        RecyclerView mRecycler = (RecyclerView) view.findViewById(R.id.recycler);
        mRecycler.setNestedScrollingEnabled(false);
        GridLayoutManager mHotLayoutManager = new GridLayoutManager(mContext,3){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        mRecycler.setLayoutManager(mHotLayoutManager);
        mAdapter = new CourseAdapter();
        mRecycler.setAdapter(mAdapter);

        mTitleLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!EmptyUtil.isEmpty(mNavigator)){
                    mNavigator.onClickConfigTitleLayout(mConfigEntity);
                }
            }
        });

        mAdapter.setListener(new RecyclerAdapter.AdapterListenerImpl<CourseVo>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, CourseVo courseVo) {
                super.onItemClick(holder, courseVo);
                if(!EmptyUtil.isEmpty(mNavigator)){
                    // 点击课程
                    mNavigator.onClickCourse(mConfigEntity,courseVo);
                }
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
     * 设置回调对象,对课程等点击事件的监听
     * @param navigator
     */
    public void setNavigator(CourseClassifyNavigator navigator){
        this.mNavigator = navigator;
    }

    /**
     * 更新学程馆分类的数据实体
     * @param entity 分类数据
     */
    public void updateClassifyView(@NonNull LQCourseConfigEntity entity){
        this.mConfigEntity = entity;
        mTitle.setText(entity.getConfigValue());
        List<CourseVo> courseList = entity.getCourseList();
        if(EmptyUtil.isNotEmpty(courseList) && courseList.size() > 3){
            // 当前课程大于三个课程
            courseList = courseList.subList(0,3);
        }
        mAdapter.replace(courseList);
    }
}
