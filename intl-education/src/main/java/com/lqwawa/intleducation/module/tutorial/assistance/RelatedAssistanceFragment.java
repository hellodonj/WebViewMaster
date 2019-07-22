package com.lqwawa.intleducation.module.tutorial.assistance;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.CourseEmptyView;
import com.lqwawa.intleducation.base.PresenterFragment;
import com.lqwawa.intleducation.common.interfaces.OnLoadStatusChangeListener;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.module.discovery.tool.LoginHelper;
import com.lqwawa.intleducation.module.discovery.ui.CourseDetailsActivity;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import java.util.List;

/**
 * 描述: 关联教辅
 * 作者|时间: djj on 2019/7/19 0019 上午 10:01
 */
public class RelatedAssistanceFragment extends PresenterFragment<RelatedAssistanceContract.Presenter>
        implements RelatedAssistanceContract.View {

    private static final String KEY_EXTRA_COURSE_ID = "KEY_EXTRA_COURSE_ID";
    private static RelatedAssistanceFragment INSTANCE = null;
    private RecyclerView mRecyclerView;
    // 关联帮辅的适配器
    private RelatedAssistanceAdapter mAdapter;
    private CourseEmptyView mEmptyLayout;

    // 课程Id
    private String mCourseId;
    private int mPageIndex;
    private OnLoadStatusChangeListener mListener;

    public static RelatedAssistanceFragment newInstance(@NonNull String courseId) {
        if (INSTANCE == null || true) {
            INSTANCE = new RelatedAssistanceFragment();
            Bundle bundle = new Bundle();
            bundle.putString(KEY_EXTRA_COURSE_ID, courseId);
            INSTANCE.setArguments(bundle);
        }
        return INSTANCE;
    }


    @Override
    protected RelatedAssistanceContract.Presenter initPresenter() {
        return new RelatedAssistancePresenter(this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_related_assistance_list;
    }

    @Override
    protected boolean initArgs(Bundle bundle) {
        mCourseId = bundle.getString(KEY_EXTRA_COURSE_ID, null);
        if (EmptyUtil.isEmpty(mCourseId)) {
            return false;
        }
        return super.initArgs(bundle);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.recycler_view);
        mEmptyLayout = (CourseEmptyView) mRootView.findViewById(R.id.empty_layout);

        mRecyclerView.setNestedScrollingEnabled(false);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext()){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new RelatedAssistanceAdapter(getActivity());
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new RelatedAssistanceAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (!UserHelper.isLogin()) {
                    LoginHelper.enterLogin(getActivity());
                    return;
                }
                CourseVo vo = (CourseVo) mAdapter.getItem(position);
                CourseDetailsActivity.start(getActivity(), vo.getCourseId(), true, UserHelper.getUserId());
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        requestSxRelationCourse(false);
    }

    /**
     * 请求列表数据
     *
     * @param moreData 是否加载更多
     */
    public void requestSxRelationCourse(boolean moreData) {
        if (moreData) {
            mPageIndex++;
            mPresenter.requestSxRelationCourse(mCourseId, mPageIndex);
        } else {
            mPageIndex = 0;
            mPresenter.requestSxRelationCourse(mCourseId, mPageIndex);
        }
    }

    /**
     * 设置下拉刷新回调的监听
     *
     * @param l
     */
    public void setOnLoadStatusChangeListener(OnLoadStatusChangeListener l) {
        this.mListener = l;
    }

    @Override
    public void updateSxRelationCourseView(@NonNull List<CourseVo> courseVos) {
        mAdapter = new RelatedAssistanceAdapter(getActivity());
        mAdapter.setData(courseVos);
        mRecyclerView.setAdapter(mAdapter);
        updateRelationCourseView(courseVos);

    }

    @Override
    public void updateMoreSxRelationCourseView(@NonNull List<CourseVo> courseVos) {
        // 设置数据
        mAdapter.addData(courseVos);
        mAdapter.notifyDataSetChanged();
        updateRelationCourseView(courseVos);
    }

    public void updateRelationCourseView(@NonNull List<CourseVo> entities) {
        if (EmptyUtil.isEmpty(entities)) {
            // 数据为空
            mRecyclerView.setVisibility(View.GONE);
            mEmptyLayout.setVisibility(View.VISIBLE);
        } else {
            // 数据不为空
            mRecyclerView.setVisibility(View.VISIBLE);
            mEmptyLayout.setVisibility(View.GONE);
        }

        // 回调给Activity,加载完毕
        if (EmptyUtil.isNotEmpty(mListener)) {
            mListener.onLoadSuccess();
        }

        // 回调给Activity,是否可以加载更多
        if (EmptyUtil.isNotEmpty(mListener)) {
            mListener.onLoadFinish(EmptyUtil.isNotEmpty(entities)
                    && entities.size() >= AppConfig.PAGE_SIZE);
        }
    }

    @Override
    public void showError(int str) {
        super.showError(str);
        if (EmptyUtil.isNotEmpty(mListener)) {
            mListener.onLoadFlailed();
        }
    }
}
