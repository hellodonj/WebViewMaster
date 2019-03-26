package com.lqwawa.intleducation.module.tutorial.course;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.CourseEmptyView;
import com.lqwawa.intleducation.base.PresenterFragment;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerItemDecoration;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerSpaceItemDecoration;
import com.lqwawa.intleducation.common.interfaces.OnLoadStatusChangeListener;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.entity.course.TutorialGroupEntity;
import com.lqwawa.intleducation.module.discovery.tool.LoginHelper;
import com.lqwawa.intleducation.module.learn.tool.TaskSliderHelper;
import com.lqwawa.intleducation.module.onclass.OnlineClassListFragment;
import com.lqwawa.intleducation.module.tutorial.course.filtrate.TutorialFiltrateGroupActivity;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import java.util.List;

/**
 * @author mrmedici
 * @desc 帮辅群功能
 */
public class TutorialGroupFragment extends PresenterFragment<TutorialGroupContract.Presenter>
    implements TutorialGroupContract.View,View.OnClickListener{

    private static final String KEY_EXTRA_COURSE_ID = "KEY_EXTRA_COURSE_ID";
    private static final String KEY_EXTRA_MEMBER_ID = "KEY_EXTRA_MEMBER_ID";

    private RecyclerView mRecycler;
    private CourseEmptyView mEmptyLayout;
    private Button mBtnMoreGroup;

    private TutorialGroupAdapter mGroupAdapter;
    private OnLoadStatusChangeListener mListener;

    private String mCourseId;
    private String mCurMemberId;

    private int pageIndex;

    public static TutorialGroupFragment newInstance(@NonNull String courseId,
                                                    @NonNull String memberId){
        TutorialGroupFragment fragment = new TutorialGroupFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_EXTRA_COURSE_ID,courseId);
        bundle.putString(KEY_EXTRA_MEMBER_ID,memberId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected TutorialGroupContract.Presenter initPresenter() {
        return new TutorialGroupPresenter(this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_tutorial_group;
    }

    @Override
    protected boolean initArgs(Bundle bundle) {
        mCourseId = bundle.getString(KEY_EXTRA_COURSE_ID);
        mCurMemberId = bundle.getString(KEY_EXTRA_MEMBER_ID,"");
        if(EmptyUtil.isEmpty(mCourseId)){
            return false;
        }
        return super.initArgs(bundle);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mRecycler = (RecyclerView) mRootView.findViewById(R.id.recycler);
        mEmptyLayout = (CourseEmptyView) mRootView.findViewById(R.id.empty_layout);
        mBtnMoreGroup = (Button) mRootView.findViewById(R.id.btn_more_group);
        mBtnMoreGroup.setOnClickListener(this);
        mRecycler.setNestedScrollingEnabled(false);
        GridLayoutManager mLayoutManager = new GridLayoutManager(getActivity(),2){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        mRecycler.setLayoutManager(mLayoutManager);
        mRecycler.addItemDecoration(new RecyclerSpaceItemDecoration(2,8));
        mGroupAdapter = new TutorialGroupAdapter(null);
        mRecycler.setAdapter(mGroupAdapter);

        mGroupAdapter.setCallback(new TutorialGroupAdapter.EntityCallback() {
            @Override
            public void onAddTutorial(int position, @NonNull TutorialGroupEntity entity) {
                // 先判断是否登录
                if(!UserHelper.isLogin()){
                    LoginHelper.enterLogin(getActivity());
                    return;
                }

                // 添加帮辅,只添加自己的
                String memberId = UserHelper.getUserId();
                mPresenter.requestAddTutorByStudentId(memberId,entity.getCreateId(),entity.getCreateName());
            }
        });

        mGroupAdapter.setListener(new RecyclerAdapter.AdapterListenerImpl<TutorialGroupEntity>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, TutorialGroupEntity entity) {
                super.onItemClick(holder, entity);
                if(EmptyUtil.isNotEmpty(TaskSliderHelper.onTutorialMarkingListener)){
                    TaskSliderHelper.onTutorialMarkingListener.enterTutorialHomePager(getActivity(),entity.getCreateId(),entity.getCreateName());
                }
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        onHeaderRefresh();
    }

    // 设置下拉刷新回调的监听
    public void setOnLoadStatusChangeListener(OnLoadStatusChangeListener l){
        this.mListener = l;
    }

    // 供Activity调用下拉刷新
    public void onHeaderRefresh(){
        pageIndex = 0;
        // 帮辅群只显示自己的，不显示孩子的
        String memberId = UserHelper.getUserId();
        mPresenter.requestTutorDataByCourseId(mCourseId,memberId,pageIndex);
    }


    // 加载更多
    public void getMore(){
        // 帮辅群只显示自己的，不显示孩子的
        String memberId = UserHelper.getUserId();
        mPresenter.requestTutorDataByCourseId(mCourseId,memberId,++pageIndex);
    }

    @Override
    public void updateTutorView(List<TutorialGroupEntity> entities) {
        mGroupAdapter.replace(entities);
        refreshAndNotify(entities);
    }

    @Override
    public void updateMoreTutorView(List<TutorialGroupEntity> entities) {
        mGroupAdapter.add(entities);
        refreshAndNotify(entities);
    }

    private void refreshAndNotify(List<TutorialGroupEntity> entities){
        if(EmptyUtil.isEmpty(entities)){
            mEmptyLayout.setVisibility(View.VISIBLE);
            mRecycler.setVisibility(View.GONE);
        }else{
            mEmptyLayout.setVisibility(View.GONE);
            mRecycler.setVisibility(View.VISIBLE);
        }

        // 回调给Activity,加载完毕
        if(EmptyUtil.isNotEmpty(mListener)){
            mListener.onLoadSuccess();
        }

        // 回调给Activity,是否可以加载更多
        if (EmptyUtil.isNotEmpty(mListener)) {
            mListener.onLoadFinish(EmptyUtil.isNotEmpty(entities)
                    && entities.size() >= AppConfig.PAGE_SIZE);
        }
    }

    @Override
    public void updateAddTutorByStudentIdView(boolean result) {
        if(result){
            UIUtil.showToastSafe(R.string.label_added_tutorial_succeed);
            // 刷新UI
            onHeaderRefresh();
        }else{
            UIUtil.showToastSafe(R.string.label_added_tutorial_failed);
        }
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if(viewId == R.id.btn_more_group){
            TutorialFiltrateGroupActivity.show(getActivity(),mCurMemberId);
        }
    }

    @Override
    public void showError(int str) {
        super.showError(str);
        if(EmptyUtil.isNotEmpty(mListener)){
            mListener.onLoadFlailed();
        }
    }

}
