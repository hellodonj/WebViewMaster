package com.lqwawa.intleducation.module.discovery.ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ui.MyBaseAdapter;
import com.lqwawa.intleducation.base.ui.MyBaseFragment;
import com.lqwawa.intleducation.base.widgets.PullRefreshView.PullToRefreshView;
import com.lqwawa.intleducation.base.widgets.SuperGridView;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.common.Common;
import com.lqwawa.intleducation.common.interfaces.OnLoadStatusChangeListener;
import com.lqwawa.intleducation.common.ui.ContactsMessageDialog;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.common.utils.Utils;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;
import com.lqwawa.intleducation.factory.helper.LQConfigHelper;
import com.lqwawa.intleducation.factory.helper.LQCourseHelper;
import com.lqwawa.intleducation.module.discovery.adapter.CourseChapterAdapter;
import com.lqwawa.intleducation.module.discovery.ui.coursedetail.CourseDetailParams;
import com.lqwawa.intleducation.module.discovery.ui.coursedetail.CourseDetailType;
import com.lqwawa.intleducation.module.discovery.ui.lesson.detail.LessonSourceParams;
import com.lqwawa.intleducation.module.discovery.ui.lesson.select.CourseSelectItemOuterFragment;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.course.chapter.CourseChapterParams;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.home.LanguageType;
import com.lqwawa.intleducation.module.discovery.ui.subject.SetupConfigType;
import com.lqwawa.intleducation.module.discovery.ui.subject.add.AddSubjectActivity;
import com.lqwawa.intleducation.module.discovery.vo.ChapterVo;
import com.lqwawa.intleducation.module.discovery.vo.CourseDetailsVo;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;
import com.lqwawa.intleducation.module.learn.tool.TaskSliderHelper;
import com.lqwawa.intleducation.module.learn.ui.ExamsAndTestsActivity;
import com.lqwawa.intleducation.module.learn.ui.LessonDetailsActivity;
import com.lqwawa.intleducation.module.learn.ui.SxLessonDetailsActivity;
import com.lqwawa.intleducation.module.organcourse.OrganLibraryType;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.lqwawa.intleducation.module.discovery.adapter.CourseChapterAdapter.TYPE_LESSON;

/**
 * ================================================
 * 作    者：Blizzard-liu
 * 版    本：1.0
 * 创建日期：2017/8/10 10:19
 * 描    述：课程选择---课程列表界面
 * 修订历史：
 * ================================================
 */

public class CourseSelectFragment extends MyBaseFragment implements View.OnClickListener {
    private static final String TAG = "CourseSelectFragment";

    private static final int SUBJECT_SETTING_REQUEST_CODE = 1 << 1;

    public static final String KEY_EXTRA_ONLINE_RELEVANCE = "KEY_EXTRA_ONLINE_RELEVANCE";
    // 需要显示的复述课件，听说课类型集合
    public static final String KEY_EXTRA_FILTER_COLLECTION = "KEY_EXTRA_FILTER_COLLECTION";
    // 是否主动触发
    public static final String KEY_EXTRA_INITIATIVE_TRIGGER = "KEY_EXTRA_INITIATIVE_TRIGGER";
    // 学校ID
    public static final String KEY_EXTRA_SCHOOL_ID = "KEY_EXTRA_SCHOOL_ID";
    // ClassId
    public static final String KEY_EXTRA_CLASS_ID = "KEY_EXTRA_CLASS_ID";
    // 入口类型
    public static final String KEY_EXTRA_ENTER_TYPE = "KEY_EXTRA_ENTER_TYPE";
    public static final int TYPE_EXAM = 1;
    public static final int TYPE_TEST = 2;

    private SuperGridView listView;
    private FrameLayout mEmptyView;

    private List<ChapterVo> courseChapterList;
    private CourseChapterAdapter courseChapterAdapter;
    OnLoadStatusChangeListener onLoadStatusChangeListener;
    private CourseVo flagCourseData = null;
    private TopBar topBar;
    private PullToRefreshView pullToRefresh;
    // 复述课件类型的过滤集合
    private ArrayList<Integer> mFilterArray;

    private FrameLayout mNewCartContainer;
    private TextView mTvWorkCart;
    private TextView mTvCartPoint;
    private boolean initiativeTrigger;

    private String mSchoolId;
    private String mClassId;
    private Bundle mExtras;

    private int mEnterType;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course_select, container, false);
        topBar = (TopBar) view.findViewById(R.id.select_top_bar);
        pullToRefresh = (PullToRefreshView) view.findViewById(R.id.select_pull_to_refresh);
        mEmptyView = (FrameLayout) view.findViewById(R.id.empty_layout);
        listView = (SuperGridView) view.findViewById(R.id.course_select_listView);
        mNewCartContainer = (FrameLayout) view.findViewById(R.id.new_cart_container);
        mTvWorkCart = (TextView) view.findViewById(R.id.tv_work_cart);
        mTvCartPoint = (TextView) view.findViewById(R.id.tv_cart_point);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = CourseSelectFragment.this.getActivity();

        flagCourseData = (CourseVo) getArguments().getSerializable("CourseVo");
        initiativeTrigger = getArguments().getBoolean(KEY_EXTRA_INITIATIVE_TRIGGER);
        mSchoolId = getArguments().getString(KEY_EXTRA_SCHOOL_ID);
        mClassId = getArguments().getString(KEY_EXTRA_CLASS_ID);
        mExtras = getArguments().getBundle(Common.Constance.KEY_EXTRAS_STUDY_TASK);
        mEnterType = getArguments().getInt(KEY_EXTRA_ENTER_TYPE);

        if (initiativeTrigger) {

            mNewCartContainer.setVisibility(View.VISIBLE);
            mNewCartContainer.setOnClickListener(this);
        }

        initData();
    }

    public void setOnLoadStatusChangeListener(OnLoadStatusChangeListener listener) {
        onLoadStatusChangeListener = listener;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshCartPoint();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.new_cart_container) {
            handleSubjectSettingData(getActivity(), UserHelper.getUserId());
        }
    }

    public void handleSubjectSettingData(Context context,
                                         String memberId) {
        int languageRes = Utils.isZh(UIUtil.getContext()) ? LanguageType.LANGUAGE_CHINESE : LanguageType.LANGUAGE_OTHER;
        LQConfigHelper.requestSetupConfigData(memberId, SetupConfigType.TYPE_TEACHER, languageRes, new DataSource.Callback<List<LQCourseConfigEntity>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                //没有数据
                popChooseSubjectDialog(context);
            }

            @Override
            public void onDataLoaded(List<LQCourseConfigEntity> entities) {
                if (entities == null || entities.size() == 0) {
                    popChooseSubjectDialog(context);
                } else {
                    //有数据
                    if (EmptyUtil.isNotEmpty(TaskSliderHelper.onWorkCartListener)) {
                        TaskSliderHelper.onWorkCartListener.enterIntroTaskDetailActivity(activity, mSchoolId, mClassId, mExtras);
                    }
                }
            }
        });
    }

    private static void popChooseSubjectDialog(Context context) {
        ContactsMessageDialog messageDialog = new ContactsMessageDialog(
                context,
                null,
                context.getString(R.string.label_unset_choose_subject),
                context.getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                },
                context.getString(R.string.label_choose_subject),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        AddSubjectActivity.show((Activity) context, false, SUBJECT_SETTING_REQUEST_CODE);
                    }
                });
        messageDialog.show();
    }

    public void updateData() {
        getData();
    }

    private void initData() {
        boolean isOnlineRelevance = getArguments().getBoolean(KEY_EXTRA_ONLINE_RELEVANCE);
        mFilterArray = getArguments().getIntegerArrayList(KEY_EXTRA_FILTER_COLLECTION);
        topBar.setBack(true);
        topBar.setTitle(flagCourseData.getName());
        topBar.findViewById(R.id.left_function1_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOnlineRelevance) {
                    getActivity().finish();
                } else {
                    getFragmentManager().popBackStack();
                }
            }
        });

        pullToRefresh.setOnHeaderRefreshListener(new PullToRefreshView.OnHeaderRefreshListener() {
            @Override
            public void onHeaderRefresh(PullToRefreshView view) {
                getData();
            }
        });
        pullToRefresh.setLoadMoreEnable(false);

        courseChapterAdapter = new CourseChapterAdapter(activity, flagCourseData.getCourseId(), false,
                new MyBaseAdapter.OnContentChangedListener() {
                    @Override
                    public void OnContentChanged() {
                        getData();
                    }
                });
        courseChapterAdapter.setCourseSelect(true);
        courseChapterAdapter.setIsClassCourseEnter(mEnterType == CourseDetailType.COURSE_DETAIL_CLASS_ENTER);
        courseChapterList = new ArrayList<ChapterVo>();
        listView.setAdapter(courseChapterAdapter);
        listView.setNumColumns(1);
        courseChapterAdapter.setOnSelectListener(new CourseChapterAdapter.OnSelectListener() {
            @Override
            public void onSelect(ChapterVo chapterVo) {
                if (chapterVo != null) {
                    CourseVo courseVo = flagCourseData;
                    String courseId = courseVo.getId();
                    String chapterId = chapterVo.getId();
                    String sectionName = chapterVo.getSectionName();
                    String name = chapterVo.getName();
                    // 当前节的状态
                    int status = chapterVo.getStatus();
                    String memberId = UserHelper.getUserId();
                    CourseDetailParams courseParams = new CourseDetailParams();
                    if (courseParams != null && courseVo != null) {
                        courseParams.setBindSchoolId(courseVo.getBindSchoolId());
                        courseParams.setBindClassId(courseVo.getBindClassId());
                        courseParams.setCourseId(courseVo.getId());
                        courseParams.setCourseName(courseVo.getName());
                        // 填充参数
                        courseParams.setSchoolId(mSchoolId);
                        courseParams.setClassId(mClassId);
                        courseParams.setCourseEnterType(mEnterType);
                        courseParams.setLibraryType(courseVo.getLibraryType());
                        courseParams.setIsVideoCourse(courseVo.getType() == 2);
                    }

                    int role = UserHelper.MoocRoleType.TEACHER;
                    int teacherType = UserHelper.TeacherType.TEACHER_LECTURER;
                    CourseChapterParams params = new CourseChapterParams(memberId, role, teacherType, false);
                    params.setCourseParams(courseParams);
                    params.setChoiceMode(true, initiativeTrigger);
                    LessonSourceParams lessonSourceParams = LessonSourceParams.buildParams(params);
                    int libraryType = courseVo == null ? -1 : courseVo.getLibraryType();

                    if (initiativeTrigger) {
                        if (libraryType == OrganLibraryType.TYPE_TEACHING_PLAN) {
                            if (chapterVo.getExamType() == TYPE_EXAM) {
                                if (chapterVo.getIsChildren()){ //测试
                                    ExamsAndTestsActivity.start(activity, courseId, chapterId, params.isTeacherVisitor(), chapterVo.getStatus(), libraryType,TYPE_TEST,lessonSourceParams);
                                }else { //考试
                                    ExamsAndTestsActivity.start(activity, courseId, chapterId, params.isTeacherVisitor(), chapterVo.getStatus(), libraryType,TYPE_EXAM,lessonSourceParams);
                                }
                            } else if (chapterVo.getExamType() == TYPE_LESSON){
                                //普通教案详情入口
                                if (chapterVo.getIsChildren()) SxLessonDetailsActivity.start(activity, courseId, chapterId,
                                        sectionName, name, false, true, true,
                                        status, memberId, chapterVo.isContainAssistantWork(),
                                        "", false, courseVo,
                                        false, false, params, mExtras);
                            }
                        } else {
                            if (chapterVo.getIsChildren()) LessonDetailsActivity.start(activity, courseId, chapterId,
                                        sectionName, name, false, true, true,
                                        status, memberId, chapterVo.isContainAssistantWork(),
                                        "", false, courseVo,
                                        false, false, params,
                                        mExtras);
                        }
                    } else {
                        Bundle arguments = getArguments();
                        int taskType = arguments.getInt("tasktype", 1);
                        if (libraryType == OrganLibraryType.TYPE_TEACHING_PLAN) {
                            if (chapterVo.getExamType() == TYPE_EXAM) {
                                if (chapterVo.getIsChildren()){ //测试
                                    ExamsAndTestsActivity.start(activity, taskType,courseId, chapterId, params.isTeacherVisitor(), chapterVo.getStatus(), libraryType,TYPE_TEST,lessonSourceParams);
                                }else { //考试
                                    ExamsAndTestsActivity.start(activity, taskType,courseId, chapterId, params.isTeacherVisitor(), chapterVo.getStatus(), libraryType,TYPE_EXAM,lessonSourceParams);
                                }
                            } else if (chapterVo.getExamType() == TYPE_LESSON){
                                //普通教案详情入口
                                SxLessonDetailsActivity.start(activity,taskType, courseId, chapterId,
                                        sectionName, name, false, true, true,
                                        status, memberId, chapterVo.isContainAssistantWork(),
                                        "", false, courseVo,
                                        false, false, params, mExtras);
                            }
                        }else {
                            if (!chapterVo.getIsChildren())return;
                            CourseVo courseVo1 = flagCourseData;
                            CourseDetailParams courseParams1 = new CourseDetailParams();
                            // 填充参数
                            courseParams1.setSchoolId(mSchoolId);
                            courseParams1.setClassId(mClassId);
                            courseParams1.setCourseEnterType(mEnterType);
                            courseParams1.setLibraryType(courseVo1.getLibraryType());
                            courseParams1.setIsVideoCourse(courseVo1.getType() == 2);


                            int multipleChoiceCount = arguments.getInt(CourseSelectItemFragment.KEY_EXTRA_MULTIPLE_CHOICE_COUNT);
                            Fragment courseSelectFragment =
                                    CourseSelectItemOuterFragment.newInstance(chapterVo, taskType, multipleChoiceCount, mFilterArray, isOnlineRelevance, courseParams1);

                            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                            fragmentTransaction.add(R.id.root_fragment_container, courseSelectFragment);
                            fragmentTransaction.show(courseSelectFragment);
                            fragmentTransaction.commit();
                            fragmentTransaction.addToBackStack(null);
                        }
                    }
                }
            }
        });

        pullToRefresh.setLastUpdated(new Date().toLocaleString());
        pullToRefresh.showRefresh();
        getData();
    }

    private int pageIndex = 0;

    private void getData() {
        pageIndex = 0;
        // mSchoolId和mClassId都不空来自班级学程
        if (!TextUtils.isEmpty(mSchoolId) && !TextUtils.isEmpty(mClassId)) {
            LQCourseHelper.requestChapterByCourseId(mClassId, flagCourseData.getId(),
                    new Callback());
        } else {
            LQCourseHelper.requestChapterByCourseId(UserHelper.getUserId(), flagCourseData.getId(),
                    mSchoolId, new Callback());
        }
    }

    private class Callback implements DataSource.Callback<CourseDetailsVo> {
        @Override
        public void onDataLoaded(CourseDetailsVo courseDetailsVo) {
            if (onLoadStatusChangeListener != null) {
                onLoadStatusChangeListener.onLoadSuccess();
            }
            pullToRefresh.onHeaderRefreshComplete();

            if (courseDetailsVo.getCode() == 0) {

                courseChapterList = courseDetailsVo.getChapters();

                if (courseChapterList != null) {
                    for (int i = 0; i < courseChapterList.size(); i++) {
                        ChapterVo chapterVo = courseChapterList.get(i);
                        chapterVo.setCourseId(flagCourseData.getCourseId());
                        chapterVo.setChapterName(courseDetailsVo.getChapterName());
                        chapterVo.setSectionName(courseDetailsVo.getSectionName());
                    }
                }
                courseChapterAdapter.setData(courseChapterList);
                courseChapterAdapter.notifyDataSetChanged();

                if (EmptyUtil.isNotEmpty(courseChapterList)) {
                    // 有数据
                    mEmptyView.setVisibility(View.GONE);
                    pullToRefresh.setVisibility(View.VISIBLE);
                } else {
                    // 没有数据
                    mEmptyView.setVisibility(View.VISIBLE);
                    pullToRefresh.setVisibility(View.GONE);
                }

            }

        }

        @Override
        public void onDataNotAvailable(int strRes) {
            pullToRefresh.onHeaderRefreshComplete();
            if (onLoadStatusChangeListener != null) {
                onLoadStatusChangeListener.onLoadFlailed();
            }
        }
    }

    /**
     * 刷新红点
     */
    private void refreshCartPoint() {
        if (EmptyUtil.isNotEmpty(TaskSliderHelper.onWorkCartListener)) {
            int count = TaskSliderHelper.onWorkCartListener.takeTaskCount();
            mTvCartPoint.setText(Integer.toString(count));
            if (count == 0) {
                mTvCartPoint.setVisibility(View.GONE);
            } else {
                mTvCartPoint.setVisibility(View.VISIBLE);
            }
        }
    }

}
