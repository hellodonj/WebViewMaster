package com.lqwawa.intleducation.module.box.course.inner;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.PresenterFragment;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.base.widgets.ui.CommonContainerActivity;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.module.box.FunctionAdapter;
import com.lqwawa.intleducation.module.box.FunctionEntity;
import com.lqwawa.intleducation.module.box.common.CommonMarkingListFragment;
import com.lqwawa.intleducation.module.box.common.CommonMarkingParams;
import com.lqwawa.intleducation.module.discovery.ui.myonline.MyOnlinePagerFragment;
import com.lqwawa.intleducation.module.learn.ui.mycourse.MyCourseListFragment;
import com.lqwawa.intleducation.module.tutorial.marking.list.TutorialMarkingListActivity;
import com.lqwawa.intleducation.module.tutorial.marking.list.TutorialMarkingParams;
import com.lqwawa.intleducation.module.tutorial.marking.list.TutorialRoleType;
import com.lqwawa.intleducation.module.tutorial.student.courses.StudentTutorialActivity;
import com.lqwawa.intleducation.module.tutorial.student.courses.StudentTutorialParams;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mrmedici
 * @desc 个人课程的页面
 */
public class MyCourseInnerFragment extends PresenterFragment<MyCourseInnerContract.Presenter>
        implements MyCourseInnerContract.View{

    private static final String KEY_EXTRA_MEMBER_ID = "KEY_EXTRA_MEMBER_ID";
    private static final String KEY_EXTRA_SCHOOL_ID = "KEY_EXTRA_SCHOOL_ID";

    private String mCurMemberId;
    private String mSchoolId;

    private RecyclerView mRecycler;
    private FunctionAdapter mAdapter;

    @Override
    protected MyCourseInnerContract.Presenter initPresenter() {
        return new MyCourseInnerPresenter(this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_my_course_inner;
    }

    @Override
    protected boolean initArgs(Bundle bundle) {
        mCurMemberId = bundle.getString(KEY_EXTRA_MEMBER_ID);
        mSchoolId = bundle.getString(KEY_EXTRA_SCHOOL_ID);
        if(EmptyUtil.isEmpty(mCurMemberId)) return false;
        return super.initArgs(bundle);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mRecycler = (RecyclerView) mRootView.findViewById(R.id.recycler);
        mRecycler.setNestedScrollingEnabled(false);
        GridLayoutManager mLayoutManager = new GridLayoutManager(getContext(),4){
            @Override
            public boolean canScrollVertically() {
                return false;
            }

            @Override
            public boolean canScrollHorizontally() {
                return false;
            }
        };

        mRecycler.setLayoutManager(mLayoutManager);
        mAdapter = new FunctionAdapter();
        mRecycler.setAdapter(mAdapter);
        mAdapter.setListener(new RecyclerAdapter.AdapterListenerImpl<FunctionEntity>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, FunctionEntity functionEntity) {
                super.onItemClick(holder, functionEntity);
                int titleId = functionEntity.getTitleId();
                if(titleId == R.string.label_student_work){
                    skipStudentWork();
                }else if(titleId == R.string.label_student_tutorial){
                    skipStudentTutorial();
                }else if(titleId == R.string.label_student_course){
                    skipStudentCourse();
                }else if(titleId == R.string.label_student_live){
                    skipStudentLive();
                }
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        List<FunctionEntity> entities = new ArrayList<>();
        entities.add(new FunctionEntity(R.string.label_student_work,R.drawable.ic_tutorial_work));
        entities.add(new FunctionEntity(R.string.label_student_tutorial,R.drawable.ic_student_tutorial));
        entities.add(new FunctionEntity(R.string.label_student_course,R.drawable.ic_student_course));
        entities.add(new FunctionEntity(R.string.label_student_live,R.drawable.ic_student_live));
        mAdapter.replace(entities);

        CommonMarkingParams params = new CommonMarkingParams(false,mCurMemberId);
        Fragment fragment = CommonMarkingListFragment.newInstance(params);
        FragmentManager fragmentManager = getChildFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.lay_content,fragment)
                .commit();
    }

    private void skipStudentWork(){
        boolean isParent = !TextUtils.equals(UserHelper.getUserId(),mCurMemberId);
        TutorialMarkingParams params = new TutorialMarkingParams(mCurMemberId,isParent?TutorialRoleType.TUTORIAL_TYPE_PARENT:TutorialRoleType.TUTORIAL_TYPE_STUDENT);
        TutorialMarkingListActivity.show(getActivity(),params);
    }

    private void skipStudentTutorial(){
        boolean isParent = !TextUtils.equals(UserHelper.getUserId(),mCurMemberId);
        String configValue = getString(R.string.label_student_tutorial);
        StudentTutorialParams params = new StudentTutorialParams(isParent,mCurMemberId,configValue);
        StudentTutorialActivity.show(getActivity(),params);
    }

    private void skipStudentCourse(){
        Bundle bundle = new Bundle();
        bundle.putString(CommonContainerActivity.CourseFragmentConstance.KEY_EXTRA_MEMBER_ID,mCurMemberId);
        bundle.putString(CommonContainerActivity.CourseFragmentConstance.KEY_EXTRA_SCHOOL_ID,mSchoolId);
        bundle.putBoolean(CommonContainerActivity.CourseFragmentConstance.KEY_EXTRA_BOOLEAN_TEACHER,false);
        CommonContainerActivity.show(getActivity(),true,
                getString(R.string.label_student_course),
                MyCourseListFragment.class.getName(),bundle);
    }

    private void skipStudentLive(){
        Bundle bundle = new Bundle();
        bundle.putString(CommonContainerActivity.OnlineLiveFragmentConstance.KEY_EXTRA_MEMBER_ID,mCurMemberId);
        bundle.putBoolean(CommonContainerActivity.OnlineLiveFragmentConstance.KEY_EXTRA_HIDE_SEARCH,false);
        CommonContainerActivity.show(getActivity(),true,
                getString(R.string.label_student_live),
                MyOnlinePagerFragment.class.getName(),bundle);
    }

    /**
     * 个人帮辅空间入口
     * @return TutorialSpaceInnerFragment
     */
    public static MyCourseInnerFragment newInstance(@NonNull String memberId,
                                                    @Nullable String schoolId){
        MyCourseInnerFragment fragment = new MyCourseInnerFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_EXTRA_MEMBER_ID,memberId);
        bundle.putString(KEY_EXTRA_SCHOOL_ID,schoolId);
        fragment.setArguments(bundle);
        return fragment;
    }
}
