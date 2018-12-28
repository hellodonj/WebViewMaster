package com.lqwawa.intleducation.module.learn.ui.mycourse;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.widget.ExpandableListView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.PresenterFragment;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;

import java.util.List;

/**
 * @author mrmedici
 * @desc V5.12版本新添加我的习课程标签筛选页面
 *
 */
public class MyCourseListFragment extends PresenterFragment<MyCourseListContract.Presenter>
    implements MyCourseListContract.View{

    private static final String KEY_EXTRA_SCHOOL_ID = "KEY_EXTRA_SCHOOL_ID";
    private static final String KEY_EXTRA_MEMBER_ID = "KEY_EXTRA_MEMBER_ID";
    private static final String KEY_EXTRA_BOOLEAN_TEACHER = "KEY_EXTRA_BOOLEAN_TEACHER";


    private ExpandableListView mExpandableView;
    private MyCourseConfigAdapter mConfigAdapter;

    private String mCurSchoolId;
    private String mCurMemberId;
    private boolean isTeacher;

    public static Fragment newInstance(@Nullable String schoolId,
                                       @NonNull String memberId,
                                       boolean isTeacher){
        Fragment fragment = new MyCourseListFragment();
        Bundle arguments = new Bundle();
        arguments.putString(KEY_EXTRA_SCHOOL_ID,schoolId);
        arguments.putString(KEY_EXTRA_MEMBER_ID,memberId);
        arguments.putBoolean(KEY_EXTRA_BOOLEAN_TEACHER,isTeacher);

        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    protected MyCourseListContract.Presenter initPresenter() {
        return new MyCourseListPresenter(this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_mycourse_outer;
    }

    @Override
    protected boolean initArgs(Bundle bundle) {
        mCurMemberId = bundle.getString(KEY_EXTRA_MEMBER_ID);
        mCurSchoolId = bundle.getString(KEY_EXTRA_SCHOOL_ID);
        isTeacher = bundle.getBoolean(KEY_EXTRA_BOOLEAN_TEACHER);
        if(EmptyUtil.isEmpty(mCurMemberId)) return false;
        return super.initArgs(bundle);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mExpandableView = (ExpandableListView) mRootView.findViewById(R.id.expandable_view);
    }

    @Override
    protected void initData() {
        super.initData();
        mPresenter.requestStudentConfigData(mCurMemberId);
    }

    public void getData(){
        mPresenter.requestStudentConfigData(mCurMemberId);
    }

    @Override
    public void updateStudentConfigView(@NonNull List<LQCourseConfigEntity> entities) {
        mConfigAdapter = new MyCourseConfigAdapter(entities);
        mExpandableView.setAdapter(mConfigAdapter);
    }
}
