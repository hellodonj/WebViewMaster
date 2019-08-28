package com.lqwawa.intleducation.module.organcourse.filtrate.pager;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.GridView;

import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.CourseEmptyView;
import com.lqwawa.intleducation.base.PresenterFragment;
import com.lqwawa.intleducation.base.utils.DisplayUtil;
import com.lqwawa.intleducation.base.widgets.NoPermissionView;
import com.lqwawa.intleducation.base.widgets.PullRefreshView.PullToRefreshView;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.school.SchoolInfoEntity;
import com.lqwawa.intleducation.factory.helper.SchoolHelper;
import com.lqwawa.intleducation.module.discovery.adapter.CourseListAdapter;
import com.lqwawa.intleducation.module.discovery.ui.CourseDetailsActivity;
import com.lqwawa.intleducation.module.discovery.ui.coursedetail.CourseDetailParams;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;
import com.lqwawa.intleducation.module.organcourse.OrganLibraryType;
import com.lqwawa.intleducation.module.organcourse.OrganLibraryUtils;
import com.lqwawa.intleducation.module.organcourse.ShopResourceData;
import com.lqwawa.intleducation.module.organcourse.filtrate.OrganCourseFiltrateNavigator;
import com.lqwawa.intleducation.module.user.tool.UserHelper;
import com.lqwawa.intleducation.module.watchcourse.WatchCourseResourceActivity;

import java.util.Date;
import java.util.List;

/**
 * @author medici
 * @desc 学程馆筛选子页面Fragment
 */
public class OrganCourseFiltratePagerFragment extends PresenterFragment<OrganCourseFiltratePagerContract.Presenter>
        implements OrganCourseFiltratePagerContract.View, OrganCourseFiltrateNavigator {

    // 下拉刷新布局
    private PullToRefreshView mRefreshLayout;
    // 空布局
    private CourseEmptyView mEmptyView;
    private NoPermissionView mNoPermissionView;
    // 列表布局
    private GridView mGridView;
    private CourseListAdapter mCourseListAdapter;
    // 当前页

    private OrganCourseFiltratePagerParams mParams;
    private ShopResourceData mResourceData;
    private int mPageIndex;
    private String[] mLibraryNames;

    public static OrganCourseFiltratePagerFragment newInstance(OrganCourseFiltratePagerParams params) {
        OrganCourseFiltratePagerFragment fragment = new OrganCourseFiltratePagerFragment();
        Bundle arguments = new Bundle();
        arguments.putSerializable(OrganCourseFiltratePagerParams.class.getSimpleName(), params);
        fragment.setArguments(arguments);
        return fragment;
    }


    @Override
    protected OrganCourseFiltratePagerContract.Presenter initPresenter() {
        return new OrganCourseFiltratePagerPresenter(this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_organ_course_filtrate_pager;
    }

    @Override
    protected boolean initArgs(Bundle bundle) {
        mParams = (OrganCourseFiltratePagerParams) bundle.getSerializable(OrganCourseFiltratePagerParams.class.getSimpleName());
        if (EmptyUtil.isEmpty(mParams)) {
            return false;
        }
        mResourceData = mParams.getShopResourceData();
        mLibraryNames = getResources().getStringArray(R.array.organ_library_names);
        return super.initArgs(bundle);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mRefreshLayout = (PullToRefreshView) mRootView.findViewById(R.id.refresh_layout);
        mGridView = (GridView) mRootView.findViewById(R.id.gridView);
        mEmptyView = (CourseEmptyView) mRootView.findViewById(R.id.empty_layout);
        mNoPermissionView = (NoPermissionView) mRootView.findViewById(R.id.no_permission_view);
        mGridView.setNumColumns(1);
        mGridView.setHorizontalSpacing(0);
        mGridView.setVerticalSpacing(DisplayUtil.dip2px(getContext(), 1));
        mGridView.setPadding(0, 0, 0, 0);

        mGridView.setOnItemClickListener((parent, view, position, id) -> {
            CourseVo vo = (CourseVo) mCourseListAdapter.getItem(position);
            if (!mParams.isClassCourseEnter()) {
                if (mParams.isSelectResource()) {
                    if (!mParams.isReallyAuthorized()) {
                        UIUtil.showToastSafe(R.string.label_please_request_authorization);
                        return;
                    }

                    // 进入选择资源的Activity
                    WatchCourseResourceActivity.show(
                            getActivity(),
                            vo.getId(),
                            mResourceData.getTaskType(),
                            mResourceData.getMultipleChoiceCount(),
                            mResourceData.getFilterArray(),
                            mResourceData.isInitiativeTrigger(),
                            mParams.getBundle(),
                            mResourceData.getSchoolId(),
                            mResourceData.getClassId(),
                            mResourceData.getEnterType(), 0);
                } else {
                    // 线下机构学程馆,是从空中学校进入的 isSchoolEnter = true;
                    // String roles = UserHelper.getUserInfo().getRoles();
                    // 这里使用 isReallyAuthorized 传值 isAuthorized可能授权的是别的分类
                    SchoolHelper.requestSchoolInfo(UserHelper.getUserId(), mParams.getOrganId(),
                            new DataSource.Callback<SchoolInfoEntity>() {
                                @Override
                                public void onDataNotAvailable(int strRes) {
                                    showError(strRes);
                                }

                                @Override
                                public void onDataLoaded(SchoolInfoEntity schoolInfoEntity) {
                                    String roles = schoolInfoEntity.getRoles();

                                    CourseDetailParams params = new CourseDetailParams(mParams.isReallyAuthorized(),
                                            mParams.getOrganId(), roles);
                                    CourseDetailsActivity.start(getActivity(),
                                            vo.getId(), true, UserHelper.getUserId(),
                                            mParams.isReallyAuthorized(), params, true);
                                }
                            });
                }
            } else {
                // 班级学程的入口进来的，控制选择
                vo.setTag(!vo.isTag());
                mCourseListAdapter.notifyDataSetChanged();
            }
        });


        mCourseListAdapter = new CourseListAdapter(getActivity(), mParams.isClassCourseEnter());
        mGridView.setAdapter(mCourseListAdapter);

        // 下拉刷新
        mRefreshLayout.setLastUpdated(new Date().toLocaleString());
        mRefreshLayout.setOnHeaderRefreshListener(view -> requestCourseData(false));

        mRefreshLayout.setOnFooterRefreshListener(view -> requestCourseData(true));
    }

    @Override
    protected void initData() {
        super.initData();
    }


    /**
     * 查询课程 isMoreLoaded=true 加载更多数据
     */
    public void requestCourseData(boolean isMoreLoaded) {
        if (getActivity() == null) return;
        if (mParams.getSort() == 0) return;

        if (isMoreLoaded) {
            mPageIndex++;
        } else {
            mPageIndex = 0;
            mRefreshLayout.setLastUpdated(new Date().toLocaleString());
            mRefreshLayout.showRefresh();
        }
        if (mParams.isSelectResource()) {
            mPresenter.requestCourseResourceData(isMoreLoaded, mPageIndex,
                        AppConfig.PAGE_SIZE, mParams);
        } else {
            mPresenter.requestCourseData(isMoreLoaded, mPageIndex,
                    AppConfig.PAGE_SIZE, mParams);
        }

    }

    private void updateView(List<CourseVo> courseVos, boolean isMoreLoad) {
        if (!isMoreLoad) {
            // 判断有无更多数据,打开或者关闭加载更多
            mRefreshLayout.onHeaderRefreshComplete();
            mRefreshLayout.onFooterRefreshComplete();
            mRefreshLayout.setLoadMoreEnable(courseVos.size() >= AppConfig.PAGE_SIZE);
            // 设置数据
            mCourseListAdapter.setData(courseVos);
            mCourseListAdapter.notifyDataSetChanged();
            if (EmptyUtil.isEmpty(courseVos)) {
                // 数据为空
                mRefreshLayout.setVisibility(View.GONE);
                updateEmptyView(mParams.getLibraryType());
            } else {
                // 数据不为空
                mRefreshLayout.setVisibility(View.VISIBLE);
                mEmptyView.setVisibility(View.GONE);
                mNoPermissionView.setVisibility(View.GONE);
            }
        } else {
            // 关闭加载更多
            mRefreshLayout.onFooterRefreshComplete();
            mRefreshLayout.setLoadMoreEnable(courseVos.size() >= AppConfig.PAGE_SIZE);
            // 设置数据
            mCourseListAdapter.addData(courseVos);
            mCourseListAdapter.notifyDataSetChanged();
        }
    }

    private void updateEmptyView(int libraryType) {
        boolean isBrainLibrary = libraryType == OrganLibraryType.TYPE_BRAIN_LIBRARY;
        mEmptyView.setVisibility(!isBrainLibrary ? View.VISIBLE : View.GONE);
        mNoPermissionView.setVisibility(!isBrainLibrary ? View.GONE : View.VISIBLE);
        mNoPermissionView.setDescription(getString(R.string.label_organ_course_permission_description, mLibraryNames[libraryType]));
        if (isBrainLibrary) {
            if (!TextUtils.isEmpty(mParams.getKeyString())) {
                mEmptyView.setVisibility(View.VISIBLE);
                mNoPermissionView.setVisibility(View.GONE);
            } else {
                mEmptyView.setVisibility(View.GONE);
                mNoPermissionView.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onCourseLoaded(List<CourseVo> courseVos) {
        updateView(courseVos, false);
    }

    @Override
    public void onMoreCourseLoaded(List<CourseVo> courseVos) {
        updateView(courseVos, true);
    }

    @Override
    public void onCourseResourceLoaded(List<CourseVo> courseVos) {
        updateView(courseVos, false);
    }

    @Override
    public void onMoreCourseResourceLoaded(List<CourseVo> courseVos) {
        updateView(courseVos, true);
    }


    @Override
    public void showError(int str) {
        super.showError(str);
        mRefreshLayout.onHeaderRefreshComplete();
        mRefreshLayout.onFooterRefreshComplete();
    }

    @Override
    public boolean triggerUpdateData(@NonNull OrganCourseFiltratePagerParams params) {
        if (params != null) {
            mParams = params;
            requestCourseData(false);
        }
        return false;
    }

    @Override
    public List<CourseVo> getCourseVoList() {
        if (mCourseListAdapter != null) {
            return mCourseListAdapter.getItems();
        }
        return null;
    }

    @Override
    public void updateReallyAuthorizeState(boolean isReallyAuthorized) {
        if (mParams != null) {
            mParams.setReallyAuthorized(isReallyAuthorized);
            mParams.setAuthorized(isReallyAuthorized);
        }
    }
}
