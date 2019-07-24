package com.lqwawa.intleducation.module.discovery.ui.lesson.sxdetail;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.IBaseFragment;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.common.utils.Utils;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.helper.LessonHelper;
import com.lqwawa.intleducation.module.discovery.ui.CourseDetailsItemFragment;
import com.lqwawa.intleducation.module.discovery.ui.lesson.detail.LessonSourceNavigator;
import com.lqwawa.intleducation.module.discovery.ui.lesson.detail.LessonSourceParams;
import com.lqwawa.intleducation.module.discovery.ui.lesson.detail.ReadWeikeHelper;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.home.LanguageType;
import com.lqwawa.intleducation.module.learn.vo.SectionDetailsVo;
import com.lqwawa.intleducation.module.learn.vo.SectionResListVo;
import com.lqwawa.intleducation.module.learn.vo.SectionTaskListVo;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 描述: 三习教案课程节详情的Fragment
 * 作者|时间: djj on 2019/7/23 0023 上午 10:27
 */
public class SxLessonSourceFragment extends IBaseFragment implements LessonSourceNavigator, SxCourseResListAdapter.CheckInterface {

    public static final String LESSON_RESOURCE_CHOICE_PUBLISH_ACTION = "LESSON_RESOURCE_CHOICE_PUBLISH_ACTION";
    private static final String KEY_EXTRA_COURSE_ID = "KEY_EXTRA_COURSE_ID";
    private static final String KEY_EXTRA_SECTION_ID = "KEY_EXTRA_SECTION_ID";
    private static final String KEY_EXTRA_EXERCISE_TYPE = "KEY_EXTRA_EXERCISE_TYPE";
    //1：预习 2:练习 3：复习   不传或者-1 全部
    private String courseId;
    private String sectionId;
    private int exerciseType;
    private LessonSourceParams mSourceParams;
    private SxCourseResListAdapter mAdapter;
    private SectionDetailsVo mSectionDetailsVo;
    private CustomExpandableListView mExpandableListView;

    private ReadWeikeHelper mReadWeikeHelper;
    private int totalCount = 0;//所选项目的数量
    private List<SectionTaskListVo> tempChapterList = new ArrayList<>();
    private List<SectionTaskListVo> chapterList;
    private List<SectionResListVo> children;
    private Map<String, List<SectionResListVo>> childMap = new HashMap<String, List<SectionResListVo>>();// 子元素数据列表


    public static SxLessonSourceFragment newInstance(@NonNull String courseId,
                                                     @NonNull String sectionId,
                                                     int exerciseType,
                                                     @NonNull LessonSourceParams params) {
        SxLessonSourceFragment fragment = new SxLessonSourceFragment();
        Bundle arguments = new Bundle();
        arguments.putString(KEY_EXTRA_COURSE_ID, courseId);
        arguments.putString(KEY_EXTRA_SECTION_ID, sectionId);
        arguments.putInt(KEY_EXTRA_EXERCISE_TYPE, exerciseType);
        arguments.putSerializable(FRAGMENT_BUNDLE_OBJECT, params);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    protected boolean initArgs(Bundle bundle) {
        courseId = bundle.getString(KEY_EXTRA_COURSE_ID);
        sectionId = bundle.getString(KEY_EXTRA_SECTION_ID);
        exerciseType = bundle.getInt(KEY_EXTRA_EXERCISE_TYPE);
        if (bundle.containsKey(FRAGMENT_BUNDLE_OBJECT)) {
            mSourceParams = (LessonSourceParams) bundle.getSerializable(FRAGMENT_BUNDLE_OBJECT);
        }



        if (EmptyUtil.isEmpty(courseId) ||
                EmptyUtil.isEmpty(sectionId) ||
                EmptyUtil.isEmpty(mSourceParams)) return false;
        return super.initArgs(bundle);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_sx_lesson_source;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mExpandableListView = (CustomExpandableListView) mRootView.findViewById(R.id.expandable_list_view);

    }

    @Override
    protected void initData() {
        super.initData();
        registerBroadcastReceiver();
        getData();
    }

    //
    private void getData() {
        String token = mSourceParams.getMemberId();
        int role = 2;
        if (mSourceParams.getRole() == UserHelper.MoocRoleType.TEACHER) {
            role = 1;
        }

        String classId = "";
        if (role == 1 && mSourceParams.getCourseParams().isClassCourseEnter()) {
            classId = mSourceParams.getCourseParams().getClassId();
        } else if (role == 1 && mAdapter.getChoiceMode()) {
            if (EmptyUtil.isNotEmpty(mSourceParams.getCourseParams().getClassId())) {
                classId = mSourceParams.getCourseParams().getClassId();
            }
        }

        // 获取中英文数据
        int languageRes = Utils.isZh(UIUtil.getContext()) ? LanguageType.LANGUAGE_CHINESE : LanguageType.LANGUAGE_OTHER;
        //exerciseType 不传或者-1 全部
        LessonHelper.requestChapterStudyTask(languageRes, token, classId, courseId, sectionId, role, exerciseType, new DataSource.Callback<SectionDetailsVo>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                UIUtil.showToastSafe(strRes);
            }

            @Override
            public void onDataLoaded(SectionDetailsVo sectionDetailsVo) {
                mSectionDetailsVo = sectionDetailsVo;
                if (EmptyUtil.isEmpty(sectionDetailsVo)) return;
                chapterList = sectionDetailsVo.getTaskList();
                for (int i = 0; i < chapterList.size(); i++) {
                    SectionTaskListVo taskListVo = chapterList.get(i);
                    tempChapterList.add(taskListVo);
                    children = chapterList.get(i).getData();
                    childMap.put(chapterList.get(i).getTaskName(), children);
                }
                updateView();
            }
        });
    }

    private void updateView() {
        mAdapter = new SxCourseResListAdapter(getActivity(), tempChapterList, childMap);
        mAdapter.setCheckInterface(SxLessonSourceFragment.this);
        mExpandableListView.setAdapter(mAdapter);
        for (int j = 0; j < mAdapter.getGroupCount(); j++) {
            mExpandableListView.expandGroup(j);
        }
    }

    @Override
    public void triggerChoice(boolean open) {
        if (EmptyUtil.isNotEmpty(mAdapter)) {
            mAdapter.triggerChoiceMode(open);
            mSourceParams.setAddMode(open);
        }
    }

    @Override
    public List<SectionResListVo> takeChoiceResource() {
        List<SectionResListVo> data = mAdapter.getData();
        List<SectionResListVo> choiceArray = new ArrayList<>();
        if (EmptyUtil.isNotEmpty(data)) {
            for (SectionResListVo vo : data) {
                if (vo.isActivated()) {
                    choiceArray.add(vo);
                }
            }
        }

        return choiceArray;
    }

    @Override
    public void clearAllResourceState() {
        List<SectionResListVo> data = mAdapter.getData();
        if (EmptyUtil.isNotEmpty(data)) {
            for (SectionResListVo vo : data) {
                vo.setActivated(false);
            }

            mSourceParams.setAddMode(false);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void checkGroup(int groupPosition, boolean isHide) {
        SectionTaskListVo group = tempChapterList.get(groupPosition);
        List<SectionResListVo> childs = childMap.get(group.getTaskName());
        for (int i = 0; i < childs.size(); i++) {
            childs.get(i).setChecked(isHide);
        }
        mAdapter.notifyDataSetChanged();
        calculate();
    }

    @Override
    public void checkChild(int groupPosition, int childPosition, boolean isChecked) {
        boolean allChildSameState = true;// 判断该组下面的所有子元素是否是同一种状态
        SectionTaskListVo group = tempChapterList.get(groupPosition);
        List<SectionResListVo> childs = childMap.get(group.getTaskName());
        for (int i = 0; i < childs.size(); i++) {
            // 不全选中
            if (childs.get(i).isChecked() != isChecked) {
                allChildSameState = false;
                break;
            }
        }
        //获取该时间段的选中项目状态
        if (allChildSameState) {
            group.setChecked(isChecked);// 如果所有子元素状态相同，那么对应的组元素被设为这种统一状态
        } else {
            group.setChecked(false);// 否则，组元素一律设置为未选中状态
        }
        mAdapter.notifyDataSetChanged();
        calculate();
    }

    /**
     * 统计操作<br>
     * 1.先清空全局计数器<br>
     * 2.遍历所有子元素，只要是被选中状态的，就进行相关的计算操作<br>
     */
    private void calculate() {
        //List<SectionTaskListVo> children;
        //    private Map<String, List<SectionResListVo>> childMap
        totalCount = 0;
        for (int i = 0; i < tempChapterList.size(); i++) {
            SectionTaskListVo group = tempChapterList.get(i);
            List<SectionResListVo> childs = childMap.get(group.getTaskName());
            for (int j = 0; j < childs.size(); j++) {
                SectionResListVo project = childs.get(j);
                if (project.isChecked()) {
                    totalCount++;
                }
            }
        }
    }


    /**
     * 注册广播事件,接收事件刷新
     */
    protected void registerBroadcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(CourseDetailsItemFragment.LQWAWA_ACTION_READ_WRITE_SINGLE);// 读写单
        myIntentFilter.addAction(LESSON_RESOURCE_CHOICE_PUBLISH_ACTION);// 作业库发布更新
        //注册广播
        getActivity().registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    /**
     * 数据刷新广播的处理
     */
    protected BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(CourseDetailsItemFragment.LQWAWA_ACTION_READ_WRITE_SINGLE) ||
                    action.equalsIgnoreCase(LESSON_RESOURCE_CHOICE_PUBLISH_ACTION)) {
                // 读写单
                // 作业库发布
                getData();
            }
        }
    };
}
