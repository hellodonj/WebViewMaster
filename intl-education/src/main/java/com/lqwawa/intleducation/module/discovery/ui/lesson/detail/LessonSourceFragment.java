package com.lqwawa.intleducation.module.discovery.ui.lesson.detail;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.GridView;

import com.lqwawa.client.pojo.SourceFromType;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.CourseEmptyView;
import com.lqwawa.intleducation.base.IBaseFragment;
import com.lqwawa.intleducation.base.utils.ButtonUtils;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.common.utils.Utils;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.helper.LessonHelper;
import com.lqwawa.intleducation.module.discovery.adapter.CourseResListAdapter;
import com.lqwawa.intleducation.module.discovery.tool.CourseDetails;
import com.lqwawa.intleducation.module.discovery.ui.CourseDetailsItemFragment;
import com.lqwawa.intleducation.module.discovery.ui.coursedetail.CourseDetailParams;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.home.LanguageType;
import com.lqwawa.intleducation.module.discovery.ui.task.detail.SectionTaskParams;
import com.lqwawa.intleducation.module.discovery.ui.videodetail.VideoDetailActivity;
import com.lqwawa.intleducation.module.learn.tool.TaskSliderHelper;
import com.lqwawa.intleducation.module.learn.ui.LessonDetailsActivity;
import com.lqwawa.intleducation.module.learn.ui.MyCourseDetailsActivity;
import com.lqwawa.intleducation.module.learn.ui.SectionTaskDetailsActivity;
import com.lqwawa.intleducation.module.learn.vo.SectionDetailsVo;
import com.lqwawa.intleducation.module.learn.vo.SectionResListVo;
import com.lqwawa.intleducation.module.learn.vo.SectionTaskListVo;
import com.lqwawa.intleducation.module.organcourse.OrganLibraryType;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 课程节详情的Fragment
 * @date 2018/05/16 11:19
 * @history v1.0
 * **********************************
 */
public class LessonSourceFragment extends IBaseFragment implements LessonSourceNavigator {

    public static final String LESSON_RESOURCE_CHOICE_PUBLISH_ACTION = "LESSON_RESOURCE_CHOICE_PUBLISH_ACTION";

    // 选择资源支持的最大数
    private static final int MAX_CHOICE_COUNT = 5;

    private static final String KEY_EXTRA_NEED_FLAG = "KEY_EXTRA_NEED_FLAG";
    private static final String KEY_EXTRA_CAN_READ = "KEY_EXTRA_CAN_READ";
    private static final String KEY_EXTRA_CAN_EDIT = "KEY_EXTRA_CAN_EDIT";
    private static final String KEY_EXTRA_ONLINE_TEACHER = "KEY_EXTRA_ONLINE_TEACHER";
    private static final String KEY_EXTRA_COURSE_ID = "KEY_EXTRA_COURSE_ID";
    private static final String KEY_EXTRA_SECTION_ID = "KEY_EXTRA_SECTION_ID";
    private static final String KEY_EXTRA_TASK_TYPE = "KEY_EXTRA_TASK_TYPE";


    public static String SECTION_NAME = "section_name";
    public static String SECTION_TITLE = "section_title";
    public static String STATUS = "status";

    private GridView mListView;
    private CourseEmptyView mEmptyLayout;
    private CourseResListAdapter mCourseResListAdapter;
    private boolean needFlag;
    private boolean canRead;
    private String courseId;
    private String sectionId;
    private int mTaskType;
    private SectionDetailsVo mSectionDetailsVo;
    private LessonSourceParams mSourceParams;
    private ReadWeikeHelper mReadWeikeHelper;
    private boolean isVideoCourse;

    public static LessonSourceFragment newInstance(boolean needFlag,
                                                   boolean canEdit,
                                                   boolean canRead,
                                                   boolean isOnlineTeacher,
                                                   @NonNull String courseId,
                                                   @NonNull String sectionId,
                                                   int taskType,
                                                   @NonNull LessonSourceParams params) {
        LessonSourceFragment fragment = new LessonSourceFragment();
        Bundle arguments = new Bundle();
        arguments.putBoolean(KEY_EXTRA_NEED_FLAG, needFlag);
        arguments.putBoolean(KEY_EXTRA_CAN_EDIT, canEdit);
        arguments.putBoolean(KEY_EXTRA_CAN_READ, canRead);
        arguments.putBoolean(KEY_EXTRA_ONLINE_TEACHER, isOnlineTeacher);
        arguments.putString(KEY_EXTRA_COURSE_ID, courseId);
        arguments.putString(KEY_EXTRA_SECTION_ID, sectionId);
        arguments.putInt(KEY_EXTRA_TASK_TYPE, taskType);
        arguments.putSerializable(FRAGMENT_BUNDLE_OBJECT, params);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_lesson_source;
    }

    @Override
    protected boolean initArgs(Bundle bundle) {
        needFlag = bundle.getBoolean(KEY_EXTRA_NEED_FLAG);
        canRead = bundle.getBoolean(KEY_EXTRA_CAN_READ);
        courseId = bundle.getString(KEY_EXTRA_COURSE_ID);
        sectionId = bundle.getString(KEY_EXTRA_SECTION_ID);
        mTaskType = bundle.getInt(KEY_EXTRA_TASK_TYPE);
        if (bundle.containsKey(FRAGMENT_BUNDLE_OBJECT)) {
            mSourceParams = (LessonSourceParams) bundle.getSerializable(FRAGMENT_BUNDLE_OBJECT);
        }

        if (mTaskType == 1 || mTaskType == 4 || mTaskType == 2 || mTaskType == 6) {
            // 看课件  或者是  复述课件(没有复述课件权限的)
            mReadWeikeHelper = new ReadWeikeHelper(getActivity());
        }

        if (mSourceParams != null && mSourceParams.getCourseParams() != null) {
            CourseDetailParams courseDetailParams = mSourceParams.getCourseParams();
            isVideoCourse =
                    courseDetailParams != null
                            && (courseDetailParams.getLibraryType() == OrganLibraryType.TYPE_VIDEO_LIBRARY
                            || (courseDetailParams.getLibraryType() == OrganLibraryType.TYPE_BRAIN_LIBRARY && courseDetailParams.isVideoCourse()));
        }

        if (EmptyUtil.isEmpty(courseId) ||
                EmptyUtil.isEmpty(sectionId) ||
                EmptyUtil.isEmpty(mSourceParams)) return false;
        return super.initArgs(bundle);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mListView = (GridView) mRootView.findViewById(R.id.listView);
        mEmptyLayout = (CourseEmptyView) mRootView.findViewById(R.id.empty_layout);
        // 老师身份不显示
        boolean lessonNeedFlag = needFlag && (mSourceParams.getRole() != UserHelper.MoocRoleType.TEACHER);
        mCourseResListAdapter = new CourseResListAdapter(getActivity(), lessonNeedFlag, isVideoCourse);
        CourseDetailParams courseParams = mSourceParams.getCourseParams();
        mCourseResListAdapter.setClassTeacher((courseParams.isClassCourseEnter() && courseParams.isClassTeacher()) ||
                (mSourceParams.isChoiceMode() && mSourceParams.isInitiativeTrigger() && courseParams.isClassCourseEnter()));
        mCourseResListAdapter.triggerChoiceMode(mSourceParams.isChoiceMode());
        mListView.setNumColumns(1);

        // canRead 是否可以查阅资源
        // 试听功能，都可以查阅资源，以及学程馆授权的
        if (canRead) {
            mCourseResListAdapter.setOnItemClickListener(new CourseResListAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position, View convertView) {
                    if (ButtonUtils.isFastDoubleClick()) {
                        return;
                    }

                    final SectionResListVo resVo = (SectionResListVo) mCourseResListAdapter.getItem(position);
                    if (resVo.isIsShield()) {
                        UIUtil.showToastSafe(R.string.res_has_shield);
                        return;
                    }

                    if (mCourseResListAdapter.getChoiceMode()) {
                        if (resVo.getTaskType() == 1 || resVo.getTaskType() == 4
                                || resVo.getTaskType() == 6) {
                            // 看课件类型
                            if (EmptyUtil.isNotEmpty(mReadWeikeHelper)) {
                                mReadWeikeHelper.readWeike(resVo);
                            }
                        } else {
                            if (TaskSliderHelper.onTaskSliderListener != null &&
                                    resVo != null) {
                                TaskSliderHelper.onTaskSliderListener.viewCourse(
                                        getActivity(), resVo.getResId(),
                                        resVo.getResType(),
                                        getActivity().getIntent().getStringExtra("schoolId"),
                                        SourceFromType.LQ_COURSE);
                            }
                        }
                        return;
                    }


                    boolean freeUser = getActivity().getIntent().getBooleanExtra(LessonDetailsActivity.KEY_ROLE_FREE_USER, false);

                    if (resVo.getTaskType() == 1 || resVo.getTaskType() == 4) {
                        if (isVideoCourse) {
                            VideoDetailActivity.start(getActivity(), resVo, mSourceParams);
                        } else {
                            // 看课件
                            // V5.14 换成看课本,视频课
                            mReadWeikeHelper.readWeike(resVo);
                        }

                        if ((needFlag && !mSourceParams.isParentRole())) {
                            // 是已经加入的课程, 并且不是家长身份
                            if (mSourceParams.getRole() != UserHelper.MoocRoleType.EDITOR &&
                                    mSourceParams.getRole() != UserHelper.MoocRoleType.TEACHER) {
                                // 如果不是主编或者小编
                                // 看课件才FlagRead
                                flagRead(resVo, position);
                            }
                        }
                    } else if (resVo.getTaskType() == 2 || resVo.getTaskType() == 5) {
                        //复述微课
                        if ((needFlag || !mSourceParams.isParentRole()) || mSourceParams.isAudition()) {
                            // 是已经加入的课程
                            // 或者是试听身份
                            enterSectionTaskDetail(resVo);
                        } else {
                            // 其它情况，只能看微课
                            mReadWeikeHelper.readWeike(resVo);
                        }
                    } else if (resVo.getTaskType() == 3 || resVo.getTaskType() == 6) {
                        // 6 Q配音
                        if ((needFlag || !mSourceParams.isParentRole()) || mSourceParams.isAudition()) {
                            // 是已经加入的课程
                            // 或者是试听身份
                            enterSectionTaskDetail(resVo);
                        } else {
                            if (TaskSliderHelper.onTaskSliderListener != null) {
                                TaskSliderHelper.onTaskSliderListener.viewCourse(getActivity(),
                                        resVo.getResId(), resVo.getResType(),
                                        getActivity().getIntent().getStringExtra("schoolId"),
                                        getActivity().getIntent().getBooleanExtra("isPublic", false),
                                        getActivity().getIntent().getBooleanExtra(MyCourseDetailsActivity
                                                .KEY_IS_FROM_MY_COURSE, false)
                                                ? SourceFromType.LQ_MY_COURSE : SourceFromType.LQ_COURSE);
                            }
                        }
                    }
                }

                @Override
                public void onItemChoice(int position, View convertView) {
                    SectionResListVo item = (SectionResListVo) mCourseResListAdapter.getItem(position);
                    if (item.isIsShield()) {
                        UIUtil.showToastSafe(R.string.res_has_shield);
                        return;
                    }

                    if (!item.isActivated()) {
                        // 点击的当前条目不是选择的状态,需判断上限
                        // 判断是否已经超过五个选择了
                        int count = takeChoiceResourceCount();
                        if (count >= MAX_CHOICE_COUNT) {
                            UIUtil.showToastSafe(getString(R.string.str_select_count_tips, MAX_CHOICE_COUNT));
                            mCourseResListAdapter.notifyDataSetChanged();
                            return;
                        }
                    }

                    item.setActivated(!item.isActivated());
                    mCourseResListAdapter.notifyDataSetChanged();
                }
            });
        } else if (!needFlag) {
            // needFlag 是否标记已读的字段
            // V5.8之后，试听功能就可以查看听说课，读写单内容了
            // mListView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void initData() {
        super.initData();
        registerBroadcastReceiver();
        getData();
    }

    @Override
    public void triggerChoice(boolean open) {
        if (EmptyUtil.isNotEmpty(mCourseResListAdapter)) {
            mCourseResListAdapter.triggerChoiceMode(open);
            mSourceParams.setAddMode(open);
        }
    }

    /**
     * 获取已经选择的资源个数
     *
     * @return
     */
    private int takeChoiceResourceCount() {
        List<SectionResListVo> choiceArray = takeChoiceResource();
        int count = choiceArray.size();
        choiceArray.clear();
        return count;
    }

    @Override
    public List<SectionResListVo> takeChoiceResource() {
        List<SectionResListVo> data = mCourseResListAdapter.getData();
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
        List<SectionResListVo> data = mCourseResListAdapter.getData();
        if (EmptyUtil.isNotEmpty(data)) {
            for (SectionResListVo vo : data) {
                vo.setActivated(false);
            }

            mSourceParams.setAddMode(false);
            mCourseResListAdapter.notifyDataSetChanged();
        }
    }

    private void getData() {
        String token = mSourceParams.getMemberId();
        int role = 2;
        if (mSourceParams.getRole() == UserHelper.MoocRoleType.TEACHER) {
            role = 1;
        }

        String classId = "";
        if (role == 1 && mSourceParams.getCourseParams().isClassCourseEnter()) {
            classId = mSourceParams.getCourseParams().getClassId();
        }else if(role == 1 && mCourseResListAdapter.getChoiceMode()){
            if(EmptyUtil.isNotEmpty(mSourceParams.getCourseParams().getClassId())){
                classId = mSourceParams.getCourseParams().getClassId();
            }
        }

        // 获取中英文数据
        int languageRes = Utils.isZh(UIUtil.getContext()) ? LanguageType.LANGUAGE_CHINESE : LanguageType.LANGUAGE_OTHER;
        LessonHelper.requestChapterStudyTask(languageRes, token, classId, courseId, sectionId, role, new DataSource.Callback<SectionDetailsVo>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                UIUtil.showToastSafe(strRes);
            }

            @Override
            public void onDataLoaded(SectionDetailsVo sectionDetailsVo) {
                mSectionDetailsVo = sectionDetailsVo;
                if (EmptyUtil.isEmpty(sectionDetailsVo)) return;
                updateView();
            }
        });
    }

    private void updateView() {
        mCourseResListAdapter.setData(null);
        if (mSectionDetailsVo != null) {
            getActivity().getIntent().putExtra(SECTION_NAME, mSectionDetailsVo.getSectionName());
            getActivity().getIntent().putExtra(SECTION_TITLE, mSectionDetailsVo.getSectionTitle());
            getActivity().getIntent().putExtra(STATUS, mSectionDetailsVo.getStatus());
            getActivity().getIntent().putExtra("isPublic", mSectionDetailsVo.isIsOpen());

            List<SectionTaskListVo> taskList = mSectionDetailsVo.getTaskList();
            if (EmptyUtil.isNotEmpty(taskList)) {
                for (int index = 0; index < taskList.size(); index++) {
                    SectionTaskListVo listVo = taskList.get(index);
                    if (listVo.getTaskType() == mTaskType) {
                        List<SectionResListVo> data = listVo.getData();
                        if (EmptyUtil.isNotEmpty(data)) {
                            for (SectionResListVo vo : data) {
                                vo.setTaskName(getTaskName(index));
                                vo.setChapterId(vo.getId());
                                vo.setTaskType(listVo.getTaskType());
                                if(mTaskType == 5){
                                    // 讲解课的显示Fragment
                                    // vo.setResProperties("");
                                }
                            }
                        }

                        mCourseResListAdapter.triggerChoiceMode(mSourceParams.isAddMode());
                        mCourseResListAdapter.addData(data);
                        mCourseResListAdapter.notifyDataSetChanged();
                        break;
                    }
                }
            }
        }

        if (mCourseResListAdapter.getCount() > 0) {
            mListView.setAdapter(mCourseResListAdapter);
            mListView.setVisibility(View.VISIBLE);
            mEmptyLayout.setVisibility(View.GONE);
        } else {
            mListView.setVisibility(View.GONE);
            mEmptyLayout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 进入复述课件，任务单详情
     *
     * @param vo 复述课件，任务单消息实体
     */
    protected void enterSectionTaskDetail(SectionResListVo vo) {
        String curMemberId = mSourceParams.getMemberId();
        int originalRole = mSourceParams.getRole();
        if (mSourceParams.isTeacherVisitor()) {
            // 这才是真实的角色身份
            originalRole = mSourceParams.getRealRole();
        }


        final String taskId = vo.getTaskId();
        if (originalRole == UserHelper.MoocRoleType.STUDENT && !TextUtils.isEmpty(taskId)) {
            // 分发任务
            LessonHelper.DispatchTask(taskId, curMemberId, null);
        }

        // 处理需要转换的角色
        int handleRole = originalRole;
        boolean isAudition = mSourceParams.isAudition();
        if (mSourceParams.isCounselor() || mSourceParams.isAudition()) {
            // 如果是辅导老师身份 或者试听
            // 角色按照浏览者，家长身份处理
            handleRole = UserHelper.MoocRoleType.PARENT;
        }

        if (handleRole != UserHelper.MoocRoleType.PARENT) {
            // 家长身份优先
            if (TextUtils.equals(UserHelper.getUserId(), vo.getCreateId())) {
                // 任务的创建者 小编
                handleRole = UserHelper.MoocRoleType.TEACHER;
            }

            if (mSourceParams.isLecturer()) {
                // 如果是讲师身份 主编身份处理
                handleRole = UserHelper.MoocRoleType.EDITOR;
            }
        }


        // 生成任务详情参数
        SectionTaskParams params = new SectionTaskParams(originalRole, handleRole);
        params.fillParams(mSourceParams);

        SectionTaskDetailsActivity.startForResultEx(getActivity(), vo, curMemberId, getActivity().getIntent
                        ().getStringExtra("schoolId"), getActivity().getIntent().getBooleanExtra
                        (MyCourseDetailsActivity.KEY_IS_FROM_MY_COURSE, false),
                null, originalRole, handleRole, null, isAudition, params);
    }


    @NonNull
    private String getTaskName(int i) {
        String taskName = mSectionDetailsVo.getTaskList().get(i).getTaskName();
        return taskName;
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

    /**
     * 标志任务已读
     *
     * @param vo       任务实体
     * @param position 位置
     */
    private void flagRead(final SectionResListVo vo, final int position) {

        LessonHelper.requestAddSourceFlag(vo.getTaskType(), vo.getId(), vo.getResId(), new DataSource.SucceedCallback<Void>() {
            @Override
            public void onDataLoaded(Void aVoid) {
                SectionResListVo item = (SectionResListVo) mCourseResListAdapter.getItem(position);
                item.setStatus(1);
                mCourseResListAdapter.notifyDataSetChanged();
                // 发送广播,课程大纲刷新UI
                CourseDetails.courseDetailsTriggerStudyTask(getActivity().getApplicationContext(), CourseDetailsItemFragment.LQWAWA_ACTION_CAN_COURSEWARE);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EmptyUtil.isNotEmpty(mReadWeikeHelper)) {
            mReadWeikeHelper.release();
        }


        if (EmptyUtil.isNotEmpty(getActivity())) {
            getActivity().unregisterReceiver(mBroadcastReceiver);
        }
    }
}
