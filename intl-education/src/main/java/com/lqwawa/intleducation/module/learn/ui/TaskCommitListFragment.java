package com.lqwawa.intleducation.module.learn.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.lqwawa.client.pojo.SourceFromType;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ui.MyBaseFragment;
import com.lqwawa.intleducation.base.utils.LogUtil;
import com.lqwawa.intleducation.base.vo.RequestVo;
import com.lqwawa.intleducation.base.widgets.PullRefreshView.PullToRefreshView;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.StringUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.course.ClassCourseEntity;
import com.lqwawa.intleducation.factory.data.entity.response.LQResourceDetailVo;
import com.lqwawa.intleducation.factory.helper.LearningTaskHelper;
import com.lqwawa.intleducation.factory.helper.LessonHelper;
import com.lqwawa.intleducation.module.discovery.ui.coursedetail.CourseDetailParams;
import com.lqwawa.intleducation.module.discovery.ui.coursedetail.CourseDetailType;
import com.lqwawa.intleducation.module.discovery.ui.task.list.TaskCommitParams;
import com.lqwawa.intleducation.module.learn.adapter.CommittedTasksAdapter;
import com.lqwawa.intleducation.module.learn.vo.LqTaskCommitListVo;
import com.lqwawa.intleducation.module.learn.vo.LqTaskCommitVo;
import com.lqwawa.intleducation.module.learn.vo.SectionResListVo;
import com.lqwawa.intleducation.module.learn.vo.SectionTaskCommitListVo;
import com.lqwawa.intleducation.module.learn.vo.SectionTaskDetailsVo;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import org.xutils.http.RequestParams;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by XChen on 2017/7/7.
 * email:man0fchina@foxmail.com
 */

public class TaskCommitListFragment extends MyBaseFragment implements View.OnClickListener{
    private static String TAG = TaskCommitListFragment.class.getSimpleName();
    // 角色信息
    public static final String KEY_EXTRA_ROLE_TYPE = "KEY_EXTRA_ROLE_TYPE";
    // 是否是游离的身份
    public static final String KEY_ROLE_FREE_USER = "KEY_ROLE_FREE_USER";

    private RelativeLayout loadFailedLayout;
    private Button btnReload;
    // 做读写单 复述课件 按钮
    private Button mBtnDone;
    // 语音评测按钮
    private Button mBtnSpeechEvaluation;
    // 成绩统计按钮
    private Button mBtnStatisticalScores;
    private PullToRefreshView pullToRefreshView;
    private ListView listView;
    private CommittedTasksAdapter committedTasksAdapter;
    private SectionResListVo sectionResListVo;
    // private SectionTaskDetailsVo sectionTaskDetailsVo;
    private DoWorkListener doWorkListener = null;
    protected boolean isLive = false;
    protected boolean isHost = false;

    private View mRootView;

    // 角色信息
    private int mRoleType;
    private String examId;
    // 是否试听
    private boolean isAudition;
    private int mCommitType;
    private TaskCommitParams mCommitParams;

    // 当前的删除状态
    private boolean isHoldTag;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        mRoleType = arguments.getInt(KEY_EXTRA_ROLE_TYPE);
        examId = arguments.getString("examId");
        isAudition = arguments.getBoolean(KEY_ROLE_FREE_USER,false);
        mCommitParams = (TaskCommitParams) arguments.getSerializable(FRAGMENT_BUNDLE_OBJECT);

        mRoleType = mCommitParams.getOriginalRole();
        isAudition = mCommitParams.isAudition();
        mCommitType = mCommitParams.getCommitType();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = mRootView = inflater.inflate(R.layout.fragment_task_commit_list, container, false);
        isLive = activity.getIntent().getBooleanExtra("isLive", false);
        isHost = activity.getIntent().getBooleanExtra("isHost", false);
        loadFailedLayout = (RelativeLayout) view.findViewById(R.id.load_failed_layout);
        btnReload = (Button) view.findViewById(R.id.reload_bt);
        mBtnDone = (Button) view.findViewById(R.id.done_bt);
        mBtnSpeechEvaluation = (Button) view.findViewById(R.id.btn_speech_evaluation);
        mBtnStatisticalScores = (Button) view.findViewById(R.id.btn_statistical_scores);
        pullToRefreshView = (PullToRefreshView) view.findViewById(R.id.pull_to_refresh);
        listView = (ListView) view.findViewById(R.id.listView);
        sectionResListVo = (SectionResListVo)getArguments().getSerializable("SectionResListVo");
        if (sectionResListVo != null) {
            fillBtnResource(sectionResListVo);
        }
        /*if(!TextUtils.equals(UserHelper.getUserId(),
                activity.getIntent().getStringExtra("memberId")) || isHost){
            view.findViewById(R.id.done_bt).setVisibility(View.GONE);
        }else {

        }*/

        mBtnDone.setOnClickListener(this);
        // 语音评测和成绩统计的点击事件
        mBtnSpeechEvaluation.setOnClickListener(this);
        mBtnStatisticalScores.setOnClickListener(this);
        return view;
    }

    /**
     * 填充按钮资源
     */
    public void fillBtnResource(@NonNull final SectionResListVo sectionResListVo){
        if (sectionResListVo.getTaskType() == 2) {
            mBtnDone.setText(getResources().getString(R.string.retell_task));
        } else if (sectionResListVo.getTaskType() == 3) {
            mBtnDone.setText(getResources().getString(R.string.do_task));
        }

        // 班级学程入口，才有成绩统计
        CourseDetailParams courseParams = mCommitParams.getCourseParams();
        if(courseParams.isClassCourseEnter() &&
                !mCommitParams.isTeacherVisitor() &&
                (sectionResListVo.getTaskType() == 2 || sectionResListVo.getTaskType() == 3)){
            // 听读课 和 读写单都有成绩统计
            mBtnStatisticalScores.setVisibility(View.VISIBLE);
        }else{
            mBtnStatisticalScores.setVisibility(View.GONE);
        }

        // 只有学生才显示读写单和复述课件
        if(mRoleType == UserHelper.MoocRoleType.STUDENT || (isAudition && mCommitParams.isTeacherVisitor())){
            // 试听身份可以查看到按钮
            mBtnDone.setVisibility(View.VISIBLE);
            // 只有学生，支持语音评测的听读课才显示语音评测
            if(sectionResListVo.getTaskType() == 2 &&
                    SectionResListVo.EXTRAS_AUTO_READ_OVER.equals(sectionResListVo.getResProperties())){
                // 支持语音评测的听读课
                mBtnSpeechEvaluation.setVisibility(View.VISIBLE);
            }else{
                mBtnSpeechEvaluation.setVisibility(View.GONE);
            }
        }else{
            mBtnDone.setVisibility(View.GONE);
            mBtnSpeechEvaluation.setVisibility(View.GONE);
        }
    }

    public void setDoWorkListener(DoWorkListener listener){
        this.doWorkListener = listener;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
    }

    public void initViews(){
        pullToRefreshView.setLoadMoreEnable(false);
        pullToRefreshView.setOnHeaderRefreshListener(new PullToRefreshView.OnHeaderRefreshListener() {
            @Override
            public void onHeaderRefresh(PullToRefreshView view) {
                getAnswerData();
            }
        });
        pullToRefreshView.setLastUpdated(new Date().toLocaleString());
        btnReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.reload_bt) {
                    pullToRefreshView.showRefresh();
                    getAnswerData();
                }
            }
        });

        committedTasksAdapter = new CommittedTasksAdapter(getActivity(),null,mCommitParams,sectionResListVo);
        listView.setAdapter(committedTasksAdapter);

        committedTasksAdapter.setCommittedNavigator(new CommittedTasksAdapter.CommittedNavigator() {
            @Override
            public void onDeleteTask(@NonNull LqTaskCommitVo vo) {
                // 删除某一个Item
                updateItemDeleteState(vo,false);
                // 进行删除操作
                deleteCommittedTask(vo);
            }

            @Override
            public void onRefreshState(boolean state) {
                updateItemDeleteState(state);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // 切换状态
                updateItemDeleteState(false);
                LqTaskCommitVo item = (LqTaskCommitVo) committedTasksAdapter.getItem(i);
                if(!EmptyUtil.isEmpty(doWorkListener)){
                    doWorkListener.onItemClick(item,false,getSourceType());
                }
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // 长按删除功能
                int handleRole = mCommitParams.getHandleRole();
                String curMemberId = mCommitParams.getMemberId();
                if(handleRole == UserHelper.MoocRoleType.EDITOR ||
                        handleRole == UserHelper.MoocRoleType.TEACHER ||
                        TextUtils.equals(sectionResListVo.getCreateId(),curMemberId)){
                    // 学习任务的创建者
                    LqTaskCommitVo item = (LqTaskCommitVo) committedTasksAdapter.getItem(position);
                    updateItemDeleteState(item,!item.isDeleteTag());
                    return true;
                }
                return false;
            }
        });

        // 先获取到答题卡的信息
        getAnswerData();
    }

    private LQResourceDetailVo mAnswerData;

    /**
     * 获取答题卡信息
     */
    private void getAnswerData(){
        if(sectionResListVo.isAutoMark()){
            // 自动批阅类型
            final String resourceId = sectionResListVo.getResId() + "-" + sectionResListVo.getResType();

            LearningTaskHelper.requestResourceDetailById(resourceId, true, new DataSource.Callback<LQResourceDetailVo>() {
                @Override
                public void onDataNotAvailable(int strRes) {
                    UIUtil.showToastSafe(strRes);
                }

                @Override
                public void onDataLoaded(LQResourceDetailVo vo) {
                    // 可能没有答题卡信息的回调
                    mAnswerData = vo;
                    updateData();
                }
            });
        }else{
            updateData();
        }
    }

    /**
     * 删除学习任务的提交
     */
    private void deleteCommittedTask(@NonNull LqTaskCommitVo vo){
        LearningTaskHelper.requestDeleteTask(vo.getId(), new DataSource.Callback<Boolean>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                UIUtil.showToastSafe(strRes);
            }

            @Override
            public void onDataLoaded(Boolean aBoolean) {
                if(true){
                    // 刷新UI
                    getAnswerData();
                    // 删除成功
                    UIUtil.showToastSafe(R.string.tip_delete_succeed);
                }
            }
        });
    }

    private LqTaskCommitListVo mLqTaskCommitListVo;

    public void updateData() {
        loadFailedLayout.setVisibility(View.GONE);
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("token", activity.getIntent().getStringExtra("memberId"));
        requestVo.addParams("cwareId", sectionResListVo.getId());
        requestVo.addParams("type", isHost ? 1 : 0);
        LogUtil.d(TAG, requestVo.getParams());
        RequestParams params =
                new RequestParams((isLive ? AppConfig.ServerUrl.GetLiveResCommitList
                        : AppConfig.ServerUrl.cwareCommitList) + requestVo.getParams());
        params.setConnectTimeout(10000);
        // 学生穿StudentId,老师,主编,小编不传,家长穿memberId;
        String studentId = getStudentId();
        CourseDetailParams courseParams = mCommitParams.getCourseParams();

        // Mooc大厅的入口，拉取列表，不需要传 机构Id和ClassId,因为要拉所有的已经提交的学习任务
        // 但是提交是需要的
        String schoolId = courseParams.getSchoolId();
        String classId = courseParams.getClassId();

        if(courseParams.getCourseEnterType() == CourseDetailType.COURSE_DETAIL_MOOC_ENTER){
            schoolId = null;
            classId = null;
        }else if(courseParams.getCourseEnterType() == CourseDetailType.COURSE_DETAIL_SCHOOL_ENTER){
            // 学程馆学习任务入口
            // 课程发生了绑定
            // 如果绑定的机构Id等于学程馆的Id 提交和列表都是用学程馆的机构Id， 只有提交才传ClassId
            // 如果绑定的机构Id不等于学程馆的Id 列表用学程馆的Id
            if(courseParams.isBindClass()){
                if(TextUtils.equals(courseParams.getSchoolId(),courseParams.getBindSchoolId())){
                    // 学程馆Id和绑定的Id,相等
                    schoolId = courseParams.getBindSchoolId();
                    classId = null;
                }
            }else{
                schoolId = courseParams.getSchoolId();
                classId = null;
            }
        }

        LessonHelper.getNewCommittedTaskByTaskId(sectionResListVo.getTaskId(),
                studentId,
                classId,
                schoolId,null,mCommitType,
                new DataSource.Callback<LqTaskCommitListVo>() {
                    @Override
                    public void onDataNotAvailable(int strRes) {
                        UIUtil.showToastSafe(strRes);
                        pullToRefreshView.onHeaderRefreshComplete();
                    }

                    @Override
                    public void onDataLoaded(LqTaskCommitListVo lqTaskCommitListVo) {
                        pullToRefreshView.onHeaderRefreshComplete();
                        mLqTaskCommitListVo = lqTaskCommitListVo;
                        filterAutoMark(mLqTaskCommitListVo.getListCommitTaskOnline());
                        committedTasksAdapter.setData(getCommitTaskList(mLqTaskCommitListVo.getListCommitTaskOnline()));
                        committedTasksAdapter.setDoWorkListener(doWorkListener);
                        committedTasksAdapter.setAnswerData(mAnswerData);
                        committedTasksAdapter.notifyDataSetChanged();
                        updateViews();
                    }
                });
        /*x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                pullToRefreshView.onHeaderRefreshComplete();
                SectionTaskDetailsVo result = JSON.parseObject(s,
                        new TypeReference<SectionTaskDetailsVo>() {
                        });
                if (result != null && result.getCode() == 0) {
                    sectionTaskDetailsVo = result;
                    if (sectionTaskDetailsVo.getData() != null){
                        sectionTaskDetailsVo = sectionTaskDetailsVo.getData();
                    }
                    updateViews();
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                loadFailedLayout.setVisibility(View.VISIBLE);
                pullToRefreshView.onFooterRefreshComplete();
            }

            @Override
            public void onFinished() {
            }
        });
    }*/
    }

    /**
     * 学生穿StudentId,老师,主编,小编不传,家长穿memberId;
     * @return
     */
    private String getStudentId(){
        String memberId = null;
        if(mRoleType == UserHelper.MoocRoleType.TEACHER ||
                mRoleType == UserHelper.MoocRoleType.EDITOR ||
                isAudition){
            if(mCommitParams.isTeacherVisitor()){
                memberId = activity.getIntent().getStringExtra("memberId");
            }else{
                // 如果是主编和小编,或者是试听,就不传
                memberId = "";
            }
        }else if(mRoleType == UserHelper.MoocRoleType.STUDENT){
            // 如果是学生,就传自己的Id
            memberId = UserHelper.getUserId();
        }else{
            // 剩下的就是家长,传孩子的Id
            memberId = activity.getIntent().getStringExtra("memberId");
        }
        return memberId;
    }

    /**
     * 过滤人工批阅的自动批阅读写单
     * @param lqTaskCommitVoList 列表数据
     */
    private void filterAutoMark(List<LqTaskCommitVo> lqTaskCommitVoList){
        if(EmptyUtil.isEmpty(lqTaskCommitVoList)) return;
        ListIterator<LqTaskCommitVo> iterator = lqTaskCommitVoList.listIterator();
        while(iterator.hasNext()){
            LqTaskCommitVo vo = iterator.next();
            if (EmptyUtil.isNotEmpty(vo)) {
                // 如果是自动批阅的读写单,还有支持人工和自动批阅的
                if (sectionResListVo.isAutoMark() &&
                        EmptyUtil.isNotEmpty(vo.getStudentResId())) {
                    // 过滤人工批阅的
                    iterator.remove();
                }
            }
        }
    }

    /**
     * 所有提交列表，老师查看未批阅提到前面
     * @param lqTaskCommitVoList 批阅列表
     * @return 处理过的集合数据
     */

    private List getCommitTaskList(List<LqTaskCommitVo> lqTaskCommitVoList) {
        if (mRoleType == UserHelper.MoocRoleType.EDITOR
                || mRoleType == UserHelper.MoocRoleType.TEACHER) {
            if (lqTaskCommitVoList != null && lqTaskCommitVoList.size() > 0) {
                List<LqTaskCommitVo> unmarkList = new ArrayList<>();
                List<LqTaskCommitVo> markList = new ArrayList<>();
                for (LqTaskCommitVo vo : lqTaskCommitVoList) {
                    if (vo != null) {

                        if(vo.isSpeechEvaluation()){
                            if(!vo.isHasVoiceReview()){
                                unmarkList.add(vo);
                            } else {
                                markList.add(vo);
                            }
                        }else if(sectionResListVo.isAutoMark()){
                            if(EmptyUtil.isEmpty(vo.getTaskScore()) /*|| TextUtils.equals(vo.getTaskScore(),"0")*/){
                                // 0分的
                                unmarkList.add(vo);
                            }else{
                                markList.add(vo);
                            }
                        }else{
                            if (!vo.isHasCommitTaskReview()) {
                                unmarkList.add(vo);
                            } else {
                                markList.add(vo);
                            }
                        }
                    }
                }
                if (!markList.isEmpty()) {
                    unmarkList.addAll(markList);
                }
                return unmarkList;
            }
        }

        return lqTaskCommitVoList;
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        // 切换状态
        updateItemDeleteState(false);
        if(EmptyUtil.isNotEmpty(sectionResListVo) && EmptyUtil.isNotEmpty(doWorkListener)){
            if(isAudition){
                UIUtil.showToastSafe(R.string.tip_join_or_by_course);
                return;
            }
        }

        if(viewId == R.id.done_bt){
            // 做听读课，读写单
            doWorkListener.onDoWork();
        }else if(viewId == R.id.btn_speech_evaluation){
            // 语音评测
            doWorkListener.onSpeechEvaluation();
        }else if(viewId == R.id.btn_statistical_scores){
            // 成绩统计
            List<LqTaskCommitVo> data = committedTasksAdapter.getData();
            doWorkListener.onStatisticalScores(data);
        }
    }

    /**
     * 更换Hold状态
     * @param state 期望的状态
     */
    private void updateItemDeleteState(boolean state){
        updateItemDeleteState(null,state);
    }

    /**
     * 更换Hold状态
     * @param state 期望的状态
     * @param hold state 为true ,hold不能为空
     */
    private void updateItemDeleteState(@NonNull LqTaskCommitVo hold,boolean state){
        this.isHoldTag = state;
        // 更改所有Hold的状态
        List<LqTaskCommitVo> items = committedTasksAdapter.getData();
        if(EmptyUtil.isNotEmpty(items)){
            for (LqTaskCommitVo vo:items) {
                if(state && vo.getId() == hold.getId()){
                    // 同一个课程
                    vo.setDeleteTag(state);
                    break;
                }

                if(!state){
                    vo.setDeleteTag(state);
                }
            }
        }

        committedTasksAdapter.notifyDataSetChanged();
    }

    private void updateViews() {
        /*if (mLqTaskCommitListVo != null) {
            committedTasksAdapter.setData(mLqTaskCommitListVo.getListCommitTaskOnline());
            committedTasksAdapter.notifyDataSetChanged();
        }*/
    }
    public void updateViews(LqTaskCommitListVo vo) {
        /*if (vo != null) {
            mLqTaskCommitListVo = vo;
            UIUtil.getHandler().post(new Runnable() {
                @Override
                public void run() {
                    committedTasksAdapter = new CommittedTasksAdapter(mLqTaskCommitListVo.getListCommitTaskOnline());
                    listView.setAdapter(committedTasksAdapter);
                    *//*committedTasksAdapter.setData(mLqTaskCommitListVo.getListCommitTaskOnline());
                    committedTasksAdapter.notifyDataSetChanged();*//*
                }
            });

        }*/
    }

    public interface DoWorkListener{
        void onDoWork();
        // 打开语音评测,进行录制
        void onSpeechEvaluation();
        // 打开成绩统计
        void onStatisticalScores(@NonNull List<LqTaskCommitVo> data);

        /**
         * 点击批阅或者查看批阅详情
         * @param vo 批阅任务实体
         * @param isCheckMark 是否是查看批阅
         * @param sourceType 数据类型
         */
        void onItemClick(@NonNull LqTaskCommitVo vo, boolean isCheckMark, int sourceType);
        void onClickCommitListItem(SectionTaskCommitListVo vo);
        void openCourseWareDetails(String resId,
                                   int resType,
                                   String resTitle,
                                   int screenType,
                                   String resourceUrl,
                                   String resourceThumbnailUrl,
                                   int commitTaskId);
        // 语音评测,打开课件
        void openCourseWareDetails(@NonNull LqTaskCommitVo vo);
    }

    private int getSourceType(){
        return activity.getIntent().getBooleanExtra("isLive", false) ?
                (activity.getIntent().getBooleanExtra(SectionTaskDetailsActivity
                        .KEY_IS_FROM_MY, false)
                        ? SourceFromType.MY_ONLINE_LIVE : SourceFromType.ONLINE_LIVE)
                : (activity.getIntent().getBooleanExtra(SectionTaskDetailsActivity
                .KEY_IS_FROM_MY, false)
                ? SourceFromType.LQ_MY_COURSE : SourceFromType.LQ_COURSE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}
