package com.lqwawa.intleducation.module.box.tutorial;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.PresenterFragment;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.module.box.FunctionAdapter;
import com.lqwawa.intleducation.module.box.FunctionEntity;
import com.lqwawa.intleducation.module.box.TutorialSpaceBoxContract;
import com.lqwawa.intleducation.module.box.TutorialSpaceBoxPresenter;
import com.lqwawa.intleducation.module.box.common.CommonMarkingListFragment;
import com.lqwawa.intleducation.module.box.common.CommonMarkingParams;
import com.lqwawa.intleducation.module.tutorial.marking.list.TutorialMarkingListActivity;
import com.lqwawa.intleducation.module.tutorial.marking.list.TutorialMarkingParams;
import com.lqwawa.intleducation.module.tutorial.marking.list.TutorialRoleType;
import com.lqwawa.intleducation.module.tutorial.teacher.courses.TutorialCoursesActivity;
import com.lqwawa.intleducation.module.tutorial.teacher.courses.TutorialCoursesParams;
import com.lqwawa.intleducation.module.tutorial.teacher.schools.TutorialSchoolsActivity;
import com.lqwawa.intleducation.module.tutorial.teacher.schools.TutorialSchoolsParams;
import com.lqwawa.intleducation.module.tutorial.teacher.students.TutorialStudentActivity;
import com.lqwawa.intleducation.module.tutorial.teacher.students.TutorialStudentParams;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * @author medici
 * @desc 帮辅空间的页面
 */
public class TutorialSpaceFragment extends PresenterFragment<TutorialSpaceContract.Presenter>
        implements TutorialSpaceContract.View{

    private RecyclerView mRecycler;
    private FunctionAdapter mAdapter;

    @Override
    protected TutorialSpaceContract.Presenter initPresenter() {
        return new TutorialSpacePresenter(this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_tutorial_space;
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
                if(titleId == R.string.label_tutorial_work){
                    skipTutorialWork();
                }else if(titleId == R.string.label_tutorial_student){
                    skipTutorialStudents();
                }else if(titleId == R.string.label_tutorial_course){
                    skipTutorialCourse();
                }else if(titleId == R.string.label_tutorial_organ){
                    skipTutorialOrgan();
                }
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        List<FunctionEntity> entities = new ArrayList<>();
        entities.add(new FunctionEntity(R.string.label_tutorial_work,R.drawable.ic_tutorial_work));
        entities.add(new FunctionEntity(R.string.label_tutorial_student,R.drawable.ic_tutorial_student));
        entities.add(new FunctionEntity(R.string.label_tutorial_course,R.drawable.ic_tutorial_course));
        entities.add(new FunctionEntity(R.string.label_tutorial_organ,R.drawable.ic_tutorial_organ));
        mAdapter.replace(entities);

        String memberId = UserHelper.getUserId();
        CommonMarkingParams params = new CommonMarkingParams(true,memberId);
        Fragment fragment = CommonMarkingListFragment.newInstance(params);
        FragmentManager fragmentManager = getChildFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.lay_content,fragment)
                .commit();
    }

    // 去作业列表页面
    private void skipTutorialWork(){
        String memberId = UserHelper.getUserId();
        TutorialMarkingParams params = new TutorialMarkingParams(memberId,TutorialRoleType.TUTORIAL_TYPE_TUTOR);
        TutorialMarkingListActivity.show(getActivity(),params);
    }

    // 去我帮辅的学生页面
    private void skipTutorialStudents(){
        String memberId = UserHelper.getUserId();
        TutorialStudentParams params = new TutorialStudentParams(memberId,getString(R.string.label_tutorial_student));
        TutorialStudentActivity.show(getActivity(),params);
    }

    // 去我帮辅的课程页面
    private void skipTutorialCourse(){
        String memberId = UserHelper.getUserId();
        String configValue = getString(R.string.label_tutorial_course);
        TutorialCoursesParams params = new TutorialCoursesParams(memberId,configValue);
        TutorialCoursesActivity.show(getActivity(),params);
    }

    // 去我的帮辅机构页面
    private void skipTutorialOrgan(){
        String memberId = UserHelper.getUserId();
        String configValue = getString(R.string.label_tutorial_organ);
        TutorialSchoolsParams params = new TutorialSchoolsParams(memberId,configValue);
        TutorialSchoolsActivity.show(getActivity(),params);
    }

    /**
     * 帮辅空间入口
     * @return TutorialSpaceFragment
     */
    public static TutorialSpaceFragment newInstance(){
        TutorialSpaceFragment fragment = new TutorialSpaceFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }
}
