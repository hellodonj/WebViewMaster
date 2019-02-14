package com.lqwawa.intleducation.module.spanceschool.pager;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.PresenterFragment;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.factory.data.entity.SchoolFunctionEntity;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.filtrate.HideSortType;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.filtrate.courselist.LQCourseListActivity;
import com.lqwawa.intleducation.module.onclass.OnlineClassListActivity;
import com.lqwawa.intleducation.module.onclass.OnlineClassRole;
import com.lqwawa.intleducation.module.onclass.teacherlist.SchoolTeacherListActivity;
import com.lqwawa.intleducation.module.organcourse.online.CourseShopListActivity;
import com.lqwawa.intleducation.module.spanceschool.SchoolFunctionStateType;

import java.util.List;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 空中学校空中课堂功能列表
 * @date 2018/06/25 10:08
 * @history v1.0
 * **********************************
 */
public class SpaceSchoolHolderPagerFragment extends PresenterFragment<SpaceSchoolHolderPagerContract.Presenter> implements SpaceSchoolHolderPagerContract.View {

    private static final String KEY_EXTRA_SCHOOL_ID = "KEY_EXTRA_SCHOOL_ID";
    private static final String KEY_EXTRA_SCHOOL_NAME = "KEY_EXTRA_SCHOOL_NAME";
    private static final String KEY_EXTRA_SCHOOL_LOGO = "KEY_EXTRA_SCHOOL_LOGO";
    private static final String KEY_EXTRA_ROLE = "KEY_EXTRA_ROLE";
    private static final String KEY_EXTRA_FUNCTION_STATE = "KEY_EXTRA_FUNCTION_TYPE";
    private static final String KEY_EXTRA_PAGER_NUMBER = "KEY_PAGER_NUMBER";

    private SchoolFunctionPagerNavigator mNavigator;

    private RecyclerView mRecycler;
    private SchoolFunctionPagerAdapter mAdapter;

    private String mSchoolId;
    private String mSchoolName;
    private String mLogoUrl;
    private String mRole;
    private int mFunctionState;
    private int mPagerNumber;

    public static SpaceSchoolHolderPagerFragment newInstance(@NonNull String schoolId,
                                                             @NonNull String schoolName,
                                                             @NonNull String logoUrl,
                                                             @NonNull @OnlineClassRole.RoleRes String role,
                                                             @SchoolFunctionStateType.FunctionStateRes int state,
                                                             int pagerNumber) {
        SpaceSchoolHolderPagerFragment fragment = new SpaceSchoolHolderPagerFragment();
        Bundle arguments = new Bundle();
        arguments.putString(KEY_EXTRA_SCHOOL_ID, schoolId);
        arguments.putString(KEY_EXTRA_SCHOOL_NAME, schoolName);
        arguments.putString(KEY_EXTRA_SCHOOL_LOGO, logoUrl);
        arguments.putString(KEY_EXTRA_ROLE, role);
        arguments.putInt(KEY_EXTRA_FUNCTION_STATE, state);
        arguments.putInt(KEY_EXTRA_PAGER_NUMBER, pagerNumber);
        fragment.setArguments(arguments);
        return fragment;
    }

    /**
     * 设置点击事件的监听
     *
     * @param navigator
     */
    public void setNavigator(@NonNull SchoolFunctionPagerNavigator navigator) {
        this.mNavigator = navigator;
    }

    @Override
    protected SpaceSchoolHolderPagerContract.Presenter initPresenter() {
        return new SpaceSchoolHolderPagerPresenter(this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_space_school_holder_pager;
    }

    @Override
    protected boolean initArgs(Bundle bundle) {
        mSchoolId = bundle.getString(KEY_EXTRA_SCHOOL_ID);
        mSchoolName = bundle.getString(KEY_EXTRA_SCHOOL_NAME);
        mLogoUrl = bundle.getString(KEY_EXTRA_SCHOOL_LOGO);
        mRole = bundle.getString(KEY_EXTRA_ROLE);
        mFunctionState = bundle.getInt(KEY_EXTRA_FUNCTION_STATE);
        mPagerNumber = bundle.getInt(KEY_EXTRA_PAGER_NUMBER);
        if (EmptyUtil.isEmpty(mSchoolId) || EmptyUtil.isEmpty(mSchoolName) || EmptyUtil.isEmpty(mRole)) {
            return false;
        }
        return super.initArgs(bundle);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mRecycler = (RecyclerView) mRootView.findViewById(R.id.recycler);
        mRecycler.setLayoutManager(new GridLayoutManager(getContext(), 4) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        mAdapter = new SchoolFunctionPagerAdapter();
        mRecycler.setAdapter(mAdapter);
        mAdapter.setListener(new RecyclerAdapter.AdapterListenerImpl<SchoolFunctionEntity>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, SchoolFunctionEntity schoolFunctionEntity) {
                super.onItemClick(holder, schoolFunctionEntity);
                switch (schoolFunctionEntity.getType()) {
                    case SchoolFunctionEntity.TYPE_FUNCTION_CLASS:
                        // 开课班
                        // V5.8版本，开课班也是机构主页入口
                        OnlineClassListActivity.show(getActivity(), mSchoolId, mSchoolName, true);
                        break;
                    case SchoolFunctionEntity.TYPE_FUNCTION_COURSE:
                        // 精品课程
                        // 进入课程列表 标题是课堂
                        // V5.8版本，精品课程也是机构主页入口
                        // LQCourseListActivity.show(getActivity(), HideSortType.TYPE_SORT_ONLINE_COURSE, getString(R.string.label_space_school_function_shop), mSchoolId, true, false);
                        CourseShopListActivity.show(getActivity(),HideSortType.TYPE_SORT_ONLINE_COURSE,getString(R.string.label_space_school_function_shop), mSchoolId, true, false);
                        break;
                    case SchoolFunctionEntity.TYPE_FUNCTION_TEACHER:
                        // 名师堂
                        SchoolTeacherListActivity.show(getActivity(), mSchoolId, mSchoolName, mLogoUrl, mRole);
                        break;
                    case SchoolFunctionEntity.TYPE_FUNCTION_CHOICE_BOOKS:
                        if (EmptyUtil.isNotEmpty(mNavigator)) {
                            mNavigator.onClickBookLibrary();
                        }
                        break;
                    case SchoolFunctionEntity.TYPE_FUNCTION_CAMPUS:
                        if (EmptyUtil.isNotEmpty(mNavigator)) {
                            mNavigator.onClickCampus(mFunctionState);
                        }
                        break;
                    case SchoolFunctionEntity.TYPE_FUNCTION_SCHOOL_FORUM:
                        if (EmptyUtil.isNotEmpty(mNavigator)) {
                            mNavigator.onClickForum();
                        }
                        break;
                }
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        List<SchoolFunctionEntity> entities = mPresenter.getFunctionEntities(mFunctionState, mPagerNumber);
        mAdapter.replace(entities);
    }
}
