package com.lqwawa.intleducation.module.discovery.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;

import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ui.MyBaseFragment;
import com.lqwawa.intleducation.base.utils.ToastUtil;
import com.lqwawa.intleducation.base.vo.ResponseVo;
import com.lqwawa.intleducation.base.widgets.SuperListView;
import com.lqwawa.intleducation.common.interfaces.OnLoadStatusChangeListener;
import com.lqwawa.intleducation.common.ui.CommentDialog;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.helper.LQCourseHelper;
import com.lqwawa.intleducation.module.discovery.adapter.CourseChapterAdapter;
import com.lqwawa.intleducation.module.discovery.adapter.CourseCommentAdapter;
import com.lqwawa.intleducation.module.discovery.adapter.CourseIntroduceAdapter;
import com.lqwawa.intleducation.module.discovery.tool.LoginHelper;
import com.lqwawa.intleducation.module.discovery.ui.classcourse.statistics.course.CourseStatisticsActivity;
import com.lqwawa.intleducation.module.discovery.ui.classcourse.statistics.course.CourseStatisticsParams;
import com.lqwawa.intleducation.module.discovery.ui.classcourse.statistics.learn.LearningStatisticsActivity;
import com.lqwawa.intleducation.module.discovery.ui.coursedetail.CourseDetailParams;
import com.lqwawa.intleducation.module.discovery.ui.lesson.detail.LessonSourceFragment;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.coursedetails.CourseDetailItemParams;
import com.lqwawa.intleducation.module.discovery.ui.navigator.CourseDetailsNavigator;
import com.lqwawa.intleducation.module.discovery.vo.ChapterVo;
import com.lqwawa.intleducation.module.discovery.vo.CommentVo;
import com.lqwawa.intleducation.module.discovery.vo.CourseDetailsVo;
import com.lqwawa.intleducation.module.discovery.vo.CourseIntroduceVo;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;
import com.lqwawa.intleducation.module.learn.tool.TaskSliderHelper;
import com.lqwawa.intleducation.module.learn.ui.MyCourseDetailsActivity;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by XChen on 2016/11/14.
 * email:man0fchina@foxmail.com
 */

public class CourseDetailsItemFragment extends MyBaseFragment implements View.OnClickListener {
    private static final String TAG = "CourseDetailsItemFragment";

    // 是否是在线课堂老师
    public static final String KEY_EXTRA_ONLINE_TEACHER = "KEY_EXTRA_ONLINE_TEACHER";

    // 定义三种通知类型,看课件,听说课,读写单
    public static final String LQWAWA_ACTION_CAN_COURSEWARE = "LQWAWA_ACTION_CAN_COURSEWARE";
    public static final String LQWAWA_ACTION_LISTEN_AND_WRITE = "LQWAWA_ACTION_LISTEN_AND_WRITE";
    public static final String LQWAWA_ACTION_READ_WRITE_SINGLE = "LQWAWA_ACTION_READ_WRITE_SINGLE";

    // 列表布局
    private SuperListView listView;
    // 没有评论显示的空布局
    private View mNoCommentTip;
    private LinearLayout mBottomLayout;
    // 学习统计,课程统计
    private Button mBtnStatisticalLearning, mBtnCourseStatistics;
    //播放列表
    private Button mBtnPlayList;
    private LinearLayout mLLPlayList;

    // 课程公告的集合以及Adapter
    private List<CourseIntroduceVo> mCourseIntroduceArray;
    private CourseIntroduceAdapter mIntroduceAdapter;

    // 课程大纲的集合以及Adapter
    private List<ChapterVo> mCourseChapterArray;
    private CourseChapterAdapter mCourseChapterAdapter;

    // 课程评论的集合以及Adapter
    private List<CommentVo> mCourseCommentArray;
    private CourseCommentAdapter mCourseCommentAdapter;

    private CourseDetailsVo mCourseDetailsVo;
    private CommentDialog commentDialog;
    OnLoadStatusChangeListener onLoadStatusChangeListener;

    private CourseVo flagCourseData;

    // 是不是我的课程详情页面 已经加入的课程
    private boolean isJoin;
    // 数据类型，页面类型
    private int mDataType;
    // 已加入课程大纲类型
    private boolean mNeedReadFlag;
    // 课程ID
    private String mCourseId;
    // 课程评价评分
    private int mStarLevel;
    // 是否是在线课堂的老师
    private boolean isOnlineTeacher;
    // 课程详情Tab参数
    private CourseDetailItemParams mDetailItemParams;
    // 分页数据
    private int pageIndex = 0;

    private CourseVo courseVo;

    private boolean mTeacherVisitor;

    private boolean isFromScan;

    private List<String> resIds;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course_details_item, container, false);
        listView = (SuperListView) view.findViewById(R.id.listView);
        mBottomLayout = (LinearLayout) view.findViewById(R.id.bottom_layout);
        mBtnStatisticalLearning = (Button) view.findViewById(R.id.btn_statistical_learning);
        mBtnCourseStatistics = (Button) view.findViewById(R.id.btn_course_statistics);
        mBtnPlayList = (Button) view.findViewById(R.id.btn_play_list);
        mLLPlayList = (LinearLayout) view.findViewById(R.id.ll_play_list);

        mBtnStatisticalLearning.setOnClickListener(this);
        mBtnCourseStatistics.setOnClickListener(this);
        mBtnPlayList.setOnClickListener(this);

        mNoCommentTip = view.findViewById(R.id.no_comment_tip);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = getActivity();

        Bundle arguments = getArguments();
        courseVo = (CourseVo) arguments.getSerializable(CourseVo.class.getSimpleName());
        isOnlineTeacher = getArguments().getBoolean(KEY_EXTRA_ONLINE_TEACHER, false);
        isFromScan = getArguments().getBoolean("isFromScan", false);
        resIds = getArguments().getStringArrayList("resIds");

        mLLPlayList.setVisibility(View.VISIBLE);
        if (EmptyUtil.isNotEmpty(resIds)){
//            List<String> list = new ArrayList<String>();
//            list.add("712577-19");
//            list.add("715481-19");
            TaskSliderHelper.onPlayListListener.setResIds(resIds);
            TaskSliderHelper.onPlayListListener.setActivity(activity);
            if (TaskSliderHelper.onPlayListListener.getPlayResourceSize()>0){
                TaskSliderHelper.onPlayListListener.startPlay();
            }
        }

        if (arguments.containsKey(FRAGMENT_BUNDLE_OBJECT)) {
            mDetailItemParams = (CourseDetailItemParams) arguments.getSerializable(FRAGMENT_BUNDLE_OBJECT);
        }

        isJoin = mDetailItemParams.isJoin();
        mDataType = mDetailItemParams.getDataType();
        mNeedReadFlag = isJoin && mDataType == CourseDetailItemParams.COURSE_DETAIL_ITEM_STUDY_PLAN;
        mCourseId = mDetailItemParams.getCourseId();

        if (mDetailItemParams.getDataType() == CourseDetailItemParams.COURSE_DETAIL_ITEM_STUDY_PLAN) {
            CourseVo vo = (CourseVo) getArguments().getSerializable("CourseVo");
            if (vo != null) {
                if (vo.getChapList() != null) {
                    if (vo.getChapList().size() > 0) {
                        this.flagCourseData = vo;
                    }
                }
            }
        }

        // 是否老师看学生
        mTeacherVisitor = arguments.getBoolean("teacherVisitor");
        registerBroadcastReceiver();
        initData();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden && mDataType == CourseDetailItemParams.COURSE_DETAIL_ITEM_COURSE_COMMENT) {
            // 课程评价显示
            // 课程评价片段显示
            Activity activity = getActivity();
            if (activity instanceof CourseDetailsNavigator) {
                // 回调接口,显示课程评价,隐藏按钮
                CourseDetailsNavigator navigator = (CourseDetailsNavigator) activity;
                navigator.courseCommentVisible();
            }
        } else if (hidden && mDataType == CourseDetailItemParams.COURSE_DETAIL_ITEM_COURSE_COMMENT) {
            // 课程评价隐藏
            Activity activity = getActivity();
            if (activity instanceof CourseDetailsNavigator) {
                // 回调接口,显示课程评价,隐藏按钮
                CourseDetailsNavigator navigator = (CourseDetailsNavigator) activity;
                navigator.otherFragmentVisible();
            }
        }
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.btn_statistical_learning) {
            // 学习统计
            // UIUtil.showToastSafe("学习统计");
            CourseDetailParams mCourseDetailParams = mDetailItemParams.getCourseParams();
            String classId = mCourseDetailParams.getClassId();
            LearningStatisticsActivity.show(getActivity(), UIUtil.getString(R.string.title_learning_statistics), classId, mCourseId, 0, mCourseDetailParams);
        } else if (viewId == R.id.btn_course_statistics) {
            // 课程统计
            // UIUtil.showToastSafe("课程统计");
            CourseDetailParams mCourseDetailParams = mDetailItemParams.getCourseParams();
            String classId = mCourseDetailParams.getClassId();
            CourseStatisticsParams params = new CourseStatisticsParams(classId, mCourseId, courseVo.getName());
            params.setCourseParams(mCourseDetailParams);
            CourseStatisticsActivity.show(getActivity(), params);
        } else if (viewId == R.id.btn_play_list) {
            //播放列表
            ToastUtil.showToast(getActivity(), "播放列表");
            if (EmptyUtil.isNotEmpty(TaskSliderHelper.onPlayListListener)) {
                TaskSliderHelper.onPlayListListener.showPlayListDialog(getActivity());
            }
        }
    }

    public void comment(CommentDialog.CommentData data) {

        if (UserHelper.isLogin()) {
            int curScort = -1;
            // LQ英语所有人都可以无限制评分
            if (mCourseDetailsVo.getStarLevel() > 0) {//已经评过分了
                curScort = mCourseDetailsVo.getStarLevel();
            }
            int commentType = CommentDialog.TYPE_COMMENT_LOW_PERMISSION;
            if (getActivity() instanceof CourseDetailsActivity || mDetailItemParams.isParentRole()) {
                // 如果是家长身份,也是低优先级
                commentType = CommentDialog.TYPE_COMMENT_LOW_PERMISSION;
            } else if (getActivity() instanceof MyCourseDetailsActivity) {
                commentType = CommentDialog.TYPE_COMMENT_HIGH_PERMISSION;
            }
            commentDialog = new CommentDialog(activity, curScort, commentType, mDetailItemParams.isParentRole(), data, new CommentDialog.CommitCallBack() {
                @Override
                public void dismiss(CommentDialog.CommentData module) {
                    if (mDataType == CourseDetailItemParams.COURSE_DETAIL_ITEM_COURSE_COMMENT) {
                        // 课程评价片段显示
                        Activity activity = getActivity();
                        if (activity instanceof CourseDetailsNavigator) {
                            // 回调接口,显示课程评价,隐藏按钮
                            CourseDetailsNavigator navigator = (CourseDetailsNavigator) activity;
                            navigator.setContent(module);
                        }
                    }
                }

                @Override
                public void triggerSend(CommentDialog.CommentData module) {
                    if (commentDialog.isShowing()) {
                        commentDialog.dismiss();
                        commitComment(module);
                    }
                }
            });

            if (commentDialog != null && !commentDialog.isShowing()) {
                Window window = commentDialog.getWindow();
                commentDialog.show();
                window.setGravity(Gravity.BOTTOM);
            }
        } else {
            LoginHelper.enterLogin(activity);
        }
    }

    public void setOnLoadStatusChangeListener(OnLoadStatusChangeListener listener) {
        onLoadStatusChangeListener = listener;
    }

    public void updateData() {
        getData(false);
    }

    public void getMore() {
        getData(true);
    }

    /**
     * 提交评论
     *
     * @param data 评论内容和评分
     */
    public void commitComment(CommentDialog.CommentData data) {
        if (null == data || TextUtils.isEmpty(data.getContent())) {
            UIUtil.showToastSafe(R.string.enter_evaluation_content_please);
            return;
        }

        String content = data.getContent().trim();
        int scort = data.getScort();
        LQCourseHelper.requestCommitCourseComment(mCourseId, 0,
                isJoin, null, content, scort, new DataSource.Callback<ResponseVo>() {
                    @Override
                    public void onDataNotAvailable(int strRes) {
                        UIUtil.showToastSafe(strRes);
                    }

                    @Override
                    public void onDataLoaded(ResponseVo responseVo) {
                        if (responseVo.isSucceed()) {
                            // 刷新UI
                            getData(false);

                            if (onLoadStatusChangeListener != null) {
                                onLoadStatusChangeListener.onCommitComment();
                            }

                            if (getActivity() instanceof CourseDetailsNavigator) {
                                CourseDetailsNavigator navigator = (CourseDetailsNavigator) getActivity();
                                navigator.commitComment();
                            }

                            // 清除评论区域的内容
                            if (getActivity() instanceof CourseDetailsNavigator) {
                                CourseDetailsNavigator navigator = (CourseDetailsNavigator) getActivity();
                                navigator.clearContent();
                            }

                            UIUtil.showToastSafe(UIUtil.getString(R.string.commit_comment) +
                                    UIUtil.getString(R.string.success) + "!");
                        } else {
                            UIUtil.showToastSafe(UIUtil.getString(R.string.commit_comment) +
                                    UIUtil.getString(R.string.failed) + "!");
                        }
                    }
                });
    }

    private void initData() {
        if (mDataType == CourseDetailItemParams.COURSE_DETAIL_ITEM_INTRODUCTION) {
            mIntroduceAdapter = new CourseIntroduceAdapter(activity);
            mCourseIntroduceArray = new ArrayList();
            listView.setAdapter(mIntroduceAdapter);
        } else if (mDataType == CourseDetailItemParams.COURSE_DETAIL_ITEM_STUDY_PLAN) {
            // 课程大纲内容发生改变回调监听
            mCourseChapterAdapter = new CourseChapterAdapter(activity, mCourseId, mNeedReadFlag, isOnlineTeacher, () -> getData(false));
            // 已经加入的学程
            mCourseChapterAdapter.setJoinCourse(isJoin);
            mCourseChapterAdapter.setIsFromScan(isFromScan);
            mCourseChapterAdapter.setTeacherVisitor(mTeacherVisitor);
            mCourseChapterArray = new ArrayList();
            listView.setAdapter(mCourseChapterAdapter);
        } else if (mDataType == CourseDetailItemParams.COURSE_DETAIL_ITEM_COURSE_COMMENT) {
            mCourseCommentAdapter = new CourseCommentAdapter(activity, () -> getData(false));
            mCourseCommentArray = new ArrayList();
            listView.setAdapter(mCourseCommentAdapter);
        }

        if (mDataType == CourseDetailItemParams.COURSE_DETAIL_ITEM_STUDY_PLAN || mDataType == CourseDetailItemParams.COURSE_DETAIL_ITEM_COURSE_COMMENT) {
            // 课程大纲和课程评价
            if (getActivity() instanceof CourseDetailsNavigator) {
                CourseDetailsNavigator navigator = (CourseDetailsNavigator) getActivity();
                Observable observable = navigator.getCourseObservable();
                if (null != observable) {
                    // 有可能出现问题,如果Fragment创建比CourseDetail请求课程信息慢的话
                    // 这种bug几乎不可能发生
                    observable.addObserver(new Observer() {
                        @Override
                        public void update(Observable o, Object arg) {
                            if (arg instanceof CourseVo) {
                                CourseVo courseVo = (CourseVo) arg;
                                if (EmptyUtil.isNotEmpty(mCourseCommentAdapter)) {
                                    mCourseCommentAdapter.setData(courseVo);
                                }

                                if (EmptyUtil.isNotEmpty(mCourseChapterAdapter)) {
                                    mCourseChapterAdapter.setCourseVo(courseVo);
                                    mCourseChapterAdapter.notifyDataSetChanged();
                                    if (mDetailItemParams != null) {
                                        mCourseChapterAdapter.setCourseDetailParams
                                                (mDetailItemParams.getCourseParams());
                                    }
                                }
                            }
                        }
                    });
                }
            }
        }

        getData(false);
    }

    /**
     * 请求课程详情数据
     */
    private void requestCourseDetail(boolean isMoreData) {
        String token = UserHelper.getUserId();
        if (mDetailItemParams.isParentRole()) {
            // 家长身份
            token = mDetailItemParams.getMemberId();
        }
        String courseId = mDetailItemParams.getCourseId();

        if (!isMoreData) {
            pageIndex = 0;
        } else {
            pageIndex++;
        }
        LQCourseHelper.requestCourseDetailByCourseId(
                token,
                courseId, null,
                mDetailItemParams.getDataType(),
                pageIndex, AppConfig.PAGE_SIZE,
                new Callback());
    }

    /**
     * 获取已加入章节列表
     */
    private void requestChapterList() {
        String token = UserHelper.getUserId();
        if (mDetailItemParams.isParentRole() || mTeacherVisitor) {
            // 家长身份
            token = mDetailItemParams.getMemberId();
        }
        String courseId = mDetailItemParams.getCourseId();
        String schoolIds = null;
        if (UserHelper.isLogin()
                && UserHelper.isTeacher(UserHelper.getUserInfo().getRoles())) {
            //仅在登陆用户是教师身份的情况下才传SchoolIds 以便server用于判断是否显示联合备课内容
            schoolIds = UserHelper.getUserInfo().getSchoolIds();
        }

        CourseDetailParams courseParams = mDetailItemParams.getCourseParams();
        if (courseParams.isClassCourseEnter() &&
                courseParams.isClassTeacher()
                && !mTeacherVisitor) {
            mBottomLayout.setVisibility(View.VISIBLE);
            LQCourseHelper.requestChapterByCourseId(courseParams.getClassId(), courseId, new Callback());
        } else {
            LQCourseHelper.requestChapterByCourseId(token, courseId, schoolIds, new Callback());
        }
    }

    /**
     * @author mrmedici
     * @desc 获取课程详情以及课程大纲的统一回调处理
     */
    private class Callback implements DataSource.Callback<CourseDetailsVo> {
        @Override
        public void onDataLoaded(CourseDetailsVo courseDetailsVo) {
            CourseDetailsItemFragment.this.mCourseDetailsVo = courseDetailsVo;
            if (onLoadStatusChangeListener != null) {
                onLoadStatusChangeListener.onLoadSuccess();
            }

            // 获取课程评分
            mStarLevel = courseDetailsVo.getStarLevel();
            if (courseDetailsVo.getCode() == 0) {
                if (mDataType == CourseDetailItemParams.COURSE_DETAIL_ITEM_INTRODUCTION) {
                    //课程介绍
                    List<CourseVo> voList = courseDetailsVo.getCourse();
                    if (EmptyUtil.isNotEmpty(voList)) {
                        CourseVo courseVo = voList.get(0);
                        mCourseIntroduceArray = CourseIntroduceVo.buildData(isJoin, courseVo);
                        mIntroduceAdapter.setData(mCourseIntroduceArray);
                        mIntroduceAdapter.notifyDataSetChanged();
                    } else {
                        mIntroduceAdapter.setData(null);
                    }
                } else if (mDataType == CourseDetailItemParams.COURSE_DETAIL_ITEM_STUDY_PLAN) {
                    //学习计划
                    if (mNeedReadFlag) {
                        mCourseChapterArray = courseDetailsVo.getChapters();
                        if (courseDetailsVo.getCexamSize() > 0 && mCourseChapterArray != null) {
                            ChapterVo lessonVo = new ChapterVo();
                            lessonVo.setIsChildren(true);
                            lessonVo.setCourseId(mCourseId);
                            lessonVo.setType(CourseChapterAdapter.TYPE_CEXAM);
                            lessonVo.setIsHide(false);
                            if (flagCourseData != null) {
                                int flagValue = 0;
                                flagValue |= (flagCourseData.getExamList() != null
                                        && flagCourseData.getExamList().size() > 0) ? 0x02 : 0;
                                lessonVo.setFlag(flagValue);
                            }
                            mCourseChapterArray.add(lessonVo);
                        }
                    } else {
                        mCourseChapterArray = courseDetailsVo.getChapterList();
                    }

                    if (EmptyUtil.isNotEmpty(mCourseChapterArray)) {
                        for (int i = 0; i < mCourseChapterArray.size(); i++) {
                            mCourseChapterArray.get(i).setCourseId(mCourseId);
                            mCourseChapterArray.get(i).setChapterName(courseDetailsVo.getChapterName());
                            mCourseChapterArray.get(i).setSectionName(courseDetailsVo.getSectionName());
                            ChapterVo chapterVo = mCourseChapterArray.get(i);
                            CourseDetailParams courseParams = mDetailItemParams.getCourseParams();
                            // 如果是班级学程入口,并且是老师身份
                            if (courseParams.isClassCourseEnter() &&
                                    UserHelper.checkCourseAuthor(courseVo, isOnlineTeacher)) {
                                chapterVo.setBuyed(true);
                            }
                            if (!chapterVo.getIsChildren() && EmptyUtil.isNotEmpty(chapterVo.getChildren())) {
                                // 如果章节下面有小节内容
                                for (ChapterVo tempVo : chapterVo.getChildren()) {
                                    // 将单元是否购买，塞进小节是否购买当中
                                    tempVo.setBuyed(chapterVo.isBuyed());
                                    // 将id塞进小节的parentId字段中
                                    tempVo.setParentId(chapterVo.getId());
                                }
                            }
                        }

                    }
                    if (flagCourseData != null) {
                        if (flagCourseData.getChapList() != null && flagCourseData.getChapList().size() > 0) {
                            for (int i = 0; i < mCourseChapterArray.size(); i++) {
                                for (int j = 0; j < flagCourseData.getChapList().size(); j++) {
                                    if (mCourseChapterArray.get(i).getId() != null && mCourseChapterArray.get(i).getId()
                                            .equals(flagCourseData.getChapList().get(j).getId())) {
                                        if (flagCourseData.getChapList().get(j).getSectionList() != null
                                                && flagCourseData.getChapList().get(j).getSectionList().size() > 0) {//在小节下
                                            for (int p = 0; p < mCourseChapterArray.get(i).getChildren().size(); p++) {
                                                for (int k = 0; k < flagCourseData.getChapList().get(j).getSectionList().size(); k++) {
                                                    if (mCourseChapterArray.get(i).getChildren().get(p).getId() != null &&
                                                            mCourseChapterArray.get(i).getChildren().get(p).getId()
                                                                    .equals(flagCourseData.getChapList().get(j).getSectionList().get(k).getId())) {
                                                        mCourseChapterArray.get(i).getChildren().get(p).setFlag(1);
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
                                            mCourseChapterArray.get(i).setFlag(flagValue);
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    mCourseChapterAdapter.setData(mCourseChapterArray);
                    mCourseChapterAdapter.buyAll(courseDetailsVo.isBuyAll());
                    CourseDetailParams courseParams = mDetailItemParams.getCourseParams();
                    // 如果是班级学程入口,并且是老师身份
                    if (courseParams.isClassCourseEnter() &&
                            UserHelper.checkCourseAuthor(courseVo, isOnlineTeacher)) {
                        mCourseChapterAdapter.buyAll(true);
                    }
                    mCourseChapterAdapter.notifyDataSetChanged();
                } else if (mDataType == CourseDetailItemParams.COURSE_DETAIL_ITEM_COURSE_COMMENT) {//课程评论
                    mCourseCommentArray = courseDetailsVo.getCommentList();
                    if (onLoadStatusChangeListener != null) {
                        onLoadStatusChangeListener.onLoadFinish(mCourseCommentArray != null
                                && mCourseCommentArray.size() >= AppConfig.PAGE_SIZE);
                    }


                    if (pageIndex == 0) {
                        if (mCourseCommentArray != null && mCourseCommentArray.size() == 0) {
                            mNoCommentTip.setVisibility(View.VISIBLE);
                        } else {
                            mNoCommentTip.setVisibility(View.GONE);
                        }

                        if (mCourseCommentArray != null && mCourseCommentArray.size() > 0) {
                            mCourseCommentAdapter.setData(mStarLevel, mCourseCommentArray);
                            mCourseCommentAdapter.notifyDataSetChanged();
                        }
                    } else {
                        if (mCourseCommentArray != null && mCourseCommentArray.size() > 0) {
                            mCourseCommentAdapter.addData(mCourseCommentArray);
                            mCourseCommentAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        }

        @Override
        public void onDataNotAvailable(int strRes) {
            UIUtil.showToastSafe(strRes);
        }
    }

    private void getData(boolean isMoreData) {
        if (mDetailItemParams.isJoin() &&
                mDetailItemParams.getDataType() == CourseDetailItemParams.COURSE_DETAIL_ITEM_STUDY_PLAN) {
            // 单独拉课程大纲列表
            requestChapterList();
        } else {
            requestCourseDetail(isMoreData);
        }
    }

    /**
     * @author mrmedici
     * @desc 广播定义，看课件，听说课，读写单，以及参加课程，提交试卷和任务单成功的事件刷新广播
     */
    protected BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(AppConfig.ServerUrl.userExamSave)// 提交试卷成功
                    || action.equals(AppConfig.ServerUrl.userTaskSave)// 提交任务单成功
                    || action.equals(AppConfig.ServerUrl.joinInCourse)// 课程参加成功
                    || action.equals(LQWAWA_ACTION_CAN_COURSEWARE) // 看课件
                    || action.equals(LQWAWA_ACTION_LISTEN_AND_WRITE) // 听说课
                    || action.equals(LQWAWA_ACTION_READ_WRITE_SINGLE)// 读写单
                    || action.equalsIgnoreCase(LessonSourceFragment.LESSON_RESOURCE_CHOICE_PUBLISH_ACTION)) {
                // 作业库发布更新
                getData(false);
            }
        }
    };

    /**
     * 注册广播事件
     */
    protected void registerBroadcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        // 要接收的类型
        // 提交了考试
        myIntentFilter.addAction(AppConfig.ServerUrl.userExamSave);
        // 提交任务单成功
        myIntentFilter.addAction(AppConfig.ServerUrl.userTaskSave);
        // 课程参加成功
        myIntentFilter.addAction(AppConfig.ServerUrl.joinInCourse);
        // 看课件
        myIntentFilter.addAction(LQWAWA_ACTION_CAN_COURSEWARE);
        // 听说课
        myIntentFilter.addAction(LQWAWA_ACTION_LISTEN_AND_WRITE);
        // 读写单
        myIntentFilter.addAction(LQWAWA_ACTION_READ_WRITE_SINGLE);
        // 作业库发布更新
        myIntentFilter.addAction(LessonSourceFragment.LESSON_RESOURCE_CHOICE_PUBLISH_ACTION);
        // 注册广播
        activity.registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        activity.unregisterReceiver(mBroadcastReceiver);
        if (EmptyUtil.isNotEmpty(TaskSliderHelper.onPlayListListener)) {
            TaskSliderHelper.onPlayListListener.releasePlayResource();

        }
    }
}
