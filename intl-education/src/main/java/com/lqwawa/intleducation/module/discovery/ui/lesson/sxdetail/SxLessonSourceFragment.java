package com.lqwawa.intleducation.module.discovery.ui.lesson.sxdetail;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.SparseIntArray;
import android.view.View;
import android.widget.FrameLayout;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.CourseEmptyView;
import com.lqwawa.intleducation.base.IBaseFragment;
import com.lqwawa.intleducation.common.ui.treeview.TreeNode;
import com.lqwawa.intleducation.common.ui.treeview.TreeView;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.common.utils.Utils;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.helper.LessonHelper;
import com.lqwawa.intleducation.module.discovery.ui.CourseSelectItemFragment;
import com.lqwawa.intleducation.module.discovery.ui.coursedetail.CourseDetailParams;
import com.lqwawa.intleducation.module.discovery.ui.coursedetail.CourseDetailType;
import com.lqwawa.intleducation.module.discovery.ui.lesson.detail.LessonSourceFragment;
import com.lqwawa.intleducation.module.discovery.ui.lesson.detail.LessonSourceParams;
import com.lqwawa.intleducation.module.discovery.ui.lesson.detail.ReadWeikeHelper;
import com.lqwawa.intleducation.module.discovery.ui.lesson.sxdetail.factory.SxNodeViewFactory;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.home.LanguageType;
import com.lqwawa.intleducation.module.discovery.vo.ExamsAndTestExtrasVo;
import com.lqwawa.intleducation.module.learn.vo.SectionDetailsVo;
import com.lqwawa.intleducation.module.learn.vo.SectionResListVo;
import com.lqwawa.intleducation.module.learn.vo.SectionTaskListVo;
import com.lqwawa.intleducation.module.organcourse.OrganLibraryType;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述: 三习教案课程节详情的Fragment
 * 作者|时间: djj on 2019/7/23 0023 上午 10:27
 */
public class SxLessonSourceFragment extends IBaseFragment implements SxLessonSourceNavigator {

    private static final String KEY_EXTRA_NEED_FLAG = "KEY_EXTRA_NEED_FLAG";
    private static final String KEY_EXTRA_CAN_READ = "KEY_EXTRA_CAN_READ";
    private static final String KEY_EXTRA_CAN_EDIT = "KEY_EXTRA_CAN_EDIT";
    private static final String KEY_EXTRA_ONLINE_TEACHER = "KEY_EXTRA_ONLINE_TEACHER";
    private static final String KEY_EXTRA_COURSE_ID = "KEY_EXTRA_COURSE_ID";
    private static final String KEY_EXTRA_SECTION_ID = "KEY_EXTRA_SECTION_ID";
    private static final String KEY_EXTRA_EXERCISE_TYPE = "KEY_EXTRA_EXERCISE_TYPE";
    private static final String KEY_STATUS = "KEY_STATUS";
    private static final String KEY_LIBRARY_TYPE = "KEY_LIBRARY_TYPE";
    private static final String KEY_TASK_TYPE = "KEY_TASK_TYPE";
    private static final String KEY_EXTRA_MULTIPLE_CHOICE_COUNT = "KEY_EXTRA_MULTIPLE_CHOICE_COUNT";
    private int sourceType;
    //1：预习 2:练习 3：复习   不传或者-1 全部
    private String courseId;
    private String sectionId;
    private int exerciseType;
    private LessonSourceParams lessonSourceParams;
    private SectionDetailsVo mSectionDetailsVo;
    private boolean lessonNeedFlag;
    private int status, taskType;
    private int libraryType;
    private boolean isVideoCourse;
    private boolean mClassTeacher;
    private CourseDetailParams courseParams;
    private FrameLayout container;
    private CourseEmptyView mEmptyLayout;
    private ReadWeikeHelper mReadWeikeHelper;
    private TreeNode root;
    private TreeView treeView;
    private SxNodeViewFactory mNodeViewFactory;
    private ExamsAndTestExtrasVo extrasVo;
    private boolean isChoiceMode;
    private boolean isInitiativeTrigger;
    private ArrayList<Integer> filterArray;
    private SparseIntArray typeTable = new SparseIntArray();
    // 可以选择的最大条目
    private int mMultipleChoiceCount;

    public static SxLessonSourceFragment newInstance(boolean needFlag,
                                                     boolean canEdit,
                                                     boolean canRead,
                                                     boolean isOnlineTeacher,
                                                     @NonNull String courseId,
                                                     @NonNull String sectionId,
                                                     int status,
                                                     int exerciseType, int libraryType, int taskType,
                                                     int multipleChoiceCount,
                                                     @NonNull LessonSourceParams params) {
        SxLessonSourceFragment fragment = new SxLessonSourceFragment();
        Bundle arguments = new Bundle();
        arguments.putBoolean(KEY_EXTRA_NEED_FLAG, needFlag);
        arguments.putBoolean(KEY_EXTRA_CAN_EDIT, canEdit);
        arguments.putBoolean(KEY_EXTRA_CAN_READ, canRead);
        arguments.putBoolean(KEY_EXTRA_ONLINE_TEACHER, isOnlineTeacher);
        arguments.putString(KEY_EXTRA_COURSE_ID, courseId);
        arguments.putString(KEY_EXTRA_SECTION_ID, sectionId);
        arguments.putInt(KEY_STATUS, status);
        arguments.putInt(KEY_EXTRA_EXERCISE_TYPE, exerciseType);
        arguments.putInt(KEY_LIBRARY_TYPE, libraryType);
        arguments.putInt(KEY_TASK_TYPE, taskType);
        arguments.putInt(KEY_EXTRA_MULTIPLE_CHOICE_COUNT, multipleChoiceCount);
        arguments.putSerializable(FRAGMENT_BUNDLE_OBJECT, params);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    protected boolean initArgs(Bundle bundle) {
        courseId = bundle.getString(KEY_EXTRA_COURSE_ID);
        sectionId = bundle.getString(KEY_EXTRA_SECTION_ID);
        exerciseType = bundle.getInt(KEY_EXTRA_EXERCISE_TYPE);
        libraryType = bundle.getInt(KEY_LIBRARY_TYPE);
        if (bundle.containsKey(FRAGMENT_BUNDLE_OBJECT)) {
            lessonSourceParams = (LessonSourceParams) bundle.getSerializable(FRAGMENT_BUNDLE_OBJECT);
        }
        status = bundle.getInt(KEY_STATUS);
        lessonNeedFlag = lessonSourceParams.getRole() != UserHelper.MoocRoleType.TEACHER;
        CourseDetailParams courseParams = lessonSourceParams.getCourseParams();
        isVideoCourse = courseParams != null && (courseParams.getLibraryType() == OrganLibraryType.TYPE_VIDEO_LIBRARY
                || (courseParams.getLibraryType() == OrganLibraryType.TYPE_BRAIN_LIBRARY && courseParams.isVideoCourse()));

        mClassTeacher = (courseParams.isClassCourseEnter() && courseParams.isClassTeacher()) ||
                (courseParams.getCourseEnterType() == CourseDetailType.COURSE_DETAIL_CLASS_ENTER && lessonSourceParams.getRole() == UserHelper.MoocRoleType.TEACHER) ||
                        (lessonSourceParams.isChoiceMode() && lessonSourceParams.isInitiativeTrigger() && courseParams.isClassCourseEnter());
        taskType = bundle.getInt(KEY_TASK_TYPE, -1);
        mMultipleChoiceCount = bundle.getInt(KEY_EXTRA_MULTIPLE_CHOICE_COUNT);
        //主动进入，并选择true，非主动进入，并选择，false， 非主动进入，并不选择，false
        isChoiceMode = lessonSourceParams != null && lessonSourceParams.isChoiceMode();
        isInitiativeTrigger = lessonSourceParams != null && lessonSourceParams.isInitiativeTrigger();
        filterArray = lessonSourceParams.getFilterArray();

        if (EmptyUtil.isEmpty(courseId) ||
                EmptyUtil.isEmpty(sectionId) ||
                EmptyUtil.isEmpty(lessonSourceParams)) return false;
        return super.initArgs(bundle);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_sx_lesson_source;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        container = (FrameLayout) mRootView.findViewById(R.id.sx_container);
        mEmptyLayout = (CourseEmptyView) mRootView.findViewById(R.id.empty_layout);
        root = TreeNode.root();
        mNodeViewFactory = new SxNodeViewFactory();
        treeView = new TreeView(root, getContext(), mNodeViewFactory);
        treeView.setOnItemCheckBoxSelectedChanged((context, treeNode, checked) -> {
            if (onItemCheckBoxSelectedChanged !=null)
                onItemCheckBoxSelectedChanged.onItemCheckBoxSelectedChanged(context, treeNode, checked);
        });
    }

    public interface OnItemCheckBoxSelectedChanged{
        void onItemCheckBoxSelectedChanged(Context context,TreeNode treeNode,boolean checked);
    }

    private OnItemCheckBoxSelectedChanged onItemCheckBoxSelectedChanged;

    public void setOnItemCheckBoxSelectedChanged(OnItemCheckBoxSelectedChanged onItemCheckBoxSelectedChanged) {
        this.onItemCheckBoxSelectedChanged = onItemCheckBoxSelectedChanged;
    }

    @Override
    protected void initData() {
        super.initData();

        typeTable.append(1, CourseSelectItemFragment.KEY_WATCH_COURSE);
        typeTable.append(2, CourseSelectItemFragment.KEY_RELL_COURSE);
        typeTable.append(3, CourseSelectItemFragment.KEY_TASK_ORDER);
        typeTable.append(4, CourseSelectItemFragment.KEY_TEXT_BOOK);
        // 讲解课类型
        typeTable.append(5, CourseSelectItemFragment.KEY_LECTURE_COURSE);
        typeTable.append(6, CourseSelectItemFragment.KEY_TEXT_BOOK);

        registerBroadcastReceiver();

        getData();
    }


    private void getData() {
        String token = lessonSourceParams.getMemberId();
        int role = 2;
        if (lessonSourceParams.getRole() == UserHelper.MoocRoleType.TEACHER) {
            role = 1;
        }

        String classId = "";
        if (role == 1 && lessonSourceParams.getCourseParams().isClassCourseEnter()) {
            classId = lessonSourceParams.getCourseParams().getClassId();
        } else if (role == 1) {
            if (EmptyUtil.isNotEmpty(lessonSourceParams.getCourseParams().getClassId())) {
                classId = lessonSourceParams.getCourseParams().getClassId();
            }
        }
        mReadWeikeHelper = new ReadWeikeHelper(getActivity());
        treeView.setExtras(mReadWeikeHelper);

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
                updateViews(sectionDetailsVo);
            }
        });
    }

    private void updateViews(SectionDetailsVo sectionDetailsVo) {
        container.removeAllViews();
        if (root != null) {
            root.getChildren().clear();
        }
        extrasVo = new ExamsAndTestExtrasVo(courseParams == null ? "" : courseParams.getSchoolId(), lessonSourceParams, lessonNeedFlag,
                status, isVideoCourse, mClassTeacher, false, lessonSourceParams.isChoiceMode(), libraryType, mMultipleChoiceCount);
        List<SectionTaskListVo> taskList = sectionDetailsVo.getTaskList();
        for (int index = 0; index < taskList.size(); index++) {
            SectionTaskListVo taskListVO = taskList.get(index);
            //不在执行循环体里continue后面的语句而是跳到下一个循环入口处执行下一个循环
            if (!isInitiativeTrigger && isChoiceMode && !isShowType(taskType, taskListVO)) continue;
            TreeNode treeNode = new TreeNode(taskListVO);
            treeNode.setLevel(0);
            List<SectionResListVo> dataList = taskListVO.getData();
            if (!isInitiativeTrigger && isChoiceMode) {
                dataList = getDataList(taskListVO, dataList);
            }
            //处理课中练习统计问题
            if (exerciseType == 1){
                sourceType =0;
            }else if (exerciseType == 2){
                sourceType = 3;
            }else if (exerciseType == 3){
                sourceType = 0;
            }
            for (SectionResListVo datum : dataList) {
                datum.setTaskType(taskListVO.getTaskType());
                datum.setTaskName(taskListVO.getTaskName());
                datum.setChapterId(datum.getId());
                datum.setCourseId(courseId);
                datum.setSourceType(sourceType);
                TreeNode treeNode1 = new TreeNode(datum);
                treeNode1.setExtras(extrasVo);
                treeNode1.setLevel(1);
                treeNode.addChild(treeNode1);
            }
            root.addChild(treeNode);
        }
        View view = treeView.getView();
        treeView.expandAll();
        if (root.getChildren().size() == 0) {
            container.setVisibility(View.GONE);
            mEmptyLayout.setVisibility(View.VISIBLE);
            return;
        } else {
            container.setVisibility(View.VISIBLE);
            mEmptyLayout.setVisibility(View.GONE);
            container.addView(view);
        }
    }

    private List<SectionResListVo> getDataList(SectionTaskListVo taskListVO,
                                               List<SectionResListVo> dataList) {
        if (taskListVO == null || dataList == null || dataList.isEmpty()) {
            return null;
        }
        List<SectionResListVo> list = new ArrayList<>();
        for (SectionResListVo sectionResListVo : dataList) {
            int resType = sectionResListVo.getResType();
            if (taskType != CourseSelectItemFragment.KEY_WATCH_COURSE) {
                if (resType > 10000) {
                    resType -= 10000;
                }

                // 原本不是看课件类型，是读写单和听读课类型，需要过滤视频音频
                if (resType == 2 || resType == 3) {
                    // 过滤视频 和 音频
                    continue;
                }

                if (taskType == CourseSelectItemFragment.KEY_TASK_ORDER) {
                    // 如果显示的是读写单类型，那就需要过滤看课件中的听读课课件
                    if (resType == 18 || resType == 19) {
                        // 过滤18,19
                        continue;
                    }
                }
            }

            if (taskType == CourseSelectItemFragment.KEY_TEXT_BOOK) {
                // Q配音的选择
                if (resType != 30) {
                    continue;
                }
            }

            if (taskType == CourseSelectItemFragment.KEY_RELL_COURSE
                    && typeTable.get(sectionResListVo.getTaskType())
                    == CourseSelectItemFragment.KEY_LECTURE_COURSE) {
                // 选择复述课件，讲解课的显示
                // sectionResListVo.setResProperties("");
            }

            if (!sectionResListVo.isIsShield()) {
                if (typeTable.get(sectionResListVo.getTaskType()) == CourseSelectItemFragment.KEY_RELL_COURSE && taskType == CourseSelectItemFragment.KEY_RELL_COURSE) {
                    if (EmptyUtil.isNotEmpty(filterArray) && filterArray.contains(resType)) {
                        list.add(sectionResListVo);
                    }
                } else {
                    list.add(sectionResListVo);
                }
            }
        }
        return list;
    }

    @Override
    public void triggerChoice(boolean open) {
        // 触发添加到作业库的动作,开放选择功能
        if (extrasVo != null) extrasVo.setmChoiceMode(open);
        treeView.notifychanged();
        treeView.deselectAll();
    }

    @Override
    public List<TreeNode> getChoiceResource() {
        // 获取已经选中的作业库
        List<TreeNode> data = treeView.getSelectedNodes();
        List<TreeNode> choiceArray = new ArrayList<>();
        if (EmptyUtil.isNotEmpty(data)) {
            for (TreeNode vo : data) {
                if (vo.isSelected()) {
                    choiceArray.add(vo);
                }
            }
        }
        return choiceArray;
    }

    @Override
    public void clearAllResourceState() {
        // 清楚所有资源选中状态
        if (treeView != null)
            treeView.deselectAll();
    }

    @Override
    public TreeView getTreeView() {
        if (EmptyUtil.isNotEmpty(treeView)) {
            return treeView;
        }
        return null;
    }

    /**
     * 判断是否需要显示
     *
     * @param realTaskType 当前选取类型
     * @param vo           资源数据集合
     * @return boolean true 需要显示
     */
    private boolean isShowType(int realTaskType, SectionTaskListVo vo) {
        int taskType = vo.getTaskType();
        if (realTaskType == CourseSelectItemFragment.KEY_RELL_COURSE) {
            // 选择复述课件
            if (taskType == 2
                    || taskType == 4
                    || taskType == 5) {
                // 听说作业 看课件 讲解课
                return true;
            }
        } else if (realTaskType == CourseSelectItemFragment.KEY_TASK_ORDER) {
            if (taskType == 3 || taskType == 4) {
                // 读写作业 看课件
                return true;
            }
        } else if (realTaskType == CourseSelectItemFragment.KEY_TEXT_BOOK) {
            // 视频课类型
            if (taskType == 1 || taskType == 6) {
                return true;
            }
        } else if (realTaskType == CourseSelectItemFragment.KEY_WATCH_COURSE) {
            // 看课本类型
            if (taskType == 1 || taskType == 4 || taskType == 2 || taskType == 5 || taskType == 6) {
                // 看课件 视频课
                // 新增选择讲解课 听说作业
                return true;
            }
        }

        return false;
    }

    /**
     * 注册广播事件,接收事件刷新
     */
    protected void registerBroadcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(LessonSourceFragment.LESSON_RESOURCE_CHOICE_PUBLISH_ACTION);//作业库发布的刷新
        //注册广播
        getActivity().registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(EmptyUtil.isNotEmpty(getActivity())){
            getActivity().unregisterReceiver(mBroadcastReceiver);
        }
    }

    /**
     * 数据刷新广播的处理
     */
    protected BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(LessonSourceFragment.LESSON_RESOURCE_CHOICE_PUBLISH_ACTION)) {// 读写单
                getData();
            }
        }
    };
}
