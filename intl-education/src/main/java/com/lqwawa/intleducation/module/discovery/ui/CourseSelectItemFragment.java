package com.lqwawa.intleducation.module.discovery.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lqwawa.client.pojo.SourceFromType;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ui.MyBaseFragment;
import com.lqwawa.intleducation.base.utils.ToastUtil;
import com.lqwawa.intleducation.base.vo.RequestVo;
import com.lqwawa.intleducation.base.vo.ResponseVo;
import com.lqwawa.intleducation.base.widgets.PullRefreshView.PullToRefreshView;
import com.lqwawa.intleducation.base.widgets.SuperListView;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.common.interfaces.OnLoadStatusChangeListener;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.RefreshUtil;
import com.lqwawa.intleducation.factory.event.EventConstant;
import com.lqwawa.intleducation.factory.event.EventWrapper;
import com.lqwawa.intleducation.module.discovery.adapter.CourseResListAdapter;
import com.lqwawa.intleducation.module.discovery.ui.lesson.detail.ReadWeikeHelper;
import com.lqwawa.intleducation.module.discovery.ui.lesson.select.ResourceSelectListener;
import com.lqwawa.intleducation.module.discovery.vo.ChapterVo;
import com.lqwawa.intleducation.module.learn.tool.TaskSliderHelper;
import com.lqwawa.intleducation.module.learn.vo.SectionDetailsVo;
import com.lqwawa.intleducation.module.learn.vo.SectionResListVo;

import org.greenrobot.eventbus.EventBus;
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
  * 描    述：课程选择---课程列表条目界面
  * 修订历史：
  * ================================================
  */

public class CourseSelectItemFragment extends MyBaseFragment {
    private static final String TAG = "CourseSelectItemFragment";

    // 选择资源个数 Bundle Key
    public static final String KEY_EXTRA_MULTIPLE_CHOICE_COUNT = "KEY_EXTRA_MULTIPLE_CHOICE_COUNT";
    public static final String KEY_EXTRA_ONLINE_RELEVANCE = "KEY_EXTRA_ONLINE_RELEVANCE";
    // 需要显示的复述课件，听说课类型集合
    public static final String KEY_EXTRA_FILTER_COLLECTION = "KEY_EXTRA_FILTER_COLLECTION";
    // 真实的选择类型
    public static final String KEY_EXTRA_REAL_TASK_TYPE = "KEY_EXTRA_REAL_TASK_TYPE";

    private SuperListView listView;
    private FrameLayout mEmptyView;
    OnLoadStatusChangeListener onLoadStatusChangeListener;
    private ResourceSelectListener mListener;
    private ChapterVo mChapterVo ;
    private TopBar topBar;
    private PullToRefreshView pullToRefresh;
    private SectionDetailsVo sectionDetailsVo;
    private CourseResListAdapter courseResListAdapter;
    private int mTaskType;
    // 真实选择的类型
    private int mRealTaskType;
    public static final int KEY_WATCH_COURSE = 9;//看课件
    public static final int KEY_RELL_COURSE = 5;//复述课件
    public static final int KEY_TASK_ORDER = 8;//任务单
    public static final int KEY_TEXT_BOOK = 13;// 看课本
    public static final String RESULT_LIST = "result_list";
    // 可以选择的最大条目
    private int mMultipleChoiceCount;
    private boolean isOnlineRelevance;
    private ArrayList<Integer> mFilterArray;
    private ReadWeikeHelper mReadWeikeHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course_select, container, false);
        topBar = (TopBar)view.findViewById(R.id.select_top_bar);
        pullToRefresh = (PullToRefreshView)view.findViewById(R.id.select_pull_to_refresh);
        mEmptyView = (FrameLayout) view.findViewById(R.id.empty_layout);
        listView = (SuperListView) view.findViewById(R.id.course_select_listView);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = CourseSelectItemFragment.this.getActivity();
        Bundle arguments = getArguments();
        mChapterVo  = (ChapterVo) arguments.getSerializable("ChapterVo");
        mTaskType = arguments.getInt("tasktype");
        mRealTaskType = arguments.getInt(KEY_EXTRA_REAL_TASK_TYPE);
        mMultipleChoiceCount = arguments.getInt(KEY_EXTRA_MULTIPLE_CHOICE_COUNT);
        isOnlineRelevance = arguments.getBoolean(KEY_EXTRA_ONLINE_RELEVANCE);
        mFilterArray = arguments.getIntegerArrayList(KEY_EXTRA_FILTER_COLLECTION);
        if(EmptyUtil.isEmpty(mReadWeikeHelper)){
            mReadWeikeHelper = new ReadWeikeHelper(activity);
        }
        initData();
    }



    public void setOnLoadStatusChangeListener(OnLoadStatusChangeListener listener) {
        onLoadStatusChangeListener = listener;
    }

    public void updateData(){
        getData();
    }

    private void initData() {
        topBar.setBack(true);
        topBar.setTitle(mChapterVo.getName());
        topBar.setVisibility(View.GONE);

        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) pullToRefresh.getLayoutParams();
        layoutParams.topMargin = 0;
        pullToRefresh.setLayoutParams(layoutParams);

        topBar.findViewById(R.id.left_function1_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RefreshUtil.getInstance().clear();
                getFragmentManager().popBackStack();
            }
        });
        topBar.setRightFunctionText1(getString(R.string.confirm), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击确定
                if (courseResListAdapter != null) {
                    ArrayList<SectionResListVo> selectData = (ArrayList<SectionResListVo>)
                            courseResListAdapter.getSelectData();
                    if (selectData.size() <= 0) {
                        ToastUtil.showToast(activity,getString(R.string.str_select_tips));
                    } else {
                        for (SectionResListVo vo:selectData) {
                            vo.setChapterId(vo.getId());
                        }
                        // 学程馆选取资源使用的
                        EventBus.getDefault().post(new EventWrapper(selectData, EventConstant.COURSE_SELECT_RESOURCE_EVENT));
                        //数据回传
                        getActivity().setResult(Activity.RESULT_OK,
                                new Intent().putExtra(RESULT_LIST, selectData));
                        RefreshUtil.getInstance().clear();
                        getActivity().finish();
                    }
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

        courseResListAdapter = new CourseResListAdapter(activity, false);
        courseResListAdapter.setCourseSelect(true,mTaskType);
        courseResListAdapter.setMultipleChoiceCount(true,mMultipleChoiceCount);
        courseResListAdapter.setOnResourceSelectListener(mListener);
        listView.setAdapter(courseResListAdapter);

        listView.setOnItemClickListener(new SuperListView.OnItemClickListener() {
            @Override
            public void onItemClick(LinearLayout parent, View view, int position) {
                SectionResListVo sectionResListVo = (SectionResListVo) courseResListAdapter.getItem(position);
                if(sectionResListVo.getTaskType() == 1 || sectionResListVo.getTaskType() == 4){
                    // 看课件类型
                    if(EmptyUtil.isNotEmpty(mReadWeikeHelper)){
                        mReadWeikeHelper.readWeike(sectionResListVo);
                    }
                }else{
                    if (TaskSliderHelper.onTaskSliderListener != null &&
                            sectionResListVo != null) {
                        TaskSliderHelper.onTaskSliderListener.viewCourse(
                                activity, sectionResListVo.getResId(),
                                sectionResListVo.getResType(),
                                activity.getIntent().getStringExtra("schoolId"),
                                SourceFromType.LQ_COURSE);
                    }
                }
            }
        });

        pullToRefresh.setLastUpdated(new Date().toLocaleString());
        pullToRefresh.showRefresh();
        getData();
    }

    private int pageIndex = 0;
    private void getData(){
        pageIndex = 0;
        getData(AppConfig.PAGE_SIZE);
    }
    private void getData(int pageSize) {
        RequestVo requestVo = new RequestVo();

        requestVo.addParams("courseId", mChapterVo.getCourseId());
        requestVo.addParams("sectionId", mChapterVo.getId());
        // 1是老师
        requestVo.addParams("role", 1);
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.courseSectionDetail + requestVo.getParams());
        params.setConnectTimeout(10000);

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                pullToRefresh.onHeaderRefreshComplete();
                ResponseVo<SectionDetailsVo> result = JSON.parseObject(s,
                        new TypeReference<ResponseVo<SectionDetailsVo>>() {
                        });
                if (result.getCode() == 0) {
                    sectionDetailsVo = result.getData();
                    updateView();
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

    private void updateView() {
        courseResListAdapter.setData(null);
        if (sectionDetailsVo != null) {
            if (sectionDetailsVo.getTaskList() != null) {

                mEmptyView.setVisibility(View.VISIBLE);
                pullToRefresh.setVisibility(View.GONE);

                for (int i = 0; i < sectionDetailsVo.getTaskList().size(); i++) {
                    int taskType = sectionDetailsVo.getTaskList().get(i).getTaskType();

                        if (sectionDetailsVo.getTaskList().get(i).getData() != null ) {

                            if (mTaskType == KEY_WATCH_COURSE && taskType == 1) {
                                updateData(i);
                            } else if (mTaskType == KEY_RELL_COURSE && taskType == 2) {
                                updateData(i);
                            } else if (mTaskType == KEY_TASK_ORDER && taskType == 3) {
                                updateData(i);
                            }else if(mTaskType == KEY_TEXT_BOOK && taskType == 4){
                                updateData(i);
                            }

                        }
                }
                courseResListAdapter.notifyDataSetChanged();
            }
        }
    }

    private void updateData(int i) {
        List<SectionResListVo> voList = sectionDetailsVo.getTaskList().get(i).getData();
        RefreshUtil.getInstance().refresh(voList);
        if (voList.size() > 0) {
            // V5.11版本 取消标题的显示
            voList.get(0).setIsTitle(false);
        }
        for (SectionResListVo vo : voList) {
            vo.setTaskName(getTaskName(i));
            vo.setTaskType(sectionDetailsVo.getTaskList().get(i).getTaskType());
        }
        List<SectionResListVo> voListNew = new ArrayList<>();
        for (SectionResListVo sectionResListVo : voList) {
            int resType = sectionResListVo.getResType();
            if(mRealTaskType != CourseSelectItemFragment.KEY_WATCH_COURSE){
                if(resType > 10000){
                    resType -= 10000;
                }

                // 原本不是看课件类型，是读写单和听读课类型，需要过滤视频音频
                if(resType == 2 || resType == 3){
                    // 过滤视频 和 音频
                    continue;
                }

                if(mRealTaskType == CourseSelectItemFragment.KEY_TASK_ORDER){
                    // 如果显示的是读写单类型，那就需要过滤看课件中的听读课课件
                    if(resType == 18 || resType == 19){
                        // 过滤18,19
                        continue;
                    }
                }
            }

            if (!sectionResListVo.isIsShield()) {
                if(isOnlineRelevance && mTaskType == KEY_RELL_COURSE){
                    // int resType = sectionResListVo.getResType();
                    if(EmptyUtil.isNotEmpty(mFilterArray) && mFilterArray.contains(resType)){
                        voListNew.add(sectionResListVo);
                    }
                } else {
                    voListNew.add(sectionResListVo);
                }
            }
        }
        courseResListAdapter.addData(voListNew);
        listView.setAdapter(courseResListAdapter);

        if(EmptyUtil.isNotEmpty(voListNew)){
            // 有数据
            mEmptyView.setVisibility(View.GONE);
            pullToRefresh.setVisibility(View.VISIBLE);
        }else{
            // 没有数据
            mEmptyView.setVisibility(View.VISIBLE);
            pullToRefresh.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(EmptyUtil.isNotEmpty(mReadWeikeHelper)){
            mReadWeikeHelper.release();
        }
    }

    @NonNull
    private String getTaskName(int i) {
        String taskName = sectionDetailsVo.getTaskList().get(i).getTaskName();
        /*int taskType = sectionDetailsVo.getTaskList().get(i).getTaskType();
        if (taskType == 1) {//看课件
            taskName = getString(R.string.lq_watch_course);
        } else if (taskType == 2) {//复述课件
            taskName = getResources().getString(R.string.retell_course);
        }else if (taskType == 3) {//任务单
            taskName = getResources().getString(R.string.coursetask);
        }*/
        return taskName;
    }

    /**
     * 获取资源选择的Adapter
     * @return courseResListAdapter
     */
    public CourseResListAdapter getResourceAdapter(){
        return courseResListAdapter;
    }

    public void setOnResourceSelectListener(@NonNull ResourceSelectListener listener){
        this.mListener = listener;
    }
}
