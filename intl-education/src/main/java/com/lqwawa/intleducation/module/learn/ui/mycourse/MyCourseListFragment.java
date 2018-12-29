package com.lqwawa.intleducation.module.learn.ui.mycourse;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ExpandableListView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.PresenterFragment;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;
import com.lqwawa.intleducation.module.discovery.ui.classcourse.Tab;
import com.lqwawa.intleducation.module.learn.ui.mycourse.detail.MyCourseConfigDetailActivity;

import java.util.List;

/**
 * @author mrmedici
 * @desc V5.12版本新添加我的习课程标签筛选页面
 *
 */
public class MyCourseListFragment extends PresenterFragment<MyCourseListContract.Presenter>
    implements MyCourseListContract.View,MyCourseConfigNavigator{


    // 小语种课程
    private static final int MINORITY_LANGUAGE_COURSE_ID = 2004;
    // 英语国际课程
    private static final int ENGLISH_INTERNATIONAL_COURSE_ID = 2001;
    // 特色课程
    private static final int CHARACTERISTIC_COURSE_ID = 2005;
    // 基础课程
    private static final int COUNTRY_COURSE_ID = 2003;

    // LQ English Primary
    private static final int ENGLISH_INTERNATIONAL_ENGLISH_PRIMARY_ID = 2011;

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
        mConfigAdapter = new MyCourseConfigAdapter(null);
        mConfigAdapter.setNavigator(this);
        mExpandableView.setAdapter(mConfigAdapter);
        mExpandableView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return true;
            }
        });
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
        mConfigAdapter.setData(entities);
        int groupCount = mExpandableView.getCount();
        for (int i=0; i<groupCount; i++) {
            mExpandableView.expandGroup(i);
        }
    }

    @Override
    public void onChoiceConfig(@NonNull LQCourseConfigEntity groupEntity,
                               @NonNull LQCourseConfigEntity childEntity,
                               @Nullable LQCourseConfigEntity configEntity) {
        // 点击标签
        String level = "";
        int rootId = groupEntity.getId();
        if(EmptyUtil.isNotEmpty(childEntity)) level = childEntity.getLevel();
        if(rootId == MINORITY_LANGUAGE_COURSE_ID){
            level = configEntity.getLevel();
        }

        int paramOneId = 0;
        int paramTwoId = 0;

        if(rootId != MINORITY_LANGUAGE_COURSE_ID){

            int rootTypeId = 0;
            if(EmptyUtil.isNotEmpty(childEntity)) rootTypeId = childEntity.getId();
            if(rootId == CHARACTERISTIC_COURSE_ID || rootId == COUNTRY_COURSE_ID){
                // 特色课程或者国家课程
                paramTwoId = configEntity.getLabelId();
            }else if(rootId == ENGLISH_INTERNATIONAL_COURSE_ID && rootTypeId == ENGLISH_INTERNATIONAL_ENGLISH_PRIMARY_ID){
                // 英语国际课程 LQ English PRIMARY
                paramTwoId = configEntity.getLabelId();
            }else{
                paramOneId = configEntity.getLabelId();
            }
        }

        MyCourseConfigDetailActivity.show(getActivity(),groupEntity,mCurSchoolId,mCurMemberId,isTeacher,level,paramOneId,paramTwoId);
    }
}
