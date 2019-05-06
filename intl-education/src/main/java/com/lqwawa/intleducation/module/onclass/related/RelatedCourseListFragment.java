package com.lqwawa.intleducation.module.onclass.related;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.CourseEmptyView;
import com.lqwawa.intleducation.base.PresenterActivity;
import com.lqwawa.intleducation.base.PresenterFragment;
import com.lqwawa.intleducation.base.widgets.PullRefreshView.PullToRefreshView;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.factory.data.entity.ClassDetailEntity;
import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;
import com.lqwawa.intleducation.module.discovery.adapter.CourseListAdapter;
import com.lqwawa.intleducation.module.discovery.ui.CourseDetailsActivity;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.filtrate.CourseFiltrateActivity;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.filtrate.state.GroupFiltrateState;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;
import com.lqwawa.intleducation.module.onclass.detail.base.BaseClassDetailActivity;
import com.lqwawa.intleducation.module.onclass.detail.notjoin.ClassDetailNavigator;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import java.util.Date;
import java.util.List;

/**
 * @author mrmedici
 * @desc 关联课程的Fragment
 */
public class RelatedCourseListFragment extends PresenterFragment<RelatedCourseListContract.Presenter>
        implements RelatedCourseListContract.View {

    private NestedScrollView mNestedView;

    private TopBar mTopBar;
    // private PullToRefreshView mRefreshLayout;
    private ListView mListView;
    private CourseListAdapter mCourseListAdapter;
    private CourseEmptyView mEmptyLayout;
    private Button mBtnMoreCourse;

    private RelatedCourseParams mRelatedParams;
    private List<CourseVo> mCourseVos;
    private ClassDetailEntity.ParamBean mParam;

    /**
     * 关联课程列表的入口
     *
     * @param params 核心参数
     */
    public static Fragment newInstance(@NonNull RelatedCourseParams params) {
        RelatedCourseListFragment fragment = new RelatedCourseListFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(FRAGMENT_BUNDLE_OBJECT, params);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_related_course_list;
    }

    @Override
    protected RelatedCourseListContract.Presenter initPresenter() {
        return new RelatedCourseListPresenter(this);
    }

    @Override
    protected boolean initArgs(@NonNull Bundle bundle) {
        if (bundle.containsKey(FRAGMENT_BUNDLE_OBJECT)) {
            mRelatedParams = (RelatedCourseParams) bundle.getSerializable(FRAGMENT_BUNDLE_OBJECT);
            mParam = mRelatedParams.getParam();
            mCourseVos = mRelatedParams.getRelatedCourse();
        }

        if (EmptyUtil.isEmpty(mParam) || EmptyUtil.isEmpty(mCourseVos)) return false;
        return super.initArgs(bundle);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        // mRefreshLayout = (PullToRefreshView) mRootView.findViewById(R.id.refresh_layout);
        mNestedView = (NestedScrollView) mRootView.findViewById(R.id.nested_view);
        mListView = (ListView) mRootView.findViewById(R.id.listView);
        mEmptyLayout = (CourseEmptyView) mRootView.findViewById(R.id.empty_layout);
        mBtnMoreCourse = (Button) mRootView.findViewById(R.id.btn_more_course);
        mBtnMoreCourse.setOnClickListener(view -> {
            watchMoreCourse();
        });

        mCourseListAdapter = new CourseListAdapter(getActivity());
        mCourseListAdapter.setData(mCourseVos);
        mListView.setAdapter(mCourseListAdapter);

        if (EmptyUtil.isEmpty(mCourseVos)) {
            // 数据为空
            // mRefreshLayout.setVisibility(View.GONE);
            mEmptyLayout.setVisibility(View.VISIBLE);
        } else {
            // 数据不为空
            // mRefreshLayout.setVisibility(View.VISIBLE);
            mEmptyLayout.setVisibility(View.GONE);
        }

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CourseVo vo = (CourseVo) mCourseListAdapter.getItem(position);
                CourseDetailsActivity.start(getActivity(), vo.getId(), true,
                        UserHelper.getUserId(), false, true, false);
            }
        });

        // 下拉刷新
        /*mRefreshLayout.setLastUpdated(new Date().toLocaleString());
        mRefreshLayout.setLoadMoreEnable(false);
        mRefreshLayout.setOnHeaderRefreshListener(new PullToRefreshView.OnHeaderRefreshListener() {
            @Override
            public void onHeaderRefresh(PullToRefreshView view) {
                mRefreshLayout.onHeaderRefreshComplete();
            }
        });*/
    }

    /**
     * 查看更多课程
     */
    private void watchMoreCourse() {
        // showLoading();
        mPresenter.requestConfigWithParam(mParam);
    }

    @Override
    public void updateConfigView(@NonNull int parentId, @NonNull ClassDetailEntity.ParamBean param, @NonNull List<LQCourseConfigEntity> entities) {
        if (EmptyUtil.isNotEmpty(entities)) {
            // 解决标签不对应的问题
            boolean haveEntity = false;
            for (LQCourseConfigEntity entity : entities) {
                if (entity.getId() == parentId) {
                    // 找到该实体
                    GroupFiltrateState state = new GroupFiltrateState(entity);
                    entity.setParamTwoId(Integer.parseInt(param.getParamTwoId()));
                    entity.setParamThreeId(Integer.parseInt(param.getParamThreeId()));
                    // 用Server返回的名字
                    entity.setConfigValue(param.getRelationName());
                    // 如果基础课程可能会，标题错误
                    CourseFiltrateActivity.show(getActivity(), entity, state);

                    haveEntity = true;
                    break;
                }
            }

            if (!haveEntity) {
                LQCourseConfigEntity entity = new LQCourseConfigEntity();
                entity.setParamTwoId(Integer.parseInt(param.getParamTwoId()));
                entity.setParamThreeId(Integer.parseInt(param.getParamThreeId()));
                entity.setId(parentId);
                entity.setLevel(param.getLevel());
                entity.setConfigType(2);
                entity.setConfigValue(param.getRelationName());
                GroupFiltrateState state = new GroupFiltrateState(entity);
                // 如果基础课程可能会，标题错误
                CourseFiltrateActivity.show(getActivity(), entity, state);
            }
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        Activity activity = getActivity();
        if (activity instanceof ClassDetailNavigator) {
            ClassDetailNavigator navigator = (ClassDetailNavigator) activity;
            navigator.onCommentChanged(getUserVisibleHint());
        }

        if (getUserVisibleHint()) {
            // 显示了课堂简介
            if (getActivity() instanceof BaseClassDetailActivity) {
                BaseClassDetailActivity parentActivity = (BaseClassDetailActivity) getActivity();
                parentActivity.addRefreshView(mNestedView);
                parentActivity.getRefreshLayout().setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        parentActivity.refreshData();
                    }
                });
            }
        }
    }
}
