package com.lqwawa.intleducation.module.discovery.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ui.MyBaseAdapter;
import com.lqwawa.intleducation.base.ui.MyBaseFragment;
import com.lqwawa.intleducation.base.utils.DisplayUtil;
import com.lqwawa.intleducation.base.vo.RequestVo;
import com.lqwawa.intleducation.base.widgets.PullRefreshView.PullToRefreshView;
import com.lqwawa.intleducation.base.widgets.SuperListView;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.common.Common;
import com.lqwawa.intleducation.common.interfaces.OnLoadStatusChangeListener;
import com.lqwawa.intleducation.common.ui.CommentDialog;
import com.lqwawa.intleducation.common.utils.DrawableUtil;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.module.discovery.adapter.CourseChapterAdapter;
import com.lqwawa.intleducation.module.discovery.adapter.CourseCommentAdapter;
import com.lqwawa.intleducation.module.discovery.adapter.CourseIntroduceAdapter;
import com.lqwawa.intleducation.module.discovery.ui.classcourse.ClassCourseActivity;
import com.lqwawa.intleducation.module.discovery.ui.coursedetail.CourseDetailParams;
import com.lqwawa.intleducation.module.discovery.ui.lesson.select.CourseSelectItemOuterFragment;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.course.chapter.CourseChapterParams;
import com.lqwawa.intleducation.module.discovery.vo.ChapterVo;
import com.lqwawa.intleducation.module.discovery.vo.CommentVo;
import com.lqwawa.intleducation.module.discovery.vo.CourseDetailsVo;
import com.lqwawa.intleducation.module.discovery.vo.CourseIntroduceVo;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;
import com.lqwawa.intleducation.module.learn.tool.TaskSliderHelper;
import com.lqwawa.intleducation.module.learn.ui.LessonDetailsActivity;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    public static final String KEY_EXTRA_ONLINE_RELEVANCE = "KEY_EXTRA_ONLINE_RELEVANCE";
    // 需要显示的复述课件，听说课类型集合
    public static final String KEY_EXTRA_FILTER_COLLECTION = "KEY_EXTRA_FILTER_COLLECTION";
    // 是否主动触发
    public static final String KEY_EXTRA_INITIATIVE_TRIGGER = "KEY_EXTRA_INITIATIVE_TRIGGER";
    // 学校ID
    public static final String KEY_EXTRA_SCHOOL_ID = "KEY_EXTRA_SCHOOL_ID";
    // ClassId
    public static final String KEY_EXTRA_CLASS_ID = "KEY_EXTRA_CLASS_ID";

    private SuperListView listView;
    private FrameLayout mEmptyView;

    private List<CourseIntroduceVo> courseIntroduceList;
    private CourseIntroduceAdapter courseIntroduceAdapter;
    private List<ChapterVo> courseChapterList;
    private CourseChapterAdapter courseChapterAdapter;
    private List<CommentVo> commentList;
    private CourseCommentAdapter courseCommentAdatpter;
    private CourseDetailsVo courseDetailsVo;
    private CommentDialog commentDialog;
    OnLoadStatusChangeListener onLoadStatusChangeListener;
    private CourseVo flagCourseData = null;
    boolean haveInitListData = false;
    private TopBar topBar;
    private PullToRefreshView pullToRefresh;
    private int mTaskType;
    // 是否是关联学程学习任务的选择资源
    private boolean isOnlineRelevance;
    // 复述课件类型的过滤集合
    private ArrayList<Integer> mFilterArray;

    private FrameLayout mNewCartContainer;
    private TextView mTvWorkCart;
    private TextView mTvCartPoint;
    private boolean initiativeTrigger;

    private String mSchoolId;
    private String mClassId;
    private Bundle mExtras;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course_select, container, false);
        topBar = (TopBar) view.findViewById(R.id.select_top_bar);
        pullToRefresh = (PullToRefreshView) view.findViewById(R.id.select_pull_to_refresh);
        mEmptyView = (FrameLayout) view.findViewById(R.id.empty_layout);
        listView = (SuperListView) view.findViewById(R.id.course_select_listView);
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

        if (initiativeTrigger) {
            int color = UIUtil.getColor(R.color.colorPink);
            int radius = DisplayUtil.dip2px(UIUtil.getContext(), 8);

            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mTvCartPoint.getLayoutParams();
            float density = UIUtil.getApp().getResources().getDisplayMetrics().density;
            int topMargin = layoutParams.topMargin = 40 - DisplayUtil.dip2px(UIUtil.getContext(), 8);
            layoutParams.topMargin = topMargin;
            mTvCartPoint.setLayoutParams(layoutParams);
            mTvCartPoint.setBackground(DrawableUtil.createDrawable(color, color, radius));

            mNewCartContainer.setVisibility(View.VISIBLE);
            mNewCartContainer.setOnClickListener(this);
        }

        int tasktype = getArguments().getInt("tasktype");
        if (tasktype == CourseSelectItemFragment.KEY_WATCH_COURSE) {
            mTaskType = 1;
        } else if (tasktype == CourseSelectItemFragment.KEY_RELL_COURSE) {
            mTaskType = 2;
        } else if (tasktype == CourseSelectItemFragment.KEY_TASK_ORDER) {
            mTaskType = 3;
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
            if (EmptyUtil.isNotEmpty(TaskSliderHelper.onWorkCartListener)) {
                TaskSliderHelper.onWorkCartListener.enterIntroTaskDetailActivity(getActivity(),mSchoolId,mClassId,mExtras);
            }
        }
    }

    public void updateData() {
        getData();
    }

    private void initData() {
        boolean isOnlineRelevance = getArguments().getBoolean(KEY_EXTRA_ONLINE_RELEVANCE);
        mFilterArray = getArguments().getIntegerArrayList(KEY_EXTRA_FILTER_COLLECTION);
        this.isOnlineRelevance = isOnlineRelevance;
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
        courseChapterList = new ArrayList<ChapterVo>();
        listView.setAdapter(courseChapterAdapter);
        courseChapterAdapter.setOnSelectListener(new CourseChapterAdapter.OnSelectListener() {
            @Override
            public void onSelect(ChapterVo chapterVo) {
                if (chapterVo != null) {
                    if (initiativeTrigger) {
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
                        }

                        int role = UserHelper.MoocRoleType.TEACHER;
                        int teacherType = UserHelper.TeacherType.TEACHER_LECTURER;
                        CourseChapterParams params = new CourseChapterParams(memberId,role,teacherType,false);
                        params.setCourseParams(courseParams);
                        params.setChoiceMode(true,true);

                        LessonDetailsActivity.start(activity, courseId, chapterId,
                                sectionName, name,false,true,true,
                                status, memberId,chapterVo.isContainAssistantWork(),
                                "",false, courseVo,
                                false,false,params,
                                mExtras);
                    } else {
                        Bundle arguments = getArguments();
                        /*Bundle bundle = new Bundle();
                        bundle.putSerializable("ChapterVo",chapterVo);
                        bundle.putInt("tasktype",arguments.getInt("tasktype",1));
                        int multipleChoiceCount = arguments.getInt(CourseSelectItemFragment.KEY_EXTRA_MULTIPLE_CHOICE_COUNT);
                        bundle.putInt(CourseSelectItemFragment.KEY_EXTRA_MULTIPLE_CHOICE_COUNT,multipleChoiceCount);
                        bundle.putIntegerArrayList(CourseSelectItemFragment.KEY_EXTRA_FILTER_COLLECTION,mFilterArray);
                        bundle.putBoolean(CourseSelectItemFragment.KEY_EXTRA_ONLINE_RELEVANCE,isOnlineRelevance);
                        CourseSelectItemFragment courseSelectFragment = new CourseSelectItemFragment();
                        courseSelectFragment.setArguments(bundle);*/

                        int taskType = arguments.getInt("tasktype", 1);
                        int multipleChoiceCount = arguments.getInt(CourseSelectItemFragment.KEY_EXTRA_MULTIPLE_CHOICE_COUNT);
                        Fragment courseSelectFragment =
                                CourseSelectItemOuterFragment.newInstance(chapterVo, taskType, multipleChoiceCount, mFilterArray, isOnlineRelevance);


                        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                        fragmentTransaction.add(R.id.root_fragment_container, courseSelectFragment);
                        fragmentTransaction.show(courseSelectFragment);
                        fragmentTransaction.commit();
                        fragmentTransaction.addToBackStack(null);
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
        getData(AppConfig.PAGE_SIZE);
    }

    private void getData(int pageSize) {
        RequestVo requestVo = new RequestVo();
//        requestVo.addParams("token", activity.getIntent().getStringExtra("memberId"));

//            requestVo.addParams("pageIndex", pageIndex);
//            requestVo.addParams("pageSize", pageSize);
        requestVo.addParams("dataType", 2);
        requestVo.addParams("id", flagCourseData.getId());
        // requestVo.addParams("taskType",mTaskType);

        RequestParams params =
                new RequestParams(
                        AppConfig.ServerUrl.GetCourseDetailsById + requestVo.getParams());
        pageIndex = 0;

        params.setConnectTimeout(50000);

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                if (onLoadStatusChangeListener != null) {
                    onLoadStatusChangeListener.onLoadSuccess();
                }
                pullToRefresh.onHeaderRefreshComplete();
                courseDetailsVo = JSON.parseObject(s,
                        new TypeReference<CourseDetailsVo>() {
                        });
                if (courseDetailsVo.getCode() == 0) {

                    courseChapterList = courseDetailsVo.getChapterList();

                    if (courseChapterList != null) {
                        for (int i = 0; i < courseChapterList.size(); i++) {
                            courseChapterList.get(i).setCourseId(flagCourseData.getCourseId());
                            courseChapterList.get(i).setChapterName(courseDetailsVo.getChapterName());
                            courseChapterList.get(i).setSectionName(courseDetailsVo.getSectionName());
                        }
                    }
                    if (flagCourseData != null && !haveInitListData) {
                        if (flagCourseData.getChapList() != null && flagCourseData.getChapList().size() > 0) {
                            for (int i = 0; i < courseChapterList.size(); i++) {
                                for (int j = 0; j < flagCourseData.getChapList().size(); j++) {
                                    if (courseChapterList.get(i).getId() != null && courseChapterList.get(i).getId()
                                            .equals(flagCourseData.getChapList().get(j).getId())) {
                                        if (flagCourseData.getChapList().get(j).getSectionList() != null
                                                && flagCourseData.getChapList().get(j).getSectionList().size() > 0) {//在小节下
                                            for (int p = 0; p < courseChapterList.get(i).getChildren().size(); p++) {
                                                for (int k = 0; k < flagCourseData.getChapList().get(j).getSectionList().size(); k++) {
                                                    if (courseChapterList.get(i).getChildren().get(p).getId() != null &&
                                                            courseChapterList.get(i).getChildren().get(p).getId()
                                                                    .equals(flagCourseData.getChapList().get(j).getSectionList().get(k).getId())) {
                                                        courseChapterList.get(i).getChildren().get(p).setFlag(1);
                                                        break;
                                                    }
                                                }
                                            }
                                        } else {
                                            int flagValue = 0;
                                            flagValue |= (flagCourseData.getChapList().get(j).getTaskList() != null
                                                    && flagCourseData.getChapList().get(j).getTaskList().size() > 0) ? 0x01 : 0;
                                            flagValue |= (flagCourseData.getChapList().get(j).getTaskList() != null
                                                    && flagCourseData.getChapList().get(j).getTaskList().size() > 0) ? 0x02 : 0;
                                            courseChapterList.get(i).setFlag(flagValue);
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    haveInitListData = true;
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
            public void onCancelled(CancelledException e) {
                pullToRefresh.onHeaderRefreshComplete();
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                pullToRefresh.onHeaderRefreshComplete();
                if (onLoadStatusChangeListener != null) {
                    onLoadStatusChangeListener.onLoadFlailed();
                }
            }

            @Override
            public void onFinished() {
            }
        });
    }

    public void getMore() {
        RequestVo requestVo = new RequestVo();

//            requestVo.addParams("pageIndex", pageIndex + 1);
//            requestVo.addParams("pageSize", AppConfig.PAGE_SIZE);


        requestVo.addParams("dataType", 2);
        requestVo.addParams("id", flagCourseData.getId());

//            requestVo.addParams("token", activity.getIntent().getStringExtra("memberId"));

        RequestParams params =
                new RequestParams(
                        AppConfig.ServerUrl.GetCourseDetailsById + requestVo.getParams());
        pageIndex = 0;
        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                pullToRefresh.onFooterRefreshComplete();
                courseDetailsVo = JSON.parseObject(s,
                        new TypeReference<CourseDetailsVo>() {
                        });
                if (courseDetailsVo.getCode() == 0) {

                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {

            }

            @Override
            public void onFinished() {
            }
        });
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
