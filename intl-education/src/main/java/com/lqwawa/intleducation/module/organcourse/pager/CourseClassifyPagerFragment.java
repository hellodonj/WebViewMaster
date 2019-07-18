package com.lqwawa.intleducation.module.organcourse.pager;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.IBaseFragment;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;
import com.lqwawa.intleducation.module.organcourse.CourseClassifyNavigator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @desc 实体机构学程馆首页分类展示页面
 */
public class CourseClassifyPagerFragment extends IBaseFragment{

    private CourseClassifyNavigator mNavigator;

    // 一级页面分类数据
    private static final String KEY_EXTRA_PAGER_OBJECT = "KEY_EXTRA_PAGER_OBJECT";

    private RecyclerView mRecycler;

    private CourseClassifyPagerAdapter mAdapter;
    private List<LQCourseConfigEntity> mConfigEntities;

    public static CourseClassifyPagerFragment newInstance(@NonNull ArrayList<LQCourseConfigEntity> entities) {
        CourseClassifyPagerFragment fragment = new CourseClassifyPagerFragment();
        Bundle arguments = new Bundle();
        arguments.putSerializable(KEY_EXTRA_PAGER_OBJECT, (Serializable) entities);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_course_classify_pager;
    }

    @Override
    protected boolean initArgs(Bundle bundle) {
        mConfigEntities = (List<LQCourseConfigEntity>) bundle.getSerializable(KEY_EXTRA_PAGER_OBJECT);
        if(EmptyUtil.isEmpty(mConfigEntities)){
            return false;
        }
        return super.initArgs(bundle);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mRecycler = (RecyclerView) mRootView.findViewById(R.id.recycler);
//        mRecycler = (RecyclerView) mRootView.findViewById(R.id.recycler);
        mRecycler.setLayoutManager(new GridLayoutManager(getContext(), mConfigEntities.size()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        mAdapter = new CourseClassifyPagerAdapter(getActivity(),mConfigEntities,new RecyclerAdapter.AdapterListenerImpl<LQCourseConfigEntity>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, LQCourseConfigEntity lqCourseConfigEntity) {
                super.onItemClick(holder, lqCourseConfigEntity);
                if(EmptyUtil.isNotEmpty(mNavigator)){
                    mNavigator.onClickConfigTitleLayout(lqCourseConfigEntity);
                }
            }
        });
        mRecycler.setAdapter(mAdapter);
    }

    /**
     * 返回该分类Pager的Adapter对象
     * @return RecyclerAdapter<LQCourseConfigEntity>
     */
    public RecyclerAdapter<LQCourseConfigEntity> getRecyclerAdapter(){
        return mAdapter;
    }

    /**
     * 设置点击回调的监听
     * @param navigator 回调对象
     */
    public void setNavigator(@NonNull CourseClassifyNavigator navigator){
        this.mNavigator = navigator;
    }
}
